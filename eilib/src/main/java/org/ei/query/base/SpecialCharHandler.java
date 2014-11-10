
package org.ei.query.base;



public class SpecialCharHandler
{

	public static String preprocess(String p)
	{
		int i = 32;
		char c = (char)i;
		return p.replace('(',c).replace(')', c).replace('{', c).replace('}', c).replace('*', c).trim();
	}

}