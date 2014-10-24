package org.ei.session;

import java.io.Serializable;

public class CARSMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8099262526474067120L;
	private String headerContent;
	private String iPAuthStatus;
	private String lastSuccessAccessTime;
	private String prevPage;
	private String currentPage;
	private boolean flowComplete;

	public String getHeaderContent() {
		return headerContent;
	}

	public void setHeaderContent(String headerContent) {
		this.headerContent = headerContent;
	}

	public String getiPAuthStatus() {
		return iPAuthStatus;
	}

	public void setiPAuthStatus(String iPAuthStatus) {
		this.iPAuthStatus = iPAuthStatus;
	}

	public String getLastSuccessAccessTime() {
		return lastSuccessAccessTime;
	}

	public void setLastSuccessAccessTime(String lastSuccessAccessTime) {
		this.lastSuccessAccessTime = lastSuccessAccessTime;
	}

	public String getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(String prevPage) {
		this.prevPage = prevPage;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public boolean isFlowComplete() {
		return flowComplete;
	}

	public void setFlowComplete(boolean flowComplete) {
		this.flowComplete = flowComplete;
	}

}