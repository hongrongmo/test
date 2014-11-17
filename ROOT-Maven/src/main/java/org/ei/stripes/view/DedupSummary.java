package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

public class DedupSummary {
	protected int fieldpref;
	protected String fieldpreflabel;
	protected String dbpref;
	protected String dbpreflabel;
	private String removedDupsCount;
    private String dbLink="";
    private String linkSet="";
    private String dedupsetCount;
    private List<RemovedDups> removedDupsList = new ArrayList<RemovedDups>();
    private List<DbCount> dbCountList = new ArrayList<DbCount>();
    private String remvoedSubsetCount;
    private String dedupSubsetCount;
    private String origin;
    
	public int getFieldpref() {
		return fieldpref;
	}

	public void setFieldpref(int fieldpref) {
		this.fieldpref = fieldpref;
	}

	public String getDbpref() {
		return dbpref;
	}

	public void setDbpref(String dbpref) {
		this.dbpref = dbpref;
	}

	public String getFieldpreflabel() {
		return fieldpreflabel;
	}

	public void setFieldpreflabel(String fieldpreflabel) {
		this.fieldpreflabel = fieldpreflabel;
	}

	public String getDbpreflabel() {
		return dbpreflabel;
	}

	public void setDbpreflabel(String dbpreflabel) {
		this.dbpreflabel = dbpreflabel;
	}

	public String getRemovedDupsCount() {
		return removedDupsCount;
	}

	public void setRemovedDupsCount(String removedDups) {
		this.removedDupsCount = removedDups;
	}

	public String getDbLink() {
		return dbLink;
	}

	public void setDbLink(String dbLink) {
		this.dbLink = dbLink;
	}

	public String getLinkSet() {
		return linkSet;
	}

	public void setLinkSet(String linkSet) {
		this.linkSet = linkSet;
	}

	public List<RemovedDups> getRemovedDupsList() {
		return removedDupsList;
	}

	public void setRemovedDupsList(List<RemovedDups> removedDupsList) {
		this.removedDupsList = removedDupsList;
	}
	
	public void addRemovedDups(RemovedDups removedDups) {
		getRemovedDupsList().add(removedDups);
	}

	public String getDedupsetCount() {
		return dedupsetCount;
	}

	public void setDedupsetCount(String dedupsetCount) {
		this.dedupsetCount = dedupsetCount;
	}

	public List<DbCount> getDbCountList() {
		return dbCountList;
	}

	public void setDbCountList(List<DbCount> dbCountList) {
		this.dbCountList = dbCountList;
	}

	public void addDbCount(DbCount dbCount) {
		getDbCountList().add(dbCount);
	}

	public String getRemvoedSubsetCount() {
		return remvoedSubsetCount;
	}

	public void setRemvoedSubsetCount(String remvoedSubsetCount) {
		this.remvoedSubsetCount = remvoedSubsetCount;
	}

	public String getDedupSubsetCount() {
		return dedupSubsetCount;
	}

	public void setDedupSubsetCount(String dedupSubsetCount) {
		this.dedupSubsetCount = dedupSubsetCount;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
