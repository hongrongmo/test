/*
 * Created on Sep 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.bulletins;

import java.io.*;
//12/22/2014 from eijava 
import java.text.SimpleDateFormat;
import java.text.DateFormat;
//
import org.apache.oro.text.perl.Perl5Util;
import java.util.*;
import java.util.regex.Pattern;

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

  //12/22/2014 from eijava  
    public BulletinTableDriver(int weekNum, String database, String action)
    {
            this.weekNumber=weekNum;
            try
            {
                //Calendar cal = Calendar.getInstance();
                //int month = cal.get(cal.MONTH) + 1;
                //out = new PrintWriter(new FileWriter("c:\\bulletins" + month + cal.get(cal.DAY_OF_MONTH) + cal.get(cal.YEAR) + ".out"));
                out = new PrintWriter(new FileWriter("bulletins_"+database+"_"+action+"_"+weekNum+".out"));
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
  //  
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
            
          //HH 01/21/2015 update to be as  eijava
            if(db != null)                              
            {
            	String pubDt = ((String) htFields.get("CREADATE")).trim();
            	String pubYr = "";
            	if (perl.match("/[0-9][0-9][0-9][0-9]/", pubDt)) {
            		pubYr = perl.getMatch().toString();
            	}

            	if (db.equalsIgnoreCase("apilit") || db.equalsIgnoreCase("aplit"))
                processLITRecord(htFields);
            	else if (db.equalsIgnoreCase("apipat"))
                processPATRecord(htFields);
            	else {
                System.err.println("Dataset not found!");
            	}
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
                    //writeBaseTableFile(directory + "\\" + dirArray[i]);   //original
                	 writeBaseTableFile(directory + "/" + dirArray[i]);    //HH 01/21/2015 from eijava

            }
        } finally {
            if (out != null)
                out.close();
        }

    }

    public static void main(String[] args) {
        //12/22/2014 from eijava
        if(args.length<4)
        {
            System.out.println("not enough parameter");
            System.out.println("Usage:  data_directory weekNumber database action");
            System.exit(1);
        }
        //
        String directory = args[0];

        int weekNum = Integer.parseInt(args[1]);
        
      //12/22/2014 from eijava
        String database = args[2];
        String action = args[3];
        int year = Integer.parseInt(args[4]);				//hteleb added 12/16/2020 to fix month at year switch
        
        String date = "";
        if(args.length>5)
        {
            date = args[5];
        }
        //
        
        //BulletinTableDriver driver = new BulletinTableDriver(weekNum);   //original
        BulletinTableDriver driver = new BulletinTableDriver(weekNum,database,action);   //HH 01/21/2015 from eijava
        // directory = "C:\\elsevier\\docs\\release\\encompassweb\\bulletin\\label_files";
        //12/22/2014 from eijava
        driver.deleteLabelFile(directory);
        driver.createLabelFile(directory,weekNum,database,action,year,date);
        //
        driver.startLoad(directory, weekNum);

    }

  //12/22/2014 from eijava  
    private void deleteLabelFile(String directory)
    {
        try
        {
            File dir = new File(directory);
            File[] toBeDeleted = dir.listFiles();

            for (File f : toBeDeleted) {
                if(f.getName().endsWith(".lbl"))
                {
                    f.delete();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //HH added year 12/16/2020 to getDate()
    private void createLabelFile(String directory,int weekNum,String database,String action,int year, String date)
    {
        Enumeration filenames = mappings.propertyNames();
        FileWriter newFile=null;
        try
        {

         while (filenames.hasMoreElements())
         {
            String labelName =(String)filenames.nextElement();
            String fullName = mappings.getProperty(labelName);
            String filename = labelName.toLowerCase()+weekNum+".lbl";

            if((database.equals("lit") && action.equals("weekly")  && (filename.indexOf("autmot")>-1 || filename.indexOf("catyst")>-1 ||filename.indexOf("helenv")>-1 || filename.indexOf("petref")>-1 ||filename.indexOf("lptsub")>-1)) ||
               (database.equals("lit") && action.equals("monthly") && (filename.indexOf("lfufrm")>-1 ||filename.indexOf("natgas")>-1 || filename.indexOf("litofc")>-1 || filename.indexOf("transt")>-1 || filename.indexOf("ltribo")>-1 )))

            {
                if(action.equals("monthly") && weekNum>12){
                    System.out.println("*** Month number can not over 12 ***");
                    System.out.println("*** No Label file created        ***");
                    System.exit(1);
                }
                newFile = new FileWriter(directory+"/"+filename);
                newFile.write("Dataset               : APILIT\n");
                newFile.write("Markname              : APILIT\n");
                newFile.write("Ordname               : LIT "+fullName.substring(0, 1).toUpperCase() + fullName.substring(1)+"\n");
                newFile.write("Custcode              : EEI\n");
                if(date!=null && date!="")
                {
                    newFile.write("Creadate              : "+date+"\n");
                }
                else
                {
                	
                    newFile.write("Creadate              : "+getDate(year)+"\n");

                }
                newFile.write("Items processed       : CURRENTLY NOT AVAILABLE\n");
                newFile.write("NrFiles               : 3\n");
                newFile.write("File1                 : "+labelName+weekNum+".HTM\n");
                newFile.write("File2                 : "+labelName+weekNum+".PDF\n");
                newFile.write("File3                 : "+labelName+weekNum+".ZIP\n");
                newFile.write("Labelnr               : "+labelName+weekNum);
            }
            else if((database.equals("pat") && action.equals("weekly") && (filename.indexOf("catzeo")>-1 || filename.indexOf("chmpro")>-1 || filename.indexOf("envtrn")>-1 || filename.indexOf("petprc")>-1 || filename.indexOf("petspe")>-1 || filename.indexOf("pptsub")>-1)) ||
                    (database.equals("pat") && action.equals("monthly") && (filename.indexOf("patofc")>-1 || filename.indexOf("pfufrm")>-1 || filename.indexOf("ptribo")>-1)))
            {
                if(action.equals("monthly") && weekNum>12){
                    System.out.println("*** Month number can not over 12 ***");
                    System.out.println("*** No Label file created        ***");
                    System.exit(1);
                }
                newFile = new FileWriter(directory+"/"+filename);
                newFile.write("Dataset               : APIPAT\n");
                newFile.write("Markname              : APIPAT\n");
                newFile.write("Ordname               : PAT "+fullName.substring(0, 1).toUpperCase() + fullName.substring(1)+"\n");
                newFile.write("Custcode              : EEI\n");
                if(date!=null && date!="")
                {
                    newFile.write("Creadate              : "+date+"\n");
                }
                else
                {
                    newFile.write("Creadate              : "+getDate(year)+"\n");
                }
                newFile.write("Items processed       : CURRENTLY NOT AVAILABLE\n");
                newFile.write("NrFiles               : 3\n");
                newFile.write("File1                 : "+labelName+weekNum+".HTM\n");
                newFile.write("File2                 : "+labelName+weekNum+".PDF\n");
                newFile.write("File3                 : "+labelName+weekNum+".ZIP\n");
                newFile.write("Labelnr               : "+labelName+weekNum);
            }
            if(newFile!=null)
                newFile.close();

        }
    }
    catch(Exception e)
    {
        e.printStackTrace();
        try{
            if(newFile!=null)
            {
                newFile.close();
            }
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }
    }
    }

    private String getDate(int year)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
       
        /*	HH added 12/16/2020 usually newstar send 2-3 weeks bulletins of next year in dec of this year
			which in turn shows wrong publication date in EV display i.e. 15-Dec-2021 though we still in DEC 2020, it should show Jan 2021
        */
       
        int currYR = Calendar.getInstance().get(Calendar.YEAR);
      	 // Only add 1 more month at the time of switch between curr and next year
        	if(currYR != year)
        	{
        		 Calendar cal = Calendar.getInstance();
                 cal.add(Calendar.MONTH, 1);
                 date = cal.getTime();
        	}
        
        return dateFormat.format(date);

    }
    
   // 
    class LabelFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.endsWith(".lbl"))
                return true;
            else
                return false;
        }
    }
}
