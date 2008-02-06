package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ei.domain.Detail;
import org.ei.domain.FullDoc;

    public class DetailedView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"DOI",
                                                        "URL",
                                                        "AVAILABILITY",
                                                        "BIBLIOGRAPHIC_LEVEL_CODE",
                                                        "ILLUSTRATION",
                                                        "ID_NUMBER",
                                                        "MAP_TYPE",
                                                        "MAP_SCALE",
                                                        "TARGET_AUDIENCE",
                                                        "HOLDING_LIBRARY",
                                                        "ANNOTATION",
                                                        "CATEGORY_CODE",
                                                        "DOCUMENT_TYPE",
                                                        "UNIVERSITY",
                                                        "TYPE_OF_DEGREE",
                                                        "AFFILIATION_SECONDARY",
                                                        "COUNTRY_OF_PUBLICATION",
                                                        "REPORT_NUMBER",
                                                        "NUMBER_OF_REFERENCES"});

        // Since the order of these keys dtermines their display on the Detailed record page
        // explicitly set the Key values here instead of comibing with Abstract or Citation classes
        private Key[] keys = new Key[]{Keys.ACCESSION_NUMBER,
                                        Keys.TITLE,
                                        Keys.TITLE_TRANSLATION,
                                        Keys.AUTHORS,
                                        Keys.EDITORS,
                                        Keys.AUTHOR_AFFS,
                                        Keys.EDITOR_AFFS,
                                        Keys.CORRESPONDENCE_PERSON,
                                        Keys.SERIAL_TITLE,
                                        Keys.ABBRV_SERIAL_TITLE,
                                        Keys.VOLUME,
                                        Keys.ISSUE,
                                        Keys.MONOGRAPH_TITLE,
                                        Keys.ISSUE_DATE,
                                        Keys.PUBLICATION_YEAR,
                                        Keys.PAGE_RANGE,
                                        Keys.PAGE_COUNT,
                                        Keys.ARTICLE_NUMBER,
                                        Keys.LANGUAGE,
                                        Keys.ISSN,
                                        Keys.E_ISSN,
                                        Keys.CODEN,
                                        Keys.ISBN,
                                        Keys.DOC_TYPE,
                                        GRFDocBuilder.DEGREE_TYPE,
                                        GRFDocBuilder.UNIVERSITY,
                                        Keys.SOURCE_COUNTRY,
                                        Keys.CONFERENCE_NAME,
                                        Keys.CONF_DATE,
                                        Keys.MEETING_LOCATION,
                                        Keys.CONF_CODE,
                                        Keys.SPONSOR,
                                        Keys.PUBLISHER,
                                        Keys.ABSTRACT,
                                        Keys.ABSTRACT_TYPE,
                                        Keys.AVAILABILITY,
                                        Keys.NUMBER_OF_REFERENCES,
                                        GRFDocBuilder.ILLUSTRATION,
                                        GRFDocBuilder.MAP_SCALE,
                                        GRFDocBuilder.MAP_TYPE,
                                        GRFDocBuilder.CATEGORY,
                                        GRFDocBuilder.HOLDING_LIBRARY,
                                        GRFDocBuilder.TARGET_AUDIENCE,
                                        Keys.MAIN_HEADING,
                                        Keys.CONTROLLED_TERMS,
                                        Keys.SPECIES_TERMS,
                                        Keys.REGION_CONTROLLED_TERMS,
                                        Keys.CLASS_CODES,
                                        Keys.TREATMENTS,
                                        Keys.GLOBAL_TAGS,
                                        Keys.PRIVATE_TAGS,
                                        Keys.DOI,
                                        Keys.DOCID,
                                        Keys.COPYRIGHT,
                                        Keys.COPYRIGHT_TEXT,
                                        Keys.PROVIDER};

        public Key[] getKeys() {
          return keys;
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
