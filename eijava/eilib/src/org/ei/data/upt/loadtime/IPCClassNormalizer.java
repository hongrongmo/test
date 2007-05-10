package org.ei.data.upt;

import java.util.*;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.DiskMap;

public class IPCClassNormalizer {

    static char DELIM = (char) 30;
    static Perl5Util perl = new Perl5Util();

    public static void main(String args[]) {

        String test1 = "C07C69/76";

        DiskMap diskMap = new DiskMap();

        try {

            IPCClassNormalizer normalizer = new IPCClassNormalizer();

            String key = normalizer.normalize(test1);

            diskMap.openRead("C:\\elsevier\\univentio\\ipc", false);

            String val = diskMap.get(key);

            System.out.println(val);

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            try {
                diskMap.close();
            }
            catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }

    public static String trimLeadingZeroFromSubClass(String code,
    									   			 String[] subs)
    {

		code = code.replaceAll(" ", "");
		if(subs == null)
		{
			return code;
		}

		for(int i=0; i<subs.length; i++)
		{
			String sub = subs[i];
			if(sub.indexOf("0") == 0)
			{
				System.out.println("Checking:"+ code);
				if(code.indexOf(sub) > -1)
				{
					System.out.println("Removing zero for:"+code);
					String subNoLeadingZero = sub.substring(1,sub.length());
					code = code.replaceFirst(sub, subNoLeadingZero);
					return code;
				}
			}
		}

		return code;
	}

    public static String normalize(String code) throws Exception {

        if (code == null)
            return "";

        code = perl.substitute("s/\\///ig", code);

        return code;
    }

    public static List convertString2List(String sList) {

        if (sList == null)
            return new ArrayList();

        List lstVals = new ArrayList();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        return lstVals;
    }

}