package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AbstractRecord {
	private String text;			// Abstract text
	private boolean v2;				// Indicates v2 of the Abstract text (Inspec only)
	private String label = "Abstract";
	private String doi;				// Doc object id
	private String issn;			// ISSN
	private String issnlink;		// ISSN link
	private String eissn;			// E-ISSN
	private String eissnlink;		// E-ISSN link
	private String coden;			// CODEN
	private String codenlink;		// CODEN link
	private String mi;				// Material Identity Number
	private String milink;			// MIN link
	
	private List<String> figures;	// Figures (Inspec backfile)
	
	private int refcount = -1;
	private int patrefcount = -1;
	private int nonpatrefcount = -1;
	private int citerefcount = -1;

    // Patent-related links
    protected String patentrefslink;
    protected String inspecrefslink;
    protected String otherrefslink;
    protected String citedbyrefslink;

	public class OrderedLabel implements Comparable<OrderedLabel>{
		private int order;
		private String label;
		public String getLabel() {
			return label;
		}
		public OrderedLabel(int order, String label) {
			this.order = order;
			this.label = label;
		}
		@Override
		public int compareTo(OrderedLabel ol) {
			if (ol.order > this.order) return -1;
			else if (ol.order < this.order) return 1;
			else {
				// Order is equivalent, check the label value
				return this.label.compareTo(ol.label);
			}
		}
	}
	
	// Various terms that can be present - controlled vocab, CAS 
	// registry, main headings, etc.
	private Map<String,List<AbstractTerm>> termmap;
		
	// Treatments
	private List<String> treatments;
		
	// Classification code maps
	private Map<String,List<ClassificationCode>> classificationcodes = new TreeMap<String,List<ClassificationCode>>();
	private Map<String,List<ClassificationCode>> inspecclassificationcodes = new TreeMap<String,List<ClassificationCode>>();
	private String mainheading;
	private String bookdescription;			//<BAB> book description
	
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getIssn() {
		return issn;
	}
	public void setIssn(String issn) {
		this.issn = issn;
	}
	public String getEissn() {
		return eissn;
	}
	public void setEissn(String eissn) {
		this.eissn = eissn;
	}
	public String getMainheading() {
		return mainheading;
	}
	public void setMainheading(String mainheading) {
		this.mainheading = mainheading;
	}
	
	public void addClassificationcode(String label, String id, String title, String link) {
		ClassificationCode clcode = new ClassificationCode();
		clcode.setId(id);
		clcode.setTitle(title);
		clcode.setLinkedid(link);
		List<ClassificationCode> clcodes = classificationcodes.get(label);
		if (clcodes == null) {
			clcodes = new ArrayList<ClassificationCode>();
			classificationcodes.put(label, clcodes);
		}
		clcodes.add(clcode);
	}
	public Map<String, List<ClassificationCode>> getClassificationcodes() {
		return classificationcodes;
	}
	public void addInspecclassificationcode(String label, String id, String title, String link) {
		ClassificationCode clcode = new ClassificationCode();
		clcode.setId(id);
		clcode.setTitle(title);
		clcode.setLinkedid(link);
		List<ClassificationCode> clcodes = inspecclassificationcodes.get(label);
		if (clcodes == null) {
			clcodes = new ArrayList<ClassificationCode>();
			inspecclassificationcodes.put(label, clcodes);
		}
		clcodes.add(clcode);
	}
	public Map<String, List<ClassificationCode>> getInspecclassificationcodes() {
		return inspecclassificationcodes;
	}
	public int getRefcount() {
		return refcount;
	}
	public void setRefcount(int refcount) {
		this.refcount = refcount;
	}
	public int getPatrefcount() {
		return patrefcount;
	}
	public void setPatrefcount(int patrefcount) {
		this.patrefcount = patrefcount;
	}
	public int getNonpatrefcount() {
		return nonpatrefcount;
	}
	public void setNonpatrefcount(int nonpatrefcount) {
		this.nonpatrefcount = nonpatrefcount;
	}
	public int getCiterefcount() {
		return citerefcount;
	}
	public void setCiterefcount(int citerefcount) {
		this.citerefcount = citerefcount;
	}
	public String getCoden() {
		return coden;
	}
	public void setCoden(String coden) {
		this.coden = coden;
	}
	public String getIssnlink() {
		return issnlink;
	}
	public void setIssnlink(String issnlink) {
		this.issnlink = issnlink;
	}
	public String getEissnlink() {
		return eissnlink;
	}
	public void setEissnlink(String eissnlink) {
		this.eissnlink = eissnlink;
	}
	public String getCodenlink() {
		return codenlink;
	}
	public void setCodenlink(String codenlink) {
		this.codenlink = codenlink;
	}
	public String getMi() {
		return mi;
	}
	public void setMi(String mi) {
		this.mi = mi;
	}
	public String getMilink() {
		return milink;
	}
	public void setMilink(String milink) {
		this.milink = milink;
	}
	
	public void addTerm(String label, AbstractTerm term) {
		if (termmap == null) termmap = new TreeMap<String,List<AbstractTerm>>();
		List<AbstractTerm> termlist = termmap.get(label);
		if (termlist == null) {
			termlist = new ArrayList<AbstractTerm>();
			termmap.put(label, termlist);
		}
		termlist.add(term);
	}
	public Map<String,List<AbstractTerm>> getTermmap() { 
		return termmap; 
	}
	public List<String> getFigures() {
		return figures;
	}
	public void addFigure(String figure) {
		if (this.figures == null) this.figures = new ArrayList<String>();
		figures.add(figure);
	}
	public List<String> getTreatments() {
		return treatments;
	}
	public void addTreatment(String treatment) {
		if (this.treatments == null) this.treatments = new ArrayList<String>();
		this.treatments.add(treatment);
	}
	public boolean isV2() {
		return v2;
	}
	public void setV2(boolean v2) {
		this.v2 = v2;
	}
	public String getBookdescription() {
		return bookdescription;
	}
	public void setBookdescription(String bookdescription) {
		this.bookdescription = bookdescription;
	}
	
    public String getPatentrefslink() {
        return patentrefslink;
    }

    public void setPatentrefslink(String patentrefslink) {
        this.patentrefslink = patentrefslink;
    }

    public String getInspecrefslink() {
        return inspecrefslink;
    }

    public void setInspecrefslink(String inspecrefslink) {
        this.inspecrefslink = inspecrefslink;
    }

    public String getOtherrefslink() {
        return otherrefslink;
    }

    public void setOtherrefslink(String otherrefslink) {
        this.otherrefslink = otherrefslink;
    }

    public String getCitedbyrefslink() {
        return citedbyrefslink;
    }

    public void setCitedbyrefslink(String citedbyrefslink) {
        this.citedbyrefslink = citedbyrefslink;
    }
}
