package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Citation_title extends BaseElement {

    List<Titletext> titletexts = new ArrayList<Titletext>();

    /*
     * public void setTitletext(Titletext titletext) { this.titletext = titletext; }
     *
     * public Titletext getTitletext() { return titletext; }
     */
    public void setTitletext(List<Titletext> titletexts) {
        this.titletexts = titletexts;
    }

    public void addTitletext(Titletext titletext) {
        titletexts.add(titletext);
    }

    public List<Titletext> getTitletext() {
        return this.titletexts;
    }

}
