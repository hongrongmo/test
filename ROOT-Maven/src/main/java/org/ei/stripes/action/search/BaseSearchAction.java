package org.ei.stripes.action.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.config.EVProperties;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.ISearchForm;
import org.ei.domain.Query;
import org.ei.domain.SearchForm;
import org.ei.stripes.action.EVActionBean;

/**
 * This is the base class for search-related actions.  It mostly
 * contains fields for the search state:  start/end year, limiters,
 * etc.  Separating it here allows us to re-use across both the
 * Search display actions and the Search results actions.
 * 
 * @author harovetm
 *
 */
public abstract class BaseSearchAction extends EVActionBean implements ISearchForm {

	// Search ID (used when editing search)
	protected String searchid;

	// "All" database checkbox
	protected String alldb;
	
    //This variable for  last four updates only
    protected String updatesno="";

    //This variable for email alert week
    protected String emailalertweek;

	// Selected values for above (edit search)
    protected String doctype;
    protected String disciplinetype;
    protected String treatmentType;
    protected String language;

    // These variable to hold search words and search options
    protected String searchWord1="";
    protected String searchWord2="";
    protected String searchWord3="";
    protected String searchWords[];
    protected String boolean1="";
    protected String boolean2="";
    protected String booleans[];
    protected String section1="";
    protected String section2="";
    protected String section3="";
    protected String sections[];
    protected String autostem="";

    protected String sort="";
    protected String sortdir="";

    // eBooks only
    protected String allcol;
    protected String[] col;
    

    protected String yearRange="";
    protected String yearselect="yearrange";
    protected String startYear="";
    protected String folderid="";
    protected String folderName="";
    protected String folderSize="";
    
    protected String useType = "";
    
	// This will become the default selected end year since it is ouput in //SESSION-DATA/END-YEAR
    protected String endYear= String.valueOf(SearchForm.ENDYEAR);
    private List<String> pageCountOption= new ArrayList<String>();
    private String limitError;
    private String savedSeachesAndAlertsLimit;
    private boolean isnavchrt;
    
   // Flag to indicate if search results are duplicate-eligible.
    protected boolean removeduplicates;
    
    
    	/**
	 * This method removes non-hosted databases (USPTO and CRC)
	 * and backfiles from mask value
	 * 
	 * @param mask The user's DB mask
	 * @return int value suitable for all checkbox
	 */
	// this class removes non-hosted databases (USPTO and CRC)
	// and backfiles from mask value
	public static int getScrubbedMask(int mask) {
		int exmasks[] = { DatabaseConfig.USPTO_MASK, DatabaseConfig.CRC_MASK,
				DatabaseConfig.C84_MASK, DatabaseConfig.IBF_MASK };

		for (int idx = 0; idx < exmasks.length; idx++) {
			if ((mask & exmasks[idx]) == exmasks[idx]) {
				mask -= exmasks[idx];
			}
		}

		return mask;
	}
	
    /**
     * Populate the search form from a Query object
     * 
     * @param query
     */
    protected void populateSearchFormFields(Query query) {
        setSearchid(query.getID());
        setSessionid(query.getSessionID());
        setSearchWord1(query.getSeaPhr1());
        setSearchWord2(query.getSeaPhr2());
        setSearchWord3(query.getSeaPhr3());
        setSearchWords(query.getSearchWords());
        setBoolean1(query.getBool1());
        setBoolean2(query.getBool2());
        setBooleans(query.getBooleans());
        setSection1(query.getSeaOpt1());
        setSection2(query.getSeaOpt2());
        setSection3(query.getSeaOpt3());
        setSections(query.getSections());
        String lastfourupdates = query.getLastFourUpdates();
        if (!GenericValidator.isBlankOrNull(lastfourupdates)) {
            setUpdatesNo(lastfourupdates);
            setYearselect("lastupdate");
        }
        setEmailalertweek(query.getEmailAlertWeek());
        setDoctype(query.getDocumentType());
        setTreatmentType(query.getTreatmentType());
        setLanguage(query.getLanguage());
        setDisciplinetype(query.getDisciplineType());
        setSort(query.getSortOption().getSortField());
        setStartYear(query.getStartYear());
        setEndYear(query.getEndYear());
        String qryAutoStem = query.getAutoStemming();
        if(!GenericValidator.isBlankOrNull(qryAutoStem)){
            if("on".equalsIgnoreCase(qryAutoStem)){
                setAutostem("false");   
            }else{
                setAutostem("true");
            }
            
        }else{
            setAutostem(qryAutoStem);
        }
        
    }

	//
	//
	// GETTERS/SETTERS
	//
	//
	
	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

	public String getAlldb() {
		return alldb;
	}

	public void setAlldb(String alldb) {
		this.alldb = alldb;
	}

	public String getUpdatesNo() {
		return updatesno;
	}

	public void setUpdatesNo(String updatesNo) {
		this.updatesno = updatesNo;
	}


	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getDisciplinetype() {
		return disciplinetype;
	}

	public void setDisciplinetype(String disciplinetype) {
		this.disciplinetype = disciplinetype;
	}

	public String getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(String treatmentType) {
		this.treatmentType = treatmentType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortdir() {
		return sortdir;
	}

	public void setSortdir(String sortdir) {
		this.sortdir = sortdir;
	}

	public String getSearchWord1() {
		return searchWord1;
	}

	public void setSearchWord1(String searchWord1) {
		this.searchWord1 = searchWord1;
	}

	public String getSearchWord2() {
		return searchWord2;
	}

	public void setSearchWord2(String searchWord2) {
		this.searchWord2 = searchWord2;
	}

	public String getSearchWord3() {
		return searchWord3;
	}

	public void setSearchWord3(String searchWord3) {
		this.searchWord3 = searchWord3;
	}

	public String getBoolean1() {
		return boolean1;
	}

	public void setBoolean1(String boolean1) {
		this.boolean1 = boolean1;
	}

	public String getBoolean2() {
		return boolean2;
	}

	public void setBoolean2(String boolean2) {
		this.boolean2 = boolean2;
	}

	public String getSection1() {
		return section1;
	}

	public void setSection1(String section1) {
		this.section1 = section1;
	}

	public String getSection2() {
		return section2;
	}

	public void setSection2(String section2) {
		this.section2 = section2;
	}

	public String getSection3() {
		return section3;
	}

	public void setSection3(String section3) {
		this.section3 = section3;
	}

	public String getAutostem() {
		return autostem;
	}

	public void setAutostem(String autostem) {
		this.autostem = autostem;
	}

	public String getyearRange() {
		return yearRange;
	}

	public void setyearRange(String yearRange) {
		this.yearRange = yearRange;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}


	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getYearselect() {
		return yearselect;
	}

	public void setYearselect(String yearselect) {
		this.yearselect = yearselect;
	}

	public String[] getSearchWords() {
		return searchWords;
	}

	public void setSearchWords(String[] searchWords) {
		this.searchWords = searchWords;
	}

	public void setSearchWords(String searchWords) {
		this.searchWords = searchWords.split("\\"+Query.TERM_SEPARATOR);
	}

	public String[] getBooleans() {
		return booleans;
	}

	public void setBooleans(String[] booleans) {
		this.booleans = booleans;
	}

	public void setBooleans(String booleans) {
		this.booleans = booleans.split("\\"+Query.TERM_SEPARATOR);
	}

	public String[] getSections() {
		return sections;
	}

	public void setSections(String[] sections) {
		this.sections = sections;
	}

	public void setSections(String sections) {
		this.sections = sections.split("\\"+Query.TERM_SEPARATOR);
	}

	public String getAllcol() {
		return allcol;
	}

	public void setAllcol(String allcol) {
		this.allcol = allcol;
	}

	public String[] getCol() {
		return col;
	}

	public void setCol(String[] col) {
		this.col = col;
	}

	public String getEmailalertweek() {
		return emailalertweek;
	}

	public void setEmailalertweek(String emailalertweek) {
		this.emailalertweek = emailalertweek;
	}

	public String getFolderid() {
		return folderid;
	}

	public void setFolderid(String folderid) {
		this.folderid = folderid;
	}
	

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public List<String> getPageCountOption() {
		return pageCountOption;
	}

	public void addPageCountOption(String pageCountOption) {
		getPageCountOption().add(pageCountOption);
	}

	public String getFolderSize() {
		return folderSize;
	}

	public void setFolderSize(String folderSize) {
		this.folderSize = folderSize;
	}

	public String getLimitError() {
		return limitError;
	}
	
	public void setLimitError(String limitError) {
		this.limitError = limitError;
	}
	public String getSavedSeachesAndAlertsLimit() {
		 return EVProperties.getRuntimeProperty("SAVED_SERCHES_ALERTS_LIMIT");
	}

	public boolean isIsnavchrt() {
		return isnavchrt;
	}

	public void setIsnavchrt(boolean isnavchrt) {
		this.isnavchrt = isnavchrt;
	}

    public boolean isRemoveduplicates() {
        return removeduplicates;
    }

    public void setRemoveduplicates(boolean removeduplicates) {
        this.removeduplicates = removeduplicates;
    }
    
	public String getUseType() {
		return useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	
}
