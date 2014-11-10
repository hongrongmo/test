package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ei.data.bd.BdDatabase;
import org.ei.data.bd.runtime.BDDocBuilder;
import org.ei.data.upt.runtime.UPTDocBuilder;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.Key;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.query.base.HitHighlightFinisher;

public class SearchResult {
    // Map for labels for fields
    private Map<String, String> labels = new TreeMap<String, String>();
    public void addLabel(String key, String label) {
        labels.put(key, label);
    }
    public Map<String,String> getLabels() {
        return labels;
    }
    
    //
    // Results object
    //
    private String dupids;
    private String title;
    private String source;
    private boolean nosource;
    private String sourceabbrev;
    private String vo;              // Corresponds to <VO|VOM> element (volume)
    private String is;              // Corresponds to <IS> element (issue)
    private String la;              // Corresponds to <LA> element (language)

    private int nr;                 // Corresponds to <NR> element (number of references - Inspec)
    
    private String doi;
    private boolean duplicate;
    private boolean fulltext;
    private String fulltextlink;
    private List<LocalHoldingLink> lhlinkObjects= new ArrayList<LocalHoldingLink>();
    private String doctype;
    private String doctypedisplay;
    private String accnum;
    private String pages;           // Corresponds to <PP> element
    private String page;            
    private String pagespp;
    private String yr;
    
    // Publication info
    private String pn;              // <PN> element (publisher); abs + det only
    private String ipn;             // <I_PN> element (publisher); abs + det only
    private String pd;              // <PD_YR> element (publication date)
    private String pla;             // <PLA> element (place of publication); det only
    private String pf;              // ??? det only
    private String pl;              // <PL> element (Country of publication); det only
    
    // Patent info
    private String pm;              // Publication number
    private String pm1;             // Publication number
    private String pa;              
    private String pan;
    private String ppn;              // Priority Number  
    private String pap;             // Application date
    private String papim;           // <PAPIM> Application information 
    protected List<String> pim;             //<PIM> Priority information
    private String pinfo;           // Patent information
    private String authcd;          // Patent authority
    private String derwent;         // DERWENT accession number
    protected List<String> designatedstates;    // <DSM> from xml
    protected List<String> assigneelinks;       // <EASM/EAS> from xml
    protected List<String> patassigneelinks;    // <PASM/PAS> from xml

    
    private String sp;              // Sponsor
    private String rsp;             // Research Sponsor
    private String rnlabel;         // <RN_LABEL> Report number  
    private String rn;
    private String sd;
    private String mt;              // Corresponds to <MT> element (monograph title)
    private String vt;
    private String arn;
    private String nv;
    private String upd;            // <UPD> Publication date
    private String kd;
    private String pfd;            // <PFD> Filing date
    private String pidd;
    private String ppd;
    private String nf;
    private String av;
    private String sc;
    private String cpr;
    private String cprt;
    private String pid;
    private String copa;    
    private String collection;
    
    private String treatment;
    private String discipline;
    
    // Conference info (detailed view only)
    private String cf;      // Conference name
    private String md;      // Conference date
    private String ml;      // Conference location
    private String cc;      // Conference code
    
    // External links - these are built in the CitationResults stylesheet
    protected String abstractlink;
    protected String detailedlink;
    protected String bookdetailslink;
    protected String pagedetailslink;
    protected String readchapterlink;
    protected String readpagelink;
    
    // Indicates result has been selected
    private boolean selected;
    // Search ID from basket
    private String basketsearchid;

    // Duplicate status
    private boolean dup;
    
    // Author list
    private List<Author> authors;
    // Editor list
    private List<Author> editors;
    // Corresponding author list
    private List<Author> cauthors;
    
    // Inventor list
    private List<Author> inventors;
    
    // Affiliation list - sorted by key (affil id)
    private SortedMap<Integer,Affil> affils = new TreeMap<Integer, Affil>();
    
    // Document object
    private Doc doc = new Doc();

    // CitedBy object
    private CitedBy citedby;
    
    // eBook info (optional)
    private String bti;     // Book title
    private String btst;    // Series title
    private String bct;     // Book chapter title
    private String bpp;     // Pages
    private String isbn;    // ISBN
    private String isbnlink;  // ISBN link (detailed view)
    private String isbn13;  // ISBN13
    private String isbn13link;  // ISBN13 link (detailed view)
    private String byr;     // Book pub year
    private String bpn;     // Book publisher
    private String bimg;    // Base path for book image
    private String pii;		// PII for books
    
    private String cidPrefix;
    
    private String bimgfullpath;    // Book image full path
    private String bpc;     // Total Pages
    private String toc; //Table of contents
    protected String readbooklink; //read book link
    private String bst; // Section title
    private String pr;  // <PR> element (Part number)
    private String documentbaskethitindex;  // <DOCUMENTBASKETHITINDEX> element  
    private String index;   // <INDEX> element
    private String cos; // <COS> Country of origin
    private String tt;  // <TT> Title of translation
    private List<OtherAffil> otherAffils;   // other Affiliation list  <OAF> <OA> 
    private List<Category> category;    // Category list  <CAT> <CA>
    private String illus;   // <ILLUS> Illustrations
    private String at;  // <AT> Abstract type
    private String gurl;    // <GURL> URL
    private String ant; // <ANT> Annotation
    private String cac; // <CAC> Author affiliation codes
    private String ntispricecode;   // <CVS> NTIS price code
    private String pnum;   // <PNUM> NTIS project number
    private String tnum;   // <TNUM> NTIS task number
    private String vi;  // <VI> Journal announcement
    private String ta;  // <TA> Title annotation
    private String co;  // <CO> Country of origin
    private String su;  // <SU> Notes
    private String cts; // <CTS> Contract numbers
    private String rpgm;    // <RPGM> Research program
    private String med; // <MED> Source medium
    private String pc;  // <PC> Page count
    private String lstm;    // <LSTM> Linked Terms
    private String caf; // <CAF> Corr. author affiliation
    private String kc;  // <KC> Kind
    private String att; // <ATT> Attorney, Agent or Firm
    private String papx;    // <PAPX> Application date
    protected List<String> fieldofsearch;   // <FSM> Field of search
    protected List<String> primaryexaminer;     // <PEXM/PEX> primary examiner from xml 
    private String ae;  // <AE> Assistant examiner
    private String panus;   // <PANUS> Unstandardized application number
    private String cpxlink; // <CPX-DOCID>
    private String inspeclink;  // <INS-DOCID>
    private String articlenumber; //<ARTICLE_NUMBER> Article number
    private String volt; //<VOLT> Translation volume
    private String isst; //<ISST> Translation issue
    private String ipnt; //<IPNT> Translation pages
    private String tdate; //<TDATE> Translation publication date
    private String fttj; //<FTTJ> Translation serial title
    private String chi; //<CHI> Chemical indexing 
    private String ttj; //<TTJ>  Translation abbreviated serial title 
    private String snt; //<SNT>  Translation ISSN 
    private String cnt; //<CNT>  Translation CODEN 
    private String cpubt; //<CPUBT>  Translation country of publication
    private String cpub; //<CPUB>  country of publication 
    private String ndi; //<NDI> Numerical data indexing 
    private String ags; //<AGS> Monitoring agency
    private String mlt; // <MLT> Manually linked terms
    private String atm; // <ATM> Indexing template
    private String lth; // <LTH> Linked Terms Holder
    private String papd;    // <PAPD> Application date
    private String pans;    // <PANS> Application number
    private String papco;   // <PAPCO> Application country
    private String ril; // <RIL> Source title
    private String pi;  // <PI>  Priority Number
    private String ipc; // <IPC>  IPE Code
    private String ecl; // <ECL>  ECLA Classes
    private List<String>  dt;   // <DT>  Document type
    private String stt; //<STT>
    private String sn; //<SN>
    
    
    public Key[] getCitationOrder() {
        Database db = DatabaseConfig.getInstance().getDatabase(this.doc.getDbid());
        DocumentBuilder builder = db.newBuilderInstance();
        if (builder instanceof MultiDatabaseDocBuilder) builder = new BDDocBuilder();
        return builder.getCitationKeys();
    }
    public Key[] getAbstractOrder() {
        Database db = DatabaseConfig.getInstance().getDatabase(this.doc.getDbid());
        DocumentBuilder builder = db.newBuilderInstance();
        if (builder instanceof MultiDatabaseDocBuilder) builder = new BDDocBuilder();
        return builder.getAbstractKeys();
    }
    public Key[] getDetailedOrder() {
        BdDatabase bdDatabase = new BdDatabase();
        Database db = DatabaseConfig.getInstance().getDatabase(this.doc.getDbid());
        DocumentBuilder builder = db.newBuilderInstance();
        if (builder instanceof MultiDatabaseDocBuilder) builder = new BDDocBuilder();
        
        String databaseID = db.getID();
        if(databaseID.length()> 3)
        {
            databaseID = databaseID.substring(0,3);
        }
        if(bdDatabase.isBdDatabase(databaseID))
        {
             builder = new BDDocBuilder();
        }
        
         if (builder instanceof UPTDocBuilder){
            if (this.getDoctype().equalsIgnoreCase("US Application") || this.getDoctype().equalsIgnoreCase("European Application") ) {
        
                return ((UPTDocBuilder) builder).getDetailedKeysApplication();
            }
         }
        
        return builder.getDetailedKeys();
    }

    public String getCidPrefix() {
        return cidPrefix;
    }
    public void setCidPrefix(String cidPrefix) {
        this.cidPrefix = cidPrefix;
    }

    // Abstract (optional)
    private AbstractRecord abstractrecord = new AbstractRecord();
    
    public void addAuthor(Author author) {
        if (authors == null) {
            authors = new ArrayList<Author>();
        }
        authors.add(author);
    }
    
    public void addEditor(Author author) {
        if (editors == null) {
            editors = new ArrayList<Author>();
        }
        editors.add(author);
    }
    
    public void addCauthor(Author cauthor) {
        if (cauthors == null) {
            cauthors = new ArrayList<Author>();
        }
        cauthors.add(cauthor);
    }
    
    public void addAffil(Affil affil) {
        affils.put(affil.getId(), affil);
    }
    
    public String getTitle() {
        return title;
    }

    public String getTitleNoHighlight() {
        return HitHighlightFinisher.removeMarkup(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isNosource() {
        return nosource;
    }
    public void setNosource(boolean nosource) {
        this.nosource = nosource;
    }
    public String getSourceabbrev() {
        return sourceabbrev;
    }
    public void setSourceabbrev(String sourceabbrev) {
        this.sourceabbrev = sourceabbrev;
    }
    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isFulltext() {
        return fulltext;
    }

    public void setFulltext(boolean fulltext) {
        this.fulltext = fulltext;
    }

    public String getFulltextlink() {
        return fulltextlink;
    }
    public void setFulltextlink(String fulltextlink) {
        this.fulltextlink = fulltextlink;
    }
    
    public void addLhlinkObjects(String url,String position, String imageUrl, String label) {
        if (lhlinkObjects == null){
        	lhlinkObjects = new ArrayList<LocalHoldingLink>();
        }
        lhlinkObjects.add(new LocalHoldingLink(url,position, imageUrl, label));
    }
    
    public List<LocalHoldingLink> getLhlinkObjects() {
		return lhlinkObjects;
	}
    
    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDoctypedisplay() {
        return doctypedisplay;
    }

    public void setDoctypedisplay(String doctypedisplay) {
        this.doctypedisplay = doctypedisplay;
    }

    public String getAccnum() {
        return accnum;
    }

    public void setAccnum(String accnum) {
        this.accnum = accnum;
    }
    
    public String getYr() {
        return yr;
    }

    public void setYr(String yr) {
        this.yr = yr;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getCprt() {
        return cprt;
    }

    public void setCprt(String cprt) {
        this.cprt = cprt;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getBasketsearchid() {
        return basketsearchid;
    }
    public void setBasketsearchid(String basketsearchid) {
        this.basketsearchid = basketsearchid;
    }
    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<Author> getEditors() {
        return editors;
    }

    public List<Author> getCauthors() {
        return cauthors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Collection<Affil> getAffils() {
        return affils.values();
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public String getBti() {
        return bti;
    }

    public void setBti(String bti) {
        this.bti = bti;
    }

    public String getBtst() {
        return btst;
    }

    public void setBtst(String btst) {
        this.btst = btst;
    }

    public String getBct() {
        return bct;
    }

    public void setBct(String bct) {
        this.bct = bct;
    }

    public String getBpp() {
        return bpp;
    }

    public void setBpp(String bpp) {
        this.bpp = bpp;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getByr() {
        return byr;
    }

    public void setByr(String byr) {
        this.byr = byr;
    }

    public String getBpn() {
        return bpn;
    }

    public void setBpn(String bpn) {
        this.bpn = bpn;
    }

    public String getBimg() {
        return bimg;
    }

    public void setBimg(String bimg) {
        this.bimg = bimg;
    }

    public String getPf() {
        return pf;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getRnlabel() {
        return rnlabel;
    }

    public void setRnlabel(String rnlabel) {
        this.rnlabel = rnlabel;
    }

    public String getRn() {
        return rn;
    }

    public void setRn(String rn) {
        this.rn = rn;
    }

    public String getVo() {
        return vo;
    }

    public void setVo(String vo) {
        this.vo = vo;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public String getVt() {
        return vt;
    }

    public void setVt(String vt) {
        this.vt = vt;
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }

    public List<String> getDesignatedstates() {
        return designatedstates;
    }

    public void addDesignatedstate(String link) {
        if (designatedstates == null) designatedstates = new ArrayList<String>();
        this.designatedstates.add(link);
    }

    public List<String> getAssigneelinks() {
        return assigneelinks;
    }

    public void addAssigneelink(String link) {
        if (assigneelinks == null) assigneelinks = new ArrayList<String>();
        this.assigneelinks.add(link);
    }

    public List<String> getPatassigneelinks() {
        return patassigneelinks;
    }

    public void addPatassigneelink(String link) {
        if (patassigneelinks == null) patassigneelinks = new ArrayList<String>();
        this.patassigneelinks.add(link);
    }

    public String getDerwent() {
        return derwent;
    }
    public void setDerwent(String derwent) {
        this.derwent = derwent;
    }
    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPap() {
        return pap;
    }

    public void setPap(String pap) {
        this.pap = pap;
    }

    public String getPapim() {
        return papim;
    }
    public void setPapim(String papim) {
        this.papim = papim;
    }
    public List<String> getPim() {
        return pim;
    }
    public void addPim(String link) {
        if (pim == null) pim = new ArrayList<String>();
        this.pim.add(link);
    }
    public String getPinfo() {
        return pinfo;
    }

    public void setPinfo(String pinfo) {
        this.pinfo = pinfo;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getNv() {
        return nv;
    }

    public void setNv(String nv) {
        this.nv = nv;
    }

    public String getKd() {
        return kd;
    }

    public void setKd(String kd) {
        this.kd = kd;
    }

    public String getPfd() {
        return pfd;
    }

    public void setPfd(String pfd) {
        this.pfd = pfd;
    }

    public String getPidd() {
        return pidd;
    }

    public void setPidd(String pidd) {
        this.pidd = pidd;
    }

    public String getPpd() {
        return ppd;
    }

    public void setPpd(String ppd) {
        this.ppd = ppd;
    }

    public String getLa() {
        return la;
    }

    public void setLa(String la) {
        this.la = la;
    }

    public String getNf() {
        return nf;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public String getAv() {
        return av;
    }

    public void setAv(String av) {
        this.av = av;
    }

    public String getSc() {
        return sc;
    }
    public void setSc(String sc) {
        this.sc = sc;
    }
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCopa() {
        return copa;
    }

    public void setCopa(String copa) {
        this.copa = copa;
    }

    public CitedBy getCitedby() {
        return citedby;
    }

    public void setCitedby(CitedBy citedby) {
        this.citedby = citedby;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    public String getAbstractlink() {
        return abstractlink;
    }

    public void setAbstractlink(String abstractlink) {
        this.abstractlink = abstractlink;
    }

    public String getDetailedlink() {
        return detailedlink;
    }

    public void setDetailedlink(String detailedlink) {
        this.detailedlink = detailedlink;
    }

    public String getBookdetailslink() {
        return bookdetailslink;
    }

    public void setBookdetailslink(String bookdetailslink) {
        this.bookdetailslink = bookdetailslink;
    }

    public String getPagedetailslink() {
        return pagedetailslink;
    }

    public void setPagedetailslink(String pagedetailslink) {
        this.pagedetailslink = pagedetailslink;
    }

    public String getReadchapterlink() {
        return readchapterlink;
    }

    public void setReadchapterlink(String readchapterlink) {
        this.readchapterlink = readchapterlink;
    }

    public String getReadpagelink() {
        return readpagelink;
    }

    public void setReadpagelink(String readpagelink) {
        this.readpagelink = readpagelink;
    }

    public AbstractRecord getAbstractrecord() {
        return abstractrecord;
    }

    public void setAbstractrecord(AbstractRecord theabstract) {
        this.abstractrecord = theabstract;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPagespp() {
        return pagespp;
    }

    public void setPagespp(String pagespp) {
        this.pagespp = pagespp;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public String getUpd() {
        return upd;
    }

    public void setUpd(String upd) {
        this.upd = upd;
    }
    public String getCf() {
        return cf;
    }
    public void setCf(String cf) {
        this.cf = cf;
    }
    public String getMd() {
        return md;
    }
    public void setMd(String md) {
        this.md = md;
    }
    public String getMl() {
        return ml;
    }
    public void setMl(String ml) {
        this.ml = ml;
    }
    public String getCc() {
        return cc;
    }
    public void setCc(String cc) {
        this.cc = cc;
    }
    public String getIs() {
        return is;
    }
    public void setIs(String is) {
        this.is = is;
    }
    public int getNr() {
        return nr;
    }
    public void setNr(int nr) {
        this.nr = nr;
    }
    public String getPla() {
        return pla;
    }
    public void setPla(String pla) {
        this.pla = pla;
    }
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    public String getDiscipline() {
        return discipline;
    }
    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
    public String getAuthcd() {
        return authcd;
    }
    public void setAuthcd(String authcd) {
        this.authcd = authcd;
    }
    public String getDupids() {
        return dupids;
    }
    public void setDupids(String dupids) {
        this.dupids = dupids;
    }
    
    public String getBimgfullpath() {
        return bimgfullpath;
    }
    public void setBimgfullpath(String bimgfullpath) {
        this.bimgfullpath = bimgfullpath;
    }
    public String getBpc() {
        return bpc;
    }
    public void setBpc(String bpc) {
        this.bpc = bpc;
    }
    public String getToc() {
        return toc;
    }
    public void setToc(String toc) {
        this.toc = toc;
    }
    public String getReadbooklink() {
        return readbooklink;
    }
    public void setReadbooklink(String readbooklink) {
        this.readbooklink = readbooklink;
    }
    public String getPl() {
        return pl;
    }
    public void setPl(String pl) {
        this.pl = pl;
    }
    public String getBst() {
        return bst;
    }
    public void setBst(String bst) {
        this.bst = bst;
    }
    public String getPr() {
        return pr;
    }
    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getDocumentbaskethitindex() {
        return documentbaskethitindex;
    }
    public void setDocumentbaskethitindex(String documentbaskethitindex) {
        this.documentbaskethitindex = documentbaskethitindex;
    }
    
    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    
    public String getCos() {
        return cos;
    }
    public void setCos(String cos) {
        this.cos = cos;
    }
    public String getTt() {
        return tt;
    }
    
    public String getTitleOfTranslationNoHighlight() {
        return HitHighlightFinisher.removeMarkup(tt);
    }
    
    public void setTt(String tt) {
        this.tt = tt;
    }
    public List<OtherAffil> getOtherAffils() {
        return otherAffils;
    }
    
    public void addOtherAffils(OtherAffil otherAffil) {
        if (otherAffils == null) otherAffils = new ArrayList<OtherAffil>();
        otherAffils.add(otherAffil);
    }
    public List<Category> getCategory() {
        return category;
    }
    
    public void addCategory(Category category) {
        if (this.category == null) this.category = new ArrayList<Category>();
        this.category.add(category);
    }
    
    public String getIllus() {
        return illus;
    }
    public void setIllus(String illus) {
        this.illus = illus;
    }
    public String getAt() {
        return at;
    }
    public void setAt(String at) {
        this.at = at;
    }
    public String getGurl() {
        return gurl;
    }
    public void setGurl(String gurl) {
        this.gurl = gurl;
    }
    public String getAnt() {
        return ant;
    }
    public void setAnt(String ant) {
        this.ant = ant;
    }
    public String getCac() {
        return cac;
    }
    public void setCac(String cac) {
        this.cac = cac;
    }
    public String getNtispricecode() {
        return ntispricecode;
    }
    public void setNtispricecode(String ntispricecode) {
        this.ntispricecode = ntispricecode;
    }
    public String getVi() {
        return vi;
    }
    public void setVi(String vi) {
        this.vi = vi;
    }
    public String getTa() {
        return ta;
    }
    public void setTa(String ta) {
        this.ta = ta;
    }
    public String getCo() {
        return co;
    }
    public void setCo(String co) {
        this.co = co;
    }
    public String getSu() {
        return su;
    }
    public void setSu(String su) {
        this.su = su;
    }
    public String getCts() {
        return cts;
    }
    public void setCts(String cts) {
        this.cts = cts;
    }
    public String getRpgm() {
        return rpgm;
    }
    public void setRpgm(String rpgm) {
        this.rpgm = rpgm;
    }
    public String getMed() {
        return med;
    }
    public void setMed(String med) {
        this.med = med;
    }
    public String getPc() {
        return pc;
    }
    public void setPc(String pc) {
        this.pc = pc;
    }
    public String getLstm() {
        return lstm;
    }
    public void setLstm(String lstm) {
        this.lstm = lstm;
    }
    public String getCaf() {
        return caf;
    }
    public void setCaf(String caf) {
        this.caf = caf;
    }
    public String getKc() {
        return kc;
    }
    public void setKc(String kc) {
        this.kc = kc;
    }
    public String getAtt() {
        return att;
    }
    public void setAtt(String att) {
        this.att = att;
    }
    public String getPapx() {
        return papx;
    }
    public void setPapx(String papx) {
        this.papx = papx;
    }
    
    public List<String> getFieldofsearch() {
        return fieldofsearch;
    }

    public void addFieldofsearch(String link) {
        if (fieldofsearch == null) fieldofsearch = new ArrayList<String>();
        this.fieldofsearch.add(link);
    }
    
    public List<String> getPrimaryexaminer() {
        return primaryexaminer;
    }

    public void addPrimaryexaminer(String link) {
        if (primaryexaminer == null) primaryexaminer = new ArrayList<String>();
        this.primaryexaminer.add(link);
    }
    public String getAe() {
        return ae;
    }
    public void setAe(String ae) {
        this.ae = ae;
    }
    public String getPanus() {
        return panus;
    }
    public void setPanus(String panus) {
        this.panus = panus;
    }
    public String getCpxlink() {
        return cpxlink;
    }
    public void setCpxlink(String cpxlink) {
        this.cpxlink = cpxlink;
    }
    public String getInspeclink() {
        return inspeclink;
    }
    public void setInspeclink(String inspeclink) {
        this.inspeclink = inspeclink;
    }
    public String getArticlenumber() {
        return articlenumber;
    }
    public void setArticlenumber(String articlenumber) {
        this.articlenumber = articlenumber;
    }
    public String getFttj() {
        return fttj;
    }
    public void setFttj(String fttj) {
        this.fttj = fttj;
    }
    public String getChi() {
        return chi;
    }
    public void setChi(String chi) {
        this.chi = chi;
    }
    public String getTtj() {
        return ttj;
    }
    public void setTtj(String ttj) {
        this.ttj = ttj;
    }
    public String getSnt() {
        return snt;
    }
    public void setSnt(String snt) {
        this.snt = snt;
    }
    public String getCnt() {
        return cnt;
    }
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
    public String getCpubt() {
        return cpubt;
    }
    public void setCpubt(String cpubt) {
        this.cpubt = cpubt;
    }
    public String getNdi() {
        return ndi;
    }
    public void setNdi(String ndi) {
        this.ndi = ndi;
    }
    public String getVolt() {
        return volt;
    }
    public void setVolt(String volt) {
        this.volt = volt;
    }
    public String getIsst() {
        return isst;
    }
    public void setIsst(String isst) {
        this.isst = isst;
    }
    public String getIpnt() {
        return ipnt;
    }
    public void setIpnt(String ipnt) {
        this.ipnt = ipnt;
    }
    public String getTdate() {
        return tdate;
    }
    public void setTdate(String tdate) {
        this.tdate = tdate;
    }
    public String getPnum() {
        return pnum;
    }
    public void setPnum(String pnum) {
        this.pnum = pnum;
    }
    public String getTnum() {
        return tnum;
    }
    public void setTnum(String tnum) {
        this.tnum = tnum;
    }
    public String getAgs() {
        return ags;
    }
    public void setAgs(String ags) {
        this.ags = ags;
    }
    public String getMlt() {
        return mlt;
    }
    public void setMlt(String mlt) {
        this.mlt = mlt;
    }
    public String getAtm() {
        return atm;
    }
    public void setAtm(String atm) {
        this.atm = atm;
    }
    public String getLth() {
        return lth;
    }
    public void setLth(String lth) {
        this.lth = lth;
    }
    public String getPapd() {
        return papd;
    }
    public void setPapd(String papd) {
        this.papd = papd;
    }
    public String getPans() {
        return pans;
    }
    public void setPans(String pans) {
        this.pans = pans;
    }
    public String getPapco() {
        return papco;
    }
    public void setPapco(String papco) {
        this.papco = papco;
    }
    public List<Author> getInventors() {
        return inventors;
    }
    public void setInventors(List<Author> inventors) {
        this.inventors = inventors;
    }
    public void addInventors(Author author) {
        if (inventors == null) {
            inventors = new ArrayList<Author>();
        }
        inventors.add(author);
    }
    public String getRil() {
        return ril;
    }
    public void setRil(String ril) {
        this.ril = ril;
    }
    public String getRsp() {
        return rsp;
    }
    public void setRsp(String rsp) {
        this.rsp = rsp;
    }
    public String getIpn() {
        return ipn;
    }
    public void setIpn(String ipn) {
        this.ipn = ipn;
    }
    public String getPpn() {
        return ppn;
    }
    public void setPpn(String ppn) {
        this.ppn = ppn;
    }
    public String getPm1() {
        return pm1;
    }
    public void setPm1(String pm1) {
        this.pm1 = pm1;
    }
    public String getPi() {
        return pi;
    }
    public void setPi(String pi) {
        this.pi = pi;
    }
    public String getIpc() {
        return ipc;
    }
    public void setIpc(String ipc) {
        this.ipc = ipc;
    }
    public String getEcl() {
        return ecl;
    }
    public void setEcl(String ecl) {
        this.ecl = ecl;
    }
    public String getCpub() {
        return cpub;
    }
    public void setCpub(String cpub) {
        this.cpub = cpub;
    }
    public List<String> getDt() {
        return dt;
    }
    
    public void addDt(String link) {
        if (dt == null) dt = new ArrayList<String>();
        this.dt.add(link);
    }
    public String getStt() {
        return stt;
    }
    public void setStt(String stt) {
        this.stt = stt;
    }
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }

    public void clear() {
        if (this.getAuthors() != null) {
            for (Author a : this.getAuthors()) {
                if (a.getAffils() != null) {
                    a.getAffils().clear();
                }
            }
            
            this.getAuthors().clear();
        }
        if (this.getAffils() != null) {
            this.getAffils().clear();
        }
    }

    public String getIsbnlink() {
        return isbnlink;
    }

    public void setIsbnlink(String isbnlink) {
        this.isbnlink = isbnlink;
    }

    public String getIsbn13link() {
        return isbn13link;
    }

    public void setIsbn13link(String isbn13link) {
        this.isbn13link = isbn13link;
    }
	public String getPii() {
		return pii;
	}
	public void setPii(String pii) {
		this.pii = pii;
	}
}
