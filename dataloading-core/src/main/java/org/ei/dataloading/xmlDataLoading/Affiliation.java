package org.ei.dataloading.xmlDataLoading;

import java.util.*;

public class Affiliation extends BaseElement {
    String text;
    List<String> organizations = new ArrayList<String>();
    String address_part;
    String city_group;
    String city;
    String state;
    String postal_code;
    String country;
    String affiliation;

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getAffiliation() {
        return this.affiliation;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setOrganizations(List<String> organizations) {
        this.organizations = organizations;
    }

    public List<String> getOrganizations() {
        return this.organizations;
    }

    public void addOrganization(String organization) {
        organizations.add(organization);
    }

    public void setOrganization(String organization) {
        addOrganization(organization);
    }

    public void setAddress_part(String address_part) {
        this.address_part = address_part;
    }

    public String getAddress_part() {
        return this.address_part;
    }

    public void setCity_group(String city_group) {
        this.city_group = city_group;
    }

    public String getCity_group() {
        return this.city_group;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getPostal_code() {

        return this.postal_code;
    }

    public void setAffiliation_country(String country) {
        this.country = country;
    }

    public String getAffiliation_country() {
        return this.country;
    }

}
