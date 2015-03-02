package org.ei.data.inspec.loadtime;

import java.io.BufferedReader;import java.io.File;import java.io.FileInputStream;import java.io.FileReader;import java.io.InputStreamReader;import java.util.Hashtable;import java.util.zip.GZIPInputStream;import java.util.zip.ZipEntry;import java.util.zip.ZipInputStream;import org.apache.oro.text.perl.Perl5Util;
public class InspecBaseTableDriverUpdater
{
	private Perl5Util perl = new Perl5Util();
	private static InspecBaseTableUpdater baseUpdater;
	private static int exitNumber;
	private int counter = 0;
	private int updateNumber = 0;
	private static String filename;


	public static void main(String args[])
		throws Exception
	{
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
	    String infile = args[4];
		int updateN = Integer.parseInt(args[5]);

		if(args.length == 7)
		{
			exitNumber = Integer.parseInt(args[6]);
		}
		else
		{
			exitNumber = 0;
		}

		InspecBaseTableDriverUpdater c = new InspecBaseTableDriverUpdater(updateN);

		c.writeBaseTableFile(url,driver,username,password,infile);
	}

	public InspecBaseTableDriverUpdater(int updateN)
	{
		this.updateNumber = updateN;
	}

	public void writeBaseTableFile(String url,String driver,String username,String password,String infile)
		throws Exception
	{
		BufferedReader in = null;
		ZipInputStream zin=null;
		ZipEntry entry=null;

		try
		{

			if ( infile.toUpperCase().endsWith(".ZIP") )
			{
				zin = new ZipInputStream(new FileInputStream(new File(infile)));
				baseUpdater = new InspecBaseTableUpdater();

				baseUpdater.begin(url,driver,username,password);
				while((entry=zin.getNextEntry())!=null)
				{
					filename=entry.getName();

						System.out.println(":"+ entry.getName());
						System.out.println("Processing XML ...");
						in = new BufferedReader(new InputStreamReader(zin));
						writeRecs(new InspecXMLReader(in));

					zin.closeEntry();

				}
				baseUpdater.end();

		 }
		else
			{
				baseUpdater = new InspecBaseTableUpdater();
				if ( infile.toUpperCase().endsWith(".GZ") )
					in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(infile)))));
				else
					in = new BufferedReader(new FileReader(new File(infile)));

				baseUpdater.begin(url,driver,username,password);
				writeRecs(new InspecXMLReader(in));
				baseUpdater.end();

			}


	}
	catch(Exception e)
	{
		System.err.println("Error in File:"+filename+" Record Number:"+counter);
		e.printStackTrace();

    }

   }

	private void writeRecs(InspecXMLReader r) throws Exception
	{

		Hashtable record = null;

		while((record = r.getRecord())!=null)
		{

			if(exitNumber != 0 && counter > exitNumber)
			{
				break;
			}
			baseUpdater.writeRec(record,Integer.toString(this.updateNumber));
			counter++;
		}

		System.out.println("**Update is done**");
	}

}
