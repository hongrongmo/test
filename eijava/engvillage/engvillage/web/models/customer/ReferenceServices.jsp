<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String
--%>
<%@ page language="java" %>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>

<%-- import statements of Java packages --%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>

<%-- import statements of ei packages. --%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%

	SessionID sessionIdObj = null;

	// Variable to hold the Personalization userid
	String pUserId = null;
	// Variable to hold the Personalizaton status
	boolean personalization = false;

	// variable to hold database name
	String database = null;

	//variable to hold personalization feature
	boolean  isPersonalizationPresent=true;
	String customizedLogo="";

	String refEmail = "";

	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

	ControllerClient client = new ControllerClient(request, response);

	UserSession ussession=(UserSession)client.getUserSession();

	//client.updateUserSession(ussession);
	sessionIdObj = ussession.getSessionID();

	pUserId = ussession.getProperty("P_USER_ID");
	if((pUserId != null) && (pUserId.trim().length() != 0)){
		personalization=true;
	}
  User user = ussession.getUser();
	ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);

  if(database == null)
  {
    database = clientCustomizer.getDefaultDB();
  }

	if(clientCustomizer.getRefEmail() != null &&
		clientCustomizer.getRefEmail().length()>0)
	{
		refEmail = clientCustomizer.getRefEmail();
	}

  /* set the mask to CPX, INS or both. IF neither, default to users default from BO */
  String[] cars = user.getCartridge();
  int userMask = (DatabaseConfig.getInstance()).getMask(cars);
  int refsvcsmask = 0;
  if((userMask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
    refsvcsmask += DatabaseConfig.CPX_MASK;
  }
  if((userMask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
    refsvcsmask += DatabaseConfig.INS_MASK;
  }
  if(refsvcsmask == 0) {
    try {
      refsvcsmask = Integer.parseInt(clientCustomizer.getDefaultDB());
    } catch(NumberFormatException e) {
      refsvcsmask = DatabaseConfig.CPX_MASK;
    }
  }
  log("refsvcsmask " + refsvcsmask);

	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		customizedLogo=clientCustomizer.getLogo();
	}

	client.setRemoteControl();

	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
%>
<PAGE>
<SESSION-ID><%=sessionIdObj.toString()%></SESSION-ID>
<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
<PERSONALIZATION-PRESENT><%=isPersonalizationPresent%></PERSONALIZATION-PRESENT>
<PERSONALIZATION><%=personalization%></PERSONALIZATION>
<REFEMAIL><%=refEmail%></REFEMAIL>
<HEADER/>
<%=	strGlobalLinksXML %>
<FOOTER/>
<DBMASK><%=database%></DBMASK>
<GURUS>
<%
  Iterator itr = authorLinks.keySet().iterator();
  while(itr.hasNext()) {
    String guru = (String) itr.next();
    String gurulink = (String) authorLinks.get(guru);
    if(guru != null) {
      out.write("<GURU NAME='" + guru + "'><![CDATA[");
      out.write(makeRefSvcsLink(refsvcsmask,gurulink));
      out.write("]]></GURU>");
    }
  }
%>
</GURUS>

<DISCIPLINES>
<%
  itr = disciplines.keySet().iterator();
  while(itr.hasNext()) {
    String discipline = (String) itr.next();
    if(discipline != null) {
      out.write("<DISCIPLINE NAME='" + discipline + "'>");
      Map gurus = (Map) disciplines.get(discipline);
      Iterator itrgurus = gurus.keySet().iterator();
      while(itrgurus.hasNext()) {
      String guru = (String) itrgurus.next();
        if(guru != null) {
          out.write("<GURU NAME='" + guru + "'>");

          Map searches = (Map) gurus.get(guru);
          Iterator itrkys = searches.keySet().iterator();
          out.write("<SEARCHES>");

          while(itrkys.hasNext()) {
            String searchky = (String) itrkys.next();
            String searchphrase = (String) searches.get(searchky);

            if((searchky != null) && (searchphrase != null)) {

              out.write("<SEARCH NAME='" + searchky + "'><![CDATA[");
              out.write(makeRefSvcsLink(refsvcsmask,searchphrase));
              out.write("]]></SEARCH>");
            }
          }
          out.write("</SEARCHES>");

          out.write("</GURU>");
        }
      }

      out.write("</DISCIPLINE>");
    }
  }

%>
</DISCIPLINES>

</PAGE>
<%!

    Map authorLinks = new HashMap();
    Map disciplines = new HashMap();

    public void jspInit()
    {
      try
      {
        authorLinks.put("Alan Halecky","((halecky, alan) WN AU)");
        authorLinks.put("Chi Hau Chen","((Chi Hau Chen ) WN AU)");
        authorLinks.put("Donald W. Merino, Jr.","((Merino, Donald W.) WN AU)");
        authorLinks.put("Earl E. Swartzlander, Jr.","((Swartzlander, Earl) WN AU)");
        authorLinks.put("Gregory A. Sedrick","((((Sedrick, Gregory) WN AU) OR ((Sedrick, g) WN AU)) OR ((sedrick, greg) WN AU))");
        authorLinks.put("Kanti Prasad","((prasad, kanti) WN AU)");
        authorLinks.put("Keith Sheppard","((Sheppard, Keith) WN AU)");
        authorLinks.put("Robert D. Borchelt","((((borchelt, r.) WN AU) OR ((borchelt, robert) WN AU)) OR ((borchelt, robert d.) WN AU))");
        authorLinks.put("Ronald A. Perez","((perez, ronald) WN AU)");
        authorLinks.put("Ryo Samuel Amano","((((Amano, Ryo Samuel ) WN AU) OR ((Amano, R) WN AU)) AND ((Amano, R. S.) WN AU))");

        Map ausearches = null;
        Map kysearches = null;

        kysearches = new HashMap();
        ausearches = new HashMap();
        kysearches.put("Aerodynamics","((aerodynamics) WN CV)");
        kysearches.put("gas/steam turbines","(((gas turbines) WN CV) OR ((steam turbines) WN CV))");
        kysearches.put("fluid mechanics/fluid dynamics","(((fluid mechanics) WN CV) OR ((fluid dynamics) WN CV))");
        kysearches.put("rocket/propulsion systems","(((rocket systems ) WN CV) OR ((propulsion systems ) WN CV))");
        kysearches.put("heat transfer/heat exchangers/engines","((((heat exchangers) WN CV) OR ((heat transfer) WN CV)) OR ((engines) WN CV))");
        ausearches.put("Ryo Samuel Amano",kysearches);

        kysearches = new HashMap();
        kysearches.put("control theory and applications","(((control theory ) WN CV) AND ((applications) WN CV))");
        kysearches.put("robust multivariable control","((robust multivariable control ) WN CV)");
        kysearches.put("nonlinear control","((nonlinear control ) WN CV)");
        kysearches.put("robotics","((robotics) WN CV)");
        kysearches.put("system modeling","(((system modeling) WN CV) AND ((mechanical engineering) WN CV))");
        kysearches.put("interaction analysis","((interaction analysis) WN CV)");
        kysearches.put("intelligent control","((intelligent control) WN CV)");
        ausearches.put("Ronald A. Perez",kysearches);
        disciplines.put("mechanical",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("computer integrated manufacturing","((computer integrated manufacturing) WN CV)");
        kysearches.put("automation and advanced manufacturing systems","(((automation ) WN CV) AND ((manufacturing systems) WN CV))");
        kysearches.put("artificial intelligence applications in robotics and manufacturing","((((artificial intelligence applications) WN CV) AND ((robotics ) WN CV)) OR ((manufacturing ) WN CV))");
        kysearches.put("automated diagnosis and error recovery","(((automation ) WN CV) OR ((error recovery ) WN CV))");
        kysearches.put("hybrid knowledge-based systems","(((hybrid) WN CV) AND (( knowledge-based systems) WN CV))");
        kysearches.put("programmable logic controller applications","(((programmable logic controller ) WN CV) AND ((applications ) WN CV))");
        kysearches.put("physical modeling of manufacturing systems","(((model) WN CV) AND ((manufacturing systems ) WN CV))");
        kysearches.put("engineering management issues related to manufacturing","(((engineering management ) WN CV) AND ((problems) WN CV))");
        kysearches.put("manufacturing processes","((manufacturing processes ) WN CV)");
        kysearches.put("systems integration","((systems integration ) WN CV)");
        kysearches.put("expert systems","((expert systems) WN CV)");
        ausearches.put("Robert D. Borchelt",kysearches);


        kysearches = new HashMap();
        kysearches.put("TQM (Total Quality Management)","((Total Quality Management) WN CV)");
        kysearches.put("ISO 9000, ISO 9000 certification","((ISO 9000 certification ) WN KY)");
        kysearches.put("work measurement","((work measurement ) WN KY)");
        kysearches.put("engineering economy","((engineering economics) WN CV)");
        kysearches.put("work design and improvement","((work design and improvement) WN KY)");
        kysearches.put("methods improvement","(((methods improvement) WN KY) AND ((engineering management) WN CV))");
        kysearches.put("small manufacturing","(\"small manufacturing\" WN KY)");
        kysearches.put("multimedia-based corporate training","(((multimedia ) WN KY) AND ((corporate training) WN KY))");
        kysearches.put("Baldridge Award preparation/judging","((Baldridge Award ) WN KY)");
        ausearches.put("Gregory A. Sedrick",kysearches);
        disciplines.put("manufacturing",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("TQM (Total Quality Management)","((Total Quality Management) WN CV)");
        kysearches.put("ISO 9000, ISO 9000 certification","((ISO 9000 certification ) WN KY)");
        kysearches.put("work measurement","((work measurement ) WN KY)");
        kysearches.put("engineering economy","((engineering economics) WN CV)");
        kysearches.put("work design and improvement","((work design and improvement) WN KY)");
        kysearches.put("methods improvement","(((methods improvement) WN KY) AND ((engineering management) WN CV))");
        kysearches.put("small manufacturing","(\"small manufacturing\" WN KY)");
        kysearches.put("multimedia-based corporate training","(((multimedia ) WN KY) AND ((corporate training) WN KY))");
        kysearches.put("Baldridge Award preparation/judging","((Baldridge Award ) WN KY)");
        ausearches.put("Gregory A. Sedrick",kysearches);

        kysearches = new HashMap();
        kysearches.put("production operations","((production operations) WN CV)");
        kysearches.put("process flow/optimization","(((process flow) WN CV) AND ((optimization ) WN CV))");
        kysearches.put("process simulation","((process simulation) WN CV)");
        kysearches.put("capital allocation","((resource allocation ) WN CV)");
        kysearches.put("cost models","((cost models) WN CV)");
        kysearches.put("concurrent engineering","((concurrent engineering ) WN CV)");
        kysearches.put("SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics","((((Statistical Quality Control ) WN CV) OR ((Statistical Process Control) WN CV)) OR ((applied statistics) WN CV))");
        kysearches.put("TQM philosophy","((Total Quality Management) WN CV)");
        kysearches.put("Benchmarking","((benchmarking, ) WN CV)");
        kysearches.put("DOE (Design of Experiments)","(((Design of Experiments) WN CV) OR ((DOE) WN CV))");
        kysearches.put("QFD (Quality Function Deployment)","(((Quality Function Deployment) WN CV) OR ((QFD ) WN CV))");
        kysearches.put("cost of quality","((cost of quality ) WN CV)");
        kysearches.put("economic/non-economic decision making","((((economic) WN CV) OR ((non-economic ) WN CV)) AND (( decision making) WN CV))");
        kysearches.put("optimization","(((optimization) WN CV) AND ((engineering management) WN CV))");
        ausearches.put("Donald W. Merino, Jr.",kysearches);
        disciplines.put("industrial",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("VLSI chip design incorporating microprocessors, controls, communications, intelligent transportation systems, GPS, computer networks, LANs, including wireless LANs","((VLSI chip) and (design)) and ((microprocessors) or (controls) or (communications) or (intelligent transportation systems) or (GPS) or (computer networks) or (LAN) or (wireless LAN)) ");
        kysearches.put("VLSI fabrication incorporating microelectronics processing such as lithography oxidation, diffusion, implantation, and metallization for silicon","((VLSI fabrication) and ((microelectronics processing) or(lithography oxidation) or (diffusion) or (implantation) or (silicon metalization)))");
        kysearches.put("GaAs technology including packing and reliability analysis","((GaAs technology) WN KY) AND ((( reliability analysis ) WN KY) OR ((packing) WN KY))");
        kysearches.put("digital and analog design employing VHDL incorporating test and simulation in entirety","(((digital design) or (analog design)) and (VHDL)) and ((test) or (simulation in entirety))");
        kysearches.put("exhaustive testing and simulation with statistically fast and event driven simulators","((exhaustive testing) or (simulation)) and ((statistically fast) or (event driven simulation))");
        ausearches.put("Kanti Prasad",kysearches);
        disciplines.put("electrical",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("Corrosion","((Corrosion ) WN CV)");
        kysearches.put("electro-deposition","((electrodeposition) WN CV)");
        kysearches.put("failure of materials","(((failure of materials ) WN CV) OR ((materials failure) WN CV))");
        kysearches.put("materials characterization","((materials characterization ) WN CV)");
        kysearches.put("scanning and transmission electron microscopy","(((scanning) WN CV) AND ((transmission electron microscopy) WN CV))");
        kysearches.put("materials selection","((materials selection) WN CV)");
        kysearches.put("materials processing","((materials processing) WN CV)");
        ausearches.put("Keith Sheppard",kysearches);
        disciplines.put("materials",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("computer engineering","((computer engineering ) WN CV)");
        kysearches.put("computer arithmetic","((computer arithmetic ) WN CV)");
        kysearches.put("application specific processing","((application specific processing ) WN CV)");
        kysearches.put("interaction between computer architecture and VLSI technology","(((VLSI ) WN CV) AND ((computer architecture) WN CV))");
        kysearches.put("signal processing","((signal processing ) WN CV)");
        ausearches.put("Earl E. Swartzlander, Jr.",kysearches);
        disciplines.put("computer",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("pattern recognition","((pattern recognition) WN CV)");
        kysearches.put("signal/image processing and neural networks","((((signal processing) WN CV) OR ((image processing) WN CV)) AND ((neural networks ) WN CV))");
        kysearches.put("applications to ultrasonic/nondestructive evaluation (NDE)","(applications) AND ((ultrasonic evaluation) OR (nondestructive evaluation) OR (NDE))");
        kysearches.put("sonar","((sonar) WN CV)");
        kysearches.put("radar","((radar) WN CV)");
        kysearches.put("seismic problems","((seismic problems ) WN CV)");
        kysearches.put("machine vision","((machine vision ) WN CV)");
        kysearches.put("artificial intelligence","(((artificial intelligence) WN CV) AND ((signal processing) WN CV))");
        kysearches.put("time series analysis","((time series analysis ) WN CV)");
        kysearches.put("wavelet analysis","((wavelet analysis) WN CV)");
        ausearches.put("Chi Hau Chen",kysearches);
        disciplines.put("signal processing",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("Compounds","(((Compounds ) WN CV) AND ((chemical) WN CV))");
        kysearches.put("Compositions","(((Compositions ) WN CV) AND ((chemical) WN CV))");
        kysearches.put("Ion Exchange - Applications","(((Ion Exchange ) WN CV) AND ((applications ) WN CV))");
        kysearches.put("Gold Plating - Ion Exchange","(((Ion Exchange ) WN CV) AND ((Gold Plating ) WN CV))");
        kysearches.put("Lead and Alloys - Removal","(((Lead and Alloys ) WN CV) AND ((Removal ) WN CV))");
        kysearches.put("Solutions","(((Solutions ) WN CV) AND ((chemical) WN CV))");
        kysearches.put("Chemical Reactions - Precipitation","(((Chemical Reactions ) WN CV) AND ((Precipitation) WN CV))");
        ausearches.put("Alan Halecky",kysearches);
        disciplines.put("chemical",ausearches);

        ausearches = new HashMap();
        kysearches = new HashMap();
        kysearches.put("production operations","((production operations) WN CV)");
        kysearches.put("process flow/optimization","(((process flow) WN CV) AND ((optimization ) WN CV))");
        kysearches.put("process simulation","((process simulation) WN CV)");
        kysearches.put("capital allocation","((resource allocation ) WN CV)");
        kysearches.put("cost models","((cost models) WN CV)");
        kysearches.put("concurrent engineering","((concurrent engineering ) WN CV)");
        kysearches.put("SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics","((((Statistical Quality Control ) WN CV) OR ((Statistical Process Control) WN CV)) OR ((applied statistics) WN CV))");
        kysearches.put("TQM philosophy","((Total Quality Management) WN CV)");
        kysearches.put("Benchmarking","((benchmarking) WN CV)");
        kysearches.put("DOE (Design of Experiments)","(((Design of Experiments) WN CV) OR ((DOE) WN CV))");
        kysearches.put("QFD (Quality Function Deployment)","(((Quality Function Deployment) WN CV) OR ((QFD ) WN CV))");
        kysearches.put("cost of quality","((cost of quality ) WN CV)");
        kysearches.put("economic/non-economic decision making","((((economic) WN CV) OR ((non-economic ) WN CV)) AND (( decision making) WN CV))");
        kysearches.put("optimization","(((optimization) WN CV) AND ((engineering management) WN CV))");
        ausearches.put("Donald W. Merino, Jr.",kysearches);
        disciplines.put("management",ausearches);

      } catch(Exception e) {
        e.printStackTrace();
      }
    }

    private String makeRefSvcsLink(int database, String searchphrase)
    {
      StringBuffer buf = new StringBuffer();

      buf.append("/controller/servlet/Controller?CID=expertSearchCitationFormat");
      buf.append("&yearselect=yearrange");
      buf.append("&database=").append(database);
      buf.append("&searchWord1=").append(URLEncoder.encode(searchphrase));

      return buf.toString();
    }
%>