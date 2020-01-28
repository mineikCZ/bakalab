package org.bakalab.app.items.absence;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class Absence {

	@Element(required=false, name="zameskanost")
	private Zameskanost zameskanost;

	@Element(required = false, name = "strop")
	private String strop;


	public Zameskanost getZameskanost(){
		return zameskanost;
	}

	public String getStrop() {
		return strop;
	}
}
