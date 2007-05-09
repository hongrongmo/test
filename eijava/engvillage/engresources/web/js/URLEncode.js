function URLEncode(plaintext)
{
	
	var SAFECHARS = "0123456789" +					
					"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +	
					"abcdefghijklmnopqrstuvwxyz" +
					"-_.!~*'()";					
	var HEX = "0123456789ABCDEF";

	var encoded = "";
	for (var i = 0; i < plaintext.length; i++ ) 
	{
		var ch = plaintext.charAt(i);
	    if (ch == " ") 
	    {
		    encoded += "+";	// x-www-urlencoded, rather than %20
	    } 
	    else if (SAFECHARS.indexOf(ch) != -1) 
	    {
		    encoded += ch;
	    } 
	    else 
	    {
		var charCode = ch.charCodeAt(0);
		encoded += "%";
		encoded += HEX.charAt((charCode >> 4) & 0xF);
		encoded += HEX.charAt(charCode & 0xF);
	    }
	} 

	return encoded;
}