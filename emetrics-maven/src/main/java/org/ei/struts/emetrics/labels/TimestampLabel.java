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
public class TimestampLabel extends AbstractLabel {

	Timestamp label;
	
	public TimestampLabel(Timestamp label) {
		this.setLabel(label);		
	}
	/* (non-Javadoc)
	 * @see org.ei.struts.emetrics.utils.AbstractLabel#accept(org.ei.struts.emetrics.utils.ChartLabelVisitor)
	 */
	public void accept(ChartLabelVisitor chartLabelVisitor) {
		// TODO Auto-generated method stub
		chartLabelVisitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see org.ei.struts.emetrics.utils.AbstractLabel#formatLabel()
	 */
	public String formatLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public Timestamp getLabel() {
		return label;
	}

	/**
	 * @param timestamp
	 */
	public void setLabel(Timestamp timestamp) {
		label = timestamp;
	}

}
