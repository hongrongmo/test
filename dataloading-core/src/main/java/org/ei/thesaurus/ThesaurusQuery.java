package org.ei.thesaurus;

import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.exception.InfrastructureException;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanQuery;

public class ThesaurusQuery {

	private Database database;
	private String terms;
	private String displayQuery;
	private String searchQuery;

	public ThesaurusQuery(Database database, String terms) {
		this.database = database;
		this.terms = terms;
	}

	public Database getDatabase() {
		return this.database;
	}

	public void compile() throws InfrastructureException {
		buildSearchQuery();
	}

	public String getSearchQuery() {
		return this.searchQuery;
	}

	private void buildSearchQuery() throws InfrastructureException {
		ThesaurusQueryWriter searchQueryWriter = new ThesaurusQueryWriter();
		terms = terms.replace('(', ' ');
		terms = terms.replace(')', ' ');
		if (terms.indexOf("*") == -1) {
			terms = "{" + terms + "}";
		}

		StringBuffer searchQueryBuffer = new StringBuffer(terms.toLowerCase());
		String databaseID = database.getIndexName();
		// This a hack. Need to change this in the data.

		if (databaseID.equals("ins")) {
			databaseID = "inspec";
		}

		searchQueryBuffer.append(" AND (" + databaseID + " WN DB)");
		BaseParser parser = new BaseParser();
		BooleanQuery queryTree = (BooleanQuery) parser.parse(searchQueryBuffer.toString());
		this.searchQuery = searchQueryWriter.getQuery(queryTree);

	}

	public static void main(String args[]) throws Exception {
		ThesaurusQuery q = new ThesaurusQuery((DatabaseConfig.getInstance()).getDatabase("ins"), args[0]);
		q.compile();
		System.out.println(q.getSearchQuery());
	}
}