package org.ei.struts.backoffice.customer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.region.Region;
import org.ei.struts.backoffice.territory.Territory;
import org.ei.struts.backoffice.territory.Territories;

public class SearchBroker {

	private Log log = LogFactory.getLog("SearchBroker");

	public Collection getCustomerView(
		String[] accessType,
		String[] status,
		String customerName,
		String[] salesRegion,
		String[] customerType,
		String[] contractType,
		String[] products,
		String action,
		String active,
		String startFromDay,
		String startFromMonth,
		String startFromYear,
		String startToDay,
		String startToMonth,
		String startToYear,
		String endFromDay,
		String endFromMonth,
		String endFromYear,
		String endToDay,
		String endToMonth,
		String endToYear)
		throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();
		String sqlString = null;
		StringBuffer fromString = new StringBuffer();
		String whereClause = null;
		whereClause =
			constructWhereClause(
				accessType,
				status,
				customerName,
				salesRegion,
				customerType,
				contractType,
				products,
				action,
				active,
				startFromDay,
				startFromMonth,
				startFromYear,
				startToDay,
				startToMonth,
				startToYear,
				endFromDay,
				endFromMonth,
				endFromYear,
				endToDay,
				endToMonth,
				endToYear);

		try {
			if ((whereClause != null) && (whereClause.length() > 0)) {
				fromString.append(" from ");
				if (whereClause.indexOf("a.") > 0) {
					fromString.append(" customer_master a ");
					fromString.append(" , sales_region s ");
				}
				if (whereClause.indexOf("b.") > 0) {
					fromString.append(" ,contract b ");
				}
				if (whereClause.indexOf("d.") > 0) {
					fromString.append(" ,contact d ");
				}
				if (whereClause.indexOf("e.") > 0) {
					fromString.append(" ,cust_contact e ");
				}
				if (whereClause.indexOf("f.") > 0) {
					fromString.append(" ,contract_data f ");
				}

				sqlString =
					"select distinct(a.customer_id),a.*, s.region "
						+ fromString.toString()
						+ whereClause
						+ " order by customer_name";
			} else {
				sqlString =
					"select * from customer_master  order by customer_name";
			}

			log.info("sqlString = " + sqlString);
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =	(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(sqlString);
			int i = 0;
			while (rs.next()) {
				if ((i >= Integer.parseInt(Constants.pageSize))
					&& (action == null)) {
					break;
				}
				String custID = rs.getString("customer_id");
				Customer customer = new Customer();
				customer.setCustomerID(custID);
				customer.setName(
					rs.getString("customer_name") == null
						? Constants.EMPTY_STRING
						: rs.getString("customer_name"));
				customer.setType(rs.getString("customer_type"));
				customer.setConsortium(
					rs.getString("parent_company") == null
						? Constants.EMPTY_STRING
						: rs.getString("parent_company"));
				customer.setRegion(rs.getInt("region_code"));
				
				Region region = new Region();
				region.setRegionID(String.valueOf(rs.getInt("region_code")));
				region.setRegionName(rs.getString("region"));
				customer.setSalesRegion(region);
				
				results.add(customer);
				i++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getCustomerView throw SQLException " + ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getNextAccessID", e);
			}
		}
		return results;
	}

	public Collection getPossibleCustomerView(
		String customerName,
		String[] salesRegion,
		String[] customerType,
		String[] contractType,
		String[] products,
		String consortiumType,
		String active,
		String startFromDay,
		String startFromMonth,
		String startFromYear,
		String startToDay,
		String startToMonth,
		String startToYear,
		String endFromDay,
		String endFromMonth,
		String endFromYear,
		String endToDay,
		String endToMonth,
		String endToYear)
		throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();
		String sqlString = null;
		try {
			String cName = customerName;
			cName = strippedInvalidCharacters(cName);
			if (cName.indexOf("'") > 0) {
				int iPos = cName.indexOf("'");
				while (iPos > 0) {
					cName =
						cName.substring(0, iPos)
							+ "'"
							+ cName.substring(iPos, cName.length());
					iPos = cName.indexOf("'", iPos + 2);
				}
			}
			if ((cName.trim()).length() <= 0) {
				return results;
			}
			sqlString =
				"select * from customer_master  where CONTAINS(customer_name,'?"
					+ cName
					+ "',1)>0  order by customer_name";
			log.info("sqlString= " + sqlString);
			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(sqlString);
			int i = 0;
			while (rs.next()) {
				if (i >= Integer.parseInt(Constants.pageSize)) {
					break;
				}
				String custID = rs.getString("customer_id");
				Customer customer = new Customer();
				customer.setCustomerID(custID);
				customer.setName(
					rs.getString("customer_name") == null
						? Constants.EMPTY_STRING
						: rs.getString("customer_name"));
				customer.setType(rs.getString("customer_type"));
				customer.setConsortium(
					rs.getString("parent_company") == null
						? Constants.EMPTY_STRING
						: rs.getString("parent_company"));
				customer.setRegion(rs.getInt("region_code"));
				results.add(customer);
				i++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getPossibleCustomerView throw SQLException "
					+ ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getNextAccessID", e);
			}
		}
		return results;
	}

	public Collection getPossibleCustomerByContact(String contactName)
		throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Collection results = new ArrayList();
		String sqlString = null;
		String sqlContactString = null;
		StringBuffer contactIDString = new StringBuffer();
		try {
			String cName = contactName;
			cName = strippedInvalidCharacters(cName);
			if (cName.indexOf("'") > 0) {
				int iPos = cName.indexOf("'");
				while (iPos > 0) {
					cName =
						cName.substring(0, iPos)
							+ "'"
							+ cName.substring(iPos, cName.length());
					iPos = cName.indexOf("'", iPos + 2);
				}
			}
			if ((cName.trim()).length() <= 0) {
				return results;
			}
			sqlContactString =
				"select contact_id from contact where contains(first_name,'?"
					+ cName
					+ "',1)>0 union select contact_id from contact where contains(last_name,'?"
					+ cName
					+ "',1)>0";
			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			rs1 = statement.executeQuery(sqlContactString);
			while (rs1.next()) {
				contactIDString.append(rs1.getString("contact_id") + ",");
			}
			if (contactIDString.length() > 0) {
				contactIDString.delete(
					contactIDString.length() - 1,
					contactIDString.length());
				sqlString =
					"select distinct(a.customer_id),a.* from customer_master a,cust_contact b where a.customer_id=b.customer_id and b.contact_id in ("
						.concat(contactIDString.toString())
						.concat(")");
				rs = statement.executeQuery(sqlString);
				int i = 0;
				while (rs.next()) {
					if (i >= Integer.parseInt(Constants.pageSize)) {
						break;
					}
					String custID = rs.getString("customer_id");
					Customer customer = new Customer();
					customer.setCustomerID(custID);
					customer.setName(
						rs.getString("customer_name") == null
							? Constants.EMPTY_STRING
							: rs.getString("customer_name"));
					customer.setType(rs.getString("customer_type"));
					customer.setRegion(rs.getInt("region_code"));

					results.add(customer);
					i++;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getPossibleCustomerByContact throw SQLException "
					+ ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("getNextAccessID", e);
			}
		}
		return results;
	}

	public Collection getCustomerByContact(String contactName)
		throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();
		String sqlString = null;
		try {
			if (contactName.indexOf("%") >= 0) {
				sqlString =
					"select distinct(e.customer_id),e.* from contact a,cust_contact b,customer_master e where a.contact_id=b.contact_id and b.customer_id=e.customer_id and (upper(a.first_name) like upper('"
						+ contactName.trim()
						+ "') or upper(a.last_name) like upper('"
						+ contactName.trim()
						+ "'))  order by e.customer_name";
			} else {
				sqlString =
					"select distinct(e.customer_id),e.* from contact a,cust_contact b,customer_master e where a.contact_id=b.contact_id and b.customer_id=e.customer_id and (upper(a.first_name)=upper('"
						+ contactName.trim()
						+ "') or upper(a.last_name)=upper('"
						+ contactName.trim()
						+ "'))  order by e.customer_name";
			}
			int checkNumber = 0;
			try {
				checkNumber = Integer.parseInt(contactName);
				checkNumber = 1;
			} catch (Exception e) {
				checkNumber = 0;
			}
			if (checkNumber == 1) {
				sqlString =
					"(select * from customer_master where customer_id="
						+ contactName
						+ " or goldmine_id="
						+ contactName
						+ ") union (select a.* from customer_master a,contract b where a.customer_id=b.customer_id and (b.contract_number="
						+ contactName
						+ " or b.customer_id="
						+ contactName
						+ "))";
			}
			log.info("getCustomerByContact()  - sqlString = " + sqlString);
			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(sqlString);
			int i = 0;
			while (rs.next()) {
				if (i >= Integer.parseInt(Constants.pageSize)) {
					break;
				}
				String custID = rs.getString("customer_id");
				Customer customer = new Customer();
				customer.setCustomerID(custID);
				customer.setName(
					rs.getString("customer_name") == null
						? Constants.EMPTY_STRING
						: rs.getString("customer_name"));
				customer.setType(rs.getString("customer_type"));
				customer.setConsortium(
					rs.getString("parent_company") == null
						? Constants.EMPTY_STRING
						: rs.getString("parent_company"));
				customer.setRegion(rs.getInt("region_code"));
				results.add(customer);
				i++;
			}
			results.addAll(getCustomerByUsername(contactName));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getCustomerByContact throw SQLException " + ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("SearchBroker.getCustomerByContact", e);
			}
		}
		return results;
	}

	public Collection getCustomerByIP(String ipAddress) throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();
		String sqlString = null;
		try {

			sqlString =
				"select * from customer_master where customer_id in (select cust_id from ip_data  where high_ip>="
					.concat(ipAddress)
					.concat(" and low_ip<=")
					.concat(ipAddress)
					.concat(")");
			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			log.info("sqlString= " + sqlString);
			rs = statement.executeQuery(sqlString);
			int i = 0;
			while (rs.next()) {
				if (i >= Integer.parseInt(Constants.pageSize)) {
					break;
				}
				String custID = rs.getString("customer_id");
				Customer customer = new Customer();
				customer.setCustomerID(custID);
				customer.setName(
					rs.getString("customer_name") == null
						? Constants.EMPTY_STRING
						: rs.getString("customer_name"));
				customer.setType(rs.getString("customer_type"));
				customer.setConsortium(
					rs.getString("parent_company") == null
						? Constants.EMPTY_STRING
						: rs.getString("parent_company"));
				customer.setRegion(rs.getInt("region_code"));
				results.add(customer);
				i++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getCustomerByIP throw SQLException " + ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("SearchBroker.getCustomerByIP", e);
			}
		}
		return results;
	}

	public Collection getCustomerByUsername(String username)
		throws SQLException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();
		String sqlString = null;
		try {
			if (username.indexOf("%") >= 0) {
				sqlString =
					"select distinct(a.customer_id),a.* from customer_master a,user_pass_data b where a.customer_id=b.cust_id and b.username like'"
						+ username
						+ "'";
			} else {
				sqlString =
					"select distinct(a.customer_id),a.* from customer_master a,user_pass_data b where a.customer_id=b.cust_id and b.username='"
						+ username
						+ "'";
			}
			Context initCtx = new InitialContext();
			Context envCtx =
				(Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
			DataSource ds =
				(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
			conn = ds.getConnection();
			statement = conn.createStatement();
			//log.info("sqlString= "+sqlString);
			rs = statement.executeQuery(sqlString);
			int i = 0;
			while (rs.next()) {
				if (i >= Integer.parseInt(Constants.pageSize)) {
					break;
				}
				String custID = rs.getString("customer_id");
				Customer customer = new Customer();
				customer.setCustomerID(custID);
				customer.setName(
					rs.getString("customer_name") == null
						? Constants.EMPTY_STRING
						: rs.getString("customer_name"));
				customer.setType(rs.getString("customer_type"));
				customer.setConsortium(
					rs.getString("parent_company") == null
						? Constants.EMPTY_STRING
						: rs.getString("parent_company"));
				customer.setRegion(rs.getInt("region_code"));
				results.add(customer);
				i++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SQLException(
				"SearchBroker.getCustomerByUsername throw SQLException " + ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				log.error("SearchBroker.getCustomerByUsername", e);
			}
		}
		return results;
	}

	public String constructWhereClause(
		String[] accessType,
		String[] status,
		String customerName,
		String[] salesRegion,
		String[] customerType,
		String[] contractType,
		String[] products,
		String action,
		String active,
		String startFromDay,
		String startFromMonth,
		String startFromYear,
		String startToDay,
		String startToMonth,
		String startToYear,
		String endFromDay,
		String endFromMonth,
		String endFromYear,
		String endToDay,
		String endToMonth,
		String endToYear) {
		StringBuffer whereClause = new StringBuffer();
		String customerNameClause = Constants.EMPTY_STRING;
		StringBuffer salesRegionClause = new StringBuffer();
		StringBuffer customerTypeClause = new StringBuffer();
		StringBuffer contractTypeClause = new StringBuffer();
		StringBuffer productsClause = new StringBuffer();
		StringBuffer accessTypeClause = new StringBuffer();
		StringBuffer statusClause = new StringBuffer();
		//String consortiumTypeClause=null;
		String activeClause = null;
		String startFromDateClause = null;
		String startToDateClause = null;
		String endFromDateClause = null;
		String endToDateClause = null;

		if ((customerName != null) && (customerName.length() > 0)) {
			String cName = customerName.trim();

			if (cName.indexOf("'") > 0) {
				int iPos = cName.indexOf("'");
				while (iPos > 0) {
					cName =
						cName.substring(0, iPos)
							+ "'"
							+ cName.substring(iPos, cName.length());
					iPos = cName.indexOf("'", iPos + 2);
				}
			}

			if (cName.indexOf("%") >= 0) {
				customerNameClause =
					customerNameClause
						+ " upper(a.customer_name) like upper('"
						+ cName
						+ "')";
			} else {
				customerNameClause =
					customerNameClause
						+ " upper(a.customer_name)=upper('"
						+ cName
						+ "')";
			}
		} //if

		if ((salesRegion != null) && (salesRegion.length > 0)) {
			if ((salesRegion.length > 0)
				&& (!salesRegion[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < salesRegion.length; i++) {
					if (salesRegion[i].length() > 0) {
						salesRegionClause.append(
							" a.region_code=" + salesRegion[i]);
					}
					if (i < (salesRegion.length - 1)) {
						salesRegionClause.append(" or ");
					} //if
				} //for
			}
		}

		if ((customerType != null) && (customerType.length > 0)) {
			if ((customerType.length > 0)
				&& (!customerType[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < customerType.length; i++) {
					if (customerType[i].length() > 0) {
						customerTypeClause.append(
							" upper(a.customer_type)=upper('"
								+ customerType[i]
								+ "')");
					}
					if (i < (customerType.length - 1)) {
						customerTypeClause.append(" or ");
					} //if
				} //for
			}

		}

		if ((contractType != null) && (contractType.length > 0)) {
			if ((contractType.length > 0)
				&& (!contractType[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < contractType.length; i++) {
					if (contractType[i].length() > 0) {
						contractTypeClause.append(
							" upper(b.contract_type)=upper('"
								+ contractType[i]
								+ "')");
					}
					if (i < (contractType.length - 1)) {
						contractTypeClause.append(" or ");
					} //if
				} //for
			}

		}

		if ((accessType != null) && (accessType.length > 0)) {
			if ((accessType.length > 0)
				&& (!accessType[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < accessType.length; i++) {
					if (accessType[i].length() > 0) {
						accessTypeClause.append(
							" upper(b.status)=upper('" + accessType[i] + "')");
					}
					if (i < (accessType.length - 1)) {
						accessTypeClause.append(" or ");
					} //if
				} //for
			}
		}

		if ((status != null) && (status.length > 0)) {
			if ((status.length > 0)
				&& (!status[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < status.length; i++) {
					if (status[i].length() > 0) {
						statusClause.append(
							" upper(b.cnvt_status)=upper('" + status[i] + "')");
					}
					if (i < (status.length - 1)) {
						statusClause.append(" or ");
					} //if
				} //for
			}

		}

		if ((products != null) && (products.length > 0)) {
			if ((products.length > 0)
				&& (!products[0].equals(Constants.EMPTY_STRING))) {
				for (int i = 0; i < products.length; i++) {
					if (products[i].length() > 0) {
						productsClause.append(" b.product_id=" + products[i]);
					}
					if (i < (products.length - 1)) {
						productsClause.append(" or ");
					} //if
				} //for
			}

		}

		if ((active != null) && (active.length() > 0)) {
			//activeClause=" b.end_date >= sysdate";
			// jam 6/2/2004 - changed active to mean not within dates 
			// but wether or not the contract has been turned off 
			activeClause =
				" b.contract_number = f.contract_id and f.STATUS='Y'";
		} //if
		/*
		    if((consortiumType!=null)&&(consortiumType.length()>0))
		    {
		        if(consortiumType.equals("yes"))
		        {
		            consortiumTypeClause=" a.parent_company is not null";
		        }
		        else
		        {
		            consortiumTypeClause=" a.parent_company is null";
		        }
		    }//if
		*/

		if (((startToDay != null)
			&& (!startToDay.equals(Constants.EMPTY_STRING)))
			&& ((startToMonth != null)
				&& (!startToMonth.equals(Constants.EMPTY_STRING)))
			&& ((startToYear != null)
				&& (!startToYear.equals(Constants.EMPTY_STRING)))) {
			startToDateClause =
				"b.start_date<=TO_DATE('"
					+ startToDay
					+ "/"
					+ startToMonth
					+ "/"
					+ startToYear
					+ "','dd/mm/yyyy')";
		} //i

		if ((startFromDay != null)
			&& (!startFromDay.equals(Constants.EMPTY_STRING))
			&& (startFromMonth != null)
			&& (!startFromMonth.equals(Constants.EMPTY_STRING))
			&& (startFromYear != null)
			&& (!startFromYear.equals(Constants.EMPTY_STRING))) {
			startFromDateClause =
				"b.start_date>=TO_DATE('"
					+ startFromDay
					+ "/"
					+ startFromMonth
					+ "/"
					+ startFromYear
					+ "','dd/mm/yyyy')";
		} //i

		if ((endToDay != null)
			&& (!endToDay.equals(Constants.EMPTY_STRING))
			&& (endToMonth != null)
			&& (!endToMonth.equals(Constants.EMPTY_STRING))
			&& (endToYear != null)
			&& (!endToYear.equals(Constants.EMPTY_STRING))) {
			endToDateClause =
				"b.end_date<=TO_DATE('"
					+ endToDay
					+ "/"
					+ endToMonth
					+ "/"
					+ endToYear
					+ "','dd/mm/yyyy')";
		} //i

		if (((endFromDay != null)
			&& (!endFromDay.equals(Constants.EMPTY_STRING)))
			&& ((endFromMonth != null)
				&& (!endFromMonth.equals(Constants.EMPTY_STRING))
				&& ((endFromYear != null)
					&& (!endFromYear.equals(Constants.EMPTY_STRING))))) {
			endFromDateClause =
				"b.end_date>=TO_DATE('"
					+ endFromDay
					+ "/"
					+ endFromMonth
					+ "/"
					+ endFromYear
					+ "','dd/mm/yyyy')";
		} //i

		if ((customerNameClause != null)
			&& (!customerNameClause.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(customerNameClause).append(
					")");
			} else {
				whereClause.append(" (").append(customerNameClause).append(")");
			}
		}

		if (salesRegionClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					salesRegionClause.toString()).append(
					")");
			} else {
				whereClause.append(" (").append(
					salesRegionClause.toString()).append(
					")");
			}
		}

		if (customerTypeClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					customerTypeClause.toString()).append(
					")");
			} else {
				whereClause.append(" (").append(
					customerTypeClause.toString()).append(
					")");
			}
		}

		if (contractTypeClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					contractTypeClause.toString()).append(
					")");
			} else {
				whereClause.append("  (").append(
					contractTypeClause.toString()).append(
					")");
			}
		}

		if (accessTypeClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					accessTypeClause.toString()).append(
					")");
			} else {
				whereClause.append("  (").append(
					accessTypeClause.toString()).append(
					")");
			}
		}

		if (statusClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					statusClause.toString()).append(
					")");
			} else {
				whereClause.append("  (").append(
					statusClause.toString()).append(
					")");
			}
		}

		if (productsClause.length() > 0) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					productsClause.toString()).append(
					")");
			} else {
				whereClause.append(" (").append(
					productsClause.toString()).append(
					")");
			}
		}
		/*
		    if((consortiumType!=null)&&(!consortiumType.equals(Constants.EMPTY_STRING)))
		    {
		        if(whereClause.length()>1)
		        {
		            whereClause.append(" and (").append(consortiumTypeClause+") ");
		        }
		        else
		        {
		            whereClause.append("  (").append(consortiumTypeClause+") ");
		        }
		    }
		*/

		if ((active != null) && (!active.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(activeClause).append(") ");
			} else {
				whereClause.append(" (").append(activeClause).append(") ");
			}
		}

		if ((startToDateClause != null)
			&& (!startToDateClause.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(startToDateClause).append(
					")");
			} else {
				whereClause.append(" (").append(startToDateClause).append(")");
			}
		}

		if ((startFromDateClause != null)
			&& (!startFromDateClause.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(
					startFromDateClause).append(
					")");
			} else {
				whereClause.append("  (").append(startFromDateClause).append(
					")");
			}
		}

		if ((endToDateClause != null)
			&& (!endToDateClause.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(endToDateClause).append(
					")");
			} else {
				whereClause.append(" (").append(endToDateClause).append(")");
			}
		}

		if ((endFromDateClause != null)
			&& (!endFromDateClause.equals(Constants.EMPTY_STRING))) {
			if (whereClause.length() > 1) {
				whereClause.append(" and (").append(endFromDateClause).append(
					")");
			} else {
				whereClause.append(" and (").append(endFromDateClause).append(
					")");
			}
		}

		StringBuffer jointTableString = new StringBuffer();
		jointTableString.append(" where ");

		if ((whereClause.toString().indexOf("c.") > 0)
			&& (whereClause.toString().indexOf("b.") < 1)) {
			jointTableString.append(" a.customer_id=b.customer_id and ");
		}
		if (whereClause.toString().indexOf("b.") > 0) {
			jointTableString.append(" a.customer_id=b.customer_id and ");
		}

        jointTableString.append(" s.REGION_CODE=a.REGION_CODE and ");

		if (whereClause.length() > 0) {

			{
				whereClause.insert(0, jointTableString.toString());
			}
		}
		return whereClause.toString();
	}

	public String strippedInvalidCharacters(String inString) {
            
        if(inString != null)
        {
		    // \p{Punct} Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ 
		    // \p{Cntrl} A control character: [\x00-\x1F\x7F] 
		    inString = inString.replaceAll("\\p{Punct}|\\p{Cntrl}","");
        }

        return inString;
	}


	public Collection getTerritoryView(String territoryid)
		throws SQLException, NamingException {

		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		Collection results = new ArrayList();

        if(Territories.getTerritory(territoryid) != null)
        {
             
    		try {
    			Context initCtx = new InitialContext();
    			Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
    			DataSource ds =	(DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
    			conn = ds.getConnection();
    
                String sql = Territories.getTerritory(territoryid).toQueryString();
    			statement = conn.createStatement();
    			rs = statement.executeQuery(sql);

    			int i = 0;
    			while (rs.next()) {
    				if (i >= Integer.parseInt(Constants.pageSize)) {
    					break;
    				}
    				String custID = rs.getString("customer_id");
    				Customer customer = new Customer();
    				customer.setCustomerID(custID);
    				customer.setName(
    					rs.getString("customer_name") == null
    						? Constants.EMPTY_STRING
    						: rs.getString("customer_name"));
    				customer.setType(rs.getString("customer_type"));
    				customer.setConsortium(
    					rs.getString("parent_company") == null
    						? Constants.EMPTY_STRING
    						: rs.getString("parent_company"));
    				customer.setRegion(rs.getInt("region_code"));
    				
    				Region region = new Region();
    				region.setRegionID(String.valueOf(rs.getInt("region_code")));
    				region.setRegionName(rs.getString("region"));
    				customer.setSalesRegion(region);
    				
    				results.add(customer);
    				i++;
    			}
    
    		} catch (NamingException ne) {
    			log.error("getTerritoryView()", ne);
    			throw ne;
    		} catch (SQLException ex) {
    			log.error("getTerritoryView()", ex);
    			throw ex;
    		} finally {
    			try {
    				if (rs != null)
    					rs.close();
    				if (statement != null)
    					statement.close();
    				if (conn != null)
    					conn.close();
    			} catch (SQLException e) {
    				log.error("getTerritoryView", e);
    			}
    		}
        }
		return results;
	}
}
