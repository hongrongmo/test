package org.ei.controller;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class SectionStream
	extends FilterInputStream
{
	private int START = 0;
	private int CLEAN = 1;
	private int OPEN_TAG = 2;
	private int EXPLANATION_POINT = 3;
	private int DASH_1 = 4;
	private int DASH_2 = 5;
	private int E = 6;
	private int B = 7;
	private int H = 8;
	private int R = 9;
	private int F = 10;
	private int DASH_3 = 11;
	private int DASH_4 = 12;
	private int DONE = 13;
	private int NEXT = 14;
	private int RECORDS_BEGIN = 15;
	private int RECORDS_END = 16;

	private int state = -1;
	private boolean GO = false;
	private boolean FINALRECORD = false;
	private char section = 'c';
	private byte[] buffer = new byte[30000];
	private int bufferIndex = 0;

	private byte[] beginSection = "<SECTION-DELIM>".getBytes();
	private byte[] endSection = "</SECTION-DELIM>".getBytes();

	public static void main(String argv[])
		throws Exception
	{

		FileInputStream in = new FileInputStream(argv[0]);
		SectionStream secIn = new SectionStream(in);
		byte[] b = secIn.readHeader();
		System.out.write(b,0,secIn.getBufferIndex());
		System.out.println("");

		while((b = secIn.readRecord()) != null)
		{
			System.out.write(b,0,secIn.getBufferIndex());
			System.out.println("");
		}

		b = secIn.readFooter();
		System.out.write(b,0,secIn.getBufferIndex());

	}

	public void beginBuffer()
	{
		for(int i = 0; i<beginSection.length; ++i)
		{
			byte b = beginSection[i];
			buffer[bufferIndex++] = b;
		}
	}

	public void endBuffer()
	{
		for(int i = 0; i<endSection.length; ++i)
		{
			byte b = endSection[i];
			buffer[bufferIndex++] = b;
		}
	}

	public SectionStream(InputStream in)
	{
		super(in);
	}

	public int available()
		throws IOException
	{
		return in.available();
	}

	public void close()
		throws IOException
	{
		in.close();
	}

	public void mark(int readLimit)
	{
		in.mark(readLimit);
	}

	public int getBufferIndex()
	{
		return this.bufferIndex;
	}

	public void reset()
		throws IOException
	{
		in.reset();
	}

	public boolean markSupported()
	{
		return in.markSupported();
	}

	public byte[] readRecord()
		throws IOException
	{


		bufferIndex = 0;
		state = CLEAN;
		GO = false;
		section = 'r';
		beginBuffer();
		while(state != DONE)
		{
			int i = read();

			if(i == -1)
			{
				break;

			}

			if(GO)
			{
				buffer[bufferIndex++] = (byte)i;
			}

			if(state == NEXT)
			{

				if(FINALRECORD)
				{
					GO = false;
					state = DONE;
				}
				else
				{
					GO = true;
					state = CLEAN;
				}
			}
		}

		if(FINALRECORD)
		{
			FINALRECORD = false;
			return null;
		}

		endBuffer();
		return buffer;
	}

	public byte[] readHeader()
		throws IOException
	{

		state = CLEAN;
		section = 'h';
		bufferIndex = 0;
		beginBuffer();
		while(state != DONE)
		{
			int i = read();

			if(i == -1)
			{
				break;
			}

			if(GO)
			{
				buffer[bufferIndex++] = (byte)i;
			}

			if(state == NEXT)
			{
				GO = true;
				state = CLEAN;
			}
		}
		endBuffer();
		return buffer;
	}

	public byte[] readFooter()
		throws IOException
	{

		bufferIndex = 0;
		state = CLEAN;
		GO = false;
		section = 'f';
		beginBuffer();
		while(state != DONE)
		{
			int i = read();

			if(i == -1)
			{
				break;
			}

			if(GO)
			{
				buffer[bufferIndex++] = (byte)i;
			}

			if(state == NEXT)
			{
				GO = true;
				state = CLEAN;
			}
		}

		endBuffer();
		return buffer;
	}


	public int read()
		throws IOException
	{

		int i = in.read();
		char c = (char)i;
		//System.out.print(c);
		if(section == 'h')
		{
			if(!GO)
			{

				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{
					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'B')
					{
							state = B;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == B)
				{
					if(c == 'H')
					{
						state = H;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == H)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{

						state = NEXT;
					}
					else
					{
						state = CLEAN;
					}
				}
			}
			else
			{

				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{

					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'E')
					{
							state = E;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == E)
				{
					if(c == 'H')
					{
						state = H;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == H)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{
						state = DONE;
					}
					else
					{
						state = CLEAN;
					}
				}
			}

		}
		else if(section == 'r')
		{

			if(!GO)
			{

				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{
					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'B')
					{
							state = B;
					}
					else if(c == '*')
					{
							//System.out.println("Final Record");
							FINALRECORD = true;
							state = R;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == B)
				{
					if(c == 'R')
					{
						state = R;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == R)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{

						state = NEXT;
						//System.out.println("Found record");
					}
					else
					{
						state = CLEAN;
					}
				}
			}
			else
			{

				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{

					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'E')
					{
							state = E;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == E)
				{
					if(c == 'R')
					{
						state = R;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == R)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{

						state = DONE;
					}
					else
					{
						state = CLEAN;
					}
				}
			}
		}
		else if(section == 'f')
		{
			if(!GO)
			{

				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{
					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'B')
					{
							state = B;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == B)
				{
					if(c == 'F')
					{
						state = F;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == F)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{

						state = NEXT;
					}
					else
					{
						state = CLEAN;
					}
				}
			}
			else
			{
				if(state == CLEAN)
				{
					if(c == '<')
					{
						state = OPEN_TAG;
					}
				}
				else if(state == OPEN_TAG)
				{
					if(c == '!')
					{
						state = EXPLANATION_POINT;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == EXPLANATION_POINT)
				{

					if(c == '-')
					{
						state = DASH_1;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_1)
				{

					if(c == '-')
					{
						state = DASH_2;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == DASH_2)
				{
					if(c == 'E')
					{
							state = E;
					}
					else
					{
							state = CLEAN;
					}
				}
				else if(state == E)
				{
					if(c == 'F')
					{
						state = F;
					}
					else
					{
						state = CLEAN;
					}
				}
				else if(state == F)
				{

					if(c == '-')
					{
						state = DASH_3;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_3)
				{

					if(c == '-')
					{
						state = DASH_4;
					}
					else
					{
						state = CLEAN;
					}

				}
				else if(state == DASH_4)
				{
					if(c == '>')
					{

						state = DONE;
					}
					else
					{
						state = CLEAN;
					}
				}
			}
		}


		return i;
	}
}