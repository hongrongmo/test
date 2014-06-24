	if ((navigator.appName == "Netscape") && (parseInt(navigator.appVersion) >3 ))
	{
		document.write("<LINK REL='stylesheet' HREF='/static/css/ev2net.css'  type='text/css'/>");
	}
	else if ((navigator.appName == "Microsoft Internet Explorer") && (parseInt(navigator.appVersion) >3 ))
	{
		document.write("<LINK REL='stylesheet' HREF='/static/css/ev2ie.css' type='text/css'/>");
	}
	else
	{
		document.write("<LINK REL='stylesheet' HREF='/static/css/ev2ie.css' type='text/css'/>");
	}
