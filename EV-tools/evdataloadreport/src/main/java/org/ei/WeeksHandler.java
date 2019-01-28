package org.ei;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class WeeksHandler extends SimpleTagSupport{

	
	public void doTag() throws JspException
	{
		String output = "<Select name=weeknum id=weeknum";
		int year = 2015;
		String weekNum;
		
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
	
		try
		{
			for(int i =0; i<53;i++)
			{
				if(i<10)
				{
					weekNum=Integer.toString(year) + "0"+i;
				}
				else
				{
					weekNum=Integer.toString(year) + i;
				}

				output += "<option value " + weekNum + ">" + i + "                          "+ " </option>";

			}
			
			output += "</select";
			out.println(output);
		}
		
		catch(Exception e)
		{
			throw new JspException("Error in WeeksHandler",e);
		}
	}
	
 
}
