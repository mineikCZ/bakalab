package org.bakalab.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.bakalab.app.activities.LoginActivity;
import org.bakalab.app.adapters.UkolyBasicAdapter;
import org.bakalab.app.adapters.ZnamkyBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.items.ukoly.Ukol;
import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkyRoot;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CallFragments {
	public static void requestRozvrh(final TextView nextTitle, final TextView nextDesc, final SwipeRefreshLayout swipeRefreshLayout, Context context){
		Retrofit retrofit =  null;
		try{
			retrofit = new Retrofit.Builder()
					.baseUrl(BakaTools.getUrl(context))
					.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
					.build();
		}catch(Exception e){
			Intent in = new Intent(context, LoginActivity.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(in);
		}


		assert retrofit != null;
		BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

		Call<RozvrhRoot> call = bakalariAPI.getRozvrh(BakaTools.getToken(context),Utils.getCurrentMonday());
		Log.d("Debug","Sending Query");

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
							 RozvrhDen rd = null;

							 boolean init = false;
							 String currentDay = new SimpleDateFormat("EE").format(new Date());
							 String currD = new SimpleDateFormat("dd").format(new Date());
							 Log.d("Debug",currentDay);

							 if(new Date().getDay() >= 1 && new Date().getDay() < 6){
							 	rd = rds.get(new Date().getDay()-1);
								 try {
									 if(new SimpleDateFormat("hh").parse(rd.getHodiny().get(rd.getHodiny().size()-1).getEndtime()).getHours() > new Date().getHours()){
									 	if(new Date().getDay() == 5){
									 		rd=rds.get(0);
										}else{
									 		rd=rds.get(new Date().getDay());
										}
									 }

									 init = true;
								 } catch (Exception e) {
									 e.printStackTrace();
								 }
							 }else{
							 	rd = rds.get(0);
							 	init = true;
							 }

							 if(!init) rd = rds.get(0); //just double check, we have initialized RozvrhDen and we won't fall through assert :)

							 Log.d("Debug", "Got here");
							 Log.d("Debug",String.valueOf(new Date().getDay()));
							 assert rd != null;
							 Date d = new Date();
							 RozvrhHodina rh;

							 if(d.getDay() == 6 || d.getDay() == 7){
							 	try{
							 		rh = rd.getHodiny().get(1);
								}catch (Exception e){
							 		 rh = new RozvrhHodina("Prázdniny / Volno", "Yay", "Doma", "Ráno");
								}
							 }else{
							 	try{
							 		if(d.getHours() < new SimpleDateFormat("hh:mm").parse(rd.getHodiny().get(rd.getHodiny().size()-1).getEndtime()).getHours()){
							 			 rh = rd.getHodiny().get(rd.getCurrentLessonInt());
									}else{
							 			 rh = rd.getHodiny().get(1);
										 if(rh.getPr().equals(null) || rh.getPr().equals("null")){
										 	rh = rd.getHodiny().get(2);
										 }
									}
								}catch(Exception e){
									 rh = new RozvrhHodina("Prázdniny / Volno", "Yay", "Doma", "Ráno");
								}
							 }


							 nextTitle.setText(String.format("%1s (%2s)",rh.getPr(),rh.getZkrpr()));
							 nextDesc.setText(String.format("Začíná v %1s, místnost %2s (%3s)", rh.getBegintime(), rh.getZkrmist(),rh.getZkruc()));
							 swipeRefreshLayout.setRefreshing(false);

							 Log.d("Debug","Finished response processing");
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

	public static void requestZnamky(final SwipeRefreshLayout swipeRefreshLayout, final List<Znamka> znamkaList, final ZnamkyBasicAdapter znamkyAdapter, final Context context){
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BakaTools.getUrl(context))
				.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
				.build();

		BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

		Call<ZnamkyRoot> call = bakalariAPI.getZnamky(BakaTools.getToken(context));

		call.enqueue(new retrofit2.Callback<ZnamkyRoot>() {
			@Override
			@EverythingIsNonNull
			public void onResponse(Call<ZnamkyRoot> call, Response<ZnamkyRoot> response) {
				if (!response.isSuccessful()) {
					Log.d("Error", response.message());
					return;
				}


				znamkaList.clear();

				assert response.body() != null;
				znamkaList.addAll(response.body().getSortedZnamky());
				znamkyAdapter.notifyDataSetChanged();

				swipeRefreshLayout.setRefreshing(false);

			}

			@Override
			@EverythingIsNonNull
			public void onFailure(Call<ZnamkyRoot> call, Throwable t) {
				t.getCause().printStackTrace();
				Log.d("Error", t.getMessage());

			}
		});
	}

	public static void requestUkoly(final SwipeRefreshLayout swipeRefreshLayout, final List<Ukol> ukolList, final UkolyBasicAdapter ukolAdapter, final Context context){
		Retrofit retrofit = null;
		try{
			retrofit = new Retrofit.Builder()
					.baseUrl(BakaTools.getUrl(context))
					.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
					.build();
		}catch(Exception e){
			Intent in = new Intent(context, LoginActivity.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(in);
		}

		assert retrofit != null;
		BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

		Call<UkolyList> call = bakalariAPI.getUkoly(BakaTools.getToken(context));

		call.enqueue(new retrofit2.Callback<UkolyList>() {
			@Override
			@EverythingIsNonNull
			public void onResponse(Call<UkolyList> call, Response<UkolyList> response) {
				if (!response.isSuccessful() && response.body() != null) {
					Log.e("Error", response.message());
					return;
				}
				ukolList.clear();
				List<Ukol> listTodo = new ArrayList<>();
				List<Ukol> listFinished = new ArrayList<>();

				assert response.body() != null;
				for(Ukol ukol : response.body().getUkoly())
					if (ukol.getStatus().equals("probehlo") || (SharedPrefHandler.getDefaultBool(context, "ukoly_done") && ukol.getStatus().equals("pozde")))
						listFinished.add(ukol);
					else if (ukol.getStatus().equals("aktivni") || (!SharedPrefHandler.getDefaultBool(context, "ukoly_done") && ukol.getStatus().equals("pozde")))
						listTodo.add(ukol);
					else
						listFinished.add(ukol);


				ukolList.addAll(listTodo);
				ukolAdapter.notifyDataSetChanged();
				swipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFailure(Call<UkolyList> call, Throwable t) {
				Log.e("Error", t.getMessage());

			}
		});
	}

	public static void requestAbsence(){
		//TODO api pro absenci

	}
}
