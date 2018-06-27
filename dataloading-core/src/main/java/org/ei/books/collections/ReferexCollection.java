package org.ei.books.collections;

import java.util.List;
import java.util.regex.Pattern;

import org.ei.domain.navigators.EiModifier;

public abstract class ReferexCollection implements Comparable<Object> {

    public static final String ALL = "All Collections";

    public static ReferexCollection ELE = new Ele();
    public static ReferexCollection CHE = new Che();
    public static ReferexCollection MAT = new Mat();
    public static ReferexCollection CIV = new Civ();
    public static ReferexCollection COM = new Com();
    public static ReferexCollection SEC = new Sec();
    public static ReferexCollection TNFELE = new Ele("TNFELE");
    public static ReferexCollection TNFCHE = new Che("TNFCHE");
    public static ReferexCollection TNFMAT = new Mat("TNFMAT");
    public static ReferexCollection TNFCIV = new Civ("TNFCIV");
    public static ReferexCollection TNFCOM = new Com("TNFCOM");
    public static ReferexCollection TNFSEC = new Sec("TNFSEC");

    public abstract String getShortname();

    public abstract String getDisplayName();

    public abstract String getAbbrev();

    public abstract List<?> populateSubjects(boolean che, boolean chestar);

    public abstract int getColMask();

    public abstract int getSortOrder();

    public static final ReferexCollection[] allcolls = new ReferexCollection[] { ELE, CHE, MAT, CIV, COM, SEC, TNFELE, TNFCHE, TNFMAT, TNFCIV };
    public static final Pattern ALLCOLS_PATTERN = Pattern.compile("(ELE|MAT|CHE|COM|SEC|CIV|TNFELE|TNFMAT|TNFCHE|TNFCOM|TNFSEC|TNFCIV)(\\d?)");

    public static String translateCollection(String longname) {

        String shortname = "";
        if (longname != null) {

            for (int i = 0; i < allcolls.length; i++) {
                ReferexCollection acol = allcolls[i];
                if (longname.equals(acol.getDisplayName())) {
                    shortname = acol.getShortname();
                    break;
                }
            }
        }

        return shortname;
    }

    public EiModifier getModifier() {
        return new EiModifier(0, getShortname(), getShortname());
    }

    public static ReferexCollection getCollection(String colname) {

        ReferexCollection coll = null;
        if (colname != null) {
            for (int i = 0; i < allcolls.length; i++) {
                ReferexCollection acol = allcolls[i];
                if (colname.toLowerCase().startsWith(acol.getAbbrev().toLowerCase())) {
                    coll = acol;
                    break;
                }
            }
        }
        return coll;
    }

    public int compareTo(Object anobject) {
        ReferexCollection col = (ReferexCollection) anobject;
        // Returns a negative integer, zero, or a positive integer as this
        // object
        // is less than, equal to, or greater than the specified object.

        if (this.getSortOrder() < col.getSortOrder()) {
            return -1;
        } else if (this.getSortOrder() > col.getSortOrder()) {

            return 1;
        } else {

            return 0;
        }

    }

    public boolean equals(Object anobject) {
        return this.compareTo(anobject) == 0;
    }

}