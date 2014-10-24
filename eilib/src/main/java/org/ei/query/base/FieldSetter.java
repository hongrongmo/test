package org.ei.query.base;

import org.ei.parser.base.BaseNodeVisitor;
import org.ei.parser.base.Expression;
import org.ei.parser.base.Field;



public class FieldSetter
	extends BaseNodeVisitor
{
	private String fieldValue;

	public void setFieldValue(Expression ex, String fieldValue)
	{
		this.fieldValue = fieldValue;
		ex.accept(this);
	}

	public void visitWith(Expression exp)
	{
		descend(exp);
	}

	public void visitWith(Field field)
	{
		field.setNodeValue(this.fieldValue);
	}
}
