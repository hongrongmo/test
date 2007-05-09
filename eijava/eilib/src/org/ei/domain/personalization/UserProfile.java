//package structure.
package org.ei.domain.personalization;

import org.ei.domain.InvalidArgumentException;

/**
	* This class is a basic type to hold all the parameters
    * required for creating the account,editing the account.
    * string reprentation of user profile object,
    * xml string representation of user profile object
    * and comparision of the objects.
    */

public class UserProfile
{
		private String nUserId;
		private String sCustomerId;
		private String sContractId;
		private String sEmail;
		private String sFirstName;
		private String sLastName;
		private String sTitle;
		private String sAnnounceFlag;
		private String sPassword;

		public UserProfile()
		{}
		public UserProfile(String userId)
		{
			this.nUserId = userId;
		}
		public String getUserId()
		{
			return nUserId;
		}
		/**
		    * set cunstomer id to instance variable.
		    * @ param : customer id.
		    */
		public void setCustomerId(String customerId)
		{
			this.sCustomerId = customerId;
		}

		/**
			* get the value of customer id.
			* @ return : String
			*/
		public String getCustomerId()
		{
			return sCustomerId;
		}
		/**
		* set contract id to instance variable.
		* @ param : contract id.
		*/
		public void setContractId(String contractId)
		{
			this.sContractId = contractId;
		}

		/**
		* get the value of contract id.
		* @ return : String
		*/
		public String getContractId()
		{
			return sContractId;
		}

		/**
		    * set email to instance variable.
		    * @ param : email.
		    */
		public void setEmail(String email) throws InvalidArgumentException
		{
			if ( (email == null) || (email.trim().length() == 0))
			{
				throw new InvalidArgumentException(" Email Address Not Found");
			}
			this.sEmail = email;
		}

		/**
			* get the value of email.
			* @ return : String
			*/
		public String getEmail()
		{
			return sEmail;
		}
		/**
		   * set first name to instance variable,if the parameter is not available it will throw an exception.
		    * @ param : firstname.
		    */
		public void setFirstName(String firstName) throws InvalidArgumentException
		{
			if( (firstName == null) || (firstName.trim().length() == 0))
			{
				throw new InvalidArgumentException("First Name Not Found");
			}
			this.sFirstName = firstName;
		}

		/**
			* get the value of first name.
			* @ return : String
			*/
		public String getFirstName()
		{
			return sFirstName;
		}
		/**
		    * set last name to instance variable,if the parameter is not available it will throw an exception.
		    * @ param : lastname.
		    */
		public void setLastName(String lastName) throws InvalidArgumentException
		{
			if( (lastName == null) || (lastName.trim().length() == 0))
			{
				throw new InvalidArgumentException("Last Name Not Found");
			}
			this.sLastName = lastName;
		}

		/**
			* get the value of last name.
			* @ return : String
			*/
		public String getLastName()
		{
			return sLastName;
		}
		/**
		    * set title to instance variable.
		    * @ param : title.
		    */
		public void setTitle(String title)
		{
			this.sTitle = title;
		}

		/**
			* get the value of title name.
			* @ return : String
			*/
		public String getTitle()
		{
			return sTitle;
		}
		/**
		    * set announce flag to instance variable.
		    * @ param : announce flag.
		    */
		public void setAnnounceFlag(String announceFlag)
		{
			this.sAnnounceFlag = announceFlag;
		}

		/**
			* get the value of announce flag.
			* @ return : String
			*/
		public String getAnnounceFlag()
		{
			return sAnnounceFlag;
		}
		/**
			* This method will return the String representation of the UserProfile object.
			* @ return : string representation of user object.
			*/
		/**
			* get the value of password.
			* @ return : String
			*/
		public String getPassword()
		{
			return sPassword;
		}
		/**
		    * set last name to instance variable,if the parameter is not available it will throw an exception.
		    * @ param : lastname.
		    */
		public void setPassword(String password) throws InvalidArgumentException
		{
			if( (password == null) || (password.trim().length() == 0))
			{
				throw new InvalidArgumentException("Password Not Found");
			}
			this.sPassword = password;
		}
		public String toString()
		{
			StringBuffer profileString = new StringBuffer("USER PROFILE OBJECT");
			profileString.append("\nUser Id:").append(nUserId)
								.append("\nCustomer Id:").append(sCustomerId)
								.append("\nContract Id:").append(sContractId)
								.append("\nEmail:").append(sEmail)
								.append("\nFirst Name:").append(sFirstName)
								.append("\nLast Name:").append(sLastName)
								.append("\nTitle:").append(sTitle)
								.append("\nAnnounce Flag:").append(sAnnounceFlag)
								.append("\nPassword:").append(sPassword);

			return profileString.toString();
		}
		/**
			* This method will return the XML String representation of the UserProfile object.
			* @ return : xml string representation of user object.
			*/
		public String toXMLString()
		{
			StringBuffer profileString = new StringBuffer("<USER-PROFILE>");
			profileString.append("<USER-ID>").append(nUserId).append("</USER-ID>")
								.append("<CUSTOMER-ID>").append(sCustomerId).append("</CUSTOMER-ID>")
								.append("<CONTRACT-ID>").append(sContractId).append("</CONTRACT-ID>")
								.append("<EMAIL>").append(sEmail).append("</EMAIL>")
								.append("<FIRST-NAME>").append(sFirstName).append("</FIRST-NAME")
								.append("<LAST- NAME>").append(sLastName).append("</LAST-NAME>")
								.append("<TITLE>").append(sTitle).append("</TITLE>")
								.append("<ANNOUNCE-FLAG>").append(sAnnounceFlag).append("</ANNOUNCE-FLAG>")
								.append("<PASSWORD>").append(sPassword).append("</PASSWORD>")
								.append("</USER-PROFILE>");
			return profileString.toString();
		}
		/**
		* this method compares the two objects based on the user id .
		* @return : true , if the two objects are equal
		*               : false , if the two objects are not equal.
		*/
		public boolean equals(Object object) throws ClassCastException
		{
				if(object == null)
				{
						return false;
				}
				else
				{
						UserProfile profileObj = (UserProfile) object;
						if(this.getUserId() == profileObj.getUserId())
						{
								return true;
						}
						else
						{
								return false;
						}
				}
		}
		/**
		* Compares the Object with the specified object for order
		* Method is implemented to get Sorted UserProfile list.
		* @exception  : ClassCastException
		* @return   : -1 if null or if less upon comparison
		*/
		public int compareTo(Object object) throws ClassCastException
		{
				if(object == null)
				{
					  return -1;
				}
				else
				{
						UserProfile profileObj=(UserProfile)object;
						String objString= profileObj.toString();
						String thisString= this.toString();
						return thisString.compareTo(objString);
				}
		}
	}




