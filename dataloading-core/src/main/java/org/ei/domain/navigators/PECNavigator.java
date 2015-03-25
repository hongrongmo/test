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
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PECNavigator extends PatentNavigator {
    public PECNavigator(String name) {
        super(name);
    }

    public EiModifier createPatentModifier(int i, String slable, String svalue) {
        return new PECModifier(i, slable, svalue);
    }

}
