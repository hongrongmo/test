package org.ei.common.ntis;

import java.util.StringTokenizer;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.Constants;

public class NTISAuthor
{

	public static String formatAuthors(String pa1,
				   String pa2,
				   String pa3,
				   String pa4,
				   String pa5,
				   String HN)
	{
		StringBuffer authors = new StringBuffer();
		Perl5Util perl = new Perl5Util();

		String pa1f = formatFirstAuthor(pa1);
		if(pa1f != null)
		{
			authors.append(pa1f);
		}

		String pa2f = formatAuthor(pa2);
		if(pa2f != null)
		{
			authors.append("; ");
			authors.append(pa2f);
		}

		String pa3f = formatAuthor(pa3);
		if(pa3f != null)
		{
			authors.append("; ");
			authors.append(pa3f);
		}


		String pa4f = formatAuthor(pa4);
		if(pa4f != null)
		{
			authors.append("; ");
			authors.append(pa4f);
		}

		String pa5f = formatAuthor(pa5);
		if(pa5f != null)
		{
			authors.append("; ");
			authors.append(pa5f);
		}

		if(authors.length() > 0)
		{
			return formatAdditionalAuthors(authors.toString(),
								     	   HN);
		}

		return null;
	}


	public static String formatAdditionalAuthors(String firstGroup,
												 String additionalHN)
	{
		//System.out.println("firstGroup= "+firstGroup);
		//System.out.println("additionalHN= "+additionalHN);
		if(additionalHN == null ||
		   additionalHN.trim().length() == 0)
		{
			return firstGroup;
		}

		Perl5Util perl = new Perl5Util();


		StringBuffer buf = new StringBuffer();
		additionalHN = perl.substitute("s#%AUT:##i", additionalHN);
		StringTokenizer aus = new StringTokenizer(additionalHN,";");
		while(aus.hasMoreTokens())
		{
			String author = aus.nextToken();
			StringTokenizer nameTokens = new StringTokenizer(author,"/");
			if(nameTokens.hasMoreTokens())
			{
				String initials = nameTokens.nextToken().trim();
				String lastName = "";

				if(buf.length() > 0)
				{
					buf.append(";");
				}

				if(nameTokens.hasMoreTokens())
				{
					lastName = nameTokens.nextToken().trim();

					buf.append(lastName);
					buf.append(", ");
					buf.append(initials);
				}
				else
				{
					buf.append(initials);
				}
			}
		}

		if(buf.length() == 0)
		{
			return firstGroup;
		}

		return firstGroup+";"+buf.toString();
	}


	public static String formatFirstAuthor(String pa1)
	{
		//System.out.println("formatFirstAuthor_input= "+pa1);
		if(pa1 == null)
		{
			return null;
		}

		Perl5Util perl = new Perl5Util();
		StringBuffer buf = new StringBuffer();
		String[] authorArray = pa1.split(Constants.AUDELIMITER);
		for(int i=0;i<authorArray.length;i++)
		{
			String singlePa = authorArray[i];
			StringTokenizer tokens = new StringTokenizer(singlePa, "{");
			if(tokens.hasMoreTokens())
			{
				String initials = tokens.nextToken().trim();
				if(tokens.hasMoreTokens())
				{
					String lastname = tokens.nextToken().trim();
					initials = perl.substitute("s#^by##i", initials);
					lastname = perl.substitute("s# and##i", lastname);
					lastname = perl.substitute("s#,##i", lastname);
					lastname = perl.substitute("s#\\.$##i", lastname);
					if(lastname.length() > 0)
					{
						buf.append(lastname.trim());
						if(initials.length() > 0)
						{
							buf.append(", ");
							buf.append(initials.trim());
						}
						if(i>authorArray.length-1)
						{
							buf.append("; ");
						}
					}
					//System.out.println("formatFirstAuthor_output= "+buf.toString());
				}
			}
		}
		if(buf.length()>0)
		{
			return buf.toString();
		}
		else
		{
			return null;
		}
	}



	public static String formatAuthor(String pa)
	{
		if(pa == null)
		{
			return null;
		}

		//System.out.println("formatAuthor_input= "+pa);
		Perl5Util perl = new Perl5Util();
		StringBuffer buf = new StringBuffer();
		String[] authorArray = pa.split(Constants.AUDELIMITER);
		
		for(int i=0;i<authorArray.length;i++)
		{
			String singlePa = authorArray[i];
			StringTokenizer tokens = new StringTokenizer(singlePa, "{");
			if(tokens.hasMoreTokens())
			{

				String initials = tokens.nextToken().trim();
				//System.out.println("INITIALS="+initials);
				if(tokens.hasMoreTokens())
				{
					String lastname = tokens.nextToken().trim();
					//System.out.println("LAST NAME="+lastname);
					lastname = perl.substitute("s/(\\.|\\,)?(\\s)*(and)?$//", lastname);

					if(lastname.length() > 0)
					{
						buf.append(lastname);
						if(initials.length() > 0)
						{
							buf.append(", ");
							buf.append(initials);
						}


					}
				}
			}
			if(i<authorArray.length-1)
			{
				buf.append("; ");
			}
		}

		//System.out.println("formatAuthor_output= "+buf.toString());

		if(buf.length()>0)
		{
			return buf.toString();
		}
		else
		{
			return null;
		}

	}


}
