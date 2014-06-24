package org.ei.download.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ei.data.CRNLookup;
import org.ei.domain.Abstract;
import org.ei.domain.Citation;
import org.ei.domain.FullDoc;
import org.ei.util.HtmlManipulator;
import org.ei.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class ExcelExportUtil.
 */
public class ExcelExportUtil {

	/** The citation col and x path mapping. */
	private static Map<String, XPathExpression> citationColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();

	private static Map<String, XPathExpression> abstractColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();

	/** The detailed col and x path mapping. */
	private static Map<String, XPathExpression> detailedColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();

	/** The citations col mapping inzld. */
	private static boolean citationsColMappingInzld = false;

	/** The abstracts col mapping inzld. */
	private static boolean abstractsColMappingInzld = false;

	/** The detailed col mapping inzld. */
	private static boolean detailedColMappingInzld = false;

	/** The Constant SEMICOLON_SEPARATER. */
	private static final String SEMICOLON_SEPARATER = "; ";

	/** The Constant DASH_SEPARATER. */
	private static final String DASH_SEPARATER = " - ";

	/** The Constant DOUBLE_SPACER. */
	private static final String DOUBLE_SPACER = "  ";
	
	/** The is referex only records. */
	private boolean isReferexOnlyRecords = false;

	/**
	 * Checks if is referex only records.
	 *
	 * @return true, if is referex only records
	 */
	public boolean isReferexOnlyRecords() {
		return isReferexOnlyRecords;
	}

	/**
	 * Sets the referex only records.
	 *
	 * @param isReferexOnlyRecords the new referex only records
	 */
	public void setReferexOnlyRecords(boolean isReferexOnlyRecords) {
		this.isReferexOnlyRecords = isReferexOnlyRecords;
	}

	/**
	 * Instantiates a new excel export util.
	 * 
	 * @param documentFormat
	 *            the document format
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	public ExcelExportUtil(String documentFormat)
			throws XPathExpressionException {
		super();
		if (Citation.CITATION_FORMAT.equalsIgnoreCase(documentFormat)) {
			compileCitationXpathQueries();
		} else if (Abstract.ABSTRACT_FORMAT.equalsIgnoreCase(documentFormat)) {
			compileAbstractXpathQueries();
		} else if (FullDoc.FULLDOC_FORMAT.equalsIgnoreCase(documentFormat)) {
			compileDetailedXpathQueries();
		}
	}

	/** The xpath common. */
	private XPath xpathCommon = XPathFactory.newInstance().newXPath();

	/**
	 * Creates the work book.
	 * 
	 * @param workbook
	 *            the workbook
	 * @param docformat
	 *            the docformat
	 * @param xmlWriter
	 *            the xml writer
	 * @return the xSSF workbook
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the sAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	public XSSFWorkbook createWorkBook(XSSFWorkbook workbook, String docformat,
			Writer xmlWriter) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		Document document = createDocument(xmlWriter);
		ArrayList<String> columnList = getExcelColList(document, docformat);
		
		if(columnList == null || columnList.isEmpty()){
			setReferexOnlyRecords(true);
		}
		
		if (Citation.CITATION_FORMAT.equalsIgnoreCase(docformat)) {
			workbook = produceCitationResultsSheet(document, columnList,
					workbook);
		} else if (Abstract.ABSTRACT_FORMAT.equalsIgnoreCase(docformat)) {
			workbook = produceAbstractResultsSheet(document, columnList,
					workbook);
		} else if (FullDoc.FULLDOC_FORMAT.equalsIgnoreCase(docformat)) {
			workbook = produceDetailedResultsSheet(document, columnList,
					workbook);
		}
		return workbook;
	}

	/**
	 * Creates the document.
	 * 
	 * @param xmlWriter
	 *            the xml writer
	 * @return the document
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the sAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Document createDocument(Writer xmlWriter)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xmlWriter
				.toString().getBytes()));
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Gets the excel col list.
	 * 
	 * @param doc
	 *            the doc
	 * @param docformat
	 *            the docformat
	 * @return the excel col list
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private ArrayList<String> getExcelColList(Document doc, String docformat)
			throws XPathExpressionException {
		ArrayList<String> columnList = new ArrayList<String>();
		NodeList listOfDocs = doc.getElementsByTagName("EI-DOCUMENT");
		if (listOfDocs == null || listOfDocs.getLength() == 0) {
			return columnList;
		}
		XPath xpath = XPathFactory.newInstance().newXPath();

		Object nonReferexDB = xpath
				.evaluate(
						"PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/DOC/DB/DBMASK[not(text()='131072')]",
						doc, XPathConstants.NODE);
		if (nonReferexDB == null) {
			return columnList;
		}

		Map<String, XPathExpression> colAndXPathMapping = null;
		if (docformat.equalsIgnoreCase(Citation.CITATION_FORMAT)) {
			colAndXPathMapping = ExcelExportUtil.citationColAndXPathMapping;
		} else if (docformat.equalsIgnoreCase(Abstract.ABSTRACT_FORMAT)) {
			colAndXPathMapping = ExcelExportUtil.abstractColAndXPathMapping;
		} else if (docformat.equalsIgnoreCase(FullDoc.FULLDOC_FORMAT)) {
			colAndXPathMapping = ExcelExportUtil.detailedColAndXPathMapping;
		}
		for (String key : colAndXPathMapping.keySet()) {
			if (!key.equalsIgnoreCase("Data Provider")) {
				Object obj = colAndXPathMapping.get(key).evaluate(doc,
						XPathConstants.NODE);
				if (obj != null) {
					columnList.add(key);
				}
			} else if (key.equalsIgnoreCase("Data Provider")) {
				columnList.add(key);
			}
		}
		return columnList;
	}

	/**
	 * Produce citation results sheet.
	 * 
	 * @param doc
	 *            the doc
	 * @param columnList
	 *            the column list
	 * @param workbook
	 *            the workbook
	 * @return the xSSF workbook
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private XSSFWorkbook produceCitationResultsSheet(Document doc,
			List<String> columnList, XSSFWorkbook workbook)
			throws XPathExpressionException {

		if (columnList != null && columnList.size() > 0) {
			XSSFSheet sheet = workbook.createSheet("Citation Results");
			Row rowHeader = sheet.createRow(0);

			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(20,
					140, 117)));
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setFont(font);

			int cellnum = 0;
			for (String columnName : columnList) {
				Cell cell = rowHeader.createCell(cellnum++);
				cell.setCellValue(columnName);
				cell.setCellStyle(style);
			}
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList docList = (NodeList) xpath
					.evaluate(
							"PAGE/PAGE-RESULTS/PAGE-ENTRY//EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]",
							doc, XPathConstants.NODESET);

			if (docList == null || docList.getLength() == 0) {
				return workbook;
			}

			int rowNum = 1;
			for (int s = 0; s < docList.getLength(); s++) {
				Element docElem = (Element) docList.item(s);

				Element dbElem = (Element) docElem.getElementsByTagName(
						"DBMASK").item(0);
				boolean isInventorElmDBs = false;
				if (dbElem != null
						&& dbElem.getFirstChild() != null
						&& (dbElem.getFirstChild().getNodeValue()
								.equalsIgnoreCase("2048")
								|| dbElem.getFirstChild().getNodeValue()
										.equalsIgnoreCase("32768") || dbElem
								.getFirstChild().getNodeValue()
								.equalsIgnoreCase("16384"))) {
					isInventorElmDBs = true;
				}
				if (docElem.getNodeType() == Node.ELEMENT_NODE) {
					Row row = sheet.createRow(rowNum++);
					/*
					 * XSSFCellStyle cellStyle = workbook.createCellStyle();
					 * cellStyle.setFillForegroundColor(new XSSFColor(new
					 * java.awt.Color(224, 227, 234)));
					 * cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
					 */

					int colNum = 0;
					for (String label : columnList) {
						if (label.equalsIgnoreCase("Title")) {
							Cell cell = row.createCell(colNum++);
							// cell.setCellStyle(cellStyle);
							cell.setCellValue(getNodeValue(docElem, "TI"));

						}
						if (label.equalsIgnoreCase("Author")) {
							Cell cell = row.createCell(colNum++);
							if (!isInventorElmDBs) {
								cell.setCellValue(getAuthorInfo(docElem,
										SEMICOLON_SEPARATER));
							} else {
								cell.setCellValue("");
							}
						}
						if (label.equalsIgnoreCase("Author affiliation")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getAuthorAffiliation(docElem,
									SEMICOLON_SEPARATER));

						}
						if (label.equalsIgnoreCase("Editor")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "EDS",
									"ED", SEMICOLON_SEPARATER));
						}

						if (label.equalsIgnoreCase("Inventor")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getChildTextNodes(docElem,
									"IVS", "IV", SEMICOLON_SEPARATER);
							if ((cellValue == null || cellValue
									.equalsIgnoreCase("")) && isInventorElmDBs) {
								cellValue = getChildTextNodes(docElem, "AUS",
										"AU", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Source")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "SO");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "RIL");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("ISBN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN"));
						}
						if (label.equalsIgnoreCase("ISBN13")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN13"));
						}
						if (label.equalsIgnoreCase("Publication year")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "YR"));
						}
						if (label.equalsIgnoreCase("Sponsor")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "RSP"));
						}
						if (label.equalsIgnoreCase("Report")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "RN_LABEL"));
						}
						if (label.equalsIgnoreCase("Report number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "RN"));
						}
						if (label.equalsIgnoreCase("Volume and Issue")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "VO"));
						}
						if (label.equalsIgnoreCase("Pages")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PP");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PP_pp");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "p_PP");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SD"));
						}
						if (label.equalsIgnoreCase("Monograph title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MT"));
						}
						if (label.equalsIgnoreCase("Paper number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PA"));
						}
						if (label.equalsIgnoreCase("Assignee")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PASM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "PASM",
										"PAS", SEMICOLON_SEPARATER);
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "EASM",
										"EAS", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Application number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAN"));
						}
						if (label.equalsIgnoreCase("Patent number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAP"));
						}
						if (label.equalsIgnoreCase("Patent information")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PINFO");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PIM");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Publication number")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PM1");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Publication date")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PD_YR");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "UPD");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PPD");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Kind")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "KD"));
						}
						if (label.equalsIgnoreCase("Filing date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PFD"));
						}
						if (label.equalsIgnoreCase("Patent issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PIDD"));
						}
						if (label.equalsIgnoreCase("Country of application")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "COPA"));
						}
						if (label.equalsIgnoreCase("Language")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "LA"));
						}
						if (label.equalsIgnoreCase("Availability")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "AV");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "AV",
										"A", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
							
						}
						if (label.equalsIgnoreCase("Article in Press")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "DT");
							if (cellValue != null
									&& cellValue
											.equalsIgnoreCase("Article in Press")) {
								cell.setCellValue("Article in Press");
							} else {
								cell.setCellValue("");
							}
						}
						if (label.equalsIgnoreCase("Database")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DBNAME"));
						}
						if (label.equalsIgnoreCase("Copyright")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "CPRT");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "CPR");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Data Provider")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue("Engineering Village");
						}
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * Produce abstract results sheet.
	 * 
	 * @param doc
	 *            the doc
	 * @param columnList
	 *            the column list
	 * @param workbook
	 *            the workbook
	 * @return the xSSF workbook
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private XSSFWorkbook produceAbstractResultsSheet(Document doc,
			List<String> columnList, XSSFWorkbook workbook)
			throws XPathExpressionException {

		if (columnList != null && columnList.size() > 0) {
			XSSFSheet sheet = workbook.createSheet("Abstract Results");
			Row rowHeader = sheet.createRow(0);

			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(20,
					140, 117)));
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setFont(font);

			int cellnum = 0;
			for (String columnName : columnList) {
				Cell cell = rowHeader.createCell(cellnum++);
				cell.setCellValue(columnName);
				cell.setCellStyle(style);
			}
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList docList = (NodeList) xpath
					.evaluate(
							"PAGE/PAGE-RESULTS/PAGE-ENTRY//EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]",
							doc, XPathConstants.NODESET);

			if (docList == null || docList.getLength() == 0) {
				return workbook;
			}

			int rowNum = 1;
			for (int s = 0; s < docList.getLength(); s++) {
				Element docElem = (Element) docList.item(s);

				Element dbElem = (Element) docElem.getElementsByTagName(
						"DBMASK").item(0);
				boolean isInventorElmDBs = false;
				if (dbElem != null
						&& dbElem.getFirstChild() != null
						&& (dbElem.getFirstChild().getNodeValue()
								.equalsIgnoreCase("2048")
								|| dbElem.getFirstChild().getNodeValue()
										.equalsIgnoreCase("32768") || dbElem
								.getFirstChild().getNodeValue()
								.equalsIgnoreCase("16384"))) {
					isInventorElmDBs = true;
				}
				if (docElem.getNodeType() == Node.ELEMENT_NODE) {
					Row row = sheet.createRow(rowNum++);

					int colNum = 0;
					for (String label : columnList) {
						if (label.equalsIgnoreCase("Title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "TI"));

						}
						if (label.equalsIgnoreCase("Author")) {
							Cell cell = row.createCell(colNum++);
							if (!isInventorElmDBs) {
								cell.setCellValue(getAuthorInfo(docElem,
										SEMICOLON_SEPARATER));
							} else {
								cell.setCellValue("");
							}
						}
						if (label.equalsIgnoreCase("Author affiliation")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getAuthorAffiliation(docElem,
									SEMICOLON_SEPARATER));

						}
						if (label.equalsIgnoreCase("Editor")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "EDS",
									"ED", SEMICOLON_SEPARATER));
						}

						if (label.equalsIgnoreCase("Inventor")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getChildTextNodes(docElem,
									"IVS", "IV", SEMICOLON_SEPARATER);
							if ((cellValue == null || cellValue
									.equalsIgnoreCase("")) && isInventorElmDBs) {
								cellValue = getChildTextNodes(docElem, "AUS",
										"AU", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Source")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "SO");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "RIL");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Sponsor")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "RSP");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "SP");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Report")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "RN_LABEL"));
						}
						if (label.equalsIgnoreCase("Report number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "RN"));
						}
						if (label.equalsIgnoreCase("Publisher")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PN");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "I_PN");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Volume and Issue")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "VO"));
						}
						if (label.equalsIgnoreCase("Pages")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PP");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PP_pp");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "p_PP");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SD"));
						}
						if (label.equalsIgnoreCase("Monograph title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MT"));
						}
						if (label.equalsIgnoreCase("Assignee")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PASM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "PASM",
										"PAS", SEMICOLON_SEPARATER);
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "EASM",
										"EAS", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Application information")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAPIM"));
						}
						if (label.equalsIgnoreCase("Application number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAN"));
						}
						if (label.equalsIgnoreCase("Patent information")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PINFO"));
						}
						if (label.equalsIgnoreCase("Publication number")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PM1");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Priority information")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PIM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "PIM",
										"PI", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Publication date")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PD_YR");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "UPD");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PPD");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PPD_YR");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Paper number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PA"));
						}
						if (label.equalsIgnoreCase("Kind")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "KD"));
						}
						if (label.equalsIgnoreCase("Patent number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAP"));
						}
						if (label.equalsIgnoreCase("Filing date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PFD"));
						}
						if (label.equalsIgnoreCase("Patent issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PIDD"));
						}
						if (label.equalsIgnoreCase("Country of application")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "COPA"));
						}
						if (label.equalsIgnoreCase("Publication year")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "YR");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PYR");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Language")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "LA"));
						}
						if (label.equalsIgnoreCase("ISSN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SN"));
						}
						if (label.equalsIgnoreCase("E-ISSN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "E_ISSN"));
						}
						if (label.equalsIgnoreCase("ISBN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN"));
						}
						if (label.equalsIgnoreCase("ISBN13")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN13"));
						}
						if (label.equalsIgnoreCase("DOI")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DO"));
						}
						if (label.equalsIgnoreCase("Article number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem,
									"ARTICLE_NUMBER"));
						}
						if (label.equalsIgnoreCase("Conference name")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "CF"));
						}
						if (label.equalsIgnoreCase("Conference date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MD"));
						}
						if (label.equalsIgnoreCase("Conference location")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "ML"));
						}
						if (label.equalsIgnoreCase("Country of publication")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "CPUB"));
						}
						if (label.equalsIgnoreCase("Translation serial title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "FTTJ"));
						}
						if (label.equalsIgnoreCase("Article in Press")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "DT");
							if (cellValue != null
									&& cellValue
											.equalsIgnoreCase("Article in Press")) {
								cell.setCellValue("Article in Press");
							} else {
								cell.setCellValue("");
							}
						}
						if (label.equalsIgnoreCase("Availability")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "AV");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "AV",
										"A", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
							
						}
						if (label.equalsIgnoreCase("Scope")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SC"));
						}
						if (label.equalsIgnoreCase("Abstract")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "AB");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "AB2");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Number of references")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "NR"));
						}
						if (label.equalsIgnoreCase("Companies")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "CPO",
									"CP", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Chemicals")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "CMS",
									"CM", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Major terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem,
									"MJSM", "MJS", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Main Heading")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MH"));
						}
						if (label.equalsIgnoreCase("Controlled/Subject terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getControlledTerms(docElem));
						}
						if (label.equalsIgnoreCase("CAS registry number(s)")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCRMValues(docElem));
						}
						if (label.equalsIgnoreCase("Uncontrolled terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "FLS",
									"FL", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Classification code")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getClasificationCodes(docElem,
									"CLS", "CL", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "CLGM",
										"CLG", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Regional terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem,
									"RGIS", "RGI", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("IPC code")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getThirdLevelChildText(docElem,
									"PIDM", "PID", "CID", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getThirdLevelChildText(docElem,
										"PIDEPM", "PIDEP", "CID",
										DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("IPC-8 code")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PIDM8", "PID", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("US classification")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PUCM", "PUC", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("ELCA code")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PECM", "PEC", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Coordinates")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCoordinates(docElem, "LOCS",
									"LOC", SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Treatment")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getThirdLevelChildText(docElem,
									"TRS", "TR", "TTI", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "TRS",
										"TR", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Database")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DBNAME"));
						}
						if (label.equalsIgnoreCase("Copyright")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "CPRT");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "CPR");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Data Provider")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue("Engineering Village");
						}
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * Produce detailed results sheet.
	 * 
	 * @param doc
	 *            the doc
	 * @param columnList
	 *            the column list
	 * @param workbook
	 *            the workbook
	 * @return the xSSF workbook
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private XSSFWorkbook produceDetailedResultsSheet(Document doc,
			List<String> columnList, XSSFWorkbook workbook)
			throws XPathExpressionException {

		if (columnList != null && columnList.size() > 0) {
			XSSFSheet sheet = workbook.createSheet("Detailed Results");
			Row rowHeader = sheet.createRow(0);

			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new java.awt.Color(20,
					140, 117)));
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);

			Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setFont(font);

			int cellnum = 0;
			for (String columnName : columnList) {
				Cell cell = rowHeader.createCell(cellnum++);
				cell.setCellValue(columnName);
				cell.setCellStyle(style);
			}
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList docList = (NodeList) xpath
					.evaluate(
							"PAGE/PAGE-RESULTS/PAGE-ENTRY//EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]",
							doc, XPathConstants.NODESET);

			if (docList == null || docList.getLength() == 0) {
				return workbook;
			}

			int rowNum = 1;
			for (int s = 0; s < docList.getLength(); s++) {
				Element docElem = (Element) docList.item(s);
				if (docElem.getNodeType() == Node.ELEMENT_NODE) {
					Row row = sheet.createRow(rowNum++);

					int colNum = 0;
					for (String label : columnList) {
						if (label.equalsIgnoreCase("Title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "TI"));

						}
						if (label.equalsIgnoreCase("Accession number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "AN"));

						}
						if (label.equalsIgnoreCase("Title of translation")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "TT"));

						}
						if (label.equalsIgnoreCase("Author")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getAuthorInfo(docElem,
									SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Author affiliation")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getAuthorAffiliation(docElem,
									SEMICOLON_SEPARATER));

						}
						if (label.equalsIgnoreCase("Corresponding author")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCorrAuthors(docElem));
						}
						if (label.equalsIgnoreCase("Editor")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "EDS",
									"ED", SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Inventor")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "IVS",
									"IV", SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Source")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "SO");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "RIL");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Abbreviated source title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SE"));

						}
						if (label.equalsIgnoreCase("Sponsor")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "RSP");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "SP");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Report number")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "RN");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "RN_LABEL");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Publisher")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PN");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "I_PN");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Volume")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "VOM"));
						}
						if (label.equalsIgnoreCase("Issue")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "IS"));
						}
						if (label.equalsIgnoreCase("Pages")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PP");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PP_pp");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "p_PP");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SD"));
						}
						if (label.equalsIgnoreCase("Monograph title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MT"));
						}
						if (label.equalsIgnoreCase("Volume title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "VT"));
						}
						if (label.equalsIgnoreCase("Assignee")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PASM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "PASM",
										"PAS", SEMICOLON_SEPARATER);
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "EASM",
										"EAS", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Application information")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAPIM"));
						}
						if (label.equalsIgnoreCase("Application number")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PAN");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PANS");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Patent information")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PINFO"));
						}
						if (label.equalsIgnoreCase("Publication number")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PM1");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Priority information")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PIM");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "PIM",
										"PI", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Publication date")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "PD_YR");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "UPD");
							}
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PPD");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Paper number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PA"));
						}
						if (label.equalsIgnoreCase("Kind")) {
							Cell cell = row.createCell(colNum++);
							String kcStr = getNodeValue(docElem, "KC");
							String kdStr = getNodeValue(docElem, "KD");
							if (kdStr != null && !kdStr.equalsIgnoreCase("")) {
								kcStr += " - " + kdStr;
							}
							cell.setCellValue(kcStr);
						}
						if (label.equalsIgnoreCase("Patent number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PAP"));
						}
						if (label.equalsIgnoreCase("Patent authority")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "AUTHCD"));
						}
						if (label.equalsIgnoreCase("Filing date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PFD"));
						}
						if (label.equalsIgnoreCase("Patent issue date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "PIDD"));
						}
						if (label.equalsIgnoreCase("Country of application")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "COPA"));
						}
						if (label.equalsIgnoreCase("Publication year")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "YR"));
						}
						if (label.equalsIgnoreCase("Language")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "LA"));
						}
						if (label.equalsIgnoreCase("ISSN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SN"));
						}
						if (label.equalsIgnoreCase("E-ISSN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "E_ISSN"));
						}
						if (label.equalsIgnoreCase("ISBN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN"));
						}
						if (label.equalsIgnoreCase("ISBN13")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "BN13"));
						}
						if (label.equalsIgnoreCase("DOI")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DO"));
						}
						if (label.equalsIgnoreCase("Article number")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem,
									"ARTICLE_NUMBER"));
						}
						if (label.equalsIgnoreCase("DERWENT accession no")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DERW"));
						}
						if (label.equalsIgnoreCase("Conference name")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "CF"));
						}
						if (label.equalsIgnoreCase("Conference date")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MD"));
						}
						if (label.equalsIgnoreCase("Conference location")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "ML"));
						}
						if (label.equalsIgnoreCase("Conference code")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "CC"));
						}
						if (label.equalsIgnoreCase("CODEN")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "CN"));
						}
						if (label.equalsIgnoreCase("Country of publication")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "CPUB");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "PL");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Translation serial title")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "FTTJ"));
						}
						if (label.equalsIgnoreCase("Document type")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "DT");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "DT",
										"D", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Availability")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "AV");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "AV",
										"A", SEMICOLON_SEPARATER);
							}
							cell.setCellValue(cellValue);
							
						}
						if (label.equalsIgnoreCase("Scope")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "SC"));
						}
						if (label.equalsIgnoreCase("Abstract")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "AB");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "AB2");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Abstract type")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "AT"));
						}
						if (label.equalsIgnoreCase("Number of references")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "NR"));
						}
						if (label.equalsIgnoreCase("Companies")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "CPO",
									"CP", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Chemicals")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "CMS",
									"CM", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Designated states")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "DSM",
									"DS", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Major terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem,
									"MJSM", "MJS", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Main Heading")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "MH"));
						}
						if (label.equalsIgnoreCase("Controlled/Subject terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getControlledTerms(docElem));
						}
						if (label.equalsIgnoreCase("CAS registry number(s)")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCRMValues(docElem));
						}
						if (label.equalsIgnoreCase("Uncontrolled terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "FLS",
									"FL", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Classification code")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getClasificationCodes(docElem,
									"CLS", "CL", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "CLGM",
										"CLG", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Field of search")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem, "FSM",
									"FS", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Regional terms")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getChildTextNodes(docElem,
									"RGIS", "RGI", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("IPC code")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getThirdLevelChildText(docElem,
									"PIDM", "PID", "CID", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getThirdLevelChildText(docElem,
										"PIDEPM", "PIDEP", "CID",
										DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("IPC-8 code")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PIDM8", "PID", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("US classification")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PUCM", "PUC", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("ELCA code")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getThirdLevelChildText(docElem,
									"PECM", "PEC", "CID", DASH_SEPARATER));
						}
						if (label.equalsIgnoreCase("Coordinates")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCoordinates(docElem, "LOCS",
									"LOC", SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Treatment")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getThirdLevelChildText(docElem,
									"TRS", "TR", "TTI", DASH_SEPARATER);
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getChildTextNodes(docElem, "TRS",
										"TR", DASH_SEPARATER);
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Discipline")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getCoordinates(docElem, "DISPS",
									"DISP", SEMICOLON_SEPARATER));
						}
						if (label.equalsIgnoreCase("Database")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue(getNodeValue(docElem, "DBNAME"));
						}
						if (label.equalsIgnoreCase("Copyright")) {
							Cell cell = row.createCell(colNum++);
							String cellValue = getNodeValue(docElem, "CPRT");
							if (cellValue == null
									|| cellValue.equalsIgnoreCase("")) {
								cellValue = getNodeValue(docElem, "CPR");
							}
							cell.setCellValue(cellValue);
						}
						if (label.equalsIgnoreCase("Data Provider")) {
							Cell cell = row.createCell(colNum++);
							cell.setCellValue("Engineering Village");
						}
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * Gets the node value.
	 * 
	 * @param elem
	 *            the elem
	 * @param tagName
	 *            the tag name
	 * @return the node value
	 */
	private String getNodeValue(Element elem, String tagName) {
		String returnStr = "";
		Element node = (Element) elem.getElementsByTagName(tagName).item(0);
		if (node != null && node.getFirstChild()!= null) {
			returnStr = node.getFirstChild().getNodeValue();
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = StringUtil.stripHtml(HtmlManipulator
					.replaceHtmlEntities(returnStr));
		}
		return returnStr;
	}

	/**
	 * Gets the author info.
	 * 
	 * @param elem
	 *            the elem
	 * @param sep
	 *            the sep
	 * @return the author info
	 */
	private String getAuthorInfo(Element elem, String sep) {
		String returnStr = "";
		NodeList auNodes = (NodeList) elem.getElementsByTagName("AU");
		if (auNodes != null) {
			for (int b = 0; b < auNodes.getLength(); b++) {
				Element auElem = (Element) auNodes.item(b);
				
				if(auElem.getFirstChild()!= null)
				returnStr += auElem.getFirstChild().getNodeValue();
				
				NodeList afidNodes = (NodeList) auElem
						.getElementsByTagName("AFID");
				if (afidNodes != null) {
					String afidStr = "";
					for (int c = 0; c < afidNodes.getLength(); c++) {
						Node afidNode = afidNodes.item(c);
						String affNumber = null;
						
						if(afidNode.getFirstChild()!= null)
						 affNumber = afidNode.getFirstChild()
								.getNodeValue();
						if (affNumber != null
								&& Integer.parseInt(affNumber) > 0) {
							if (afidStr.equalsIgnoreCase("")) {
								afidStr += "" + affNumber;
							} else {
								afidStr += "," + affNumber;
							}
						}
					}
					if (!afidStr.equalsIgnoreCase("")) {
						returnStr += "(" + afidStr + ")";
					}
				}
				if (b < (auNodes.getLength() - 1)) {
					returnStr += sep;
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = StringUtil.stripHtml(HtmlManipulator
					.replaceHtmlEntities(returnStr));
		}
		return returnStr;
	}

	/**
	 * Gets the author affiliation.
	 * 
	 * @param elem
	 *            the elem
	 * @param sep
	 *            the sep
	 * @return the author affiliation
	 */
	private String getAuthorAffiliation(Element elem, String sep) {
		String returnStr = "";
		NodeList nodes = elem.getElementsByTagName("AF");
		if (nodes != null) {
			for (int b = 0; b < nodes.getLength(); b++) {
				Node node = nodes.item(b);
				if (node != null) {
					if (node.getAttributes() != null
							&& node.getAttributes().getNamedItem("id") != null) {
						String affId = node.getAttributes().getNamedItem("id")
								.getNodeValue();
						if (affId != null && !affId.equalsIgnoreCase("")) {
							returnStr += "(" + affId + ") ";
						}
					}
					
					if(node.getFirstChild()!= null)
					returnStr += node.getFirstChild().getNodeValue();
					
					if (b < (nodes.getLength() - 1)) {
						returnStr += sep;
					}
				}
			}
		}
		if (returnStr == null || returnStr.equalsIgnoreCase("")) {
			returnStr = getNodeValue(elem, "PF");
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = StringUtil.stripHtml(HtmlManipulator
					.replaceHtmlEntities(returnStr));
		}
		return returnStr;
	}

	/**
	 * Gets the child text nodes.
	 * 
	 * @param elem
	 *            the elem
	 * @param parentElm
	 *            the parent elm
	 * @param childElm
	 *            the child elm
	 * @param sep
	 *            the sep
	 * @return the child text nodes
	 */
	private String getChildTextNodes(Element elem, String parentElm,
			String childElm, String sep) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName(parentElm);
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName(childElm);
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Node childNode = childNodes.item(b);
					if (childNode != null && childNode.getFirstChild() != null) {
						returnStr += childNode.getFirstChild().getNodeValue();
					}
					if (b < (childNodes.getLength() - 1)) {
						returnStr += sep;
					}
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}

	/**
	 * Gets the clasification codes.
	 * 
	 * @param elem
	 *            the elem
	 * @param parentElm
	 *            the parent elm
	 * @param childElm
	 *            the child elm
	 * @param sep
	 *            the sep
	 * @return the clasification codes
	 */
	private String getClasificationCodes(Element elem, String parentElm,
			String childElm, String sep) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName(parentElm);
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName(childElm);
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Element childElem = (Element) childNodes.item(b);
					if (childElem != null) {
						String cidString = getNodeValue(childElem, "CID");
						String ctiString = getNodeValue(childElem, "CTI");
						if (cidString != null
								&& !cidString.equalsIgnoreCase("")) {
							returnStr += cidString;
						}
						if (ctiString != null
								&& !ctiString.equalsIgnoreCase("")) {
							if (cidString != null
									&& !cidString.equalsIgnoreCase(""))
								returnStr += " ";
							returnStr += ctiString;
						}
						if (b < (childNodes.getLength() - 1)) {
							returnStr += sep;
						}
					}
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}

	/**
	 * Gets the third level child text.
	 * 
	 * @param elem
	 *            the elem
	 * @param parentElm
	 *            the parent elm
	 * @param childElm
	 *            the child elm
	 * @param thirdLevelElm
	 *            the third level elm
	 * @param sep
	 *            the sep
	 * @return the third level child text
	 */
	private String getThirdLevelChildText(Element elem, String parentElm,
			String childElm, String thirdLevelElm, String sep) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName(parentElm);
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName(childElm);
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Element childElem = (Element) childNodes.item(b);
					if (childElem != null) {
						String thirdLvlString = getNodeValue(childElem,
								thirdLevelElm);
						if (thirdLvlString != null
								&& !thirdLvlString.equalsIgnoreCase("")) {
							returnStr += thirdLvlString;
							if (b < (childNodes.getLength() - 1)) {
								returnStr += sep;
							}
						}

					}
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}

	/**
	 * Gets the coordinates.
	 * 
	 * @param elem
	 *            the elem
	 * @param parentElm
	 *            the parent elm
	 * @param childElm
	 *            the child elm
	 * @param sep
	 *            the sep
	 * @return the coordinates
	 */
	private String getCoordinates(Element elem, String parentElm,
			String childElm, String sep) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName(parentElm);
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName(childElm);
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Element childElem = (Element) childNodes.item(b);
					if (childElem != null) {
						if (childElem.getAttributes() != null
								&& childElem.getAttributes().getNamedItem("ID") != null) {
							String attrVal = childElem.getAttributes()
									.getNamedItem("ID").getNodeValue();
							if (attrVal != null) {
								returnStr += attrVal;
								returnStr += " - ";
							}
						}
						if(childElem.getFirstChild() != null)
						returnStr += childElem.getFirstChild().getNodeValue();
						
						if (b < (childNodes.getLength() - 1)) {
							returnStr += sep;
						}
					}
				}
			}
		}

		return returnStr;
	}
	
	/**
	 * Gets the corr authors.
	 *
	 * @param elem the elem
	 * @return the corr authors
	 */
	private String getCorrAuthors(Element elem) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName("CAUS");
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName("CAU");
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Element childElem = (Element) childNodes.item(b);
					if (childElem != null) {
						
						if(childElem.getFirstChild() != null)
						returnStr += childElem.getFirstChild().getNodeValue();
						
						String cidString = getNodeValue(childElem, "EMAIL");
						if(cidString != null && !cidString.equalsIgnoreCase("")){
							returnStr += " ("+cidString+")";
						}
					}
					if (b < (childNodes.getLength() - 1)) {
						returnStr += " ";
					}
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}
	
	
	/**
	 * Gets the cRM values.
	 *
	 * @param elem the elem
	 * @return the cRM values
	 */
	private String getCRMValues(Element elem) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName("CRM");
		if (nodes != null && nodes.item(0) != null) {
			Element parentElement = (Element) nodes.item(0);
			NodeList childNodes = parentElement.getElementsByTagName("CR");
			if (childNodes != null) {
				for (int b = 0; b < childNodes.getLength(); b++) {
					Element childElem = (Element) childNodes.item(b);
					if (childElem != null) {
						
						if(childElem.getFirstChild() != null){
							returnStr += childElem.getFirstChild().getNodeValue();
							String crmName = CRNLookup.getName(childElem.getFirstChild().getNodeValue());
							if( crmName != null && !crmName.equalsIgnoreCase("")){
								returnStr += " "+crmName;
							}
						}
						
					}
					if (b < (childNodes.getLength() - 1)) {
						returnStr += DASH_SEPARATER;
					}
				}
			}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}
	
	/**
	 * Gets the controlled terms.
	 *
	 * @param elem the elem
	 * @return the controlled terms
	 */
	private String getControlledTerms(Element elem) {
		String returnStr = "";
		NodeList nodes = (NodeList) elem.getElementsByTagName("CV");
		if (nodes != null) {
				for (int b = 0; b < nodes.getLength(); b++) {
					Element cvElem = (Element) nodes.item(b);
					if (cvElem != null && cvElem.getFirstChild() != null) {
						returnStr += cvElem.getFirstChild().getNodeValue();
					}
					if (b < (nodes.getLength() - 1)) {
						returnStr += " - ";
					}
				}
		}
		if (returnStr != null && !returnStr.equalsIgnoreCase("")) {
			returnStr = HtmlManipulator.replaceHtmlEntities(returnStr);
		}
		return returnStr;
	}

	/**
	 * Compile citation xpath queries.
	 * 
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private void compileCitationXpathQueries() throws XPathExpressionException {
		if (!ExcelExportUtil.citationsColMappingInzld) {
			ExcelExportUtil.citationColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();
			ExcelExportUtil.citationColAndXPathMapping
					.put("Title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TI"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Author",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072') and not(./DOC/DB/DBMASK='2048') and not(./DOC/DB/DBMASK='32768') and not(./DOC/DB/DBMASK='16384')]/AUS"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Author affiliation",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AFS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PF"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Editor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EDS"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Inventor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/IVS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[./DOC/DB/DBMASK[contains(',2048,32768,16384,',concat(',',text(),','))]]/AUS"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Source",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RIL"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("ISBN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("ISBN13",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN13"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Publication year",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/YR"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Sponsor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RSP"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Report",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN_LABEL"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Report number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Volume and Issue",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/VO"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Pages",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP_pp | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/p_PP"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SD"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Monograph title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MT"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Paper number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PA"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Assignee",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PASM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EASM"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Application number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAN"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Patent number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAP"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Patent information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PINFO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIM"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Publication number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM1"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Publication date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PD_YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/UPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PPD"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Kind",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/KD"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Filing date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PFD"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Patent issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDD"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Country of application",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/COPA"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Language",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/LA"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Availability",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AV"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Article In Press",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DT[text()='Article in Press']"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Database",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DOC/DB/DBNAME"));
			ExcelExportUtil.citationColAndXPathMapping
					.put("Copyright",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPRT|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPR"));
			ExcelExportUtil.citationColAndXPathMapping.put("Data Provider",
					null);
			ExcelExportUtil.citationsColMappingInzld = true;
		}
	}
	

	/**
	 * Compile abstract xpath queries.
	 * 
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private void compileAbstractXpathQueries() throws XPathExpressionException {
		if (!ExcelExportUtil.abstractsColMappingInzld) {
			ExcelExportUtil.abstractColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TI"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Author",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072') and not(./DOC/DB/DBMASK='2048') and not(./DOC/DB/DBMASK='32768') and not(./DOC/DB/DBMASK='16384')]/AUS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Author affiliation",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AFS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PF"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Editor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EDS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Inventor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/IVS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[./DOC/DB/DBMASK[contains(',2048,32768,16384,',concat(',',text(),','))]]/AUS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Source",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RIL"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Sponsor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RSP|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SP"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Report",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN_LABEL"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Report number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Publisher",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PN|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/I_PN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Volume and Issue",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/VO"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Pages",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP_pp | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/p_PP"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SD"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Monograph title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MT"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Assignee",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PASM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EASM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Application information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAPIM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Application number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Patent information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PINFO"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Publication number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM1"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Priority information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Publication date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PD_YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/UPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PPD_YR"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Paper number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PA"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Kind",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/KD"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Patent number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAP"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Filing date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PFD"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Patent issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDD"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Country of application",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/COPA"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Publication year",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PYR"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Language",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/LA"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("ISSN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("E-ISSN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/E_ISSN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("ISBN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("ISBN13",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN13"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("DOI",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DO"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Article number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/ARTICLE_NUMBER"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Conference name",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CF"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Conference date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MD"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Conference location",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/ML"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Country of publication",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPUB"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Translation serial title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/FTTJ"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Article In Press",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DT[text()='Article in Press']"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Availability",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AV"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Scope",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SC"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Abstract",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AB | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AB2"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Number of references",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/NR"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Companies",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPO"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Chemicals",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CMS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Major terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MJSM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Main Heading",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MH"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Controlled/Subject terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CVS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("CAS registry number(s)",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CRM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Uncontrolled terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/FLS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Classification code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CLS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CLGM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Regional terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RGIS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("IPC code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDEPM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("IPC-8 code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDM8"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("US classification",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PUCM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("ELCA code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PECM"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Coordinates",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/LOCS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Treatment",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TRS"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Database",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DOC/DB/DBNAME"));
			ExcelExportUtil.abstractColAndXPathMapping
					.put("Copyright",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPRT|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPR"));
			ExcelExportUtil.abstractColAndXPathMapping.put("Data Provider",
					null);
			ExcelExportUtil.abstractsColMappingInzld = true;
		}
	}

	/**
	 * Compile detailed xpath queries.
	 * 
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private void compileDetailedXpathQueries() throws XPathExpressionException {
		if (!ExcelExportUtil.detailedColMappingInzld) {
			ExcelExportUtil.detailedColAndXPathMapping = new LinkedHashMap<String, XPathExpression>();
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TI"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Accession number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Title of translation",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TT"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Author",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AUS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Author affiliation",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AFS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PF"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Corresponding author",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CAUS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Editor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EDS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Inventor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/IVS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Source",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SO | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RIL"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Abbreviated source title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SE"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Sponsor",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RSP|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SP"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Report number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RN_LABEL"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Publisher",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PN|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/I_PN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Volume",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/VOM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Issue",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/IS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Pages",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PP_pp | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/p_PP"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Monograph title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MT"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Volume title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/VT"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Assignee",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PASM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/EASM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Application information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAPIM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Application number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAN|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PANS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Patent information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PINFO"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Publication number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PM1"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Priority information",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Publication date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PD_YR | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/UPD | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PPD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Paper number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PA"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Kind",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/KC"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Patent number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PAP"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Patent authority",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AUTHCD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Filing date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PFD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Patent issue date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Country of application",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/COPA"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Publication year",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/YR"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Language",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/LA"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("ISSN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("E-ISSN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/E_ISSN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("ISBN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("ISBN13",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/BN13"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("DOI",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DO"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Article number",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/ARTICLE_NUMBER"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("DERWENT accession no",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DERW"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Conference name",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CF"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Conference date",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MD"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Conference location",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/ML"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Conference code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CC"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("CODEN",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CN"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Country of publication",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPUB | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PL"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Translation serial title",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/FTTJ"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Document type",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DT"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Availability",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AV"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Scope",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/SC"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Abstract",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AB | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AB2"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Abstract type",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/AT"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Number of references",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/NR"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Companies",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPO"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Chemicals",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CMS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Designated states",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DSM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Major terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MJSM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Main Heading",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/MH"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Controlled/Subject terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CVS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("CAS registry number(s)",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CRM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Uncontrolled terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/FLS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Classification code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CLS | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CLGM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Field of search",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/FSM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Regional terms",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/RGIS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("IPC code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDM | PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDEPM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("IPC-8 code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PIDM8"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("US classification",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PUCM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("ELCA code",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/PECM"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Coordinates",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/LOCS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Treatment",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/TRS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Discipline",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DISPS"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Database",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/DOC/DB/DBNAME"));
			ExcelExportUtil.detailedColAndXPathMapping
					.put("Copyright",
							xpathCommon
									.compile("PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPRT|PAGE/PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT[not(./DOC/DB/DBMASK='131072')]/CPR"));
			ExcelExportUtil.detailedColAndXPathMapping.put("Data Provider",
					null);
			ExcelExportUtil.detailedColMappingInzld = true;
		}
	}

}
