package org.ei.dataloading.inspec.loadtime;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import org.ei.common.*;
//import oracle.sql.CLOB;

public class InspecBaseTableUpdater
{

	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	public final int AUS_MAXSIZE = 4000;
    private Connection conn;
	private String[] baseTableFields;
    static PreparedStatement nodoipstmt = null;
    static PreparedStatement doipstmt = null;

    public void end()
    {
        try{
            conn.commit();
            close(conn);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
		if(nodoipstmt != null)
		{
			try
			{
				nodoipstmt.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
		if(doipstmt != null)
		{
			try
			{
				doipstmt.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}

    }
    private void close(Connection conn) {

        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void begin(String connectionURL,
					 String driver,
					 String username,
					 String password) throws Exception {

        try {
       		 conn=getConnection(connectionURL,
	                              driver,
	                            username,
                                password);
		}
		catch (Exception e) {
		     e.printStackTrace();
        }
    }

    private static Connection getConnection(String connectionURL,
                                             String driver,
                                             String username,
                                             String password)
                throws Exception
    {
            Class.forName(driver);
           Connection con = DriverManager.getConnection(connectionURL,
														 username,
														 password);
		   con.setAutoCommit(false);
            return con;
    }

	private  PreparedStatement setPreparedStatement(boolean doi) throws Exception
	{

       StringBuffer updateStringbuf = new StringBuffer();
		    updateStringbuf.append("update test_new_ins_master set ");
		    updateStringbuf.append("ASDATE = ?,");
		    updateStringbuf.append("ADATE = ?,");
		    updateStringbuf.append("RTYPE = ?,");
		    updateStringbuf.append("NRTYPE = ?,");
		    updateStringbuf.append("CPR = ?,");
		    updateStringbuf.append("SU = ?,");
		    updateStringbuf.append("TI = ?,");
		    updateStringbuf.append("CLS = ?,");
		    updateStringbuf.append("CVS = ?,");
		    updateStringbuf.append("FLS = ?,");
		    updateStringbuf.append("TRMC = ?,");
		    updateStringbuf.append("NDI = ?,");
		    updateStringbuf.append("CHI = ?,");
		    updateStringbuf.append("AOI = ?,");
		    updateStringbuf.append("PFLAG = ?,");
		    updateStringbuf.append("PJID = ?,");
		    updateStringbuf.append("PFJT = ?,");
		    updateStringbuf.append("PAJT = ?,");
		    updateStringbuf.append("PVOL = ?,");
		    updateStringbuf.append("PISS = ?,");
		    updateStringbuf.append("PVOLISS = ?,");
		    updateStringbuf.append("PIPN = ?,");
		    updateStringbuf.append("PSPDATE = ?,");
		    updateStringbuf.append("PEPDATE = ?,");
		    updateStringbuf.append("POPDATE = ?,");
		    updateStringbuf.append("PCPUB = ?,");
		    updateStringbuf.append("PCDN = ?,");
		    updateStringbuf.append("PSN = ?,");
		    updateStringbuf.append("PSICI = ?,");
		    updateStringbuf.append("PPUB = ?,");
		    updateStringbuf.append("PCCCC = ?,");
		    updateStringbuf.append("PUM = ?,");
		    updateStringbuf.append("PDNUM = ?,");
		    updateStringbuf.append("PURL = ?,");
		    updateStringbuf.append("PDCURL = ?,");
		    updateStringbuf.append("SJID = ?,");
		    updateStringbuf.append("SFJT = ?,");
		    updateStringbuf.append("SAJT = ?,");
		    updateStringbuf.append("SVOL = ?,");
		    updateStringbuf.append("SISS = ?,");
		    updateStringbuf.append("SVOLISS = ?,");
		    updateStringbuf.append("SIPN = ?,");
		    updateStringbuf.append("SSPDATE = ?,");
		    updateStringbuf.append("SEPDATE = ?,");
		    updateStringbuf.append("SOPDATE = ?,");
		    updateStringbuf.append("SCPUB = ?,");
		    updateStringbuf.append("SCDN = ?,");
		    updateStringbuf.append("SSN = ?,");
		    updateStringbuf.append("SSICI = ?,");
		    updateStringbuf.append("LA = ?,");
		    updateStringbuf.append("TC = ?,");
		    updateStringbuf.append("AC = ?,");
		    updateStringbuf.append("AUS = ?,");
		    updateStringbuf.append("AUS2 = ?,");
		    updateStringbuf.append("EDS = ?,");
		    updateStringbuf.append("TRS = ?,");
		    updateStringbuf.append("ABNUM = ?,");
		    updateStringbuf.append("MATID = ?,");
		    updateStringbuf.append("SBN = ?,");
		    updateStringbuf.append("RNUM = ?,");
		    updateStringbuf.append("UGCHN = ?,");
		    updateStringbuf.append("CNUM = ?,");
		    updateStringbuf.append("PNUM = ?,");
		    updateStringbuf.append("OPAN = ?,");
		    updateStringbuf.append("PARTNO = ?,");
		    updateStringbuf.append("AMDREF = ?,");
		    updateStringbuf.append("CLOC = ?,");
		    updateStringbuf.append("BPPUB = ?,");
		    updateStringbuf.append("CIORG = ?,");
		    updateStringbuf.append("CCNF = ?,");
		    updateStringbuf.append("CPAT = ?,");
		    updateStringbuf.append("COPA = ?,");
		    updateStringbuf.append("PUBTI = ?,");
		    updateStringbuf.append("NPL1 = ?,");
		    updateStringbuf.append("NPL2 = ?,");
		    updateStringbuf.append("XREFNO = ?,");
		    updateStringbuf.append("AAFF = ?,");
		    updateStringbuf.append("AFC = ?,");
		    updateStringbuf.append("EAFF = ?,");
		    updateStringbuf.append("EFC = ?,");
		    updateStringbuf.append("PAS = ?,");
		    updateStringbuf.append("IORG = ?,");
		    updateStringbuf.append("SORG = ?,");
		    updateStringbuf.append("AVAIL = ?,");
		    updateStringbuf.append("PRICE = ?,");
		    updateStringbuf.append("CDATE = ?,");
		    updateStringbuf.append("CEDATE = ?,");
		    updateStringbuf.append("CODATE = ?,");
		    updateStringbuf.append("PYR = ?,");
		    updateStringbuf.append("FDATE = ?,");
		    updateStringbuf.append("OFDATE = ?,");
		    updateStringbuf.append("PPDATE = ?,");
		    updateStringbuf.append("OPPDATE = ?,");
		    updateStringbuf.append("UPDATE_NUMBER = ?");
		    if(doi)
		    updateStringbuf.append(",PDOI = ?");
		    updateStringbuf.append("  where ANUM = ?");

			return conn.prepareStatement(updateStringbuf.toString());
	}

	public void writeRec(Hashtable record, String updateN)
		throws Exception
	{

        if(record == null)
        {
        	System.out.println("Record was null");
        	return;
        }

        boolean pdoi = record.containsKey("PDOI");
        PreparedStatement pstmt1 = null;

        if(pdoi)
        {
			 if(doipstmt == null) {
			    doipstmt = setPreparedStatement(pdoi);
			 }
			pstmt1 = doipstmt;
		}
		else
		{
			if(nodoipstmt == null) {
			   nodoipstmt = setPreparedStatement(false);
			}
			pstmt1 = nodoipstmt;
		}

        try
        {

			StringBuffer valueS = (StringBuffer) record.get("AUS");
			String value = null;
			if(valueS != null)
			{
				value = valueS.toString();
				if(record.get("AUS") != null)
				{
					if(value.length() > AUS_MAXSIZE)
					{
						// store the AUS_MAXSIZE+ substring in Hashtable for later processing
						record.put("AUS2", new StringBuffer(value.substring(AUS_MAXSIZE)));
						// truncate first author string to AUS_MAXSIZE - we don't care if we truncate
						// an author name since AUS + AUS2 fields will be rejoined by concatenation
						value = value.substring(0, AUS_MAXSIZE);
					}
				}
			}
			if(record.get("AUS2") != null)
			{
				// fix last author in string to make sure it isn't a partial author name
				value = fixAuthors(value);
				record.put("AUS2", new StringBuffer(value));
			}

			pstmt1.setString(1, getValue(record,"ASPDATE"));
		    pstmt1.setString(2, getValue(record,"AOPDATE"));
			pstmt1.setString(3, getValue(record,"RTYPE"));
			pstmt1.setString(4, getValue(record,"NRTYPE"));
			pstmt1.setString(5, getValue(record,"CPR"));
			pstmt1.setString(6, getValue(record,"SU"));
			pstmt1.setString(7, getValue(record,"TI"));
			pstmt1.setString(8, getValue(record,"CLS"));
			pstmt1.setString(9, getValue(record,"CVS"));
			pstmt1.setString(10, getValue(record,"FLS"));
			pstmt1.setString(11, getValue(record,"TRMC"));
			pstmt1.setString(12, getValue(record,"NDI"));;
			pstmt1.setString(13, getValue(record,"CHI"));
			pstmt1.setString(14, getValue(record,"AOI"));
			pstmt1.setString(15, getValue(record,"PFLAG"));
			pstmt1.setString(16, getValue(record,"PJID"));
			pstmt1.setString(17, getValue(record,"PFJT"));
			pstmt1.setString(18, getValue(record,"PAJT"));
			pstmt1.setString(19, getValue(record,"PVOL"));
			pstmt1.setString(20, getValue(record,"PISS"));
			pstmt1.setString(21, getValue(record,"PVOLISS"));
			pstmt1.setString(22, getValue(record,"PIPN"));
			pstmt1.setString(23, getValue(record,"PSPDATE"));
			pstmt1.setString(24, getValue(record,"PEPDATE"));
			pstmt1.setString(25, getValue(record,"POPDATE"));
			pstmt1.setString(26, getValue(record,"PCPUB"));
			pstmt1.setString(27, getValue(record,"PCDN"));
			pstmt1.setString(28, getValue(record,"PSN"));
			pstmt1.setString(29, getValue(record,"PSICI"));
			pstmt1.setString(30, getValue(record,"PPUB"));
			pstmt1.setString(31, getValue(record,"PCCCC"));
			pstmt1.setString(32, getValue(record,"PUM"));
			pstmt1.setString(33, getValue(record,"PDNUM"));
			pstmt1.setString(34, getValue(record,"PURL"));
			pstmt1.setString(35, getValue(record,"PDCURL"));
			pstmt1.setString(36, getValue(record,"SJID"));
			pstmt1.setString(37, getValue(record,"SFJT"));
			pstmt1.setString(38, getValue(record,"SAJT"));
			pstmt1.setString(39, getValue(record,"SVOL"));
			pstmt1.setString(40, getValue(record,"SISS"));
			pstmt1.setString(41, getValue(record,"SVOLISS"));
			pstmt1.setString(42, getValue(record,"SIPN"));
			pstmt1.setString(43, getValue(record,"SSPDATE"));
			pstmt1.setString(44, getValue(record,"SEPDATE"));
			pstmt1.setString(45, getValue(record,"SOPDATE"));
			pstmt1.setString(46, getValue(record,"SCPUB"));
			pstmt1.setString(47, getValue(record,"SCDN"));
			pstmt1.setString(48, getValue(record,"SSN"));
			pstmt1.setString(49, getValue(record,"SSICI"));
			pstmt1.setString(50, getValue(record,"LA"));
			pstmt1.setString(51, getValue(record,"TC"));
			pstmt1.setString(52, getValue(record,"AC"));
			pstmt1.setString(53, getValue(record,"AUS"));
			pstmt1.setString(54, getValue(record,"AUS2"));
			pstmt1.setString(55, getValue(record,"EDS"));
			pstmt1.setString(56, getValue(record,"TRS"));
			pstmt1.setString(57, getValue(record,"ABNUM"));
			pstmt1.setString(58, getValue(record,"MATID"));
			pstmt1.setString(59, getValue(record,"SBN"));
			pstmt1.setString(60, getValue(record,"RNUM"));
			pstmt1.setString(61, getValue(record,"UGCHN"));
			pstmt1.setString(62, getValue(record,"CNUM"));
			pstmt1.setString(63, getValue(record,"PNUM"));
			pstmt1.setString(64, getValue(record,"OPAN"));
			pstmt1.setString(65, getValue(record,"PARTNO"));
			pstmt1.setString(66, getValue(record,"AMDREF"));
			pstmt1.setString(67, getValue(record,"CLOC"));
			pstmt1.setString(68, getValue(record,"BPPUB"));
			pstmt1.setString(69, getValue(record,"CIORG"));
			pstmt1.setString(70, getValue(record,"CCNF"));
			pstmt1.setString(71, getValue(record,"CPAT"));
			pstmt1.setString(72, getValue(record,"COPA"));
			pstmt1.setString(73, getValue(record,"PUBTI"));
			pstmt1.setString(74, getValue(record,"NPL1"));
			pstmt1.setString(75, getValue(record,"NPL2"));
			pstmt1.setString(76, getValue(record,"XREFNO"));
			pstmt1.setString(77, getValue(record,"AAFF"));
			pstmt1.setString(78, getValue(record,"AFC"));
			pstmt1.setString(79, getValue(record,"EAFF"));
			pstmt1.setString(80, getValue(record,"EFC"));
			pstmt1.setString(81, getValue(record,"PAS"));
			pstmt1.setString(82, getValue(record,"IORG"));
			pstmt1.setString(83, getValue(record,"SORG"));
			pstmt1.setString(84, getValue(record,"AVAIL"));
			pstmt1.setString(85, getValue(record,"PRICE"));
			pstmt1.setString(86, getValue(record,"CSPDATE"));
			pstmt1.setString(87, getValue(record,"CEPDATE"));
			pstmt1.setString(88, getValue(record,"COPDATE"));
			pstmt1.setString(89, getValue(record,"PYR"));
			pstmt1.setString(90, getValue(record,"FDATE"));
			pstmt1.setString(91, getValue(record,"OFDATE"));
			pstmt1.setString(92, getValue(record,"PPDATE"));
			pstmt1.setString(93, getValue(record,"OPPDATE"));
			pstmt1.setString(94, updateN);
			int i = 0;
			if(pdoi) {
			  pstmt1.setString(95, getValue(record,"PDOI"));
			  i++;
		    }
			pstmt1.setString((95+i), getValue(record,"ANUM"));
			pstmt1.executeUpdate();

			int numUpd;
			numUpd = pstmt1.executeUpdate();
			if(numUpd == 0)
			   System.out.println("Unable to update this row ANUM:"+getValue(record,"ANUM"));
            conn.commit();

		}
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

	private String getValue(Hashtable record, String key)
	{
		String value = "";

		if(record.containsKey(key))
		{
		   value = record.get(key).toString();
		}

		return value;
	}
	private String fixAuthors(String authors)
	{
		if(authors != null && authors.length() > AUS_MAXSIZE)
		{
			authors = authors.substring(0,AUS_MAXSIZE-1);
			int i = authors.lastIndexOf(Constants.AUDELIMITER);
            if(i != -1)
            {
			    authors = authors.substring(0,i);
            }
		}
		return authors;
	}

}
