/*
 * Created on Oct 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data.upt;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;
import java.util.*;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class USCLDataParser {

    private Perl5Util perl = new Perl5Util();

    public void parse() {

        BufferedReader in = null;
        PrintWriter out = null;

        try {
            out = new PrintWriter(new FileWriter("c:\\uscl.dat"), true);
            in = new BufferedReader(new FileReader("C:\\elsevier\\patent_codes\\uscl\\data\\USCL-definitions.TXT"));

            String line = null;

            List vals = new ArrayList();

            while ((line = in.readLine()) != null) {
                perl.split(vals, "/\",\"/", line);
                String title = (String) vals.get(1);
                String code = (String) vals.get(3);
                code = code.trim();
                code = perl.substitute("s/\\*//", code);
                title = perl.substitute("s/\\s+/ /", title);
                out.println(code + "\t" + title + "\t" + "upt");
                vals.clear();
            }
        }
        catch (Exception ex) {

        }
        finally {
            if (in != null) {

                try {
                    in.close();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (out != null)
                out.close();
        }

    }
    public static void main(String[] args) {
        new USCLDataParser().parse();
    }
}
