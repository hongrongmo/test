	/**
	 * Handle the print link
	 */
	function displayFormat() {
		$("#printdocument").submit();
		return false;
			/*
		var printurl = "/delivery/print/display.url?timestamp="+new Date().getTime();
		var myindex  = printdocument.displayformat.selectedIndex;
		// Set the CID
		var displaytype = printdocument.displayformat.options[myindex].value;
		printurl += "&displayformat="+displaytype;
        document.printdocument.action = printurl;
        document.printdocument.submit();
		return true;
		*/
	}