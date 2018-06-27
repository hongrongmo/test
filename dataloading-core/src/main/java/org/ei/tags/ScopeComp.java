package org.ei.tags;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ScopeComp implements Comparator<Tag> {

    public int[] order = { 0, 1, 2, 4, 3 };

    public int compare(Tag o1, Tag o2) {
        Tag tag1 = o1;
        Tag tag2 = o2;

        /*
         * This a little confusing. The order array maintains a fixed order that every scope should sort by.
         */

        int scopeOrder1 = order[tag1.getScope()];
        int scopeOrder2 = order[tag2.getScope()];

        if (scopeOrder1 == scopeOrder2) {
            if (scopeOrder1 == 4) {
                String group1 = tag1.getGroupID();
                String group2 = tag2.getGroupID();
                int groupComp = group1.compareTo(group2);
                if (groupComp == 0) {
                    String tagValue1 = tag1.getTagSearchValue();
                    String tagValue2 = tag2.getTagSearchValue();
                    return tagValue1.compareTo(tagValue2);
                } else {
                    return groupComp;
                }
            } else {
                String tagValue1 = tag1.getTagSearchValue();
                String tagValue2 = tag2.getTagSearchValue();
                return tagValue1.compareTo(tagValue2);
            }

        } else if (scopeOrder1 < scopeOrder2) {
            return -1;
        } else {
            return 1;
        }
    }

	@Override
	public Comparator<Tag> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Tag> thenComparing(Comparator<? super Tag> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> Comparator<Tag> thenComparing(
			Function<? super Tag, ? extends U> keyExtractor,
			Comparator<? super U> keyComparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<Tag> thenComparing(
			Function<? super Tag, ? extends U> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Tag> thenComparingInt(
			ToIntFunction<? super Tag> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Tag> thenComparingLong(
			ToLongFunction<? super Tag> keyExtractor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<Tag> thenComparingDouble(
			ToDoubleFunction<? super Tag> keyExtractor) {
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