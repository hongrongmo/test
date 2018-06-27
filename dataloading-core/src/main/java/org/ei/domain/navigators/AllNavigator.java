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
public class AllNavigator extends EiNavigator
{
	//protected static Log log = LogFactory.getLog(AllNavigator.class);

	private String DISPLAYNAME = "All";

	public AllNavigator()
	{
		super(EiNavigator.ALL);
		setDisplayname(DISPLAYNAME);
        setFieldname("ALL");
	}

	// (one two three) => one AND two AND three
	public String getQueryString()
	{
		StringBuffer sb = new StringBuffer();

		Iterator itrmods = (this.getModifiers()).iterator();
		while(itrmods.hasNext())
		{
			EiModifier modifier = (EiModifier) itrmods.next();
			String svalue = (String) modifier.getValue();
			sb.append(svalue);
		}

		return sb.toString();

	}

}
