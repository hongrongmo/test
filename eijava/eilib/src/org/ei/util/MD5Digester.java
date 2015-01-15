
package org.ei.util;

import java.security.MessageDigest;

public class MD5Digester
{

	public String asHex(byte[] hash)
	{
		StringBuffer buf = new StringBuffer(hash.length * 2);
    		int i;

    		for (i = 0; i < hash.length; i++) 
		{
      			if (((int) hash[i] & 0xff) < 0x10)
      			{
				buf.append("0");
			}
      	
      			buf.append(Long.toString((int) hash[i] & 0xff, 16));
    		}

    		return buf.toString();	
	}
	
	public byte[] digest(String data)
		throws Exception
	{
		MessageDigest  md5 = MessageDigest.getInstance("MD5");
		return md5.digest(data.getBytes());
	}
}
