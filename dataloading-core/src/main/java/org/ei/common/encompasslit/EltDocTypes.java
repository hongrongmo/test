package org.ei.common.encompasslit;

import java.util.Hashtable;

public class EltDocTypes
{
    private Hashtable<String, String> eltDoctypes ;

    public String getMappedDocType(String dtype)
    {
        if(dtype != null)
        {
            String formatDT = dtype.toUpperCase().trim();
            if(eltDoctypes.containsKey(formatDT))
            {
                return (String) eltDoctypes.get(formatDT);
            }
        }

        return "";
    }

    public Hashtable<String, String> getDocTypes()
    {
        return this.eltDoctypes;
    }
    public EltDocTypes()
    {
        eltDoctypes = new Hashtable<String, String>();

       	eltDoctypes.put("K_CP","CA");
    	eltDoctypes.put("P_AB","CA");
    	eltDoctypes.put("P_AR","CA");
    	eltDoctypes.put("P_CP","CA");
    	eltDoctypes.put("P_ED","CA");
    	eltDoctypes.put("P_LE","CA");
    	eltDoctypes.put("P_NO","CA");
    	eltDoctypes.put("P_RE","CA");
    	eltDoctypes.put("J_CP","CA");
    	eltDoctypes.put("D_CP","CA");
    	eltDoctypes.put("J_AB","JA");
    	eltDoctypes.put("J_AR","JA");
    	eltDoctypes.put("J_BZ","JA");
    	eltDoctypes.put("J_ED","JA");
    	eltDoctypes.put("J_ER","JA");
    	eltDoctypes.put("J_LE","JA");
    	eltDoctypes.put("J_NO","JA");
    	eltDoctypes.put("J_RE","JA");
    	eltDoctypes.put("J_SH","JA");
    	eltDoctypes.put("D_AB","JA");
    	eltDoctypes.put("D_AR","JA");
    	eltDoctypes.put("D_BZ","JA");
    	eltDoctypes.put("D_ER","JA");
    	eltDoctypes.put("D_LE","JA");
    	eltDoctypes.put("D_NO","JA");
    	eltDoctypes.put("D_RE","JA");
    	eltDoctypes.put("K_AR","MC");
    	eltDoctypes.put("B_ER","MC");
    	eltDoctypes.put("R_AB","RC");
    	eltDoctypes.put("R_RE","RR");
    	eltDoctypes.put("AB","AB");
    	eltDoctypes.put("P","CA");
    	eltDoctypes.put("DI","DS");
    	eltDoctypes.put("Other","RC");
    	eltDoctypes.put("R","RC");
    	eltDoctypes.put("RE","RC");

    }

}
