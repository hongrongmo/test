package org.ei.bulletins;

import org.ei.util.MD5Digester;

public class BulletinSecurity
{
    private static final String SECRET = "qazwsxeujm";
    private static final long TWENTY_MINUTES = 1200000; // 20 minutes

    public boolean isValidKey(String authorityCode, String date, String filename) throws Exception
    {
        boolean isvalid = false;
        if (authorityCode != null)
        {
            String md5key1 = getKey(filename,date);
            if (authorityCode.equalsIgnoreCase(md5key1))
            {
                isvalid = true;
            }
        }
        return isvalid;
    }

    public boolean isExpired(String timems)
    {
        boolean isExpired = false;
        // Test if time diff is greater than xx minutes
        try
        {
          if ((System.currentTimeMillis() - (Long.parseLong(timems))) > BulletinSecurity.TWENTY_MINUTES)
          {
              isExpired = true;
          }
        }
        catch(NumberFormatException e)
        {
          /// if we cannot parse the timestring, fail the validation
          isExpired = true;
        }
        return isExpired;
    }

    public String getKey(String fileName) throws Exception
    {
        String time = getTime();
        return getKey(fileName,time);
    }

    public String getKey(String fileName,String timems) throws Exception
    {
        MD5Digester digester = new MD5Digester();
        StringBuffer dataBuffer = new StringBuffer();
        dataBuffer.append(BulletinSecurity.SECRET).append(timems+fileName);
        String strMD5 = digester.asHex(digester.digest(dataBuffer.toString()));
        return strMD5;
    }

    public String getTime() throws Exception
    {
        return String.valueOf(System.currentTimeMillis());
    }
}