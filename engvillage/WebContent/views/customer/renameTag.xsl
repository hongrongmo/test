<?xml version="1.0" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:java="java:java.net.URLDecoder"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="java xsl html">

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="addTagNavigationBar.xsl"/>
<xsl:include href="groupsNavigationBar.xsl"/>
<xsl:include href="Footer.xsl"/>

 <xsl:template match="PAGE/TAGS">
  	<!-- 	<xsl:variable name="TAG"><xsl:value-of select="TAG"/></xsl:variable> -->
    <!--    <option value=""><xsl:value-of select="TAG"/></option> -->
    <!--    <xsl:value-of select="$TAG"/> -->
    <xsl:apply-templates />
  </xsl:template>

<xsl:template match="TAG">
	<xsl:variable name="TAG"><xsl:value-of select="." disable-output-escaping="yes"/></xsl:variable>
	<option value="{$TAG}"><xsl:value-of select="$TAG"/>
	</option>
</xsl:template>

  <xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
        <xsl:value-of select="DBMASK"/>
    </xsl:variable>

  <html>
    <head>
    <title>Rename Tags</title>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
      <xsl:text disable-output-escaping="yes">
      <![CDATA[
        <xsl:comment>
        <script language="javascript">
          function renameTags()
      	  {
      	    if (document.forms[0].newtag.value.length == 0)
      		  {
      			  window.alert("New Tag name cannot be empty");
      			  return false;
      		  }
      	    else if(document.forms[0].newtag.value == '')
      	    {
              window.alert("Tag Name must be given");
      	      return false;
            }
            else if (document.forms[0].newtag.value.length > 30)            
            {
                  window.alert("New Tag name should not exceed 30 characters");
      			  return false;
            }

            var newtag =  encodeURIComponent(document.forms[0].newtag.value);
            var url = "/controller/servlet/Controller?CID=renameTag";
            var scopelength = document.forms[0].scope.length;
            var rtaglength =  document.forms[0].rtag.length;
            var personalization = 'true';
            var i=0;
            var scopevalue = null;
            var rtagvalue = null;

            for(i=0; i < scopelength ; i++)
            {
              if(document.forms[0].scope[i].selected)
              {
                scopevalue = document.forms[0].scope[i].value;
                break;
              }
            }

            for(i=0; i < rtaglength ; i++)
            {
              if(document.forms[0].rtag[i].selected)
              {
                rtagvalue = encodeURIComponent(document.forms[0].rtag[i].value);
                break;
              }
            }

            if(personalization == 'true' )
            {
              url = url +
                "&scope="+scopevalue +
                "&oldtag="+rtagvalue+
                "&newtag="+newtag;
              document.location = url;
            }
            else
            {
              nexturl = nexturl+"&amp;tag="+tag+"&scope="+scopevalue;
              personalloging = personalloging +
                "&nexturl="+nexturl+
                "&backurl="+nexturl;
              document.location = personalloging;
      	    }
      	  }

          function findTags()
          {
      	      var url = "/controller/servlet/Controller?CID=renameTag";
              var scopelength = document.forms[0].scope.length;
              var personalization = 'true';
              var i=0;
              var scopevalue = null;

              for(i=0; i < scopelength ; i++)
              {
                if(document.forms[0].scope[i].selected)
                {
                  scopevalue = document.forms[0].scope[i].value;
                  break;
                }
              }

              if(personalization == 'true' )
              {
                url = url +
                "&scope="+scopevalue;
                document.location = url;
              }
              else
              {
                nexturl = nexturl+"&amp;tag="+tag+"&scope="+scopevalue;
                personalloging = personalloging +
                "&nexturl="+nexturl+
                "&backurl="+nexturl;
                document.location = personalloging;
              }
      	  }
          </script>
        </xsl:comment>
      ]]>
      </xsl:text>
    </head>

    <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

    <center>

      <!-- APPLY HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="DATABASE"/>
      </xsl:apply-templates>

      <!-- Insert the Global Links -->
      <!-- logo, search history, selected records, my folders. end session -->
      <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK">Tags</xsl:with-param>
      </xsl:apply-templates>

      <xsl:apply-templates select="ADDTAGSELECTRANGE-NAVIGATION-BAR"/>

      <xsl:apply-templates select="GROUPS-NAVIGATION-BAR">
        <xsl:with-param name="LOCATION">RenameTags</xsl:with-param>
      </xsl:apply-templates>

      <form style="margin:0px; padding:0px;" name="renametag"  onSubmit="javascript:renameTags();return false;" method="POST">
        <input type="hidden" name="DATABASE" value="{$DATABASE}"/>
        <table border="0" width="99%" cellpadding="0" cellspacing="0">
        	<tr>
        	  <td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
        	</tr>
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%"><b><a class="MedGreyTextTag">Choose Tag Category:</a></b></td>
  	      </tr>
        	<tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%">
              <a class="SmBlackText">
                <xsl:variable name="SCOPE">
                  <xsl:value-of select="SCOPE" />
                </xsl:variable>
                <xsl:variable name="SCOPECOLOR">
                  <xsl:value-of select="SCOPECOLOR" />
                </xsl:variable>

                <select size="1" name="scope" onchange="return findTags()" >
                  <option value="1">
                  <xsl:if test="'1'=$SCOPE">
                    <xsl:attribute name="selected">true</xsl:attribute>
                  </xsl:if>Public</option>
                  <option value="2">
                  <xsl:if test="'2'=$SCOPE">
                    <xsl:attribute name="selected">true</xsl:attribute>
                  </xsl:if>
                  Private</option>
                	<option value="4">
                	<xsl:if test="'4'=$SCOPE">
                	  <xsl:attribute name="selected">true</xsl:attribute>
                	</xsl:if>
                  My Institution</option>
                  <xsl:for-each select="/PAGE/TGROUPS/TGROUP">
                    <xsl:variable name="CGROUPID">
                      <xsl:value-of select="GID" />
                    </xsl:variable>
                    <xsl:variable name="GTITLE">
                      <xsl:value-of select="GTITLE" />
                    </xsl:variable>
                    <option>
                      <xsl:attribute name="value"><xsl:value-of select="$CGROUPID"/></xsl:attribute>
                      <xsl:if test="$CGROUPID=$SCOPE">
                        <xsl:attribute name="selected">true</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="$GTITLE"/>
                    </option>
                  </xsl:for-each>
                </select>
              </a>
            </td>
  	      </tr>
        	<tr>
        	  <td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
        	</tr>
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%"><b><a class="MedGreyTextTag">Choose a Tag to Rename:</a></b></td>
  	      </tr>
  	      
  	      
     <xsl:choose>
     	<xsl:when test="count(TAGS/TAG) &gt; 0">
	
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
		<td width="90%">
			<select size="1" name="rtag">
				<xsl:apply-templates select="TAGS"/>
			</select>
		</td>
  	      </tr>
	      <tr>
	      		<td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
	      </tr>
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%"><hr height="0.2" color="#6c8dac" width="53%" align="left"/></td>
  	      </tr>
        	<tr>
        	  <td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
        	</tr>
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%"><b><a class="MedGreyTextTag">Rename Tag:</a></b></td>
  	      </tr>
  	      <tr>
  	        <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
  		      <td width="90%"><input size="30" name="newtag"/></td>
             </tr>
        	<tr>
        	  <td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
        	</tr>
        	<tr>
        	  <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		<td width="90%">
              <a onclick="javascript:renameTags();return false;">
                 <img src="/engresources/tagimages/renametag_button.gif" border="0" alt="Rename Tag" />
              </a>
            </td>
          </tr>
        </xsl:when>
        <xsl:otherwise>
        <tr>  	        
          <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
	  <td>
        	<span CLASS="MedBlackText">No tags found.</span>
          </td>
        </tr>
        </xsl:otherwise>
     </xsl:choose>
          
          
        </table>
      </form>

    </center>

    <!-- Start of footer-->
    <xsl:apply-templates select="FOOTER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>

