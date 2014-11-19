package org.ei.alert.email;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ei.config.ApplicationProperties;
import org.ei.domain.Page;
import org.ei.util.StringUtil;

public class EmailAlertResultsManager
{
	 //this variable(listOfDocids) basically used for holding list of Docid objects
	 private Page oPage;
	 //this variable(displayFormat) basically used for holding displayFormat(email,ris,ascii).
	 @SuppressWarnings("unused")
	private String displayFormat;
	 
	 //this variable(format) basically used for holding format(citation,abstract,detailed).
	 @SuppressWarnings("unused")
	private String format;
	 //this variable(setFlag) basically used for holding whether the request came form selected set or not
	 @SuppressWarnings("unused")
	private boolean setFlag;
	 //this variable(orderList) basically used for holding the original order

	List<?> orderList=new ArrayList<Object>();
	//this variable(queryStr) basically used for holding queryStr.
	private String queryStr;

	/*  Basically this constructor expects list of docids,displayformat(email,ris,ascii),
	*  format (citation,detailed,abstract) and a selected set flag.This flag basically
	*  tells us about whether the list of docids come from selected set or not.
	*  @param List
	*  @param string
	*  @param string
	*  @param boolean
	*/
	public EmailAlertResultsManager(Page oPage,
    					  String aDisplayFormat,
    					  String aFormat,
    					  boolean aSetFlag)
	{
		this.oPage=oPage;
		this.displayFormat=aDisplayFormat;
		if(aFormat.trim().equals("detailed"))
		{
			this.format="fullDoc";
		}
		else
		{
			this.format=aFormat;
		}
		this.setFlag=aSetFlag;
		
	}

	/**  After getting an instance of ResultsManager we call this
	*   That typically gives us the output based on the parameter supplied at
	*   instanciation of Results Manager.
	*   @retun String
	*/
	public String toDisplayFormat() throws Exception{

		StringBuffer sb = new StringBuffer();
		String retStr = StringUtil.EMPTY_STRING;
		sb.append("<PAGE>");
		sb.append(getQueryString());

		try {
			if((oPage != null) && (oPage.docCount() != 0)) {
				StringWriter asw=new StringWriter();
				oPage.toXML(asw);
				sb.append(asw.toString());
				asw = null;
			}
			else {
				sb.append("<NO-RESULTS>EMPTY SET");
				sb.append("</NO-RESULTS>");
			}

			sb.append("</PAGE>");

			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer =tfactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(ApplicationProperties.getInstance().getProperty("XSLFILE"))));
			
			StringReader sr=new StringReader(sb.toString());
			StringWriter sw=new StringWriter();
			transformer.transform(new StreamSource(sr),new StreamResult(sw));

			retStr=sw.toString();

		} catch(Exception e) {
			e.printStackTrace();
		}

		return retStr;
	}

	/** This method sets query string object
	* @param String
	*/
	public void setQueryString(String aQueryStr)
	{
		this.queryStr=aQueryStr;
	}// end of method

	/** This method gets query string object
  	* @return  String
	*/
	public String getQueryString()
	{
		return queryStr;
	}

}// end of class