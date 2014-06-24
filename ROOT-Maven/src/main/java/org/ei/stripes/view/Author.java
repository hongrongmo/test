package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

public class Author {
	private int id;
	private String name;
	private String searchlink;
	private String email;
	private List<Affil> affils;
	private String co;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name == null ? null : name.trim();
	}

	public String getNameupper() {
		return name == null ? null : name.trim().toUpperCase();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSearchlink() {
		return searchlink;
	}

	public void setSearchlink(String searchlink) {
		this.searchlink = searchlink;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Affil> getAffils() {
		return affils;
	}

	public void setAffils(List<Affil> affils) {
		this.affils = affils;
	}
	
	public void addAffil(Affil affil) {
		if (affils == null) {
			affils = new ArrayList<Affil>();
		}
		affils.add(affil);
	}

	public String getCo() {
		return co;
	}

	public void setCo(String co) {
		this.co = co;
	}
}
