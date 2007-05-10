package org.ei.data.ntis;

import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;

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
		if(pa1 == null)
		{
			return null;
		}

		Perl5Util perl = new Perl5Util();
		StringBuffer buf = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(pa1, "{");
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

					return buf.toString();
				}
			}
		}

		return null;
	}



	public static String formatAuthor(String pa)
	{
		if(pa == null)
		{
			return null;
		}
		Perl5Util perl = new Perl5Util();
		StringBuffer buf = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(pa, "{");
		if(tokens.hasMoreTokens())
		{

			String initials = tokens.nextToken().trim();
			if(tokens.hasMoreTokens())
			{
				String lastname = tokens.nextToken().trim();
				lastname = perl.substitute("s/(\\.|\\,)?(\\s)*(and)?$//", lastname);

				if(lastname.length() > 0)
				{
					buf.append(lastname);
					if(initials.length() > 0)
					{
						buf.append(", ");
						buf.append(initials);
					}

					return buf.toString();
				}
			}
		}

		return null;
	}


}