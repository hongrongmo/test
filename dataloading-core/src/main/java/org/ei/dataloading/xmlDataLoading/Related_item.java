package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Related_item extends BaseElement {
    String pii;
    String doi;
    Citation_info citation_info;
    Citation_title citation_title;
    List<Contributor_group> contributorGroups = new ArrayList<Contributor_group>();
    Source source;

    public void setPii(String pii) {
        this.pii = pii;
    }

    public String getPii() {
        return pii;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDoi() {
        return doi;
    }

    public void setCitation_info(Citation_info citation_info) {
        this.citation_info = citation_info;
    }

    public Citation_info getCitation_info() {
        return citation_info;
    }

    public void setContributor_groups(List<Contributor_group> contributorGroups) {
        this.contributorGroups = contributorGroups;
    }

    public void addContributor_group(Contributor_group contributorGroup) {
        contributorGroups.add(contributorGroup);
    }

    public List<Contributor_group> getContributor_groups() {
        return contributorGroups;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

}
