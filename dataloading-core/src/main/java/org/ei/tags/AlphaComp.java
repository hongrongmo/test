package org.ei.tags;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class AlphaComp implements Comparator<Tag> {

    public int compare(Tag o1, Tag o2) {
        Tag tag1 = (Tag) o1;
        Tag tag2 = (Tag) o2;

        String tagname1 = tag1.getTagSearchValue();
        String tagname2 = tag2.getTagSearchValue();
        return tagname1.compareTo(tagname2);

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