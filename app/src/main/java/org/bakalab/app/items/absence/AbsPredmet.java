package org.bakalab.app.items.absence;

import org.simpleframework.xml.Element;

public class AbsPredmet {
	public AbsPredmet(){
		super();
	}
	public AbsPredmet(String nazev){
		this.nazev = nazev;
		this.oduceno = "1";
		this.absenceZakladni = "1";
		this.absencePozdniP = "0";
	}

	@Element(required = false, name="nazev")
	private String nazev;

	@Element(required = false, name = "oduceno")
	private String oduceno;

	@Element(required = false,name = "absbase")
	private String absenceZakladni;

	@Element(required = false,name = "abslate")
	private String absencePozdniP;


	public String getNazev() {
		return nazev;
	}

	public double getProcentaAbs(){
		return (double) Integer.parseInt(oduceno) / Integer.parseInt(absenceZakladni);
	}

	public int getAbsence(){
		return Integer.parseInt(absenceZakladni);
	}

	public int getPozdniPrichody(){
		return Integer.parseInt(absencePozdniP);
	}

	public int getOduceno() { return Integer.parseInt(oduceno);}
}
