package org.bakalab.app.items.absence_item;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class Zameskanost {
	public Zameskanost(){
		super();
	}

	@ElementList(required = false)
	private List<AbsPredmet> predmet;

	@Element(required = false)
	private String nadpis;

	public List<AbsPredmet> getPredmety() {return predmet;}

	public String getNadpis() {
		return nadpis;
	}
}
