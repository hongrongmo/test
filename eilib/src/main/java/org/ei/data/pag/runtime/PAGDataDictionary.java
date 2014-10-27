package org.ei.data.pag.runtime;

import java.util.Hashtable;

import org.ei.domain.DataDictionary;

public class PAGDataDictionary implements DataDictionary {
    Hashtable<String, String> classCodes = null;

    public PAGDataDictionary() {
        classCodes = new Hashtable<String, String>();

        classCodes.put("ELE", "Electronics & Electrical");
        classCodes.put("MAT", "Materials & Mechanical");
        classCodes.put("CHE", "Chemical, Petrochemical & Process");
        classCodes.put("CIV", "Civil & Environmental");
        classCodes.put("COM", "Computing");
        classCodes.put("SEC", "Security & Networking");
        classCodes.put("TNFELE", "Electronics & Electrical");
        classCodes.put("TNFMAT", "Materials & Mechanical");
        classCodes.put("TNFCHE", "Chemical, Petrochemical & Process");
        classCodes.put("TNFCIV", "Civil & Environmental");
        classCodes.put("TNFCOM", "Computing");
        classCodes.put("TNFSEC", "Security & Networking");
    }

    public String getClassCodeTitle(String arg0) {
        return (String) classCodes.get(arg0);
    }

    public Hashtable<String, String> getClassCodes() {
        return classCodes;
    }

    public Hashtable<String, String> getTreatments() {
        return null;
    }

    public Hashtable<String, String> getAuthorityCodes() {
        return null;
    }

    public String getTreatmentTitle(String mTreatmentCode) {
        return null;
    }
}