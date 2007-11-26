package org.ei.bulletins;


import java.util.Calendar;
import java.net.URLEncoder;
import org.ei.util.MD5Digester;



public class BulletinSecurity
{
    private static final String SECRET="qazwsxeujm";
	private static final int expired=10; //10 minutes

    public boolean isValidKey(String authorityCode,
                            		 String date)
                            throws Exception
    {
        boolean test=false;



        if(authorityCode != null)
        {
            String md5key1 = getKey(date);
            if(authorityCode.equalsIgnoreCase(md5key1))
                test=true;
        }

        return test;

    }

    public boolean isExpired(String time) throws Exception
    {
		boolean isExpired = true;
		if((Long.parseLong(getTime())-Long.parseLong(time))<expired)
		{
			isExpired = false;
		}
		return isExpired;
	}

	public String getKey() throws Exception
	{
		String time = getTime();
		return getKey(time);
	}

    public String getKey(String date) throws Exception
    {
        MD5Digester digester = new MD5Digester();
        StringBuffer dataBuffer = new StringBuffer();
        dataBuffer.append(SECRET).append(date);
        String strMD5 = digester.asHex(digester.digest(dataBuffer.toString()));
        return strMD5;
    }

    public String getTime() throws Exception
	{
		int days = 0;
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(System.currentTimeMillis());

		String DATE_FORMAT = "yyyyMMddHHmm";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);

		return sdf.format(c1.getTime());
    }
}