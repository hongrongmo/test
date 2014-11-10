<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Folders">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_folder.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="folders" class="paddingL15">
		<h1>Folders</h1>
		<div class="hr" style="height: 2px; margin-right: 7px"><hr/></div>

	<div style="border: 2px solid #ababab;min-width: 500px;margin: 10px 0 10px 10px;width:60%">
	<c:if test="${actionBean.foldercount > 0}" >
    <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
    <table border="0"  cellspacing="0" cellpadding="0" id="folder" width="100%">
    <tr><td>&nbsp;</td></tr>
      <!-- Spacer Row / Grey Border / DO NOT CHANGE -->
      <tr>
        <!-- Spacer Cel / Grey Border / DO NOT CHANGE -->
        <td valign="top">
          <!-- Inner Template Table / DO NOT CHANGE -->
          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <!-- Spacer Row / White Background / DO NOT CHANGE -->
            <tr>
              <!-- Indentation Cel / DO NOT CHANGE -->
              <!-- Content Goes Here -->
              <td colspan="3">

                  <!-- Content Goes Here -->
                  <table border="0" cellspacing="0" cellpadding="0" width="100%" >

                  	<FORM name="folderslist">

                      <!-- iterate through the loop to display folder names -->
                      <c:forEach items="${actionBean.folderlist}" var="folder">
                               <tr><td valign="top" height="4" colspan="10"><img src="/static/images/spacer.gif" height="4"/></td></tr>
                    			<tr>
                    				<td valign="top" width="1%" style="padding-left:20px"><img src="/static/images/spacer.gif"/></td>
                    				<td valign="top" width="1%"><img src="/static/images/SavetoFolder.png"/></td>
                    				<td valign="top" width="2%" ><img src="/static/images/spacer.gif"/></td>
                    				<td valign="top" >
                    					<b>${folder.folderName}</b>
                    					<input type="hidden" name="FOLDER-NAME" value="${folder.folderName}">
                    				</td>
                    				<td valign="top" width="10%" ><img src="/static/images/spacer.gif"/></td>
                    				<td valign="top" >
                    					<a class="viewfolder" title="View folder"  href="/selected/citationfolder.url?CID=viewCitationSavedRecords&folderid=${folder.folderID}">View Folder</a>
                    				</td>
                    				<td valign="top" width="5%" ><img src="/static/images/spacer.gif"/></td>
                    				<td valign="top" >
                    					<a class="renamefolder" title="Rename folder"  href="/personal/folders.url?rename=t&folderid=${folder.folderID}&oldfoldername=${folder.encFolderName}">
                    						Rename Folder
                    					</a>
                    				</td>
                    				<td valign="top" width="5%" ><img src="/static/images/spacer.gif"/></td>
                    				<td valign="top" >
                    					<a id=deletefolderlink class="deletefolder" title="Delete folder"  href="/personal/folders.url?delete=t&folderid=${folder.folderID}">Delete Folder</a>
                    				</td>
                    			</tr>

                    	</c:forEach>
                    	</FORM>
                    	</table>
<!-- Content Goes Here -->
              </td>
            </tr>
            <!-- Sopacer Row Below Content / DO NOT CHANGE -->
            <tr><td valign="top" colspan="4" height="2">&nbsp;</td></tr>
          </table>
        </td>

      </tr>

    </table>
    <c:if test="${actionBean.foldercount < 3}" >
	<div style="border: 1px solid #ababab;"></div>
	</c:if>
	</c:if>
	<c:if test="${actionBean.foldercount < 3}" >


    <stripes:form name="folder" id="folderform" action="/personal/folders.url?CID=addPersonalFolder&database=${actionBean.database}" method="POST">
    <stripes:hidden name="foldercount" id="foldercount"/>

    <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
    <table border="0"  cellspacing="0" cellpadding="0" >
    <tr><td>&nbsp;</td></tr>
      <!-- Spacer Row / Grey Border / DO NOT CHANGE -->
      <tr>
        <!-- Spacer Cel / Grey Border / DO NOT CHANGE -->
        <td valign="top">
          <!-- Inner Template Table / DO NOT CHANGE -->
          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <!-- Spacer Row / White Background / DO NOT CHANGE -->
            <tr>
              <!-- Indentation Cel / DO NOT CHANGE -->
              <!-- Content Goes Here -->
              <td colspan="3">
                    	 <table border="0" cellspacing="0" cellpadding="0" width="100%" >
                    	<!-- if the number of folders are less than three display the form to create new folder -->


                    		<tr>
                    			<td valign="top"  colspan="10" style="padding-left:25px;padding-bottom:5px">
                    				With your Personal Account, you can create up to three folders in which to save selected records.<br/> Each folder can contain up to 50 records. To create a folder, please enter a folder name:
                    			</td>
                    		</tr>
                    		<tr>
                    		    <td valign="top"  colspan="10" style="padding-left:25px">
                    				<stripes:text name="foldername" id="foldername" size="37" maxlength="32"/>&#160;
                            &#160;<stripes:submit class="folderbtn" id="addfolder" name="add" value="Save" title="Save folder" />
                            &#160;<stripes:submit id="cancel" name="cancel" value="Cancel" title="Cancel action"/>
  				                  <!-- <input type="button"  name="reset" value="Reset" alt="Reset" onclick="javascript:reset();return false;"/>  -->&#160; &#160;
                    			</td>
                    		</tr>

                    	<!-- end of folder creation form -->

                  </table>

                <!-- Content Goes Here -->
              </td>
            </tr>
            <!-- Spacer Row Below Content / DO NOT CHANGE -->
            <tr><td valign="top" colspan="4" height="2">&nbsp;</td></tr>
          </table>
        </td>

      </tr>

    </table>
    </stripes:form>
    </c:if>
    </div>

    </div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/Folders.js?V=1"></SCRIPT>
	    <script type="text/javascript">
	    GALIBRARY.createWebEvent('Folders', 'Home');
	    </script>
	</stripes:layout-component>

</stripes:layout-render>
