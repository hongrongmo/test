package org.ei.dataloading.bd.loadtime;

import java.util.*;

public class BdNumericalIndexmapping
{
	public static Map nimapping = new HashMap();
	/*
	nimapping.put("amount_of_substance_minimum","AMOUNT_OF_SUBSTANCE_MINIMUM");
	nimapping.put("amount_of_substance_maximum","AMOUNT_OF_SUBSTANCE_MAXIMUM");
	nimapping.put("amount_of_substance_text","AMOUNT_OF_SUBSTANCE_TEXT");
	
	nimapping.put("capacitance_minimum","CAPACITANCE_MINIMUM");
	nimapping.put("capacitance_maximum","CAPACITANCE_MAXIMUM");
	nimapping.put("capacitance_text","CAPACITANCE_TEXT");
	
	nimapping.put("electric_current_minimum","ELECTRIC_CURRENT_MINIMUM");
	nimapping.put("electric_current_maximum","ELECTRIC_CURRENT_MAXIMUM");
	nimapping.put("electric_current_text","ELECTRIC_CURRENT_TEXT");
	
	nimapping.put("frequency_minimum","FREQUENCY_MINIMUM");
	nimapping.put("frequency_maximum","FREQUENCY_MAXIMUM");
	nimapping.put("frequency_text","FREQUENCY_TEXT");
	
	nimapping.put("voltage_minimum","VOLTAGE_MINIMUM");
	nimapping.put("voltage_maximum","VOLTAGE_MAXIMUM");
	nimapping.put("voltage_text","VOLTAGE_TEXT");
	*/
	public static String get(String input)
	{
		return nimapping.get(input);
	}
	
}
