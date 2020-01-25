package org.bakalab.app.items.absence;

import org.simpleframework.xml.Element;

public class Predmet {
	public Predmet(){
		super();
	}

	@Element(required = false, name="nazev")
	private String nazev;

	public String getNazev() {
		return nazev;
	}
}
