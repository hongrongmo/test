package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.ei.domain.Detail;
import org.ei.domain.FullDoc;

    public class DetailedView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"DOI",
                                                        "COORDINATES",
                                                        "URL",
                                                        "EDITION",
                                                        "MEDIUM_OF_SOURCE",
                                                        "SUMMARY_ONLY_NOTE",
                                                        "SOURCE_NOTE",
                                                        "CORPORATE_BODY_ANALYTIC",
                                                        "CORPORATE_BODY_MONOGRAPH",
                                                        "CORPORATE_BODY_COLLECTION",
                                                        "AVAILABILITY",
                                                        "BIBLIOGRAPHIC_LEVEL_CODE",
                                                        "ILLUSTRATION",
                                                        "ID_NUMBER",
                                                        "MAP_TYPE",
                                                        "MAP_SCALE",
                                                        "TARGET_AUDIENCE",
                                                        "HOLDING_LIBRARY",
                                                        "RESEARCH_PROGRAM",
                                                        "ANNOTATION",
                                                        "CATEGORY_CODE",
                                                        "DOCUMENT_TYPE",
                                                        "LOCATION_OF_MEETING",
                                                        "UNIVERSITY",
                                                        "TYPE_OF_DEGREE",
                                                        "AFFILIATION_SECONDARY",
                                                        "COUNTRY_OF_PUBLICATION",
                                                        "REPORT_NUMBER",
                                                        "NUMBER_OF_REFERENCES"});

        // Since the order of these keys dtermines their display on the Detailed record page
        // explicitly set the Key values here instead of comibing with Abstract or Citation classes
        // AND we also use a LinkedHashMap to guarantee iteration order
        private Map keymap = new LinkedHashMap() {
            {
                put(Keys.ACCESSION_NUMBER,"");
                put(Keys.TITLE,"");
                put(Keys.TITLE_TRANSLATION,"");
                put(Keys.AUTHORS,"");
                put(Keys.EDITORS,"");
                put(Keys.AUTHOR_AFFS,"");
                put(Keys.EDITOR_AFFS,"");
                put(GRFDocBuilder.AFFILIATION_OTHER,"");
                put(Keys.CORRESPONDENCE_PERSON,"");
                put(Keys.SERIAL_TITLE,"");
                put(Keys.ABBRV_SERIAL_TITLE,"");
                put(Keys.VOLUME,"");
                put(Keys.ISSUE,"");
                put(Keys.EDITION,"");
                put(Keys.MONOGRAPH_TITLE,"");
                put(Keys.COLLECTION_TITLE,"");
                put(Keys.REPORT_NUMBER,"");
                put(Keys.ISSUE_DATE,"");
                put(Keys.PUBLICATION_YEAR,"");
                put(Keys.PAGE_RANGE,"");
                put(Keys.PAGE_COUNT,"");
                put(Keys.ARTICLE_NUMBER,"");
                put(Keys.LANGUAGE,"");
                put(Keys.ISSN,"");
                put(Keys.E_ISSN,"");
                put(Keys.CODEN,"");
                //put(Keys.ISBN,"");
                put(GRFDocBuilder.MULTI_ISBN,"");
                put(Keys.DOC_TYPE,"");
                put(GRFDocBuilder.GRF_URLS,"");
                put(GRFDocBuilder.SOURCE_MEDIUM,"");
                put(GRFDocBuilder.SOURCE_NOTE,"");
                put(GRFDocBuilder.DEGREE_TYPE,"");
                put(GRFDocBuilder.UNIVERSITY,"");
                put(GRFDocBuilder.RESEARCH_PROGRAM,"");
                put(Keys.SOURCE_COUNTRY,"");
                put(Keys.CONFERENCE_NAME,"");
                put(Keys.CONF_DATE,"");
                put(Keys.MEETING_LOCATION,"");
                put(Keys.CONF_CODE,"");
                put(Keys.SPONSOR,"");
                put(Keys.PUBLISHER,"");
                put(Keys.RSRCH_SPONSOR,"");
                //put(Keys.COUNTRY_OF_PUB,"");
                put(Keys.ABSTRACT,"");
                put(Keys.ABSTRACT_TYPE,"");
                put(Keys.AVAILABILITY,"");
                put(Keys.NUMBER_OF_REFERENCES,"");
                //put(GRFDocBuilder.SUMMARY_ONLY_NOTE,"");
                put(GRFDocBuilder.ANNOTATION,"");
                put(GRFDocBuilder.ILLUSTRATION,"");
                put(GRFDocBuilder.MAP_SCALE,"");
                put(GRFDocBuilder.MAP_TYPE,"");
                put(GRFDocBuilder.CATEGORY,"");
                put(GRFDocBuilder.HOLDING_LIBRARY,"");
                put(GRFDocBuilder.TARGET_AUDIENCE,"");
                put(Keys.MAIN_HEADING,"");
                put(Keys.CONTROLLED_TERMS,"");
                put(Keys.UNCONTROLLED_TERMS,"");
                put(Keys.REGION_CONTROLLED_TERMS,"");
                put(Keys.CLASS_CODES,"");
                put(Keys.TREATMENTS,"");
                put(Keys.GLOBAL_TAGS,"");
                put(Keys.PRIVATE_TAGS,"");
                put(Keys.DOI,"");
                put(Keys.DOCID,"");
                put(GRFDocBuilder.LOCATIONS,"");
                //put(GRFDocBuilder.COORDINATES,"");
                put(GRFDocBuilder.MERIDIAN,"");
                put(Keys.COPYRIGHT,"");
                put(Keys.COPYRIGHT_TEXT,"");
                put(Keys.PROVIDER,"") ;
            }
        };

        public Map getKeyMap() {
          return keymap;
        }

        public Key[] getKeys() {
          return (Key[]) (getKeyMap().keySet()).toArray(new Key[]{});
        }

        public List getFields()
        {
          List abstractfields = new ArrayList();
          abstractfields.addAll((new AbstractView()).getFields());
          abstractfields.addAll(fields);
          return abstractfields;
        }

        public boolean exportLabels() { return true; }
        public String getFormat() { return FullDoc.FULLDOC_FORMAT; }
        // Detail.FULLDOC_FORMAT
    }
