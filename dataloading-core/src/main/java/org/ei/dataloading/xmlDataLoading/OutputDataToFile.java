package org.ei.dataloading.xmlDataLoading;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.GUID;

public class OutputDataToFile
{
	PrintWriter outputFile = null;
	//PrintWriter outputFile_UpdateAff = null;
	String delimited31 = String.valueOf(DataParser.delimited31);
	String delimited30 = String.valueOf(DataParser.delimited30);
	String IDDELIMITED = String.valueOf(DataParser.delimited29);
	String GROUPDELIMITED = String.valueOf(DataParser.delimited02);
	//CountryLanguageConverter countryLanguage = null;
	private static Hashtable dtMappings = new Hashtable();
	private static Hashtable trMappings = new Hashtable();


	static
	{
		dtMappings.put("B","MR");
		dtMappings.put("D","JA");
		dtMappings.put("J","JA");
		dtMappings.put("P","CA");

    }

	public OutputDataToFile(File file)
	{
		try
		{
			System.out.println("Output:"+file.getPath());
			outputFile = new PrintWriter(new FileWriter(file));
			//outputFile_UpdateAff = new PrintWriter(new FileWriter(getUpdate_FileName()));
			//outputFile.write("M_ID|ACCESSION_NUMBER|CODEN|SOURCE_ISSUE|AUTHORS|AUTHOR2|AUTHOR_AFFILIATION|AUTHOR_AFFILIATION_CITY|");
			//outputFile.write("AUTHOR_AFFILIATION_STATE|AUTHOR_AFFILIATION_COUNTRY|AUTHORS_EMAILADDRESS|AUTHORS_INDEXNAME|");
			//outputFile.write("AUTHOR_AFFILIATION_ADDRESSPART|ABSTRACT|ABSTRACT_ORIGINAL|ABSTRACT_SOURCE|ABSTRACT_PUBLISHERCOPYRIGHT|");
			//outputFile.write("ISBN|CONFERENCE_NAME|CONFERENCE_CODE|CLASSIFICATION_DESCRIPTION|CLASSIFICATION|CLASSIFICATION_SUBJECT|");
			//outputFile.write("DESCRIPTOR_MAINTERM_GDE|DESCRIPTOR_MAINTERM_RGI|DESCRIPTOR_MAINTERM_SPC|DESCRIPTOR_MAINTERM_SPC2|CITATION_TYPE|DOCUMENT_TYPE|");
			//outputFile.write("SOURCE_EDITOR|CORRESPONDENCE_PERSON|CORRESPONDENCE_PERSON_INDEXNAME|CORRESPONDENCE_AFFILIATION|");
			//outputFile.write("CORRESPONDENCE_AFFILIATION_CITY|CORRESPONDENCE_AFFILIATION_STATE|CORRESPONDENCE_AFFILIATION_COUNTRY|CORRESPONDENCE_ADDRESS_PART|");
			//outputFile.write("CORRESPONDENCE_EMAIL|SOURCE_PUBLICATIONDATE|SOURCE_TITLE|ISSN_TYPE|ISSN|E_ISSN|REFFERENT_COUNT|SOURCE_PAGERANGE|");
			//outputFile.write("SOURCE_PUBLICATIONYEAR|PII|PUI|DELIVERED_DATE|SORT_DATE|CREATED_DATE|COMPLETED_DATE|REVISED_DATE|");
			//outputFile.write("STATUS_TYPE|STATUS_STATE|STATUS_STAGE|DBCOLLECTION|CITATION_LANGUAGE|ABSTRACT_LANGUAGE|CITATION_TITLE|TITLETEXT_LANGUAGE|");
			//outputFile.write("TITLETEXT_ORIGINAL|SOURCE_TYPE|SOURCE_COUNTRY|SOURCE_SRCID|SOURCE_VOLUME|LOAD_NUMBER|DOI|COPYRIGHT|COPYRIGHT_TYPE|");
			//outputFile.write("ISSUE_TITLE|CONFERENCE_DATE|CONFERENCE_CITY|CONFERENCE_STATE|CONFERENCE_COUNTRY|REPORT_NUMBER|PUBLISHER_NAME|PUBLISHER_CITY|");
			//outputFile.write("PUBLSHER_STATE|PUBLISHER_COUNTRY|PAGES|ABBR_SOURCETITLE|CONFERENCE_SPONSORS|TR|TRANSLATED_TITLE|VOLUME_TITLE|");
			//outputFile.write("CONFERENCE_LOCATION|ARTICLE_NUMBER|PAGECOUNT|PROCPAGERANGE|FIGURE_INFORMATION");
			//outputFile.write("\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void outputData(Hashtable inputData)
	{
		Perl5Util perl = new Perl5Util();
		try
		{
			/**************************      M_ID        ***********************************/
			String m_id = new GUID().toString();
			outputFile.write("geo_"+m_id);
			outputFile.write(delimited31);

			/************************** ACCESSION_NUMBER ***********************************/
			String accessNumber = (String)inputData.get("Item_info-item_id");
			//System.out.println("accessNumber= "+accessNumber);
			outputFile.write(accessNumber);
			outputFile.write(delimited31);

			/**************************** CODEN *********************************/
			String coden = (String)inputData.get("Head-Source-Codencode");
			if(coden==null||coden.length()==0)
				coden = (String)inputData.get("Related_item-Source-Codencode");

			if(coden!=null)
				outputFile.write(coden);
			outputFile.write(delimited31);

			/************************* SOURCE_ISSUE ***************************/
			String issue = (String)inputData.get("Source-voliss_issue");
			if(issue!=null)
				outputFile.write(issue);

			outputFile.write(delimited31);

			/****************************** AUTHORS ************************/
			String author = (String)inputData.get("Head-Authors_fullName");
			String author2 = null;
			if(author==null)
			{
				author = (String)inputData.get("Head-Authors_fullName");
			}

			if(author!=null)
			{
				if(author.length()>4000)
				{
					author2 = author.substring(4000);
					author = author.substring(0,4000);
				}
				outputFile.write(author);
			}

			outputFile.write(delimited31);

			/****************************** AUTHOR2 ****************************/

			if(author2!=null)
			{
				outputFile.write(author2);
			}

			outputFile.write(delimited31);

			/************************ AUTHOR_AFFILIATION ***********************/
			String affiliation  = (String)inputData.get("Head-Affiliation-Organization");
			//outputFile_UpdateAff.write(accessNumber+delimited31);
			if(affiliation == null || affiliation.length()<1)
			{
				affiliation = (String)inputData.get("Head-Affiliation-Text");
				/*
				if(affiliation!=null)
				{
					outputFile_UpdateAff.write(affiliation);
				}
				//System.out.println("text= "+affiliation);
				*/
			}
			if(affiliation!=null)
			{
				outputFile.write(affiliation);
			}

			outputFile.write(delimited31);

			/************************** AUTHOR_AFFILIATION_CITY *************************/
			String affiliation_city = (String)inputData.get("Head-Affiliation-City");

			if(affiliation_city==null)
			{
				affiliation_city = (String)inputData.get("Head-Affiliation-CityGroup");
			}

			if(affiliation_city!=null)
			{
				outputFile.write(affiliation_city);
			}

			outputFile.write(delimited31);

			/************************** AUTHOR_AFFILIATION_STATE *****************************/
			String affiliationState = (String)inputData.get("Head-Affiliation-State");

			if(affiliationState!=null)
			{
				outputFile.write(affiliationState);
			}

			outputFile.write(delimited31);

			/*************************** AUTHOR_AFFILIATION_COUNTRY ******************************/
			String affiliationCountry = (String)inputData.get("Head-Affiliation-Country");

			if(affiliationCountry!=null && affiliationCountry.length()>0)
			{
				//String countryFullName = countryLanguage.getIso3166Country(affiliationCountry);

				if(affiliationCountry!=null)
				{
					outputFile.write(affiliationCountry);
				}
			}
			outputFile.write(delimited31);

			/*******************************  AUTHORS_EMAILADDRESS  ***********************************/
			String authors_emailaddress = (String)inputData.get("Head-Authors_emailAddress");

			if(authors_emailaddress!=null)
				outputFile.write(authors_emailaddress);
			outputFile.write(delimited31);

			/*******************************  AUTHORS_INDEXNAME  ***********************************/
			String authors_indexname = (String)inputData.get("Head-Authors_indexedName");

			if(authors_indexname!=null)
				outputFile.write(authors_indexname);
			outputFile.write(delimited31);

			/*******************************  AUTHOR_ADDRESS_PART  ***********************************/
			String author_aff_addresspart = (String)inputData.get("Head-Affiliation-AddressPart");

			if(author_aff_addresspart!=null)
				outputFile.write(author_aff_addresspart);
			outputFile.write(delimited31);


			/***************************** ABSTRACT ******************************/
			String abstractContent = (String)inputData.get("Abstract-para");
			if(abstractContent!=null)
			{
				if(abstractContent.indexOf(delimited31)>-1)
				{
					abstractContent = perl.substitute("s/\\"+delimited31+"/&#124/g", abstractContent);
				}
				outputFile.write(abstractContent);
			}
			outputFile.write(delimited31);

			/****************************** ABSTRACT_ORIGINAL *****************************/
			String abstractOriginal = (String)inputData.get("Abstract-original");
			if(abstractOriginal!=null)
			{
				outputFile.write(abstractOriginal);
				if(abstractOriginal.length()>1)
					System.out.println("abstractOriginal length= "+abstractOriginal+" "+ abstractOriginal.length());
			}
			outputFile.write(delimited31);
			/****************************** ABSTRACT_SOURCE *****************************/
			String abstractSource = (String)inputData.get("Abstract-absSource");
			if(abstractSource!=null)
				outputFile.write(abstractSource);
			outputFile.write(delimited31);

			/****************************** ABSTRACT_PUBLISHERCOPYRIGHT *****************************/
			String abstractCopyright = (String)inputData.get("Abstract-publishercopyright");
			if(abstractCopyright!=null)
				outputFile.write(abstractCopyright);
			outputFile.write(delimited31);

			/****************************** ISBN *****************************/
			String isbn = (String)inputData.get("Head-Source-ISBN");
			if(isbn==null)
			{
				isbn = (String)inputData.get("Related_item-Source-ISBN");
			}
			if(isbn==null)
			{
				isbn = (String)inputData.get("Refd_itemcitation-isbn");
			}
			if(isbn!=null)
				outputFile.write(isbn);
			outputFile.write(delimited31);

			/******************************* CONFERENCE_NAME *****************************/
			String conferenceName = (String)inputData.get("Conferenceinfo-confname");

			if(conferenceName!=null)
				outputFile.write(conferenceName);
			outputFile.write(delimited31);

			/******************************* CONFERENCE_CODE *******************************/
			String conferenceCode = (String)inputData.get("Conferenceinfo-confcode");

			if(conferenceCode!=null)
				outputFile.write(conferenceCode);
			outputFile.write(delimited31);

			/******************************* CLASSIFICATION_DESCRIPTION ********************************/

			String classificationDescription = (String)inputData.get("Enhancement-classification_description");

			if(classificationDescription!=null)
			{
				outputFile.write(classificationDescription);
			}
			outputFile.write(delimited31);

			/******************************* CLASSIFICATION ********************************/
			String classification = (String)inputData.get("Enhancement-classification");
			if(classification!=null)
				outputFile.write(classification);
			outputFile.write(delimited31);

			/******************************* CLASSIFICATION_SUBJECT ********************************/
			String classification_subject = (String)inputData.get("Enhancement-classification_subject");
			if(classification_subject!=null)
				outputFile.write(classification_subject);
			outputFile.write(delimited31);

			/******************************* DESCRIPTOR_MAINTERM_GDE *******************************/

			String controlledTerm = (String)inputData.get("Enhancement-maintermGDE");

			if(controlledTerm!=null)
			{
				if(controlledTerm.length()>4000)
				{
					controlledTerm = controlledTerm.substring(0,3999);
				}
				outputFile.write(controlledTerm);


			}
			outputFile.write(delimited31);

			/******************************* DESCRIPTOR_MAINTERM_RGI ***********************************/
			String regionalTerm = (String)inputData.get("Enhancement-maintermRGI");
			if(regionalTerm!=null)
			{
				if(regionalTerm.length()>4000)
				{
					regionalTerm = regionalTerm.substring(0,4000);
				}
				outputFile.write(regionalTerm);
			}
			outputFile.write(delimited31);

			/******************************* DESCRIPTOR_MAINTERM_SPC ***********************************/
			String unControlledTerm  = (String)inputData.get("Enhancement-maintermSPC");;
			String unControlledTerm2 = null;
			if(unControlledTerm!=null)
			{
				if(unControlledTerm.length()>4000)
				{
					unControlledTerm2 = unControlledTerm.substring(4000);
					unControlledTerm  = unControlledTerm.substring(0,4000);
				}
				outputFile.write(unControlledTerm);
			}
			outputFile.write(delimited31);

			/******************************* DESCRIPTOR_MAINTERM_SPC2 ***********************************/
			if(unControlledTerm2 != null)
			{
				outputFile.write(unControlledTerm2);
			}

			outputFile.write(delimited31);

			/******************************* CITATION_TYPE ********************************/

			String citationType = (String)inputData.get("Head-Citation_type_code");

			if(citationType!=null)
			{
				outputFile.write(citationType);
			}
			outputFile.write(delimited31);

			/******************************* DOCUMENT_TYPE ********************************/
			String source_type = (String)inputData.get("Head-Source_type");
			String documentType = source_type;

			if(documentType!=null)
			{
				documentType = documenttypeMapping(documentType);
				outputFile.write(documentType);
			}
			outputFile.write(delimited31);


			/******************************* SOURCE_EDITOR ********************************/

			String editor = (String)inputData.get("Source-editor-FullName");

			if(editor!=null)
				outputFile.write(editor);
			outputFile.write(delimited31);

			/******************************* CORRESPONDENCE_PERSON *********************************/

			String person_initials		= (String)inputData.get("Correspondence_Person-Initials");
			String person_degree		= (String)inputData.get("Correspondence_Person-degree");
			String person_surname		= (String)inputData.get("Correspondence_Person-surname");
			String person_givename		= (String)inputData.get("Correspondence_Person-givename");
			String person_suffix		= (String)inputData.get("Correspondence_Person-suffix");
			String person_nameText		= (String)inputData.get("Correspondence_Person-nameText");
			StringBuffer person_fullname= new StringBuffer();

			if(person_surname != null)
			{
				if(person_givename!=null)
				{
					person_fullname.append(person_surname+", "+person_givename);
				}
				else if(person_initials!=null)
				{
					person_fullname.append(person_surname+", "+person_initials);
				}
				else
				{
					person_fullname.append(person_surname);
				}
			}

			if(person_fullname.length()>0)
				outputFile.write(person_fullname.toString());
			outputFile.write(delimited31);

			/******************************* CORRESPONDENCE_PERSON_INDEXNAME *********************************/
			String person_indexedName	= (String)inputData.get("Correspondence_Person-indexedName");

			if(person_indexedName != null)
				outputFile.write(person_indexedName);
			outputFile.write(delimited31);

			/******************************* CORRESPONDENCE_AFFILIATION *********************************/

			String correspondence_affiliation  = (String)inputData.get("Correspondence-Affiliation-Organization");
			if(correspondence_affiliation == null || correspondence_affiliation.length()<1)
			{
				correspondence_affiliation = (String)inputData.get("Correspondence-Affiliation-Text");
				//if(correspondence_affiliation!=null)
				//{
				//	outputFile_UpdateAff.write(accessNumber+delimited31+correspondence_affiliation+"\n");
				//}
			}
			//outputFile_UpdateAff.write("\n");
			if(correspondence_affiliation!=null)
			{
				outputFile.write(correspondence_affiliation);
			}
			outputFile.write(delimited31);

			/************************** CORRESPONDENCE_AFFILIATION_CITY **********************************/
			String correspondenceAffCity = (String)inputData.get("Correspondence-Affiliation-City");

			if(correspondenceAffCity==null)
			{
				correspondenceAffCity = (String)inputData.get("Correspondence-Affiliation-CityGroup");
				//if(correspondenceAffCity!=null)
				//{
					//outputFile_UpdateAff.write(accessNumber+delimited31+correspondenceAffCity+"\n");
				//}
			}

			if(correspondenceAffCity!=null)
			{
				outputFile.write(correspondenceAffCity);
			}


			outputFile.write(delimited31);

			/************************** CORRESPONDENCE_AFFILIATION_STATE *********************************/
			String correspondenceAffState = (String)inputData.get("Correspondence-Affiliation-State");

			if(correspondenceAffState!=null)
			{
				outputFile.write(correspondenceAffState);
			}

			outputFile.write(delimited31);

			/*************************** CORRESPONDENCE_AFFILIATION_COUNTRY ******************************/
			String correspondenceAffCountry = (String)inputData.get("Correspondence-Affiliation-Country");

			if(correspondenceAffCountry!=null && correspondenceAffCountry.length()>0)
			{
				//String countryFullName = countryLanguage.getIso3166Country(correspondenceAffCountry);
				if(correspondenceAffCountry!=null)
					outputFile.write(correspondenceAffCountry);
			}

			outputFile.write(delimited31);

			/*******************************  CORRESPONDENCE_ADDRESS_PART  ***********************************/
			String correspondence_aff_addresspart = (String)inputData.get("Correspondence-Affiliation-AddressPart");

			if(correspondence_aff_addresspart!=null)
				outputFile.write(correspondence_aff_addresspart);
			outputFile.write(delimited31);

			/*************************** CORRESPONDENCE_EMAIL ******************************/
			String correspondenceEmail = (String)inputData.get("Head-eaddress");

			if(correspondenceEmail!=null && correspondenceEmail.length()>0)
			{
				outputFile.write(correspondenceEmail);
			}
			outputFile.write(delimited31);



			/******************************* SOURCE_PUBLICATIONDATE ***********************************/
			String publicationDate 	= (String)inputData.get("Source-Publicationdate_dateText");
			String publicationMonth = (String)inputData.get("Source-Publicationdate_month");
			String publicationDay 	= (String)inputData.get("Source-Publicationdate_day");
			String publicationYear 	= (String)inputData.get("Source-Publicationdate_year");

			if(publicationDate!=null)
				outputFile.write(publicationDate);
			else
			{
				if(publicationMonth!=null&&publicationMonth.length()>0)
				{
					outputFile.write(monthConverted(publicationMonth)+" ");
				}

				if((publicationDay!=null) && (publicationMonth!=null))
				{
					outputFile.write(publicationDay+" ");
				}

				if(publicationYear!=null)
				{
					outputFile.write(publicationYear);
				}

			}

			outputFile.write(delimited31);

			/*******************************  SOURCE_TITLE  ***********************************/
			String sourceTitle = (String)inputData.get("Head-Source-sourcetitle");

			if(sourceTitle!=null)
			{
				if(sourceTitle.indexOf(delimited31)>-1)
				{
					sourceTitle = perl.substitute("s/\\"+delimited31+"/&#124/g", sourceTitle);
				}
				outputFile.write(sourceTitle);
			}
			outputFile.write(delimited31);


			/*******************************  ISSN_TYPE  ***********************************/
			String issn_type = (String)inputData.get("Head-Source-ISSN_TYPE");

			if(issn_type!=null)
				outputFile.write(issn_type);
			outputFile.write(delimited31);

			/*******************************  ISSN  ***********************************/
			String issn = (String)inputData.get("Head-Source-ISSN");
			String[] issnTypeArray  = null;
			String[] issnArray		= null;
			String printIssn		= null;
			String electronicIssn	= null;

			if(issn_type.indexOf(delimited30)>0)
			{
				issnTypeArray = issn_type.split(delimited30);
			}
			else
			{
				issnTypeArray = new String[1];
				issnTypeArray[0] = issn_type;
			}

			if(issn.indexOf(delimited30)>0)
			{
				issnArray = issn.split(delimited30);
			}
			else
			{
				issnArray = new String[1];
				issnArray[0] = issn;
			}

			for(int i=0;i<issnTypeArray.length;i++)
			{
				if(issnTypeArray[i]!=null && issnTypeArray[i].equalsIgnoreCase("electronic"))
				{
					electronicIssn = issnArray[i];
				}
				else
				{
					printIssn = issnArray[i];
				}
			}

			if(printIssn != null)
				outputFile.write(printIssn);
			outputFile.write(delimited31);

			/*******************************     E_ISSN      *************************************/
			if(electronicIssn!=null)
			{
					outputFile.write(electronicIssn);
			}
			outputFile.write(delimited31);

			/******************************* REFFERENT_COUNT ***********************************/
			String numberOfreferrences = (String)inputData.get("Tail-bibliographyRefcount");
			if(numberOfreferrences!=null)
				outputFile.write(numberOfreferrences);
			outputFile.write(delimited31);

			/*******************************  SOURCE_PAGERANGE  ***********************************/
			String firstPage = (String)inputData.get("Source-pagerange_first");
			String lastPage  = (String)inputData.get("Source-pagerange_last");

			if(firstPage!=null)
				outputFile.write(firstPage);
			if(firstPage!=null&&lastPage!=null)
				outputFile.write("-");
			if(lastPage!=null)
				outputFile.write(lastPage);

			outputFile.write(delimited31);

			/*******************************  SOURCE_PUBLICATIONYEAR  ***********************************/
			String publictionYear = (String)inputData.get("Head-Source-Publicationyear_First");
			if(publictionYear==null)
				publictionYear = (String)inputData.get("Head-Source-Publicationyear_Last");
			if(publictionYear!=null)
				outputFile.write(publictionYear);
			outputFile.write(delimited31);

			/*******************************  PII  ***********************************/
			String pii = (String)inputData.get("Item_info-pii");

			if(pii!=null)
				outputFile.write(pii);
			outputFile.write(delimited31);

			/*******************************  PUI  ***********************************/
			String pui = (String)inputData.get("Item_info-pui");

			if(pui!=null)
				outputFile.write(pui);
			outputFile.write(delimited31);

			/*******************************  DELIVERED_DATE  ***********************************/
			String d_year = (String)inputData.get("Process_info-dateDeliveredYear");
			String d_month = (String)inputData.get("Process_info-dateDeliveredMonth");
			String d_day = (String)inputData.get("Process_info-dateDeliveredDay");

			if(d_month!=null)
					outputFile.write(d_month+"-");
			if(d_day!=null)
					outputFile.write(d_day+"-");
			if(d_year!=null)
					outputFile.write(d_year);
			outputFile.write(delimited31);

			/*******************************  SORT_DATE  ***********************************/
			String s_year 	= (String)inputData.get("Process_info-dateSortYear");
			String s_month 	= (String)inputData.get("Process_info-dateSortMonth");
			String s_day 	= (String)inputData.get("Process_info-dateSortDay");

			if(s_month!=null)
					outputFile.write(s_month+"-");
			if(s_day!=null)
					outputFile.write(s_day+"-");
			if(s_year!=null)
					outputFile.write(s_year);
			outputFile.write(delimited31);

			/*******************************  CREATED_DATE  ***********************************/
			String c_year 	= (String)inputData.get("Item_info-History-dateCreatedYear");
			String c_month 	= (String)inputData.get("Item_info-History-dateCreatedMonth");
			String c_day 	= (String)inputData.get("Item_info-History-dateCreatedDay");

			if(c_month!=null)
					outputFile.write(c_month+"-");
			if(c_day!=null)
					outputFile.write(c_day+"-");
			if(c_year!=null)
					outputFile.write(c_year);
			outputFile.write(delimited31);

			/*******************************  COMPLETED_DATE  ***********************************/
			String completed_year 	= (String)inputData.get("Item_info-History-dateCompletedYear");
			String completed_month 	= (String)inputData.get("Item_info-History-dateCompletedMonth");
			String completed_day 	= (String)inputData.get("Item_info-History-dateCompletedDay");

			if(completed_month!=null)
					outputFile.write(completed_month+"-");
			if(completed_day!=null)
					outputFile.write(completed_day+"-");
			if(completed_year!=null)
					outputFile.write(completed_year);
			outputFile.write(delimited31);

			/*******************************  REVISED_DATE  ***********************************/
			String r_year 	= (String)inputData.get("Item_info-History-dateRevisedYear");
			String r_month 	= (String)inputData.get("Item_info-History-dateRevisedMonth");
			String r_day 	= (String)inputData.get("Item_info-History-dateRevisedDay");

			if(r_month!=null)
					outputFile.write(r_month+"-");
			if(r_day!=null)
					outputFile.write(r_day+"-");
			if(r_year!=null)
					outputFile.write(r_year);
			outputFile.write(delimited31);

			/*******************************  STATUS_TYPE  ***********************************/
			String s_type = (String)inputData.get("Process_info-statusType");

			if(s_type!=null)
					outputFile.write(s_type);
			outputFile.write(delimited31);

			/*******************************  STATUS_STATE  ***********************************/
			String s_state = (String)inputData.get("Process_info-statusState");

			if(s_state!=null)
					outputFile.write(s_state);
			outputFile.write(delimited31);

			/*******************************  STATUS_STAGE  ***********************************/
			String s_stage = (String)inputData.get("Process_info-statusStage");

			if(s_stage!=null)
					outputFile.write(s_stage);
			outputFile.write(delimited31);


			/*******************************  DBCOLLECTION  ***********************************/
			String dbCollection = (String)inputData.get("Item_info-dbCollection");

			if(dbCollection!=null)
				outputFile.write(dbCollection);
			outputFile.write(delimited31);


			/*******************************  CITATION_LANGUAGE  ***********************************/
			String citation_language = (String)inputData.get("Head-Citation_language_lang");

			if(citation_language!=null)
				outputFile.write(citation_language);
			outputFile.write(delimited31);

			/*******************************  ABSTRACT_LANGUAGE  ***********************************/
			String abstract_language = (String)inputData.get("Head-Abstract_language_lang");

			if(abstract_language!=null)
			{
				outputFile.write(abstract_language);
				//System.out.println("abstract_language= "+countryLanguage.getIso639Language(abstract_language));
			}
			outputFile.write(delimited31);


			/*******************************  CITATION_TITLE  ***********************************/
			String citationTitle = (String)inputData.get("Head-Citation_title-titletext");

			if(citationTitle==null)
				citationTitle = (String)inputData.get("Refd_itemcitation-Citation_title-titletext");

			if(citationTitle!=null)
			{
				if(citationTitle.indexOf(delimited31)>-1)
				{
					citationTitle = perl.substitute("s/\\"+delimited31+"/&#124/g", citationTitle);
				}
				outputFile.write(citationTitle);
			}
			outputFile.write(delimited31);

			/*******************************  TITLETEXT_LANGUAGE  ***********************************/
			String titletext_language = (String)inputData.get("Head-Citation_title-titletextLang");

			if(titletext_language!=null)
				outputFile.write(titletext_language);
			outputFile.write(delimited31);

			/*******************************  TITLETEXT_ORIGINAL  ***********************************/
			String titletext_original = (String)inputData.get("Head-Citation_title-titletext_Original");

			if(titletext_original!=null)
				outputFile.write(titletext_original);
			outputFile.write(delimited31);

			/*******************************  SOURCE_TYPE  ***********************************/
			//String source_type = (String)inputData.get("Head-Source_type");

			if(source_type!=null)
				outputFile.write(source_type);
			outputFile.write(delimited31);

			/*******************************  SOURCE_COUNTRY  ***********************************/
			String source_country = (String)inputData.get("Head-Source_country");

			if(source_country!=null)
			{
				//outputFile.write(countryLanguage.getIso3166Country(source_country));
				outputFile.write(source_country);
			}
			outputFile.write(delimited31);

			/*******************************  SOURCE_SRCID  ***********************************/
			String source_srcid = (String)inputData.get("Head-Source_srcid");

			if(source_srcid == null)
			{
				source_srcid = (String)inputData.get("Related_item_Source_srcid");
			}

			if(source_srcid!=null)
				outputFile.write(source_srcid);
			outputFile.write(delimited31);

			/*******************************  SOURCE_VOLUME  ***********************************/
			String volume = (String)inputData.get("Source-voliss_volume");

			if(volume!=null)
				outputFile.write(volume);

			outputFile.write(delimited31);


			/*******************************  LOAD_NUMBER  ***********************************/
			String loadingNumber = DataParser.loadNumber;

			if(loadingNumber!=null)
				outputFile.write(loadingNumber);
			outputFile.write(delimited31);

			/*******************************  DOI  ***********************************/
			String doi = (String)inputData.get("Item_info-doi");

			if(doi!=null)
				outputFile.write(doi);
			outputFile.write(delimited31);

			/*******************************  COPYRIGHT  ***********************************/
			String copyright = (String)inputData.get("Item_info-copyright");

			if(copyright!=null)
					outputFile.write(copyright);
			outputFile.write(delimited31);

			/*******************************  COPYRIGHT_TYPE  ***********************************/
			String copyright_type = (String)inputData.get("Item_info-copyright_type");

			if(copyright_type!=null)
				outputFile.write(copyright_type);
			outputFile.write(delimited31);

			/******************************* ISSUE_TITLE ***********************************/
			String monographTitle = (String)inputData.get("Head-Source-issueTitle");
			if(monographTitle==null)
				monographTitle = (String)inputData.get("Related_item-Source-issueTitle");
			if(monographTitle==null)
				monographTitle = (String)inputData.get("Conferenceinfo-confname");

			if(monographTitle!=null)
				outputFile.write(monographTitle);
			outputFile.write(delimited31);

			/******************************* CONFERENCE_DATE ***********************************/
			StringBuffer meetingDate = new StringBuffer();
			String meetingDateStartDateYear = (String)inputData.get("Conferenceinfo-startdate_year");
			String meetingDateStartDateMonth = (String)inputData.get("Conferenceinfo-startdate_month");
			String meetingDateStartDateDay = (String)inputData.get("Conferenceinfo-startdate_day");
			String meetingDateEndDateYear = (String)inputData.get("Conferenceinfo-enddate_year");
			String meetingDateEndDateMonth = (String)inputData.get("Conferenceinfo-enddate_month");
			String meetingDateEndDateDay = (String)inputData.get("Conferenceinfo-enddate_day");
			String meetingDateDateText   = (String)inputData.get("Conferenceinfo-date_text");
			if(meetingDateStartDateMonth!=null)
			{
				meetingDate.append(meetingDateStartDateMonth+"/");
			}
			if(meetingDateStartDateDay!=null)
			{
				meetingDate.append(meetingDateStartDateDay+"/");
			}
			if(meetingDateStartDateYear!=null)
			{
				meetingDate.append(meetingDateStartDateYear+"-");
			}
			if(meetingDateEndDateMonth!=null)
			{
				meetingDate.append(meetingDateEndDateMonth+"/");
			}
			if(meetingDateEndDateDay!=null)
			{
				meetingDate.append(meetingDateEndDateDay+"/");
			}
			if(meetingDateEndDateYear!=null)
			{
				meetingDate.append(meetingDateEndDateYear);
			}
			if(meetingDate.length()<1&&meetingDateDateText!=null)
			{
				meetingDate.append(meetingDateDateText);
			}

			if(meetingDate.length()>0)
				outputFile.write(meetingDate.toString());
			outputFile.write(delimited31);

			/******************************* CONFERENCE_CITY ***********************************/

			String meetingCity = (String)inputData.get("Conferenceinfo-city");

			if(meetingCity==null)
			{
				meetingCity = (String)inputData.get("Conferenceinfo-city_group");
			}
			if(meetingCity!=null)
				outputFile.write(meetingCity);
			outputFile.write(delimited31);

			/******************************* CONFERENCE_STATE ***********************************/

			String meetingState = (String)inputData.get("Conferenceinfo-state");
			if(meetingState!=null)
				outputFile.write(meetingState);
			outputFile.write(delimited31);

			/******************************* CONFERENCE_COUNTRY ***********************************/

			//String meetingCountryName = null;
			String meetingCountry = (String)inputData.get("Conferenceinfo-country");
			if(meetingCountry==null)
				meetingCountry = (String)inputData.get("Conferenceinfo-editoraddress");

			if(meetingCountry!=null)
			{
				//meetingCountryName = countryLanguage.getIso3166Country(meetingCountry);
				if(meetingCountry!=null)
					outputFile.write(meetingCountry);

			}
			outputFile.write(delimited31);

			/******************************* REPORT_NUMBER ***********************************/

			String reportNumber = (String)inputData.get("Additional_srcinfo-reportnumber");
			if(reportNumber!=null)
				outputFile.write(reportNumber);
			outputFile.write(delimited31);

			/******************************* PUBLISHER_NAME ***********************************/

			String publisherName = (String)inputData.get("Publisher_name");

			if(publisherName!=null)
					outputFile.write(publisherName);
			outputFile.write(delimited31);


			/******************************* PUBLISHER_CITY ***********************************/
			String publisherAddress = (String)inputData.get("Publisher_address");
			String publisherCity = null;
			String[] publisherAddressArray = null;

			if(publisherAddress!=null && publisherAddress.indexOf(",")>0)
			{
				publisherAddressArray 	= publisherAddress.split(",");
				if(publisherAddressArray.length>1)
				{
					publisherCity			= publisherAddressArray[1];
				}
			}
			else if(publisherAddress!=null)
			{
				publisherAddressArray 		= new String[1];
				publisherAddressArray[0] 	= publisherAddress;
				publisherCity				= publisherAddress;
			}


			if(publisherCity!=null)
					outputFile.write(publisherCity.trim());
			outputFile.write(delimited31);

			/******************************* PUBLISHER_STATE ***********************************/
			String publisherState = null;

			if(publisherAddressArray!=null && publisherAddressArray.length>0)
			{
				publisherState = publisherAddressArray[0];
			}
			else
			{
				publisherState = publisherAddress;
			}

			if(publisherState!=null)
				outputFile.write(publisherState.trim());
			outputFile.write(delimited31);

			/**************************** PUBLISHER_COUNTRY ***************************/

			String publisherCountry = null;

			if(publisherAddressArray!=null && publisherAddressArray.length>2)
			{
				publisherCountry = publisherAddressArray[2];
			}
			else
			{
				publisherCountry = publisherAddress;
			}

			if(publisherCountry!=null)
				outputFile.write(publisherCountry.trim());
			outputFile.write(delimited31);

			/******************************* PAGES ***********************************/

			String pages = (String)inputData.get("Source-pages");

			if(pages==null)
				pages = (String)inputData.get("Ref_info-pages");
			if(pages==null)
				pages = (String)inputData.get("Refd_itemcitation-pages");
			if(pages==null)
				pages = (String)inputData.get("Additional_srcinfo-pages");

			if(pages!=null)
				outputFile.write(pages);

			outputFile.write(delimited31);

			/**************************  ABBR_SOURCETITLE  ****************************/

			String sourceTitleAbbrev = (String)inputData.get("Head-Source-sourcetitle_abbrev");

			if(sourceTitleAbbrev==null)
				sourceTitleAbbrev = (String)inputData.get("Related_item-Source-sourcetitle_abbrev");

			if(sourceTitleAbbrev==null)
				sourceTitleAbbrev = (String)inputData.get("Additional_srcinfo-sourcetitle_abbrev");

			if(sourceTitleAbbrev!=null && sourceTitleAbbrev.length()>0)
			{
				if(sourceTitleAbbrev.indexOf(delimited31)>-1)
				{
					sourceTitleAbbrev = perl.substitute("s/\\"+delimited31+"/&#124/g", sourceTitleAbbrev);
				}
				outputFile.write(sourceTitleAbbrev);
			}
			outputFile.write(delimited31);

			/*************************  CONFERENCE_SPONSORS  *****************************/

			String confsponsors = (String)inputData.get("Conferenceinfo-confsponsors");

			if(confsponsors!=null)
			{
				outputFile.write(confsponsors);
			}
			outputFile.write(delimited31);

			/**********************************  TR  ***************************************/

			String treatmentType = "";

			if(treatmentType!=null)
				outputFile.write(treatmentType);
			outputFile.write(delimited31);


			/*******************************  TRANSLATED_TITLE ***********************************/

			String translatedTitle = (String)inputData.get("Head-Citation_title-translated_titletext");

			if(translatedTitle==null)
			{
				translatedTitle = (String)inputData.get("Related_item-Citation_title-translated_titletext");
			}

			if(translatedTitle!=null)
			{
				if(translatedTitle.indexOf(delimited31)>-1)
				{
					translatedTitle = perl.substitute("s/\\"+delimited31+"/&#124/g", translatedTitle);
				}
				outputFile.write(translatedTitle);
			}
			outputFile.write(delimited31);

			/*******************************  VOLUME_TITLE  ***********************************/
			String volumeTitle = (String)inputData.get("Head-Source-volumeTitle");

			if(volumeTitle==null)
				volumeTitle = (String)inputData.get("Related_item-Source-volumeTitle");

			if(volumeTitle!=null)
				outputFile.write(volumeTitle);
			outputFile.write(delimited31);

			/*******************************  CONFERENCE_LOCATION  ***********************************/
			StringBuffer meetinglocation = new StringBuffer();
			if(meetingCity!=null)
				meetinglocation.append(meetingCity+" ");
			if(meetingState!=null)
				meetinglocation.append(meetingState+" ");
			if(meetingCountry!=null)
				meetinglocation.append(meetingCountry);

			if(meetinglocation.length()>0)
				outputFile.write(meetinglocation.toString());
			outputFile.write(delimited31);

			/*****************************  ARTICLE_NUMBER  ****************************/
			String article_number = (String)inputData.get("Head-Source-article_number");

			if(article_number!=null)
				outputFile.write(article_number);
			outputFile.write(delimited31);

			/*****************************    PAGECOUNT     ****************************/
			String pagecount = (String)inputData.get("Source-pagecount");

			if(pagecount!=null)
				outputFile.write(pagecount);
			outputFile.write(delimited31);

			/*****************************   PROCPAGERANGE   ****************************/
			String procpagerange = (String)inputData.get("Conferenceinfo-procpagerange");

			if(procpagerange!=null)
					outputFile.write(procpagerange);
			outputFile.write(delimited31);

			/*****************************   FIGURE_INFORMATION   ****************************/
			String figureInformation = (String)inputData.get("Head-Figure_information");

			if(figureInformation!=null)
					outputFile.write(figureInformation);
			outputFile.write(delimited31);

			outputFile.write("\n");

			outputFile.flush();
			//outputFile_UpdateAff.flush();

		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception: "+e);
		}
	}

	public String getUpdate_FileName() throws IOException
	{
			String fileName=DataParser.fileName.getName();
			String rPath = null;
			String aPath = null;
			if(fileName.indexOf("/")>-1)
			{
				int sPosition = fileName.indexOf("/");
				fileName = "updateCor_CITYGROUP_"+fileName.substring(sPosition+1)+".out";
				System.out.println("updateFileName= "+fileName);
			}

			return fileName;
	}

	public String getFileName() throws IOException
	{
		String fileName=DataParser.fileName.getName();
		String rPath = null;
		String aPath = null;
		if(fileName.indexOf("/")>-1)
		{
			int sPosition = fileName.indexOf("/");
			fileName = fileName.substring(sPosition+1);
			System.out.println("fileName= "+fileName);
		}

		rPath= getPath();
		File newFile = new File(rPath);
		newFile.mkdirs();
		aPath = newFile.getAbsolutePath();
		System.out.println("apath:"+aPath);
		fileName = aPath+"/"+(fileName).replaceAll(".xml|.XML",".dat");
		System.out.println("FILENAME= "+fileName);

		return fileName;
	}

	private String getPath()
	{
		//Calendar rightNow = Calendar.getInstance();
		//String year = Integer.toString(rightNow.get(Calendar.YEAR));
		//String month = getMonthString(rightNow.get(Calendar.MONTH));
		//String day = getDayString(rightNow.get(Calendar.DAY_OF_MONTH));
		//String path = "data/"+year+month+day;
		String path = "data";
		return path;
	}

	private String getDayString(int day)
	{
		String d = Integer.toString(day);
		if(d.length() == 1)
		{
			d = "0"+d;
		}

		return d;
	}

	private String getMonthString(int month)
	{
		month++;
		String m = Integer.toString(month);
		if(m.length() == 1)
		{
			m="0"+m;
		}

		return m;
	}

	private String monthConverted(String month)
	{
		int intMonth =0;
		if(month!=null && month.length()>0)
		{
			try
			{
		 		intMonth = Integer.parseInt(month);
			}
			catch(Exception e)
			{
				intMonth = 0;
			}
		}
		switch(intMonth)
		{
			case 1:		month = "January";		break;
			case 2: 	month = "February"; 	break;
			case 3: 	month = "March"; 		break;
			case 4: 	month = "April"; 		break;
			case 5: 	month = "May"; 			break;
			case 6: 	month = "June"; 		break;
			case 7: 	month = "July"; 		break;
			case 8: 	month = "August"; 		break;
			case 9: 	month = "September"; 	break;
			case 10: 	month = "October"; 		break;
			case 11: 	month = "November"; 	break;
			case 12: 	month = "December"; 	break;
		}
		return month;

	}

	private String documenttypeMapping(String documentType)
	{

		String newDT = (String)dtMappings.get(documentType.toUpperCase());

		if(newDT == null)
		{
			newDT = documentType;
		}
		return newDT;
	}

}
