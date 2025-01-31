package org.ei.dataloading;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;


public class ThesaurusTableDriver
{

	protected static final String MAIN_TERM = "MT";
	protected static final String SCOPE_NOTES = "SC";
	protected static final String HISTORY_SCOPE_NOTES = "HN";
	protected static final String USE_TERMS = "US";
	protected static final String NARROWER_TERMS = "NT";
	protected static final String BROADER_TERMS = "BT";
	protected static final String RELATED_TERMS = "RT";
	protected static final String CLASS_CODES = "CL";
	protected static final String PRIOR_TERMS = "PT";
	protected static final String DATE_OF_INTRO = "DT";
	protected static final String LEADIN_TERMS = "LT";
	protected static final String STATUS = "ST";
	protected static final String TOP_TERMS = "TT";

	public class SortAlgo
				implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			TRecord t1 = (TRecord)o1;
			TRecord t2 = (TRecord)o2;
			String term1 = t1.getFieldValues(MAIN_TERM);
			String term2 = t2.getFieldValues(MAIN_TERM);
			return term1.toLowerCase().compareTo(term2.toLowerCase());
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class TRecord
	{
		Properties pr = new Properties();

		public String getFieldValues(String tag)
		{
			return pr.getProperty(tag);
		}

		public int size()
		{
			return pr.size();
		}

		public void addFieldValue(String tag, String value)
		{
			if(pr.containsKey(tag))
			{
				String v = pr.getProperty(tag);
				v = v+";"+value;
				pr.setProperty(tag, v);
			}
			else
			{
				pr.setProperty(tag, value);
			}
		}
	}


	public String notNull(String s)
	{
		if(s != null)
		{
			return s;
		}

		return "";
	}

	public void writeRecords(List list, PrintWriter writer, String database)
		throws Exception
	{
		for(int i=0; i<list.size(); ++i)
		{
			TRecord tr = (TRecord)list.get(i);
			StringBuffer buf = new StringBuffer();
			buf.append(Integer.toString(i));
			buf.append("	");
			buf.append(database);
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(MAIN_TERM)));
			buf.append("	");
			buf.append(notNull((tr.getFieldValues(MAIN_TERM)).toLowerCase()));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(STATUS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(SCOPE_NOTES)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(HISTORY_SCOPE_NOTES)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(USE_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(LEADIN_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(NARROWER_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(BROADER_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(RELATED_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(TOP_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(CLASS_CODES)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(PRIOR_TERMS)));
			buf.append("	");
			buf.append(notNull(tr.getFieldValues(DATE_OF_INTRO)));
			writer.println(buf.toString());
		}
	}
}
