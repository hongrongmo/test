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
    public static final String PRIMARY1 = "1";
    public static final String PRIMARY2 = "2";


    public static void main(String[] args)
        throws Exception
    {
            String filename = args[0];
           Hashtable rec;
     /*   BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
           InspecXMLReader r = new InspecXMLReader(in);
           while((rec=r.getRecord())!=null)
           {
				System.out.println(rec.toString());
		 	} */
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
			return record;
		}
		return null;
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

		   if(n.getAttribute("id")!=null)
		   {
			  name.append(IDDELIMITER);
			  name.append(n.getAttribute("id").getValue());
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

		StringBuffer affiliation = new StringBuffer();
		List auaff = e.getChildren("faff");

	    for(int j=0;j<auaff.size();j++)
		{
			 Element m = (Element)auaff.get(j);

		   getMixData(m.getChild("aff").getContent(),affiliation);

		   if(m.getChild("cntry")!=null)
		   {
			   record.put(keyprfx+"FC",getMixData(m.getChild("cntry").getContent(),new StringBuffer()));
		   }
		   if(m.getAttribute("rid")!=null)
		   {
			  affiliation.append(IDDELIMITER);
			  affiliation.append(m.getAttribute("rid").getValue());
		   }

		   affiliation.append(AUDELIMITER);

		}

		 if(affiliation.lastIndexOf(AUDELIMITER) != -1) {
			 return affiliation.delete(affiliation.lastIndexOf(AUDELIMITER),affiliation.length());
		 }
		 else {
			 return affiliation;
		 }

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
		if(e.getChild("issn")!=null)
			record.put(keyprfx+"SN",getMixData(e.getChild("issn").getContent(),new StringBuffer()));

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
		if(e.getName().equals("rptg")||e.getName().equals("dssg"))
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
		if(e.getChild("issn")!=null)
			record.put("PSN",new StringBuffer(e.getChildTextTrim("issn")));
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


	//Indexing Methods

	private StringBuffer getIndexing(Element e,String type)
	{
		StringBuffer terms = new StringBuffer();
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