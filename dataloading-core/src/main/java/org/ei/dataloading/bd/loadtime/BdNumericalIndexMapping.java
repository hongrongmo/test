package org.ei.dataloading.bd.loadtime;

import java.util.*;

import org.ei.dataloading.*;

public class BdNumericalIndexMapping
{
	public static HashMap nimapping = new HashMap();
	public BdNumericalIndexMapping()
	{
		//amount of substance		
		nimapping.put("amount_of_substance_text",EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT);
		nimapping.put("amount_of_substance_ranges",EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES);
		
		//electric current		
		nimapping.put("electric_current_text",EVCombinedRec.ELECTRIC_CURRENT_TEXT);
		nimapping.put("electric_current_ranges",EVCombinedRec.ELECTRIC_CURRENT_RANGES);
		
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
		
		//electrical conductance
		nimapping.put("electrical_conductance_text",EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT);
		nimapping.put("electrical_conductance_ranges",EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES);
		
		//electrical conductivity		
		nimapping.put("electrical_conductivity_text",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT);
		nimapping.put("electrical_conductivity_ranges",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES);
				
		//thermal conductivity
		nimapping.put("thermal_conductivity_text",EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT);
		nimapping.put("thermal_conductivity_ranges",EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES);
		
		//voltage
		nimapping.put("voltage_text",EVCombinedRec.VOLTAGE_TEXT);
		nimapping.put("voltage_ranges",EVCombinedRec.VOLTAGE_RANGES);
		
		//electric field strength
		nimapping.put("electric_field_strength_text",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT); 
		nimapping.put("electric_field_strength_ranges",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES); 
		
		//current density
		nimapping.put("current_density_text",EVCombinedRec.CURRENT_DENSITY_TEXT); 
		nimapping.put("current_density_ranges",EVCombinedRec.CURRENT_DENSITY_RANGES);
		
		//energy
		nimapping.put("energy_text",EVCombinedRec.ENERGY_TEXT); 
		nimapping.put("energy_ranges",EVCombinedRec.ENERGY_RANGES);
		
		//electrical resistance
		nimapping.put("electrical_resistance_text",EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT); 
		nimapping.put("electrical_resistance_ranges",EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES); 
		
		//electrical resistivity
		nimapping.put("electrical_resistivity_text",EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT);
		nimapping.put("electrical_resistivity_ranges",EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES);
		
		//electron volt energy
		nimapping.put("electron_volt_energy_text",EVCombinedRec.ELECTRON_VOLT_ENERGY_TEXT); 
		nimapping.put("electron_volt_energy_ranges",EVCombinedRec.ELECTRON_VOLT_ENERGY_RANGES); 
		
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
		
		//percentage
		nimapping.put("percentage_text",EVCombinedRec.PERCENTAGE_TEXT); 
		nimapping.put("percentage_ranges",EVCombinedRec.PERCENTAGE_RANGES); 
		
		//magnetic flux density
		nimapping.put("magnetic_flux_density_text",EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT);
		nimapping.put("magnetic_flux_density_ranges",EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES);
		
		//inductance
		nimapping.put("inductance_text",EVCombinedRec.INDUCTANCE_TEXT);
		nimapping.put("inductance_ranges",EVCombinedRec.INDUCTANCE_RANGES);
		
		//volume charge density
		nimapping.put("volume_charge_density_text",EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT);
		nimapping.put("volume_charge_density_ranges",EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES);
		
		//surface charge density
		nimapping.put("surface_charge_density_text",EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT);
		nimapping.put("surface_charge_density_ranges",EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES);
		
		//decibel
		nimapping.put("decibel_text",EVCombinedRec.DECIBEL_TEXT);
		nimapping.put("decibel_ranges",EVCombinedRec.DECIBEL_RANGES);
		
		//luminous flux
		nimapping.put("luminous_flux_text",EVCombinedRec.LUMINOUS_FLUX_TEXT);
		nimapping.put("luminous_flux_ranges",EVCombinedRec.LUMINOUS_FLUX_RANGES);
		
		//illuminance		
		nimapping.put("illuminance_text",EVCombinedRec.ILLUMINANCE_TEXT);
		nimapping.put("illuminance_ranges",EVCombinedRec.ILLUMINANCE_RANGES);
		
		//bit rate
		nimapping.put("bit_rate_text",EVCombinedRec.BIT_RATE_TEXT);
		nimapping.put("bit_rate_ranges",EVCombinedRec.BIT_RATE_RANGES);
		
		//mass density
		nimapping.put("mass_density_text",EVCombinedRec.MASS_DENSITY_TEXT);
		nimapping.put("mass_density_ranges",EVCombinedRec.MASS_DENSITY_RANGES);
		
		//mass flow rate
		nimapping.put("mass_flow_rate_text",EVCombinedRec.MASS_FLOW_RATE_TEXT);
		nimapping.put("mass_flow_rate_ranges",EVCombinedRec.MASS_FLOW_RATE_RANGES);				
		
		//pressure
		nimapping.put("pressure_text",EVCombinedRec.PRESSURE_TEXT); 
		nimapping.put("pressure_ranges",EVCombinedRec.PRESSURE_RANGES);
		
		//force
		nimapping.put("force_text",EVCombinedRec.FORCE_TEXT);
		nimapping.put("force_ranges",EVCombinedRec.FORCE_RANGES);
		
		//torque
		nimapping.put("torque_text",EVCombinedRec.TORQUE_TEXT);
		nimapping.put("torque_ranges",EVCombinedRec.TORQUE_RANGES);
		
		//area
		nimapping.put("area_text",EVCombinedRec.AREA_TEXT);
		nimapping.put("area_ranges",EVCombinedRec.AREA_RANGES);
		
		//volume
		nimapping.put("volume_text",EVCombinedRec.VOLUME_TEXT);
		nimapping.put("volume_ranges",EVCombinedRec.VOLUME_RANGES);
		
		//velocity		
		nimapping.put("velocity_text",EVCombinedRec.VELOCITY_TEXT);   
		nimapping.put("velocity_ranges",EVCombinedRec.VELOCITY_RANGES); 
		
		//acceleration
		nimapping.put("acceleration_text",EVCombinedRec.ACCELERATION_TEXT);   
		nimapping.put("acceleration_ranges",EVCombinedRec.ACCELERATION_RANGES);  
		
		//angular velocity
		nimapping.put("angular_velocity_text",EVCombinedRec.ANGULAR_VELOCITY_TEXT);  
		nimapping.put("angular_velocity_ranges",EVCombinedRec.ANGULAR_VELOCITY_RANGES); 
		
		//rotational speed 						
		nimapping.put("rotational_speed_text",EVCombinedRec.ROTATIONAL_SPEED_TEXT);  
		nimapping.put("rotational_speed_ranges",EVCombinedRec.ROTATIONAL_SPEED_RANGES);
		
		//age		
		nimapping.put("age_text",EVCombinedRec.AGE_TEXT); 
		nimapping.put("age_ranges",EVCombinedRec.AGE_RANGES); 
		
		//molar mass		
		nimapping.put("molar_mass_text",EVCombinedRec.MOLAR_MASS_TEXT); 
		nimapping.put("molar_mass_ranges",EVCombinedRec.MOLAR_MASS_RANGES); 
		
		//molality	
		
		nimapping.put("molality_text",EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT); 
		nimapping.put("molality_ranges",EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES); 

		//radioactivity 
		nimapping.put("radioactivity_text",EVCombinedRec.RADIOACTIVITY_TEXT); 
		nimapping.put("radioactivity_ranges",EVCombinedRec.RADIOACTIVITY_RANGES); 
		
		//absorbed dose
		nimapping.put("absorbed_dose_text",EVCombinedRec.ABSORBED_DOSE_TEXT); 
		nimapping.put("absorbed_dose_ranges",EVCombinedRec.ABSORBED_DOSE_RANGES);

		//radiation exposure		
		nimapping.put("radiation_exposure_text",EVCombinedRec.RADIATION_EXPOSURE_TEXT);
		nimapping.put("radiation_exposure_ranges",EVCombinedRec.RADIATION_EXPOSURE_RANGES);		
		
		//Electron_Volt
		nimapping.put("electron_volt_text",EVCombinedRec.ELECTRON_VOLT_TEXT);
		nimapping.put("electron_volt_ranges",EVCombinedRec.ELECTRON_VOLT_RANGES);
		 
		//Luminance
		nimapping.put("luminance_text",EVCombinedRec.LUMINANCE_TEXT);
		nimapping.put("luminance_ranges",EVCombinedRec.LUMINANCE_RANGES);
				
		//Luminance_Efficacy
		nimapping.put("luminance_efficacy_text",EVCombinedRec.LUMINANCE_EFFICACY_TEXT);
		nimapping.put("luminance_efficacy_ranges",EVCombinedRec.LUMINANCE_EFFICACY_RANGES);
				 
		//luminance_efficiency
		nimapping.put("luminance_efficiency_text",EVCombinedRec.LUMINANCE_EFFICIENCY_TEXT);
		nimapping.put("luminance_efficiency_ranges",EVCombinedRec.LUMINANCE_EFFICIENCY_RANGES);
		
		//Magnetic field strength
		nimapping.put("magnetic_field_strength_text",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT);
		nimapping.put("magnetic_field_strength_ranges",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES);
						
		//Spectral_Efficiency
		nimapping.put("spectral_efficiency_text",EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT);
		nimapping.put("spectral_efficiency_ranges",EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES);
						 
		//Surface_Power_Density
		nimapping.put("surface_power_density_text",EVCombinedRec.SURFACE_POWER_DENSITY_TEXT);
		nimapping.put("surface_power_density_ranges",EVCombinedRec.SURFACE_POWER_DENSITY_RANGES);
		
		//added on 4/11/2016
		//Decibel isotropic
		nimapping.put("decibel_isotropic_text",EVCombinedRec.DECIBEL_ISOTROPIC_TEXT);
		nimapping.put("decibel_isotropic_ranges",EVCombinedRec.DECIBEL_ISOTROPIC_RANGES);
		
		//decibel_milliwatts
		nimapping.put("decibel_milliwatts_text",EVCombinedRec.DECIBEL_MILLIWATTS_TEXT);
		nimapping.put("decibel_milliwatts_ranges",EVCombinedRec.DECIBEL_MILLIWATTS_RANGES);	
		
		//Equivalent Dose
		nimapping.put("equivalent_dose_text",EVCombinedRec.EQUIVALENT_DOSE_TEXT);
		nimapping.put("equivalent_dose_ranges",EVCombinedRec.EQUIVALENT_DOSE_RANGES);	
		
		//Molar concentration
		nimapping.put("molar_concentration_text",EVCombinedRec.MOLAR_CONCENTRATION_TEXT);
		nimapping.put("molar_concentration_ranges",EVCombinedRec.MOLAR_CONCENTRATION_RANGES);	
		
		//Linear Density
		nimapping.put("linear_density_text",EVCombinedRec.LINEAR_DENSITY_TEXT);
		nimapping.put("linear_density_ranges",EVCombinedRec.LINEAR_DENSITY_RANGES);
		
		//luminous efficiency
		nimapping.put("luminous_efficiency_text",EVCombinedRec.LUMINOUS_EFFICIENCY_TEXT);
		nimapping.put("luminous_efficiency_ranges",EVCombinedRec.LUMINOUS_EFFICIENCY_RANGES);
		
		//luminous efficacy
		nimapping.put("luminous_efficacy_text",EVCombinedRec.LUMINOUS_EFFICACY_TEXT);
		nimapping.put("luminous_efficacy_ranges",EVCombinedRec.LUMINOUS_EFFICACY_RANGES);
		
		//Specific Energy
		nimapping.put("specific_energy_text",EVCombinedRec.SPECIFIC_ENERGY_TEXT);
		nimapping.put("specific_energy_ranges",EVCombinedRec.SPECIFIC_ENERGY_RANGES);
		
		//Specific Surface area
		nimapping.put("specific_surface_area_text",EVCombinedRec.SPECIFIC_SURFACE_AREA_TEXT);
		nimapping.put("specific_surface_area_ranges",EVCombinedRec.SPECIFIC_SURFACE_AREA_RANGES);
		
		//Specific Volume
		nimapping.put("specific_volume_text",EVCombinedRec.SPECIFIC_VOLUME_TEXT);
		nimapping.put("specific_volume_ranges",EVCombinedRec.SPECIFIC_VOLUME_RANGES);
		
		//Surface Tension
		nimapping.put("surface_tension_text",EVCombinedRec.SURFACE_TENSION_TEXT);
		nimapping.put("surface_tension_ranges",EVCombinedRec.SURFACE_TENSION_RANGES);
		
		//Surface Density
		nimapping.put("surface_density_text",EVCombinedRec.SURFACE_DENSITY_TEXT);
		nimapping.put("surface_density_ranges",EVCombinedRec.SURFACE_DENSITY_RANGES);
		
		
		
		
		
		
		
	}
		
	public String get(String input)
	{
		return (String)nimapping.get(input);
	}
	
}
