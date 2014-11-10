package org.ei.query.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.navigators.EiNavigator;
import org.ei.parser.base.AndPhrase;
import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNode;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanPhrase;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotPhrase;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrPhrase;
import org.ei.parser.base.OrQuery;
import org.ei.parser.base.Phrase;
import org.ei.parser.base.Term;

public class DatabaseMaskGatherer extends BaseNodeVisitor {
    private List<String> termList;
    private List<Integer> dbfieldList;
    private boolean isnot = false;

    private static DatabaseConfig dbconfig = DatabaseConfig.getInstance();

    public int gatherDbMask(BooleanQuery bQuery, int mask) {
        dbfieldList = new ArrayList<Integer>();
        bQuery.accept(this);

        int newmask = mask;
        Iterator<Integer> itrdbs = dbfieldList.iterator();
        while (itrdbs.hasNext()) {
            int dbmask = ((Integer) itrdbs.next()).intValue();
            newmask &= ((dbmask < 0) ? ~Math.abs(dbmask) : dbmask);
        }

        return newmask;
    }

    public void visitWith(BooleanQuery bQuery) {
        descend(bQuery);
    }

    public void visitWith(AndQuery aQuery) {
        descend(aQuery);
    }

    public void visitWith(OrQuery oQuery) {
        descend(oQuery);
    }

    public void visitWith(NotQuery nQuery) {
        isnot = false;
        descend((BaseNode) nQuery.getChildAt(0));
        isnot = true;
        descend((BaseNode) nQuery.getChildAt(2));
        isnot = false;
    }

    public void visitWith(Expression exp) {
        // gather together DB terms
        if ((new FieldGetter()).getFieldValue(exp).equalsIgnoreCase(EiNavigator.DB)) {
            termList = new ArrayList<String>();

            // descend down to gather terms
            descend((BaseNode) exp.getChildAt(0));
            gather();

            descend((BaseNode) exp.getChildAt(1));
        }
    }

    public void visitWith(BooleanPhrase bPhrase) {
        descend(bPhrase);
    }

    public void visitWith(AndPhrase phrase) {
        descend(phrase);
    }

    public void visitWith(OrPhrase phrase) {
        descend(phrase);
    }

    public void visitWith(NotPhrase phrase) {
        descend(phrase);
    }

    public void visitWith(Phrase phrase) {
        descend(phrase);
    }

    public void visitWith(Term term) {
        if ((termList != null) && (term != null)) {
            termList.add(term.getValue().trim());
        }
        descend(term);
    }

    private void gather() {
        if ((termList != null) && (!termList.isEmpty())) {
            int mask = 0;
            Iterator<String> itrTerms = termList.iterator();
            while (itrTerms.hasNext()) {
                String dbstr = (String) itrTerms.next();
                Database db = dbconfig.getDatabase(dbstr);
                if (db != null) {
                    mask += (((isnot) ? -db.getMask() : db.getMask()));
                }
            }
            dbfieldList.add(new Integer(mask));
        }

        termList = new ArrayList<String>();
    }
}