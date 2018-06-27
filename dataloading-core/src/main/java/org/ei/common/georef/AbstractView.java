package org.ei.common.georef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.domain.Abstract;
import org.ei.domain.Key;
import org.ei.domain.Keys;


public class AbstractView extends DocumentView {

    private static final List<String> fields = Arrays.asList(new String[]{"ABSTRACT",
        "COORDINATES",
        "CODEN",
        "INDEX_TERMS",
        "UNCONTROLLED_TERMS",
        "REPORT_NUMBER",
        "DOCUMENT_TYPE",
        "BIBLIOGRAPHIC_LEVEL_CODE",
        "NAME_OF_MEETING"});

    private static final Map<Key, String> keymap = new HashMap<Key, String>() {
                                                     {
                                                         put(Keys.REPORT_NUMBER, "");
                                                         put(Keys.ABSTRACT, "");
                                                         put(Keys.CLASS_CODES, "");
                                                         put(Keys.CODEN, "");
                                                         put(Keys.CONFERENCE_NAME, "");
                                                         put(Keys.CONF_DATE, "");
                                                         put(Keys.EDITOR_AFFS, "");
                                                         put(Keys.INDEX_TERM, "");
                                                         put(Keys.UNCONTROLLED_TERMS, "");
                                                         put(Keys.MAIN_HEADING, "");
                                                         put(Keys.PROVIDER, "");
                                                         put(Keys.SPONSOR, "");
                                                         // put(Keys.COORDINATES,"");
                                                         put(Keys.LOCATIONS, "");
                                                         put(Keys.ISBN, "");
                                                         put(Keys.DOC_TYPE, "");

                                                         // HERE WE ARE APPENDING ALL OF THE CITATION VIEW KEYS
                                                         putAll((new CitationView()).getKeyMap());
                                                     }
                                                 };

    public Map<Key, String> getKeyMap() {
        return keymap;
    }

    public List<String> getFields() {
        List<String> abstractfields = new ArrayList<String>();
        abstractfields.addAll((new CitationView()).getFields());
        abstractfields.addAll(fields);
        return abstractfields;
    }

    public boolean exportLabels() {
        return true;
    }

    public String getFormat() {
        return Abstract.ABSTRACT_FORMAT;
    }

    public String getLanguage() {
        String strlang = super.getLanguage();
        return new CitationAbstractLanguageDecorator(strlang).getValue();
    }
}
