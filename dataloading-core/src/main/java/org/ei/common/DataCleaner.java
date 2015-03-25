package org.ei.common;

import org.apache.oro.text.perl.*;

public class DataCleaner
{
    Perl5Util util = new Perl5Util();

    public DataCleaner()
    {
    }

    public String fixISSN(String issn)
    {
        if(issn == null)
        {
            return null;
        }

        issn = util.substitute("s/-//g",issn);
        issn = util.substitute("s/ //g",issn);

        return issn;
    }

    public String stripBadChars(String base)
    {
        if(base == null)
        {
            return null;
        }

        base = util.substitute("s/,//g", base);
        base = util.substitute("s/\\./ /g", base);
        base = util.substitute("s/-/ /g", base);
        base = util.substitute("s/'//g", base);
        base = util.substitute("s/\\s+/ /g",base);

        return base;
    }

    public String joinAuthor(String author)
    {
        return util.substitute("s/ /9/g", author);
    }

    public static String restoreAuthor(String author)
    {
        return ((author != null) ? author.replaceAll("9"," ") : null);
    }

    public String cleanEntitiesForDisplay(String line)
    {
        if(util.match("/&die;/", line))
        {
            line = util.substitute("s/&die;//g", line);
        }
        if(util.match("/&caron;/", line))
        {
            line = util.substitute("s/&caron;//g", line);
        }
        if(util.match("/&breve;/", line))
        {
            line = util.substitute("s/&breve;//g", line);
        }
        if(util.match("/&grave;/", line))
        {
            line = util.substitute("s/&grave;/&#096;/g", line);
        }

        return line;
    }


    public String cleanEntities(String line)
    {

        if(util.match("/&grave;/", line))
        {
            line = util.substitute("s/&grave;//g", line);
        }
        if(util.match("/&acute;/", line))
        {
            line = util.substitute("s/&acute;//g", line);
        }
        if(util.match("/&circ;/", line))
        {
            line = util.substitute("s/&circ;//g", line);
        }
        if(util.match("/&tild;/", line))
        {
            line = util.substitute("s/&tild;//g", line);
        }
        if(util.match("/&macr;/", line))
        {
            line = util.substitute("s/&macr;//g", line);
        }
        if(util.match("/&dot;/", line))
        {
            line = util.substitute("s/&dot;//g", line);
        }
        if(util.match("/&die;/", line))
        {
            line = util.substitute("s/&die;//g", line);
        }
        if(util.match("/&ring;/", line))
        {
            line = util.substitute("s/&ring;//g", line);
        }
        if(util.match("/&caron;/", line))
        {
            line = util.substitute("s/&caron;//g", line);
        }
        if(util.match("/&cedil;/", line))
        {
            line = util.substitute("s/&cedil;//g", line);
        }
        if(util.match("/&AElig;/", line))
        {
            line = util.substitute("s/&AElig;/AE/g", line);
        }
        if(util.match("/&aelig;/", line))
        {
            line = util.substitute("s/&aelig;/ae/g", line);
        }
        if(util.match("/&OElig;/", line))
        {
            line = util.substitute("s/&OElig;/OE/g", line);
        }
        if(util.match("/&oelig;/", line))
        {
            line = util.substitute("s/&oelig;/oe/g", line);
        }
        if(util.match("/&Oslash;/", line))
        {
            line = util.substitute("s/&Oslash;/O/g", line);
        }
        if(util.match("/&oslash;/", line))
        {
            line = util.substitute("s/&oslash;/o/g", line);
        }
        if(util.match("/&Lstrok;/", line))
        {
            line = util.substitute("s/&Lstrok;/L/g", line);
        }
        if(util.match("/&lstrok;/", line))
        {
            line = util.substitute("s/&lstrok;/l/g", line);
        }
        if(util.match("/&szlig;/", line))
        {
            line = util.substitute("s/&szlig;/ss/g", line);
        }

        return line;
    }

}
