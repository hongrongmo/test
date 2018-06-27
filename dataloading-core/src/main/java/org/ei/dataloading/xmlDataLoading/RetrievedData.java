package org.ei.dataloading.xmlDataLoading;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.ei.common.*;

public class RetrievedData {
    String delimited30 = String.valueOf(Constants.AUDELIMITER);
    String delimited29 = String.valueOf(Constants.GROUPDELIMITER);
    String delimited02 = String.valueOf('2');

    public void setItem(Item itemObject, Hashtable<String, Object> dataTable) {
        try {

            Process_info processInfo = itemObject.getProcess_info();
            if (processInfo != null)
                setProcessInfo(processInfo, dataTable);

            Bibrecord bibrecord = itemObject.getBibrecord();
            if (bibrecord != null) {
                Item_info itemInfo = bibrecord.getItem_info();
                if (itemInfo != null)
                    setItemInfo(itemInfo, dataTable);

                Head head = bibrecord.getHead();
                if (head != null)
                    setHead(head, dataTable);

                Tail tail = bibrecord.getTail();
                if (tail != null) {
                    setTail(tail, dataTable);
                } else {
                    // System.out.println("tail is null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setProcessInfo(Process_info processInfo, Hashtable<String, Object> dataTable) {

        try {
            Date_delivered dateDelivered = processInfo.getDate_delivered();
            String dateDeliveredYear = dateDelivered.getDate_delivered_year();
            String dateDeliveredMonth = dateDelivered.getDate_delivered_month();
            String dateDeliveredDay = dateDelivered.getDate_delivered_day();
            Date_sort dateSort = processInfo.getDate_sort();
            String dateSortYear = dateSort.getDate_sort_year();
            String dateSortMonth = dateSort.getDate_sort_month();
            String dateSortDay = dateSort.getDate_sort_day();
            Status status = processInfo.getStatus();
            String statusType = status.getStatus_type();
            String statusState = status.getStatus_state();
            String statusStage = status.getStatus_stage();
            if (dateDelivered != null)
                dataTable.put("Process_info-dateDelivered", dateDelivered);
            if (dateDeliveredYear != null && dateDeliveredYear.length() > 0)
                dataTable.put("Process_info-dateDeliveredYear", dateDeliveredYear);
            if (dateDeliveredMonth != null && dateDeliveredMonth.length() > 0)
                dataTable.put("Process_info-dateDeliveredMonth", dateDeliveredMonth);
            if (dateDeliveredDay != null && dateDeliveredDay.length() > 0)
                dataTable.put("Process_info-dateDeliveredDay", dateDeliveredDay);
            if (dateSort != null)
                dataTable.put("Process_info-dateSort", dateSort);
            if (dateSortYear != null && dateSortYear.length() > 0)
                dataTable.put("Process_info-dateSortYear", dateSortYear);
            if (dateSortMonth != null && dateSortMonth.length() > 0)
                dataTable.put("Process_info-dateSortMonth", dateSortMonth);
            if (dateSortDay != null && dateSortDay.length() > 0)
                dataTable.put("Process_info-dateSortDay", dateSortDay);
            if (status != null)
                dataTable.put("Process_info-status", status);
            if (statusType != null && statusType.length() > 0)
                dataTable.put("Process_info-statusType", statusType);
            if (statusState != null && statusState.length() > 0)
                dataTable.put("Process_info-statusState", statusState);
            if (statusStage != null && statusStage.length() > 0)
                dataTable.put("Process_info-statusStage", statusStage);
        } catch (Exception e) {
            System.out.println(" Exception from ProcessInfo" + e);
        }
    }

    void setItemInfo(Item_info itemInfo, Hashtable<String, Object> dataTable) {

        Copyright copyrights = itemInfo.getCopyright();
        String copyrightContent = copyrights.getCopyright();
        String copyrightType = copyrights.getCopyright_type();
        String pui = null;
        String wtaID = null;
        String chemID = null;
        String besteinID = null;
        String embaseID = null;
        String embioID = null;
        String cabsID = null;
        String medlID = null;
        String pchID = null;

        List<?> dbCollectionList = itemInfo.getDbcollection();
        StringBuffer dbCollection = new StringBuffer();

        for (int i = 0; i < dbCollectionList.size(); i++) {
            Dbcollection dbCollections = (Dbcollection) dbCollectionList.get(i);
            dbCollection.append((String) dbCollections.getDbcollection());
            if (i != dbCollectionList.size() - 1) {
                dbCollection.append(delimited30);
            }
        }
        dataTable.put("Item_info-dbCollection", dbCollection.toString());

        Itemidlist itemidlist = itemInfo.getItemidlist();
        List<?> itemids = itemidlist.getItemids();

        StringBuffer itemidType = new StringBuffer();
        StringBuffer item_id = new StringBuffer();
        for (int i = 0; i < itemids.size(); i++) {
            Itemid itemid = (Itemid) itemids.get(i);

            // Special for CPX DATABASE
            if ((itemid.getItemid_idtype()).equals(DataParser.preferedDatabase)) {
                itemidType.append(itemid.getItemid_idtype());
                item_id.append(itemid.getItemid());
            } else if ((itemid.getItemid_idtype()).equals("PUI")) {
                pui = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("WTA")) {
                wtaID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("CHEM")) {
                chemID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("BSTEIN")) {
                besteinID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("EMBASEID")) {
                embaseID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("EMBIO")) {
                embioID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("CABS")) {
                cabsID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("MEDL")) {
                medlID = itemid.getItemid();
            } else if ((itemid.getItemid_idtype()).equals("PCH")) {
                pchID = itemid.getItemid();
            }

        }

        String pii = itemidlist.getPii();
        String doi = itemidlist.getDoi();

        History history = itemInfo.getHistory();
        setHistory(history, dataTable);

        dataTable.put("Item_info-copyright", copyrightContent);
        dataTable.put("Item_info-copyright_type", copyrightType);
        dataTable.put("Item_info-itemid_type", itemidType.toString());
        dataTable.put("Item_info-item_id", item_id.toString());
        if (pii != null)
            dataTable.put("Item_info-pii", pii);
        if (doi != null)
            dataTable.put("Item_info-doi", doi);
        if (pui != null)
            dataTable.put("Item_info-pui", pui);
        if (wtaID != null)
            dataTable.put("Item_info-wtaID", wtaID);
        if (chemID != null)
            dataTable.put("Item_info-chemID", chemID);
        if (besteinID != null)
            dataTable.put("Item_info-besteinID", besteinID);
        if (embaseID != null)
            dataTable.put("Item_info-embaseID", embaseID);
        if (embioID != null)
            dataTable.put("Item_info-embioID", embioID);
        if (cabsID != null)
            dataTable.put("Item_info-cabsID", cabsID);
        if (medlID != null)
            dataTable.put("Item_info-medlID", medlID);
        if (pchID != null)
            dataTable.put("Item_info-pchID", pchID);

    }

    void setHead(Head head, Hashtable<String, Object> dataTable) {
        try {

            Citation_info citationInfo = head.getCitation_info();
            if (citationInfo != null) {
                setCitationInfo(citationInfo, dataTable);
            }
            StringBuffer etAl = new StringBuffer();
            StringBuffer indexed_name = new StringBuffer();
            StringBuffer text = new StringBuffer();

            List<?> relatedItems = head.getRelated_items();
            for (int i = 0; i < relatedItems.size(); i++) {
                Related_item relatedItem = (Related_item) relatedItems.get(i);
                setRelatedItem(relatedItem, dataTable);
            }

            Citation_title citationTitle = head.getCitation_title();
            if (citationTitle != null) {
                setCitationTitle(citationTitle, dataTable, "Head");
            }

            List<?> authorGroups = head.getAuthor_groups();
            if (authorGroups != null) {
                int authorGroupID = 1;
                for (int i = 0; i < authorGroups.size(); i++) {
                    authorGroupID = i + 1;
                    Author_group authorGroup = (Author_group) authorGroups.get(i);
                    etAl.append(authorGroup.getEt_Al());
                    List<?> authors = authorGroup.getAuthors();
                    setAuthors(authors, dataTable, "Head", authorGroupID);

                    List<?> collaborations = authorGroup.getCollaborations();

                    for (int j = 0; j < collaborations.size(); j++) {
                        Collaboration collaboration = (Collaboration) collaborations.get(j);
                        indexed_name.append(collaboration.getIndexed_Name());
                        text.append(collaboration.getText());
                        if (j != (collaborations.size() - 1)) {
                            indexed_name.append(delimited30);
                            text.append(delimited30);
                        }
                    }

                    if (i != authorGroups.size()) {
                        indexed_name.append("&&");
                        text.append("&&");
                    }

                    Affiliation affiliation = authorGroup.getAffiliation();
                    if (affiliation != null) {
                        setAffiliation(affiliation, dataTable, "Head", authorGroupID);
                    }
                }
            }

            Correspondence correspondence = head.getCorrespondence();
            if (correspondence != null)
                setCorrespondence(correspondence, dataTable);

            Abstracts abstracts = head.getAbstracts();
            if (abstracts != null)
                setAbstracts(abstracts, dataTable);

            Source source = head.getSource();
            if (source != null)
                setSource(source, dataTable, "Head");

            Enhancement enhancement = head.getEnhancement();
            if (enhancement != null)
                setEnhancement(enhancement, dataTable);

            if (indexed_name != null)
                dataTable.put("Head-Collaboration-indexed_name", indexed_name);
            if (text != null)
                dataTable.put("Head-Collaboration-text", text);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setCorrespondence(Correspondence correspondence, Hashtable<String, Object> dataTable) {
        Person person = correspondence.getPerson();
        if (person != null) {
            setPerson(person, dataTable, "Correspondence");
        }
        Affiliation affiliation = correspondence.getAffiliation();
        if (affiliation != null) {
            setAffiliation(affiliation, dataTable, "Correspondence", 1);
        }
        String eAddress = correspondence.getE_address();
        if (eAddress != null)
            dataTable.put("Head-eaddress", eAddress);
    }

    void setPerson(Person person, Hashtable<String, Object> dataTable, String name) {
        String initials = person.getInitials();
        String indexedName = person.getIndexed_name();
        String degrees = person.getDegrees();
        String surname = person.getSurname();
        String givenName = person.getGiven_name();
        String suffix = person.getSuffix();
        String nameText = person.getNametext();

        if (initials != null)
            dataTable.put(name + "_Person-Initials", initials);
        if (indexedName != null)
            dataTable.put(name + "_Person-indexedName", indexedName);
        if (degrees != null)
            dataTable.put(name + "_Person-degree", degrees);
        if (surname != null)
            dataTable.put(name + "_Person-surname", surname);
        if (givenName != null)
            dataTable.put(name + "_Person-givename", givenName);
        if (suffix != null)
            dataTable.put(name + "_Person-suffix", suffix);
        if (nameText != null)
            dataTable.put(name + "_Person-nameText", nameText);

    }

    void setCitationInfo(Citation_info citationInfo, Hashtable<String, Object> dataTable) {
        List<?> citationTypes = citationInfo.getCitation_type();
        List<?> citationLanguages = citationInfo.getCitation_language();
        List<?> abstractLanguages = citationInfo.getAbstract_language();
        List<?> publicationNotes = citationInfo.getPublication_notes();
        List<?> mediums = citationInfo.getMediums();

        StringBuffer citationTypeCode = new StringBuffer();
        StringBuffer citationLanguageLang = new StringBuffer();
        StringBuffer abstractLanguageLang = new StringBuffer();
        StringBuffer publicationNote = new StringBuffer();
        StringBuffer publicationNotesType = new StringBuffer();
        StringBuffer medium = new StringBuffer();
        StringBuffer mediumCovered = new StringBuffer();
        String authorKeywords = citationInfo.getAuthor_keywords();
        String figureInformation = citationInfo.getFigure_information();
        String price = citationInfo.getPrice();
        String documentDelivery = citationInfo.getDocument_delivery();
        String degree = citationInfo.getDegree();

        for (int i = 0; i < citationTypes.size(); i++) {
            Citation_type citationTypeObject = (Citation_type) citationTypes.get(i);
            String citationTypeCodeString = citationTypeObject.getCitation_type_code();
            citationTypeCode.append(citationTypeCodeString);
            if (i != (citationTypes.size() - 1)) {
                citationTypeCode.append(delimited30);
            }
        }

        for (int i = 0; i < citationLanguages.size(); i++) {
            Citation_language citationLanguageObject = (Citation_language) citationLanguages.get(i);
            String citationLanguagelangString = citationLanguageObject.getCitation_language_lang();
            citationLanguageLang.append(citationLanguagelangString);
            if (i != (citationLanguages.size() - 1)) {
                citationLanguageLang.append(delimited30);
            }
        }

        for (int i = 0; i < abstractLanguages.size(); i++) {
            Abstract_language abstractLanguageObject = (Abstract_language) abstractLanguages.get(i);
            String abstractLanguagelangString = abstractLanguageObject.getAbstract_language_lang();
            abstractLanguageLang.append(abstractLanguagelangString);
            if (i != (abstractLanguages.size() - 1)) {
                abstractLanguageLang.append(delimited30);
            }
        }

        for (int i = 0; i < publicationNotes.size(); i++) {
            Publication_notes publicationNoteObject = (Publication_notes) publicationNotes.get(i);
            publicationNote.append(publicationNoteObject.getPublication_notes());
            publicationNotesType.append(publicationNoteObject.getPublication_notes_type());
            if (i != (publicationNotes.size() - 1)) {
                publicationNote.append(delimited30);
                publicationNotesType.append(delimited30);
            }
        }

        for (int i = 0; i < mediums.size(); i++) {
            Medium mediumObject = (Medium) mediums.get(i);
            medium.append(mediumObject.getMedium());
            mediumCovered.append(mediumObject.getMedium_covered());
            if (i != (mediums.size() - 1)) {
                medium.append(delimited30);
                mediumCovered.append(delimited30);
            }
        }

        dataTable.put("Head-Citation_type_code", citationTypeCode.toString());
        dataTable.put("Head-Citation_language_lang", citationLanguageLang.toString());
        dataTable.put("Head-Abstract_language_lang", abstractLanguageLang.toString());
        dataTable.put("Head-Publication_Note", publicationNote.toString());
        dataTable.put("Head-Publication_Notes_Type", publicationNotesType.toString());
        dataTable.put("Head-Medium", medium.toString());
        dataTable.put("Head-Medium_Covered", mediumCovered.toString());
        if (authorKeywords != null)
            dataTable.put("Head-Author_Keywords", authorKeywords);
        if (figureInformation != null)
            dataTable.put("Head-Figure_information", figureInformation);
        if (price != null)
            dataTable.put("Head-Price", price);
        if (documentDelivery != null)
            dataTable.put("Head-Document_delivery", documentDelivery);
        if (degree != null)
            dataTable.put("Head-Degree", degree);
    }

    void setRelatedItem(Related_item relatedItem, Hashtable<String, Object> dataTable) {
        String pii = relatedItem.getPii();
        String doi = relatedItem.getDoi();
        Citation_info citation_info = relatedItem.getCitation_info();
        List<?> contributorGroups = relatedItem.getContributor_groups();
        for (int i = 0; i < contributorGroups.size(); i++) {
            Contributor_group contributorGroupObject = (Contributor_group) contributorGroups.get(i);
            setContributorGroup(contributorGroupObject, dataTable, "Related_item");
        }
        Source source = relatedItem.getSource();
        setSource(source, dataTable, "Related_item");
        if (pii != null)
            dataTable.put("Head-pii", pii);
        if (doi != null)
            dataTable.put("Head-doi", doi);

    }

    void setAbstracts(Abstracts abstracts, Hashtable<String, Object> dataTable) {
        Abstract _abstract = abstracts.getAbstract();
        String publishercopyright = _abstract.getPublishercopyright();
        String para = _abstract.getPara();
        String original = _abstract.getAbstract_original();
        String absSource = _abstract.getAbstract_source();

        if (publishercopyright != null)
            dataTable.put("Abstract-publishercopyright", publishercopyright);
        if (para != null)
            dataTable.put("Abstract-para", para);
        if (original != null)
            dataTable.put("Abstract-original", original);
        if (absSource != null)
            dataTable.put("Abstract-absSource", absSource);
    }

    void setSource(Source source, Hashtable<String, Object> dataTable, String name) {
        Sourcetitle sourcetitleObject = source.getSourcetitle();
        String sourcetitle = null;
        if (sourcetitleObject != null) {
            sourcetitle = sourcetitleObject.getSourcetitle();
        }
        String sourcetitle_abbrev = source.getSourcetitle_abbrev();
        String volumeTitle = source.getVolumetitle();
        String issueTitle = source.getIssuetitle();
        StringBuffer translatedSourcetitle = new StringBuffer();
        StringBuffer translatedSourcetitleLang = new StringBuffer();
        StringBuffer issn = new StringBuffer();
        StringBuffer issnType = new StringBuffer();
        StringBuffer isbn = new StringBuffer();
        StringBuffer isbnType = new StringBuffer();
        String codencode = source.getCodencode();
        String bibText = source.getBib_text();
        String publicationyear = null;
        String publicationyearFirst = null;
        String publicationyearLast = null;
        String editorsComplete = null;
        String source_type = source.getSource_type();
        String source_country = source.getSource_country();
        String source_srcid = source.getSource_srcid();
        String article_number = source.getArticle_number();

        List<?> translatedSourcetitles = source.getTranslated_sourcetitle();
        for (int i = 0; i < translatedSourcetitles.size(); i++) {
            Translated_sourcetitle translatedSourcetitleObject = (Translated_sourcetitle) translatedSourcetitles.get(i);
            translatedSourcetitle.append(translatedSourcetitleObject.getTranslated_sourcetitle() + "#");
            translatedSourcetitleLang.append(translatedSourcetitleObject.getTranslated_sourcetitle_lang() + "#");
        }

        List<?> issns = source.getIssns();
        for (int i = 0; i < issns.size(); i++) {
            Issn issnObject = (Issn) issns.get(i);
            issn.append(issnObject.getIssn());
            issnType.append(issnObject.getIssn_type());
            if (i != (issns.size() - 1)) {
                issn.append(delimited30);
                issnType.append(delimited30);
            }
        }

        List<?> isbns = source.getIsbns();
        for (int i = 0; i < isbns.size(); i++) {
            Isbn isbnObject = (Isbn) isbns.get(i);
            isbn.append(isbnObject.getIsbn());
            isbnType.append(isbnObject.getIsbn_type());
            if (i != (isbns.size() - 1)) {
                isbn.append(delimited30);
                isbnType.append(delimited30);
            }
        }

        Publicationyear publicationyearObject = source.getPublicationyear();
        if (publicationyearObject != null) {
            publicationyear = publicationyearObject.getPublicationyear();
            publicationyearFirst = publicationyearObject.getPublicationyear_first();
            publicationyearLast = publicationyearObject.getPublicationyear_last();
        }

        List<?> contributorGroups = source.getContributor_groups();
        for (int i = 0; i < contributorGroups.size(); i++) {
            Contributor_group contributorGroup = (Contributor_group) contributorGroups.get(i);
            setContributorGroup(contributorGroup, dataTable, "Source");
        }

        Publicationdate publicationdate = source.getPublicationdate();
        if (publicationdate != null)
            setPublicationdate(publicationdate, dataTable, "Source");

        List<?> websites = source.getWebsites();
        for (int i = 0; i < websites.size(); i++) {
            Website websiteObject = (Website) websites.get(i);
            setWebsite(websiteObject, dataTable, "Source");
        }

        Volisspag volisspag = source.getVolisspag();
        if (volisspag != null)
            setVolisspag(volisspag, dataTable, "Source");

        Editors editors = source.getEditors();
        if (editors != null) {
            editorsComplete = editors.getEditors_complete();
            List<?> editor = editors.getEditor();
            setEditor(editor, dataTable, "Source");
        }

        List<?> publishers = source.getPublishers();
        setPublishers(publishers, dataTable);

        Additional_srcinfo additionalSrcinfo = source.getAdditional_srcinfo();
        if (additionalSrcinfo != null)
            setAdditionalSrcinfo(additionalSrcinfo, dataTable);

        if (article_number != null)
            dataTable.put(name + "-Source-article_number", article_number);
        if (sourcetitle != null)
            dataTable.put(name + "-Source-sourcetitle", sourcetitle);
        if (sourcetitle_abbrev != null)
            dataTable.put(name + "-Source-sourcetitle_abbrev", sourcetitle_abbrev);
        if (volumeTitle != null)
            dataTable.put(name + "-Source-volumeTitle", volumeTitle);
        if (issueTitle != null)
            dataTable.put(name + "-Source-issueTitle", issueTitle);
        if (translatedSourcetitle != null)
            dataTable.put(name + "-Source-TranslatedSourcetitle", translatedSourcetitle.toString());
        if (translatedSourcetitleLang != null)
            dataTable.put(name + "-Source-TranslatedSourcetitle_Lang", translatedSourcetitleLang.toString());

        dataTable.put(name + "-Source-ISBN", isbn.toString());

        dataTable.put(name + "-Source-ISBN_TYPE", isbnType.toString());

        dataTable.put(name + "-Source-ISSN", issn.toString());

        dataTable.put(name + "-Source-ISSN_TYPE", issnType.toString());

        if (publicationyear != null)
            dataTable.put(name + "-Source-Publicationyear", publicationyear);
        if (publicationyearFirst != null)
            dataTable.put(name + "-Source-Publicationyear_First", publicationyearFirst);
        if (publicationyearLast != null)
            dataTable.put(name + "-Source-Publicationyear_Last", publicationyearLast);
        if (bibText != null)
            dataTable.put(name + "-Source-bibText", bibText);
        if (codencode != null)
            dataTable.put(name + "-Source-Codencode", codencode);
        if (editorsComplete != null)
            dataTable.put(name + "-Source-editors_complete", editorsComplete);
        if (source_type != null)
            dataTable.put(name + "-Source_type", source_type);
        if (source_country != null)
            dataTable.put(name + "-Source_country", source_country);
        if (source_srcid != null)
            dataTable.put(name + "-Source_srcid", source_srcid);

    }

    void setPublishers(List<?> publishers, Hashtable<String, Object> dataTable) {
        StringBuffer publishername = new StringBuffer();
        StringBuffer publisheraddress = new StringBuffer();
        StringBuffer publishereaddress = new StringBuffer();
        for (int i = 0; i < publishers.size(); i++) {
            Publisher publisherObject = (Publisher) publishers.get(i);
            if (publisherObject.getPublishername() != null) {
                publishername.append(publisherObject.getPublishername());
            }
            if (publisherObject.getPublisheraddress() != null) {
                publisheraddress.append(publisherObject.getPublisheraddress());
            }
            if (publisherObject.getE_address() != null) {
                publishereaddress.append(publisherObject.getE_address());
            }

            if (i != (publishers.size() - 1)) {
                if (publisherObject.getPublishername() != null) {
                    publishername.append(delimited30);
                }
                if (publisherObject.getPublisheraddress() != null) {
                    publisheraddress.append(delimited30);
                }
                if (publisherObject.getE_address() != null) {
                    publishereaddress.append(delimited30);
                }
            }
        }

        if (publishername.length() > 0) {
            dataTable.put("Publisher_name", publishername.toString());
        }
        if (publisheraddress.length() > 0) {
            dataTable.put("Publisher_address", publisheraddress.toString());
        }
        if (publishereaddress.length() > 0) {
            dataTable.put("Publisher_E-address", publishereaddress.toString());
        }
    }

    void setAffiliation(Affiliation affiliation, Hashtable<String, Object> dataTable, String name, int id) {
        String affText = affiliation.getText();
        List<?> affOrganizations = affiliation.getOrganizations();
        StringBuffer affOrganization = new StringBuffer();
        String affAddressPart = affiliation.getAddress_part();
        String affCityGroup = affiliation.getCity_group();
        String affCity = affiliation.getCity();
        String affState = affiliation.getState();
        String affPostalCode = affiliation.getPostal_code();
        String affCountry = affiliation.getAffiliation_country();

        if (affText != null)
            dataTable.put(name + "-Affiliation-Text", "1" + delimited29 + affText);

        for (int i = 0; i < affOrganizations.size(); i++) {
            affOrganization.append((String) affOrganizations.get(i));
            if (i != (affOrganizations.size() - 1)) {
                affOrganization.append(",");
            }
        }

        if (affOrganization.length() > 0) {
            if (dataTable.get(name + "-Affiliation-Organization") == null) {
                dataTable.put(name + "-Affiliation-Organization", id + delimited29 + affOrganization.toString());
            } else {
                dataTable.put(name + "-Affiliation-Organization", dataTable.get(name + "-Affiliation-Organization") + delimited02 + id + delimited29
                    + affOrganization.toString());
            }
        }

        if (affAddressPart != null) {
            if (dataTable.get(name + "-Affiliation-AddressPart") == null) {
                dataTable.put(name + "-Affiliation-AddressPart", id + delimited29 + affAddressPart);
            } else {
                dataTable.put(name + "-Affiliation-AddressPart", dataTable.get(name + "-Affiliation-AddressPart") + delimited02 + id + delimited29
                    + affAddressPart);
            }
        }

        if (affCityGroup != null) {
            if (dataTable.get(name + "-Affiliation-CityGroup") == null) {
                dataTable.put(name + "-Affiliation-CityGroup", id + delimited29 + affCityGroup);
            } else {
                dataTable.put(name + "-Affiliation-CityGroup", dataTable.get(name + "-Affiliation-CityGroup") + delimited02 + id + delimited29 + affCityGroup);
            }
        }

        if (affCity != null) {
            if (dataTable.get(name + "-Affiliation-City") == null) {
                dataTable.put(name + "-Affiliation-City", id + delimited29 + affCity);
            } else {
                dataTable.put(name + "-Affiliation-City", dataTable.get(name + "-Affiliation-City") + delimited02 + id + delimited29 + affCity);
            }
        }

        if (affState != null) {
            if (dataTable.get(name + "-Affiliation-State") == null) {
                dataTable.put(name + "-Affiliation-State", id + delimited29 + affState);
            } else {
                dataTable.put(name + "-Affiliation-State", dataTable.get(name + "-Affiliation-State") + delimited02 + id + delimited29 + affState);
            }
        }

        if (affPostalCode != null) {
            if (dataTable.get(name + "-Affiliation-PostalCode") == null) {
                dataTable.put(name + "-Affiliation-PostalCode", id + delimited29 + affPostalCode);
            } else {
                dataTable.put(name + "-Affiliation-PostalCode", dataTable.get(name + "-Affiliation-PostalCode") + delimited02 + id + delimited29
                    + affPostalCode);
            }
        }

        if (affCountry != null) {
            if (dataTable.get(name + "-Affiliation-Country") == null) {
                dataTable.put(name + "-Affiliation-Country", id + delimited29 + affCountry);
            } else {
                dataTable.put(name + "-Affiliation-Country", dataTable.get(name + "-Affiliation-Country") + delimited02 + id + delimited29 + affCountry);
            }
        }
    }

    void setEditor(List<?> editors, Hashtable<String, Object> dataTable, String name) {
        StringBuffer editorRole = new StringBuffer();
        StringBuffer editorType = new StringBuffer();
        String initials = null;
        StringBuffer indexed_Name = new StringBuffer();
        String degrees = null;
        String surname = null;
        String givenName = null;
        String suffix = null;
        String nametext = null;
        StringBuffer fullName = new StringBuffer();

        for (int i = 0; i < editors.size(); i++) {
            Editor editor = (Editor) editors.get(i);
            if (editor != null) {
                if (editor.getEditor_role() != null)
                    editorRole.append(editor.getEditor_role());
                if (editor.getEditor_type() != null)
                    editorType.append(editor.getEditor_type());
                initials = editor.getInitials();
                if (editor.getIndexed_name() != null)
                    indexed_Name.append(editor.getIndexed_name());
                degrees = editor.getDegrees();
                surname = editor.getSurname();
                givenName = editor.getGiven_name();
                suffix = editor.getSuffix();
                nametext = editor.getNametext();

                if (surname != null) {
                    if (givenName != null)
                        fullName.append(surname + ", " + givenName);
                    else if (initials != null)
                        fullName.append(surname + ", " + initials);
                } else if (nametext != null) {
                    fullName.append(nametext);
                }

                if (i != (editors.size() - 1)) {
                    editorRole.append(delimited30);
                    editorType.append(delimited30);
                    indexed_Name.append(delimited30);
                    fullName.append(delimited30);
                }
            }
        }

        if (editorRole.length() > 0)
            dataTable.put(name + "-editor-Role", editorRole.toString());
        if (editorType.length() > 0)
            dataTable.put(name + "-editor-Type", editorType.toString());
        if (indexed_Name.length() > 0)
            dataTable.put(name + "-editor-Indexed_Name", indexed_Name.toString());
        if (fullName.length() > 0)
            dataTable.put(name + "-editor-FullName", fullName.toString());

    }

    void setWebsite(Website website, Hashtable<String, Object> dataTable, String name) {
        String websitename = website.getWebsitename();
        String emailAddress = website.getE_address();
        String websiteType = website.getWebsite_type();
        if (websitename != null)
            dataTable.put(name + "-Website_name", websitename);
        if (emailAddress != null)
            dataTable.put(name + "-Website_emailAddress", emailAddress);
        if (websiteType != null)
            dataTable.put(name + "-website_Type", websiteType);
        if (name.indexOf("more") > 0) {
            dataTable.put(name + "-Website_name", dataTable.get(name + "Website_name") + "&&");
            dataTable.put(name + "-Website_emailAddress", dataTable.get(name + "Website_emailAddress") + "&&");
            dataTable.put(name + "-website_Type", dataTable.get(name + "website_Type") + "&&");
        }
    }

    void setAdditionalSrcinfo(Additional_srcinfo additionalSrcinfo, Hashtable<String, Object> dataTable) {
        String sourcetitle = null;
        String sourcetitleAbbrev = null;
        String issn = null;
        String issnType = null;
        String publicationyear = null;
        String publicationyear_first = null;
        String publicationyear_last = null;
        String voliss = null;
        String volissVolume = null;
        String volissIssue = null;
        Secondaryjournal secondaryjournal = additionalSrcinfo.getSecondaryjournal();
        if (secondaryjournal != null) {
            sourcetitle = secondaryjournal.getSourcetitle();
            sourcetitleAbbrev = secondaryjournal.getSourcetitle_abbrev();
            Issn issnObject = secondaryjournal.getIssn();
            if (issnObject != null) {
                issn = issnObject.getIssn();
                issnType = issnObject.getIssn_type();
            }
            Publicationyear publicationyearObject = secondaryjournal.getPublicationyear();
            if (publicationyearObject != null) {
                publicationyear = publicationyearObject.getPublicationyear();
                publicationyear_first = publicationyearObject.getPublicationyear_first();
                publicationyear_last = publicationyearObject.getPublicationyear_last();
            }
            Voliss volissObject = secondaryjournal.getVoliss();
            if (volissObject != null) {
                voliss = volissObject.getVoliss();
                volissVolume = volissObject.getVoliss_volume();
                volissIssue = volissObject.getVoliss_issue();
            }

            Publicationdate publicationdate = secondaryjournal.getPublicationdate();
            setPublicationdate(publicationdate, dataTable, "Additional_srcinfo");
        }

        Conferenceinfo conferenceinfo = additionalSrcinfo.getConferenceinfo();
        if (conferenceinfo != null)
            setConferenceinfo(conferenceinfo, dataTable);

        Reportinfo reportinfo = additionalSrcinfo.getReportinfo();
        String reportnumber = null;
        if (reportinfo != null)
            reportnumber = reportinfo.getReportnumber();

        Toc toc = additionalSrcinfo.getToc();
        if (toc != null)
            setToc(toc, dataTable);

        if (issn != null)
            dataTable.put("Additional_srcinfo-issn", issn);
        if (issnType != null)
            dataTable.put("Additional_srcinfo-issnType", issnType);
        if (publicationyear != null)
            dataTable.put("Additional_srcinfo-publicationyear", publicationyear);
        if (publicationyear_first != null)
            dataTable.put("Additional_srcinfo-publicationyear_first", publicationyear_first);
        if (publicationyear_last != null)
            dataTable.put("Additional_srcinfo-publicationyear_last", publicationyear_last);
        if (voliss != null)
            dataTable.put("Additional_srcinfo-voliss", voliss);
        if (volissVolume != null)
            dataTable.put("Additional_srcinfo-volissVolume", volissVolume);
        if (volissIssue != null)
            dataTable.put("Additional_srcinfo-volissIssue", volissIssue);
        if (reportnumber != null)
            dataTable.put("Additional_srcinfo-reportnumber", reportnumber);
        if (sourcetitle != null)
            dataTable.put("Additional_srcinfo-sourcetitle", sourcetitle);
        if (sourcetitleAbbrev != null)
            dataTable.put("Additional_srcinfo-sourcetitleAbbrev", sourcetitleAbbrev);

    }

    void setConferenceinfo(Conferenceinfo conferenceinfo, Hashtable<String, Object> dataTable) {
        Confevent confevent = conferenceinfo.getConfevent();
        String confname = null;
        String confnumber = null;
        String conflocationCountry = null;
        String confcatnumber = null;
        String confcode = null;
        Confsponsors confsponsorsObject = null;
        List<?> confsponsors = null;
        String confsponsorsComplete = null;
        StringBuffer confsponsor = new StringBuffer();

        if (confevent != null) {
            confname = confevent.getConfname();
            confnumber = confevent.getConfnumber();
            conflocationCountry = confevent.getConflocation_country();
            confcatnumber = confevent.getConfcatnumber();
            confcode = confevent.getConfcode();
            confsponsorsObject = confevent.getConfsponsors();
            if (confsponsorsObject != null) {
                confsponsors = confsponsorsObject.getConfsponsors();
                confsponsorsComplete = confsponsorsObject.getConfsponsors_complete();
                for (int i = 0; i < confsponsors.size(); i++) {
                    Confsponsor confsponsorObject = (Confsponsor) confsponsors.get(i);
                    confsponsor.append(confsponsorObject.getConfsponsor());
                    if (i != (confsponsors.size() - 1)) {
                        confsponsor.append(delimited30);
                    }
                }

            }

            Conflocation conflocation = confevent.getConflocation();
            if (conflocation != null)
                setConflocation(conflocation, dataTable);

            Confdate confdate = confevent.getConfdate();
            if (confdate != null)
                setConfdate(confdate, dataTable);
        }

        Confpublication confpublication = conferenceinfo.getConfpublication();
        if (confpublication != null)
            setConfpublication(confpublication, dataTable);

        if (confname != null)
            dataTable.put("Conferenceinfo-confname", confname);
        if (confnumber != null)
            dataTable.put("Conferenceinfo-confnumber", confnumber);
        if (conflocationCountry != null)
            dataTable.put("Conferenceinfo-conflocationCountry", conflocationCountry);
        if (confcatnumber != null)
            dataTable.put("Conferenceinfo-confcatnumber", confcatnumber);
        if (confcode != null)
            dataTable.put("Conferenceinfo-confcode", confcode);
        if (confsponsor.length() > 0)
            dataTable.put("Conferenceinfo-confsponsors", confsponsor.toString());
        if (confsponsorsComplete != null)
            dataTable.put("Conferenceinfo-confsponsorsComplete", confsponsorsComplete);
    }

    void setConflocation(Conflocation conflocation, Hashtable<String, Object> dataTable) {

        String venue = conflocation.getVenue();
        String addressPart = conflocation.getAddress_part();
        String cityGroup = conflocation.getCity_group();
        String city = conflocation.getCity();
        String state = conflocation.getState();
        String postalCode = conflocation.getPostal_code();
        String country = conflocation.getConflocation_country();

        if (venue != null)
            dataTable.put("Conferenceinfo-venue", venue);
        if (addressPart != null)
            dataTable.put("Conferenceinfo-addressPart", addressPart);
        if (cityGroup != null)
            dataTable.put("Conferenceinfo-city_group", cityGroup);
        if (city != null)
            dataTable.put("Conferenceinfo-city", city);
        if (state != null)
            dataTable.put("Conferenceinfo-state", state);
        if (postalCode != null)
            dataTable.put("Conferenceinfo-postal_code", postalCode);
        if (country != null)
            dataTable.put("Conferenceinfo-country", country);
    }

    void setConfdate(Confdate confdate, Hashtable<String, Object> dataTable) {
        String startdate = null;
        String startdateYear = null;
        String startdateMonth = null;
        String startdateDay = null;
        String enddate = null;
        String enddateYear = null;
        String enddateMonth = null;
        String enddateDay = null;
        String dateText = null;

        Startdate startdateObject = confdate.getStartdate();
        if (startdateObject != null) {
            startdate = startdateObject.getStartdate();
            startdateYear = startdateObject.getStartdate_year();
            startdateMonth = startdateObject.getStartdate_month();
            startdateDay = startdateObject.getStartdate_day();
        }
        Enddate enddateObject = confdate.getEnddate();
        if (enddateObject != null) {
            enddate = enddateObject.getEnddate();
            enddateYear = enddateObject.getEnddate_year();
            enddateMonth = enddateObject.getEnddate_month();
            enddateDay = enddateObject.getEnddate_day();
            dateText = confdate.getDate_text();
        }
        if (startdate != null && startdate.length() > 0)
            dataTable.put("Conferenceinfo-startdate", startdate);
        if (startdateYear != null && startdateYear.length() > 0)
            dataTable.put("Conferenceinfo-startdate_year", startdateYear);
        if (startdateMonth != null && startdateMonth.length() > 0)
            dataTable.put("Conferenceinfo-startdate_month", startdateMonth);
        if (startdateDay != null && startdateDay.length() > 0)
            dataTable.put("Conferenceinfo-startdate_day", startdateDay);
        if (enddate != null && enddate.length() > 0)
            dataTable.put("Conferenceinfo-enddate", enddate);
        if (enddateYear != null && enddateYear.length() > 0)
            dataTable.put("Conferenceinfo-enddate_year", enddateYear);
        if (enddateMonth != null && enddateMonth.length() > 0)
            dataTable.put("Conferenceinfo-enddate_month", enddateMonth);
        if (enddateDay != null && enddateDay.length() > 0)
            dataTable.put("Conferenceinfo-enddate_day", enddateDay);
        if (dateText != null && dateText.length() > 0)
            dataTable.put("Conferenceinfo-date_text", dateText);
    }

    void setConfpublication(Confpublication confpublication, Hashtable<String, Object> dataTable) {
        String procpartno = confpublication.getProcpartno();
        String procpagerange = confpublication.getProcpagerange();
        String procpagecount = confpublication.getProcpagecount();
        List<?> editororganizations = null;
        String editoraddress = null;
        String editorsComplete = null;
        StringBuffer editororganization = new StringBuffer();

        Confeditors confeditors = confpublication.getConfeditors();
        if (confeditors != null) {
            editororganizations = confeditors.getEditororganizations();

            if (editororganizations != null) {
                for (int i = 0; i < editororganizations.size(); i++) {
                    editororganization.append((String) editororganizations.get(i));
                    if (i != (editororganizations.size() - 1)) {
                        editororganization.append(delimited30);
                    }
                }
            }

            editoraddress = confeditors.getEditoraddress();
            Editors editors = confeditors.getEditors();
            if (editors != null) {
                editorsComplete = editors.getEditors_complete();
                List<?> editor = editors.getEditor();

                setEditor(editor, dataTable, "Conferenceinfo");
            }
        }

        if (procpartno != null && procpartno.length() > 0 && !procpartno.equals("null"))
            dataTable.put("Conferenceinfo-procpartno", procpartno);
        if (procpagerange != null && procpagerange.length() > 0 && !procpagerange.equals("null"))
            dataTable.put("Conferenceinfo-procpagerange", procpagerange);
        if (procpagecount != null && procpagecount.length() > 0 && !procpagecount.equals("null"))
            dataTable.put("Conferenceinfo-procpagecount", procpagecount);
        if (editororganization.length() > 0) {
            dataTable.put("Conferenceinfo-editororganization", editororganization.toString());
        }
        if (editoraddress != null && editoraddress.length() > 0 && !editoraddress.equals("null"))
            dataTable.put("Conferenceinfo-editoraddress", editoraddress);
        if (editorsComplete != null && editorsComplete.length() > 0 && !editorsComplete.equals("null"))
            dataTable.put("Conferenceinfo-editorsComplete", editorsComplete);

    }

    void setEnhancement(Enhancement enhancement, Hashtable<String, Object> dataTable) {

        Patent patent = enhancement.getPatent();
        if (patent != null)
            setPatent(patent, dataTable);

        Descriptorgroup descriptorgroup = enhancement.getDescriptorgroup();
        if (descriptorgroup != null)
            setDescriptorgroup(descriptorgroup, dataTable);

        Classificationgroup classificationgroup = enhancement.getClassificationgroup();
        if (classificationgroup != null)
            setClassificationgroup(classificationgroup, dataTable);

        Manufacturergroup manufacturergroup = enhancement.getManufacturergroup();
        if (manufacturergroup != null)
            setManufacturergroup(manufacturergroup, dataTable);

        Tradenamegroup tradenamegroup = enhancement.getTradenamegroup();
        if (tradenamegroup != null)
            setTradenamegroup(tradenamegroup, dataTable);

        Sequencebanks sequencebanks = enhancement.getSequencebanks();
        if (sequencebanks != null)
            setSequencebanks(sequencebanks, dataTable);

        Chemicalgroup chemicalgroup = enhancement.getChemicalgroup();
        if (chemicalgroup != null)
            setChemicalgroup(chemicalgroup, dataTable);
    }

    void setToc(Toc toc, Hashtable<String, Object> dataTable) {
        Tocentry tocentry = toc.getTocentry();
        String sectiontitle = null;
        String sectiontitleType = null;
        String sectionauthortext = null;
        String pagerange = null;
        String pagerange_first = null;
        String pagerange_last = null;
        String pages = null;
        String formatinfo = null;
        String formatinfoType = null;
        String linkinfo = null;
        String linkinfoType = null;
        String initials = null;
        String indexedName = null;
        String degrees = null;
        String surname = null;
        String givenName = null;
        String suffix = null;
        String nameText = null;
        String fullName = null;

        if (tocentry != null) {
            Sectiontitle sectiontitleObject = tocentry.getSectiontitle();
            if (sectiontitleObject != null) {
                sectiontitle = sectiontitleObject.getSectiontitle();
                sectiontitleType = sectiontitleObject.getSectiontitle_type();
                sectionauthortext = tocentry.getSectionauthortext();
            }
            Pagerange pagerangeObject = tocentry.getPagerange();
            if (pagerangeObject != null) {
                pagerange = pagerangeObject.getPagerange();
                pagerange_first = pagerangeObject.getPagerange_first();
                pagerange_last = pagerangeObject.getPagerange_last();
                pages = tocentry.getPages();
            }
            Formatinfo formatinfoObject = tocentry.getFormatinfo();
            if (formatinfoObject != null) {
                formatinfo = formatinfoObject.getFormatinfo();
                formatinfoType = formatinfoObject.getFormatinfo_type();
            }
            Linkinfo linkinfoObject = tocentry.getLinkinfo();
            if (linkinfoObject != null) {
                linkinfo = linkinfoObject.getLinkinfo();
                linkinfoType = linkinfoObject.getLinkinfo_type();
            }

            Sectionauthor sectionauthor = tocentry.getSectionauthor();
            if (sectionauthor != null) {
                initials = sectionauthor.getInitials();
                indexedName = sectionauthor.getIndexed_name();
                degrees = sectionauthor.getDegrees();
                surname = sectionauthor.getSurname();
                givenName = sectionauthor.getGiven_name();
                suffix = sectionauthor.getSuffix();
                nameText = sectionauthor.getNametext();
                if (surname != null) {
                    if (givenName != null)
                        fullName = surname + ", " + givenName;
                    else if (initials != null)
                        fullName = surname + ", " + initials;
                    else
                        fullName = surname;
                }
            }
        }

        if (sectiontitle != null)
            dataTable.put("Additional_srcinfo-sectiontitle", sectiontitle);
        if (sectiontitleType != null)
            dataTable.put("Additional_srcinfo-sectiontitle_type", sectiontitleType);
        if (sectionauthortext != null)
            dataTable.put("Additional_srcinfo-sectionauthortext", sectionauthortext);
        if (pagerange != null)
            dataTable.put("Additional_srcinfo-pagerange", pagerange);
        if (pagerange_first != null)
            dataTable.put("Additional_srcinfo-pagerange_first", pagerange_first);
        if (pagerange_last != null)
            dataTable.put("Additional_srcinfo-pagerange_last", pagerange_last);
        if (pages != null)
            dataTable.put("Additional_srcinfo-pages", pages);
        if (formatinfo != null)
            dataTable.put("Additional_srcinfo-formatinfo", formatinfo);
        if (formatinfoType != null)
            dataTable.put("Additional_srcinfo-formatinfo_type", formatinfoType);
        if (linkinfo != null)
            dataTable.put("Additional_srcinfo-linkinfo", linkinfo);
        if (linkinfoType != null)
            dataTable.put("Additional_srcinfo-linkinfo_type", linkinfoType);
        if (initials != null)
            dataTable.put("Additional_srcinfo-initials", initials);
        if (indexedName != null)
            dataTable.put("Additional_srcinfo-indexed_name", indexedName);
        if (degrees != null)
            dataTable.put("Additional_srcinfo-degrees", degrees);
        if (surname != null)
            dataTable.put("Additional_srcinfo-surname", surname);
        if (givenName != null)
            dataTable.put("Additional_srcinfo-given_name", givenName);
        if (suffix != null)
            dataTable.put("Additional_srcinfo-suffix", suffix);
        if (nameText != null)
            dataTable.put("Additional_srcinfo-nameText", nameText);
        if (fullName != null)
            dataTable.put("Additional_srcinfo-fullName", fullName);
    }

    void setClassificationgroup(Classificationgroup classificationgroup, Hashtable<String, Object> dataTable) {
        String classificationType = null;
        List<?> classificationList = new ArrayList<Object>();
        StringBuffer classification = new StringBuffer();
        StringBuffer classificationDesc = new StringBuffer();
        StringBuffer classificationSubject = new StringBuffer();
        Classifications classificationsObject = null;
        List<?> classifications = classificationgroup.getClassifications();
        for (int i = 0; i < classifications.size(); i++) {
            classificationsObject = (Classifications) classifications.get(i);
            classificationType = classificationsObject.getClassification_type();
            classificationList = classificationsObject.getClassification();

            for (int j = 0; j < classificationList.size(); j++) {
                if (classificationType.equals("GEOCLASSDESC")) {
                    classificationDesc.append((String) classificationList.get(j));
                    if (j != (classificationList.size() - 1)) {
                        classificationDesc.append(delimited30);
                    }
                } else if (classificationType.equals("SUBJECT")) {
                    classificationSubject.append((String) classificationList.get(j));
                    if (j != (classificationList.size() - 1)) {
                        classificationSubject.append(delimited30);
                    }
                } else {
                    classification.append((String) classificationList.get(j));
                    if (j != (classificationList.size() - 1)) {
                        classification.append(delimited30);
                    }
                }

            }

            if (classificationType != null) {
                if (classificationType.equals("CPXCLASS") || classificationType.equals("GEOCLASS")) {
                    dataTable.put("Enhancement-classification_type", classificationType);
                } else if (classificationType.equals("SUBJECT")) {
                    dataTable.put("Enhancement-classification_subject_type", classificationType);
                } else if (classificationType.equals("GEOCLASSDESC")) {
                    dataTable.put("Enhancement-classification_description_type", classificationType);
                }
            }
            if (classification != null) {
                if (classificationType.equals("CPXCLASS") || classificationType.equals("GEOCLASS")) {
                    dataTable.put("Enhancement-classification", classification.toString());
                } else if (classificationType.equals("SUBJECT")) {
                    dataTable.put("Enhancement-classification_subject", classificationSubject.toString());
                } else if (classificationType.equals("GEOCLASSDESC")) {
                    dataTable.put("Enhancement-classification_description", classificationDesc.toString());
                }
            }
        }

    }

    void setManufacturergroup(Manufacturergroup manufacturergroup, Hashtable<String, Object> dataTable) {
        String manufacturersType = null;
        String manufacturer = null;
        String manufacturerCountry = null;
        Manufacturers manufacturers = manufacturergroup.getManufacturers();
        if (manufacturers != null) {
            manufacturersType = manufacturers.getManufacturers_type();
            Manufacturer manufacturerObject = manufacturers.getManufacturer();
            if (manufacturerObject != null) {
                manufacturer = manufacturerObject.getManufacturer();
                manufacturerCountry = manufacturerObject.getManufacturer_country();
            }
        }
        if (manufacturersType != null)
            dataTable.put("Enhancement-manufacturers_type", manufacturersType);
        if (manufacturer != null)
            dataTable.put("Enhancement-manufacturer", manufacturer);
        if (manufacturerCountry != null)
            dataTable.put("Enhancement-manufacturer_country", manufacturerCountry);
    }

    void setTradenamegroup(Tradenamegroup tradenamegroup, Hashtable<String, Object> dataTable) {
        String tradename = null;
        String manufacturer = null;
        String manufacturerCountry = null;
        Tradenames tradenames = tradenamegroup.getTradenames();
        if (tradenames != null) {
            Trademanuitem trademanuitem = tradenames.getTrademanuitem();
            if (trademanuitem != null) {
                tradename = trademanuitem.getTradename();
                Manufacturer manufacturerObject = trademanuitem.getManufacturer();
                if (manufacturerObject != null) {
                    manufacturer = manufacturerObject.getManufacturer();
                    manufacturerCountry = manufacturerObject.getManufacturer_country();
                }
            }
        }

        if (tradename != null)
            dataTable.put("Enhancement-tradename", tradename);
        if (manufacturer != null)
            dataTable.put("Enhancement-Tradenamegroup-manufacturer", manufacturer);
        if (manufacturerCountry != null)
            dataTable.put("Enhancement-Tradenamegroup-manufacturer_country", manufacturerCountry);

    }

    void setSequencebanks(Sequencebanks sequencebanks, Hashtable<String, Object> dataTable) {
        String sequencebankComplete = null;
        String sequencebankName = null;
        String sequenceNumber = null;
        String sequenceNumberType = null;

        Sequencebank sequencebankObject = sequencebanks.getSequencebank();
        if (sequencebankObject != null) {
            sequencebankComplete = sequencebankObject.getSequencebank_complete();
            sequencebankName = sequencebankObject.getSequencebank_name();
            Sequence_number sequenceNumberObject = sequencebankObject.getSequence_number();
            if (sequenceNumberObject != null) {
                sequenceNumber = sequenceNumberObject.getSequence_number();
                sequenceNumberType = sequenceNumberObject.getSequence_number_type();
            }
        }
        if (sequencebankComplete != null)
            dataTable.put("Enhancement-sequencebank_complete", sequencebankComplete);
        if (sequencebankName != null)
            dataTable.put("Enhancement-sequencebank_name", sequencebankName);
        if (sequenceNumber != null)
            dataTable.put("Enhancement-sequence_number", sequenceNumber);
        if (sequenceNumberType != null)
            dataTable.put("Enhancement-sequence_number_type", sequenceNumberType);
    }

    void setChemicalgroup(Chemicalgroup chemicalgroup, Hashtable<String, Object> dataTable) {

        List<?> chemicalsObject = chemicalgroup.getChemicalss();
        StringBuffer chemicalName = new StringBuffer();
        StringBuffer CasRegistryNumber = new StringBuffer();
        for (int i = 0; i < chemicalsObject.size(); i++) {
            Chemicals chemicals = (Chemicals) chemicalsObject.get(i);
            String chemicals_source = chemicals.getChemical_source();
            List<?> chemicalList = chemicals.getChemicals();

            for (int j = 0; j < chemicalList.size(); j++) {
                Chemical chemical = (Chemical) chemicalList.get(j);
                chemicalName.append(chemical.getChemical_name());
                CasRegistryNumber.append(chemical.getCas_registry_number());
                if (i != (chemicalList.size() - 1)) {
                    chemicalName.append(delimited30);
                    CasRegistryNumber.append(delimited30);
                }
            }
            if (i != (chemicalsObject.size() - 1)) {
                chemicalName.append("::");
                CasRegistryNumber.append("::");
            }
        }
        if (chemicalName != null)
            dataTable.put("Enhancement-chemical_name", chemicalName);
        if (CasRegistryNumber != null)
            dataTable.put("Enhancement-cas_registry_number", CasRegistryNumber);
    }

    void setTail(Tail tail, Hashtable<String, Object> dataTable) {

        Bibliography bibliography = tail.getBibliography();
        String bibliographyRefcount = bibliography.getBibliography_refcount();
        StringBuffer referenceId = new StringBuffer();
        StringBuffer refFulltext = new StringBuffer();
        StringBuffer refdItemcitationType = new StringBuffer();
        if (bibliography != null) {
            List<?> references = bibliography.getReferences();

            for (int i = 0; i < references.size(); i++) {
                Reference reference = (Reference) references.get(i);
                if (reference != null) {
                    referenceId.append(reference.getReference_id());
                    refFulltext.append(reference.getRef_fulltext());
                    refdItemcitationType.append(reference.getRefd_itemcitation_type());

                    Ref_info refInfo = reference.getRef_info();
                    if (refInfo != null)
                        setRefInfo(refInfo, dataTable, "tail");

                    Refd_itemcitation refdItemcitation = reference.getRefd_itemcitation();
                    if (refdItemcitation != null)
                        setRefdItemcitation(refdItemcitation, dataTable, "tail");
                    if (i != (references.size() - 1)) {
                        referenceId.append(delimited30);
                        refFulltext.append(delimited30);
                        refdItemcitationType.append(delimited30);
                        if (refdItemcitation != null)
                            setRefdItemcitation(refdItemcitation, dataTable, "tail_more");
                    }
                }
            }
        }

        dataTable.put("Tail-bibliographyRefcount", bibliographyRefcount);
        dataTable.put("Tail-referenceId", referenceId.toString());
        dataTable.put("Tail-refFulltext", refFulltext.toString());
        dataTable.put("Tail-refdItemcitationType", refdItemcitationType.toString());

    }

    void setRefdItemcitation(Refd_itemcitation refdItemcitation, Hashtable<String, Object> dataTable, String name) {
        String pii = refdItemcitation.getPii();
        String doi = refdItemcitation.getDoi();
        String sourcetitle = refdItemcitation.getSourcetitle();
        String sourcetitleAbbrev = refdItemcitation.getSourcetitle_abbrev();
        String issn = null;
        String issnType = null;
        String isbn = null;
        String isbnType = null;
        String etAl = null;
        List<?> authors = null;

        Issn issnObject = refdItemcitation.getIssn();
        if (issnObject != null) {
            issn = issnObject.getIssn();
            issnType = issnObject.getIssn_type();
        }
        Isbn isbnObject = refdItemcitation.getIsbn();
        if (isbnObject != null) {
            isbn = isbnObject.getIsbn();
            isbnType = isbnObject.getIsbn_type();
        }
        String codencode = refdItemcitation.getCodencode();
        String publicationyear = refdItemcitation.getPublicationyear();
        String refdItemcitationType = refdItemcitation.getRefd_itemcitation_type();
        String refText = refdItemcitation.getRef_text();

        Volisspag volisspag = refdItemcitation.getVolisspag();
        if (volisspag != null) {
            setVolisspag(volisspag, dataTable, "Refd_itemcitation");
        }

        Website website = refdItemcitation.getWebsite();
        if (website != null) {
            setWebsite(website, dataTable, "Refd_itemcitation");
        }
        Citation_title citationTitle = refdItemcitation.getCitation_title();
        if (citationTitle != null) {
            setCitationTitle(citationTitle, dataTable, "Refd_itemcitation");
        }
        Author_group authorGroup = refdItemcitation.getAuthor_group();
        if (citationTitle != null) {
            etAl = authorGroup.getEt_Al();
            authors = authorGroup.getAuthors();
            if (authors != null) {
                setAuthors(authors, dataTable, "Refd_itemcitation", 1);
            }
        }

        if (pii != null)
            dataTable.put("Refd_itemcitation-pii", pii);
        if (doi != null)
            dataTable.put("Refd_itemcitation-doi", doi);
        if (sourcetitle != null)
            dataTable.put("Refd_itemcitation-sourcetitle", sourcetitle);
        if (sourcetitleAbbrev != null)
            dataTable.put("Refd_itemcitation-sourcetitle_abbrev", sourcetitleAbbrev);
        if (issn != null)
            dataTable.put("Refd_itemcitation-issn", issn);
        if (issnType != null)
            dataTable.put("Refd_itemcitation-issn_type", issnType);
        if (isbn != null)
            dataTable.put("Refd_itemcitation-isbn", isbn);
        if (isbnType != null)
            dataTable.put("Refd_itemcitation-isbn_type", isbnType);
        if (codencode != null)
            dataTable.put("Refd_itemcitation-codencode", codencode);
        if (publicationyear != null)
            dataTable.put("Refd_itemcitation-publicationyear", publicationyear);
        if (refdItemcitationType != null)
            dataTable.put("Refd_itemcitation-refd_itemcitation_type", refdItemcitationType);
        if (refText != null)
            dataTable.put("Refd_itemcitation-ref_text", refText);
        if (etAl != null)
            dataTable.put("Refd_itemcitation-et_al", etAl);

        if (name.indexOf("more") > 0) {
            dataTable.put("Refd_itemcitation-pii", (String) dataTable.get("Refd_itemcitation-pii") + "&&");
            dataTable.put("Refd_itemcitation-doi", (String) dataTable.get("Refd_itemcitation-doi") + "&&");
            dataTable.put("Refd_itemcitation-sourcetitle", (String) dataTable.get("Refd_itemcitation-sourcetitle") + "&&");
            dataTable.put("Refd_itemcitation-sourcetitle_abbrev", (String) dataTable.get("Refd_itemcitation-sourcetitle_abbrev") + "&&");
            dataTable.put("Refd_itemcitation-issn", (String) dataTable.get("Refd_itemcitation-issn") + "&&");
            dataTable.put("Refd_itemcitation-issn_type", (String) dataTable.get("Refd_itemcitation-issn_type") + "&&");
            dataTable.put("Refd_itemcitation-isbn", (String) dataTable.get("Refd_itemcitation-isbn") + "&&");
            dataTable.put("Refd_itemcitation-isbn_type", (String) dataTable.get("Refd_itemcitation-isbn_type") + "&&");
            dataTable.put("Refd_itemcitation-codencode", (String) dataTable.get("Refd_itemcitation-codencode") + "&&");
            dataTable.put("Refd_itemcitation-publicationyear", (String) dataTable.get("Refd_itemcitation-publicationyear") + "&&");
            dataTable.put("Refd_itemcitation-refd_itemcitation_type", (String) dataTable.get("Refd_itemcitation-refd_itemcitation_type") + "&&");
            dataTable.put("Refd_itemcitation-ref_text", (String) dataTable.get("Refd_itemcitation-ref_text") + "&&");
            dataTable.put("Refd_itemcitation-et_al", (String) dataTable.get("Refd_itemcitation-et_al") + "&&");
            setVolisspag(volisspag, dataTable, "Refd_itemcitation_more");
            setWebsite(website, dataTable, "Refd_itemcitation_more");
            setCitationTitle(citationTitle, dataTable, "Refd_itemcitation_more");
            setAuthors(authors, dataTable, "Refd_itemcitation_more", 1);
        }

    }

    void setCitationTitle(Citation_title citationTitle, Hashtable<String, Object> dataTable, String name) {
        List<?> titletextObjectList = citationTitle.getTitletext();
        for (int i = 0; i < titletextObjectList.size(); i++) {
            Titletext titletextObject = (Titletext) titletextObjectList.get(i);
            if (titletextObject != null) {
                String titletext = null;
                String titletextLang = titletextObject.getTitletext_lang();
                String titletextOriginal = titletextObject.getTitletext_original();

                if (titletextOriginal != null) {
                    titletext = titletextObject.getTitletext();
                    if (titletextOriginal.equals("y")) {
                        dataTable.put(name + "-Citation_title-titletext_Original", titletextOriginal);
                        if (titletext != null) {
                            dataTable.put(name + "-Citation_title-titletext", titletext);
                        }
                        if (titletextLang != null) {
                            dataTable.put(name + "-Citation_title-titletextLang", titletextLang);
                        }
                    } else {
                        if (titletext != null) {
                            if (dataTable.get(name + "-Citation_title-translated_titletext") == null) {
                                dataTable.put(name + "-Citation_title-translated_titletext", titletext);
                            } else {
                                dataTable.put(name + "-Citation_title-translated_titletext", dataTable.get(name + "-Citation_title-translated_titletext")
                                    + "&&" + titletext);
                            }
                        }
                    }
                }

            }
        }

    }

    void setRefInfo(Ref_info refInfo, Hashtable<String, Object> dataTable, String name) {
        String refSourcetitle = refInfo.getRef_sourcetitle();
        String refPublicationyear = null;
        String refpublicationyearFirst = null;
        String refPublicationyearLast = null;
        String refTitletext = null;
        String itemid = null;
        String etAl = null;
        String refText = null;
        List<?> authors = null;
        String websitename = null;
        String emailAddress = null;
        StringBuffer indexed_name = new StringBuffer();
        StringBuffer text = new StringBuffer();

        Ref_publicationyear refPublicationyearObject = refInfo.getRef_publicationyear();
        if (refPublicationyearObject != null) {
            refPublicationyear = refPublicationyearObject.getRef_publicationyear();
            refpublicationyearFirst = refPublicationyearObject.getRef_publicationyear_first();
            refPublicationyearLast = refPublicationyearObject.getRef_publicationyear_last();
        }
        refText = refInfo.getRef_text();
        Ref_title refTitle = refInfo.getRef_title();
        if (refTitle != null) {
            refTitletext = refTitle.getRef_titletext();
        }
        Refd_itemidlist refdItemidlist = refInfo.getRefd_itemidlist();
        if (refdItemidlist != null) {
            itemid = refdItemidlist.getItemid();
        }
        Ref_authors refAuthors = refInfo.getRef_authors();
        if (refAuthors != null) {
            etAl = refAuthors.getEt_al();
            authors = refAuthors.getAuthors();
            if (authors != null) {
                setAuthors(authors, dataTable, "Ref_info", 1);
            }

            List<?> collaborations = refAuthors.getCollaborations();

            for (int i = 0; i < collaborations.size(); i++) {
                Collaboration collaboration = (Collaboration) collaborations.get(i);
                indexed_name.append(collaboration.getIndexed_Name());
                text.append(collaboration.getText());
                if (i != (collaborations.size() - 1)) {
                    indexed_name.append(delimited30);
                    text.append(delimited30);
                }
            }
        }

        Volisspag refVolisspag = refInfo.getRef_volisspag();
        if (refVolisspag != null) {
            setVolisspag(refVolisspag, dataTable, "Ref_info");
        }

        Ref_website refWebsite = refInfo.getRef_website();
        if (refWebsite != null) {
            websitename = refWebsite.getWebsitename();
            emailAddress = refWebsite.getE_address();
        }

        dataTable.put("Ref_info-indexed_name", indexed_name.toString());
        dataTable.put("Ref_info-text", text.toString());
        if (refSourcetitle != null)
            dataTable.put("Ref_info-ref_sourcetitle", refSourcetitle);
        if (refPublicationyear != null)
            dataTable.put("Ref_info-ref_publicationyear", refPublicationyear);
        if (refpublicationyearFirst != null)
            dataTable.put("Ref_info-ref_publicationyearFirst", refpublicationyearFirst);
        if (refPublicationyearLast != null)
            dataTable.put("Ref_info-ref_publicationyearLast", refPublicationyearLast);
        if (refText != null)
            dataTable.put("Ref_info-ref_text", refText);
        if (refTitletext != null)
            dataTable.put("Ref_info-ref_titletext", refTitletext);
        if (itemid != null)
            dataTable.put("Ref_info-itemid", itemid);
        if (etAl != null)
            dataTable.put("Ref_info-et_al", etAl);
        if (websitename != null)
            dataTable.put("Ref_info-websitename", websitename);
        if (emailAddress != null)
            dataTable.put("Ref_info-emailAddress", emailAddress);

        if (name.indexOf("more") > 0) {
            dataTable.put("Ref_info-indexed_name", dataTable.get("Ref_info-indexed_name") + "&&");
            dataTable.put("Ref_info-text", dataTable.get("Ref_info-text") + "&&");
            dataTable.put("Ref_info-ref_sourcetitle", dataTable.get("Ref_info-ref_sourcetitle") + "&&");
            dataTable.put("Ref_info-ref_publicationyear", dataTable.get("Ref_info-ref_publicationyear") + "&&");
            dataTable.put("Ref_info-ref_publicationyearFirst", dataTable.get("Ref_info-ref_publicationyearFirst") + "&&");
            dataTable.put("Ref_info-ref_publicationyearLast", dataTable.get("Ref_info-ref_publicationyearLast") + "&&");
            dataTable.put("Ref_info-ref_text", dataTable.get("Ref_info-ref_text") + "&&");
            dataTable.put("Ref_info-ref_titletext", dataTable.get("Ref_info-ref_titletext") + "&&");
            dataTable.put("Ref_info-itemid", dataTable.get("Ref_info-itemid") + "&&");
            dataTable.put("Ref_info-et_al", dataTable.get("Ref_info-et_al") + "&&");
            dataTable.put("Ref_info-websitename", dataTable.get("Ref_info-websitename") + "&&");
            dataTable.put("Ref_info-emailAddress", dataTable.get("Ref_info-emailAddress") + "&&");
            setVolisspag(refVolisspag, dataTable, "Ref_info_more");
            setAuthors(authors, dataTable, "Ref_info_more", 1);
        }

    }

    void setPatent(Patent patent, Hashtable<String, Object> dataTable) {
        Registrations registrations = patent.getRegistrations();
        String registration = null;
        String localapplication = null;
        String prioapplication = null;
        String assignee = null;
        String designatedcountrie = null;
        String inventor = null;
        String ipcCode = null;

        if (registrations != null)
            registration = registrations.getRegistration();

        Localapplications localapplications = patent.getLocalapplications();
        if (localapplication != null)
            localapplication = localapplications.getLocalapplication();

        Prioapplications prioapplications = patent.getPrioapplications();
        if (prioapplications != null)
            prioapplication = prioapplications.getPrioapplication();

        Assignees assignees = patent.getAssignees();
        if (assignees != null)
            assignee = assignees.getAssignees();

        Designatedcountries designatedcountries = patent.getDesignatedcountries();
        if (designatedcountries != null) {
            designatedcountrie = designatedcountries.getCountry();
        }

        Inventors inventors = patent.getInventors();
        if (inventors != null)
            inventor = inventors.getInventor();

        Ipc_codes ipcCodes = patent.getIpc_codes();
        if (ipcCodes != null)
            ipcCode = ipcCodes.getIpc_code();

        if (registration != null)
            dataTable.put("Patent-registration", registration);
        if (localapplication != null)
            dataTable.put("Patent-localapplication", localapplication);
        if (prioapplication != null)
            dataTable.put("Patent-prioapplication", prioapplication);
        if (assignee != null)
            dataTable.put("Patent-assignee", assignee);
        if (designatedcountrie != null)
            dataTable.put("Patent-designatedcountrie", designatedcountrie);
        if (inventor != null)
            dataTable.put("Patent-inventor", inventor);
        if (ipcCode != null)
            dataTable.put("Patent-ipcCode", ipcCode);
    }

    void setPublicationdate(Publicationdate publicationdate, Hashtable<String, Object> dataTable, String name) {
        String previous = publicationdate.getPrevious();
        String rePrint = publicationdate.getReprint();
        String year = publicationdate.getYear();
        String month = publicationdate.getMonth();
        String day = publicationdate.getDay();
        String season = publicationdate.getSeason();
        String dateText = publicationdate.getDate_Text();

        if (previous != null)
            dataTable.put(name + "-Publicationdate_previous", previous);
        if (rePrint != null)
            dataTable.put(name + "-Publicationdate_rePrint", rePrint);
        if (year != null)
            dataTable.put(name + "-Publicationdate_year", year);
        if (month != null)
            dataTable.put(name + "-Publicationdate_month", month);
        if (day != null)
            dataTable.put(name + "-Publicationdate_day", day);
        if (season != null)
            dataTable.put(name + "-Publicationdate_season", season);
        if (dateText != null)
            dataTable.put(name + "-Publicationdate_dateText", dateText);
    }

    void setAuthors(List<?> authors, Hashtable<String, Object> dataTable, String name, int authorGroupID) {

        StringBuffer type = new StringBuffer();
        StringBuffer seq = new StringBuffer();
        StringBuffer auID = new StringBuffer();
        StringBuffer initials = new StringBuffer();
        StringBuffer indexedName = new StringBuffer();
        StringBuffer degrees = new StringBuffer();
        StringBuffer surname = new StringBuffer();
        StringBuffer givenName = new StringBuffer();
        StringBuffer suffix = new StringBuffer();
        StringBuffer nameText = new StringBuffer();
        StringBuffer emailAddress = new StringBuffer();
        StringBuffer fullName = new StringBuffer();

        for (int j = 0; j < authors.size(); j++) {
            Author author = (Author) authors.get(j);
            if (author != null) {
                if (author.getE_address() != null && emailAddress.length() > 0) {
                    emailAddress.append(delimited30);
                }

                if (author.getIndexed_name() != null && indexedName.length() > 0) {
                    indexedName.append(delimited30);
                }

                if (author.getNametext() != null && nameText.length() > 0) {
                    nameText.append(delimited30);
                }

                if (author.getSurname() != null && fullName.length() > 0) {
                    fullName.append(delimited30);
                }

                if (author.getE_address() != null) {
                    emailAddress.append(author.getE_address());
                }

                if (author.getIndexed_name() != null) {
                    indexedName.append(author.getIndexed_name());
                }

                if (author.getNametext() != null) {
                    nameText.append(author.getNametext());
                }

                if (author.getSurname() != null) {
                    if (author.getGiven_name() != null)
                        fullName.append(author.getSurname() + ", " + author.getGiven_name());
                    else if (author.getInitials() != null)
                        fullName.append(author.getSurname() + ", " + author.getInitials());
                    else
                        fullName.append(author.getSurname());
                }

            }
        }

        if (indexedName.length() > 0) {
            if (dataTable.get(name + "-Authors_indexedName") == null) {
                dataTable.put(name + "-Authors_indexedName", authorGroupID + delimited29 + indexedName.toString());
            } else {
                dataTable.put(name + "-Authors_indexedName", dataTable.get(name + "-Authors_indexedName") + delimited02 + authorGroupID + delimited29
                    + indexedName.toString());
            }
        }

        if (dataTable.get(name + "-Authors_nameText") == null) {
            dataTable.put(name + "-Authors_nameText", nameText.toString());
        } else {
            dataTable.put(name + "-Authors_nameText",
                dataTable.get(name + "-Authors_nameText") + delimited02 + authorGroupID + delimited29 + nameText.toString());
        }

        if (emailAddress.length() > 0) {
            if (dataTable.get(name + "-Authors_emailAddress") == null) {
                dataTable.put(name + "-Authors_emailAddress", authorGroupID + delimited29 + emailAddress.toString());
            } else {
                dataTable.put(name + "-Authors_emailAddress", dataTable.get(name + "-Authors_emailAddress") + delimited02 + authorGroupID + delimited29
                    + emailAddress.toString());
            }
        }

        if (fullName.length() > 0) {
            if (dataTable.get(name + "-Authors_fullName") == null) {
                dataTable.put(name + "-Authors_fullName", authorGroupID + delimited29 + fullName.toString());
            } else {
                dataTable.put(name + "-Authors_fullName",
                    dataTable.get(name + "-Authors_fullName") + delimited02 + authorGroupID + delimited29 + fullName.toString());
            }
        }

    }

    void setVolisspag(Volisspag volisspag, Hashtable<String, Object> dataTable, String name) {
        String voliss = null;
        String volissVolume = null;
        String volissIssue = null;
        String pagerange = null;
        String pagerangeFirst = null;
        String pagerangeLast = null;
        String pagecount = null;
        String pagecountType = null;
        String pages = null;
        Voliss volissObject = volisspag.getVoliss();
        if (volissObject != null) {
            voliss = volissObject.getVoliss();
            volissVolume = volissObject.getVoliss_volume();
            volissIssue = volissObject.getVoliss_issue();
        }
        Pagerange pagerangeObject = volisspag.getPagerange();

        if (pagerangeObject != null) {
            pagerange = pagerangeObject.getPagerange();
            pagerangeFirst = pagerangeObject.getPagerange_first();
            pagerangeLast = pagerangeObject.getPagerange_last();
        }

        pages = volisspag.getPages();
        Pagecount pagecountObject = volisspag.getPagecount();

        if (pagecountObject != null) {
            pagecount = pagecountObject.getPagecount();
            pagecountType = pagecountObject.getPagecount_type();
        }

        if (voliss != null)
            dataTable.put(name + "-voliss", voliss);
        if (volissVolume != null)
            dataTable.put(name + "-voliss_volume", volissVolume);
        if (volissIssue != null)
            dataTable.put(name + "-voliss_issue", volissIssue);
        if (pagerange != null)
            dataTable.put(name + "-pagerange", pagerange);
        if (pagerangeFirst != null)
            dataTable.put(name + "-pagerange_first", pagerangeFirst);
        if (pagerangeLast != null)
            dataTable.put(name + "-pagerange_last", pagerangeLast);
        if (pages != null)
            dataTable.put(name + "-pages", pages);
        if (pagecount != null)
            dataTable.put(name + "-pagecount", pagecount);
        if (pagecountType != null)
            dataTable.put(name + "-pagecount_type", pagecountType);

        if (name.indexOf("more") > 0) {
            dataTable.put(name + "-voliss", dataTable.get(name + "-voliss") + "&&");
            dataTable.put(name + "-voliss_volume", dataTable.get(name + "-voliss_volume") + "&&");
            dataTable.put(name + "-voliss_issue", dataTable.get(name + "-voliss_issue") + "&&");
            dataTable.put(name + "-pagerange", dataTable.get(name + "-pagerange") + "&&");
            dataTable.put(name + "-pagerange_first", dataTable.get(name + "-pagerange_first") + "&&");
            dataTable.put(name + "-pagerange_last", dataTable.get(name + "-pagerange_last") + "&&");
            dataTable.put(name + "-pages", dataTable.get(name + "-pages") + "&&");
            dataTable.put(name + "-pagecount", dataTable.get(name + "-pagecount") + "&&");
            dataTable.put(name + "-pagecount_type", dataTable.get(name + "-pagecount_type") + "&&");
        }
    }

    void setHistory(History history, Hashtable<String, Object> dataTable) {
        String dateCreatedYear = null;
        String dateCreatedMonth = null;
        String dateCreatedDay = null;
        String dateCompletedYear = null;
        String dateCompletedMonth = null;
        String dateCompletedDay = null;
        String dateRevisedYear = null;
        String dateRevisedMonth = null;
        String dateRevisedDay = null;

        Date_created dateCreated = history.getDate_created();

        if (dateCreated != null) {
            dateCreatedYear = dateCreated.getDate_created_year();
            dateCreatedMonth = dateCreated.getDate_created_month();
            dateCreatedDay = dateCreated.getDate_created_day();
        }

        Date_completed dateCompleted = history.getDate_completed();

        if (dateCompleted != null) {
            dateCompletedYear = dateCompleted.getDate_completed_year();
            dateCompletedMonth = dateCompleted.getDate_completed_month();
            dateCompletedDay = dateCompleted.getDate_completed_day();
        }

        Date_revised dateRevised = history.getDate_revised();

        if (dateRevised != null) {
            dateRevisedYear = dateRevised.getDate_revised_year();
            dateRevisedMonth = dateRevised.getDate_revised_month();
            dateRevisedDay = dateRevised.getDate_revised_day();
        }

        if (dateCreatedYear != null && dateCreatedYear.length() > 0)
            dataTable.put("Item_info-History-dateCreatedYear", dateCreatedYear);
        if (dateCreatedMonth != null && dateCreatedMonth.length() > 0)
            dataTable.put("Item_info-History-dateCreatedMonth", dateCreatedMonth);
        if (dateCreatedDay != null && dateCreatedDay.length() > 0)
            dataTable.put("Item_info-History-dateCreatedDay", dateCreatedDay);

        if (dateCompletedYear != null && dateCompletedYear.length() > 0)
            dataTable.put("Item_info-History-dateCompletedYear", dateCompletedYear);
        if (dateCompletedMonth != null && dateCompletedMonth.length() > 0)
            dataTable.put("Item_info-History-dateCompletedMonth", dateCompletedMonth);
        if (dateCompletedDay != null && dateCompletedDay.length() > 0)
            dataTable.put("Item_info-History-dateCompletedDay", dateCompletedDay);

        if (dateRevisedYear != null && dateRevisedYear.length() > 0)
            dataTable.put("Item_info-History-dateRevisedYear", dateRevisedYear);
        if (dateRevisedMonth != null && dateRevisedMonth.length() > 0)
            dataTable.put("Item_info-History-dateRevisedMonth", dateRevisedMonth);
        if (dateRevisedDay != null && dateRevisedDay.length() > 0)
            dataTable.put("Item_info-History-dateRevisedDay", dateRevisedDay);

    }

    void setDescriptorgroup(Descriptorgroup descriptorgroup, Hashtable<String, Object> dataTable) {
        String descriptorsType = null;
        String descriptorsControlled = null;
        StringBuffer maintermGDE = new StringBuffer();
        StringBuffer maintermSPC = new StringBuffer();
        StringBuffer maintermRGI = new StringBuffer();

        Descriptor descriptor = null;

        List<?> descriptors = descriptorgroup.getDescriptorss();

        for (int i = 0; i < descriptors.size(); i++) {
            Descriptors descriptorsObject = (Descriptors) descriptors.get(i);
            descriptorsType = descriptorsObject.getDescriptors_type();
            descriptorsControlled = descriptorsObject.getDescriptors_controlled();

            if (descriptorsObject != null) {
                List<?> descriptorList = descriptorsObject.getDescriptors();
                for (int j = 0; j < descriptorList.size(); j++) {

                    descriptor = (Descriptor) descriptorList.get(j);
                    List<?> mainterms = descriptor.getMainterm();
                    for (int k = 0; k < mainterms.size(); k++) {
                        Mainterm maintermObject = (Mainterm) mainterms.get(k);
                        if (descriptorsControlled.equals("y") && descriptorsType.equals("GDE")) {
                            maintermGDE.append(maintermObject.getMainterm());
                            // System.out.println("SPC= "+maintermObject.getMainterm());
                        }

                        if (descriptorsControlled.equals("y") && (descriptorsType.equals("SPC"))) {
                            maintermSPC.append(maintermObject.getMainterm());
                        }

                        if (descriptorsControlled.equals("y") && descriptorsType.equals("RGI")) {
                            maintermRGI.append(maintermObject.getMainterm());
                        }

                        if (k != (mainterms.size() - 1)) {
                            if (descriptorsControlled.equals("y") && descriptorsType.equals("GDE")) {
                                maintermGDE.append(delimited30);
                            } else if (descriptorsControlled.equals("y") && descriptorsType.equals("SPC")) {
                                maintermSPC.append(delimited30);
                            } else if (descriptorsControlled.equals("y") && descriptorsType.equals("RGI")) {
                                maintermRGI.append(delimited30);
                            }
                        }
                    }

                    if (j != (descriptorList.size() - 1)) {
                        if (descriptorsControlled.equals("y") && descriptorsType.equals("GDE")) {
                            maintermGDE.append(delimited30);
                        } else if (descriptorsControlled.equals("y") && descriptorsType.equals("SPC")) {
                            maintermSPC.append(delimited30);
                        } else if (descriptorsControlled.equals("y") && descriptorsType.equals("RGI")) {
                            maintermRGI.append(delimited30);
                        }
                    }
                }
            }

        }

        dataTable.put("Enhancement-maintermGDE", maintermGDE.toString());
        dataTable.put("Enhancement-maintermSPC", maintermSPC.toString());
        dataTable.put("Enhancement-maintermRGI", maintermRGI.toString());
    }

    void setContributorGroup(Contributor_group contributorGroup, Hashtable<String, Object> dataTable, String name) {
        List<?> contributors = contributorGroup.getContributors();
        StringBuffer initials = new StringBuffer();
        StringBuffer indexedName = new StringBuffer();
        StringBuffer degrees = new StringBuffer();
        StringBuffer surname = new StringBuffer();
        StringBuffer given_name = new StringBuffer();
        StringBuffer suffix = new StringBuffer();
        StringBuffer nametext = new StringBuffer();
        StringBuffer e_address = new StringBuffer();
        StringBuffer contributor_role = new StringBuffer();
        StringBuffer contributor_auid = new StringBuffer();
        StringBuffer contributor_seq = new StringBuffer();
        StringBuffer contributor_type = new StringBuffer();
        StringBuffer contributorFullName = new StringBuffer();

        for (int i = 0; i < contributors.size(); i++) {
            Contributor contributor = (Contributor) contributors.get(i);
            if (contributor != null) {
                String sName = contributor.getSurname() == null ? "" : contributor.getSurname() + " ";
                String gName = contributor.getGiven_name() == null ? "" : contributor.getGiven_name() + " ";
                String ini = contributor.getInitials() == null ? "" : contributor.getInitials();
                String fullName = sName + " " + gName + " ";

                initials.append(ini);
                indexedName.append(contributor.getIndexed_name());
                degrees.append(contributor.getDegrees());
                surname.append(sName);
                given_name.append(gName);
                suffix.append(contributor.getSuffix());
                nametext.append(contributor.getNametext());
                e_address.append(contributor.getE_address());
                contributor_role.append(contributor.getContributor_role());
                contributor_auid.append(contributor.getContributor_auid());
                contributor_seq.append(contributor.getContributor_seq());
                contributor_type.append(contributor.getContributor_type());

                contributorFullName.append(fullName);

            }
            if (i != (contributors.size() - 1)) {
                initials.append(delimited30);
                indexedName.append(delimited30);
                degrees.append(delimited30);
                surname.append(delimited30);
                given_name.append(delimited30);
                suffix.append(delimited30);
                nametext.append(delimited30);
                e_address.append(delimited30);
                contributor_role.append(delimited30);
                contributor_auid.append(delimited30);
                contributor_seq.append(delimited30);
                contributor_type.append(delimited30);
                contributorFullName.append(delimited30);
            }

        }

        dataTable.put("Head-" + name + "-Initials", initials.toString());
        dataTable.put("Head-" + name + "-Indexed_name", indexedName.toString());
        dataTable.put("Head-" + name + "-Degrees", degrees.toString());
        dataTable.put("Head-" + name + "-Surname", surname.toString());
        dataTable.put("Head-" + name + "-Given_name", given_name.toString());
        dataTable.put("Head-" + name + "-Suffix", suffix.toString());
        dataTable.put("Head-" + name + "-Nametext", nametext.toString());
        dataTable.put("Head-" + name + "-E_address", e_address.toString());
        dataTable.put("Head-" + name + "-Contributor_role", contributor_role.toString());
        dataTable.put("Head-" + name + "-Contributor_auid", contributor_auid.toString());
        dataTable.put("Head-" + name + "-Contributor_seq", contributor_seq.toString());
        dataTable.put("Head-" + name + "-Contributor_type", contributor_type.toString());
        dataTable.put("Head-" + name + "-Contributor_FullName", contributorFullName.toString());

        List<?> collaborations = contributorGroup.getCollaborations();

        StringBuffer indexed_name = new StringBuffer();
        StringBuffer text = new StringBuffer();
        for (int j = 0; j < collaborations.size(); j++) {
            Collaboration collaboration = (Collaboration) collaborations.get(j);
            if (collaboration != null) {
                indexed_name.append(collaboration.getIndexed_Name());
                text.append(collaboration.getText());
            }
            if (j != (collaborations.size())) {
                indexed_name.append(delimited30);
                text.append(delimited30);
            }
        }

        dataTable.put("Head-" + name + "-Collaboration-Indexed_name", indexed_name.toString());
        dataTable.put("Head-" + name + "-Collaboration-Text", text.toString());

        String et_al = contributorGroup.getEt_al();

        Affiliation affiliation = contributorGroup.getAffiliation();
        if (affiliation != null)
            setAffiliation(affiliation, dataTable, "ContributorGroup", 1);

    }

}
