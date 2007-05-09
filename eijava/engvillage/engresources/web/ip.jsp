<%@ page session="false" %>
<%

String ip = request.getHeader("x-forwarded-for");
if(ip == null)
{
        ip = request.getRemoteAddr();
}

out.println(ip);

%>
