package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Descriptorgroup extends BaseElement {
    List<Descriptors> descriptorss = new ArrayList<Descriptors>();
    String descriptors_controlled;
    String descriptors_type;

    public void setDescriptorss(List<Descriptors> descriptorss) {
        this.descriptorss = descriptorss;
    }

    public List<Descriptors> getDescriptorss() {
        return this.descriptorss;
    }

    public void addDescriptors(Descriptors descriptors) {
        descriptorss.add(descriptors);
    }

    public void setDescriptors_controlled(String descriptors_controlled) {
        this.descriptors_controlled = descriptors_controlled;
    }

    public String getDescriptors_controlled() {
        return this.descriptors_controlled;
    }

    public void setDescriptors_type(String descriptors_type) {
        this.descriptors_type = descriptors_type;
    }

    public String getDescriptors_type() {
        return this.descriptors_type;
    }

}
