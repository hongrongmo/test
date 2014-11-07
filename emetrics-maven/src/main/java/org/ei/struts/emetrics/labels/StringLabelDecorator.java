/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.display.ColumnDecorator;
import org.ei.struts.emetrics.Constants;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StringLabelDecorator extends ColumnDecorator {

    protected static Log log = LogFactory.getLog(StringLabelDecorator.class);

    public String decorate(Object columnValue) {

		if(columnValue == null) {
			return Constants.EMPTY_STRING;
		}

        return (columnValue.toString());

     }
}
