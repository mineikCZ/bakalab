package org.bakalab.app.items.absence;

import android.util.Log;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;

import java.util.List;

@Root(name = "results", strict=false)
public class Abs {

	private int hraniceProc;

	@Element(name = "absence", required = false)
	Absence absence;

	public static class Absence{
		@Element(name="zameskanost", required = false)
		private Zameskanost zameskanost;

		@Element(name = "hranice", required = false)
		private String hranice;
		public double getHranice() {
			hranice = hranice.replace(',','.');
			return Double.parseDouble(this.hranice);
		}
		public Zameskanost getZameskanost(){
			return zameskanost;
		}

		public static class Zameskanost{
			@ElementList(name = "predmet", required = true, inline = true)
//			@Path("results/absence/zameskanost")
			List<AbsPredmet> predmety;


			public List<AbsPredmet> getPredmety(){
				Log.d("Debug",String.valueOf(predmety));
				return predmety;
			}
		}
	}



	public Absence getAbsence(){return this.absence; }

	public int getHraniceAbs(){return this.hraniceProc;}

	@Commit
	private void shit(){
		this.hraniceProc = (int)(absence.getHranice() * 100);
	}
}
