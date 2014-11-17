/**
 * Run some code at document ready to prepare the
 * search results page for user interaction
 */
$(document).ready(function() {

	// Bubble box is DISABLED by default so that it can only
	// be used with JS on!
	$("#tagsgroups").show();

	//
	// Hovers
	//
	$("a.cloudtag").bind('mouseover', function() {
		$(this).css("background-color","#cccccc");
	});
	$("a.cloudtag").bind('mouseout', function() {
		$(this).css("background-color","#ffffff");
	});
	$("li.vieweditgroups").bind('mouseover', function() {
		$(this).css("background-image","url('/static/images/New_Tag_On.png')");
	});
	$("li.vieweditgroups").bind('mouseout', function() {
		$(this).css("background-image","url('/static/images/New_Tag.png')");
	});
	$("li.renametag").bind('mouseover', function() {
		$(this).css("background-image","url('/static/images/Edit_On.png')");
	});
	$("li.renametag").bind('mouseout', function() {
		$(this).css("background-image","url('/static/images/Edit.png')");
	});
	$("li.deletetag").bind('mouseover', function() {
		$(this).css("background-image","url('/static/images/Delete_On.png')");
	});
	$("li.deletetag").bind('mouseout', function() {
		$(this).css("background-image","url('/static/images/Delete.png')");
	});
	
	//
	// Handle the cloud select
	//
	$("#cloudselect").change(function(event) {
		var sort = $("#tagdisplayform input[name='sort']").val();
		var scope = $(this).val();
		if (scope == "6") {
			document.location = "/customer/authenticate/loginfull.url?backurl=/tagsgroups/display.url";
		} else {
			document.location = "/tagsgroups/display.url?scope="+scope+"&sort="+sort;
		}
	});
	
	$("#searchscopeselect").change(function(event) {
		var sort = $("#searchtagsform input[name='sort']").val();
		var scope = $(this).val();
		if (scope == "6") {
			document.location = "/customer/authenticate/loginfull.url?backurl=/tagsgroups/display.url";
		} 
	});
	
	//
	// Handle various validation on groups
	//
	$(".groupdeletelink").click(function(event) {
		var doit = confirm("Are you sure you wish to delete this group?");
		if (!doit) event.preventDefault();
	});
	$(".groupcancellink").click(function(event) {
		var doit = confirm("Are you sure you wish to cancel your membership?");
		if (!doit) event.preventDefault();
	});
	
	// 
	// Handle create group form submit
	//
	$("#groupsform").submit(function(event) {
		var groupname = $(this).find("input[name='groupname']");
		if (groupname.length == 0 || !groupname.val()) {
			alert("Group name is required.");
			event.preventDefault();
		}
		var groupdescription = $(this).find("textarea[name='groupdescription']");
		if (groupdescription.length > 0 && groupdescription.val().length > 300) {
			alert("You may only enter a maximum of 300 characters.  Please enter a shorter description.");
			event.preventDefault();
		}
	});
	
	// 
	// Handle rename tag form submit
	//
	$("#renametag").submit(function(event) {
		var newtag= encodeURIComponent($(this).find("input[name='newtag']").val());
		if (newtag.length == 0){
			  window.alert("New Tag name cannot be empty");
			  return false;
		  }else if(newtag == ''){
			  window.alert("Tag Name must be given");
			  return false;
		  }else if (newtag.length > 30){
	      window.alert("New Tag name should not exceed 30 characters");
			  return false;
		  }
		return true; 
	});

	// 
	// Handle delete tag form submit
	//
	$("#deletetag").submit(function(event) {
		var dtag =  encodeURIComponent(($("#deletetag").find("option:selected").val()));
		var url = "/tagsgroups/deletetag.url";
		var scopevalue = ($("#scopeOption").find("option:selected").val());
		url = url +"&scope="+scopevalue +"&deletetag="+dtag;
		document.location = url;
		return 
	});

});

$("#scopeOption").change(
	function(event) {
		document.location = $(this).find('option:selected').attr('href');
		return;
	});




