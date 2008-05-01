package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.ei.domain.Citation;


    public class CitationView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"M_ID",
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
                                                        "PUBLISHER",
                                                        "PUBLISHER_ADDRESS",
                                                        "AVAILABILITY",
                                                        "LOAD_NUMBER",
                                                        "COPYRIGHT",
                                                        "DOI",
                                                        "COLLATION_ANALYTIC",
                                                        "COLLATION_MONOGRAPH",
                                                        "COLLATION_COLLECTION"});
        private Map keymap = new HashMap() {
            {
                put(Keys.AUTHORS,"");
                put(Keys.AUTHOR_AFFS,"");
                put(Keys.COPYRIGHT,"");
                put(Keys.COPYRIGHT_TEXT,"");
                put(Keys.DOCID,"");
                put(Keys.EDITORS,"");
                put(Keys.ISSN,"");
                put(Keys.E_ISSN,"");
                put(Keys.ISSUE_DATE,"");
                put(Keys.LANGUAGE ,"");
                put(Keys.MONOGRAPH_TITLE,"");
                put(Keys.NO_SO,"");
                put(Keys.PAGE_RANGE,"");
                put(Keys.PUBLICATION_DATE,"");
                put(Keys.PUBLICATION_YEAR,"");
                put(Keys.PUBLISHER,"");
                put(Keys.SOURCE,"");
                put(Keys.TITLE,"");
                put(Keys.VOLISSUE,"");
                put(Keys.VOLUME,"");
                put(Keys.ISSUE,"");
                put(Keys.ACCESSION_NUMBER,"");
                put(Keys.DOI,"");
            }
        };

        public Map getKeyMap() {
          return keymap;
        }

        public List getFields()
        {
          return fields;
        }
        public boolean exportLabels() { return true; }
        public String getFormat() { return Citation.CITATION_FORMAT; }
}
