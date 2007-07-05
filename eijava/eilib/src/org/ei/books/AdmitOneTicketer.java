package org.ei.books;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AdmitOneTicketer
{

	private String secretname = "Elsevier";
	private String secret = "35738437";
	private static final String SHRDKEY = "!MM01234-5-6789MM#";
	private String id = "Authorised";
    private String baseUrl = "http://referexengineering.elsevier.com";
	//private String baseUrl = "http://usage.elsevier.com:11080/wobl";
	private static final long expireIn =   172800000L; // 48 Hours
	private static final long createEvery = 86400000L; // 24 Hours
	private long createTime = -1L;
	private Hashtable tickets;
	private static AdmitOneTicketer instance;
	private static int count = 0;

	public static synchronized AdmitOneTicketer getInstance()
	{
		if(instance == null)
		{
		   instance = new AdmitOneTicketer();
		}

		return instance;
	}

	private AdmitOneTicketer()
	{
		tickets = new Properties();

	}

	public String getPageTicket(String isbn,
								String custid,
								long currentTime)
		throws BookException
	{
		// getTicket();
		StringBuffer ticketVal = new StringBuffer();
		ticketVal.append(isbn).append(custid).append(currentTime);
		ticketVal.append(AdmitOneTicketer.SHRDKEY);
		String ticket = "";

		try {
			ticket = getTicket(ticketVal.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ticket = ticket.concat(":").concat(custid).concat(":").concat(String.valueOf(currentTime));
		System.out.println(ticket);
		
		return ticket;
	}
	
	public String getSectionTicketedURL(String link,
										String custID,
										long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		String parsedLink = link.replaceAll("\\s",""); //perl.substitute("s/\\s+//g", link);
		buf.append(parsedLink);
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}

	public String getBookTicketedURL(String isbn,
									 String custID,
									 long currentTime)
		throws BookException
	{
		StringBuffer buf = new StringBuffer(baseUrl);
		buf.append("/");
		buf.append("e");
		buf.append(isbn);
		buf.append(".pdf");
		return getTicketedURL(buf.toString(),
							  custID,
							  currentTime);
	}



	private synchronized String getTicketedURL(String fullUrl,
											   String custID,
											   long currentTime)
		throws BookException
	{
		Ticket ticket = null;
		StringBuffer buf = new StringBuffer();
		String formattedExpires;


		try
		{

			String anchor = null;
			String root = getRoot(fullUrl);
			String path = getPath(fullUrl);

			if (fullUrl.indexOf("#") > -1)
			{
				anchor = getAnchor(fullUrl);
			}


			if((createTime == -1) ||
			   ((currentTime - createTime) > createEvery))
			{
				tickets = new Hashtable();
				createTime = currentTime;
			}
			else
			{
				ticket = (Ticket)tickets.get(path);
			}


			if(ticket == null)
			{
				ticket = new Ticket();
				formattedExpires = formatExpires(expireIn);
				ticket.t = getTicket(path + formattedExpires + secretname + secret + id);
				ticket.e = formattedExpires;
				tickets.put(path,ticket);
			}


			buf.append(root);
			buf.append(path);
			buf.append("?expires=");
			buf.append(ticket.e);
			buf.append("&secretname=");
			buf.append(URLEncoder.encode(secretname,"UTF-8"));
			buf.append("&id=");
			buf.append(URLEncoder.encode(id,"UTF-8"));
			buf.append("&ticket=");
			buf.append(ticket.t);
			buf.append("&f=d.exe");
			buf.append("&count="+Integer.toString(count++));
			buf.append("&custid="+custID);
			if(count > 1000000)
			{
				count = 0;
			}

			if (anchor != null)
			{
				buf.append("#nameddest=").append(anchor);
				buf.append("&zoom=100");
				buf.append("&pagemode=none");
			} else {
				buf.append("#page=1&zoom=100");
				buf.append("&pagemode=none");
			}
		}
		catch (Exception e)
		{
			throw new BookException(e);
		}

		return buf.toString();
	}

	private String getTicket(String s) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] di = md5.digest(s.getBytes());
		return asHex(di);
	}

	private String getPath(String fullUrl)
	{
		String rest = fullUrl.substring(7, fullUrl.length());
		int slashIndex = rest.indexOf("/");
		int hashIndex = rest.indexOf("#");
		if (hashIndex > -1)
		{
			return rest.substring(slashIndex, hashIndex);
		}
		else
		{
			return rest.substring(slashIndex);
		}
	}

	private String getRoot(String fullUrl)
	{
		String part1 = fullUrl.substring(0, 7);
		String rest = fullUrl.substring(7, fullUrl.length());
		int slashIndex = rest.indexOf("/");
		return part1 + rest.substring(0, slashIndex);
	}

	private String getAnchor(String fullUrl)
	{
		int hashIndex = fullUrl.indexOf("#") + 1;

		return fullUrl.substring(hashIndex);
	}

	private String asHex(byte[] hash)
	{
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++)
		{
			if (((int) hash[i] & 0xff) < 0x10)
			{
				buf.append("0");
			}

			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}

		return buf.toString();
	}

	private String formatExpires(long expireIn)
	{
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() + AdmitOneTicketer.expireIn);
        DateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        return sd.format(cal.getTime());
	}


	class Ticket
	{
		public String t;
		public String e;
	}

	public static void main(String[] args) {
        Map allIsbns = new Hashtable();
    
        allIsbns.put("0080426794","9780080426792");
        allIsbns.put("0080426824","9780080426822");
        allIsbns.put("0080426832","9780080426839");
        allIsbns.put("0080426956","9780080426952");
        allIsbns.put("0080426964","9780080426969");
        allIsbns.put("0080427022","9780080427027");
        allIsbns.put("0080429998","9780080429991");
        allIsbns.put("0080430090","9780080430096");
        allIsbns.put("0080430104","9780080430102");
        allIsbns.put("0080437125","9780080437125");
        allIsbns.put("0080438644","9780080438641");
        allIsbns.put("0080439217","9780080439211");
        allIsbns.put("0080439381","9780080439389");
        allIsbns.put("0080439594","9780080439594");
        allIsbns.put("0080440290","9780080440293");
        allIsbns.put("0080440649","9780080440644");
        allIsbns.put("0080441041","9780080441047");
        allIsbns.put("0080441068","9780080441061");
        allIsbns.put("0080441297","9780080441290");
        allIsbns.put("0080441637","9780080441634");
        allIsbns.put("0080442730","9780080442730");
        allIsbns.put("008044377x","9780080443775");
        allIsbns.put("0080444954","9780080444956");
        allIsbns.put("0080445020","9780080445021");
        allIsbns.put("0080445454","9780080445458");
        allIsbns.put("0120451433","9780120451432");
        allIsbns.put("0120451441","9780120451449");
        allIsbns.put("0120471418","9780120471416");
        allIsbns.put("0120530791","9780120530793");
        allIsbns.put("0120641550","9780120641550");
        allIsbns.put("0120777908","9780120777907");
        allIsbns.put("0120845903","9780120845903");
        allIsbns.put("0121189309","9780121189303");
        allIsbns.put("0121197905","9780121197902");
        allIsbns.put("0121370305","9780121370305");
        allIsbns.put("0121641376","9780121641375");
        allIsbns.put("0121709604","9780121709600");
        allIsbns.put("0121782514","9780121782511");
        allIsbns.put("0122078918","9780122078910");
        allIsbns.put("0122078926","9780122078927");
        allIsbns.put("012222695x","9780122226953");
        allIsbns.put("0122252616","9780122252617");
        allIsbns.put("0122368452","9780122368455");
        allIsbns.put("0122370759","9780122370755");
        allIsbns.put("0122374614","9780122374616");
        allIsbns.put("012237472x","9780122374722");
        allIsbns.put("0122482913","9780122482915");
        allIsbns.put("0122619129","9780122619120");
        allIsbns.put("0122698517","9780122698514");
        allIsbns.put("0122821602","9780122821608");
        allIsbns.put("0122896769","9780122896767");
        allIsbns.put("012387582x","9780123875822");
        allIsbns.put("0123951704","9780123951700");
        allIsbns.put("0123951712","9780123951717");
        allIsbns.put("0123951720","9780123951724");
        allIsbns.put("0123951739","9780123951731");
        allIsbns.put("0123965616","9780123965615");
        allIsbns.put("0124005608","9780124005600");
        allIsbns.put("0124518303","9780124518308");
        allIsbns.put("0124518311","9780124518315");
        allIsbns.put("0124647863","9780124647862");
        allIsbns.put("012466606x","9780124666061");
        allIsbns.put("0124680410","9780124680418");
        allIsbns.put("012471370x","9780124713703");
        allIsbns.put("012524990x","9780125249904");
        allIsbns.put("0125443609","9780125443609");
        allIsbns.put("0125444613","9780125444613");
        allIsbns.put("0125571909","9780125571906");
        allIsbns.put("012561540x","9780125615402");
        allIsbns.put("0125769601","9780125769600");
        allIsbns.put("0125816502","9780125816502");
        allIsbns.put("0125824602","9780125824606");
        allIsbns.put("0125824610","9780125824613");
        allIsbns.put("0126058113","9780126058116");
        allIsbns.put("0126185204","9780126185201");
        allIsbns.put("0126354901","9780126354904");
        allIsbns.put("0126561508","9780126561500");
        allIsbns.put("0126561532","9780126561531");
        allIsbns.put("0126853517","9780126853513");
        allIsbns.put("0126858756","9780126858754");
        allIsbns.put("0126912955","9780126912951");
        allIsbns.put("0127100571","9780127100579");
        allIsbns.put("0127345302","9780127345307");
        allIsbns.put("0127597514","9780127597515");
        allIsbns.put("0127748113","9780127748115");
        allIsbns.put("0340614579","9780340614570");
        allIsbns.put("0340676507","9780340676509");
        allIsbns.put("034069159x","9780340691595");
        allIsbns.put("0340705884","9780340705889");
        allIsbns.put("0340740760","9780340740767");
        allIsbns.put("0444510524","9780444510525");
        allIsbns.put("0444513094","9780444513090");
        allIsbns.put("0444515577","9780444515575");
        allIsbns.put("0444516999","9780444516992");
        allIsbns.put("0444823735","9780444823731");
        allIsbns.put("0444828230","9780444828231");
        allIsbns.put("0444829520","9780444829528");
        allIsbns.put("0444898751","9780444898753");
        allIsbns.put("0750611588","9780750611589");
        allIsbns.put("0750611820","9780750611824");
        allIsbns.put("0750611987","9780750611985");
        allIsbns.put("0750619600","9780750619608");
        allIsbns.put("0750625295","9780750625296");
        allIsbns.put("0750625309","9780750625302");
        allIsbns.put("0750626291","9780750626293");
        allIsbns.put("0750627824","9780750627825");
        allIsbns.put("0750628944","9780750628945");
        allIsbns.put("0750629266","9780750629263");
        allIsbns.put("0750630817","9780750630818");
        allIsbns.put("0750631406","9780750631402");
        allIsbns.put("0750634294","9780750634298");
        allIsbns.put("0750635894","9780750635899");
        allIsbns.put("0750636238","9780750636230");
        allIsbns.put("0750636246","9780750636247");
        allIsbns.put("0750636254","9780750636254");
        allIsbns.put("0750636343","9780750636346");
        allIsbns.put("075063636x","9780750636360");
        allIsbns.put("0750637641","9780750637640");
        allIsbns.put("0750637684","9780750637688");
        allIsbns.put("0750637706","9780750637701");
        allIsbns.put("0750639962","9780750639965");
        allIsbns.put("0750640197","9780750640190");
        allIsbns.put("0750641010","9780750641012");
        allIsbns.put("0750641320","9780750641326");
        allIsbns.put("0750641339","9780750641333");
        allIsbns.put("0750642181","9780750642187");
        allIsbns.put("075064219x","9780750642194");
        allIsbns.put("0750642343","9780750642347");
        allIsbns.put("075064284x","9780750642842");
        allIsbns.put("0750642866","9780750642866");
        allIsbns.put("0750643552","9780750643559");
        allIsbns.put("0750643986","9780750643986");
        allIsbns.put("0750644451","9780750644457");
        allIsbns.put("0750644478","9780750644471");
        allIsbns.put("0750644494","9780750644495");
        allIsbns.put("0750644516","9780750644518");
        allIsbns.put("0750645644","9780750645645");
        allIsbns.put("0750645687","9780750645683");
        allIsbns.put("075064625x","9780750646253");
        allIsbns.put("0750646373","9780750646376");
        allIsbns.put("0750647779","9780750647779");
        allIsbns.put("0750647906","9780750647908");
        allIsbns.put("0750648104","9780750648103");
        allIsbns.put("0750648139","9780750648134");
        allIsbns.put("0750648333","9780750648332");
        allIsbns.put("0750648341","9780750648349");
        allIsbns.put("0750648430","9780750648431");
        allIsbns.put("0750648449","9780750648448");
        allIsbns.put("0750648511","9780750648516");
        allIsbns.put("0750648791","9780750648790");
        allIsbns.put("075064883x","9780750648837");
        allIsbns.put("0750648856","9780750648851");
        allIsbns.put("0750648872","9780750648875");
        allIsbns.put("0750648880","9780750648882");
        allIsbns.put("0750649305","9780750649308");
        allIsbns.put("0750649321","9780750649322");
        allIsbns.put("075064947x","9780750649476");
        allIsbns.put("075064950x","9780750649506");
        allIsbns.put("0750650036","9780750650038");
        allIsbns.put("0750650133","9780750650137");
        allIsbns.put("0750650303","9780750650304");
        allIsbns.put("0750650451","9780750650458");
        allIsbns.put("075065046x","9780750650465");
        allIsbns.put("0750650494","9780750650496");
        allIsbns.put("0750650508","9780750650502");
        allIsbns.put("0750650540","9780750650540");
        allIsbns.put("0750650559","9780750650557");
        allIsbns.put("0750650583","9780750650588");
        allIsbns.put("0750650753","9780750650755");
        allIsbns.put("0750650761","9780750650762");
        allIsbns.put("0750650788","9780750650786");
        allIsbns.put("0750650885","9780750650885");
        allIsbns.put("0750650893","9780750650892");
        allIsbns.put("0750650923","9780750650922");
        allIsbns.put("0750651008","9780750651004");
        allIsbns.put("0750651113","9780750651110");
        allIsbns.put("0750651202","9780750651202");
        allIsbns.put("0750651253","9780750651257");
        allIsbns.put("0750651261","9780750651264");
        allIsbns.put("0750651318","9780750651318");
        allIsbns.put("0750651385","9780750651387");
        allIsbns.put("0750651415","9780750651417");
        allIsbns.put("0750651555","9780750651554");
        allIsbns.put("0750651687","9780750651684");
        allIsbns.put("0750652144","9780750652148");
        allIsbns.put("0750652314","9780750652315");
        allIsbns.put("0750652977","9780750652971");
        allIsbns.put("0750653000","9780750653008");
        allIsbns.put("0750653698","9780750653695");
        allIsbns.put("0750653965","9780750653961");
        allIsbns.put("0750653973","9780750653978");
        allIsbns.put("0750654228","9780750654227");
        allIsbns.put("0750654376","9780750654371");
        allIsbns.put("0750654716","9780750654715");
        allIsbns.put("0750654937","9780750654937");
        allIsbns.put("0750655208","9780750655200");
        allIsbns.put("0750655585","9780750655583");
        allIsbns.put("0750656085","9780750656085");
        allIsbns.put("0750656107","9780750656108");
        allIsbns.put("0750656115","9780750656115");
        allIsbns.put("0750656131","9780750656139");
        allIsbns.put("0750656360","9780750656368");
        allIsbns.put("0750657200","9780750657204");
        allIsbns.put("0750657219","9780750657211");
        allIsbns.put("0750657278","9780750657273");
        allIsbns.put("0750657286","9780750657280");
        allIsbns.put("075065757x","9780750657570");
        allIsbns.put("075065760x","9780750657600");
        allIsbns.put("0750657650","9780750657655");
        allIsbns.put("0750657669","9780750657662");
        allIsbns.put("0750657723","9780750657723");
        allIsbns.put("0750657731","9780750657730");
        allIsbns.put("0750657766","9780750657761");
        allIsbns.put("0750657847","9780750657846");
        allIsbns.put("0750657960","9780750657969");
        allIsbns.put("0750657979","9780750657976");
        allIsbns.put("0750657987","9780750657983");
        allIsbns.put("0750658029","9780750658027");
        allIsbns.put("0750658037","9780750658034");
        allIsbns.put("0750658053","9780750658058");
        allIsbns.put("0750658088","9780750658089");
        allIsbns.put("0750658436","9780750658430");
        allIsbns.put("0750658444","9780750658447");
        allIsbns.put("0750658460","9780750658461");
        allIsbns.put("075065855x","9780750658553");
        allIsbns.put("0750658665","9780750658669");
        allIsbns.put("0750659149","9780750659147");
        allIsbns.put("075065922x","9780750659222");
        allIsbns.put("0750659882","9780750659888");
        allIsbns.put("0750661003","9780750661003");
        allIsbns.put("0750661763","9780750661768");
        allIsbns.put("0750661798","9780750661799");
        allIsbns.put("0750662689","9780750662680");
        allIsbns.put("0750662697","9780750662697");
        allIsbns.put("0750662700","9780750662703");
        allIsbns.put("0750662743","9780750662741");
        allIsbns.put("0750662778","9780750662772");
        allIsbns.put("0750662786","9780750662789");
        allIsbns.put("0750663154","9780750663151");
        allIsbns.put("0750663855","9780750663854");
        allIsbns.put("0750663960","9780750663960");
        allIsbns.put("0750663979","9780750663977");
        allIsbns.put("0750663987","9780750663984");
        allIsbns.put("0750663995","9780750663991");
        allIsbns.put("0750664002","9780750664004");
        allIsbns.put("0750664339","9780750664332");
        allIsbns.put("0750664517","9780750664516");
        allIsbns.put("0750665297","9780750665292");
        allIsbns.put("0750667222","9780750667227");
        allIsbns.put("0750670088","9780750670081");
        allIsbns.put("0750670223","9780750670227");
        allIsbns.put("0750670592","9780750670593");
        allIsbns.put("0750670622","9780750670623");
        allIsbns.put("0750671238","9780750671231");
        allIsbns.put("0750671262","9780750671262");
        allIsbns.put("0750671351","9780750671354");
        allIsbns.put("075067136x","9780750671361");
        allIsbns.put("0750671505","9780750671507");
        allIsbns.put("0750671580","9780750671583");
        allIsbns.put("0750671750","9780750671750");
        allIsbns.put("0750671947","9780750671941");
        allIsbns.put("0750672080","9780750672085");
        allIsbns.put("0750672099","9780750672092");
        allIsbns.put("0750672188","9780750672184");
        allIsbns.put("0750672196","9780750672191");
        allIsbns.put("0750672439","9780750672436");
        allIsbns.put("0750672803","9780750672801");
        allIsbns.put("0750672811","9780750672818");
        allIsbns.put("0750672919","9780750672917");
        allIsbns.put("0750672943","9780750672948");
        allIsbns.put("0750673044","9780750673044");
        allIsbns.put("0750673176","9780750673174");
        allIsbns.put("0750673281","9780750673280");
        allIsbns.put("075067329x","9780750673297");
        allIsbns.put("0750673397","9780750673396");
        allIsbns.put("0750673516","9780750673518");
        allIsbns.put("0750674024","9780750674027");
        allIsbns.put("0750674032","9780750674034");
        allIsbns.put("0750674369","9780750674362");
        allIsbns.put("0750674393","9780750674393");
        allIsbns.put("075067444x","9780750674447");
        allIsbns.put("075067461x","9780750674614");
        allIsbns.put("0750674717","9780750674713");
        allIsbns.put("0750674954","9780750674959");
        allIsbns.put("0750674989","9780750674980");
        allIsbns.put("0750674997","9780750674997");
        allIsbns.put("0750675101","9780750675109");
        allIsbns.put("0750675217","9780750675215");
        allIsbns.put("0750675314","9780750675314");
        allIsbns.put("0750675349","9780750675345");
        allIsbns.put("0750675438","9780750675437");
        allIsbns.put("0750675462","9780750675468");
        allIsbns.put("0750675470","9780750675475");
        allIsbns.put("0750675551","9780750675550");
        allIsbns.put("0750675675","9780750675673");
        allIsbns.put("0750675683","9780750675680");
        allIsbns.put("0750675713","9780750675710");
        allIsbns.put("0750675799","9780750675796");
        allIsbns.put("0750676027","9780750676021");
        allIsbns.put("0750676035","9780750676038");
        allIsbns.put("0750676116","9780750676113");
        allIsbns.put("0750676183","9780750676182");
        allIsbns.put("0750676213","9780750676212");
        allIsbns.put("0750677015","9780750677011");
        allIsbns.put("075067704x","9780750677042");
        allIsbns.put("0750677074","9780750677073");
        allIsbns.put("0750677082","9780750677080");
        allIsbns.put("0750677112","9780750677110");
        allIsbns.put("0750677171","9780750677172");
        allIsbns.put("0750677198","9780750677196");
        allIsbns.put("0750677236","9780750677233");
        allIsbns.put("0750677252","9780750677257");
        allIsbns.put("0750677260","9780750677264");
        allIsbns.put("0750677295","9780750677295");
        allIsbns.put("0750677406","9780750677400");
        allIsbns.put("0750677430","9780750677431");
        allIsbns.put("075067749x","9780750677493");
        allIsbns.put("0750677562","9780750677561");
        allIsbns.put("0750677570","9780750677578");
        allIsbns.put("0750677783","9780750677783");
        allIsbns.put("0750678151","9780750678155");
        allIsbns.put("0750678380","9780750678384");
        allIsbns.put("0750678410","9780750678414");
        allIsbns.put("0750678445","9780750678445");
        allIsbns.put("0750678461","9780750678469");
        allIsbns.put("0750691689","9780750691680");
        allIsbns.put("0750693851","9780750693851");
        allIsbns.put("0750694831","9780750694834");
        allIsbns.put("0750694998","9780750694995");
        allIsbns.put("0750696400","9780750696401");
        allIsbns.put("0750697024","9780750697026");
        allIsbns.put("0750698667","9780750698665");
        allIsbns.put("0750698691","9780750698696");
        allIsbns.put("0750699167","9780750699167");
        allIsbns.put("0750699469","9780750699464");
        allIsbns.put("087201147x","9780872011472");
        allIsbns.put("0872011496","9780872011496");
        allIsbns.put("087201200x","9780872012004");
        allIsbns.put("0872014266","9780872014268");
        allIsbns.put("0872017818","9780872017818");
        allIsbns.put("0884150119","9780884150114");
        allIsbns.put("0884150259","9780884150251");
        allIsbns.put("0884150569","9780884150565");
        allIsbns.put("0884151018","9780884151012");
        allIsbns.put("0884151646","9780884151647");
        allIsbns.put("0884152200","9780884152200");
        allIsbns.put("0884152596","9780884152590");
        allIsbns.put("088415260x","9780884152606");
        allIsbns.put("0884152731","9780884152736");
        allIsbns.put("0884152898","9780884152897");
        allIsbns.put("0884153010","9780884153016");
        allIsbns.put("0884153150","9780884153153");
        allIsbns.put("0884153177","9780884153177");
        allIsbns.put("0884153207","9780884153207");
        allIsbns.put("0884153428","9780884153429");
        allIsbns.put("088415372x","9780884153726");
        allIsbns.put("0884154114","9780884154112");
        allIsbns.put("0884154815","9780884154815");
        allIsbns.put("0884155099","9780884155096");
        allIsbns.put("0884155250","9780884155256");
        allIsbns.put("0884156427","9780884156420");
        allIsbns.put("0884156435","9780884156437");
        allIsbns.put("0884156516","9780884156512");
        allIsbns.put("0884156575","9780884156574");
        allIsbns.put("0884156613","9780884156611");
        allIsbns.put("088415663x","9780884156635");
        allIsbns.put("0884157326","9780884157328");
        allIsbns.put("088415758x","9780884157588");
        allIsbns.put("0884157709","9780884157700");
        allIsbns.put("0884157903","9780884157908");
        allIsbns.put("0884158217","9780884158219");
        allIsbns.put("0884158225","9780884158226");
        allIsbns.put("0884158578","9780884158578");
        allIsbns.put("0884158586","9780884158585");
        allIsbns.put("0884158594","9780884158592");
        allIsbns.put("0884158608","9780884158608");
        allIsbns.put("0884158861","9780884158868");
        allIsbns.put("0884159205","9780884159209");
        allIsbns.put("0884159485","9780884159483");
        allIsbns.put("1589950046","9781589950047");
        allIsbns.put("1856173879","9781856173872");
        allIsbns.put("1856173895","9781856173896");
        allIsbns.put("1856173909","9781856173902");
        allIsbns.put("185617395x","9781856173957");
        allIsbns.put("1856174093","9781856174091");
        allIsbns.put("1856174166","9781856174169");
        allIsbns.put("1878707507","9781878707505");
        allIsbns.put("1878707523","9781878707529");
        allIsbns.put("1878707558","9781878707550");
        allIsbns.put("1878707566","9781878707567");
        allIsbns.put("1878707574","9781878707574");
        allIsbns.put("1878707981","9781878707987");
        allIsbns.put("187870799x","9781878707994");
        allIsbns.put("0080426999a","9780080426990");
        allIsbns.put("0080426999b","9780080426990");
        allIsbns.put("0080439500a","9780080439501");
        allIsbns.put("0444511407a","9780444511409");
        allIsbns.put("0444511407b","9780444511409");
        allIsbns.put("0444898751a","9780444898753");
        allIsbns.put("0444898751b","9780444898753");
        allIsbns.put("0750610778a","9780750610773");
        allIsbns.put("0750610778b","9780750610773");
        allIsbns.put("0750615478a","9780750615471");
        allIsbns.put("0750615478b","9780750615471");
        allIsbns.put("0750615478c","9780750615471");
        allIsbns.put("075063605xa","9780750636056");
        allIsbns.put("075063605xb","9780750636056");

        AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
        
        Iterator itr = allIsbns.keySet().iterator();
        while(itr.hasNext()) {
            String tktUrl = null;
            String isbn10 = (String) itr.next();
            String isbn13 = (String) allIsbns.get(isbn10);
            try {
                tktUrl = ticketer.getBookTicketedURL(isbn10, "923038", System.currentTimeMillis());
            } catch (BookException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(isbn10.length() > 10) {
                isbn13 = isbn13 + isbn10.substring(10, isbn10.length()); 
            }
            System.out.print("wget -nv -b -O " + isbn13 + ".pdf \""); 
            System.out.println(tktUrl + "\"");
        }
        
    }
}
