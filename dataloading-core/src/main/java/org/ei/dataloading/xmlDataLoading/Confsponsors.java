package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Confsponsors extends BaseElement {

    Confsponsor confsponsor;
    String confsponsor_complete;
    List<Confsponsor> confsponsors = new ArrayList<Confsponsor>();

    public void setConfsponsors(List<Confsponsor> confsponsors) {
        this.confsponsors = confsponsors;
    }

    public void addConfsponsor(Confsponsor confsponsor) {
        confsponsors.add(confsponsor);
    }

    public List<Confsponsor> getConfsponsors() {
        return this.confsponsors;
    }

    public void setConfsponsors_complete(String confsponsor_complete) {
        this.confsponsor_complete = confsponsor_complete;
    }

    public String getConfsponsors_complete() {
        return this.confsponsor_complete;
    }

}
