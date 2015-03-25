package org.ei.fulldoc;


import java.util.Calendar;
import java.net.URLEncoder;
import org.ei.util.MD5Digester;



public class UniventioPDFGateway
{


    private static final String URL= "http://ipdatadirect.lexisnexis.com//downloadpdf.aspx?lg=Elsevier&pw=N3Wservice2&pdf=";
    private static final String SECRET="qazwsxeujm";



    public static String getUniventioLink(String authorityCode,
                                   String patentNumber,
                                   String kindCode)
        throws Exception
    {
        StringBuffer fullUrl = new StringBuffer();
        fullUrl.append(URL).append(authorityCode).append(",").append(patentNumber).append(",").append(kindCode);
        //System.out.println(fullUrl.toString());
        return fullUrl.toString();
    }


    public static String  getPatentLink(String authorityCode,
                                String patentNumber,
                                String kindCode )
            throws Exception
    {
        StringBuffer fullUrl = new StringBuffer();
        fullUrl.append("/controller/servlet/PatentPDF?").append("ac=").append(authorityCode).append("&pn=").append(patentNumber).append("&kc=").append(kindCode).append("&type=PDF").append("&key=").append(getKey(authorityCode,patentNumber,kindCode,getDateKey(true)));
        return fullUrl.toString();
    }

    public static String  getPatentLink(String authorityCode,
                                    String patentNumber,
                                    String kindCode,
                                    String redirect)
                throws Exception
    {
        StringBuffer fullUrl = new StringBuffer();
        fullUrl.append("/controller/servlet/Patent.pdf?").append("ac=").append(authorityCode).append("&pn=").append(patentNumber).append("&kc=").append(kindCode).append("&type=PDF").append("&key=").append(getKey(authorityCode,patentNumber,kindCode,getDateKey(true))).append("&rurl=").append(URLEncoder.encode(redirect,"UTF-8"));
        return fullUrl.toString();
    }

    public static boolean isValidKey(String authorityCode,
                             String patentNumber,
                             String kindCode,
                             String key )
                            throws Exception
    {
        boolean test=false;
        if(key != null) {
            String md5key1 = getKey(authorityCode,patentNumber,kindCode,getDateKey(true));
            if(key.equalsIgnoreCase(md5key1))
                test=true;
            else if (key.equalsIgnoreCase(getKey(authorityCode,patentNumber,kindCode,getDateKey(false))))
                test=true;
        }
            return test;

    }


    public static String getKey(String ac,String pn,String kc,String date) throws Exception
    {
        MD5Digester digester = new MD5Digester();
        StringBuffer dataBuffer = new StringBuffer();
        dataBuffer.append(SECRET).append(ac).append(pn).append(kc).append(date);
        String strMD5 = digester.asHex(digester.digest(dataBuffer.toString()));
        //System.out.println(strMD5);
        return strMD5;
    }

    public static String getDateKey(boolean today)
    {
        int days = 0;
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(System.currentTimeMillis());
        if (!today)
          c1.add(Calendar.DATE,-1);

        String DATE_FORMAT = "yyyyMMdd";
        java.text.SimpleDateFormat sdf =
              new java.text.SimpleDateFormat(DATE_FORMAT);

         //System.out.println(sdf.format(c1.getTime()));
        return sdf.format(c1.getTime());
    }
}