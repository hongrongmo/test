package org.ei.books.library;

import java.io.UnsupportedEncodingException;
import org.ei.books.collections.ReferexCollection;

public class CitationVisitor extends BookResultVisitor
{
  private String m_isbn;
  private String m_imgurl;

	public CitationVisitor(String isbn, String imgurl)
	{
	  m_isbn = isbn;
	  m_imgurl = imgurl;
	}

	public void visit(BookCollection bookCollection)
	{
  	for (int i = 0; i < bookCollection.getBookCount(); i++)
		{
			Book book = (Book) bookCollection.getBook(i);
			if(book.getIsbn().equals(m_isbn)) {
        String citationhtml = "<img border=\"0\" src=\"" + m_imgurl + "/images/" + book.getIsbn()  + "/" + book.getIsbn()  + "small.jpg\" width=\"56\" height=\"69\" style=\"float:left; vertical-align:middle; margin-right:10px;\"/>";
        citationhtml = citationhtml.concat("<a class=\"MedBlackText\"><b>" + book.getTitle() + "</b></a><br>");
        citationhtml = citationhtml.concat("<a class=\"SmBlueText\" style=\"text-decoration:underline;\" >" + (book.getAuthors()).replaceAll(";",";</a> <a class=\"SmBlueText\" style=\"text-decoration:underline;\" >") + "</a>");
        citationhtml = citationhtml.concat("&#160;<a class=\"SmBlackText\">ISBN: " + m_isbn + ",&#160;");
        citationhtml = citationhtml.concat("" + book.getPublisher() + ",&#160;" + book.getPubyear() + "</a><br>");
        citationhtml = citationhtml.concat("<a class=\"SmBlackText\"><b>Database:</b>&#160;Referex</a>");
        citationhtml = citationhtml.concat("&#160;<a class=\"SmBlackText\"><b>Collection:</b>&#160;" + ReferexCollection.getCollection(book.getCollection()).getDisplayName() + "</a>");
        try {
    			setResult(java.net.URLEncoder.encode(citationhtml.replaceAll("\\x27","\\\\'").replaceAll("\\s+"," ").replaceAll("[\\x0D\\x0A]"," "),"UTF-8"));
    		}
    		catch(UnsupportedEncodingException e) {
    		  setResult("");
    		}
        break;
  		}
		}
  }

}
