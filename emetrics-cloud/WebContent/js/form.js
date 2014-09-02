<!--
function setMin() {
	var j= 0;
	for(var i= 0; i< document.reportForm.endmonth.length; i++) {
		if(document.reportForm.endmonth[i].selected) {
			j= i;
			break;
		}
	}
	var k= 0;
	for(var i= 0; i< document.reportForm.month.length; i++) {
		if(document.reportForm.month[i].selected) {
			k= i;
			break;
		}
	}

	if (k>j) {
		for (var i= 0; i< document.reportForm.endmonth.length; i++) {
			if (i==k) {
				document.reportForm.endmonth[i].selected = true;
			} else {
				document.reportForm.endmonth[i].selected = false;
			}
		}
		alert("Starting month can not be greater than ending month.");
	}
}

function setMax() {
	var j= 0;
	for(var i= 0; i< document.reportForm.endmonth.length; i++) {
		if(document.reportForm.endmonth[i].selected) {
			j= i;
			break;
		}
	}
	var k= 0;
	for(var i= 0; i< document.reportForm.month.length; i++) {
		if(document.reportForm.month[i].selected) {
			k= i;
			break;
		}
	}

	if (j<k) {
		for (var i= 0; i< document.reportForm.month.length; i++) {
			if (i==j) {
				document.reportForm.month[i].selected = true;
			} else {
				document.reportForm.month[i].selected = false;
			}
		}
		alert("Ending month can not be less than starting month.");
	}
}

function setMinD() {
	var j= 0;
	for(var i= 0; i< document.reportForm.startday.length; i++) {
		if(document.reportForm.startday[i].selected) {
			j= i;
			break;
		}
	}
	var k= 0;
	for(var i= 0; i< document.reportForm.endday.length; i++) {
		if(document.reportForm.endday[i].selected) {
			k= i;
			break;
		}
	}

	if (k>j) {
		for (var i= 0; i< document.reportForm.startday.length; i++) {
			if (i==k) {
				document.reportForm.startday[i].selected = true;
			} else {
				document.reportForm.startday[i].selected = false;
			}
		}
		alert("Starting month can not be greater than ending month.");
	}
}

function setMaxD() {
	var j= 0;
	for(var i= 0; i< document.reportForm.startday.length; i++) {
		if(document.reportForm.startday[i].selected) {
			j= i;
			break;
		}
	}
	var k= 0;
	for(var i= 0; i< document.reportForm.endday.length; i++) {
		if(document.reportForm.endday[i].selected) {
			k= i;
			break;
		}
	}

	if (j<k) {
		for (var i= 0; i< document.reportForm.endday.length; i++) {
			if (i==j) {
				document.reportForm.endday[i].selected = true;
			} else {
				document.reportForm.endday[i].selected = false;
			}
		}
		alert("Ending month can not be less than starting month.");
	}
}

-->