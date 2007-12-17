function openRemoteDb(name,sessionId,searchtype)
{
    var wnd;
    var wndLocation;
    if (name=="LexisNexis")
    {
        window.open('/controller/servlet/Controller?CID=REDIRECT&URL=%2Fengresources%2Fhelp%2FLexisNexis.html', 'newpg', 'status=yes,resizable,scrollbars=1,width=550,height=450');
    }
    else
    {
        if (name=="Scirus")
        {
            wndLocation="http://www.scirus.com";
        }
        else if (name=="Techstreet Standards")
        {
            wndLocation="http://www.techstreet.com/STD.tmpl?tabName=STD";
        }
        else if (name=="Esp@cenet")
        {
            wndLocation="http://ep.espacenet.com";
        }
        else if (name=="EMSAT")
        {
            wndLocation="http://www.elsevier.com/homepage/sai/emsat_gateway/";
        }
        else if (name=="ReedLink")
        {
            wndLocation="http://www.reedlink.com/";
        }
        else if (name=="KellySearch")
        {
            wndLocation="http://www.kellysearch.com/";
        }
        else if (name=="EEVL")
        {
            wndLocation="http://www.intute.ac.uk/sciences/";
        }
        else if (name=="OJPS")
        {
            wndLocation="http://searchojps.aip.org";
        }
        else if (name=="SPIEWeb")
        {
            wndLocation="http://spie.org/app/publications/index.cfm?fuseaction=advsearch";
        }
        else if (name=="GSP")
        {
            wndLocation="http://engineeringvillage2.globalspec.com/";
        }
        else if(name=="IHS")
        {
            wndLocation="http://global.ihs.com/?RID=EV2";
        }
        else
        {
            document.location = '/controller/servlet/Controller?CID=redirsearch&searchType='+searchtype+'&EISESSION='+sessionId+'&database='+name;
        }

        if(wndLocation != null)
        {
            wnd = window.open(wndLocation, "New_Window", "height="+(screen.height*.75)+",width="+(screen.height*.75)+",top=0,left="+(screen.width*.25)+",scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
            wnd.focus();
            return false;
        }

        if (name=="Referex")
        {
            document.location='/controller/servlet/Controller?CID=keyWordSearch';
        }
    }

    return true;
}