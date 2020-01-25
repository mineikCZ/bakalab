package org.bakalab.app.fragments;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.bakalab.app.R;
import org.bakalab.app.adapters.RozvrhBasicAdapter;
import org.bakalab.app.adapters.UkolyBasicAdapter;
import org.bakalab.app.adapters.ZnamkyBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.items.main.MainScreen;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.items.ukoly.Ukol;
import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.ItemClickSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.bakalab.app.utils.Utils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

import static okhttp3.internal.Util.UTC;
import static okhttp3.internal.Util.verifyAsIpAddress;

public class MainScreenFragment extends Fragment implements Callback, SwipeRefreshLayout.OnRefreshListener {

	private static class Result {
		List<Ukol> ukolItems = new ArrayList<>();
		List<Znamka> znamkaItems = new ArrayList<>();
		Predmet predmet;

		List<Ukol> getUkolItems() {
			return ukolItems;
		}

		void setUkolItems(List<Ukol> ukolItems) {
			this.ukolItems = ukolItems;
		}

		List<Znamka> getZnamkaItems() {
			return znamkaItems;
		}

		void setZnamkaItems(List<Znamka> znamkaItems) {
			this.znamkaItems = znamkaItems;
		}

		Predmet getPredmet() {
			return predmet;
		}

		void setPredmet(Predmet predmet) {
			this.predmet = predmet;
		}
	}

	private static class Predmet {
		Predmet(boolean free) {
			this.free = free;
		}

		Predmet() {

		}

		private String nazev = "", ucitel = "", mistnost = "", cas = "";
		boolean free = false;

		String getCas() {
			return cas;
		}

		void setCas(String cas) {
			this.cas = cas;
		}

		String getNazev() {
			return nazev;
		}

		void setNazev(String nazev) {
			this.nazev = nazev;
		}

		String getUcitel() {
			return ucitel;
		}

		void setUcitel(String ucitel) {
			this.ucitel = ucitel;
		}

		String getMistnost() {
			return mistnost;
		}

		void setMistnost(String mistnost) {
			this.mistnost = mistnost;
		}

		boolean isFree() {
			return free;
		}
	}

	private List<Znamka> znamkaList = new ArrayList<>();
	private ZnamkyBasicAdapter znamkyBasicAdapter = new ZnamkyBasicAdapter(znamkaList);

	private List<Ukol> ukolList = new ArrayList<>();
	private UkolyBasicAdapter ukolyBasicAdapter = new UkolyBasicAdapter(ukolList);

	private RecyclerView ukolyRec;
	private RecyclerView znamkyRec;

	private SwipeRefreshLayout swipeRefreshLayout;

	private SkeletonScreen skeletonScreenUkoly;
	private SkeletonScreen skeletonScreenZnamky;
	private SkeletonScreen skeletonScreenNext;

	private TextView nextTitle, nextDesc;

	private ConstraintLayout root;

	private boolean blockClick = true;


	public MainScreenFragment() {
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_screen, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.button_ukoly).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.ukolyFragment));
		view.findViewById(R.id.button_rozvrh).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.rozvrhFragment));
		view.findViewById(R.id.button_znamky).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.znamkyFragment));

		ukolyRec = view.findViewById(R.id.ukoly_list);
		znamkyRec = view.findViewById(R.id.znamky_list);
		swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
		nextTitle = view.findViewById(R.id.next_title);
		nextDesc = view.findViewById(R.id.next_desc);
		root = view.findViewById(R.id.root);

		swipeRefreshLayout.setOnRefreshListener(this);

		ukolyRec.setHasFixedSize(true);
		znamkyRec.setHasFixedSize(true);

		LinearLayoutManager layoutManagerUkoly = new LinearLayoutManager(getContext());
		LinearLayoutManager layoutManagerZnamky = new LinearLayoutManager(getContext());

		ukolyRec.setLayoutManager(layoutManagerUkoly);
		znamkyRec.setLayoutManager(layoutManagerZnamky);

		ukolyRec.setAdapter(ukolyBasicAdapter);
		znamkyRec.setAdapter(znamkyBasicAdapter);

		ItemClickSupport.addTo(ukolyRec).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				if (!blockClick) {
					boolean expanded = ukolyBasicAdapter.ukolyList.get(position).isExpanded();
					ukolyBasicAdapter.ukolyList.get(position).setExpanded(!expanded);
					ukolyBasicAdapter.notifyItemChanged(position);
				}
			}
		});

		ItemClickSupport.addTo(znamkyRec).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				if (!blockClick) {
					boolean expanded = znamkyBasicAdapter.znamkyList.get(position).isExpanded();
					znamkyBasicAdapter.znamkyList.get(position).setExpanded(!expanded);
					znamkyBasicAdapter.notifyItemChanged(position);
				}
			}
		});

		makeRequest();
	}

	private void showSkeletons() {

		skeletonScreenUkoly = Skeleton.bind(ukolyRec)
				.adapter(ukolyBasicAdapter)
				.load(R.layout.list_item_skeleton)
				.count(3)
				.show();

		skeletonScreenZnamky = Skeleton.bind(znamkyRec)
				.adapter(znamkyBasicAdapter)
				.load(R.layout.list_item_skeleton)
				.count(3)
				.show();

		skeletonScreenNext = Skeleton.bind(root)
				.load(R.layout.skeleton_next_card)
				.show();
	}

	@Override
	public void onRefresh() {
		makeRequest();
	}

	private void makeRequest() {
		showSkeletons();
		blockClick = true;

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BakaTools.getUrl(this.getContext()))
				.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
				.build();

		BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

		Call<RozvrhRoot> call = bakalariAPI.getRozvrh(BakaTools.getToken(this.getContext()),Utils.getCurrentMonday());
		Log.d("Debug","Sending Query");
		nextTitle.setText("Loading");
		nextDesc.setText("Please Wait");
		skeletonScreenNext.hide();

		call.enqueue(new retrofit2.Callback<RozvrhRoot>() {
						 @SuppressLint("SimpleDateFormat")
						 @Override
						 @EverythingIsNonNull
						 public void onResponse(Call<RozvrhRoot> call, Response<RozvrhRoot> response) {
							Log.d("Debug","Response is Here");
							 if (!response.isSuccessful()) {
								 Log.d("Error", response.message());
								 return;
							 }

							 assert response.body() != null;
							 Rozvrh r = response.body().getRozvrh();
							 List<RozvrhDen> rds = r.getDny();
							 RozvrhDen rd;
							 Log.d("Debug",new SimpleDateFormat("EE").format(new Date()));
							 switch(new SimpleDateFormat("EE").format(new Date())){
								 case "tue":
								 case "Tue":
								 case "ut":
								 case "Ut":
								 case "út":
								 case "Út":
								 	rd=rds.get(1); break;
								 case "wed":
								 case "Wed":
								 case "St":
								 case "st":
								 	rd=rds.get(2); break;
								 case "thu":
								 case "Thu":
								 case "ct":
								 case "Ct":
								 case "čt":
								 case "Čt":
								 	rd=rds.get(3); break;
								 case "fri":
								 case "Fri":
								 case "pa":
								 case "Pa":
								 case "Pá": //prostě neni čas na testování jakej den je jakej lmao
								 case "pá":
								 	rd=rds.get(4); break;
								 default: //TODO: víkend a pondělí implementace
								 	rd=rds.get(0); break;
							 }
							Log.d("Debug", "Got here");
							 Log.d("Debug",String.valueOf(new Date().getDay()));
							 RozvrhHodina rh = rd.getHodiny().get(((new Date().getDay() != 6)||(new Date().getDay() != 5))? 1:rd.getCurrentLessonInt()-1);

							 nextTitle.setText(String.format("%1s (%2s)",rh.getPr(),rh.getZkrpr()));
							 nextDesc.setText(String.format("Začíná v %1s, místnost %2s (%3s)", rh.getBegintime(), rh.getZkrmist(),rh.getZkruc()));
							 swipeRefreshLayout.setRefreshing(false);

							 Log.d("Debug","Finished response processing");
//							 onCallbackFinish(v);
						 }

						 @Override
						 public void onFailure(Call<RozvrhRoot> call, Throwable t) {
							nextTitle.setText("Error");
							nextDesc.setText(t.getMessage());
							swipeRefreshLayout.setRefreshing(false);
						 }
					 }
		);
	}

	@Override
	public void onCallbackFinish(Object callResult) {
		if (callResult != null) {
			Result result = (Result) callResult;
			Log.d("Debug","Reached onCallBackFinish");
			ukolList.clear();
			znamkaList.clear();
			ukolList.addAll(result.getUkolItems());
			znamkaList.addAll(result.getZnamkaItems());

			ukolyBasicAdapter.notifyDataSetChanged();
			znamkyBasicAdapter.notifyDataSetChanged();

			//TODO: properly fix this bug
			boolean isntFree;
			try {
				isntFree = !result.getPredmet().isFree();
			} catch (NullPointerException e) {
				isntFree = false;
			}

			if (isntFree) {
				nextTitle.setText(result.getPredmet().getNazev());
				nextDesc.setText(String.format(getString(R.string.rozvrh_description), result.getPredmet().getCas(), result.getPredmet().getMistnost(), result.getPredmet().getUcitel()));
				nextDesc.setVisibility(View.VISIBLE);
			} else {
				nextTitle.setText(getString(R.string.free_hour));
				nextDesc.setVisibility(View.GONE);

			}

			skeletonScreenUkoly.hide();
			skeletonScreenZnamky.hide();
			skeletonScreenNext.hide();

			blockClick = false;

			swipeRefreshLayout.setRefreshing(false);

		} else {
			Toast.makeText(getContext(), "Chyba při zpracovávání", Toast.LENGTH_SHORT).show();
			swipeRefreshLayout.setVisibility(View.GONE);
		}
	}
}
