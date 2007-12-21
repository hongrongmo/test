  var varTableBody;
  var varTable;
  var varDiv;
  var varTerms;
  var varInputField;
  var varTerms;
  var varMid;
  var xmlHttpEncFields;
  var schid;
  var istag;

	function createXMLHttpRequestEnc()
	{
    	try
    	{
       		xmlHttpEncFields = new XMLHttpRequest();
    	}
    	catch (trymicrosoft)
    	{
			try
			{
				xmlHttpEncFields = new ActiveXObject("Msxml2.XMLHTTP");
			}
			catch (othermicrosoft)
			{
				try
				{
					xmlHttpEncFields = new ActiveXObject("Microsoft.XMLHTTP");
				}
				catch (failed)
				{
					xmlHttpEncFields = null;
				}
			}
    	}
    	if (xmlHttpEncFields == null)
    	{
        alert("Error creating request object!");
      }
	}

	function findLongLinkTerms(mid)
	{
	   	var nameschid = "schid"+mid;
	   	varMid = mid;
      schid = document.getElementById(nameschid).value;
     	var nameistag = "istag"+mid;
      var varistag = document.getElementById(nameistag).value;
    	var date = new Date();
    	createXMLHttpRequestEnc();
    	var url = "/controller/servlet/Controller?CID=encLongterms&searchId="+schid+"&istag="+varistag+"&docid="+mid+"&timestamp="+date.getTime();
    	xmlHttpEncFields.open("GET", url, true);
    	xmlHttpEncFields.onreadystatechange = callbackEncFields;
    	xmlHttpEncFields.send(null);

	}


	function callbackEncFields()
	{

    	if (xmlHttpEncFields.readyState == 4)
    	{

        	if (xmlHttpEncFields.status == 200)
        	{
            	var allterms = xmlHttpEncFields.responseXML;
            	var terms = allterms.getElementsByTagName("body");
            	if (terms != null && terms[0].firstChild != null)
            	{
                	if (terms[0].firstChild.nodeValue != null)
                	{
                    	var longltTerms = terms[0].firstChild.nodeValue;
          						initVarsEncFields(longltTerms,"longlt", varMid)
          						varTerms = longltTerms;
                    	drawTerms("longlt", varMid);
                	}
            	}
        	}
    	}
	}


	function flipEncImg(terms, termtype, docid)
	{
		var varid = termtype +docid;

			// if image set to plus open change to minuse close and drow terms
			// else clearTerms and set img to plus open

				//draw(terms, termtype);
				var imgname = termtype+'OpenClose'+docid;
				var elementimg = document.getElementById(imgname);
				var imgsrc = elementimg.src;

				var imgTermname = termtype+docid;
				var elementimgTerm = document.getElementById(imgTermname);
				var imgTermsrc = elementimg.src;

				if (imgsrc.indexOf('Close') > 0)
				{
					clearTerms(termtype, docid);
					elementimgTerm.src="/engresources/images/encPlus.gif";
				    if(termtype == "lst")
				    {
				        elementimg.src="/engresources/images/encLinkedOpen.gif";
				    }
				    else if (termtype == "mlt")
				    {
				    	elementimg.src="/engresources/images/encMltOpen.gif";
				    }
				    else if (termtype == "atm")
				    {
				    	elementimg.src="/engresources/images/encTemplatesOpen.gif";
				    }
				    else if (termtype == "longlt")
				    {
				    	elementimg.src="/engresources/images/encLinkedOpen.gif";
				    }
				    else
				    {
				    	elementimg.src="/engresources/images/encLinkedOpen.gif";
				    }
				}
				else
				{
				    draw(terms, termtype, docid );
				    elementimgTerm.src="/engresources/images/encMinus.gif";
				    if(termtype == "lst")
				    {
				        elementimg.src="/engresources/images/encLinkedClose.gif";
				    }
				    else if (termtype == "mlt")
				    {
				    	elementimg.src="/engresources/images/encMltClose.gif";
				    }
				    else if (termtype == "atm")
				    {
				    	elementimg.src="/engresources/images/encTemplatesClose.gif";
				    }
				    else if (termtype == "longlt")
				    {
				    	elementimg.src="/engresources/images/encLinkedClose.gif";
				    }
				    else
				    {
				    	elementimg.src="/engresources/images/encLinkedClose.gif";
				    }
				}

	}

	function initVarsEncFields(terms,termtype, docid)
	{

			varTerms = terms;
			var nameDiv = termtype+"div"+docid;
			varDiv = document.getElementById(nameDiv);
			var nameInputField = termtype+"field"+docid;
			varInputField = document.getElementById(nameInputField);
			var nameTableBody = termtype+"_table_body"+docid;
			varTableBody = document.getElementById(nameTableBody);
			var nameTable = termtype+"_table"+docid;
			varTable = document.getElementById(nameTable);

			if (termtype == "longlt")
    	{
        	varTerms = "Retrieving...";
    	}
    	return;
	}



  	function draw(terms, termtype, docid)
    {
      initVarsEncFields(terms,termtype, docid);
      if(termtype == "longlt")
      {
          findLongLinkTerms(terms);
      }
  		setOffsetTerms(termtype , docid);
  		drawTerms(termtype, docid);
  		return false;
  	}


    function setOffsetTerms(termtype, docid)
	  {
			var gend = varInputField.offsetWidth;
			var gleft = calculateOffsetLeftGroup1(varInputField)+gend;
			var gtop = calculateOffsetTopGroup1(varInputField);
			varDiv.style.border = "black 0px solid";
			varDiv.style.left = gleft + "px";
			varDiv.style.top = gtop + "px";
			varTable.style.width = "360px";

	  }


	function clearTerms(termtype, docid)
	{
			var nameTableBody = termtype+"_table_body"+docid;
			varTableBody = document.getElementById(nameTableBody);
			var nameDiv = termtype+"div"+docid;
			varDiv = document.getElementById(nameDiv);

			if (varTableBody != null )
			{
				var gi = varTableBody.childNodes.length;
				for (var i = gi - 1; i >= 0 ; i--)
				{
					varTableBody.removeChild(varTableBody.childNodes[i]);
				}
				varDiv.style.border = "none";
			}
	}



	function drawTerms(termtype, docid)
	{

		clearTerms(termtype, docid);
		var allterms = new Array();
		if(termtype == "atm")
		{
			allterms= varTerms.split("</br>");
		}
		else
		{
      allterms = varTerms.split("|");
    }
    var size = allterms.length;
		var row, cell, txtNode;
		for (var i = 0; i < size; i++)
		{
			var nextNode = allterms[i];
			row = document.createElement("tr");
			cell = document.createElement("td");
			cell.tabIndex =1;
			cell.style.paddingLeft="5px";

			if (nextNode != "")
			{
				var sepimg = document.createElement("img");
				if (termtype != "atm")
				{
					sepimg.setAttribute("src", "/engresources/images/separator.gif");
				}
	    		sepimg.setAttribute("height", "7");
	    		sepimg.setAttribute("width", "7");
	    		sepimg.setAttribute("alt", "separ");
	    		cell.appendChild(sepimg);

	    		var spaceimg = document.createElement("img");
	    		spaceimg.setAttribute("src", "/engresources/images/s.gif");
	    		spaceimg.setAttribute("height", "7");
	    		spaceimg.setAttribute("width", "5");
	    		cell.appendChild(spaceimg);
	    	}

			cell.style.paddingLeft="5px";

			if (nextNode != "" && termtype != "atm")
			{
				nextNode = "<img src=\"/engresources/images/separator.gif\">"  + nextNode;
			}
			cell.setAttribute("className", "SmBlackText");
			cell.setAttribute("class", "SmBlackText");
			cell.innerHTML = nextNode;
			row.appendChild(cell);

			varTableBody.appendChild(row);

		}
		return false;

	}


	function calculateOffsetLeftGroup1(gfield)
	{
		return calculateOffsetGroup1(gfield, "offsetLeft");
	}
	function calculateOffsetTopGroup1(gfield)
	{
		return calculateOffsetGroup1(gfield, "offsetTop");
	}
	function calculateOffsetGroup1(gfield, gattr)
	{
		var goffset = 0;
		while(gfield)
		{
			goffset += gfield[gattr];
			gfield = gfield.offsetParent;
		}
		return goffset;
	}