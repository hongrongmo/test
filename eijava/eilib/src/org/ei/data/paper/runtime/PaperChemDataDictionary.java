package org.ei.data.paper.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;


public class PaperChemDataDictionary
    implements DataDictionary
{

    private Hashtable treatmentCodes;

    public Hashtable getTreatments()
    {
        return this.treatmentCodes;
    }

    public PaperChemDataDictionary()
    {

        treatmentCodes = new Hashtable();

        treatmentCodes.put("A","Applications (APP)");
        treatmentCodes.put("B","Biographical (BIO)");
        treatmentCodes.put("E","Economic (ECO)");
        treatmentCodes.put("X","Experimental (EXP)");
        treatmentCodes.put("G","General review (GEN)");
        treatmentCodes.put("H","Historical (HIS)");
        treatmentCodes.put("L","Literature review (LIT)");
        treatmentCodes.put("M","Management aspects (MAN)");
        treatmentCodes.put("N","Numerical (NUM)");
        treatmentCodes.put("T","Theoretical (THR)");

    }

	public Hashtable getAuthorityCodes()
	{
		return null;
	}
	public String getClassCodeTitle(String classCode)
	{
		return null;
	}


	public Hashtable getClassCodes()
	{
		return null;
	}
	
    public String getTreatmentTitle(String mTreatmentCode)
    {
    	return null;
    }

}