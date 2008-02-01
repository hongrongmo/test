  function includeNavigators()
  {

  }

  var xmlHttpNav;
  var MODSTATECOUNT = 10;
  var MORE = "more";
  var LESS = "less";

  function createXMLHttpRequestNav()
  {
    try
    {
      xmlHttpNav = new XMLHttpRequest();
    }
    catch (trymicrosoft)
    {
      try
      {
        xmlHttpNav = new ActiveXObject("Msxml2.XMLHTTP");
      }
      catch (othermicrosoft)
      {
        try
        {
          xmlHttpNav = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (failed)
        {
          xmlHttpNav = null;
        }
      }
    }
    if (xmlHttpNav == null)
    {
      alert("Error creating request object!");
    }
  }

  function getNavigators()
  {
    var searchid = "";
    createXMLHttpRequestNav();
    var url = "/controller/servlet/Controller?CID=dynamicNavigators&searchId="+searchid+"&timestamp="+(new Date()).getTime();
    xmlHttpNav.open("GET", url, true);
    xmlHttpNav.onreadystatechange = callbackNavigators;
    xmlHttpNav.send(null);
  }

  function callbackNavigators()
  {
    if (xmlHttpNav.readyState == 4)
    {
      if (xmlHttpNav.status == 200)
      {
        var xmlDocument = xmlHttpNav.responseXML;
        if(typeof(xmlDocument) != 'undefined')
        {
          var navdiv = document.getElementById("navigators");
          navdiv.style.border = "0px black solid";
          navdiv.style.background="#c3c8d1";

          var navigators = xmlDocument.getElementsByTagName("NAVIGATOR")
          for(var count = 0; count < navigators.length; count++)
          {
            navfieldset = document.createElement("fieldset");
            navlegend = document.createElement("legend");
            navlegend.appendChild(document.createTextNode(navigators[count].getAttribute("LABEL")));
            navlegend.className="MedOrangeText";
            navfieldset.appendChild(navlegend);

            var navfield = navigators[count].getAttribute("FIELD")
            var modul = newUL();
            modul.id = navfield;
            modul.setAttribute("shown",MODSTATECOUNT);

            var mods = navigators[count].getElementsByTagName("MODIFIER");
            for(var modcount = 0; (modcount < mods.length); modcount++)
            {
              var modid = navfield + modcount;
              /* create checkbox */
              modchk = document.createElement("input");
              modchk.setAttribute("type","checkbox");
              modchk.id = modid;
              /* create label */
              modlbl = document.createElement("label");
              modlbl.className = "SmBlackText";
              modlbl.htmlFor = modid;
              modlbl.appendChild(document.createTextNode(mods[modcount].getElementsByTagName("LABEL")[0].firstChild.nodeValue));
              modlbl.appendChild(document.createTextNode(" (" + mods[modcount].getAttribute("COUNT") + ")"));

              /* create listitem */
              modli = document.createElement("li");
              modli.style.display="none";

              /* append checkbox and label */
              modli.appendChild(modchk);
              modli.appendChild(modlbl);

              /* don't show them all */
              if(modcount < MODSTATECOUNT)
              {
                modli.style.display="block";
              }
              /* append modifier listitem */
              modul.appendChild(modli);
            }
            /* append unordered list of modifiers */
            navfieldset.appendChild(modul);

            /* <PAGERS FIELD="yr"><MORE COUNT="20"/></PAGERS> */
            var divpagers = document.createElement("div");
            divpagers.style.textAlign="right";
            divpagers.id = navfield + "pagers";

            if(modul.childNodes.length > MODSTATECOUNT)
            {
              var modpager = newPager(navfield, MORE);
              divpagers.appendChild(modpager);
            }

            navfieldset.appendChild(divpagers);
            navdiv.appendChild(navfieldset);
          }
        }
      }
    }
  }

  function pageModifiers(navfieldid,direction)
  {
    var navdiv = document.getElementById(navfieldid);
    var shown = navdiv.getAttribute("shown") * 1;
    if(direction == MORE)
    {
      var lindex = shown;
      for(; ((lindex < navdiv.childNodes.length) && (lindex < shown + MODSTATECOUNT)); lindex++)
      {
        navdiv.childNodes[lindex].style.display="block";
      }
      navdiv.setAttribute("shown",lindex);
    }
    else if(direction == LESS)
    {
      var lindex = shown;
      var decrement = ((shown % MODSTATECOUNT) == 0) ? MODSTATECOUNT : (shown % MODSTATECOUNT);
      for(; ((shown > 0) && (lindex > shown - decrement)); lindex--)
      {
        navdiv.childNodes[lindex].style.display="none";
      }
      navdiv.setAttribute("shown",lindex);
    }
    var divpagers =  document.getElementById(navfieldid + "pagers");
    while(divpagers.firstChild) divpagers.removeChild(divpagers.firstChild);

    nowshown = navdiv.getAttribute("shown") * 1;
    if(nowshown > MODSTATECOUNT)
    {
      var modpager = newPager(navfieldid, LESS);
      divpagers.appendChild(modpager);
      divpagers.appendChild(document.createTextNode(" "));
    }
    if(nowshown < navdiv.childNodes.length)
    {
      var modpager = newPager(navfieldid, MORE);
      divpagers.appendChild(modpager);
    }

  }

  function newPager(navfield, linklabel)
  {
    var modpager = document.createElement("a");
    modpager.className="MedBlueLink";
    modpager.setAttribute("href","javascript:pageModifiers('" + navfield + "','" + linklabel + "')");
    modpager.appendChild(document.createTextNode(linklabel));
    modpager.appendChild(document.createTextNode('\u0085'));


    return modpager;
  }

  function newUL()
  {
    var uldiv = document.createElement("ul");
    uldiv.style.listStyleType = "none";
    uldiv.style.margin = "0";
    uldiv.style.padding = "0";
    uldiv.style.marginBottom = "1px";

    return uldiv;
  }
  getNavigators();
