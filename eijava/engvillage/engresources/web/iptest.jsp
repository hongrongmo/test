<%@ page session="false" %>
<html>
<body>
Remote address: <%= request.getRemoteAddr() %>
<br>
Remote Forwarded address: <%= request.getHeader("x-forwarded-for") %>
</body>
</html>
