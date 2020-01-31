package org.bakalab.app.adapters;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.bakalab.app.R;
import org.bakalab.app.items.absence_item.AbsPredmet;

import java.util.List;

public class AbsenceBasicAdapter extends RecyclerView.Adapter<AbsenceBasicAdapter.MyViewHolder>{
	public List<AbsPredmet> absPredmetList;

	public AbsenceBasicAdapter(List<AbsPredmet> absPredmets) {
		this.absPredmetList = absPredmets;
	}

	@Override
	public AbsenceBasicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_absence, parent, false);

		return new AbsenceBasicAdapter.MyViewHolder(itemView);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onBindViewHolder(AbsenceBasicAdapter.MyViewHolder holder, final int position) {
		final AbsPredmet absPredmet = absPredmetList.get(position);

		holder.popis.setMaxLines(2);
		holder.popis.setEllipsize(TextUtils.TruncateAt.END);
		holder.predmet.setMaxLines(1);
		holder.predmet.setEllipsize(TextUtils.TruncateAt.END);

		holder.predmet.setText(absPredmet.getNazev());
		holder.procenta.setText(String.valueOf((int)(absPredmet.getProcentaAbs()*100))+"%");
		holder.popis.setText(String.format("Absence: %1d Pozdní příchody: %2d (Odučeno %3d hodin)", absPredmet.getAbsence(), absPredmet.getPozdniPrichody(), absPredmet.getOduceno()));

		if(absPredmet.getProcentaAbs() >= 0.3){
			holder.symbol.setImageResource(R.drawable.ic_report);
			ImageViewCompat.setImageTintList(holder.symbol, ColorStateList.valueOf(Color.parseColor("#b00020")));
		}else if(absPredmet.getProcentaAbs() == 0){
			holder.symbol.setImageResource(R.drawable.ic_done_all);
			ImageViewCompat.setImageTintList(holder.symbol, ColorStateList.valueOf(Color.parseColor("#1976d2")));
		}else{
			holder.symbol.setImageResource(R.drawable.ic_done);
		}

	}

	@Override
	public int getItemCount() {
		return absPredmetList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView predmet, procenta, popis;
		public ImageView symbol;
		public RelativeLayout root_view;
		public View divider;

		public MyViewHolder(View view) {
			super(view);
			predmet = view.findViewById(R.id.absPredmet);
			procenta = view.findViewById(R.id.procenta);
			popis = view.findViewById(R.id.popis);
			symbol = view.findViewById(R.id.symbol);
			divider = view.findViewById(R.id.divider);
			root_view = view.findViewById(R.id.root_container);

		}
	}
}
