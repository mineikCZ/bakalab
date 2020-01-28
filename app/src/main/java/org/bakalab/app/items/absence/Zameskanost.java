package org.bakalab.app.items.absence;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class Zameskanost {
	public Zameskanost(){
		super();
	}

	@ElementList(required = false , name="predmet")
	private List<AbsPredmet> absPredmet = new ArrayList<>();

	public List<AbsPredmet> getPredmety() {return absPredmet;}
}
