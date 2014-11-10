package org.ei.gui;

import java.util.Iterator;
import java.util.List;

import org.ei.books.BookQuery;

class RefineSearchForm extends Form implements IFormBuilder {

	Form htmlForm;
	String term1 = null;
	String term2 = null;
	String term3 = null;
	String field1 = null;
	String field2 = null;
	String field3 = null;
	String bool1 = null;
	String bool2 = null;
	String bool3 = null;

	public RefineSearchForm() {
		htmlForm = new Form();
		htmlForm.setCID("resultsKeyWord");
		htmlForm.addHiddenField("docIndex", "1");
		htmlForm.setName("search");
		htmlForm.setActionUrl("/controller/servlet/Controller");
	}
	public void setQuery(String queryString) {

		BookQuery bookQuery = new BookQuery("ID");
		bookQuery.setQuery(queryString);

		term1 = bookQuery.getTerm1();
		term2 = bookQuery.getTerm2();
		term3 = bookQuery.getTerm3();
		field1 = bookQuery.getField1();
		field2 = bookQuery.getField2();
		field3 = bookQuery.getField3();
		bool1 = bookQuery.getBool1();
		
		if(bool1 != null)
			bool1 = bool1.toUpperCase();
		
		bool2 = bookQuery.getBool2();
		
		if(bool1 != null)
					bool1 = bool1.toUpperCase();
					
		List catCodes = bookQuery.getCatCodes();
		
		for (Iterator iter = catCodes.iterator(); iter.hasNext();) {
			String catCode = (String) iter.next();
			
			htmlForm.addHiddenField("catCodes",catCode);
			
		}
	}
	public void setTerm1(String term1) {
		this.term1 = term1;
	}
	public void setTerm2(String term2) {
		this.term2 = term2;
	}
	public void setTerm3(String term3) {
		this.term3 = term3;
	}
	public void setBool1(String bool1) {
		this.bool1 = bool1;

	}
	public void setBool2(String bool2) {
		this.bool2 = bool2;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	public void setField3(String field3) {
		this.field3 = field3;
	}
	public String toHtml() {

		return "";
	}
	public static void main(String args[]) {

		RefineSearchForm frm = new RefineSearchForm();
		System.out.println(frm.toHtml());

	}

}
