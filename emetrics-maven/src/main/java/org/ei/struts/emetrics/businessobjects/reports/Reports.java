package org.ei.struts.emetrics.businessobjects.reports;

import java.util.Collection;
import javax.servlet.ServletContext;

public interface Reports {

    public abstract String getPathname();
    public abstract void setPathname(String pathname);

	public abstract String getDtdfile();
	public abstract void setDtdfile(String dtdfile);


    public abstract void close() throws Exception;
    public abstract void open() throws Exception;
	public void open(ServletContext context) throws Exception;

    public abstract void addReport(Report report);
    public abstract Collection getReports();

//    public abstract Report findReport(int reportId);

}

