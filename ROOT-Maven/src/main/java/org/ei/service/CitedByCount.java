package org.ei.service;

import java.util.List;

/**
 * The Class CitedByCount.
 */
public class CitedByCount {
	String _citedByCount;
	String _eid;
	String _doi;
	String _scopusid;
	String _clientCRF;
	List<?> _publisherList;
	List<?> _sourceInfoList;
	List<?> _sourceYearInfoList;
	List<?> _topicList;
	String _issn;
	String _isbn;
	String _eissn;
	String _vol;
	String _issue;
	String _documenttype;
	String _firstAuthorSurname;
	String _yearOfPublication;
	String _firstPageNumber;
	String _lastPageNumber;
	String _firstInitialFirstAuthor;
	String _articleTitle;
	String _pii;
	String _an;
	String _sessionID;
	String _md5;

	public void setAccessionNumber(String an) {
		_an = an;
	}

	public void setAn(String an) {
		_an = an;
	}

	public String getAccessionNumber() {
		return _an;
	}

	public void setSessionID(String sessionID) {
		_sessionID = sessionID;
	}
	public void setSid(String sessionID) {
		_sessionID = sessionID;
	}
	public String getSessionID() {
		return _sessionID;
	}

	public void setMD5(String md5) {
		_md5 = md5;
	}
	public void setSecurity(String md5) {
		_md5 = md5;
	}

	public String getMD5() {
		return _md5;
	}

	public String getDoi() {
		return _doi;
	}

	public void setDoi(String doi) {
		this._doi = doi;
	}

	public String getCitedByCount() {
		return _citedByCount;
	}

	public void setCitedByCount(String citedByCount) {
		this._citedByCount = citedByCount;
	}

	public void setScopusid(String scopusid) {
		this._scopusid = scopusid;
	}

	public String getScopusid() {
		return _scopusid;
	}

	public void setClientCRF(String clientCRF) {
		this._clientCRF = clientCRF;
	}

	public String getClientCRF() {
		return _clientCRF;
	}

	public void setPublisherList(List<?> publisherList) {
		this._publisherList = publisherList;
	}

	public List<?> getPublisherList() {
		return _publisherList;
	}

	public void setSourceInfoList(List<?> sourceInfoList) {
		this._sourceInfoList = sourceInfoList;
	}

	public List<?> getSourceInfoList() {
		return _sourceInfoList;
	}

	public void setSourceYearInfoList(List<?> sourceYearInfoList) {
		this._sourceYearInfoList = sourceYearInfoList;
	}

	public List<?> getSourceYearInfoList() {
		return _sourceYearInfoList;
	}

	public void setTopicListt(List<?> topicList) {
		this._topicList = topicList;
	}

	public List<?> getTopicList() {
		return _topicList;
	}

	public void setIssn(String issn) {
		this._issn = issn;
	}

	public String getIssn() {
		return _issn;
	}

	public void setIsbn(String isbn) {
		this._isbn = isbn;
	}

	public String getIsbn() {
		return _isbn;
	}

	public void setEissn(String eissn) {
		this._eissn = eissn;
	}

	public String getEissn() {
		return _eissn;
	}

	public void setVol(String vol) {
		this._vol = vol;
	}


	public String getVol() {
		return _vol;
	}

	public void setIssue(String issue) {
		this._issue = issue;
	}

	public String getIssue() {
		return _issue;
	}

	public String getDocumenttype() {
		return _documenttype;
	}

	public void setDocumenttype(String documenttype) {
		this._documenttype = documenttype;
	}

	public String getFirstAuthorSurname() {
		return _firstAuthorSurname;
	}

	public void setFirstAuthorSurname(String firstAuthorSurname) {
		this._firstAuthorSurname = firstAuthorSurname;
	}

	public String getYearOfPublication() {
		return _yearOfPublication;
	}

	public void setYearOfPublication(String yearOfPublication) {
		this._yearOfPublication = yearOfPublication;
	}

	public void setFirstPageNumber(String firstPageNumber) {
		this._firstPageNumber = firstPageNumber;
	}

	public void setPage(String firstPageNumber) {
		this._firstPageNumber = firstPageNumber;
	}

	public String getFirstPageNumber() {
		return _firstPageNumber;
	}

	public void setLastPageNumber(String lastPageNumber) {
		this._lastPageNumber = lastPageNumber;
	}

	public String getLastPageNumber() {
		return _lastPageNumber;
	}

	public void setFirstInitialFirstAuthor(String firstInitialFirstAuthor) {
		this._firstInitialFirstAuthor = firstInitialFirstAuthor;
	}

	public String getFirstInitialFirstAuthor() {
		return _firstInitialFirstAuthor;
	}

	public void setArticleTitle(String articleTitle) {
		this._articleTitle = articleTitle;
	}

	public String getArticleTitle() {
		return _articleTitle;
	}

	public void setPii(String pii) {
		this._pii = pii;
	}

	public String getPii() {
		return _pii;
	}
}