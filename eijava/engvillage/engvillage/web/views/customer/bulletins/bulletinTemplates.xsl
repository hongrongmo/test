<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:java="java:java.net.URLEncoder">
    <xsl:template name="LT">
    	<xsl:variable name="WK">
		<xsl:value-of select="WK"/>
	</xsl:variable>
	<xsl:variable name="PD">
	    <xsl:value-of select="PD"/>
	</xsl:variable>
	<xsl:variable name="ID">
	    <xsl:value-of select="ID"/>
	</xsl:variable>
	<xsl:variable name="DB">
	     <xsl:value-of select="DB"/>
	</xsl:variable>
	<xsl:variable name="NM">
	    <xsl:value-of select="NM"/>
	</xsl:variable>
	<xsl:variable name="CY">
	    <xsl:value-of select="CY"/>
	</xsl:variable>
        <xsl:variable name="YR">
	    <xsl:value-of select="YR"/>
	</xsl:variable>
	<xsl:variable name="CYD">
		<xsl:value-of select="CYD"/>
	</xsl:variable>
	<xsl:variable name="TIME">
		<xsl:value-of select="TIME"/>
	</xsl:variable>
	<xsl:variable name="SECURITYCODE">
		<xsl:value-of select="SECURITYCODE"/>
	</xsl:variable>
        <tr>
            <td valign="top">      
		<xsl:choose>
			<xsl:when test="boolean($LIT-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=HTML&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>              
			</xsl:when>
			<xsl:otherwise>
			    <a class="SmBlueText"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>
			</xsl:otherwise>
            	</xsl:choose>
            </td>
            <td width="5" height="20">
                <img src="/engresources/images/s.gif" width="5" height="20"/>
            </td>
            <td nowrap="true">
                <a class="SmBlackText">
                    <xsl:value-of select="$PD" disable-output-escaping="yes"/>
                </a>
            </td>
           
            <td width="5">
                <img src="/engresources/images/s.gif" width="5"/>
            </td>
            <td valign="top" nowrap="true">
            <xsl:choose>
                <xsl:when test="boolean($LIT-PDF='true')">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=PDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">View  </a>          
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=SAVEPDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>
                </xsl:when>
                <xsl:otherwise>
                    <a class="SmBlueText">View&#160;&#160;Save</a>
                </xsl:otherwise>
            </xsl:choose>
            </td>
           
            <td>
                 <img src="/engresources/images/s.gif"/>
            </td>
            <td>
	         <img src="/engresources/images/s.gif"/>
            </td>
       
        </tr>
        <tr>
            <td height="6" colspan="7">
                 <img src="/engresources/images/s.gif" height="6"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="PT">
        <xsl:variable name="WK">
		<xsl:value-of select="WK"/>
	</xsl:variable>
	<xsl:variable name="PD">
	    <xsl:value-of select="PD"/>
	</xsl:variable>
	<xsl:variable name="ID">
	    <xsl:value-of select="ID"/>
	</xsl:variable>
	<xsl:variable name="DB">
	     <xsl:value-of select="DB"/>
	</xsl:variable>
	<xsl:variable name="NM">
	    <xsl:value-of select="NM"/>
	</xsl:variable>
	<xsl:variable name="CY">
		<xsl:value-of select="CY"/>
	</xsl:variable>
	<xsl:variable name="ZP">
			<xsl:value-of select="ZP"/>
	</xsl:variable>
	<xsl:variable name="YR">
		    <xsl:value-of select="YR"/>
	</xsl:variable>
        <xsl:variable name="CYD">
		<xsl:value-of select="CYD"/>
	</xsl:variable>
	<xsl:variable name="TIME">
		<xsl:value-of select="TIME"/>
	</xsl:variable>
	<xsl:variable name="SECURITYCODE">
		<xsl:value-of select="SECURITYCODE"/>
	</xsl:variable>
        <tr>
            <td>
                <xsl:choose>
		    <xsl:when test="boolean($PAT-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=HTML&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>              
		    </xsl:when>
		    <xsl:otherwise>
			    <a class="SmBlueText"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>
		    </xsl:otherwise>
            	</xsl:choose>
            </td>
            <td width="5" height="20">
                <img src="/engresources/images/s.gif" width="5" height="20"/>
            </td>
            <td nowrap="true">
                <a class="SmBlackText">
                    <xsl:value-of select="$PD" disable-output-escaping="yes"/>
                </a>
            </td>
                     
            <td width="5" height="20">
                <img src="/engresources/images/s.gif" width="5" height="20"/>
            </td>
            <td nowrap="true">
               <xsl:choose>
                <xsl:when test="boolean($PAT-PDF='true')">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=PDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=SAVEPDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>
                </xsl:when>
                <xsl:otherwise>
                    <a class="SmBlueText">View&#160;&#160;Save</a>
                </xsl:otherwise>
            </xsl:choose>
            </td>
           
            <td width="50" height="20">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td width="90">
            <xsl:choose>
                <xsl:when test="not($ZP='') and not($ZP='null')">                   
                    <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=ZIP&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>                   
                </xsl:when>
            </xsl:choose>
            </td>
        </tr>
        <tr>
            <td height="6" colspan="7">
                <img src="/engresources/images/s.gif"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="BL[@FORMAT = '3']">
        <xsl:variable name="WK">
	    <xsl:value-of select="WK"/>
        </xsl:variable>
        <xsl:variable name="PD">
            <xsl:value-of select="PD"/>
        </xsl:variable>
        <xsl:variable name="ID">
            <xsl:value-of select="ID"/>
        </xsl:variable>
        <xsl:variable name="DB">
	     <xsl:value-of select="DB"/>
	</xsl:variable>
	<xsl:variable name="NM">
	     <xsl:value-of select="NM"/>
        </xsl:variable>
	<xsl:variable name="CY">
		<xsl:value-of select="CY"/>
	</xsl:variable>
        <xsl:variable name="YR">
		<xsl:value-of select="YR"/>
	</xsl:variable>
	<xsl:variable name="CYD">
		<xsl:value-of select="CYD"/>
	</xsl:variable>
	<xsl:variable name="TIME">
		<xsl:value-of select="TIME"/>
	</xsl:variable>
	<xsl:variable name="SECURITYCODE">
		<xsl:value-of select="SECURITYCODE"/>
	</xsl:variable>
        <tr>
            <td height="20">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=HTML&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">
                   Issue <xsl:value-of select="$WK" disable-output-escaping="yes"/>
                </a>
            </td>
            <td width="50">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td nowrap="true">
                <a class="SmBlackText">
                    <xsl:value-of select="$PD" disable-output-escaping="yes"/>
                </a>
            </td>        
            <td width="50">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td nowrap="true">
                 <xsl:choose>
                <xsl:when test="boolean($SHOW-PDF='true')">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=PDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=SAVEPDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>
                </xsl:when>
                <xsl:otherwise>
                    <a class="SmBlueText">View&#160;&#160;Save</a>
                </xsl:otherwise>
            </xsl:choose>
            </td>
            <td colspan="3">
	    	<img src="/engresources/images/s.gif" height="6"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="BL[@FORMAT = '4']">
        <xsl:variable name="NM">
            <xsl:value-of select="NM"/>
        </xsl:variable>
        <xsl:variable name="WK">
	     <xsl:value-of select="WK"/>
        </xsl:variable>
        <xsl:variable name="PD">
            <xsl:value-of select="PD"/>
        </xsl:variable>
        <xsl:variable name="ID">
            <xsl:value-of select="ID"/>
        </xsl:variable>
        <xsl:variable name="ZP">
            <xsl:value-of select="ZP"/>
        </xsl:variable>
        <xsl:variable name="DB">
		<xsl:value-of select="DB"/>
	</xsl:variable>
	<xsl:variable name="YR">
		<xsl:value-of select="YR"/>
	</xsl:variable>
	<xsl:variable name="CY">
		<xsl:value-of select="CY"/>
	</xsl:variable>
	<xsl:variable name="CYD">
		<xsl:value-of select="CYD"/>
	</xsl:variable>
	<xsl:variable name="TIME">
		<xsl:value-of select="TIME"/>
	</xsl:variable>
	<xsl:variable name="SECURITYCODE">
		<xsl:value-of select="SECURITYCODE"/>
	</xsl:variable>
        <tr>
            <td height="15">
            	<xsl:choose>
		    <xsl:when test="boolean($SHOW-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=HTML&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">
			 Issue <xsl:value-of select="$WK" disable-output-escaping="yes"/>
			</a>
		    </xsl:when>
		    <xsl:otherwise>
			<a class="SmBlueText"><xsl:value-of select="$NM" disable-output-escaping="yes"/></a>
		    </xsl:otherwise>
            	</xsl:choose>            
            </td>
            <td width="50">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td nowrap="true">
                <a class="SmBlackText">
                    <xsl:value-of select="$PD" disable-output-escaping="yes"/>
                </a>
            </td>
                   
            <td width="50">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td nowrap="true">
                <xsl:choose>
                <xsl:when test="boolean($SHOW-PDF='true')">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=PDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=SAVEPDF&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>
                </xsl:when>
                <xsl:otherwise>
                    <a class="S">View&#160;&#160;Save</a>
                </xsl:otherwise>
            </xsl:choose>
            </td>
            
            <td width="50">
                <img src="/engresources/images/s.gif" width="50" height="20"/>
            </td>
            <td colspan="2">
            <xsl:choose>
                <xsl:when test="not($ZP='') and not($ZP='null')">                
                        <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;db={$DB}&amp;fn={$NM}&amp;cType=ZIP&amp;id={$ID}&amp;yr={$YR}&amp;cy={$CYD}&amp;tm={$TIME}&amp;sc={$SECURITYCODE}">Save</a>                    
                </xsl:when>
                <xsl:otherwise/>
            </xsl:choose>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="BULLETINS" mode="LT">
        <xsl:apply-templates select="BL" mode="LT"/>
    </xsl:template>
    <xsl:template match="BULLETINS" mode="PT">
        <xsl:apply-templates select="BL" mode="PT"/>
    </xsl:template>
    <xsl:template match="BULLETINS" mode="RESULTS">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="BL" mode="LT">
        <xsl:if test="@FORMAT='1'">
            <!--Target attribute specified.-->
            <xsl:call-template name="LT"/>
        </xsl:if>
    </xsl:template>
    <xsl:template match="BL" mode="PT">
        <xsl:if test="@FORMAT='2'">
            <!--Target attribute specified.-->
            <xsl:call-template name="PT"/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
