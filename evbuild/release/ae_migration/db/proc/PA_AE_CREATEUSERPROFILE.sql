create or replace 
procedure PA_ae_createUserProfile(userProfileID varchar2,contractID varchar2,customerID varchar2,
			  userTitle varchar2,firstName varchar2,lastname varchar2,emailAddress varchar2,userPassword varchar2,
			  addDate varchar2,accessDate varchar2,announceFlag varchar2, webUserId varchar2, merged varchar2) is
	begin
			INSERT INTO USER_PROFILE_CONTRACT(USER_PROFILE_ID, CONTRACT_ID,CUSTOMER_ID,TITLE,FIRST_NAME,LAST_NAME, EMAIL,PASSWORD,ADD_DATE,ACCESS_DATE,ANNOUNCE_FLAG, AE_WEB_USER_ID, MERGED)
			VALUES(userProfileID,contractID,customerID,userTitle,firstName,lastName,emailAddress,userPassword,TO_DATE(addDate,'MM-DD-YYYY'),TO_DATE(accessDate,'MM-DD-YYYY'),announceFlag, TO_NUMBER(webUserId), merged);
			commit;

	EXCEPTION
	   WHEN OTHERS THEN
			ROLLBACK;
			RAISE_APPLICATION_ERROR(-20101, 'Error in procedure PersonalAccount_createUserProfile, error='||sqlerrm);
	end PA_ae_createUserProfile;