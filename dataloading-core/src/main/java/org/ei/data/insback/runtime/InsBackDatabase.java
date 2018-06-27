package org.ei.data.insback.runtime;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.fulldoc.LinkingStrategy;

import org.ei.common.insback.*;
import org.ei.common.inspec.*;

public class InsBackDatabase extends Database {
	//HH 01/16/2015
	private DataDictionary dataDictionary =  InspecDataDictionary.getInstance();

	public int getStartYear(boolean hasBackFile) {
		return 1896;
	}

	private LinkingStrategy linkingStrategy = new IBFLinkingStrategy();

	protected String getBaseTableHook() {
		return "ibf_master";
	}

	public List<SortField> getSortableFields() {
		return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
	}

	public DocumentBuilder newBuilderInstance() {
		return new org.ei.data.insback.runtime.InsBackDocBuilder(this);
	}

	public SearchControl newSearchControlInstance() {
		return new FastSearchControl();
	}

	/*
	 * Because this a backfile overide getLastFourUpdates so that never returns
	 * a hit from FAST.
	 */

	public String getUpdates(int num) throws InfrastructureException {
		return "13000-13001";
	}

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public String getID() {
		return "ibf";
	}

	public String getLegendID() {
		return "ib";
	}

	public String getName() {
		return "Inspec";
	}

	public String getIndexName() {
		return "ibf";
	}

	public boolean isBackfile() {
		return true;
	}

	public String getShortName() {
		return "Inspec";
	}

	public boolean hasField(SearchField searchField) {
		return false;
	}

	public LinkingStrategy getLinkingStrategy() {
		return linkingStrategy;
	}

	public int getMask() {
		return 4096;
	}

	public int getSortValue() {
		return 2;
	}

	public Map<String, String> getTreatments() {

		Map<String, String> mp = new Hashtable<String, String>();

		mp.put("A", "Applications (APP)");
		mp.put("B", "Bibliography (BIB)");
		mp.put("E", "Economic (ECO)");
		mp.put("G", "General review (GEN)");
		mp.put("N", "New development (NEW)");
		mp.put("P", "Practical (PRA)");
		mp.put("R", "Product review (PRO)");
		mp.put("T", "Theoretical (THR)");
		mp.put("X", "Experimental (EXP)");

		return mp;
	}

	public boolean linkLocalHoldings(String linklabel) {
		if (linklabel.indexOf("NTIS") > -1) {
			return false;
		} else {
			return true;
		}
	}

	public String getSingleCharName() {
		return "F";
	}

}
