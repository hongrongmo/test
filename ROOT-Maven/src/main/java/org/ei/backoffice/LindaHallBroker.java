/** This file has methods required for client customization.
As of now its used for Linda hall Library **/

package org.ei.backoffice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ei.biz.personalization.IEVWebUser;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.ContactInfo;
import org.ei.domain.LhlException;
import org.ei.domain.LhlUserInfo;
import org.ei.session.UserBroker;
import org.ei.util.StringUtil;

public class LindaHallBroker {
    private static final String SESSION_POOL = "search";
    private static final String m_strProductId = "9004";
    private static final String m_strProductName = "DDS";

    public LindaHallBroker() {

    }

    @Deprecated
    public boolean authenticateByPassword(String strCustomerID, String strPassword, String strContractId) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean flag = false;
        String conId = StringUtil.EMPTY_STRING;
        try {
            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer sqlQuery = new StringBuffer("SELECT CONTRACT_ID FROM USER_PASS_DATA WHERE ");
            sqlQuery.append(" CUST_ID=").append(strCustomerID).append(" AND ").append(" CONTRACT_ID=").append(strContractId).append(" AND ")
                .append(" PROD_ID=").append(m_strProductId).append(" AND ").append(" PASSWORD='").append(strPassword).append("'");

            rs = stmt.executeQuery(sqlQuery.toString());
            if (rs.next() == true) {
                conId = rs.getString("CONTRACT_ID");
            }
            flag = conId.equals(strContractId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }// end of finally
        return flag;

    }

    /** This method is called when the user needs to be authenticated by IPADDRESS */
    @Deprecated
    public boolean authenticateByIp(String ipAddress, String strContractId) throws LhlException {
        boolean flag = false;

        try {
            // use the pre-existing user classes to retreive a user object
            // by the ip address
            // compare the two users and then compare the created
            // user's contract id to that of this (LindalHall) object
            // if all match up, return true
            IEVWebUser aUser = (new UserBroker()).getUser(m_strProductName, null, null, ipAddress, null, null);

            flag = strContractId.equals(aUser.getContractID());
        } catch (Exception e) {

            e.printStackTrace();
            throw new LhlException(e);
        }

        return flag;
    }

    /**
     * This method returns a LHLUserInfo object which has the information of a linda hall User It first checks the contract table to see whether this customer
     * has the productId that is sent to the method as a parameter.If the product id matches it talks to the DDS table and gets the userInfo,builds the User
     * Info object which in builds a Contact Info object
     */
    @Deprecated
    public LhlUserInfo getLhlUserInfo(String strCustomerID, String strContractID) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        LhlUserInfo userInfo = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();
            String prodId = StringUtil.EMPTY_STRING;

            StringBuffer sBuffer = new StringBuffer("SELECT PROD_ID FROM USER_PASS_DATA WHERE CONTRACT_ID='");

            sBuffer.append(strContractID).append("'  AND ").append(" CUST_ID='").append(strCustomerID).append("'  AND ").append(" PROD_ID='")
                .append(m_strProductId).append("'");

            rs = stmt.executeQuery(sBuffer.toString());

            if (rs.next()) {
                prodId = rs.getString("PROD_ID");
            }
            // "Authorize" to make sure the passed in product ID matches
            // the product ID that was retreived from the DB
            if (prodId.equals(m_strProductId)) {
                userInfo = this.buildUserInfo(strCustomerID, strContractID);
            } else {
                throw new LhlException(new Exception("LindaHall\nProductId=" + m_strProductId + "\nUser= " + strCustomerID + "\ncontract=" + strContractID
                    + "\nprodId=" + prodId));
            }

        } catch (Exception e) {
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }// end of finally

        return userInfo;

    }

    /*
     * Building User Info object.A contact Info object is first builtand later an UserInfo object is built using this contactInfo objectprivate method called by
     * the getLhluserInfo method
     */
    @Deprecated
    private LhlUserInfo buildUserInfo(String strCustomerID, String strContractID) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        LhlUserInfo userInfo = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();
            String shipping = StringUtil.EMPTY_STRING;
            String userFName = StringUtil.EMPTY_STRING;
            String userLName = StringUtil.EMPTY_STRING;
            String userEmail = StringUtil.EMPTY_STRING;
            String userCreatedDate = StringUtil.EMPTY_STRING;
            String address1 = StringUtil.EMPTY_STRING;
            String company = StringUtil.EMPTY_STRING;
            String city = StringUtil.EMPTY_STRING;
            String state = StringUtil.EMPTY_STRING;
            String zip = StringUtil.EMPTY_STRING;
            String phoneNumber = StringUtil.EMPTY_STRING;
            String faxNumber = StringUtil.EMPTY_STRING;
            String country = StringUtil.EMPTY_STRING;
            String address2 = StringUtil.EMPTY_STRING;

            StringBuffer getBuffer = new StringBuffer("SELECT * FROM DDS_DATA WHERE CONTRACT_ID='");
            getBuffer.append(strContractID);
            getBuffer.append("'");

            // Building the LhlUser Info object
            userInfo = new LhlUserInfo();
            userInfo.setCustomerID(Long.parseLong(strCustomerID));
            userInfo.setContractID(Long.parseLong(strContractID));

            rs = stmt.executeQuery(getBuffer.toString());

            if (rs.next()) {
                // Building the contactInfo object
                // This SHOULD REALLY NOT BE HERE
                // ContactInfo should just separated into its own
                // Builder/Factory, etc.
                ContactInfo contactInfo = new ContactInfo();

                userFName = rs.getString("USER_FIRST_NAME");
                if (userFName != null) {
                    contactInfo.setUserFirstName(userFName);
                }

                userLName = rs.getString("USER_LAST_NAME");
                if (userLName != null) {
                    contactInfo.setUserLastName(userLName);
                }

                userEmail = rs.getString("USER_EMAIL");
                if (userEmail != null) {
                    contactInfo.setUserEmail(userEmail);
                }

                company = rs.getString("COMPANY");
                if (company != null) {
                    contactInfo.setCompany(company);
                }

                address1 = rs.getString("ADDRESS");
                if (address1 != null) {
                    contactInfo.setAddress1(address1);
                }

                city = rs.getString("CITY");
                if (city != null) {
                    contactInfo.setCity(city);
                }
                state = rs.getString("STATE");
                if (state != null) {
                    contactInfo.setState(state);
                }

                zip = rs.getString("ZIP");
                if (zip != null) {
                    contactInfo.setZip(zip);
                }

                phoneNumber = rs.getString("PHONE_NUMBER");
                if (phoneNumber != null) {
                    contactInfo.setPhoneNumber(phoneNumber);
                }

                faxNumber = rs.getString("FAX_NUMBER");
                if (faxNumber != null) {
                    contactInfo.setFaxNumber(faxNumber);
                }

                country = rs.getString("COUNTRY");
                if (country != null) {
                    contactInfo.setCountry(country);
                }

                address2 = rs.getString("ADDRESS2");
                if (address2 != null) {
                    contactInfo.setAddress2(address2);
                }

                // adding the contactInfo object to LhlUserInfo
                userInfo.setContactInfo(contactInfo);

                // set the local properties
                shipping = rs.getString("SHIPPING");
                if (shipping != null) {
                    userInfo.setShipping(shipping.toLowerCase());
                }

                userCreatedDate = rs.getString("CREATED_DATE");
                if (userCreatedDate != null) {
                    java.util.Date dtetCreatedDate = StringUtil.getDateFromString("yyyy-MM-dd hh:mm:ss.S", userCreatedDate);
                    userInfo.setCreatedDate(dtetCreatedDate);
                }

                // The remaining local properties are set through these
                // local methods which have their own separate queries
                String strPoductName = this.getProductName(strCustomerID, strContractID);
                String strAccessType = this.getAccessType(strCustomerID, strContractID);

                userInfo.setProductName(strPoductName);
                userInfo.setAccessType(strAccessType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }// end of finally
        return userInfo;

    }

    /**
     * a method which inserts data into the order table.this table contains all the info of the order
     */
    public boolean sendOrderInfo(LhlUserInfo lhlUserInfo, String docInfo, String attention, String shipInfo, String deliveryValue, String serviceLevel,
        String orderNumber, String confirmedEmail) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean flag = false;
        PreparedStatement pstmt = null;

        try {

            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();

            String firstName = lhlUserInfo.getContactInfo().getUserFirstName();
            String lastName = lhlUserInfo.getContactInfo().getUserLastName();
            String userEmail = lhlUserInfo.getContactInfo().getUserEmail();
            String company = lhlUserInfo.getContactInfo().getCompany();
            String address1 = lhlUserInfo.getContactInfo().getAddress1();
            String city = lhlUserInfo.getContactInfo().getCity();
            String state = lhlUserInfo.getContactInfo().getState();
            String zip = lhlUserInfo.getContactInfo().getZip();
            String phoneNumber = lhlUserInfo.getContactInfo().getPhoneNumber();
            String faxNumber = lhlUserInfo.getContactInfo().getFaxNumber();
            String accountNumber = lhlUserInfo.getContactInfo().getAccountNumber();
            String country = lhlUserInfo.getContactInfo().getCountry();
            String address2 = lhlUserInfo.getContactInfo().getAddress2();
            StringUtil.getFormattedDate();
            String status = "P";

            String billingName = firstName.concat(lastName);
            String billingStreet = address1;
            String billingStreet2 = address2;
            String billingCity = city;
            String billingState = state;
            String billingCountry = country;
            String billingZip = zip;
            String billingPhone = phoneNumber;
            String billingFax = faxNumber;

            StringBuffer updateBuffer = new StringBuffer("INSERT INTO ORDER_DATA(CUST_ID,CONTRACT_ID,"); // 2
            updateBuffer.append("ORDER_NUMBER,COST,DOC_INFO,FIRST_NAME,LAST_NAME,SHIPMENT_INFO,"); // 6
            updateBuffer.append("DELIVERY_VALUE,FEDEX,SERVICE_LEVEL,ATTENTION,CONF_EMAIL,"); // 5
            updateBuffer.append("COMPANY_EMAIL,COMPANY,ADDRESS,ADDRESS2,CITY,STATE,COUNTRY,"); // 5
            updateBuffer.append("ZIP,PHONE_NUMBER,FAX_NUMBER,ACCOUNT_NUMBER, BILLING_NAME,"); // 5
            updateBuffer.append("BILLING_STREET,BILLING_STREET2,BILLING_CITY,BILLING_STATE,"); // 4
            updateBuffer.append("BILLING_COUNTRY,BILLING_ZIP,BILLING_PHONE,BILLING_FAX,STATUS,"); // 5
            updateBuffer.append("AE_ACCOUNT_NUMBER,AE_ACCOUNT_NAME,AE_DEPT_NAME,CREATED_DATE)"); // 4
            updateBuffer.append("  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE)");
            pstmt = conn.prepareStatement(updateBuffer.toString());

            int intIndex = 1;
            pstmt.setLong(intIndex++, lhlUserInfo.getCustomerID());
            pstmt.setLong(intIndex++, lhlUserInfo.getContractID());
            pstmt.setString(intIndex++, orderNumber);
            pstmt.setString(intIndex++, StringUtil.EMPTY_STRING);
            pstmt.setString(intIndex++, docInfo);
            pstmt.setString(intIndex++, firstName);
            pstmt.setString(intIndex++, lastName);
            pstmt.setString(intIndex++, shipInfo);
            pstmt.setString(intIndex++, deliveryValue);
            pstmt.setString(intIndex++, accountNumber);
            pstmt.setString(intIndex++, serviceLevel);
            pstmt.setString(intIndex++, attention);
            pstmt.setString(intIndex++, confirmedEmail);
            pstmt.setString(intIndex++, userEmail);
            pstmt.setString(intIndex++, company);
            pstmt.setString(intIndex++, address1);
            pstmt.setString(intIndex++, address2);
            pstmt.setString(intIndex++, city);
            pstmt.setString(intIndex++, state);
            pstmt.setString(intIndex++, country);
            pstmt.setString(intIndex++, zip);
            pstmt.setString(intIndex++, phoneNumber);
            pstmt.setString(intIndex++, faxNumber);
            pstmt.setString(intIndex++, StringUtil.EMPTY_STRING);
            pstmt.setString(intIndex++, billingName);
            pstmt.setString(intIndex++, billingStreet);
            pstmt.setString(intIndex++, billingStreet2);
            pstmt.setString(intIndex++, billingCity);
            pstmt.setString(intIndex++, billingState);
            pstmt.setString(intIndex++, billingCountry);
            pstmt.setString(intIndex++, billingZip);
            pstmt.setString(intIndex++, billingPhone);
            pstmt.setString(intIndex++, billingFax);
            pstmt.setString(intIndex++, status);
            pstmt.setString(intIndex++, lhlUserInfo.getAccountNumber());
            pstmt.setString(intIndex++, lhlUserInfo.getAccountName());
            pstmt.setString(intIndex++, lhlUserInfo.getDeptName());

            // executeUpdate returns rowcount for INSERT statements
            // if the result is intIndex++, flag = true
            flag = (pstmt.executeUpdate() == 1);
        } catch (Exception e) {
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }// end of finally

        return flag;
    }

    /**
     * a method which gets the product name for a given contractid, custid, prod_id
     */
    @Deprecated
    private String getProductName(String strCustomerID, String strContractID) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String prodName = null;
        try {

            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer sqlQuery = new StringBuffer("SELECT PROD_NAME FROM USER_PASS_DATA WHERE ");
            sqlQuery.append(" CONTRACT_ID='").append(strContractID).append("'");
            sqlQuery.append(" AND CUST_ID='").append(strCustomerID).append("'");
            sqlQuery.append(" AND PROD_ID='").append(m_strProductId).append("'");

            rs = stmt.executeQuery(sqlQuery.toString());
            if (rs.next() == true) {
                prodName = rs.getString("PROD_NAME");

            }
        } catch (Exception e) {
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }// end of finally
        return prodName;
    }

    /**
     * This method is to get the access method whether the user is to be authenticated by Login thru password or Ip
     */
    @Deprecated
    private String getAccessType(String strCustomerID, String strContractID) throws LhlException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String accessMethod = StringUtil.EMPTY_STRING;
        try {

            conn = ConnectionBroker.getInstance().getConnection(SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer sqlQuery = new StringBuffer("SELECT * FROM IP_DATA WHERE ");
            sqlQuery.append(" CONTRACT_ID='").append(strContractID).append("'");
            sqlQuery.append(" AND CUST_ID='").append(strCustomerID).append("'");

            rs = stmt.executeQuery(sqlQuery.toString());
            if (rs.next() == true) {
                accessMethod = "IPA";
            } else {
                accessMethod = "PWD";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LhlException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        } // end of finally
        return accessMethod;
    }

}
