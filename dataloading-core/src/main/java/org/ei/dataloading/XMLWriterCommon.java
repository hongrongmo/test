package org.ei.dataloading;

import org.apache.oro.text.perl.Perl5Util;

public class XMLWriterCommon
{
    private static Perl5Util perl = new Perl5Util();

    public static String formatClassCodes(String c)
    {
        if (c == null)
        {
            return null;
        }

        c = perl.substitute("s/\\./DQD/g", c);

        return c;

    }

}
