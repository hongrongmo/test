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
public abstract class ChartLabelVisitor {

	String chartLabel;
	public void setChartLabel(String chartLabelIn) {this.chartLabel = chartLabelIn;}
	public String getChartLabel() {return this.chartLabel;}     
    
	public abstract void visit(TimestampLabel labelinfo);  
	public abstract void visit(StringLabel labelinfo);   

}
