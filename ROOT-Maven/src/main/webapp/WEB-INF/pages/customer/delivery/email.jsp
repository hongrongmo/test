<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search Results">

    <stripes:layout-component name="csshead">
    <link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
    </stripes:layout-component>

	<stripes:layout-component name="header">
	<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component> 
	
    <stripes:layout-component name="contents">
    
    <div id="delivery_content" class="marginL15">
    
	<ul class="errors" id="jserrors">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>

    <c:choose>
    <c:when test="${actionBean.confirm}">
    <br/>
    <p>Your email has been successfully sent to ${actionBean.to}</p>
    </c:when>
    <c:when test="${(empty actionBean.basket || actionBean.basket.basketSize eq 0) && (empty actionBean.docidlist) && (empty actionBean.folderid)}">
            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top" colspan="2"><h2>Email Records</h2></td>
            </tr>
            <tr><td valign="top" height="2" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top">
            <span CLASS="MedBlackText">
            You did not select any records to email. Please select records from the search results and try again.
            </span>
            </td>
            <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
            </tr>
            </table>    
    </c:when>
    <c:when test="${actionBean.context.eventName eq 'display' or actionBean.context.eventName eq 'submit'}">
    
	<stripes:form action="/delivery/email/submit.url" name="sendemail" method="POST" onsubmit="return validateForm(document.sendemail);">
    <stripes:hidden name="docidlist" />
    <stripes:hidden name="handlelist" />
    <stripes:hidden name="folderid" />
    <stripes:hidden name="database" />
    
	<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
		<tbody>
			<tr>
				<td valign="top" height="15" colspan="3"><img
					src="/static/images/spacer.gif" border="0"></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top" colspan="2"><h2>Email Records</h2></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top"><span class="MedBlackText">Enter the email address(es), separated by commas, where you would like to have your <b>${actionBean.count}</b> result<c:if test="${actionBean.count gt 1}">s</c:if> sent.
				</span></td>
				<td valign="top" width="10"><img
					src="/static/images/spacer.gif" border="0" width="10"></td>
			</tr>
			<tr>
				<td valign="top" height="10" colspan="3"><img
					src="/static/images/spacer.gif" border="0" height="10"></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top"><span class="MedBlackText">NOTE: Your
						selected records (to a maximum of 500) will be kept until your
						session ends. However, to delete them after this task:</span></td>
				<td valign="top" width="10"><img
					src="/static/images/spacer.gif" border="0" width="10"></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top"><span class="MedBlackText"><li
						style="margin-left: 20px;">Return to the Search results page
							and click Delete Selected Records, or</li></span></td>
				<td valign="top" width="10"><img
					src="/static/images/spacer.gif" border="0" width="10"></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top"><span class="MedBlackText"><li
						style="margin-left: 20px;">Go to the Selected records page
							and click Remove All, or</li></span></td>
				<td valign="top" width="10"><img
					src="/static/images/spacer.gif" border="0" width="10"></td>
			</tr>
			<tr>
				<td valign="top" width="20"><img
					src="/static/images/spacer.gif" border="0" width="20"></td>
				<td valign="top"><span class="MedBlackText"><li
						style="margin-left: 20px;">Click the End session link at the
							top of the page</li></span></td>
				<td valign="top" width="10"><img
					src="/static/images/spacer.gif" border="0" width="10"></td>
			</tr>
			<tr>
				<td valign="top" height="10" colspan="3"><img
					src="/static/images/spacer.gif" border="0" height="10"></td>
			</tr>
			<tr>
				<td valign="top" colspan="3"><table border="0" width="65%"
						cellspacing="0" cellpadding="0" align="center">
						<tbody>
							<tr>
								<td valign="top" width="100%" cellspacing="0" cellpadding="0"
									height="2" bgcolor="#3173B5" colspan="4"></td>
							</tr>
							<tr>
								<td valign="top" width="2" bgcolor="#3173B5"><img
									src="/static/images/spacer.gif" border="0" width="2"></td>
								<td valign="top" width="4"><img
									src="/static/images/spacer.gif" border="0" width="4"></td>
								<td valign="top">
										<table border="0" width="300" cellspacing="0" cellpadding="0">
											<tbody>
												<tr>
													<td valign="top" height="10" colspan="3"></td>
												</tr>
												<tr>
													<td valign="top" align="right"><span
														class="MedBlackText">Record output: </span></td>
													<td valign="top" width="6"><img
														src="/static/images/spacer.gif" border="0"
														width="6"></td>
													<td valign="top">
													<stripes:select name="displayformat">
														<stripes:option value="citation">Citation</stripes:option>
														<stripes:option value="abstract">Abstract</stripes:option>
														<stripes:option value="detailed">Detailed record</stripes:option>
													</stripes:select></td>
												</tr>
												<tr>
													<td valign="top" height="3" colspan="3"></td>
												</tr>
												<tr>
													<td valign="top" align="right"><span
														class="MedBlackText">To: </span></td>
													<td valign="top" width="6"><img
														src="/static/images/spacer.gif" border="0"
														width="6"></td>
													<td valign="top"><span class="MedBlackText"><stripes:text name="to" size="25"/></span></td>
												</tr>
												<tr>
													<td valign="top" height="3" colspan="3"></td>
												</tr>
												<tr>
													<td valign="top" align="right"><span
														class="MedBlackText">Your Email: </span></td>
													<td valign="top" width="6"><img
														src="/static/images/spacer.gif" border="0"
														width="6"></td>
													<td valign="top"><span class="MedBlackText"><stripes:text name="from" size="25"/></span></td>
												</tr>
												<tr>
													<td valign="top" height="3" colspan="3"></td>
												</tr>
												<tr>
													<td valign="top" align="right"><span
														class="MedBlackText">Subject: </span></td>
													<td valign="top" width="6"><img
														src="/static/images/spacer.gif" border="0"
														width="6"></td>
													<td valign="top"><span class="MedBlackText"><stripes:text name="subject" size="25"/></span></td>
												</tr>
												<tr>
													<td valign="top" height="3" colspan="3"></td>
												</tr>
												<tr>
													<td valign="top" align="right"><span
														class="MedBlackText">Message: </span></td>
													<td valign="top" width="6"><img
														src="/static/images/spacer.gif" border="0"
														width="6"></td>
													<td valign="top"><span class="MedBlackText"><stripes:textarea rows="3" cols="20" name="message"></stripes:textarea></span></td>
												</tr>
												<tr>
													<td valign="top" height="10"></td>
												</tr>
												<tr>
													<td valign="top" colspan="2">&nbsp;</td>
													<td valign="top"><span class="SmBlackText"><input
															type="submit" name="submit" value="Send Email"></span></td>
												</tr>
												<tr>
													<td valign="top" height="10"></td>
												</tr>
											</tbody>
										</table>
								</td>
								<td valign="top" width="2" bgcolor="#3173B5"><img
									src="/static/images/spacer.gif" border="0" width="2"></td>
							</tr>
							<tr>
								<td valign="top" width="100%" cellspacing="0" cellpadding="0"
									height="2" bgcolor="#3173B5" colspan="4"></td>
							</tr>
						</tbody>
					</table></td>
			</tr>
		</tbody>
	</table>
       </stripes:form>		
	</c:when>
	</c:choose>
	
    </div>	

    </stripes:layout-component>

	<stripes:layout-component name="footer">
        <div class="hr" style="margin:20px 0 7px 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr/></div>
	    <jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
	</stripes:layout-component> 

    <stripes:layout-component name="jsbottom_custom">
            <SCRIPT LANGUAGE="JavaScript">

            // This function basically validates the data entered in Email form fields.
            // typically it checks for a valid mail id and not null conditions
            function validateForm(sendemail) {

                var milli = (new Date()).getTime() ;

                sendemail.from.value = sendemail.from.value.replace(/\s+/g,"");
                sendemail.to.value = sendemail.to.value.replace(/\s+/g,"");

                var from = sendemail.from.value;
                var to = sendemail.to.value;
                to = to.replace(/;/g,",");

                var subject = sendemail.subject.value;
                
                var folder_id = "";
                
                if(sendemail.folderid){
                	folder_id = sendemail.folderid.value;
                }

                if (to.length == 0) {
                   window.alert("You must enter the recipients email address");
                   sendemail.to.focus();
                   return false;
                }
                var booleanTo=validateToRecipients(to);
                if(booleanTo==false){
                    alert ("Invalid Email address");
                    sendemail.to.focus();
                    return false;
                }

                if (from.length == 0) {
                    alert("You must enter the Sender email address");
                    sendemail.from.focus();
                    return false;
                }
                
                 var fromSplit = from.replace(/;/g,",");
                 fromSplit = fromSplit.split(/,/);
                 
                 if(fromSplit.length>1){
                    alert ("You may only enter one email address in the Your Email field.");
                    sendemail.from.focus();
                    return false;
                }
                
                var booleanFrom=validateEmail(from);
                if(booleanFrom==false){
                    alert ("Invalid Email address");
                    sendemail.from.focus();
                    return false;
                }

                if (subject.length == 0) {
                    alert("Subject Field cannot be blank");
                    sendemail.subject.focus();
                    return (false);
                }

                var database = sendemail.database.value;
                //var displayformat = sendemail.displayformat.value;
                var myindex  = sendemail.usrseldisplayformat.selectedIndex;
                displayformat = sendemail.usrseldisplayformat.options[myindex].value;
                var selectedset = sendemail.selectedset.value;
                var searchquery = sendemail.searchquery.value;

                var hiddensize = sendemail.elements.length;
                var docidstring = "&docidlist=";
                var handlestring = "&handlelist=";
                var dbstring = "&dblist=";
                // logic to construct docidList,HandleList and databaseList
                for(var i=0 ; i < hiddensize ; i++)
                {
                    var nameOfElement = sendemail.elements[i].name;
                    var valueOfElement = sendemail.elements[i].value;

                    if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
                    {
                        var subdocstring = valueOfElement+",";
                        docidstring +=subdocstring;
                    }
                    if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
                    {
                        var subhandlestring = valueOfElement+",";
                        handlestring +=subhandlestring;
                    }
                    if((nameOfElement.search(/DATABASE/)!=-1) && (valueOfElement != ""))
                    {
                        var subdbstring = valueOfElement+",";
                        dbstring +=subdbstring;
                    }
                }

                if(typeof(sendemail.docidlist) != 'undefined') {

                    url = "/delivery/email/submit.url?displayformat="+displayformat+
                    "&timestamp="+milli+
                    "&docidlist="+sendemail.docidlist.value+
                    "&handlelist="+sendemail.handlelist.value;
                }
                else {

                    if(sendemail.saved_records.value != "true") {
                        url = "/delivery/email/submit.url?displayformat="+displayformat+
                        "&timestamp="+milli;
                    } else {
                        url = "/delivery/email/submit.url?displayformat="+displayformat+
                        "&folderid="+folder_id+
                        "&timestamp="+milli;
                    }
                }
                //url = url + "&to="+sendemail.to.value;
                //url = url + "&from="+sendemail.from.value;
                //url = url + "&subject="+sendemail.subject.value;
                //url = url + "&message="+sendemail.message.value;
            
                sendemail.to.value = sendemail.to.value.replace(/;/g,",");
                sendemail.action = url;

                //window.open(url);
                //window.close();

                return (true);

            } // end function - validateForm

            // This function basically validates the data entered in TO Field for Multiple receipients
            function validateToRecipients(toEmail)
            {
                var result = true;
                receipients = toEmail.split(/,/);
                
                for(var i=0; i < receipients.length; i++)
                {
                  if(!echeck(receipients[i])){
                    return false;
                  }
                  result = result || validateEmail(email);
                }
                return result;
            }

            // This function basically does email validation(ie @ . and special characters)
            function validateEmail(email)
            {
              var splitted = email.match("^(.+)@(.+)$");
              if(splitted == null) return false;
              if(splitted[1] != null )
              {
                var regexp_user=/^\"?[\w-_\.]*\"?$/;
                if(splitted[1].match(regexp_user) == null) return false;
              }
              if(splitted[2] != null)
              {
                var regexp_domain=/^[\w-\.]*\.[A-Za-z]{2,4}$/;
                if(splitted[2].match(regexp_domain) == null)
                {
                  var regexp_ip =/^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
                  if(splitted[2].match(regexp_ip) == null) return false;
                }
                return true;
              }
              return false;
            }
            
            function echeck(str) {

                var at="@"
                var dot="."
                var lat=str.indexOf(at)
                var lstr=str.length
                var ldot=str.indexOf(dot)
                if (str.indexOf(at)==-1){
                   return false
                }
        
                if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
                   return false
                }
        
                if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
                    return false
                }
        
                 if (str.indexOf(at,(lat+1))!=-1){
                    return false
                 }
        
                 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
                    return false
                 }
        
                 if (str.indexOf(dot,(lat+2))==-1){
                    return false
                 }
                
                 if (str.indexOf(" ")!=-1){
                    return false
                 }
        
                 return true                    
            }
            
            </SCRIPT>
    </stripes:layout-component>


</stripes:layout-render>