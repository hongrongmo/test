<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Linda Hall Library Document Request">

    <stripes:layout-component name="header">
    <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="modal_dialog"></stripes:layout-component>

    <stripes:layout-component name="contents">
    
    <stripes:form name="lhlrequest" method="post" action="/lindahall/orderform.url" onsubmit="return validForm(lhlrequest)">

    <stripes:hidden name="docid"/>
    <stripes:hidden name="database"/>

	<div class="marginL10">
        <ul class="errors" id="jserrors">
        <stripes:errors>
             <stripes:errors-header></stripes:errors-header>
             <li><stripes:individual-error/></li>
             <stripes:errors-footer></stripes:errors-footer>
        </stripes:errors>
        </ul>

		<h2>Linda Hall Library Document Request</h2>

		<p CLASS="MedBlackText">You have selected the following document
			to order from Linda Hall Library. You must complete the order form
			below to submit the request.</p>

		<div class="contentMain" style="margin-right: 10px !important;">
			<div class="contentShadow">
				<div class="padding10">
                <c:import var="lhlxslt" url="document.jsp" />
                <x:transform  xml="${lhldoc}" xslt="${lhlxslt}"/>
				</div>
			</div>
		</div>
	
	     <table border="0" width="99%" cellspacing="0" cellpadding="0">
	         <tr><td valign="top" colspan="3"><span CLASS="MedBlackText"><b>Document charges</b>
	         <br/>Document delivery: </p><br/>
	         <li><span CLASS="MedBlackText">First 50 pages:  $12.00 for academic institutions; $16.00 for nonacademic institutions</span></li>
	         <li><span CLASS="MedBlackText">Add $0.25 per page for each page over 50.</span></li><br/>
	         <span CLASS="MedBlackText">Book loan:</span><br/>
	         <li><span CLASS="MedBlackText">$12.00 for academic institutions; $16.00 for nonacademic institutions</span></li>
	         <!-- <li><span CLASS="MedBlackText">You must select FedEx delivery.</span></li> -->
	         </td></tr>
	         <tr><td valign="top" colspan="3" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
	         <tr><td valign="top" colspan="3"><span CLASS="MedBlackText"><b>Copyright charges</b></span><br/>
	         <span CLASS="MedBlackText">A copyright payment must be made for each request:</span>
	         <li><span CLASS="MedBlackText">$7.00 for documents with an actual copyright royalty fee is less than $15.00</span></li>
	         <li><span CLASS="MedBlackText">If the actual royalty fee is $15.00 or more, you will be charged the actual royalty fee.</span></li>
	         </td></tr>
	         <tr><td valign="top" height="20" colspan="2"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
	     </table>
	
	     <table border="0" cellspacing="0" cellpadding="0" class="lhl_table">
           <tbody>

	        <tr>
	            <th class="lhl_table_title lhl_table_lhc" colspan="3" width="330px"><b>Delivery method (choose one)</p></th>
	            <th class="lhl_table_title lhl_table_rhc" width="120px">Cost</th>
	        </tr>
	
	        <tr>
	           <td class="lhl_table_text" colspan="4"><b>Electronic delivery:</b></td>
	        </tr>
	        <tr>
	           <td class="lhl_table_input"><stripes:radio name="method" value="Email"/></td>
	           <td class="lhl_table_text" width="1%">Email - address required:</td>
	           <td class="lhl_table_text"><stripes:text name="deliveryEmail" size="25" onclick="document.delivery.method[0].checked=true"/></td>
               <td class="lhl_table_rhc">No charge</td>
	        </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="Ariel"/></td>
               <td class="lhl_table_text" width="1%">Ariel - address required</td>
               <td class="lhl_table_text"><stripes:text name="ariel" size="25" onclick="document.delivery.method[1].checked=true"/></td>
               <td class="lhl_table_rhc">No charge</td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>
            
            <tr>
               <td class="lhl_table_text" colspan="4"><b>North America delivery:</b></td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="Fax"/></td>
               <td class="lhl_table_text" colspan="2">Fax</td>
               <td class="lhl_table_rhc">No charge</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="First class mail"/></td>
               <td class="lhl_table_text" colspan="2">First class mail</td>
               <td class="lhl_table_rhc">$3.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="FedEx 2-Day"/></td>
               <td class="lhl_table_text" colspan="2">FedEx 2-Day (No charge if your FedEx account is used)</td>
               <td class="lhl_table_rhc">$4.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="FedEx Overnight"/></td>
               <td class="lhl_table_text" colspan="2">FedEx Overnight (No charge if your FedEx account is used)</td>
               <td class="lhl_table_rhc">$5.00</td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>
	         
            <tr>
               <td class="lhl_table_text" colspan="4"><b>International delivery:</b></td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="International fax"/></td>
               <td class="lhl_table_text" colspan="2">International Fax</td>
               <td class="lhl_table_rhc">$5.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="Air mail"/></td>
               <td class="lhl_table_text" colspan="2">Air mail</td>
               <td class="lhl_table_rhc">$3.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="method" value="FedEx International"/></td>
               <td class="lhl_table_text" colspan="2">FedEx International (No charge if your FedEx account is used)</td>
               <td class="lhl_table_rhc">$20.00</td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>

            <tr>
               <td class="lhl_table_text" colspan="4"><b>Charge to my FedEx Account:</b></td>
            </tr>
            <tr>
               <td class="lhl_table_text" colspan="2" width="1%">FedEx Account #</td>
               <td class="lhl_table_text"><stripes:text name="fedex" size="25"/></td>
               <td class="lhl_table_rhc">No charge</td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>

            <tr>
                <th class="lhl_table_title lhl_table_lhc" colspan="3" width="330px"><b>Service level (choose one)</p></th>
                <th class="lhl_table_title lhl_table_rhc" width="120px">Cost</th>
            </tr>
    
            <tr>
               <td class="lhl_table_text lhl_table_lhc" colspan="4"><b>Choose service level</b></td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="service" value="Regular service"/></td>
               <td class="lhl_table_text" colspan="2">Regular service: within 24-48 hours</td>
               <td class="lhl_table_rhc">No charge</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="service" value="Rush service"/></td>
               <td class="lhl_table_text" colspan="2">Rush service: within 6 working hours</td>
               <td class="lhl_table_rhc">$6.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="service" value="Super rush service"/></td>
               <td class="lhl_table_text" colspan="2">Super rush service: within 3 working hours</td>
               <td class="lhl_table_rhc">$12.00</td>
            </tr>
            <tr>
               <td class="lhl_table_input"><stripes:radio name="service" value="Drop everything service"/></td>
               <td class="lhl_table_text" colspan="2">Drop everything service: within 1 working hour</td>
               <td class="lhl_table_rhc">$100.00</td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>

            <tr>
               <td class="lhl_table_text lhl_table_lhc" colspan="4"><b>Shipping information</b></td>
            </tr>
            <tr>
               <td class="lhl_table_text" colspan="2">Send to the attention of:</td>
               <td class="lhl_table_text" colspan="2"><stripes:text name="attention" size="25"/></td>
            </tr>
            <tr>
               <td class="lhl_table_text" colspan="2">Email address for order confirmation: </td>
               <td class="lhl_table_text" colspan="2"><stripes:text name="confirmationEmail" size="25"/></td>
            </tr>
            <tr><td colspan="4" class="lhl_bottom_padder">&nbsp;</td></tr>

            <tr>
               <td class="lhl_table_text lhl_table_lhc" colspan="4"><b>Ship to:</b></td>
            </tr>

            <tr>
            <td colspan="4">
    <c:choose>
    <c:when test="${actionBean.editable}">
                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">First name: </span></td>
                            <td valign="top">
                                <stripes:text name="firstname" size="15"/>
                            </td>
                            <td valign="middle"><span CLASS="MedBlackText">Last name: </span></td>
                            <td valign="top">
                                <stripes:text name="lastname" size="15"/>
                            </td>
                        </tr>
        
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">Company name: </span></td>
                            <td valign="top">
                                <stripes:text name="companyname" size="15"/>
                            </td>
                        </tr>
        
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">Address1: </span></td>
                            <td valign="top">
                                <stripes:text name="address1" size="15"/>
                            </td>
                            <td valign="middle"><span CLASS="MedBlackText">Address2: </span></td>
                            <td valign="top">
                                <stripes:text name="address2" size="15"/>
                            </td>
                        </tr>
        
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">City: </span></td>
                            <td valign="top">
                                <stripes:text name="city" size="10"/>
                            </td>
                            <td valign="middle"><span CLASS="MedBlackText">State: </span></td><td valign="top">
                                <stripes:text name="state" size="15"/>
                            </td>
                        </tr>
        
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">Country: </span></td>
                            <td valign="top">
                                <stripes:text name="country" size="10"/>
                            </td>
        
                            <td valign="middle"><span CLASS="MedBlackText">Zip: </span></td><td valign="top">
                                <stripes:text name="zip" size="15"/>
                            </td>
                        </tr>
        
        
                        <tr>
                            <td valign="middle"><span CLASS="MedBlackText">Telephone: </span></td>
                            <td valign="top">
                                <stripes:text name="phone" size="10"/>
                            </td>
                            <td valign="middle"><span CLASS="MedBlackText">Fax: </span></td><td valign="top">
                                <stripes:text name="fax" size="15"/>
                            </td>
                        </tr>
        
                        <tr>
                            <td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td>
                            <td valign="middle" colspan="2">
                                <p CLASS="MedBlackText">
                                    <input type="submit" name="submitorder" value="Submit" />
                                    <input type="button" name="close" value="Close" onclick="javascript:window.close();"/>
                                </p>
                            </td>
                        </tr>
                        </table>
</c:when>
<c:otherwise>
                <stripes:hidden name="firstname"/>
                <stripes:hidden name="lastname"/>
                <stripes:hidden name="companyname"/>
                <stripes:hidden name="address1"/>
                <stripes:hidden name="address2"/>
                <stripes:hidden name="city"/>
                <stripes:hidden name="state"/>
                <stripes:hidden name="country"/>
                <stripes:hidden name="zip"/>
                <stripes:hidden name="fax"/>
                <stripes:hidden name="confirmationEmail"/>
                <stripes:hidden name="phone"/>

                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td valign="top" colspan="3">
                            <span CLASS="MedBlackText">
                            ${actionBean.firstname} ${actionBean.lastname}<br/>
                            ${actionBean.companyname}<br/>
                            ${actionBean.address1} ${actionBean.address2}<br/>
                            ${actionBean.city}, ${actionBean.state} ${actionBean.zip} ${actionBean.country}<br/>
                            ${actionBean.phone}<br/>
                            ${actionBean.fax}<br/>
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" colspan="3" height="4"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
                    <tr>
                        <td valign="top" colspan="3">
                           <input type="submit" name="submitorder" value="Submit" />
                           <input type="button" name="close" value="Close" onclick="javascript:window.close();"/>
                        </td>
                    </tr>
                </table>
</c:otherwise>
</c:choose>
            </td>
            </tr>
            
	       </tbody>
	       </table>
	       	
    </div>




    </stripes:form>
    
    </stripes:layout-component>
	<stripes:layout-component name="modal_dialog_msg"/>
	<stripes:layout-component name="modal_dialog"/>
	<stripes:layout-component name="jsbottom_custom">
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/LindaHallOrder.js?v=${releaseversion}"/>
    </stripes:layout-component>
	<stripes:layout-component name="footer">

    <div class="hr" style="margin:20px 0 7px 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr/></div>
	
	<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
	
	</stripes:layout-component> 
</stripes:layout-render>
