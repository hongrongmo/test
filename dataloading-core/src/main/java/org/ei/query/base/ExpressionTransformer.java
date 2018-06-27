package org.ei.query.base;

import org.ei.parser.base.AndQuery;
import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.BooleanOr;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Expression;
import org.ei.parser.base.NotQuery;
import org.ei.parser.base.OrQuery;


public class ExpressionTransformer
    extends BaseNodeVisitor
{

    private FieldGetter fget = new FieldGetter();
    private FieldSetter fset = new FieldSetter();
    private String field;
    private String[] mappings;



    public BooleanQuery transform(int searchMask,
                                  int transformMask,
                                  String field,
                                  String[] mappings,
                                  BooleanQuery bQuery)
    {
        if((searchMask & transformMask) > 0)
        {
            System.out.println("Transforming expression");
            this.field = field;
            this.mappings = mappings;
            bQuery.accept(this);
        }

        return bQuery;
    }


    public void visitWith(Expression exp)
    {
        String qfield = fget.getFieldValue(exp);
        if(this.field.equalsIgnoreCase(qfield))
        {
            BooleanQuery tNode = null;
            BooleanOr bOR = new BooleanOr("OR");
            for(int x=0; x < mappings.length; ++x)
            {
                Expression ex = (Expression)exp.deepCopy();
                fset.setFieldValue(ex, mappings[x]);

                BooleanQuery bq = new BooleanQuery(ex);
                if(tNode != null)
                {
                    OrQuery oq = new OrQuery(tNode, bOR, bq);
                    tNode = new BooleanQuery(oq);
                }
                else
                {
                    tNode = bq;
                }
            }

            BooleanQuery expressionParent = (BooleanQuery)exp.getParent();
            expressionParent.setChildAt(exp.getChildIndex(), tNode);
        }
    }

    public void visitWith(BooleanQuery query)
    {
        descend(query);
    }

    public void visitWith(OrQuery oQuery)
    {
        descend(oQuery);
    }

    public void visitWith(NotQuery nQuery)
    {
        descend(nQuery);
    }

    public void visitWith(AndQuery aQuery)
    {
        descend(aQuery);
    }


}
