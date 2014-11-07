<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:build="java:org.ei.system.Build"
    xmlns:srt="java:org.ei.domain.Sort"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:book="java:org.ei.books.BookDocument"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:custoptions="java:org.ei.fulldoc.FullTextOptions"
    exclude-result-prefixes="java html xsl DD srt bit xslcid custoptions book"
>
  <xsl:include href="Header.xsl" />
  <xsl:include href="GlobalLinks.xsl" />
  <xsl:include href="NavigationBar.xsl" />
  <xsl:include href="template.SEARCH_RESULTS.xsl" />
  <xsl:include href="common/CitationResults.xsl" />
  <xsl:include href="DeDupControl.xsl" />
  <xsl:include href="LocalHolding.xsl" />
  <xsl:include href="Footer.xsl" />

  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

<xsl:param name="CUST-ID">0</xsl:param>


<xsl:variable name="FULLTEXT"><xsl:value-of select="//FULLTEXT" /></xsl:variable>
<xsl:variable name="LOCALHOLDINGS-CITATION"><xsl:value-of select="//LOCALHOLDINGS-CITATION"/></xsl:variable>
<xsl:variable name="BOOKS_OPEN_WINDOW_PARAMS">height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no</xsl:variable>

<xsl:variable name="CHINA"><xsl:value-of select="build:china()"/></xsl:variable>
<xsl:variable name="DEDUP"><xsl:value-of select="/PAGE/DEDUP"/></xsl:variable>
<xsl:variable name="SEARCH-ID"><xsl:value-of select="/PAGE/SEARCH-ID"/></xsl:variable>
<xsl:variable name="SESSION-ID"><xsl:value-of select="/PAGE/SESSION-ID" /></xsl:variable>
<xsl:variable name="CURRENT-PAGE"><xsl:value-of select="/PAGE/CURR-PAGE-ID"/></xsl:variable>
<xsl:variable name="SELECTED-DB"><xsl:value-of select="/PAGE/DBMASK"/></xsl:variable>
<xsl:variable name="COMPMASK"><xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/></xsl:variable>

  <xsl:template match="PAGE">

    <xsl:variable name="COUNT"><xsl:value-of select="//CURR-PAGE-ID"/></xsl:variable>
    <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="//RESULTS-COUNT"/></xsl:variable>
    <xsl:variable name="RESULTS-PER-PAGE"><xsl:value-of select="//RESULTS-PER-PAGE"/></xsl:variable>
    <xsl:variable name="DATABASE-DISPLAYNAME"><xsl:value-of select="DD:getDisplayName(DBMASK)"/></xsl:variable>
    <xsl:variable name="DATABASE"><xsl:value-of select="DBMASK"/></xsl:variable>
    <xsl:variable name="DATABASE-ID"><xsl:value-of select="//SESSION-DATA/DATABASE/ID"/></xsl:variable>
    <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="SESSION-DATA/SEARCH-TYPE"/></xsl:variable>

    <html>
      <head>
        <META http-equiv="Expires" content="0"/>
        <META http-equiv="Pragma" content="no-cache"/>
        <META http-equiv="Cache-Control" content="no-cache"/>
        <title>Engineering Village  - <xsl:value-of select="$DATABASE-DISPLAYNAME"/><xsl:text> </xsl:text><xsl:value-of select="$SEARCH-TYPE"/> Search Results</title>

        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/SearchResults_V7.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/URLEncode.js"/>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>

         <SCRIPT LANGUAGE="Javascript" SRC="/static/js/jquery-1.4.2.min.js"/>

        <xsl:if test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus') or ($SEARCH-TYPE='Combined')">
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/QuickResults.js"/>
        </xsl:if>
        <xsl:if test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Easy')">
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/ExpertResults.js"/>
        </xsl:if>

        <xsl:if test="DEDUPABLE='true'">
          <xsl:call-template name="DEDUP-SCRIPT" />
        </xsl:if>

      <xsl:if test="($COMPMASK='8192' or $COMPMASK='2097152' or $COMPMASK='2105344') and (/PAGE/NAVIGATORS/NAVIGATOR[@NAME='geonav']) and ($CHINA='false')">
      <script type="text/javascript">
          var ev_searchID = "<xsl:value-of select="$SEARCH-ID"/>";
          var ev_dbmask = "<xsl:value-of select="$SELECTED-DB"/>";
      //<![CDATA[
          function createCookie(name,value,days) {
            if (days != 0) {
              var date = new Date();
              date.setTime(date.getTime()+(days*24*60*60*1000));
              var expires = "; expires="+date.toGMTString();
            }
            else var expires = "";
            document.cookie = name+"="+value+expires+"; path=/controller/servlet/";
          }

          function readCookie(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for(var i=0;i < ca.length;i++) {
              var c = ca[i];
              while (c.charAt(0)==' ') c = c.substring(1,c.length);
              if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
            }
            return null;
          }

          var populated = false;
          var EV_MAXZOOM = 5;
          var EV_MAXMARKERCOUNT = 30;
          var EV_MAPSTATE = 'mapstate';
          var EV_UNAVAILABLE = 'unavailable';
          var EV_OPEN = 'open';
          var EV_CLOSED = 'closed';
          var EV_SHOW_MAP_SRC = "/static/images/show_map.gif"
          var EV_HIDE_MAP_SRC = "/static/images/hide_map.gif";
          var bounds;
          var map;
          var messages = {"show":"Show Geographic Map", "hide":"Hide Geographic Map"};
          var apikeys = { "www.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", "cert.engineeringvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhTq1aVOHKz326Ly74ktyh0JEC50cxRcwqEG9_IZn1rf-kXlYHD0OAjeKw", "www.engineeringvillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQycePdRUCs6MMAMRXc5pXGU3hn3hRfr_zEZfLKIyUShHi94g2KWSfGEw","www.engineeringvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSAdZp0PxlTEhTFv3KMS9ULbNb5zBRqzELdPImli2KTwR6NaFokcBv4bA","www.engineeringvillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSQVDkHczPvGQBBH-1zyyQlxvW27BTYxtGf1JR2Az5uJMv5BiYUcHc0fg","www.chemvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRc2oirmkLapUOdAKcS-7VrLe4NmRRygHzNcODvNaPwfo8u7kjzm8R3Pg","www.papervillage2.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRoQ9NpbmNyNRg_ZVHqDXsW5KVm9BQAIDpSj4LWpPYl79Ju3n8FsZYtiA","www.paperchem.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhSwBt7CUughPYSQRGOq0g9Cnw2rgRS09l1TiHYrU0IFHRsrrd0Ioscj6g","www.papervillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR4KYQ73p3WzPF7syNfyrFgFZtwaxR5sJ7ySrsRekJV7hIExrrtRqD08g","www.papervillage2.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhS3XaAm7oOu6A7GrMVbYRe2u9FCpRSfk-An4_9sm-2YoETe1SnzwtWffg","www.apiencompass.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhRHcUpwnapUojBcsWU6PxPlD9oAeBR6FefYn5tiecY3qDiH2LruH550xw","www.eiencompass.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhR1px2Xrv_o7VDsm6kNUoHpHa-UvhQXJf3NTv0tT22yWiSZ3EogdInG7Q","www.encompassvillage.com":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQyD6pGsbDZ_IMKl1sG3ErY3WRqNRTxQeViRtl5W6vKzuRQpp1iqSg8Ig","www.encompassvillage.org":"ABQIAAAAkbhUQPChkkl1dfIAvmBVFhQG36b3Ti2jJLY9hFCzqh5YnzGkahRkPw6Cz4ehvpyGlSKiP6_Kp_z9Ug"}
          var domainkey = null;

          function initialize() {
            var strdomain = document.domain;
            domainkey = apikeys[strdomain];

            var atoggleImg = document.getElementById("mapToggleImg");
            atoggleImg.style.width = "110px";
            atoggleImg.style.height = "18px";
            atoggleImg.src = EV_SHOW_MAP_SRC;
            atoggleImg.title = messages["show"];

            var atoggleAnchor = document.getElementById("mapToggleAnchor");
            if(domainkey != null)
            {
              domainkey = domainkey + "&client=gme-elsevier";
              atoggleAnchor.setAttribute("href","javascript:loadScript()");
            }
            else
            {
              atoggleAnchor.setAttribute("href","javascript:mapUnavailable()");
            }
          }

          function uninitialize() {
            if(populated) {
              GUnload();
            }
          }

          function mapUnavailable() {
            alert("The Engineering Village mapping tool does not support requests from your current domain (" + document.domain + "). If you feel you have received this message in error please contact eicustomersupport@elsevier.com.");
          }

          function loadScript() {
            if(!populated) {
              var script = document.createElement("script");
              script.type = 'text/javascript';
              script.src = "http://maps.google.com/maps?oe=utf-8&file=api&v=2&async=2&callback=togglemap&key=" + domainkey;

              document.body.appendChild(script);

              // change onclick function since loadScript has been called we do not want to call
              // loadScript again
              var atoggleAnchor = document.getElementById("mapToggleAnchor");
              atoggleAnchor.setAttribute("href","javascript:togglemap()");
            }
          }

          function togglemap() {
            var divmap = document.getElementById("map");
            var atoggleImg = document.getElementById("mapToggleImg");

            if(divmap.style.display == "block") {
              // hide Map
              divmap.style.display = "none";
              setmapstate(EV_CLOSED);

              // change button
              atoggleImg.src = EV_SHOW_MAP_SRC;
              atoggleImg.title = messages["show"];
            }
            else {
              if (GBrowserIsCompatible()) {
                // show map
                divmap.style.display = "block";
                setmapstate(EV_OPEN);

                populatemap();

                // change button
                atoggleImg.src = EV_HIDE_MAP_SRC;
                atoggleImg.title = messages["hide"];
              }
            }
          }

          function populatemap() {
            if(populated) return;

            var mapcontainer = document.getElementById("map_canvas");
            mapcontainer.style.background = "#FFFFFF url(/static/images/waiting.gif) no-repeat";

            var request = GXmlHttp.create();
            var milli = (new Date()).getTime();
            request.open("GET", "/controller/servlet/Controller?CID=geoTerms&searchId=" + ev_searchID + "&dbmask=" + ev_dbmask + "&timestamp=" + milli, true);
            request.onreadystatechange = function() {
              if (request.readyState == 4) {
                if (request.status == 200) {
                  mapcontainer.style.background = "none";
                  var places = eval('(' + request.responseText + ')');
                  if(places.placemarks != undefined)
                  {
                    map = new GMap(document.getElementById("map_canvas"));
                    map.setMapType(G_PHYSICAL_MAP);
                    map.setCenter(new GLatLng(0, 0), 1);
                    map.addControl(new GLargeMapControl());
                    bounds = new GLatLngBounds();

                    var markercount = ((places.placemarks.length < EV_MAXMARKERCOUNT) ? places.placemarks.length : EV_MAXMARKERCOUNT);
                    for(var i = 0; i < markercount; i++) {

                      var point = new GLatLng(places.placemarks[i].point.lat, places.placemarks[i].point.lng);
                      var marker = createMarker(point,places.placemarks[i].name,places.placemarks[i].description,places.placemarks[i].search);

                      map.addOverlay(marker);
                      bounds.extend(point);
                    }
                    resetCenterAndZoom();
                    populated = true;
                  }
                }
              }
            }
            request.send(null);
          }

          function createMarker(point,name,description,searchurl) {
            var newIcon = new GIcon(G_DEFAULT_ICON);
            var marker = new GMarker(point,{icon:newIcon, title:description});
            GEvent.addListener(marker, "click", function() {
              document.location = searchurl;
              setmapstate(EV_OPEN);
             });
            return marker;
          }

          function resetCenterAndZoom() {
            var zoom  = map.getBoundsZoomLevel(bounds);
            zoom = (zoom < 1) ? 1 : zoom;
            map.setCenter(bounds.getCenter(), zoom > EV_MAXZOOM ? EV_MAXZOOM : zoom);
          }

          function setmapstate(state) {
            createCookie(EV_MAPSTATE,state,0);
          }
          function getmapstate() {
            return (readCookie(EV_MAPSTATE) == null) ? EV_CLOSED : readCookie(EV_MAPSTATE);
          }

      //]]>
      </script>
    </xsl:if>
    <!-- End of javascript -->
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
      <xsl:if test="($COMPMASK='8192' or $COMPMASK='2097152' or $COMPMASK='2105344') and (/PAGE/NAVIGATORS/NAVIGATOR[@NAME='geonav']) and ($CHINA='false')">
        <xsl:attribute name="onload">initialize()</xsl:attribute>
        <xsl:attribute name="onunload">uninitialize()</xsl:attribute>
      </xsl:if>

      <!-- <script language="JavaScript" type="text/javascript" src="/static/js/wz_tooltip.js"></script> AJAX Dynamic navigators change -->

    <center>

      <!-- APPLY HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
      </xsl:apply-templates>

      <!-- Insert the Global Links -->
      <!-- logo, search history, selected records, my folders. end session -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
      </xsl:apply-templates>

      <!-- Insert the navigation bar -->
      <!-- tabbed navigation -->
      <xsl:apply-templates select="NAVIGATION-BAR">
        <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
        <xsl:with-param name="LOCATION">Top</xsl:with-param>
      </xsl:apply-templates>

        <table border="0" width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top">

              <!-- Insert the TOP Results Manager -->
              <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="LOCATION">top</xsl:with-param>
                <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
              </xsl:call-template>

            <xsl:call-template name="SEARCH-RESULTS"/>
            <xsl:if test="not($SEARCH-TYPE='Easy')">
                <xsl:apply-templates select="//SESSION-DATA/SC"/>
            </xsl:if>
            <xsl:apply-templates select="//SESSION-DATA/DATABASE"/>

              <div style="clear:both;"/>
              <xsl:apply-templates select="//SESSION-DATA/REFINE-STACK"/>

              <div style="clear:both;">
                <xsl:apply-templates select="SORTABLE-FIELDS"/>
                <xsl:apply-templates select="PAGE-RESULTS"/>
              </div>

              <!-- Display of 'Next Page' Navigation Bar -->
              <xsl:apply-templates select="NAVIGATION-BAR">
                <xsl:with-param name="HEAD" select="$SEARCH-ID"/>
                <xsl:with-param name="LOCATION">Bottom</xsl:with-param>
              </xsl:apply-templates>

              <!-- Insert the BOTTOM Results Manager -->
              <xsl:call-template name="RESULTS-MANAGER">
                <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
                <xsl:with-param name="LOCATION">bottom</xsl:with-param>
                <xsl:with-param name="DISPLAY-FORMAT">citation</xsl:with-param>
              </xsl:call-template>

              <!--
              <xsl:if test="DEDUPABLE='true'">
                <xsl:call-template name="DEDUP-NEW">
                    <xsl:with-param name="DBPREF" select="$DBPREF"/>
                    <xsl:with-param name="FIELDPREF" select="$FIELDPREF"/>
                </xsl:call-template>
              </xsl:if> -->

              <p align="right">
              <a class="MedBlueLink" href="#">Back to top</a> <a href="#"><img src="/static/images/top.gif" border="0"/></a>
              </p>

            </td>

            <td width="220" valign="top">
                <!-- <xsl:call-template name="NAVIGATORS"/> AJAX Navigators Call -->
                <xsl:apply-templates select="NAVIGATORS"/>
            </td>

            </tr>
        </table>

      </center>

      <!-- Insert the Footer table -->
      <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      </xsl:apply-templates>

      <br/>

      <script language="JavaScript" type="text/javascript" src="/static/js/wz_tooltip.js"></script>
      <script language="JavaScript" type="text/javascript" src="/static/js/CitedBy.js?v=3"></script>
      <script>ajaxCitedByFunction();</script>
    </body>
    </html>

  </xsl:template>

  <!-- This xsl displays the results in Citation Format when the database is Compendex -->
  <xsl:template match="PAGE-RESULTS">

    <!-- Start of  Citation Results  -->
    <FORM name="quicksearchresultsform">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <xsl:apply-templates select="PAGE-ENTRY"/>
    </table>
    </FORM>
    <!-- END of  Citation Results  -->

  </xsl:template>

  <xsl:template match="PAGE-ENTRY[@DUP='true']">
    <xsl:call-template name="DUPRECORD">
      <xsl:with-param name="HIT-INDEX">
        <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:variable name="SEARCH-TYPE">
    <xsl:value-of select="//SESSION-DATA/SEARCH-TYPE"/>
  </xsl:variable>

  <xsl:variable name="CID-PREFIX">
    <xsl:choose>
      <xsl:when test="($SEARCH-TYPE='Quick') or ($SEARCH-TYPE='Thesaurus')">quickSearch</xsl:when>
      <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">expertSearch</xsl:when>
      <xsl:otherwise>expertSearch</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="HREF-PREFIX"/>

  <xsl:template match="PAGE-ENTRY">
    <xsl:param name="SEARCH-CID"/>

    <xsl:variable name="FULLTEXT-LINK">
      <xsl:value-of select="EI-DOCUMENT/FT/@FTLINK"/>
    </xsl:variable>

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="//SESSION-DATA/SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-ID">
      <xsl:value-of select="//SEARCH-ID"/>
    </xsl:variable>

    <xsl:variable name="RESULTS-COUNT">
      <xsl:value-of select="//RESULTS-COUNT"/>
    </xsl:variable>

    <xsl:variable name="RESULTS-PER-PAGE">
      <xsl:value-of select="//RESULTS-PER-PAGE"/>
    </xsl:variable>

    <xsl:variable name="DISPLAY-QUERY">
      <xsl:value-of select="//SESSION-DATA/DISPLAY-QUERY"/>
    </xsl:variable>

    <xsl:variable name="ENCODED-DISPLAY-QUERY">
      <xsl:value-of select="java:encode($DISPLAY-QUERY)"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DB/ID"/>
    </xsl:variable>

    <xsl:variable name="INDEX">
      <xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/>
    </xsl:variable>

    <xsl:variable name="DOC-ID">
      <xsl:value-of select="EI-DOCUMENT/DOC/DOC-ID"/>
    </xsl:variable>

    <xsl:variable name="AN">
          <xsl:value-of select="EI-DOCUMENT/AN"/>
    </xsl:variable>

    <xsl:variable name="REFERENCES-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>ReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="NP-REFERENCES-LINK-CID">
      <xsl:choose>
        <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>NonPatentReferencesFormat&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/></xsl:when>
        <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="AUTHCD">
      <xsl:value-of select="EI-DOCUMENT/AUTHCD"/>
    </xsl:variable>
    <xsl:variable name="PNUM">
      <xsl:value-of select="EI-DOCUMENT/PAP"/>
    </xsl:variable>
    <xsl:variable name="KIND">
      <xsl:value-of select="EI-DOCUMENT/KC"/>
    </xsl:variable>
    <xsl:variable name="EPNUM">
      <xsl:value-of select="EI-DOCUMENT/AUTHCD"/><xsl:value-of select="EI-DOCUMENT/PAP"/>
    </xsl:variable>  
    <xsl:variable name="INS-REF-CNT">
       <xsl:value-of select="EI-DOCUMENT/NR"/>
    </xsl:variable>
    <xsl:variable name="CIT-CNT">
      <xsl:value-of select="EI-DOCUMENT/CCT"/>
    </xsl:variable>
    <xsl:variable name="REF-CNT">
      <xsl:value-of select="EI-DOCUMENT/RCT"/>
    </xsl:variable>
    <xsl:variable name="DEFAULT-DB-MASK">
        <xsl:value-of select="//DEFAULT-DB-MASK"/>
    </xsl:variable>
    <xsl:variable name="NONREF-CNT">
      <xsl:value-of select="EI-DOCUMENT/NPRCT" />
    </xsl:variable>

    <!-- "&amp;pcinav=0~<patent#>~Patents that cite <patent#>" encoded into links -->
    <xsl:variable name="CITEDBY-PM">
      <xsl:value-of select="EI-DOCUMENT/PM"/>
    </xsl:variable>

    <!-- ZY 9/15/09 Append patent kind in searchword1 for EP patents -->
    <xsl:variable name="CITEDBY-QSTR">
      <xsl:choose>
        <xsl:when test="($SEARCH-TYPE='Quick')">searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTHCD='EP'"><xsl:value-of select="$KIND"/></xsl:if>&amp;section1=PCI&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:when>
        <xsl:otherwise>searchWord1=<xsl:value-of select="$CITEDBY-PM"/><xsl:if test="$AUTHCD='EP'"><xsl:value-of select="$KIND"/></xsl:if><xsl:value-of select="java:encode(' WN PCI')"/>&amp;database=49152&amp;pcinav=0~<xsl:value-of select="$CITEDBY-PM"/>~<xsl:value-of select="java:encode('Patents that cite ')"/><xsl:value-of select="$CITEDBY-PM"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
        <tr>
            <td valign="top" colspan="4" height="5"><img src="/static/images/s.gif"/></td>
        </tr>
        <tr>
            <td valign="top" align="left" width="3">
              <input type="hidden" name="HANDLE"><xsl:attribute name="value"><xsl:value-of select="$INDEX"/></xsl:attribute></input>
              <input type="hidden" name="DOC-ID"><xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute></input>
              <xsl:variable name="IS-SELECTED"><xsl:value-of select="IS-SELECTED" disable-output-escaping="yes"/></xsl:variable>
              <xsl:variable name="CB-NO"><xsl:number/></xsl:variable>
              <input type="checkbox" name="cbresult" onClick="selectUnselectRecord( this,{$INDEX},'{$DOC-ID}','{$ENCODED-DISPLAY-QUERY}','{$DATABASE-ID}','$SESSIONID','{$SEARCH-ID}',{$RESULTS-COUNT})">
              <xsl:if test="$IS-SELECTED='true'">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
              </input>
            </td>
            <td valign="top" width="3"><img src="/static/images/s.gif"/><A class="MedBlackText"><xsl:value-of select="$INDEX" />.</A></td>
            <td valign="top" width="3"><img src="/static/images/s.gif" width="2"/><img width="1" src="/static/images/s.gif" name="image_basket"/></td>
            <td valign="top"><xsl:apply-templates select="EI-DOCUMENT"/>
            <br/>

            <xsl:variable name="ABSTRACT-LINK-CID">
              <xsl:choose>
                <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>AbstractFormat</xsl:when>
                <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
                <xsl:variable name="DETAILED-LINK-CID">
              <xsl:choose>
                <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat</xsl:when>
                <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <!-- do not show Abstract and Detailed links if this is a Referex Record -->
            <xsl:if test="not((EI-DOCUMENT/DOC/DB/DBMASK = '131072'))">
              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Abstract</xsl:attribute>
                <xsl:attribute name="HREF">/search/doc/abstract.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>AbstractFormat</xsl:attribute>Abstract</a>

              <a class="MedBlackText">&#160; - &#160;</a>
              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Detailed</xsl:attribute>
                <xsl:attribute name="ID"><xsl:value-of select="$AN"/>Detail</xsl:attribute>
                <xsl:attribute name="HREF">/search/doc/detailed.url?SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat</xsl:attribute>Detailed</a>
            </xsl:if>

            <!-- Show book summary if this is a Referex Book Record (page 0) or a Referex Page Record (page != 0) -->
            <xsl:if test="(EI-DOCUMENT/DOC/DB/DBMASK = '131072')">

              <!-- jam: Page Details (SAME AS Detailed link) link only for book pages (page !=0) -->
              <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Page Details</xsl:attribute>
                <xsl:attribute name="HREF">/search/book/detailed.url?pageType=page&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;<xsl:value-of select="$DETAILED-LINK-CID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat</xsl:attribute>Page Details</a>
                <a class="MedBlackText">&#160; - &#160;</a>
              </xsl:if>

              <xsl:variable name="PII">
                <xsl:if test="string(EI-DOCUMENT/PII)">&amp;pii=<xsl:value-of select="EI-DOCUMENT/PII"/></xsl:if>
              </xsl:variable>

              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Book Details</xsl:attribute>
                <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
                  <xsl:attribute name="HREF">/search/book/detailed.url?pageType=book&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat<xsl:value-of select="$PII"/></xsl:attribute>
                </xsl:if>
                <xsl:if test="(EI-DOCUMENT/BPP = '0')">
                  <xsl:attribute name="HREF">/search/doc/abstract.url?<xsl:value-of select="$ABSTRACT-LINK-CID"/>&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;pageType=<xsl:value-of select="$CID-PREFIX"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>&amp;format=<xsl:value-of select="$CID-PREFIX"/>DetailedFormat</xsl:attribute>
                </xsl:if>Book Details</a>

              <xsl:if test="not(EI-DOCUMENT/BPP = '0')">
                <a class="MedBlackText">&#160; - &#160;</a>
                <a class="LgBlueLink">
                  <xsl:attribute name="TITLE">Read Page</xsl:attribute>
                  <xsl:attribute name="HREF">javascript:_referex=window.open('/controller/servlet/Controller?CID=bookFrameset&amp;SEARCHID=<xsl:value-of select="$SEARCH-ID"/>&amp;DOCINDEX=<xsl:value-of select="$INDEX"/>&amp;docid=<xsl:value-of select="$DOC-ID"/>&amp;database=<xsl:value-of select="$SELECTED-DB"/>','_referex','<xsl:value-of select="$BOOKS_OPEN_WINDOW_PARAMS"/>');_referex.focus();void('');</xsl:attribute>
                  <img alt="Read Page" src="/static/images/read_page.gif" style="border:0px; vertical-align:middle"/>
                  </a>
              </xsl:if>

              <xsl:if test="string(EI-DOCUMENT/PII)">
                <a class="MedBlackText">&#160; - &#160;</a>
                <a class="LgBlueLink">
                  <xsl:attribute name="target">_referex</xsl:attribute>
                  <xsl:attribute name="TITLE">Read Chapter</xsl:attribute>
                  <xsl:attribute name="HREF"><xsl:value-of select="book:getReadChapterLink(WOBLSERVER, EI-DOCUMENT/BN13,EI-DOCUMENT/PII, /PAGE/CUSTOMER-ID)"/>&amp;EISESSION=$SESSIONID</xsl:attribute>
                  <img alt="Read Chapter" src="/static/images/read_chp.gif" style="border:0px; vertical-align:middle"/>
                  </a>
              </xsl:if>

            </xsl:if>

            <xsl:if test="($REF-CNT) and not($REF-CNT ='0') and not($REF-CNT ='')">
              <A class="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that are referenced by this patent" class="LgBlueLink" HREF="/controller/servlet/Controller?{$REFERENCES-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=patentReferencesFormat&amp;docid={$DOC-ID}">Patent Refs</A>
              &nbsp;<A class="MedBlackText">(<xsl:value-of select="$REF-CNT"/>)</A>
            </xsl:if>
            
             <xsl:if test="($INS-REF-CNT) and not($INS-REF-CNT ='0') and not($INS-REF-CNT ='')">
		<A class="MedBlackText">&#160; - &#160;</A>
		<A title="Show records that are referenced this record" class="LgBlueLink" HREF="/controller/servlet/Controller?{$REFERENCES-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=patentReferencesFormat&amp;docid={$DOC-ID}">Inspec Refs</A>
		&nbsp;<A class="MedBlackText">(<xsl:value-of select="$INS-REF-CNT"/>)</A>
             </xsl:if>

            <xsl:if test="($NONREF-CNT) and not($NONREF-CNT ='0') and not($NONREF-CNT ='')">
              <A class="MedBlackText">&#160; - &#160;</A>
              <A class="LgBlueLink" HREF="/controller/servlet/Controller?{$NP-REFERENCES-LINK-CID}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;database={$SELECTED-DB}&amp;format=ReferencesFormat&amp;docid={$DOC-ID}">Other Refs</A>
              &nbsp;<A class="MedBlackText">(<xsl:value-of select="$NONREF-CNT"/>)</A>
            </xsl:if>

            <xsl:if test="($CIT-CNT) and not($CIT-CNT ='0') and not($CIT-CNT ='')">
              <A class="MedBlackText">&#160; - &#160;</A>
              <A title="Show patents that reference this patent" class="LgBlueLink" HREF="/controller/servlet/Controller?CID={$CID-PREFIX}CitationFormat&amp;{$CITEDBY-QSTR}&amp;yearselect=yearrange&amp;searchtype={$SEARCH-TYPE}&amp;sort=yr">Cited by</A>
              &nbsp;<A class="MedBlackText">(<xsl:value-of select="$CIT-CNT"/>)</A>
            </xsl:if>

            <xsl:variable name="CHECK-CUSTOM-OPT">
              <xsl:value-of select="custoptions:checkFullText($FULLTEXT, $FULLTEXT-LINK, $CUST-ID, EI-DOCUMENT/DO ,EI-DOCUMENT/DOC/DB/DBMASK)" />
            </xsl:variable>

            <xsl:if test="($CHECK-CUSTOM-OPT ='true')">
              <a class="MedBlackText">&#160; - &#160;</a>
              <a class="LgBlueLink">
                <xsl:attribute name="TITLE">Full-text</xsl:attribute>
                <xsl:attribute name="HREF">javascript:newwindow=window.open('/controller/servlet/Controller?CID=FullTextLink&amp;docID=<xsl:value-of select="$DOC-ID"/>','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');</xsl:attribute>
                <img alt="Full-text" src="/static/images/av.gif" id="ftimg"/>
                </a>
            </xsl:if>

             <a class="MedBlackText">
             <xsl:attribute name="ID"><xsl:value-of select='$AN'/>dash</xsl:attribute>
             </a>
	     <a class="LgBlueLink" >
		<xsl:attribute name="ID"><xsl:value-of select='$AN'/></xsl:attribute>
		<xsl:attribute name="TITLE">Cited-by</xsl:attribute>
		<xsl:attribute name="ALT">Scopus Cited-by</xsl:attribute>
             </a>

            <xsl:if test="($LOCALHOLDINGS-CITATION='true')">
              <xsl:apply-templates select="LOCAL-HOLDINGS" mode="CIT">
              <xsl:with-param name="vISSN"><xsl:value-of select="EI-DOCUMENT/SN"/></xsl:with-param>
              </xsl:apply-templates>
            </xsl:if>

      </td>
    </tr>
    <tr>
      <td valign="top" colspan="4" height="5"><img src="/static/images/s.gif"/></td>
    </tr>

</xsl:template>


<!--
  The following templates use the following global variables
  <xsl:variable name="SEARCH-TYPE">
-->
    <xsl:variable name="RERUN-CID">
      <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
    </xsl:variable>

<xsl:template match="SORTABLE-FIELDS">

    <xsl:variable name="SORTMASK">
      <xsl:choose>
        <xsl:when test="string(/PAGE/NAVIGATORS/COMPMASK)">
          <xsl:value-of select="/PAGE/NAVIGATORS/COMPMASK"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="/PAGE/DBMASK"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>


    <A class="RedText"><B>Sort by:</B></A>&#160;&#160;
    <xsl:apply-templates select="SORTBYFIELD[(bit:containsBit($SORTMASK,@mask))]"/>
    <br/>
    <!-- debugging
    by <xsl:value-of select="//SESSION-DATA/SORT-OPTION" /> - <xsl:value-of select="//SESSION-DATA/SORT-DIRECTION" />
    <br/> -->

</xsl:template>

<!-- jam - CURRENT SEARCH'S SORT SETTINGS -->
<!-- //SESSION-DATA/SORT-DIRECTION -->
<!-- //SESSION-DATA/SORT-OPTION -->

<!-- show UN-selected Sort options with link to their default direction! -->
<xsl:template match="SORTBYFIELD[(@value!=//SESSION-DATA/SORT-OPTION)]">

    &#160;<img src="/static/images/s.gif" width="12" height="12" border="0"/>
    &#160;<A title="Sort by {.}" class="SpLink"><xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;location=SORT&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;sortdir=<xsl:value-of select="@defaultdir"/>&amp;sort=<xsl:value-of select="@value"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/></xsl:attribute><xsl:value-of select="."/></A>

</xsl:template>

<!-- show Selected Sort option with direction arrow and, if available,
    a link to their opposite direction! -->
<xsl:template match="SORTBYFIELD[(@value=//SESSION-DATA/SORT-OPTION)]">

    <xsl:variable name="dir"><xsl:value-of select="//SESSION-DATA/SORT-DIRECTION"/></xsl:variable>

    &#160;<img src="/static/images/{$dir}ar.gif" width="12" height="12" border="0"><xsl:attribute name="alt">Sorted <xsl:value-of select="."/>&#160;<xsl:value-of select="srt:displayDir($dir)"/></xsl:attribute></img>
    &#160;<A class="SpLink"><xsl:apply-templates/></A>

</xsl:template>

<xsl:template match="SORT-DIR[@dir!=//SESSION-DATA/SORT-DIRECTION]">
    <xsl:attribute name="TITLE">Re-Sort <xsl:value-of select="../."/>&#160;<xsl:value-of select="srt:displayDir(@dir)"/></xsl:attribute>
    <xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;sortdir=<xsl:value-of select="@dir"/>&amp;sort=<xsl:value-of select="../@value"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/></xsl:attribute>
</xsl:template>

<xsl:template match="NAVIGATORS"> <!-- name="NAVIGATORS" AJAX Navigators call -->

  <!-- jam turkey - added for Help link to have proper anchor to reflect page context -->
  <xsl:variable name="REFCTX">
    <xsl:choose>
      <xsl:when test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">Refine_Within_Easy_Search.htm</xsl:when>
      <xsl:otherwise>Refining_within_Quick_Search_or_Expert_Search_or_Thesaurus_Search.htm</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <form name="navigator" method="POST" >
    <xsl:attribute name="action">/search/results/expert.url?CID=expertSearchCitationFormat&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/></xsl:attribute>

    <!-- preserve sort data -->
    <input type="hidden" name="sort">
      <xsl:attribute name="value"><xsl:value-of select="//SESSION-DATA/SORT-OPTION"/></xsl:attribute>
    </input>
    <input type="hidden" name="sortdir">
      <xsl:attribute name="value"><xsl:value-of select="//SESSION-DATA/SORT-DIRECTION"/></xsl:attribute>
    </input>

    <!-- jam  This refine search form ALWAYS becomes an expert search -->
    <table border="0" cellspacing="0" cellpadding="0" width="100%" >
      <tr><td colspan="3" height="4"><img src="/static/images/s.gif" height="4"/></td></tr>
      <tr><td colspan="3" height="1" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" height="1"/></td></tr>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="3" width="1"/></td>
        <td valign="top"><img src="/static/images/s.gif" border="0" height="3" width="1" /></td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="3" width="1"/></td>
      </tr>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        <td valign="middle">
          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
              <td valign="bottom" align="left"><a class="LgOrangeText">&#160; <b>Refine Results</b></a></td>
              <td valign="top" align="right"><a class="SmBlueText"><b>?</b></a>
                <a class="DecLink" href="javascript:makeUrl('{$REFCTX}')">Help</a>&#160;&#160;</td>
            </tr>
          </table>
        </td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
      </tr>
      <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
        <tr>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
          <td valign="top" align="right"><input onclick="javascript:return navigatorsOnsubmit('limit');" type="image" src="/static/images/sch.gif" align="absbottom" name="search" value="Search" border="0" />&#160;&#160;<input onclick="javascript:return navigatorsOnsubmit('exclude');" type="image" src="/static/images/xsch.gif" align="absbottom" name="exclude" value="Exclude" border="0" />&#160;&#160;</td>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        </tr>
      </xsl:if>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        <td valign="top">
          <!--  AJAX Navigators call
          <div id="navigators" style="background:#FFFFFF url(/static/images/waiting.gif) no-repeat;">
            <img align="absmiddle" src="/static/images/s.gif" border="0" width="30" height="30"/>
            <span class="MedBoldBlackText">Loading facets...</span>
          </div> -->
          <xsl:apply-templates/>
        </td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
      </tr>
      <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
        <tr>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
          <td valign="top">

            <!-- use same format as a Navigator/Modifier for Add a term checkbox and input -->
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr>
              <td>
                <table border="0" width="100%" cellspacing="0" cellpadding="0">
                  <tr><td colspan="3" ><img src="/static/images/s.gif" height="2"/></td></tr>
                  <tr><td width="4" valign="top" ><img src="/static/images/s.gif" width="4"/></td><td valign="top" colspan="2"><a class="MedOrangeText"><label for="txtAdd"><b>Add a term</b></label></a></td></tr>
                  <tr>
                    <td width="4"><img src="/static/images/s.gif" width="4"/></td>
                    <td width="4" align="left" >
                      <!-- jam replaced checkbox with spacer -->
                      <img src="/static/images/s.gif" width="4"/>
                    </td>
                    <td width="100%" valign="top" align="left">
                    <a class="SmBlackText"><input id="txtAdd" type="text" name="append" class="SmBlackText" size="15" maxlength="30"/></a>
                    </td>
                  </tr>
                  <tr><td colspan="3" ><img src="/static/images/s.gif" height="2"/></td></tr>
                  <tr><td colspan="3" ><img src="/static/images/s.gif" height="2"/></td></tr>
                </table>
              </td>
            </tr>
            </table>
          </td>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
          <td valign="middle" align="left">
            <A class="SmBlackText">
              &#160;<input id="rdRes" checked="checked" type="radio" name="resultsorall" value="results"/><label for="rdRes">Search within results</label><br/>
              &#160;<input id="rdAll" type="radio" name="resultsorall" value="all"/><label for="rdAll">Search all content</label><br/>
            </A>
          </td>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="2" width="1"/></td>
          <td valign="top"><img src="/static/images/s.gif" border="0" height="2" width="1" /></td>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="2" width="1"/></td>
        </tr>
        <tr>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
          <td valign="middle" align="right"><input type="image" onclick="javascript:return navigatorsOnsubmit('limit');" src="/static/images/sch.gif" name="search" align="absbottom" value="Search" border="0" />&#160;&#160;<input onclick="javascript:return navigatorsOnsubmit('exclude');" type="image" src="/static/images/xsch.gif" align="absbottom" name="exclude" value="Exclude" border="0" />&#160;&#160;</td>
          <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        </tr>
      </xsl:if>

      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="4" width="1"/></td>
        <td valign="top"><img src="/static/images/s.gif" border="0" height="4" width="1" /></td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="4" width="1"/></td>
      </tr>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="5" width="1"/></td>
        <td valign="top" align="right"><a class="SmBlueText"><b>?</b></a>
        <a class="DecLink" href="javascript:makeUrl('{$REFCTX}')">Help</a>&#160;&#160;</td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="5" width="1"/></td>
      </tr>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
        <td valign="top">
          <xsl:apply-templates select="../NAVPAGER"/>
        </td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" width="1"/></td>
      </tr>
      <tr>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="3" width="1"/></td>
        <td valign="top"><img src="/static/images/s.gif" border="0" height="3" width="1" /></td>
        <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" border="0" height="3" width="1"/></td>
      </tr>
      <tr>
        <td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" border="0"/></td>
      </tr>
    </table>
  </form>

</xsl:template>

<xsl:template match="NAVPAGER" >
  <p align="right"><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="PAGERS" >
    <tr>
      <td width="4" ><img src="/static/images/s.gif" width="4"/></td>
      <td width="12" align="left" height="10"><img src="/static/images/s.gif" height="10" border="0"/></td>
      <td width="100%" valign="top" align="right" height="10">
      <xsl:apply-templates/>
    </td>
    </tr>
</xsl:template>

<!-- jam - added search anchors so the more and less links return you to the same navigator -->
<xsl:template match="MORE" >
  <a class="MedBlueLink">
  <xsl:attribute name="title">expand selections</xsl:attribute>
  <xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;navigator=MORE&amp;FIELD=<xsl:value-of select="../@FIELD"/>:<xsl:value-of select="@COUNT"/>&amp;SEARCHID=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;COUNT=<xsl:value-of select="//CURR-PAGE-ID"/>&amp;#<xsl:value-of select="../@FIELD"/></xsl:attribute>
  more...</a><img src="/static/images/s.gif" width="5" height="1"/>
</xsl:template>

<xsl:template match="LESS" >
  <a class="MedBlueLink">
  <xsl:attribute name="title">collapse selections</xsl:attribute>
  <xsl:attribute name="href">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;navigator=MORE&amp;FIELD=<xsl:value-of select="../@FIELD"/>:<xsl:value-of select="@COUNT"/>&amp;SEARCHID=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;COUNT=<xsl:value-of select="//CURR-PAGE-ID"/>&amp;#<xsl:value-of select="../@FIELD"/></xsl:attribute>
  ...less</a><img src="/static/images/s.gif" width="5" height="1"/>
</xsl:template>

<xsl:template match="COMPMASK">
</xsl:template>

<!-- jam - added name attribute to navigator for more and less href anchors -->
<xsl:template match="NAVIGATOR">
  <xsl:variable name="SEARCHID">
    <xsl:value-of select="//SESSION-DATA/QUERY-ID"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:variable>

  <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr>
      <td>
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
          <tr>
          <td width="4" valign="top" ><img src="/static/images/s.gif" width="4"/></td><td valign="top" colspan="2">&#160;<a class="MedOrangeText" name="{@FIELD}"><b><xsl:value-of select="@LABEL"/></b></a>&nbsp;&nbsp;
            <xsl:if test="/PAGE/NAVCHRT='true'">
             <a title="View chart" href="" onclick="window.open('/controller/servlet/Controller?CID=analyzeNav&amp;SEARCHID={$SEARCHID}&amp;database={$DATABASE}&amp;field={@FIELD}','newwindow','width=600,height=600,toolbar=no,location=no,scrollbars,resizable');return false">
             <img src="/static/images/gr_img.gif" border="0"/></a>&nbsp;&nbsp;
             <a title="Download data" href="/controller/servlet/Controller?CID=downloadNavigatorCSV&amp;SEARCHID={$SEARCHID}&amp;database={$DATABASE}&amp;nav={@FIELD}nav">
             <img src="/static/images/note.gif" border="0"/></a>&nbsp;&nbsp;
            </xsl:if>
          </td>
          </tr>
          <xsl:apply-templates/>
        </table>
      </td>
    </tr>
  </table>
</xsl:template>

<xsl:template match="MODIFIER">
    <tr>
        <td width="4"><img src="/static/images/s.gif" width="4"/></td>
        <td width="12" valign="top" align="left" >
          <xsl:if test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
            <img src="/static/images/s.gif" width="12"/>
          </xsl:if>
          <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
            <input type="checkbox">
            <xsl:attribute name="id"><xsl:value-of select="../@NAME"/><xsl:value-of select="position()" /></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="../@NAME"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="@COUNT"/>~<xsl:value-of select="VALUE"/>~<xsl:value-of select="LABEL"/></xsl:attribute>
            </input>
          </xsl:if>
        </td>
        <td width="100%" valign="middle" align="left">
          <!-- [<xsl:value-of select="(normalize-space(VALUE)='')"/>] -->
          <!-- [<xsl:value-of select="count(../MODIFIER) = 1"/>] -->
          <!--<xsl:if test="(../@NAME)='pecnav' or (../@NAME)='pidnav' or (../@NAME)='pucnav'">
            <a href="#"><img src="/static/images/plus.gif" border="0" width="8"/></a>
          </xsl:if>-->

          <a>
            <xsl:choose>
              <xsl:when test="(TITLE[@TYPE='BOOK'])">
                 <xsl:attribute name="class">SmBlueText</xsl:attribute>
              </xsl:when>
              <xsl:when test="(TITLE[@TYPE='SECT'])">
                 <xsl:attribute name="class">SmBlueText</xsl:attribute>
              </xsl:when>
              <xsl:when test="(TITLE != '')">
                 	<xsl:attribute name="class">SmBoldBlueText2</xsl:attribute>
              </xsl:when>
              <xsl:when test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
                <xsl:attribute name="class">MedBlueLink</xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="class">SmBlackText</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="(TITLE != '')">
              <xsl:attribute name="onmouseover">this.T_WIDTH=450;return escape('<xsl:value-of select="TITLE"/>')</xsl:attribute>
            </xsl:if>

            <!-- suppress HREF when VALUE is missing -->
            <xsl:if test="((/PAGE/SESSION-DATA/SEARCH-TYPE='Easy') and not(normalize-space(VALUE)=''))">
              <xsl:if test="not(@COUNT=0)">
                <xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/>&amp;append=<xsl:value-of select="@COUNT"/>~<xsl:value-of select="java:encode(VALUE)"/>~<xsl:value-of select="java:encode(LABEL)"/>&amp;section=<xsl:value-of select="../@NAME"/></xsl:attribute>
              </xsl:if>
            </xsl:if>
            <xsl:if test="/PAGE/SESSION-DATA/SEARCH-TYPE='Easy'">
              <xsl:value-of select="LABEL"  disable-output-escaping="yes"/>
            </xsl:if>
            <xsl:if test="/PAGE/SESSION-DATA/SEARCH-TYPE != 'Easy'">
              <label>
                <xsl:attribute name="for"><xsl:value-of select="../@NAME"/><xsl:value-of select="position()" /></xsl:attribute>
                  <xsl:value-of select="LABEL" disable-output-escaping="yes"/>
              </label>
            </xsl:if>
          </a>
          <img src="/static/images/s.gif" width="4" height="1"/>
          <a class="SmBlackText">(<xsl:value-of select="@COUNT"/>)</a>
        </td>
    </tr>
    <!--  CS added if statement to give more space inbetween queries in easy search -->
    <tr>
      <td colspan="3" >
        <xsl:if test="(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <img src="/static/images/s.gif" height="6"/>
        </xsl:if>
        <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">
          <img src="/static/images/s.gif" height="1"/>
        </xsl:if>
      </td>
    </tr>
</xsl:template>

<xsl:template match="REFINE-STACK">
  <xsl:if test="count(NAVIGATOR)!=0">
    <xsl:apply-templates mode="breadcrumb"/>
    <br/><img src="/static/images/s.gif" height="4"/><br/>
  </xsl:if>
</xsl:template>

<xsl:template match="NAVIGATOR[not(position()=1) or ((position()=1) and not(position()=last())) ]" mode="breadcrumb">
    <A class="SpLink"><xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;REMOVE=<xsl:value-of select="position()"/></xsl:attribute><img src="/static/images/x.gif" border="0" alt="Remove Term"/></A>&#160;<xsl:call-template name="NAVIGATOR"/>
</xsl:template>

<xsl:template match="NAVIGATOR" name="NAVIGATOR" mode="breadcrumb">
  <xsl:if test="not(/PAGE/SESSION-DATA/SEARCH-TYPE='Easy')">&#160;<img src="/static/images/{@INCLUDEALL}.gif"/></xsl:if><A class="LgBlueLink"><xsl:attribute name="HREF">/controller/servlet/Controller?CID=<xsl:value-of select="$RERUN-CID"/>&amp;RERUN=<xsl:value-of select="//SESSION-DATA/QUERY-ID"/>&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;STEP=<xsl:value-of select="position()"/></xsl:attribute><B><xsl:value-of select="MODIFIER/LABEL"/></B></A>&#160;
</xsl:template>

</xsl:stylesheet>