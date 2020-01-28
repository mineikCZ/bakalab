package org.bakalab.app.items.absence_item;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

// FIXME: 28.01.2020 shit be broken throwing
//@Root(strict = false, name = "predmet")
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


	public String getNazev() {
		return nazev;
	}

	public double getProcentaAbs(){
		return (double) Integer.parseInt(oduceno) / Integer.parseInt(absbase);
	}

	public int getAbsence(){
		return Integer.parseInt(absbase);
	}

	public int getPozdniPrichody(){
		return Integer.parseInt(abslate);
	}

	public int getOduceno() { return Integer.parseInt(oduceno);}
}
