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

$(document).ready(function() {
	$("span.encompassOpenClose a").click(function(event) {
		event.preventDefault();
		var parent = $(this).parent();
		var termtype = parent.attr('termtype');
		var docid = parent.attr('docid');
		var terms = parent.attr('data');
		var isopen = eval(parent.attr('isopen'));
		if (!isopen) {
			draw(terms, termtype, docid);
		} else {
			clearTerms(termtype, docid);
		}
		parent.find("a").toggle();
		parent.attr('isopen',!isopen);
	});
	
});


function findLongLinkTerms(mid) {
	var nameschid = "schid" + mid;
	varMid = mid;
	schid = document.getElementById(nameschid).value;
	var nameistag = "istag" + mid;
	var varistag = document.getElementById(nameistag).value;
	var date = new Date();
	var url = "/search/doc/enclongterms.url?searchid="
			+ schid + "&istag=" + varistag + "&docid=" + mid + "&timestamp="
			+ date.getTime();
	varTerms = "Retrieving...";
	$.ajax({
			url:url, 
			success: function(data) {
				initVarsEncFields(data, "longlt", varMid);
				varTerms = data;
				drawTerms("longlt", varMid);
			},
			error: function() {
				varTerms = "An error has occurred.";
			}
	});
}

function initVarsEncFields(terms, termtype, docid) {

	varTerms = terms;
	var nameDiv = termtype + "div" + docid;
	varDiv = document.getElementById(nameDiv);
	var nameInputField = termtype + "field" + docid;
	varInputField = document.getElementById(nameInputField);
	var nameTableBody = termtype + "_table_body" + docid;
	varTableBody = document.getElementById(nameTableBody);
	var nameTable = termtype + "_table" + docid;
	varTable = document.getElementById(nameTable);
	return;
}

function draw(terms, termtype, docid) {
	initVarsEncFields(terms, termtype, docid);
	if (termtype == "longlt") {
		findLongLinkTerms(terms);
	}
	setOffsetTerms(termtype, docid);
	drawTerms(termtype, docid);
	return false;
}

function setOffsetTerms(termtype, docid) {
	var gend = varInputField.offsetWidth;
	var gleft = calculateOffsetLeftGroup1(varInputField) + gend;
	var gtop = calculateOffsetTopGroup1(varInputField);
	varDiv.style.border = "black 0px solid";
	varDiv.style.left = gleft + "px";
	varDiv.style.top = gtop + "px";
	varTable.style.width = "360px";

}

function clearTerms(termtype, docid) {
	var nameTableBody = termtype + "_table_body" + docid;
	varTableBody = document.getElementById(nameTableBody);
	var nameDiv = termtype + "div" + docid;
	varDiv = document.getElementById(nameDiv);

	if (varTableBody != null) {
		var gi = varTableBody.childNodes.length;
		for ( var i = gi - 1; i >= 0; i--) {
			varTableBody.removeChild(varTableBody.childNodes[i]);
		}
		varDiv.style.border = "none";
	}
}

function drawTerms(termtype, docid) {

	clearTerms(termtype, docid);
	var allterms = new Array();
	if (termtype == "atm") {
		allterms = varTerms.split("</br>");
	} else {
		allterms = varTerms.split("|");
	}
	var size = allterms.length;
	var row, cell, txtNode;
	for ( var i = 0; i < size; i++) {
		var nextNode = $.trim(allterms[i]);

		if (nextNode != "") {
			row = document.createElement("tr");
			cell = document.createElement("td");
			cell.tabIndex = 1;
			cell.style.paddingLeft = "5px";

			var sepimg = document.createElement("img");
			if (termtype != "atm") {
				sepimg
						.setAttribute("src",
								"/static/images/separator.gif");
				sepimg.setAttribute("height", "7");
				sepimg.setAttribute("width", "7");
				sepimg.setAttribute("alt", "separ");
				cell.appendChild(sepimg);
			}

			cell.innerHTML += " " + nextNode;
			row.appendChild(cell);
			varTableBody.appendChild(row);
		}
	}
	return false;

}

function calculateOffsetLeftGroup1(gfield) {
	return calculateOffsetGroup1(gfield, "offsetLeft");
}
function calculateOffsetTopGroup1(gfield) {
	return calculateOffsetGroup1(gfield, "offsetTop");
}
function calculateOffsetGroup1(gfield, gattr) {
	var goffset = 0;
	while (gfield) {
		goffset += gfield[gattr];
		gfield = gfield.offsetParent;
	}
	return goffset;
}