package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Civ extends ReferexCollection {
    private String name = "CIV";

    protected Civ() {
    }

    protected Civ(String name) {
        this.name = name;
    }

    public int getColMask() {
        return 16;
    }

    public int getSortOrder() {
        return 6;
    }

    public String getAbbrev() {
        // TODO Auto-generated method stub
        return this.name;
    }

    public String getDisplayName() {
        // TODO Auto-generated method stub
        return "Civil & Environmental";
    }

    public String getShortname() {
        // TODO Auto-generated method stub
        return "Civil";
    }

    public List<?> populateSubjects(boolean che, boolean chestar) {
        List<?> subjs = new LinkedList<Object>();

        return subjs;
    }

}
