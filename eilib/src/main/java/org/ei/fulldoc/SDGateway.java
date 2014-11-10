package org.ei.fulldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.util.MD5Digester;
import org.ei.util.StringUtil;


public class SDGateway
{

    private Hashtable<String, ElsevierJournal> issnTable = new Hashtable<String, ElsevierJournal>();
    private static SDGateway instance;
    private static String scienceDirectUrl = "http://www.sciencedirect.com/science";
    private static String URLVersion = "1";
    private static String saltVersion = "1";
    private static String salt = "Kyyzg5iYkajHw9s0FdjNR9vGkeR3wJB:";
    private static String origin = "ei";
    private static String object = "GatewayURL";
    private static String method = "citationSearch";
    public static String elsevierID = "10.1016";

    private static StringUtil sUtil = new StringUtil();

    public static synchronized SDGateway getInstance()
    {
        if(instance == null)
        {
            instance = new SDGateway();
        }

        return instance;
    }


    public String getPII(String doi)
    	throws Exception
    {
		ConnectionBroker broker = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String pii = null;

		try
		{
			broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
			pstmt = con.prepareStatement("select * from DOI_PII where do = ?");
			pstmt.setString(1, doi);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				pii = rs.getString("PI");
			}
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(con != null)
			{
				try
				{
					broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return pii;
	}



    public boolean isUnfriendly(String pii)
    {
        if(pii.indexOf(".") > -1)
        {
            return false;
        }
        return true;
    }

    public static String getPIILink(String PII)
        throws Exception
    {

        String fPII = fixPII(PII);
        String httpVariables = getHttpVariablesPII(
                            origin,
                            URLVersion,
                            saltVersion,
                            method,
                            object,
                            fPII);

        MD5Digester digester = new MD5Digester();
        String theDigest = digester.asHex(digester.digest(httpVariables+salt));
        httpVariables = httpVariables+"&md5="+theDigest;
        return scienceDirectUrl+"?"+httpVariables;
    }

    public static String getScienceDirectLink(String PII)
                throws Exception
    {
        String fPII = fixPII(PII);
        String httpVariables = getHttpVariablesPII(
                            origin,
                            URLVersion,
                            saltVersion,
                            method,
                            object,
                            fPII);

        MD5Digester digester = new MD5Digester();
        String theDigest = digester.asHex(digester.digest(httpVariables+salt));
        httpVariables = httpVariables+"&md5="+theDigest;
        return scienceDirectUrl+"?"+httpVariables;
    }

    private static String getHttpVariablesPII(String origin,
                       String URLVersion,
                       String saltVersion,
                       String method,
                       String object,
                       String fPII)
    {

        StringBuffer buffer = new StringBuffer();
        buffer.append("_ob=");
        buffer.append(object);
        buffer.append("&_origin=");
        buffer.append(origin);
        buffer.append("&_urlversion=");
        buffer.append(URLVersion);
        buffer.append("&_method=");
        buffer.append(method);
        buffer.append("&_piikey=");
        buffer.append(fPII);
        buffer.append("&_version=");
        buffer.append(saltVersion);
        return buffer.toString();
    }


    private static String fixPII(String PII)
    {
        PII = sUtil.replace(PII,
                    ")",
                    "",
                    StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

        PII = sUtil.replace(PII,
                    "(",
                    "",
                    StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

        PII = sUtil.replace(PII,
                    "-",
                    "",
                    StringUtil.REPLACE_GLOBAL,
                        StringUtil.MATCH_CASE_INSENSITIVE);

        return PII.toLowerCase();
    }



    public SDGateway()
    {


		//issnTable.put("00011541", new ElsevierJournal(44,49));
		issnTable.put("00014575", new ElsevierJournal(1));
		issnTable.put("00016160", new ElsevierJournal(1));
		issnTable.put("0001706X", new ElsevierJournal(46));
		issnTable.put("00018686", new ElsevierJournal(1));
		issnTable.put("00032670", new ElsevierJournal(1));
		issnTable.put("00034843", new ElsevierJournal(1));
		issnTable.put("00034878", new ElsevierJournal(39,45));
		issnTable.put("00034916", new ElsevierJournal(1));
		issnTable.put("0003682X", new ElsevierJournal(1));
		issnTable.put("00036870", new ElsevierJournal(1));
		issnTable.put("00039861", new ElsevierJournal(300));
		issnTable.put("00043702", new ElsevierJournal(1));
		issnTable.put("00046981", new ElsevierJournal(1));
		issnTable.put("00051098", new ElsevierJournal(1));
		issnTable.put("00052736", new ElsevierJournal(135));
		issnTable.put("0006355X", new ElsevierJournal(32,35));
		issnTable.put("00076813", new ElsevierJournal(27));
		issnTable.put("00084433", new ElsevierJournal(34,38));
		issnTable.put("00086215", new ElsevierJournal(1));
		issnTable.put("00086223", new ElsevierJournal(1));
		issnTable.put("00088846", new ElsevierJournal(1));
		issnTable.put("00092509", new ElsevierJournal(1));
		issnTable.put("00092541", new ElsevierJournal(1));
		issnTable.put("00092614", new ElsevierJournal(1));
		issnTable.put("00093084", new ElsevierJournal(1));
		issnTable.put("00099260", new ElsevierJournal(54));
		issnTable.put("00100277", new ElsevierJournal(1));
		issnTable.put("00100285", new ElsevierJournal(1));
		issnTable.put("00102180", new ElsevierJournal(1));
		issnTable.put("00104361", new ElsevierJournal(1));
		issnTable.put("00104485", new ElsevierJournal(1));
		issnTable.put("00104655", new ElsevierJournal(1));
		issnTable.put("00104809", new ElsevierJournal(1));
		issnTable.put("00104825", new ElsevierJournal(1));
		issnTable.put("0010938X", new ElsevierJournal(1));
		issnTable.put("00112240", new ElsevierJournal(30));
		issnTable.put("00112275", new ElsevierJournal(1));
		issnTable.put("00119164", new ElsevierJournal(1));
		issnTable.put("0012365X", new ElsevierJournal(1));
		issnTable.put("0012821X", new ElsevierJournal(1));
		issnTable.put("00128252", new ElsevierJournal(1));
		issnTable.put("00134686", new ElsevierJournal(1));
		issnTable.put("00134694", new ElsevierJournal(1));
		issnTable.put("00137944", new ElsevierJournal(1));
		issnTable.put("00137952", new ElsevierJournal(1));
		issnTable.put("00143057", new ElsevierJournal(1));
		issnTable.put("00144835", new ElsevierJournal(12));
		issnTable.put("00150568", new ElsevierJournal(1));
		issnTable.put("00151882", new ElsevierJournal(27));
		issnTable.put("00160032", new ElsevierJournal(1));
		issnTable.put("00162361", new ElsevierJournal(49));
		issnTable.put("00163287", new ElsevierJournal(1));
		issnTable.put("00167037", new ElsevierJournal(1));
		issnTable.put("00167061", new ElsevierJournal(1));
		issnTable.put("00167142", new ElsevierJournal(1));
		issnTable.put("00179310", new ElsevierJournal(1));
		issnTable.put("00190578", new ElsevierJournal(28,40));
		issnTable.put("00191035", new ElsevierJournal(1));
		issnTable.put("00200190", new ElsevierJournal(1));
		issnTable.put("00200255", new ElsevierJournal(1));
		issnTable.put("00200271", new ElsevierJournal(1));
		issnTable.put("00200891", new ElsevierJournal(1));
		issnTable.put("00201650", new ElsevierJournal(2));
		issnTable.put("00201693", new ElsevierJournal(1));
		issnTable.put("0020708X", new ElsevierJournal(1));
		issnTable.put("0020711X", new ElsevierJournal(1));
		issnTable.put("00207225", new ElsevierJournal(1));
		issnTable.put("00207357", new ElsevierJournal(1));
		issnTable.put("00207373", new ElsevierJournal(31));
		issnTable.put("00207381", new ElsevierJournal(1));
		issnTable.put("00207403", new ElsevierJournal(1));
		issnTable.put("00207462", new ElsevierJournal(1));
		issnTable.put("00207683", new ElsevierJournal(1));
		issnTable.put("00218502", new ElsevierJournal(1));
		issnTable.put("00218634", new ElsevierJournal(10));
		issnTable.put("00218928", new ElsevierJournal(22));
		issnTable.put("00219045", new ElsevierJournal(1));
		issnTable.put("00219169", new ElsevierJournal(1));
		issnTable.put("00219290", new ElsevierJournal(1));
		issnTable.put("00219517", new ElsevierJournal(1));
		issnTable.put("00219614", new ElsevierJournal(1));
		issnTable.put("00219673", new ElsevierJournal(1));
		issnTable.put("00219797", new ElsevierJournal(11));
		issnTable.put("00219991", new ElsevierJournal(1));
		issnTable.put("00220000", new ElsevierJournal(16));
		issnTable.put("00220248", new ElsevierJournal(1));
		issnTable.put("00220396", new ElsevierJournal(2));
		issnTable.put("00220728", new ElsevierJournal(1));
		issnTable.put("00221139", new ElsevierJournal(1));
		issnTable.put("00221236", new ElsevierJournal(1));
		issnTable.put("00221694", new ElsevierJournal(1));
		issnTable.put("00221902", new ElsevierJournal(1));
		issnTable.put("00222313", new ElsevierJournal(1));
		issnTable.put("0022247X", new ElsevierJournal(1));
		issnTable.put("00222496", new ElsevierJournal(1));
		issnTable.put("00222569", new ElsevierJournal(1));
		issnTable.put("00222852", new ElsevierJournal(1));
		issnTable.put("00222860", new ElsevierJournal(1));
		issnTable.put("00223093", new ElsevierJournal(1));
		issnTable.put("00223115", new ElsevierJournal(1));
		issnTable.put("0022314X", new ElsevierJournal(1));
		issnTable.put("0022328X", new ElsevierJournal(1));
		issnTable.put("00223697", new ElsevierJournal(1));
		issnTable.put("00224073", new ElsevierJournal(1));
		issnTable.put("00224375", new ElsevierJournal(13));
		issnTable.put("00224596", new ElsevierJournal(1));
		issnTable.put("0022460X", new ElsevierJournal(1));
		issnTable.put("0022474X", new ElsevierJournal(1));
		issnTable.put("00224898", new ElsevierJournal(1));
		issnTable.put("00225088", new ElsevierJournal(1));
		issnTable.put("00225096", new ElsevierJournal(1));
		issnTable.put("00236438", new ElsevierJournal(26));
		issnTable.put("00243795", new ElsevierJournal(1));
		issnTable.put("00246301", new ElsevierJournal(1));
		issnTable.put("00253227", new ElsevierJournal(1));
		issnTable.put("0025326X", new ElsevierJournal(1));
		issnTable.put("00255408", new ElsevierJournal(1));
		issnTable.put("00255416", new ElsevierJournal(1));
		issnTable.put("00255564", new ElsevierJournal(1));
		issnTable.put("00260576", new ElsevierJournal(93));
		issnTable.put("00260657", new ElsevierJournal(45));
		issnTable.put("00260800", new ElsevierJournal(1));
		issnTable.put("0026265X", new ElsevierJournal(47));
		issnTable.put("00262692", new ElsevierJournal(20));
		issnTable.put("00262714", new ElsevierJournal(1));
		issnTable.put("00291021", new ElsevierJournal(1));
		issnTable.put("00295493", new ElsevierJournal(1));
		issnTable.put("0029554X", new ElsevierJournal(151));
		issnTable.put("00295582", new ElsevierJournal(1));
		issnTable.put("00298018", new ElsevierJournal(1));
		issnTable.put("00303992", new ElsevierJournal(3));
		issnTable.put("00304018", new ElsevierJournal(1));
		issnTable.put("00310182", new ElsevierJournal(1));
		issnTable.put("00313203", new ElsevierJournal(1));
		issnTable.put("00318663", new ElsevierJournal(20));
		issnTable.put("00318914", new ElsevierJournal(1));
		issnTable.put("00319201", new ElsevierJournal(1));
		issnTable.put("00320633", new ElsevierJournal(1));
		issnTable.put("00323861", new ElsevierJournal(1));
		issnTable.put("00323950", new ElsevierJournal(1));
		issnTable.put("00325910", new ElsevierJournal(1));
		issnTable.put("00329592", new ElsevierJournal(26));
		issnTable.put("00335894", new ElsevierJournal(39));
		issnTable.put("00343617", new ElsevierJournal(34));
		issnTable.put("00344257", new ElsevierJournal(1));
		issnTable.put("00344877", new ElsevierJournal(1));
		issnTable.put("00353159", new ElsevierJournal(35));
		issnTable.put("00353183", new ElsevierJournal(1998));
		issnTable.put("00369748", new ElsevierJournal(1));
		issnTable.put("00370738", new ElsevierJournal(1));
		issnTable.put("00380717", new ElsevierJournal(1));
		issnTable.put("0038092X", new ElsevierJournal(1));
		issnTable.put("00381098", new ElsevierJournal(1));
		issnTable.put("00381101", new ElsevierJournal(1));
		issnTable.put("00393681", new ElsevierJournal(1));
		issnTable.put("00396028", new ElsevierJournal(1));
		issnTable.put("00399140", new ElsevierJournal(1));
		issnTable.put("00401625", new ElsevierJournal(2));
		issnTable.put("00401951", new ElsevierJournal(1));
		issnTable.put("00404020", new ElsevierJournal(1));
		issnTable.put("00404039", new ElsevierJournal(1));
		issnTable.put("00406031", new ElsevierJournal(1));
		issnTable.put("00406090", new ElsevierJournal(1));
		issnTable.put("00411647", new ElsevierJournal(1));
		issnTable.put("0041624X", new ElsevierJournal(1));
		issnTable.put("0042207X", new ElsevierJournal(1));
		issnTable.put("00426989", new ElsevierJournal(1));
		issnTable.put("00431354", new ElsevierJournal(1));
		issnTable.put("00431648", new ElsevierJournal(1));
		issnTable.put("00456535", new ElsevierJournal(1));
		issnTable.put("00457825", new ElsevierJournal(1));
		issnTable.put("00457906", new ElsevierJournal(1));
		issnTable.put("00457930", new ElsevierJournal(1));
		issnTable.put("00457949", new ElsevierJournal(1));
		issnTable.put("0047259X", new ElsevierJournal(1));
		issnTable.put("00472670", new ElsevierJournal(1));
		issnTable.put("00477206", new ElsevierJournal(1));
		issnTable.put("00487333", new ElsevierJournal(1));
		issnTable.put("00489697", new ElsevierJournal(1));
		issnTable.put("0065227X", new ElsevierJournal(15));
		issnTable.put("00791946", new ElsevierJournal(1));
		issnTable.put("00796107", new ElsevierJournal(13));
		issnTable.put("00796425", new ElsevierJournal(1));
		issnTable.put("00796565", new ElsevierJournal(1));
		issnTable.put("00796611", new ElsevierJournal(1));
		issnTable.put("00796700", new ElsevierJournal(1));
		issnTable.put("00796727", new ElsevierJournal(1));
		issnTable.put("00796786", new ElsevierJournal(1));
		issnTable.put("00796816", new ElsevierJournal(1));
		issnTable.put("00836656", new ElsevierJournal(1));
		issnTable.put("00903752", new ElsevierJournal(68));
		issnTable.put("0092640X", new ElsevierJournal(12));
		issnTable.put("00928240", new ElsevierJournal(58));
		issnTable.put("00928674", new ElsevierJournal(1));
		issnTable.put("00936413", new ElsevierJournal(1));
		issnTable.put("0094114X", new ElsevierJournal(7));
		issnTable.put("00944548", new ElsevierJournal(1));
		issnTable.put("00945765", new ElsevierJournal(1));
		issnTable.put("00950696", new ElsevierJournal(1));
		issnTable.put("00958956", new ElsevierJournal(11));
		issnTable.put("00960551", new ElsevierJournal(1));
		issnTable.put("00963003", new ElsevierJournal(1));
		issnTable.put("00973165", new ElsevierJournal(10));
		issnTable.put("00978485", new ElsevierJournal(1));
		issnTable.put("00978493", new ElsevierJournal(1));
		issnTable.put("00981354", new ElsevierJournal(1));
		issnTable.put("00983004", new ElsevierJournal(1));
		issnTable.put("00988472", new ElsevierJournal(16));
		issnTable.put("00991333", new ElsevierJournal(18));
		issnTable.put("00993964", new ElsevierJournal(1));
		issnTable.put("01401963", new ElsevierJournal(24));
		issnTable.put("01403664", new ElsevierJournal(1));
		issnTable.put("01407007", new ElsevierJournal(1));
		issnTable.put("01409883", new ElsevierJournal(1));
		issnTable.put("01410229", new ElsevierJournal(1));
		issnTable.put("01410296", new ElsevierJournal(1));
		issnTable.put("01411136", new ElsevierJournal(1));
		issnTable.put("01411187", new ElsevierJournal(1));
		issnTable.put("01411195", new ElsevierJournal(1));
		issnTable.put("01413910", new ElsevierJournal(1));
		issnTable.put("01415530", new ElsevierJournal(1));
		issnTable.put("01416359", new ElsevierJournal(1));
		issnTable.put("01419331", new ElsevierJournal(3));
		issnTable.put("01419382", new ElsevierJournal(1));
		issnTable.put("01420496", new ElsevierJournal(1));
		issnTable.put("01420615", new ElsevierJournal(1));
		issnTable.put("01421123", new ElsevierJournal(1));
		issnTable.put("0142694X", new ElsevierJournal(1));
		issnTable.put("0142727X", new ElsevierJournal(1));
		issnTable.put("01429418", new ElsevierJournal(1));
		issnTable.put("01429612", new ElsevierJournal(1));
		issnTable.put("01436236", new ElsevierJournal(1));
		issnTable.put("01437208", new ElsevierJournal(1));
		issnTable.put("01437496", new ElsevierJournal(1));
		issnTable.put("01438166", new ElsevierJournal(1));
		issnTable.put("01438174", new ElsevierJournal(1));
		issnTable.put("0143974X", new ElsevierJournal(1));
		issnTable.put("01442449", new ElsevierJournal(3));
		issnTable.put("01442880", new ElsevierJournal(1));
		issnTable.put("01448609", new ElsevierJournal(1));
		issnTable.put("01448617", new ElsevierJournal(1));
		issnTable.put("0145224X", new ElsevierJournal(1));
		issnTable.put("01463535", new ElsevierJournal(1));
		issnTable.put("01465724", new ElsevierJournal(9));
		issnTable.put("01466313", new ElsevierJournal(1));
		issnTable.put("01466380", new ElsevierJournal(1));
		issnTable.put("01466410", new ElsevierJournal(1));
		issnTable.put("01466453", new ElsevierJournal(1));
		issnTable.put("01476513", new ElsevierJournal(25));
		issnTable.put("01489062", new ElsevierJournal(1));
		issnTable.put("01491970", new ElsevierJournal(1));
		issnTable.put("01519107", new ElsevierJournal(23,28));
		issnTable.put("01604120", new ElsevierJournal(1));
		issnTable.put("0160791X", new ElsevierJournal(1));
		issnTable.put("01609327", new ElsevierJournal(1));
		issnTable.put("01617346", new ElsevierJournal(1));
		issnTable.put("01641212", new ElsevierJournal(1));
		issnTable.put("01650114", new ElsevierJournal(1));
		issnTable.put("0165022X", new ElsevierJournal(1));
		issnTable.put("01650270", new ElsevierJournal(1));
		issnTable.put("01650572", new ElsevierJournal(1));
		issnTable.put("01651633", new ElsevierJournal(1));
		issnTable.put("01651684", new ElsevierJournal(1));
		issnTable.put("01651889", new ElsevierJournal(1));
		issnTable.put("01652125", new ElsevierJournal(1));
		issnTable.put("0165232X", new ElsevierJournal(1));
		issnTable.put("01652370", new ElsevierJournal(1));
		issnTable.put("01654896", new ElsevierJournal(1));
		issnTable.put("01655817", new ElsevierJournal(49));
		issnTable.put("01661280", new ElsevierJournal(76));
		issnTable.put("0166218X", new ElsevierJournal(1));
		issnTable.put("01663615", new ElsevierJournal(1));
		issnTable.put("0166445X", new ElsevierJournal(1));
		issnTable.put("01664972", new ElsevierJournal(1));
		issnTable.put("01665162", new ElsevierJournal(1));
		issnTable.put("01665316", new ElsevierJournal(1));
		issnTable.put("01666622", new ElsevierJournal(1));
		issnTable.put("01669834", new ElsevierJournal(1));
		issnTable.put("0167188X", new ElsevierJournal(5));
		issnTable.put("01671987", new ElsevierJournal(1));
		issnTable.put("01672681", new ElsevierJournal(1));
		issnTable.put("01672738", new ElsevierJournal(1));
		issnTable.put("01672789", new ElsevierJournal(1));
		issnTable.put("01674048", new ElsevierJournal(1));
		issnTable.put("01674730", new ElsevierJournal(1));
		issnTable.put("01675087", new ElsevierJournal(197));
		issnTable.put("01675419", new ElsevierJournal(1));
		issnTable.put("01675729", new ElsevierJournal(1));
		issnTable.put("0167577X", new ElsevierJournal(1));
		issnTable.put("01676105", new ElsevierJournal(1));
		issnTable.put("01676245", new ElsevierJournal(1));
		issnTable.put("01676377", new ElsevierJournal(1));
		issnTable.put("01676393", new ElsevierJournal(1));
		issnTable.put("01676423", new ElsevierJournal(1));
		issnTable.put("01676636", new ElsevierJournal(1));
		issnTable.put("01676687", new ElsevierJournal(1));
		issnTable.put("01676911", new ElsevierJournal(1));
		issnTable.put("01676989", new ElsevierJournal(1));
		issnTable.put("01677152", new ElsevierJournal(1));
		issnTable.put("01677187", new ElsevierJournal(1));
		issnTable.put("01677322", new ElsevierJournal(25));
		issnTable.put("0167739X", new ElsevierJournal(1));
		issnTable.put("01677799", new ElsevierJournal(1));
		issnTable.put("01677977", new ElsevierJournal(1));
		issnTable.put("01678140", new ElsevierJournal(34));
		issnTable.put("01678191", new ElsevierJournal(1));
		issnTable.put("01678396", new ElsevierJournal(1));
		issnTable.put("01678442", new ElsevierJournal(1));
		issnTable.put("01678493", new ElsevierJournal(1));
		issnTable.put("01678655", new ElsevierJournal(1));
		issnTable.put("01678809", new ElsevierJournal(9));
		issnTable.put("01679236", new ElsevierJournal(1));
		issnTable.put("01679260", new ElsevierJournal(1));
		issnTable.put("01679317", new ElsevierJournal(1));
		issnTable.put("01679457", new ElsevierJournal(1));
		issnTable.put("01679473", new ElsevierJournal(1));
		issnTable.put("01680072", new ElsevierJournal(24));
		issnTable.put("01681176", new ElsevierJournal(54));
		issnTable.put("01681605", new ElsevierJournal(1));
		issnTable.put("01681656", new ElsevierJournal(1));
		issnTable.put("01681699", new ElsevierJournal(1));
		issnTable.put("01683659", new ElsevierJournal(1));
		issnTable.put("01685597", new ElsevierJournal(59));
		issnTable.put("0168583X", new ElsevierJournal(1));
		issnTable.put("01687336", new ElsevierJournal(1));
		issnTable.put("0168874X", new ElsevierJournal(1));
		issnTable.put("01689002", new ElsevierJournal(226));
		issnTable.put("01689274", new ElsevierJournal(1));
		issnTable.put("01689452", new ElsevierJournal(38));
		issnTable.put("0169023X", new ElsevierJournal(1));
		issnTable.put("01691317", new ElsevierJournal(1));
		issnTable.put("01691368", new ElsevierJournal(1));
		issnTable.put("01692046", new ElsevierJournal(13));
		issnTable.put("01692070", new ElsevierJournal(1));
		issnTable.put("01692607", new ElsevierJournal(20));
		issnTable.put("0169409X", new ElsevierJournal(1));
		issnTable.put("01694332", new ElsevierJournal(22));
		issnTable.put("0169555X", new ElsevierJournal(1));
		issnTable.put("01695983", new ElsevierJournal(1));
		issnTable.put("01697439", new ElsevierJournal(1));
		issnTable.put("01697552", new ElsevierJournal(9));
		issnTable.put("01697722", new ElsevierJournal(1));
		issnTable.put("01698095", new ElsevierJournal(20));
		issnTable.put("01698141", new ElsevierJournal(1));
		issnTable.put("01722190", new ElsevierJournal(1));
		issnTable.put("01912607", new ElsevierJournal(13));
		issnTable.put("01912615", new ElsevierJournal(13));
		issnTable.put("0191278X", new ElsevierJournal(3));
		issnTable.put("01918141", new ElsevierJournal(1));
		issnTable.put("01956698", new ElsevierJournal(14));
		issnTable.put("01959255", new ElsevierJournal(5));
		issnTable.put("01966774", new ElsevierJournal(1));
		issnTable.put("01968904", new ElsevierJournal(20));
		issnTable.put("01973975", new ElsevierJournal(1));
		issnTable.put("01980149", new ElsevierJournal(26));
		issnTable.put("01987593", new ElsevierJournal(1));
		issnTable.put("01989715", new ElsevierJournal(5));
		issnTable.put("02506874", new ElsevierJournal(1));
		issnTable.put("02540584", new ElsevierJournal(8));
		issnTable.put("02552701", new ElsevierJournal(18));
		issnTable.put("02578972", new ElsevierJournal(27));
		issnTable.put("02608774", new ElsevierJournal(1));
		issnTable.put("02613069", new ElsevierJournal(2));
		issnTable.put("02617277", new ElsevierJournal(1));
		issnTable.put("02621762", new ElsevierJournal(1995));
		issnTable.put("02625075", new ElsevierJournal(1));
		issnTable.put("02628856", new ElsevierJournal(1));
		issnTable.put("02632241", new ElsevierJournal(1));
		issnTable.put("02634368", new ElsevierJournal(10));
		issnTable.put("02637855", new ElsevierJournal(1));
		issnTable.put("02637863", new ElsevierJournal(1));
		issnTable.put("02638223", new ElsevierJournal(1));
		issnTable.put("02638231", new ElsevierJournal(1));
		issnTable.put("02643707", new ElsevierJournal(1));
		issnTable.put("0264682X", new ElsevierJournal(1));
		issnTable.put("02648172", new ElsevierJournal(1));
		issnTable.put("02653036", new ElsevierJournal(23));
		issnTable.put("0265928X", new ElsevierJournal(1));
		issnTable.put("0265931X", new ElsevierJournal(1));
		issnTable.put("02661144", new ElsevierJournal(1));
		issnTable.put("0266352X", new ElsevierJournal(1));
		issnTable.put("02663538", new ElsevierJournal(22));
		issnTable.put("02668920", new ElsevierJournal(1));
		issnTable.put("02669838", new ElsevierJournal(1));
		issnTable.put("02673649", new ElsevierJournal(1));
		issnTable.put("02673762", new ElsevierJournal(1));
		issnTable.put("02677261", new ElsevierJournal(5));
		issnTable.put("02680033", new ElsevierJournal(1));
		issnTable.put("02684012", new ElsevierJournal(6));
		issnTable.put("02697491", new ElsevierJournal(50));
		issnTable.put("02700255", new ElsevierJournal(1));
		issnTable.put("02715309", new ElsevierJournal(1));
		issnTable.put("02726963", new ElsevierJournal(1));
		issnTable.put("02727714", new ElsevierJournal(36));
		issnTable.put("02728842", new ElsevierJournal(7));
		issnTable.put("02731177", new ElsevierJournal(1));
		issnTable.put("02731223", new ElsevierJournal(31,40));
		issnTable.put("02751062", new ElsevierJournal(5));
		issnTable.put("02755408", new ElsevierJournal(15,21));
		issnTable.put("02773791", new ElsevierJournal(1));
		issnTable.put("02775387", new ElsevierJournal(1));
		issnTable.put("02779390", new ElsevierJournal(9));
		issnTable.put("02784343", new ElsevierJournal(1));
		issnTable.put("03009416", new ElsevierJournal(1));
		issnTable.put("03009440", new ElsevierJournal(1));
		issnTable.put("03009467", new ElsevierJournal(1));
		issnTable.put("03010104", new ElsevierJournal(1));
		issnTable.put("03014207", new ElsevierJournal(1));
		issnTable.put("03014215", new ElsevierJournal(1));
		issnTable.put("03015629", new ElsevierJournal(1));
		issnTable.put("0301679X", new ElsevierJournal(8));
		issnTable.put("03017516", new ElsevierJournal(1));
		issnTable.put("03019268", new ElsevierJournal(1));
		issnTable.put("03019322", new ElsevierJournal(1));
		issnTable.put("0302184X", new ElsevierJournal(1));
		issnTable.put("03024598", new ElsevierJournal(1));
		issnTable.put("03032434", new ElsevierJournal(1));
		issnTable.put("03032647", new ElsevierJournal(1));
		issnTable.put("0304386X", new ElsevierJournal(1));
		issnTable.put("03043886", new ElsevierJournal(1));
		issnTable.put("03043894", new ElsevierJournal(1));
		issnTable.put("03043975", new ElsevierJournal(1));
		issnTable.put("03043991", new ElsevierJournal(1));
		issnTable.put("03044149", new ElsevierJournal(1));
		issnTable.put("03045102", new ElsevierJournal(1));
		issnTable.put("03048853", new ElsevierJournal(1));
		issnTable.put("03050483", new ElsevierJournal(1));
		issnTable.put("03050548", new ElsevierJournal(1));
		issnTable.put("03062619", new ElsevierJournal(1));
		issnTable.put("03063747", new ElsevierJournal(17));
		issnTable.put("03064379", new ElsevierJournal(1));
		issnTable.put("03064549", new ElsevierJournal(2));
		issnTable.put("03064565", new ElsevierJournal(1));
		issnTable.put("03064573", new ElsevierJournal(11));
		issnTable.put("0307904X", new ElsevierJournal(1));
		issnTable.put("03080161", new ElsevierJournal(1));
		issnTable.put("03085953", new ElsevierJournal(1));
		issnTable.put("03085961", new ElsevierJournal(1));
		issnTable.put("03089126", new ElsevierJournal(9));
		issnTable.put("03091708", new ElsevierJournal(1));
		issnTable.put("03601285", new ElsevierJournal(1));
		issnTable.put("03601315", new ElsevierJournal(1));
		issnTable.put("03601323", new ElsevierJournal(11));
		issnTable.put("03603016", new ElsevierJournal(31));
		issnTable.put("03603199", new ElsevierJournal(1));
		issnTable.put("03605442", new ElsevierJournal(1));
		issnTable.put("03608352", new ElsevierJournal(1));
		issnTable.put("0362546X", new ElsevierJournal(1));
		issnTable.put("03640213", new ElsevierJournal(12));
		issnTable.put("03645916", new ElsevierJournal(1));
		issnTable.put("03646408", new ElsevierJournal(1));
		issnTable.put("03682048", new ElsevierJournal(1));
		issnTable.put("03701573", new ElsevierJournal(1));
		issnTable.put("03702693", new ElsevierJournal(24));
		issnTable.put("03743926", new ElsevierJournal(2));
		issnTable.put("03756505", new ElsevierJournal(1));
		issnTable.put("03756742", new ElsevierJournal(1));
		issnTable.put("03759474", new ElsevierJournal(90));
		issnTable.put("03759601", new ElsevierJournal(5));
		issnTable.put("03760421", new ElsevierJournal(1));
		issnTable.put("03764583", new ElsevierJournal(4));
		issnTable.put("03766349", new ElsevierJournal(1));
		issnTable.put("03767388", new ElsevierJournal(1));
		issnTable.put("03770257", new ElsevierJournal(1));
		issnTable.put("03770265", new ElsevierJournal(1));
		issnTable.put("03770273", new ElsevierJournal(1));
		issnTable.put("03770427", new ElsevierJournal(1));
		issnTable.put("03772217", new ElsevierJournal(1));
		issnTable.put("03778398", new ElsevierJournal(1));
		issnTable.put("03781127", new ElsevierJournal(1));
		issnTable.put("03782166", new ElsevierJournal(1));
		issnTable.put("03783758", new ElsevierJournal(1));
		issnTable.put("03783774", new ElsevierJournal(1));
		issnTable.put("03783804", new ElsevierJournal(1));
		issnTable.put("03783812", new ElsevierJournal(1));
		issnTable.put("03783820", new ElsevierJournal(1));
		issnTable.put("03783839", new ElsevierJournal(1));
		issnTable.put("03784290", new ElsevierJournal(1));
		issnTable.put("03784347", new ElsevierJournal(143));
		issnTable.put("03784363", new ElsevierJournal(79));
		issnTable.put("03784371", new ElsevierJournal(79));
		issnTable.put("03784487", new ElsevierJournal(10));
		issnTable.put("03784754", new ElsevierJournal(1));
		issnTable.put("03785955", new ElsevierJournal(1));
		issnTable.put("03785963", new ElsevierJournal(1));
		issnTable.put("03787206", new ElsevierJournal(1));
		issnTable.put("03787753", new ElsevierJournal(1));
		issnTable.put("03787788", new ElsevierJournal(1));
		issnTable.put("03787796", new ElsevierJournal(1));
		issnTable.put("03796779", new ElsevierJournal(1));
		issnTable.put("03796787", new ElsevierJournal(1));
		issnTable.put("03797112", new ElsevierJournal(1));
		issnTable.put("03894304", new ElsevierJournal(15));
		issnTable.put("03905519", new ElsevierJournal(1));
		issnTable.put("03930440", new ElsevierJournal(1));
		issnTable.put("03991784", new ElsevierJournal(21,26));
		issnTable.put("05503213", new ElsevierJournal(1));
		issnTable.put("05848539", new ElsevierJournal(23));
		issnTable.put("05848547", new ElsevierJournal(23));
		issnTable.put("0720048X", new ElsevierJournal(10));
		issnTable.put("0730725X", new ElsevierJournal(1));
		issnTable.put("07335210", new ElsevierJournal(17));
		//issnTable.put("0734242X", new ElsevierJournal(11));
		issnTable.put("0734743X", new ElsevierJournal(1));
		issnTable.put("07349750", new ElsevierJournal(1));
		issnTable.put("07351933", new ElsevierJournal(1));
		issnTable.put("0735245X", new ElsevierJournal(21));
		issnTable.put("07356757", new ElsevierJournal(18));
		issnTable.put("07360266", new ElsevierJournal(19));
		issnTable.put("07365845", new ElsevierJournal(1));
		issnTable.put("07365853", new ElsevierJournal(1));
		issnTable.put("07376782", new ElsevierJournal(1,19));
		//issnTable.put("07388551", new ElsevierJournal(18));
		issnTable.put("07396260", new ElsevierJournal(15));
		issnTable.put("0740624X", new ElsevierJournal(1));
		issnTable.put("07408188", new ElsevierJournal(16));
		issnTable.put("0741983X", new ElsevierJournal(1));
		issnTable.put("07431066", new ElsevierJournal(1));
		issnTable.put("07437315", new ElsevierJournal(1));
		issnTable.put("07457138", new ElsevierJournal(16));
		issnTable.put("07475632", new ElsevierJournal(1));
		issnTable.put("07477171", new ElsevierJournal(15));
		issnTable.put("07496036", new ElsevierJournal(1));
		issnTable.put("07496419", new ElsevierJournal(1));
		issnTable.put("07644442", new ElsevierJournal(324));
		issnTable.put("07644469", new ElsevierJournal(320));
		issnTable.put("08832889", new ElsevierJournal(24));
		issnTable.put("08832927", new ElsevierJournal(1));
		issnTable.put("0885064X", new ElsevierJournal(1));
		issnTable.put("08852308", new ElsevierJournal(2));
		issnTable.put("08867798", new ElsevierJournal(1));
		issnTable.put("08883270", new ElsevierJournal(1));
		issnTable.put("0888613X", new ElsevierJournal(1));
		issnTable.put("08899746", new ElsevierJournal(1));
		issnTable.put("08904332", new ElsevierJournal(7));
		issnTable.put("08905401", new ElsevierJournal(92));
		issnTable.put("08906955", new ElsevierJournal(28));
		issnTable.put("08926875", new ElsevierJournal(1));
		issnTable.put("08936080", new ElsevierJournal(1));
		issnTable.put("08939659", new ElsevierJournal(1));
		issnTable.put("08941777", new ElsevierJournal(1));
		issnTable.put("08953996", new ElsevierJournal(1));
		issnTable.put("08956111", new ElsevierJournal(12));
		issnTable.put("08957177", new ElsevierJournal(10));
		issnTable.put("08959811", new ElsevierJournal(1));
		issnTable.put("08968446", new ElsevierJournal(1));
		issnTable.put("08981221", new ElsevierJournal(1));
		issnTable.put("08995362", new ElsevierJournal(1));
		issnTable.put("08997071", new ElsevierJournal(13));
		issnTable.put("08998248", new ElsevierJournal(1));
		issnTable.put("08998256", new ElsevierJournal(1));
		issnTable.put("09202307", new ElsevierJournal(1));
		issnTable.put("09203796", new ElsevierJournal(5));
		issnTable.put("09204105", new ElsevierJournal(1));
		issnTable.put("09205489", new ElsevierJournal(5));
		issnTable.put("09205632", new ElsevierJournal(1));
		issnTable.put("09205861", new ElsevierJournal(1));
		issnTable.put("09213449", new ElsevierJournal(1));
		issnTable.put("09214526", new ElsevierJournal(127));
		issnTable.put("09214534", new ElsevierJournal(152));
		issnTable.put("09215093", new ElsevierJournal(107));
		issnTable.put("09215107", new ElsevierJournal(1));
		issnTable.put("09218181", new ElsevierJournal(1));
		issnTable.put("09218890", new ElsevierJournal(4));
		issnTable.put("0922338X", new ElsevierJournal(67));
		issnTable.put("09230467", new ElsevierJournal(53));
		issnTable.put("09231137", new ElsevierJournal(10));
		issnTable.put("09234748", new ElsevierJournal(6));
		issnTable.put("09235965", new ElsevierJournal(1));
		issnTable.put("09240136", new ElsevierJournal(21));
		issnTable.put("09242031", new ElsevierJournal(1));
		issnTable.put("09242244", new ElsevierJournal(1));
		issnTable.put("09242716", new ElsevierJournal(44));
		issnTable.put("09244247", new ElsevierJournal(21));
		issnTable.put("09247963", new ElsevierJournal(1));
		issnTable.put("0924980X", new ElsevierJournal(97));
		issnTable.put("09252312", new ElsevierJournal(1));
		issnTable.put("09253467", new ElsevierJournal(1));
		issnTable.put("09254005", new ElsevierJournal(1));
		issnTable.put("09255273", new ElsevierJournal(22));
		issnTable.put("09257535", new ElsevierJournal(14));
		issnTable.put("09257721", new ElsevierJournal(1));
		issnTable.put("09258388", new ElsevierJournal(176));
		issnTable.put("09258574", new ElsevierJournal(1));
		issnTable.put("09259635", new ElsevierJournal(1));
		issnTable.put("09262040", new ElsevierJournal(1));
		issnTable.put("09262245", new ElsevierJournal(1));
		issnTable.put("09263373", new ElsevierJournal(1));
		issnTable.put("09265805", new ElsevierJournal(1));
		issnTable.put("09266690", new ElsevierJournal(1));
		issnTable.put("09269851", new ElsevierJournal(29));
		issnTable.put("09270248", new ElsevierJournal(25));
		issnTable.put("09270256", new ElsevierJournal(1));
		issnTable.put("09276505", new ElsevierJournal(1));
		issnTable.put("09276513", new ElsevierJournal(1));
		issnTable.put("09277757", new ElsevierJournal(70));
		issnTable.put("09277765", new ElsevierJournal(1));
		issnTable.put("0927796X", new ElsevierJournal(10));
		issnTable.put("09280987", new ElsevierJournal(1));
		issnTable.put("09284869", new ElsevierJournal(1));
		issnTable.put("09284931", new ElsevierJournal(1));
		issnTable.put("09287655", new ElsevierJournal(15));
		issnTable.put("09298266", new ElsevierJournal(2));
		issnTable.put("09333657", new ElsevierJournal(1));
		issnTable.put("09500618", new ElsevierJournal(1));
		issnTable.put("09504214", new ElsevierJournal(1));
		issnTable.put("09504230", new ElsevierJournal(1));
		issnTable.put("09505849", new ElsevierJournal(29));
		issnTable.put("09507051", new ElsevierJournal(1));
		issnTable.put("09515240", new ElsevierJournal(1));
		issnTable.put("09518312", new ElsevierJournal(11));
		issnTable.put("09518320", new ElsevierJournal(20));
		issnTable.put("09518339", new ElsevierJournal(1));
		issnTable.put("09521976", new ElsevierJournal(1));
		issnTable.put("09535438", new ElsevierJournal(1));
		issnTable.put("09541810", new ElsevierJournal(1));
		issnTable.put("0954349X", new ElsevierJournal(1));
		issnTable.put("09552219", new ElsevierJournal(5));
		issnTable.put("09555986", new ElsevierJournal(1));
		issnTable.put("09557997", new ElsevierJournal(6));
		issnTable.put("0956053X", new ElsevierJournal(9));
		issnTable.put("09565663", new ElsevierJournal(5));
		issnTable.put("09567151", new ElsevierJournal(38));
		issnTable.put("0956716X", new ElsevierJournal(24));
		issnTable.put("09569618", new ElsevierJournal(1));
		issnTable.put("09571272", new ElsevierJournal(24));
		issnTable.put("09571787", new ElsevierJournal(1));
		issnTable.put("09574158", new ElsevierJournal(1));
		issnTable.put("09574166", new ElsevierJournal(1));
		issnTable.put("09574174", new ElsevierJournal(1));
		issnTable.put("09581669", new ElsevierJournal(1));
		issnTable.put("09583947", new ElsevierJournal(20));
		issnTable.put("09589465", new ElsevierJournal(11));
		issnTable.put("09591524", new ElsevierJournal(1));
		issnTable.put("09593527", new ElsevierJournal(1));
		issnTable.put("09596526", new ElsevierJournal(1));
		issnTable.put("09598022", new ElsevierJournal(1));
		issnTable.put("09600779", new ElsevierJournal(1));
		issnTable.put("09601481", new ElsevierJournal(1));
		issnTable.put("09601686", new ElsevierJournal(24));
		issnTable.put("09602593", new ElsevierJournal(1989));
		issnTable.put("09608524", new ElsevierJournal(35));
		issnTable.put("09608974", new ElsevierJournal(20));
		issnTable.put("09611290", new ElsevierJournal(4));
		issnTable.put("09613552", new ElsevierJournal(13));
		issnTable.put("09619534", new ElsevierJournal(1));
		issnTable.put("09638687", new ElsevierJournal(1));
		issnTable.put("09638695", new ElsevierJournal(23));
		issnTable.put("09639969", new ElsevierJournal(25));
		issnTable.put("09641807", new ElsevierJournal(1));
		issnTable.put("09645691", new ElsevierJournal(17));
		issnTable.put("09648305", new ElsevierJournal(29));
		issnTable.put("09658564", new ElsevierJournal(26));
		issnTable.put("09659773", new ElsevierJournal(1));
		issnTable.put("09659978", new ElsevierJournal(14));
		issnTable.put("09667822", new ElsevierJournal(1));
		issnTable.put("09668349", new ElsevierJournal(3));
		issnTable.put("09669795", new ElsevierJournal(1));
		issnTable.put("09670637", new ElsevierJournal(40));
		issnTable.put("09670645", new ElsevierJournal(40));
		issnTable.put("09670661", new ElsevierJournal(1));
		issnTable.put("0968090X", new ElsevierJournal(1));
		issnTable.put("09685677", new ElsevierJournal(1));
		issnTable.put("09694765", new ElsevierJournal(7));
		issnTable.put("09696016", new ElsevierJournal(1,7));
		issnTable.put("09698043", new ElsevierJournal(24));
		issnTable.put("0969806X", new ElsevierJournal(41));
		issnTable.put("09924361", new ElsevierJournal(35));
		issnTable.put("09977538", new ElsevierJournal(17));
		issnTable.put("09977546", new ElsevierJournal(17));
		issnTable.put("10075704", new ElsevierJournal(1));
		issnTable.put("10106030", new ElsevierJournal(40));
		issnTable.put("10406182", new ElsevierJournal(1));
		issnTable.put("10408347", new ElsevierJournal(28));
		issnTable.put("10408436", new ElsevierJournal(23));
		issnTable.put("10428143", new ElsevierJournal(3));
		issnTable.put("10440305", new ElsevierJournal(1));
		issnTable.put("10445803", new ElsevierJournal(24));
		issnTable.put("1045926X", new ElsevierJournal(3));
		issnTable.put("10473203", new ElsevierJournal(4));
		issnTable.put("10499652", new ElsevierJournal(53));
		issnTable.put("10499660", new ElsevierJournal(57));
		issnTable.put("10512004", new ElsevierJournal(1));
		issnTable.put("10588337", new ElsevierJournal(7));
		issnTable.put("10635203", new ElsevierJournal(1));
		issnTable.put("10641858", new ElsevierJournal(101));
		issnTable.put("10641866", new ElsevierJournal(101));
		issnTable.put("10643389", new ElsevierJournal(28));
		issnTable.put("10657355", new ElsevierJournal(1));
		issnTable.put("10667938", new ElsevierJournal(5,9));
		issnTable.put("10685200", new ElsevierJournal(1));
		issnTable.put("10690115", new ElsevierJournal(1));
		issnTable.put("10715797", new ElsevierJournal(1));
		issnTable.put("10715819", new ElsevierJournal(40));
		issnTable.put("10749098", new ElsevierJournal(6));
		issnTable.put("10772014", new ElsevierJournal(1));
		issnTable.put("10773142", new ElsevierJournal(57));
		issnTable.put("10773169", new ElsevierJournal(57));
		issnTable.put("10848045", new ElsevierJournal(19));
		issnTable.put("10848568", new ElsevierJournal(1));
		issnTable.put("1088467X", new ElsevierJournal(1,3));
		issnTable.put("10893156", new ElsevierJournal(7));
		issnTable.put("10907807", new ElsevierJournal(124));
		issnTable.put("10930191", new ElsevierJournal(4));
		issnTable.put("10933263", new ElsevierJournal(15));
		issnTable.put("10967516", new ElsevierJournal(1));
		issnTable.put("10972765", new ElsevierJournal(1));
		issnTable.put("11645563", new ElsevierJournal(34));
		issnTable.put("12518069", new ElsevierJournal(324));
		issnTable.put("12709638", new ElsevierJournal(1));
		issnTable.put("12874620", new ElsevierJournal(326));
		issnTable.put("12883255", new ElsevierJournal(1));
		issnTable.put("12932558", new ElsevierJournal(1));
		issnTable.put("12962147", new ElsevierJournal(1));
		issnTable.put("13504177", new ElsevierJournal(1));
		issnTable.put("13504487", new ElsevierJournal(23));
		issnTable.put("13504495", new ElsevierJournal(35));
		issnTable.put("13504533", new ElsevierJournal(17));
		issnTable.put("13506307", new ElsevierJournal(1));
		issnTable.put("13520237", new ElsevierJournal(21));
		issnTable.put("13522310", new ElsevierJournal(28));
		issnTable.put("13528661", new ElsevierJournal(6,15));
		issnTable.put("13534858", new ElsevierJournal(1994));
		issnTable.put("13590286", new ElsevierJournal(1));
		issnTable.put("13590294", new ElsevierJournal(4));
		issnTable.put("13594311", new ElsevierJournal(16));
		issnTable.put("13596454", new ElsevierJournal(44));
		issnTable.put("13596462", new ElsevierJournal(34));
		issnTable.put("1359835X", new ElsevierJournal(27));
		issnTable.put("13598368", new ElsevierJournal(27));
		issnTable.put("13613723", new ElsevierJournal(1996));
		issnTable.put("13619209", new ElsevierJournal(1));
		issnTable.put("13634127", new ElsevierJournal(1));
		issnTable.put("13640321", new ElsevierJournal(1));
		issnTable.put("13646613", new ElsevierJournal(1));
		issnTable.put("13646826", new ElsevierJournal(59));
		issnTable.put("13648152", new ElsevierJournal(12));
		issnTable.put("13651609", new ElsevierJournal(34));
		issnTable.put("13675788", new ElsevierJournal(20));
		issnTable.put("13697021", new ElsevierJournal(5));
		issnTable.put("1369703X", new ElsevierJournal(1));
		issnTable.put("13698001", new ElsevierJournal(1));
		issnTable.put("13698869", new ElsevierJournal(1));
		issnTable.put("13811169", new ElsevierJournal(95));
		issnTable.put("13811177", new ElsevierJournal(1));
		issnTable.put("1381141X", new ElsevierJournal(31));
		issnTable.put("13815148", new ElsevierJournal(26));
		issnTable.put("13835866", new ElsevierJournal(11));
		issnTable.put("13837621", new ElsevierJournal(42));
		issnTable.put("13841076", new ElsevierJournal(1));
		issnTable.put("13858947", new ElsevierJournal(65));
		issnTable.put("13861425", new ElsevierJournal(51));
		issnTable.put("13865056", new ElsevierJournal(44));
		issnTable.put("13869477", new ElsevierJournal(1));
		issnTable.put("13871811", new ElsevierJournal(20));
		issnTable.put("13873806", new ElsevierJournal(176));
		issnTable.put("13876473", new ElsevierJournal(42));
		issnTable.put("13882457", new ElsevierJournal(110));
		issnTable.put("13882481", new ElsevierJournal(1));
		issnTable.put("13890417", new ElsevierJournal(1));
		issnTable.put("13891286", new ElsevierJournal(31));
		issnTable.put("13891723", new ElsevierJournal(87));
		issnTable.put("13899341", new ElsevierJournal(1));
		issnTable.put("14630184", new ElsevierJournal(2));
		issnTable.put("14641895", new ElsevierJournal(24));
		issnTable.put("14641909", new ElsevierJournal(24));
		issnTable.put("14641917", new ElsevierJournal(24));
		issnTable.put("14649055", new ElsevierJournal(23));
		issnTable.put("14659972", new ElsevierJournal(1));
		issnTable.put("14670895", new ElsevierJournal(1));
		issnTable.put("14681218", new ElsevierJournal(1));
		issnTable.put("14686996", new ElsevierJournal(1));
		issnTable.put("14713918", new ElsevierJournal(2000));
		issnTable.put("14714051", new ElsevierJournal(1));
		issnTable.put("14715317", new ElsevierJournal(1));
		issnTable.put("14717727", new ElsevierJournal(11));
		issnTable.put("14738325", new ElsevierJournal(2002));
		issnTable.put("14740346", new ElsevierJournal(16));
		issnTable.put("14747065", new ElsevierJournal(1));
		issnTable.put("14769271", new ElsevierJournal(27));
		issnTable.put("15240703", new ElsevierJournal(62));
		issnTable.put("15320464", new ElsevierJournal(34));
		issnTable.put("15661199", new ElsevierJournal(1));
		issnTable.put("15662535", new ElsevierJournal(1));
		issnTable.put("15674223", new ElsevierJournal(1));
		issnTable.put("15675394", new ElsevierJournal(51));
		issnTable.put("15678326", new ElsevierJournal(47));
		issnTable.put("15684946", new ElsevierJournal(1));
		issnTable.put("1569190X", new ElsevierJournal(10));
		issnTable.put("15700232", new ElsevierJournal(766));
		issnTable.put("16207742", new ElsevierJournal(328));
		issnTable.put("87554615", new ElsevierJournal(1));
    }

    public boolean hasLink(String ISSN_1,
                           String firstVolume_1,
                           String firstPage_1,
                           String firstIssue_1)
    {
        if((ISSN_1 == null || ISSN_1.length() == 0)
            || (firstVolume_1 == null || firstVolume_1.length() == 0)
            || (firstPage_1 == null || firstPage_1.length() == 0))
        {
            return false;
        }

        if(issnTable.containsKey(ISSN_1))
        {
            ElsevierJournal journal = (ElsevierJournal)issnTable.get(ISSN_1);
            int vol = -1;

            try
            {
                vol = Integer.parseInt(firstVolume_1);
            }
            catch(Exception e)
            {
                return false;
            }

            if(vol < journal.getVolBegin() || (journal.getVolEnd() != -1 && vol > journal.getVolEnd()))
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        return false;
    }

    public String getVolkeyLink(String ISSN_1,
                                String firstVolume_1,
                                String firstPage_1,
                                String firstIssue_1)
        throws Exception
    {
        String link = null;

        if(hasLink(ISSN_1,
                   firstVolume_1,
                   firstPage_1,
                   firstIssue_1))
        {
            String httpVariables = null;
            String volkey        = null;
            StringBuffer volkeyBf = new StringBuffer();

            volkeyBf.append(ISSN_1);
            volkeyBf.append("#");
            volkeyBf.append(firstVolume_1);
            volkeyBf.append("#");
            volkeyBf.append(firstPage_1);
            if((firstIssue_1 != null) && (firstIssue_1.length() > 0))
            {
                volkeyBf.append("#");
                volkeyBf.append(firstIssue_1);
            }

            volkey = volkeyBf.toString();

            httpVariables = getHttpVariables(origin,
                                URLVersion,
                                saltVersion,
                                method,
                                object,
                                volkey);

            MD5Digester digester = new MD5Digester();
            String theDigest = digester.asHex(digester.digest(httpVariables+salt));

            httpVariables = sUtil.replace(httpVariables, "#", "%23", 1, 4);

            httpVariables = httpVariables+"&md5="+theDigest;

            link = scienceDirectUrl+"?"+httpVariables;
        }

        return link;
    }

    public String getHttpVariables(String origin,
                       String URLVersion,
                       String saltVersion,
                       String method,
                       String object,
                       String volkey)
    {

        StringBuffer buffer = new StringBuffer();
        buffer.append("_ob=");
        buffer.append(object);
        buffer.append("&_origin=");
        buffer.append(origin);
        buffer.append("&_urlversion=");
        buffer.append(URLVersion);
        buffer.append("&_method=");
        buffer.append(method);
        buffer.append("&_volkey=");
        buffer.append(volkey);
        buffer.append("&_version=");
        buffer.append(saltVersion);

//System.out.println("getHttpVariables: "+buffer.toString());

        return buffer.toString();
    }


    class ElsevierJournal
    {
        private int volBegin;
        private int volEnd = -1;

        public ElsevierJournal(int _volBegin)
        {
            this.volBegin = _volBegin;
        }

        public ElsevierJournal(int _volBegin, int _volEnd)
        {
               this.volBegin = _volBegin;
               this.volEnd = _volEnd;
        }

        public int getVolBegin()
        {
            return this.volBegin;
        }

        public int getVolEnd()
        {
            return this.volEnd;
        }

    }
}
