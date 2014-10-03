$(document).ready(function(){
	// Trigger this method every minute to ping the server for session status
	setInterval(checkSessionStatus,60000);
});

function checkSessionStatus(){
	$.ajax({ url:"/application/checksessionstatus?resourcetype=checkstatus",cache: false} )
	  .done(function( data, textStatus, jqXHR) {
		  
		  if(data.sessionValid === false){
			  window.location.href = "/application/checksessionstatus?resourcetype=redirect";
		  }
	  })
	  .fail(function(  jqXHR, textStatus, errorThrown) {
	      console.log("Eror occured while trying to check the session validity!");
	  })
	  .always(function( data, textStatus, jqXHROrErrorThrown) {
	    
	  });
}