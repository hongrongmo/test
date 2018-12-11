
package org.ei.dataloading.upt.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UPTRefCntUpdateExtract
{
    private  String setURL = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI";
    private  String setUserName = "ap_pro1";
    private  String setPassword = "ei3it";


    public UPTRefCntUpdateExtract(String URL, String userName, String passwd)
                                                                throws Exception
    {
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        this.setURL = URL;
        this.setUserName = userName;
        this.setPassword = passwd;
    }

    public void writeExtract(String dir,String sql,boolean commit)

    {

        ResultSet rs = null;
        Statement stmt = null;
        Connection con = null;
        PrintWriter countsOut = null;
        int count = 0;
        PrintWriter out =  null;

        try {

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = con.createStatement();
            out =  new PrintWriter(new FileWriter(dir), true);
            stmt.execute(sql);
            rs = stmt.getResultSet();

            while (rs.next())
            {
                count++;
                String result = rs.getString(1);
                out.println(result);
                if (commit && count % 1000 == 0)
                {
                    out.println("commit;");
                }
            }
            if(commit)
                out.println("commit;");

        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
            close(out);
            File f=new File(dir);
            f.renameTo(new File(dir.replaceAll("count",String.valueOf(count))));
        }
    }
    /*
     * Writes the updates for all patent ref_cnt
     */
    public void writeRefExtract(String dir)
    {
        String sql = "SELECT 'update upt_master set ref_cnt = '||COUNT(*)||' where m_id = '''||prt_mid||''';'  FROM patent_refs GROUP BY prt_mid";
        writeExtract(dir,sql,true);
    }


     /*
      * Writes the updates for all patent cit_cnt
      */
    public void writeCitedByExtract(String dir)
    {
        String sql = "SELECT 'update upt_master set cit_cnt = '||COUNT(DISTINCT prt_mid)||',  where m_id = '''||cit_mid||''';'  FROM patent_refs GROUP BY cit_mid";
        writeExtract(dir,sql,true);
    }

    public void writeNonPatRefExtract(String dir)
    {
        String sql = "SELECT 'update upt_master set np_cnt = '||COUNT(*)||' where m_id = '''||prt_mid||''';'  FROM patent_refs GROUP BY prt_mid";
        writeExtract(dir,sql,true);
    }

    /*
     * Writes the updates for weekly patent files ref_cnt
     */
    public void writeRefExtract(String dir,String loadNumber)
    {
        String sql = "SELECT 'update upt_master set ref_cnt = '||COUNT(*)||' ,md = sysdate where m_id = '''||prt_mid||''';'  FROM  patent_refs WHERE PRT_MID IS NOT NULL and load_number ="+loadNumber+" GROUP BY prt_mid";
        writeExtract(dir,sql,true);
    }


     /*
      * Writes the updates for weekly patent files cit_cnt
      */
    public void writeCitedByExtract(String dir,String loadNumber)

    {
        //String sql = "SELECT 'update upt_master set cit_cnt = nvl(cit_cnt,0) +'||COUNT(DISTINCT prt_mid)||', md = sysdate where m_id = '''||cit_mid||''';'  FROM patent_refs WHERE cit_mid IS NOT NULL AND load_number = "+loadNumber+" GROUP BY cit_mid";
        //String sql = "SELECT 'update upt_master set cit_cnt ='||COUNT(DISTINCT prt_mid)||', md = sysdate, update_number ="+loadNumber+" where m_id = '''||cit_mid||''';'  FROM patent_refs WHERE cit_mid IS NOT NULL AND   cit_mid in (select distinct cit_mid from patent_refs where load_number = "+loadNumber+" and cit_mid is not null) and (load_number is null or load_number <="+loadNumber+") GROUP BY cit_mid";
    	//String sql = "SELECT 'update upt_master set cit_cnt ='||COUNT(prt_mid)||', md = sysdate, update_number ="+loadNumber+" where m_id = '''||cit_mid||''';'  FROM patent_refs WHERE cit_mid IS NOT NULL AND   cit_mid in (select distinct cit_mid from patent_refs where load_number = "+loadNumber+" and cit_mid is not null) and (load_number is null or load_number <="+loadNumber+") GROUP BY cit_mid";
    	String sql = "SELECT 'update upt_master set cit_cnt ='||COUNT(DISTINCT prt_mid)||', md = sysdate, update_number ="+loadNumber+" where m_id = '''||cit_mid||''';'  FROM patent_refs WHERE cit_mid IS NOT NULL AND   cit_mid in (select distinct cit_mid from patent_refs where load_number = "+loadNumber+" and cit_mid is not null) GROUP BY cit_mid";
    	System.out.println("Citation count Query="+sql);
    	writeExtract(dir,sql,true);
    }

    public void writeNonPatRefExtract(String dir,String loadNumber)
    {
        String sql = "SELECT 'update upt_master set np_cnt = '||COUNT(*)||' ,md = sysdate where m_id = '''||prt_mid||''';'  FROM  non_pat_refs WHERE PRT_MID IS NOT NULL and load_number ="+loadNumber+" GROUP BY prt_mid";
        writeExtract(dir,sql,true);
    }
    public void writeFastCitExtract(String dir,String loadNumber)
    {
        String sql = "SELECT 'PCITED~'||m_id||'~'||cit_cnt from upt_master WHERE MD>to_date('"+loadNumber+"','yyyymmdd') and cit_cnt > 0";
        writeExtract(dir,sql,false);
    }

    public void close(ResultSet rs)
    {

        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void close(PrintWriter countsOut)
    {

        if (countsOut != null)
        {
            try
            {
                countsOut.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public void close(Statement stmt)
    {

        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void close(Connection conn)
    {

        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(" starting extract ");
        String url = args[0];
        String user = args[1];
        String passwd = args[2];
        String type = args[3];
        String loadNum = args[4];
        UPTRefCntUpdateExtract extract = new UPTRefCntUpdateExtract(url,user,passwd);
        if (type != null
                && type.toUpperCase().indexOf("CT")>-1)
        {
            if (loadNum != null){
                if (loadNum.equalsIgnoreCase("ALL")){
                    extract.writeCitedByExtract("pat_cit_all.sql");
                }
                else {
                    extract.writeCitedByExtract("pat_cit_"+loadNum+".sql",loadNum);
                }
            }
        }
        if (type != null
                    && type.toUpperCase().indexOf("RF")>-1)
        {
            if (loadNum != null){
                if (loadNum.equalsIgnoreCase("ALL")){
                    extract.writeRefExtract("pat_ref_all.sql");
                }
                else {
                    extract.writeRefExtract("pat_ref_"+loadNum+".sql",loadNum);
                }
            }
        }
        if (type != null
                    && type.toUpperCase().indexOf("NP")>-1)
        {
            if (loadNum != null){
                if (loadNum.equalsIgnoreCase("ALL")){
                    extract.writeNonPatRefExtract("non_pat_ref_all.sql");
                }
                else {
                    extract.writeNonPatRefExtract("non_pat_ref_"+loadNum+".sql",loadNum);
                }
            }
        }
        if (type != null
                    && type.toUpperCase().indexOf("FAST")>-1)
        {
            if (loadNum != null){
                extract.writeFastCitExtract(System.currentTimeMillis()+"_partial_updates_count_upt_pcited-"+loadNum+".txt",loadNum);
                //<timestamp>_partial_updates_<number-of-updates>_<db-name>_<field-name>-<loaddate>.txt.gz


            }
        }
        System.out.println(" end ");
    }

}
