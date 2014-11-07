package org.ei.data.xmlDataLoading;

import java.util.*;

public class Classification extends BaseElement {
    List<String> classificationList = new ArrayList<String>();
    String classificationsType;

    public void addClassification(String classification) {
        try {
            classificationList.add(classification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClassification(List<String> classificationList) {
        this.classificationList = classificationList;
    }

    public void setClassification(String classification) {
        addClassification(classification);
    }

    public List<String> getClassification() {
        return this.classificationList;
    }

    public void setClassification_type(String classificationsType) {
        this.classificationsType = classificationsType;
    }

    public String getClassification_type() {
        return this.classificationsType;
    }
}