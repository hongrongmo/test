package org.ei.fulldoc;

/*
*	This class is designed for use inside of the XSL to check to see
*	if the customer should see the full text button.
*/

public class FullTextOptions
{

	private static final String IEEE_PUBPREFIX = "10.1109";
	private static final String IOP_PUBPREFIX = "10.1088";
	private static final String ELSEVIER_PUBPREFIX = "10.1016";
	private static final String ACADEMIC_PRESS_PUBPREFIX = "10.1006";

	private static final String ANALOG_CUSTID = "3797";

	public static String checkFullText(String backofficeFlag,
									   String dataFlag,
									   String customerID,
									   String doi,
									   String dbmask)
	{
		/*
		System.out.println("####################");
		System.out.println("Backoffice flag:"+backofficeFlag);
		System.out.println("Data flag:"+dataFlag);
		System.out.println("CustomerID:"+customerID);
		System.out.println("DOI:"+doi);
		System.out.println("dbmask:"+dbmask);
		*/

		if((backofficeFlag.equals("true") && dataFlag.equals("Y")) || dataFlag.equals("A"))
		{
			return checkFullText(customerID,doi,dbmask);
		}

		return "false";
	}

	public static String checkFullText(String customerID,
									   String doi,
									   String dbmask)
	{
		/*
		* Handle the customer specific logic
		*/

		if(customerID != null &&
		   customerID.equals(ANALOG_CUSTID))
		{
			/*
			* This is Analog Devices so run the Analog devices logic
			*/
			return analogDevicesLogic(doi, dbmask);
		}

		return "true";
	}

	private static String analogDevicesLogic(String doi,
											 String dbmask)
	{

		/*
		* Handle the database specific logic for Analog Devices.
		*/

		if(dbmask != null && (dbmask.equals("1") || dbmask.equals("2")))
		{
			/*
			*	Handle the DOI publisher prefix logic Compendex and Inspec
			*/

			if(doi != null && ((doi.indexOf(IEEE_PUBPREFIX) == 0) ||
			                   (doi.indexOf(IOP_PUBPREFIX) == 0)  ||
			                   (doi.indexOf(ELSEVIER_PUBPREFIX) == 0)  ||
			                   (doi.indexOf(ACADEMIC_PRESS_PUBPREFIX) == 0)))
			{
				return "true";
			}
			else
			{
				return "false";
			}
		}

		return "true";
	}
}
