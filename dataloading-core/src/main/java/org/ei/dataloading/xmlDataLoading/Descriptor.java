package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Descriptor extends BaseElement {
    List<Mainterm> mainterms = new ArrayList<Mainterm>();
    Mainterm maintermObject = null;
    String link;
    String sublink;
    String subsublink;
    String descriptor;

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    public void setMainterm(List<Mainterm> mainterms) {
        this.mainterms = mainterms;
    }

    public void addMainterm(Mainterm mainterm) {
        mainterms.add(mainterm);
    }

    public List<Mainterm> getMainterm() {
        return this.mainterms;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public void setSublink(String sublink) {
        this.sublink = sublink;
    }

    public String getSublink() {
        return this.sublink;
    }

    public void setSubsublink(String subsublink) {
        this.subsublink = subsublink;
    }

    public String getSubsublink() {
        return this.subsublink;
    }

}
