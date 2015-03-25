package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.List;

public class Head extends BaseElement {
    Citation_info citation_info;
    List<Related_item> relatedItems = new ArrayList<Related_item>();
    Citation_title citation_title;
    List<Author_group> authorGroups = new ArrayList<Author_group>();
    Correspondence correspondence;
    Abstracts abstracts;
    Source source;
    Enhancement enhancement;

    public void setCitation_info(Citation_info citation_info) {
        this.citation_info = citation_info;
    }

    public Citation_info getCitation_info() {
        return this.citation_info;
    }

    public void setRelated_items(List<Related_item> relatedItems) {
        this.relatedItems = relatedItems;
    }

    public List<Related_item> getRelated_items() {
        return this.relatedItems;
    }

    public void addRelated_items(Related_item relatedItem) {
        relatedItems.add(relatedItem);
    }

    public void setCitation_title(Citation_title citation_title) {
        this.citation_title = citation_title;
    }

    public Citation_title getCitation_title() {
        return this.citation_title;
    }

    public void setAuthor_groups(List<Author_group> authorGroups) {
        this.authorGroups = authorGroups;
    }

    public List<Author_group> getAuthor_groups() {
        return this.authorGroups;
    }

    public void addAuthor_group(Author_group authorGroup) {
        authorGroups.add(authorGroup);
    }

    public void setCorrespondence(Correspondence correspondence) {
        this.correspondence = correspondence;
    }

    public Correspondence getCorrespondence() {
        return this.correspondence;
    }

    public void setAbstracts(Abstracts abstracts) {
        this.abstracts = abstracts;
    }

    public Abstracts getAbstracts() {
        return this.abstracts;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return this.source;
    }

    public void setEnhancement(Enhancement enhancement) {
        this.enhancement = enhancement;
    }

    public Enhancement getEnhancement() {
        return this.enhancement;
    }

}
