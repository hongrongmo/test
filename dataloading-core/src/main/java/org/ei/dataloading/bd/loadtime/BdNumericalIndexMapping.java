package org.ei.dataloading.bd.loadtime;

import java.util.*;

import org.ei.dataloading.*;

public class BdNumericalIndexMapping
{
	public static HashMap nimapping = new HashMap();
	public BdNumericalIndexMapping()
	{
		//amount of substance
		//nimapping.put("amount_of_substance_minimum",EVCombinedRec.AMOUNT_OF_SUBSTANCE_MINIMUM);
		//nimapping.put("amount_of_substance_maximum",EVCombinedRec.AMOUNT_OF_SUBSTANCE_MAXIMUM);
		nimapping.put("amount_of_substance_text",EVCombinedRec.AMOUNT_OF_SUBSTANCE_TEXT);
		nimapping.put("amount_of_substance_ranges",EVCombinedRec.AMOUNT_OF_SUBSTANCE_RANGES);
		
		//electric current
		//nimapping.put("electric_current_minimum",EVCombinedRec.ELECTRIC_CURRENT_MINIMUM);
		//nimapping.put("electric_current_maximum",EVCombinedRec.ELECTRIC_CURRENT_MAXIMUM);
		nimapping.put("electric_current_text",EVCombinedRec.ELECTRIC_CURRENT_TEXT);
		nimapping.put("electric_current_ranges",EVCombinedRec.ELECTRIC_CURRENT_RANGES);
		
		//luminous intensity
		//nimapping.put("luminous_intensity_minimum",EVCombinedRec.LUMINOUS_INTENSITY_MINIMUM); 
		//nimapping.put("luminous_intensity_maximum",EVCombinedRec.LUMINOUS_INTENSITY_MAXIMUM);
		nimapping.put("luminous_intensity_text",EVCombinedRec.LUMINOUS_INTENSITY_TEXT);
		nimapping.put("luminous_intensity_ranges",EVCombinedRec.LUMINOUS_INTENSITY_RANGES);
		
		//mass
		//nimapping.put("mass_minimum",EVCombinedRec.MASS_MINIMUM); 
		//nimapping.put("mass_maximum",EVCombinedRec.MASS_MAXIMUM);
		nimapping.put("mass_text",EVCombinedRec.MASS_TEXT);
		nimapping.put("mass_ranges",EVCombinedRec.MASS_RANGES);
		
		//temperature
		//nimapping.put("temperature_minimum",EVCombinedRec.TEMPERATURE_MINIMUM); 
		//nimapping.put("temperature_maximum",EVCombinedRec.TEMPERATURE_MAXIMUM);
		nimapping.put("temperature_text",EVCombinedRec.TEMPERATURE_TEXT);
		nimapping.put("temperature_ranges",EVCombinedRec.TEMPERATURE_RANGES);
		
		//time
		//nimapping.put("time_minimum",EVCombinedRec.TIME_MINIMUM); 
		//nimapping.put("time_maximum",EVCombinedRec.TIME_MAXIMUM);
		nimapping.put("time_text",EVCombinedRec.TIME_TEXT);
		nimapping.put("time_ranges",EVCombinedRec.TIME_RANGES);
		
		//size
		//nimapping.put("size_minimum",EVCombinedRec.SIZE_MINIMUM); 
		//nimapping.put("size_maximum",EVCombinedRec.SIZE_MAXIMUM);
		nimapping.put("size_text",EVCombinedRec.SIZE_TEXT);
		nimapping.put("size_ranges",EVCombinedRec.SIZE_RANGES);
		
		//electrical conductance
		//nimapping.put("electrical_conductance_minimum",EVCombinedRec.ELECTRICAL_CONDUCTANCE_MINIMUM); 
		//nimapping.put("electrical_conductance_maximum",EVCombinedRec.ELECTRICAL_CONDUCTANCE_MAXIMUM);
		nimapping.put("electrical_conductance_text",EVCombinedRec.ELECTRICAL_CONDUCTANCE_TEXT);
		nimapping.put("electrical_conductance_ranges",EVCombinedRec.ELECTRICAL_CONDUCTANCE_RANGES);
		
		//electrical conductivity		
		//nimapping.put("electrical_conductivity_minimum",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_MINIMUM); 
		//nimapping.put("electrical_conductivity_maximum",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_MAXIMUM);
		nimapping.put("electrical_conductivity_text",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_TEXT);
		nimapping.put("electrical_conductivity_ranges",EVCombinedRec.ELECTRICAL_CONDUCTIVITY_RANGES);
				
		//thermal conductivity
		//nimapping.put("thermal_conductivity_minimum",EVCombinedRec.THERMAL_CONDUCTIVITY_MINIMUM); 
		//nimapping.put("thermal_conductivity_maximum",EVCombinedRec.THERMAL_CONDUCTIVITY_MAXIMUM);
		nimapping.put("thermal_conductivity_text",EVCombinedRec.THERMAL_CONDUCTIVITY_TEXT);
		nimapping.put("thermal_conductivity_ranges",EVCombinedRec.THERMAL_CONDUCTIVITY_RANGES);
		
		//voltage
		//nimapping.put("voltage_minimum",EVCombinedRec.VOLTAGE_MINIMUM);
		//nimapping.put("voltage_maximum",EVCombinedRec.VOLTAGE_MAXIMUM);
		nimapping.put("voltage_text",EVCombinedRec.VOLTAGE_TEXT);
		nimapping.put("voltage_ranges",EVCombinedRec.VOLTAGE_RANGES);
		
		//electric field strength
		//nimapping.put("electric_field_strength_minimum",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_MINIMUM); 
		//nimapping.put("electric_field_strength_maximum",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_MAXIMUM);
		nimapping.put("electric_field_strength_text",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_TEXT); 
		nimapping.put("electric_field_strength_ranges",EVCombinedRec.ELECTRIC_FIELD_STRENGTH_RANGES); 
		
		//current density
		//nimapping.put("current_density_minimum",EVCombinedRec.CURRENT_DENSITY_MINIMUM); 
		//nimapping.put("current_density_maximum",EVCombinedRec.CURRENT_DENSITY_MAXIMUM);
		nimapping.put("current_density_text",EVCombinedRec.CURRENT_DENSITY_TEXT); 
		nimapping.put("current_density_ranges",EVCombinedRec.CURRENT_DENSITY_RANGES);
		
		//energy
		//nimapping.put("energy_minimum",EVCombinedRec.ENERGY_MINIMUM); 
		//nimapping.put("energy_maximum",EVCombinedRec.ENERGY_MAXIMUM);
		nimapping.put("energy_text",EVCombinedRec.ENERGY_TEXT); 
		nimapping.put("energy_ranges",EVCombinedRec.ENERGY_RANGES);
		
		//electrical resistance
		//nimapping.put("electrical_resistance_minimum",EVCombinedRec.ELECTRICAL_RESISTANCE_MINIMUM); 
		//nimapping.put("electrical_resistance_maximum",EVCombinedRec.ELECTRICAL_RESISTANCE_MAXIMUM);
		nimapping.put("electrical_resistance_text",EVCombinedRec.ELECTRICAL_RESISTANCE_TEXT); 
		nimapping.put("electrical_resistance_ranges",EVCombinedRec.ELECTRICAL_RESISTANCE_RANGES); 
		
		//electrical resistivity
		//nimapping.put("electrical_resistivity_minimum",EVCombinedRec.ELECTRICAL_RESISTIVITY_MINIMUM); 
		//nimapping.put("electrical_resistivity_maximum",EVCombinedRec.ELECTRICAL_RESISTIVITY_MAXIMUM);
		nimapping.put("electrical_resistivity_text",EVCombinedRec.ELECTRICAL_RESISTIVITY_TEXT);
		nimapping.put("electrical_resistivity_ranges",EVCombinedRec.ELECTRICAL_RESISTIVITY_RANGES);
		
		//electron volt energy
		//nimapping.put("electron_volt_energy_minimum",EVCombinedRec.ELECTRON_VOLT_ENERGY_MINIMUM); 
		//nimapping.put("electron_volt_energy_maximum",EVCombinedRec.ELECTRON_VOLT_ENERGY_MAXIMUM);
		nimapping.put("electron_volt_energy_text",EVCombinedRec.ELECTRON_VOLT_ENERGY_TEXT); 
		nimapping.put("electron_volt_energy_ranges",EVCombinedRec.ELECTRON_VOLT_ENERGY_RANGES); 
		
		//capacitance
		//nimapping.put("capacitance_minimum",EVCombinedRec.CAPACITANCE_MINIMUM);
		//nimapping.put("capacitance_maximum",EVCombinedRec.CAPACITANCE_MAXIMUM);
		nimapping.put("capacitance_text",EVCombinedRec.CAPACITANCE_TEXT);	
		nimapping.put("capacitance_ranges",EVCombinedRec.CAPACITANCE_RANGES);	
				
		//permittivity
		//nimapping.put("permittivity_minimum",EVCombinedRec.PERMITTIVITY_MINIMUM); 
		//nimapping.put("permittivity_maximum",EVCombinedRec.PERMITTIVITY_MAXIMUM);
		nimapping.put("permittivity_text",EVCombinedRec.PERMITTIVITY_TEXT); 
		nimapping.put("permittivity_ranges",EVCombinedRec.PERMITTIVITY_RANGES); 
		
		//frequency
		//nimapping.put("frequency_minimum",EVCombinedRec.FREQUENCY_MINIMUM);
		//nimapping.put("frequency_maximum",EVCombinedRec.FREQUENCY_MAXIMUM);
		nimapping.put("frequency_text",EVCombinedRec.FREQUENCY_TEXT);
		nimapping.put("frequency_ranges",EVCombinedRec.FREQUENCY_RANGES);
				
		//power
		//nimapping.put("power_minimum",EVCombinedRec.POWER_MINIMUM); 
		//nimapping.put("power_maximum",EVCombinedRec.POWER_MAXIMUM);
		nimapping.put("power_text",EVCombinedRec.POWER_TEXT);  
		nimapping.put("power_ranges",EVCombinedRec.POWER_RANGES);  
		
		//apparent power 
		//nimapping.put("apparent_power_minimum",EVCombinedRec.APPARENT_POWER_MINIMUM); 
		//nimapping.put("apparent_power_maximum",EVCombinedRec.APPARENT_POWER_MAXIMUM);
		nimapping.put("apparent_power_text",EVCombinedRec.APPARENT_POWER_TEXT); 
		nimapping.put("apparent_power_ranges",EVCombinedRec.APPARENT_POWER_RANGES);
		
		//reactive power
		//nimapping.put("reactive_power_minimum",EVCombinedRec.REACTIVE_POWER_MINIMUM); 
		//nimapping.put("reactive_power_maximum",EVCombinedRec.REACTIVE_POWER_MAXIMUM);
		nimapping.put("reactive_power_text",EVCombinedRec.REACTIVE_POWER_TEXT); 
		nimapping.put("reactive_power_ranges",EVCombinedRec.REACTIVE_POWER_RANGES); 
		
		//heat flux density
		//nimapping.put("heat_flux_density_minimum",EVCombinedRec.HEAT_FLUX_DENSITY_MINIMUM); 
		//nimapping.put("heat_flux_density_Maximum",EVCombinedRec.HEAT_FLUX_DENSITY_MAXIMUM);
		nimapping.put("heat_flux_density_text",EVCombinedRec.HEAT_FLUX_DENSITY_TEXT); 
		nimapping.put("heat_flux_density_ranges",EVCombinedRec.HEAT_FLUX_DENSITY_RANGES); 
		
		//percentage
		//nimapping.put("percentage_minimum",EVCombinedRec.PERCENTAGE_MINIMUM); 
		//nimapping.put("percentage_maximum",EVCombinedRec.PERCENTAGE_MAXIMUM);
		nimapping.put("percentage_text",EVCombinedRec.PERCENTAGE_TEXT); 
		nimapping.put("percentage_ranges",EVCombinedRec.PERCENTAGE_RANGES); 
		
		//magnetic flux density
		//nimapping.put("magnetic_flux_density_minimum",EVCombinedRec.MAGNETIC_FLUX_DENSITY_MINIMUM); 
		//nimapping.put("magnetic_flux_density_maximum",EVCombinedRec.MAGNETIC_FLUX_DENSITY_MAXIMUM);
		nimapping.put("magnetic_flux_density_text",EVCombinedRec.MAGNETIC_FLUX_DENSITY_TEXT);
		nimapping.put("magnetic_flux_density_ranges",EVCombinedRec.MAGNETIC_FLUX_DENSITY_RANGES);
		
		//magnetic flux
		//nimapping.put("magnetic_flux_minimum",EVCombinedRec.MAGNETIC_FLUX_MINIMUM); 
		//nimapping.put("magnetic_flux__maximum",EVCombinedRec.MAGNETIC_FLUX_MAXIMUM);
		nimapping.put("magnetic_flux_text",EVCombinedRec.MAGNETIC_FLUX_TEXT);
		nimapping.put("magnetic_flux_ranges",EVCombinedRec.MAGNETIC_FLUX_RANGES);
		
		//inductance
		//nimapping.put("inductance_minimum",EVCombinedRec.INDUCTANCE_MINIMUM); 
		//nimapping.put("inductance_maximum",EVCombinedRec.INDUCTANCE_MAXIMUM);
		nimapping.put("inductance_text",EVCombinedRec.INDUCTANCE_TEXT);
		nimapping.put("inductance_ranges",EVCombinedRec.INDUCTANCE_RANGES);
		
		//permeability
		//nimapping.put("permeability_minimum",EVCombinedRec.PERMEABILITY_MINIMUM); 
		//nimapping.put("permeability_maximum",EVCombinedRec.PERMEABILITY_MAXIMUM);
		nimapping.put("permeability_text",EVCombinedRec.PERMEABILITY_TEXT);
		nimapping.put("permeability_ranges",EVCombinedRec.PERMEABILITY_RANGES);
		
		//electric charge
		//nimapping.put("electric_charge_minimum",EVCombinedRec.ELECTRIC_CHARGE_MINIMUM); 
		//nimapping.put("electric_charge_maximum",EVCombinedRec.ELECTRIC_CHARGE_MAXIMUM);
		nimapping.put("electric_charge_text",EVCombinedRec.ELECTRIC_CHARGE_TEXT);
		nimapping.put("electric_charge_ranges",EVCombinedRec.ELECTRIC_CHARGE_RANGES);
		
		//volume charge density
		//nimapping.put("volume_charge_density_minimum",EVCombinedRec.VOLUME_CHARGE_DENSITY_MINIMUM); 
		//nimapping.put("volume_charge_density_maximum",EVCombinedRec.VOLUME_CHARGE_DENSITY_MAXIMUM);
		nimapping.put("volume_charge_density_text",EVCombinedRec.VOLUME_CHARGE_DENSITY_TEXT);
		nimapping.put("volume_charge_density_ranges",EVCombinedRec.VOLUME_CHARGE_DENSITY_RANGES);
		
		//surface charge density
		//nimapping.put("surface_charge_density_minimum",EVCombinedRec.SURFACE_CHARGE_DENSITY_MINIMUM); 
		//nimapping.put("surface_charge_density_maximum",EVCombinedRec.SURFACE_CHARGE_DENSITY_MAXIMUM);
		nimapping.put("surface_charge_density_text",EVCombinedRec.SURFACE_CHARGE_DENSITY_TEXT);
		nimapping.put("surface_charge_density_ranges",EVCombinedRec.SURFACE_CHARGE_DENSITY_RANGES);
		
		//linear charge density
		//nimapping.put("linear_charge_density_minimum",EVCombinedRec.LINEAR_CHARGE_DENSITY_MINIMUM); 
		//nimapping.put("linear_charge_density_maximum",EVCombinedRec.LINEAR_CHARGE_DENSITY_MAXIMUM);
		nimapping.put("linear_charge_density_text",EVCombinedRec.LINEAR_CHARGE_DENSITY_TEXT);
		nimapping.put("linear_charge_density_ranges",EVCombinedRec.LINEAR_CHARGE_DENSITY_RANGES);
		
		//decibel
		//nimapping.put("decibel_minimum",EVCombinedRec.DECIBEL_MINIMUM); 
		//nimapping.put("decibel_maximum",EVCombinedRec.DECIBEL_MAXIMUM);
		nimapping.put("decibel_text",EVCombinedRec.DECIBEL_TEXT);
		nimapping.put("decibel_ranges",EVCombinedRec.DECIBEL_RANGES);
		
		//luminous flux
		//nimapping.put("luminous_flux_minimum",EVCombinedRec.LUMINOUS_FLUX_MINIMUM); 
		//nimapping.put("luminous_flux_maximum",EVCombinedRec.LUMINOUS_FLUX_MAXIMUM);
		nimapping.put("luminous_flux_text",EVCombinedRec.LUMINOUS_FLUX_TEXT);
		nimapping.put("luminous_flux_ranges",EVCombinedRec.LUMINOUS_FLUX_RANGES);
		
		//illuminance
		//nimapping.put("illuminance_minimum",EVCombinedRec.ILLUMINANCE_MINIMUM); 
		//nimapping.put("illuminance_maximum",EVCombinedRec.ILLUMINANCE_MAXIMUM);
		nimapping.put("illuminance_text",EVCombinedRec.ILLUMINANCE_TEXT);
		nimapping.put("illuminance_ranges",EVCombinedRec.ILLUMINANCE_RANGES);
		
		//bit rate
		//nimapping.put("bit_rate_minimum",EVCombinedRec.BIT_RATE_MINIMUM); 
		//nimapping.put("bit_rate_maximum",EVCombinedRec.BIT_RATE_MAXIMUM);
		nimapping.put("bit_rate_text",EVCombinedRec.BIT_RATE_TEXT);
		nimapping.put("bit_rate_ranges",EVCombinedRec.BIT_RATE_RANGES);
		
		//picture element
		//nimapping.put("picture_element_minimum",EVCombinedRec.PICTURE_ELEMENT_MINIMUM); 
		//nimapping.put("picture_element_maximum",EVCombinedRec.PICTURE_ELEMENT_MAXIMUM);
		nimapping.put("picture_element_text",EVCombinedRec.PICTURE_ELEMENT_TEXT);
		nimapping.put("picture_element_ranges",EVCombinedRec.PICTURE_ELEMENT_RANGES);
		
		//mass density
		//nimapping.put("mass_density_minimum",EVCombinedRec.MASS_DENSITY_MINIMUM); 
		//nimapping.put("mass_density_maximum",EVCombinedRec.MASS_DENSITY_MAXIMUM);
		nimapping.put("mass_density_text",EVCombinedRec.MASS_DENSITY_TEXT);
		nimapping.put("mass_density_ranges",EVCombinedRec.MASS_DENSITY_RANGES);
		
		//mass flow rate
		//nimapping.put("mass_flow_rate_minimum",EVCombinedRec.MASS_FLOW_RATE_MINIMUM); 
		//nimapping.put("mass_flow_rate_maximum",EVCombinedRec.MASS_FLOW_RATE_MAXIMUM);
		nimapping.put("mass_flow_rate_text",EVCombinedRec.MASS_FLOW_RATE_TEXT);
		nimapping.put("mass_flow_rate_ranges",EVCombinedRec.MASS_FLOW_RATE_RANGES);
		
		//volumetric flow rate
		//nimapping.put("volumetric_flow_rate_minimum",EVCombinedRec.VOLUMETRIC_FLOW_RATE_MINIMUM); 
		//nimapping.put("volumetric_flow_rate_maximum",EVCombinedRec.VOLUMETRIC_FLOW_RATE_MAXIMUM);
		nimapping.put("volumetric_flow_rate_text",EVCombinedRec.VOLUMETRIC_FLOW_RATE_TEXT); 
		nimapping.put("volumetric_flow_rate_ranges",EVCombinedRec.VOLUMETRIC_FLOW_RATE_RANGES);
		
		//unit of information
		//nimapping.put("unit_of_information_minimum",EVCombinedRec.UNIT_OF_INFORMATION_MINIMUM); 
		//nimapping.put("unit_of_information_maximum",EVCombinedRec.UNIT_OF_INFORMATION_MAXIMUM);
		nimapping.put("unit_of_information_text",EVCombinedRec.UNIT_OF_INFORMATION_TEXT);  
		nimapping.put("unit_of_information_ranges",EVCombinedRec.UNIT_OF_INFORMATION_RANGES); 
		
		//angle
		//nimapping.put("angle_minimum",EVCombinedRec.ANGLE_MINIMUM); 
		//nimapping.put("angle_maximum",EVCombinedRec.ANGLE_MAXIMUM);
		nimapping.put("angle_text",EVCombinedRec.ANGLE_TEXT);  
		nimapping.put("angle_ranges",EVCombinedRec.ANGLE_RANGES);  
		
		//solid angle
		//nimapping.put("solid_angle_minimum",EVCombinedRec.SOLID_ANGLE_MINIMUM); 
		//nimapping.put("solid_angle_maximum",EVCombinedRec.SOLID_ANGLE_MAXIMUM);
		nimapping.put("solid_angle_text",EVCombinedRec.SOLID_ANGLE_TEXT);  
		nimapping.put("solid_angle_ranges",EVCombinedRec.SOLID_ANGLE_RANGES);  
		
		//pressure
		//nimapping.put("pressure_minimum",EVCombinedRec.PRESSURE_MINIMUM); 
		//nimapping.put("pressure_maximum",EVCombinedRec.PRESSURE_MAXIMUM);
		nimapping.put("pressure_text",EVCombinedRec.PRESSURE_TEXT); 
		nimapping.put("pressure_ranges",EVCombinedRec.PRESSURE_RANGES);
		
		//dynamic viscosity
		//nimapping.put("dynamic_viscosity_minimum",EVCombinedRec.DYNAMIC_VISCOSITY_MINIMUM); 
		//nimapping.put("dynamic_viscosity_maximum",EVCombinedRec.DYNAMIC_VISCOSITY_MAXIMUM);
		nimapping.put("dynamic_viscosity_text",EVCombinedRec.DYNAMIC_VISCOSITY_TEXT); 
		nimapping.put("dynamic_viscosity_ranges",EVCombinedRec.DYNAMIC_VISCOSITY_RANGES); 
		
		//force
		//nimapping.put("force_minimum",EVCombinedRec.FORCE_MINIMUM); 
		//nimapping.put("force_maximum",EVCombinedRec.FORCE_MAXIMUM);
		nimapping.put("force_text",EVCombinedRec.FORCE_TEXT);
		nimapping.put("force_ranges",EVCombinedRec.FORCE_RANGES);
		
		//torque
		//nimapping.put("torque_minimum",EVCombinedRec.TORQUE_MINIMUM); 
		//nimapping.put("torque_maximum",EVCombinedRec.TORQUE_MAXIMUM);
		nimapping.put("torque_text",EVCombinedRec.TORQUE_TEXT);
		nimapping.put("torque_ranges",EVCombinedRec.TORQUE_RANGES);
		
		//area
		//nimapping.put("area_minimum",EVCombinedRec.AREA_MINIMUM); 
		//nimapping.put("area_maximum",EVCombinedRec.AREA_MAXIMUM);
		nimapping.put("area_text",EVCombinedRec.AREA_TEXT);
		nimapping.put("area_ranges",EVCombinedRec.AREA_RANGES);
		
		//volume
		//nimapping.put("volume_minimum",EVCombinedRec.VOLUME_MINIMUM); 
		//nimapping.put("volumn_maximum",EVCombinedRec.VOLUME_MAXIMUM);
		nimapping.put("volumn_text",EVCombinedRec.VOLUME_TEXT);
		nimapping.put("volumn_ranges",EVCombinedRec.VOLUME_RANGES);
		
		//velocity		
		//nimapping.put("velocity_minimum",EVCombinedRec.VELOCITY_MINIMUM); 
		//nimapping.put("velocity_maximum",EVCombinedRec.VELOCITY_MAXIMUM);
		nimapping.put("velocity_text",EVCombinedRec.VELOCITY_TEXT);   
		nimapping.put("velocity_ranges",EVCombinedRec.VELOCITY_RANGES); 
		
		//acceleration
		//nimapping.put("acceleration_minimum",EVCombinedRec.ACCELERATION_MINIMUM); 
		//nimapping.put("acceleration_maximum",EVCombinedRec.ACCELERATION_MAXIMUM);
		nimapping.put("acceleration_text",EVCombinedRec.ACCELERATION_TEXT);   
		nimapping.put("acceleration_ranges",EVCombinedRec.ACCELERATION_RANGES);  
		
		//angular velocity
		//nimapping.put("angular_velocity_minimum",EVCombinedRec.ANGULAR_VELOCITY_MINIMUM); 
		//nimapping.put("angular_velocity_maximum",EVCombinedRec.ANGULAR_VELOCITY_MAXIMUM);
		nimapping.put("angular_velocity_text",EVCombinedRec.ANGULAR_VELOCITY_TEXT);  
		nimapping.put("angular_velocity_ranges",EVCombinedRec.ANGULAR_VELOCITY_RANGES); 
		
		//rotational speed 				
		//nimapping.put("rotational_speed_minimum",EVCombinedRec.ROTATIONAL_SPEED_MINIMUM); 
		//nimapping.put("rotational_speed_maximum",EVCombinedRec.ROTATIONAL_SPEED_MAXIMUM);
		nimapping.put("rotational_speed_text",EVCombinedRec.ROTATIONAL_SPEED_TEXT);  
		nimapping.put("rotational_speed_ranges",EVCombinedRec.ROTATIONAL_SPEED_RANGES);
		
		//age		
		//nimapping.put("age_minimum",EVCombinedRec.AGE_MINIMUM); 
		//nimapping.put("age_maximum",EVCombinedRec.AGE_MAXIMUM);
		nimapping.put("age_text",EVCombinedRec.AGE_TEXT); 
		nimapping.put("age_ranges",EVCombinedRec.AGE_RANGES); 
		
		//molar mass		
		//nimapping.put("molar_mass_minimum",EVCombinedRec.MOLAR_MASS_MINIMUM); 
		//nimapping.put("molar_mass_maximum",EVCombinedRec.MOLAR_MASS_MAXIMUM);
		nimapping.put("molar_mass_text",EVCombinedRec.MOLAR_MASS_TEXT); 
		nimapping.put("molar_mass_ranges",EVCombinedRec.MOLAR_MASS_RANGES); 
		
		//molality of substance		
		//nimapping.put("molality_of_substance_minimum",EVCombinedRec.MOLALITY_OF_SUBSTANCE_MINIMUM); 
		//nimapping.put("molality_of_substance_maximum",EVCombinedRec.MOLALITY_OF_SUBSTANCE_MAXIMUM);
		nimapping.put("molality_of_substance_text",EVCombinedRec.MOLALITY_OF_SUBSTANCE_TEXT); 
		nimapping.put("molality_of_substance_ranges",EVCombinedRec.MOLALITY_OF_SUBSTANCE_RANGES); 
		
		//pH value
		//nimapping.put("ph_value_minimum",EVCombinedRec.PH_VALUE_MINIMUM); 
		//nimapping.put("ph_value_maximum",EVCombinedRec.PH_VALUE_MAXIMUM);
		nimapping.put("ph_value_text",EVCombinedRec.PH_VALUE_TEXT); 
		nimapping.put("ph_value_ranges",EVCombinedRec.PH_VALUE_RANGES); 

		//radioactivity 
		//nimapping.put("radioactivity_minimum",EVCombinedRec.RADIOACTIVITY_MINIMUM); 
		//nimapping.put("radioactivity_maximum",EVCombinedRec.RADIOACTIVITY_MAXIMUM);
		nimapping.put("radioactivity_text",EVCombinedRec.RADIOACTIVITY_TEXT); 
		nimapping.put("radioactivity_ranges",EVCombinedRec.RADIOACTIVITY_RANGES); 
		
		//absorbed dose
		//nimapping.put("absorbed_dose_minimum",EVCombinedRec.ABSORBED_DOSE_MINIMUM); 
		//nimapping.put("absorbed_dose_maximum",EVCombinedRec.ABSORBED_DOSE_MAXIMUM);
		nimapping.put("absorbed_dose_text",EVCombinedRec.ABSORBED_DOSE_TEXT); 
		nimapping.put("absorbed_dose_ranges",EVCombinedRec.ABSORBED_DOSE_RANGES);
		
		//dose equivalent		
		//nimapping.put("dose_equivalent_minimum",EVCombinedRec.DOSE_EQUIVALENT_MINIMUM); 
		//nimapping.put("dose_equivalent_maximum",EVCombinedRec.DOSE_EQUIVALENT_MAXIMUM);
		nimapping.put("dose_equivalent_text",EVCombinedRec.DOSE_EQUIVALENT_TEXT);
		nimapping.put("dose_equivalent_ranges",EVCombinedRec.DOSE_EQUIVALENT_RANGES);
		
		//radiation exposure		
		//nimapping.put("radiation_exposure_minimum",EVCombinedRec.RADIATION_EXPOSURE_MINIMUM); 
		//nimapping.put("radiation_exposure_maximum",EVCombinedRec.RADIATION_EXPOSURE_MAXIMUM);
		nimapping.put("radiation_exposure_text",EVCombinedRec.RADIATION_EXPOSURE_TEXT);
		nimapping.put("radiation_exposure_ranges",EVCombinedRec.RADIATION_EXPOSURE_RANGES);
		
		//catalytic activity
		//nimapping.put("catalytic_activity_minimum",EVCombinedRec.CATALYTIC_ACTIVITY_MINIMUM); 
		//nimapping.put("catalytic_activity_maximum",EVCombinedRec.CATALYTIC_ACTIVITY_MAXIMUM);
		nimapping.put("catalytic_activity_text",EVCombinedRec.CATALYTIC_ACTIVITY_TEXT);
		nimapping.put("catalytic_activity_ranges",EVCombinedRec.CATALYTIC_ACTIVITY_RANGES);
		
		//Electron_Volt
		//nimapping.put("electron_volt_minimum",EVCombinedRec.ELECTRON_VOLT_MINIMUM); 
		//nimapping.put("electron_volt_maximum",EVCombinedRec.ELECTRON_VOLT_MAXIMUM);
		nimapping.put("electron_volt_text",EVCombinedRec.ELECTRON_VOLT_TEXT);
		nimapping.put("electron_volt_ranges",EVCombinedRec.ELECTRON_VOLT_RANGES);
		 
		//Luminance
		//nimapping.put("luminance_minimum",EVCombinedRec.LUMINANCE_MINIMUM); 
		//nimapping.put("luminance_maximum",EVCombinedRec.LUMINANCE_MAXIMUM);
		nimapping.put("luminance_text",EVCombinedRec.LUMINANCE_TEXT);
		nimapping.put("luminance_ranges",EVCombinedRec.LUMINANCE_RANGES);
				
		//Luminance_Efficacy
		//nimapping.put("luminance_efficacy_minimum",EVCombinedRec.LUMINANCE_EFFICACY_MINIMUM); 
		//nimapping.put("luminance_efficacy_maximum",EVCombinedRec.LUMINANCE_EFFICACY_MAXIMUM);
		nimapping.put("luminance_efficacy_text",EVCombinedRec.LUMINANCE_EFFICACY_TEXT);
		nimapping.put("luminance_efficacy_ranges",EVCombinedRec.LUMINANCE_EFFICACY_RANGES);
				 
		//luminance_efficiency
		//nimapping.put("luminance_efficiency_minimum",EVCombinedRec.LUMINANCE_EFFICIENCY_MINIMUM); 
		//nimapping.put("luminance_efficiency_maximum",EVCombinedRec.LUMINANCE_EFFICIENCY_MAXIMUM);
		nimapping.put("luminance_efficiency_text",EVCombinedRec.LUMINANCE_EFFICIENCY_TEXT);
		nimapping.put("luminance_efficiency_ranges",EVCombinedRec.LUMINANCE_EFFICIENCY_RANGES);
		
		//Magnetic field strength
		//nimapping.put("magnetic_field_strength_minimum",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_MINIMUM); 
		//nimapping.put("magnetic_field_strength_maximum",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_MAXIMUM);
		nimapping.put("magnetic_field_strength_text",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_TEXT);
		nimapping.put("magnetic_field_strength_ranges",EVCombinedRec.MAGNETIC_FIELD_STRENGTH_RANGES);
						
		//Spectral_Efficiency
		//nimapping.put("spectral_efficiency_minimum",EVCombinedRec.SPECTRAL_EFFICIENCY_MINIMUM); 
		//nimapping.put("spectral_efficiency_maximum",EVCombinedRec.SPECTRAL_EFFICIENCY_MAXIMUM);
		nimapping.put("spectral_efficiency_text",EVCombinedRec.SPECTRAL_EFFICIENCY_TEXT);
		nimapping.put("spectral_efficiency_ranges",EVCombinedRec.SPECTRAL_EFFICIENCY_RANGES);
						 
		//Surface_Power_Density
		//nimapping.put("surface_power_density_minimum",EVCombinedRec.SURFACE_POWER_DENSITY_MINIMUM); 
		//nimapping.put("surface_power_density_maximum",EVCombinedRec.SURFACE_POWER_DENSITY_MAXIMUM);
		nimapping.put("surface_power_density_text",EVCombinedRec.SURFACE_POWER_DENSITY_TEXT);
		nimapping.put("surface_power_density_ranges",EVCombinedRec.SURFACE_POWER_DENSITY_RANGES);
				
		
		
		
		
		
	}
		
	public String get(String input)
	{
		return (String)nimapping.get(input);
	}
	
}
