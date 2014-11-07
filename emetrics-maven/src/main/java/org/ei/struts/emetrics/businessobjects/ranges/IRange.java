package org.ei.struts.emetrics.businessobjects.ranges;

public interface IRange {
    public abstract boolean isWithinRange(Object val);

    public abstract boolean isYearWithinRange(int year);

}

