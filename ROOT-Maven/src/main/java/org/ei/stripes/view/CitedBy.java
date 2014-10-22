package org.ei.stripes.view;

import java.net.URLEncoder;

import org.apache.commons.httpclient.URIException;

public class CitedBy {
	private String md5; 	// MD5
	private String issn;	// ISSN
	private String firstissue;	
	private String firstvolume; 
	private String firstpage;	
	private String doi;		
	private String pii;		
	private String an;
	
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getIssn() {
		return issn;
	}
	public void setIssn(String issn) {
		this.issn = issn;
	}
	public String getFirstissue() {
		return firstissue;
	}
	public void setFirstissue(String firstissue) {
		this.firstissue = firstissue;
	}
	public String getFirstvolume() {
		return firstvolume;
	}
	public void setFirstvolume(String firstvolume) {
		this.firstvolume = firstvolume;
	}
	public String getFirstpage() {
		return firstpage;
	}
	public void setFirstpage(String firstpage) {
		this.firstpage = firstpage;
	}
	public String getDoi() {
		return doi;
	}
	public String getDoiencoded() throws URIException {
		return URLEncoder.encode(doi);
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getPii() {
		return pii;
	}
	public void setPii(String pii) {
		this.pii = pii;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}	

}
