package org.ei.domain;


public interface ISearchForm {
	public String getSearchid();
	public String getUpdatesNo();
	public String getDoctype();
	public String getDisciplinetype();
	public String getTreatmentType();
	public String getLanguage();
	public String getSort();
	public String getSortdir();
	public String getSearchWord1();
	public String getSearchWord2();
	public String getSearchWord3();
	public String[] getSearchWords();
	public String getBoolean1();
	public String getBoolean2();
	public String[] getBooleans();
	public String getSection1();
	public String getSection2();
	public String getSection3();
    public String[] getSections();
	public String getAutostem();
	public String getyearRange();
	public String getStartYear();
	public String getEndYear();
	public String getYearselect();
	public String getEmailalertweek();
	// eBook parms
	public String getAllcol();
	public String[] getCol();
}
