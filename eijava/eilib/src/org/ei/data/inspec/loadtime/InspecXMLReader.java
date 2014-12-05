package org.ei.data.inspec.loadtime;

import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

public class InspecXMLReader extends FilterReader
{
	private Hashtable record = null;
	private List articles = null;
	private Document doc = null;
	private Iterator rec =null;
	private Perl5Util perl = new Perl5Util();
	private boolean inabstract = false;
	private HashSet entity = null;
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {29});
	/*
	 * If there is field delimiter that is 2 or more values for one field eg, A;B;C,
	 * use 'AUDELIMITER' between A, B and C
	 * If there is subfields in one value of the field use 'IDDELIMITER' ,
	 * for eg. A:1;B:2;C:3 USE AUDELIMITER between A, B and C
	 * To separate fields use 'IDDELIMITER between A and 1, B and 2 and C and 3.
	 */
    public static final String PRIMARY1 = "1";
    public static final String PRIMARY2 = "2";
    public static final String CURRENT_DATA = "current";
    public static final String BACKFILE_DATA = "backfill";
    public static final String ARTICLETYPE = "articletype";


    public static void main(String[] args)
        throws Exception
    {
    	String filename = args[0];
    	Hashtable rec;

        BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
        InspecXMLReader r = new InspecXMLReader(in);
        while((rec=r.getRecord())!=null)
        {
        	System.out.println(rec.toString());
		}

	}

	public InspecXMLReader(Reader r)throws Exception
	{
			super(r);
			SAXBuilder builder = new SAXBuilder();
			builder.setExpandEntities(false);
			this.doc = builder.build(this);
			Element inspecRoot = doc.getRootElement();
			this.articles = inspecRoot.getChildren("article");
			this.rec=articles.iterator();

	}

	public int getRecordCount()
	{
		return articles.size();
	}
	public void close()
	{
		System.out.println("Closed");
	}

	public Hashtable getRecord()
	{
		entity = new HashSet();
		if(rec.hasNext())
		{
			Element article = (Element)rec.next();
			record = new Hashtable();

			//06/07 TS Inspec updated data file,
			//for current data there is an article type="current"
			//for backfile data there is an article type="backfile"

			//if type="current" or type is not provided
			record.put(ARTICLETYPE, CURRENT_DATA);

			/* removed based on frank request on 6/4/2014
			//if type="backfile" - record extract is not processed
			if (article.getAttribute("type") != null &&
					article.getAttribute("type").getValue().equals(BACKFILE_DATA))
			{
				System.out.println("article type is backfile, it is not processed for current Inspec data extract");
				record.put(ARTICLETYPE, BACKFILE_DATA);
				return record;
			}
			*/

			//06/07 - the end

			/*
				Control Group Elements

			*/

			Element controlGroup = article.getChild("contg");
			//AN Number
			record.put("ANUM",new StringBuffer(controlGroup.getChild("accn").getTextTrim()));

			//**VB:new Year and Issue No.
			if(controlGroup.getChild("tapiss")!=null)
				getSU(controlGroup.getChild("tapiss"));

			// Copyright name
			if(controlGroup.getChild("crt")!=null)
				record.put("CPR",getCRT(controlGroup.getChild("crt")));

			// Material Identity No.
			if(controlGroup.getChild("ming")!=null)
			   record.put("MATID",new StringBuffer(controlGroup.getChild("ming").getChild("min2").getTextTrim()));

			//VB:abstract number
			if(controlGroup.getChild("abng")!=null)
				record.put("ABNUM",getFields(controlGroup.getChild("abng")));

			//Record type
			if(controlGroup.getChild("rtypg").getChild("rtyp")!=null)
				record.put("RTYPE",new StringBuffer(controlGroup.getChild("rtypg").getChild("rtyp").getTextTrim()));

			//VB:new Record type
			if(controlGroup.getChild("rtypg").getChild("newrt")!=null)
				record.put("NRTYPE",new StringBuffer(controlGroup.getChild("rtypg").getChild("newrt").getTextTrim()));

			//Amendment date
			if(controlGroup.getChild("amndt")!=null)
				getPubDate(controlGroup.getChild("amndt"),"");
			/*

				Biblio Group Elements

			*/
			Element bibGroup = article.getChild("bibliog");

			// Title
			record.put("TI",getMixData(bibGroup.getChild("ti").getContent(),new StringBuffer()));

			// Authors
			if(bibGroup.getChild("aug")!=null) {
				record.put("AUS",getName(bibGroup.getChild("aug")));
				record.put("AAFF",getAffiliation(bibGroup.getChild("aug"),"A"));
			}

			//Journal
			if(bibGroup.getChild("jrefg")!=null)
			{
				if(bibGroup.getChild("jrefg").getChild("jrog")!=null)
				{
					record.put("PFLAG",new StringBuffer(PRIMARY1));
					getJournalData(bibGroup.getChild("jrefg").getChild("jrog"));
				}
				if(bibGroup.getChild("jrefg").getChild("jrtg")!=null)
				{
					record.put("PFLAG",new StringBuffer(PRIMARY2));
					getJournalData(bibGroup.getChild("jrefg").getChild("jrtg"));
				}
				if(bibGroup.getChild("jrefg").getChild("troag")!=null)
				{
					getJournalData(bibGroup.getChild("jrefg").getChild("troag"));
				}
				if(bibGroup.getChild("jrefg").getChild("trtag")!=null)
				{
					getJournalData(bibGroup.getChild("jrefg").getChild("trtag"));
				}

			}
			//Book Group
			if(bibGroup.getChild("bookg")!=null)
				getBookData(bibGroup.getChild("bookg"));

			//Report Group
			if(bibGroup.getChild("rptg")!=null)
				getBookData(bibGroup.getChild("rptg"));

			//Report Group
			if(bibGroup.getChild("stdg")!=null)
				getBookData(bibGroup.getChild("stdg"));


			// Dissertation Group
			if(bibGroup.getChild("dssg")!=null)
				getBookData(bibGroup.getChild("dssg"));

			//Patent Group
			if(bibGroup.getChild("patg")!=null)
				getPatentData(bibGroup.getChild("patg"));

			//Conference Group
			if(bibGroup.getChild("cng")!=null)
				getConferenceData(bibGroup.getChild("cng"));

			//Language
			if(bibGroup.getChild("lng")!=null)
				record.put("LA",getFields(bibGroup.getChild("lng")));

			//Abstract Group
			if(bibGroup.getChild("abs")!=null)
				record.put("AB",getMixCData(bibGroup.getChild("abs").getContent(), new StringBuffer()));

			//No. of references
			if(bibGroup.getChild("norefs")!=null)
		  		record.put("XREFNO",getMixData(bibGroup.getChild("norefs").getContent(),new StringBuffer()));

			//Amendment Group
			if(bibGroup.getChild("amndg")!=null)
				getAmendmentData(bibGroup.getChild("amndg"));

			/*
				Indexing Group
			*/

			Element idxGroup = article.getChild("indexg");
			if(idxGroup.getChild("cindg")!=null)
				record.put("CVS",getIndexing(idxGroup.getChild("cindg"),"term"));
			if(idxGroup.getChild("ccg")!=null)
				record.put("CLS",getIndexing(idxGroup.getChild("ccg"),"cc"));


			if(idxGroup.getChild("ipcg")!=null)
			{
				record.put("IPC",getIndexing(idxGroup.getChild("ipcg"),"cc"));
			}
			if(idxGroup.getChild("ucindg")!=null)
				record.put("FLS",getIndexing(idxGroup.getChild("ucindg"),"term"));
			//VB:tcg
			if(idxGroup.getChild("tcg")!=null)
				record.put("TRMC",getIndexing(idxGroup.getChild("tcg"),"tc"));
			if(idxGroup.getChild("ndig")!=null)
				getNumIndexingData(idxGroup.getChild("ndig"));
			if(idxGroup.getChild("cig")!=null)
				getChemIndexingData(idxGroup.getChild("cig"));
			if(idxGroup.getChild("aoig")!=null)
				record.put("AOI",getFields(idxGroup.getChild("aoig")));
			//System.out.println("Record:"+record.toString());

			// sortdate
			Element sortDate = controlGroup.getChild("sortdate");
			if (sortDate != null)
			{
			    StringBuffer sDate = new StringBuffer();

			    if(sortDate.getChild("day")!=null)
			    {
			        sDate.append(sortDate.getChild("day").getTextTrim());
			    }
			    sDate.append(AUDELIMITER);

			    if(sortDate.getChild("mo")!=null)
			    {
			        sDate.append(sortDate.getChild("mo").getTextTrim());
			    }
			    sDate.append(AUDELIMITER);

			    if(sortDate.getChild("yr")!=null)
			    {
			        sDate.append(sortDate.getChild("yr").getTextTrim());
			    }

			    record.put("SRTDATE",sDate);

			}

			/*
				Citation Group
				new May-3-2011
			*/

			Element citGroup = article.getChild("citeg");
			if(citGroup!= null)
			{
				StringBuffer citation=getCitationGroup(citGroup);
				record.put("CIT",citation);
			}

			return record;
		}
		return null;
    }

    private StringBuffer getCitationGroup(Element citGroup)
    {
		StringBuffer citS = new StringBuffer();
		try
		{
			if(citGroup != null)
			{
				List cit = citGroup.getChildren("cite");
				if(cit != null)
				{
					Iterator it = cit.iterator();

					 while(it.hasNext())
					{
						Element cite = (Element)it.next();

						//Accessnumber
						citS.append("ACCESSION_NUMBER::");
						if(cite.getChild("brfinacc")!=null)
						{
							citS.append(cite.getChild("brfinacc").getTextTrim());
						}

						citS.append(IDDELIMITER);

						//DOI
						citS.append("DOI::");
						if(cite.getChild("brfdoi")!=null)
						{
							citS.append(cite.getChild("brfdoi").getTextTrim());
						}

						citS.append(IDDELIMITER);

						//Citation Type
						citS.append("CITATION_TYPE::");
						if(cite.getAttributeValue("type")!=null)
						{
							citS.append(cite.getAttributeValue("type"));
						}
						citS.append(IDDELIMITER);

						// Citation Label
						citS.append("CITATION_LABEL::");
						if(cite.getChild("brflab")!=null)
						{
							citS.append(cite.getChild("brflab").getTextTrim());
						}

						citS.append(IDDELIMITER);

						// Citation Author
						citS.append("AUTHOR::");
						Element authorGroup = cite.getChild("brfaug");
						if(authorGroup!=null)
						{

							List nameList = authorGroup.getChildren("pname");
							citS.append(getPname(nameList));
						}

						citS.append(IDDELIMITER);

						// Citation Year
						citS.append("YEAR::");
						if(cite.getChild("brfyr")!=null)
						{
							citS.append(cite.getChild("brfyr").getTextTrim());
						}

						citS.append(IDDELIMITER);

						// Journal title
						citS.append("JOURNAL_TITLE::");
						if(cite.getChild("brfjrti")!=null)
						{
							citS.append(cite.getChild("brfjrti").getTextTrim());
						}

						citS.append(IDDELIMITER);


						// Abbreviated journal title
						citS.append("ABBR_TITLE::");
						if(cite.getChild("brfajt")!=null)
						{
							citS.append(cite.getChild("brfajt").getTextTrim());
						}

						citS.append(IDDELIMITER);


						// ISSN
						citS.append("ISSN::");
						if(cite.getChild("brfissn")!=null)
						{
							citS.append(cite.getChild("brfissn").getTextTrim());
						}

						citS.append(IDDELIMITER);


						// Volume and issue data group
						citS.append("VOLUME_ISSUE::");
						if(cite.getChild("brfvid")!=null)
						{
							Element viGroup=cite.getChild("brfvid");
							if(viGroup.getChild("voliss")!=null)
							{
								citS.append(viGroup.getChild("voliss").getTextTrim());
							}
							citS.append(GROUPDELIMITER);
							if(viGroup.getChild("vol")!=null)
							{
								citS.append(viGroup.getChild("vol").getTextTrim());
							}
							citS.append(GROUPDELIMITER);
							if(viGroup.getChild("ino")!=null)
							{
								citS.append(viGroup.getChild("ino").getTextTrim());
							}
						}

						citS.append(IDDELIMITER);

						// Publication date
						citS.append("PUBLICATION_DATE::");
						if(cite.getChild("brfpdt")!=null)
						{
							Element pdGroup=cite.getChild("brfpdt");
							if(pdGroup.getChild("sdate")!=null)
							{
								Element sdate = pdGroup.getChild("sdate");
								if(sdate.getChild("mo")!=null)
								{
									citS.append(sdate.getChild("mo").getTextTrim());
									citS.append("-");
								}

								if(sdate.getChild("day")!=null)
								{
									citS.append(sdate.getChild("day").getTextTrim());
									citS.append("-");
								}

							}
							citS.append(GROUPDELIMITER);
							if(pdGroup.getChild("edate")!=null)
							{
								Element sdate = pdGroup.getChild("edate");
								if(sdate.getChild("mo")!=null)
								{
									citS.append(sdate.getChild("mo").getTextTrim());
									citS.append("-");
								}

								if(sdate.getChild("day")!=null)
								{
									citS.append(sdate.getChild("day").getTextTrim());
									citS.append("-");
								}

							}
							citS.append(GROUPDELIMITER);
							if(pdGroup.getChild("odate")!=null)
							{
								citS.append(pdGroup.getChild("odate").getTextTrim());
							}
						}

						citS.append(IDDELIMITER);


						// First Page
						citS.append("FIRST_PAGE::");
						if(cite.getChild("brffp")!=null)
						{
							citS.append(cite.getChild("brffp").getTextTrim());
						}

						citS.append(IDDELIMITER);


						// Page
						citS.append("PAGE::");
						if(cite.getChild("brfpp")!=null)
						{
							citS.append(cite.getChild("brfpp").getTextTrim());
						}

						citS.append(IDDELIMITER);



						// Raw text
						citS.append("RAW::");
						if(cite.getChild("brfraw")!=null)
						{
							String rawString = cite.getChild("brfraw").getTextTrim();
							if(rawString.indexOf("\n")>-1)
							{
								rawString = rawString.replaceAll("\n"," ");
							}
							citS.append(rawString);
						}

						citS.append(IDDELIMITER);


						// Collaboration
						citS.append("COLLABORATION::");
						if(cite.getChild("collab")!=null)
						{
							citS.append(cite.getChild("collab").getTextTrim());
						}

						citS.append(IDDELIMITER);

						// Et al
						citS.append("ETAL::");
						if(cite.getChild("etal")!=null)
						{
							citS.append(cite.getChild("etal").getTextTrim());
						}

						citS.append(IDDELIMITER);


						//Title
						citS.append("TITLE::");
						if(cite.getChild("brfti")!=null)
						{
							citS.append(cite.getChild("brfti").getTextTrim());
						}

						citS.append(IDDELIMITER);



						//Publication  Title
						citS.append("PUBLCATION_TITLE::");
						if(cite.getChild("brfpubti")!=null)
						{
							citS.append(cite.getChild("brfpubti").getTextTrim());
						}

						citS.append(IDDELIMITER);


						//Abbreviated journal title
						citS.append("ABBR_JOURNAL_TITLE::");
						if(cite.getChild("brfajt")!=null)
						{
							citS.append(cite.getChild("brfajt").getTextTrim());
						}

						citS.append(IDDELIMITER);


						//Publisher
						citS.append("PUBLISHER::");
						if(cite.getChild("brfpnm")!=null)
						{
							citS.append(cite.getChild("brfpnm").getTextTrim());
						}

						citS.append(IDDELIMITER);



						//Series title
						citS.append("SERIES_TITLE::");
						if(cite.getChild("brfser")!=null)
						{
							citS.append(cite.getChild("brfser").getTextTrim());
						}

						citS.append(IDDELIMITER);


						//Editor group

						citS.append("EDITOR::");
						if(cite.getChild("brfedg")!=null)
						{
							Element editorGroup = cite.getChild("brfedg");
							List editorList = null;
							if(editorGroup.getChildren("pname")!=null)
							{
								editorList = editorGroup.getChildren("pname");
								citS.append(getPname(editorList));
							}
							else if(editorGroup.getChildren("collab")!=null)
							{
								editorList = editorGroup.getChildren("collab");
								citS.append(getCollab(editorList));
							}
							else if(editorGroup.getChild("etal")!=null)
							{
								citS.append(cite.getChild("etal").getTextTrim());
							}

						}

						citS.append(IDDELIMITER);


						//Issuing Organisation
						citS.append("ISSUE_ORGANIZATION::");
						if(cite.getChild("brfisorg")!=null)
						{
							Element issuingOrg = cite.getChild("brfisorg");
							if(issuingOrg.getChild("orgn")!=null)
							{
								citS.append(issuingOrg.getChild("orgn").getTextTrim());
							}
							else if(issuingOrg.getChild("cntry")!=null)
							{
								citS.append(issuingOrg.getChild("cntry").getTextTrim());
							}
						}

						citS.append(IDDELIMITER);


						//Place of publication
						citS.append("PUBLICATION_PLACE::");
						if(cite.getChild("brfloc")!=null)
						{
							citS.append(cite.getChild("brfloc").getTextTrim());
						}

						citS.append(IDDELIMITER);

						//Country
						citS.append("COUNTRY::");
						if(cite.getChild("brfcny")!=null)
						{
							citS.append(cite.getChild("brfcny").getTextTrim());
						}
						citS.append(IDDELIMITER);

						//ISBN
						citS.append("ISBN::");
						if(cite.getChild("brfisbn")!=null)
						{
							citS.append(cite.getChild("brfisbn").getTextTrim());
						}
						citS.append(IDDELIMITER);


						//Conference group
						citS.append("CONFERENCE::");
						if(cite.getChild("brfcng")!=null)
						{
							citS.append(cite.getChild("brfcng").getTextTrim());
						}
						citS.append(IDDELIMITER);


						//Report number
						citS.append("REPORT_NUMBER::");
						if(cite.getChild("brfrepno")!=null)
						{
							citS.append(cite.getChild("brfrepno").getTextTrim());
						}
						citS.append(IDDELIMITER);


						//Standard number
						citS.append("STANDARD_NUMBER::");
						if(cite.getChild("brfstdno")!=null)
						{
							citS.append(cite.getChild("brfstdno").getTextTrim());
						}
						citS.append(IDDELIMITER);

						//Patent details group
						citS.append("PATENT_DETAIL::");
						if(cite.getChild("brfstdno")!=null)
						{
							citS.append(cite.getChild("brfstdno").getTextTrim());
						}
						citS.append(IDDELIMITER);

						//Patent data
						citS.append("PATENT_DATA::");
						if(cite.getChild("brfpat")!=null)
						{
							citS.append(cite.getChild("brfpat").getTextTrim());
						}
						citS.append(IDDELIMITER);

						// Date
						citS.append("DATE::");
						if(cite.getChild("brfdate")!=null)
						{
							citS.append(cite.getChild("brfdate").getTextTrim());
						}
						citS.append(IDDELIMITER);

						// LINK
						citS.append("LINK::");
						if(cite.getChild("brflink")!=null)
						{
							Element link=cite.getChild("brflink");

							if(link.getAttribute("type")!=null)
							{
								citS.append(link.getAttributeValue("type"));
							}
							citS.append(GROUPDELIMITER);
							citS.append(cite.getChild("brflink").getTextTrim());
						}
						citS.append(IDDELIMITER);

						// NOTES
						citS.append("NOTES::");
						if(cite.getChild("brfnotes")!=null)
						{
							citS.append(cite.getChild("brfnotes").getTextTrim());
						}

						citS.append(AUDELIMITER);

					}
					if(citS.length()>0)
					{
						citS.deleteCharAt(citS.length()-1);
					}
				}


			}
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}

		return citS;
	}

	private String getCollab(List collabList)
	{
		StringBuffer citS = new StringBuffer();
		if(collabList!=null)
		{
			Iterator collabIt = collabList.iterator();
			while(collabIt.hasNext())
			{
				Element collabEl = (Element)collabIt.next();
				citS.append(collabEl.getTextTrim());
				citS.append(GROUPDELIMITER);
			}
		}
		return citS.toString();
	}

	private String getPname(List nameList)
	{
		StringBuffer citS = new StringBuffer();
		if(nameList!=null)
		{
			Iterator nameIt = nameList.iterator();
			while(nameIt.hasNext())
			{
				Element nameEl = (Element)nameIt.next();
				if(nameEl.getAttribute("id")!=null)
				{
				  citS.append(nameEl.getAttribute("id").getValue());
				}
				citS.append("|");

				if(nameEl.getChild("snm")!=null)
				{
					citS.append(getMixData(nameEl.getChild("snm").getContent(),new StringBuffer()));
				}

				citS.append("|");
				if(nameEl.getChild("init")!=null)
				{
					citS.append(nameEl.getChild("init").getTextTrim());
				}
				citS.append("|");

			   if(nameEl.getChild("sfix")!=null)
			   {
				  citS.append(nameEl.getChild("sfix").getTextTrim());
			   }
			   citS.append("|");

			   if(nameEl.getChildren("fnm") != null)
			   {
				   List l = nameEl.getChildren("fnm");
				   for (int k = 0; k < l.size(); k++)
				   {
					   Element el = (Element) l.get(k);
					   citS.append(getMixData(el.getContent(),new StringBuffer()));
					   if(k < (l.size()-1))
					   {
						   citS.append(", ");
					   }
				   }
			   }
			   citS.append("|");
			   if(nameEl.getChild("email")!=null)
			   {
				   citS.append(nameEl.getChild("email").getTextTrim());
			   }
			   citS.append("|");
			   if(nameEl.getChild("pid") != null)
			   {
				   citS.append(nameEl.getChild("pid").getTextTrim());
			   }
			   citS.append("|");
			   citS.append(GROUPDELIMITER);
			}
		}
		return citS.toString();

	}

    private  StringBuffer getMixData(List l, StringBuffer b)
    {
        Iterator it = l.iterator();

        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				String text=((Text)o).getText();
				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);

				b.append(text);

            }
             else if(o instanceof EntityRef)
             {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }

        return b;
    }

    private  StringBuffer getMixCData(List l, StringBuffer b)
	{
		inabstract=true;
		b=getMixData(l,b);
		inabstract=false;
		return b;
	}

   	private StringBuffer getName(Element e)
    {
   		//pname: snm , init , sfix :IDDELIMITER: id:IDDELIMITER:
   		//  fnm (, multi):IDDELIMITER: email :IDDELIMITER: ,email :IDDELIMITER: ,pid :AUDELIMITER

   		StringBuffer name = new StringBuffer();
		List pname = e.getChildren("pname");

		for(int i=0;i<pname.size();i++)
		{

		   Element n = (Element)pname.get(i);

		   getMixData(n.getChild("snm").getContent(),name);

		   if(n.getChild("init")!=null)
		   {
			   name.append(", ");
			   getMixData(n.getChild("init").getContent(),name);
		   }
		   if(n.getChild("sfix")!=null)
		   {
			   name.append(", ");
			   getMixData(n.getChild("sfix").getContent(),name);
		   }
		   name.append(IDDELIMITER);
		   if(n.getAttribute("id")!=null)
		   {
			  name.append(n.getAttribute("id").getValue());
		   }
		   name.append(IDDELIMITER);
		   if(n.getChildren("fnm") != null)
		   {
			   List l = n.getChildren("fnm");
			   for (int k = 0; k < l.size(); k++)
			   {
				   Element el = (Element) l.get(k);
				   name.append(el.getTextTrim());
				   if(k < (l.size()-1))
				   {
					   name.append(", ");
				   }
			   }
		   }
		   name.append(IDDELIMITER);
		   if(n.getChild("email")!=null)
		   {
			   name.append(n.getChild("email").getTextTrim());
		   }
		   name.append(IDDELIMITER);
		   if(n.getChild("pid") != null)
		   {
			   name.append(n.getChild("pid").getTextTrim());
		   }
		   name.append(AUDELIMITER);
	    }

        if(name.lastIndexOf(AUDELIMITER) != -1) {
        	return name.delete(name.lastIndexOf(AUDELIMITER),name.length());
        }
        else {
        	return name;
        }


	}


	private StringBuffer getAffiliation(Element e, String keyprfx)
    {

		// field AAFF or EAFF depend on keyprfx
		StringBuffer aff = new StringBuffer();
		// 06/07 new db field for multi affiliations and field overflow
		// AAFFMULTI1, AAFFMULTI2 ,EAFFMULTI1, EAFFMULTI2
		StringBuffer affmulti1 = new StringBuffer();
		StringBuffer affmulti2 = null;
		StringBuffer affmulti2_max = null;

		List auaff = e.getChildren("faff");


	    for( int j=0 ; j < auaff.size() ; j++ )
		{
			// H Limit to 8000 Length, so no record rejected for aaffmulti2
			if(affmulti1.length()>0 && affmulti1.length()>8000)
			{
			  break;
			}

	        Element m = (Element)auaff.get(j);
	        StringBuffer oneAffiliation = new StringBuffer();
	        StringBuffer country = new StringBuffer();
	        getMixData(m.getChild("aff").getContent(),oneAffiliation);
	        oneAffiliation.append(IDDELIMITER);

        	//first author affiliation, independent from au-affiliation:
	        // check if it is empty then add the value

	        if(aff.length() < 1)
        	{
        		getMixData(m.getChild("aff").getContent(),aff);
        		aff.append(IDDELIMITER);
    	        if(m.getAttribute("rid")!=null)
    	        {
    	        	aff.append(m.getAttribute("rid").getValue());
    	        }
        	}
	        // end of first author affiliation

	        if(m.getAttribute("rid")!=null)
	        {
	        	oneAffiliation.append(m.getAttribute("rid").getValue());
	        }
	        oneAffiliation.append(IDDELIMITER);
	        if(m.getChild("cntry")!=null)
	        {
	        	getMixData(m.getChild("cntry").getContent(),country);

	        	if(j == 0)
	        	{
	        		record.put(keyprfx+"FC",country);

	        	}
	        	//add country to AAFFMULTI, EAFFMULTI fields

	        	oneAffiliation.append(country);
		    }

	        // add new xml elements to aff.
	        //orgn
	        oneAffiliation.append(IDDELIMITER);

	        if(m.getChild("orgn")!= null)
	        {
	        	oneAffiliation.append(m.getChild("orgn").getTextTrim());
	        }
	        //dept
	        oneAffiliation.append(IDDELIMITER);
	        if(m.getChild("dept") != null)
	        {
	        	oneAffiliation.append(m.getChild("dept").getTextTrim());
	        }
	        //addressline - could be multy
	        oneAffiliation.append(IDDELIMITER);

	        if(m.getChildren("addline") != null)
	        {
	        	List l = m.getChildren("addline");
	        	StringBuffer addline = new StringBuffer();
	        	for (int i = 0 ; i < l.size(); i++)
	        	{
	        		Element el = (Element) l.get(i);
	        		addline.append(el.getTextTrim());
	        		if(i < (l.size()-1))
	        		{
	        			addline.append(", ");
	        		}

	        	}
	        	oneAffiliation.append(addline);
	        }

	        oneAffiliation.append(IDDELIMITER);
	        //city
	        if(m.getChild("city")!= null)
	        {
	        	oneAffiliation.append(m.getChild("city").getTextTrim());

	        }

	        oneAffiliation.append(IDDELIMITER);
	        //state
	        if(m.getChild("state") != null)
	        {
	        	oneAffiliation.append(m.getChild("state").getTextTrim());
	        }

	        oneAffiliation.append(IDDELIMITER);
	        //pcode
	        if(m.getChild("pcode") != null)
	        {
	        	oneAffiliation.append(m.getChild("pcode").getTextTrim());
	        }

	        oneAffiliation.append(IDDELIMITER);
	        //orgid

	        if(m.getChild("orgid") != null)
	        {
	        	oneAffiliation.append(m.getChild("orgid").getTextTrim());
	        }

	        if(affmulti1.length() > 0 )
	        {
	        	affmulti1.append(AUDELIMITER);
	        }
	        affmulti1.append(oneAffiliation);

		}
	    // db field overflow logic
		if (affmulti1.length() > 0 && affmulti1.length() > 3990)
		{
			//affmulti2 = new StringBuffer(affmulti1.substring(3900));    origin
			//affmulti1.delete(3900, affmulti1.length());   //origin
			//record.put(keyprfx+"AFFMULTI2",affmulti2);   //origin
			affmulti2 = new StringBuffer(affmulti1.substring(3990));
			affmulti1.delete(3990, affmulti1.length());

			if (affmulti2.length() > 0 && affmulti2.length() > 3990)
			{
				 affmulti2.delete(3991, affmulti2.length());
				 affmulti2_max = new StringBuffer(affmulti2.substring(0, affmulti2.lastIndexOf(AUDELIMITER)));

				 record.put(keyprfx+"AFFMULTI2",affmulti2_max);
			}
			else
			{
				 record.put(keyprfx+"AFFMULTI2",affmulti2);
			}
		}

	    if (affmulti1.length() > 0)
	    {
	    	record.put(keyprfx+"AFFMULTI1",affmulti1);
	    }

		return aff;

   	}

	private StringBuffer getCRT(Element e)
	{
		StringBuffer crt = new StringBuffer();
		crt.append("Copyright ");
		crt.append(e.getChildTextTrim("yr"));
		List crn = e.getChildren("crn");

		for(int i=0;i<crn.size();i++)
		{
			 Element n = (Element)crn.get(i);

				crt.append(", " );
				crt.append(n.getTextTrim());
		}

		return crt;
	}


   private StringBuffer getDate(Element e)
   {
	   	StringBuffer date = new StringBuffer();

		if(e.getChild("day")!=null)
		{
			date.append(e.getChildTextTrim("day"));
			date.append(" ");
		}
		if(e.getChild("mo")!=null)
		{
			date.append(e.getChildTextTrim("mo"));
			date.append(" ");
		}

		date.append(e.getChildTextTrim("yr"));

		return date;
	}

	private void getJournalData(Element e)
	{
		String keyprfx="";
		if((e.getName().equals("jrog")) || (e.getName().equals("jrtg")))
		{
			keyprfx="P";
		}
	    if((e.getName().equals("troag")) || (e.getName().equals("trtag")))
		{
			keyprfx="S";
		}

		//Full Journal Id
		if(e.getChild("jin")!=null)
			record.put(keyprfx+"JID",getMixData(e.getChild("jin").getContent(),new StringBuffer()));

		//Full Journal Title
		if(e.getChild("jt")!=null)
			record.put(keyprfx+"FJT",getMixData(e.getChild("jt").getContent(),new StringBuffer()));

		//Modern Abbreviated. Title
		if(e.getChild("ajt")!=null) {
			record.put(keyprfx+"AJT",getMixData(e.getChild("ajt").getContent(),new StringBuffer()));
		}
		// VID
		if(e.getChild("vid")!=null)
		{
			if(e.getChild("vid").getChild("vol")!=null)
				record.put(keyprfx+"VOL",getMixData(e.getChild("vid").getChild("vol").getContent(),new StringBuffer()));
			if(e.getChild("vid").getChild("ino")!=null)
				record.put(keyprfx+"ISS",new StringBuffer(e.getChild("vid").getChildTextTrim("ino")));
			if(e.getChild("vid").getChild("iss")!=null)
				record.put(keyprfx+"ISS",new StringBuffer(e.getChild("vid").getChildTextTrim("iss")));
			if(e.getChild("vid").getChild("voliss")!=null)
				record.put(keyprfx+"VOLISS",getMixData(e.getChild("vid").getChild("voliss").getContent(),new StringBuffer()));
		}

		//Page
		if(e.getChild("pgn")!=null)
			record.put(keyprfx+"IPN",getMixData(e.getChild("pgn").getContent(),new StringBuffer()));

		//Publication Date
		if(e.getChild("pdt")!=null)
			getPubDate(e.getChild("pdt"),keyprfx);

		//Publisher Name
		if(e.getChild("pnm")!=null)
			record.put(keyprfx+"PUB",getMixData(e.getChild("pnm").getContent(),new StringBuffer()));

	   //Publication Country
		if(e.getChild("cntry")!=null)
			record.put(keyprfx+"CPUB",getMixData(e.getChild("cntry").getContent(),new StringBuffer()));

		//Coden
		if(e.getChild("cdn")!=null)
			record.put(keyprfx+"CDN",getMixData(e.getChild("cdn").getContent(),new StringBuffer()));


		//ISSN
		//06/07 new  format
		if(e.getChild("issng")!=null)
		{
			record.put(keyprfx+ "SN",getIssn(e.getChild("issng"),"print"));
			record.put("N"+ keyprfx + "SN",getIssn(e.getChild("issng")));
		}
		// 06/07 this is for current format
		else if(e.getChild("issn")!=null)
		{
			record.put(keyprfx+"SN",getMixData(e.getChild("issn").getContent(),new StringBuffer()));
		}

		//SICI
		if(e.getChild("sici")!=null)
			record.put(keyprfx+"SICI",getMixData(e.getChild("sici").getContent(),new StringBuffer()));

		//CCCC
		if(e.getChild("cccc")!=null)
			record.put(keyprfx+"CCCC",getMixData(e.getChild("cccc").getContent(),new StringBuffer()));

		//UNCMED
		if(e.getChild("uncmed")!=null)
			record.put(keyprfx+"UM",getMixData(e.getChild("uncmed").getContent(),new StringBuffer()));

		//DOCUMENT NUMBER
		if(e.getChild("docnum")!=null)
			record.put(keyprfx+"DNUM",getMixData(e.getChild("docnum").getContent(),new StringBuffer()));

		//DOI
		if(e.getChild("doi")!=null)
			record.put(keyprfx+"DOI",new StringBuffer(e.getChildTextTrim("doi")));

		//URL
		if(e.getChild("url")!=null)
			record.put(keyprfx+"URL",getMixData(e.getChild("url").getContent(),new StringBuffer()));

		//DCURL
		if(e.getChild("dcurl")!=null)
			record.put(keyprfx+"DCURL",getMixData(e.getChild("dcurl").getContent(),new StringBuffer()));

	}

	private void getBookData(Element e)
	{
		if(e.getName().equals("bookg"))
		{
			if(e.getChild("part")!=null)
				record.put("PARTNO",new StringBuffer(e.getChildTextTrim("part")));
			//VB:for translation
			if(e.getChild("trg") !=null)
				record.put("TRS",getName(e.getChild("trg")));
		}

		if(e.getName().equals("bookg")||e.getName().equals("rptg"))
		{
			if(e.getChild("pubti")!=null)
				record.put("PUBTI",getMixData(e.getChild("pubti").getContent(),new StringBuffer()));
			if(e.getChild("editg")!=null) {
				record.put("EDS",getName(e.getChild("editg")));
				record.put("EAFF",getAffiliation(e.getChild("editg"),"E"));
			}
			if(e.getChild("pgn")!=null)
				record.put("PIPN",getMixData(e.getChild("pgn").getContent(),new StringBuffer()));

		}

		if(e.getName().equals("rptg"))
		{
			//VB:fields for new correction file
			if(e.getChild("usgchno")!=null)
				record.put("UGCHN",getMixData(e.getChild("usgchno").getContent(),new StringBuffer()));
			if(e.getChild("contno")!=null)
				record.put("CNUM",getMixData(e.getChild("contno").getContent(),new StringBuffer()));
			if(e.getChild("repno")!=null)
				record.put("RNUM",getMixData(e.getChild("repno").getContent(),new StringBuffer()));
		}
	// new doc type - Standards stdg , added 05/2007

		if(e.getName().equals("stdg"))
		{
			if(e.getChild("stdno")!=null)
				record.put("RNUM",getMixData(e.getChild("stdno").getContent(),new StringBuffer()));
			if(e.getChild("version")!=null)
				record.put("VRN",getMixData(e.getChild("version").getContent(),new StringBuffer()));
		}



		if(e.getName().equals("rptg") ||
		        e.getName().equals("dssg") ||
		        e.getName().equals("stdg"))
		{
			if(e.getChild("issorg")!=null)
			{
				StringBuffer iorgdata = new StringBuffer();
				if(e.getChild("issorg").getChild("orgn") != null)
					iorgdata = iorgdata.append(getMixData(e.getChild("issorg").getChild("orgn").getContent(),new StringBuffer()));
				if((e.getChild("issorg").getChild("orgn") != null) && (e.getChild("issorg").getChild("cntry")!=null))
					iorgdata = iorgdata.append(", ");
				if(e.getChild("issorg").getChild("cntry")!=null)
					iorgdata = iorgdata.append(getMixData(e.getChild("issorg").getChild("cntry").getContent(),new StringBuffer()));
					record.put("IORG",iorgdata);
				if(e.getChild("issorg").getChild("cntry")!=null)
					record.put("CIORG",getMixData(e.getChild("issorg").getChild("cntry").getContent(),new StringBuffer()));
			}
		}
		if(e.getName().equals("dssg"))
		{
			if(e.getChild("subdt")!=null)
			{
				if(e.getChild("subdt").getChild("sdate")!=null)
					record.put("FDATE",getDate(e.getChild("subdt").getChild("sdate")));
				if(e.getChild("subdt").getChild("odate")!=null)
					record.put("OFDATE",getMixData(e.getChild("subdt").getChild("odate").getContent(),new StringBuffer()));
			}
		}
		if(e.getChild("pug")!=null)
			getPub(e.getChild("pug"));
	}

	private void getConferenceData(Element e)
	{
		if(e.getChild("ct")!=null)
			record.put("TC",getMixData(e.getChild("ct").getContent(),new StringBuffer()));
		if(e.getChild("cndt")!=null)
			getPubDate(e.getChild("cndt"),"");
		if((e.getChild("loc")!=null) || (e.getChild("cntry")!=null)) {
			record.put("CLOC",getPlace(e));
		}
		if(e.getChild("cntry")!=null)
			record.put("CCNF",getMixData(e.getChild("cntry").getContent(),new StringBuffer()));
		if(e.getChild("cnsg")!=null)
			record.put("SORG",getFields(e.getChild("cnsg")));
	}

	private StringBuffer getFields(Element e)
	{
		StringBuffer field = new StringBuffer();
		List lt = e.getChildren();

		for(int i=0;i<lt.size();i++)
		{
			Element t = (Element)lt.get(i);
			getMixData(t.getContent(),field);
			field.append(AUDELIMITER);
		}
		return field.delete(field.lastIndexOf(AUDELIMITER),field.length());
	}

	private void getPatentData(Element e)
	{
		if(e.getChild("pdg")!=null)
		{
			if(e.getChild("pdg").getChildTextTrim("patno")!=null)
				record.put("PNUM",new StringBuffer(e.getChild("pdg").getChildTextTrim("patno")));
			if(e.getChild("pdg").getChild("cntry")!=null)
				record.put("CPAT",getMixData(e.getChild("pdg").getChild("cntry").getContent(),new StringBuffer()));
			if(e.getChild("pdg").getChild("subdt")!=null)
			{
				if(e.getChild("pdg").getChild("subdt").getChild("sdate")!=null)
					record.put("FDATE",getDate(e.getChild("pdg").getChild("subdt").getChild("sdate")));
				if(e.getChild("pdg").getChild("subdt").getChild("odate")!=null)
					record.put("OFDATE",getMixData(e.getChild("pdg").getChild("subdt").getChild("odate").getContent(),new StringBuffer()));
			}
			if(e.getChild("pdg").getChild("assg")!=null)
				record.put("PAS",getFields(e.getChild("pdg").getChild("assg")));
		}
		//VB:added for original patent
		if(e.getChild("opag")!=null)
		{
			if(e.getChild("opag").getChildTextTrim("patno") !=null)
				record.put("OPAN",new StringBuffer(e.getChild("opag").getChildTextTrim("patno")));
			if(e.getChild("opag").getChildTextTrim("cntry") !=null)
				record.put("COPA",getMixData(e.getChild("opag").getChild("cntry").getContent(),new StringBuffer()));
			if(e.getChild("opag").getChild("prdt")!=null)
			{
				if(e.getChild("opag").getChild("prdt").getChild("sdate") !=null)
					record.put("PPDATE",getDate(e.getChild("opag").getChild("prdt").getChild("sdate")));
				if(e.getChild("opag").getChild("prdt").getChild("odate") !=null)
					record.put("OPPDATE",getMixData(e.getChild("opag").getChild("prdt").getChild("odate").getContent(),new StringBuffer()));
			}
		}
		if(e.getChild("pdt")!=null)
			getPubDate(e.getChild("pdt"),"P");
		if(e.getChild("cntry")!=null)
			record.put("PCPUB",getMixData(e.getChild("cntry").getContent(),new StringBuffer()));
		if(e.getChild("pp")!=null)
			record.put("NPL1",getMixData(e.getChild("pp").getContent(),new StringBuffer()));
	}

	private void getAmendmentData(Element e)
	{
		record.put("AC",getMixData(e.getChild("comm").getContent(),new StringBuffer()));
		record.put("AMDREF",getMixData(e.getChild("ref").getContent(),new StringBuffer()));
	}

	private void getPubDate(Element e, String keyprfx)
	{
		//Publication Date or Conference Date
		if(e.getName().equals("pdt"))
		{
			// Take only the primary date
			if(keyprfx.equals("P")) {
				getPubYear(e);
			}
		}
		else if(e.getName().equals("cndt"))
			keyprfx=keyprfx+"C";
		else if(e.getName().equals("amndt"))
			keyprfx=keyprfx+"A";

		if(e.getChild("sdate")!=null)
			record.put(keyprfx+"SPDATE",getDate(e.getChild("sdate")));
		if(e.getChild("edate")!=null)
			record.put(keyprfx+"EPDATE",getDate(e.getChild("edate")));
		if(e.getChild("odate")!=null)
			record.put(keyprfx+"OPDATE",getMixData(e.getChild("odate").getContent(),new StringBuffer()));
	}

	private void getPubYear(Element e)
	{
		if(e.getChild("sdate")!=null)
			record.put("PYR",new StringBuffer(e.getChild("sdate").getChildTextTrim("yr")));
		else if(e.getChild("edate")!=null)
		{
			record.put("PYR",new StringBuffer(e.getChild("edate").getChildTextTrim("yr")));
		}
		else if(e.getChild("odate")!=null)
		{
			StringBuffer str= getMixData(e.getChild("odate").getContent(),new StringBuffer());
			if (str.substring(str.length()-4).matches("[1][8-9][0-9][0-9]"))
				record.put("PYR",new StringBuffer(str.substring(str.length()-4)));
		}
	}


	//VB:added for numerical data indexing
	private void getNumIndexingData(Element e)
	{
		StringBuffer numindex = new StringBuffer();
		List lt = e.getChildren();

		for(int i=0;i<lt.size();i++)
		{
			Element t = (Element)lt.get(i);
			if(t.getChild("quan")!=null)
				numindex = numindex.append(getMixData(t.getChild("quan").getContent(),new StringBuffer()));
			if(t.getChild("value1")!=null) {
				numindex = numindex.append(IDDELIMITER);
				numindex = numindex.append(getMixData(t.getChild("value1").getContent(),new StringBuffer()));
			}
			if(t.getChild("value2")!=null) {
				numindex = numindex.append(IDDELIMITER);
				numindex = numindex.append(getMixData(t.getChild("value2").getContent(),new StringBuffer()));
			}
			if(t.getChild("unit")!=null) {
				numindex = numindex.append(IDDELIMITER);
				numindex = numindex.append(getMixData(t.getChild("unit").getContent(),new StringBuffer()));
			}

			if(i < lt.size()-1)
				numindex = numindex.append(AUDELIMITER);
		}
		record.put("NDI",numindex);
	}

	//VB: added for SU
	private void getSU(Element e)
	{
		StringBuffer sudata = new StringBuffer();
		if(e.getChild("yr")!=null)
			sudata = sudata.append(e.getChild("yr").getTextTrim());
		if((e.getChild("yr")!=null) && (e.getChild("iss")!=null))
			sudata = sudata.append("-");
		if(e.getChild("iss")!=null)
			sudata = sudata.append(e.getChild("iss").getTextTrim());
		record.put("SU",sudata);
	}

	//VB:added for Chemical data indexing
	private void getChemIndexingData(Element e)
	{
		StringBuffer chemindex = new StringBuffer();
		List lt = e.getChildren();

		for(int i=0;i<lt.size();i++)
		{
			Element t = (Element)lt.get(i);

			List lt2 = t.getChildren();
			for(int j=0;j<lt2.size();j++)
			{
				Element t2 = (Element)lt2.get(j);

				if(t2.getChild("item")!=null)
					chemindex = chemindex.append(getMixData(t2.getChild("item").getContent(),new StringBuffer()));
				if(t2.getChild("role")!=null) {
					chemindex = chemindex.append("/");
					chemindex = chemindex.append(getMixData(t2.getChild("role").getContent(),new StringBuffer()));
				}
				if(j < lt2.size()-1)
					chemindex = chemindex.append(IDDELIMITER);
			}

			if(i < lt.size()-1)
				chemindex = chemindex.append(AUDELIMITER);
		}

		record.put("CHI",chemindex);
	}

	private void getPub(Element e)
	{
		//Publication Info
		if(e.getChild("pdt")!=null)
			getPubDate(e.getChild("pdt"),"P");
		if(e.getChild("pnm")!=null)
			record.put("PPUB",getMixData(e.getChild("pnm").getContent(),new StringBuffer()));
		if((e.getChild("loc")!=null) || (e.getChild("cntry")!=null)) {
			record.put("BPPUB",getPlace(e));
		}
		if(e.getChild("pp")!=null)
			record.put("NPL1",getMixData(e.getChild("pp").getContent(),new StringBuffer()));
		if(e.getChild("doi")!=null)
			record.put("PDOI",new StringBuffer(e.getChildTextTrim("doi")));
		//VB:fields  correction file
		if(e.getChild("cntry")!=null)
			record.put("PCPUB",getMixData(e.getChild("cntry").getContent(),new StringBuffer()));
		if(e.getChild("isbn")!=null)
			record.put("SBN",new StringBuffer(e.getChildTextTrim("isbn")));
		if(e.getChild("cccc")!=null)
			record.put("PCCCC",getMixData(e.getChild("cccc").getContent(),new StringBuffer()));
		if(e.getChild("uncmed")!=null)
			record.put("PUM",getMixData(e.getChild("uncmed").getContent(),new StringBuffer()));
		if(e.getChild("avail")!=null)
			record.put("AVAIL",getMixData(e.getChild("avail").getContent(),new StringBuffer()));
		if(e.getChild("prc")!=null)
			record.put("PRICE",new StringBuffer(e.getChildTextTrim("prc")));
		if(e.getChild("docnum")!=null)
			record.put("PDNUM", new StringBuffer(e.getChildTextTrim("docnum")));
		if(e.getChild("url")!=null)
			record.put("PURL", getMixData(e.getChild("url").getContent(),new StringBuffer()));
		if(e.getChild("dcurl")!=null)
			record.put("PDCURL", getMixData(e.getChild("dcurl").getContent(),new StringBuffer()));
		if(e.getChild("issng")!=null)
		{
			record.put("PSN",getIssn(e.getChild("issng"),"print"));
			record.put("NPSN",getIssn(e.getChild("issng")));
		}
		//06/07 - this is for current format
		else if (e.getChild("issn")!=null)
		{
			record.put("PSN",new StringBuffer(e.getChildTextTrim("issn")));

		}
	}

    private StringBuffer getPlace(Element e)
    {
	    StringBuffer place = new StringBuffer();
	    if((e.getChild("loc")!=null) || (e.getChild("cntry")!=null)) {
		    if(e.getChild("loc")!=null)
				place.append(getMixData(e.getChild("loc").getContent(),new StringBuffer()));
			if((e.getChild("loc")!=null) && (e.getChild("cntry")!=null)) {
				place.append(", ");
			}
			if(e.getChild("cntry")!=null)
				place.append(getMixData(e.getChild("cntry").getContent(),new StringBuffer()));
		}
		return place;
    }

	//Issn

	private StringBuffer getIssn(Element e, String type)
	{
		String issnType = type;
		//new db fields NPSN, NSSN will contain combined print and online issn
		boolean isCombinedIssn = false;
		ArrayList types = new ArrayList();

		//existing db issn fields PSN, SSN contain only print issns
		if (type !=  null)
		{
			types.add(type);
		}
		else // new db table field contains combined print and online issns
		{
			isCombinedIssn = true;
			types.add("print");
			types.add("online");
		}
		StringBuffer issn = new StringBuffer();

		issn.append(getChildByAttribute(e,
										(String)types.get(0),
										isCombinedIssn));

		if (isCombinedIssn)
		{
			issn.append(AUDELIMITER);
			issn.append(getChildByAttribute(e,
											(String)types.get(1),
											isCombinedIssn));
		}

		return issn;
	}

	private StringBuffer getChildByAttribute(Element e,
											 String attributeValue,
											 boolean isCombinedIssn)
	{
		StringBuffer c = new StringBuffer();
		List lt = e.getChildren();

		for( int j=0 ; j < lt.size() ; j++)
		{
			Element i = (Element)lt.get(j);

			if(i.getAttributeValue("type")!= null &&
			        (i.getAttributeValue("type")).equals(attributeValue))
			{
				// add prefix for new db fields NPSN, NSSN for combined issn
				if (isCombinedIssn)
				{
					c.append(attributeValue).append("_");
				}
				c.append(i.getText());
			}

		}
		return c;

	}

	private StringBuffer getIssn(Element e)
	{
		return getIssn(e, null);
	}

	//Indexing Methods

	private StringBuffer getIndexing(Element e,String type)
	{
		StringBuffer terms = new StringBuffer();
		String elementname = e.getName().trim();
		List lt = e.getChildren(type);

		for(int i=0;i<lt.size();i++)
		{
			Element t = (Element)lt.get(i);
			if(t.getName().equals("cc"))
			{
				if(t.getAttributeValue("type")!=null && (t.getAttributeValue("type")).equals("prime"))
				{
					terms.append("*");
				}
				terms.append(t.getChildTextTrim("code"));

				if(elementname.equalsIgnoreCase("ipcg") &&
										t.getChild("cct")!= null )
				{
					terms.append(IDDELIMITER);
					terms.append(t.getChildTextTrim("cct"));
				}
				terms.append(AUDELIMITER);

			}
			else
			{
				getMixData(t.getContent(),terms);
				terms.append(AUDELIMITER);
			}
		}
		return terms.delete(terms.lastIndexOf(AUDELIMITER),terms.length());
	}

}
