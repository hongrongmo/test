package org.ei.stripes.action;

public enum EVPathUrl {
	
    EV_HOME("/home.url"),
    EV_QUICK_SEARCH("/search/quick.url"),
    EV_EXPERT_SEARCH("/search/expert.url"),
    EV_EBOOK_SEARCH("/search/ebook.url"),
    EV_THES_SEARCH("/search/thesHome.url"),
    EV_THES_INIT_SEARCH("/search/thesHome.url#init");
    
    private final String m_value;

	private EVPathUrl(String str) {
		this.m_value = str;
	}

	public String value() {
		return m_value;
	}
}