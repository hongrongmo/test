package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;


public class Discipline {
	

	private String name;	
	private List<Guru> gurus = new ArrayList<Guru>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Guru> getGurus() {
		return gurus;
	}

	public void setGurus(List<Guru> gurus) {
		this.gurus = gurus;
	}
	
	public void addGuru(Guru guru) {
		gurus.add(guru);
	}
}
