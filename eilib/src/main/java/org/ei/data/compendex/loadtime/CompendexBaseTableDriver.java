package org.ei.data.compendex.loadtime;

import java.io.BufferedReader;import java.io.FileInputStream;import java.io.FileReader;import java.io.IOException;import java.io.InputStreamReader;import java.util.Hashtable;import java.util.zip.GZIPInputStream;import org.apache.oro.text.perl.Perl5Util;import org.ei.data.LoadNumber;import org.ei.util.GUID;



public class CompendexBaseTableDriver
{
    private Perl5Util perl = new Perl5Util();
    private static CompendexBaseTableWriter baseWriter;
    //private static CompendexBaseTableWriter sourceWriter;

    private static boolean fix;
    private static boolean toc;

    private static int exitNumber;
    private int counter = 0;
    private int loadNumber;

    public static void main(String args[])
        throws Exception
    {        int loadN=0;
        int sw=0;
        if(args[sw].trim().equals("-ln"))
        {
             loadN=LoadNumber.getCurrentWeekNumber();
        }
        else
        {

            if(args[sw].trim().equals("-i"))
            {
                loadN = Integer.parseInt(args[++sw]);
                if(loadN != LoadNumber.getCurrentWeekNumber())
                {

                   System.out.println("Load Number does not match the current week number:");
                   System.out.print(loadN +", Are you sure (Y/N)? ");
                   int ch = System.in.read();
                   if((Character.toLowerCase((char)ch) != 'y') ||(Character.toUpperCase((char) ch) !='Y'))
                        System.exit(1);
               }
            }
            else
                loadN = Integer.parseInt(args[sw]);
        }

        System.out.println("Load Number:"+loadN);
        String infile = args[sw+1];
        String prpfile= "cpx";
        fix=false;
        toc=false;
        boolean log=false;

        if((args.length >=(sw+3)) && (args[sw+2].equalsIgnoreCase("fix")))
        {
            fix=true;
            sw++;
        }

        if((args.length >=(sw+3)) && (args[sw+2].equalsIgnoreCase("toc")))
                {
                    toc=true;
                    sw++;
        }

        if((args.length >=(sw+3) )&& (args[sw+2].startsWith(prpfile)))
        {
            prpfile= args[sw+2];
            System.out.println(prpfile);
            sw++;
        }


        if(args.length == sw+3)
        {
            exitNumber = Integer.parseInt(args[sw+2]);
        }
        else
        {
            exitNumber = 0;
        }

        CompendexBaseTableDriver c = new CompendexBaseTableDriver(loadN);
        c.writeBaseTableFile(infile,prpfile);


    }
    public CompendexBaseTableDriver(int loadN)
    {
        this.loadNumber = loadN;

    }

    public int getCount()
    {
        return counter;
    }

    public void writeBaseTableFile(String infile,String prpfile)
        throws Exception
    {
        BufferedReader in = null;

        try
        {
            baseWriter = new CompendexBaseTableWriter(infile+"."+loadNumber+".out");
            //sourceWriter = new CompendexBaseTableWriter(infile+"."+loadNumber+".src");
            if ( infile.toLowerCase().endsWith(".gz") )
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(infile))));
            else
                in = new BufferedReader(new FileReader(infile));

            baseWriter.begin();
            //sourceWriter.begin(prpfile+".properties");
            writeRecs(in);
            baseWriter.end();
            //sourceWriter.end();
        }
        catch (IOException e)
        {
            System.err.println (e);
            System.exit(1);
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }
    private void writeRecs(BufferedReader in)
        throws Exception
    {
        Hashtable record = null;
        String state = null;
        String line = null;
        String fieldName = null;
        while((line = in.readLine()) != null)
        {
            if(exitNumber != 0 &&
               counter == exitNumber)
            {
                break;
            }


            char c = line.charAt(0);
            int i = (int)c;

            if(c == '*')
            {
                state = "endRecord";
            }
            else if(i == 32)
            {
                state = "continueField";
            }
            else
            {
                state = "beginField";
            }
            if(state.equals("beginField"))
            {
                if(line.length() < 2)
                {
                    System.out.println(line);
                }
                else
                {
                    fieldName = line.substring(0,2);
                    fieldName = fieldName.toUpperCase();
                    String data = line.substring(2, line.length());
                    if(record.containsKey(fieldName))
                    {
                        StringBuffer value = (StringBuffer)record.get(fieldName);
                        record.put(fieldName, value.append(";"+data.trim()));
                    }
                    else
                    {
                        record.put(fieldName, new StringBuffer(data.trim()));
                    }
                }
            }
            if(state.equals("endRecord"))
            {
                if(record != null && record.size() > 0)
                {
                    record.put("GU", new StringBuffer("cpx_"+new GUID().toString()));

                    record.put("LN",new StringBuffer(Integer.toString(loadNumber)));
                    if(toc)
                        record.put("RT",new StringBuffer("TOC"));
                    if((!record.containsKey("AB")) && (!fix))
                    {
                        record.put("AB",new StringBuffer("No abstract available"));

                    }
                    //System.out.println(record.toString());
                    baseWriter.writeRec(record,true);
                    //sourceWriter.writeRec(record,fix);
                    ++counter;
                }


                record = new Hashtable();
            }
            if(state.equals("continueField"))
            {
                StringBuffer value = (StringBuffer)record.get(fieldName);
                String data = line.substring(2, line.length());
                record.put(fieldName, value.append(" "+data.trim()));
            }
        }
        if(record != null &&
           record.size() > 0)
        {
            record.put("GU", new StringBuffer("cpx_"+new GUID().toString()));
            record.put("LN",new StringBuffer(Integer.toString(loadNumber)));
            if(toc)
                        record.put("RT",new StringBuffer("TOC"));
            if((!record.containsKey("AB")) && (!fix))
            {
                record.put("AB",new StringBuffer("No abstract available"));

            }
            //System.out.println(record.toString());
            baseWriter.writeRec(record,true);
            //sourceWriter.writeRec(record,fix);
            counter++;
        }
    }
}
