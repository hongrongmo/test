/*
 * Created on Mar 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.emetrics.labels;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StringLabel extends AbstractLabel {

	String label;
	public StringLabel(String t) {
		this.setLabel(t);		
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
	public String getLabel() {
		return label;
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		label = string;
	}

}
