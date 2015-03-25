package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Item_info extends BaseElement {
    Copyright copyrights;
    Itemidlist itemidlist;
    History history;
    List<Dbcollection> dbcollections = new ArrayList<Dbcollection>();

    public void setItemidlist(Itemidlist itemidlist) {
        this.itemidlist = itemidlist;
    }

    public Itemidlist getItemidlist() {
        return this.itemidlist;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public History getHistory() {
        return this.history;
    }

    public void setCopyright(Copyright copyrights) {
        this.copyrights = copyrights;
    }

    public Copyright getCopyright() {
        return copyrights;
    }

    public void setDbcollection(List<Dbcollection> dbcollections) {
        this.dbcollections = dbcollections;
    }

    public void addDbcollection(Dbcollection dbcollection) {
        dbcollections.add(dbcollection);
    }

    public List<Dbcollection> getDbcollection() {
        return dbcollections;
    }
}
