

function checkFocus(){
	if (document.delivery.method[0].checked==true) {
		delivery.email.focus()
	}
else
	if (document.delivery.method[1].checked==true) {
		delivery.ariel.focus()
	}
}

// Function that takes care all validation of ship to Info based on Shipping Flag
function checkdelivery(delivery,shippingFlag) {

	if (document.delivery.method[0].checked==true) {
		var emailLength= document.delivery.deliveryEmail.value.length;
		var tempEmailLength=checkForSpaces(document.delivery.deliveryEmail.value);
		if (emailLength == tempEmailLength) {
			alert("You have to enter E-mail address")
			document.delivery.deliveryEmail.focus()
			return false;
		}
		else {
			var tf = validateEmail(document.delivery.deliveryEmail.value);
			if(tf==false){
				alert ("Invalid Email address");
				document.delivery.deliveryEmail.focus();
				return false;
			}
		}
	}
	else {
		if (document.delivery.method[1].checked==true) {
			var arielLength= document.delivery.ariel.value.length;
			var tempArielLength=checkForSpaces(document.delivery.ariel.value);
			if (arielLength == tempArielLength) {
				alert("You have to enter the required address")
				document.delivery.ariel.focus()
				return false;
			}
		}
	}
	var emailLength= document.delivery.confirmationEmail.value.length;
	var tempEmailLength=checkForSpaces(document.delivery.confirmationEmail.value);
	if (emailLength == tempEmailLength) {
			alert("You have to enter a Confirmation E-mail address")
			document.delivery.confirmationEmail.focus()
			return false;
	} else {
		var tf=	validateEmail(document.delivery.confirmationEmail.value);
		if(tf==false){
			alert ("Invalid Confirmation Email address");
			document.delivery.confirmationEmail.focus();
			return false;
		}
	}

	var fedexLength= document.delivery.fedex.value.length;
	var tempFedexLength=checkForSpaces(document.delivery.fedex.value);
	if (fedexLength == tempFedexLength) {
		document.delivery.fedex.value='';
	}

	if(shippingFlag=='d'){

		var fnLength= document.delivery.firstname.value.length;
		var tempFNLength=checkForSpaces(document.delivery.firstname.value);
		if (fnLength == tempFNLength) {
			alert("You have to enter the first name")
			document.delivery.firstname.focus()
			return false;
		}else{
			var tf=validateName(document.delivery.firstname.value);
			if(tf==false){
				document.delivery.firstname.focus();
				return false;
			}
		}


		var lnLength= document.delivery.lastname.value.length;
		var tempLNLength=checkForSpaces(document.delivery.lastname.value);
		if (lnLength == tempLNLength) {
			alert("You have to enter the last name")
			document.delivery.lastname.focus()
			return false;
		}
		else{
			var tf=validateName(document.delivery.lastname.value);
			if(tf==false){
				document.delivery.lastname.focus();
				return false;
			}
		}


		var cnLength= document.delivery.companyname.value.length;
		var tempCNLength=checkForSpaces(document.delivery.companyname.value);
		if (cnLength == tempCNLength) {
			alert("You have to enter the company name")
			document.delivery.companyname.focus()
			return false;
		}


		var a1Length= document.delivery.address1.value.length;
		var tempA1Length=checkForSpaces(document.delivery.address1.value);
		if (a1Length == tempA1Length) {
			alert("You have to enter the address line 1")
			document.delivery.address1.focus()
			return false;
		}

		var a2Length= document.delivery.address2.value.length;
		var tempA2Length=checkForSpaces(document.delivery.address2.value);


		var cityLength= document.delivery.city.value.length;
		var tempCityLength=checkForSpaces(document.delivery.city.value);
		if (cityLength == tempCityLength) {
			alert("You have to enter the city name")
			document.delivery.city.focus()
			return false;
		}

		var stateLength= document.delivery.state.value.length;
		var tempStateLength=checkForSpaces(document.delivery.state.value);
		if (stateLength == tempStateLength) {
			alert("You have to enter the state name")
			document.delivery.state.focus()
			return false;
		}



		var cLength= document.delivery.country.value.length;
		var tempCLength=checkForSpaces(document.delivery.country.value);
		if (cLength == tempCLength) {
			alert("You have to enter the country name")
			document.delivery.country.focus()
			return false;
		}



		var zipLength= document.delivery.zip.value.length;
		var tempZipLength=checkForSpaces(document.delivery.zip.value);
		if (zipLength == tempZipLength) {
			alert("You have to enter the zip code")
			document.delivery.zip.focus()
			return false;
		}
		else {
			var tf=validateZip(document.delivery.zip.value);
			if(tf==false){
				document.delivery.zip.focus();
				return false;
			}
		}

		var phoneLength= document.delivery.phone.value.length;
		var tempPhoneLength=checkForSpaces(document.delivery.phone.value);
		if (phoneLength == tempPhoneLength) {
			alert("You have to enter the telephone number")
			document.delivery.phone.focus()
			return false;
		}
		else {
			var tf=validateTelFax(document.delivery.phone.value);
			if(tf==false){
				document.delivery.phone.focus();
				return false;
			}
		}


		var faxLength= document.delivery.fax.value.length;
		var tempFaxLength=checkForSpaces(document.delivery.fax.value);
		if (faxLength == tempFaxLength) {
			window.alert("You have to enter the fax number");
			document.delivery.fax.focus()
			return false;
		}
		else{
			var tf=validateTelFax(document.delivery.fax.value);
			if(tf==false){
				document.delivery.fax.focus();
				return false;
			}
		}

		var youremailLength= document.delivery.deliveryEmail.value.length;
		var tempEmailLength=checkForSpaces(document.delivery.deliveryEmail.value);
		if (youremailLength == tempEmailLength) {
			window.alert("You have to enter the email address");
			document.delivery.deliveryEmail.focus()
			return false;
		}
		else {
			var tf=validateEmail(document.delivery.deliveryEmail.value);
			if(tf==false){
				alert ("Invalid Email address");
				document.delivery.deliveryEmail.focus();
				return false;
			}
		}
	}

}


// This function basically does Check for spaces validation;
function checkForSpaces(str){
	var tempword = str;
	var tempLength=0;
	while (tempword.substring(0,1) == ' ') {
		tempword = tempword.substring(1);
		tempLength = tempLength + 1;
	}
	return tempLength;
}

// This function basically does Name validation;
function validateName(nameField){
	var valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,@ '.&"
	var yesno = true;
	var temp;
	for (var i=0; i<nameField.length; i++) {
		temp = "" + nameField.substring(i, i+1);
		if (valid.indexOf(temp) == "-1") yesno = false;
	}
	if (yesno == false) {
		alert("Invalid entry!  Only characters and quote are accepted!");
	}
	return yesno;
}

// This function basically does Telephone and Fax validation(ie numbers,- "");
function validateTelFax(telFaxField){
	var valid = "0123456789- ()"
	var yesno = true;
	var temp;
	for (var j=0; j<telFaxField.length; j++) {
		temp = "" + telFaxField.substring(j, j+1);
		if (valid.indexOf(temp) == "-1") yesno = false;
	}
	if (yesno == false) {
		alert("Invalid entry!  Only Numbers and - are accepted!");
	}
	return yesno;
}

// This function basically does Zip validation(ie numbers)
function validateZip(zipField){
	var valid = "0123456789"
	var yesno = true;
	var temp;
	for (var i=0; i<zipField.length; i++) {
		temp = "" + zipField.substring(i, i+1);
		if (valid.indexOf(temp) == "-1") yesno = false;
	}
	if (yesno == false) {
		alert("Invalid entry!  Only Numbers are accepted!");
	}
	return yesno;
}

// This function basically does email validation(ie @ . and special characters)
function validateEmail(email)
{
	var splitted = email.match("^(.+)@(.+)$");
	if(splitted == null) return false;
	if(splitted[1] != null )
	{
		var regexp_user=/^\"?[\w-_\.]*\"?$/;
		if(splitted[1].match(regexp_user) == null) return false;
	}
	if(splitted[2] != null)
	{
		var regexp_domain=/^[\w-\.]*\.[A-Za-z]{2,4}$/;
		if(splitted[2].match(regexp_domain) == null)
		{
			var regexp_ip =/^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
			if(splitted[2].match(regexp_ip) == null) return false;
		}// if
		return true;
	}
	return false;
}

