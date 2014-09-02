/*
 * Created on Mar 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.sql.Timestamp;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractLabel {

   public static AbstractLabel createLabel(Object val) {
		if(val != null) {
			String classname = val.getClass().getName();
	
			if(classname.equalsIgnoreCase("java.sql.timestamp")) {
				return new TimestampLabel( (Timestamp) val); 
			}
			if(classname.equalsIgnoreCase("java.lang.string")) {
				return new StringLabel( (String) val); 
			}
			return new StringLabel("ERROR! unknown type: " + classname);
		}
		else {
			return new StringLabel("NULL!");
		}
   }
   
   public abstract void accept(ChartLabelVisitor chartLabelVisitor);
}
