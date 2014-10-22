<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Open RSS status">

    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    </stripes:layout-component>
    
    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>
    
<%-- **************************************** --%>  
<%-- CONTENTS                                 --%>  
<%-- **************************************** --%>  
    <stripes:layout-component name="contents">
    <div id="container">
    
    <jsp:include page="include/tabs.jsp"/>
    
    <div class="marginL10">

        <h2>Test OpenRSS</h2>

        <form method="post" action="/controller/servlet/OpenURL">
            <p>
                <b><font size="2">Choose the OpenURL standard search fields:</font></b>
            </p>
            <p>
                <b><font size="2">database:</font></b>
                <font size="2">&nbsp; </font>
                <font size="4"> <input type="checkbox" value="EI:1" checked="checked"
                    name="sid" /></font>
                <font size="2">compendex&nbsp;&nbsp;&nbsp; </font>
                <font size="4"> 
                    <input type="checkbox" value="EI:2" name="sid" />
                    <font size="2">inspec&nbsp;&nbsp;&nbsp;&nbsp; </font> <input type="checkbox" value="EI:3" name="sid" />
                    <font size="2">combined</font>
                </font>
            </p>
            <p>
                <font size="2"><b>genre:&nbsp;&nbsp;&nbsp;</b>&nbsp;&nbsp;&nbsp; </font>
                <font size="4"> <input type="radio" value="article" name="genre" checked="checked" /></font>
                <font size="2">article(journal article)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font>
                <font size="4"><input type="radio" name="genre" value="conference" /></font>
                <font size="2">conference(conference proceeding)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font>
                <font size="4"> <input type="radio" name="genre" value="proceeding" /></font>
                <font size="2">proceeding(conference article)&nbsp; </font>
                <font size="4"> <input type="radio" name="genre" value="all" /></font>
                <font size="2">all</font>
            </p>
            <p>
                <font size="2"><b>aulast:&nbsp;</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font><font size="4"> <input type="text" name="aulast" size="20" /></font>
            </p>
            <p>
                <font size="2"><b>issn:&nbsp;&nbsp;</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font>
                <font size="4"> <input type="text" name="issn" size="20" /></font>
            </p>
            <p>
                <font size="2"><b>coden:&nbsp;</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
                <font size="4"><input type="text" name="coden" size="20" /></font>
            </p>
            <p>
                <font size="2"><b>isbn:&nbsp;</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
                <font size="4"><input type="text" name="isbn" size="20" /></font>
            </p>
            <p>
                <font size="2"><b>title</b>(title of a journal, book, conference)&nbsp;&nbsp;&nbsp;&nbsp; </font>
                <font size="4"> <input type="text" name="title" size="77" /></font>
            </p>
            <p>
                <font size="2"><b>stitle</b>(abbreviated title)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
                <font size="4"> <input type="text" name="stitle" size="77" /></font>
            </p>
            <p>
                <font size="2"><b>atitle</b>(article title)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </font>
                <font size="4">&nbsp;<input type="text" name="atitle" size="77" /></font>
            </p>
            <p>
                <font size="2"><b>date</b>(year between 1969 - 2003) </font>
                <font size="4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input
                    type="text" name="date" size="20" /></font>
            </p>
            <p align="left">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" value="Submit" name="B1" />&nbsp;&nbsp;&nbsp; <input type="reset" value="Reset" name="B2" />
            </p>
        </form>


    </div>
    
    </div>    

    </stripes:layout-component>
    
</stripes:layout-render>