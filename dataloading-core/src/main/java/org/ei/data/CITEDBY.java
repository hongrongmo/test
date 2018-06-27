package org.ei.data;

import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.domain.ElementData;
import org.ei.domain.Key;

public class CITEDBY implements ElementData
{
	String localIssn;
	String localIsbn;
	String localIsbn13;
	String localVolume;
	String localIssue;
	String localPage;
	String localCitedby;
	String localDoi;
	String localPii;
	String localFirstAuthorSurname;
	String localFirstInitialFirstAuthor;
	String localAccessionNumber;

	Key localKey;
	boolean labels;

	private String[] numberPatterns = {"/[1-9][0-9]*/"};
	Perl5Util perl = new Perl5Util();

	public CITEDBY()
	{

	}

	public CITEDBY(Key key, String doi)
	{
		localDoi = doi;
	}

	public CITEDBY(Key key, String issn, String volume, String issue, String page)
	{
		localIssn = issn;
		localVolume = volume;
		localIssue = issue;
		localPage = page;
		localKey = key;
	}


	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String s[] = {this.localCitedby};
		return s;
	}

	public void setElementData(String[] s)
	{
		this.localCitedby = s[0];
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

			if(getDoi()!=null)
			{
				out.write(buildDoi());
			}

			if(getPii()!=null)
			{
				out.write(buildPii());
			}

			if(getISSN()!=null)
			{
				out.write(buildISSN());
			}
			
			if(getIsbn()!=null)
			{
				out.write(buildISBN());
			}
			
			if(getIsbn13()!=null)
			{
				out.write(buildISBN13());
			}

			if(getVolume()!=null)
			{
				out.write(buildFirstVolume());
			}

			if(getIssue()!=null)
			{
				out.write(buildFirstIssue());
			}

			if(getPage()!=null)
			{
				out.write(buildFirstPage());
			}

			if(getAccessionNumber()!=null)
			{
				out.write(buildAccessionNumber());
			}


			out.write(">");

			out.write("</");
			out.write(this.localKey.getKey());
			out.write(">");

	}

	/** This method returns DOI **/

	private String buildDoi()
	{
		    StringBuffer tempDoi=new StringBuffer();
		    if(getDoi()!=null)
		    {
		    	tempDoi.append(" DOI=").append("\"").append(getDoi().trim()).append("\"");
			}
		    return tempDoi.toString();
	}

	private String buildAccessionNumber()
	{
			StringBuffer tempAccessionNumber=new StringBuffer();
			if(getAccessionNumber()!=null)
			{
				tempAccessionNumber.append(" AN=").append("\"").append(getAccessionNumber().trim()).append("\"");
			}
			return tempAccessionNumber.toString();
	}

	/** This method returns PII **/

	private String buildPii()
	{
			StringBuffer tempPii=new StringBuffer();
			if(getPii()!=null)
			{
				tempPii.append(" PII=").append("\"").append(getPii().trim()).append("\"");
			}
			return tempPii.toString();
	}

	/** This method returns ISSN part of IVIP
	  * ISSN format is abcd-xyz which is processed
	  * by this method Removes "-" and returns abcdxyz.
	  * @return tempStr
	  */
	private String buildISSN()
	{

	    StringBuffer tempISSN=new StringBuffer();

		if(getISSN()!=null)
		{
	    	tempISSN.append(" ISSN=").append("\"").append(getISSN().trim()).append("\"");
		}
	    return tempISSN.toString();
	}
	
	/** This method returns ISSN part of IVIP
	  * ISSN format is abcd-xyz which is processed
	  * by this method Removes "-" and returns abcdxyz.
	  * @return tempStr
	  */
	private String buildISBN()
	{

	    StringBuffer tempISBN=new StringBuffer();

		if(getIsbn()!=null)
		{
	    	tempISBN.append(" ISBN=").append("\"").append(getIsbn().trim()).append("\"");
		}
	    return tempISBN.toString();
	}
	
	/** This method returns ISSN part of IVIP
	  * ISSN format is abcd-xyz which is processed
	  * by this method Removes "-" and returns abcdxyz.
	  * @return tempStr
	  */
	private String buildISBN13()
	{

	    StringBuffer tempISBN13=new StringBuffer();

		if(getIsbn13()!=null)
		{
			tempISBN13.append(" ISBN13=").append("\"").append(getIsbn13().trim()).append("\"");
		}
	    return tempISBN13.toString();
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

		StringTokenizer tmpPage = new StringTokenizer(getPage(),"-");
		if (tmpPage.countTokens()>0) {
			firstPage = tmpPage.nextToken();
		} else {
			firstPage = getPage();
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

	public String getAccessionNumber()
	{
		return localAccessionNumber;
	}

	public String getVolume()
	{
		return localVolume;
	}

	public String getDoi()
	{
		return localDoi;
	}

	public String getPii()
	{
		return localPii;
	}

	public String getPage(){
		return localPage;
	}

	public void setIssue(String issue)
	{
		this.localIssue = issue;
	}


	public void setVolume(String volume)
	{
		this.localVolume = volume;
	}

	public void setDoi(String doi)
	{
		this.localDoi = doi;
	}

	public void setAccessionNumber(String access)
	{
		this.localAccessionNumber = access;
	}

	public void setIssn(String issn)
	{
		this.localIssn = issn;
	}


	public void setPii(String pii)
	{
		this.localPii = pii;
	}

	public void setPage(String page){
		this.localPage = page;
	}

	public void  setFirstInitialFirstAuthor(String firstInitialFirstAuthor)
	{
		this.localFirstInitialFirstAuthor = firstInitialFirstAuthor;
	}

	public void  setFirstAuthorSurname(String firstAuthorSurname)
	{
		this.localFirstAuthorSurname = firstAuthorSurname;
	}
	public String getIsbn() {
		return localIsbn;
	}

	public void setIsbn(String localIsbn) {
		this.localIsbn = localIsbn;
	}

	public String getIsbn13() {
		return localIsbn13;
	}

	public void setIsbn13(String localIsbn13) {
		this.localIsbn13 = localIsbn13;
	}



}