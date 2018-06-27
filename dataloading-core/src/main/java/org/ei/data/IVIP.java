package org.ei.data;

import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.domain.ElementData;
import org.ei.domain.Key;


public class IVIP implements ElementData
{
	String localIssn;
	String localVolume;
	String localIssue;
	String localPage;
	String localIvip;
	Key localKey;
	boolean labels;

	private String[] numberPatterns = {"/[1-9][0-9]*/"};
	Perl5Util perl = new Perl5Util();

	public IVIP()
	{

	}

	public IVIP(Key key, String issn, String volume, String issue, String page)
	{
		localIssn = issn;
		localVolume = volume;
		localIssue = issue;
		localPage = page;
		localKey = key;
	}
	/** This method returns IVIP
	  * @return IVIP format  eg:<IVIP ISSN="abc" firstPage="11" firstVolume="11" />
	  */
	  /*
	public String buildIVIP(){
	    StringBuffer tempStr=new StringBuffer();
	    tempStr.append("<IVIP");
	    tempStr.append(buildISSN());
	    tempStr.append(buildFirstVolume());
	    tempStr.append(buildFirstIssue());
	    tempStr.append(buildFirstPage());
	    tempStr.append(">");
	    tempStr.append("</IVIP>");
	    return tempStr.toString();
	}
	*/

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String s[] = {this.localIvip};
		return s;
	}

	public void setElementData(String[] s)
	{
		this.localIvip = s[0];
	}

	public void setKey(Key akey)
	{
	  this.localKey = akey;
	}

	public Key getKey() { return this.localKey; }



	public void toXML(Writer out)
			throws IOException

		{
			out.write("<");
			out.write(this.localKey.getKey());

			out.write(buildISSN());
			out.write(buildFirstVolume());
			out.write(buildFirstIssue());
			out.write(buildFirstPage());

			out.write(">");

			out.write("</");
			out.write(this.localKey.getKey());
			out.write(">");

	}

	/** This method returns ISSN part of IVIP
	  * ISSN format is abcd-xyz which is processed
	  * by this method Removes "-" and returns abcdxyz.
	  * @return tempStr
	  */
	private String buildISSN()
	{

	    StringBuffer tempISSN=new StringBuffer();


	    tempISSN.append(" ISSN=").append("\"").append(getISSN()).append("\"");
	    return tempISSN.toString();
	}

	/** This method returns First Volume part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the volume part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstVolume()
	{
	    StringBuffer retStr=new StringBuffer();
		retStr.append(" firstVolume=\"");
		String tmpNum = getVolume();

		for(int x=0; x<numberPatterns.length; ++x)
		{
			String pattern = numberPatterns[x];
			if(perl.match(pattern, tmpNum))
			{
				MatchResult mResult = perl.getMatch();
				retStr.append(mResult.toString());
				break;
			}
		}
		retStr.append("\"");
	    return retStr.toString();
	}

	/** This method returns First Isuue part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the volume part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstIssue()
	{
	    StringBuffer retStr=new StringBuffer();
	    retStr.append(" firstIssue=\"");
	    String tmpNum = getIssue();
		for(int x=0; x<numberPatterns.length; ++x)
		{
			String pattern = numberPatterns[x];
			if(perl.match(pattern, tmpNum))
			{
				MatchResult mResult = perl.getMatch();
				retStr.append(mResult.toString());
				break;
			}
		}
		retStr.append("\"");
	    return retStr.toString();
	}

	/** This method returns First Page part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the firstpage part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstPage()
	{
		StringBuffer retStr=new StringBuffer();
		retStr.append(" firstPage=\"");
		String firstPage = null;

		StringTokenizer tmpPage = new StringTokenizer(getPageRange(),"-");
		if (tmpPage.countTokens()>0) {
			firstPage = tmpPage.nextToken();
		} else {
			firstPage = getPageRange();
		}


		for(int x=0; x<numberPatterns.length; ++x)
		{
			String pattern = numberPatterns[x];
			if(perl.match(pattern, firstPage))
			{
				MatchResult mResult = perl.getMatch();
				retStr.append(mResult.toString());
				break;
			}
		}
		retStr.append("\"");
	    return retStr.toString();
	}

	 public String getISSN()
	{
		if(localIssn != null)
		{
			if (localIssn.length() == 9)
			{
				return localIssn;
			}
			else if(localIssn.length() == 8)
			{
				return localIssn.substring(0,4)+"-"+localIssn.substring(4,8);
			}
		}

		return null;
	}

	public String getIssue()
	{
		return localIssue;
	}


	public String getVolume()
	{
		return localVolume;
	}


	public String getPageRange(){
		return localPage;
	}

}