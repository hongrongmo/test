package org.ei.query.base;

import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotQuery;

public class QueryMask extends BaseNodeVisitor {
    private int mask;
    private String operation = "&";

    public static void main(String args[]) throws Exception {
        BaseParser parser = new BaseParser();
        String query = "(water WN TI) AND ((cpx OR ntis) WN DB)";
        BooleanQuery tree = (BooleanQuery) parser.parse(query);
        // walk(tree);
        QueryMask qm = new QueryMask();
        int finalMask = qm.getQueryMask(tree, 7);
        System.out.println("Final mask:" + finalMask);
    }

    public int getQueryMask(BooleanQuery bQuery, int mask) {
        this.mask = mask;
        descend(bQuery);
        return this.mask;
    }

    public void visitWith(BooleanQuery bQuery) {
        descend(bQuery);
    }

    public void visitWith(AndQuery aQuery) {
        this.operation = "&";
        descend(aQuery);
    }

    public void visitWith(NotQuery nQuery) {
        this.operation = "^";
        descend(nQuery);
    }

    public void visitWith(Expression exp) {
        FieldGetter fg = new FieldGetter();
        String value = fg.getFieldValue(exp);

        if (value.equalsIgnoreCase("db")) {
            ExpressionMask em = new ExpressionMask();
            int expressionMask = em.getExpressionMask(exp);
            if (operation.equals("&")) {
                this.mask = this.mask & expressionMask;
            } else if (operation.equals("^")) {
                this.mask = expressionMask ^ this.mask;
            }
        }
    }
}