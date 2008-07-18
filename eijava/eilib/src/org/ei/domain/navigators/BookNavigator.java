package org.ei.domain.navigators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ei.config.ConfigService;
import org.ei.config.RuntimeProperties;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.books.collections.ReferexCollection;
import org.ei.util.Base64Coder;
import org.ei.books.collections.*;
import org.ei.util.StringUtil;

import org.ei.books.library.*;

public class BookNavigator extends EiNavigator {
  private String docview_url = null;
	public String getDocviewUrl() {
	    return docview_url;
	}

  public void setDocviewUrl() {
    /* catch null pointer when this Navigator is created from the BarChart servlet since RuntimeProperties is unavailable in the controller */
    /* It is not needed to render the chart anyway, this is just used to get the modifiers inside the navigator */
    try {
      RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
      docview_url = eiProps.getProperty("WholeBookDownloadBaseUrl");
    } catch(Exception e)
    {
    }
	}

	public BookNavigator(String navname) {
		super(navname);
    setDocviewUrl();
		if(navname.equals(EiNavigator.ST))
		{
			navname = EiNavigator.BKT;
		}
		else if(navname.equals(EiNavigator.CO))
		{
			navname = EiNavigator.BKS;
		}
		setName(navname);
		setFieldname((String) fieldNames.get(navname));
    setDisplayname((String) displayNames.get(navname));

	}

	public static EiNavigator createBookNavigator(EiNavigator anav)
	{
		EiNavigator newnav = new BookNavigator(anav.getName());

		List bookmods = new ArrayList();
		Iterator itrMods = anav.getModifiers().iterator();
		while (itrMods.hasNext()) {
			EiModifier amod = (EiModifier) itrMods.next();

			// skip any empty modifiers
			if((amod != null) && (amod.getLabel().length() != 0))
			{
				EiModifier newmod = newnav.createModifier(amod.getCount(), amod.getLabel(), amod.getValue());
				if(newmod != null)
				{
					bookmods.add(newmod);
				}
			}
		}
		newnav.setModifiers(bookmods);
		return newnav;
	}

	protected static Pattern patTitleIsbn = Pattern.compile("(.*), (\\d+\\w+)$");
	protected static Pattern patIsbn = Pattern.compile("(\\d+[a-zA-Z]*)$");

	public EiModifier createModifier(int mcount, String slabel, String svalue)
    {
    	String isbn = null;
    	String title = null;
    	String modtype = BookModifier.TYPE_BOOK;

		if(slabel.startsWith("B64")) {
			slabel = Base64Coder.decode(slabel.substring(3));
			svalue = Base64Coder.decode(svalue.substring(3));
		}
		// matches() requires EXACT match!
		Matcher m = patTitleIsbn.matcher(svalue);
		if(m.matches())
		{
	    	modtype = BookModifier.TYPE_SECT;
    		// This is "section, ISBN" (BKS) navigator
    		slabel = m.group(1);
    		isbn = m.group(2);

    		// ack! BUg work around - empty section titles - do not create mod - return null
    		if((slabel == null ) || (slabel.length() == 0))
    		{
    			return null;
    		}
    		// clean up leading numbers and punctuaion at front
    		// of chapter navigator label
    		slabel = slabel.replaceFirst("^\\d+([\\.:,])?", "");
    		//title = ("<img border=\"0\" src=\"" + getDocviewUrl() + "/images/" + isbn + "/" + isbn + "small.jpg\" width=\"56\" height=\"69\" style=\"float:left; vertical-align:middle; margin-right:10px;\"/>");
    		//title = title.concat(BookNavigator.getBookCitation(isbn));
    		title = getBookCitation(isbn);
    		// fix section title label by uppercasing the first char in each 'word'
    		slabel = (new Perl5Util()).substitute("s/(\\w+)/\\u$1/g",slabel);
		}
		else
		{
			  // This  is an "ISBN" (BKT) navigator
    		isbn = getBookIsbn(svalue.toLowerCase());

        if((isbn != null) && !isbn.equals(StringUtil.EMPTY_STRING))
        {
          // get the properly formatted title from the isbn
          slabel = getBookTitle(isbn);

          // use the properly fomatted title for searching
          svalue = slabel;

          title = getBookCitation(isbn);
        }
    }

        return new BookModifier(mcount, slabel, svalue, title, modtype);
    }

	private static void removeCollCodeModifer(EiNavigator anav, ReferexCollection col)
	{
		// Each Collection that is present in results will have modifiers
		// for each sub collection, and a modifier using
		// its shortname, i.e ELE, ELE1, ELE2, electrical
		// Remove these for display purposes
	    anav.getModifiers().remove(col.getModifier());
	    anav.getModifiers().remove(new EiModifier(0,col.getAbbrev(),col.getAbbrev()));
	    anav.getModifiers().remove(new EiModifier(0,col.getAbbrev().concat("1"),col.getAbbrev().concat("1")));
	    anav.getModifiers().remove(new EiModifier(0,col.getAbbrev().concat("2"),col.getAbbrev().concat("2")));
	    anav.getModifiers().remove(new EiModifier(0,col.getAbbrev().concat("3"),col.getAbbrev().concat("3")));
	}

	public static EiNavigator cleanCLNavigator(EiNavigator anav)
	{
    		ReferexCollection[] allcolls = ReferexCollection.allcolls;
      	Map colMods = new LinkedHashMap();
        Iterator itrMods = anav.getModifiers().iterator();
        while(itrMods.hasNext())
        {
        	EiModifier amod = (EiModifier) itrMods.next();
        	for(int i = 0; i < allcolls.length; i++) {
            if(amod.equals(allcolls[i].getModifier()))
          	{
          		colMods.put(allcolls[i], amod);
          		break;
          	}
          }
        }
        itrMods = null;
        itrMods = colMods.keySet().iterator();
        while(itrMods.hasNext())
        {
        	ReferexCollection col = (ReferexCollection) itrMods.next();
        	EiModifier amod = (EiModifier) colMods.get(col);

        	removeCollCodeModifer(anav, col);
        	anav.getModifiers().add(new EiModifier(amod.getCount(),col.getDisplayName(),amod.getValue()));
        }

        return anav;
	}

    public String getBookCitation(String isbn)
    {
        String citationhtml  = "";
        try
        {
          Library library = Library.getInstance();
          Book book = library.getBook(isbn);
          String imgurl = getDocviewUrl();
          citationhtml = "<img border=\"0\" src=\"" + imgurl + "/images/" + book.getIsbn()  + "/" + book.getIsbn()  + "small.jpg\" width=\"56\" height=\"69\" style=\"float:left; vertical-align:middle; margin-right:10px;\"/>";
          citationhtml = citationhtml.concat("<a class=\"MedBlackText\"><b>" + book.getTitle() + "</b></a><br>");
          citationhtml = citationhtml.concat("<a class=\"SmBlueText\" style=\"text-decoration:underline;\" >" + (book.getAuthors()).replaceAll(";",";</a> <a class=\"SmBlueText\" style=\"text-decoration:underline;\" >") + "</a>");
          citationhtml = citationhtml.concat("&#160;<a class=\"SmBlackText\">ISBN: " + isbn + ",&#160;");
          citationhtml = citationhtml.concat("" + book.getPublisher() + ",&#160;" + book.getPubyear() + "</a><br>");
          citationhtml = citationhtml.concat("<a class=\"SmBlackText\"><b>Database:</b>&#160;Referex</a>");
          citationhtml = citationhtml.concat("&#160;<a class=\"SmBlackText\"><b>Collection:</b>&#160;" + ReferexCollection.getCollection(book.getCollection()).getDisplayName().replaceAll("&", "&amp;") + "</a>");
          citationhtml = java.net.URLEncoder.encode(citationhtml.replaceAll("\\x27","\\\\'").replaceAll("\\s+"," ").replaceAll("[\\x0D\\x0A]"," "),"UTF-8");

          if(citationhtml == null)
          {
            citationhtml = "<H1>ACK</H1>";
          }
        }
        catch(Exception e) {

        }
        return citationhtml;
    }

    public String getBookIsbn(String title)
    {
        String isbn  = "";
        try
        {
            Library library = Library.getInstance();
            Book book = library.getBook(title);
            isbn = book.getIsbn();

          if(isbn == null)
          {
            isbn = "Cant find isbn for title =" + title;
          }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
        return isbn;
    }

    public String getBookTitle(String isbn)
    {
        String title = "";
        try
        {
          Library library = Library.getInstance();
          Book book = library.getBook(isbn);
          title = book.getTitle();

          if(title == null)
          {
            title = "missing title :" + isbn;
          }
        }
        catch(Exception e) {
          e.printStackTrace();
        }
        return title;
    }
}
