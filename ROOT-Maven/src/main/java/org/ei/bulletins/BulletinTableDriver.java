/*
 * Created on Sep 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.bulletins;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;
import java.util.*;

/**
 * @author KFokuo
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulletinTableDriver {

    private Perl5Util perl = new Perl5Util();
    private static Properties mappings = new Properties();
    PrintWriter out = null;
    int weekNumber;

    static {

        mappings.put("AUTMOT", "automotive");
        mappings.put("CATYST", "catalysys-zeolites:catalysts-zeolites");
        mappings.put("CATZEO", "catalysys-zeolites:catalysts-zeolites");
        mappings.put("CHMPRO", "chemical_products");
        mappings.put("ENVTRN", "environment_transport_storage");
        mappings.put("LFUFRM", "fuel_reformation:fuel_reformulation");
        mappings.put("PFUFRM", "fuel_reformation:fuel_reformulation");
        mappings.put("HELENV", "health_environment");
        mappings.put("NATGAS", "natural_gas");
        mappings.put("LITOFC", "oilfield_chemicals");
        mappings.put("PATOFC", "oilfield_chemicals");
        mappings.put("PETPRC", "petroleum_processes");
        mappings.put("PETREF", "petroleum_refining_petrochemicals");
        mappings.put("PETSPE", "petroleum_speciality_products");
        mappings.put("PPTSUB", "petroleum_substitutes");
        mappings.put("LPTSUB", "petroleum_substitutes");
        mappings.put("POL", "polymers");
        mappings.put("TRANST", "transportation_storage");
        mappings.put("PTRIBO", "tribology");
        mappings.put("LTRIBO", "tribology");
    }

    public BulletinTableDriver(int weekNum) {
        this.weekNumber = weekNum;
        try {
            // Calendar cal = Calendar.getInstance();
            // int month = cal.get(cal.MONTH) + 1;
            // out = new PrintWriter(new FileWriter("c:\\bulletins" + month + cal.get(cal.DAY_OF_MONTH) + cal.get(cal.YEAR) + ".out"));
            out = new PrintWriter(new FileWriter("bulletins_" + weekNum + ".out"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeRecs(BufferedReader in, String file) {

        String line = null;
        Hashtable<String, String> htFields = new Hashtable<String, String>();

        try {

            while ((line = in.readLine()) != null) {

                List<String> parms = new ArrayList<String>();
                if (!line.trim().equals("")) {

                    perl.split(parms, "/:/", line);
                    htFields.put(parms.get(0).toString().trim().toUpperCase(), parms.get(1).toString().trim());

                }

                parms.clear();
            }
            String db = (String) htFields.get("DATASET");
            String pubDt = ((String) htFields.get("CREADATE")).trim();
            if (perl.match("/[0-9][0-9][0-9][0-9]/", pubDt)) {
            }

            if (db.equalsIgnoreCase("apilit") || db.equalsIgnoreCase("aplit"))
                processLITRecord(htFields);
            else if (db.equalsIgnoreCase("apipat"))
                processPATRecord(htFields);
            else {
                System.err.println("Dataset not found!");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }

    }

    public void processLITRecord(Hashtable<String, String> htFields) {

        String db = "1";
        String pubDt = ((String) htFields.get("CREADATE")).trim();

        String fileName = ((String) htFields.get("LABELNR")).trim().toLowerCase();
        String label = "";
        String pubYr = "";
        String newCy = "";
        String zipFileName = "";

        if (perl.match("/[0-9][0-9][0-9][0-9]/", pubDt))
            pubYr = perl.getMatch().toString();
        if (perl.match("/[A-Za-z]+/", fileName))
            label = perl.getMatch().toString().toUpperCase().trim();

        newCy = mappings.getProperty(label);

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/:/", newCy);

        if (lstTokens.size() == 2) {

            if (db.equals("1")) // LIT
                newCy = (String) lstTokens.get(0);

            else if (db.equals("2")) // PAT
                newCy = (String) lstTokens.get(1);

        }

        String day = "";
        if (perl.match("/[0-9][0-9]-/", pubDt)) {
            day = perl.getMatch().toString();
            day = perl.substitute("s/-//g", day);
        }

        out.println(db + "\t" + newCy.trim() + "\t" + pubYr.trim() + "\t" + pubDt.trim() + "\t" + fileName.trim() + "\t" + zipFileName.trim() + "\t"
            + weekNumber);

    }

    private String replaceNull(Object sVal) {

        if (sVal == null)
            return "";

        return sVal.toString();
    }

    public void processPATRecord(Hashtable<String, String> htFields) {

        String db = "2";
        String pubDt = ((String) htFields.get("CREADATE")).trim();
        String fileName = ((String) htFields.get("LABELNR")).trim().toLowerCase();
        String file1 = ((String) replaceNull(htFields.get("FILE1")).trim().toLowerCase());
        String file2 = ((String) replaceNull(htFields.get("FILE2")).trim().toLowerCase());
        String file3 = ((String) replaceNull(htFields.get("FILE3")).trim().toLowerCase());
        String label = "";
        String pubYr = "";
        String newCy = "";
        String zipFileName = "";

        if (perl.match("/[0-9][0-9][0-9][0-9]/", pubDt))
            pubYr = perl.getMatch().toString();
        if (perl.match("/[a-zA-Z]+/", fileName.toLowerCase()))
            label = perl.getMatch().toString().toUpperCase().trim();

        if (file1.endsWith(".zip"))
            zipFileName = file1.substring(0, file1.length() - 4);
        else if (file2.endsWith(".zip"))
            zipFileName = file1.substring(0, file2.length() - 4);
        else if (file3.endsWith(".zip"))
            zipFileName = file1.substring(0, file3.length() - 4);

        newCy = mappings.getProperty(label);

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/:/", newCy);

        if (lstTokens.size() == 2) {

            if (db.equals("1")) // LIT
                newCy = (String) lstTokens.get(0);

            else if (db.equals("2")) // PAT
                newCy = (String) lstTokens.get(1);

        }

        String day = "";
        if (perl.match("/[0-9][0-9]-/", pubDt)) {
            day = perl.getMatch().toString();
            day = perl.substitute("s/-//g", day);
        }

        out.println(db + "\t" + newCy.trim() + "\t" + pubYr.trim() + "\t" + pubDt.trim() + "\t" + fileName.trim() + "\t" + zipFileName.trim() + "\t"
            + weekNumber);

    }

    private void writeBaseTableFile(String file) {

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            writeRecs(in, file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void startLoad(String directory, int iWeekNum) {

        String[] dirArray = new File(directory).list(new LabelFileFilter());

        try {

            int iWeekNum2 = 0;

            for (int i = 0; i < dirArray.length; i++) {

                if (perl.match("/[0-9]+/", dirArray[i]))
                    iWeekNum2 = Integer.parseInt(perl.getMatch().toString());

                if (iWeekNum == iWeekNum2)
                    writeBaseTableFile(directory + "\\" + dirArray[i]);

            }
        } finally {
            if (out != null)
                out.close();
        }

    }

    public static void main(String[] args) {

        String directory = args[0];

        int weekNum = Integer.parseInt(args[1]);
        BulletinTableDriver driver = new BulletinTableDriver(weekNum);
        // directory = "C:\\elsevier\\docs\\release\\encompassweb\\bulletin\\label_files";
        driver.startLoad(directory, weekNum);

    }

    class LabelFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.endsWith(".lbl"))
                return true;
            else
                return false;
        }
    }
}
