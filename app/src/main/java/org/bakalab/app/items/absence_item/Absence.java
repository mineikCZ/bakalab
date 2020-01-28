package org.bakalab.app.items.absence_item;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//@Root(strict = false)
public class Absence {
	public Absence(){super();}

	@Element(required=false)
	private Zameskanost zameskanost;

	@Element(required = false)
	private String hranice;


	public Zameskanost getZameskanost(){
		return zameskanost;
	}

	public String getStrop() {
		return hranice;
	}
}
