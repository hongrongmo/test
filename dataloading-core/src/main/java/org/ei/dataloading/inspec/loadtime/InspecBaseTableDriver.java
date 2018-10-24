package org.ei.dataloading.inspec.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.GUID;
import org.ei.common.inspec.*;

public class InspecBaseTableDriver
{

    private Perl5Util perl = new Perl5Util();

    private static InspecBaseTableWriter baseWriter;



    private static int exitNumber;

    private int counter = 0;

    private int loadNumber = 0;
    private static String filename;


    private static Hashtable<String, String> tagmap = new Hashtable<String, String>();

    {
        tagmap.put("001","anum");
        tagmap.put("005","adate");
        tagmap.put("010","rtype");
        tagmap.put("020","cpr");
        tagmap.put("100","ti");
        tagmap.put("110","ab");
        tagmap.put("120","cls");
        tagmap.put("130","cvs");
        tagmap.put("131","fls");
        tagmap.put("132","trmc");
        tagmap.put("133","ndi");
        tagmap.put("135","chi");
        tagmap.put("137","aoi");
        tagmap.put("150","thlp");
        tagmap.put("151","ttj");
        tagmap.put("152","fjt");
        tagmap.put("153","fttj");
        tagmap.put("160","la");
        tagmap.put("170","tc");
        tagmap.put("180","ac");
        tagmap.put("200","aus");
        tagmap.put("210","eds");
        tagmap.put("220","trs");
        tagmap.put("300","abnum");
        tagmap.put("310","cn");
        tagmap.put("311","cnt");
        tagmap.put("312","sn");
        tagmap.put("313","snt");
        tagmap.put("315","cccc");
        tagmap.put("318","matid");
        tagmap.put("320","sbn");
        tagmap.put("330","rnum");
        tagmap.put("340","ugchn");
        tagmap.put("350","cnum");
        tagmap.put("360","pnum");
        tagmap.put("370","opan");
        tagmap.put("380","sici");
        tagmap.put("381","sicit");
        tagmap.put("395","dnum");
        tagmap.put("399","doi");
        tagmap.put("400","voliss");
        tagmap.put("401","volisst");
        tagmap.put("450","partno");
        tagmap.put("460","amdref");
        tagmap.put("500","cloc");
        tagmap.put("510","ppub");
        tagmap.put("520","cpat");
        tagmap.put("530","copa");
        tagmap.put("540","cpub");
        tagmap.put("541","cpubt");
        tagmap.put("600","npl1");
        tagmap.put("610","npl2");
        tagmap.put("620","ipn");
        tagmap.put("621","ipnt");
        tagmap.put("630","xrefno");
        tagmap.put("640","um");
        tagmap.put("650","url");
        tagmap.put("660","dcurl");
        tagmap.put("700","aaff");
        tagmap.put("710","eaff");
        tagmap.put("730","pas");
        tagmap.put("740","pub");
        tagmap.put("750","iorg");
        tagmap.put("760","sorg");
        tagmap.put("770","avail");
        tagmap.put("780","price");
        tagmap.put("800","cdate");
        tagmap.put("810","pdate");
        tagmap.put("811","tdate");
        tagmap.put("820","fdate");
        tagmap.put("830","ppdate");
    }



    public static void main(String args[])
        throws Exception
    {

        int loadN = Integer.parseInt(args[0]);
        String infile = args[1];
        String type;

        if(args.length == 3)
        {
             type=args[2];
        }
        else
        {
             type="ASC";
        }
        if(args.length == 4)
        {
            exitNumber = Integer.parseInt(args[3]);
        }
        else
        {
            exitNumber = 0;
        }

        InspecBaseTableDriver c = new InspecBaseTableDriver(loadN);
        c.writeBaseTableFile(infile,type);

    }

    public InspecBaseTableDriver(int loadN)
    {
        this.loadNumber = loadN;
    }

    public void writeBaseTableFile(String infile)
	        throws Exception
    {
		String type="XML";
		writeBaseTableFile(infile,type);
	}

    public void writeBaseTableFile(String infile,String type)
        throws Exception
    {
        BufferedReader in = null;
        ZipInputStream zin=null;
        ZipEntry entry=null;
        
        try
        {

            if ( infile.toUpperCase().endsWith(".ZIP") )
            {
                zin = new ZipInputStream(new FileInputStream(new File(infile)));

                baseWriter = new InspecBaseTableWriter(infile+".out",type);
                baseWriter.begin();
                while((entry=zin.getNextEntry())!=null)
                {
                    filename=entry.getName();

                    if(entry.getName().toUpperCase().endsWith("."+type.toUpperCase()))
                    {

                        System.out.println("Processing XML ...");
                        in = new BufferedReader(new InputStreamReader(zin));


                        if(type.equalsIgnoreCase("XML"))
                        {                        	
                            writeRecs(new InspecXMLReader(in));
                        }
                        else
                        {

                            writeRecs(in);
                        }

                    }

                    zin.closeEntry();

                }
                baseWriter.end();

         }
        else
            {
                baseWriter = new InspecBaseTableWriter(infile+".out",type);
                if ( infile.toUpperCase().endsWith(".GZ") )
                    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(infile)))));
                else
                    in = new BufferedReader(new FileReader(new File(infile)));

                baseWriter.begin();
                if(type.equalsIgnoreCase("XML"))
                {
                    writeRecs(new InspecXMLReader(in));
                }
                else
                {
                    writeRecs(in);
                }
                baseWriter.end();
            }


    }catch(Exception e){
        e.printStackTrace();

}

    }

    private void writeRecs(BufferedReader in)
        throws Exception
    {

        Hashtable record = null;
        String state = null;
        String line = null;
        String fieldName = null;
        String newtag = null;
        boolean ignore = false;
        while((line = in.readLine()) != null)
        {

            if(exitNumber != 0 && counter > exitNumber)
            {
                break;
            }
            record = new Hashtable();

            MARC marc = new MARC(line);
            //Get the anum

            int col = 1;
            while (marc.hasMoreFields())
            {
                Field field = marc.nextField();

                String tag = field.getTag();
                int value = Integer.parseInt(tag);
                if (value <= 9)
                {
                    newtag = (String) tagmap.get(tag);
                    String temp = unicodeMap(field.nextSubField().getValue());
                    if (newtag != null)
                    {

                        if (newtag.equalsIgnoreCase("ANUM"))
                        {
                            record.put(newtag.toUpperCase(),
                                      new StringBuffer(temp));
                        }


                        if (newtag.equalsIgnoreCase("ADATE"))
                        {
                            record.put(newtag.toUpperCase(),
                            new StringBuffer(temp));
                        }
                    }
                }
                else
                {
                    newtag = (String) tagmap.get(tag);
                    StringBuffer valueBuf = new StringBuffer();

                    while (field.hasMoreSubFields())
                    {
                        SubField subfield = field.nextSubField();
                        char type = subfield.getType();
                        String temp = unicodeMap(subfield.getValue());

                        if (newtag != null)
                        {

                            if (field.hasMoreSubFields())
                            {

                                valueBuf.append(temp + ";");
                            }
                            else
                            {
                                valueBuf.append(temp);
                            }

                        }
                    }

                    if (newtag != null)
                    {
                        record.put(newtag.toUpperCase(), valueBuf);
                    }

                }
            }

            if (!ignore)
            {
                if (!(record.containsKey("LA")))
                {
                    record.put("LA", new StringBuffer("English"));
                }


                if (record.containsKey("PDATE"))
                {
                    record.put("LOAD_NUMBER", new StringBuffer(Integer.toString(this.loadNumber)));
                    record.put("M_ID", new StringBuffer("inspec_"+(new GUID()).toString()));
                    baseWriter.writeRec(record);
                    counter++;

                }
            }

        }
    }
    private void writeRecs(InspecXMLReader r) throws Exception
    {

        Hashtable<String,StringBuffer> record = null;
        while((record = r.getRecord())!=null )
        {
            if(exitNumber != 0 && counter > exitNumber)
            {
                break;
            }
            String articletype = record.get(InspecXMLReader.ARTICLETYPE).toString();
           
            if (articletype.equals(InspecXMLReader.CURRENT_DATA))
            {           	
                record.put("LOAD_NUMBER", new StringBuffer(Integer.toString(this.loadNumber)));
                record.put("M_ID", new StringBuffer("inspec_"+(new GUID()).toString()));
                baseWriter.writeRec(record);
                counter++;
            }

        }

    }


    private static String unicodeMap(String txt) {
            int len = txt.length();
            StringBuffer sb = new StringBuffer();
            char c;

            for (int i = 0; i < len; i++) {
                c = txt.charAt(i);
                switch (c) {
                    case '\u00A1':
                        sb.append('\u0141');
                        break;
                    case '\u00A2':
                        sb.append('\u00D8');
                        break;
                    case '\u00A3':
                        sb.append('\u0110');
                        break;
                    case '\u00A4':
                        sb.append('\u00DE');
                        break;
                    case '\u00A5':
                        sb.append('\u00C6');
                        break;
                    case '\u00A6':
                        sb.append('\u008C');
                        break;
                    case '\u00A7':
                        sb.append('\u00B4');
                        break;
                    case '\u00A8':
                        sb.append('\u00B7');
                        break;
                    case '\u00A9':
                        sb.append('\u266D');
                        break;
                    case '\u00AA':
                        sb.append('\u00AE');
                        break;
                    case '\u00AB':
                        sb.append('\u00B1');
                        break;
                    case '\u00AC':
                        sb.append('\u01A0');
                        break;
                    case '\u00AD':
                        sb.append('\u01AF');
                        break;
                    case '\u00AE':
                        sb.append('\u02BE');
                        break;
                    case '\u00B0':
                        sb.append('\u02BF');
                        break;
                    case '\u00B1':
                        sb.append('\u0142');
                        break;
                    case '\u00B2':
                        sb.append('\u00F8');
                        break;
                    case '\u00B3':
                        sb.append('\u0111');
                        break;
                    case '\u00B4':
                        sb.append('\u00FE');
                        break;
                    case '\u00B5':
                        sb.append('\u00E6');
                        break;
                    case '\u00B6':
                        sb.append('\u009C');
                        break;
                    case '\u00B7':
                        sb.append('\u02BA');
                        break;
                    case '\u00B8':
                        sb.append('\u0131');
                        break;
                    case '\u00B9':
                        sb.append('\u00A3');
                        break;
                    case '\u00BA':
                        sb.append('\u00F0');
                        break;
                    case '\u00BC':
                        sb.append('\u01A1');
                        break;
                    case '\u00BD':
                        sb.append('\u01B0');
                        break;
                    case '\u00C0':
                        sb.append('\u00B0');
                        break;
                    case '\u00C1':
                        sb.append('\u2113');
                        break;
                    case '\u00C2':
                        sb.append('\u2117');
                        break;
                    case '\u00C3':
                        sb.append('\u00A9');
                        break;
                    case '\u00C4':
                        sb.append('\u266F');
                        break;
                    case '\u00C5':
                        sb.append('\u00BF');
                        break;
                    case '\u00C6':
                        sb.append('\u00A1');
                        break;
                    case '\u00E0':
                        sb.append('\u0309');
                        break;
                    case '\u00E1':
                        sb.append('\u0300');
                        break;
                    case '\u00E2':
                        sb.append('\u0301');
                        break;
                    case '\u00E3':
                        sb.append('\u0302');
                        break;
                    case '\u00E4':
                        sb.append('\u0303');
                        break;
                    case '\u00E5':
                        sb.append('\u0304');
                        break;
                    case '\u00E6':
                        sb.append('\u0306');
                        break;
                    case '\u00E7':
                        sb.append('\u0307');
                        break;
                    case '\u00E8':
                        sb.append('\u0308');
                        break;
                    case '\u00E9':
                        sb.append('\u030C');
                        break;
                    case '\u00EA':
                        sb.append('\u030A');
                        break;
                    case '\u00EB':
                        sb.append('\uFE20');
                        break;
                    case '\u00EC':
                        sb.append('\uFE21');
                        break;
                    case '\u00ED':
                        sb.append('\u0315');
                        break;
                    case '\u00EE':
                        sb.append('\u030B');
                        break;
                    case '\u00EF':
                        sb.append('\u0310');
                        break;
                    case '\u00F0':
                        sb.append('\u0327');
                        break;
                    case '\u00F1':
                        sb.append('\u0328');
                        break;
                    case '\u00F2':
                        sb.append('\u0323');
                        break;
                    case '\u00F3':
                        sb.append('\u0324');
                        break;
                    case '\u00F4':
                        sb.append('\u0325');
                        break;
                    case '\u00F5':
                        sb.append('\u0333');
                        break;
                    case '\u00F6':
                        sb.append('\u0332');
                        break;
                    case '\u00F7':
                        sb.append('\u0326');
                        break;
                    case '\u00F8':
                        sb.append('\u031C');
                        break;
                    case '\u00F9':
                        sb.append('\u032E');
                        break;
                    case '\u00FA':
                        sb.append('\uFE22');
                        break;
                    case '\u00FB':
                        sb.append('\uFE23');
                        break;
                    case '\u00FE':
                        sb.append('\u0313');
                        break;
                    case '\u001E':
                        // Get rid of any end of field
                        //sb.append('');
                        break;
                    case '\r':
                    case '\n':
                        // Escape '
                        //sb.append('');
                        //sb.append('\'');
                        break;
                    case '"':
                        // Escape '
                        sb.append('"');
                    //sb.append('"');
                    //break;
                    default:
                        sb.append(c);
                }
            }
            return sb.toString();
    }




}
