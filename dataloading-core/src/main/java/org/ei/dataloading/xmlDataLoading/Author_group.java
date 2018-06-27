package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Author_group extends BaseElement {
    List<Author> authors = new ArrayList<Author>();
    List<Collaboration> collaborations = new ArrayList<Collaboration>();
    String et_al;
    Affiliation affiliation;
    String author_group = null;

    public void setAuthor_group(String author_group) {
        this.author_group = author_group;
    }

    public String getAuthor_group(String author_group) {
        return this.author_group;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
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

    public void setAffiliation(Affiliation affiliation) {
        this.affiliation = affiliation;
    }

    public Affiliation getAffiliation() {
        return this.affiliation;
    }

    public void setEt_Al(String et_al) {
        this.et_al = et_al;
    }

    public String getEt_Al() {
        return this.et_al;
    }

}
