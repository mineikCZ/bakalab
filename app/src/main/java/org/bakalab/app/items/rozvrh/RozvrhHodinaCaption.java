package org.bakalab.app.items.rozvrh;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "hod", strict = false)
public class RozvrhHodinaCaption {

    public RozvrhHodinaCaption() {
        super();
    }

    @Element(required = false)
    private String caption;

    @Element(required = false)
    private String begintime;

    @Element(required = false)
    private String endtime;

    public String getCaption() {
        return caption;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getEndtime() { return endtime; }
}
