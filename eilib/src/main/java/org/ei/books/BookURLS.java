package org.ei.books;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookURLS {

    public static void main(String[] args) {
        List<String> booklist = new ArrayList<String>();
        String wobl_url = "http://cert-wobl.engineeringvillage.com/wobl";
        try {
            AdmitOneTicketer aot = AdmitOneTicketer.getInstance();

            File isbnlist = new File("C:\\Documents and Settings\\moschettoj\\Desktop\\Slovak_Natl_Libr_21043522.txt");
            if (isbnlist.exists()) {
                BufferedReader rdr = new BufferedReader(new FileReader(isbnlist)); // new InputStreamReader(getResourceAsStream("georefids.txt")));
                try {
                    if (rdr != null) {
                        while (rdr.ready()) {
                            String aline = rdr.readLine();
                            booklist.add(aline);
                        }
                    } 
                } catch (IOException e) {
                } finally {
                    try {
                        rdr.close();
                    } catch (IOException e) {
                    }
                }
                isbnlist = null;
            }
            Iterator<String> itrdocs = booklist.iterator();
            while (itrdocs.hasNext()) {
                String isbn = (String) itrdocs.next();
                System.out.println("wget -O " + isbn + ".pdf \"" + aot.getBookTicketedURL(wobl_url, isbn, "21043522", System.currentTimeMillis()) + "\"");
            }
        } catch (Exception e) {
        }
    }

}
