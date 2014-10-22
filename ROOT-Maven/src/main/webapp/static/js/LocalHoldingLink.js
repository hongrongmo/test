function openLocalHoldingsLink(e,orginPage,currentObject){
	e.preventDefault();
	var originalUrl = $(currentObject).attr("href");
	if(typeof  originalUrl == undefined || originalUrl == null || originalUrl == ""){
		return true;
	}
	var newUrl = originalUrl.replace("localholdinglinks","localholdinglinksstreamurl");
	$.get(
			newUrl,
		    function(data) {
				window.open(data,'newwindow');
				if (GALIBRARY) GALIBRARY.createWebEventWithLabel('Local Holding Link',orginPage,data);
			}
		);

	return false;
}