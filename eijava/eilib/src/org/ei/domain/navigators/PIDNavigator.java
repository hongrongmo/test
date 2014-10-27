<<<<<<< HEAD
/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.ei.util.StringUtil;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PIDNavigator extends PatentNavigator
{
	public PIDNavigator(String name)
	{
		super(name);
	}

    public EiModifier createPatentModifier(int i, String slable, String svalue)
    {

        return new PIDModifier(i, slable, svalue);
    }

}
=======
/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.ei.util.StringUtil;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PIDNavigator extends PatentNavigator
{
	public PIDNavigator(String name)
	{
		super(name);
	}

    public EiModifier createPatentModifier(int i, String slable, String svalue)
    {
        return new PIDModifier(i, slable, svalue);
    }
    
}
>>>>>>> 15dca715b02cb03c5fa8fd5c16635f7e802f1eae
