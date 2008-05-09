<%@ page language="java" %><%@ page import="java.util.*"%><%@ page import="org.ei.domain.navigators.*"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.data.georef.runtime.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%@ page import="org.ei.parser.base.*"%>
<%!
    String[] credentials = new String[]{"CPX", "UPO", "CRC", "SOL", "INS", "DSS", "ESN", "SCI", "EMS", "EEV", "OJP", "SPI", "NTI", "THS", "C84", "IBF", "UPA", "EUP", "CBN", "GEO", "PCH", "CHM", "ELT", "EPT", "GSP", "LHC", "EZY", "GAR", "ELE", "CHE", "MAT", "COM", "CIV", "SEC", "BPE", "ZBF", "CSY2004", "a", "czl", "frl", "he", "ng", "ocl", "prp", "ps_l", "ts", "tl", "czp", "cp", "ets", "frp", "ocp", "pp", "psp", "ps_p", "pol", "tp", "PAG", "GRF", "GRF", "GRF"};
%>
<%

try
{
  ControllerClient client = null;
  ResultNavigator nav = null;
  UserSession ussession = null;
  User user = null;
  String sessionId = null;
  int dbmask = 0;

  client = new ControllerClient(request, response);

  ussession = (UserSession) client.getUserSession();
  if(ussession != null)
  {
    user = ussession.getUser();
    sessionId = ussession.getID();
    if(user != null)
    {
      credentials = user.getCartridge();
    }
  }


  String searchId = request.getParameter("searchId");
/*  if(searchId == null)
  {
    searchId = "1474e45117d09b01c1M7ff814536192173";
  } */

  if(searchId != null && !searchId.equals("") && !searchId.equals("undefined"))
  {
    long start1 = System.currentTimeMillis();
    NavigatorCache navcache = new NavigatorCache(sessionId);
    nav = navcache.getFromCache(searchId);
    System.out.println("done ms: " + String.valueOf(System.currentTimeMillis() - start1));
  }

  GeoRefCoordinateMap coords = GeoRefCoordinateMap.getInstance();
  EiNavigator geo = nav.getNavigatorByName(EiNavigator.GEO);
  if(geo != null)
  {
    out.write("<js><![CDATA[");

    Iterator itrmods = (geo.getModifiers()).iterator();
    out.write("{");
    out.write("\"placemarks\":[ ");
    long start = System.currentTimeMillis();
    for(int mindex = 0;itrmods.hasNext();mindex++)
    {
      EiModifier modifier = (EiModifier) itrmods.next();
      if(modifier != null)
      {
        String geoterm = modifier.getLabel();
        String geovalue = modifier.getValue();
        int geocount = modifier.getCount();

        String coordniates = coords.lookupGeoRefTermCoordinates(geoterm);
        if(coordniates != null)
        {
          out.write("{");
          out.write("\"search\":\"/controller/servlet/Controller?CID=expertSearchCitationFormat&database=" + dbmask + "&RERUN="+searchId+"&geonav=" + geocount + "~" + geovalue  + "~" + geoterm + "&mapevent=search\",");
          out.write("\"count\":\"" + modifier.getCount() + "\",");
          out.write("\"name\":\"" + geoterm + "\",");
          out.write("\"description\":\"" + geoterm + " (" + modifier.getCount() + " records)\",");
          out.write(coordniates);
          out.write("}");
          if(itrmods.hasNext())
          {
            out.write(",");
          }
        }
      }
    }
    out.write(" ]");
    out.write("}");
    out.write("]]></js>");
  }
}
catch(Exception e)
{
  System.out.println("Exception !");
  e.printStackTrace();
}
finally
{
  out.close();
}
%>