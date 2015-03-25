package org.ei.dataloading.ntis.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;

import org.ei.common.*;

public class NTISXMLBaseTableWriter
{

	private Perl5Util perl = new Perl5Util();
	private int recsPerFile = 10000;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	private static Properties props;
	private String[] baseTableFields = NTISBaseTableRecord.baseTableFields;
	private String[] xmlBaseTableFields = NTISBaseTableRecord.xmlbaseTableFields;



	public NTISXMLBaseTableWriter(int recsPerFile,
							   String filename)
	{
		this.recsPerFile = recsPerFile;
		this.filename = filename;
	}

	public NTISXMLBaseTableWriter(String filename)
	{
			this.filename = filename;
	}

	public void begin()
			throws Exception
	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable record)
			throws Exception
	{

			if(curRecNum >= recsPerFile)
			{
				if(open)
				{
					end();
				}
				begin();
			}

			StringBuffer recordBuf = new StringBuffer();
			for(int i=0; i<baseTableFields.length; ++i)
			{
				String bf = baseTableFields[i];
				if(record != null)
				{
					String value = (String)record.get(bf);
					if(value != null)
					{
						value = perl.substitute("s/\\t/     /g", value);
					}


					if(i > 0)
					{
						recordBuf.append("	");
					}


					if(value != null)
					{
						recordBuf.append(value);
					}
				}

			}

			out.println(recordBuf.toString());
			out.flush();
			++curRecNum;
	}

	public void writeXMLRec(Hashtable record)
				throws Exception
	{

			StringBuffer pa1Buffer = new StringBuffer("by ");
			StringBuffer pa2Buffer = new StringBuffer();
			StringBuffer pa3Buffer = new StringBuffer();
			StringBuffer pa4Buffer = new StringBuffer();
			StringBuffer pa5Buffer = new StringBuffer();
			if(curRecNum >= recsPerFile)
			{
				if(open)
				{
					end();
				}
				begin();
			}

			StringBuffer recordBuf = new StringBuffer();
			for(int j=0; j<baseTableFields.length; j++)
			{
				String bf = baseTableFields[j];
				if(record != null)
				{
					String value=null;
					if(bf.equals("M_ID"))   								// M_ID
					{
						value = (String)record.get("M_ID");
						//System.out.println("M_ID="+value);

					}
					else if(bf.equals("AN")) 											//AN
					{
						value = (String)record.get("AccessionNumber");
						//System.out.println("AccessionNumber="+value);
					}
					else if(bf.equals("CAT"))											//CAT
					{
						value = (String)record.get("CategoryCode");
						StringBuffer catBuffer = new StringBuffer("Field ");

						String[] catArray = value.split(Constants.AUDELIMITER);
						for(int i=0;i<catArray.length;i++)
						{
							if(catArray[i]!=null && catArray[i].indexOf("|")>-1)
							{
								catBuffer.append(catArray[i].substring(0,catArray[i].indexOf("|")).trim());
								if(i<catArray.length-1)
								{
									catBuffer.append(", ");
								}
							}
							else
							{
								catBuffer.append(catArray[i].trim());
							}

						}
						value=catBuffer.toString();
					}
					else if(bf.equals("IC"))											//IC
					{
						value = (String)record.get("SourceCode");
						StringBuffer icBuffer = new StringBuffer("U/");
						if(value!=null)
						{
							if(value.indexOf(Constants.AUDELIMITER)>-1)
							{
								value = value.replaceAll(Constants.AUDELIMITER,"/");
							}
							icBuffer.append(value.toLowerCase());

						}

						value = (String)record.get("CountryCode");
						if(value!=null && value.length()>0)
						{
							String[] countryArray = value.split(Constants.AUDELIMITER);
							for(int i=0;i<countryArray.length;i++)
							{
								icBuffer.append("/fn"+countryArray[i].toLowerCase());
							}

						}

						value = (String)record.get("LanguageCode");
						if(value!=null && value.length()>0)
						{
							String[] languageArray = value.split(Constants.AUDELIMITER);
							for(int i=0;i<languageArray.length;i++)
							{
								icBuffer.append("/ln"+languageArray[i].toLowerCase());
							}
							icBuffer.append(";12113,1101");

						}
						else
						{
							icBuffer.append(";12111,1101");
						}
						value=icBuffer.toString();
					}
					else if(bf.equals("PR"))									//PR
					{

						value=null;

					}
					else if(bf.equals("SO"))										//SO
					{
						StringBuffer soBuffer = new StringBuffer("");
						value=(String)record.get("PrimaryAuthor");
						if(value!=null && value.length()>0)
						{
							soBuffer.append(value);
						}
						value=(String)record.get("SecondaryAuthor");
						if(value!=null && value.length()>0)
						{
							String[] secondaryAuthorArray = value.split(Constants.AUDELIMITER);
							for(int i=0;i<secondaryAuthorArray.length;i++)
							{
								if(secondaryAuthorArray[i]!=null)
								{
									if(soBuffer.length()>0)
									{
										soBuffer.append("**"+secondaryAuthorArray[i]);
									}
									else
									{
										soBuffer.append(secondaryAuthorArray[i]);
									}
								}

							}

						}
						value=(String)record.get("SponsorAuthor");

						String[] sponsorAuthorArray = value.split(Constants.AUDELIMITER);
						for(int i=0;i<sponsorAuthorArray.length;i++)
						{
							if(sponsorAuthorArray[i]!=null && sponsorAuthorArray[i].length()>0)
							{
								if(soBuffer.length()>0)
								{
									soBuffer.append("*"+sponsorAuthorArray[i]);
								}
								else
								{
									soBuffer.append(sponsorAuthorArray[i]);
								}
							}

						}


						if(soBuffer.length()>0)
						{
							value = soBuffer.toString();
						}
						else
						{
							value=null;
						}

					}
					else if(bf.equals("TI"))											//TI
					{

						value = (String)record.get("Title");

					}
					else if(bf.equals("IDES"))											//IDES
					{

						value=null;

					}
					else if(bf.equals("VI"))											//VI
					{

						value = (String)record.get("GVVII");

					}
					else if(bf.equals("TN"))											//TN
					{

						value = (String)record.get("TitleNote");

					}
					else if(bf.equals("PA1"))											//PA1
					{
						value = (String)record.get("PersonalAuthor");
						//pa1Buffer = new StringBuffer("by ");

						String[] pa1Array = value.split(Constants.AUDELIMITER);
						for(int i=0;i<pa1Array.length;i++)
						{
							String singlePa1 = pa1Array[i];
							if(singlePa1!=null)
							{
								if(singlePa1.indexOf(",")>-1)
								{
									singlePa1=singlePa1.substring(singlePa1.indexOf(",")+1)+" {"+singlePa1.substring(0,singlePa1.indexOf(","));
								}
							}

							if(i==0)
							{
								pa1Buffer.append(singlePa1);

							}
							else if((pa2Buffer.length()+singlePa1.length())<3999)
							{
								pa2Buffer.append(singlePa1);
								if(i<pa1Array.length-1)
								{
									pa2Buffer.append(Constants.AUDELIMITER);
								}
							}
							else if((pa3Buffer.length()+singlePa1.length())<3999)
							{
								pa3Buffer.append(singlePa1);
								if(i<pa1Array.length-1)
								{
									pa3Buffer.append(Constants.AUDELIMITER);
								}
							}
							else if((pa4Buffer.length()+singlePa1.length())<3999)
							{
								pa4Buffer.append(singlePa1);
								if(i<pa1Array.length-1)
								{
									pa4Buffer.append(Constants.AUDELIMITER);
								}
							}
							else if((pa5Buffer.length()+singlePa1.length())<3999)
							{
								pa5Buffer.append(singlePa1);
								if(i<pa1Array.length-1)
								{
									pa5Buffer.append(Constants.AUDELIMITER);
								}
							}



						}
						if(value!=null && value.length()>0)
						{
							value = pa1Buffer.toString();
						}
						else
						{
							value = " {";
						}

					}
					else if(bf.equals("PA2"))											//PA2
					{
						if(pa2Buffer.length()>0)
						{
							value=pa2Buffer.toString();
						}
						else
						{
							value="{";
						}

					}
					else if(bf.equals("PA3"))											//PA3
					{

						if(pa3Buffer.length()>0)
						{
							value=pa3Buffer.toString();
						}
						else
						{
							value="{";
						}

					}
					else if(bf.equals("PA4"))											//PA4
					{

						if(pa4Buffer.length()>0)
						{
							value=pa4Buffer.toString();
						}
						else
						{
							value="{";
						}

					}
					else if(bf.equals("PA5"))											//PA5
					{

						if(pa5Buffer.length()>0)
						{
							value=pa5Buffer.toString();
						}
						else
						{
							value="{";
						}

					}
					else if(bf.equals("RD"))											//RD
					{
						StringBuffer reportBuffer = new StringBuffer();
						if(record.get("ReportDay")!=null)
						{
							reportBuffer.append((String)record.get("ReportDay"));
						}
						if(record.get("ReportMonth")!=null)
						{
							if(reportBuffer.length()>0)
							{
								reportBuffer.append(" ");
							}
							reportBuffer.append((String)record.get("ReportMonth"));
						}
						if(record.get("ReportYear")!=null)
						{
							if(reportBuffer.length()>0)
							{
								reportBuffer.append(" ");
							}
							reportBuffer.append((String)record.get("ReportYear"));
						}
						value=reportBuffer.toString();

					}
					else if(bf.equals("XP"))											//XP
					{
						value=(String)record.get("PageCount");
						if(value!=null && value.length()>0)
						{
							value=value+"p";
						}

					}
					else if(bf.equals("PDES"))											//PDES
					{
						value=null;
					}
					else if(bf.equals("RN"))										//RN
					{
						value=(String)record.get("ReportNumber");
						if(value!=null && value.length()>0)
						{
							value="{"+value.replaceAll(Constants.AUDELIMITER,", {")+"{";
						}
						else
						{
							value="{{";
						}

					}
					else if(bf.equals("CT"))										//CT
					{
						StringBuffer ctBuffer=new StringBuffer();
						value=(String)record.get("ContractNumber");
						if(value!=null && value.length()>0)
						{
							value=value.replaceAll(Constants.AUDELIMITER,", {");
							ctBuffer.append("Contract {"+value.toString());
						}

						value=(String)record.get("GrantNumber");
						if(value!=null && value.length()>0)
						{
							if(ctBuffer.length()>0)
							{
								ctBuffer.append(Constants.AUDELIMITER);
							}

							value=value.replaceAll(Constants.AUDELIMITER,", {");
							ctBuffer.append("Grants {"+value.toString());
						}

						value=(String)record.get("OrderNumber");
						if(value!=null && value.length()>0)
						{
							if(ctBuffer.length()>0)
							{
								ctBuffer.append(Constants.AUDELIMITER);
							}

							value=value.replaceAll(Constants.AUDELIMITER,", {");
							ctBuffer.append("Order {"+value.toString());
						}

						value=ctBuffer.toString();
						if(value.length()==0)
						{
							value="{{";
						}

					}
					else if(bf.equals("PN"))											//PN
					{
						StringBuffer ctBuffer=new StringBuffer("projs. {");
						value=(String)record.get("ProjectNumber");
						if(value!=null && value.length()>0)
						{
							value=value.replaceAll(Constants.AUDELIMITER,", {");
						}
						else
						{
							value="{{";
						}

					}
					else if(bf.equals("TNUM"))											//TNUM
					{
						StringBuffer tnBuffer= new StringBuffer();
						value=(String)record.get("TaskNumber");
						if(value!=null && value.length()>0)
						{
							String[] taskNumberArray = value.split(Constants.AUDELIMITER);
							if(taskNumberArray.length>1)
							{
								tnBuffer.append("Tasks {");
							}
							else
							{
								tnBuffer.append("Task {");
							}
							value=value.replaceAll(Constants.AUDELIMITER,", {");
						}
						else
						{
							value="{{";
						}
					}
					else if(bf.equals("MAA1"))											//MAA1
					{
						StringBuffer ctBuffer=new StringBuffer();
						value=(String)record.get("MonitorAgencyNumber");
						if(value!=null)
						{
							value=value.replaceAll(Constants.AUDELIMITER,"/");
						}

					}
					else if(bf.equals("MAA2"))											//MAA2
					{
						value=null;
					}
					else if(bf.equals("MAN1"))											//MAN1
					{
						value=null;
					}
					else if(bf.equals("MAN2"))											//MAN2
					{
						value=null;
					}
					else if(bf.equals("CL"))											//CL
					{
						value=null;
					}
					else if(bf.equals("SU"))										//SU
					{
						StringBuffer ctBuffer=new StringBuffer();
						value=(String)record.get("SupplementaryNotes");
					}
					else if(bf.equals("AV"))											//AV
					{
						StringBuffer ctBuffer=new StringBuffer();
						value=(String)record.get("AvailabilityNote");
					}
					else if(bf.equals("DES"))											//DES
					{
						value=(String)record.get("Descriptor");
						if(value!=null && value.length()>0)
						{
							value="Descriptors: "+value.replaceAll(Constants.AUDELIMITER,", ");
						}

					}
					else if(bf.equals("NU1"))											//NU1
					{
						value=null;
					}
					else if(bf.equals("IDE"))											//IDE
					{
						value=(String)record.get("Identifier");
						if(value!=null && value.length()>0)
						{
							value="Identifiers: "+value.replaceAll(Constants.AUDELIMITER,", ");
						}

					}
					else if(bf.equals("HN"))											//HN
					{
						value=null;
					}
					else if(bf.equals("AB"))											//AB
					{
						StringBuffer ctBuffer=new StringBuffer();
						value=(String)record.get("Abstract");
					}
					else if(bf.equals("NU2"))											//NU2
					{
						value=null;
					}
					else if(bf.equals("IB"))											//IB
					{
						value=null;
					}
					else if(bf.equals("TA"))											//TA
					{
						value=null;
					}
					else if(bf.equals("NU3"))											//NU3
					{
						value=null;
					}
					else if(bf.equals("NU4"))											//NU4
					{
						value=null;
					}
					else if(bf.equals("LC"))											//LC
					{
						value=null;
					}
					else if(bf.equals("SE"))											//SE
					{
						value=null;
					}
					else if(bf.equals("CAC"))											//CAC
					{
						StringBuffer cacBuffer = new StringBuffer();
						value =(String)record.get("PrimaryAuthorCode");
						if(value!=null && value.length()>0)
						{
							cacBuffer.append(value);
						}

						value=(String)record.get("SecondaryAuthorCode");
						if(value!=null && value.length()>0)
						{
							if(cacBuffer.length()>0)
							{
								cacBuffer.append(" ");
							}
							cacBuffer.append(value);
						}

						value=(String)record.get("SponsorAuthorCode");
						if(value!=null && value.length()>0)
						{
							if(cacBuffer.length()>0)
							{
								cacBuffer.append(" ");
							}
							cacBuffer.append(value);
						}
						value=cacBuffer.toString();
						value=value.replaceAll(Constants.AUDELIMITER," ");
					}
					else if(bf.equals("DLC"))											//DLC
					{
						value=(String)record.get("DTICLimitationCode");
					}
					else if(bf.equals("LOAD_NUMBER"))									//LOAD_NUMBER
					{
						value=(String)record.get("LOAD_NUMBER");
					}

					if(value != null)
					{
						value = perl.substitute("s/\\t/     /g", value);
					}


					if(j > 0 && recordBuf.length()>0)
					{
						recordBuf.append("\t");
					}


					if(value != null)
					{
						recordBuf.append(value);
					}
				}

			}

			out.println(recordBuf.toString());
			out.flush();
			++curRecNum;
	}


	public void end()
			throws Exception
	{
		if(open)
		{
			out.close();
			open = false;
		}
	}
}
