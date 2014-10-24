	create or replace procedure PA_createUserProfile(userProfileID varchar2,contractID varchar2,customerID varchar2,
			  userTitle varchar2,firstName varchar2,lastname varchar2,emailAddress varchar2,userPassword varchar2,
			  addDate varchar2,accessDate varchar2,announceFlag varchar2) is
	begin
			INSERT INTO USER_PROFILE_CONTRACT(USER_PROFILE_ID, CONTRACT_ID,CUSTOMER_ID,TITLE,FIRST_NAME,LAST_NAME, EMAIL,PASSWORD,ADD_DATE,ACCESS_DATE,ANNOUNCE_FLAG)
			VALUES(userProfileID,contractID,customerID,userTitle,firstName,lastName,emailAddress,userPassword,TO_DATE(addDate,'MM-DD-YYYY'),TO_DATE(accessDate,'MM-DD-YYYY'),announceFlag);
			commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure PersonalAccount_createUserProfile, error='||sqlerrm);
	end PA_createUserProfile;

/

	create or replace procedure PA_touch(userProfileID varchar2,accessDate varchar2) is
	begin
		update USER_PROFILE_CONTRACT set ACCESS_DATE=TO_DATE(accessDate,'MM-DD-YYYY') where USER_PROFILE_ID=userProfileID;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure PersonalAccount_touch, error='||sqlerrm);
	end PA_touch;

/

	create or replace procedure PA_updateUserProfile(userProfileID varchar2,contractID varchar2,customerID varchar2,
			  userTitle varchar2,firstName varchar2,lastname varchar2,emailAddress varchar2,userPassword varchar2,
			  addDate varchar2,accessDate varchar2,announceFlag varchar2) is
	begin

		update USER_PROFILE_CONTRACT set ANNOUNCE_FLAG=announceFlag,EMAIL=emailAddress, FIRST_NAME=firstName, LAST_NAME=lastName, ACCESS_DATE=TO_DATE(accessDate,'MM-DD-YYYY'), TITLE=userTitle, PASSWORD=userPassword where USER_PROFILE_ID=userProfileID;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure PA_updateUserProfile, error='||sqlerrm);
	end PA_updateUserProfile;

/

	create or replace procedure PA_removeUserProfile(userProfileID varchar2) is
	begin

		delete from USER_PROFILE_CONTRACT where USER_PROFILE_ID=userProfileID;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure PA_removeUserProfile, error='||sqlerrm);
	end;

/
	create or replace procedure SavedRecord_addSelectedRecord(recordID varchar2,folderID varchar2,databaseID varchar2,docID varchar2,saveDate varchar2,accessDate varchar2,sciData varchar2) is
	begin
		insert into saved_records (record_id,folder_id,database_id,guid,save_date,access_date,sci_Data)
		values (recordID, folderID, databaseID, docID, TO_DATE(saveDate,'MM-DD-YY'), TO_DATE(accessDate,'MM-DD-YY'), sciData);
		commit;
	EXCEPTION
	   when DUP_VAL_ON_INDEX then
			ROLLBACK;
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_addSelectedRecord, error='||sqlerrm);
	end;

/
	create or replace procedure SavedRecord_addFolder(folderID varchar2, userID varchar2, folderName varchar2, saveDate varchar2, accessDate varchar2) is
	begin
			insert into folder(folder_id,user_id,folder_name,save_date,access_date)
			values (folderID,userID,folderName,to_date(saveDate,'MM-DD-YY'),to_date(accessDate,'MM-DD-YY'));
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_addFolder, error='||sqlerrm);
	end;

/
	create or replace procedure SavedRecord_removeAllInFolder(folderID varchar2) is
	begin

		delete from saved_records where FOLDER_ID=folderID;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_removeAllInFolder, error='||sqlerrm);
	end;

/
	create or replace procedure SavedRecord_removeFolder(folderID varchar2,userID varchar2) is
	begin
		delete from saved_records where FOLDER_ID=folderID;
		delete from folder where user_id=userID and FOLDER_ID=folderID;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_removeFolder, error='||sqlerrm);
	end;

/

	create or replace procedure SavedRecord_renameFolder(folderID varchar2, userID varchar2, folderName varchar2,accessDate varchar2) is
	begin
		update folder set folder_name=folderName,access_date=to_date(accessDate,'mm-dd-yy') where user_id=userID and folder_id=folderID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_renameFolder, error='||sqlerrm);
	end;
/
	create or replace procedure SavedRecord_removeSelected(folderID varchar2,docID varchar2) is

	begin
		delete from saved_records where folder_ID=folderID and guid=docID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedRecord_Delete, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_insertSearch(searchID varchar2,
					userID varchar2,
					sessionID varchar2,
					maskInfo varchar2,
					searchType varchar2,
					emailAlert varchar2,
					savedInfo varchar2,
					EAWeek varchar2,
					searchPhrase1 varchar2,
					searchPhrase2 varchar2,
					searchPhrase3 varchar2,
					searchOption1 varchar2,
					searchOption2 varchar2,
					searchOption3 varchar2,
					boolean1 varchar2,
					boolean2 varchar2,
					resultsCount varchar2,
					subCounts varchar2,
					languageInfo varchar2,
					startYear varchar2,
					endYear varchar2,
					autoStem varchar2,
					sortOption varchar2,
					sortDirection varchar2,
					displayQuery varchar2,
					documentType varchar2,
					treatmentType varchar2,
					disciplineType varchar2,
					rfrxColls varchar2,
					last4Update varchar2,
					dupSetInfo varchar2,
					dedupInfo varchar2,
					dedupDB varchar2,
					refineStack varchar2,
					resultState varchar2) is
	begin

		 INSERT INTO SEARCHES(	SEARCH_ID,USER_ID,SESSION_ID,MASK,SEARCH_TYPE,SAVE_DATE,
								ACCESS_DATE,EMAIL_ALERT,SAVED,EMAILALERTWEEK,SEARCH_PHRASE_1,SEARCH_PHRASE_2,
								SEARCH_PHRASE_3,SEARCH_OPTION_1,SEARCH_OPTION_2,SEARCH_OPTION_3,BOOLEAN_1,
								BOOLEAN_2,RESULTS_COUNT,SUBCOUNTS,LANGUAGE,START_YEAR,END_YEAR,AUTOSTEMMING,
								SORT_OPTION,SORT_DIRECTION,DISPLAY_QUERY,DOCUMENT_TYPE,TREATMENT_TYPE,
								DISCIPLINE_TYPE,RFRX_COLLS,LASTUPDATES,DUPSET,DEDUP,DEDUPDB,REFINE_STACK,RESULTS_STATE)
					VALUES(    	searchID,userID,sessionID,maskInfo,searchType,sysdate,
								sysdate,emailAlert,savedInfo,EAWeek,searchPhrase1,searchPhrase2,
								searchPhrase3,searchOption1,searchOption2,searchOption3,boolean1,
								boolean2,resultsCount,subCounts,languageInfo,startYear,endYear,autoStem,
								sortOption,sortDirection,displayQuery,documentType,treatmentType,
								disciplineType,rfrxColls,last4Update,dupSetInfo,dedupInfo,dedupDB,refineStack,resultState);
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches.insertSearch, error='||sqlerrm);

	end;

/

	create or replace procedure Searches_updateSearches(searchID varchar2,dupsetInfo varchar2,dedupInfo varchar2,dedupdbInfo varchar2) is

	begin

		UPDATE SEARCHES SET DUPSET=dupsetInfo,DEDUP=dedupInfo,DEDUPDB=dedupdbInfo,ACCESS_DATE=SYSDATE WHERE SEARCH_ID = searchID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches.updateSearches, error='||sqlerrm);

	end;

/

	create or replace procedure Searches_removeSavedSearches(savedInfo varchar2,emailAlert varchar2,userID varchar2) is
	begin
		UPDATE SEARCHES SET SAVED=savedInfo, EMAIL_ALERT=emailAlert WHERE USER_ID=userID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_removeUserSavedSearches, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_removeSessionSearch(visibleInfo varchar2,sessionID varchar2) is
	begin
		UPDATE SEARCHES SET VISIBLE=visibleInfo WHERE SESSION_ID=sessionID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_removeSessionSearches, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_updateVisibility(visibleInfo varchar2,searchID varchar2) is
	begin
		UPDATE SEARCHES SET VISIBLE=visibleInfo, ACCESS_DATE=SYSDATE WHERE SEARCH_ID=searchID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_updateVisibility, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_updateSearch(userID varchar2,savedInfo varchar2,emailAlert varchar2,searchID varchar2) is
	begin
		UPDATE SEARCHES SET USER_ID=userID, SAVED=savedInfo, EMAIL_ALERT=emailAlert WHERE SEARCH_ID=searchID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_updateSearch, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_updateRefinements(refineStack varchar2,searchID varchar2) is
	begin
		UPDATE SEARCHES SET REFINE_STACK=refineStack WHERE SEARCH_ID=searchID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_updateSearchRefinements, error='||sqlerrm);
	end;

/

	create or replace procedure Searches_updateRefineState(resultsState varchar2,searchID varchar2)  is
	begin
		UPDATE SEARCHES SET RESULTS_STATE=resultsState, ACCESS_DATE=SYSDATE WHERE SEARCH_ID=searchID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Searches_updateRefineState, error='||sqlerrm);
	end;

/
	create or replace procedure SavedSearches_insertSearch(searchID varchar2,
					userID varchar2,
					sessionID varchar2,
					maskInfo varchar2,
					searchType varchar2,
					emailAlert varchar2,
					savedInfo varchar2,
					EAWeek varchar2,
					searchPhrase1 varchar2,
					searchPhrase2 varchar2,
					searchPhrase3 varchar2,
					searchOption1 varchar2,
					searchOption2 varchar2,
					searchOption3 varchar2,
					boolean1 varchar2,
					boolean2 varchar2,
					resultsCount varchar2,
					subCounts varchar2,
					languageInfo varchar2,
					startYear varchar2,
					endYear varchar2,
					autoStem varchar2,
					sortOption varchar2,
					sortDirection varchar2,
					displayQuery varchar2,
					documentType varchar2,
					treatmentType varchar2,
					disciplineType varchar2,
					rfrxColls varchar2,
					last4Update varchar2,
					dupSetInfo varchar2,
					dedupInfo varchar2,
					dedupDB varchar2,
					refineStack varchar2,
					resultState varchar2) is
	begin

		 INSERT INTO SEARCHES_SAVED(SEARCH_ID,USER_ID,SESSION_ID,MASK,SEARCH_TYPE,SAVE_DATE,
								ACCESS_DATE,EMAIL_ALERT,SAVED,EMAILALERTWEEK,SEARCH_PHRASE_1,SEARCH_PHRASE_2,
								SEARCH_PHRASE_3,SEARCH_OPTION_1,SEARCH_OPTION_2,SEARCH_OPTION_3,BOOLEAN_1,
								BOOLEAN_2,RESULTS_COUNT,SUBCOUNTS,LANGUAGE,START_YEAR,END_YEAR,AUTOSTEMMING,
								SORT_OPTION,SORT_DIRECTION,DISPLAY_QUERY,DOCUMENT_TYPE,TREATMENT_TYPE,
								DISCIPLINE_TYPE,RFRX_COLLS,LASTUPDATES,DUPSET,DEDUP,DEDUPDB,REFINE_STACK,RESULTS_STATE)
					VALUES(    	searchID,userID,sessionID,maskInfo,searchType,sysdate,
								sysdate,emailAlert,'On',EAWeek,searchPhrase1,searchPhrase2,
								searchPhrase3,searchOption1,searchOption2,searchOption3,boolean1,
								boolean2,resultsCount,subCounts,languageInfo,startYear,endYear,autoStem,
								sortOption,sortDirection,displayQuery,documentType,treatmentType,
								disciplineType,rfrxColls,last4Update,dupSetInfo,dedupInfo,dedupDB,refineStack,resultState);
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SavedSearches.insertSearch, error='||sqlerrm);

	end;
/

	CREATE OR REPLACE procedure NavigatorCache_addToCache(searchID varchar2,
										sessionID varchar2,
										database varchar2,
										year varchar2,
										controllTerm varchar2,
										classification varchar2,
										unControllTerm varchar2,
										serialTitle varchar2,
										publisher varchar2,
										author varchar2,
										authorAffiliation varchar2,
										documentType varchar2,
										language varchar2,
										country varchar2,
										patentKind varchar2,
										patentECLACode varchar2,
										patentAuthorityCode varchar2,
										patentUSCode varchar2,
										patentCitedIndex varchar2,
										patentIntCode varchar2 ) is
	begin
		INSERT INTO NAVIGATOR_CACHE (SEARCH_ID, SESSION_ID, DB, YR, CV, CL, FL, ST, PN, AU, AF, DT, LA, CO, PK, PEC, PAC , PUC, PCI, PID)
		VALUES(searchID,sessionID,database,year,controllTerm,classification,unControllTerm,serialTitle,publisher,author,authorAffiliation,documentType,language,country, patentKind,patentECLACode,patentAuthorityCode,patentUSCode, patentCitedIndex , patentIntCode);
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure NavigatorCache_addToCache, error='||sqlerrm);
	end;

/

	create or replace procedure SavedSearch_updateSearch(	userID varchar2,
											savedSetting varchar2,
											emailAlertSetting varchar2,
											queryID varchar2) is

	begin

		UPDATE SEARCHES_SAVED SET USER_ID=userID, SAVED=savedSetting, EMAIL_ALERT=emailAlertSetting WHERE SEARCH_ID=queryID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearches_updateSearch, error='||sqlerrm);
	end;
/
	create or replace procedure SavedSearch_removeSearch(searchID varchar2) is
	begin

		DELETE FROM SEARCHES_SAVED WHERE SEARCH_ID=searchID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearches_removeSearch, error='||sqlerrm);
	end;
/
	create or replace procedure SavedSearch_removeUserSearch(userID varchar2) is
	begin

		DELETE FROM SEARCHES_SAVED WHERE USER_ID=userID;
		commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearch_removeUserSearch, error='||sqlerrm);
	end;
/
	create or replace procedure SavedSearch_removeUserSearch1(emailAlert varchar2,userID varchar2) is
	begin

		UPDATE SEARCHES_SAVED SET EMAIL_ALERT=emailAlert WHERE USER_ID=userID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearch_removeUserSearch1, error='||sqlerrm);
	end;
/
	create or replace procedure SavedSearch_deleteAllSearch(userID varchar2) is
	begin
		DELETE FROM SAVED_SEARCHES_CC WHERE USER_ID=userID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearch_deleteAllSearch, error='||sqlerrm);
	end;
/
	create or replace procedure Searches_updateSearchDeDup(dedupString varchar2,dedupdbString varchar2,searchID varchar2) is
	begin
		UPDATE SEARCHES SET DEDUP=dedupString, DEDUPDB=dedupdbString,ACCESS_DATE=SYSDATE WHERE SEARCH_ID =searchID;
		commit;
	EXCEPTION

	   WHEN OTHERS THEN
			ROLLBACK;
		RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.Searches_updateSearchDeDup, error='||sqlerrm);
	end;
/
	create or replace procedure Searches_updateSearchDupset(depsetString varchar2,searchID varchar2) is
	begin
		UPDATE SEARCHES SET DUPSET=depsetString,ACCESS_DATE=SYSDATE WHERE SEARCH_ID = searchID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
		RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.Searches_updateSearchDupset, error='||sqlerrm);
	end;

/

	create or replace procedure SavedSearch_deleteCCSearch(searchID varchar2,userID varchar2) is
	begin
		DELETE FROM SAVED_SEARCHES_CC WHERE SEARCH_ID=searchID AND USER_ID=userID;
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearch_deleteCCSearch, error='||sqlerrm);
	end;
/
	create or replace procedure SavedSearch_storeCCSearch(searchID varchar2,userID varchar2,ccList varchar2) is
	begin
		INSERT INTO SAVED_SEARCHES_CC (SEARCH_ID,USER_ID,CC_LIST,SAVE_DATE) VALUES(searchID,userID,ccList,SYSDATE);
		commit;
	EXCEPTION
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure personalization.SavedSearch_deleteCCSearch, error='||sqlerrm);
	end;

/

	create or replace procedure DB_addAll(sessionID varchar2,
						searchID varchar2,
						DiHandle number,
						DiDocID varchar2,
						DiDocType varchar2,
						DiDatabase varchar2,
						QSearchQuery varchar2,
						QRecordCount varchar2) is
	cursor lockSession is
		select * from user_session where session_id = sessionID for update;
	maxSno number;
	BEGIN
		open lockSession;
		select max(sno) into maxSno from basket where session_id=sessionID;
		if maxSno is null then
			maxSno :=0;
		end if;
		maxSno :=maxSno+1;
		INSERT INTO basket(session_id,search_id,sno,di_handle,di_docid,di_doctype,di_database,q_searchquery,q_recordcount)
		VALUES(sessionID,searchID,maxSno,DiHandle,DiDocID,DiDocType,DiDatabase,QSearchQuery,QRecordCount);
		close lockSession;
		commit;
	EXCEPTION
		when DUP_VAL_ON_INDEX then
		begin
			begin
				if lockSession%isopen then
					close lockSession;
				end if;
			exception
				when others then
				null;
			end;
			begin
				rollback;
			end;
		end;
		when others then
			begin
				if lockSession%isopen then
					close lockSession;
				end if;
			exception
				when others then
					null;
			end;
			begin
				rollback;
			end;
			raise_application_error(-20101, 'error in procedure DocumentBasket_addAll, error='||sqlerrm);
	end;
/
	create or replace procedure DB_removeAll(sessionID varchar2) is

	begin
			Delete from BASKET where SESSION_ID=sessionID;
			commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure DocumentBasket_removeAll, error='||sqlerrm);
	end;

/
	create or replace procedure DB_removeSelected(sessionID varchar2,DiDocID varchar2,DiDatabase varchar2) is

	begin

		Delete from BASKET where SESSION_ID=sessionID and DI_DOCID=DiDocID and DI_DATABASE=DiDatabase;
		commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure DocumentBasket_removeSelected, error='||sqlerrm);
	end;

/
	create or replace procedure LookupSkipper_lookup_skipper(lookupSearchID varchar2, pageNumber number, sessionID varchar2, skipCount number) is
	begin
		insert into lookup_skipper(search_id,pnum,session_id,skip_count) values (lookupSearchID,pageNumber,sessionID,skipCount);
		commit;
	EXCEPTION
		   WHEN OTHERS THEN
				ROLLBACK;
			RAISE_APPLICATION_ERROR(-20001, 'Error in procedure LookupSkipper_lookup_skipper, error='||sqlerrm);
	end;
/
	create or replace procedure PageCache_writePage(sessionID varchar2,searchID varchar2,pageNumber number,lastTouched number,pageData varchar2) is
	begin
		insert into page_cache values(sessionID,searchID,pageNumber,pageData);
		commit;
	EXCEPTION
		WHEN DUP_VAL_ON_INDEX then
			ROLLBACK;
		WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20001, 'Error in procedure PageCache_writePage, error='||sqlerrm);
	end;
/
	create or replace procedure DB_updateClearOnNewSearch(sessionID varchar2,clearOnNewSearch number,lastTouched number) is
	rowCount number;
	begin
		update basket_state set clear_on_new_search = clearOnNewSearch,last_touched = lastTouched where session_id=sessionID;
		if SQL%NOTFOUND then
			insert into basket_state(session_id,clear_on_new_search,last_touched)
			values(sessionID,clearOnNewSearch,lastTouched);
		end if;
		commit;
	EXCEPTION
		   WHEN OTHERS THEN
				ROLLBACK;
			RAISE_APPLICATION_ERROR(-20001, 'Error in procedure DocumentBasket_updateClearOnNewSearch, error='||sqlerrm);
	end;

/
	create or replace procedure SessionBroker_createSession(sessionID varchar2,versionNumber number,contractID number, lastTouched number,userObject varchar2) is
	begin

		 insert into user_session (session_id,version_no,contract_id,last_touched,user_object)
		 values (sessionID,versionNumber,contractID,lastTouched,userObject);
		 commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SessionBroker_createSession. error='||sqlerrm);
	end;

/

	create or replace procedure SessionBroker_touch(sessionID varchar2,lastTouched number) is
	begin

		 update user_session set last_touched=lastTouched where session_id = sessionID;
		 commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SessionBroker_touch. error='||sqlerrm);
	end;

/

	create or replace procedure SessionBroker_updateSession(sessionID varchar2,versionNumber number,contractID number,lastTouched number,userObject varchar2) is
	begin
			update user_session set last_touched = lastTouched, user_object = userObject, contract_id = contractID, version_no = versionNumber where session_id = sessionID;
			delete from session_props where session_id = sessionID;
			commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SessionBroker_updateSession. error='||sqlerrm);
	end;

/
	create or replace procedure SessionBroker_updateSession1(sessionID varchar2,keyInfo varchar2,valueInfo varchar2) is
	begin
			if(instr(keyInfo,'ENV_') < 1) then
				insert into session_props values (sessionID,keyInfo,valueInfo);
			end if;

			commit;
	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure SessionBroker_updateSession1. error='||sqlerrm);
	end;

	/

	create or replace procedure ThesaurusPath_addStep(sessionID varchar2,contextData varchar2,titleData varchar2,linkData varchar2) is
		maxIndex number :=0;
		begin
			select max(index_num)+1 into maxIndex from thesaurus_path where session_id = sessionID order by index_num DESC;
			if maxIndex is null then
				maxIndex :=0;
			end if;
			insert into thesaurus_path(session_id,index_num,context,title,link) values (sessionID,maxIndex,contextData,titleData,linkData);
			commit;
		exception
			when others then
				rollback;
				raise_application_error(-20101, 'Error in procedure ThesaurusPath_addStep. error='||sqlerrm);
		end;

	/

		create or replace procedure ThesaurusPath_clearPathFrom(sessionID varchar2,indexNum number) is
		begin

			delete from thesaurus_path where session_id=sessionID and index_num>=indexNum;
			commit;

		exception
			when others then
				rollback;
				raise_application_error(-20101, 'Error in procedure ThesaurusPath_clearPathFrom. error='||sqlerrm);
		end;

	/

	create or replace procedure TrialUserBroker_addTrialUser(indexID varchar2,firstname varchar2,lastname varchar2,title varchar2,
					company varchar2,address1 varchar2,address2 varchar2,website varchar2,city varchar2,
					state varchar2,zipCode varchar2,countryName varchar2,phoneNumber varchar2,emailAddress varchar2,
					howHear varchar2,source varchar2,product varchar2,byMail varchar2,byEMail varchar2,
					referringURL varchar2,timeStamp Date) is
	begin
		insert into trial_user (index_id,first_name,last_name,job_title,company,address1,address2,web_site,city,
		state,zip,country,phone_number,email_address,how_hear,source,product,by_mail,by_email,referring_url,time_stamp)
		values(indexID,firstname,lastname,title,company,address1,address2,website,city,state,zipCode,countryName,
		phoneNumber,emailAddress,howHear,source,product,byMail,byEMail,referringURL,sysdate);
		commit;
	exception
		when others then
			rollback;
	      	raise_application_error(-20101, 'Error in procedure TrialUserBroker_addTrialUser. error='||sqlerrm);
	end;

	/

	create or replace procedure RSS_setQuery(rssID varchar2,rssQuery varchar2, dbMask varchar2, custID varchar2) is
	begin

		insert into  rss_query(rssid,query,database,customer_id,last_change)
		values(rssID,rssQuery,dbMask,custID,sysdate);
		commit;

	exception
		when others then
			rollback;
			raise_application_error(-20101, 'Error in procedure RSS_setQuery. error='||sqlerrm);
	end;

	/

	create or replace procedure RSS_touch(ID varchar2) is
	begin
		update rss_query set last_change=sysdate where  rssID=ID;
		commit;

	exception
		when others then
			rollback;
			raise_application_error(-20101, 'Error in procedure RSS_touch. error='||sqlerrm);
	end;

	/
	CREATE OR REPLACE PROCEDURE TAGS_ADDTAG
	(
	  t_id         VARCHAR2,
	  group_id     NUMBER,
	  tag          VARCHAR2 ,
	  tag_search   VARCHAR2 ,
	  doc_id       VARCHAR2 ,
	  timestamp    NUMBER,
	  scope        VARCHAR2 ,
	  db           NUMBER,
	  user_id      VARCHAR2 ,
	  customer_id  NUMBER)
	  is
	  begin

	  INSERT INTO TAGS(T_ID , GROUP_ID , TAG , TAG_SEARCH  , DOC_ID  , TIMESTAMP  , SCOPE, DB,   USER_ID, CUSTOMER_ID)
	  VALUES( t_id  , group_id   , tag  , tag_search , doc_id  ,  timestamp  , scope , db , user_id , customer_id);
	  commit;
	   EXCEPTION
	         WHEN OTHERS THEN
	              ROLLBACK;
	              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.addTag, error='||sqlerrm);

	  end;

	/

	CREATE OR REPLACE PROCEDURE TAGS_DELETERECORD
	(
	  docid        VARCHAR2 ,
	  userid        VARCHAR2,
	  custid        NUMBER)
	  is
	  begin

	  DELETE TAGS T WHERE T.CUSTOMER_ID = custid and T.USER_ID = userid and (LTRIM(RTRIM(T.DOC_ID))) =  docid;
	  commit;
	  EXCEPTION
	     WHEN OTHERS THEN
	              ROLLBACK;
	              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure TAGS_DELETERECORD, error='||sqlerrm);

  	end;

  	/

	CREATE OR REPLACE PROCEDURE TAGS_DELETETAG
	(
	  tagsearch     VARCHAR2 ,
	  userid        VARCHAR2,
	  custid        NUMBER,
	  docid         VARCHAR2,
	  scope         VARCHAR2)
	  is
	  begin

	  DELETE TAGS T WHERE T.CUSTOMER_ID = custid and T.USER_ID = userid and (LTRIM(RTRIM(T.TAG_SEARCH))) =  tagsearch and T.DOC_ID = docid and T.SCOPE = scope;
	  commit;
	  EXCEPTION
	     WHEN OTHERS THEN
		      ROLLBACK;
		      RAISE_APPLICATION_ERROR(-20101, 'Error in procedure TAGS_UPDATETAG, error='||sqlerrm);

	  end;
  	/

  	CREATE OR REPLACE PROCEDURE TAGS_UPDATETAG
	(
	  newtag        VARCHAR2 ,
	  newtag_search VARCHAR2 ,
	  tagsearch     VARCHAR2 ,
	  timestamp     NUMBER,
	  userid        VARCHAR2,
	  custid        NUMBER)
	  is
	  begin

	  UPDATE TAGS T SET T.TAG = newtag , T.TAG_SEARCH = newtag_search WHERE T.CUSTOMER_ID = custid and T.USER_ID = userid and (LTRIM(RTRIM(T.TAG_SEARCH))) =  tagsearch;
	  commit;
	  EXCEPTION
	     WHEN OTHERS THEN
	              ROLLBACK;
	              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure TAGS_UPDATETAG, error='||sqlerrm);

  	end;

  	/

  	CREATE OR REPLACE PROCEDURE TAGS_DELETETAGSEARCH
	(
	  tagsearch     VARCHAR2 ,
	  userid        VARCHAR2,
	  custid        NUMBER)
	  is
	  begin

	  DELETE TAGS T WHERE T.CUSTOMER_ID = custid and T.USER_ID = userid and (LTRIM(RTRIM(T.TAG_SEARCH))) =  tagsearch;
	  commit;
	  EXCEPTION
	     WHEN OTHERS THEN
	              ROLLBACK;
	              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure TAGS_DELETETAG, error='||sqlerrm);

  	end;
  	/

	CREATE OR REPLACE PROCEDURE TAGS_ADDAUTOCOMPLETE
	(
	  tagname         VARCHAR2)
	  is
	  begin

	  INSERT INTO LOOKUP_TAGS LT VALUES (tagname);
	  commit;
	  EXCEPTION
		 WHEN OTHERS THEN
		      ROLLBACK;

	end;
	/

	CREATE OR REPLACE PROCEDURE TAGS_DELETEAUTOCOMPLETE
	(
	  tagname         VARCHAR2)
	  is
	  begin

	  DELETE LOOKUP_TAGS LT WHERE LT.TAGNAME = tagname;
	  commit;
	  EXCEPTION
		 WHEN OTHERS THEN
		      ROLLBACK;
		      RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.LOOKUP_TAGS, error='||sqlerrm);

	  end;
	/
	CREATE OR REPLACE PROCEDURE TAGS_DELETETAG_SCOPE
	(
	  tagsearch     VARCHAR2 ,
	  userid        VARCHAR2,
	  custid        NUMBER,
	  docid         VARCHAR2,
	  scope         VARCHAR2)

	  is
	  begin

	  DELETE TAGS T WHERE T.CUSTOMER_ID = custid and T.USER_ID = userid and (LTRIM(RTRIM(T.TAG_SEARCH))) =  tagsearch and T.DOC_ID = docid and T.SCOPE = scope  ;
	  commit;
	  EXCEPTION
	     WHEN OTHERS THEN
	              ROLLBACK;
	              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure TAGS_DELETETAG_SCOPE, error='||sqlerrm);

  	end;

  	/
  	CREATE OR REPLACE PROCEDURE TAGS_EDITGROUP
	(
  		groupid     VARCHAR2,
  		groupdescirption VARCHAR2,
  		gcolor VARCHAR2)
  		is
  		begin

  		UPDATE TAG_GROUPS SET
  		DESCRIPTION = groupdescirption, COLOR = gcolor
  		WHERE GROUP_ID = groupid ;
  		commit;
  		EXCEPTION
         WHEN OTHERS THEN
              ROLLBACK;
              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.TAGS_EDITGROUP, error='||sqlerrm);

  	end;
	/

	CREATE OR REPLACE PROCEDURE TAGS_ADDGROUP
	(
  		groupid     VARCHAR2,
  		grouptitle  VARCHAR2,
  		userid      VARCHAR2,
  		groupdescirption VARCHAR2,
  		groupfounded LONG,
  		gcolor VARCHAR2)
  	is
  	begin

  	INSERT INTO TAG_GROUPS(GROUP_ID, GROUP_TITLE,DESCRIPTION,DATE_FOUNDED,COLOR, USER_ID)
  	VALUES ( groupid, grouptitle, groupdescirption , groupfounded, gcolor,userid );
  	commit;
  	EXCEPTION
         WHEN OTHERS THEN
              ROLLBACK;
              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.ADDGROUP, error='||sqlerrm);

  	end;
	/
	
	CREATE OR REPLACE PROCEDURE TAGS_ADDGROUPMEMBERS
	(
 		gid     VARCHAR2,
  		uid      VARCHAR2)
  	is
  	begin

  	INSERT INTO GROUP_MEMBERS (GROUP_ID, USER_ID)
  	VALUES( gid, uid );
  	commit;
   	EXCEPTION
         WHEN OTHERS THEN
              ROLLBACK;
              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.TAGS_ADDGROUPMEMBERS, error='||sqlerrm);

  	end;
	/
	
	
	CREATE OR REPLACE PROCEDURE TAGS_DELETEGROUP
	(
  		gid     VARCHAR2)
  	is
  	begin

  	DELETE TAG_GROUPS WHERE GROUP_ID = gid;
  	DELETE GROUP_MEMBERS WHERE GROUP_ID = gid;
  	DELETE TAGS WHERE GROUP_ID = gid;
  	DELETE CONSOLIDATED_TAGS WHERE GROUP_ID = gid;
  
  
  	commit;
   	EXCEPTION
         WHEN OTHERS THEN
              ROLLBACK;
              RAISE_APPLICATION_ERROR(-20101, 'Error in procedure Tags.TAG_GROUPS, error='||sqlerrm);

  end;
/

create or replace procedure update_backup_table is
begin
	insert into cpx_correction_backup select * from cpx_master_orig where ex in (select ex from cpx_correction_temp);
	commit;
exception
when others then
	rollback;
	raise_application_error(-20101,'Error '||sqlerrm);
end;

/

create or replace procedure update_master_table as

v_ex    cpx_correction_temp.ex%type;
counter number :=0;
cursor getAccessionNumber is select ex from cpx_correction_temp;


begin
	open getAccessionNumber;
	loop
	    fetch getAccessionNumber into v_ex;
	    begin
	    	  delete from cpx_master_orig where ex=v_ex;
	    	  insert into cpx_master_orig select * from cpx_correction_temp where ex = v_ex;
	    	  commit;
	    	  counter := counter+1;
	    	  dbms_output.put_line(counter || ' ' || v_ex);
	    exception
	    when others then
	          rollback;	          
	          dbms_output.put_line('Error in procedure update_temp_table, error='||sqlerrm);
	    end;
	    exit when getAccessionNumber%notfound;
	end loop;

 exception
	when others then
	    rollback;
	    RAISE_APPLICATION_ERROR(-20101, 'Error in procedure update_temp_table, error='||sqlerrm);
end;

/

create or replace procedure update_temp_table(v_update_number number) is

v_ex          cpx_correction_temp.ex%type;
v_m_id        cpx_correction_temp.m_id%type;
v_load_number cpx_correction_temp.load_number%type :=0;
v_doi         cpx_correction_temp.do%type;
v_t_doi       varchar2(128);
v_pn          cpx_correction_temp.pn%type;
v_pc          cpx_correction_temp.pc%type;
v_ps          cpx_correction_temp.ps%type;
v_py          cpx_correction_temp.py%type;
i	      number :=0;

cursor getSingleRecord is
	select ex,m_id,load_number,do,pn,pc,ps,py from cpx_master_orig where ex in(select ex from cpx_correction_temp);
	
begin
	open getSingleRecord;
	loop	   
	   fetch getSingleRecord into v_ex,v_m_id,v_load_number,v_doi,v_pn,v_pc,v_ps,v_py;
	  
	   select do into v_t_doi from cpx_correction_temp where ex=v_ex;
	   
	   if(v_t_doi is null or v_t_doi='') then
	    	update cpx_correction_temp set m_id=v_m_id,load_number=v_load_number,do=v_doi,pn=v_pn,pc=v_pc,ps=v_ps,py=v_py,update_number=v_update_number where ex=v_ex;
	   else
	    	update cpx_correction_temp set m_id=v_m_id,load_number=v_load_number,pn=v_pn,pc=v_pc,ps=v_ps,py=v_py,update_number=v_update_number where ex=v_ex;
	   end if;
	    	    
	    i :=i+1;
	    if(i=200) then
	    	commit;
	    	i :=0;
	    end if;
	  
	    exit when getSingleRecord%notfound;
	end loop;
	commit;
	
exception         
WHEN OTHERS THEN
	ROLLBACK;
	dbms_output.put_line('error= '||sqlerrm);
	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure update_temp_table, error='||sqlerrm);


end;
/

CREATE OR REPLACE PROCEDURE update_bd_backup_table(v_load_number NUMBER,dbName VARCHAR2) IS
	message VARCHAR2(4000);
	BEGIN
		INSERT INTO BD_CORRECTION_BACKUP SELECT * FROM BD_MASTER_ORIG WHERE DATABASE=dbName AND accessnumber IN (SELECT accessnumber FROM BD_CORRECTION_TEMP);
		INSERT INTO BD_TEMP_BACKUP SELECT * FROM BD_MASTER_ORIG WHERE DATABASE=dbName AND accessnumber IN (SELECT accessnumber FROM BD_CORRECTION_TEMP);
		COMMIT;
	EXCEPTION
	WHEN OTHERS THEN
		ROLLBACK;
		message :=SQLERRM;
		INSERT INTO BD_CORRECTION_ERROR VALUES('000000',v_load_number,systimestamp,message,'update_bd_backup_table');
		COMMIT;
		RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);
	END;
/

CREATE OR REPLACE PROCEDURE update_bd_master_table(v_update_number NUMBER,dbName VARCHAR2) AS

v_accessnumber    BD_CORRECTION_TEMP.accessnumber%TYPE :='0';
message VARCHAR2(4000);
CURSOR getAccessionNumber IS SELECT accessnumber FROM BD_CORRECTION_TEMP;

BEGIN
	FOR a IN getAccessionNumber LOOP
	    v_accessnumber :=a.accessnumber;
	    BEGIN
	    IF INSTR(v_accessnumber,'200138',1,1)<1 THEN
		  
		  INSERT INTO BD_CORRECTION_DELETION SELECT * FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND accessnumber=a.accessnumber;
		  DELETE FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND accessnumber=a.accessnumber;
		  IF SQL%NOTFOUND THEN
			INSERT INTO BD_CORRECTION_NEW SELECT * FROM BD_CORRECTION_TEMP WHERE accessnumber = a.accessnumber;

			INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (a.accessnumber,v_update_number,systimestamp,'record '||a.accessnumber||' not found on master table','update_bd_master_table');

		  END IF;

		  INSERT INTO BD_MASTER_ORIG SELECT * FROM BD_CORRECTION_TEMP WHERE accessnumber = a.accessnumber;

		  INSERT INTO BD_PRO_CORRECTION_LOG(ex,update_number,action_date,message,source) VALUES(a.accessnumber,v_update_number,systimestamp,'insert','update_bd_master_table');
	    ELSE
	    	  INSERT INTO BD_PRO_CORRECTION_LOG(ex,update_number,action_date,message,source) VALUES(a.accessnumber,v_update_number,systimestamp,'skip','update_bd_master_table');
	    END IF;

	    COMMIT;

	    EXCEPTION
	    WHEN OTHERS THEN
		  ROLLBACK;
		  message := SQLERRM;
		  INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (a.accessnumber,10000,systimestamp,'message','update_bd_master_table');
		  COMMIT;
		  RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);
	    END;

	END LOOP;

 EXCEPTION
	WHEN OTHERS THEN
	    ROLLBACK;
	    message := SQLERRM;
	    INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (v_accessnumber,v_update_number,systimestamp,message,'update_bd_master_table');
	    COMMIT;
	    RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);

END;
/

CREATE OR REPLACE PROCEDURE update_bd_temp_table(v_update_number NUMBER,fileName VARCHAR2,dbName VARCHAR2) IS

v_ex          BD_CORRECTION_TEMP.accessnumber%TYPE :='00';
v_m_id        BD_CORRECTION_TEMP.m_id%TYPE;
v_load_number BD_CORRECTION_TEMP.loadnumber%TYPE :=1;
v_doi         BD_CORRECTION_TEMP.doi%TYPE;
v_t_doi       VARCHAR2(128);
v_pn          BD_CORRECTION_TEMP.publishername%TYPE;
v_pc          BD_CORRECTION_TEMP.publisheraddress%TYPE;
v_ur	      BD_CORRECTION_TEMP.updateresource%TYPE;
v_uc 	      BD_CORRECTION_TEMP.updatecodestamp%TYPE;
v_ut	      BD_CORRECTION_TEMP.updatetimestamp%TYPE;
v_snum	      BD_CORRECTION_TEMP.SEQ_NUM%TYPE;

message       VARCHAR2(4000);

CURSOR getSingleRecord IS
	SELECT accessnumber,m_id,loadnumber,doi,publishername,publisheraddress,updatecodestamp,updateresource,updatetimestamp,SEQ_NUM
	FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND accessnumber IN(SELECT accessnumber FROM BD_CORRECTION_TEMP);

BEGIN
	OPEN getSingleRecord;
	LOOP
	   FETCH getSingleRecord INTO v_ex,v_m_id,v_load_number,v_doi,v_pn,v_pc,v_uc,v_ur,v_ut,v_snum;
	   EXIT WHEN getSingleRecord%NOTFOUND;
	   SELECT doi INTO v_t_doi FROM BD_CORRECTION_TEMP WHERE accessnumber=v_ex;

	   IF(v_t_doi IS NULL OR v_t_doi='') THEN
		UPDATE BD_CORRECTION_TEMP SET m_id=v_m_id,loadnumber=v_load_number,doi=v_doi,publishername=v_pn,publisheraddress=v_pc,
		updatenumber=v_update_number,updatecodestamp=v_uc||' '||'UPDATE',updateresource=v_ur||' '||fileName,updatetimestamp=systimestamp,SEQ_NUM=v_snum WHERE accessnumber=v_ex;
	   ELSE
		UPDATE BD_CORRECTION_TEMP SET m_id=v_m_id,loadnumber=v_load_number,publishername=v_pn,publisheraddress=v_pc,updatenumber=v_update_number,
		updatecodestamp=v_uc||' '||'UPDATE',updateresource=v_ur||' '||fileName,updatetimestamp=systimestamp,SEQ_NUM=v_snum WHERE accessnumber=v_ex;
	   END IF;
	 COMMIT;
	 END LOOP;

	CLOSE getSingleRecord;

	UPDATE BD_CORRECTION_TEMP SET updatenumber=v_update_number,updatecodestamp='NEW',updateresource=fileName,updatetimestamp=systimestamp WHERE updatenumber IS NULL;
	COMMIT;

EXCEPTION
WHEN OTHERS THEN
	ROLLBACK;
	message := SQLERRM;
	INSERT INTO BD_CORRECTION_ERROR VALUES (v_ex,v_update_number,systimestamp,message,'update_bd_temp_table');
	COMMIT;
	dbms_output.put_line('error= '||SQLERRM);
	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure update_bd_temp_table, error='||SQLERRM);

END;
/

CREATE OR REPLACE PROCEDURE delete_bd_lookup_table AS

v_field    	DELETED_LOOKUPINDEX.field%TYPE;
v_term    	DELETED_LOOKUPINDEX.term%TYPE;
v_database    	DELETED_LOOKUPINDEX.DATABASE%TYPE;

message VARCHAR2(4000);
CURSOR getlookupData IS SELECT field,term,DATABASE FROM DELETED_LOOKUPINDEX;

BEGIN
	FOR a IN getlookupData LOOP
	    v_field :=a.field;
	    v_term :=a.term;
	    v_database :=a.DATABASE;
	    BEGIN
	    	  IF v_field='AU' THEN

		  	DELETE FROM CMB_AU_LOOKUP WHERE DBASE = v_database AND display_name=UPPER(v_term);

		  ELSIF v_field='AF' THEN

		  	DELETE FROM CMB_AF_LOOKUP WHERE DBASE = v_database AND institute_name=UPPER(v_term);

		  ELSIF v_field='PN' THEN

		  	DELETE FROM CMB_PN_LOOKUP WHERE DBASE = v_database AND publisher_name=UPPER(v_term);

		  ELSIF v_field='ST' THEN

		  	DELETE FROM CMB_ST_LOOKUP WHERE DBASE = v_database AND source_title=UPPER(v_term);
		  END IF;
		  INSERT INTO BD_LOOKUPINDEX_LOG(field,term,DATABASE,TIMESTAMP) VALUES (v_field,v_term,v_database,systimestamp);
		  COMMIT;

	    EXCEPTION
	    WHEN OTHERS THEN
		message := SQLERRM;
		INSERT INTO BD_LOOKUPINDEX_ERROR(field,term,DATABASE,TIMESTAMP,message) VALUES (v_field,v_term,v_database,systimestamp,message);
	    COMMIT;
	    END;

	END LOOP;

 EXCEPTION
	WHEN OTHERS THEN
	    ROLLBACK;
	    message := SQLERRM;
	    INSERT INTO BD_LOOKUPINDEX_ERROR(field,term,DATABASE,TIMESTAMP,message) VALUES (v_field,v_term,v_database,systimestamp,message);
	    COMMIT;

END;
/

CREATE OR REPLACE PROCEDURE delete_bd_master_table(v_update_number NUMBER,fileName VARCHAR2,dbName VARCHAR2) AS

v_accessnumber    BD_CORRECTION_TEMP.accessnumber%TYPE :='0';
message VARCHAR2(4000);
CURSOR getAccessionNumber IS SELECT accessnumber FROM BD_CORRECTION_TEMP;

BEGIN
	FOR a IN getAccessionNumber LOOP
	    v_accessnumber :=a.accessnumber;
	    BEGIN
	    	  UPDATE BD_CORRECTION_TEMP SET UPDATENUMBER=v_update_number,updateresource=fileName,UPDATECODESTAMP='DELETE',
		  updatetimestamp=systimestamp,loadnumber=loadnumber;
	          commit;
		  UPDATE BD_MASTER_ORIG SET UPDATENUMBER=v_update_number,updateresource=updateresource||' '||fileName,UPDATECODESTAMP='DELETE',
		  updatetimestamp=systimestamp,accessnumber='D'||a.accessnumber,loadnumber='3'||loadnumber WHERE accessnumber=a.accessnumber AND DATABASE=dbName;
		  IF SQL%NOTFOUND THEN
			INSERT INTO BD_CORRECTION_NEW SELECT * FROM BD_CORRECTION_TEMP WHERE accessnumber = a.accessnumber;

			INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (a.accessnumber,v_update_number,sysdate,'record '||a.accessnumber||' not found on master table','delete_bd_master_table');

			COMMIT;
		  ELSE
			INSERT INTO BD_PRO_CORRECTION_LOG(ex,update_number,action_date,message,source) VALUES (a.accessnumber,v_update_number,sysdate,'delete','update_bd_master_table');

		 END IF;

		 COMMIT;

	    EXCEPTION
	    WHEN OTHERS THEN
		ROLLBACK;
		message := SQLERRM;
		INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (v_accessnumber,v_update_number,sysdate,message,'delete_bd_master_table');
		COMMIT;
		RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);
	    END;

	END LOOP;

 EXCEPTION
	WHEN OTHERS THEN
	    ROLLBACK;
	    message := SQLERRM;
	    dbms_output.put_line('***********************************************************');
	    INSERT INTO BD_CORRECTION_ERROR(ex,update_number,action_date,message,source) VALUES (v_accessnumber,v_update_number,SYSDATE,message,'delete_bd_master_table');
	    COMMIT;
	    RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);

END;
/

/*********************************************************************************************/

CREATE OR REPLACE PROCEDURE update_aip_backup_table(v_load_number NUMBER,dbName VARCHAR2) IS
	message VARCHAR2(4000);
	BEGIN
		INSERT INTO BD_AIP_BACKUP SELECT * FROM BD_MASTER_ORIG WHERE DATABASE=dbName AND pui IN (SELECT pui FROM BD_AIP_TEMP);
		INSERT INTO BD_AIP_TEMP_BACKUP SELECT * FROM BD_MASTER_ORIG WHERE DATABASE=dbName AND pui IN (SELECT pui FROM BD_AIP_TEMP);
		COMMIT;
	EXCEPTION
	WHEN OTHERS THEN
		ROLLBACK;
		message :=SQLERRM;
		INSERT INTO BD_AIP_ERROR VALUES('000000',v_load_number,systimestamp,message,'update_aip_backup_table');
		COMMIT;
		RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);
END;
/

CREATE OR REPLACE PROCEDURE update_aip_temp_table(v_update_number NUMBER,fileName VARCHAR2,dbName VARCHAR2) IS

v_ex          BD_AIP_TEMP.accessnumber%TYPE :='00';
v_m_id        BD_AIP_TEMP.m_id%TYPE;
v_load_number BD_AIP_TEMP.loadnumber%TYPE :=1;
v_pn          BD_AIP_TEMP.publishername%TYPE;
v_pc          BD_AIP_TEMP.publisheraddress%TYPE;
v_ur	      BD_AIP_TEMP.updateresource%TYPE;
v_uc 	      BD_AIP_TEMP.updatecodestamp%TYPE;
v_ut	      BD_AIP_TEMP.updatetimestamp%TYPE;
v_snum	      BD_AIP_TEMP.SEQ_NUM%TYPE;
v_pui	      BD_AIP_TEMP.pui%TYPE;

message       VARCHAR2(4000);

CURSOR getSingleRecord IS
	SELECT pui,accessnumber,m_id,loadnumber,updatecodestamp,updateresource,updatetimestamp,SEQ_NUM
	FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND pui IN(SELECT pui FROM BD_AIP_TEMP);

BEGIN
	OPEN getSingleRecord;
	LOOP
	   FETCH getSingleRecord INTO v_pui,v_ex,v_m_id,v_load_number,v_uc,v_ur,v_ut,v_snum;
	   EXIT WHEN getSingleRecord%NOTFOUND;

	   UPDATE BD_AIP_TEMP SET m_id=v_m_id,loadnumber=v_load_number,updatenumber=v_update_number,
	   updatecodestamp=v_uc||' '||'AIP',updateresource=v_ur||' '||fileName,updatetimestamp=systimestamp,SEQ_NUM=v_snum WHERE pui=v_pui;

	   COMMIT;
	 END LOOP;

	CLOSE getSingleRecord;

	UPDATE BD_AIP_TEMP SET updatenumber=v_update_number,updatecodestamp='AIP',updateresource=fileName,updatetimestamp=systimestamp WHERE updatenumber IS NULL;
	COMMIT;

EXCEPTION
WHEN OTHERS THEN
	ROLLBACK;
	message := SQLERRM;
	INSERT INTO BD_AIP_ERROR VALUES (v_pui,v_update_number,systimestamp,message,'update_aip_temp_table');
	COMMIT;
	dbms_output.put_line('error= '||SQLERRM);
	RAISE_APPLICATION_ERROR(-20101, 'Error in procedure update_aip_temp_table, error='||SQLERRM);

END;

/

CREATE OR REPLACE PROCEDURE update_aip_master_table(v_update_number NUMBER,dbName VARCHAR2) AS

v_pui    BD_AIP_TEMP.pui%TYPE :='0';
message VARCHAR2(4000);
CURSOR getPui IS SELECT pui FROM BD_AIP_TEMP;

BEGIN
	FOR a IN getPui LOOP
	    v_pui :=a.pui;
	    BEGIN

		  DELETE FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND pui=a.pui;
		  IF SQL%NOTFOUND THEN
			INSERT INTO BD_AIP_NEW SELECT * FROM BD_AIP_TEMP WHERE pui = a.pui;
			INSERT INTO BD_AIP_ERROR(ex,update_number,action_date,message,source) VALUES (a.pui,v_update_number,systimestamp,'record '||a.pui||' not found on master table','update_aip_master_table');
		  	INSERT INTO BD_MASTER_ORIG SELECT * FROM BD_AIP_TEMP WHERE pui = a.pui;
		  	INSERT INTO BD_PRO_AIP_LOG(ex,update_number,action_date,message,source) VALUES(a.pui,v_update_number,systimestamp,'insert','update_aip_master_table');
		  ELSE
		  	INSERT INTO BD_AIP_DELETION SELECT * FROM BD_MASTER_ORIG WHERE DATABASE = dbName AND pui=a.pui;
		  	INSERT INTO BD_MASTER_ORIG SELECT * FROM BD_AIP_TEMP WHERE pui = a.pui;
		  	INSERT INTO BD_PRO_AIP_LOG(ex,update_number,action_date,message,source) VALUES(a.pui,v_update_number,systimestamp,'update','update_aip_master_table');
		  END IF;

	    COMMIT;
	    EXCEPTION
	    WHEN OTHERS THEN
		  ROLLBACK;
		  message := SQLERRM;
		  INSERT INTO BD_AIP_ERROR(ex,update_number,action_date,message,source) VALUES (a.pui,10000,systimestamp,message,'update_aip_master_table');
		  COMMIT;
		  RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);
	    END;

	END LOOP;

 EXCEPTION
	WHEN OTHERS THEN
	    ROLLBACK;
	    message := SQLERRM;
	    INSERT INTO BD_AIP_ERROR(ex,update_number,action_date,message,source) VALUES (v_pui,v_update_number,systimestamp,message,'update_aip_master_table');
	    COMMIT;
	    RAISE_APPLICATION_ERROR(-20101,'Error '||SQLERRM);

END;
/

