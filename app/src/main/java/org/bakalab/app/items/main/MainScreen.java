package org.bakalab.app.items.main;

import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "results")
public class MainScreen {
/*
    @Element(required = false, name = "xmlznamky")
    private List<ZnamkyRoot> znamkaPredmetyLists;

    @Element(required = false, name = "xmlukoly")
    private List<UkolyList> ukolyLists;

    @Element(required = false, name = "xmlrozvrhakt")
    private List<Rozvrh> rozvrhList;
*/
    @Element(required = false, name = "xmlrozvrhnext")
    private Rozvrh rozvrh;

    public Rozvrh getRozvrh() {
        return rozvrh;
    }

    //TODO Return next hour
  /*  public List<Rozvrh> getRozvrh(){return rozvrhList;}
    public List<ZnamkyRoot> getZnamky(){return znamkaPredmetyLists;}
    public List<UkolyList> getUkoly(){return ukolyLists;}
*/
}
