DeliveryManager = {
	emailform : {
		ele : $("#emailform"),
		from : $("#emailform input#from"),
		to : $("#emailform input#to"),
		subject : $("#emailform input#subject"),
		message : $("#emailform textarea#message"),
		validateForm : function(event) {
			
			var from = DeliveryManager.emailform.from.val().replace(/\s+/g, "").replace(/;/g, ",");
			var to = DeliveryManager.emailform.to.val().replace(/\s+/g, "").replace(/;/g, ",");

			//
			// Ensure "To" field is not empty and is valid
			//
			if (to.length == 0) {
				event.preventDefault();
				window.alert("You must enter the recipients email address");
				DeliveryManager.emailform.to.focus();
				return false;
			}
			if (!validateToRecipients(to)) {
				event.preventDefault();
				alert("Invalid Email address");
				DeliveryManager.emailform.to.focus();
				return false;
			}

			//
			// Ensure "From" field is not empty and is valid
			//
			if (from.length == 0) {
				alert("You must enter the Sender email address");
				DeliveryManager.emailform.from.focus();
				return false;
			}
			if (from.indexOf(',') >= 0) {
				alert("You may only enter one email address in the Your Email field.");
				DeliveryManager.emailform.from.focus();
				return false;
			}
			if (!validateEmail(from)) {
				alert("Invalid Email address");
				DeliveryManager.emailform.from.focus();
				return false;
			}
			
			//
			// Ensure "Subject" field is not empty and is valid
			//
			if (subject.length == 0) {
				alert("Subject Field cannot be blank");
				DeliveryManager.emailform.subject.focus();
				return (false);
			}
			
			var database = sendemail.database.value;
			// var displayformat = sendemail.displayformat.value;
			var myindex = sendemail.displayformat.selectedIndex;
			displayformat = sendemail.displayformat.options[myindex].value;
			var selectedset = sendemail.selectedset.value;
			var searchquery = sendemail.searchquery.value;
			var hiddensize = sendemail.elements.length;
			var docidstring = "&docidlist=";
			var handlestring = "&handlelist=";
			var dbstring = "&dblist=";
			// logic to construct docidList,HandleList and databaseList
			for ( var i = 0; i < hiddensize; i++) {
				var nameOfElement = sendemail.elements[i].name;
				var valueOfElement = sendemail.elements[i].value;
				if ((nameOfElement.search(/DOC-ID/) != -1) && (valueOfElement != "")) {
					var subdocstring = valueOfElement + ",";
					docidstring += subdocstring;
				}
				if ((nameOfElement.search(/HANDLE/) != -1) && (valueOfElement != "")) {
					var subhandlestring = valueOfElement + ",";
					handlestring += subhandlestring;
				}
				if ((nameOfElement.search(/DATABASE/) != -1) && (valueOfElement != "")) {
					var subdbstring = valueOfElement + ",";
					dbstring += subdbstring;
				}
			}
			if (typeof (sendemail.docidlist) != 'undefined') {
				url = "/controller/servlet/Controller?EISESSION=" + sessionid
						+ "&CID=emailSelectedRecords" + "&displayformat="
						+ displayformat + "&timestamp=" + milli + "&docidlist="
						+ sendemail.docidlist.value + "&handlelist="
						+ sendemail.handlelist.value;
			} else {
				if (sendemail.saved_records.value != "true") {
					url = "/controller/servlet/Controller?EISESSION=" + sessionid
							+ "&CID=emailSelectedSet" + "&displayformat="
							+ displayformat + "&timestamp=" + milli;
				} else {
					url = "/controller/servlet/Controller?EISESSION=" + sessionid
							+ "&CID=emailSavedRecordsOfFolder" + "&displayformat="
							+ displayformat + "&folderid=" + folder_id + "&timestamp="
							+ milli;
				}
			}
			// url = url + "&to="+sendemail.to.value;
			// url = url + "&from="+sendemail.from.value;
			// url = url + "&subject="+sendemail.subject.value;
			// url = url + "&message="+sendemail.message.value;
			sendemail.to.value = sendemail.to.value.replace(/;/g, ",");
			sendemail.action = url;

		}

	},


	//		
	// Change the display type
	//
	changeDisplay: function(event) {
		alert("Change display...");
	},

	//
	// Initialize!
	//
	init : function() {
		DeliveryManager.emailform.ele.submit(DeliveryManager.emailform.validateForm);
		
		this.display = $("#display");
		this.displayselected = $("#display option:selected").val();
		this.docid = $("#docid");
		this.handle = $("#handle");
		
		this.display.change(this.changeDisplay);
	}
};

$(document).ready(function() {
	DeliveryManager.init();
});

//
// This function basically validates the data entered in TO Field for Multiple
// recipients
function validateToRecipients(toEmail) {
	var result = true;
	receipients = toEmail.split(/,/);
	for ( var i = 0; i < receipients.length; i++) {
		if (!echeck(receipients[i])) {
			return false;
		}
		result = result || validateEmail(email);
	}
	return result;
}

//
//This function basically does email validation(ie @ . and special characters)
///
function validateEmail(email) {
	var splitted = email.match("^(.+)@(.+)$");
	if (splitted == null)
		return false;
	if (splitted[1] != null) {
		var regexp_user = /^\"?[\w-_\.]*\"?$/;
		if (splitted[1].match(regexp_user) == null)
			return false;
	}
	if (splitted[2] != null) {
		var regexp_domain = /^[\w-\.]*\.[A-Za-z]{2,4}$/;
		if (splitted[2].match(regexp_domain) == null) {
			var regexp_ip = /^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
			if (splitted[2].match(regexp_ip) == null)
				return false;
		}
		return true;
	}
	return false;
}

// Validate email parts
function echeck(str) {
	var at = "@";
	var dot = ".";
	var lat = str.indexOf(at);
	var lstr = str.length;
	var ldot = str.indexOf(dot);
	if (str.indexOf(at) == -1) {
		return false;
	}
	if (str.indexOf(at) == -1 || str.indexOf(at) == 0
			|| str.indexOf(at) == lstr) {
		return false;
	}
	if (str.indexOf(dot) == -1 || str.indexOf(dot) == 0
			|| str.indexOf(dot) == lstr) {
		return false;
	}
	if (str.indexOf(at, (lat + 1)) != -1) {
		return false;
	}
	if (str.substring(lat - 1, lat) == dot
			|| str.substring(lat + 1, lat + 2) == dot) {
		return false;
	}
	if (str.indexOf(dot, (lat + 2)) == -1) {
		return false;
	}
	if (str.indexOf(" ") != -1) {
		return false;
	}
	return true;
}

// This function basically validates the data entered in Email form fields.
// typically it checks for a valid mail id and not null conditions
/*
function validateForm(sendemail) {
	var milli = (new Date()).getTime();
	sendemail.from.value = sendemail.from.value.replace(/\s+/g, "");
	sendemail.to.value = sendemail.to.value.replace(/\s+/g, "");
	var from = sendemail.from.value;
	var to = sendemail.to.value;
	to = to.replace(/;/g, ",");
	var subject = sendemail.subject.value;
	var message = sendemail.message.value;
	var sessionid = sendemail.sessionid.value;
	var folder_id = sendemail.folder_id.value;
	if (to.length == 0) {
		window.alert("You must enter the recipients email address");
		sendemail.to.focus();
		return false;
	}
	var booleanTo = validateToRecipients(to);
	if (booleanTo == false) {
		alert("Invalid Email address");
		sendemail.to.focus();
		return false;
	}
	if (from.length == 0) {
		alert("You must enter the Sender email address");
		sendemail.from.focus();
		return false;
	}
	var fromSplit = from.replace(/;/g, ",");
	fromSplit = fromSplit.split(/,/);
	if (fromSplit.length > 1) {
		alert("You may only enter one email address in the Your Email field.");
		sendemail.from.focus();
		return false;
	}
	var booleanFrom = validateEmail(from);
	if (booleanFrom == false) {
		alert("Invalid Email address");
		sendemail.from.focus();
		return false;
	}
	if (subject.length == 0) {
		alert("Subject Field cannot be blank");
		sendemail.subject.focus();
		return (false);
	}
	var database = sendemail.database.value;
	// var displayformat = sendemail.displayformat.value;
	var myindex = sendemail.displayformat.selectedIndex;
	displayformat = sendemail.displayformat.options[myindex].value;
	var selectedset = sendemail.selectedset.value;
	var searchquery = sendemail.searchquery.value;
	var hiddensize = sendemail.elements.length;
	var docidstring = "&docidlist=";
	var handlestring = "&handlelist=";
	var dbstring = "&dblist=";
	// logic to construct docidList,HandleList and databaseList
	for ( var i = 0; i < hiddensize; i++) {
		var nameOfElement = sendemail.elements[i].name;
		var valueOfElement = sendemail.elements[i].value;
		if ((nameOfElement.search(/DOC-ID/) != -1) && (valueOfElement != "")) {
			var subdocstring = valueOfElement + ",";
			docidstring += subdocstring;
		}
		if ((nameOfElement.search(/HANDLE/) != -1) && (valueOfElement != "")) {
			var subhandlestring = valueOfElement + ",";
			handlestring += subhandlestring;
		}
		if ((nameOfElement.search(/DATABASE/) != -1) && (valueOfElement != "")) {
			var subdbstring = valueOfElement + ",";
			dbstring += subdbstring;
		}
	}
	if (typeof (sendemail.docidlist) != 'undefined') {
		url = "/controller/servlet/Controller?EISESSION=" + sessionid
				+ "&CID=emailSelectedRecords" + "&displayformat="
				+ displayformat + "&timestamp=" + milli + "&docidlist="
				+ sendemail.docidlist.value + "&handlelist="
				+ sendemail.handlelist.value;
	} else {
		if (sendemail.saved_records.value != "true") {
			url = "/controller/servlet/Controller?EISESSION=" + sessionid
					+ "&CID=emailSelectedSet" + "&displayformat="
					+ displayformat + "&timestamp=" + milli;
		} else {
			url = "/controller/servlet/Controller?EISESSION=" + sessionid
					+ "&CID=emailSavedRecordsOfFolder" + "&displayformat="
					+ displayformat + "&folderid=" + folder_id + "&timestamp="
					+ milli;
		}
	}
	// url = url + "&to="+sendemail.to.value;
	// url = url + "&from="+sendemail.from.value;
	// url = url + "&subject="+sendemail.subject.value;
	// url = url + "&message="+sendemail.message.value;
	sendemail.to.value = sendemail.to.value.replace(/;/g, ",");
	sendemail.action = url;
	// window.open(url);
	// window.close();
	return (true);
} // end function - validateForm
*/