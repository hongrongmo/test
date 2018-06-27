/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;


/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PUCNavigator extends PatentNavigator
{
	public PUCNavigator(String name)
	{
		super(name);
	}

    public EiModifier createPatentModifier(int i, String slable, String svalue)
    {
        return new PUCModifier(i, slable, svalue);
    }
    
}
