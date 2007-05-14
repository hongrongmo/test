
package org.ei.data.compendex.loadtime;

import java.io.*;
import java.util.*;

class CpxAtomicReader
{
        private static final char CONT =32,REC='*',EOF=65535;

        BufferedReader  in;
        public CpxAtomicReader(Reader r) throws Exception
        {
            this.in = new BufferedReader(r);
            if(in.readLine().charAt(0)==REC)
            {
                System.out.println("File OK");
            }

        }

        public Hashtable readRecord()
                throws IOException
        {
            char ch;
            String fieldName = new String();
            StringBuffer buffer = new StringBuffer();
            Hashtable record = new Hashtable();
            String line;

            if(!in.ready())
            {
                return null;
            }

            //while((line=in.readLine()).charAt(0)!=REC)
            while(true)
            {
				line=in.readLine();
				if(line == null || line.length()<1)
				{
					continue;
				}
				else if(line.charAt(0)==REC)
				{
					break;
				}

                if(line.charAt(0)!=CONT)
                {
					fieldName = line.substring(0,2).trim().toUpperCase();
					String data = line.substring(3, line.length()).trim();
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
                else
                {
                    StringBuffer value = (StringBuffer)record.get(fieldName);
                    String data = line.substring(2, line.length());
                    record.put(fieldName, value.append(" "+data.trim()));
                }
                if(!in.ready())
                {
                  break;
                }
            }
          	return record;
        }

        public static void main(String[] args) throws Exception
        {
            CpxAtomicReader test = new CpxAtomicReader(new FileReader(new File(args[0])));
            Hashtable rec=null;
            while((rec=test.readRecord())!=null)
            {
                System.out.println(rec.toString().replaceAll(", ([A-Z][A-Z]=)","\n$1")+"\n\n\n");
            }
        }
}




