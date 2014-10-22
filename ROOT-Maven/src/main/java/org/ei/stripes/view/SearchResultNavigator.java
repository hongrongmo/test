package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

import org.ei.domain.navigators.state.ResultNavigatorState;

public class SearchResultNavigator implements Comparable<SearchResultNavigator> {
	

	private String name;	// Name for navigator e.g. "dbnav" or "aunav"
	private String label;	// Label for navigator
	private String state;	
	private int more;		// More count
    private int less;       // Less count
    private int count;      // Count of navigator values
	private String field;	// Field name e.g. "db" or "au"
	private int order=999;		// Display order
	private int totalSize;
	// @see org.ei.domain.navigators.state.ResultsNavigatorState for open integer values
	private int open = ResultNavigatorState.OPEN.UNINITIALIZED.getVal(); 
	
	private List<ResultsNavigatorItem> items = new ArrayList<ResultsNavigatorItem>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getMore() {
		return more;
	}

	public void setMore(int more) {
		this.more = more;
	}

	public int getLess() {
		return less;
	}

	public void setLess(int less) {
		this.less = less;
	}

	public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<ResultsNavigatorItem> getItems() {
		return items;
	}

	public void setItems(List<ResultsNavigatorItem> items) {
		this.items = items;
	}
	
	public void addItem(ResultsNavigatorItem item) {
		getItems().add(item);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	@Override
	public int compareTo(SearchResultNavigator o) {
		if (this.order < ((SearchResultNavigator) o).order) return -1;
		else if (this.order > ((SearchResultNavigator) o).order) return 1;
		else return 0;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
}
