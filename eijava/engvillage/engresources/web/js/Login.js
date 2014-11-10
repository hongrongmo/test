function uncheckEmail() {
	if (window.opener.searchhistoryform) {
		if (window.opener.searchhistoryform.emailalert){
			window.opener.searchhistoryform.emailalert.checked = false;
		}
	}
}

function validateLogin(personallogin)
{
	if (personallogin.email.value =="")
	{
		 window.alert ("You must enter an e-mail address(username)");
		 personallogin.email.value = "";
		 personallogin.email.focus();
		 return false;
	}
	var emailValid = validateEmail(personallogin.email.value);
	if (!emailValid)
	{
		alert("The e-mail field contains an invalid e-mail Format.\nPlease enter your correct e-mail address.");
		personallogin.email.focus();
		return false;
	}
	if (personallogin.password.value =="")
	{
		alert ("You must enter a password");
		   personallogin.password.focus();
		   return false;
	}
	else
	{
			var pwdLength= personallogin.password.value.length;
			var tempPwd = personallogin.password.value;
			var tempLength=0;
			if(tempPwd.substring(0,1) == ' ')
			{
				   window.alert("Password should not starts with spaces");
				   personallogin.password.focus();
				   return false;
			}
			else
			{
				while (tempPwd.substring(0,1) == ' ')
				{
					   tempPwd = tempPwd.substring(1);
					   tempLength = tempLength + 1;
				}
				if ( pwdLength == tempLength)
				{
					   window.alert("Spaces are not allowed as Password");
					   personallogin.password.value = "";
					   personallogin.password.focus();
					   return false;
				}
			}
	}
	return true;
}
// This function validates email format.
function validateEmail(email)
{
		   var splitted = email.match("^(.+)@(.+)$");
		   if(splitted == null) return false;
		   if(splitted[1] != null )
		   {
				var regexp_user=/^\"?[\w-_\.]*\"?$/;
				if(splitted[1].match(regexp_user) == null)
				{
					return false;
				}
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