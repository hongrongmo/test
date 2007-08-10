package org.ei.data.encompasslit.loadtime;

import java.util.Hashtable;

public class EltDocTypes
{
    private Hashtable eltDoctypes ;

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

    public Hashtable getDocTypes()
    {
        return this.eltDoctypes;
    }
    public EltDocTypes()
    {
        eltDoctypes = new Hashtable();

        eltDoctypes.put("K_CP","CA");
        eltDoctypes.put("P_AB","CA");
        eltDoctypes.put("P_AR","CA");
        eltDoctypes.put("P_BK","CA");
        eltDoctypes.put("P_BR","CA");
        eltDoctypes.put("P_BZ","CA");
        eltDoctypes.put("P_CH","CA");
        eltDoctypes.put("P_CP","CA");
        eltDoctypes.put("P_DI","CA");
        eltDoctypes.put("P_ED","CA");
        eltDoctypes.put("P_ER","CA");
        eltDoctypes.put("P_LE","CA");
        eltDoctypes.put("P_NO","CA");
        eltDoctypes.put("P_PR","CA");
        eltDoctypes.put("P_PA","CA");
        eltDoctypes.put("P_RP","CA");
        eltDoctypes.put("P_RE","CA");
        eltDoctypes.put("P_SH","CA");
        eltDoctypes.put("P_WP","CA");
        eltDoctypes.put("J_CP","CA");
        eltDoctypes.put("R_CP","CA");
        eltDoctypes.put("D_CP","CA");
        eltDoctypes.put("K_CR","CP");
        eltDoctypes.put("P_CR","CP");
        eltDoctypes.put("J_CR","CP");
        eltDoctypes.put("R_CR","CP");
        eltDoctypes.put("D_CR","CP");
        eltDoctypes.put("J_AB","JA");
        eltDoctypes.put("J_AR","JA");
        eltDoctypes.put("J_BK","JA");
        eltDoctypes.put("J_BR","JA");
        eltDoctypes.put("J_BZ","JA");
        eltDoctypes.put("J_CH","JA");
        eltDoctypes.put("J_DI","JA");
        eltDoctypes.put("J_ED","JA");
        eltDoctypes.put("J_ER","JA");
        eltDoctypes.put("J_LE","JA");
        eltDoctypes.put("J_NO","JA");
        eltDoctypes.put("J_PR","JA");
        eltDoctypes.put("J_PA","JA");
        eltDoctypes.put("J_RP","JA");
        eltDoctypes.put("J_RE","JA");
        eltDoctypes.put("J_SH","JA");
        eltDoctypes.put("J_WP","JA");
        eltDoctypes.put("D_AB","JA");
        eltDoctypes.put("D_AR","JA");
        eltDoctypes.put("D_BK","JA");
        eltDoctypes.put("D_BR","JA");
        eltDoctypes.put("D_BZ","JA");
        eltDoctypes.put("D_CH","JA");
        eltDoctypes.put("D_DI","JA");
        eltDoctypes.put("D_ED","JA");
        eltDoctypes.put("D_ER","JA");
        eltDoctypes.put("D_LE","JA");
        eltDoctypes.put("D_NO","JA");
        eltDoctypes.put("D_PR","JA");
        eltDoctypes.put("D_PA","JA");
        eltDoctypes.put("D_RP","JA");
        eltDoctypes.put("D_RE","JA");
        eltDoctypes.put("D_SH","JA");
        eltDoctypes.put("D_WP","JA");
        eltDoctypes.put("K_AB","MC");
        eltDoctypes.put("K_AR","MC");
        eltDoctypes.put("K_BK","MC");
        eltDoctypes.put("K_BR","MC");
        eltDoctypes.put("K_BZ","MC");
        eltDoctypes.put("K_CH","MC");
        eltDoctypes.put("K_DI","MC");
        eltDoctypes.put("K_ED","MC");
        eltDoctypes.put("K_ER","MC");
        eltDoctypes.put("K_LE","MC");
        eltDoctypes.put("K_NO","MC");
        eltDoctypes.put("K_PR","MC");
        eltDoctypes.put("K_PA","MC");
        eltDoctypes.put("K_RP","MC");
        eltDoctypes.put("K_SH","MC");
        eltDoctypes.put("K_WP","MC");
        eltDoctypes.put("B_AB","MC");
        eltDoctypes.put("B_AR","MC");
        eltDoctypes.put("B_BR","MC");
        eltDoctypes.put("B_BZ","MC");
        eltDoctypes.put("B_CH","MC");
        eltDoctypes.put("B_CP","MC");
        eltDoctypes.put("B_CR","MC");
        eltDoctypes.put("B_DI","MC");
        eltDoctypes.put("B_ED","MC");
        eltDoctypes.put("B_ER","MC");
        eltDoctypes.put("B_LE","MC");
        eltDoctypes.put("B_NO","MC");
        eltDoctypes.put("B_PR","MC");
        eltDoctypes.put("B_PA","MC");
        eltDoctypes.put("B_RP","MC");
        eltDoctypes.put("B_SH","MC");
        eltDoctypes.put("B_WP","MC");
        eltDoctypes.put("K_RE","MR");
        eltDoctypes.put("B_RE","MR");
        eltDoctypes.put("R_AB","RC");
        eltDoctypes.put("R_AR","RC");
        eltDoctypes.put("R_BK","RC");
        eltDoctypes.put("R_BR","RC");
        eltDoctypes.put("R_BZ","RC");
        eltDoctypes.put("R_CH","RC");
        eltDoctypes.put("R_DI","RC");
        eltDoctypes.put("R_ED","RC");
        eltDoctypes.put("R_ER","RC");
        eltDoctypes.put("R_LE","RC");
        eltDoctypes.put("R_NO","RC");
        eltDoctypes.put("R_PR","RC");
        eltDoctypes.put("R_PA","RC");
        eltDoctypes.put("R_RP","RC");
        eltDoctypes.put("R_RE","RR");
        eltDoctypes.put("R_SH","RC");
        eltDoctypes.put("R_WP","RC");
        eltDoctypes.put("AB","JA");
        eltDoctypes.put("M_AB","MC");
        eltDoctypes.put("M_AR","MC");
        eltDoctypes.put("M_BK","MC");
        eltDoctypes.put("M_BR","MC");
        eltDoctypes.put("M_BZ","MC");
        eltDoctypes.put("M_CH","MC");
        eltDoctypes.put("M_CP","MC");
        eltDoctypes.put("M_CR","MC");
        eltDoctypes.put("M_DI","MC");
        eltDoctypes.put("M_ED","MC");
        eltDoctypes.put("M_ER","MC");
        eltDoctypes.put("M_LE","MC");
        eltDoctypes.put("M_NO","MC");
        eltDoctypes.put("M_PR","MC");
        eltDoctypes.put("M_PA","MC");
        eltDoctypes.put("M_RP","MC");
        eltDoctypes.put("M_RE","MC");
        eltDoctypes.put("M_SH","MC");
        eltDoctypes.put("M_WP","MC");
        eltDoctypes.put("P","CA");

    }

}
