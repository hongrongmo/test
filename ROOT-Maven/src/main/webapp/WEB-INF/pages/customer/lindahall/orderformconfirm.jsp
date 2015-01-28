<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Linda Hall Library Document Request Confirmation">

    <stripes:layout-component name="header">
    <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="modal_dialog"></stripes:layout-component>
    <stripes:layout-component name="modal_dialog_2"></stripes:layout-component>

    <stripes:layout-component name="contents">
    
    <stripes:form name="lhlrequest" method="post" action="/lindahall/orderform.url">

    <stripes:hidden name="docid"/>
    <stripes:hidden name="database"/>
    <stripes:hidden name="shippingvalue"/>

    <stripes:hidden name="method"/>
    <stripes:hidden name="service"/>
    <stripes:hidden name="accountnumber"/>
    <stripes:hidden name="attention" />
    <stripes:hidden name="firstname"/>
    <stripes:hidden name="lastname"/>
    <stripes:hidden name="companyname"/>
    <stripes:hidden name="address1"/>
    <stripes:hidden name="address2"/>
    <stripes:hidden name="city"/>
    <stripes:hidden name="state"/>
    <stripes:hidden name="country"/>
    <stripes:hidden name="zip"/>
    <stripes:hidden name="phone"/>
    <stripes:hidden name="fax"/>

    <stripes:hidden name="confirmationEmail"/>
    <stripes:hidden name="deliveryEmail" />
            
	<div class="marginL10">
		<h2>Linda Hall Library Document Request Confirmation</h2>

        <ul class="errors" id="jserrors">
        <stripes:errors>
             <stripes:errors-header></stripes:errors-header>
             <li><stripes:individual-error/></li>
             <stripes:errors-footer></stripes:errors-footer>
        </stripes:errors>
        </ul>

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
                
                <tr><td valign="top">
                <span CLASS="MedBlackText"><b>Delivery method: </b></span></td><td valign="top"><span CLASS="MedBlackText"><c:choose><c:when test="${actionBean.method eq 'Email'}">Email</c:when><c:otherwise>${actionBean.method}</c:otherwise></c:choose>
                    <c:choose><c:when test="${actionBean.method eq 'Email'}">(${actionBean.deliveryEmail})</c:when><c:when test="${actionBean.method eq 'Ariel'}">(${actionBean.ariel})</c:when></c:choose>
                </span>
                </td></tr>
                
                <tr><td valign="top"><span CLASS="MedBlackText"><b>Service level: </b></span></td><td valign="top"><span CLASS="MedBlackText">${actionBean.service}</span></td></tr>
                <tr><td valign="top"><span CLASS="MedBlackText"><b>FedEx account: </b></span></td><td valign="top"><span CLASS="MedBlackText">${actionBean.accountnumber}</span></td></tr>
                <tr><td valign="top" colspan="2" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
                <tr><td valign="top"><span CLASS="MedBlackText"><b>Attention:</b></span></td><td valign="top"><span CLASS="MedBlackText">${actionBean.attention}</span></td></tr>
                <tr><td valign="top"><span CLASS="MedBlackText"><b>Email confirmation: </b></span></td><td valign="top"><span CLASS="MedBlackText">${actionBean.confirmationEmail}</span></td></tr>
                <tr><td valign="top"><span CLASS="MedBlackText"><b>Ship to: </b></span></td><td valign="top"><span CLASS="MedBlackText">${actionBean.firstname} ${actionBean.lastname}<br/>
                ${actionBean.companyname}<br/>
                ${actionBean.address1} ${actionBean.address2}<br/>
                ${actionBean.city}, ${actionBean.state} ${actionBean.zip} ${actionBean.country}<br/>
                ${actionBean.phone}<br/>
                ${actionBean.fax}<br/>
                </span></td></tr>

                <tr><td valign="top" height="15" colspan="2"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
                <tr>
                    <td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td>
                    <td valign="top">
                        <span CLASS="MedBlackText">
                        <stripes:submit name="submitedit" value="Edit Request"/>
                        <stripes:submit name="submitemail" value="Submit Request"/>
                        </span>
                    </td>
                </tr>
                <tr><td valign="top" colspan="2" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
            </table>
        </form>
    </center>

    </div>




    </stripes:form>
    
    </stripes:layout-component>
    <stripes:layout-component name="modal_dialog_msg"/>
	<stripes:layout-component name="modal_dialog"/>
    
    <stripes:layout-component name="jsbottom_custom">
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/LindaHallOrder.js?v=${releaseversion}"/>
    </stripes:layout-component>
    <stripes:layout-component name="modal_dialog_msg"/>
	<stripes:layout-component name="modal_dialog"/>
	<stripes:layout-component name="footer">

    <div class="hr" style="margin:20px 0 7px 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr/></div>
	
	<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
	
	</stripes:layout-component> 
</stripes:layout-render>
