package org.bakalab.app.items.absence;

import android.util.Log;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//TODO co je kurva tohle
@Root(strict = false, name = "predmet")
public class AbsPredmet {
	public AbsPredmet(){
		super();
	}
	public AbsPredmet(String nazev){
		this.nazev = nazev;
		this.oduceno = "1";
		this.absbase = "1";
		this.abslate = "0";
	}

	@Element(required = false)
	private String nazev;

	@Element(required = false)
	private String oduceno;

	@Element(required = false)
	private String absbase;

	@Element(required = false)
	private String abslate;


	public String getNazev() { return nazev; }

	public double getProcentaAbs(){
		return Double.parseDouble(absbase) / Double.parseDouble(oduceno);
	}

	public int getAbsence(){
		return Integer.parseInt(absbase);
	}

	public int getPozdniPrichody(){
		return Integer.parseInt(abslate);
	}

	public int getOduceno() { return Integer.parseInt(oduceno);}
}
