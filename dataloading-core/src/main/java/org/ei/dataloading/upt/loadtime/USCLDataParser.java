/*
 * Created on Oct 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.upt.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;

/**
 * @author KFokuo
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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

            List<String> vals = new ArrayList<String>();

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
        } catch (Exception ex) {

        } finally {
            if (in != null) {

                try {
                    in.close();
                } catch (IOException e) {
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
