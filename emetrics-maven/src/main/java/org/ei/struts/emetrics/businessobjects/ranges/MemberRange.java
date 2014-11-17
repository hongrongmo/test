package org.ei.struts.emetrics.businessobjects.ranges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts.util.LabelValueBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MemberRange implements IRange {

    protected Log log = LogFactory.getLog(MemberRange.class);

    private String m_member = null;
    private Collection m_members = null;

    private String getMember(){ return m_member; }
    protected void setMember(String id){ m_member = id; }


    public MemberRange(Collection members) {
        // do nothing
        m_members = members;
    }
    public boolean isYearWithinRange(int year)
    {
      return true;
    }

    public Collection getMembers()
    {
      List lblValueMembers = new ArrayList();
      Iterator itrmembers = m_members.iterator();
      while(itrmembers.hasNext()) {
        Map member = (Map) itrmembers.next();
        lblValueMembers.add(new LabelValueBean(
            (String) member.get("name"), (String) member.get("custid")
            ));
      }
      return lblValueMembers;
    }

    public boolean isWithinRange(Object val)
    {
      boolean result = false;
      return !result;
    }


    public String getHtmlFormPage() {
      return "/common/htmlforms/memberRange.jsp";
    }
}


