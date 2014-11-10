<?xml version="1.0" ?>
   <xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java html xsl"
>

<xsl:template match="HELP-TEMPLATE" name="HELP-TEMPLATE">
  <xsl:param name="SELECTED-DB"/>
  <xsl:param name="CH-ICON"/>

    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Robohelp.js"/>
    <script language="javascript">
    <xsl:comment>
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
    var newWin;
    function openTrainingHelp(url)
    {
      newWin=window.open(url,'NewWindow','status=yes,resizable,scrollbars=1,menubar=yes,addressbar=1,width=700,height=500');
      if (window.focus) {newWin.focus()}
      return false;
    }
    ]]>
    </xsl:text>
   // </xsl:comment>
   </script>

<center>
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top">
        <table border="0">
          <tr>
            <td colspan="3"><a class="taggroup_title">Help</a></td>
          </tr>
          <tr>
            <td colspan="3">&#160;</td>
          </tr>
          <tr>
            <td height="30" valign="top"><a CLASS="MedGreyTextTag"> Engineering Village help is available in the following formats:</a></td>
            <td height="30" >&#160;</td>
            <td height="30" valign="top"><a CLASS="MedGreyTextTag"> Training Materials: </a></td>
          </tr>
          <tr>
            <td>
              <table border="0">
                <tr>
                  <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.pdf','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"><img src="/static/images/RobohelpPDF.gif" border="0"/></a></td>
                  <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Flash/Engineering_Village_Help.htm','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"> <img src="/static/images/Flash.gif" border="0"/></a><img src="/static/images/s.gif" width="4" /></td>
                  <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.doc','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"><img src="/static/images/Word.gif" border="0"/></a></td>
                </tr>
                <tr>
                  <td  height = "1" valign="top"><img src="/static/images/s.gif" width="17" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.pdf','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">PDF</a><img src="/static/images/s.gif" width="18" height="7"/></td>
                  <td  height = "1" valign="top"><img src="/static/images/s.gif" width="13" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Flash/Engineering_Village_Help.htm','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">Flash</a><img src="/static/images/s.gif" width="5" height="7"/></td>
                  <td  height = "1" valign="top"><img src="/static/images/s.gif" width="14" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.doc','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">MS Word</a></td>
                </tr>
              </table>
              <a CLASS="MedGreyTextTag"> Chinese and Japanese versions of Engineering Village are also available:</a>
              <table border="0">
                <tr><td valign="top"><img width="8" height="7" src="/static/images/s.gif" border="0" /></td></tr>
                <tr><td valign="top"><xsl:if test="($CH-ICON='true')"><a href="/controller/servlet/Controller?CID=chinahelp&amp;database={$SELECTED-DB}"><img src="/static/images/Chinese.gif" border="0"/></a></xsl:if> <img src="/static/images/s.gif" width="30" height="7"/><a href="javascript: window.open('/engresources/help/EVJapaneseHelp.pdf','NewWindow','status=yes,resizable,scrollbars=1,menubar=yes,addressbar=1,width=700,height=500');void('');"><img src="/static/images/Japnes.gif" border="0"/></a></td></tr>
              </table>
            </td>
            <td>&#160;</td>
            <td valign="top">
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/userfiles/SourceLists/CPX%20JOURNAL%20LIST_%202010_0402.pdf')">Compendex Source List for Journals</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/userfiles/SourceLists/CPX%20MONO%20SOURCE%20LIST_%202010_0402.pdf')">Compendex Source List for all other sources</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/trainingpresentations')">Training Presentations</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/trainingpresentations')">Product Tours</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/newsletter')">Ei Newsletter</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/trainingsessionsschedule')">Training Session Schedules</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/node/31')">Podcasts</A><BR/>
              <A CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/bulletinboard')">Ask-An-Expert Bulletin Board</A><BR/>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</center>
</xsl:template>
</xsl:stylesheet>