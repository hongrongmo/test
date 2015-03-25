package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Chemicals extends BaseElement {
    List<Chemical> chemicals = new ArrayList<Chemical>();
    String chemical_source;

    public void setChemicals(List<Chemical> chemicals) {
        this.chemicals = chemicals;
    }

    public void addChemical(Chemical chemical) {
        chemicals.add(chemical);
    }

    public List<Chemical> getChemicals() {
        return this.chemicals;
    }

    public void setChemical_source(String chemical_source) {
        this.chemical_source = chemical_source;
    }

    public String getChemical_source() {
        return this.chemical_source;
    }

}
