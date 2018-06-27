package org.ei.dataloading.cbnb.loadtime;

import java.io.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

public class EntityCleaner
{

    private static Perl5Util perl = new Perl5Util();
    public static void main(String args[])
            throws Exception

    {
        EntityCleaner.writeFile(args[0]);
    }

    public static void writeFile(String infile) throws Exception
    {
         BufferedReader in = new BufferedReader(new FileReader(infile));
         FileWriter out = new FileWriter(infile+".cln");
        String line=null;
        while((line = in.readLine()) != null)
        {   line = line.trim();
            line = perl.substitute("s/<S>/<S><![CDATA[/g",line);
            line = perl.substitute("s/<\\/S>/]]><\\/S>/g",line);
            line = perl.substitute("s/<FJL>/<FJL><![CDATA[/g",line);
            line = perl.substitute("s/<\\/FJL>/]]><\\/FJL>/g",line);
            line = perl.substitute("s/<ATL>/<ATL><![CDATA[/g",line);
            line = perl.substitute("s/<\\/ATL>/]]><\\/ATL>/g",line);
            line = perl.substitute("s/<OTL>/<OTL><![CDATA[/g",line);
            line = perl.substitute("s/<\\/OTL>/]]><\\/OTL>/g",line);
            line = perl.substitute("s/<AVL>/<AVL><![CDATA[/g",line);
            line = perl.substitute("s/<\\/AVL>/]]><\\/AVL>/g",line);
            line = perl.substitute("s/<SRC>/<SRC><![CDATA[/g",line);
            line = perl.substitute("s/<\\/SRC>/]]><\\/SRC>/g",line);
            line = perl.substitute("s/<PBR>/<PBR><![CDATA[/g",line);
            line = perl.substitute("s/<\\/PBR>/]]><\\/PBR>/g",line);
            line = perl.substitute("s/<ISS>/<ISS><![CDATA[/g",line);
            line = perl.substitute("s/<\\/ISS>/]]><\\/ISS>/g",line);
            line = perl.substitute("s/<EBT>/<EBT><![CDATA[/g",line);
            line = perl.substitute("s/<\\/EBT>/]]><\\/EBT>/g",line);
            line = perl.substitute("s/<PAG>/<PAG><![CDATA[/g",line);
            line = perl.substitute("s/<\\/PAG>/]]><\\/PAG>/g",line);
            line = perl.substitute("s/<CIN>/<CIN><![CDATA[/g",line);
            line = perl.substitute("s/<\\/CIN>/]]><\\/CIN>/g",line);
            line = perl.substitute("s/<PAD>/<PAD><![CDATA[/g",line);
            line = perl.substitute("s/<\\/PAD>/]]><\\/PAD>/g",line);
            line = perl.substitute("s/<RECORD>/\n<RECORD>/g",line);

            out.write(" "+line);

        }
        in.close();
        out.close();
    }
}
