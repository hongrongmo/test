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

  var thissearchid;
  function getNavigators(searchid)
  {
    thissearchid =  searchid;
    createXMLHttpRequestNav();
    var url = "/controller/servlet/Controller?CID=dynamicNavigators&searchId="+searchid+"&timestamp="+(new Date()).getTime();
    xmlHttpNav.open("GET", url, true);
    xmlHttpNav.onreadystatechange = callbackNavigators;
    xmlHttpNav.send(null);
  }

  function showTip() {
    Tip(this.getAttribute("tooltip"),WIDTH,450);
  }
  function showChart() {
    window.open('/controller/servlet/Controller?CID=analyzeNav&SEARCHID=' + this.searchid + '&database=188423&field=' + this.navfield)
  }
  function showDownload() {
    Tip(this.getAttribute("tooltip"),WIDTH,450);
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
          var navcontainer = document.getElementById("navigators");
          navcontainer.style.border = "0px black solid";
          /* navdiv.style.background="#c3c8d1";*/

          var navigators = xmlDocument.getElementsByTagName("NAVIGATOR")
          for(var count = 0; count < navigators.length; count++)
          {
            var navfield = navigators[count].getAttribute("FIELD")

            var navfieldset = document.createElement("fieldset");
            var navlegend = document.createElement("legend");
            var legendlink = document.createElement("a");
            legendlink.id = navfield + "link";
            legendlink.setAttribute("href","javascript:toggleNavigator('" + navfield + "')");
            legendlink.className="MedOrangeText";
            legendlink.title = "Toggle facet";
            legendlink.appendChild(document.createTextNode("- " + navigators[count].getAttribute("LABEL")));

            var downloadimg = document.createElement("img");
            downloadimg.src = "/engresources/images/note.gif";
            downloadimg.border = 0;
            var downloadlink = document.createElement("a");
            downloadlink.id = navfield + "download";
            downloadlink.setAttribute("href","#");
            downloadlink.title = "Download data";
            downloadlink.appendChild(downloadimg);

            var chartimg = document.createElement("img");
            chartimg.src = "/engresources/images/gr_img.gif";
            chartimg.border = 0;
            var chartlink = document.createElement("a");
            chartlink.id = navfield + "chart";
            chartlink.navfield = navfield;
            chartlink.searchid = thissearchid;
            chartlink.onclick = showChart;

            chartlink.title = "View chart";
            chartlink.appendChild(chartimg);

            navlegend.appendChild(legendlink);
            navlegend.appendChild(document.createTextNode("&nbsp;"));
            navlegend.appendChild(chartlink);
            navlegend.appendChild(document.createTextNode("&nbsp;"));
            navlegend.appendChild(downloadlink);

            navfieldset.appendChild(navlegend);

            var modul = newUL();
            modul.id = navfield;
            modul.setAttribute("shown",MODSTATECOUNT);
            modul.setAttribute("visible","true");

            var mods = navigators[count].getElementsByTagName("MODIFIER");
            for(var modcount = 0; (modcount < mods.length); modcount++)
            {
              var modid = navfield + "nav" + modcount;
              var modifier_count = mods[modcount].getAttribute("COUNT");
              var modifier_label = mods[modcount].getElementsByTagName("LABEL")[0].firstChild.nodeValue;
              var modifier_value = mods[modcount].getElementsByTagName("VALUE")[0].firstChild.nodeValue;

              /* create checkbox */
              var modchk = document.createElement("input");
              modchk.setAttribute("type","checkbox");
              modchk.id = modid;
              modchk.name = navfield + "nav";
              modchk.value =  modifier_count + "~" + modifier_value  + "~" + modifier_label;
              /* create label */
              var modlbl = document.createElement("label");
              modlbl.htmlFor = modid;
              modlbl.appendChild(document.createTextNode(modifier_label));
              modlbl.appendChild(document.createTextNode(" (" + modifier_count+ ")"));

              var modanchor = document.createElement("a");

              if(mods[modcount].childNodes.length > 2)
              {
                var mouseover_title = mods[modcount].getElementsByTagName("TITLE")[0].firstChild.nodeValue;
                modanchor.setAttribute("tooltip",mouseover_title);
                modanchor.onmouseover = showTip;
                modanchor.onmouseout = UnTip;

               // modanchor.addEventListener( "mouseover", showTip, false);
               // modanchor.addEventListener( "mouseout", UnTip, false);
                if((navfield == "bkt") || (navfield = "bks"))
                {
                  modanchor.className = "SmBlueText";
                }
                else
                {
                  modanchor.className = "SmBoldBlueText2";
                }
              }
              else
              {
                modlbl.className = "SmBlackText";
              }

              /* create listitem */
              var modli = document.createElement("li");
              modli.style.display="none";

              /* append checkbox and label */
              modanchor.appendChild(modlbl);
              modli.appendChild(modchk);
              //modli.appendChild(modlbl);
              modli.appendChild(modanchor);

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

            var divpagers = document.createElement("div");
            divpagers.style.textAlign="right";
            divpagers.id = navfield + "pagers";

            if(modul.childNodes.length > MODSTATECOUNT)
            {
              var modpager = newPager(navfield, MORE);
              divpagers.appendChild(modpager);
            }

            navfieldset.appendChild(divpagers);
            navcontainer.appendChild(navfieldset);
          }
        }
      }
    }
  }

  function toggleNavigator(navfieldid)
  {
    var navul = document.getElementById(navfieldid);
    if(navul != null)
    {
      var pagerdiv = document.getElementById(navfieldid + "pagers");
      var isvisible = navul.getAttribute("visible");
      var legendlink = document.getElementById(navfieldid + "link");
      var label = legendlink.firstChild.nodeValue;

      if(isvisible == "true")
      {
        navul.style.display="none";
        pagerdiv.style.display="none";
        navul.setAttribute("visible","false");
        label = "+" + label.substring(1);
      }
      else
      {
        navul.style.display="block";
        pagerdiv.style.display="block";
        navul.setAttribute("visible","true");
        label = "-" + label.substring(1);
      }
      legendlink.firstChild.nodeValue = label;
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
        navdiv.childNodes[lindex-1].style.display="none";
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
    modpager.innerHTML = (linklabel == MORE) ? linklabel + "&#133" : "&#133" + linklabel;


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
