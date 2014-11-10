package org.ei.thesaurus;

import org.ei.domain.ClassificationID;


public interface ThesaurusRecord
    extends ThesaurusNode
{


    public ThesaurusRecordID getRecID();

    public void setStatus(String status);

    public String getStatus();

    public void setScopeNotes(String scopeNotes);

    public String getScopeNotes();
    
    public void setSearchInfo(String searchInfo);

    public String getSearchInfo();

    public String getDateOfIntro();

    public void setHistoryScopeNotes(String historyScopeNotes);

    public String getHistoryScopeNotes();

    public void setCoordinates(String coordinates);

    public String getCoordinates();

    public void setType(String type);

    public String getType();

    public String getTranslatedType();

    public void setUseTerms(ThesaurusPage useTerms);

    public void setUserTermAndOrFlag(String userTermAndOrFlag);
    public String getUserTermAndOrFlag();

    public ThesaurusPage getUseTerms()
        throws ThesaurusException;

    public int countUseTerms();

    public void setLeadinTerms(ThesaurusPage leadinTerms);

    public ThesaurusPage getLeadinTerms()
        throws ThesaurusException;

    public int countLeadinTerms();

    public void setNarrowerTerms(ThesaurusPage narrowerTerms);

    public ThesaurusPage getNarrowerTerms()
        throws ThesaurusException;

    public int countNarrowerTerms();

    public void setBroaderTerms(ThesaurusPage broaderTerms);

    public ThesaurusPage getBroaderTerms()
        throws ThesaurusException;

    public int countBroaderTerms();

    public void setRelatedTerms(ThesaurusPage relatedTerms);

    public ThesaurusPage getRelatedTerms()
        throws ThesaurusException;

    public int countRelatedTerms();

    public void setTopTerms(ThesaurusPage topTerms);

    public ThesaurusPage getTopTerms()
        throws ThesaurusException;

    public int countTopTerms();

    public void setPriorTerms(ThesaurusPage priorTerms);

    public ThesaurusPage getPriorTerms()
        throws ThesaurusException;

    public void setClassificationIDs(ClassificationID[] cid);

    public ClassificationID[] getClassificationIDs();

    public void setDateOfIntro(String dateOfIntro);

    public boolean hasInfo();




}