package org.ei.gui;

import org.ei.books.BookQuery;

class ExpertSearchForm extends Form implements IFormBuilder {

	Form htmlForm;
	TextField tfTerm1 = null;
	TextField tfTerm2 = null;
	TextField tfTerm3 = null;
	ListBox lbField1 = null;
	ListBox lbField2 = null;
	ListBox lbField3 = null;
	ListBox lbBool1 = null;
	ListBox lbBool2 = null;
	String formType;

	public ExpertSearchForm(String formType, String queryString) {

		BookQuery bookQuery = new BookQuery("ID");

		this.formType = formType;

		bookQuery.setQuery(queryString);

		String term1 = bookQuery.getTerm1();
		String term2 = bookQuery.getTerm2();
		String term3 = bookQuery.getTerm3();
		String field1 = bookQuery.getField1();
		String field2 = bookQuery.getField2();
		String field3 = bookQuery.getField3();
		String bool1 = bookQuery.getBool1();
		String bool2 = bookQuery.getBool2();

		tfTerm1 = new TextField(29);
		tfTerm2 = new TextField(17);
		tfTerm3 = new TextField(17);

		lbField1 = new ListBox();
		lbField1.setOptions(new String[] { "KeyWord", "Author", "ISBN", "Publisher", "Title" });
		lbField1.setValues(new String[] { "AB", "AU", "BN", "PN", "TI" });
		lbField1.setDefaultChoice(field1);
		lbField1.setName("field1");

		lbField2 = new ListBox();
		lbField2.setOptions(new String[] { "KeyWord", "Author", "ISBN", "Publisher", "Title" });
		lbField2.setValues(new String[] { "AB", "AU", "BN", "PN", "TI" });
		lbField2.setDefaultChoice(field2);
		lbField2.setName("field2");

		lbField3 = new ListBox();
		lbField3.setOptions(new String[] { "KeyWord", "Author", "ISBN", "Publisher", "Title" });
		lbField3.setValues(new String[] { "AB", "AU", "BN", "PN", "TI" });
		lbField3.setDefaultChoice(field3);
		lbField3.setName("field3");

		lbBool1.setOptions(new String[] { "AND", "OR", "NOT" });
		lbBool1.setDefaultChoice(bool1);

		lbBool2.setOptions(new String[] { "AND", "OR", "NOT" });
		lbBool2.setDefaultChoice(bool2);

	}
	public void setQuery(String queryString) {
	}

	public String toHtml() {

		StringBuffer toHtmlBuffer = new StringBuffer();

		toHtmlBuffer.append("<form name=\"search\" method=\"post\" action=\"/controller/servlet/Controller\">");
		toHtmlBuffer.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"470\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("<td valign=\"top\" height=\"8\" colspan=\"5\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" height=\"8\"/> ");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("</tr>");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/>");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("<td valign=\"top\" colspan=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<a class=\"SmBlueTableText\"><b>CHOOSE COLLECTION</b></a> ");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("</tr>");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/>");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("<td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" width=\"15\"/>");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("<td valign=\"top\" colspan=\"3\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<a class=\"SmBlackText\">");

		if (!formType.equals("REFINE_SEARCH")) {
			toHtmlBuffer.append("<input type=\"checkbox\" name=\"allcodes\" value = \"ALL\" onclick=\"clearOthers();\">All Referex Engineering collections<br/>");
			toHtmlBuffer.append("<input type=\"checkbox\" name=\"catCodes\" value=\"MAT\" onclick=\"clearAll();\">Materials and mechanical<br/>");
			toHtmlBuffer.append("<input type=\"checkbox\" name=\"catCodes\" value=\"ELE\" onclick=\"clearAll();\">Electronics and electrical<br/>");
			toHtmlBuffer.append("<input type=\"checkbox\" name=\"catCodes\" value=\"CHE\" onclick=\"clearAll();\">Chemical, petrochemical, and process<br/>");
		}

		toHtmlBuffer.append("</a>");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("</tr>");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("<td valign=\"top\" colspan=\"5\" bgcolor=\"#C3C8D1\" height=\"8\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" height=\"8\"/> ");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("</tr>");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("<td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/>");
		toHtmlBuffer.append("</td>");
		toHtmlBuffer.append("<td valign=\"top\" colspan=\"2\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("<a class=\"SmBlueTableText\"><b>SEARCH FOR</b></a>");
		toHtmlBuffer.append("  </td>");
		toHtmlBuffer.append(" <td valign=\"top\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("	<a class=\"SmBlueTableText\"><b>SEARCH IN</b></a> ");
		toHtmlBuffer.append("  </td>");
		toHtmlBuffer.append("</tr>");
		toHtmlBuffer.append("<tr>");
		toHtmlBuffer.append("  <td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("	<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/> ");
		toHtmlBuffer.append("  </td>");
		toHtmlBuffer.append("  <td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("	<img src=\"images/spacer.gif\" border=\"0\" width=\"15\"/> ");
		toHtmlBuffer.append("  </td>");
		toHtmlBuffer.append("  <td valign=\"top\" bgcolor=\"#C3C8D1\">");

		//Add Term1
		toHtmlBuffer.append(tfTerm1.render());

		toHtmlBuffer.append("  </td>");
		toHtmlBuffer.append("  <td valign=\"top\" bgcolor=\"#C3C8D1\" align=\"left\">");
		toHtmlBuffer.append("	<a class=\"MedBlackText\" colspan=\"2\">&nbsp; &nbsp; &nbsp;");

		//Add Field1
		toHtmlBuffer.append(lbField1.render());
		toHtmlBuffer.append("</a><img src=\"images/blue_help1.gif\" border=\"0\"/> ");

		if (!formType.equals("QUICK_SEARCH")) {

			toHtmlBuffer.append("<tr>");
			toHtmlBuffer.append("  <td valign=\"top\" colspan=\"4\" height=\"4\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" height=\"4\"/> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	</tr>");
			toHtmlBuffer.append("	<tr>");
			toHtmlBuffer.append("	  <td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("	<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/> ");
			toHtmlBuffer.append("  </td>");
			toHtmlBuffer.append("  <td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("	<img src=\"images/spacer.gif\" border=\"0\" width=\"15\"/> ");
			toHtmlBuffer.append("  </td>");
			toHtmlBuffer.append("  <td valign=\"top\" height=\"10\" bgcolor=\"#C3C8D1\" width=\"240\">");
			toHtmlBuffer.append("	<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"240\" height=\"10\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("	  <tr>");
			toHtmlBuffer.append("		<td valign=\"top\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("		  <a class=\"MedBlackText\">");

			//Add Bool1
			toHtmlBuffer.append(lbBool1.render());

			toHtmlBuffer.append("</a> ");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("			<td valign=\"top\" width=\"4\">");
			toHtmlBuffer.append("			  <img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/> ");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("			<td valign=\"top\" height=\"10\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("			  <a class=\"MedBlackText\">");

			//Add Term2
			toHtmlBuffer.append(tfTerm2.render());

			toHtmlBuffer.append("</a>");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("		  </tr>");
			toHtmlBuffer.append("		</table>");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	  <td valign=\"top\" bgcolor=\"#C3C8D1\" width=\"211\" align=\"left\">");
			toHtmlBuffer.append("		<a class=\"MedBlackText\" height=\"10\">&nbsp; &nbsp; &nbsp;");

			//Add Field2
			toHtmlBuffer.append(lbField2.render());

			toHtmlBuffer.append("</a> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	</tr>");
			toHtmlBuffer.append("	<tr>");
			toHtmlBuffer.append("	  <td valign=\"top\" height=\"4\" colspan=\"5\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" height=\"4\"/> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	</tr>");
			toHtmlBuffer.append("	<tr>");
			toHtmlBuffer.append("	  <td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	  <td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" width=\"15\"/> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	  <td valign=\"top\" height=\"10\" bgcolor=\"#C3C8D1\" width=\"240\">");
			toHtmlBuffer.append("		<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"240\" height=\"10\">");
			toHtmlBuffer.append("		  <tr>");
			toHtmlBuffer.append("			<td valign=\"top\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("			  <a class=\"MedBlackText\">");

			//Add Bool2
			toHtmlBuffer.append(lbBool2.render());

			toHtmlBuffer.append("</a> ");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("			<td valign=\"top\" width=\"4\">");
			toHtmlBuffer.append("			  <img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/>");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("			<td valign=\"top\" height=\"10\" bgcolor=\"#C3C8D1\">");
			toHtmlBuffer.append("			  <a class=\"MedBlackText\"><input type=\"text\" size=\"17\" name=\"for3\"></a> ");
			toHtmlBuffer.append("			</td>");
			toHtmlBuffer.append("		  </tr>");
			toHtmlBuffer.append("		</table>");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	  <td valign=\"top\" bgcolor=\"#C3C8D1\" width=\"211\" align=\"left\">");
			toHtmlBuffer.append("		<a class=\"MedBlackText\" height=\"10\">&nbsp; &nbsp; &nbsp;");

			//Add Field3
			toHtmlBuffer.append(lbField3);

			toHtmlBuffer.append("</a> ");
			toHtmlBuffer.append("	  </td>");
			toHtmlBuffer.append("	</tr>");

		}
		toHtmlBuffer.append("	<tr>");
		toHtmlBuffer.append("	  <td colspan=\"4\" height=\"12\">");
		toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" height=\"12\"/> ");
		toHtmlBuffer.append("	  </td>");
		toHtmlBuffer.append("	</tr>");
		toHtmlBuffer.append("	<tr>");
		toHtmlBuffer.append("	  <td valign=\"top\" width=\"4\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" width=\"4\"/> ");
		toHtmlBuffer.append("	  </td>");
		toHtmlBuffer.append("	  <td valign=\"top\" width=\"15\" bgcolor=\"#C3C8D1\">");
		toHtmlBuffer.append("		<img src=\"images/spacer.gif\" border=\"0\" width=\"15\"/> ");
		toHtmlBuffer.append("	  </td>");
		toHtmlBuffer.append("	  <td valign=\"top\" colspan=\"2\">");
		toHtmlBuffer.append("		<a CLASS=\"MedBlackText\" onClick=\"return searchValidation()\"><img src=\"images/search_orange2.gif\" border=\"0\"/></a>&nbsp; &nbsp; ");
		toHtmlBuffer.append("<a CLASS=\"MedBlackText\"><img src=\"images/reset_orange2.gif\" border=\"0\"/></a>&nbsp; &nbsp; &nbsp;");

		if (formType.equals("QUICK_SEARCH"))
			toHtmlBuffer.append("<a class=\"MedBlueLink\" href=\"/controller/servlet/Controller?CID=keyWordSearch\">Quick Search</a> ");
		else if (formType.equals("EXPERT_SEARCH"))
			toHtmlBuffer.append("<a class=\"MedBlueLink\" href=\"/controller/servlet/Controller?CID=advancedSearch\">Advanced Search</a> ");

		toHtmlBuffer.append("	  </td>");
		toHtmlBuffer.append("	</tr>");

		toHtmlBuffer.append("  </table>");
		toHtmlBuffer.append("</form>");

		return toHtmlBuffer.toString();

	}
	public static void main(String args[]) {

		ExpertSearchForm frm = new ExpertSearchForm("", "");
		System.out.println(frm.toHtml());

	}
}
