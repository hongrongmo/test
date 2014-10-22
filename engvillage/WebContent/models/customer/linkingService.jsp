<%@ page import=" java.util.*"%>
<%@ page session="false" %>

<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.fulldoc.*"%>
<%@ page import="org.ei.logging.*"%>
<%@ page import="org.ei.util.*;"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%

    DocID docID = null;
    String did = null;
    String issn = null;
    List docIDList = new ArrayList();

    ControllerClient client = new ControllerClient(request, response);
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    UserSession ussession = (UserSession)client.getUserSession();

    did = request.getParameter("docID");
    docID = new DocID(did,
                      databaseConfig.getDatabase(did.substring(0,3)));

    docIDList.add(docID);

    /*  get EIDoc list from page    */
    MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
    List bList = builder.buildPage(docIDList, Citation.CITATION_FORMAT);

    EIDoc eiDoc = (EIDoc)bList.get(0);
    issn = eiDoc.getISSN2();

    LinkInfo linkInfo = eiDoc.buildFT();

    if(linkInfo != null)
    {

        /*  log functionality   */
        if(issn != null)
        {
            client.log("FULLTEXT", issn);
        }
        else
        {
     	    client.log("FULLTEXT", "Y");
        }
        
        client.log("DOC_ID", docID.getDocID());
        client.log("DB_NAME",Integer.toString(docID.getDatabase().getMask()));
        client.setRedirectURL(linkInfo.url);
        client.setRemoteControl();

    }

%>

<NODOC/>

