package org.ei.common.bd;

import java.util.Map;
import java.util.HashMap;
import java.util.*;
//import org.ei.data.bd.loadtime.*;

public class BdLoadNumberHash
{
	private static Map lnMap = new HashMap();

    static
    {
	   lnMap.put("01","02");
	   lnMap.put("02","06");
	   lnMap.put("03","10");
	   lnMap.put("04","14");
	   lnMap.put("05","18");
	   lnMap.put("06","22");
	   lnMap.put("07","26");
	   lnMap.put("08","30");
	   lnMap.put("09","34");
	   lnMap.put("10","38");
	   lnMap.put("11","42");
	   lnMap.put("12","46");

    }

    public static String getWeekNumber(String month) throws Exception
    {
        return (String)lnMap.get(month);
    }


}
