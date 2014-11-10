		var xmlHttp;
        var completeDiv;
        var inputField;
        var nameTable;
        var nameTableBody;
        var date;
        var scopegroup = "";


        function createXMLHttpRequest()
        {
            try
            {
                xmlHttp = new XMLHttpRequest();
            }
            catch (trymicrosoft)
            {
                try
                {
                    xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
                }
                catch (othermicrosoft)
                {
                    try
                    {
                        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
                    }
                    catch (failed)
                    {
                        xmlHttp = null;
                    }
                }
            }
            if (xmlHttp == null)

                alert("Error creating request object!");
        }

        function initVars()
        {
            inputField = document.getElementById("tagname");
            nameTable = document.getElementById("name_table");
            completeDiv = document.getElementById("popup");
            nameTableBody = document.getElementById("name_table_body");

            date = new Date();
        }


       function findTags()
       {
            initVars();
            if (inputField.value.length >0 )
            {
                createXMLHttpRequest();
				var url = "/controller/servlet/Controller?CID=tagAutocomplete&tag=" + escape(inputField.value)+"&timestamp="+date.getTime();
                xmlHttp.open("GET", url, true);
                xmlHttp.onreadystatechange = callback;
                xmlHttp.send(null);
            }
            else
            {
                clearTagnames();
            }
        }

	   function setTagnames(names)
	   {

			var the_names = new Array();
			the_names= names.split(";");

			clearTagnames();
			var size = the_names.length;
			setOffsets();

			var row, cell, txtNode;
			for (var i = 0; i < size; i++)
			{
				var nextNode = the_names[i];
				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.tabIndex =1;

				cell.onmouseout = function() {this.className='mouseOver';};
				cell.onmouseover = function() {this.className='mouseOut';};
				cell.style.paddingLeft="5px";
				cell.setAttribute("bgcolor", "#FFFAFA");
				cell.setAttribute("class", "SmBlackText");
				cell.setAttribute("border", "0");
				cell.AutoPostBack;
				cell.onclick = function() { populateName(this); } ;
				cell.onfocus = function() { populateName1(this); } ;

				txtNode = document.createTextNode(nextNode);
				cell.appendChild(txtNode);
				row.appendChild(cell);
				nameTableBody.appendChild(row);
			}

			inputField.onclick  =  function(){clearTagnames()};
		}

        function callback()
        {
            if (xmlHttp.readyState == 4)
            {
                if (xmlHttp.status == 200)
                {
					var names = xmlHttp.responseXML;
					var tag = names.getElementsByTagName("body");

					if (tag != null &&  tag[0].firstChild != null)
					{
						if (tag[0].firstChild.nodeValue != null)
						{

							setTagnames(tag[0].firstChild.nodeValue);
						}
					}
					else if (tag != null && tag[0].firstChild == null)
					{
						clearTagnames();
					}
                }
                else if (xmlHttp.status == 204)
                {
                    clearTagnames();
                }
            }
        }

        function setOffsets()
        {
            var end = inputField.offsetWidth;
            var left = calculateOffsetLeft(inputField);
            var top = calculateOffsetTop(inputField) + inputField.offsetHeight;
            completeDiv.style.border = "black 1px solid";
            completeDiv.style.left = left + "px";
            completeDiv.style.top = top + "px";
            nameTable.style.width = end + "px";
        }


        function calculateOffsetLeft(field)
        {
        	return calculateOffset(field, "offsetLeft");
        }

        function calculateOffsetTop(field)
        {
        	return calculateOffset(field, "offsetTop");
        }

        function calculateOffset(field, attr)
        {
            var offset = 0;
            while(field)
            {
                offset += field[attr];
                field = field.offsetParent;
            }
            return offset;
        }

        function populateName(cell)
        {
            inputField.value = cell.firstChild.nodeValue;
            clearTagnames();
        }


        function populateName1(cell)
        {
            inputField.value = cell.firstChild.nodeValue;
        }

        function clearTagnames()
        {

            var ind = nameTableBody.childNodes.length;
            for (var i = ind - 1; i >= 0 ; i--)
            {
                 nameTableBody.removeChild(nameTableBody.childNodes[i]);
            }
            completeDiv.style.border = "none";
        }


        function selectedRange(selectform,resultsform)
	    {

	        var startrange = 0;
	        var endrange = 0;
	        var i=0;
	        var displaytype = null;
	        var  resultscount = 400;

		if (addtag.rangefrom.value.length == 0 || addtag.rangeto.value.length == 0)
		{
			window.alert("Start Range and End Range and Tag Name must be given");
			return false;
		}
		else if (addtag.tagname.length == 0)
		{
			window.alert("Tag Name must be given");
			return false;
		}
	      	else  if( ( addtag.rangefrom.value == '') || ( addtag.rangeto.value == ''))
	        {
	        	window.alert("Start Range and End Range must be given");
	            return false;
	        }
	        else  if( addtag.tagname.value == '')
	        {
	            window.alert("Tag Name must be given");
	            return false;
	        }
	        else if((isNaN(addtag.rangefrom.value)) || (isNaN(addtag.rangeto.value)))
	        {
	            window.alert("Please enter numbers only");
	            return false;
	        }
	        else if ( addtag.rangeto.value - addtag.rangefrom.value  > 400 )
	       	{
				window.alert("Selected range cannot exceed 400 records");
				return false;
	        }
	        else if (  addtag.rangefrom.value  > addtag.rangeto.value  )
	       	{
				window.alert("Invalid range");
				return false;
	        }
	        else
	        {
	            var selectFromLength= addtag.rangefrom.value.length;
	            var tempFromWord = addtag.rangefrom.value;
	            var tempFromLength=0;

	            while (tempFromWord.substring(0,1) == ' ')
	            {
	                tempFromWord = tempFromWord.substring(1);
	                tempFromLength = tempFromLength + 1;
	            }

	            var selectToLength = addtag.rangeto.value.length;
	            var tempToWord = addtag.rangeto.value;
	            var tempToLength=0;

	            while (tempToWord.substring(0,1) == ' ')
	            {
	                tempToWord = tempToWord.substring(1);
	                tempToLength = tempToLength + 1;
	            }

	            if (( selectFromLength == tempFromLength) || ( selectToLength == tempToLength) )
	            {
	                window.alert("Spaces are not allowed in the range");
	                return false;
	            }
	            else
	            {
	                return true;
	            } // else

	        } // else

   	} // function