package org.ei.logging;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

public class ALSSqlHandler {

    private static final Logger log4j = Logger.getLogger(ALSSqlHandler.class);

	/**
	 * Gets the statement attribute of the ALSSqlHandler object
	 *
	 * @param event
	 *            Description of Parameter
	 * @return The statement value
	 * @exception Exception
	 *                Description of Exception
	 * @since
	 */
	public void writeMessage(CLFMessage message, Connection con) throws Exception {
		StringBuffer insert = new StringBuffer();

		// REGEX stuff
		int limit, interps, i;
		PatternMatcher matcher;
		PatternCompiler compiler;
		Pattern pattern = null;
		String regularExpression, input;
		String appID, reqID, httpData, cookie, appData;

		// Create Perl5Compiler and Perl5Matcher instances.
		compiler = new Perl5Compiler();
		matcher = new Perl5Matcher();

		regularExpression = "^<(.*?)> <(.*?)> <(.*?)> <(.*?)> <(.*?)>";

		// Attempt to compile the pattern. If the pattern is not valid,
		// report the error and exit.
		try {
			pattern = compiler.compile(regularExpression);
		} catch (MalformedPatternException e) {
			System.err.println("Bad pattern.");
			System.err.println(e.getMessage());
		}

		if (matcher.contains(message.getCLFMessage(), pattern)) {
			MatchResult result = matcher.getMatch();
			if (log4j.isInfoEnabled()) {
				log4j.info("APPID   : " + result.group(1));
				log4j.info("REQID   : " + result.group(2));
				log4j.info("HTTPDATA: " + result.group(3) + " size=" + result.group(3).toString().length());
				log4j.info("COOKIE  : " + result.group(4));
				log4j.info("APPDATA : " + result.group(5) + " size=" + result.group(5).toString().length());
			}

			appID = result.group(1);
			reqID = result.group(2);
			httpData = result.group(3);
			if (httpData != null) {
				// clean up potential data sql insert issues
				httpData = httpData.replaceAll("'", "''");
			}
			cookie = result.group(4);
			appData = result.group(5);
			if (appData != null) {
				// clean up potential data sql insert issues
				appData = appData.replaceAll("'", "''");
			}

			// Construct the insert statment for ALS Analytic Logging Service DB
			// format.
			// Consult Websphere for format.
			insert.append("INSERT INTO als_village_log (tmstp, appid, reqid, httpdata,cookie,appdata) ");
			insert.append(" VALUES ( ");
			insert.append("SYSDATE ,");
			insert.append("'" + appID + "' ,");
			insert.append("'" + reqID + "' ,");
			insert.append("'" + httpData + "' ,");
			insert.append("'" + cookie + "' ,");
			insert.append("'" + appData + "' )");
		} else {
			// insert.insert(0, "SELECT * FROM dual");
			insert = new StringBuffer();
			insert.append("SELECT * FROM dual");
		}
        if (log4j.isInfoEnabled()) {
			log4j.info(insert.toString());
		}

		String sql = insert.toString();

		Statement stmt = null;

		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
