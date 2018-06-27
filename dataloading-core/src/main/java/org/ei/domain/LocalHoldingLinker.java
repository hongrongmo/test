package org.ei.domain;

public interface LocalHoldingLinker
{
    public static final String GENRE = "[GENRE]";
    public static final String AULAST = "[AULAST]";
    public static final String AUFIRST = "[AUFIRST]";
    public static final String ISSN = "[ISSN]";
    public static final String ISBN1 = "[ISBN1]";
    public static final String ISBN2 = "[ISBN2]";
    public static final String AUFULL = "[AUFULL]";
    public static final String ISSN9 = "[ISSN9]";
    public static final String EISSN = "[EISSN]";
    public static final String CODEN = "[CODEN]";
    public static final String TITLE = "[TITLE]";
    public static final String STITLE = "[STITLE]";
    public static final String ATITLE = "[ATITLE]";
    public static final String CTITLE = "[CTITLE]";
    public static final String VOLUME = "[VOLUME]";
	public static final String VOLUME2 = "[VOLUME2]";  //full volume number
	public static final String ISSUE = "[ISSUE]";
    public static final String ISSUE2 = "[ISSUE2]";	  //full issue number
    public static final String SPAGE = "[SPAGE]";
    public static final String EPAGE = "[EPAGE]";
    public static final String PAGES = "[PAGES]";
    public static final String ARTNUM = "[ARTNUM]";// The number of an individual item, in cases where there are no pages available.
    public static final String YEAR = "[YEAR]";
    public static final String ANUMBER = "[ANUMBER]";
    public static final String DOI = "[DOI]";

    public static final String[] TZ_LINK_LABELS = { "LINK_LABEL_1", "LINK_LABEL_2", "LINK_LABEL_3", "LINK_LABEL_4", "LINK_LABEL_5" };
    public static final String[] TZ_DYNAMIC_URLS = { "DYNAMIC_URL_1", "DYNAMIC_URL_2", "DYNAMIC_URL_3", "DYNAMIC_URL_4", "DYNAMIC_URL_5" };
    public static final String[] TZ_DEFAULT_URLS = { "DEFAULT_URL_1", "DEFAULT_URL_2", "DEFAULT_URL_3", "DEFAULT_URL_4", "DEFAULT_URL_5" };
    public static final String[] TZ_IMAGE_URLS = { "IMAGE_URL_1", "IMAGE_URL_2", "IMAGE_URL_3", "IMAGE_URL_4", "IMAGE_URL_5" };

    public static final String[] localHoldingFields = {GENRE,AULAST,AUFIRST ,AUFULL,ISSN,ISBN1,ISBN2,ISSN9,CODEN, TITLE,STITLE,ATITLE,CTITLE, VOLUME2,VOLUME,ISSUE2,ISSUE,SPAGE,EPAGE,PAGES,YEAR,ANUMBER,DOI,ARTNUM,EISSN};
    public String getLocalHoldingLink(String URL);
}