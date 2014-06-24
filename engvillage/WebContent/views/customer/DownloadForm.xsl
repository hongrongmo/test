<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="xsl html"
>

<!-- This xsl displays the various format options for download.It sends the
     parameters as a URL to a jsp.
-->

<xsl:output method="html"/>
<xsl:include href="currentYear.xsl" />
<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>
    <xsl:variable name="DISPLAY-FORMAT">
        <xsl:value-of select="DISPLAY-FORMAT"/>
    </xsl:variable>
    <xsl:variable name="ERROR-PAGE">
        <xsl:value-of select="ERROR-PAGE"/>
    </xsl:variable>

    <xsl:variable name="SAVED-RECORDS">
        <xsl:choose>
            <xsl:when test="string(SAVED-RECORDS)">
                <xsl:value-of select="SAVED-RECORDS"/>
            </xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <html>
    <head>
        <title>Download Records</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

        <xsl:text disable-output-escaping="yes">

        <![CDATA[
                <xsl:comment>
            <script language="javascript">
                function validate(sessionid, displaytype)
                {
                    var milli = (new Date()).getTime();
                    var selectedVal= "";
                    var baseAddress = document.forms.feedback.base_address.value;
                    var len=document.feedback.downloadformat.length;

                    for(var i=0;i<len;i++)
                    {
                        if(document.feedback.downloadformat[i].checked) {
                            selectedVal=document.feedback.downloadformat[i].value;
                            break;
                        }
                    }

                    if(selectedVal == "") {
                        alert("You must choose a download format.");
                        return (false);
                    }
                    var strCID = "&CID=download";


                    // ignore displaytype for RIS format
                    if(selectedVal.indexOf("ris") < 0 && selectedVal.indexOf("bib") < 0 && selectedVal.indexOf("ref") < 0) {
                        strCID = strCID + displaytype;
                    }

                    // CIDs will look like the following (minus the '['s)
                    // for ascii
                    // download[citation][SelectedSet][ascii]
                    // download[fullDoc][SelectedSet][ascii]
                    // download[abstract][SelectedSet][ascii]
                    // for RIS
                    // download[SelectedRecords][risinspec]
                    // download[SelectedRecords][riscpx]


                    if(typeof(document.feedback.docidlist) != 'undefined') {
                        if(selectedVal == "refworks") {
                            url = "http://"+baseAddress+"/controller/servlet/Controller?EISESSION="+sessionid+
                            strCID+"SelectedRecordsrefworks"+
                            "&format="+selectedVal+
                            "&docidlist="+document.feedback.docidlist.value+
                            "&handlelist="+document.feedback.handlelist.value
                            "&timestamp="+milli;

                            var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software&url="+escape(url);
                            window.open(refworksURL, "RefWorksMain", "width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
                            return false;
                        }

                        url = "/controller/servlet/Controller?EISESSION="+sessionid+
                        strCID+"SelectedRecords"+selectedVal+
                        "&format="+selectedVal+
                        "&displayformat="+displaytype+
                        "&timestamp="+milli+
                        "&docidlist="+document.feedback.docidlist.value+
                        "&handlelist="+document.feedback.handlelist.value;


                    }
                    else {

                        if(document.feedback.saved_records.value != "true") {
                            if(selectedVal == "refworks") {

                                url = "http://"+baseAddress+"/controller/servlet/Controller?EISESSION="+sessionid+
                                strCID+"SelectedSetrefworks"+
                                "&format="+selectedVal+
                                "&timestamp="+milli;

                                var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software&url="+escape(url);
                                window.open(refworksURL, "RefWorksMain", "width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");

                                //window.open(refworksURL,'RefWorksMain',300,500);
                                return false;
                                }

                            url = "/controller/servlet/Controller?EISESSION="+sessionid+
                            strCID+"SelectedSet"+selectedVal+
                            "&format="+selectedVal+
                            "&displayformat="+displaytype+
                            "&timestamp="+milli;
                        } else {

                            if(selectedVal == "refworks") {
                                url = "http://"+baseAddress+"/controller/servlet/Controller?EISESSION="+sessionid+
                                strCID+"SavedRecordsOfFolderrefworks"+
                                "&format="+selectedVal+
                                "&folderid="+document.feedback.folder_id.value+
                                "&timestamp="+milli;

                                var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software&url="+escape(url);
                                window.open(refworksURL, "RefWorksMain", "width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
                                return false;
                              }
                            url = "/controller/servlet/Controller?EISESSION="+sessionid+
                            strCID+"SavedRecordsOfFolder"+selectedVal+
                            "&format="+selectedVal+
                            "&displayformat="+displaytype+
                            "&folderid="+document.feedback.folder_id.value+
                            "&timestamp="+milli;
                           }
                    }

                    document.feedback.action = url;
                    //window.open(url);
                    //window.close();
                    // method is POST
                    // let if fall through and use same window!
                    return (true);

                }
            </script>
            </xsl:comment>
        ]]>
        </xsl:text>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_txt_v01.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0"  >
	<!-- HEADER -->
	<div id="header">
		<div id="logoEV">
			<a href="/controller/servlet/Controller?CID=home"><img src="/static/images/EV-logo.gif"/></a>
		</div>
	
		<div id="headerLink">
			<div class="clearfix" id="suites">&#160;</div>
		</div>
	
		<div class="navigation txtLarger clearfix">&#160;</div>
	</div>


    <xsl:choose>
        <xsl:when test="$ERROR-PAGE='true'">
            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr><td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
            <td valign="top" colspan="2"><a class="EvHeaderText">Download Records</a></td>
            </tr>
            <tr><td valign="top" height="2" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
            <td valign="top">
            <A CLASS="MedBlackText">
            You did not select any records to download. Please select records from the search results and try again.
            </A>
            </td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
            </tr>
            </table>
        </xsl:when>

        <xsl:otherwise>
            <!-- Start of the lower area below the navigation bar -->
            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
                <tr>
                    <td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0"/></td>
                </tr>
                <tr>
                    <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
                    <td valign="top" colspan="2"><a class="EvHeaderText">Download Records</a></td>
                </tr>
                <tr>
                    <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
                    <td valign="top"><A CLASS="MedBlackText">To download records, please select a format below.</A></td>
                    <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
                </tr>
                <tr>
                    <td valign="top" height="20" colspan="3"><img src="/static/images/s.gif" border="0" height="20"/></td>
                </tr>
                <tr>
                    <td valign="top" colspan="3">

                    <!-- table for outer lines for feedback form -->
                    <table border="0" width="65%" cellspacing="0" cellpadding="0" align="center">
                    <tr>
                        <td valign="top" width="100%" height="2" bgcolor="#3173B5" colspan="4">
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" width="2"/></td>
                        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                        <td valign="top">

                        <form name="feedback" method="post" onSubmit="return validate('$SESSIONID','{$DISPLAY-FORMAT}');">

                            <xsl:if test="string(HANDLES)">
                                <input type="hidden" name="handlelist" value="{HANDLES}"/>
                            </xsl:if>
                            <xsl:if test="count(DOCID-LIST/DOC) = 0">
                                <input type="hidden" name="docidlist" value="{DOCID-LIST}"/>
                            </xsl:if>

                            <input type="hidden" name="saved_records" value="{SAVED-RECORDS}"/>
                            <input type="hidden" name="folder_id" value="{FOLDER-ID}"/>
                            <input type="hidden" name="base_address" value="{BASE-ADDRESS}"/>


                            <table border="0" width="300" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td valign="top" height="10" colspan="2"></td>
                                </tr>

                            <!--    <xsl:choose>
                                    <xsl:when test="count(DB-LIST/DB-NAME) = 0">
                                        <xsl:apply-templates select="./DATABASE"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:apply-templates select="./DB-LIST/DB-NAME"/>
                                    </xsl:otherwise>
                                </xsl:choose> -->

                                <xsl:call-template name="radio_ris"/>
                                <xsl:call-template name="radio_bibtex"/>
                                <xsl:call-template name="radio_refworks"/>
                                <xsl:call-template name="radio_ascii"/>

                                <tr>
                                    <td valign="top" colspan="2"><A CLASS="MedBlackText"><input type="submit" name="submit" value="Download"/><br/><br/></A></td>
                                </tr>
                            </table>


                        </form>
                        </td>
                        <td valign="top" width="2" bgcolor="#3173B5"></td>
                    </tr>
                    <tr>
                        <td valign="top" width="100%" cellspacing="0" cellpadding="0" height="2" bgcolor="#3173B5" colspan="4">
                        </td>
                    </tr>
                    </table>
                <!-- end of table for outer lines for feedback form -->
                </td>
                </tr>
                <!-- end of the lower area below the navigation bar -->
            </table>
        </xsl:otherwise>
    </xsl:choose>

    <br/>

    <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
    <td>
    <center>
    <A CLASS="SmBlackText">&#169; <!-- INCLUDE YEAR  -->
		<xsl:call-template name="YEAR"/> Elsevier Inc. All rights reserved.</A>
    </center>
    </td>
    </tr>
    </table>

    </body>
    </html>
</xsl:template>

    <!-- **************************************************************************** -->
    <!-- <xsl:template match="DATABASE|DB-LIST/DB-NAME">
        <xsl:call-template name="ShowRadio">
            <xsl:with-param name="db"><xsl:value-of select="."/></xsl:with-param>
        </xsl:call-template>
    </xsl:template> -->
    <!-- **************************************************************************** -->
    <!-- <xsl:template name="ShowRadio">
        <xsl:param name="db"/>
        <xsl:choose>
            <xsl:when test="$db='cpx'">
                <xsl:call-template name="radio_cpx"/>
            </xsl:when>
            <xsl:when test="$db='Compendex'">
                <xsl:call-template name="radio_cpx"/>
            </xsl:when>
            <xsl:when test="$db='INSPEC'">
                <xsl:call-template name="radio_inspec"/>
            </xsl:when>
            <xsl:when test="$db='inspec'">
                <xsl:call-template name="radio_inspec"/>
            </xsl:when>
            <xsl:when test="$db='COM'">
                <xsl:call-template name="radio_cpx"/>
                <xsl:call-template name="radio_inspec"/>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> -->

    <xsl:template name="radio_ris">
        <tr>
            <td valign="top"><input id="rdRis" type="radio" name="downloadformat" value="ris"/></td><td valign="to"><A CLASS="MedBlackText"><label for="rdRis">RIS, EndNote, ProCite, Reference Manager</label></A></td>
        </tr>
        <tr>
            <td valign="top" height="3" colspan="2"></td>
        </tr>
    </xsl:template>
    <xsl:template name="radio_refworks">
        <tr>
            <td valign="top"><input id="rdRef" type="radio" name="downloadformat" value="refworks"/></td><td valign="top"><A CLASS="MedBlackText"><label for="rdRef">RefWorks direct import</label></A></td>
        </tr>
        <tr>
            <td valign="top" height="3" colspan="2"></td>
        </tr>
    </xsl:template>

    <xsl:template name="radio_ascii">
        <tr>
            <td valign="top"><input id="rdAsc" type="radio" name="downloadformat" value="ascii"/></td><td valign="top"><A CLASS="MedBlackText"><label for="rdAsc">Plain text format (ASCII)</label></A></td>
        </tr>
        <tr>
            <td valign="top" height="10" colspan="2"></td>
        </tr>
    </xsl:template>

    <xsl:template name="radio_bibtex">
        <tr>
            <td valign="top"><input id="rdBib" type="radio" name="downloadformat" value="bib"/></td><td valign="top"><A CLASS="MedBlackText"><label for="rdBib">BibTex format</label></A></td>
        </tr>
        <tr>
            <td valign="top" height="3" colspan="2"></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>

