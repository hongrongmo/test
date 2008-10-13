

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
			listOfDocIDs.add(new DocID("pch_B9CB8C083E7510C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_B9CB8C03873410C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_B9CB8C08184610C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_B9CB8C07B53010C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_B9CB8C0806B010C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_B9CB8C0806B110C6E03408002081DCA4", databaseConfig.getDatabase("cpx")));

			listOfDocIDs.add(new DocID("pch_34f213f85aae815aM672219817173212", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_34f213f85aae815aM7e1a19817173212", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_115f0a9f85ab60809M7ea819817173212", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("pch_34f213f85aae815aM7e2b19817173212", databaseConfig.getDatabase("cpx")));

/*
			listOfDocIDs.add(new DocID("cpx_18a992f10b61b5d4a9M74342061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("cpx_18a992f10b61b5d4a9M74252061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("geo_152513a113d01a997cM73da2061377553", databaseConfig.getDatabase("geo")));
			listOfDocIDs.add(new DocID("cpx_18a992f10c593a6af2M7f882061377553", databaseConfig.getDatabase("cpx")));
			listOfDocIDs.add(new DocID("cpx_18a992f10b61b5d4a9M743d2061377553", databaseConfig.getDatabase("cpx")));
*/
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

			assertDocID(fullDocPages);

			assertMedia(fullDocPages);
			assertCsess(fullDocPages);
			assertPatNo(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertAppln(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertPling(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertPriorNum(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertAssignee(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertPcode(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertClaim(fullDocPages, FullDoc.FULLDOC_FORMAT);

			assertNofig(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertNotab(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertSourc(fullDocPages, FullDoc.FULLDOC_FORMAT);

			assertSubIndex(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertSpecn(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertSuppl(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertAbstractType(fullDocPages, FullDoc.FULLDOC_FORMAT);

			assertAuthors(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertAuthorAffiliations(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertEditors(fullDocPages, FullDoc.FULLDOC_FORMAT);

			assertPdfix(fullDocPages);
			assertReportnumber(fullDocPages);
			assertAccessionNumber(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertVolumeTitle(fullDocPages);
			assertSerialTitle(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertAbbrevSerialTitle(fullDocPages);
			assertPi(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertDOI(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfCode(fullDocPages);
			//assertCorrespondencename(fullDocPages, FullDoc.FULLDOC_FORMAT);
			//assertCorrespondenceeaddress(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfName(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertISSN(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertISBN(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertCoden(fullDocPages);
			assertVolume(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertIssue(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertPubYear(fullDocPages, FullDoc.FULLDOC_FORMAT);

			/****************************************************/

			assertConfLocation(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfLocation(risPages, RIS.RIS_FORMAT);
			assertConfDate(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfDate(risPages, RIS.RIS_FORMAT);

			assertPublisher(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertPublisher(citationPages,Citation.CITATION_FORMAT);
			assertPublisher(risPages,RIS.RIS_FORMAT);
			assertControlledTerms(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertControlledTerms(risPages,RIS.RIS_FORMAT);
			assertPageCount(fullDocPages, FullDoc.FULLDOC_FORMAT);
			//assertLanguage(citationPages, Citation.CITATION_FORMAT);
			//assertLanguage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			//assertLanguage(risPages,RIS.RIS_FORMAT);
			assertSponsor(fullDocPages);
			//assertTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			//assertTranslatedTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertTreatments(fullDocPages);
			//assertUnControlledTerms(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertStartPage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertEndPage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			//assertClassCode(fullDocPages);

			/*

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
			assertSerialTitle(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertAbbrevSerialTitle(fullDocPages);
			assertISSN(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertIssueTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertMainHeading(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertControlledTerms(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertUnControlledTerms(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertTreatments(fullDocPages);
			assertTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertTranslatedTitle(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertISBN(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertPageRange(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertAbstract(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertVolumeTitle(fullDocPages);
			assertPublisher(fullDocPages,FullDoc.FULLDOC_FORMAT);
			assertDocType(fullDocPages,FullDoc.FULLDOC_FORMAT);
			//assertConfName(fullDocPages,FullDoc.FULLDOC_FORMAT);

			assertAuthorAffiliations(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfLocation(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertSponsor(fullDocPages);
			assertRegionalTerms(fullDocPages);
			assertSpeciesTerms(fullDocPages);
			assertLanguage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertConfDate(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertStartPage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertEndPage(fullDocPages, FullDoc.FULLDOC_FORMAT);
			assertClassCode(fullDocPages);
			assertProvider(fullDocPages);

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
			assertDOI(risPages, RIS.RIS_FORMAT);
			assertUnControlledTerms(risPages,RIS.RIS_FORMAT);
			assertIssueTitle(risPages, RIS.RIS_FORMAT);
			assertSerialTitle(risPages,RIS.RIS_FORMAT);
			assertTitle(risPages, RIS.RIS_FORMAT);
			assertMainHeading(risPages, RIS.RIS_FORMAT);
			assertControlledTerms(risPages,RIS.RIS_FORMAT);
			assertAbstract(risPages,RIS.RIS_FORMAT);
			assertPublisher(risPages,RIS.RIS_FORMAT);
			//assertAccessionNumber(risPages,RIS.RIS_FORMAT);
			assertISSN(risPages,RIS.RIS_FORMAT);
			assertISBN(risPages,RIS.RIS_FORMAT);
			//assertAuthors(risPages,RIS.RIS_FORMAT);
			//assertAuthorAffiliations(risPages,RIS.RIS_FORMAT);
			assertTranslatedTitle(risPages,RIS.RIS_FORMAT);
			//assertConfName(risPages,RIS.RIS_FORMAT);
			assertConfDate(risPages,RIS.RIS_FORMAT);
			assertConfLocation(risPages,RIS.RIS_FORMAT);
			//assertStartPage(risPages,RIS.RIS_FORMAT);
			//assertEndPage(risPages,RIS.RIS_FORMAT);
			//assertPubYear(risPages,RIS.RIS_FORMAT);

		*/

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
			//System.out.println("DOCID_xmlString= "+xmlString);

			correctString = "<DOC-ID>" + correctDocId.getDocID() + "</DOC-ID>";
			//System.out.println("DOCID_correctString= "+correctString);
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertAccessionNumber(List EIDocs, String dataFormat) throws Exception
	{
		HashMap aNumbers = new HashMap();
		/*
		aNumbers.put("cpx_18a992f10b61b5d4a9M74342061377553", "06189858062");
		aNumbers.put("cpx_18a992f10b61b5d4a9M74252061377553", "2006189858079");
		aNumbers.put("geo_152513a113d01a997cM73da2061377553", "3030204");
		aNumbers.put("cpx_18a992f10c593a6af2M7f882061377553", "06269957701");
		aNumbers.put("cpx_18a992f10b61b5d4a9M743d2061377553", "06189858053");
		*/
		aNumbers.put("pch_B9CB8C08184610C6E03408002081DCA4", "537077");

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
				correctString = "<AN><![CDATA[" +(String)aNumbers.get(correctDocId.getDocID()) + "]]></AN>";
			}
			else
			{
				correctString = "<AN label=\"Accession number\"><![CDATA[" +(String)aNumbers.get(correctDocId.getDocID()) + "]]></AN>";
			}
			if(aNumbers.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}



	protected void assertPdfix(List EIDocs) throws Exception
	{
		HashMap pdfix = new HashMap();
		pdfix.put("pch_B9CB8C08184610C6E03408002081DCA4", "1994/00/00");

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
			correctString = "<PDFIX label=\"Pdfix\"><![CDATA[" +(String)pdfix.get(correctDocId.getDocID()) + "]]></PDFIX>";

			if(pdfix.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertReportnumber(List EIDocs) throws Exception
	{
		HashMap pap = new HashMap();
		pap.put("pch_B9CB8C08184610C6E03408002081DCA4", "537077");

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
			correctString = "<PA label=\"Paper number\"><![CDATA[" +(String)pap.get(correctDocId.getDocID()) + "]]></PA>";

			if(pap.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertMedia(List EIDocs) throws Exception
	{
		HashMap media = new HashMap();
		media.put("pch_B9CB8C08184610C6E03408002081DCA4", "paperback printed:PB");

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
			correctString = "<MEDIA label=\"Media\"><![CDATA[" +(String)media.get(correctDocId.getDocID()) + "]]></MEDIA>";

			if(media.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertCsess(List EIDocs) throws Exception
	{
		HashMap csess = new HashMap();
		csess.put("pch_B9CB8C083E7510C6E03408002081DCA4", "2");
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
			correctString = "<CSESS label=\"Csess\"><![CDATA[" +(String)csess.get(correctDocId.getDocID()) + "]]></CSESS>";

			if(csess.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertPatNo(List EIDocs, String dataFormat ) throws Exception
	{
		HashMap patno = new HashMap();
		patno.put("pch_B9CB8C03873410C6E03408002081DCA4", "3900621");
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
			correctString = "<PAP label=\"Patent number\"><![CDATA[" +(String)patno.get(correctDocId.getDocID()) + "]]></PAP>";


			if(patno.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertPling(List EIDocs, String dataFormat) throws Exception
	{
		HashMap vols = new HashMap();
		vols.put("pch_B9CB8C07B53010C6E03408002081DCA4", "FR 2752431");
		vols.put("pch_B9CB8C0806B010C6E03408002081DCA4", "US 5125886");
		vols.put("pch_B9CB8C0806B110C6E03408002081DCA4", "US 4799618");

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
				correctString = "<PLING><![CDATA[" +(String)vols.get(correctDocId.getDocID()) +"]]></PLING>";
			}
			else
			{

				correctString = "<PLING label=\"Pling\"><![CDATA[" +(String)vols.get(correctDocId.getDocID()) + "]]></PLING>";
			}

			if(vols.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}
	protected void assertPriorNum(List EIDocs, String dataFormat) throws Exception
	{
		HashMap priornum = new HashMap();
		priornum.put("pch_B9CB8C07B53010C6E03408002081DCA4", "FR 16518/84 (1984/10/24 AP)");
		priornum.put("pch_B9CB8C0806B010C6E03408002081DCA4", "US 451495 (1989/12/15 AP)");
		priornum.put("pch_B9CB8C0806B110C6E03408002081DCA4", "US 911412 (1986/09/25 Ap)");

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
				correctString = "<PIM><![CDATA[" +(String)priornum.get(correctDocId.getDocID()) +"]]></PIM>";
			}
			else
			{
				correctString = "<PIM label=\"Priority information\"><![CDATA[" +(String)priornum.get(correctDocId.getDocID()) + "]]></PIM>";
			}
			if(priornum.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertAssignee(List EIDocs, String dataFormat) throws Exception
	{
		HashMap assignee = new HashMap();
		assignee.put("pch_B9CB8C0806B010C6E03408002081DCA4", "Procter & Gamble Co.,");
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
				correctString = "<EASM><![CDATA[" +(String)assignee.get(correctDocId.getDocID()) +"]]></EASM>";
			}
			else
			{
				correctString = "<PASM label=\"Assignee\"><![CDATA[" +(String)assignee.get(correctDocId.getDocID()) + "]]></PASM>";

			}

			if(assignee.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertPcode(List EIDocs, String dataFormat) throws Exception
	{
		HashMap pcode = new HashMap();
		pcode.put("pch_B9CB8C0806B010C6E03408002081DCA4", "B65D5/74");

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
				correctString = "<PCODE><![CDATA[" +(String)pcode.get(correctDocId.getDocID()) +"]]></PCODE>";
			}
			else
			{
				correctString = "<PCODE label=\"Pcode\"><![CDATA[" +(String)pcode.get(correctDocId.getDocID()) + "]]></PCODE>";

			}

			if(pcode.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertClaim(List EIDocs, String dataFormat) throws Exception
	{
		HashMap claim = new HashMap();
		claim.put("pch_B9CB8C0806B010C6E03408002081DCA4", "33");

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
				correctString = "<CLAIM><![CDATA[" +(String)claim.get(correctDocId.getDocID()) +"]]></CLAIM>";
			}
			else
			{
				correctString = "<CLAIM label=\"Number of claims\"><![CDATA[" +(String)claim.get(correctDocId.getDocID()) + "]]></CLAIM>";

			}

			if(claim.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertAppln(List EIDocs, String dataFormat) throws Exception
	{
		HashMap appln = new HashMap();
		appln.put("pch_B9CB8C07B53010C6E03408002081DCA4", "CA 493708 (1985/10/24)");
		appln.put("pch_B9CB8C0806B010C6E03408002081DCA4", "CA 2067204 (1991/06/16)");
		appln.put("pch_B9CB8C0806B110C6E03408002081DCA4", "CA 547266 (1987/09/18)");

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
				correctString = "<PAN><![CDATA[" +(String)appln.get(correctDocId.getDocID()) +"]]></PAN>";
			}
			else
			{
				correctString = "<PAN label=\"Application number\"><![CDATA[" +(String)appln.get(correctDocId.getDocID()) + "]]></PAN>";

			}

			if(appln.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertNofig(List EIDocs, String dataFormat) throws Exception
	{
		HashMap nofig = new HashMap();
		nofig.put("pch_B9CB8C08184610C6E03408002081DCA4", "3");
		nofig.put("pch_B9CB8C083E7510C6E03408002081DCA4", "8");

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
				correctString = "<NF><![CDATA[" +(String)nofig.get(correctDocId.getDocID()) +"]]></NF>";
			}
			else
			{
				correctString = "<NF label=\"Number of figures\"><![CDATA[" +(String)nofig.get(correctDocId.getDocID()) + "]]></NF>";

			}

			if(nofig.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}
	protected void assertNotab(List EIDocs, String dataFormat) throws Exception
	{
		HashMap notab = new HashMap();
		notab.put("pch_B9CB8C08184610C6E03408002081DCA4", "2");
		notab.put("pch_B9CB8C083E7510C6E03408002081DCA4", "6");

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
				correctString = "<NOTAB><![CDATA[" +(String)notab.get(correctDocId.getDocID()) +"]]></NOTAB>";
			}
			else
			{
				correctString = "<NOTAB label=\"Number of tables\"><![CDATA[" +(String)notab.get(correctDocId.getDocID()) + "]]></NOTAB>";

			}

			if(notab.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}
	protected void assertSourc(List EIDocs, String dataFormat) throws Exception
	{
		HashMap sourc = new HashMap();
		sourc.put("pch_B9CB8C03873410C6E03408002081DCA4", "U.S. pat. 3,900,621.  Issued Aug. 19, 1975.  3 claims.  3 p.  Cl.427/430. Filed: U.S. appln. 507,213 (Sept. 18, 1974).  Priority:  U.S. pat. 3,852,230 [filed as] U.S. appln. 177,493 (Sept. 2, 1971); Czechosl. 6906/70 (Oct. 14, 1970).");

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
				correctString = "<SO><![CDATA[" +(String)sourc.get(correctDocId.getDocID()) +"]]></SO>";
			}
			else
			{
				correctString = "<SO label=\"Source\"><![CDATA[" +(String)sourc.get(correctDocId.getDocID()) + "]]></SO>";

			}

			if(sourc.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertSubIndex(List EIDocs, String dataFormat) throws Exception
	{
		HashMap subindex = new HashMap();
		subindex.put("pch_B9CB8C083E7510C6E03408002081DCA4","GROUND WOOD PULPING;POPULUS;PRESSURE GROUND WOOD");


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
				correctString = "<SUBINDEX><![CDATA[" +(String)subindex.get(correctDocId.getDocID()) +"]]></SUBINDEX>";
			}
			else
			{
				correctString = "<SUBINDEX label=\"SubIndex\"><![CDATA[" +(String)subindex.get(correctDocId.getDocID()) + "]]></SUBINDEX>";

			}

			if(subindex.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertSpecn(List EIDocs, String dataFormat) throws Exception
	{
		HashMap specn = new HashMap();
		specn.put("pch_B9CB8C083E7510C6E03408002081DCA4","Tampella Pressure Groundwood System");


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
				correctString = "<SPECN><![CDATA[" +(String)specn.get(correctDocId.getDocID()) +"]]></SPECN>";
			}
			else
			{
				correctString = "<SPECN label=\"Specific Names\"><![CDATA[" +(String)specn.get(correctDocId.getDocID()) + "]]></SPECN>";

			}


			if(specn.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertSuppl(List EIDocs, String dataFormat) throws Exception
	{
		HashMap suppl = new HashMap();
		suppl.put("pch_B9CB8C08184610C6E03408002081DCA4","Convention Issue");

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
				correctString = "<SUPPL><![CDATA[" +(String)suppl.get(correctDocId.getDocID()) +"]]></SUPPL>";
			}
			else
			{
				correctString = "<SUPPL label=\"Suppl\"><![CDATA[" +(String)suppl.get(correctDocId.getDocID()) + "]]></SUPPL>";

			}

			if(suppl.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}




	protected void assertVolume(List EIDocs, String dataFormat) throws Exception
	{
		HashMap vols = new HashMap();
		/*
		vols.put("cpx_18a992f10b61b5d4a9M74342061377553", "50");
		vols.put("cpx_18a992f10b61b5d4a9M74252061377553", "6113");
		vols.put("geo_152513a113d01a997cM73da2061377553", "36");
		vols.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		vols.put("cpx_18a992f10b61b5d4a9M743d2061377553", "2006");
		*/

		vols.put("pch_34f213f85aae815aM7e1a19817173212", "4171");

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

	protected void assertCorrespondencename(List EIDocs, String dataFormat) throws Exception
	{
		HashMap cname = new HashMap();

		cname.put("pch_34f213f85aae815aM672219817173212","0Mille C.J.");
		/*
		dois.put("cpx_18a992f10b61b5d4a9M74342061377553","10.1016/j.frl.2005.09.001");
		dois.put("cpx_18a992f10b61b5d4a9M74252061377553","10.1117/12.657463");
		dois.put("geo_152513a113d01a997cM73da2061377553","10.1080/03009480600991573");
		dois.put("cpx_18a992f10c593a6af2M7f882061377553",null);
		dois.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);
*/
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

			correctString = "<CAUS label=\"Corr. author\"><![CDATA[" +(String)cname.get(correctDocId.getDocID()) + "]]></CAUS>";

			if(cname.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}
	protected void assertCorrespondenceeaddress(List EIDocs, String dataFormat) throws Exception
	{
		HashMap caddress = new HashMap();
		caddress.put("pch_115f0a9f85ab60809M7ea819817173212","ithompso@nrcan.gc.ca");
		/*
		dois.put("cpx_18a992f10b61b5d4a9M74342061377553","10.1016/j.frl.2005.09.001");
		dois.put("cpx_18a992f10b61b5d4a9M74252061377553","10.1117/12.657463");
		dois.put("geo_152513a113d01a997cM73da2061377553","10.1080/03009480600991573");
		dois.put("cpx_18a992f10c593a6af2M7f882061377553",null);
		dois.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);
*/
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

			correctString = "<CEML label=\"Corr. author email\"><![CDATA[" +(String)caddress.get(correctDocId.getDocID()) + "]]></CEML>";

			if(caddress.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}


	protected void assertDOI(List EIDocs, String dataFormat) throws Exception
	{
		HashMap dois = new HashMap();

		dois.put("pch_34f213f85aae815aM672219817173212","10.1117/12.437014");
		/*
		dois.put("cpx_18a992f10b61b5d4a9M74342061377553","10.1016/j.frl.2005.09.001");
		dois.put("cpx_18a992f10b61b5d4a9M74252061377553","10.1117/12.657463");
		dois.put("geo_152513a113d01a997cM73da2061377553","10.1080/03009480600991573");
		dois.put("cpx_18a992f10c593a6af2M7f882061377553",null);
		dois.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);
*/
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
		/*
		pyears.put("cpx_18a992f10b61b5d4a9M74342061377553", "2005");
		pyears.put("cpx_18a992f10b61b5d4a9M74252061377553", "2006");
		pyears.put("geo_152513a113d01a997cM73da2061377553", "2007");
		pyears.put("cpx_18a992f10c593a6af2M7f882061377553", "2005");
		pyears.put("cpx_18a992f10b61b5d4a9M743d2061377553", "2006");
		*/

		pyears.put("pch_34f213f85aae815aM7e2b19817173212", "2001");

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
				correctString = "<PY><![CDATA[" + (String)pyears.get(correctDocId.getDocID()) + "]]></PY>";
			}
			else if(!dataFormat.equals(Citation.CITATION_FORMAT))
			{
				correctString = "<YR label=\"Publication year\"><![CDATA[" + (String)pyears.get(correctDocId.getDocID()) + "]]></YR>";
			}
			else
			{
				correctString = "<YR><![CDATA[" + (String)pyears.get(correctDocId.getDocID()) + "]]></YR>";
			}

			if(pyears.get(correctDocId.getDocID()) != null)
			{
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertIssue(List EIDocs, String dataFormat) throws Exception
	{
		HashMap iss = new HashMap();
		/*
		iss.put("cpx_18a992f10b61b5d4a9M74342061377553", "6");
		iss.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		iss.put("geo_152513a113d01a997cM73da2061377553", "2");
		iss.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		iss.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);
		*/
		iss.put("pch_115f0a9f85ab60809M7ea819817173212", "1-3");

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

				if(iss.get(correctDocId.getDocID()) != null)
				{
					assertTrue(xmlString.indexOf(correctString) != -1);
				}
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
		/*
		codens.put("cpx_18a992f10b61b5d4a9M74342061377553", "BMZTA");
		codens.put("cpx_18a992f10b61b5d4a9M74252061377553", "PSISD");
		codens.put("geo_152513a113d01a997cM73da2061377553", null);
		codens.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		codens.put("cpx_18a992f10b61b5d4a9M743d2061377553", null);
		*/

		codens.put("pch_34f213f85aae815aM7e1a19817173212", "PSISDG");

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
		confCodes.put("pch_34f213f85aae815aM672219817173212", "58788");

		/*
		confCodes.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		confCodes.put("cpx_18a992f10b61b5d4a9M74252061377553", "67156");
		confCodes.put("geo_152513a113d01a997cM73da2061377553", null);
		confCodes.put("cpx_18a992f10c593a6af2M7f882061377553", "67492");
		confCodes.put("cpx_18a992f10b61b5d4a9M743d2061377553", "67098");
		*/
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

	protected void assertSerialTitle(List EIDocs,String dataFormat) throws Exception
	{
		HashMap stitles = new HashMap();
		/*
		stitles.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomedizinische Technik");
		stitles.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proceedings of SPIE - The International Society for Optical Engineering");
		stitles.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		stitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proceedings of the Twelfth International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors");
		stitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium Technology");
		*/
		stitles.put("pch_B9CB8C08410F10C6E03408002081DCA4", "Papeterie");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					if(xmlString.indexOf("<TY><![CDATA[JOUR]]></TY>")>-1)
					{
						correctString = "<JO><![CDATA[" + (String)stitles.get(correctDocId.getDocID()) + "]]></JO>";
					}
					else
					{
						correctString = "<T3><![CDATA[" + (String)stitles.get(correctDocId.getDocID()) + "]]></T3>";
					}
				}
				else
				{
					correctString = "<ST label=\"Serial title\"><![CDATA[" + (String)stitles.get(correctDocId.getDocID()) + "]]></ST>";
				}
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}



	protected void assertPi(List EIDocs,String dataFormat) throws Exception
	{
		HashMap pi = new HashMap();
		/*
		stitles.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomedizinische Technik");
		stitles.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proceedings of SPIE - The International Society for Optical Engineering");
		stitles.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		stitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proceedings of the Twelfth International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors");
		stitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnesium Technology");
		*/
		pi.put("pch_115f0a9f85ab60809M7ea819817173212", "S037811270200453X");

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
			if(pi.get(correctDocId.getDocID()) != null)
			{

				correctString = "<PI label=\"Pi\"><![CDATA[" + (String)pi.get(correctDocId.getDocID()) + "]]></PI>";
				if(pi.get(correctDocId.getDocID()) != null)
				{
					assertTrue(xmlString.indexOf(correctString) != -1);
				}
			}
		}
	}


	protected void assertAbbrevSerialTitle(List EIDocs) throws Exception
	{
		HashMap stitles = new HashMap();

		/*stitles.put("cpx_18a992f10b61b5d4a9M74342061377553", "Biomed. Tech.");
		stitles.put("cpx_18a992f10b61b5d4a9M74252061377553", "Proc SPIE Int Soc Opt Eng");
		stitles.put("geo_152513a113d01a997cM73da2061377553", "Boreas");
		stitles.put("cpx_18a992f10c593a6af2M7f882061377553", "Proc. Twelfth Int. Conf. Environ. Degrad. Mater. Nucl. Power Syst. Water React.");
		stitles.put("cpx_18a992f10b61b5d4a9M743d2061377553", "Magnes. Technol.");
*/
		stitles.put("pch_B9CB8C08410F10C6E03408002081DCA4", "Papeterie");
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

	protected void assertISSN(List EIDocs, String dataFormat) throws Exception
	{
		HashMap issns = new HashMap();
		issns.put("cpx_18a992f10b61b5d4a9M74342061377553", "00135585");
		issns.put("cpx_18a992f10b61b5d4a9M74252061377553", "0277786X");
		issns.put("geo_152513a113d01a997cM73da2061377553", "03009483");
		issns.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		issns.put("cpx_18a992f10b61b5d4a9M743d2061377553", "03781127");

		issns.put("pch_34f213f85aae815aM7e1a19817173212","0277-786X");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<SN><![CDATA[" + (String)issns.get(correctDocId.getDocID()) + "]]></SN>";
				}
				else
				{
					correctString = "<SN label=\"ISSN\"><![CDATA[" + (String)issns.get(correctDocId.getDocID()) + "]]></SN>";
				}

				if(issns.get(correctDocId.getDocID()) != null)
				{
					assertTrue(xmlString.indexOf(correctString) != -1);
				}
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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<BT><![CDATA[" + (String)issueTitles.get(correctDocId.getDocID()) + "]]></BT>";
				}
				else if(!dataFormat.equals(Citation.CITATION_FORMAT))
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

	protected void assertMainHeading(List EIDocs,String dataFormat) throws Exception
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
			PrintWriter out = new PrintWriter ( swriter );
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(mainheadingMap.get(correctDocId.getDocID()) != null)
			{
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<KW><![CDATA[" + (String)mainheadingMap.get(correctDocId.getDocID()) + "]]></KW>";
				}
				else
				{
					correctString = "<MH label=\"Main heading\"><![CDATA[" + (String)mainheadingMap.get(correctDocId.getDocID()) + "]]></MH>";
				}
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertPageCount(List EIDocs,String dataFormat) throws Exception
	{
		HashMap ctermsMap = new HashMap();

		ctermsMap.put("pch_34f213f85aae815aM7e1a19817173212", null);
		ctermsMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", "<PC><![CDATA[24 p.]]></PC>");
		ctermsMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", "<PC><![CDATA[42 p.]]></PC>");

		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter );
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(ctermsMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)ctermsMap.get(correctDocId.getDocID());
				//System.out.println("PC correctString= "+ correctString);
				//System.out.println("PC xmlString= "+	 xmlString);
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertControlledTerms(List EIDocs,String dataFormat) throws Exception
	{
		HashMap ctermsMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			ctermsMap.put("pch_34f213f85aae815aM7e1a19817173212", "<CVS><CV><![CDATA[Plants]]></CV><CV><![CDATA[Remote sensing]]></CV><CV><![CDATA[Forestry]]></CV></CVS>");
			ctermsMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", "<CVS><CV><![CDATA[CONTINUOUS FORMS]]></CV><CV><![CDATA[ENGLISH]]></CV><CV><![CDATA[GAA]]></CV><CV><![CDATA[GRAPHIC ARTS]]></CV><CV><![CDATA[MAIL]]></CV><CV><![CDATA[OPENING FEATURES]]></CV><CV><![CDATA[PATENTS]]></CV><CV><![CDATA[PRDS]]></CV><CV><![CDATA[PRINTING ]]></CV></CVS>");
			ctermsMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", "<CVS><CV><![CDATA[BOXES]]></CV><CV><![CDATA[CONTAINERS]]></CV><CV><![CDATA[ENGLISH]]></CV><CV><![CDATA[HEAT SEALING]]></CV><CV><![CDATA[LEAK PROOF CONTAINERS]]></CV><CV><![CDATA[PAPER BOARD CONTAINERS]]></CV><CV><![CDATA[PATENTS]]></CV><CV><![CDATA[PCKG]]></CV><CV><![CDATA[PLASTIC COATED PAPERS]]></CV><CV><![CDATA[POLYMER PAPER COMBINATIONS]]></CV><CV><![CDATA[POURING SPOUTS]]></CV><CV><![CDATA[SEALING]]></CV></CVS>");
		}
		else
		{
			ctermsMap.put("pch_34f213f85aae815aM7e1a19817173212", "<CVS label=\"Controlled terms\"><CV><![CDATA[Plants]]></CV><CV><![CDATA[Remote sensing]]></CV><CV><![CDATA[Forestry]]></CV></CVS>");
			ctermsMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", "<CVS label=\"Controlled terms\"><CV><![CDATA[CONTINUOUS FORMS]]></CV><CV><![CDATA[ENGLISH]]></CV><CV><![CDATA[GAA]]></CV><CV><![CDATA[GRAPHIC ARTS]]></CV><CV><![CDATA[MAIL]]></CV><CV><![CDATA[OPENING FEATURES]]></CV><CV><![CDATA[PATENTS]]></CV><CV><![CDATA[PRDS]]></CV><CV><![CDATA[PRINTING ]]></CV></CVS>");
			ctermsMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", "<CVS label=\"Controlled terms\"><CV><![CDATA[BOXES]]></CV><CV><![CDATA[CONTAINERS]]></CV><CV><![CDATA[ENGLISH]]></CV><CV><![CDATA[HEAT SEALING]]></CV><CV><![CDATA[LEAK PROOF CONTAINERS]]></CV><CV><![CDATA[PAPER BOARD CONTAINERS]]></CV><CV><![CDATA[PATENTS]]></CV><CV><![CDATA[PCKG]]></CV><CV><![CDATA[PLASTIC COATED PAPERS]]></CV><CV><![CDATA[POLYMER PAPER COMBINATIONS]]></CV><CV><![CDATA[POURING SPOUTS]]></CV><CV><![CDATA[SEALING]]></CV></CVS>");
		}
		String correctString = null;

		for(int i = 0; i < EIDocs.size();i++)
		{
			StringWriter swriter = new StringWriter();
			PrintWriter out = new PrintWriter ( swriter );
			EIDoc eidoc = (EIDoc)EIDocs.get(i);
			eidoc.toXML(out);
			out.close();
			String xmlString = swriter.toString();
			DocID correctDocId = (DocID)eidoc.getDocID();
			String docidString = correctDocId.getDocID();

			if(ctermsMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)ctermsMap.get(correctDocId.getDocID());
				//System.out.println("correctString= "+	 correctString);
				//System.out.println("xmlString= "+	 xmlString);
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertUnControlledTerms(List EIDocs,String dataFormat) throws Exception
	{
		HashMap uctermsMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			uctermsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<FLS><FL><![CDATA[Bone-implant-interface]]></FL><FL><![CDATA[Critical load]]></FL><FL><![CDATA[Interface mechanisms]]></FL><FL><![CDATA[Shear strength, push-out test]]></FL></FLS>");
			uctermsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<FLS><FL><![CDATA[Coronagraphs]]></FL><FL><![CDATA[Deformable mirror]]></FL><FL><![CDATA[Extrasolar planets]]></FL><FL><![CDATA[Gemini Planet Imager]]></FL><FL><![CDATA[MEMS]]></FL></FLS>");
			uctermsMap.put("geo_152513a113d01a997cM73da2061377553", "<FLS><FL><![CDATA[Alnus]]></FL><FL><![CDATA[Betula]]></FL><FL><![CDATA[Corylus]]></FL><FL><![CDATA[Juniperus]]></FL><FL><![CDATA[Malvaceae]]></FL><FL><![CDATA[Quercus]]></FL><FL><![CDATA[Tilia]]></FL><FL><![CDATA[Ulmus]]></FL></FLS>");
			uctermsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<FLS><FL><![CDATA[Accumulated damage]]></FL><FL><![CDATA[Electrochemical corrosion potential]]></FL><FL><![CDATA[IGSCC]]></FL><FL><![CDATA[Intergranular stress corrosion cracking (IGSCC)]]></FL></FLS>");
			uctermsMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<FLS><FL><![CDATA[Atmospheric environments]]></FL><FL><![CDATA[Magnesium casting]]></FL><FL><![CDATA[Rheocast magnesium alloy]]></FL><FL><![CDATA[Shrinkage porosity]]></FL></FLS>");
		}
		else
		{
			uctermsMap.put("pch_34f213f85aae815aM7e1a19817173212", "<FLS><FL><![CDATA[Nitrogen fertilization;vegetation indices (VI)]]></FL><FL><![CDATA[Soil moisture]]></FL><FL><![CDATA[Canopy]]></FL><FL><![CDATA[Composite ecosystem degradation index]]></FL><FL><![CDATA[Reflectance anisotropy]]></FL><FL><![CDATA[Interferometric radar]]></FL><FL><![CDATA[Geological hazards]]></FL><FL><![CDATA[EiRev]]></FL></FLS>");
			uctermsMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", "<FLS><FL><![CDATA[CONTINUOUS FORMS]]></FL><FL><![CDATA[ENGLISH]]></FL><FL><![CDATA[GAA]]></FL><FL><![CDATA[GRAPHIC ARTS]]></FL><FL><![CDATA[MAIL]]></FL><FL><![CDATA[OPENING FEATURES]]></FL><FL><![CDATA[PATENTS]]></FL><FL><![CDATA[PRDS]]></FL><FL><![CDATA[PRINTING ]]></FL></FLS>");
			uctermsMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", "<FLS><FL><![CDATA[BOXES]]></FL><FL><![CDATA[CONTAINERS]]></FL><FL><![CDATA[ENGLISH]]></FL><FL><![CDATA[HEAT SEALING]]></FL><FL><![CDATA[LEAK PROOF CONTAINERS]]></FL><FL><![CDATA[PAPER BOARD CONTAINERS]]></FL><FL><![CDATA[PATENTS]]></FL><FL><![CDATA[PCKG]]></FL><FL><![CDATA[PLASTIC COATED PAPERS]]></FL><FL><![CDATA[POLYMER PAPER COMBINATIONS]]></FL><FL><![CDATA[POURING SPOUTS]]></FL><FL><![CDATA[SEALING]]></FL></FLS>");
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
		treatmentsMap.put("pch_34f213f85aae815aM7e1a19817173212", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[G]]></TCO><TTI><![CDATA[General review (GEN)]]></TTI></TR></TRS>");
		treatmentsMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
		treatmentsMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
		treatmentsMap.put("pch_B9CB8C08184610C6E03408002081DCA4", null);
		treatmentsMap.put("pch_115f0a9f85ab60809M7ea819817173212", "<TRS label=\"Treatment\"><TR><TCO><![CDATA[G]]></TCO><TTI><![CDATA[General review (GEN)]]></TTI></TR><TR><TCO><![CDATA[T]]></TCO><TTI><![CDATA[Theoretical (THR)]]></TTI></TR></TRS>");

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
				System.out.println("*************************\n");
				System.out.println("TR correctString= "+correctString);
				System.out.println("TR xmlString= "+xmlString);
				System.out.println("\n*************************");
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertTitle(List EIDocs, String dataFormat) throws Exception
	{
		HashMap titleMap = new HashMap();
		titleMap.put("pch_34f213f85aae815aM7e1a19817173212", "Remote Sensing for Agriculture, Ecosystems, and Hydrology II: Proceedings");
		titleMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", "Bifolded Mailer with Insert");
		titleMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", "Improved One-Piece Pouring Spout Sealed to Innermost and Outermost Surfaces of Moisture-Impervious Carton");
		titleMap.put("pch_B9CB8C07B53010C6E03408002081DCA4", "Procede pour la preparation des pates papetieres chimiques en vue du blanchiment");
		titleMap.put("pch_B9CB8C08184610C6E03408002081DCA4", "Ozone for Pulp Bleaching: Market Aspects and Technological Advances");

		titleMap.put("pch_B9CB8C03873410C6E03408002081DCA4", "IMPREGNATING COMPOSITIONS FOR FIBROUS SHEET MATERIALS");
		titleMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", "Aspen [Populus] Pressure Groundwood Pulp");
		titleMap.put("pch_115f0a9f85ab60809M7ea819817173212", "A review of the long-term effects of post-harvest silviculture on vertebrate wildlife, and predictive models, with an emphasis on boreal forests in Ontario, Canada");
		titleMap.put("pch_34f213f85aae815aM7e2b19817173212", "Water budget model of eucalyptus forest using a canopy characterization by remote sensing techniques and a soil water flux parameterization");
		titleMap.put("pch_B9CB8C08410F10C6E03408002081DCA4", "Resultats 1995: Boomerang conjoncturel?");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<TI><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></TI>";
				}
				else if(!dataFormat.equals(Citation.CITATION_FORMAT))
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

	protected void assertTranslatedTitle(List EIDocs,String dataFormat) throws Exception
	{
		HashMap titleMap = new HashMap();
		titleMap.put("pch_34f213f85aae815aM7e1a19817173212", null);
		titleMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
		titleMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
		titleMap.put("pch_B9CB8C07B53010C6E03408002081DCA4", "Process of Preparing Chemical Papermaking Pulps for Bleaching");
		titleMap.put("pch_B9CB8C08184610C6E03408002081DCA4", null);

		titleMap.put("pch_B9CB8C03873410C6E03408002081DCA4", null);
		titleMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", null);
		titleMap.put("pch_115f0a9f85ab60809M7ea819817173212", null);
		titleMap.put("pch_34f213f85aae815aM7e2b19817173212", null);
		titleMap.put("pch_B9CB8C08410F10C6E03408002081DCA4", "1995 Results: [for the French Pulp and Paper Industry] Boomerang Economic Climate?");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<T1><T><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></T></T1>";
				}
				else
				{
					correctString = "<TT label=\"Title of translation\"><T><![CDATA[" + (String)titleMap.get(correctDocId.getDocID()) + "]]></T></TT>";
				}
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertISBN(List EIDocs, String dataFormat) throws Exception
	{
		HashMap isbnMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			isbnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
			isbnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<S1><![CDATA[0819461555]]></S1>");
			isbnMap.put("geo_152513a113d01a997cM73da2061377553", null);
			//isbnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<S1><![CDATA[9780873395953]]></S1>");
			isbnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<S1><![CDATA[0873396200]]></S1>");
		}
		else
		{
			/*
			isbnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
			isbnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<BN label=\"ISBN-10\"><![CDATA[0819461555]]></BN><BN13 label=\"ISBN-13\"><![CDATA[9780819461551]]></BN13>");
			isbnMap.put("geo_152513a113d01a997cM73da2061377553", null);
			isbnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<BN13 label=\"ISBN-13\"><![CDATA[9780873395953]]></BN13>");
			isbnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<BN label=\"ISBN-10\"><![CDATA[0873396200]]></BN><BN13 label=\"ISBN-13\"><![CDATA[9780873396202]]></BN13>");
			*/
			isbnMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", "<BN label=\"ISBN-10\"><![CDATA[0662159446]]></BN>");
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

	protected void assertAbstract(List EIDocs,String dataFormat) throws Exception
	{
		HashMap absMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			absMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<N2><![CDATA[To study the mechanical behaviour of the implant-bone interface the push- or pull-out test was overtaken from material science. Most authors equate the maximum load (break point) with the failure of the implant integration. Extending the test procedure by acoustic emission analysis reveals the possibility to detect the failure of the interface more in detail and from its earliest beginning. The development of disconnection between host and implant was found to start long before the ultimate load is reached and can be monitored and quantified during this period. The active interface mechanisms are characterized by the distribution function of acoustic emissions and the number of hits per time defines the kinetics of the failure. From clinical studies a gradual subsidence of loaded implants is known starting long time before the definite implant failure. The presented extension of the push-out test with acoustic emission analysis allows the detection of a critical shear stress tc which demarks the onset of the gradual interface failure. We believe this value to represent the real critical load which should not be exceeded in the clinical application of intraosseous implants.]]></N2>");
			absMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<N2><![CDATA[The next major step in the study of extrasolar planets will be the direct detection, resolved from their parent star, of a significant sample of Jupiter-like extrasolar giant planets. Such detection will open up new parts of the extrasolar planet distribution and allow spectroscopic characterization of the planets themselves. Detecting Jovian planets at 5-50 AU scale orbiting nearby stars requires adaptive optics systems and coronagraphs an order of magnitude more powerful than those available today - the realm of \"Extreme\" adaptive optics. We present the basic requirements and design for such a system, the Gemini Planet Imager (GPI.) GPI will require a MEMS-based deformable mirror with good surface quality, 2-4 micron stroke (operated in tandem with a conventional low-order \"woofer\" mirror), and a fully-functional 48-actuator-diameter aperture. Adaptive optics, extrasolar planets, speckle, coronagraphs.]]></N2>");
			absMap.put("geo_152513a113d01a997cM73da2061377553", "<N2><![CDATA[Detailed pollen, charcoal and loss on ignition profiles were analysed from Llyn Cororion, North Wales, UK. The chronology was based on 11 radiocarbon dates. This site is particularly important for this region because its high-resolution record improves the spatial and temporal resolution of records of Holocene vegetation change in an area characterized by a highly variable environment. An early Holocene phase of Juniperus-Betula scrub was succeeded by Betula-Corylus woodland. Quercus and Ulmus were established by c. 8600 <sup>14</sup>C yr BP, with Pinus dominating at c. 8430 <sup>14</sup>C yr BP. Local disturbance then allowed the spread of Alnus; Tilia was a common component of the forest by 5650 <sup>14</sup>C yr BP. Charcoal and pollen records suggest that by c. 2600 <sup>14</sup>C yr BP there was progressive deforestation, increased use of fire and spread of grassland; the first cereal grain was recorded at c. 2900 <sup>14</sup>C yr BP. Compared with data from upland Snowdonia, the results show that within a topographically diverse region there were significant local variations in forest composition. These variations developed as a response to interactions between many environmental parameters and were further complicated by the influence of human activity. In an area such as North Wales it is therefore unlikely that one site can be representative of regional Holocene vegetational development. The site is additionally important because it contributes to the data available for meta-analyses of environmental change in the North Atlantic region, particularly as detailed pollen diagrams from coastal lake sites around estern Europe are rare.]]></N2>");
			absMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<N2><![CDATA[Computer codes developed by this group over the past decade for modeling water chemistry and estimating the accumulated damage from stress corrosion cracking in boiling water reactors (BWRs), including DAMAGE_PREDICTOR, REMAIN, and ALERT, have now been superceded by a new code, FOCUS. This new code predicts water chemistry (radiolysis), electrochemical corrosion potential, crack velocity, and accumulated damage (crack depth) in BWR primary coolant circuits at many points simultaneously under normal water chemistry (NWC) and hydrogen water chemistry (HWC) operating protocols over specified operating histories. FOCUS includes the Advanced Coupled Environment Fracture Model (ACEFM) for estimating crack growth rate over a wide temperature range and hence is particularly useful for modeling BWRs that are subject to frequent start ups and shut downs. Additionally, a more robust and flexible water chemistry code is incorporated into FOCUS that allows for more accurate simulation of changes in coolant conductivity under upset conditions. The application of FOCUS for modeling the chemistry, electrochemistry, and the accumulation of intergranular stress corrosion cracking (IGSCC) damage in BWR primary coolant circuits is illustrated in this paper.]]></N2>");
			absMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<N2><![CDATA[The proceedings contain 87 papers. The topics discussed include: simulation of atmospheric environments for storage and transport of magnesium and its alloys; the physical chemistry of the carbothermic route to magnesium; thermal de-coating of magnesium - a first step towards recycling of coated magnesium; phenomena of formation of gas induced shrinkage porosity in pressure die-cast Mg-alloys; the road to 2020: overview of the magnesium casting industry technology roadmap; heat treatment and mechanical properties of a rheocast magnesium alloy; microstructural refinement of magnesium alloy by electromagnetic vibrations; and fabrication of carbon long fiber reinforced magnesium parts in high pressure die casting.]]></N2>");
		}
		else
		{
			absMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AB label=\"Abstract\"><![CDATA[To study the mechanical behaviour of the implant-bone interface the push- or pull-out test was overtaken from material science. Most authors equate the maximum load (break point) with the failure of the implant integration. Extending the test procedure by acoustic emission analysis reveals the possibility to detect the failure of the interface more in detail and from its earliest beginning. The development of disconnection between host and implant was found to start long before the ultimate load is reached and can be monitored and quantified during this period. The active interface mechanisms are characterized by the distribution function of acoustic emissions and the number of hits per time defines the kinetics of the failure. From clinical studies a gradual subsidence of loaded implants is known starting long time before the definite implant failure. The presented extension of the push-out test with acoustic emission analysis allows the detection of a critical shear stress tc which demarks the onset of the gradual interface failure. We believe this value to represent the real critical load which should not be exceeded in the clinical application of intraosseous implants.]]></AB>");
			absMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AB label=\"Abstract\"><![CDATA[The next major step in the study of extrasolar planets will be the direct detection, resolved from their parent star, of a significant sample of Jupiter-like extrasolar giant planets. Such detection will open up new parts of the extrasolar planet distribution and allow spectroscopic characterization of the planets themselves. Detecting Jovian planets at 5-50 AU scale orbiting nearby stars requires adaptive optics systems and coronagraphs an order of magnitude more powerful than those available today - the realm of \"Extreme\" adaptive optics. We present the basic requirements and design for such a system, the Gemini Planet Imager (GPI.) GPI will require a MEMS-based deformable mirror with good surface quality, 2-4 micron stroke (operated in tandem with a conventional low-order \"woofer\" mirror), and a fully-functional 48-actuator-diameter aperture. Adaptive optics, extrasolar planets, speckle, coronagraphs.]]></AB>");
			absMap.put("geo_152513a113d01a997cM73da2061377553", "<AB label=\"Abstract\"><![CDATA[Detailed pollen, charcoal and loss on ignition profiles were analysed from Llyn Cororion, North Wales, UK. The chronology was based on 11 radiocarbon dates. This site is particularly important for this region because its high-resolution record improves the spatial and temporal resolution of records of Holocene vegetation change in an area characterized by a highly variable environment. An early Holocene phase of Juniperus-Betula scrub was succeeded by Betula-Corylus woodland. Quercus and Ulmus were established by c. 8600 <sup>14</sup>C yr BP, with Pinus dominating at c. 8430 <sup>14</sup>C yr BP. Local disturbance then allowed the spread of Alnus; Tilia was a common component of the forest by 5650 <sup>14</sup>C yr BP. Charcoal and pollen records suggest that by c. 2600 <sup>14</sup>C yr BP there was progressive deforestation, increased use of fire and spread of grassland; the first cereal grain was recorded at c. 2900 <sup>14</sup>C yr BP. Compared with data from upland Snowdonia, the results show that within a topographically diverse region there were significant local variations in forest composition. These variations developed as a response to interactions between many environmental parameters and were further complicated by the influence of human activity. In an area such as North Wales it is therefore unlikely that one site can be representative of regional Holocene vegetational development. The site is additionally important because it contributes to the data available for meta-analyses of environmental change in the North Atlantic region, particularly as detailed pollen diagrams from coastal lake sites around estern Europe are rare.]]></AB>");
			absMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AB label=\"Abstract\"><![CDATA[Computer codes developed by this group over the past decade for modeling water chemistry and estimating the accumulated damage from stress corrosion cracking in boiling water reactors (BWRs), including DAMAGE_PREDICTOR, REMAIN, and ALERT, have now been superceded by a new code, FOCUS. This new code predicts water chemistry (radiolysis), electrochemical corrosion potential, crack velocity, and accumulated damage (crack depth) in BWR primary coolant circuits at many points simultaneously under normal water chemistry (NWC) and hydrogen water chemistry (HWC) operating protocols over specified operating histories. FOCUS includes the Advanced Coupled Environment Fracture Model (ACEFM) for estimating crack growth rate over a wide temperature range and hence is particularly useful for modeling BWRs that are subject to frequent start ups and shut downs. Additionally, a more robust and flexible water chemistry code is incorporated into FOCUS that allows for more accurate simulation of changes in coolant conductivity under upset conditions. The application of FOCUS for modeling the chemistry, electrochemistry, and the accumulation of intergranular stress corrosion cracking (IGSCC) damage in BWR primary coolant circuits is illustrated in this paper.]]></AB>");
			absMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<AB label=\"Abstract\"><![CDATA[The proceedings contain 87 papers. The topics discussed include: simulation of atmospheric environments for storage and transport of magnesium and its alloys; the physical chemistry of the carbothermic route to magnesium; thermal de-coating of magnesium - a first step towards recycling of coated magnesium; phenomena of formation of gas induced shrinkage porosity in pressure die-cast Mg-alloys; the road to 2020: overview of the magnesium casting industry technology roadmap; heat treatment and mechanical properties of a rheocast magnesium alloy; microstructural refinement of magnesium alloy by electromagnetic vibrations; and fabrication of carbon long fiber reinforced magnesium parts in high pressure die casting.]]></AB>");
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
		/*
		vtMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		vtMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<VT label=\"Volume title\"><![CDATA[MEMS/MOEMS Components and Their Applications III]]></VT>");
		vtMap.put("geo_152513a113d01a997cM73da2061377553", null);
		vtMap.put("cpx_18a992f10c593a6af2M7f882061377553", null);
		vtMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<VT label=\"Volume title\"><![CDATA[Magnesium Technology 2006  - Proceedings of Symposium Sponsored by the Magnesium Committee of the Light Metals Division of TMS]]></VT>");

		*/
		vtMap.put("pch_B9CB8C08410F10C6E03408002081DCA4", "<VT label=\"Volume title\"><![CDATA[1634 Amber Trail (Mr. John P. Graham)]]></VT>");

//select VOLUMETITLE from bd_master where m_id ='pch_B9CB8C08410F10C6E03408002081DCA4'
//1634 Amber Trail (Mr. John P. Graham)


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
		pnMap.put("pch_34f213f85aae815aM7e1a19817173212", "The International Society for Optical Engineering");
		pnMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
		pnMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
		pnMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", "Forestry Canada");
		pnMap.put("pch_115f0a9f85ab60809M7ea819817173212", "Elsevier Science B.V.");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<PB><![CDATA[" + (String)pnMap.get(correctDocId.getDocID()) + "]]></PB>";
				}
				else if(!dataFormat.equals(Citation.CITATION_FORMAT))
				{
					correctString = "<PN label=\"Publisher\"><![CDATA[" + (String)pnMap.get(correctDocId.getDocID()) + "]]></PN>";
				}
				else
				{
					correctString = "<PN><![CDATA[" + (String)pnMap.get(correctDocId.getDocID()) + "]]></PN>";
				}
				//System.out.println("PN correctString= "+ correctString);
				//System.out.println("PN xmlString= "+ xmlString);
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

	protected void assertConfName(List EIDocs,String dataFormat) throws Exception
	{
		HashMap cnMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			cnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
			cnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<BT><![CDATA[MEMS/MOEMS Components and Their Applications III]]></BT>");
			cnMap.put("geo_152513a113d01a997cM73da2061377553", null);
			cnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<BT><![CDATA[12th International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors]]></BT>");
			cnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<BT><![CDATA[TMS 2006 Annual Meeting -  Magnesium Technology]]></BT>");
		}
		else
		{
			/*
			cnMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
			cnMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<CF label=\"Conference name\"><![CDATA[MEMS/MOEMS Components and Their Applications III]]></CF>");
			cnMap.put("geo_152513a113d01a997cM73da2061377553", null);
			cnMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<CF label=\"Conference name\"><![CDATA[12th International Conference on Environmental Degradation of Materials in Nuclear Power Systems-Water Reactors]]></CF>");
			cnMap.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<CF label=\"Conference name\"><![CDATA[TMS 2006 Annual Meeting -  Magnesium Technology]]></CF>");
			*/
			cnMap.put("pch_34f213f85aae815aM7e1a19817173212","<CF label=\"Conference name\"><![CDATA[Remote Sensing for Agriculture, Ecosystems, and Hydrology II]]></CF>");
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
			if(cnMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)cnMap.get(correctDocId.getDocID());

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertAuthors(List EIDocs, String dataFormat) throws Exception
	{
		HashMap authorsMap = new HashMap();
		if(dataFormat.equals(Citation.CITATION_FORMAT))
		{
			authorsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AUS><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS>");
			authorsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AUS><AU id=\"1\"><![CDATA[Macintosh Bruce]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Graham James]]><AFS><AFID>1</AFID><AFID>4</AFID></AFS></AU><AU id=\"1\"><![CDATA[Oppenheimer Ben]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"1\"><![CDATA[Poyneer Lisa]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Sivaramakrishnan Anand]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"5\"><![CDATA[Veran Jean-Pierre]]><AFS><AFID>5</AFID></AFS></AU></AUS>");
			authorsMap.put("geo_152513a113d01a997cM73da2061377553", "<AUS><AU id=\"0\"><![CDATA[Watkins Ruth]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Scourse James D.]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Allen Judy R.M.]]><AFS><AFID>0</AFID></AFS></AU></AUS>");
			authorsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AUS><AU id=\"1\"><![CDATA[Kim Hansang]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Urquidi-Macdonald Mima]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Macdonald Digby]]><AFS><AFID>1</AFID></AFS></AU></AUS>");
		}
		else if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			authorsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AUS><AU><![CDATA[Brandt Jo&die;rg]]></AU><AU><![CDATA[Biero&die;gel]]></AU><AU><![CDATA[Holweg]]></AU><AU><![CDATA[Hein]]></AU><AU><![CDATA[Grellmann]]></AU></AUS>");
			authorsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AUS><AU><![CDATA[Macintosh Bruce]]></AU><AU><![CDATA[Graham James]]></AU><AU><![CDATA[Oppenheimer Ben]]></AU><AU><![CDATA[Poyneer Lisa]]></AU><AU><![CDATA[Sivaramakrishnan Anand]]></AU><AU><![CDATA[Veran Jean-Pierre]]></AU></AUS>");
			authorsMap.put("geo_152513a113d01a997cM73da2061377553", "<AUS><AU><![CDATA[Watkins Ruth]]></AU><AU><![CDATA[Scourse James D.]]></AU><AU><![CDATA[Allen Judy R.M.]]></AU></AUS>");
			authorsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AUS ><AU><![CDATA[Kim Hansang]]></AU><AU><![CDATA[Urquidi-Macdonald Mima]]></AU><AU><![CDATA[Macdonald Digby]]></AU></AUS>");
		}
		else
		{
			/*
			authorsMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Brandt Jo&die;rg]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"2\"><![CDATA[Biero&die;gel]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"2\"><![CDATA[Holweg]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Hein]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Grellmann]]><AFS><AFID>2</AFID></AFS></AU></AUS>");
			authorsMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Macintosh Bruce]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Graham James]]><AFS><AFID>1</AFID><AFID>4</AFID></AFS></AU><AU id=\"1\"><![CDATA[Oppenheimer Ben]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"1\"><![CDATA[Poyneer Lisa]]><AFS><AFID>1</AFID><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Sivaramakrishnan Anand]]><AFS><AFID>1</AFID><AFID>3</AFID></AFS></AU><AU id=\"5\"><![CDATA[Veran Jean-Pierre]]><AFS><AFID>5</AFID></AFS></AU></AUS>");
			authorsMap.put("geo_152513a113d01a997cM73da2061377553", "<AUS label=\"Authors\"><AU id=\"0\"><![CDATA[Watkins Ruth]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Scourse James D.]]><AFS><AFID>0</AFID></AFS></AU><AU id=\"0\"><![CDATA[Allen Judy R.M.]]><AFS><AFID>0</AFID></AFS></AU></AUS>");
			authorsMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Kim Hansang]]><AFS><AFID>1</AFID></AFS></AU><AU id=\"2\"><![CDATA[Urquidi-Macdonald Mima]]><AFS><AFID>2</AFID></AFS></AU><AU id=\"1\"><![CDATA[Macdonald Digby]]><AFS><AFID>1</AFID></AFS></AU></AUS>");
			*/
			authorsMap.put("pch_B9CB8C08184610C6E03408002081DCA4", "<AUS label=\"Authors\"><AU id=\"1\"><![CDATA[Gover  T. R.]]><AFS><AFID>1</AFID></AFS></AU></AUS>");
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

			if(authorsMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)authorsMap.get(correctDocId.getDocID());

				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertAuthorAffiliations(List EIDocs,String dataFormat) throws Exception
	{
		HashMap auafFullMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			auafFullMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AD><A><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></A><A><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></A><A><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></A></AD>");
			auafFullMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AD><A><![CDATA[NSF Center for Adaptive Optics]]></A><A><![CDATA[Lawrence Livermore National Laboratory, 7000 East Ave., Livermore, CA 94551]]></A><A><![CDATA[Astrophysics Department, American Museum of Natural History, 79th Street at Central Park West, New York, NY 10024-5192]]></A><A><![CDATA[Department of Astronomy, University of California at Berkeley, Berkeley, CA 94720]]></A><A><![CDATA[Herzberg Institute of Astrophysics, 5071 W. Saanich Road, Victoria, canada]]></A></AD>");
			auafFullMap.put("geo_152513a113d01a997cM73da2061377553", null);
			auafFullMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AD><A><![CDATA[Department of Materials Science and Engineering, Pennsylvania State University, University Park, PA 16802]]></A><A><![CDATA[Department of Engineering Science and Mechanics, Pennsylvania State University, University Park, PA 16802]]></A></AD>");
		}
		else
		{
			/*
			auafFullMap.put("cpx_18a992f10b61b5d4a9M74342061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF><AF id=\"2\"><![CDATA[Martin-Luther-Universita&die;t Halle-Wittenberg, FB Ingenieurwissenschaften, Institut fu&die;r Werkstoffwissenschaft, Geusaer Stra&szlig;e 88, D-06217 Merseburg, germany]]></AF><AF id=\"3\"><![CDATA[Universita&die;tsklinik und Poliklinik fu&die;r Orthopa&die;die und Physikalische Medizin, Magdeburger Stra&szlig;e 22, D-06112 Halle, germany]]></AF></AFS>");
			auafFullMap.put("cpx_18a992f10b61b5d4a9M74252061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[NSF Center for Adaptive Optics]]></AF><AF id=\"2\"><![CDATA[Lawrence Livermore National Laboratory, 7000 East Ave., Livermore, CA 94551]]></AF><AF id=\"3\"><![CDATA[Astrophysics Department, American Museum of Natural History, 79th Street at Central Park West, New York, NY 10024-5192]]></AF><AF id=\"4\"><![CDATA[Department of Astronomy, University of California at Berkeley, Berkeley, CA 94720]]></AF><AF id=\"5\"><![CDATA[Herzberg Institute of Astrophysics, 5071 W. Saanich Road, Victoria, canada]]></AF></AFS>");
			auafFullMap.put("geo_152513a113d01a997cM73da2061377553", null);
			auafFullMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Department of Materials Science and Engineering, Pennsylvania State University, University Park, PA 16802]]></AF><AF id=\"2\"><![CDATA[Department of Engineering Science and Mechanics, Pennsylvania State University, University Park, PA 16802]]></AF></AFS>");
			*/

			auafFullMap.put("pch_B9CB8C08184610C6E03408002081DCA4", "<AFS label=\"Author affiliation\"><AF id=\"1\"><![CDATA[Air Liquide, france]]></AF></AFS>");
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

			if(auafFullMap.get(correctDocId.getDocID()) != null)
			{

				correctString = (String)auafFullMap.get(correctDocId.getDocID());
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertConfLocation(List EIDocs,String dataFormat) throws Exception
	{
		HashMap clMap = new HashMap();
		if(dataFormat.equals(RIS.RIS_FORMAT))
		{
			clMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
			clMap.put("pch_34f213f85aae815aM7e1a19817173212", "<CY><![CDATA[Barcelona, Spain]]></CY>");
			clMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C07B53010C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C08184610C6E03408002081DCA4", "<CY><![CDATA[New Delhi]]></CY>");
			clMap.put("pch_B9CB8C03873410C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", "<CY><![CDATA[Edmonton , Alberta]]></CY>");
			clMap.put("pch_115f0a9f85ab60809M7ea819817173212", null);
			clMap.put("pch_B9CB8C08410F10C6E03408002081DCA4", null);
			clMap.put("pch_34f213f85aae815aM7e2b19817173212", "<CY><![CDATA[Barcelona, Spain]]></CY>");
		}
		else
		{
			clMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
			clMap.put("pch_34f213f85aae815aM7e1a19817173212", "<ML label=\"Conference location\"><![CDATA[Barcelona, Spain]]></ML>");
			clMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C07B53010C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C08184610C6E03408002081DCA4", "<ML label=\"Conference location\"><![CDATA[New Delhi]]></ML>");
			clMap.put("pch_B9CB8C03873410C6E03408002081DCA4", null);
			clMap.put("pch_B9CB8C083E7510C6E03408002081DCA4", "<ML label=\"Conference location\"><![CDATA[Edmonton , Alberta]]></ML>");
			clMap.put("pch_115f0a9f85ab60809M7ea819817173212", null);
			clMap.put("pch_B9CB8C08410F10C6E03408002081DCA4", null);
			clMap.put("pch_34f213f85aae815aM7e2b19817173212", "<ML label=\"Conference location\"><![CDATA[Barcelona, Spain]]></ML>");
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

			if(clMap.get(correctDocId.getDocID()) != null)
			{
				correctString = (String)clMap.get(correctDocId.getDocID());
				//System.out.println("correctString= "+correctString);
				//System.out.println("xmlString= "+xmlString);
				assertTrue(xmlString.indexOf(correctString) != -1);
			}

		}
	}

	protected void assertSponsor(List EIDocs) throws Exception
	{
		HashMap sMap = new HashMap();
		sMap.put("pch_B9CB8C0806B110C6E03408002081DCA4",null);
		sMap.put("pch_34f213f85aae815aM7e1a19817173212","<SP label=\"Sponsor\"><S><![CDATA[EOS;SPIE;NASA]]></S></SP>");
		sMap.put("pch_B9CB8C0806B010C6E03408002081DCA4",null);
		sMap.put("pch_B9CB8C07B53010C6E03408002081DCA4",null);
		sMap.put("pch_B9CB8C08184610C6E03408002081DCA4","<SP label=\"Sponsor\"><S><![CDATA[IPPTA, Saharanpur, India]]></S></SP>");
		sMap.put("pch_B9CB8C03873410C6E03408002081DCA4",null);
		sMap.put("pch_B9CB8C083E7510C6E03408002081DCA4","<SP label=\"Sponsor\"><S><![CDATA[Alberta Department of Forestry, Lands, and Wildlife, Edmonton, Alberta, Canada; Forestry Canada, Edmonton, Alberta, Canada]]></S></SP>");
		sMap.put("pch_115f0a9f85ab60809M7ea819817173212",null);
		sMap.put("pch_B9CB8C08410F10C6E03408002081DCA4",null);

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
				//System.out.println("correctString= "+correctString);
				//System.out.println("xmlString= "+xmlString);
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
		langMap.put("pch_34f213f85aae815aM7e1a19817173212","English");
		langMap.put("pch_B9CB8C0806B110C6E03408002081DCA4","English");
		langMap.put("pch_B9CB8C07B53010C6E03408002081DCA4","French");
		langMap.put("pch_B9CB8C0806B010C6E03408002081DCA4","English");
		langMap.put("pch_B9CB8C08184610C6E03408002081DCA4","English");
		langMap.put("pch_B9CB8C03873410C6E03408002081DCA4","English");
		langMap.put("pch_B9CB8C083E7510C6E03408002081DCA4","English");


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
			assertTrue(xmlString.indexOf(correctString) != -1);
		}
	}

	protected void assertEditors(List EIDocs,String dataFormat ) throws Exception
	{
		HashMap edMap = new HashMap();
		edMap.put("pch_34f213f85aae815aM7e1a19817173212", "<EDS label=\"Editors\"><ED><![CDATA[Ow  M.]]></ED><ED><![CDATA[Urs  G.]]></ED><ED><![CDATA[Ziliol  E.]]></ED></EDS>");


		/*
		edMap.put("cpx_18a992f10b61b5d4a9M74342061377553", null);
		edMap.put("cpx_18a992f10b61b5d4a9M74252061377553", null);
		edMap.put("geo_152513a113d01a997cM73da2061377553", null);
		edMap.put("cpx_18a992f10c593a6af2M7f882061377553", "<EDS label=\"Editors\"><ED><![CDATA[Allen]]></ED><ED><![CDATA[King]]></ED><ED><![CDATA[Nelson]]></ED></EDS>");
		*/
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

	protected void assertConfDate(List EIDocs,String dataFormat) throws Exception
	{
		HashMap cdates = new HashMap();

		cdates.put("pch_34f213f85aae815aM7e2b19817173212", "Sep 25-27 2000");
		cdates.put("pch_34f213f85aae815aM7e1a19817173212", "Sep 25-27 2000");
		cdates.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
		cdates.put("pch_B9CB8C07B53010C6E03408002081DCA4", null);
		cdates.put("pch_B9CB8C08184610C6E03408002081DCA4", "1994/12/16 - 1994/12/17");
		cdates.put("pch_B9CB8C03873410C6E03408002081DCA4", null);
		cdates.put("pch_B9CB8C083E7510C6E03408002081DCA4", "1987/03/25 - 1987/03/26");
		cdates.put("pch_115f0a9f85ab60809M7ea819817173212", null);
		cdates.put("pch_34f213f85aae815aM7e2b19817173212", "Sep 25-27 2000");
		cdates.put("pch_B9CB8C08410F10C6E03408002081DCA4", null);
		cdates.put("pch_34f213f85aae815aM672219817173212", "Apr 16-19 2001");

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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<MD><![CDATA[" + (String)cdates.get(correctDocId.getDocID()) + "]]></MD>";
				}
				else
				{
					correctString = "<MD label=\"Conference date\"><![CDATA[" + (String)cdates.get(correctDocId.getDocID()) + "]]></MD>";
				}
				//System.out.println("correctString= "+correctString);
				//System.out.println("xmlString= "+xmlString);
				assertTrue(xmlString.indexOf(correctString) != -1);
			}
		}
	}

	protected void assertStartPage(List EIDocs,String dataFormat) throws Exception
	{
		HashMap startPageMap = new HashMap();
		startPageMap.put("pch_34f213f85aae815aM7e1a19817173212", "392p");
		startPageMap.put("pch_B9CB8C08184610C6E03408002081DCA4", null);


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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<SP><![CDATA["+(String)startPageMap.get(correctDocId.getDocID())+"</SP>";
				}
				else
				{
					correctString = (String)startPageMap.get(correctDocId.getDocID());
				}
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertEndPage(List EIDocs,String dataFormat) throws Exception
	{
		HashMap endPageMap = new HashMap();
		endPageMap.put("pch_34f213f85aae815aM7e1a19817173212", null);


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
				if(dataFormat.equals(RIS.RIS_FORMAT))
				{
					correctString = "<EP><![CDATA["+(String)endPageMap.get(correctDocId.getDocID())+"</EP>";
				}
				else
				{
					correctString = (String)endPageMap.get(correctDocId.getDocID());
				}
				assertTrue(data[0].indexOf(correctString) != -1);
			}
		}
	}

	protected void assertClassCode(List EIDocs) throws Exception
	{
		HashMap ccMap = new HashMap();
		ccMap.put("pch_34f213f85aae815aM7e1a19817173212", "<CLS label=\"Classification code\"><CL><CID>731.1</CID><CTI><![CDATA[Control Systems]]></CTI></CL><CL><CID>821</CID><CTI><![CDATA[Agricultural Equipment and Methods; Vegetation and Pest Control]]></CTI></CL><CL><CID>483.1</CID><CTI><![CDATA[Soils and Soil Mechanics]]></CTI></CL><CL><CID>716.2</CID><CTI><![CDATA[Radar Systems and Equipment]]></CTI></CL><CL><CID>723.2</CID><CTI><![CDATA[Data Processing and Image Processing]]></CTI></CL><CL><CID>741</CID><CTI><![CDATA[Light, Optics and Optical Devices]]></CTI></CL><CL><CID>921</CID><CTI><![CDATA[Mathematics]]></CTI></CL><CL><CID>403</CID><CTI><![CDATA[Urban and Regional Planning and Development]]></CTI></CL><CL><CID>914.2</CID><CTI><![CDATA[Fires and Fire Protection]]></CTI></CL><CL><CID>944.2</CID><CTI><![CDATA[Moisture Measurements]]></CTI></CL><CL><CID>711</CID><CTI><![CDATA[Electromagnetic Waves]]></CTI></CL><CL><CID>821.0</CID><CTI><![CDATA[Woodlands and Forestry]]></CTI></CL></CLS>");
		ccMap.put("pch_B9CB8C0806B110C6E03408002081DCA4", null);
		ccMap.put("pch_B9CB8C0806B010C6E03408002081DCA4", null);
		ccMap.put("pch_B9CB8C07B53010C6E03408002081DCA4", null);
		ccMap.put("pch_115f0a9f85ab60809M7ea819817173212", "<CLS label=\"Classification code\"><CL><CID>821.0</CID><CTI><![CDATA[Woodlands and Forestry]]></CTI></CL><CL><CID>821.3</CID><CTI><![CDATA[Agricultural Methods]]></CTI></CL><CL><CID>454</CID><CTI><![CDATA[Environmental Engineering]]></CTI></CL><CL><CID>804</CID><CTI><![CDATA[Chemical Products Generally]]></CTI></CL><CL><CID>821.2</CID><CTI><![CDATA[Agricultural Chemicals]]></CTI></CL></CLS>");

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



	protected void assertAbstractType(List EIDocs, String dataFormat) throws Exception
	{
		HashMap absType = new HashMap();
		//absType.put("cpx_18a992f10b61b5d4a9M743d2061377553", "<AT label=\"Abstract type\"><![CDATA[(Edited Abstract)]]></AT>");
		absType.put("pch_B9CB8C08184610C6E03408002081DCA4","The technology of ozone bleaching is reviewed, the economics of the process are discussed, and the results of pilot tests by France");
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
