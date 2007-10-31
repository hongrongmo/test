<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY copy '<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text>'>
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:java="java:java.net.URLEncoder">
    <xsl:template name="LT">
        <xsl:param name="CY"/>
        <xsl:param name="PD"/>
        <xsl:param name="ID"/>
       
        <tr>
            <td valign="top">      
		<xsl:choose>
			<xsl:when test="boolean($LIT-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=HTML&amp;id={$ID}" target="_blank"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>              
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
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=PDF&amp;id={$ID}" target="_blank">View  </a>          
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=SAVEPDF&amp;id={$ID}">Save</a>
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
        <xsl:param name="CY"/>
        <xsl:param name="PD"/>
        <xsl:param name="ID"/>
        <xsl:param name="ZP"/>
       
        <tr>
            <td>
                <xsl:choose>
		    <xsl:when test="boolean($PAT-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=HTML&amp;id={$ID}" target="_blank"><xsl:value-of select="$CY" disable-output-escaping="yes"/></a>              
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
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=PDF&amp;id={$ID}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=SAVEPDF&amp;id={$ID}">Save</a>
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
                    <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=ZIP&amp;id={$ID}">Save</a>                   
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
       
        
        <tr>
            <td height="20">
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=HTML&amp;id={$ID}" target="_blank">
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
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=PDF&amp;id={$ID}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=SAVEPDF&amp;id={$ID}">Save</a>
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
        <tr>
            <td height="15">
            	<xsl:choose>
		    <xsl:when test="boolean($SHOW-HTML='true')">
			<a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=HTML&amp;id={$ID}" target="_blank">
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
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=PDF&amp;id={$ID}" target="_blank">View  </a>
                &#160;&#160;
                <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=SAVEPDF&amp;id={$ID}">Save</a>
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
                        <a class="SmBlueText" href="/engresources/servlet/ViewBulletin?EISESSION={$SESSION-ID}&amp;cType=ZIP&amp;id={$ID}">Save</a>                    
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
            <xsl:call-template name="LT">
                <xsl:with-param name="CY" select="CY"/>
                <xsl:with-param name="PD" select="PD"/>
                <xsl:with-param name="ID" select="ID"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template match="BL" mode="PT">
        <xsl:if test="@FORMAT='2'">
            <!--Target attribute specified.-->
            <xsl:call-template name="PT">
                <xsl:with-param name="CY" select="CY"/>
                <xsl:with-param name="PD" select="PD"/>
                <xsl:with-param name="ID" select="ID"/>
                <xsl:with-param name="ZP" select="ZP"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
