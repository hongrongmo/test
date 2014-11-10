package org.ei.query.base;

import org.ei.parser.base.BooleanQuery;

public interface RelevanceBooster
{
	public BooleanQuery applyBoost(BooleanQuery bq);

}