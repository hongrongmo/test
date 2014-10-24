/*
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.bulletins;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulletinLinkBuilder {

	private static final String PAT_PATH = "bulletins/api/patent/";
	private static final String LIT_PATH = "bulletins/api/lit/";

	public BulletinLinkBuilder(){
    	
	}
	/**
	 * Builds the bulletin link
	 * @param bulletin - The bulletin
	 * @return String
	 */
	public String buildLink(Bulletin bulletin) {

		String cType = bulletin.getContentType();
		String link = "";

		if (cType.equalsIgnoreCase("PDF") || cType.equalsIgnoreCase("SAVEPDF"))
			link = buildPDFLink(bulletin);
		else if (cType.equalsIgnoreCase("HTML") || cType.equalsIgnoreCase("SAVEHTML"))
			link = buildHTMLLink(bulletin);
		else if (cType.equalsIgnoreCase("ZIP"))
			link = buildGIFLink(bulletin);

		return link;

	}
	/**
	 * Build link to pdf version of the bulletin
	 * @param bulletin - The bulletin
	 * @return String
	 */
	public String buildPDFLink(Bulletin bulletin) {

		String db = bulletin.getDatabase();
		String yr = bulletin.getYear();
		String category = bulletin.getCategory();

		StringBuffer bufLink = new StringBuffer();

		bufLink.append(buildDBPath(db));

		bufLink.append(yr).append("/");
		bufLink.append(category).append("/");
		bufLink.append("pdf/");
		bufLink.append(bulletin.getFileName()).append(".pdf");

		return bufLink.toString();
	}
	/**
	 * Build link to html version of bulletin
	 * @param bulletin - The bulletin
	 * @return String
	 */
	public String buildHTMLLink(Bulletin bulletin) {

		String db = bulletin.getDatabase();
		String yr = bulletin.getYear();
		String category = bulletin.getCategory();

		StringBuffer bufLink = new StringBuffer();

		bufLink.append(buildDBPath(db));

		bufLink.append(yr).append("/");
		bufLink.append(category).append("/");
		bufLink.append("html/");
		bufLink.append(bulletin.getFileName()).append(".htm");

		return bufLink.toString();
	}
	/**
	 * Build Link to patent gif files
	 * @param bulletin - The bulletin
	 * @return String
	 */
	public String buildGIFLink(Bulletin bulletin) {

		String db = bulletin.getDatabase();
		String yr = bulletin.getYear();
		String category = bulletin.getCategory();

		StringBuffer bufLink = new StringBuffer();

		bufLink.append(buildDBPath(db));

		bufLink.append(yr).append("/");
		bufLink.append(category).append("/");
		bufLink.append("graphic/");
		bufLink.append(bulletin.getZipFileName()).append(".zip");

		return bufLink.toString();
	}
	/**
	 * Returns the bulletin db path. 1 = /lit/, 2 = /patent/
	 * @param db - The database 1 for LIT 2 for PAT
	 * @return String
	 */
	private String buildDBPath(String db) {

		if (db.equals("1"))
			return LIT_PATH;
		else
			return PAT_PATH;

	}
}
