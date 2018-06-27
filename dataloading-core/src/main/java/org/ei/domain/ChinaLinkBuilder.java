package org.ei.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;

public class ChinaLinkBuilder
{
    private String aulast   = null;
    private String aufirst  = null;
    private String aufull   = null;
    private String issn     = null;
    private String issn9    = null;
    private String coden    = null;
    private String title    = null;
    private String stitle   = null;
    private String atitle   = null;
    private String volume   = null;
    private String issue    = null;
    private String spage    = null;
    private String epage    = null;
    private String elsevierUrl = null;
    private String springerUrl = null;
    private String ieeeUrl = null;
    private String apsUrl = null;
    private String ojpsUrl = null;
    private String wSciUrl = null;
    private PublisherBroker pubBroker = PublisherBroker.getInstance();


	private List<String> credlist = new ArrayList<String>();

	private Map<String,String> pubIdUrlHash = new Hashtable<String,String>();

	public void setAulast(String aulast)
	{
		if(aulast != null)
		{
			this.aulast = aulast;
		}
	}
	public void setAufirst(String aufirst)
	{
		if(aufirst != null)
		{
			this.aufirst = aufirst;
		}
	}
	public void setAufull(String aufull)
	{
		if(aufull != null)
		{
			this.aufull = aufull;
		}
	}
	public void setIssn(String issn)
	{
		if(issn != null)
		{
			this.issn = issn;
		}
	}
	public void setIssn9(String issn9)
	{
		if(issn9 != null)
		{
			this.issn9 = issn9;
		}
	}
	public void setCoden(String coden)
	{
		if(coden != null)
		{
			this.coden = coden;
		}
	}
	public void setTitle(String title)
	{
		if(title != null)
		{
			this.title = title;
		}
	}
	public void setStitle(String stitle)
	{
		if(stitle != null)
		{
			this.stitle = stitle;
		}
	}
	public void setAtitle(String atitle)
	{
		if(atitle != null)
		{
			this.atitle = atitle;
		}
	}
	public void setVolume(String volume)
	{
		if(volume != null)
		{
			this.volume = volume;
		}
	}
	public void setIssue(String issue)
	{
		if(issue != null)
		{
			this.issue = issue;
		}
	}
	public void setSpage(String spage)
	{
		if(spage != null)
		{
			this.spage = spage;
		}
	}
	public void setEpage(String epage)
	{
		if(epage != null)
		{
			this.epage = epage;
		}
	}

	public String getAulast()
	{
		return this.aulast;
	}

	public String getAufirst()
	{
		return this.aufirst;
	}

	public String getAufull()
	{
		return this.aufull;
	}

	public String getIssn()
	{
		return this.issn;
	}

	public String getIssn9()
	{
		return this.issn9;
	}

	public String getCoden()
	{
		return this.coden;
	}
	public String getTitle()
	{
		return this.title;
	}
	public String getStitle()
	{
		return this.stitle;
	}
	public String getAtitle()
	{
		return this.atitle;
	}
	public String getVolume()
	{
		return this.volume;
	}
	public String getIssue()
	{
		return this.issue;
	}
	public String getSpage()
	{
		return this.spage;
	}
	public String getEpage()
	{
		return this.epage;
	}

    public List<String> getCreds()
    {
		return this.credlist;
	}

    public void setCreds(String creds)
    {
    	Perl5Util perl = new Perl5Util();
    	if(creds != null)
    	{
      		perl.split(credlist, "/;/", creds);
    	}
  	}

    public void buildElsevierUrl()
    {
            elsevierUrl = "http://sdos.lib.tsinghua.edu.cn/cgi-bin/sciserv.pl?collection=journals";

            if((this.issn != null && this.issn.length() > 0) &&
               (this.volume != null && this.volume.length() > 0) &&
               (this.spage != null && this.spage.length() > 0))
            {
                if(this.issue != null && this.issue.length() > 0)
                {
                    elsevierUrl = "http://elsevier.lib.tsinghua.edu.cn/science?_volkey="+this.issn+"$"+this.volume+"$"+this.spage;

                }
                else
                {
                    elsevierUrl = "http://elsevier.lib.tsinghua.edu.cn/science?_volkey="+this.issn+"$"+this.volume+"$"+this.spage+"$"+this.issue;
                }
            }
            else if(this.issn != null && this.issn.length() > 0)
            {
                elsevierUrl = "http://elsevier.lib.tsinghua.edu.cn/science?_volkey="+this.issn;
            }
            else
            {
                elsevierUrl = "http://sdos.lib.tsinghua.edu.cn/cgi-bin/sciserv.pl?collection=journals";

            }
    }

    public void buildSpringerUrl()
    {
        springerUrl = "http://springer.lib.tsinghua.edu.cn/";

         if((this.issn != null && this.issn.length() > 0) &&
            (this.volume != null && this.volume.length() > 0) &&
            (this.spage != null && this.spage.length() > 0))
        {
            if(this.issue != null && this.issue.length() > 0)
            {
                springerUrl = "http://springer.lib.tsinghua.edu.cn/openurl.asp?genre=article&issn="+this.issn+"&volume="+this.volume+"&issue="+this.issue+"&spage="+this.spage;

            }
            else
            {
                springerUrl = "http://springer.lib.tsinghua.edu.cn/openurl.asp?genre=journal&issn="+this.issn;
            }
        }
        else if(this.issn != null && this.issn.length() > 0)
        {
            springerUrl = "http://springer.lib.tsinghua.edu.cn/openurl.asp?genre=journal&issn="+this.issn;
        }
        else
        {
            springerUrl = "http://springer.lib.tsinghua.edu.cn/";

        }
    }

    public void buildIEEEUrl() throws PublisherException
    {
        ieeeUrl = " http://ieeexplore.ieee.org/";
        String punumber = "";
        if (this.issn !=null && this.issn.length()>0)
        {
            punumber = getPunumber(this.issn);
        }

        if((punumber !=null && punumber.length() > 0) &&
           (this.volume != null && this.volume.length() > 0) &&
           (this.issue != null && this.issue.length() > 0))
        {

                ieeeUrl = " http://ieeexplore.ieee.org/servlet/opac?punumber="+punumber+"&isvol="+this.volume+"&isno="+this.issue;

        }
        else if(punumber !=null && punumber.length() > 0)
        {
            ieeeUrl = "http://ieeexplore.ieee.org/servlet/opac?punumber="+punumber;
        }
        else
        {
            ieeeUrl = "http://ieeexplore.ieee.org/";
        }
    }

    //World Scientific
    public void buildWsciUrl()
    {
            if(this.issn   != null && this.issn.length()   > 0 &&
               this.volume != null && this.volume.length() > 0 &&
               this.spage  != null && this.spage.length()  > 0 &&
               this.issue  != null && this.issue.length()  > 0)
            {
                wSciUrl = "http://worldscinet.lib.tsinghua.edu.cn/openurl.asp?genre=article&issn="+this.issn+"&volume="+this.volume+"&issue="+this.issue+"&spage="+this.spage;
            }
            else if(this.issn != null && this.issn.length() > 0)
            {
                wSciUrl = "http://worldscinet.lib.tsinghua.edu.cn/openurl.asp?genre=journal&issn="+this.issn;
            }
            else
            {
                wSciUrl = "http://worldscinet.lib.tsinghua.edu.cn/";
            }
    }


    public String getPunumber(String issn) throws PublisherException
    {
        String punumber = pubBroker.fetchIEEEPunumber(issn);
        return punumber;
    }

    public void buildOJPSUrl() throws PublisherException
    {
        ojpsUrl = "http://scitation.aip.org/journals/doc/MY-SCI/myBrowseAZ.jsp";
        String conumber = "";
        if (this.issn !=null && this.issn.length()>0)
        {
            conumber = getConumber(this.issn);
        }


        if((conumber !=null && conumber.length() > 0) &&
           (this.volume != null && this.volume.length() > 0))
        {
            //link to abstract level
            if((conumber !=null && conumber.length() > 0) &&
               (this.volume != null && this.volume.length() > 0) &&
               (this.spage != null && this.spage.length() > 0))
            {
                ojpsUrl = "http://link.aip.org/link/?"+conumber+"/"+volume+"/"+spage;

            }
            //link to issue level
            else if(conumber !=null && conumber.length() > 0 &&
                   (this.volume != null && this.volume.length() > 0) &&
                   (this.issue != null && this.issue.length() > 0))
            {
                ojpsUrl = "http://link.aip.org/link/?"+conumber+"/"+volume+"/"+issue+"/htmltoc";
            }

        }
        // link to journal level
        else if(this.coden != null && this.coden.length() > 0)
        {
            ojpsUrl = "http://scitation.aip.org/dbt/dbt.jsp?KEY="+this.coden;
        }
        else
        {
            ojpsUrl = "http://scitation.aip.org/journals/doc/MY-SCI/myBrowseAZ.jsp";
        }
    }

	public String getConumber(String issn) throws PublisherException
	{
		String conumber = pubBroker.fetchOJPSConumber(issn);
		return conumber;
	}

	public Map<String, String> getCredUrls()
	{
		Map<String, String> mapCredUrls = new Hashtable<String, String>();
	  	Iterator<String> itr = pubIdUrlHash.keySet().iterator();
	  	while(itr.hasNext())
	  	{
			String strKey = (String) itr.next();
			if(this.credlist.contains(strKey))
			{
				mapCredUrls.put(strKey, (String) pubIdUrlHash.get(strKey));
			}
		}
		return mapCredUrls;
	}

    public void buildUrls() throws PublisherException
    {
        buildElsevierUrl();
        pubIdUrlHash.put("Elsevier",elsevierUrl);
        buildSpringerUrl();
        pubIdUrlHash.put("Springer",springerUrl);
        buildWsciUrl();
        pubIdUrlHash.put("Wsci",wSciUrl);

        pubIdUrlHash.put("ACM", "http://acm.lib.tsinghua.edu.cn");
        //AIP?APS?MAIK?ASCE?ASME
        buildOJPSUrl();
        pubIdUrlHash.put("AIP", ojpsUrl);
        pubIdUrlHash.put("APS", ojpsUrl);
        pubIdUrlHash.put("MAIK",ojpsUrl);
        pubIdUrlHash.put("ASCE",ojpsUrl);
        pubIdUrlHash.put("ASME",ojpsUrl);

        pubIdUrlHash.put("AIPCP","http://166.111.120.23/cd.htm");
        buildIEEEUrl();
        pubIdUrlHash.put("Ieee",ieeeUrl);
        pubIdUrlHash.put("Science","http://intl.sciencemag.org");
        pubIdUrlHash.put("Kluwer","http://kluwer.calis.edu.cn");
    }

	public String getPubURL(String pubid)
	{
		String url = null;
		if(this.credlist.contains(pubid))
		{
			url = (String) pubIdUrlHash.get(pubid);
		}

		return url;
	}
}
