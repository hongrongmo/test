package org.ei.dataloading.cafe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ei.common.bd.BdAffiliations;
import org.ei.common.bd.BdAuthors;
import java.util.List;

import java.sql.DriverManager;

/**
 * 
 * @author TELEBH
 * @Date: 07/14/2020 @Description, To keep AuID/AfId doc-count for
 *        AUthor/affiliation search result page in sync with EV doc search,
 *        Harold used to send file with all AUIDS list with corresponding
 *        doc-count from Fast for us to compare with previous week list to find
 *        updated doc_count and so re-index those impacted author profiles to
 *        AWS elsaticsearch. Since we migrating from fast to ES, shared search
 *        service can't provide the same list as it used to be for Fast after
 *        discussion with Hawk team and EV team, the temp solution would be only
 *        get list of AUids/AFids for CPX docs processed for current week only,
 *        instead of checking all millions of auids/afids. By this we can limit
 *        # of Ids to check and so wont cause overhead on shared search
 *        service/ES
 * 
 *        Weekly CPX data will include (BD new, update, delete, cafe update,
 *        delete)
 * 
 */
public class FetchWeeklyAuAfIdsForES {

	String url = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_xml";
	String password = "";
	/* ONLY to know current weeknumber */
	int weekNumber;

	String dirName = null;
	File dir = null;
	String auIdsFileName = null;
	String afIdsFileName = null;

	Connection con;
	ResultSet rs;
	Statement stmt;

	FileWriter auIdsOut, afIdsOut;
	Logger logger = Logger.getLogger(FetchWeeklyAuAfIdsForES.class);

	public FetchWeeklyAuAfIdsForES() {
	}

	public FetchWeeklyAuAfIdsForES(Connection con, int weekNumber) {
		this.con = con;
		this.weekNumber = weekNumber;
	}
	
	public FetchWeeklyAuAfIdsForES(String url, String driver, String username, String password, int weekNumber) {
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.weekNumber = weekNumber;
	}

	/* Only to initialize out files names */
	public String[] init() {
		/* to be used for searchEntry when called from StartWeeklyAuAfIdsForES */
		String[] fileNames = new String[2];

		/* create dir to hold out files */
		dirName = System.getProperty("user.dir");
		dirName = dirName.concat(File.separator).concat("weekly_auafid");
		dir = new File(dirName);
		if (!dir.exists())
			dir.mkdir();

		auIdsFileName = "weekly_auids_" + weekNumber + ".txt";
		afIdsFileName = "weekly_afids_" + weekNumber + ".txt";

		fileNames[0] = dir.getAbsolutePath() + File.separator + auIdsFileName;
		fileNames[1] = dir.getAbsolutePath() + File.separator + afIdsFileName;
		return fileNames;
	}

	public void fetchWeeklyIds() {

		try {
			// configure log
			String log4jFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "resources"
					+ File.separator + "log4j.properties";
			PropertyConfigurator.configure(log4jFile);

			/* monitor time consumed to fetch Ids and process write to out files */
			long startTime = System.currentTimeMillis();

			String query = "select pui,author,author_1, affiliation, affiliation_1, loadnumber,updatenumber, count(*) over() total_rows from cafe_master where pui in "
					+ "(select distinct a.pui from cafe_master a, cafe_pui_list_master b, cpx_weekly_load c where a.pui = b.pui and b.puisecondary = c.pui) and rownum<11";
			//con = getConnection(url, driver, username, password);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			if (rs != null) {
				processInfo(rs);
			}

			long finishTime = System.currentTimeMillis();
			System.out.println("Total Time to fetch all ids from oracle for weekly cpx load: "
					+ Long.valueOf((finishTime - startTime) / 1000) + " seconds");

		}

		catch (SQLException ex) {
			logger.error("Exception running sql stmt!!!");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Initialize out files to hold auids and AfIds info, read data from RS & write
	 * to out files
	 */
	public void processInfo(ResultSet rs) {

		try (BufferedWriter bwAuid = new BufferedWriter(
				new FileWriter(new File(dir.getAbsolutePath() + File.separator + auIdsFileName)));
				BufferedWriter bwAfid = new BufferedWriter(
						new FileWriter(new File(dir.getAbsolutePath() + File.separator + afIdsFileName)))) {
			while (rs.next()) {
				String authors;
				String affiliations;

				/* find auids & write to out files */
				authors = rs.getString("AUTHOR");
				if (authors != null && !authors.isEmpty()) {
					if (rs.getString("AUTHOR_1") != null && !rs.getString("AUTHOR_1").isEmpty())
						authors = authors + rs.getString("AUTHOR_1");

					BdAuthors aus = new BdAuthors(authors);
					List<String> auIds = aus.getAuids();
					if (auIds.size() > 0)
						for (String auid : auIds)
							// bwAuid.write(auid + "\t" + rs.getString("LOADNUMBER") + "\t" +
							// rs.getString("UPDATENUMBER") + "\n");
							bwAuid.write(auid + "\n");
				}
				/* find afids & write to out files */
				affiliations = rs.getString("AFFILIATION");
				if (affiliations != null && !affiliations.isEmpty()) {
					if (rs.getString("AFFILIATION_1") != null && !rs.getString("AFFILIATION_1").isEmpty())
						affiliations = affiliations + rs.getString("AFFILIATION_1");

					BdAffiliations aff = new BdAffiliations(affiliations);
					List<String> afIds = aff.getAfids();
					// System.out.println(afIds.size());
					if (afIds.size() > 0)
						for (String afId : afIds)
							// bwAfid.write(afId + "\t" + rs.getString("LOADNUMBER") + "\t" +
							// rs.getString("UPDATENUMBER") + "\n");
							bwAfid.write(afId + "\n");
				}

			}

			System.out.println("Process Complete....");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	/*
	 * create db connection private Connection getConnection(String connectionURL,
	 * String driver, String username, String password) throws Exception {
	 * Class.forName(driver); Connection con =
	 * DriverManager.getConnection(connectionURL, username, password); return con; }
	 */

}
