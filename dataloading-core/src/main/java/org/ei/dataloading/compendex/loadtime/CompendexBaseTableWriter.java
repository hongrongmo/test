package org.ei.dataloading.compendex.loadtime;



import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;



public class CompendexBaseTableWriter

{



	private Perl5Util perl = new Perl5Util();

	private int recsPerFile = -1;

	private int curRecNum = 0;

	private String filename;

	private String propsfilename;

	private PrintWriter out;

	private String filepath;

	private int loadnumber;

	private int filenumber = 0;

	private boolean open = false;

	private Properties props;

	private String[] baseTableFields = CompendexBaseTableRecord.baseTableFields;

	private CompendexRecordFixer fixer = new CompendexRecordFixer();

	{

		props = new Properties();

		props.setProperty("M_ID", "GU");

		props.setProperty("ID", "ID");

		props.setProperty("AN", "EX");

		props.setProperty("IG", "IG");

		props.setProperty("CN", "CN");

		props.setProperty("ISS","IS");

		props.setProperty("LA","LA");

		props.setProperty("AUS","AU");

		props.setProperty("AV","AV");

		props.setProperty("AF", "AF");

		props.setProperty("AC", "AC");

		props.setProperty("AFS", "AS"); // Question mark

		props.setProperty("AY", "AY");

		props.setProperty("AB", "AB");

		props.setProperty("AT", "AT");

		props.setProperty("BN", "BN");

		props.setProperty("BR", "BR");

		props.setProperty("CF", "CF");

		props.setProperty("CC", "CC");

		props.setProperty("CL", "CL");

		props.setProperty("CLS", "CL");

		props.setProperty("CVS", "CV");

		props.setProperty("DS", "DS");

		props.setProperty("DT", "DT");

		props.setProperty("ED", "ED");

		props.setProperty("EF", "EF");

		props.setProperty("EM", "EM");

		props.setProperty("EC", "EC");

		props.setProperty("ES", "ES");

		props.setProperty("EV", "EV");

		props.setProperty("EY","EY");

		props.setProperty("FLS", "FL");

		props.setProperty("MT","MT");

		props.setProperty("ME","ME");

		props.setProperty("MD","MD");

		props.setProperty("M1","M1");

		props.setProperty("M2","M2");

		props.setProperty("MC","MC");

		props.setProperty("MT","MT");

		props.setProperty("MS","MS");

		props.setProperty("MV","MV");

		props.setProperty("MY","MY");

		props.setProperty("MH","MH");

		props.setProperty("MN","MN");

		props.setProperty("NR","NR");

		props.setProperty("NV","NV");

		props.setProperty("OA","OA");

		props.setProperty("PA","PA");

		props.setProperty("PN","PN");

		props.setProperty("PC","PC");

		props.setProperty("PS","PS");

		props.setProperty("PV","PV");

		props.setProperty("PY","PY");

		props.setProperty("PP","PP");

		props.setProperty("SD","SD");

		props.setProperty("ST","ST");

		props.setProperty("SE","SE");

		props.setProperty("SN","SN");

		props.setProperty("SP","SP");

		props.setProperty("SU","SU");

		props.setProperty("TG","TG");

		props.setProperty("TI","TI");

		props.setProperty("TR","TR");

		props.setProperty("TT","TT");

		props.setProperty("UP","UP");

		props.setProperty("VO","VO");

		props.setProperty("VT","VT");

		props.setProperty("XP","XP");

		props.setProperty("YN","YN");

		props.setProperty("YR","YR");

		props.setProperty("TI","TI");

		props.setProperty("RN","RN");

		props.setProperty("SH","SH");

		props.setProperty("WT","WT");

		props.setProperty("AM","AM");

		props.setProperty("CAL","EMPTY"); //Question Mark

		props.setProperty("SC","SC");

		props.setProperty("SS","SS");

		props.setProperty("SV","SV");

		props.setProperty("SY","SY");

		props.setProperty("PO","PO");

		props.setProperty("VN","VN");

		props.setProperty("VM","VM");

		props.setProperty("VC","VC");

		props.setProperty("VS","VS");

		props.setProperty("VV","VV");

		props.setProperty("VY","VY");

		props.setProperty("VX","VX");

		props.setProperty("EX","EX");

		props.setProperty("LF","LF");

		props.setProperty("UR","UR");

		props.setProperty("YP","YP");

		props.setProperty("ML","ML");

		props.setProperty("ASS","AS"); // Question Mark

		props.setProperty("OD","EMPTY");

		props.setProperty("RT","RT");

		props.setProperty("LOAD_NUMBER", "LN");

		props.setProperty("PI","PI");

		props.setProperty("DO","DO");

		props.setProperty("EN","EN");

		props.setProperty("AR","AR");

        props.setProperty("IT","IT");

        props.setProperty("BX", "BX");

        props.setProperty("BE", "BE");

        props.setProperty("BV", "BV");

        props.setProperty("CP", "CP");

        props.setProperty("TY","TY");

        props.setProperty("UPDATE_NUMBER","UN");

	}

	public CompendexBaseTableWriter(int recsPerFile,
									String filename)

	{

		this.recsPerFile = recsPerFile;
		this.filename = filename;

	}

	public CompendexBaseTableWriter(String filename)

	{

		this.filename = filename;

	}



	private void load(String propsfilename)
	    		throws Exception

	{
		try{

			props = new Properties();
			props.load(new FileInputStream(propsfilename));
//			CompendexBaseTableRecord trec = new CompendexBaseTableRecord();
			baseTableFields = CompendexBaseTableRecord.propReader(propsfilename);
		}

		catch(IOException e){
			System.err.println("CPX Property File Not Found:[" + propsfilename + "]");
			System.exit(1);
		}

 	}

	public void begin(String propsfilename)

			throws Exception

	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		open = true;
		curRecNum = 0;
		load(propsfilename);
	}

	public void begin()
			throws Exception
	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable record,boolean fix)
			throws Exception
	{

		StringBuffer recordBuf = new StringBuffer();
		for(int i=0; i<baseTableFields.length; ++i)
		{
			String bf = baseTableFields[i];
			//System.out.println(bf+":"+props.getProperty(bf));
			if(record == null)
			{
				System.out.println("Record was null");
			}

			StringBuffer value = (StringBuffer)record.get(props.getProperty(bf).trim());
			//System.out.println(props.getProperty(bf)+":"+value);
			String valueS = null;
			if(value != null)
			{
				valueS = value.toString();
				if(bf.equals("AN") || bf.equals("EX"))
				{
					valueS = perl.substitute("s/EIX//ig", valueS);
				}

				valueS = perl.substitute("s/\\t/     /g", valueS);

			}

			if(i > 0)
			{
				recordBuf.append("\t");
			}

			if(valueS != null)
			{
				recordBuf.append(valueS);
			}

		}
		if(fix){
			out.println(fixer.fixRecord(recordBuf.toString()));
		}
		else {
			out.println(recordBuf.toString());
		}
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
