package org.ei.domain;

import java.util.Hashtable;

public interface DataDictionary
{
    public Hashtable<String, String> getClassCodes();
    public Hashtable<String, String> getTreatments();
    public String getClassCodeTitle(String classCode);
    public String getTreatmentTitle(String treatmentTitle);
    public Hashtable<String, String> getAuthorityCodes();
}