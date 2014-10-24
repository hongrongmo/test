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

        private static final List fields = Arrays.asList(new String[]{"ABSTRACT",
                                                        "COORDINATES",
                                                        "CODEN",
                                                        "INDEX_TERMS",
                                                        "UNCONTROLLED_TERMS",
                                                        "REPORT_NUMBER",
                                                        "NAME_OF_MEETING"});

        private static final Map keymap = new HashMap() {
            {
                put(Keys.REPORT_NUMBER,"");
                put(Keys.ABSTRACT,"");
                put(Keys.CLASS_CODES,"");
                put(Keys.CODEN,"");
                put(Keys.CONFERENCE_NAME,"");
                put(Keys.CONF_DATE,"");
                put(Keys.EDITOR_AFFS,"");
                put(Keys.INDEX_TERM,"");
                put(Keys.UNCONTROLLED_TERMS,"");
                put(Keys.MAIN_HEADING,"");
                put(Keys.PROVIDER,"");
                put(Keys.SPONSOR,"");
                //put(Keys.COORDINATES,"");
                put(Keys.LOCATIONS,"");
                put(Keys.ISBN,"");

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

        public String getLanguage()
        {
          String strlang = super.getLanguage();
          return new CitationAbstractLanguageDecorator(strlang).getValue();
        }
    }
