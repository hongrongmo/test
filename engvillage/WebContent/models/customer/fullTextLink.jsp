<%@ page import=" java.util.*"%>
<%@ page session="false"%>

<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.fulldoc.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%
    String sessionId;
    String searchId;
    int currentPage = 0;
    int pageNo = 0;
    List docIDList;

    OHUBID[] ids = null;

    String issn = null;
    String issue = null;
    String firstPage = null;
    String volume = null;

    /*  prepare script to flip image    */
    String preScript = "function imageFlip(){imageFlip1(); setTimeout(\"imageFlip1();\",2000);} function imageFlip1(){";
    StringBuffer script = new StringBuffer();
    StringBuffer flipScript = new StringBuffer();
    Hashtable indexLookup = new Hashtable();

    long begin = System.currentTimeMillis();

    long end = -1L;

    try {

        ControllerClient client = new ControllerClient(request, response);

        UserSession ussession = (UserSession) client.getUserSession();
        sessionId = ussession.getSessionid();

        searchId = request.getParameter("searchId");
        currentPage = Integer.parseInt(request.getParameter("currentPage"));

        /*  get page number     */
        if (currentPage % 25 == 0) {
            pageNo = currentPage / 25;
        } else {
            pageNo = currentPage / 25 + 1;
        }

        /*  get page from page cache    */
        PageCache pCache = new PageCache(sessionId);
        docIDList = pCache.getPage(pageNo, searchId, 3);

        /*  get EIDoc list from page    */
        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
        List bList = builder.buildPage(docIDList, Citation.CITATION_FORMAT);

        /*  this list keeps IssueVolumeIDs sent out in the request  */
        List sendingIDs = new ArrayList();

        /*  this list keeps original image indexes on page */
        List sendingIndex = new ArrayList();

        IssueVolumeID is = null;
        AlgoLinker algo = new AlgoLinker();

        for (int i = 0; i < bList.size(); i++) {
            EIDoc eiDoc = (EIDoc) bList.get(i);
            is = eiDoc.getIssueVolumeID();

            issn = is.getISSN();
            issue = is.getFirstIssue();
            firstPage = is.getFirstPage();
            volume = is.getFirstVolume();

            /* build full text link for each IssueVolumeID
               if buildLink() is successful, flip the image
               otherwise collect the IDs for sending out OHUB request
             */
            if ((issn != null) && (issn.length() > 0) && (firstPage != null) && (firstPage.length() > 0) && (volume != null) && (volume.length() > 0)
                && (issue != null) && (issue.length() > 0)) {
                if (algo.buildLink(is) != null) {
                    script.append("if(document.fullDocImageCit").append(i).append("){");
                    script.append("document.fullDocImageCit").append(i).append(".src = \"/ohubservice/images/avcit.gif\";}");
                    indexLookup.put(Integer.toString(i), "yes");
                } else {
                    /* add IssueVolumeIDs to the list to keep the original order */
                    sendingIDs.add(is);
                    /* add original image index to this list    */
                    sendingIndex.add(Integer.toString(i));
                }
            }
        }

        if (!sendingIDs.isEmpty()) {

            /* get sending IssueVolumeID[]  */
            IssueVolumeID[] iss = null;
            iss = (IssueVolumeID[]) sendingIDs.toArray(new IssueVolumeID[] {});
            Hashtable indexMapping = new Hashtable();

            /* determine ids size   */
            int y = 0;
            for (int x = 0; x < iss.length; x++) {
                issue = iss[x].getFirstIssue();
                if (issue != null) {
                    y++;
                }
            }
            ids = new OHUBID[iss.length + y];

            /* index of ids   */
            int j = 0;

            /* populate OHUBID[]
               sending 2 requests for each OHUBID
               1st without issue, 2nd with issue
             */
            for (int i = 0; i < iss.length; i++) {
                issn = iss[i].getISSN();
                issue = iss[i].getFirstIssue();
                firstPage = iss[i].getFirstPage();
                volume = iss[i].getFirstVolume();

                /*  nois: IVID without issue
                    iss[i]: IVID with issue
                 */
                IssueVolumeID nois = new IssueVolumeID();
                nois.setISSN(issn);
                nois.setFirstPage(firstPage);
                nois.setFirstVolume(volume);

                ids[j] = nois;
                indexMapping.put(Integer.toString(j), Integer.toString(i));

                if (issue != null) {
                    ids[j + 1] = iss[i];
                    /* mapping index of resquests and index of sending out IDs */
                    indexMapping.put(Integer.toString(j + 1), Integer.toString(i));
                    j += 2;
                } else {
                    j++;
                }
            }

            OHUBRequest orequest = null;
            OHUBResponses theResponses = null;
            orequest = new OHUBRequest(OHUBService.salt, OHUBService.saltVersion, OHUBService.partnerID, ids);
            OHUBClient oClient = new OHUBClient(OHUBService.ohuburl);
            theResponses = oClient.getOHUBResponses(orequest);

            /* flip the images according to the responses   */
            for (int i = 0; i < ids.length; i++) {
                if (theResponses.responseAt(i).itemCount() == 1) {

                    /* get index of sending out IDs
                       according to the index of responses
                     */
                    String positionInSendingIDs = (String) indexMapping.get(Integer.toString(i));

                    /* get the original image index according
                       to the index of sending out IDs
                     */
                    String imageIndex = (String) sendingIndex.get(Integer.parseInt(positionInSendingIDs));

                    //if(!indexLookup.containsKey(imageIndex))
                    //{
                    script.append("if(document.fullDocImageCit").append(imageIndex).append("){");
                    script.append("document.fullDocImageCit").append(imageIndex).append(".src = \"/ohubservice/images/avcit.gif\";}");
                    indexLookup.put(imageIndex, "yes");
                    //}
                }
            }
        }

    }

    catch (Exception e) {
        //e.printStackTrace();
    }

    finally {

        /* complete flip script   */
        end = System.currentTimeMillis();
        int diff = (int) (end - begin) / 1000;
        flipScript.append(preScript).append(script).append("}");

        out.print("<js><![CDATA[");
        out.print(flipScript.toString());
        out.print(";window.status='");
        out.print(Integer.toString(indexLookup.size()));
        out.print(" Full text documents retrieved in ");
        out.print(Integer.toString(diff));
        out.print(" seconds.'");
        out.print("]]></js>");
        out.flush();
    }
%>