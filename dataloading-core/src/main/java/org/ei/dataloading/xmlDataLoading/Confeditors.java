package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Confeditors extends BaseElement {
    Editors editors;
    String editororganization = null;
    List<String> editororganizations = new ArrayList<String>();
    String editoraddress;

    public void setEditors(Editors editors) {
        this.editors = editors;
    }

    public Editors getEditors() {
        return this.editors;
    }

    public void setEditororganization(String editororganization) {
        addEditororganization(editororganization);
    }

    public void addEditororganization(String editororganization) {
        editororganizations.add(editororganization);
    }

    public List<String> getEditororganizations() {
        return this.editororganizations;
    }

    public void setEditoraddress(String editoraddress) {
        this.editoraddress = editoraddress;
    }

    public String getEditoraddress() {
        return this.editoraddress;
    }

}
