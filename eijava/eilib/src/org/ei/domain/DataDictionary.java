package org.ei.domain;

import java.util.Hashtable;

public interface DataDictionary
{
    public Hashtable getClassCodes();
    public Hashtable getTreatments();
    public String getClassCodeTitle(String classCode);
    public Hashtable getAuthorityCodes();
}