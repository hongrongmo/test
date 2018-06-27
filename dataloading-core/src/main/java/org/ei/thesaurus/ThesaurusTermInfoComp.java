package org.ei.thesaurus;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * fix stars issue for browse search - Jan 28,2013
 *
 * @author telebh
 *
 */

public class ThesaurusTermInfoComp implements Comparator<ThesaurusRecord> {

	public int compare(ThesaurusRecord rec1, ThesaurusRecord rec2) {

		int result = 0;
		try {
			// Update, Feb 26,2013
			String term1 = "";
			String term2 = "";
			if (rec1 != null && rec1.getRecID() != null) {
				term1 = Integer.toString(rec1.getRecID().getRecordID());
			}

			if (rec2 != null && rec2.getRecID() != null) {
				term2 = Integer.toString(rec2.getRecID().getRecordID());
			}

			//
			result = term1.toLowerCase().compareTo(term2.toLowerCase());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Comparator<ThesaurusRecord> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<ThesaurusRecord> thenComparing(
			Comparator<? super ThesaurusRecord> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> Comparator<ThesaurusRecord> thenComparing(
			Function<? super ThesaurusRecord, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<ThesaurusRecord> thenComparing(
			Function<? super ThesaurusRecord, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<ThesaurusRecord> thenComparingInt(
			ToIntFunction<? super ThesaurusRecord> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<ThesaurusRecord> thenComparingLong(
			ToLongFunction<? super ThesaurusRecord> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<ThesaurusRecord> thenComparingDouble(
			ToDoubleFunction<? super ThesaurusRecord> keyExtractor) {
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
	public <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) {
		// TODO Auto-generated method stub
		return null;
	}
	public <T> Comparator<T> nullsLast(Comparator<? super T> comparator) {
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
