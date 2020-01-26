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
import org.bakalab.app.utils.CallFragments;

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

		CallFragments.requestRozvrh(nextTitle,nextDesc,swipeRefreshLayout, this.getContext()); //request next period
		skeletonScreenNext.hide();

		CallFragments.requestZnamky(swipeRefreshLayout, znamkaList, znamkyBasicAdapter, this.getContext()); //request grades
		skeletonScreenZnamky.hide();

		//TODO ukoly call
		CallFragments.requestUkoly(swipeRefreshLayout, ukolList, ukolyBasicAdapter, this.getContext());
		skeletonScreenUkoly.hide();

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