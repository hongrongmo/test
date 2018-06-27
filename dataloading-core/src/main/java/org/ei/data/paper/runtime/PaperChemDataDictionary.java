package org.ei.data.paper.runtime;

import java.util.Hashtable;

import org.ei.domain.DataDictionary;

public class PaperChemDataDictionary implements DataDictionary {

    private Hashtable<String, String> treatmentCodes;

    public Hashtable<String, String> getTreatments() {
        return this.treatmentCodes;
    }

    public PaperChemDataDictionary() {

        treatmentCodes = new Hashtable<String, String>();

        treatmentCodes.put("A", "Applications (APP)");
        treatmentCodes.put("B", "Biographical (BIO)");
        treatmentCodes.put("E", "Economic (ECO)");
        treatmentCodes.put("X", "Experimental (EXP)");
        treatmentCodes.put("G", "General review (GEN)");
        treatmentCodes.put("H", "Historical (HIS)");
        treatmentCodes.put("L", "Literature review (LIT)");
        treatmentCodes.put("M", "Management aspects (MAN)");
        treatmentCodes.put("N", "Numerical (NUM)");
        treatmentCodes.put("T", "Theoretical (THR)");

    }

    public Hashtable<String, String> getAuthorityCodes() {
        return null;
    }

    public String getClassCodeTitle(String classCode) {
        return null;
    }

    public Hashtable<String, String> getClassCodes() {
        return null;
    }

    public String getTreatmentTitle(String mTreatmentCode) {
        if (treatmentCodes.containsKey(mTreatmentCode)) {
            return (String) treatmentCodes.get(mTreatmentCode);
        }
        return "A";
    }

}