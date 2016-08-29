package org.ei.thesaurus;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ThesaurusTermComp implements Comparator<ThesaurusRecord> {
	public int compare(ThesaurusRecord tag1, ThesaurusRecord tag2) {
		int result = 0;
		try {
			String term1 = "";
			String term2 = "";
			if (tag1 != null && tag1.getRecID() != null) {
				term1 = tag1.getRecID().getMainTerm();
			}
			if (tag2 != null && tag2.getRecID() != null) {
				term2 = tag2.getRecID().getMainTerm();
			}
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