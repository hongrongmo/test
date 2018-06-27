package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Contributor_group extends BaseElement {

    List<Contributor> contributors = new ArrayList<Contributor>();
    List<Collaboration> collaborations = new ArrayList<Collaboration>();
    String et_al;
    Affiliation affiliation;

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public void addContributor(Contributor contributor) {
        contributors.add(contributor);
    }

    public List<Contributor> getContributors() {
        return this.contributors;
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

    public void setEt_al(String et_al) {
        this.et_al = et_al;
    }

    public String getEt_al() {
        return this.et_al;
    }

    public void setAffiliation(Affiliation affiliation) {
        this.affiliation = affiliation;
    }

    public Affiliation getAffiliation() {
        return this.affiliation;
    }
}
