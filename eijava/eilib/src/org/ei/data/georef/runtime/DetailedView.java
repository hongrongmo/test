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
                                                        "ILLUSTRATION",
                                                        "ID_NUMBER",
                                                        "MAP_TYPE",
                                                        "MAP_SCALE",
                                                        "ANNOTATION",
                                                        "CATEGORY_CODE",
                                                        "DOCUMENT_TYPE",
                                                        "UNIVERSITY",
                                                        "TYPE_OF_DEGREE",
                                                        "AFFILIATION_SECONDARY",
                                                        "COUNTRY_OF_PUBLICATION",
                                                        "REPORT_NUMBER",
                                                        "NUMBER_OF_REFERENCES"});

        private Key[] keys = new Key[]{Keys.ABBRV_SERIAL_TITLE,
                                        Keys.ACCESSION_NUMBER,
                                        GRFDocBuilder.AFFILIATION_OTHER,
                                        GRFDocBuilder.ANNOTATION,
                                        Keys.ARTICLE_NUMBER,
                                        Keys.CONF_CODE,
                                        Keys.COUNTRY_OF_PUB,
                                        Keys.DOC_TYPE,
                                        Keys.DOI,
                                        Keys.GLOBAL_TAGS,
                                        Keys.ISSUE,
                                        GRFDocBuilder.ILLUSTRATION,
                                        GRFDocBuilder.MAP_SCALE,
                                        GRFDocBuilder.MAP_TYPE,
                                        GRFDocBuilder.CATEGORY,
                                        Keys.MEETING_LOCATION,
                                        Keys.NUMBER_OF_REFERENCES,
                                        Keys.PAGE_COUNT,
                                        Keys.PRIVATE_TAGS,
                                        Keys.REGION_CONTROLLED_TERMS,
                                        Keys.REPORT_NUMBER,
                                        Keys.SERIAL_TITLE,
                                        Keys.SOURCE_COUNTRY,
                                        Keys.TITLE_TRANSLATION,
                                        Keys.DOC_URL,
                                        Keys.TREATMENTS,
                                        Keys.VOLUME};

        public Key[] getKeys() {
          List abstractkeys = new ArrayList();
          abstractkeys.addAll(Arrays.asList((new AbstractView()).getKeys()));
          abstractkeys.addAll(Arrays.asList(keys));
          abstractkeys.remove(Keys.VOLISSUE);
          return (Key[]) abstractkeys.toArray(new Key[]{});
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
