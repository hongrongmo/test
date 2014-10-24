package org.ei.thesaurus;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;

import org.ei.data.georef.runtime.GRFDataDictionary;
import org.ei.domain.ClassificationID;

public class ThesaurusRecordImpl implements ThesaurusRecord {

    private ThesaurusRecordID recID;
    private String status;
    private String scopeNotes;
    private String dateOfIntro;
    private String historyScopeNotes;
    private String coordinates;
    private String type;
    private String useTermAndOrFlag;
    private String searchInfo;
    private String registryNumber;
    private String registryNumberBroaderTerm;
    private ThesaurusPage seeAlso;
    private ThesaurusPage see;
    private ThesaurusPage useTerms;
    private ThesaurusPage leadinTerms;
    private ThesaurusPage narrowerTerms;
    private ThesaurusPage chemicalAspect;
    private ThesaurusPage broaderTerms;
    private ThesaurusPage relatedTerms;
    private ThesaurusPage topTerms;
    private ThesaurusPage priorTerms;
    private ClassificationID[] classificationIDs;

    public void accept(ThesaurusNodeVisitor v) throws ThesaurusException {
        v.visitWith(this);
    }

    public ThesaurusRecordImpl(ThesaurusRecordID recID) {
        this.recID = recID;
    }

    public ThesaurusRecordID getRecID() {
        return this.recID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setScopeNotes(String scopeNotes) {
        this.scopeNotes = scopeNotes;
    }

    public String getScopeNotes() {
        return this.scopeNotes;
    }

    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }

    public String getSearchInfo() {
        return this.searchInfo;
    }

    public void setHistoryScopeNotes(String historyScopeNotes) {
        this.historyScopeNotes = historyScopeNotes;
    }

    public String getHistoryScopeNotes() {
        return this.historyScopeNotes;
    }

    public void setUseTerms(ThesaurusPage useTerms) {
        this.useTerms = useTerms;
    }

    public void setUserTermAndOrFlag(String useTermAndOrFlag) {
        this.useTermAndOrFlag = useTermAndOrFlag;
    }

    public ThesaurusPage getUseTerms() {
        return this.useTerms;
    }

    public String getUserTermAndOrFlag() {
        return this.useTermAndOrFlag;
    }

    public int countUseTerms() {
        return useTerms.size();
    }

    public void setLeadinTerms(ThesaurusPage leadinTerms) {
        this.leadinTerms = leadinTerms;
    }

    public ThesaurusPage getLeadinTerms() {
        return this.leadinTerms;
    }

    public int countLeadinTerms() {
        return leadinTerms.size();
    }

    public void setNarrowerTerms(ThesaurusPage narrowerTerms) {
        this.narrowerTerms = narrowerTerms;
    }

    public int countNarrowerTerms() {
        return narrowerTerms.size();
    }

    public ThesaurusPage getNarrowerTerms() {
        return this.narrowerTerms;
    }

    public void setChemicalAspects(ThesaurusPage chemicalAspect) {
        this.chemicalAspect = chemicalAspect;
    }

    public int countChemicalAspects() {
        return chemicalAspect.size();
    }

    public ThesaurusPage getChemicalAspects() {
        return this.chemicalAspect;
    }

    public void setBroaderTerms(ThesaurusPage broaderTerms) {
        this.broaderTerms = broaderTerms;
    }

    public int countBroaderTerms() {
        return broaderTerms.size();
    }

    public ThesaurusPage getBroaderTerms() {
        return this.broaderTerms;
    }

    public void setRelatedTerms(ThesaurusPage relatedTerms) {
        this.relatedTerms = relatedTerms;
    }

    public int countRelatedTerms() {
        return relatedTerms.size();
    }

    public ThesaurusPage getRelatedTerms() {
        return this.relatedTerms;
    }

    public void setTopTerms(ThesaurusPage topTerms) {
        this.topTerms = topTerms;
    }

    public int countTopTerms() {
        return topTerms.size();
    }

    public ThesaurusPage getTopTerms() {
        return this.topTerms;
    }

    public void setPriorTerms(ThesaurusPage priorTerms) {
        this.priorTerms = priorTerms;
    }

    public ThesaurusPage getPriorTerms() {
        return this.priorTerms;
    }

    public void setClassificationIDs(ClassificationID[] classificationIDs) {
        this.classificationIDs = classificationIDs;
    }

    public ClassificationID[] getClassificationIDs() {
        return this.classificationIDs;
    }

    public void setDateOfIntro(String dateOfIntro) {
        this.dateOfIntro = dateOfIntro;
    }

    public String getDateOfIntro() {
        return this.dateOfIntro;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCoordinates() {
        return this.coordinates;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setRegistryNumber(String registryNumber) {
        this.registryNumber = registryNumber;
    }

    public String getRegistryNumber() {
        return this.registryNumber;
    }

    public void setRegistryNumberBroaderTerm(String registryNumberBroaderTerm) {
        this.registryNumberBroaderTerm = registryNumberBroaderTerm;
    }

    public String getRegistryNumberBroaderTerm() {
        return this.registryNumberBroaderTerm;
    }

    public void setSeeAlso(ThesaurusPage seeAlso) {
        this.seeAlso = seeAlso;
    }

    public ThesaurusPage getSeeAlso() {
        return this.seeAlso;
    }

    public void setSee(ThesaurusPage see) {
        this.see = see;
    }

    public ThesaurusPage getSee() {
        return this.see;
    }

    public String getTranslatedType() {
        StringBuffer translatedVal = new StringBuffer();
        if (this.type != null) {
            StringTokenizer stk = new StringTokenizer(this.type, ";");
            while (stk.hasMoreTokens()) {
                String curToken = (String) stk.nextElement();
                GRFDataDictionary gdd = GRFDataDictionary.getInstance();
                Map tt = gdd.getThesaurusTranslatedTermType();
                translatedVal.append((String) tt.get(curToken));
                if (stk.hasMoreTokens()) {
                    translatedVal.append("; ");
                }
            }

            return translatedVal.toString();
        } else
            return null;
    }

    public boolean hasInfo() {

        if ((scopeNotes != null) || (searchInfo != null) || (dateOfIntro != null) || (classificationIDs != null) || (coordinates != null) || (type != null)) {
            return true;
        } else {
            return false;
        }

    }

    public void toXML(Writer out) throws IOException {

    }

}