package org.ei.thesaurus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThesaurusPage implements ThesaurusNode {

    private List<ThesaurusRecord> recs = new ArrayList<ThesaurusRecord>();

    public void accept(ThesaurusNodeVisitor v) throws ThesaurusException {
        v.visitWith(this);
    }

    public ThesaurusPage(int size) {
        for (int i = 0; i < size; i++) {
            recs.add(null);
        }
    }

    public void set(int index, ThesaurusRecord rec) {
        recs.set(index, rec);
    }

    public void add(ThesaurusRecord rec) {
        recs.add(rec);
    }

    public void add() {
        recs.add(null);
    }

    public ThesaurusRecord get(int index) {
        if (recs.size() == 0) {
            return null;
        } else {
            return (ThesaurusRecord) recs.get(index);
        }
    }

    public List<ThesaurusRecord> getRecords() {

        // Hanan fix unsorted related terms, Jan 17,2013
        Collections.sort(recs, new ThesaurusTermComp());  // added
        return recs;
    }

    public int size() {
        return recs.size();
    }
}