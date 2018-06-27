package org.ei.common.upt;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.ClassNodeManager;
import org.ei.util.DiskMap;
import org.ei.util.StringUtil;

public class ECLAClassNormalizer {

    static char DELIM = (char) 30;
    static Perl5Util perl = new Perl5Util();

    public static String normalize(String code)
    {
        if (code == null)
        {
            return StringUtil.EMPTY_STRING;
        }

        try
        {
            String classtitle = (ClassNodeManager.getInstance()).seekECLA(code);
            if((classtitle == null) || classtitle.equals(StringUtil.EMPTY_STRING))
            {
                //System.out.print("ECLAClassNormalizer failed on: " + code);

                String normalcode = perl.substitute("s/0+([1-9A-Z]+)$/$1/i", code);
                while((code != null) && !code.equals(normalcode))
                {
                    //System.out.print("  trying: " + normalcode);
                    classtitle = (ClassNodeManager.getInstance()).seekECLA(normalcode);
                    // set code equal to normalcode so if the seekECLA was successful
                    // classtitle won't be null or empty and we will break out of the loop
                    code = normalcode;
                    if((classtitle == null) || classtitle.equals(StringUtil.EMPTY_STRING))
                    {
                        // if substitue fails, normalcode == code, so we break the loop
                        // otherwise we loop back around to try the new normalized code
                        normalcode = perl.substitute("s/0+([1-9A-Z]+)$/$1/i", code);
                    }
                   // else
                   // {
                        //System.out.print(" success: " + code);
                   // }
                }
                //System.out.println(". ");
            }

        }
        catch(Exception e)
        {
            System.out.println("ECLAClassNormalizer: " + e.getMessage());
        }

        return code;
    }

}
