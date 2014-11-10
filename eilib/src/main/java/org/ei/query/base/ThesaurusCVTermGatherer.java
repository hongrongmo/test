package org.ei.query.base;

import org.ei.parser.base.Expression;

public class ThesaurusCVTermGatherer
  extends ExactTermGatherer
{
  public void visitWith(Expression exp)
  {
    FieldGetter fg = new FieldGetter();
    String strfield = fg.getFieldValue(exp);

    if((strfield != null) && (strfield.equalsIgnoreCase("CV"))) {
      descend(exp);
    }
  }
}
