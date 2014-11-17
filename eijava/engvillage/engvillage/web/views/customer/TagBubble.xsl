<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:ti="java:org.ei.query.base.HitHighlightFinisher"
  exclude-result-prefixes="java html ti xsl">

<xsl:template match="TAG-BUBBLE">

    <xsl:variable name="EDIT-URL">
      <xsl:value-of select="URLS/EDIT-TAG-URL"/>
    </xsl:variable>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[
      <script language="javascript">
        var tageditTableBody;
        var tageditTable;
        var editDiv;
        var editInputField;
        var editflag = "1";

        function initBubbleVars()
        {
          editDiv = document.getElementById("editfield");
          editInputField = document.getElementById("editfield");
          tageditTableBody = document.getElementById("tagedit_table_body");
          tageditTable = document.getElementById("tagedit_table");
        }

        function flipimg()
        {
          if (editflag == "1")
          {
            drawEditTags();
            document.editlink.src="/engresources/images/tagedit_minus.gif";
            editflag = "2";
          }
          else if (editflag == "2")
          {
            clearEdit();
            document.editlink.src="/engresources/images/tagedit_plus.gif";
            editflag = "1";
          }
        }

        function drawEditTags()
        {
          initBubbleVars();
          setOffsetEdit();
          drawEdit();
          return false;
        }
      </script>
    ]]>
    </xsl:text>

    <script language="javascript">
      <xsl:value-of select="/PAGE/EDIT-TAGS/JS"/>
    </script>

    <xsl:text disable-output-escaping="yes">
    <![CDATA[
        <script language="javascript">

          function clearEdit()
          {
            if (tageditTableBody != null )
            {
              var gi = tageditTableBody.childNodes.length;
              for (var i = gi - 1; i >= 0 ; i--)
              {
                tageditTableBody.removeChild(tageditTableBody.childNodes[i]);
              }
              editDiv.style.border = "none";
            }
          }

          function setOffsetEdit()
          {
            var gend = editInputField.offsetWidth;
            var gleft = calculateOffsetLeftGroup(editInputField)+gend;
            var gtop = calculateOffsetTopGroup(editInputField);
            editDiv.style.border = "black 0px solid";
            editDiv.style.left = gleft + "px";
            editDiv.style.top = gtop + "px";
            tageditTable.style.width = "160px";
          }

          function calculateOffsetLeftGroup(gfield)
          {
            return calculateOffsetGroup(gfield, "offsetLeft");
          }
          function calculateOffsetTopGroup(gfield)
          {
            return calculateOffsetGroup(gfield, "offsetTop");
          }
          function calculateOffsetGroup(gfield, gattr)
          {
            var goffset = 0;
            while(gfield)
            {
              goffset += gfield[gattr];
              gfield = gfield.offsetParent;
            }
            return goffset;
          }



        function addNewTag()
        {
            if (document.tagbubble.tagname.value != null)
            {
                var avalue = document.tagbubble.tagname.value;
		        var arr = avalue.split(",");
                var k = 0;
			    for (k = 0; k < arr.length ; k++)
			    {
			        if (arr[k].length > 30)
				    {
			            alert("Tag title should not exceed 30 characters : "+arr[k]);
				        return false;
				    }
			    }
			}

            var url = document.tagbubble.addtagurl.value;
            var tag = encodeURIComponent(document.tagbubble.tagname.value);
            var scopelength = document.tagbubble.scope.length;
            var tagdoc = document.tagbubble.tagdoc.value;
            var personalization = document.tagbubble.personalization.value;
            var personallogin = document.tagbubble.personallogin.value;
            var nexturl = document.tagbubble.nexturl.value;
            var notifygroup = "off";
            var i=0;
            var scopevalue = null;
            var groupID = null;

            if (typeof(document.tagbubble.notifygroup) != "undefined")
            {
              // if a checkbox with no value always has the value of 'on'
              // even if it is not checked - so test if the box is checked first
              if(document.tagbubble.notifygroup.checked)
              {
                notifygroup = document.tagbubble.notifygroup.value;
              }
            }
            for(i=0; i < scopelength ; i++)
            {
              if(document.tagbubble.scope[i].selected)
              {
                scopevalue = document.tagbubble.scope[i].value;
                break;
              }
            }
            if (tag == null || tag == "")
            {
              return false;
            }
            if(personalization == 'true' )
            {
              url = "/controller/servlet/Controller?"+ url +
              "&tag="+tag+
              "&tagdoc="+tagdoc+
              "&notifygroup="+notifygroup+
              "&scope="+scopevalue;
              document.location = url;
            }
            else
            {

		nexturl = url +
		"&tag="+tag+
		"&tagdoc="+tagdoc+
		"&notifygroup="+notifygroup+
		"&scope="+scopevalue;
		personallogin = personallogin +
		"&nexturl="+escape(nexturl)+
		"&backurl="+escape(nexturl);

		document.location = personallogin;
            }
          }

          function findGroups()
          {

            var personallogin = document.tagbubble.personallogin.value;

            clearEmail();
            var groups = document.forms["tagbubble"].scope.value;
            if (groups != "1" && groups != "2" && groups != "4" && groups != "6")
            {
              drawEmail();
            }
            else if(groups == "6")
            {
            	var nexturl = document.tagbubble.nexturl.value;
            	document.location = personallogin+
                "&nexturl="+nexturl+
                "&backurl="+nexturl;
            }

            return false;
          }

          function setOffsetsGroup()
          {
            var ginputField = document.getElementById("scope");
            var gcompleteDiv = document.getElementById("gpopup");

            /*var gend = ginputField.offsetWidth;
            var gleft = calculateOffsetLeftGroup(ginputField)+gend;
            var gtop = calculateOffsetTopGroup(ginputField);
            gcompleteDiv.style.border = "black 0px solid";
            gcompleteDiv.style.left = gleft + "px";
            gcompleteDiv.style.top = gtop + "px";
            //gnameTable.style.width =  "";
            */
          }

          function calculateOffsetLeftGroup(gfield)
          {
            return calculateOffsetGroup(gfield, "offsetLeft");
          }

          function calculateOffsetTopGroup(gfield)
          {
            return calculateOffsetGroup(gfield, "offsetTop");
          }

          function calculateOffsetGroup(gfield, gattr)
          {
            var goffset = 0;
            while(gfield)
            {
              goffset += gfield[gattr];
              gfield = gfield.offsetParent;
            }
            return goffset;
          }

          function drawEmail()
          {
            var ip = document.createElement("input");
            ip.setAttribute("type","checkbox");
            ip.setAttribute("name","notifygroup");
            ip.setAttribute("id","notifygroup");
            ip.style.verticalAlign = "middle";

            var ipLabel = document.createElement("label");
            ipLabel.setAttribute("name","notifylabel");
            ipLabel.htmlFor="notifygroup";
            ipLabel.style.verticalAlign = "middle";
            ipLabel.appendChild(document.createTextNode("E-mail Group"));

            var parentDiv = document.getElementById("gpopup");
            var oldSpan = parentDiv.childNodes[0];

            var newSpan = document.createElement("span");
            newSpan.setAttribute("name","notify_option");
            newSpan.setAttribute("id","notify_option");
            newSpan.style.fontSize = "11px";
            newSpan.appendChild(ip);
            newSpan.appendChild(ipLabel);

            parentDiv.replaceChild(newSpan,oldSpan);

            return false;
          }

          function clearEmail()
          {
            var parentDiv = document.getElementById("gpopup");
            var oldSpan = parentDiv.childNodes[0];
            var newSpan = document.createElement("span");
            newSpan.setAttribute("id","notify_option");

            parentDiv.replaceChild(newSpan,oldSpan);
          }



        function validateEdit()
        {
          	var j = 0;

			for (j = 0 ; j < document.tagedit.length ; j++)
			{
				var name = document.tagedit.elements[j].name;
				if (name.substring(0,4) == "Edit")
				{
					var evalue = document.tagedit.elements[j].value;
					var arr = evalue.split(",");
					var k = 0;
					for (k = 0; k < arr.length ; k++)
					{
						if (arr[k].length > 30)
						{
							alert("Tag title should not exceed 30 characters : "+arr[k]);
							return false;
						}
					}
				}

			}
			return true;
		}


        </script>
    ]]>
    </xsl:text>

    <xsl:variable name="ADDTAGURL">
      <xsl:value-of select="URLS/ADD-TAG-URL"/>
    </xsl:variable>
    <xsl:variable name="BACKURL">
      <xsl:value-of select="/PAGE/BACKURL"/>
    </xsl:variable>
    <xsl:variable name="PLOGIN">
      <xsl:value-of select="LOGGED-IN"/>
    </xsl:variable>
    <xsl:variable name="TAGDOC">
      <xsl:value-of select="TAG-DOCID"/>
    </xsl:variable>
    <xsl:variable name="PERSONALIZATION">
      <xsl:value-of select="//PERSONALIZATION"/>
    </xsl:variable>
    <xsl:variable name="PERSONALLOGIN">/controller/servlet/Controller?CID=personalLoginForm&amp;database=<xsl:value-of select="/PAGE/DBMASK"/>&amp;displaylogin=true</xsl:variable>
    <xsl:variable name="DOC-ID">
      <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DOC-ID" />
    </xsl:variable>
    <xsl:variable name="DATABASE-ID">
      <xsl:value-of select="//EI-DOCUMENT/DOC/DB/ID" />
    </xsl:variable>
    <xsl:variable name="ENCODED-DOCID">
          <xsl:value-of select="java:encode($DOC-ID)" />
    </xsl:variable>
    <xsl:variable name="TITLE1">
      <xsl:value-of select="java:encode(ti:removeMarkup(//EI-DOCUMENT/TI))" />
    </xsl:variable>
    <xsl:variable name="TITLE2">
      <xsl:value-of select="java:encode(ti:removeMarkup(//FIELD[@ID='TI']/VALUE))" />
    </xsl:variable>
    <xsl:variable name="TITLE3">
      <xsl:value-of select="java:encode(ti:removeMarkup(//EI-DOCUMENT/BTI))" />
    </xsl:variable>
    <xsl:variable name="TITLE">
      <xsl:if test="not($TITLE1='null')">
        <xsl:value-of select="$TITLE1" />
      </xsl:if>
      <xsl:if test="not($TITLE2='null')">
        <xsl:value-of select="$TITLE2" />
      </xsl:if>
      <xsl:if test="not($TITLE3='null')">
        <xsl:value-of select="$TITLE3" />
      </xsl:if>
    </xsl:variable>


 <div  style="width:215px;" id="citedby_box"/>
        <div>
        <table><tr><td><img src="/engresources/images/s.gif" height="4"/></td></tr></table>
        </div>

      <div class="t" style="width:215px;">
      <div class="b">
      <div class="l">
      <div class="rc">
      <div class="bl">
      <div class="br">
      <div class="tl">
      <div class="trc">
        <table border="0" style="margin:0px; padding:0px; width:100%">
          <tr><td colspan="3"><img src="/engresources/images/s.gif" height="4"/></td></tr>
          <tr>
            <td><img src="/engresources/images/s.gif" border="0" width="1"/></td>
            <td align="left" bgcolor="#FFFFFF">
              <form style="margin:0px; padding:0px;" name="tagbubble"  action="/controller/servlet/Controller?{$EDIT-URL}" METHOD="POST" onSubmit="javascript:addNewTag();return false;">
                <input type = "hidden"  name="nexturl"  value ="{$BACKURL}" />
                <input type = "hidden"  name="addtagurl"  value ="{$ADDTAGURL}" />
                <input type = "hidden"  name="personalization"  value ="{$PLOGIN}" />
                <input type = "hidden"  name="personallogin" value ="{$PERSONALLOGIN}" />
                <input type = "hidden"  name="tagdoc" value ="{$TAGDOC}" />
             

                <a title="Separate each tag with a comma: nanotech, carbon nanotubes, Environmental 101" class="tag_title"><label for="tagname">Add a tag</label></a>&nbsp;
                 <a title="Separate each tag with a comma: nanotech, carbon nanotubes, Environmental 101" href="javascript:makeUrl('Tagging_a_record.htm')">
			<img src="/engresources/images/blue_help.gif" alt="Separate each tag with a comma: nanotech, carbon nanotubes, Environmental 101" border="0"/>
	         </a>
                <br/>

                <br/>
                <select style="vertical-align: middle;" size="1" name="scope" id="scope" class="SmBlackText" onchange="return findGroups()">
                  <option value="1">Public</option>
                  <option value="2">Private</option>
                  <option value="4">My Institution</option>
                  <xsl:if test="$PLOGIN=''">
		 	<option value="6">Login for groups</option>
                  </xsl:if>
                  <xsl:for-each select="TGROUPS/TGROUP">
                    <option>
                      <xsl:attribute name="value"><xsl:value-of select="GID"/></xsl:attribute>
                      <xsl:value-of select="GTITLE"/>
                    </option>
                  </xsl:for-each>
                </select>
                <div style="margin:0 0 2 0; padding:0px; border:0px solid black;" id="gpopup">
                  <span class="SmBlackText" id="notify_option"></span>
                </div>
                <input style="vertical-align: middle; margin-right:4px; padding-left:5px;" class="SmBlackText" autocomplete="off" size="19" name="tagname" id="tagname"  onkeyup="findTags();" />
                <input style="vertical-align: middle;" alt="Add Tag" type="image" src="/engresources/images/addtag_button.gif" border="0" width="54" height="19"/>
                <!-- AJAX tag 'suggestion' dropdown -->
                <div style="position:absolute;" id="popup" >
                  <table id="name_table" bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="0" class="SmBlackText">
                    <tbody class="SmBlackText" id="name_table_body"></tbody>
                  </table>
                </div>
              </form>
            </td>
            <td><img src="/engresources/images/s.gif" border="0" width="1"/></td>
          </tr>

          <!-- dividing blue line only if tags are present or logged in with editable tags present -->
          <xsl:if test="(TAGS/TAG) or (($PERSONALIZATION='true') and (/PAGE/EDIT-TAGS/EDIT-TAG))">
            <tr><td>&#160;</td><td><div class="idev">&#160;</div></td><td>&#160;</td></tr>
          </xsl:if>

          <!-- display tags here. Test for TAG children first to avoid empty space -->
          <xsl:if test="(TAGS/TAG)">
            <tr>
              <td>&#160;</td>
              <td>
                <div class="im">
                  <a class="MedBlackText"><xsl:apply-templates select="TAGS"/></a>
                </div>
              </td>
              <td>&#160;</td>
            </tr>
          </xsl:if>

          <!-- edit tag groups starts here. Test for Editable, personal children first to avoid empty space  -->
          <xsl:if test="($PERSONALIZATION='true') and (/PAGE/EDIT-TAGS/EDIT-TAG)">
            <tr>
              <td>&#160;</td>
              <td>
                <form style="margin:0px; padding:0px;" name="tagedit" action="/controller/servlet/Controller?{$EDIT-URL}" METHOD="POST" onSubmit="javascript:return validateEdit();">
                  <input type="hidden"  name="tagdoc" value ="{$TAGDOC}" />
                  <input type="hidden" name="editfield" id="editfield"/>
                  <xsl:for-each select="/PAGE/EDIT-TAGS/EDIT-TAG" >
                    <input type="hidden">
                      <xsl:attribute name="name">
                        <xsl:value-of select="EDIT-HFIELD-NAME"/>
                      </xsl:attribute>
                      <xsl:attribute name="id">
                        <xsl:value-of select="EDIT-SCOPE" />
                      </xsl:attribute>
                      <xsl:attribute name="value">
                        <xsl:value-of select="EDIT-TAGS-NAMES" />
                      </xsl:attribute>
                    </input>
                  </xsl:for-each>
                  <div class="im">
                    <xsl:if test="($PERSONALIZATION='true')">
                      <xsl:if test="(/PAGE/EDIT-TAGS/SET-EDIT)">
                        <a class="SmBlueText" href="self" onclick="javascript:flipimg(); return false;">Edit my tags<img name="editlink" src="/engresources/images/tagedit_plus.gif"  border="0" /></a>
                      </xsl:if>
                    </xsl:if>
                    <table style="margin:0px; padding:0px; border:0px black solid; width:100%" id="tagedit_table">
                      <tbody id="tagedit_table_body"/>
                    </table>
                  </div>
                </form>
              </td>
              <td>&#160;</td>
            </tr>
          </xsl:if>
          <!-- dividing blue line -->
          <tr><td>&#160;</td><td><div class="idev">&#160;</div></td><td>&#160;</td></tr>
          <xsl:variable name= "LOCATION">http://<xsl:value-of select="//SERVER-NAME"/>/controller/servlet/Controller?CID=blogDocument&amp;MID=<xsl:value-of select="$ENCODED-DOCID"/>&amp;DATABASE=<xsl:value-of select="$DATABASE-ID"/></xsl:variable>
          <xsl:variable name="ENCODED-LOCATION">
            <xsl:value-of select="java:encode($LOCATION)"/>
          </xsl:variable>
          <!--<xsl:variable name="COLLABDATA">
            <xsl:value-of select="/PAGE/COLLABLINKDATA"/>
          </xsl:variable>-->
          <tr>
            <td colspan="3">
            <!--  <div style="display:inline;" class="im"><a href="http://www.2collab.com/bookmark/addremote?url={$ENCODED-LOCATION}&amp;title={$TITLE}{$COLLABDATA}" title="Bookmark and share in 2collab (opens in new window)" class="LgBlueLink" onclick="javascript:window.open(this.href); return false;"><img src="/engresources/images/2collab_logo.gif" align="absmiddle" border="0" width="37" height="24" />Add to 2collab</a></div>-->
              <div style="display:inline;" class="im"><a title="Save this page to del.icio.us" class="LgBlueLink" href="http://del.icio.us/post" onclick="javascript:window.open('http://del.icio.us/post?v=4&amp;noui&amp;jump=close&amp;url={$ENCODED-LOCATION}&amp;title={$TITLE}', 'delicious', 'toolbar=no,width=700,height=400'); return false;"><img src="/engresources/images/delicious.small.gif" align="absmiddle" border="0" width="10" height="10" />del.icio.us</a></div>
            </td>
          </tr>
          <!-- edit form ends here -->
          <tr><td colspan="3"><img src="/engresources/images/s.gif" height="4"/></td></tr>
        </table>
      </div></div></div></div></div></div></div></div>

  </xsl:template>

  <xsl:template match="TAGS">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="TAG">

    <xsl:variable name= "SCOPE">
      <xsl:value-of select="SCOPE"/>
    </xsl:variable>

    <xsl:variable name="SCO">
      <xsl:choose>
        <xsl:when test="($SCOPE = '3')"><xsl:value-of select="$SCOPE"/>:<xsl:value-of select="TGROUP/GID"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="$SCOPE"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name= "ENCODED-TAGNAME">
      <xsl:value-of select="java:encode(TAGNAME)"/>
    </xsl:variable>

    <xsl:variable name= "BG">
      <xsl:choose>
        <xsl:when test="($SCOPE='1')">#002D68</xsl:when>
        <xsl:when test="($SCOPE='2')">#A3B710</xsl:when>
        <xsl:when test="($SCOPE='4')">#A56500</xsl:when>
        <xsl:when test="($SCOPE='3')"><xsl:value-of select="TGROUP/COLOR/CODE"/></xsl:when>
      </xsl:choose>
    </xsl:variable>

    <a class="TagSimple" onmouseover="style.backgroundColor='#CCCCCC'" onmouseout="style.backgroundColor='#FFFFFF'" href="/controller/servlet/Controller?CID=tagSearch&amp;searchtype=TagSearch&amp;searchtags={$ENCODED-TAGNAME}&amp;scope={$SCO}">
    <font color="{$BG}"><xsl:value-of select="TAGNAME"  disable-output-escaping="yes" /></font>
    </a>&#160;
  </xsl:template>

</xsl:stylesheet>
