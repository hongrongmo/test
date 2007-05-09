 //for linda hall
  function lindaHall(sessionid,docid,database)
	{

	var now = new Date() ;
	  var milli = now.getTime() ;

	var url="/controller/servlet/Controller?EISESSION="+sessionid+"&CID=lhlAuthentication&docid="+docid+"&database="+database+"&timestamp="+milli;
	NewWindow = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
	NewWindow.focus();
	}
