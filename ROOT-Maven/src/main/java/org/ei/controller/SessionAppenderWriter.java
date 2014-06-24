package org.ei.controller;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;


public class SessionAppenderWriter
	extends FilterWriter
{


	/*
	*	All the states of the output stream
	*/

	private int CLEAN = 1;
	private int TAG_OPEN = 2;
	private int F_IN_FORM = 3;
	private int O_IN_FORM = 4;
	private int R_IN_FORM = 5;
	private int M_IN_FORM = 6;
	private int BEGIN_ANCHOR = 7;
	private int ATTRIBUTE_BORDER = 8;
	private int A_IN_ACTION = 9;
	private int H_IN_HREF = 10;
	private int R_IN_HREF = 11;
	private int E_IN_HREF = 12;
	private int F_IN_HREF = 13;
	private int BEGIN_URL = 14;
	private int OPEN_QUOTE = 15;
	private int LOCAL_ADDRESS = 16;
	private int WRONG_ATTRIBUTE = 21;


	/*
	*	All  the ascii characters to look for
	*/

	private int OPEN_TAG = 60;
	private int CLOSE_TAG = 62;


	private int LOWER_F = 102;
	private int UPPER_F = 70;

	private int UPPER_E = 69;
	private int LOWER_E = 101;

	private int UPPER_M = 77;
	private int LOWER_M = 109;

	private int UPPER_O = 79;
	private int LOWER_O = 111;

	private int UPPER_A = 65;
	private int LOWER_A = 97;

	private int UPPER_H = 72;
	private int LOWER_H = 104;

	private int UPPER_R = 82;
	private int LOWER_R = 114;

	private int UPPER_C = 67;
	private int LOWER_C = 99;

	private int UPPER_P = 80;
	private int LOWER_P = 112;

	private int UPPER_T = 84;
	private int LOWER_T = 116;

	private int UPPER_J = 74;
	private int LOWER_J = 106;

	private int UPPER_V = 86;
	private int LOWER_V = 118;

	private int UPPER_S = 83;
	private int LOWER_S = 115;

	private int UPPER_I = 73;
	private int LOWER_I = 105;


	private int SPACE = 32;
	private int CARRIAGE_RETURN = 13;
	private int LINE_BREAK = 10;
	private int TAB = 41;
	private int QUOTE = 34;
	private int COLON = 58;
	private int QUESTION_MARK = 63;
	private int POUND = 35;
	private int AMPERSTAND = 38;




	private char[] sessionBuf;

	private int state = CLEAN;

	private boolean skip = false;


	private static void main(String[] args)
		throws Exception
	{
		/*
		StringBuffer buf = new StringBuffer();
		buf.append(" Hello world here is some stuff  <font><a href=\"/going/home.jsp\">Test</a></font>");
		buf.append(" Lets try some more <A href=\"http://www.go.home?fdfds\"> test 2</a>");
		buf.append("bla blah <a other=\"yes\" HREF=\"Controller?CID=3\">Home</a>");
		buf.append("<FORM name=\"test\" action=\"Controller\"></form>");
		buf.append("<p><a href=\"javascript:go()\">Test Java script</a> $TEST");
		SessionAppenderStream sout = new SessionAppenderStream(System.out, "333333");
		PrintStream pout = new PrintStream(sout);
		pout.println(buf.toString());
		*/
	}


	public SessionAppenderWriter(Writer out,
								 String sessionID)
	{
		super(out);
		String appendString = "EISESSION="+sessionID;
		sessionBuf = appendString.toCharArray();
	}

	public void close()
		throws IOException
	{
		out.close();
	}

	public void flush()
		throws IOException
	{
		out.flush();
	}


	public void write(char buf[], int off, int length)
		throws IOException
	{
		for(int x=0; x<length; ++x)
		{
			write((int)buf[off]);
			++off;
		}
	}

	public void write(String s, int off, int length)
		throws IOException
	{
		char[] buf = s.toCharArray();
		for(int x=0; x<buf.length; ++x)
		{
			write((int)buf[x]);
		}
	}

	public void write(int b)
		throws IOException
	{

		if(b == CLOSE_TAG)
		{
			//System.out.println("State is clean");
			state = CLEAN;
		}
		if(state == CLEAN)
		{

			if(b == OPEN_TAG)
			{
				//System.out.println("Found a tag");
				state = TAG_OPEN;
			}
		}
		else if(state == TAG_OPEN)
		{
			if(b == UPPER_A || b == LOWER_A)
			{
				//System.out.println("Found an Anchor");
				state = BEGIN_ANCHOR;
			}
			else if(b == UPPER_F || b == LOWER_F)
			{
				state = F_IN_FORM;
			}
			else
			{
				state=CLEAN;
			}
		}
		else if(state == F_IN_FORM)
		{

			if(b == UPPER_O || b == LOWER_O)
			{
				state = O_IN_FORM;
			}
		}
		else if(state == O_IN_FORM)
		{

			if(b == UPPER_R || b == LOWER_R)
			{
				state = R_IN_FORM;
			}
		}
		else if(state == R_IN_FORM)
		{
			if(b == UPPER_M | b == LOWER_M)
			{
				state = M_IN_FORM;
			}
		}
		else if(state == M_IN_FORM || state == BEGIN_ANCHOR || state == WRONG_ATTRIBUTE)
		{
			if(b == SPACE || b == LINE_BREAK || b == CARRIAGE_RETURN || b == TAB)
			{
				//System.out.println("At an attribute border");
				state = ATTRIBUTE_BORDER;
			}
		}
		else if(state == ATTRIBUTE_BORDER)
		{

			if(b == UPPER_H || b == LOWER_H)
			{
				//System.out.println("At the H in HREF");
				state = H_IN_HREF;
			}
			else if(b == UPPER_A || b == LOWER_A)
			{
				state = A_IN_ACTION;
			}
			else
			{
				state = WRONG_ATTRIBUTE;
			}
		}
		else if(state == A_IN_ACTION)
		{
			if(b == UPPER_C || b == LOWER_C)
			{
				//System.out.println("Begining URL 1");
				state = BEGIN_URL;
			}
		}
		else if(state == H_IN_HREF)
		{
			if(b == UPPER_R || b == LOWER_R)
			{
				//System.out.println("At the R in HREF");
				state = R_IN_HREF;
			}
		}
		else if(state == R_IN_HREF)
		{
			//System.out.println("THIS SHOULD PRINT");
			if(b == UPPER_E || b == LOWER_E)
			{
				//System.out.println("At the E in href");
				state = E_IN_HREF;
			}
		}
		else if(state == E_IN_HREF)
		{
			if(b == UPPER_F || b == LOWER_F)
			{
				//System.out.println("Beginning URL 2");
				state = BEGIN_URL;
			}
		}
		else if(state == BEGIN_URL)
		{
			if(b == QUOTE)
			{
				state = OPEN_QUOTE;
			}
		}
		else if(state == OPEN_QUOTE)
		{
			if(b == COLON || b == POUND)
			{
				state = CLEAN;
			}
			else if(b == QUESTION_MARK)
			{
				//System.out.println("Getting ready to append session");
				out.write(b);
				out.write(sessionBuf);
				out.write(AMPERSTAND);
				state = CLEAN;
				skip = true;
			}
			else if(b == QUOTE)
			{
				out.write(QUESTION_MARK);
				out.write(sessionBuf);
				state = CLEAN;
			}
		}

		if(skip)
		{
			skip = false;
		}
		else
		{
			out.write(b);
		}
	}

}


