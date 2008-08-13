

import junit.framework.TestCase;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.data.compendex.runtime.CPXDatabase;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.ei.data.bd.runtime.*;
import java.sql.*;

public class BDDocBuilderUnitTest extends TestCase {

	private DatabaseConfig databaseConfig;
	private MultiDatabaseDocBuilder multidbDocBuilder;
	private List listOfDocIDs = null;
	private List fullDocPages = null;
	private List citationPages = null;
	private List xmlPages = null;
	private List risPages = null;
	private static final String COMPLETE_DOC = "<EI-DOCUMENT VIEW=\"detailed\"><FT  FTLINK=\"Y\"/><PO><![CDATA[20051201]]></PO><FLS label=\"Uncontrolled terms\"><FL><![CDATA[Bone-implant-interface]]></FL><FL><![CDATA[Critical load]]></FL><FL><![CDATA[Interface mechanisms]]></FL><FL><![CDATA[Shear strength, push-out test]]></FL></FLS><AN label=\"Accession number\"><![CDATA[06189858062]]></AN><TI label=\"Title\"><![CDATA[Extended push-out test to characterize the failure of bone-implant interface]]></TI><TT label=\"Title of translation\"><T><![CDATA[Erweiterter push out-test zur scha&die;digungscharakterisierung der implantat-knochen-grenzfla&die;che]]></T></TT><AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS><AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF><AF id=\"2\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></AF><AF id=\"3\"><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF></AFS><ST label=\"Serial title\"><![CDATA[Biomedizinische Technik]]></ST><SE label=\"Abbreviated serial title\"><![CDATA[Biomed. Tech.]]></SE><VOM label=\"Volume\"><![CDATA[50]]></VOM><IS label=\"Issue\"><![CDATA[6]]></IS><SD label=\"Issue date\"><![CDATA[June 2005]]></SD><YR label=\"Publication year\"><![CDATA[2005]]></YR><PP label=\"Pages\"><![CDATA[p 201 - 206]]></PP><LA label=\"Language\"><![CDATA[German]]></LA><SN label=\"ISSN\"><![CDATA[00135585]]></SN><CN label=\"CODEN\"><![CDATA[BMZTA]]></CN><DT label=\"Document type\"><![CDATA[Journal article (JA)]]></DT><PN label=\"Publisher\"><![CDATA[Fachverlag Schiele und Sohn GmbH]]></PN><AB label=\"Abstract\"><![CDATA[To study the mechanical behaviour of the implant-bone interface the push- or pull-out test was overtaken from material science. Most authors equate the maximum load (break point) with the failure of the implant integration. Extending the test procedure by acoustic emission analysis reveals the possibility to detect the failure of the interface more in detail and from its earliest beginning. The development of disconnection between host and implant was found to start long before the ultimate load is reached and can be monitored and quantified during this period. The active interface mechanisms are characterized by the distribution function of acoustic emissions and the number of hits per time defines the kinetics of the failure. From clinical studies a gradual subsidence of loaded implants is known starting long time before the definite implant failure. The presented extension of the push-out test with acoustic emission analysis allows the detection of a critical shear stress tc which demarks the onset of the gradual interface failure. We believe this value to represent the real critical load which should not be exceeded in the clinical application of intraosseous implants.]]></AB><NR label=\"Number of references\"><![CDATA[13]]></NR><MH label=\"Main heading\"><![CDATA[Implants (surgical)]]></MH><CVS label=\"Controlled terms\"><CV><![CDATA[Acoustic emission testing]]></CV><CV><![CDATA[Biomaterials]]></CV><CV><![CDATA[Bone]]></CV><CV><![CDATA[Interfaces (materials)]]></CV><CV><![CDATA[Materials science]]></CV><CV><![CDATA[Mechanical alloying]]></CV><CV><![CDATA[Shear strength]]></CV></CVS><FLS label=\"Uncontrolled terms\"><FL><![CDATA[Bone-implant-interface]]></FL><FL><![CDATA[Critical load]]></FL><FL><![CDATA[Interface mechanisms]]></FL><FL><![CDATA[Shear strength, push-out test]]></FL></FLS><CLS label=\"Classification code\"><CL><CID>461.2</CID><CTI><![CDATA[Biological Materials and Tissue Engineering]]></CTI></CL><CL><CID>462.4</CID><CTI><![CDATA[Prosthetics]]></CTI></CL><CL><CID>462.5</CID><CTI><![CDATA[Biomaterials (including synthetics)]]></CTI></CL><CL><CID>531</CID><CTI><![CDATA[Metallurgy and Metallography]]></CTI></CL><CL><CID>751.2</CID><CTI><![CDATA[Acoustic Properties of Materials]]></CTI></CL><CL><CID>931.2</CID><CTI><![CDATA[Physical Properties of Gases, Liquids and Solids]]></CTI></CL></CLS><TRS label=\"Treatment\"><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR></TRS><DO label=\"DOI\"><![CDATA[10.1016/j.frl.2005.09.001]]></DO><DOC><HITINDEX>0</HITINDEX><DOC-ID>cpx_18a992f10b61b5d4a9M74342061377553</DOC-ID><DB><ID>cpx</ID><DBMASK>1</DBMASK><DBNAME>Compendex</DBNAME><DBSNAME>Compendex</DBSNAME><DBINAME>cpx</DBINAME></DB></DOC><CPR label=\"Copyright\"><![CDATA[Copyright 2007 Elsevier B.V., All rights reserved.]]></CPR><CPRT><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></CPRT><PVD label=\"Provider\"><![CDATA[Ei]]></PVD></EI-DOCUMENT>";
	private static final String COMPLTE_CITATION = "<EI-DOCUMENT VIEW=\"citation\"><FT  FTLINK=\"Y\"/><DOC><HITINDEX>0</HITINDEX><DOC-ID>cpx_18a992f10b61b5d4a9M74342061377553</DOC-ID><DB><ID>cpx</ID><DBMASK>1</DBMASK><DBNAME>Compendex</DBNAME><DBSNAME>Compendex</DBSNAME><DBINAME>cpx</DBINAME></DB></DOC><TI><![CDATA[Extended push-out test to characterize the failure of bone-implant interface]]></TI><TT><T><![CDATA[Erweiterter push out-test zur scha&die;digungscharakterisierung der implantat-knochen-grenzfla&die;che]]></T></TT><AUS><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS><AFS><AF id=\"1\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF><AF id=\"2\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></AF><AF id=\"3\"><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF></AFS><SO><![CDATA[Biomedizinische Technik]]></SO><PP><![CDATA[p 201 - 206]]></PP><VO><![CDATA[v 50, n 6]]></VO><YR><![CDATA[2005]]></YR><PN><![CDATA[Fachverlag Schiele und Sohn GmbH]]></PN><SD><![CDATA[June 2005]]></SD><SN><![CDATA[00135585]]></SN><LA><![CDATA[German]]></LA><CPR><![CDATA[Copyright 2007 Elsevier B.V., All rights reserved.]]></CPR><CPRT><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></CPRT><DO><![CDATA[10.1016/j.frl.2005.09.001]]></DO></EI-DOCUMENT>";
	private static final String COMPLTE_XML_CITATION = "<EI-DOCUMENT VIEW=\"xml_citation\"><FT  FTLINK=\"Y\"/><SN><![CDATA[00135585]]></SN><YR><![CDATA[2005]]></YR><AUS><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS><DOC><HITINDEX>0</HITINDEX><DOC-ID>cpx_18a992f10b61b5d4a9M74342061377553</DOC-ID><DB><ID>cpx</ID><DBMASK>1</DBMASK><DBNAME>Compendex</DBNAME><DBSNAME>Compendex</DBSNAME><DBINAME>cpx</DBINAME></DB></DOC><SO><![CDATA[Biomedizinische Technik]]></SO><PN><![CDATA[Fachverlag Schiele und Sohn GmbH]]></PN><VOM><![CDATA[50]]></VOM><AFS><AF id=\"1\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF><AF id=\"2\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></AF><AF id=\"3\"><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF></AFS><PVD><![CDATA[Ei]]></PVD><SD><![CDATA[June 2005]]></SD><CPRT><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></CPRT><DO><![CDATA[10.1016/j.frl.2005.09.001]]></DO><TI><![CDATA[Extended push-out test to characterize the failure of bone-implant interface]]></TI><LA><![CDATA[German]]></LA><PP><![CDATA[p 201 - 206]]></PP><CPR><![CDATA[Copyright 2007 Elsevier B.V., All rights reserved.]]></CPR><IS><![CDATA[6]]></IS><AN><![CDATA[06189858062]]></AN><CVS><CV><![CDATA[Acoustic emission testing]]></CV><CV><![CDATA[Biomaterials]]></CV><CV><![CDATA[Bone]]></CV><CV><![CDATA[Interfaces (materials)]]></CV><CV><![CDATA[Materials science]]></CV><CV><![CDATA[Mechanical alloying]]></CV><CV><![CDATA[Shear strength]]></CV></CVS></EI-DOCUMENT>";
	private static final String COMPLTE_RIS = "<EI-DOCUMENT VIEW=\"ris\"><FT  FTLINK=\"N\"/><TY><![CDATA[JOUR]]></TY><LA><![CDATA[German]]></LA><N1><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></N1><TI><![CDATA[Erweiterter push out-test zur schadigungscharakterisierung der implantat-knochen-grenzflache]]></TI><T1><![CDATA[Extended push-out test to characterize the failure of bone-implant interface]]></T1><JO><![CDATA[Biomedizinische Technik]]></JO><AUS><AU><![CDATA[Brandt, Jorg]]></AU><AU><![CDATA[Bierogel, C.]]></AU><AU><![CDATA[Holweg, K.]]></AU><AU><![CDATA[Hein, W.]]></AU><AU><![CDATA[Grellmann, W.]]></AU></AUS><AD><A><![CDATA[Universitatsklinik und Poliklinik fur Orthopadie und Physikalische Medizin, D-06112 Halle, Germany]]></A></AD><VL><![CDATA[ 50]]></VL><IS><![CDATA[ 6]]></IS><PY><![CDATA[2005]]></PY><AN><![CDATA[06189858062]]></AN><SP><![CDATA[201]]></SP><EP><![CDATA[206]]></EP><SN><![CDATA[0013-5585]]></SN><PB><![CDATA[Fachverlag Schiele und Sohn GmbH, Berlin, D-10969, Germany]]></PB><N2><![CDATA[To study the mechanical behaviour of the implant-bone interface the push- or pull-out test was overtaken from material science. Most authors equate the maximum load (break point) with the failure of the implant integration. Extending the test procedure by acoustic emission analysis reveals the possibility to detect the failure of the interface more in detail and from its earliest beginning. The development of disconnection between host and implant was found to start long before the ultimate load is reached and can be monitored and quantified during this period. The active interface mechanisms are characterized by the distribution function of acoustic emissions and the number of hits per time defines the kinetics of the failure. From clinical studies a gradual subsidence of loaded implants is known starting long time before the definite implant failure. The presented extension of the push-out test with acoustic emission analysis allows the detection of a critical shear stress tc which demarks the onset of the gradual interface failure. We believe this value to represent the real critical load which should not be exceeded in the clinical application of intraosseous implants.]]></N2><KW><![CDATA[Implants (surgical)]]></KW><CVS><CV><![CDATA[Bone]]></CV><CV><![CDATA[Interfaces (materials)]]></CV><CV><![CDATA[Biomaterials]]></CV><CV><![CDATA[Acoustic emission testing]]></CV><CV><![CDATA[Shear strength]]></CV><CV><![CDATA[Mechanical alloying]]></CV><CV><![CDATA[Materials science]]></CV></CVS><FLS><FL><![CDATA[Bone-implant-interface]]></FL><FL><![CDATA[Shear strength, push-out test]]></FL><FL><![CDATA[Critical load]]></FL><FL><![CDATA[Interface mechanisms]]></FL></FLS></EI-DOCUMENT>";
	ConnectionBroker broker = null;

	protected void setUp() {

		try
		{
			broker = ConnectionBroker.getInstance("C:/baja/appserver.4/webapps/engvillage/WEB-INF/pools.xml");
			DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			multidbDocBuilder = new MultiDatabaseDocBuilder();
			listOfDocIDs = new ArrayList();
			listOfDocIDs.add(new DocID("cpx_18a992f10b61b5d4a9M74342061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add( new DocID("cpx_18a992f10b61b5d4a9M74252061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("geo_152513a113d01a997cM73da2061377553", databaseConfig.getDatabase("geo")));
			listOfDocIDs.add(new DocID("cpx_18a992f10c593a6af2M7f882061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("cpx_18a992f10b61b5d4a9M743d2061377553", databaseConfig.getDatabase("cpx")));

			fullDocPages = multidbDocBuilder.buildPage(listOfDocIDs, FullDoc.FULLDOC_FORMAT);
			citationPages = multidbDocBuilder.buildPage(listOfDocIDs, Citation.CITATION_FORMAT);
			xmlPages = multidbDocBuilder.buildPage(listOfDocIDs, Citation.XMLCITATION_FORMAT);
			risPages = multidbDocBuilder.buildPage(listOfDocIDs, RIS.RIS_FORMAT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void tearDown() {

		try
		{
			broker.closeConnections();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void testBuildPage() throws Exception
	{

		try
		{
			//full doc view tests

			assertDocID(fullDocPages);
			assertAccessionNumber(fullDocPages);
			assertVolume(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertDOI(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertPubYear(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertIssue(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertCopyRight(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertCopyRightText(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertIssueDate(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertCoden(fullDocPages);
			assertConfCode(fullDocPages);
			assertNumOfRefs(fullDocPages);
			assertSerialTitle(fullDocPages);
			assertAbbrevSerialTitle(fullDocPages);
			assertISSN(fullDocPages);
			assertIssueTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertMainHeading(fullDocPages);
			assertControlledTerms(fullDocPages);
			assertUnControlledTerms(fullDocPages);
			assertTreatments(fullDocPages);
			assertTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertTranslatedTitle(fullDocPages);
			assertISBN(fullDocPages);
			assertPageRange(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertAbstract(fullDocPages);
			assertVolumeTitle(fullDocPages);
			assertPublisher(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertDocType(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertConfName(fullDocPages);
			assertAuthors(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertAuthorAffiliations(fullDocPages);
			assertConfLocation(fullDocPages);
			assertSponsor(fullDocPages);
			assertRegionalTerms(fullDocPages);
			assertSpeciesTerms(fullDocPages);
			assertLanguage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfDate(fullDocPages);
			assertStartPage(fullDocPages);
			assertEndPage(fullDocPages);
			assertClassCode(fullDocPages);
			assertProvider(fullDocPages);
			assertAbstractType(fullDocPages);
			assertPubOrder(fullDocPages);
			assertFullDoc(fullDocPages);

			//citation view tests
			//assertDocID(citationPages);
			assertDOI(citationPages, Citation.CITATION_FORMAT);
			assertPubYear(citationPages, Citation.CITATION_FORMAT);
			assertCopyRight(citationPages, Citation.CITATION_FORMAT);
			assertCopyRightText(citationPages,Citation.CITATION_FORMAT);
			assertIssueDate(citationPages, Citation.CITATION_FORMAT);
			assertIssueTitle(citationPages, Citation.CITATION_FORMAT);
			assertVolIssue(citationPages);
			assertSource(citationPages);
			assertTitle(citationPages, Citation.CITATION_FORMAT);
			assertPageRange(citationPages, Citation.CITATION_FORMAT);
			assertPublisher(citationPages,Citation.CITATION_FORMAT);
			assertAuthors(citationPages, Citation.CITATION_FORMAT);
			assertLanguage(citationPages, Citation.CITATION_FORMAT);
			assertFullCitation(citationPages);

			// xml view test
			assertFullXmlCitation(xmlPages);

			// ris view test

			assertDocType(risPages,RIS.RIS_FORMAT);
			assertLanguage(risPages, RIS.RIS_FORMAT);
			assertCopyRightText(risPages, RIS.RIS_FORMAT);
			assertVolume(risPages, RIS.RIS_FORMAT);
			assertIssue(risPages,RIS.RIS_FORMAT);
		}
		finally
		{
			broker.closeConnections();
		}

	}

	protected void assertFullDoc(List EIDocs) throws Exception
	{
		StringWriter swriter = new StringWriter();
		PrintWriter out = new PrintWriter ( swriter );
		EIDoc eidoc = (EIDoc)EIDocs.get(0);
		eidoc.toXML(out);
		out.close();
		String xmlString = swriter.toString();
		assertTrue(xmlString.equals(COMPLETE_DOC));
	}

	protected void assertFullCitation(List EIDocs) throws Exception
	{
		StringWriter swriter = new StringWriter();
		PrintWriter out = new PrintWriter ( swriter );
		EIDoc eidoc = (EIDoc)EIDocs.get(0);
		eidoc.toXML(out);
		out.close();
		String xmlString = swriter.toString();
		assertTrue(xmlString.equals(COMPLTE_CITATION));
	}

	protected void assertFullXmlCitation(List EIDocs) throws Exception
	{
		StringWriter swriter = new StringWriter();
		PrintWriter out = new PrintWriter ( swriter ) ;
		EIDoc eidoc = (EIDoc)EIDocs.get(0);
		eidoc.toXML(out);
		out.close();
		String xmlString = swriter.toString();
		assertTrue(xmlString.equals(COMPLTE_XML_CITATION));
	}

	protected void assertDocID(List EIDocs) throws Exception
	{
		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			correctString = "<DOC-ID>" + correctDocId.getDocID() + "</DOC-ID>";
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertAccessionNumber(List EIDocs) throws Exception
	{
		HashMap aNumbers = new HashMap();
		aNumbers.put("cpx_18a992f10b61b5d4a9M74342061377553", "06189858062");
		aNumbers.put("cpx_18a992f10b61b5d4a9M74252061377553", "2006189858079");
		aNumbers.put("geo_152513a113d01a997cM73da2061377553", "3030204");
		aNumbers.put("cpx_18a992f10c593a6af2M7f882061377553", "06269957701");
		aNumbers.put("cpx_18a992f10b61b5d4a9M743d2061377553", "06189858053");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			correctString = "<AN label=\"Accession number\"><![CDATA[" +(String)aNumbers.get(correctDocId.getDocID()) + "]]></AN>";
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertVolume(List EIDocs, String dataFormat) throws Exception
	{
		HashMap vols = new HashMap();
		vols.put("cpx_18a992f10b61b5d4a9M74342061377553", "50");
		vols.put("cpx_18a992f10b61b5d4a9M74252061377553", "6113");
		vols.put("geo_152513a113d01a997cM73da2061377553", "36");
		vols.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		vols.put("cpx_18a992f10b61b5d4a9M743d2061377553", "2006");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(dataFormat.equals(RIS.RIS_FORMAT))
			{
				correctString = "<VL><![CDATA[" +(String)vols.get(correctDocId.getDocID()) +"]]></VL>";
			}
			else
			{
				correctString = "<VOM label=\"Volume\"><![CDATA[" +(String)vols.get(correctDocId.getDocID()) + "]]></VOM>";
			}

			if(vols.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertDOI(List EIDocs, String dataFormat) throws Exception
	{
		HashMap dois = new HashMap();
		dois.put("cpx_18a992f10b61b5d4a9M74342061377553","10.1016/j.frl.2005.09.001");
		dois.put("cpx_18a992f10b61b5d4a9M74252061377553","10.1117/12.657463");
		dois.put("geo_152513a113d01a997cM73da2061377553","10.1080/03009480600991573");
		dois.put("cpx_18a992f10c593a6af2M7f882061377553",null);
		dois.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(!dataFormat.equals(Citation.CITATION_FORMAT))
			{
				correctString = "<DO label=\"DOI\"><![CDATA[" +(String)dois.get(correctDocId.getDocID()) + "]]></DO>";
			}
			else
			{

				correctString = "<DO><![CDATA[" +(String)dois.get(correctDocId.getDocID()) + "]]></DO>";
			}

			if(dois.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertPubYear(List EIDocs, String dataFormat) throws Exception
	{
		HashMap pyears = new HashMap();
		pyears.put("cpx_18a992f10b61b5d4a9M74342061377553", "2005");
		pyears.put("cpx_18a992f10b61b5d4a9M74252061377553", "2006");
		pyears.put("geo_152513a113d01a997cM73da2061377553", "2007");
		pyears.put("cpx_18a992f10c593a6af2M7f882061377553", "2005");
		pyears.put("cpx_18a992f10b61b5d4a9M743d2061377553", "2006");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(!dataFormat.equals(Citation.CITATION_FORMAT))
			{
				correctString = "<YR label=\"Publication year\"><![CDATA[" + (String)pyears.get(correctDocId.getDocID()) + "]]></YR>";
			}
			else
			{
				correctString = "<YR><![CDATA[" + (String)pyears.get(correctDocId.getDocID()) + "]]></YR>";
			}

			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertIssue(List EIDocs, String dataFormat) throws Exception
	{
		HashMap iss = new HashMap();
		iss.put("cpx_18a992f10b61b5d4a9M74342061377553", "6");
		iss.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		iss.put("geo_152513a113d01a997cM73da2061377553", "2");
		iss.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		iss.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(iss.get(correctDocId.getDocID()) != null)
			{
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<IS><![CDATA["+ (String)iss.get(correctDocId.getDocID())+"]]></IS>";
				}
				else
				{
					correctString = "<IS label=\"Issue\"><![CDATA[" + (String)iss.get(correctDocId.getDocID()) + "]]></IS>";
				}
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertCopyRight(List EIDocs, String dataFormat) throws Exception
	{
		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();

			if(dataFormat.equals(RIS.RIS_FORMAT))
			{
				correctString = "<N1><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></N1>";
			}
			else if(!dataFormat.equals(Citation.CITATION_FORMAT))
			{
				correctString = "<CPR label=\"Copyright\"><![CDATA[Copyright 2007 Elsevier B.V., All rights reserved.]]></CPR>";
			}
			else
			{

				correctString = "<CPR><![CDATA[Copyright 2007 Elsevier B.V., All rights reserved.]]></CPR>";
			}

			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertCopyRightText(List EIDocs, String dataFormat) throws Exception
	{
		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			if(dataFormat.equals(RIS.RIS_FORMAT))
			{
				correctString = "<N1><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></N1>";
			}
			else
			{
				correctString = "<CPRT><![CDATA[Compilation and indexing terms, Copyright 2008 Elsevier Inc.]]></CPRT>";
			}
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertIssueDate(List EIDocs, String dataFormat) throws Exception
	{
		HashMap issDates = new HashMap();
		issDates.put("cpx_18a992f10b61b5d4a9M74342061377553", "June 2005");
		issDates.put("cpx_18a992f10b61b5d4a9M74252061377553", "2006");
		issDates.put("geo_152513a113d01a997cM73da2061377553", "2007");
		issDates.put("cpx_18a992f10c593a6af2M7f882061377553", "2005");
		issDates.put("cpx_18a992f10b61b5d4a9M743d2061377553", "2006");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(!dataFormat.equals(Citation.CITATION_FORMAT))
			{
				correctString = "<SD label=\"Issue date\"><![CDATA[" + (String)issDates.get(correctDocId.getDocID()) + "]]></SD>";
			}
			else
			{
				correctString = "<SD><![CDATA[" + (String)issDates.get(correctDocId.getDocID()) + "]]></SD>";
			}

			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertCoden(List EIDocs) throws Exception
	{
		HashMap codens = new HashMap();
		codens.put("cpx_18a992f10b61b5d4a9M74342061377553", "BMZTA");
		codens.put("cpx_18a992f10b61b5d4a9M74252061377553", "PSISD");
		codens.put("geo_152513a113d01a997cM73da2061377553", null);
		codens.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		codens.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(codens.get(correctDocId.getDocID()) != null)
			{
				correctString = "<CN label=\"CODEN\"><![CDATA[" + (String)codens.get(correctDocId.getDocID()) + "]]></CN>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertConfCode(List EIDocs) throws Exception
	{
		HashMap confCodes = new HashMap();
		confCodes.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		confCodes.put("cpx_18a992f10b61b5d4a9M74252061377553", "67156");
		confCodes.put("geo_152513a113d01a997cM73da2061377553", null);
		confCodes.put("cpx_18a992f10c593a6af2M7f882061377553", "67492");
		confCodes.put("cpx_18a992f10b61b5d4a9M743d2061377553", "67098");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();


			if(confCodes.get(correctDocId.getDocID()) != null)
			{
				correctString = "<CC label=\"Conference code\"><![CDATA[" + (String)confCodes.get(correctDocId.getDocID()) + "]]></CC>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertNumOfRefs(List EIDocs) throws Exception
	{
		HashMap numRefs = new HashMap();
		numRefs.put("cpx_18a992f10b61b5d4a9M74342061377553", "13");
		numRefs.put("cpx_18a992f10b61b5d4a9M74252061377553", "26");
		numRefs.put("geo_152513a113d01a997cM73da2061377553", "70");
		numRefs.put("cpx_18a992f10c593a6af2M7f882061377553", "18");
		numRefs.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(numRefs.get(correctDocId.getDocID()) != null)
			{
				correctString = "<NR label=\"Number of references\"><![CDATA[" + (String)numRefs.get(correctDocId.getDocID()) + "]]></NR>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertSerialTitle(List EIDocs) throws Exception
	{
		HashMap stitles = new HashMap();
		stitles.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomedizinische Technik");
		stitles.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proceedings of SPIE - The International Society for Optical Engineering");
		stitles.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		stitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proceedings of the Twelfth International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors");
		stitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium Technology");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(stitles.get(correctDocId.getDocID()) != null)
			{
				correctString = "<ST label=\"Serial title\"><![CDATA[" + (String)stitles.get(correctDocId.getDocID()) + "]]></ST>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertAbbrevSerialTitle(List EIDocs) throws Exception
	{
		HashMap stitles = new HashMap();
		stitles.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomed. Tech.");
		stitles.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proc SPIE Int Soc Opt Eng");
		stitles.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		stitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proc. Twelfth Int. Conf. Environ. Degrad. Mater. Nucl. Power Syst. Water React.");
		stitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnes. Technol.");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(stitles.get(correctDocId.getDocID()) != null)
			{
				correctString = "<SE label=\"Abbreviated serial title\"><![CDATA[" + (String)stitles.get(correctDocId.getDocID()) + "]]></SE>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertISSN(List EIDocs) throws Exception
	{
		HashMap issns = new HashMap();
		issns.put("cpx_18a992f10b61b5d4a9M74342061377553", "00135585");
		issns.put("cpx_18a992f10b61b5d4a9M74252061377553", "0277786X");
		issns.put("geo_152513a113d01a997cM73da2061377553", "03009483");
		issns.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		issns.put("cpx_18a992f10b61b5d4a9M743d2061377553", "15454150");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(issns.get(correctDocId.getDocID()) != null)
			{
				correctString = "<SN label=\"ISSN\"><![CDATA[" + (String)issns.get(correctDocId.getDocID()) + "]]></SN>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertIssueTitle(List EIDocs, String dataFormat) throws Exception
	{
		HashMap issueTitles = new HashMap();
		issueTitles.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		issueTitles.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		issueTitles.put("geo_152513a113d01a997cM73da2061377553", null);
		issueTitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proceedings of the Twelfth International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors");
		issueTitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(issueTitles.get(correctDocId.getDocID()) != null)
			{
				if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = "<MT label=\"Monograph title\"><![CDATA[" + (String)issueTitles.get(correctDocId.getDocID()) + "]]></MT>";
				}
				else
				{
					correctString = "<MT><![CDATA[" + (String)issueTitles.get(correctDocId.getDocID()) + "]]></MT>";
				}

				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertVolIssue(List EIDocs) throws Exception
	{
		HashMap volIssMap = new HashMap();
		volIssMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "v 50, n 6");
		volIssMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		volIssMap.put("geo_152513a113d01a997cM73da2061377553", "v 36, n 2");
		volIssMap.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		volIssMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "v 2006");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			DocID correctDocId = (DocID)eidoc.getDocID();

			ElementDataMap elementDataMap = eidoc.getElementDataMap();

			if(volIssMap.get(correctDocId.getDocID()) != null)
			{
				ElementData elementData = elementDataMap.get(Keys.VOLISSUE);
				String[] data = elementData.getElementData();
				correctString = (String)volIssMap.get(correctDocId.getDocID());
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertSource(List EIDocs) throws Exception
	{
		HashMap sourceMap = new HashMap();
		sourceMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomedizinische Technik");
		sourceMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proceedings of SPIE - The International Society for Optical Engineering");
		sourceMap.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		sourceMap.put("cpx_18a992f10c593a6af2M7f882061377553", "Proceedings of the Twelfth International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors");
		sourceMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium Technology");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			DocID correctDocId = (DocID)eidoc.getDocID();

			ElementDataMap elementDataMap = eidoc.getElementDataMap();

			if(sourceMap.get(correctDocId.getDocID()) != null)
			{
				ElementData elementData = elementDataMap.get(Keys.SOURCE);
				String[] data = elementData.getElementData();
				correctString = (String)sourceMap.get(correctDocId.getDocID());
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertMainHeading(List EIDocs) throws Exception
	{
		HashMap mainheadingMap = new HashMap();
		mainheadingMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "Implants (surgical)");
		mainheadingMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "Demodulation");
		mainheadingMap.put("geo_152513a113d01a997cM73da2061377553", null);
		mainheadingMap.put("cpx_18a992f10c593a6af2M7f882061377553", "Boiling water reactors");
		mainheadingMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium alloys");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(mainheadingMap.get(correctDocId.getDocID()) != null)
			{
				correctString = "<MH label=\"Main heading\"><![CDATA[" + (String)mainheadingMap.get(correctDocId.getDocID()) + "]]></MH>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertControlledTerms(List EIDocs) throws Exception
	{
		HashMap ctermsMap = new HashMap();
		ctermsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<CVS label=\"Controlled terms\"><CV><![CDATA[Acoustic emission testing]]></CV><CV><![CDATA[Biomaterials]]></CV><CV><![CDATA[Bone]]></CV><CV><![CDATA[Interfaces (materials)]]></CV><CV><![CDATA[Materials science]]></CV><CV><![CDATA[Mechanical alloying]]></CV><CV><![CDATA[Shear strength]]></CV></CVS>");
		ctermsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<CVS label=\"Controlled terms\"><CV><![CDATA[Adaptive optics]]></CV><CV><![CDATA[Mirrors]]></CV><CV><![CDATA[Optical design]]></CV><CV><![CDATA[Planets]]></CV><CV><![CDATA[Spectroscopic analysis]]></CV></CVS>");
		ctermsMap.put("geo_152513a113d01a997cM73da2061377553", "<CVS label=\"Controlled terms\"><CV><![CDATA[charcoal]]></CV><CV><![CDATA[deforestation]]></CV><CV><![CDATA[environmental change]]></CV><CV><![CDATA[Holocene]]></CV><CV><![CDATA[human activity]]></CV><CV><![CDATA[palynology]]></CV><CV><![CDATA[radiocarbon dating]]></CV><CV><![CDATA[spatiotemporal analysis]]></CV><CV><![CDATA[vegetation history]]></CV></CVS>");
		ctermsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<CVS label=\"Controlled terms\"><CV><![CDATA[Codes (symbols)]]></CV><CV><![CDATA[Computer simulation]]></CV><CV><![CDATA[Coolants]]></CV><CV><![CDATA[Electrochemical corrosion]]></CV><CV><![CDATA[Electrochemistry]]></CV><CV><![CDATA[Radiolysis]]></CV><CV><![CDATA[Stress corrosion cracking]]></CV></CVS>");
		ctermsMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<CVS label=\"Controlled terms\"><CV><![CDATA[Computer simulation]]></CV><CV><![CDATA[Die casting]]></CV><CV><![CDATA[Light metals]]></CV><CV><![CDATA[Porosity]]></CV><CV><![CDATA[Shrinkage]]></CV></CVS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(ctermsMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)ctermsMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertUnControlledTerms(List EIDocs) throws Exception
	{
		HashMap uctermsMap = new HashMap();
		uctermsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<FLS label=\"Uncontrolled terms\"><FL><![CDATA[Bone-implant-interface]]></FL><FL><![CDATA[Critical load]]></FL><FL><![CDATA[Interface mechanisms]]></FL><FL><![CDATA[Shear strength, push-out test]]></FL></FLS>");
		uctermsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<FLS label=\"Uncontrolled terms\"><FL><![CDATA[Coronagraphs]]></FL><FL><![CDATA[Deformable mirror]]></FL><FL><![CDATA[Extrasolar planets]]></FL><FL><![CDATA[Gemini Planet Imager]]></FL><FL><![CDATA[MEMS]]></FL></FLS>");
		uctermsMap.put("geo_152513a113d01a997cM73da2061377553", null);
		uctermsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<FLS label=\"Uncontrolled terms\"><FL><![CDATA[Accumulated damage]]></FL><FL><![CDATA[Electrochemical corrosion potential]]></FL><FL><![CDATA[IGSCC]]></FL><FL><![CDATA[Intergranular stress corrosion cracking (IGSCC)]]></FL></FLS>");
		uctermsMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<FLS label=\"Uncontrolled terms\"><FL><![CDATA[Atmospheric environments]]></FL><FL><![CDATA[Magnesium casting]]></FL><FL><![CDATA[Rheocast magnesium alloy]]></FL><FL><![CDATA[Shrinkage porosity]]></FL></FLS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(uctermsMap.get(correctDocId.getDocID()) != null)
			{
				correctString =  (String)uctermsMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertTreatments(List EIDocs) throws Exception
	{
		HashMap treatmentsMap = new HashMap();
		treatmentsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR></TRS>");
		treatmentsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR></TRS>");
		treatmentsMap.put("geo_152513a113d01a997cM73da2061377553", null);
		treatmentsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR><TR><TCO><![CDATA[X]]></TCO><TTI><![CDATA[Experimental (EXP)]]></TTI></TR></TRS>");
		treatmentsMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR><TR><TCO><![CDATA[X]]></TCO><TTI><![CDATA[Experimental (EXP)]]></TTI></TR></TRS><DOC>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(treatmentsMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)treatmentsMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertTitle(List EIDocs, String dataFormat) throws Exception
	{
		HashMap titleMap = new HashMap();
		titleMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "Extended push-out test to characterize the failure of bone-implant interface");
		titleMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "MEMS-based extreme adaptive optics for planet detection");
		titleMap.put("geo_152513a113d01a997cM73da2061377553", "The Holocene vegetation history of the Arfon Platform, North Wales,UK");
		titleMap.put("cpx_18a992f10c593a6af2M7f882061377553", "The electrochemistry of boiling water reactors");
		titleMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium Technology 2006 - Proceedings of Symposium Sponsored by the Magnesium Committee of the Light Metals Division of TMS");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(titleMap.get(correctDocId.getDocID()) != null)
			{
				if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = "<TI label=\"Title\"><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></TI>";
				}
				else
				{
					correctString = "<TI><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></TI>";
				}

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertTranslatedTitle(List EIDocs) throws Exception
	{
		HashMap titleMap = new HashMap();
		titleMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "Erweiterter push out-test zur scha&die;digungscharakterisierung der implantat-knochen-grenzfla&die;che");
		titleMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		titleMap.put("geo_152513a113d01a997cM73da2061377553", null);
		titleMap.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		titleMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(titleMap.get(correctDocId.getDocID()) != null)
			{
				correctString = "<TT label=\"Title of translation\"><T><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></T></TT>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertISBN(List EIDocs) throws Exception
	{
		HashMap isbnMap = new HashMap();
		isbnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		isbnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<BN label=\"ISBN-10\"><![CDATA[0819461555]]></BN><BN13 label=\"ISBN-13\"><![CDATA[9780819461551]]></BN13>");
		isbnMap.put("geo_152513a113d01a997cM73da2061377553", null);
		isbnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<BN13 label=\"ISBN-13\"><![CDATA[9780873395953]]></BN13>");
		isbnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<BN label=\"ISBN-10\"><![CDATA[0873396200]]></BN><BN13 label=\"ISBN-13\"><![CDATA[9780873396202]]></BN13>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(isbnMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)isbnMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertPageRange(List EIDocs, String dataFormat) throws Exception
	{
		HashMap prMap = new HashMap();
		prMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "p 201 - 206");
		prMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "p 611308");
		prMap.put("geo_152513a113d01a997cM73da2061377553", "p 170 - 181");
		prMap.put("cpx_18a992f10c593a6af2M7f882061377553", "p 125 - 133");
		prMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "551");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(prMap.get(correctDocId.getDocID()) != null)
			{
				if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = "<PP label=\"Pages\"><![CDATA[" + (String)prMap.get(correctDocId.getDocID()) + "]]></PP>";
				}
				else
				{
					correctString = "<PP><![CDATA[" + (String)prMap.get(correctDocId.getDocID()) + "]]></PP>";
				}

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertAbstract(List EIDocs) throws Exception
	{
		HashMap absMap = new HashMap();
		absMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AB label=\"Abstract\"><![CDATA[To study the mechanical behaviour of the implant-bone interface the push- or pull-out test was overtaken from material science. Most authors equate the maximum load (break point) with the failure of the implant integration. Extending the test procedure by acoustic emission analysis reveals the possibility to detect the failure of the interface more in detail and from its earliest beginning. The development of disconnection between host and implant was found to start long before the ultimate load is reached and can be monitored and quantified during this period. The active interface mechanisms are characterized by the distribution function of acoustic emissions and the number of hits per time defines the kinetics of the failure. From clinical studies a gradual subsidence of loaded implants is known starting long time before the definite implant failure. The presented extension of the push-out test with acoustic emission analysis allows the detection of a critical shear stress tc which demarks the onset of the gradual interface failure. We believe this value to represent the real critical load which should not be exceeded in the clinical application of intraosseous implants.]]></AB>");
		absMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AB label=\"Abstract\"><![CDATA[The next major step in the study of extrasolar planets will be the direct detection, resolved from their parent star, of a significant sample of Jupiter-like extrasolar giant planets. Such detection will open up new parts of the extrasolar planet distribution and allow spectroscopic characterization of the planets themselves. Detecting Jovian planets at 5-50 AU scale orbiting nearby stars requires adaptive optics systems and coronagraphs an order of magnitude more powerful than those available today - the realm of \"Extreme\" adaptive optics. We present the basic requirements and design for such a system, the Gemini Planet Imager (GPI.) GPI will require a MEMS-based deformable mirror with good surface quality, 2-4 micron stroke (operated in tandem with a conventional low-order \"woofer\" mirror), and a fully-functional 48-actuator-diameter aperture. Adaptive optics, extrasolar planets, speckle, coronagraphs.]]></AB>");
		absMap.put("geo_152513a113d01a997cM73da2061377553", "<AB label=\"Abstract\"><![CDATA[Detailed pollen, charcoal and loss on ignition profiles were analysed from Llyn Cororion, North Wales, UK. The chronology was based on 11 radiocarbon dates. This site is particularly important for this region because its high-resolution record improves the spatial and temporal resolution of records of Holocene vegetation change in an area characterized by a highly variable environment. An early Holocene phase of Juniperus-Betula scrub was succeeded by Betula-Corylus woodland. Quercus and Ulmus were established by c. 8600 <sup>14</sup>C yr BP, with Pinus dominating at c. 8430 <sup>14</sup>C yr BP. Local disturbance then allowed the spread of Alnus; Tilia was a common component of the forest by 5650 <sup>14</sup>C yr BP. Charcoal and pollen records suggest that by c. 2600 <sup>14</sup>C yr BP there was progressive deforestation, increased use of fire and spread of grassland; the first cereal grain was recorded at c. 2900 <sup>14</sup>C yr BP. Compared with data from upland Snowdonia, the results show that within a topographically diverse region there were significant local variations in forest composition. These variations developed as a response to interactions between many environmental parameters and were further complicated by the influence of human activity. In an area such as North Wales it is therefore unlikely that one site can be representative of regional Holocene vegetational development. The site is additionally important because it contributes to the data available for meta-analyses of environmental change in the North Atlantic region, particularly as detailed pollen diagrams from coastal lake sites around estern Europe are rare.]]></AB>");
		absMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AB label=\"Abstract\"><![CDATA[Computer codes developed by this group over the past decade for modeling water chemistry and estimating the accumulated damage from stress corrosion cracking in boiling water reactors (BWRs), including DAMAGE_PREDICTOR, REMAIN, and ALERT, have now been superceded by a new code, FOCUS. This new code predicts water chemistry (radiolysis), electrochemical corrosion potential, crack velocity, and accumulated damage (crack depth) in BWR primary coolant circuits at many points simultaneously under normal water chemistry (NWC) and hydrogen water chemistry (HWC) operating protocols over specified operating histories. FOCUS includes the Advanced Coupled Environment Fracture Model (ACEFM) for estimating crack growth rate over a wide temperature range and hence is particularly useful for modeling BWRs that are subject to frequent start ups and shut downs. Additionally, a more robust and flexible water chemistry code is incorporated into FOCUS that allows for more accurate simulation of changes in coolant conductivity under upset conditions. The application of FOCUS for modeling the chemistry, electrochemistry, and the accumulation of intergranular stress corrosion cracking (IGSCC) damage in BWR primary coolant circuits is illustrated in this paper.]]></AB>");
		absMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<AB label=\"Abstract\"><![CDATA[The proceedings contain 87 papers. The topics discussed include: simulation of atmospheric environments for storage and transport of magnesium and its alloys; the physical chemistry of the carbothermic route to magnesium; thermal de-coating of magnesium - a first step towards recycling of coated magnesium; phenomena of formation of gas induced shrinkage porosity in pressure die-cast Mg-alloys; the road to 2020: overview of the magnesium casting industry technology roadmap; heat treatment and mechanical properties of a rheocast magnesium alloy; microstructural refinement of magnesium alloy by electromagnetic vibrations; and fabrication of carbon long fiber reinforced magnesium parts in high pressure die casting.]]></AB>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(absMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)absMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertVolumeTitle(List EIDocs) throws Exception
	{
		HashMap vtMap = new HashMap();
		vtMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		vtMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<VT label=\"Volume title\"><![CDATA[MEMS/MOEMS Components and Their Applications III]]></VT>");
		vtMap.put("geo_152513a113d01a997cM73da2061377553", null);
		vtMap.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		vtMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<VT label=\"Volume title\"><![CDATA[Magnesium Technology 2006  - Proceedings of Symposium Sponsored by the Magnesium Committee of the Light Metals Division of TMS]]></VT>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(vtMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)vtMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertPublisher(List EIDocs, String dataFormat) throws Exception
	{
		HashMap pnMap = new HashMap();
		pnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		pnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "SPIE");
		pnMap.put("geo_152513a113d01a997cM73da2061377553", null);
		pnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "Minerals, Metals and Materials Society");
		pnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Minerals, Metals and Materials Society");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(pnMap.get(correctDocId.getDocID()) != null)
			{
				if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = "<PN label=\"Publisher\"><![CDATA[" + (String)pnMap.get(correctDocId.getDocID()) + "]]></PN>";
				}
				else
				{
					correctString = "<PN><![CDATA[" + (String)pnMap.get(correctDocId.getDocID()) + "]]></PN>";
				}

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertPaperNumber(List EIDocs) throws Exception
	{
		HashMap pnMap = new HashMap();
		pnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		pnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		pnMap.put("geo_152513a113d01a997cM73da2061377553", null);
		pnMap.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		pnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(pnMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)pnMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertDocType(List EIDocs,String dataFormat) throws Exception
	{
		HashMap dtMap = new HashMap();
		if(!dataFormat.equals(RIS.RIS_FORMAT))
		{
			dtMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<DT label=\"Document type\"><![CDATA[Journal article (JA)]]></DT>");
			dtMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<DT label=\"Document type\"><![CDATA[Conference article (CA)]]></DT>");
			dtMap.put("geo_152513a113d01a997cM73da2061377553", "<DT label=\"Document type\"><![CDATA[Journal article (JA)]]></DT>");
			dtMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<DT label=\"Document type\"><![CDATA[Conference article (CA)]]></DT>");
			dtMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<DT label=\"Document type\"><![CDATA[Conference proceeding (CP)]]></DT>");
		}
		else
		{
			dtMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<TY><![CDATA[CONF]]></TY>");
			dtMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<TY><![CDATA[JOUR]]></TY>");
			dtMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<TY><![CDATA[CONF]]></TY>");
			dtMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<TY><![CDATA[CONF]]></TY>");
			dtMap.put("geo_152513a113d01a997cM73da2061377553", "<TY><![CDATA[JOUR]]></TY>");
		}

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(dtMap.get(correctDocId.getDocID()) != null)
			{

				correctString = (String)dtMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertConfName(List EIDocs) throws Exception
	{
		HashMap cnMap = new HashMap();
		cnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		cnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<CF label=\"Conference name\"><![CDATA[MEMS/MOEMS Components and Their Applications III]]></CF>");
		cnMap.put("geo_152513a113d01a997cM73da2061377553", null);
		cnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<CF label=\"Conference name\"><![CDATA[12th International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors]]></CF>");
		cnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<CF label=\"Conference name\"><![CDATA[TMS 2006 Annual Meeting -  Magnesium Technology]]></CF>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(cnMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)cnMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertAuthors(List EIDocs, String dataFormat) throws Exception
	{
		HashMap authorsFullMap = new HashMap();
		authorsFullMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS>");
		authorsFullMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Macintosh Bruce]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Graham James]]><AFS><AFID>1</AFID><AFID>4</AFID></AFS></AU><AU id=\"1\"><![CDATA[Oppenheimer Ben]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"1\"><![CDATA[Poyneer Lisa]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Sivaramakrishnan Anand]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"5\"><![CDATA[Veran Jean-Pierre]]><AFS><AFID>5</AFID></AFS></AU></AUS>");
		authorsFullMap.put("geo_152513a113d01a997cM73da2061377553", "<AUS label=\"Authors\"><AU id=\"0\"><![CDATA[Watkins Ruth]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Scourse James D.]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Allen Judy R.M.]]><AFS><AFID>0</AFID></AFS></AU></AUS>");
		authorsFullMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Kim Hansang]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Urquidi-Macdonald Mima]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Macdonald Digby]]><AFS><AFID>1</AFID></AFS></AU></AUS>");

		HashMap authorsCitationMap = new HashMap();
		authorsCitationMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AUS><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS>");
		authorsCitationMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AUS><AU id=\"1\"><![CDATA[Macintosh Bruce]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Graham James]]><AFS><AFID>1</AFID><AFID>4</AFID></AFS></AU><AU id=\"1\"><![CDATA[Oppenheimer Ben]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"1\"><![CDATA[Poyneer Lisa]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Sivaramakrishnan Anand]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"5\"><![CDATA[Veran Jean-Pierre]]><AFS><AFID>5</AFID></AFS></AU></AUS>");
		authorsCitationMap.put("geo_152513a113d01a997cM73da2061377553", "<AUS><AU id=\"0\"><![CDATA[Watkins Ruth]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Scourse James D.]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Allen Judy R.M.]]><AFS><AFID>0</AFID></AFS></AU></AUS>");
		authorsCitationMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AUS><AU id=\"1\"><![CDATA[Kim Hansang]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Urquidi-Macdonald Mima]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Macdonald Digby]]><AFS><AFID>1</AFID></AFS></AU></AUS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(authorsFullMap.get(correctDocId.getDocID()) != null)
			{
				if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = (String)authorsFullMap.get(correctDocId.getDocID());
				}
				else
				{
					correctString = (String)authorsCitationMap.get(correctDocId.getDocID());
				}

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertAuthorAffiliations(List EIDocs) throws Exception
	{
		HashMap auafFullMap = new HashMap();
		auafFullMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF><AF id=\"2\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></AF><AF id=\"3\"><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF></AFS>");
		auafFullMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[NSF Center for Adaptive Optics]]></AF><AF id=\"2\"><![CDATA[Lawrence Livermore National Laboratory, 7000 East Ave., Livermore, CA 94551]]></AF><AF id=\"3\"><![CDATA[Astrophysics Department, American Museum of Natural History, 79th Street at Central Park West, New York, NY 10024-5192]]></AF><AF id=\"4\"><![CDATA[Department of Astronomy, University of California at Berkeley, Berkeley, CA 94720]]></AF><AF id=\"5\"><![CDATA[Herzberg Institute of Astrophysics, 5071 W. Saanich Road, Victoria, canada]]></AF></AFS>");
		auafFullMap.put("geo_152513a113d01a997cM73da2061377553", null);
		auafFullMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Department of Materials Science and Engineering, Pennsylvania State University, University Park, PA 16802]]></AF><AF id=\"2\"><![CDATA[Department of Engineering Science and Mechanics, Pennsylvania State University, University Park, PA 16802]]></AF></AFS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(auafFullMap.get(correctDocId.getDocID()) != null)
			{

				correctString = (String)auafFullMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertConfLocation(List EIDocs) throws Exception
	{
		HashMap clMap = new HashMap();
		clMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		clMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<ML label=\"Conference location\"><![CDATA[San Jose, CA, united states]]></ML>");
		clMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<ML label=\"Conference location\"><![CDATA[Salt Lake City, UT, united states]]></ML>");
		clMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<ML label=\"Conference location\"><![CDATA[Salt Lake City, UT, united states]]></ML>");
		clMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<ML label=\"Conference location\"><![CDATA[San Antonio, TX, united states]]></ML>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(clMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)clMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertSponsor(List EIDocs) throws Exception
	{
		HashMap sMap = new HashMap();
		sMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		sMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<SP label=\"Sponsor\"><S><![CDATA[SPIE]]></S><S><![CDATA[Center for Adaptive Optics, an NSF Science and Technology Center]]></S></SP>");
		sMap.put("geo_152513a113d01a997cM73da2061377553", null);
		sMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<SP label=\"Sponsor\"><S><![CDATA[Minerals, Metals and Materials Society, TMS]]></S><S><![CDATA[American Nuclear Society, ANS]]></S><S><![CDATA[NACE International]]></S></SP>");
		sMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<SP label=\"Sponsor\"><S><![CDATA[The Minerals, Metals and Materials Society, TMS]]></S></SP>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(sMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)sMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertRegionalTerms(List EIDocs) throws Exception
	{
		HashMap rMap = new HashMap();
		rMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		rMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		rMap.put("geo_152513a113d01a997cM73da2061377553", "<RGIS label=\"Regional terms\"><RGI><![CDATA[Atlantic Ocean]]></RGI><RGI><![CDATA[Atlantic Ocean (North)]]></RGI><RGI><![CDATA[Eurasia]]></RGI><RGI><![CDATA[Europe]]></RGI><RGI><![CDATA[United Kingdom]]></RGI><RGI><![CDATA[Wales]]></RGI><RGI><![CDATA[Western Europe]]></RGI></RGIS>");
		rMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(rMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)rMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertSpeciesTerms(List EIDocs) throws Exception
	{
		HashMap sMap = new HashMap();
		sMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		sMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		sMap.put("geo_152513a113d01a997cM73da2061377553", "<FLS label=\"Species terms\"><FL><![CDATA[Alnus]]></FL><FL><![CDATA[Betula]]></FL><FL><![CDATA[Corylus]]></FL><FL><![CDATA[Juniperus]]></FL><FL><![CDATA[Malvaceae]]></FL><FL><![CDATA[Quercus]]></FL><FL><![CDATA[Tilia]]></FL><FL><![CDATA[Ulmus]]></FL></FLS>");
		sMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(sMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)sMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertLanguage(List EIDocs, String dataFormat) throws Exception
	{
		HashMap langMap = new HashMap();
		langMap.put("cpx_18a992f10b61b5d4a9M74342061377553","German");
		langMap.put("cpx_18a992f10b61b5d4a9M74252061377553","English");
		langMap.put("geo_152513a113d01a997cM73da2061377553","English");
		langMap.put("cpx_18a992f10c593a6af2M7f882061377553","English");
		langMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "English");


		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(!dataFormat.equals(Citation.CITATION_FORMAT) && !dataFormat.equals(RIS.RIS_FORMAT))
			{
				correctString = "<LA label=\"Language\"><![CDATA[" +(String)langMap.get(correctDocId.getDocID()) + "]]></LA>";
			}
			else
			{

				correctString = "<LA><![CDATA[" +(String)langMap.get(correctDocId.getDocID()) + "]]></LA>";
			}
			//System.out.println("DOCID= "+correctDocId.getDocID()+" xmlString "+xmlString+" correctString "+correctString);
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertEditors(List EIDocs) throws Exception
	{
		HashMap edMap = new HashMap();
		edMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		edMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		edMap.put("geo_152513a113d01a997cM73da2061377553", null);
		edMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<EDS label=\"Editors\"><ED><![CDATA[Allen]]></ED><ED><![CDATA[King]]></ED><ED><![CDATA[Nelson]]></ED></EDS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(edMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)edMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertConfDate(List EIDocs) throws Exception
	{
		HashMap cdates = new HashMap();
		cdates.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		cdates.put("cpx_18a992f10b61b5d4a9M74252061377553", "January 23,2006 - January 25,2006");
		cdates.put("geo_152513a113d01a997cM73da2061377553", null);
		cdates.put("cpx_18a992f10c593a6af2M7f882061377553", "August 14,2005 - August 18,2005");
		cdates.put("cpx_18a992f10b61b5d4a9M743d2061377553", "March 12,2006 - March 16,2006");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(cdates.get(correctDocId.getDocID()) != null)
			{
				correctString = "<MD label=\"Conference date\"><![CDATA[" + (String)cdates.get(correctDocId.getDocID()) + "]]></MD>";
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertStartPage(List EIDocs) throws Exception
	{
		HashMap startPageMap = new HashMap();
		startPageMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "201");
		startPageMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		startPageMap.put("geo_152513a113d01a997cM73da2061377553", "170");
		startPageMap.put("cpx_18a992f10c593a6af2M7f882061377553", "125");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			DocID correctDocId = (DocID)eidoc.getDocID();

			ElementDataMap elementDataMap = eidoc.getElementDataMap();

			if(startPageMap.get(correctDocId.getDocID()) != null)
			{
				ElementData elementData = elementDataMap.get(Keys.START_PAGE);
				String[] data = elementData.getElementData();
				correctString = (String)startPageMap.get(correctDocId.getDocID());
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertEndPage(List EIDocs) throws Exception
	{
		HashMap endPageMap = new HashMap();
		endPageMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "206");
		endPageMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		endPageMap.put("geo_152513a113d01a997cM73da2061377553", "181");
		endPageMap.put("cpx_18a992f10c593a6af2M7f882061377553", "133");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			DocID correctDocId = (DocID)eidoc.getDocID();

			ElementDataMap elementDataMap = eidoc.getElementDataMap();

			if(endPageMap.get(correctDocId.getDocID()) != null)
			{
				ElementData elementData = elementDataMap.get(Keys.END_PAGE);
				String[] data = elementData.getElementData();
				correctString = (String)endPageMap.get(correctDocId.getDocID());
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertClassCode(List EIDocs) throws Exception
	{
		HashMap ccMap = new HashMap();
		ccMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<CLS label=\"Classification code\"><CL><CID>461.2</CID><CTI><![CDATA[Biological Materials and Tissue Engineering]]></CTI></CL><CL><CID>462.4</CID><CTI><![CDATA[Prosthetics]]></CTI></CL><CL><CID>462.5</CID><CTI><![CDATA[Biomaterials (including synthetics)]]></CTI></CL><CL><CID>531</CID><CTI><![CDATA[Metallurgy and Metallography]]></CTI></CL><CL><CID>751.2</CID><CTI><![CDATA[Acoustic Properties of Materials]]></CTI></CL><CL><CID>931.2</CID><CTI><![CDATA[Physical Properties of Gases, Liquids and Solids]]></CTI></CL></CLS>");
		ccMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<CLS label=\"Classification code\"><CL><CID>657.2</CID><CTI><![CDATA[Extraterrestrial Physics and Stellar Phenomena]]></CTI></CL><CL><CID>741.1</CID><CTI><![CDATA[Light/Optics]]></CTI></CL><CL><CID>741.3</CID><CTI><![CDATA[Optical Devices and Systems]]></CTI></CL><CL><CID>801</CID><CTI><![CDATA[Chemistry]]></CTI></CL></CLS>");
		ccMap.put("geo_152513a113d01a997cM73da2061377553", "<CLS label=\"Classification code\"><CL><CID>71.3.7</CID><CTI><![CDATA[The Holocene]]></CTI></CL><CL><CID>72.7.7</CID><CTI><![CDATA[The Holocene]]></CTI></CL></CLS>");
		ccMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<CLS label=\"Classification code\"><CL><CID>803</CID><CTI><![CDATA[Chemical Agents and Basic Industrial Chemicals]]></CTI></CL><CL><CID>802.2</CID><CTI><![CDATA[Chemical Reactions]]></CTI></CL><CL><CID>801.4.1</CID><CTI><![CDATA[Electrochemistry]]></CTI></CL><CL><CID>932.2</CID><CTI><![CDATA[Nuclear Physics]]></CTI></CL><CL><CID>723.5</CID><CTI><![CDATA[Computer Applications]]></CTI></CL><CL><CID>621.1</CID><CTI><![CDATA[Fission Reactors]]></CTI></CL><CL><CID>539.1</CID><CTI><![CDATA[Metals Corrosion]]></CTI></CL><CL><CID>723.2</CID><CTI><![CDATA[Data Processing and Image Processing]]></CTI></CL></CLS>");
		ccMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<CLS label=\"Classification code\"><CL><CID>534.2</CID><CTI><![CDATA[Foundry Practice]]></CTI></CL><CL><CID>537.1</CID><CTI><![CDATA[Heat Treatment Processes]]></CTI></CL><CL><CID>541.1</CID><CTI><![CDATA[Aluminum]]></CTI></CL><CL><CID>542.2</CID><CTI><![CDATA[Magnesium and Alloys]]></CTI></CL><CL><CID>723.5</CID><CTI><![CDATA[Computer Applications]]></CTI></CL><CL><CID>931.2</CID><CTI><![CDATA[Physical Properties of Gases, Liquids and Solids]]></CTI></CL></CLS>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(ccMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)ccMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertAbstractType(List EIDocs) throws Exception
	{
		HashMap absType = new HashMap();
		absType.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<AT label=\"Abstract type\"><![CDATA[(Edited Abstract)]]></AT>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(absType.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)absType.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertProvider(List EIDocs) throws Exception
	{
		String correctString = null;
		correctString = "<PVD label=\"Provider\"><![CDATA[Ei]]></PVD>";

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			assertTrue(xmlString.indexOf(correctString) != -1);

		}
	}

	protected void assertPubOrder(List EIDocs) throws Exception
	{
		HashMap poMap = new HashMap();
		poMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<PO><![CDATA[20051201]]></PO>");
		poMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<PO><![CDATA[20060515]]></PO>");
		poMap.put("geo_152513a113d01a997cM73da2061377553", "<PO><![CDATA[20070711]]></PO>");
		poMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<PO><![CDATA[20051201]]></PO>");
		poMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<PO><![CDATA[20060515]]></PO>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter ) ;
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();
			if(poMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)poMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

}
