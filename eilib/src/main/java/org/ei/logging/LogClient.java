package org.ei.logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

/**
*  Description of the Class
*
*@author     dbaptist
*@created    November 29, 2001
*/
public class LogClient {
    /**
    *  URLConnection to server
    *
    *@since
    */
    //protected HttpURLConnection conn;

    /**
    *  Output stream to URLConnection
    *
    *@since
    */
    //protected PrintWriter ps;

    /**
    *  Input stream to URLConnection
    *
    *@since
    */
    //protected BufferedReader is;

    private Hashtable appdata = new Hashtable();
    private String baseURL;

    // Application ID. Unique identifier
    private String appid;

    // Record ID. Unique identifier
    private String rid;

    //The IP address or host/subdomain name of the HTTP client that made the HTTP resource request.
    private String host;
    //(125.125.125.125 in the example)

    //The identifier used to identify the client making the HTTP request. If no value is present, a "-" is substituted.
    private String rfc931 = "-";
    //("-" in the example)

    //The username, (or user ID) used by the client for authentication. If no value is present, a "-" is substituted.
    private String username = "-";
    //(dsmith in the example)

    //The date and time stamp of the HTTP request
    //the specified number of milliseconds since the standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
    private long date;

    //HTTP method (GET,POST...)
    private String HTTPmethod = "GET";

    // requested resource stem
    private String uri_stem;

    // requested resource query (Optional) (default empty)
    private String uri_query = null;

    /**
    *  HTTP protocol version (Optional) (default 1.0) .
    *
    *@since
    */
    private String prot_version = "1.0";

    /**
    *  The status is the numeric code indicating the success or failure of the
    *  HTTP request. (200 in the example)
    *
    *@since
    */
    private int statuscode;

    /**
    *  (1043 in the example) The bytes field is a numeric field containing the
    *  number of bytes of data transferred as part of the HTTP request, not
    *  including the HTTP header.
    *
    *@since
    */
    private int bytes = 0;

    /**
    *  ("http://www.ibm.com/" in the example) The URL which linked the user to
    *  your site. (Optional)
    *
    *@since
    */
    private String referrer = "-";

    /**
    *  ("Mozilla/4.05 [en] (WinNT; I)" in the example) The Web browser and
    *  platform used by the visitor to your site.(Optional)
    *
    *@since
    */
    private String user_agent = "-";

    /**
    *  ("USERID=CustomerA;IMPID=01234" in the example) Cookies are pieces of
    *  information that the HTTP server can send back to client along the with the
    *  requested resources. A client's browser may store this information and
    *  subsequently send it back to the HTTP server upon making additional
    *  resource requests. The HTTP server can establish multiple cookies per HTTP
    *  request. Cookies take the form KEY = VALUE. Multiple cookie key value pairs
    *  are delineated by semicolons(;).
    *
    *@since
    */
    private String cookies = "-";

    // Additional fields for village_log
    //The customer numerical identifier from authenication.
    private String cust_id = "0";
    //Session id
    private String sid;
    // Transaction ID. Each click on the pages is defined as a transaction.
    private long tid;
    // The database searched
    private String db_name = "-";
    // Initial request time
    private long begin_time;
    // Request completion time
    private long end_time;
    // Total Time for Request
    private long response_time;
    // Report name??
    private String report_name = "-";
    // Number of hits returned by Oracle CTX/
    private int hits;
    // Query sent to Oracle
    private String query_string = "-";
    // Unsure.
    private int rec_range;
    // Unknown
    private int save_search;
    // Action e.g. basics earch, advance search ....
    private String action = "-";
    // Application e.g. Engineering Village 2, ChemVillage
    private String application = "-";
    // Number of records fetched.
    private int num_recs;


    /**
    *  Constructor for the LogClient object
    *
    *@param  baseURL  URL for the logging server
    *@since
    */
    public LogClient(String baseURL) {
        this.baseURL = baseURL;
    }


    public void setrid(String rid)
    {
        this.rid = rid;
    }

    public String getrid()
    {
        return this.rid;
    }


    /**
    *  Set the IP address (125.125.125.125 in the example)
    *
    *@param  host  The IP address or host/subdomain name of the HTTP client that
    *      made the HTTP resource request
    *@since
    */
    public void setHost(String host) {
        this.host = host;
    }


    /**
    *  Set the rfc931 identifier ("-" for example)
    *
    *@param  rfc931  The identifier used to identify the client making the HTTP
    *      request. If no value is present, a "-" is substituted.
    *@since
    */
    public void setrfc931(String rfc931) {
        this.rfc931 = rfc931;
    }


    /**
    *  Set the username
    *
    *@param  username  The username, (or user ID) used by the client for
    *      authentication. If no value is present, a "-" is substituted.
    *@since
    */
    public void setusername(String username) {
        this.username = username;
    }


    /**
    *  Set the date and time stamp of the HTTP request
    *
    *@param  date  the specified number of milliseconds since the standard base
    *      time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
    *@since
    */
    public void setdate(long date) {
        this.date = date;
    }



    /**
    *  Set the method (GET,POST...) of the HTTP request
    *
    *@param  HTTPmethod  The new hTTPmethod value
    *@since
    */
    public void setHTTPmethod(String HTTPmethod) {
        this.HTTPmethod = HTTPmethod;
    }


    /**
    *  Set the requested resource stem
    *
    *@param  uri_stem  The uri_stem value
    *@since
    */
    public void seturi_stem(String uri_stem) {
        this.uri_stem = uri_stem;
    }


    /**
    *  Set the requested resource query (Optional)
    *
    *@param  uri_query  The uri_query value
    *@since
    */
    public void seturi_query(String uri_query) {
        this.uri_query = uri_query;
    }


    /**
    *  Set the cookies (Optional)
    *
    *@param  cookies  ("USERID=CustomerA;IMPID=01234" in the example) Cookies are
    *      pieces of information that the HTTP server can send back to client
    *      along the with the requested resources. A client's browser may store
    *      this information and subsequently send it back to the HTTP server upon
    *      making additional resource requests. The HTTP server can establish
    *      multiple cookies per HTTP request. Cookies take the form KEY = VALUE.
    *      Multiple cookie key value pairs are delineated by semicolons(;).
    *@since
    */
    public void setcookies(String cookies) {
        this.cookies = cookies;
    }


    /**
    *  Set the user_agent (Optional)
    *
    *@param  user_agent  ("Mozilla/4.05 [en] (WinNT; I)" in the example) The Web
    *      browser and platform used by the visitor to your site.(Optional)
    *@since
    */
    public void setuser_agent(String user_agent) {
        this.user_agent = user_agent;
    }


    /**
    *  Set the referrer (Optional)
    *
    *@param  referrer  ("http://www.ibm.com/" in the example) The URL which linked
    *      the user to your site. (Optional)
    *@since
    */
    public void setreferrer(String referrer) {
        this.referrer = referrer;
    }


    /**
    *  Set the bytes
    *
    *@param  bytes  (1043 in the example) The bytes field is a numeric field
    *      containing the number of bytes of data transferred as part of the HTTP
    *      request, not including the HTTP header.
    *@since
    */
    public void setbytes(int bytes) {
        this.bytes = bytes;
    }


    /**
    *  Set the statuscode
    *
    *@param  statuscode  The status is the numeric code indicating the success or
    *      failure of the HTTP request. (200 in the example)
    *@since
    */
    public void setstatuscode(int statuscode) {
        this.statuscode = statuscode;
    }


    /**
    *  Set the cust_id. The customer identifier from authenication.
    *
    *@param  cust_id  The Customer ID (Max 8 digits)
    *@since
    */
    public void setcust_id(String cust_id) {
        this.cust_id = cust_id;
    }


    /**
    *  Set the Session id.
    *
    *@param  sid  Session id
    *@since
    */
    public void setsid(String sid) {
        this.sid = sid;
    }


    /**
    *  Set the Transaction ID. Each click on the pages is defined as a
    *  transaction.
    *
    *@param  tid  Transaction ID
    *@since
    */
    public void settid(long tid) {
        this.tid = tid;
    }


    /**
    *  Set the database searched.
    *
    *@param  db_name  database searched.
    *@since
    */
    public void setdb_name(String db_name)
    {
		if(db_name != null)
		{
        	this.db_name = db_name;
		}
    }


    /**
    *  Set the Initial request time.
    *
    *@param  begin_time  Transaction ID
    *@since
    */
    public void setbegin_time(long begin_time) {
        this.begin_time = begin_time;
    }


    /**
    *  Set the Request completion time.
    *
    *@param  end_time  Request completion time
    *@since
    */
    public void setend_time(long end_time) {
        this.end_time = end_time;
    }


    /**
    *  Set the Total Time for Request.
    *
    *@param  response_time  Total Time for Request.
    *@since
    */
    public void setresponse_time(long response_time) {
        this.response_time = response_time;
    }



    /**
    *  Set the Report name??.
    *
    *@param  report_name  Report name??.
    *@since
    */
    public void setreport_name(String report_name) {
        this.report_name = report_name;
    }



    /**
    *  Set the Number of hits returned by Oracle CTX.
    *
    *@param  hits  Number of hits returned by Oracle CTX.
    *@since
    */
    public void sethits(int hits) {
        this.hits = hits;
    }


    /**
    *  Set the Query sent to Oracle CTX.
    *
    *@param  query_string  Query sent to Oracle CTX.
    *@since
    */
    public void setquery_string(String query_string) {
        this.query_string = query_string;
    }



    /**
    *  Set the rec_range??.
    *
    *@param  rec_range  rec_range??
    *@since
    */
    public void setrec_range(int rec_range) {
        this.rec_range = rec_range;
    }



    /**
    *  Set the save_search??.
    *
    *@param  save_search  save_search??
    *@since
    */
    public void setsave_search(int save_search) {
        this.save_search = save_search;
    }



    /**
    *  Set the Action e.g. basics earch, advance search .....
    *
    *@param  action  Action e.g. basics earch, advance search ....
    *@since
    */
    public void setaction(String action) {
        this.action = action;
    }



    /**
    *  Set the Application e.g. Engineering Village 2, ChemVillage
    *
    *@param  application  Application e.g. Engineering Village 2, ChemVillage
    *@since
    */
    public void setapplication(String application) {
        this.application = application;
    }



    /**
    *  Set the Number of records fetched.
    *
    *@param  num_recs  Number of records fetched.
    *@since
    */
    public void setnum_recs(int num_recs) {
        this.num_recs = num_recs;
    }


    /**
    *  Set application data (data not part of the Combined Logfiles Format).
    *  Name/value pairs stored in a HashTable
    *
    *@param  appdata  Application (name value .
    *@since
    */
    public void setappdata(Hashtable appdata) {
        this.appdata = appdata;
    }


    /**
    *  Set application id. Id for use in the reporting system
    *
    *@param  appid  Application id
    *@since
    */
    public void setappid(String appid) {
        this.appid = appid;
    }


    /**
    *  Gets the application id of the LogClient object
    *
    *@return    application id
    *@since
    */
    public String getappid() {
        return this.appid;
    }


    /**
    *  Gets the appdata ref of the LogClient object
    *
    *@return    appdata ref
    *@since
    */
    public Hashtable getappdata() {
        return this.appdata;
    }


    /**
    *  Gets the num_recs attribute of the LogClient object
    *
    *@return    int num_recs value
    *@since
    */
    public int getnum_recs() {
        return this.num_recs;
    }



    /**
    *  Gets the application attribute of the LogClient object
    *
    *@return    String application value
    *@since
    */
    public String getapplication() {
        return this.application;
    }



    /**
    *  Gets the action attribute of the LogClient object
    *
    *@return    String action value
    *@since
    */
    public String getaction() {
        return this.action;
    }



    /**
    *  Gets the query_string attribute of the LogClient object
    *
    *@return    String query_string value
    *@since
    */
    public String getquery_string() {
        return this.query_string;
    }


    /**
    *  Gets the save_search attribute of the LogClient object
    *
    *@return    int save_search value
    *@since
    */
    public int getsave_search() {
        return this.save_search;
    }



    /**
    *  Gets the rec_range attribute of the LogClient object
    *
    *@return    int rec_range value
    *@since
    */
    public int getrec_range() {
        return this.rec_range;
    }



    /**
    *  Gets the hits attribute of the LogClient object
    *
    *@return    int hits value
    *@since
    */
    public int gethits() {
        return this.hits;
    }



    /**
    *  Gets the report_name attribute of the LogClient object
    *
    *@return    String report_name value
    *@since
    */
    public String getreport_name() {
        return this.report_name;
    }



    /**
    *  Gets the response_time attribute of the LogClient object
    *
    *@return    long response_time value
    *@since
    */
    public long getresponse_time() {
        return this.response_time;
    }



    /**
    *  Gets the end_time attribute of the LogClient object
    *
    *@return    long end_time value
    *@since
    */
    public long getend_time() {
        return this.end_time;
    }



    /**
    *  Gets the begin_time attribute of the LogClient object
    *
    *@return    long begin_time value
    *@since
    */
    public long getbegin_time() {
        return this.begin_time;
    }


    /**
    *  Gets the db_name attribute of the LogClient object
    *
    *@return    String db_name value
    *@since
    */
    public String getdb_name() {
        return this.db_name;
    }


    /**
    *  Gets the tid attribute of the LogClient object
    *
    *@return    long tid value
    *@since
    */
    public long gettid() {
        return this.tid;
    }


    /**
    *  Gets the sid attribute of the LogClient object
    *
    *@return    long sid value
    *@since
    */
    public String getsid() {
        return this.sid;
    }


    /**
    *  Gets the sid as a string attribute of the LogClient object
    *
    *@return    string sid value
    *@since

    public String getsidAsLongString() {
    return makeLongString(this.sid);
    }

    *
    */


    /**
    *  Gets the cust_id attribute of the LogClient object
    *
    *@return    long cust_id value
    *@since
    */
    public String getcust_id() {
        return this.cust_id;
    }


    /**
    *  Gets the statuscode attribute of the LogClient object
    *
    *@return    statuscode Value
    *@since
    */
    public int getstatuscode() {
        return this.statuscode;
    }



    /**
    *  Gets the bytes attribute of the LogClient object
    *
    *@return    bytes Value
    *@since
    */
    public int getbytes() {
        return this.bytes;
    }



    /**
    *  Gets the referrer attribute of the LogClient object
    *
    *@return    referrer Value
    *@since
    */
    public String getreferrer() {
        return this.referrer;
    }


    /**
    *  Gets the user_agent attribute of the LogClient object
    *
    *@return    user_agent Value
    *@since
    */
    public String getuser_agent() {
        return this.user_agent;
    }


    /**
    *  Gets the cookies attribute of the LogClient object
    *
    *@return    cookies Value
    *@since
    */
    public String getcookies() {
        return this.cookies;
    }


    /**
    *  Gets the date attribute of the LogClient object
    *
    *@return    date Value
    *@since
    */
    public long getdate() {
        return this.date;
    }


    /**
    *  Gets the uri_stem attribute of the LogClient object
    *
    *@return    uri_stem Value
    *@since
    */
    public String geturi_stem() {
        return this.uri_stem;
    }


    /**
    *  Gets the uri_query attribute of the LogClient object
    *
    *@return    uri_query Value
    *@since
    */
    public String geturi_query() {
        return this.uri_query;
    }


    /**
    *  Gets the HTTP method attribute of the LogClient object
    *
    *@return    HTTPmethod Value
    *@since
    */
    public String getHTTPmethod() {
        return this.HTTPmethod;
    }



    /**
    *  Gets the username attribute of the LogClient object
    *
    *@return    username Value
    *@since
    */
    public String getusername() {
        return this.username;
    }


    /**
    *  Gets the host attribute of the LogClient object
    *
    *@return    The host value
    *@since
    */
    public String getHost() {
        return this.host;
    }


    /**
    *  Gets the rfc931 attribute of the LogClient object
    *
    *@return    The rfc931 value
    *@since
    */
    public String getrfc931() {
        return this.rfc931;
    }


    /**
    *  Resets all attributes of the LogClient object
    *
    *@since
    *@void
    */
    public void reset() {

        this.appdata.clear();

        this.appid = null;
        this.rid = null;
        this.host = "-";
        this.rfc931 = "-";
        this.username = "-";
        this.date = 0;
        this.HTTPmethod = "GET";
        this.uri_stem = "-";
        this.uri_query = "-";
        this.prot_version = "1.0";
        this.statuscode = 200;
        this.bytes = 0;
        this.referrer = "-";
        this.user_agent = "-";
        this.cookies = "-";
        this.cust_id = "0";
        this.sid = null;
        this.tid = 0;
        this.db_name = "-";

        this.begin_time = 0;
        this.end_time = 0;
        this.response_time = 0;
        this.report_name = "-";
        this.hits = 0;
        this.query_string = "-";
        this.rec_range = 0;
        this.save_search = 0;
        this.action = "-";
        this.application = "-";
        this.num_recs = 0;
    }


    /**
    *  Post the data to the LogServer
    *
    *@return                  Description of the Returned Value
    *@exception  IOException  If post fail log to flatfile (not implemented)
    *@since
    */
    public int sendit() throws IOException {

        InputStream inStream = null;
        HttpURLConnection conn = null;
        PrintWriter ps = null;

        try {
        	
        	//System.out.println("baseURL"+baseURL);
            URL myNewURL = new URL(baseURL);

            conn = (HttpURLConnection) myNewURL.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            // TMH - set the User-Agent so we can see calls in the log
            conn.setRequestProperty("User-Agent", "EngineeringVillage LogClient");

            StringBuffer psBuffer = new StringBuffer();

            psBuffer.append(URLEncoder.encode("reqid")).append("=").append(URLEncoder.encode(rid)).append("&");
            psBuffer.append(URLEncoder.encode("appid")).append("=").append(URLEncoder.encode(appid)).append("&");
            psBuffer.append(URLEncoder.encode("host")).append("=").append(URLEncoder.encode(host)).append("&");
            psBuffer.append(URLEncoder.encode("rfc931")).append("=").append(URLEncoder.encode(rfc931)).append("&");
            psBuffer.append(URLEncoder.encode("username")).append("=").append(URLEncoder.encode(username)).append("&");
            psBuffer.append(URLEncoder.encode("date")).append("=").append(URLEncoder.encode(Long.toString(date))).append("&");
            psBuffer.append(URLEncoder.encode("HTTPmethod")).append("=").append(URLEncoder.encode(HTTPmethod)).append("&");
            psBuffer.append(URLEncoder.encode("uri_stem")).append("=").append(URLEncoder.encode(uri_stem)).append("&");

            if (uri_query != null)
            {
                psBuffer.append(URLEncoder.encode("uri_query")).append("=").append(URLEncoder.encode(uri_query)).append("&");
            }
            psBuffer.append(URLEncoder.encode("prot_version")).append("=").append(URLEncoder.encode(prot_version)).append("&");
            psBuffer.append(URLEncoder.encode("statuscode")).append("=").append(URLEncoder.encode(Integer.toString(statuscode))).append("&");
            psBuffer.append(URLEncoder.encode("bytes")).append("=").append(URLEncoder.encode(Integer.toString(bytes))).append("&");

            if (referrer != null)
            {
                psBuffer.append(URLEncoder.encode("referrer")).append("=").append(URLEncoder.encode(referrer)).append("&");
            }

            if(user_agent != null)
            {
                psBuffer.append(URLEncoder.encode("user_agent")).append("=").append(URLEncoder.encode(user_agent)).append("&");
            }
            else
            {
                psBuffer.append(URLEncoder.encode("user_agent")).append("=NA&");
            }

            if (cookies != null)
            {
                psBuffer.append(URLEncoder.encode("cookies")).append("=").append(URLEncoder.encode(cookies)).append("&");
            }
            if (cust_id != null)
            {
                psBuffer.append(URLEncoder.encode("cust_id")).append("=").append(URLEncoder.encode(cust_id)).append("&");
            }
            if (sid != null)
            {
                psBuffer.append(URLEncoder.encode("sid")).append("=").append(URLEncoder.encode(getsid())).append("&");
            }
            if (tid > 0)
            {
                psBuffer.append(URLEncoder.encode("tid")).append("=").append(URLEncoder.encode(Long.toString(tid))).append("&");
            }

            //if (db_name != null) {
            //  psBuffer.append(URLEncoder.encode("db_name")).append("=").append(URLEncoder.encode(db_name)).append("&");
            //}
            psBuffer.append(URLEncoder.encode("begin_time")).append("=").append(URLEncoder.encode(Long.toString(begin_time))).append("&");

            psBuffer.append(URLEncoder.encode("end_time")).append("=").append(URLEncoder.encode(Long.toString(end_time))).append("&");
            if (tid > -1)
            {
                psBuffer.append(URLEncoder.encode("response_time")).append("=").append(URLEncoder.encode(Long.toString(response_time))).append("&");
            }
            if (report_name != null)
            {
                psBuffer.append(URLEncoder.encode("report_name")).append("=").append(URLEncoder.encode(report_name)).append("&");
            }

            //System.out.println("psBuffer.toString()"+psBuffer.toString());
            ps = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));
            ps.print(psBuffer.toString());
            psBuffer = null;

            if (appdata != null)
            {
                // Change to lower case to remove duplicates.
                Enumeration k = appdata.keys();
                while (k.hasMoreElements())
                {
                    String key = (String) k.nextElement();
                    String value = (String) appdata.get(key);
                    appdata.remove(key);
                    appdata.put(key.toLowerCase(),value);

                }

                addToAppData("cust_id", new String(cust_id));
                addToAppData("ip", new String(host));
                addToAppData("sid", new String(getsid()));
                addToAppData("tid", new String(Long.toString(tid)));
                addToAppData("db_name", new String(db_name));
                addToAppData("begin_time", new String(Long.toString(begin_time)));
                addToAppData("end_time", new String(Long.toString(end_time)));
                addToAppData("response_time", new String(Long.toString(response_time)));
                addToAppData("report_name", new String(report_name));
                addToAppData("hits", new String(Integer.toString(hits)));
                addToAppData("report_name", new String(report_name));
                addToAppData("query_string", new String(query_string));
                addToAppData("rec_range", new String(Integer.toString(rec_range)));
                addToAppData("save_search", new String(Integer.toString(save_search)));
                addToAppData("action", new String(action));
                addToAppData("application", new String(application));
                addToAppData("num_recs", new String(Integer.toString(num_recs)));

                if (referrer != null)
                {
                    this.appdata.put("referrer",referrer);
                }
                else
                {
                    this.appdata.put("referrer","-");
                }
                /*
                this.appdata.put("cookies",cookies);
                this.appdata.put("reqid",rid);
                this.appdata.put("appid",appid);
                this.appdata.put("host",host);
                this.appdata.put("rfc931",rfc931);

                if (username != null) {
                this.appdata.put("username",username);
                } else {
                this.appdata.put("username","-");
                }

                this.appdata.put("date",Long.toString(date));
                this.appdata.put("HTTPmethod",HTTPmethod);
                this.appdata.put("uri_stem",uri_stem);
                if (uri_query != null) {
                this.appdata.put("uri_query",uri_query);
                }
                this.appdata.put("prot_version",prot_version);
                this.appdata.put("statuscode",Integer.toString(statuscode));
                this.appdata.put("bytes",Integer.toString(bytes));*/

                //String appdatas = new String(appdata.toString());
                StringBuffer appdatas = new StringBuffer();

                Enumeration aenum = appdata.keys();

                while(aenum.hasMoreElements())
                {
                    String lKey = (String)aenum.nextElement();
                    if (lKey.equalsIgnoreCase("format"))
                    {
                        if ( ((String) appdata.get(lKey)).equalsIgnoreCase("cit"))
                        {
                            appdatas.append(lKey).append("=\"citation\";");
                        }
                        else if ( ((String) appdata.get(lKey)).equalsIgnoreCase("ab"))
                        {
                            appdatas.append(lKey).append("=\"abstract\";");
                        }
                        else if ( ((String) appdata.get(lKey)).equalsIgnoreCase("ful"))
                        {
                            appdatas.append(lKey).append("=\"detailed\";");
                        }
                        else
                        {
                            appdatas.append(lKey).append("=\"").append(appdata.get(lKey)).append("\";");
                        }
                    }
                    else
                    {
                        appdatas.append(lKey).append("=\"").append(appdata.get(lKey)).append("\";");
                    }
                }
                //System.out.println("appdata as toString()"+appdatas.toString());
                ps.print(URLEncoder.encode("appdata"));
                ps.print("=");
                ps.print(URLEncoder.encode(appdatas.substring(0,appdatas.length()-1)));
                ps.print("&");
            }
            ps.flush();

            inStream = conn.getInputStream();
            while(inStream.read() != -1) {

            }

            return (conn.getResponseCode());
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch(Exception nested)
                {
                    nested.printStackTrace();
                }
            }
            if(inStream != null)
            {
                try
                {
                    inStream.close();
                }
                catch(Exception nested)
                {
                    nested.printStackTrace();
                }
            }
            if(conn != null)
            {
                try
                {
                    conn.disconnect();
                }
                catch(Exception nested)
                {
                    nested.printStackTrace();
                }

            }
        }

    }


    /**
    *  Puts parameters into Appdata Hashtable only if not "-" (default for most).
    *  Hashtabe values (jsp set values) have higher priority over values set in controller
    *
    *@param  key     String Hash key
    *@param  value   String value
    *@return boolean true if inserted
    *@since
    */
    public boolean addToAppData(String key, String value) {

        if (value.equals("-") || this.appdata.containsKey(key)) {
            return (false);
        } else {
            this.appdata.put(key,value);
        }

        return (true);
    }


}

