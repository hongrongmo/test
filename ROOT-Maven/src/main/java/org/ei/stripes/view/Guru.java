package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;


public class Guru {
	

	private String name;	
	private String searchLink;
	private String guruInfo;
	private List<Search> items = new ArrayList<Search>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSearchLink() {
		return searchLink;
	}

	public void setSearchLink(String searchLink) {
		this.searchLink = searchLink;
	}
	
	public String getGuruInfo() {
		return guruInfo;
	}

	public void setGuruInfo(String guruInfo) {
		this.guruInfo = guruInfo;
	}

	public List<Search> getItems() {
		return items;
	}

	public void setItems(List<Search> items) {
		this.items = items;
	}

	
	public void addItem(Search search) {
		getItems().add(search);
	}

}
