package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.ei.domain.Abstract;

    public class AbstractView extends DocumentView {

        private List fields = Arrays.asList(new String[]{"ABSTRACT",
                                                        "COORDINATES",
                                                        "CODEN",
                                                        "EISSN",
                                                        "INDEX_TERMS",
                                                        "UNCONTROLLED_TERMS",
                                                        "NAME_OF_MEETING",
                                                        "ISBN"});

        private Map keymap = new HashMap() {
            {
                put(Keys.ABSTRACT,"");
                put(Keys.CLASS_CODES,"");
                put(Keys.CODEN,"");
                put(Keys.CONFERENCE_NAME,"");
                put(Keys.CONF_DATE,"");
                put(Keys.EDITOR_AFFS,"");
                put(Keys.E_ISSN,"");
                put(Keys.INDEX_TERM,"");
                put(Keys.UNCONTROLLED_TERMS,"");
                put(Keys.ISBN,"");
                put(Keys.I_PUBLISHER,"");
                put(Keys.MAIN_HEADING,"");
                put(Keys.PROVIDER,"");
                put(Keys.SPONSOR,"");
                //put(GRFDocBuilder.COORDINATES,"");
                put(GRFDocBuilder.LOCATIONS,"");

                // HERE WE ARE APPENDING ALL OF THE CITATION VIEW KEYS
                putAll((new CitationView()).getKeyMap());
            }
        };

        public Map getKeyMap() {
          return keymap;
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
