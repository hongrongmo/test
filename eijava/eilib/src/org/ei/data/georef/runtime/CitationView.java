package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ei.domain.Citation;

public class CitationView extends DocumentView {

    private List fields = Arrays.asList(new String[]{"M_ID",
                                                    "ISSN",
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
                                                    "LOAD_NUMBER",
                                                    "COPYRIGHT",
                                                    "COLLATION_ANALYTIC",
                                                    "COLLATION_MONOGRAPH"});
    private Key[] keys = new Key[]{Keys.AUTHORS,
                                    Keys.AUTHOR_AFFS,
                                    Keys.COPYRIGHT,
                                    Keys.COPYRIGHT_TEXT,
                                    Keys.DOCID,
                                    Keys.EDITORS,
                                    Keys.ISSN,
                                    Keys.ISSUE_DATE,
                                    Keys.LANGUAGE ,
                                    Keys.MONOGRAPH_TITLE,
                                    Keys.NO_SO,
                                    Keys.PAGE_RANGE,
                                    Keys.PUBLICATION_DATE,
                                    Keys.PUBLICATION_YEAR,
                                    Keys.PUBLISHER,
                                    Keys.SOURCE,
                                    Keys.TITLE,
                                    Keys.VOLISSUE};

    public Key[] getKeys() {
      List abstractkeys = new ArrayList();
      abstractkeys.addAll(Arrays.asList(keys));
      return (Key[]) abstractkeys.toArray(new Key[]{});
    }

    public List getFields()
    {
      List abstractfields = new ArrayList();
      abstractfields.addAll(fields);
      return abstractfields;
    }
    public boolean exportLabels() { return true; }
    public String getFormat() { return Citation.CITATION_FORMAT; }
}
