
Once in a while you may be asked to 'remove' a user's email alerts.
For a varitery of reasons.
Zhun Yang worked on a spec for a tool to do this once.  Was not completed.

What we do is just turn the alerts related to the email address to 'Off'.
We do not remove the personalization account and orphan other Folders, Saved Records and Saved Searches.
--------------------------------------------------------------------
Here is the code/procedure for doing it
--------------------------------------------------------------------
CONNECT TO THE LIVE AP_EV_SESSION SCHEMA

SELECT USER_PROFILE_ID,EMAIL FROM USER_PROFILE_CONTRACT WHERE LOWER(EMAIL) LIKE LOWER('%<EMAIL_ADDRESS>%')
--> CHECK THE EMAIL, COPY USER_PROFILE_ID

SELECT * FROM SEARCHES_SAVED WHERE USER_ID='<USER_PROFILE_ID>'
--> CHECK TO SEE HOW MANY RECORDS WILL BE AFFECTED.  EXAMINE RESULTS.
--> IF UNSURE DO NOT CONTINUE.

UPDATE SEARCHES_SAVED SET EMAIL_ALERT='Off' WHERE USER_ID='<USER_PROFILE_ID>';

COMMIT;


NOTES ON: GE India - Edutech
------------------------
UPDATE  SEARCHES_SAVED  SET MASK = 1 WHERE USER_ID='3dfcbf314f06ea1M7c2b1000103' AND EMAIL_ALERT='On' AND MASK=3
