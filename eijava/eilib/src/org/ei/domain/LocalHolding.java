// package structure
package org.ei.domain;

//import packages
import java.util.StringTokenizer;

import org.ei.session.UserSession;
import org.ei.util.StringUtil;

/**
*   This object gets local holding labels and its urls  based on customer and contract ids.
*
*/

public class LocalHolding
{
   //these are member variables
    private String propString = null;
    private EIDoc doc = null;
    private int displayMax = -1;


    /**
    *@param java.lang.String customerId
    *@param java.lang.String contactId
    * @throws ClientCustomerException
    * This constructor sets customerId and contractId
    */

    public LocalHolding(UserSession userSes)
    {
            propString = userSes.getProperty(UserSession.LOCAL_HOLDING_KEY);
    }

    public LocalHolding(UserSession userSes,
                        int _displayMax)
    {
        this.propString = userSes.getProperty(UserSession.LOCAL_HOLDING_KEY);
        this.displayMax = _displayMax;
    }



    /**
    * @return java.lang.String xmlString
    * @throws ClientCustomerException
    * This method gets link label,dynamic url,default url and future url for a user
    * and generates xml output.
    */

    public String getLocalHoldingData()
    {
        String slinkLabel = null;
        String sdynamicUrl = null;
        String sdefaultUrl = null;
        String sfutureUrl = null;
        String issn = doc.getISSN();

        StringBuffer strbuff=new StringBuffer();


        Database database = (this.doc.getDocID()).getDatabase();
        int count = 0;
        if ((propString != null) && (propString != " "))
        {
            StringTokenizer st = new StringTokenizer(propString,"|",false);
            while(st.hasMoreTokens())
            {
                boolean go = true;
                StringTokenizer st2 = new StringTokenizer(st.nextToken(),",",false);
                if (st2.hasMoreTokens())
                {

                    slinkLabel = st2.nextToken();

					if(database.linkLocalHoldings(slinkLabel))
					{
						go = true;
					}
                    else
                    {
						go = false;
					}

                    if(go)
                    {
                        count++;
                        strbuff.append("<LOCAL-HOLDING>");

                        if (slinkLabel != " ")
                        {
                            strbuff.append("<LINK-LABEL>");
                            strbuff.append("<![CDATA["+slinkLabel+"]]>");
                            strbuff.append("</LINK-LABEL>");
                        }
                        else
                        {
                            strbuff.append("<LINK-LABEL></LINK-LABEL>");
                        }

                        sdynamicUrl = st2.nextToken();

						String localURL = null;

                        if (sdynamicUrl != null && sdynamicUrl.length() > 0)
                        {
							localURL = doc.getLocalHoldingLink(sdynamicUrl);
                            strbuff.append("<DYNAMIC-URL>");
                            strbuff.append("<![CDATA["+localURL+"]]>");
                            strbuff.append("</DYNAMIC-URL>");
                        }
                        else
                        {
                            strbuff.append("<DYNAMIC-URL></DYNAMIC-URL>");
                        }

                        sdefaultUrl = st2.nextToken();
                        if (localURL != null)
                        {
                            strbuff.append("<DEFAULT-URL>");
                            strbuff.append("<![CDATA["+localURL+"]]>");
                            strbuff.append("</DEFAULT-URL>");
                        }
                        else
                        {
                            strbuff.append("<DEFAULT-URL></DEFAULT-URL>");
                        }
                        if(st2.hasMoreTokens())
                        {
                            sfutureUrl = st2.nextToken();
                            if((sfutureUrl != null) && (!sfutureUrl.trim().equals(StringUtil.EMPTY_STRING)))
                            {
                                strbuff.append("<FUTURE-URL>");
                                strbuff.append("<![CDATA["+sfutureUrl+"]]>");
                                strbuff.append("</FUTURE-URL>");
                            }
                            else
                            {
                                strbuff.append("<FUTURE-URL></FUTURE-URL>");
                            }
                        }
                        else
                        {
                            strbuff.append("<FUTURE-URL></FUTURE-URL>");
                        }
                        strbuff.append("</LOCAL-HOLDING>");
                    }
                }

                if(displayMax > -1 && count == displayMax)
                {
                    break;
                }
            }
        }

		if(strbuff.length() > 0)
		{
			strbuff.insert(0,"<LOCAL-HOLDINGS>");
        	strbuff.append("</LOCAL-HOLDINGS>");
		}
        return strbuff.toString();
    }

    /**
    * @return java.lang.String xmlString
    * @throws ClientCustomerException
    *  returns xml output.
    */

     public String getLocalHoldingsXML(EIDoc inDoc)
     {
        this.doc = inDoc;
        String xmlString=null;

        xmlString = getLocalHoldingData();

        return xmlString;
     }

}