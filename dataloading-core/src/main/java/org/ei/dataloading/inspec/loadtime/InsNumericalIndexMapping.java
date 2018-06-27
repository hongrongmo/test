package org.ei.dataloading.inspec.loadtime;

import java.util.*;

import org.ei.dataloading.*;

public class InsNumericalIndexMapping
{
	public static HashMap nimapping = new HashMap();
	public InsNumericalIndexMapping()
	{
		
		//current		
		nimapping.put("current_text",EVCombinedRec.ELECTRIC_CURRENT_TEXT);
		nimapping.put("current_ranges",EVCombinedRec.ELECTRIC_CURRENT_RANGES);
		
		//mass		
		nimapping.put("mass_text",EVCombinedRec.MASS_TEXT);
		nimapping.put("mass_ranges",EVCombinedRec.MASS_RANGES);
		
		//temperature
		nimapping.put("temperature_text",EVCombinedRec.TEMPERATURE_TEXT);
		nimapping.put("temperature_ranges",EVCombinedRec.TEMPERATURE_RANGES);
		
		//time
		nimapping.put("time_text",EVCombinedRec.TIME_TEXT);
		nimapping.put("time_ranges",EVCombinedRec.TIME_RANGES);
		
		//size
		nimapping.put("size_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("size_ranges",EVCombinedRec.SIZE_RANGES);
		
		//electrical conductivity		
		nimapping.put("electrical_conductivity_text",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT);
		nimapping.put("electrical_conductivity_ranges",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES);
		
		//voltage
		nimapping.put("voltage_text",EVCombinedRec.VOLTAGE_TEXT);
		nimapping.put("voltage_ranges",EVCombinedRec.VOLTAGE_RANGES);
		
		//energy
		nimapping.put("energy_text",EVCombinedRec.ENERGY_TEXT); 
		nimapping.put("energy_ranges",EVCombinedRec.ENERGY_RANGES);
		
		//resistance
		nimapping.put("resistance_text",EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT); 
		nimapping.put("resistance_ranges",EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES); 
		
		//resistivity
		nimapping.put("resistivity_text",EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT);
		nimapping.put("resistivity_ranges",EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES);
		
		//electron volt energy
		nimapping.put("electron_volt_energy_text",EVCombinedRec.ELECTRON_VOLT_TEXT); 
		nimapping.put("electron_volt_energy_ranges",EVCombinedRec.ELECTRON_VOLT_RANGES); 
		
		//capacitance
		nimapping.put("capacitance_text",EVCombinedRec.CAPACITANCE_TEXT);	
		nimapping.put("capacitance_ranges",EVCombinedRec.CAPACITANCE_RANGES);	
		
		//frequency		
		nimapping.put("frequency_text",EVCombinedRec.FREQUENCY_TEXT);
		nimapping.put("frequency_ranges",EVCombinedRec.FREQUENCY_RANGES);
				
		//power		
		nimapping.put("power_text",EVCombinedRec.POWER_TEXT);  
		nimapping.put("power_ranges",EVCombinedRec.POWER_RANGES);  
		
		//apparent power 
		nimapping.put("apparent_power_text",EVCombinedRec.APPARENT_POWER_TEXT); 
		nimapping.put("apparent_power_ranges",EVCombinedRec.APPARENT_POWER_RANGES);
		
		//magnetic flux density
		nimapping.put("magnetic_flux_density_text",EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT);
		nimapping.put("magnetic_flux_density_ranges",EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES);

		//bit rate
		nimapping.put("bit_rate_text",EVCombinedRec.BIT_RATE_TEXT);
		nimapping.put("bit_rate_ranges",EVCombinedRec.BIT_RATE_RANGES);			
		
		//pressure
		nimapping.put("pressure_text",EVCombinedRec.PRESSURE_TEXT); 
		nimapping.put("pressure_ranges",EVCombinedRec.PRESSURE_RANGES);
		
		//velocity		
		nimapping.put("velocity_text",EVCombinedRec.VELOCITY_TEXT);   
		nimapping.put("velocity_ranges",EVCombinedRec.VELOCITY_RANGES); 
		
		//age		
		nimapping.put("age_text",EVCombinedRec.AGE_TEXT); 
		nimapping.put("age_ranges",EVCombinedRec.AGE_RANGES); 

		//radioactivity 
		nimapping.put("radioactivity_text",EVCombinedRec.RADIOACTIVITY_TEXT); 
		nimapping.put("radioactivity_ranges",EVCombinedRec.RADIOACTIVITY_RANGES); 
		
		//absorbed dose		
		nimapping.put("radiation_absorbed_dose_text",EVCombinedRec.ABSORBED_DOSE_TEXT); 
		nimapping.put("radiation_absorbed_dose_ranges",EVCombinedRec.ABSORBED_DOSE_RANGES);
		
		//dose equivalent		
		nimapping.put("radiation_dose_equivalent_text",EVCombinedRec.EQUIVALENT_DOSE_TEXT);
		nimapping.put("radiation_dose_equivalent_ranges",EVCombinedRec.EQUIVALENT_DOSE_RANGES);
		
		//radiation exposure		
		nimapping.put("radiation_exposure_text",EVCombinedRec.RADIATION_EXPOSURE_TEXT);
		nimapping.put("radiation_exposure_ranges",EVCombinedRec.RADIATION_EXPOSURE_RANGES);
		
		//conductance
		nimapping.put("conductance_text",EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT);
		nimapping.put("conductance_ranges",EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES);
		
		//**************************************************************************************//
		
		/*
		//altitude
		nimapping.put("altitude_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("altitude_ranges",EVCombinedRec.SIZE_RANGES);
		
		//distance
		nimapping.put("distance_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("distance_ranges",EVCombinedRec.SIZE_RANGES);
		
		//depth
		nimapping.put("depth_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("depth_ranges",EVCombinedRec.SIZE_RANGES);
		
		//wavelength
		nimapping.put("wavelength_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("wavelength_ranges",EVCombinedRec.SIZE_RANGES);
		
		//bandwidth 		
		nimapping.put("bandwidth_text",EVCombinedRec.FREQUENCY_TEXT);
		nimapping.put("bandwidth_ranges",EVCombinedRec.FREQUENCY_RANGES);
		
		//efficiency
		nimapping.put("efficiency_text",EVCombinedRec.PERCENTAGE_TEXT); 
		nimapping.put("efficiency_ranges",EVCombinedRec.PERCENTAGE_RANGES);
		
		//gain
		nimapping.put("gain_text",EVCombinedRec.DECIBEL_TEXT);
		nimapping.put("gain_ranges",EVCombinedRec.DECIBEL_RANGES);
		
		//noise figure 
		nimapping.put("noise_figure_text",EVCombinedRec.DECIBEL_TEXT);
		nimapping.put("noise_figure_ranges",EVCombinedRec.DECIBEL_RANGES);
		
		//loss
		nimapping.put("loss_text",EVCombinedRec.DECIBEL_TEXT);
		nimapping.put("loss_ranges",EVCombinedRec.DECIBEL_RANGES);
		*/
				
	}
		
	public String get(String input)
	{
		return (String)nimapping.get(input);
	}
	
}