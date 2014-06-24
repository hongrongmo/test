function viewBulletin(url){

	w_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=750,height=600');
	w_window.focus();
}

function refreshYears(){
	
}
var polymers = false;
$(document).ready(function() {
	$("select.catdrop").hide();
	if ($("#radLit").is(":checked")) {
		$("select.lit").show();
	} else {
		$("select.pat").show();
	}
	
	// Handle changing of database
	$("input[name='db']").click(function(event){
		$(".catdrop").toggle();
		$(".catdrop option:first-child").attr("selected","selected");
		if (polymers) {
			$("select.allyears option").remove();
			$("select.restoreyr option").appendTo($("select.allyears"))
			$("select.restoreyr option").remove();
			polymers = false;
		}
	});

	// Polymers only get 2001, 2002
	$("select.catdrop").change(function(event) {
		var sel = $(this).find(":selected");
		if (sel.val() == 'polymers') {
			polymers = true;
			$("select.allyears option").clone().appendTo($("select.restoreyr"))
			$("select.allyears option").filter("[value!='2001']").filter("[value!='2002']").remove();
		} else {
			if (polymers) {
				polymers = false;
				$("select.allyears option").remove();
				$("select.restoreyr option").appendTo($("select.allyears"))
				$("select.restoreyr option").remove();
			}
		}
	});
	
	$("#archiveform").submit(function(event){
		event.preventDefault();
		var db = $("input[name='db']:checked").val();
		var cy = $("select.catdrop:not(:hidden) :selected").val();
		var yr = $("select[name='yr'] :selected").val();
		var url = "/bulletins/archive.url?docIndex=1&cy="+cy+"&yr="+yr+"&db="+db+"&database="+$("#database").val();
		window.location = url;
	});
	
});
	