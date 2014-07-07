package org.ei.data;

import java.sql.Clob;
import java.sql.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.data.*;
import org.ei.data.encompasspat.loadtime.*;
import org.ei.data.encompasslit.loadtime.*;
import org.ei.data.upt.loadtime.*;
import org.ei.data.ntis.loadtime.*;
import org.ei.data.cbnb.loadtime.*;
import org.ei.data.bd.*;
import org.ei.data.bd.loadtime.*;
import org.ei.data.inspec.loadtime.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.util.*;
import org.ei.connectionpool.*;
import java.text.*;
import java.io.*;
import java.lang.Process;
import java.util.regex.*;
import org.ei.util.GUID;
import org.ei.xml.Entity;

public class LookupIndex
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

	private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    static String url="jdbc:oracle:thin:@jupiter:1521:eidb1";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_ev_search";
    static String password="ei3it";
    static String database;
    static String action;
    static int updateNumber=0;
    static boolean test = false;
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {31});
    public static final String GROUPDELIMITER = new String(new char[] {02});

    public static void main(String args[])
        throws Exception
    {
		long startTime = System.currentTimeMillis();
		String fileToBeLoaded 	= null;
		String input;
		int iThisChar; // To read individual chars with System.in.read()


		if(args.length>7)
		{
			if(args[0]!=null)
			{
				FastSearchControl.BASE_URL = args[0];
			}
			if(args[1]!=null)
			{
				url = args[1];
			}
			if(args[2]!=null)
			{
				driver = args[2];
			}
			if(args[3]!=null)
			{
				username = args[3];
			}
			if(args[4]!=null)
			{
				password = args[4];
			}

			if(args[5]!=null)
			{
				action = args[5];
			}

			if(args[6]!=null)
			{
				database = args[6];
			}

			if (args[7]!=null)
			{
				updateNumber = Integer.parseInt(args[7]);
			}

		}


		try
		{

			LookupIndex bdc = new LookupIndex();
			con = bdc.getConnection(url,driver,username,password);

			if(action.equalsIgnoreCase("lookupIndex"))
			{
				bdc.outputLookupIndex(bdc.getLookupData("lookupIndex"),updateNumber);
			}

		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
		}

        System.exit(1);
    }

    private void outputLookupIndex(HashMap lookupData, int updateNumber)
    {

		if(lookupData.get("AUTHOR")!=null)
		{
			writeToFile((ArrayList)lookupData.get("AUTHOR"),"AUTHOR",updateNumber);
		}

		if(lookupData.get("AFFILIATION")!=null)
		{
			writeToFile((ArrayList)lookupData.get("AFFILIATION"),"AFFILIATION",updateNumber);
		}

		if(lookupData.get("CONTROLLEDTERM")!=null)
		{
			writeToFile((ArrayList)lookupData.get("CONTROLLEDTERM"),"CONTROLLEDTERM",updateNumber);
		}

		if(lookupData.get("PUBLISHERNAME")!=null)
		{
			writeToFile((ArrayList)lookupData.get("PUBLISHERNAME"),"PUBLISHERNAME",updateNumber);
		}

		if(lookupData.get("SERIALTITLE")!=null)
		{
			writeToFile((ArrayList)lookupData.get("SERIALTITLE"),"SERIALTITLE",updateNumber);
		}

	}



	private void writeToFile(List data, String field, int updateNumber)
	{
		String fileName = "./lookupindex/"+database+"/"+database+"-"+field+"-"+updateNumber+".txt";
		FileWriter out;

		File file=new File("lookupindex/"+database);

		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}

			out = new FileWriter(fileName);
			System.out.println("field==> "+field);
			if(data != null)
			{
				for(int i=0;i<data.size();i++)
				{
					if(data.get(i)!=null)
					{
						out.write(data.get(i)+"\t"+database+"\n");
					}
				}
			}
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

    private void creatDeleteFile(ResultSet rs,String database,int updateNumber)
    {

		String batchidFormat = "0000";
		String batchID = "0001";
		String numberID = "0000";
		File file=new File("fast");
		FileWriter out= null;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,database);

		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}

			long starttime = System.currentTimeMillis();
			String batchPath = "fast/batch_" + updateNumber+"_"+batchID;

			file=new File(batchPath);
			if(!file.exists())
			{
				file.mkdir();
			}
			String root = batchPath +"/EIDATA/tmp";
			file=new File(root);

			if(!file.exists())
			{
				file.mkdir();
			}

			file = new File(root+"/delete.txt");

			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(file);
			while (rs.next())
			{
				if(rs.getString("M_ID") != null)
				{
					out.write(rs.getString("M_ID")+"\n");
				}
			}
			out.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs !=null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if(out !=null)
			{
				try
				{
					out.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}

	}


	private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
	{

		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
		String[] credentials = new String[]{"CPX","PCH","CHM","GEO"};
		String[] dbName = {database};

		int intDbMask = databaseConfig.getMask(dbName);

		Iterator searchTerms = inputMap.keySet().iterator();

		while (searchTerms.hasNext())
		{
			String term1=null;
			try
			{
				SearchControl sc = new FastSearchControl();
				term1 = (String) searchTerms.next();

				int oc = Integer.parseInt((String)inputMap.get(term1));
				Query queryObject = new Query(databaseConfig, credentials);
				queryObject.setDataBase(intDbMask);

				String searchID = (new GUID()).toString();
				queryObject.setID(searchID);
				queryObject.setSearchType(Query.TYPE_QUICK);

				queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.compile();
				String sessionId = null;
				int pagesize = 25;
				SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
				int c = result.getHitCount();
				String indexCount = (String)inputMap.get(term1);
				if(indexCount!=null && indexCount!="" && Integer.parseInt(indexCount) >= c)
				{
					outputList.add(term1);
				}

			}
			catch(Exception e)
			{
				System.out.println("term1= "+term1);
				e.printStackTrace();
			}

		}

		return outputList;

	}

    public HashMap getLookupData(String action) throws Exception
    {
		Statement stmt = null;
		ResultSet rs = null;
		HashMap results = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			String sqlString=null;
			if(database.equals("ins"))
			{
				sqlString = "select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from new_ins_master where load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setInspecRecs(rs);

			}
			else if(database.equals("grf"))
			{
				sqlString = "select * from georef_master where load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setGRFRecs(rs);

			}
			else if(database.equals("ept"))
			{
				sqlString = "select * from ept_master where load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setEPTRecs(rs);

			}
			else if(database.equals("upa"))
			{
				sqlString = "select * from upt_master where ac='US' and load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setUPARecs(rs);

			}
			else if(database.equals("eup"))
			{
				sqlString = "select * from upt_master where ac='EP' and load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setUPARecs(rs);

			}
			else if(database.equals("nti"))
			{
				sqlString = "select * from ntis_master where load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setNTISRecs(rs);

			}
			else if(database.equals("cbn"))
			{
				sqlString = "select * from cbn_master where load_number="+updateNumber;
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setCBNRecs(rs);

			}
			else
			{
				sqlString = "select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM BD_MASTER where loadNumber="+updateNumber+" and database='"+database+"'";
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setRecs(rs);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
        }

        return results;

	}

	public HashMap setInspecRecs(ResultSet rs)
				throws Exception
		{
			int i = 0;
			CombinedWriter writer = new CombinedXMLWriter(10000,10000,"ins","dev");
			XmlCombiner xml = new XmlCombiner(writer);
			HashMap recs = new HashMap();
			List authorList = new ArrayList();
			List affiliationList = new ArrayList();
			List serialTitleList = new ArrayList();
			List controltermList = new ArrayList();
			List publishernameList = new ArrayList();
			String database = null;
			String accessNumber = null;
			try
			{
				INSPECCombiner c = new INSPECCombiner(writer);
				while (rs.next())
				{
					++i;
					EVCombinedRec rec = new EVCombinedRec();

					accessNumber = rs.getString("anum");

					if(accessNumber !=null && accessNumber.length()>5)
					{

						if((rs.getString("aus") != null) || (rs.getString("aus2") != null))
						{
							StringBuffer aus = new StringBuffer();
							if(rs.getString("aus") != null)
							{
								aus.append(rs.getString("aus"));
							}
							if(rs.getString("aus2") != null)
							{
								aus.append(rs.getString("aus2"));
							}
							authorList.addAll(Arrays.asList(c.prepareAuthor(aus.toString().toUpperCase())));
						}
						//else if(rs.getString("eds") != null)
						//{
						//	rec.put(EVCombinedRec.EDITOR, c.prepareAuthor(rs.getString("eds")));
	                	//}

						if(rs.getString("aaff") != null)
						{
							StringBuffer aaff = new StringBuffer(rs.getString("aaff"));

							if(rs.getString("aaffmulti1") != null)
							{
								aaff = new StringBuffer(rs.getString("aaffmulti1"));

								if (rs.getString("aaffmulti2") != null)
								{
									aaff.append(rs.getString("aaffmulti2"));
								}
							}
							affiliationList.addAll(Arrays.asList(c.prepareAuthor(aaff.toString())));
						}

						if(rs.getString("cvs") != null)
						{
						     controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("cvs").toUpperCase())));
	                	}

						if(rs.getString("ppub") != null)
						{
						     publishernameList.add(xml.preparePublisherName(rs.getString("ppub").toUpperCase()));
	                    }

	                    if(rs.getString("chi") != null)
						{
						      controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("chi").toUpperCase())));
	                	}

						if(rs.getString("pubti") != null)
						{
							serialTitleList.add(rs.getString("pubti").toUpperCase());
						}

						if(rs.getString("pfjt") != null)
						{
							serialTitleList.add(rs.getString("pfjt").toUpperCase());
						}


						if(rs.getString("ipc")!=null)
						{
							String ipcString = rs.getString("ipc");
							ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
							rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipcString);
						}

					}
				}

				recs.put("AUTHOR",authorList);
				recs.put("AFFILIATION",affiliationList);
				recs.put("CONTROLLEDTERM",controltermList);
				recs.put("PUBLISHERNAME",publishernameList);
				recs.put("SERIALTITLE",serialTitleList);
				//recs.put("DATABASE",database);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return recs;
	}

	public HashMap setGRFRecs(ResultSet rs)
					throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"grf","dev");
		XmlCombiner xml = new XmlCombiner(writer);
		HashMap recs = new HashMap();
		List authorList = new ArrayList();
		List affiliationList = new ArrayList();
		List serialTitleList = new ArrayList();
		List controltermList = new ArrayList();
		List publishernameList = new ArrayList();
		String database = null;
		String accessNumber = null;
		try
		{
			while (rs.next())
			{

				accessNumber = rs.getString("ID_NUMBER");

				if(accessNumber !=null && accessNumber.length()>5)
				{
					//rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);


					//AUTHOR************
					String aString = rs.getString("PERSON_ANALYTIC");
					if(aString != null)
					{
						authorList.addAll(Arrays.asList(aString.split(AUDELIMITER)));
					}
					else if(rs.getString("PERSON_MONOGRAPH") != null)
					{
						String eString = rs.getString("PERSON_MONOGRAPH");

						String otherEditors = rs.getString("PERSON_COLLECTION");
						if(otherEditors != null)
						{
							eString = eString.concat(AUDELIMITER).concat(otherEditors);
						}
						//rec.put(EVCombinedRec.EDITOR, eString.split(AUDELIMITER));

					}

					// AUTHOR AFFLICATION *********

					String affilitation = rs.getString("AUTHOR_AFFILIATION");
					if(affilitation != null)
					{
					  List affilations= new ArrayList();
					  String[] affilvalues = null;
					  String[] values = null;
					  affilvalues = affilitation.split(AUDELIMITER);
					  for(int x = 0 ; x < affilvalues.length; x++)
					  {
						  affilations.add(affilvalues[x]);
				  	  }
					 if(rs.getString("AFFILIATION_SECONDARY") != null)
					  {
						String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
						affilvalues = secondaffiliations.split(AUDELIMITER);
						for(int x = 0 ; x < affilvalues.length; x++)
						{
						  values = affilvalues[x].split(IDDELIMITER);
						  affilations.add(values[0]);
						}
					  }
					  if(!affilations.isEmpty())
					  {
						//rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, (String[]) affilations.toArray(new String[]{}));
					  	affiliationList.addAll(affilations);
					  }
					}

					// CONTROLL_TERMS (CVS)
					if(rs.getString("INDEX_TERMS") != null)
					{
						String[] idxterms = rs.getString("INDEX_TERMS").split(AUDELIMITER);
						for(int z = 0; z < idxterms.length; z++)
						{
							idxterms[z] = idxterms[z].replaceAll("[A-Z]*" + IDDELIMITER,"");
						}
						controltermList.addAll(Arrays.asList(idxterms));
					}

					if(rs.getString("PUBLISHER") != null)
					{
						publishernameList.addAll(Arrays.asList(rs.getString("PUBLISHER").split(AUDELIMITER)));
					}

					if(rs.getString("TITLE_OF_SERIAL") != null)
					{
						serialTitleList.add(rs.getString("TITLE_OF_SERIAL"));
					}
				}
			}

			recs.put("AUTHOR",authorList);
			recs.put("AFFILIATION",affiliationList);
			recs.put("CONTROLLEDTERM",controltermList);
			recs.put("PUBLISHERNAME",publishernameList);
			recs.put("SERIALTITLE",serialTitleList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return recs;
	}

	public HashMap setEPTRecs(ResultSet rs)
						throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"ept","dev");
		EptCombiner c = new EptCombiner(writer);
		HashMap recs = new HashMap();
		List authorList = new ArrayList();
		List affiliationList = new ArrayList();
		List serialTitleList = new ArrayList();
		List controltermList = new ArrayList();
		List publishernameList = new ArrayList();
		String database = null;
		String accessNumber = null;
		try
		{
			while (rs.next())
			{
				++i;
				EVCombinedRec rec = new EVCombinedRec();

				accessNumber = rs.getString("dn");

				if(accessNumber !=null && accessNumber.length()>5)
				{
					rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);


					//AUTHOR************
					String aString = rs.getString("pat_in");
					if(aString != null)
					{
						authorList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(c.replaceNull(aString)))));
					}


					// AUTHOR AFFLICATION *********

					String affilitation = rs.getString("cs");
					if(affilitation != null)
					{
						affiliationList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(c.replaceNull(affilitation)))));

					}



					// CONTROLL_TERMS (CVS)
					String ct = c.replaceNull(rs.getString("ct"));
					if(ct != null)
					{
						CVSTermBuilder termBuilder = new CVSTermBuilder();
						String cv = termBuilder.getNonMajorTerms(ct);
						String mh = termBuilder.getMajorTerms(ct);
						StringBuffer cvsBuffer = new StringBuffer();

						String expandedMajorTerms = termBuilder.expandMajorTerms(mh);
						String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);
						String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
						String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

						if (!expandedCV2.equals(""))
							cvsBuffer.append(expandedCV1).append(";").append(expandedCV2);
						else
							cvsBuffer.append(expandedCV1);

						String parsedCV = termBuilder.formatCT(cvsBuffer.toString());

						String parsedMH = termBuilder.formatCT(expandedMH);
						controltermList.addAll(Arrays.asList(c.prepareMulti(termBuilder.getStandardTerms(parsedCV), Constants.CVS)));
						controltermList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(termBuilder.getStandardTerms(parsedMH)), Constants.CVS)));
					}

				}
			}

			recs.put("AUTHOR",authorList);
			recs.put("AFFILIATION",affiliationList);
			recs.put("CONTROLLEDTERM",controltermList);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return recs;
	}

	public HashMap setUPARecs(ResultSet rs)
							throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"upa","dev");
		UPTCombiner c = new UPTCombiner("upa",writer);
		HashMap recs = new HashMap();
		List authorList = new ArrayList();
		List affiliationList = new ArrayList();
		List serialTitleList = new ArrayList();
		List controltermList = new ArrayList();
		List publishernameList = new ArrayList();
		String database = null;
		String accessNumber = null;
		try
		{
			while (rs.next())
			{
				String patentNumber = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("pn"))));
				String kindCode = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("kc"))));
                String authCode = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("ac"))));

				if (patentNumber != null)
				{

					//AUTHOR************

					if (rs.getString("inv") != null)
					{
						if (rs.getString("asg") != null)
						{
							List lstAsg = c.convertString2List(rs.getString("asg"));
							List lstInv = c.convertString2List(rs.getString("inv"));

							if (authCode.equals(c.EP_CY) && lstInv.size() > 0 && lstAsg.size() > 0)
							{
								lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
							}

							String[] arrVals = (String[]) lstInv.toArray(new String[1]);
							arrVals[0] = c.replaceNull(arrVals[0]);

							for (int j = 0; j < arrVals.length; j++) {
								arrVals[j] = c.formatAuthor(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(arrVals[j]))));
							}

							if (arrVals != null)
							{
								authorList.addAll(Arrays.asList(arrVals));
							}

						}
						else
						{
							authorList.addAll(Arrays.asList(c.convert2Array(c.formatAuthor(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("inv"))))))));
						}
					}


					// AUTHOR AFFLICATION *********

					 if (rs.getString("asg") != null)
					 {
						if (rs.getString("inv") != null)
						{
							List lstAsg = c.convertString2List(rs.getString("asg"));
							List lstInv = c.convertString2List(rs.getString("inv"));

							if (authCode.equals(c.US_CY) && lstInv.size() > 0 && lstAsg.size() > 0) {
								lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
							}

							String[] arrVals = (String[]) lstAsg.toArray(new String[1]);

							arrVals[0] = c.replaceNull(arrVals[0]);

							for (int j = 0; j < arrVals.length; j++) {
								arrVals[j] = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(arrVals[j])));

							}

							if (arrVals != null)
							{
								affiliationList.addAll(Arrays.asList(arrVals));
							}
						}
						else
						{
							affiliationList.addAll(Arrays.asList(c.convert2Array(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("asg")))))));
						}

					}
				}
			}

			recs.put("AUTHOR",authorList);
			recs.put("AFFILIATION",affiliationList);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return recs;
	}

	public HashMap setNTISRecs(ResultSet rs)
						throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"nti","dev");
		NTISCombiner ntis = new NTISCombiner(writer);
		HashMap recs = new HashMap();
		List authorList = new ArrayList();
		List affiliationList = new ArrayList();
		List serialTitleList = new ArrayList();
		List controltermList = new ArrayList();
		List publishernameList = new ArrayList();
		String database = null;
		String accessNumber = null;
		try
		{
			while (rs.next())
			{

				accessNumber = rs.getString("AN");

				if(accessNumber !=null)
				{


					//AUTHOR************
					 String aut = NTISAuthor.formatAuthors(rs.getString("PA1"),
					                    				   rs.getString("PA2"),
					                    				   rs.getString("PA3"),
					                    				   rs.getString("PA4"),
					                    				   rs.getString("PA5"),
					                    				   rs.getString("HN"));

					if (aut != null)
					{
						authorList.addAll(Arrays.asList(ntis.prepareAuthor(aut)));
            		}


					// AUTHOR AFFLICATION *********

					String affil = ntis.formatAffil(rs.getString("SO"));
					Map pAndS = NTISData.authorAffiliationAndSponsor(affil);
					if(pAndS.containsKey(Keys.PERFORMER))
					{
						affiliationList.add(pAndS.get(Keys.PERFORMER));
					}

					if(pAndS.containsKey(Keys.RSRCH_SPONSOR))
					{
						affiliationList.add(pAndS.get(Keys.RSRCH_SPONSOR));
                	}


					// CONTROLL_TERMS (CVS)
					String cv = ntis.formatDelimiter(ntis.formatCV(rs.getString("DES")));
					if (cv != null)
					{
						controltermList.addAll(Arrays.asList(ntis.prepareMulti(cv)));
					}


				}
			}

			recs.put("AUTHOR",authorList);
			recs.put("AFFILIATION",affiliationList);
			recs.put("CONTROLLEDTERM",controltermList);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return recs;
	}

	public HashMap setCBNRecs(ResultSet rs)
							throws Exception
		{

			CombinedWriter writer = new CombinedXMLWriter(10000,10000,"cbn","dev");
			CBNBCombiner cbn = new CBNBCombiner(writer);
			HashMap recs = new HashMap();
			List serialTitleList = new ArrayList();
			List controltermList = new ArrayList();


			try
			{
				while (rs.next())
				{


					// CONTROLL_TERMS (CVS)

					if (rs.getString("ebt") != null)
					{

						controltermList.addAll(Arrays.asList(cbn.prepareMulti(rs.getString("ebt"))));
					}

					// SERIAL_TITLE

					if (rs.getString("fjl") != null)
					{
						serialTitleList.add(rs.getString("fjl"));
					}




				}

				recs.put("CONTROLLEDTERM",controltermList);
				recs.put("SERIALTITLE",serialTitleList);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return recs;
	}


	private HashMap setRecs(ResultSet rs)
			throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"cpx","dev");
		XmlCombiner xml = new XmlCombiner(writer);
		HashMap recs = new HashMap();
		List authorList = new ArrayList();
		List affiliationList = new ArrayList();
		List serialTitleList = new ArrayList();
		List controltermList = new ArrayList();
		List publishernameList = new ArrayList();
		String database = null;
		String accessNumber = null;
		try
		{
			while (rs.next())
			{
				++i;
				EVCombinedRec rec = new EVCombinedRec();

				accessNumber = rs.getString("ACCESSNUMBER");

				if(accessNumber !=null && accessNumber.length()>5 && !(accessNumber.substring(0,6).equals("200138")))
				{
					rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);

					if(rs.getString("AUTHOR") != null)
					{
						String authorString = rs.getString("AUTHOR");
						if(rs.getString("AUTHOR_1") !=null)
						{
							authorString=authorString+rs.getString("AUTHOR_1");
						}
						authorList.addAll(Arrays.asList(xml.prepareBdAuthor(authorString.toUpperCase())));
					}

					if (rs.getString("AFFILIATION") != null)
					{
						String affiliation = rs.getString("AFFILIATION");
						if(rs.getString("AFFILIATION_1")!=null)
						{
							affiliation = affiliation+rs.getString("AFFILIATION_1");
						}
						BdAffiliations aff = new BdAffiliations(affiliation.toUpperCase());
						affiliationList.addAll(Arrays.asList(aff.getSearchValue()));

					}

					if (rs.getString("CHEMICALTERM") != null)
					{
						controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CHEMICALTERM").toUpperCase())));
					}

					if (rs.getString("CONTROLLEDTERM") != null)
					{
						controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CONTROLLEDTERM").toUpperCase())));
					}

					if (rs.getString("PUBLISHERNAME") != null)
					{
						publishernameList.add(xml.preparePublisherName(rs.getString("PUBLISHERNAME").toUpperCase()));
					}

					if (rs.getString("SOURCETITLE") != null)
					{
						serialTitleList.add(rs.getString("SOURCETITLE").toUpperCase());
					}

					if(rs.getString("DATABASE") != null)
					{
						database = rs.getString("DATABASE");
					}
				}
			}

			recs.put("AUTHOR",authorList);
			recs.put("AFFILIATION",affiliationList);
			recs.put("CONTROLLEDTERM",controltermList);
			recs.put("PUBLISHERNAME",publishernameList);
			recs.put("SERIALTITLE",serialTitleList);
			recs.put("DATABASE",database);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return recs;
	}

	 protected Connection getConnection(String connectionURL,
	                                         String driver,
	                                         String username,
	                                         String password)
	            throws Exception
	 {
		 		System.out.println("connectionURL= "+connectionURL);
		 		System.out.println("driver= "+driver);
		 		System.out.println("username= "+username);
		 		System.out.println("password= "+password);

	            Class.forName(driver);
	            Connection con = DriverManager.getConnection(connectionURL,
	                                              username,
	                                              password);
	            return con;
     }

}