package org.ei.data.bd;

import java.util.Map;
import java.util.HashMap;
import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdCoden
{
	private static Map codenMap = new HashMap();
	private static Map reverseCodenMap = new HashMap();
	private static Map remainderMap = new HashMap();
	private static int[] nums = {11,7,5,3,1};

    static
    {
	   char letter = 'A';
       int x = (int) letter;

       for(int i = 0; i < 26; i++)
       {
		   String a = "" + (char)(x+i);
		   String b = "" + (x+i-64);

		   codenMap.put(a,new Integer(b));
		   reverseCodenMap.put(b,a);
	   }

       for(int i = 1; i < 10; i++)
       {
		   String a = ""+ i;
		   String b = ""+ (i+26);

		   codenMap.put(a,new Integer(b));
		   reverseCodenMap.put(b,a);
	   }

	   codenMap.put("0",new Integer(36));
	   reverseCodenMap.put("36","0");

       for(int i = 27; i < 34; i++)
       {
		   String a = ""+ i;
		   String b = ""+ (i-25);

		   remainderMap.put(a,new Integer(b));
	   }

	   remainderMap.put("0",new Integer(9));

    }

    public static String convertCoden(String fiveDigitCoden) throws Exception
    {
		String returnVal = null;
		Integer cint = (Integer)codenMap.get(fiveDigitCoden.substring(4,5));
		int checkDigit = 0;

		if(fiveDigitCoden.length() < 5 || cint == null)
			return null;

		for(int i = 0; i < 5; i++)
		{
			Integer m = (Integer)codenMap.get(fiveDigitCoden.substring(i,i+1));
			checkDigit += m.intValue() * nums[i];
		}

		checkDigit = checkDigit % 34;
		if(checkDigit > 0 && checkDigit < 27)
		{
			returnVal = ""+reverseCodenMap.get(""+checkDigit);
		}
		else
		{
			returnVal = ""+(Integer)remainderMap.get(""+checkDigit);
		}

        return fiveDigitCoden + returnVal;
    }

    public static void main(String[] args) throws Exception
    {
	   char letter = 'A';
       int x = (int) letter;
 	   System.out.println(BdCoden.convertCoden("48THA"));
	}

}