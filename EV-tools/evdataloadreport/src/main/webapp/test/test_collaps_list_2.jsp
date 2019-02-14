<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="jquery-1.4.2.min.js" type="text/javascript"></script>
<script type="text/javascript">

$(document).ready(function() { 
	$('li.category').addClass('plusimageapply'); $('li.category').children().addClass('selectedimage'); $('li.category').children().hide(); 
	$('li.category').each( function(column) { 
	$(this).click(function(event){ 
	if (this == event.target) { 
	if($(this).is('.plusimageapply')) { 
	$(this).children().show(); 
	$(this).removeClass('plusimageapply'); $(this).addClass('minusimageapply'); 
	} else { 
	$(this).children().hide(); 
	$(this).removeClass('minusimageapply'); $(this).addClass('plusimageapply'); 
	} 
	} 
	}); 
	} ); 
	});

</script>

<style type="text/css">
.plusimageapply{ 
list-style-image:url(../Images/plus.png); cursor:pointer; } 
.minusimageapply{ list-style-image:url(../Images/minus.png); cursor:pointer; } 
.selectedimage{ list-style-image:url(../Images/selected.png); cursor:pointer; }


</style>
</head>


<body>
<ul> 
<li class="category">Design 
<ul> 
<li>Graphic Design</li> 
<li class="category">Web Design 
<ul> <li>HTML</li> 
<li>CSS</li> 
</ul> 
</li> 
<li>Print</li> 
</ul> 
</li> 
<li class="category">Development 
<ul> 
<li>PHP</li> 
<li>Java</li> 
</ul> 
</li> 
</ul>
 
</body>
</html>