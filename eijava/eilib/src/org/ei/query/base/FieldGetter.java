package org.ei.query.base;

import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;



public class FieldGetter
	extends BaseNodeVisitor
{

	private String fieldValue;
	private boolean get = false;

	public String getFieldValue(Expression exp)
	{
		exp.accept(this);
		return fieldValue;
	}

	public void visitWith(Expression exp)
	{
		descend(exp);
	}
	
	public void visitWith(Field field)
	{
		fieldValue = field.getNodeValue();
	}


		
	
}
