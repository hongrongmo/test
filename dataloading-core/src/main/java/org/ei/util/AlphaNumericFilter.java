package org.ei.util;


public class AlphaNumericFilter
{
	public static String alphaNumeric(String s)
	{
		if(s == null)
		{
			return null;
		}

		char space = (char)32;
		char lastChar = ' ';
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			int j = (int)c;
			if((j >= 97 && j <= 122) ||
			   (j >= 65 && j <= 90)  ||
			   (j >= 48 && j <= 57))
			{
				buf.append(c);
				lastChar = c;
			}
			else if(j == space)
			{
				if(lastChar != ' ')
				{
					buf.append(c);
					lastChar = space;
				}
			}
			else
			{
				if(lastChar != ' ')
				{
					buf.append(space);
					lastChar = space;
				}

			}
		}

		return buf.toString();
	}
}