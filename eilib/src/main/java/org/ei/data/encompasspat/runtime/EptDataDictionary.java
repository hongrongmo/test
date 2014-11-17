package org.ei.data.encompasspat.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;

public class EptDataDictionary implements DataDictionary {
    private Hashtable<String, String> authorityCodes;
    private Hashtable<String, String> classCodes;
    
    public String getClassCodeTitle(String classCode) {
        return (String) classCodes.get(classCode);
    }

    public Hashtable<String, String> getAuthorityCodes() {
        return this.authorityCodes;
    }

    public Hashtable<String, String> getClassCodes() {
        return null;
    }

    public Hashtable<String, String> getTreatments() {
        return null;
    }

    public EptDataDictionary() {
        authorityCodes = new Hashtable<String, String>();

        authorityCodes.put("AR", "Argentina");
        authorityCodes.put("AT", "Austria");
        authorityCodes.put("AU", "Australia");
        authorityCodes.put("BE", "Belgium");
        authorityCodes.put("BR", "Brazil");
        authorityCodes.put("CA", "Canada");
        authorityCodes.put("CH", "Switzerland");
        authorityCodes.put("CN", "China");
        authorityCodes.put("CS", "Czechoslovakia");
        authorityCodes.put("CZ", "Czech Republic");
        authorityCodes.put("DD", "East Germany");
        authorityCodes.put("DE", "Germany");
        authorityCodes.put("DK", "Denmark");
        authorityCodes.put("EP", "European Patent Office");
        authorityCodes.put("ES", "Spain");
        authorityCodes.put("FI", "Finland");
        authorityCodes.put("FR", "France");
        authorityCodes.put("GB", "United Kingdom");
        authorityCodes.put("HU", "Hungary");
        authorityCodes.put("IE", "Ireland");
        authorityCodes.put("IL", "Israel");
        authorityCodes.put("IN", "India");
        authorityCodes.put("IT", "Italy");
        authorityCodes.put("JP", "Japan");
        authorityCodes.put("KR", "South Korea");
        authorityCodes.put("KP", "North Korea");
        authorityCodes.put("LT", "Lithuania");
        authorityCodes.put("LU", "Luxembourg");
        authorityCodes.put("MX", "Mexico");
        authorityCodes.put("NL", "Netherlands");
        authorityCodes.put("NO", "Norway");
        authorityCodes.put("NZ", "New Zealand");
        authorityCodes.put("PH", "Philippines");
        authorityCodes.put("PL", "Poland");
        authorityCodes.put("PT", "Portugal");
        authorityCodes.put("RD", "Research Disclosures");
        authorityCodes.put("RO", "Romania");
        authorityCodes.put("RU", "Russian Federation");
        authorityCodes.put("SE", "Sweden");
        authorityCodes.put("SG", "Singapore");
        authorityCodes.put("SK", "Slovakia");
        authorityCodes.put("SU", "U.S.S.R.");
        authorityCodes.put("TW", "Taiwan");
        authorityCodes.put("US", "United States");
        authorityCodes.put("WO", "World Patent Office");
        authorityCodes.put("ZA", "South Africa");

    }

    public String getTreatmentTitle(String mTreatmentCode) {
        return null;
    }
}