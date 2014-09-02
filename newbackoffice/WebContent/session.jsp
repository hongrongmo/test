
<%@ page  import="java.util.Enumeration"%>

<%@ page  import="org.apache.commons.dbcp.*"%>
<%@ page  import="org.ei.struts.backoffice.Constants"%>


<%

  	Enumeration names = request.getSession().getAttributeNames();
    while(names.hasMoreElements())
    {
        String attribName = (String) names.nextElement();
        out.write(attribName.toString() + " = ");
        Object obj = (request.getSession().getAttribute(attribName)).getClass();
        out.write(obj.toString() + " <br> ");

    }


%>
