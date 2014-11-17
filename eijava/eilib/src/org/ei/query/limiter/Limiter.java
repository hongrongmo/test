package org.ei.query.limiter;

public interface Limiter {
	public String getDisplayQueryFormat(); 
	public String getPhysicalQueryFormat();
	public String getDBStoreFormat();
	public String toXML();
}
