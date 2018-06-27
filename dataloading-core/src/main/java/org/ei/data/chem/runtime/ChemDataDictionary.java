package org.ei.data.chem.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;

public class ChemDataDictionary implements DataDictionary {
    private Hashtable<String, String> classCodes;
    private Hashtable<String, String> treatmentCodes;

    public String getTreatmentTitle(String mTreatmentCode) {
        String treatmentcode = null;

        if (treatmentCodes.containsKey(mTreatmentCode)) {
            treatmentcode = (String) treatmentCodes.get(mTreatmentCode);
        }
        return treatmentcode;
    }

    public String getClassCodeTitle(String classCode) {
        if (classCode != null)
            return (String) classCodes.get(classCode.toUpperCase());
        else
            return null;
    }

    public Hashtable<String, String> getClassCodes() {
        return this.classCodes;
    }

    public Hashtable<String, String> getTreatments() {
        return null;
    }

    public ChemDataDictionary() {
        classCodes = new Hashtable<String, String>();
        classCodes.put("1S-17", "Coatings, paints and inks");
        classCodes.put("21", "Pharmaceuticals prosthetics and medical chemistry");
        classCodes.put("4", "Pharmaceuticals prosthetics and medical chemistry");
        classCodes.put("7S-08", "Petrochemical industry");
        classCodes.put("7S-12", "Metals industries");
        classCodes.put("7S-21", "Biotechnology");
        classCodes.put("LA-00", "Legal Aspects");
        classCodes.put("LA-10", "Regulations and Rulings");
        classCodes.put("LA-20", "Health and Safety");
        classCodes.put("LA-30", "Environment");
        classCodes.put("LA-40", "Permits and Patents");
        classCodes.put("MS 16", "Plastics and rubber applications");
        classCodes.put("MS-00", "Chemical Businesses Generally");
        classCodes.put("MS-01", "Cosmetics and Toiletries");
        classCodes.put("MS-02", "Household and Cleaning Chemicals");
        classCodes.put("MS-03", "Food, Feed, and Beverages");
        classCodes.put("MS-04", "Pharmaceuticals, Prosthetics, and Medical Chemistry");
        classCodes.put("MS-05", "Agrochemicals");
        classCodes.put("MS-06", "Fertilizers");
        classCodes.put("MS-07", "Chemical Fibres and Textiles");
        classCodes.put("MS-08", "Petrochemical Industry");
        classCodes.put("MS-09", "Polymer and Elastomer Production");
        classCodes.put("MS-10", "Raw Material Winning");
        classCodes.put("MS-11", "Paper Industry");
        classCodes.put("MS-12", "Metals Industries");
        classCodes.put("MS-13", "Transportation and Vehicles");
        classCodes.put("MS-14", "Construction Industry");
        classCodes.put("MS-15", "Electronics Industry");
        classCodes.put("MS-16", "Plastics and Rubbers Applications");
        classCodes.put("MS-17", "Coatings, Paints, and Inks");
        classCodes.put("MS-18", "Dyes and Pigments");
        classCodes.put("MS-19", "Speciality Chemicals");
        classCodes.put("MS-20", "Other Industries");
        classCodes.put("MS-21", "Biotechnology");
        classCodes.put("S-09", "Polymer and elastomer production");
        classCodes.put("TR-00", "Trends - General");
        classCodes.put("TR-10", "Trends - New Technology");
        classCodes.put("TR-20", "Trends - New Material");
        classCodes.put("TR-30", "Trends - New Application");
        classCodes.put("TR-40", "Trends - General");
        classCodes.put("dMS-05", "Agrochemicals");
        classCodes.put("fMS-20", "Other Industries");

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

}
