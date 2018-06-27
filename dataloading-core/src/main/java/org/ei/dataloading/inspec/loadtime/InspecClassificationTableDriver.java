
package org.ei.dataloading.inspec.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

public class InspecClassificationTableDriver
{
	private String infile;
	private String outfile;


	private static final String REC_TYPE = "010";
	private static final String STATUS = "005";
	private static final String CLASS_CODE = "220";
	private static final String CLASS_LEVEL = "225";
	private static final String CLASS_TITLE = "230";
	private static final String SCOPE_NOTES = "232";
	private static final String SEE_ALSO_CROSS_REF = "234";
	private static final String SEE_CROSS_REF = "236";
	private static final String HISTORY_SCOPE_NOTES = "238";
	private static final String FUTURE_SCOPE_NOTES = "240";


	public static void main(String args[])
		throws Exception
	{
		String infile = args[0];
		String outfile = args[1];
		InspecClassificationTableDriver driver = new InspecClassificationTableDriver(infile, outfile);
		driver.createData();
	}

	public InspecClassificationTableDriver(String in,
									  String out)
	{
		this.infile = in;
		this.outfile = out;
	}


	public void createData()
		throws Exception
	{
		BufferedReader reader = null;
		PrintWriter writer = null;


		try
		{
			reader = new BufferedReader(new FileReader(this.infile));
			writer = new PrintWriter(new FileWriter(this.outfile));
			String line = null;
			int i = 0;
			while((line = reader.readLine()) != null)
			{
				TRecord tr = new TRecord();
				MARC marc = new MARC(line);
				while (marc.hasMoreFields())
				{
					Field field = marc.nextField();
					String tag = field.getTag();
					while (field.hasMoreSubFields())
					{
						SubField subfield = field.nextSubField();
						char type = subfield.getType();
						String value = unicodeMap(subfield.getValue());
						tr.addFieldValue(tag, value);
					}
				}

				String recordType = tr.getFieldValues(REC_TYPE);

				if(recordType.equals("C"))
				{
					StringBuffer buf = new StringBuffer();
					buf.append(notNull(tr.getFieldValues(CLASS_CODE)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(STATUS)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(CLASS_LEVEL)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(CLASS_TITLE)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(SCOPE_NOTES)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(SEE_ALSO_CROSS_REF)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(SEE_CROSS_REF)));
					buf.append("	");
					buf.append(notNull(tr.getFieldValues(HISTORY_SCOPE_NOTES)));
					writer.println(buf.toString());
					i++;
				}


			}
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
		}
	}




	public String notNull(String s)
	{
		if(s != null)
		{
			return s;
		}

		return "";
	}



	public class TRecord
	{
		Properties pr = new Properties();

		public String getFieldValues(String tag)
		{
			return pr.getProperty(tag);
		}

		public void addFieldValue(String tag, String value)
		{
			if(pr.containsKey(tag))
			{
				String v = pr.getProperty(tag);
				v = v+";"+value;
				pr.setProperty(tag, v);
			}
			else
			{
				pr.setProperty(tag, value);
			}
		}
	}

	private static String unicodeMap(String txt)
	{
		int len = txt.length();
		StringBuffer sb = new StringBuffer();
		char c;

		for (int i = 0; i < len; i++) {
			c = txt.charAt(i);
			switch (c) {
				case '\u00A1':
					sb.append('\u0141');
					break;
				case '\u00A2':
					sb.append('\u00D8');
					break;
				case '\u00A3':
					sb.append('\u0110');
					break;
				case '\u00A4':
					sb.append('\u00DE');
					break;
				case '\u00A5':
					sb.append('\u00C6');
					break;
				case '\u00A6':
					sb.append('\u008C');
					break;
				case '\u00A7':
					sb.append('\u00B4');
					break;
				case '\u00A8':
					sb.append('\u00B7');
					break;
				case '\u00A9':
					sb.append('\u266D');
					break;
				case '\u00AA':
					sb.append('\u00AE');
					break;
				case '\u00AB':
					sb.append('\u00B1');
					break;
				case '\u00AC':
					sb.append('\u01A0');
					break;
				case '\u00AD':
					sb.append('\u01AF');
					break;
				case '\u00AE':
					sb.append('\u02BE');
					break;
				case '\u00B0':
					sb.append('\u02BF');
					break;
				case '\u00B1':
					sb.append('\u0142');
					break;
				case '\u00B2':
					sb.append('\u00F8');
					break;
				case '\u00B3':
					sb.append('\u0111');
					break;
				case '\u00B4':
					sb.append('\u00FE');
					break;
				case '\u00B5':
					sb.append('\u00E6');
					break;
				case '\u00B6':
					sb.append('\u009C');
					break;
				case '\u00B7':
					sb.append('\u02BA');
					break;
				case '\u00B8':
					sb.append('\u0131');
					break;
				case '\u00B9':
					sb.append('\u00A3');
					break;
				case '\u00BA':
					sb.append('\u00F0');
					break;
				case '\u00BC':
					sb.append('\u01A1');
					break;
				case '\u00BD':
					sb.append('\u01B0');
					break;
				case '\u00C0':
					sb.append('\u00B0');
					break;
				case '\u00C1':
					sb.append('\u2113');
					break;
				case '\u00C2':
					sb.append('\u2117');
					break;
				case '\u00C3':
					sb.append('\u00A9');
					break;
				case '\u00C4':
					sb.append('\u266F');
					break;
				case '\u00C5':
					sb.append('\u00BF');
					break;
				case '\u00C6':
					sb.append('\u00A1');
					break;
				case '\u00E0':
					sb.append('\u0309');
					break;
				case '\u00E1':
					sb.append('\u0300');
					break;
				case '\u00E2':
					sb.append('\u0301');
					break;
				case '\u00E3':
					sb.append('\u0302');
					break;
				case '\u00E4':
					sb.append('\u0303');
					break;
				case '\u00E5':
					sb.append('\u0304');
					break;
				case '\u00E6':
					sb.append('\u0306');
					break;
				case '\u00E7':
					sb.append('\u0307');
					break;
				case '\u00E8':
					sb.append('\u0308');
					break;
				case '\u00E9':
					sb.append('\u030C');
					break;
				case '\u00EA':
					sb.append('\u030A');
					break;
				case '\u00EB':
					sb.append('\uFE20');
					break;
				case '\u00EC':
					sb.append('\uFE21');
					break;
				case '\u00ED':
					sb.append('\u0315');
					break;
				case '\u00EE':
					sb.append('\u030B');
					break;
				case '\u00EF':
					sb.append('\u0310');
					break;
				case '\u00F0':
					sb.append('\u0327');
					break;
				case '\u00F1':
					sb.append('\u0328');
					break;
				case '\u00F2':
					sb.append('\u0323');
					break;
				case '\u00F3':
					sb.append('\u0324');
					break;
				case '\u00F4':
					sb.append('\u0325');
					break;
				case '\u00F5':
					sb.append('\u0333');
					break;
				case '\u00F6':
					sb.append('\u0332');
					break;
				case '\u00F7':
					sb.append('\u0326');
					break;
				case '\u00F8':
					sb.append('\u031C');
					break;
				case '\u00F9':
					sb.append('\u032E');
					break;
				case '\u00FA':
					sb.append('\uFE22');
					break;
				case '\u00FB':
					sb.append('\uFE23');
					break;
				case '\u00FE':
					sb.append('\u0313');
					break;
				case '\u001E':
					// Get rid of any end of field
					//sb.append('');
					break;
				case '\r':
				case '\n':
					// Escape '
					//sb.append('');
					//sb.append('\'');
					break;
				case '"':
					// Escape '
					sb.append('"');
				//sb.append('"');
				//break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}



}
