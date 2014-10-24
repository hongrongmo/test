// package structure
package org.ei.domain;

//import packages
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.util.StringUtil;

/**
 * This object gets local holding labels and its urls based on customer and contract ids.
 *
 */

public class LocalHolding
{
    private final static Logger log4j = Logger.getLogger(LocalHolding.class);

    // these are member variables
    private String propString = null;
    private EIDoc doc = null;
    private int displayMax = -1;
    private String linkLabel = null;
    private String dynamicUrl = null;
    private String defaultUrl = null;
    private String imageUrl = null;

    // Constructor from UserSession
    @Deprecated
    public LocalHolding(String localholdingkey) {
        this.propString = localholdingkey;
    }

    // Constructor from UserSession and displayMax limit
    @Deprecated
    public LocalHolding(String localholdingkey, int _displayMax)
    {
        this.propString = localholdingkey;
        this.displayMax = _displayMax;
    }

    // Constructor
    public LocalHolding(String linkLabel, String dynamicUrl, String defaultUrl, String imageUrl) {
        this.linkLabel = linkLabel;
        this.dynamicUrl = dynamicUrl;
        this.defaultUrl = defaultUrl;
        this.imageUrl = imageUrl;
    }

    /**
     * Return a list of LocalHolding objects built from User's text zone values
     *
     * @return
     */
    public static List<LocalHolding> buildFromTextZones(Map<String, String> textzones) {
        List<LocalHolding> lhlist = new ArrayList<LocalHolding>();
        if (textzones.isEmpty()) {
            log4j.warn("No textzones passed in!");
            return lhlist;
        }

        String linkLabel;
        String dynamicUrl;
        String defaultUrl;
        String imageUrl;

        // Loop through text zones up to limit and create LocalHolding objects
        for (int i = 0; i < LocalHoldingLinker.TZ_LINK_LABELS.length; i++) {

            linkLabel = textzones.get(LocalHoldingLinker.TZ_LINK_LABELS[i]);
            if (!StringUtils.isEmpty(linkLabel)) {
	            // Only add if link label is present
	            dynamicUrl = textzones.get(LocalHoldingLinker.TZ_DYNAMIC_URLS[i]);
	            defaultUrl = textzones.get(LocalHoldingLinker.TZ_DEFAULT_URLS[i]);
	            imageUrl = textzones.get(LocalHoldingLinker.TZ_IMAGE_URLS[i]);
                lhlist.add(new LocalHolding(linkLabel, dynamicUrl, defaultUrl, imageUrl));
            }
        }

        return lhlist;
    }

    /**
     * @return java.lang.String xmlString
     * @throws ClientCustomerException
     *             This method gets link label,dynamic url,default url and future url for a user and generates xml
     *             output.
     */

    public String getLocalHoldingData()
    {
        String slinkLabel = null;
        String sdynamicUrl = null;
        String sdefaultUrl = null;
        String sfutureUrl = null;
        String slinkKey = null;

        StringBuffer strbuff = new StringBuffer();

        Database database = (this.doc.getDocID()).getDatabase();
        int count = 0;
        if ((propString != null) && (propString != " "))
        {
            StringTokenizer st = new StringTokenizer(propString, "|", false);
            while (st.hasMoreTokens())
            {
                boolean go = true;
                StringTokenizer st2 = new StringTokenizer(st.nextToken(), ",", false);
                if (st2.hasMoreTokens())
                {
                	slinkKey = st2.nextToken();
                    slinkLabel = st2.nextToken();

                    if (database.linkLocalHoldings(slinkLabel))
                    {
                        go = true;
                    }
                    else
                    {
                        go = false;
                    }

                    if (go)
                    {
                        count++;
                        strbuff.append("<LOCAL-HOLDING>");

                        if (slinkKey != null && slinkKey.length() > 0)
                        {
                        	strbuff.append("<POSITION>");
                            strbuff.append("<![CDATA[" + slinkKey + "]]>");
                            strbuff.append("</POSITION>");
                        }
                        else
                        {
                            strbuff.append("<POSITION></POSITION>");
                        }

                        if (slinkLabel != " ")
                        {
                            strbuff.append("<LINK-LABEL>");
                            strbuff.append("<![CDATA[" + slinkLabel + "]]>");
                            strbuff.append("</LINK-LABEL>");
                        }
                        else
                        {
                            strbuff.append("<LINK-LABEL></LINK-LABEL>");
                        }

                        sdynamicUrl = st2.nextToken();

                        if (sdynamicUrl != null && sdynamicUrl.length() > 0)
                        {
                        	strbuff.append("<DYNAMIC-URL>");
                            strbuff.append("<![CDATA[" + sdynamicUrl + "]]>");
                            strbuff.append("</DYNAMIC-URL>");
                        }
                        else
                        {
                            strbuff.append("<DYNAMIC-URL></DYNAMIC-URL>");
                        }


                        sdefaultUrl = st2.nextToken();
                        if (sdefaultUrl != null && sdefaultUrl.length() > 0)
                        {
                        	strbuff.append("<DEFAULT-URL>");
                            strbuff.append("<![CDATA[" + sdefaultUrl + "]]>");
                            strbuff.append("</DEFAULT-URL>");
                        }
                        else
                        {
                            strbuff.append("<DEFAULT-URL></DEFAULT-URL>");
                        }
                        if (st2.hasMoreTokens())
                        {
                            sfutureUrl = st2.nextToken();
                            if ((sfutureUrl != null) && (!sfutureUrl.trim().equals(StringUtil.EMPTY_STRING)))
                            {
                                strbuff.append("<FUTURE-URL>");
                                strbuff.append("<![CDATA[" + sfutureUrl + "]]>");
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

                if (displayMax > -1 && count == displayMax)
                {
                    break;
                }
            }
        }

        if (strbuff.length() > 0)
        {
            strbuff.insert(0, "<LOCAL-HOLDINGS>");
            strbuff.append("</LOCAL-HOLDINGS>");
        }
        return strbuff.toString();
    }

    /**
     * @return java.lang.String xmlString
     * @throws ClientCustomerException
     *             returns xml output.
     */

    public String getLocalHoldingsXML(EIDoc inDoc)
    {
        this.doc = inDoc;
        String xmlString = null;

        xmlString = getLocalHoldingData();

        return xmlString;
    }

    public String getLinkLabel() {
        return this.linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    public String getDynamicUrl() {
        return this.dynamicUrl;
    }

    public void setDynamicUrl(String dynamicUrl) {
        this.dynamicUrl = dynamicUrl;
    }

    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}