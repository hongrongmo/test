package org.ei.stripes.view.thesaurus;

/**
 * Term Path for Thesaurus search results display (top line)
 * @author harovetm
 *
 */
public class TermPath {
	private String sti;
	private String slink;
	private String scon;
	private int snum;
	public String getSti() {
		return sti;
	}
	public void setSti(String sti) {
		this.sti = sti;
	}
	public String getSlink() {
		return slink;
	}
	public void setSlink(String slink) {
		this.slink = slink;
	}
	public String getScon() {
		return scon;
	}
	public void setScon(String scon) {
		this.scon = scon;
	}
	public int getSnum() {
		return snum;
	}
	public void setSnum(int snum) {
		this.snum = snum;
	}
}
