package org.bakalab.app.items.absence;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

public class Zameskanost {
	public Zameskanost(){
		super();
	}

	@Element(required = false , name="Predmet")
	private List<Predmet> predmet = new ArrayList<>();

	public List<Predmet> getPredmet() {return predmet;}
}
