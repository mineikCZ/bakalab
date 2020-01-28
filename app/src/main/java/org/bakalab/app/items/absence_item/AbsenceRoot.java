package org.bakalab.app.items.absence_item;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "results", strict = false)
public class AbsenceRoot {
	public AbsenceRoot(){ super();}

	@Element(required = false)
	private Absence absence;

	public Absence getAbsence() {
		return absence;
	}
}
