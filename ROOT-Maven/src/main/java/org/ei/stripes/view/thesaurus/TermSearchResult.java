package org.ei.stripes.view.thesaurus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Term Search Result object 
 * @author harovetm
 */
public class TermSearchResult {
	private String term;
	private String type;
	private int rnum;
	private int ntc;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getEncodedterm() throws UnsupportedEncodingException {
		return URLEncoder.encode(term, "UTF-8");
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRnum() {
		return rnum;
	}
	public void setRnum(int rnum) {
		this.rnum = rnum;
	}
	public int getNtc() {
		return ntc;
	}
	public void setNtc(int ntc) {
		this.ntc = ntc;
	}
}
