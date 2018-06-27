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
public class RONavigator extends EiNavigator {
    public RONavigator() {
        super(EiNavigator.RO);
    }

    public EiModifier createModifier(int i, String slabel, String svalue) {
        if (svalue.toUpperCase().equalsIgnoreCase("R")) {
            slabel = "Reagent";
        } else if (svalue.toUpperCase().equalsIgnoreCase("P")) {
            slabel = "Product";
        } else if (svalue.toUpperCase().equalsIgnoreCase("N")) {
            slabel = "No Role";
        }
        return new EiModifier(i, slabel, svalue);
    }
}
