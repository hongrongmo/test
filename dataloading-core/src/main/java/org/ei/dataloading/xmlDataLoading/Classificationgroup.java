package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Classificationgroup extends BaseElement {
    Classifications classifications;
    List<Classifications> classificationList = new ArrayList<Classifications>();
    String classificationsType;

    public void addClassifications(Classifications classifications) {
        try {
            classificationList.add(classifications);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClassifications(Classifications classifications) {
        try {
            this.classifications = classifications;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Classifications> getClassifications() {

        return this.classificationList;

    }

    public void setClassifications_type(String classificationsType) {
        try {
            this.classificationsType = classificationsType;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getClassification_type() {
        return this.classificationsType;
    }

}
