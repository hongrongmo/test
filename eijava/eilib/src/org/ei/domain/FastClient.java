package org.ei.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ei.domain.navigators.EiNavigator;
import org.ei.util.StringUtil;

public class FastClient
{
	// preProcess navigators
	// This can/will/should eb refactored into a set of classes
	// with a generic interface/base class and subclasses
	// which override the base method(s)
	private boolean preProcessNavid(String navid, int mask)
	{
		// Always include DB
		if(EiNavigator.DB.equals(navid)){
			return true;
		}
		// include YR only if this isn't Referex by itself
		else if(EiNavigator.YR.equals(navid)) {
			if(mask != DatabaseConfig.PAG_MASK){
				return true;
			}
		}
		// include CV only if this isn't Referex by itself
		else if(EiNavigator.CV.equals(navid)) {
			if(mask != DatabaseConfig.PAG_MASK) {
				return true;
			}
		}
		// include CL only if Referex isn't combined with another database
		else if(EiNavigator.CL.equals(navid)) {
			if((mask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
				return true;
			}
			else if(mask == DatabaseConfig.PAG_MASK) {
				return true;
			}
			else {
				return false;
			}
		}
		// include ST only if Referex isn't combined with another database
		//		TS -  and CBF
		else if(EiNavigator.ST.equals(navid))
		{
			if(mask == DatabaseConfig.PAG_MASK ||
			   mask == DatabaseConfig.CPX_MASK ||
			   mask == DatabaseConfig.INS_MASK ||
			   mask == DatabaseConfig.GEO_MASK ||
			   mask == DatabaseConfig.ELT_MASK ||
			   mask == DatabaseConfig.EPT_MASK ||
			   mask == DatabaseConfig.CBF_MASK
			   )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		// include FL only if Referex is alone
		else if(EiNavigator.FL.equals(navid)) {
			if((mask == DatabaseConfig.PAG_MASK)) {
				return true;
			}
		}
		// include PN only if CPX, INS, or Referex
		//TS -  and CBF
		else if(EiNavigator.PN.equals(navid)) {
			if(((mask & (DatabaseConfig.CPX_MASK +
						 DatabaseConfig.INS_MASK +
						 DatabaseConfig.PAG_MASK +
						 DatabaseConfig.ELT_MASK +
						 DatabaseConfig.EPT_MASK +
						 DatabaseConfig.CBF_MASK)) > 0)){
				return true;
			}
		}
		// Always include AU
		else if(EiNavigator.AU.equals(navid)){
			return true;
		}
		// include AF only if this isn't Referex by itself
		else if(EiNavigator.AF.equals(navid)) {
			if(mask != DatabaseConfig.PAG_MASK){
				return true;
			}
		}
		// include DT only if Referex isn't combined with another database
		else if(EiNavigator.DT.equals(navid)) {
			if((mask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
				return true;
			}
			else if(mask == DatabaseConfig.PAG_MASK) {
				// DT will be removed from display in this case
				return true;
			}
			else {
				return false;
			}
		}
		// include LA if CPX, INS, NTI or GEO
		//		TS -  and CBF
		else if(EiNavigator.LA.equals(navid)) {
			if(((mask & (DatabaseConfig.CPX_MASK +
							DatabaseConfig.INS_MASK +
							DatabaseConfig.NTI_MASK +
							DatabaseConfig.GEO_MASK +
							DatabaseConfig.CBF_MASK)) > 0)){
				return true;
			}
		}
		// include CO only if Referex isn't combined with another database
		else if(EiNavigator.CO.equals(navid)) {
			if((mask & DatabaseConfig.PAG_MASK) != DatabaseConfig.PAG_MASK) {
				return true;
			}
			else if(mask == DatabaseConfig.PAG_MASK) {
				return true;
			}
			else {
				return false;
			}
		}
		// include PEC only if Patents are included
		else if(EiNavigator.PEC.equals(navid)) {
			if((mask & (DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK)) > 0) {
				return true;
			}
		}
		// include PID only if Patents are included
		else if(EiNavigator.PID.equals(navid)) {
			if((mask & (DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK)) > 0) {
				return true;
			}
		}
		// include PUC only if Patents are included
		else if(EiNavigator.PUC.equals(navid)) {
			if((mask & (DatabaseConfig.EUP_MASK + DatabaseConfig.UPA_MASK)) > 0) {
				return true;
			}
		}
		// include PEC only if Encomapss Pat are included
		else if(EiNavigator.PAC.equals(navid)) {
			if((mask & (DatabaseConfig.EPT_MASK )) > 0) {
				return true;
			}
		}

		return false;
	}

    // Create string of active navigators for use in doNavigators clause of buildSearchURL
    public String getNavigatorString()
    {
//        System.out.println(" getNavigatorString() w/ mask = " + getNavigatorMask());

        String navstring = "";
        boolean navstrempty = true;

        if(this.getNavigatorMask() == 0) {
            return navstring;
        }

        Iterator itrnav = EiNavigator.getNavigatorNames().iterator();
        while(itrnav.hasNext()) {
            String navid = (String) itrnav.next();

            // only include the navigator if the navigator's mask
            // is present in the query gathered mask
            boolean passes = preProcessNavid(navid, getNavigatorMask());
            if(passes) {
                if (navstrempty){
                    navstring = navstring.concat(navid);
                    navstrempty = false;
                }
                else {
                    navstring = navstring.concat(",").concat(navid);
                }
            }
        }

        return navstring;
    }


    private StringUtil sutil = new StringUtil();

    private String resultView;
    private String baseURL;
    private String primarySort;
    private String primarySortDirection = "+";
    private String queryString;
    private boolean doNavigators = false;
    private boolean doCatCount = false;
    private int pageSize = 25;
    private int offSet = 0;
    private Hashtable subcats = new Hashtable();
    private Hashtable navigators = new Hashtable();
    private int hitCount = -1;
    private long searchTime = 0L;
    private String errorCode;
    private List docIDs = new ArrayList();


    private int navigatormask = 0;
    public int getNavigatorMask()
    {
        return this.navigatormask;
    }
    public void setNavigatorMask(int mask)
    {
        this.navigatormask = mask;
    }

    public static void main(String args[])
        throws Exception
    {
        BufferedReader in = null;

        try
        {


            //in = new BufferedReader(new FileReader("test.txt"));
            FastClient client = new FastClient();
            client.setBaseURL("http://rei11.bos3.fastsearch.net:15100");
            client.setResultView("ei");
            client.setOffSet(0);
            client.setPageSize(25);
            client.setQueryString("ti:water");
            client.setDoCatCount(true);
            client.setDoNavigators(true);
            client.setPrimarySort("ausort");
            client.setPrimarySortDirection("+");
            client.search();

            List l = client.getDocIDs();
            for(int i=0;i<l.size();i++)
            {
                String[] docID = (String[])l.get(i);
            }

            Hashtable cl = client.getSubcats();
            Enumeration clusterKeys = cl.keys();
            while(clusterKeys.hasMoreElements())
            {
                String clusterKey = (String)clusterKeys.nextElement();
                String clusterValue = (String)cl.get(clusterKey);
            }

            Hashtable navs = client.getNavigators();
            List mods = (List)navs.get("aunav");
            for(int i=0;i<mods.size();i++)
            {
                String[] mod = (String[])mods.get(i);
            }
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }

    public void setResultView(String r)
    {
        this.resultView = r;
    }

    public String getErrorCode()
    {
        return this.errorCode;
    }

    public void setBaseURL(String bURL)
    {
        this.baseURL = bURL;
    }

    public void setPrimarySort(String psort)
    {
        this.primarySort = psort;
    }

    public void setPrimarySortDirection(String psortD)
    {
        this.primarySortDirection = psortD;
    }

    public void setQueryString(String qstring)
    {
        this.queryString = qstring;
    }

    public void setDoClustering(boolean c)
    {
    }

    public void setDoNavigators(boolean n)
    {
        this.doNavigators = n;
    }

    public void setDoCatCount(boolean c)
    {
        this.doCatCount = c;
    }

    public void setPageSize(int p)
    {
        this.pageSize = p;
    }

    public void setOffSet(int o)
    {
        this.offSet = o;
    }

    public void search()
        throws SearchException
    {
        BufferedReader in = null;
        HttpMethod method = null;

        try
        {
            String URL = buildSearchURL();
            HttpClient client = new HttpClient();
            method = new GetMethod(URL);
//            System.out.println(" FastClient URL " + java.net.URLDecoder.decode(URL));
            int statusCode = client.executeMethod(method);
            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            read(in);
        }
        catch(Exception e)
        {
            throw new SearchException(new Exception("<DISPLAY>A network error has occurred, your request cannot be completed.</DISPLAY>"));
        }
        finally
        {

            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
            if(method != null)
            {
                try
                {
                    method.releaseConnection();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

	public FastDeduper dedupSearch(String fieldPref,
								   String databasePref)
		throws SearchException
	{
		BufferedReader in = null;
		HttpMethod method = null;
		FastDeduper fdd = null;
		try
		{
			String URL = buildDedupSearchURL();
			//System.out.println(URL);
			HttpClient client = new HttpClient();
			method = new GetMethod(URL);
			int statusCode = client.executeMethod(method);
			in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			fdd = new FastDeduper(in,
								  fieldPref,
								  databasePref);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SearchException(new Exception("<DISPLAY>A network error has occurred, your request cannot be completed.</DISPLAY>"));
		}
		finally
		{

			if(in != null)
			{
				try
				{
					in.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
			if(method != null)
			{
				try
				{
					method.releaseConnection();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}

		return fdd;
	}

    public Hashtable getSubcats()
    {
        return this.subcats;
    }

    public int getHitCount()
    {
        return hitCount;
    }

    public long getSearchTime()
    {
        return this.searchTime;
    }

    public List getDocIDs()
    {
        return docIDs;
    }

    public Hashtable getNavigators()
    {
        return navigators;
    }

	public String buildDedupSearchURL()
		throws Exception
	{
		if(this.baseURL == null)
		{
			throw new Exception("Base URL is not set.");
		}
		else if(this.queryString == null)
		{
			throw new Exception("Query String is not set.");
		}

		StringBuffer buf = new StringBuffer(this.baseURL);
		buf.append("/cgi-bin/search");



		buf.append("?type=adv&encoding=utf-8&rpf_clustering:enabled=false&rpf_clustering:root=*&rpf_clustering:overridehits=1");
		buf.append("&query=");
		buf.append(URLEncoder.encode(this.queryString,"UTF-8"));
		buf.append("&offset=");
		buf.append(Integer.toString(this.offSet));
		buf.append("&hits=");
		buf.append(Integer.toString(this.pageSize));
		if(this.primarySort != null)
		{
			buf.append("&sortby=");
			buf.append(URLEncoder.encode(this.primarySortDirection,"UTF-8"));
			buf.append(this.primarySort);
			if(this.primarySort.equals("yr"))
			{
				buf.append(URLEncoder.encode(this.primarySortDirection,"UTF-8"));
				buf.append("wk");
				buf.append("&fullsort=yes");
			}
		}
		return buf.toString();
	}



    public String buildSearchURL()
        throws Exception
    {
        if(this.baseURL == null)
        {
            throw new Exception("Base URL is not set.");
        }
        else if(this.queryString == null)
        {
            throw new Exception("Query String is not set.");
        }
        else if(this.resultView == null)
        {
            throw new Exception("Result View is not set.");
        }

        StringBuffer buf = new StringBuffer(this.baseURL);
        buf.append("/cgi-bin/");
        buf.append(this.resultView);
        buf.append("?type=adv&encoding=utf-8&rpf_clustering:enabled=false&rpf_clustering:root=*&rpf_clustering:overridehits=1&resultview=");
        buf.append(this.resultView);
        buf.append("&query=");
        buf.append(URLEncoder.encode(this.queryString,"UTF-8"));
        buf.append("&offset=");
        buf.append(Integer.toString(this.offSet));
        buf.append("&hits=");
        buf.append(Integer.toString(this.pageSize));
        if(this.primarySort != null)
        {
            buf.append("&sortby=");
            buf.append(URLEncoder.encode(this.primarySortDirection,"UTF-8"));
            buf.append(this.primarySort);
            if(this.primarySort.equals("yr"))
            {
                buf.append(URLEncoder.encode(this.primarySortDirection,"UTF-8"));
                buf.append("wk");
                buf.append("&fullsort=yes");
            }
        }

        if(doNavigators)
        {
            buf.append("&rpf_navigation:enabled=1");
            buf.append("&rpf_navigation:navigators=");

            buf.append(URLEncoder.encode(getNavigatorString(),"UTF-8"));
//          buf.append(URLEncoder.encode("clnav,cvnav,pnnav,dtnav,stnav,flnav,lanav,afnav,aunav"));
        }

        if(doCatCount)
        {
            buf.append("&catn=-1");
        }

        //System.out.println(buf.toString());
        return buf.toString();
    }

    public void read(BufferedReader in)
        throws IOException
    {
        String line = null;
        while((line = in.readLine()) != null)
        {
//            if(line.indexOf("#CAT") == 0)
//            {
//                if(doCatCount)
//                {
//                    parseSubcats(line);
//                }
//            }
            if(line.indexOf("#ERC") == 0)
            {
                parseErrorCode(line);
            }
            else if(line.indexOf("#CNT") == 0)
            {
                parseCount(line);
            }
            else if(line.indexOf("#TIM") == 0)
            {
                parseSearchTime(line);
            }
            else if(line.indexOf("#eidocid") == 0)
            {
 				String dedupKey = in.readLine();
 				String doi = in.readLine();
 				String dmask = in.readLine();
 				parseDocID(line,dedupKey,doi,dmask);
            }
            else if(line.indexOf("#tdocid") == 0)
            {
                parseTDocID(line);
            }
            else if(line.indexOf("#NAV NAME") == 0)
            {
                String navNames = skip(in, "#NAV NAMES");
                String navCounts = skip(in, "#NAV CNTS");
                parseNavigator(line,
                               navNames,
                               navCounts);
            }

        }
    }

    private void parseNavigator(String navLine,
                                String nameLine,
                                String navCounts)
    {
        String navName = parseNavName(navLine);
        char[] c = new char[1];
        c[0] = (char)30;
        String delimiter = new String(c);
        nameLine = strip(nameLine, "#NAV NAMES ");
        navCounts = strip(navCounts, "#NAV CNTS ");
        StringTokenizer nameTokens = new StringTokenizer(nameLine, delimiter);
        StringTokenizer countTokens = new StringTokenizer(navCounts, delimiter);
        ArrayList navData = new ArrayList();
        int i = 0;
        while(nameTokens.hasMoreTokens())
        {
            String[] dataElement = new String[2];
            dataElement[0] = nameTokens.nextToken();
            dataElement[1] = countTokens.nextToken();
            navData.add(dataElement);
        }

        navigators.put(navName, navData);

    }

    private String parseNavName(String navLine)
    {
        StringTokenizer tokens = new StringTokenizer(navLine);
        tokens.nextToken();
        tokens.nextToken();
        String data = tokens.nextToken();
        return data;
    }

    private void parseErrorCode(String errorLine)
    {
        StringTokenizer tokens = new StringTokenizer(errorLine);
        tokens.nextToken();
        this.errorCode = tokens.nextToken();
    }

    private void parseSearchTime(String timeLine)
    {
        StringTokenizer tokens = new StringTokenizer(timeLine);
        tokens.nextToken();
        String time = tokens.nextToken();
        double t = Double.parseDouble(time);
        double tt = t*1000;
        BigDecimal bd = new BigDecimal(tt);
        this.searchTime = bd.longValue();
    }

    private String skip(BufferedReader in,
                        String marker)
        throws IOException
    {
        String line = null;
        while((line = in.readLine()) != null)
        {
            if(line.indexOf(marker) == 0)
            {
                return line;
            }
        }

        return null;
    }

    protected void parseDocID(String docIdLine,
                              String dedupKeyLine,
                              String doiLine,
                              String dmaskLine)
    {
        String[] id = new String[4];
        StringTokenizer tokens1 = new StringTokenizer(docIdLine);
        tokens1.nextToken();
        id[0] = tokens1.nextToken().trim();
        StringTokenizer tokens2 = new StringTokenizer(dedupKeyLine);
        tokens2.nextToken();
        id[1] = tokens2.nextToken().trim();
        StringTokenizer tokens3 = new StringTokenizer(doiLine);
        tokens3.nextToken();
        if(tokens3.hasMoreTokens())
        {
        	tokens3.nextToken();
        	id[2] = tokens3.nextToken().trim();
		}
        StringTokenizer tokens4 = new StringTokenizer(dmaskLine);
        tokens4.nextToken();
        if(tokens4.hasMoreTokens())
        {
        	id[3] = tokens4.nextToken().trim();
		}

        docIDs.add(id);
    }

    protected void parseTDocID(String docIdLine)
    {
        String[] id = new String[2];
        StringTokenizer tokens1 = new StringTokenizer(docIdLine);
        tokens1.nextToken();
        id[0] = tokens1.nextToken().trim();
        docIDs.add(id);
    }


    protected void parseCount(String line)
    {
        StringTokenizer tokens = new StringTokenizer(line);
        tokens.nextToken();
        String c = tokens.nextToken().trim();
        this.hitCount = Integer.parseInt(c);
    }
/* Subcats are  now handled as a DB Navigator
    protected void parseSubcats(String line)
    {

        StringTokenizer tokens = new StringTokenizer(line);
        tokens.nextToken();
        tokens.nextToken();
        String data = tokens.nextToken();
        StringTokenizer tokenPairs = new StringTokenizer(data, ",");
        ArrayList navData = new ArrayList();
        while(tokenPairs.hasMoreTokens())
        {
            String pair = tokenPairs.nextToken();
            StringTokenizer tokenParts = new StringTokenizer(pair,":");
            String database = tokenParts.nextToken();
            String count = tokenParts.nextToken();
            subcats.put(database, count);
            String[] navel = new String[2];
            navel[0] = database;
            navel[1] = count;
            navData.add(navel);
        }
        navigators.put("db", navData);
    }
*/
    private String strip(String in, String str)
    {
        return this.sutil.replace(in,
                                  str,
                                  "",
                                  StringUtil.REPLACE_FIRST,
                                  StringUtil.MATCH_CASE_INSENSITIVE);
    }
}