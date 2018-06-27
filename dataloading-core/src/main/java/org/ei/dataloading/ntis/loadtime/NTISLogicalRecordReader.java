package org.ei.dataloading.ntis.loadtime;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

public class NTISLogicalRecordReader
	extends FilterReader
{
	Reader in;
	public NTISLogicalRecordReader(Reader r)
	{
		super(r);
		this.in = r;
	}


	public Hashtable readLogicalRecord()
		throws IOException
	{
		StringBuffer buf = new StringBuffer();
		String serialNumber = buildBuffer(buf);
		return new NTISRecord(serialNumber, buf.toString());
	}


	private String buildBuffer(StringBuffer buffer)
		throws IOException
	{
		char[] charData = new char[1024];
		int numRead = in.read(charData);
		if(numRead != 1024)
		{
			throw new IOException("Didn't read 1024 characters");
		}

		char continueFlag = charData[10];
		buffer.append(new String(charData, 24, 1000));
		String serialNumber = new String(charData,0,9);
		//System.out.println(continueFlag);
		//System.out.println(serialNumber);


		if(continueFlag == '0')
		{
			buildBuffer(buffer);
		}

		return serialNumber;
	}






}
