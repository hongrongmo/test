package org.ei.data.paper.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.GUID;
public class PaperChemBaseTableDriver
{
    private Perl5Util perl = new Perl5Util();
    private static PaperChemBaseTableWriter baseWriter;

    private static int exitNumber;
    private int counter = 0;
    private int loadNumber;

    public static void main(String args[])
        throws Exception
    {
        int loadN = Integer.parseInt(args[0]);
        String infile = args[1];
        if(args.length == 3)
        {
            exitNumber = Integer.parseInt(args[2]);
        }
        else
        {
            exitNumber = 0;
        }

        PaperChemBaseTableDriver c = new PaperChemBaseTableDriver(loadN);
        c.writeBaseTableFile(infile);
    }
    public PaperChemBaseTableDriver(int loadN)
    {
        this.loadNumber = loadN;
    }

    public void writeBaseTableFile(String infile)
        throws Exception
    {
        BufferedReader in = null;

        try
        {
            baseWriter = new PaperChemBaseTableWriter(infile+".out");
            in = new BufferedReader(new FileReader(infile));
            baseWriter.begin();
            writeRecs(in);
            baseWriter.end();
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
                if(record != null)
                {
                    record.put("GU", new StringBuffer("pch_"+new GUID().toString()));
                    record.put("LN",new StringBuffer(Integer.toString(loadNumber)));
                    baseWriter.writeRec(record);
                    ++counter;
                }

                record = new Properties();
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
            record.put("GU", new StringBuffer("pch_"+new GUID().toString()));
            record.put("LN",new StringBuffer(Integer.toString(loadNumber)));
            baseWriter.writeRec(record);
        }
    }
}
