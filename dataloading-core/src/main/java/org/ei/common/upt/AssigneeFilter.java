/*
 * Created on Nov 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.common.upt;

import java.util.*;

import org.apache.oro.text.perl.*;

/**
 * @author KFokuo
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssigneeFilter {

    private static Perl5Util perl = new Perl5Util();

    public static List<String> filterInventors(List<String> lstInvs, List<?> lstAffs) {
        return filterInventors(lstInvs, lstAffs, true);
    }

    public static List<String> filterInventors(List<String> lstInvs, List<?> lstAffs, boolean allowZero) {

        List<String> newAffs = new ArrayList<String>();
        boolean found = false;
        String af = null;
        for (int i = 0; i < lstAffs.size(); i++) {

            af = (String) lstAffs.get(i);

            for (int j = 0; j < lstInvs.size(); j++) {
                String inv = (String) lstInvs.get(j);

                String newAf = perl.substitute("s/[,\\.]*//ig", af);

                inv = perl.substitute("s/[,\\.]*//ig", inv);

                if (inv.equalsIgnoreCase(newAf)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                newAffs.add(af);

            found = false;
        }

        if (newAffs.size() > 0 || allowZero) {
            return newAffs;
        } else {
            return lstInvs;
        }
    }

}
