package org.ei.query.base;

import java.io.*;
import java.util.*;
import org.ei.domain.ElementData;
import org.ei.domain.Key;
import org.ei.domain.Keys;

public class Highlights
	implements ElementData
{
  private static final String eTag =":H::";
  private static final String bTag = "::H:";
	private String[] edata = new String[1];
	private Key key;
	private int numhits;
	private int numwords;
	private boolean labels;
	private boolean highlighted = false;
	private boolean escape = true;
	private String label;

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public Highlights(Key key,
					  String text,
					  int numhits,
					  int numwords)
	{
		this.edata[0] = text;
		this.key = key;
		this.numhits = numhits;
		this.numwords = numwords;
	}

	public Highlights(Key key,
					  String label,
					  String text,
					  int numhits,
					  int numwords)
	{
		//System.out.println(text);
		this.edata[0] = text;
		this.key = key;
		this.numhits = numhits;
		this.numwords = numwords;
		this.label = label;
	}

	public void setEscape(boolean _escape)
	{
		this.escape = _escape;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		return this.edata;
	}

	public void setElementData(String[] edata)
	{
		this.highlighted = true;
		this.edata = edata;
	}

	public void toXML(Writer out)
		throws IOException
	{
		String highlights = getHighlights(this.edata[0],
										  this.numhits,
										  this.numwords);
		if(highlights.length() == 0)
		{
			return;
		}

		out.write("<");
		out.write(this.key.getKey());

		if(labels)
		{
			String lbl = null;

			if(this.label != null)
			{
				lbl = this.label;
			}
			else
			{
			  	if(this.key != null)
			  	{
					lbl = this.key.getLabel();
				}
			}

			if(lbl != null)
			{
				out.write(" label=\"");
				out.write(lbl);
				out.write("\"");
			}
		}
		out.write("><![CDATA[");
		out.write(highlights);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}

    private String getHighlights(String text,
    							 int numget,
    							 int numwords)
    {
		if(!highlighted)
		{
			return "";
		}

        boolean inTag = false;
        boolean inEntity = false;
		int gotten = 0;

        if(text == null)
        {
            return null;
        }

        Stack charPool = new Stack();
        for(int i=0; i<4 ; ++i)
        {
            charPool.push(new CharWrapper());
        }

        int numOpen = 0;
        StringBuffer buf = new StringBuffer();
        LinkedList charList = new LinkedList();
        char[] databuf = new char[text.length()];
        text.getChars(0,
                      text.length(),
                      databuf,
                      0);

        for(int i=0; i<databuf.length; ++i)
        {
            CharWrapper cw = (CharWrapper)charPool.pop();
            cw.c = databuf[i];

            charList.addLast(cw);

            if(charList.size() == 4)
            {

                if(isOpenHit(charList))
                {
                    ++numOpen;
                    if(numOpen == 1)
                    {
                        if(!inTag && !inEntity)
                        {
							buf.append(prependContext(databuf,i));
                            buf.append(bTag);
                        }

                    }

                    while(charList.size()>0)
                    {
                        charPool.push(charList.removeFirst());
                    }
                }
                else if(isCloseHit(charList))
                {
                    if(numOpen == 1)
                    {
                        if(!inTag && !inEntity)
                        {
							buf.append(eTag);
							buf.append(appendContext(databuf,i+1));
                            ++gotten;
                        }
                    }

                    --numOpen;

                    while(charList.size()>0)
                    {
                        charPool.push(charList.removeFirst());
                    }
                }
                else
                {
                    CharWrapper w = (CharWrapper)charList.removeFirst();
                    if(numOpen > 0)
                    {
                    	buf.append(w.c);
					}
                    charPool.push(w);
                }

                if(gotten == numget)
                {
					break;
				}
            }
        }

        return buf.toString();
    }

    private String prependContext(char databuf[],
    							  int index)
    {
		int begin = index - 4;
		StringBuffer buf = new StringBuffer();
		LinkedList stack = new LinkedList();

		if(begin > 0)
		{
			int word = 0;
			while(begin >= 0)
			{
				if(databuf[begin] == ':')
				{
					break;
				}
				else if(databuf[begin] == ' ')
				{
					word++;
					if(word > numwords)
					{
						break;
					}
					else
					{
						stack.addFirst(new Character(databuf[begin]));
					}
				}
				else
				{
					stack.addFirst(new Character(databuf[begin]));
				}
				begin--;
			}
		}

		if(stack.size() > 0)
		{
			if(begin > 0)
			{
				buf.append(" <b>...</b>");
			}

			while(stack.size() > 0)
			{
				Character c =(Character)stack.removeFirst();
				char cv = c.charValue();
				if(cv == '>' && escape)
				{
					buf.append("&gt;");
				}
				else if(cv == '<' && escape)
				{
					buf.append("&lt;");
				}
				else
				{
					buf.append(cv);
				}
			}
		}

		return buf.toString();
	}

	private String appendContext(char databuf[],
							     int index)
	{
		StringBuffer buf = new StringBuffer();
		int length = databuf.length;
		int numleft = length - index;
		int words = 0;

		if(numleft > 0)
		{
			for(int i=0; i<numleft; i++)
			{
				if(databuf[index] == ':')
				{
					break;
				}
				else if(databuf[index] == ' ')
				{
					words++;
					if(words > numwords)
					{
						break;
					}
					else
					{
						if(databuf[index] == '>' && escape)
						{
							buf.append("&gt;");
						}
						else if(databuf[index] == '<' && escape)
						{
							buf.append("&lt;");
						}
						else
						{
							buf.append(databuf[index]);
						}
					}
				}
				else
				{
					if(databuf[index] == '>' && escape)
					{
						buf.append("&gt;");
					}
					else if(databuf[index] == '<' && escape)
					{
						buf.append("&lt;");
					}
					else
					{
						buf.append(databuf[index]);
					}
				}

				index++;
			}
		}

		return buf.toString();
	}

    private boolean isCloseHit(LinkedList l)
    {
        CharWrapper a = (CharWrapper)l.get(0);

        if(a.c != ':')
        {
            return false;
        }


        a = (CharWrapper)l.get(1);

        if(a.c != 'H')
        {
            return false;
        }

        a = (CharWrapper)l.get(2);

        if(a.c != ':')
        {
            return false;
        }


        a = (CharWrapper)l.get(3);

        if(a.c != ':')
        {
            return false;
        }

        return true;
    }

    private boolean isOpenHit(LinkedList l)
    {
        CharWrapper a = (CharWrapper)l.get(0);
        if(a.c != ':')
        {
            return false;
        }

        a = (CharWrapper)l.get(1);

        if(a.c != ':')
        {
            return false;
        }

        a = (CharWrapper)l.get(2);

        if(a.c != 'H')
        {
            return false;
        }

        a = (CharWrapper)l.get(3);

        if(a.c != ':')
        {
            return false;
        }

        return true;
    }

	public static void main(String args[])
		throws Exception
	{
		String text = "blah blah <&>hah ::H:hah:H:: < & This> is a test of the ::H:::H:highlight:H:: yes:H:: code this only a ::H:test:H:: la di da hah ::H:smell:H::.";
		String text2 = "blah blah hah";

		Highlights h = new Highlights(Keys.ABSTRACT,
									  text,
									  4,
									  6);
		//h.setEscape(false);

		String[] ed = {text};
		h.setElementData(ed);
		FileWriter out = null;
		try
		{
			out = new FileWriter("test.xml");
			h.toXML(out);
			out.close();
		}
		finally
		{
			if(out != null)
			{
				out.close();
			}
		}
	}

}