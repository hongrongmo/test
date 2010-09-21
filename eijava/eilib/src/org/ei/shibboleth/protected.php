<?php
date_default_timezone_set("America/New_York");
$secret = "tm789@@#mhlflsdf";
$instant = date('Y-m-dH:i:s',time());
if (isset($_SERVER['Shib-Identity-Provider']))
{
   $authCode = md5($instant . $secret . $_SERVER['Shib-Identity-Provider']);
   $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant=' . $instant . '&atok=' . $authCode  . '&idp=' . $_SERVER['Shib-Identity-Provider'];
}
if (isset($_SERVER['Shib-Origin-Site']))
{
  $authCode = md5($instant . $secret . $_SERVER['Shib-Origin-Site']);
  $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant='. $instant . '&atok=' . $authCode  . '&idp=' . $_SERVER['Shib-Origin-Site'];
}
  if($_SERVER['Shib-Identity-Provider'] == "https://idp.protectnetwork.org/protectnetwork-idp")
  {
    if(isset($_SERVER['eppn']) and $_SERVER['eppn'] == "pro_test@idp.protectnetwork.org")
    {
     $authCode = md5($instant . $secret . "https://idp.northumbria.ac.uk/shibboleth");
     $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant=' . $instant . '&atok=' . $authCode  . '&idp=' . "https://idp.northumbria.ac.uk/shibboleth";
    }

    else
    {
      foreach($_SERVER as $i=>$val)
      {
        print $i . "-->" . $val;
        print "<br>";
      }

     exit;
    }
  }
  if($_SERVER['Shib-Identity-Provider'] == "https://login.northumbria.ac.uk/idp/shibboleth")
  {
   $authCode = md5($instant . $secret . "https://idp.northumbria.ac.uk/shibboleth");
   $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant=' . $instant . '&atok=' . $authCode  . '&idp=' . "https://idp.northumbria.ac.uk/shibboleth";
  }
  if($_SERVER['Shib-Identity-Provider'] == "https://shib2idp.rgu.ac.uk/idp/shibboleth")
  {
   $authCode = md5($instant . $secret . "https://mcshib.rgu.ac.uk/shibboleth");
   $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant=' . $instant . '&atok=' . $authCode  . '&idp=' . "https://mcshib.rgu.ac.uk/shibboleth";
  }
  if($_SERVER['Shib-Identity-Provider'] == "https://idp.shibboleth.qmul.ac.uk/idp/shibboleth")
  {
   $authCode = md5($instant . $secret . "https://shibboleth-idp.stu.qmul.ac.uk/idp");
   $reurl = 'http://www.engineeringvillage.com/controller/servlet/ShibbolethService?custid=' . $_GET['custid'] . '&instant=' . $instant . '&atok=' . $authCode  . '&idp=' . "https://shibboleth-idp.stu.qmul.ac.uk/idp";
  }  
print "<meta http-equiv=\"Refresh\" content=\"0;URL=$reurl\">";
print "<title>Shibboleth Redirect </title></head><body>";
print "</body></html>";
exit;

?>



