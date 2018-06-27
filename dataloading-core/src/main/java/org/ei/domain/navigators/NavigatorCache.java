/*
 * Created on Aug 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.PageCacheException;
import org.ei.util.StringUtil;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigatorCache {
    // protected static Log log = LogFactory.getLog(NavigatorCache.class);

    private String sessionid = null;

    public NavigatorCache(String sessionid) {
        this.sessionid = sessionid;
    }

    public void addToCache(String searchid, ResultNavigator nav) throws PageCacheException {
        if (nav == null) {
            return;
        }

        String navstring = StringUtil.EMPTY_STRING;
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);

            // TMH - attempt to delete from cache first. Avoids INSERT exception when
            // first search results page is refreshed.
            pstmt = con.prepareStatement("DELETE FROM NAVIGATOR_CACHE WHERE SEARCH_ID=? AND SESSION_ID=?");
            pstmt.setString(1, searchid);
            pstmt.setString(2, this.sessionid);
            pstmt.executeUpdate();

            // pstmt = con.prepareCall("{ call NavigatorCache_addToCache(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            pstmt = con
                .prepareStatement("INSERT INTO NAVIGATOR_CACHE(SEARCH_ID, SESSION_ID, DB, YR, CV, CL, FL, ST, PN, AU, AF, DT, LA, CO, PK, PEC, PAC, PUC, PCI, PID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            // jam - 11/9/2004 added DB Column
            int intStmtIndex = 1;

            /*
             * Zipped fields --------------------------------- EiNavigator.FL/EiNavigator.KY EiNavigator.ST/EiNavigator.BKT EiNavigator.PN EiNavigator.AU
             * EiNavigator.AF EiNavigator.CO/EiNavigator.BKS EiNavigator.PK/EiNavigator.IC
             */

            // log.info("UNCOMP EiNavigator.YR " + nav.getNavigatorByName(EiNavigator.YR).toString().length());
            // log.info("UNCOMP EiNavigator.CV " + nav.getNavigatorByName(EiNavigator.CV).toString().length());
            // log.info("UNCOMP EiNavigator.CL " + nav.getNavigatorByName(EiNavigator.CL).toString().length());
            // log.info("UNCOMP EiNavigator.FL " + nav.getNavigatorByName(EiNavigator.FL).toString().length());
            // log.info("UNCOMP EiNavigator.ST " + nav.getNavigatorByName(EiNavigator.ST).toString().length());
            // log.info("UNCOMP EiNavigator.PN " + nav.getNavigatorByName(EiNavigator.PN).toString().length());
            // log.info("UNCOMP EiNavigator.AU " + nav.getNavigatorByName(EiNavigator.AU).toString().length());
            // log.info("UNCOMP EiNavigator.AF " + nav.getNavigatorByName(EiNavigator.AF).toString().length());
            // log.info("UNCOMP EiNavigator.DT " + nav.getNavigatorByName(EiNavigator.DT).toString().length());
            // log.info("UNCOMP EiNavigator.LA " + nav.getNavigatorByName(EiNavigator.LA).toString().length());

            pstmt.setString(intStmtIndex++, searchid);
            pstmt.setString(intStmtIndex++, sessionid);
            // jam - 11/9/2004 added DB Navigator
            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.DB) != null) ? nav.getNavigatorByName(EiNavigator.DB).toString()
                : StringUtil.EMPTY_STRING);

            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.YR) != null) ? nav.getNavigatorByName(EiNavigator.YR).toString()
                : StringUtil.EMPTY_STRING);

            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.CV) != null) ? nav.getNavigatorByName(EiNavigator.CV).toString()
                : StringUtil.EMPTY_STRING);

            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.CL) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.CL).toString();
            }
            pstmt.setString(intStmtIndex++, navstring);

            String flnav = (nav.getNavigatorByName(EiNavigator.FL) != null) ? nav.getNavigatorByName(EiNavigator.FL).toString() : StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.KY) != null) {
                flnav = nav.getNavigatorByName(EiNavigator.KY).toString();
            }
            pstmt.setString(intStmtIndex++, StringUtil.zipText(flnav));

            String stnav = (nav.getNavigatorByName(EiNavigator.ST) != null) ? nav.getNavigatorByName(EiNavigator.ST).toString() : StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.BKT) != null) {
                stnav = nav.getNavigatorByName(EiNavigator.BKT).toString();
            }
            pstmt.setString(intStmtIndex++, StringUtil.zipText(stnav));

            pstmt.setString(intStmtIndex++, StringUtil.zipText((nav.getNavigatorByName(EiNavigator.PN) != null) ? nav.getNavigatorByName(EiNavigator.PN)
                .toString() : StringUtil.EMPTY_STRING));

            pstmt.setString(intStmtIndex++, StringUtil.zipText((nav.getNavigatorByName(EiNavigator.AU) != null) ? nav.getNavigatorByName(EiNavigator.AU)
                .toString() : StringUtil.EMPTY_STRING));
            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.AF) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.AF).toString();
            } else if (nav.getNavigatorByName(EiNavigator.GD) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.GD).toString();
            }
            pstmt.setString(intStmtIndex++, StringUtil.zipText(navstring));

            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.DT) != null) ? nav.getNavigatorByName(EiNavigator.DT).toString()
                : StringUtil.EMPTY_STRING);

            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.LA) != null) ? nav.getNavigatorByName(EiNavigator.LA).toString()
                : StringUtil.EMPTY_STRING);

            String conav = (nav.getNavigatorByName(EiNavigator.CO) != null) ? nav.getNavigatorByName(EiNavigator.CO).toString() : StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.BKS) != null) {
                conav = nav.getNavigatorByName(EiNavigator.BKS).toString();
            }
            pstmt.setString(intStmtIndex++, StringUtil.zipText(conav));

            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.PK) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.PK).toString();
            } else if (nav.getNavigatorByName(EiNavigator.IC) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.IC).toString();
            }
            pstmt.setString(intStmtIndex++, StringUtil.zipText(navstring));

            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.PEC) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.PEC).toString();
            } else if (nav.getNavigatorByName(EiNavigator.CM) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.CM).toString();
            } else if (nav.getNavigatorByName(EiNavigator.CVM) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.CVM).toString();
            }
            pstmt.setString(intStmtIndex++, navstring);

            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.PAC) != null) ? nav.getNavigatorByName(EiNavigator.PAC).toString()
                : StringUtil.EMPTY_STRING);

            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.PUC) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.PUC).toString();
            } else if (nav.getNavigatorByName(EiNavigator.RO) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.RO).toString();
            }
            pstmt.setString(intStmtIndex++, navstring);
            pstmt.setString(intStmtIndex++, (nav.getNavigatorByName(EiNavigator.PCI) != null) ? nav.getNavigatorByName(EiNavigator.PCI).toString()
                : StringUtil.EMPTY_STRING);
            navstring = StringUtil.EMPTY_STRING;
            if (nav.getNavigatorByName(EiNavigator.PID) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.PID).toString();
            } else if (nav.getNavigatorByName(EiNavigator.CP) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.CP).toString();
            } else if (nav.getNavigatorByName(EiNavigator.GEO) != null) {
                navstring = nav.getNavigatorByName(EiNavigator.GEO).toString();
            }
            pstmt.setString(intStmtIndex++, navstring);

            pstmt.executeUpdate();

        } catch (Exception e) {
            // log.error("Exception", e);
            throw new PageCacheException(e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
                }
            }
            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception cpe) {
                }
            }
        }

    }

    public ResultNavigator getFromCache(String searchid) throws PageCacheException {
        ResultSet rset = null;
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        StringBuffer sb = new StringBuffer();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);

            // jam - 11/9/2004 added DB Column
            pstmt = con
                .prepareStatement("SELECT DB, YR, CV, CL, FL, ST, PN, AU, AF, DT, LA, CO, PK, PEC, PAC, PUC, PCI, PID FROM NAVIGATOR_CACHE WHERE SEARCH_ID=? AND SESSION_ID=?");
            int intStmtIndex = 1;
            pstmt.setString(intStmtIndex++, searchid);
            pstmt.setString(intStmtIndex++, sessionid);

            rset = pstmt.executeQuery();
            if (rset.next()) {
                // jam - 11/9/2004 added DB Navigator
                sb.append(rset.getString("DB"));
                sb.append(EiNavigator.NAVS_DELIM);

                sb.append(rset.getString("YR"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("CV"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("CL"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("FL")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("ST")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("PN")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("AU")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("AF")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("DT"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("LA"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("CO")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(StringUtil.unZipText(rset.getString("PK")));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("PEC"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("PAC"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("PUC"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("PCI"));
                sb.append(EiNavigator.NAVS_DELIM);
                sb.append(rset.getString("PID"));
                sb.append(EiNavigator.NAVS_DELIM);
            }

        } catch (Exception e) {
            // log.error("Exception", e);
            throw new PageCacheException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception sqle) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
                }
            }
            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception cpe) {
                }
            }
        }

        return new ResultNavigator(sb.toString());
    }
    /*
     * public String zipText(String s) { byte[] bytes = s.getBytes(); ByteArrayOutputStream bout = new ByteArrayOutputStream(); String zippedString = null;
     * 
     * try { GZIPOutputStream zip = new GZIPOutputStream(bout); zip.write(bytes, 0, bytes.length); zip.close(); char chars[] =
     * Base64Coder.encode(bout.toByteArray()); zippedString = new String(chars); } catch (Exception ex) { ex.printStackTrace(); }
     * 
     * return zippedString;
     * 
     * }
     */
    /*
     * Method returns Decoded - Uncompressed GZIP string If decode or unzip fails, input string is returned unmodified!
     * 
     * If used 'by mistake' on uncompressed text, method will return input and will not fail.
     */
    /*
     * private String unZipText(String text) { StringBuffer buf = new StringBuffer();
     * 
     * try { byte[] decodedStr = Base64Coder.decode(text.toCharArray()); ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedStr); GZIPInputStream in
     * = null;
     * 
     * in = new GZIPInputStream(bytesIn); byte[] bytes = new byte[1024]; int i = -1; buf = new StringBuffer(); while ((i = in.read(bytes, 0, 1024)) != -1) {
     * String a = new String(bytes, 0, i); buf.append(a); }
     * 
     * } catch (Exception ex) { // Upon failure - return input string buf = new StringBuffer(new String(text)); ex.printStackTrace(); }
     * 
     * return buf.toString(); }
     */
}
