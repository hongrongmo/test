package org.ei.common.georef;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.domain.Citation;
import org.ei.domain.Key;
import org.ei.domain.Keys;


public class CitationView extends DocumentView {

    private static final List<String> fields = Arrays.asList(new String[]{
        "M_ID",
        "ISSN",
        "ISBN",
        "EISSN",
        "ID_NUMBER",
        "TITLE_OF_SERIAL",
        "VOLUME_ID",
        "ISSUE_ID",
        "TITLE_OF_ANALYTIC",
        "TITLE_OF_MONOGRAPH",
        "TITLE_OF_COLLECTION",
        "PERSON_ANALYTIC",
        "PERSON_MONOGRAPH",
        "PERSON_COLLECTION",
        "AUTHOR_AFFILIATION",
        "AUTHOR_AFFILIATION_ADDRESS",
        "AUTHOR_AFFILIATION_COUNTRY",
        "DATE_OF_PUBLICATION",
        "DATE_OF_MEETING",
        "LANGUAGE_TEXT",
        "AVAILABILITY",
        "LOAD_NUMBER",
        "COPYRIGHT",
        "DOI",
        "DOCUMENT_TYPE",
        "BIBLIOGRAPHIC_LEVEL_CODE",
        "COLLATION_ANALYTIC",
        "COLLATION_MONOGRAPH",
        "COLLATION_COLLECTION"});

    private static final Map<Key, String> keymap = new HashMap<Key, String>() {
                                                     {
                                                         put(Keys.AUTHORS, "");
                                                         put(Keys.AUTHOR_AFFS, "");
                                                         put(Keys.COPYRIGHT, "");
                                                         put(Keys.COPYRIGHT_TEXT, "");
                                                         put(Keys.DOC_TYPE, "");
                                                         put(Keys.DOCID, "");
                                                         put(Keys.EDITORS, "");
                                                         put(Keys.ISSN, "");
                                                         put(Keys.E_ISSN, "");
                                                         put(Keys.ISSUE_DATE, "");
                                                         put(Keys.LANGUAGE, "");
                                                         put(Keys.MONOGRAPH_TITLE, "");
                                                         put(Keys.NO_SO, "");
                                                         put(Keys.PAGE_RANGE, "");
                                                         put(Keys.PUBLICATION_DATE, "");
                                                         put(Keys.PUBLICATION_YEAR, "");
                                                         put(Keys.SOURCE, "");
                                                         put(Keys.TITLE, "");
                                                         put(Keys.VOLISSUE, "");
                                                         put(Keys.VOLUME, "");
                                                         put(Keys.ISSUE, "");
                                                         put(Keys.ACCESSION_NUMBER, "");
                                                         put(Keys.DOI, "");
                                                         put(Keys.DOC_TYPE, "");
                                                         put(Keys.CITEDBY, "");
                                                     }
                                                 };

    public Map<Key, String> getKeyMap() {
        return keymap;
    }

    public List<String> getFields() {
        return fields;
    }

    public boolean exportLabels() {
        return true;
    }

    public String getFormat() {
        return Citation.CITATION_FORMAT;
    }


    public String getLanguage() {
        String strlang = super.getLanguage();
        return new CitationAbstractLanguageDecorator(strlang).getValue();
    }
}
