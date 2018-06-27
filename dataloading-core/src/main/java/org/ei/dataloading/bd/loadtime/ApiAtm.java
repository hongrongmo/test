package org.ei.dataloading.bd.loadtime;

import java.util.ArrayList;
import java.util.List;

/*import org.jdom.Element;
import org.jdom.Namespace;*/

import org.jdom2.Element;
import org.jdom2.Namespace;

public class ApiAtm
{
    private Element apiatmgroup ;
    private StringBuffer apiatmStr = new StringBuffer("");
    private Namespace noNamespace=Namespace.getNamespace("","");

    public static final String PIPE = "| ";
    public static final String OPEN_BRACKET ="[ ";
    public static final String CLOSE_BRACKET = " ]";
    public static final String SEMDELIM = ";";
    public static final String NEW_LINE = " |> ";

    //apiltm
    /*
     <!ELEMENT API-ATM-group ( API-ATM+ ) >
     <!ELEMENT API-ATM(ATM-template, LT-count, API-term*, ATM-Vgroup*, ATM-Sgroup? ) >
     <LT-count>00020</LT-count>
     in HTML string saved in database :
     ATM-template starts from $$
     LT-count starts ##
    */

    public ApiAtm(Element e)
    {
        this.apiatmgroup = e;
        noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");;
        setApiAtm();
    }

    public void setApiAtm()
    {
        if(this.apiatmgroup != null)
        {
            List l = apiatmgroup.getChildren();
            Element apiatm = apiatmgroup.getChild("API-ATM",noNamespace);
            if(apiatm != null)
            {
                // template name , role
                if(apiatm.getChild("ATM-template",noNamespace)!= null)
                {
                    Element e = apiatm.getChild("ATM-template",noNamespace);

                    // template name
                    apiatmStr.append("$$ ");
                    apiatmStr.append(e.getTextTrim());
                    apiatmStr.append(" ");
                    //template role
                    if(e.getAttribute("template-role")!= null)
                    {
                        String role = (String)e.getAttributeValue("template-role");
                        apiatmStr.append("ROLE: ");
                        apiatmStr.append(role);
                        apiatmStr.append(" ");
                    }
                }
                // count
                if(apiatm.getChild("LT-count",noNamespace)!= null)
                {
                    Element e = apiatm.getChild("LT-count",noNamespace);
                    apiatmStr.append("| ## ");
                    apiatmStr.append(e.getTextTrim());
                    apiatmStr.append(" ");
                }
                //apiatm term
                List apiatmlist = apiatm.getChildren("API-term",noNamespace);
                for(int j = 0; j < apiatmlist.size(); j++)
                {
                    Element el = (Element)apiatmlist.get(j);
                    StringBuffer eachTerm = new StringBuffer(el.getTextTrim());
                    apiatmStr.append(PIPE);
                    apiatmStr.append(setNewLine(eachTerm, false));

                }
                List vgroups = apiatm.getChildren("ATM-Vgroup",noNamespace);
                apiatmStr.append(setGroups(vgroups));
                List sgroups = apiatm.getChildren("ATM-Sgroup", noNamespace);
                apiatmStr.append(setGroups(sgroups));
            }
        }
    }

    public StringBuffer setGroups(List groupList)
    {
        StringBuffer aGroups = new StringBuffer();
        for(int i = 0; i < groupList.size(); i++)
        {
            Element atmgroup = (Element) groupList.get(i);
            aGroups.append(setGroup(atmgroup));
        }
        return aGroups;
    }


    public String setGroup(Element atmgroup)
    {
        StringBuffer aGroupStrbuf = new StringBuffer();
        List groupList = atmgroup.getChildren("API-term",noNamespace);

        if( groupList.size() > 0)
        {
            //1. set variable
            if(groupList != null && groupList.size() > 0)
            {
                Element eterm = (Element)groupList.get(0);
                if(eterm.getAttribute("group-indicator") != null)
                {
                    String variable = eterm.getAttributeValue("group-indicator");
                    aGroupStrbuf.append(PIPE);
                    aGroupStrbuf.append(variable);
                    aGroupStrbuf.append(" ");
                }
            }
            //2. getGroupStr
            StringBuffer groupStr = new StringBuffer();
            if(groupList != null && groupList.size() > 0)
            {
                for(int m = 0; m < groupList.size(); m++)
                {
                    Element mtoc = (Element)groupList.get(m);
                    String mtocStr = mtoc.getTextTrim();

                    if(mtocStr.indexOf(SEMDELIM) > 0)
                    {
                        // add brackets
                        groupStr.append(OPEN_BRACKET);
                        groupStr.append(mtocStr);
                        groupStr.append(CLOSE_BRACKET);
                        if(m < (groupList.size()-1))
                        {
                            groupStr.append(SEMDELIM);
                        }
                    }
                    else
                    {
                        groupStr.append(mtocStr);
                        if(m < (groupList.size()-1))
                        {
                            groupStr.append(SEMDELIM);
                        }
                    }
                }
            }
            //3. setNewLines
            aGroupStrbuf.append(setNewLine(groupStr, true));
        }

        return aGroupStrbuf.toString();
    }

    public String toAPIString()
    {
        return apiatmStr.toString();
    }


    public StringBuffer setNewLine(StringBuffer group, boolean isGroup)
    {
        //count six elements and insert NEW_LINE
        //also for V,S groups insert SEMDELIM between tok
        StringBuffer result = new StringBuffer();
        String groupStr = group.toString();
        String[] list = groupStr.split(SEMDELIM);
        int p=1;
        for (int z = 0; z < list.length; z++)
        {
            result.append(list[z]);
            if(z < (list.length-1) && isGroup)
            {
                result.append(SEMDELIM);
            }
            //don't put NEW_LINE after last tok in the group
            if(z < (list.length-1) && p%6==0)
            {
                result.append(NEW_LINE);
            }
            p++;
        }


        return result;
    }

}
