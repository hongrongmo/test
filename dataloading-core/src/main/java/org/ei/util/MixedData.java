package org.ei.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Text;

/**
 * 
 * @author TELEBH
 * @Date: 04/29/2022
 * @Description, I noticed that method "getMixedData" is duplicated in multiple classes like bdParser, inspecReader,...
 * It is better to be added as a method in a common used class which is this one
 */
public class MixedData {

	  Perl5Util perl = new Perl5Util();
	  boolean inabstract = false;
	  private HashSet<Object> entity = new HashSet<>();
	
	public String getMixData(List l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }

    public StringBuffer getMixData(List l, StringBuffer b)
    {
    	
    	
    	
        Iterator it = l.iterator();

        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				String text=((Text)o).getText();

				text= perl.substitute("s/</&lt;/g",text);   	//restore by hmo@3/21/2021
				text= perl.substitute("s/>/&gt;/g",text);		//restore by hmo@3/21/2021
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);	
				text= perl.substitute("s/�/\"/g",text);	
				text= perl.substitute("s/�/\"/g",text);
				b.append(text);
				//System.out.println("text2::"+text);

            }
            else if(o instanceof EntityRef)
            {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
		
        return b;
    }

    public  StringBuffer getMixCData(List l, StringBuffer b)
	{
		inabstract=true;
		b=getMixData(l,b);
		inabstract=false;
		return b;
	}
    
    public StringBuffer getMixData(Iterator it, StringBuffer b)
    {
        
        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				//String text=((Text)o).getTextTrim();
				String text=((Text)o).getText();

				//System.out.println("text3::"+text);

				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);

				//System.out.println("text4::"+text);
				b.append(text);

            }
            else if(o instanceof EntityRef)
            {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getDescendants(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
		
        return b;
    }
    
    public String getMixData(Iterator l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }
	
}
