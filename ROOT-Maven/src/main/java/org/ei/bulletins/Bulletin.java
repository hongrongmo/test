package org.ei.bulletins;

public class Bulletin {

	String id;
	String database;
	String category;
	String year;
	String pubDt;
	String fileName;
	String zipFileName;
	String contentType;
	String week;

	int format = -1;

	public static final int FORMAT_LIT_RECENT = 1;
	public static final int FORMAT_PAT_RECENT = 2;
	public static final int FORMAT_LIT_RESULTS = 3;
	public static final int FORMAT_PAT_RESULTS = 4;

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return
	 */
	public String getDisplayCategory() {
		return BulletinQuery.getDisplayCategory(category);
	}

	/**
	 * @return
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getPublishedDt() {
		return pubDt;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param string
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param string
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	/**
	 * @param string
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param string
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @param string
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param string
	 */
	public void setPublishedDt(String pubDt) {
		this.pubDt = pubDt;
	}

	/**
	 * @param string
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @param string
	 */

	public void setWeek(String week) {
		this.week = week;
	}

	/**
	 * @return
	 */
	public String getWeek() {
		return week;
	}

	/**
	 * @return
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @param i
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	/**
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return
	 */
	public String getZipFileName() {
		return zipFileName;
	}

	/**
	 * @param string
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void accept(BulletinVisitor v) throws Exception {
		v.visitWith(this);
	}

}