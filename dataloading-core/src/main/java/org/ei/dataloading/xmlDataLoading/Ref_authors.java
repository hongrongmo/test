package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Ref_authors extends BaseElement {

    List<Author> authors = new ArrayList<Author>();
    List<Collaboration> collaborations = new ArrayList<Collaboration>();
    String et_al;

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthors(Author author) {
        authors.add(author);
    }

    public List<Author> getAuthors() {
        return this.authors;
    }

    public void setCollaborations(List<Collaboration> collaborations) {
        this.collaborations = collaborations;
    }

    public void addCollaboration(Collaboration collaboration) {
        collaborations.add(collaboration);
    }

    public List<Collaboration> getCollaborations() {
        return this.collaborations;
    }

    public void setEt_alr(String et_al) {
        this.et_al = et_al;
    }

    public String getEt_al() {
        return this.et_al;
    }
}
