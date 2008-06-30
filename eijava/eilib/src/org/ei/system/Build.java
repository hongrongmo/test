package org.ei.system;

public class Build
{
	public static boolean china()
	{
		String loc = System.getProperty("loc");
		if(loc != null &&
		   loc.equals("china"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static String version()
	{
		return "Baja 9.2";
	}
}