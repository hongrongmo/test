/*
 * Created on Feb 14, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

import org.ei.data.CountryFormatter;

/**
 * @author solovyevat
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BdCorrespAffiliation {
    private ArrayList<String> affOrganization = new ArrayList<String>();
    private String affAddressPart;
    private String affCityGroup;
    private String affCountry;
    private String affCity;
    private String affState;
    private String affPostalCode;
    private String affText;
    private int affid;
    private String affOrganizationStr;
    private String affVenue;

    private static ArrayList<String> cMixedElements = new ArrayList<String>();

    static {
        cMixedElements.add("affOrganization");
        cMixedElements.add("affAddressPart");
        cMixedElements.add("affCityGroup");
        cMixedElements.add("affCountry");
    }

    public BdCorrespAffiliation() {

    }

    public String getIdDislpayValue() {
        if (affid != 0) {
            return String.valueOf(affid);
        }
        return null;
    }

    public BdCorrespAffiliation(String bdData, ArrayList<?> elements) {
        if (bdData != null && bdData.trim().length() > 0) {
            String[] affelements = bdData.split(BdParser.IDDELIMITER, -1);
            int aSize = affelements.length;

            for (int i = 0; i < elements.size(); i++) {
                String affField = (String) elements.get(i);
                if (affField.equals("affText")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        if (!parseMixedData(affelements[i])) {
                            this.setAffText(affelements[i]);
                        }
                    }
                } else if (affField.equals("affid")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        this.setAffid(Integer.parseInt(affelements[i]));
                    }
                }

            }
        }
    }

    public boolean parseMixedData(String bdData) {
        boolean isMixed = false;

        if (bdData != null && bdData.trim().length() > 0) {
            String[] affelements = bdData.split(BdParser.GROUPDELIMITER, -1);
            int aSize = affelements.length;
            if (aSize > 0) {
                isMixed = true;
            }

            for (int i = 0; i < cMixedElements.size(); i++) {

                String affField = (String) cMixedElements.get(i);
                if (affField.equals("affAddressPart")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        this.setAffAddressPart(affelements[i]);
                    }
                } else if (affField.equals("affOrganization")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        this.setAffOrganizationStr(affelements[i]);
                    }
                } else if (affField.equals("affCityGroup")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        this.setAffCityGroup(affelements[i]);
                    }
                } else if (affField.equals("affCountry")) {
                    if (aSize > i && affelements[i] != null && !affelements[i].trim().equals("")) {
                        this.setAffCountry(affelements[i]);
                    }
                }
            }

        }
        return isMixed;
    }

    public String getSearchValue() {

        if (this.affOrganizationStr != null) {
            return this.affOrganizationStr;
        }

        return null;
    }

    public String getDisplayValue() {
        StringBuffer searchValue = new StringBuffer();
        if (this.affOrganizationStr != null) {
            searchValue.append(this.affOrganizationStr);
        }
        if (this.affText != null) {
            if (searchValue.length() > 0) {
                searchValue.append(", ");
            }
            searchValue.append(this.affText);
        }
        if (this.affAddressPart != null) {
            if (searchValue.length() > 0) {
                searchValue.append(", ");
            }
            searchValue.append(this.affAddressPart);
        }
        if (this.affCityGroup != null) {
            if (searchValue.length() > 0) {
                searchValue.append(", ");
            }
            searchValue.append(this.affCityGroup);
        }
        if (this.affCountry != null) {
            if (searchValue.length() > 0) {
                searchValue.append(", ");
            }
            searchValue.append(CountryFormatter.formatCase(CountryFormatter.formatCountry(this.affCountry)));
        }

        return searchValue.toString();
    }

    public String getCountriesSearchValue() {

        if (this.affCountry != null) {
            return CountryFormatter.formatCountry(this.affCountry);
        }

        return null;
    }

    public String getLocationsSearchValue() {
        StringBuffer searchValue = new StringBuffer();
        if (this.affCountry != null) {
            searchValue.append(CountryFormatter.formatCountry(this.affCountry)).append(" ");
        }
        if (this.affText != null) {
            searchValue.append(this.affText).append(" ");
        }
        if (this.affAddressPart != null) {
            searchValue.append(this.affAddressPart).append(" ");
        }
        if (this.affCityGroup != null) {
            searchValue.append(this.affCityGroup).append(" ");
        }

        return searchValue.toString();
    }

    /**
     * @return Returns the affCityGroup.
     */
    public String getAffCityGroup() {
        if (affCityGroup != null) {
            return affCityGroup;
        } else if (affCity != null || affState != null || affPostalCode != null) {
            StringBuffer affCitiGroupbuf = new StringBuffer();
            if (affCity != null) {
                affCitiGroupbuf.append(affCity).append("; ");
            }
            if (affState != null) {
                affCitiGroupbuf.append(affState).append("; ");
            }
            if (affPostalCode != null) {
                affCitiGroupbuf.append(affPostalCode).append("; ");
            }

            int lastMarker = affCitiGroupbuf.lastIndexOf("; ");
            String affCitiGroup = affCitiGroupbuf.substring(0, lastMarker).toString();
            return affCitiGroup;
        }
        return "";
    }

    /**
     * @param affCityGroup
     *            The affCityGroup to set.
     */
    public void setAffCityGroup(String affCityGroup) {
        this.affCityGroup = affCityGroup;
    }

    /**
     * @return Returns the affOrganization.
     */
    public void setAffOrganizationStr(String affOrganizationStr)

    {
        this.affOrganizationStr = affOrganizationStr;
    }

    public String getAffOrganization() {

        if (affOrganization != null && affOrganization.size() > 0) {
            StringBuffer affs = new StringBuffer();
            for (int i = 0; i < affOrganization.size(); i++) {
                affs.append(affOrganization.get(i));
                if (i < affOrganization.size() - 1) {
                    affs.append(", ");
                }

            }
            return affs.toString();
        }
        return null;
    }

    /**
     * @param affOrganization
     *            The affOrganization to set.
     */
    public void setAffOrganization(ArrayList<String> affOrganization) {
        this.affOrganization = affOrganization;
    }

    public void addAffOrganization(String aOrganization) {
        this.affOrganization.add(aOrganization);
    }

    /**
     * @return Returns the affPostalCode.
     */
    public String getAffPostalCode() {
        return affPostalCode;
    }

    /**
     * @param affPostalCode
     *            The affPostalCode to set.
     */
    public void setAffPostalCode(String affPostalCode) {
        this.affPostalCode = affPostalCode;
    }

    /**
     * @return Returns the affState.
     */
    public String getAffState() {
        return affState;
    }

    /**
     * @param affState
     *            The affState to set.
     */
    public void setAffState(String affState) {
        this.affState = affState;
    }

    /**
     * @return Returns the affAddressPart.
     */
    public String getAffAddressPart() {
        return affAddressPart;
    }

    /**
     * @param affAddressPart
     *            The affAddressPart to set.
     */
    public void setAffAddressPart(String affAddressPart) {
        this.affAddressPart = affAddressPart;
    }

    /**
     * @return Returns the affCity.
     */
    public String getAffCity() {
        return affCity;
    }

    /**
     * @param affCity
     *            The affCity to set.
     */
    public void setAffCity(String affCity) {
        this.affCity = affCity;
    }

    /**
     * @return Returns the affid.
     */
    public int getAffid() {
        return affid;
    }

    /**
     * @param affid
     *            The affid to set.
     */
    public void setAffid(int affid) {
        this.affid = affid;
    }

    /**
     * @return Returns the affCountry.
     */
    public String getAffCountry() {
        return affCountry;
    }

    /**
     * @param affCountry
     *            The affCountry to set.
     */
    public void setAffCountry(String affCountry) {
        this.affCountry = affCountry;
    }

    /**
     * @return Returns the affText.
     */
    public String getAffText() {
        return affText;
    }

    /**
     * @param affText
     *            The affText to set.
     */
    public void setAffText(String affText) {
        this.affText = affText;
    }

    /**
     * @return Returns the affVenue.
     */
    public String getAffVenue() {
        return affVenue;
    }

    /**
     * @param affVenue
     *            The affVenue to set.
     */
    public void setAffVenue(String affVenue) {
        this.affVenue = affVenue;
    }
}
