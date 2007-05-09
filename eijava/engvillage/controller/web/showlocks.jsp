<%@ page import="org.ei.util.SpinLock"%>
<%@ page session="false" %>

<pre>
<%
	SpinLock s = SpinLock.getInstance();
	s.lockdump(out);
%>
</pre>