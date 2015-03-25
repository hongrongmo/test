package org.ei.common.bd;
import java.util.*;


import org.apache.oro.text.perl.Perl5Util;
//import org.ei.data.bd.loadtime.BdParser;
import org.apache.oro.text.perl.*;


public class CVTerms
{

   private Perl5Util perl = new Perl5Util();
   private String cvTerms = null;
   List result = new  ArrayList();
   private List arrcvTerms = new ArrayList();


   private List cvt = new ArrayList();
   private List cvm = new ArrayList();
   private List cva = new ArrayList();
   private List cvn = new ArrayList();
   private List cvp  = new ArrayList();
   private List cvma = new ArrayList();
   private List cvmn = new ArrayList();
   private List cvmp = new ArrayList();

   private String [] cvexpand = null;
   private String cvexpandstr = null;

   private String [] cvmexpand = null;
   private String cvmexpandstr = null;


	public CVTerms(String cv)
	{
		if(cv != null &&
				!cv.trim().equals(""))
		{
			this.cvTerms = cv.trim();
		}
	}

	public CVTerms()
	{

	}


	public void parse()
	{
		String DELIM= new String(new char[] {31});
		String [] cvelements = null;
		if(this.cvTerms != null && this.cvTerms.length() >0)
		{
			cvelements = this.cvTerms.split(DELIM);
		}

		List l = Arrays.asList(cvelements);
		if(l.size() > 0)
		{
			for(int i = 0; i < l.size(); i++)
			{
				String str =(String) l.get(i);
				CVTerm cvt = new CVTerm();
				int boffset = 0;
				int eoffset = str.length();
				if(str.length()>2 && str.substring(0,2).equals("*-"))
				{
					cvt.setPrefix("*");
					boffset =2;

				}
				if(str.lastIndexOf("-")>1)
				{
					int offset = str.lastIndexOf("-");
					String pf = str.substring(offset+1);
					if(pf.equals("*N*A*P") ||
							pf.equals("*N*AP") ||
							pf.equals("*NA*P") ||
							pf.equals("*NAP") ||
							pf.equals("N*A*P") ||
							pf.equals("NA*P") ||
							pf.equals("N*AP") ||
							pf.equals("NAP") ||
							pf.equals("*AP") ||
							pf.equals("*A*P") ||
							pf.equals("A*P") ||
							pf.equals("AP") ||
							pf.equals("*NP") ||
							pf.equals("*N*P") ||
							pf.equals("N*P") ||
							pf.equals("NP") ||
							pf.equals("*NA") ||
							pf.equals("*N*A") ||
							pf.equals("N*A") ||
							pf.equals("NA") ||
							pf.equals("*N") ||
							pf.equals("*A") ||
							pf.equals("*P") ||
							pf.equals("N") ||
							pf.equals("A") ||
							pf.equals("P"))
					{
						cvt.setPostfix(pf);
						eoffset = offset;
					}

				}
				cvt.setTerm(str.substring(boffset, eoffset));
				//System.out.println("pref::"+cvt.getPrefix());
				//System.out.println("postf::"+cvt.getPostfix());
				//System.out.println("term::"+cvt.getTerm());
				addToarrcvTerms(cvt);
			}
		}
		parse(this.arrcvTerms);
	}

	public void parse(List cvterms)
	{
		if(cvterms != null && cvterms.size() >0)
		{
			ArrayList cvarr = new ArrayList();
			ArrayList cvmarr = new ArrayList();
			for (int i = 0; i < cvterms.size();i++)
			{
				CVTerm cvt = (CVTerm)cvterms.get(i);

				//N and *N
				if(perl.match("/\\*N/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-N");
					this.cvmn.add(term);
					cvmarr.add("*".concat(term));
				}
				else if
				(perl.match("/N/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-N");
					this.cvn.add(term);
					cvarr.add(term);
				}

				//P and *P
				if(perl.match("/\\*P/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-P");
					this.cvmp.add(term);
					cvmarr.add("*".concat(term));
				}
				else if
				(perl.match("/P/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-P");
					this.cvp.add(term);
					cvarr.add(term);
				}

				//P and *P
				if(perl.match("/\\*A/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-A");
					this.cvma.add(term);
					cvmarr.add("*".concat(term));
				}
				else if
				(perl.match("/A/i",cvt.getPostfix()))
				{
					String term = cvt.getTerm().concat("-A");
					this.cva.add(term);
					cvarr.add(term);
				}

				if((cvt.getPrefix() == null ||
						cvt.getPrefix().equals(""))&&
						(cvt.getPostfix() == null ||
						cvt.getPostfix().equals("")))
				{
					String term = cvt.getTerm();
					this.cvt.add(term);
					cvarr.add(term);
				}
				if((cvt.getPrefix() != null &&
						!cvt.getPrefix().equals(""))&&
						(cvt.getPostfix() == null ||
						cvt.getPostfix().equals("")))
				{
					String term = cvt.getTerm();
					this.cvm.add(term);
					cvmarr.add("*".concat(term));
				}
				if(cvarr != null && cvarr.size()>0)
				{
					this.cvexpand = (String[]) cvarr.toArray(new String[0]);
				}
				if(cvmarr != null && cvmarr.size()>0)
				{
					this.cvmexpand = (String[]) cvmarr.toArray(new String[0]);
				}
			}
		}
	}


   public String getCvmexpandstr()
   	{
   		if(cvmexpand != null && cvmexpand.length > 0)
   		{
   			StringBuffer buf = new StringBuffer();

   			for (int i = 0; i < cvmexpand.length; i++)
   			{
   				buf.append(cvmexpand[i]).append(";");
   			}
   			return buf.toString();
   		}
   		return cvmexpandstr;
   	}

   	public void setCvmexpandstr(String cvmexpandstr)
   	{
   		this.cvmexpandstr = cvmexpandstr;
   	}

   	public String getCvexpandstr()
   	{
   		if(cvexpand != null && cvexpand.length > 0)
   		{
   			StringBuffer buf = new StringBuffer();
   			for (int i = 0; i < cvexpand.length; i++)
   			{
   				buf.append(cvexpand[i]).append(";");
   			}
   			return buf.toString();
   		}
   		return cvexpandstr;
   	}

   	public void setCvexpandstr(String cvexpandstr)
   	{
   		this.cvexpandstr = cvexpandstr;
	}


   public void setCvt(List cvt) {
		this.cvt = cvt;
	}

	public List getCvm() {
		return cvm;
	}

	public void setCvm(List cvm) {
		this.cvm = cvm;
	}

	public List getCva() {
		return cva;
	}

	public void setCva(List cva) {
		this.cva = cva;
	}

	public List getCvn() {
		return cvn;
	}

	public void setCvn(List cvn) {
		this.cvn = cvn;
	}

	public List getCvp() {
		return cvp;
	}

	public void setCvp(List cvp) {
		this.cvp = cvp;
	}

	public List getCvma() {
		return cvma;
	}

	public void setCvma(List cvma) {
		this.cvma = cvma;
	}

	public List getCvmn() {
		return cvmn;
	}

	public void setCvmn(List cvmn) {
		this.cvmn = cvmn;
	}

	public List getCvmp() {
		return cvmp;
	}

	public void setCvmp(List cvmp) {
		this.cvmp = cvmp;
	}
	public String[] getCvexpand() {
		return cvexpand;
	}

	public void setCvexpand(String[] cvexpand) {
		this.cvexpand = cvexpand;
	}

	public List getCvt()
	{
		return cvt;
	}

	public List getArrcvTerms()
	{
		return arrcvTerms;
	}

	public void setArrcvTerms(List arrcvTerms)
	{
		this.arrcvTerms = arrcvTerms;
	}

	public void addToarrcvTerms(CVTerm term)
	{
		this.arrcvTerms.add(term);
	}
}
