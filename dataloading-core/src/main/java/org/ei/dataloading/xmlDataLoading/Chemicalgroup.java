package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Chemicalgroup extends BaseElement {
    List<Chemicals> chemicals = new ArrayList<Chemicals>();

    public void setChemicalss(List<Chemicals> chemicals) {
        this.chemicals = chemicals;
    }

    public void addChemicals(Chemicals chemical) {
        chemicals.add(chemical);
    }

    public List<Chemicals> getChemicalss() {
        return this.chemicals;
    }

}
