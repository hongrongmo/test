<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test List Expand-Collaps</title>

<script type="text/javascript" src="jquery-1.4.2.min.js">
        </script>
        <script type="text/javascript" src="scripts.js">
        </script>
        
        

<style type="text/css">
#listContainer{
  margin-top:15px;
}
 
#expList ul, li {
    list-style: none;
    margin:0;
    padding:0;
    cursor: pointer;
}
#expList p {
    margin:0;
    display:block;
}
#expList p:hover {
    background-color:#121212;
}
#expList li {
    line-height:140%;
    text-indent:0px;
    background-position: 1px 8px;
    padding-left: 20px;
    background-repeat: no-repeat;
}
 
/* Collapsed state for list element */
#expList .collapsed {
    background-image: url(collapsed.png);
}
/* Expanded state for list element
/* NOTE: This class must be located UNDER the collapsed one */
#expList .expanded {
    background-image: url(expanded.png);
}
</style>


</head>


<body>

<div id="listContainer">
  <ul id="expList">
    <li>Item A
      <ul>
        <li>Item A.1
          <ul>
            <li>
              <span>Lorem ipsum dolor sit amet</span>
            </li>
          </ul>
        </li>
        <li>Item A.2</li>
        <li>Item A.3
          <ul>
            <li>
              <span>Lorem ipsum dolor sit amet</span>
            </li>
          </ul>
        </li>
      </ul>
    </li>
    <li>Item B</li>
    <li>Item C
      <ul>
        <li>Item C.1</li>
        <li>Item C.2
          <ul>
            <li>
              <span>Lorem ipsum dolor sit amet</span>
            </li>
          </ul>
        </li>
      </ul>
    </li>
  </ul>
</div>

</body>
</html>