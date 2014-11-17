package org.ei.query.base;

import java.util.Vector;

import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrQuery;

public class FieldValidator extends BaseNodeVisitor {

    private String[] fields;
    private boolean foundErrors = false;
    // private FieldGetter fget = new FieldGetter();

    private Vector<String> invalidFields = new Vector<String>();

    public FieldValidator(String[] fields) {
        this.fields = fields;
    }

    public Vector<String> validateFields(BooleanQuery bQuery) {
        bQuery.accept(this);
        return this.invalidFields;
    }

    public boolean foundErrors() {
        return foundErrors;
    }

    public Vector<String> getInvalidFields() {
        return invalidFields;
    }

    public void visitWith(Expression exp) {
        descend(exp);
    }

    public void visitWith(BooleanQuery query) {
        descend(query);
    }

    public void visitWith(AndQuery aQuery) {
        descend(aQuery);
    }

    public void visitWith(OrQuery oQuery) {
        descend(oQuery);
    }

    public void visitWith(NotQuery nQuery) {
        descend(nQuery);
    }

    public void visitWith(Field field) {
        String strNodeValue = field.getNodeValue();
        boolean found = false;

        for (int i = 0; i < fields.length; i++) {
            if (strNodeValue.equals(fields[i])) {
                found = true;
            }
        }

        if (found == false) {
            invalidFields.addElement(strNodeValue);
            foundErrors = true;
            return;
        }
    }
}
