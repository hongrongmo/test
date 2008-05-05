package org.ei.domain;

import java.io.*;


public class ISBN
	implements ElementData
{
  public static final String ISBN13_PREFIX = "978";
	protected String isbn;
	protected Key key;
	protected boolean labels = true;

	public ISBN(String isbn)
	{
		this.key = Keys.ISBN;
		this.isbn = isbn;
	}

	public ISBN(Key key, String isbn)
	{
		this.key = key;
		this.isbn = isbn;
	}


	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

  private static char getISBN13CheckDigit(String isbn) {
    int len = isbn.length();
    int digitSum = 0;
    int calcValue = 0;

    // length of passed in isbn must be 12 or
    // we will get a nullpointer here
    for (int i = 0; i < 12; i++) {
        int val = Integer.parseInt((isbn.substring(i, i + 1)));
        if (i % 2 == 1) {
            digitSum += val * 3;
        } else {
            digitSum += val;
        }
    }

    calcValue = (10 - (digitSum % 10)) % 10;

    return (char) (calcValue + '0');
  }

	public String withoutDash()
	{
		return this.isbn.replaceAll("[-|\\s]","");
	}

	public String withDash()
	{
		return this.isbn.replaceAll("\\s","-");
	}

	public String[] getElementData()
	{
		String s[] = {this.isbn};
		return s;
	}

	public void setElementData(String[] s)
	{
		this.isbn = s[0];
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public void toXML(Writer out)
		throws IOException
	{

		out.write("<");
		out.write(this.key.getKey());

    if(this.labels && this.key.getLabel() != null)
    {
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
    }

		out.write("><![CDATA[");
		out.write(this.isbn);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}

}

