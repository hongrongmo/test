package org.ei.tags;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class GroupCreateComp implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        TagGroup group1 = (TagGroup) o1;
        TagGroup group2 = (TagGroup) o2;

        long date1 = group1.getDatefounded();
        long date2 = group2.getDatefounded();

        if (date1 == date2) {
            return 0;
        } else if (date1 < date2) {
            return -1;
        } else {
            return 1;
        }
    }

	@Override
	public Comparator<Object> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Object> thenComparing(Comparator<? super Object> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> Comparator<Object> thenComparing(
			Function<? super Object, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<Object> thenComparing(
			Function<? super Object, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Object> thenComparingInt(
			ToIntFunction<? super Object> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Object> thenComparingLong(
			ToLongFunction<? super Object> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Object> thenComparingDouble(
			ToDoubleFunction<? super Object> keyExtractor) {
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