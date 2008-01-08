package org.ei.data.georef.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;


public class GRFDataDictionary implements DataDictionary
{
    public GRFDataDictionary()
    {

    }

    public Hashtable getClassCodes() {
        return new Hashtable();
    }

    public Hashtable getTreatments() {
        return new Hashtable();
    }

    public String getClassCodeTitle(String classCode) {
        return "";
    }

    public Hashtable getAuthorityCodes() {
        return new Hashtable();
    }

}