	if ((navigator.appName == "Netscape") && (parseInt(navigator.appVersion) >3 ))
	{
		document.write("<LINK REL='stylesheet' HREF='/engresources/stylesheets/ev2net_V7.css'  type='text/css'/>");
	}
	else if ((navigator.appName == "Microsoft Internet Explorer") && (parseInt(navigator.appVersion) >3 ))
	{
		document.write("<LINK REL='stylesheet' HREF='/engresources/stylesheets/ev2ie_V7.css' type='text/css'/>");
	}
	else
	{
		document.write("<LINK REL='stylesheet' HREF='/engresources/stylesheets/ev2ie_V7.css' type='text/css'/>");
	}
