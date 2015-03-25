/*
 * Created on Nov 8, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data.upt.runtime;


/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PatRefQuery
{
	private StringBuffer qBuf = new StringBuffer();

	public String getQuery()
	{
		if(qBuf.length() <= 1)
		{
			return null;
		}

		qBuf.append(")");
		return qBuf.toString();
	}

    public void addPat(String authCode,
    				   String pn)
    {
		if (authCode != null)
		{
			if(qBuf.length() > 0)
			{
				qBuf.append(" OR ");
			}
			else
			{
				qBuf.append("(");
			}


			if (pn != null && !pn.equals(""))
			{
				qBuf.append(authCode);
				qBuf.append(pn);
				if(authCode.equalsIgnoreCase("ep"))
				{
					qBuf.append("A*");
				}
				qBuf.append(" WN PM ");
			}

		}
    }
}
