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
    <title>Delete Tags</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
    <xsl:comment>
    <script language="javascript">

    function deleteTags()
	{

	   var dtag =  encodeURIComponent(document.deletetag.dtag.value);
	   var url = "/controller/servlet/Controller?CID=deleteTag";
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
	       "&scope="+scopevalue +
	       "&deletetag="+dtag;
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
	    var url = "/controller/servlet/Controller?CID=deleteTag";
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
        <xsl:with-param name="LOCATION">DeleteTags</xsl:with-param>
      </xsl:apply-templates>

      <form style="margin:0px; padding:0px;" name="deletetag" action="/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=renameTag&amp;database={$DATABASE}"  method="POST">
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
              <a CLASS="SmBlackText">
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
                  </xsl:if>
                  Public</option>
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
        	<td width="90%"><b><a class="MedGreyTextTag">Choose a Tag to Delete:</a></b></td>
  	      </tr>
  	      <xsl:choose>
     	      	<xsl:when test="count(TAGS/TAG) &gt; 0">
        	  	<tr>
  		  	      <td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
        		      <td>
              		      	<select size="1" name="dtag" >
                			<xsl:apply-templates select="TAGS"/>
              			</select>
            	      	      </td>
          		</tr>
          		<tr>
        			<td colspan="2"><img src="/static/images/s.gif" height="15" width="30"/></td>
          		</tr>
          		<tr>
            			<td width="10%"><img src="/static/images/s.gif" height="15" width="30"/></td>
		    		<td valign="top">
            	  			<a onclick="javascript:deleteTags();return false;">
            	  	  			<img src="/engresources/tagimages/deletetag_button.gif" border="0" alt="Delete Tag" />
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

