/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Iterator;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBNavigator extends EiNavigator
{
	public DBNavigator(String name)
	{
		super(name);
	}

	// (one two three) => one AND two AND three
	public String getQueryString()
	{
		//log.info(" OVERRIDDEN getQueryString");
		StringBuffer sb = new StringBuffer();
		sb.append("(");

		Iterator itrmods = (this.getModifiers()).iterator();
		while(itrmods.hasNext())
		{
			EiModifier modifier = (EiModifier) itrmods.next();
			String svalue = (String) modifier.getValue().substring(0,3);
			sb.append(svalue);
			if(itrmods.hasNext())
			{
				sb.append(" OR ");
			}
		}
		sb.append(") WN DB");
//		sb.append((super.getFieldname()).toUpperCase());

		return sb.toString();

	}

}
