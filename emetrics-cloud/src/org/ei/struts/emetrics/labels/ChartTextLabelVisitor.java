/*
 * Created on Mar 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

import java.text.SimpleDateFormat;


/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChartTextLabelVisitor extends ChartLabelVisitor {

	/* (non-Javadoc)
	 * @see org.ei.struts.emetrics.utils.ChartLableVisitor#visit(java.sql.Timestamp)
	 */
	public void visit(TimestampLabel label) {
	
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		this.setChartLabel(formatter.format(label.getLabel()));
	}

	/* (non-Javadoc)
	 * @see org.ei.struts.emetrics.utils.ChartLableVisitor#visit(java.lang.String)
	 */
	public void visit(StringLabel label) {
		this.setChartLabel(label.getLabel());
	}

}
