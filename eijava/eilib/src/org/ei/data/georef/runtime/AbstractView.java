package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ei.domain.Abstract;

    public class AbstractView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"ABSTRACT",
                                                        "BIBLIOGRAPHIC_LEVEL_CODE",
                                                        "CODEN",
                                                        "EISSN",
                                                        "INDEX_TERMS",
                                                        "NAME_OF_MEETING",
                                                        "ISBN"});

        private Key[] keys = new Key[]{Keys.ABSTRACT,
                                        Keys.ABSTRACT_TYPE,
                                        Keys.CLASS_CODES,
                                        Keys.CODEN,
                                        Keys.CONFERENCE_NAME,
                                        Keys.CONF_DATE,
                                        Keys.EDITOR_AFFS,
                                        Keys.E_ISSN,
                                        Keys.INDEX_TERM,
                                        Keys.ISBN,
                                        Keys.I_PUBLISHER,
                                        Keys.MAIN_HEADING,
                                        Keys.PROVIDER,
                                        Keys.SPONSOR};

        public Key[] getKeys() {
          List abstractkeys = new ArrayList();
          abstractkeys.addAll(Arrays.asList((new CitationView()).getKeys()));
          abstractkeys.addAll(Arrays.asList(keys));
          return (Key[]) abstractkeys.toArray(new Key[]{});
        }

        public List getFields()
        {
          List abstractfields = new ArrayList();
          abstractfields.addAll((new CitationView()).getFields());
          abstractfields.addAll(fields);
          return abstractfields;
        }
        public boolean exportLabels() { return true; }
        public String getFormat() { return Abstract.ABSTRACT_FORMAT; }
    }
