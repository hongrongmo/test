package org.ei.data.cbnb.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;


public class CBNBDataDictionary
    implements DataDictionary
{
    private Hashtable classCodes;

    public String getClassCodeTitle(String classCode)
    {
        if (classCode != null)
      		return (String)classCodes.get(classCode.toUpperCase());
	    else
	    	return null;
	}

    public Hashtable getClassCodes()
    {
        return this.classCodes;
    }
    public Hashtable getTreatments()
    {
        return null;
    }
    public CBNBDataDictionary()
    {
       classCodes = new Hashtable();
       classCodes.put("1S-17","Coatings, paints and inks");
	   classCodes.put("21","Pharmaceuticals prosthetics and medical chemistry");
	   classCodes.put("4","Pharmaceuticals prosthetics and medical chemistry");
	   classCodes.put("7S-08","Petrochemical industry");
	   classCodes.put("7S-12","Metals industries");
	   classCodes.put("7S-21","Biotechnology");
	   classCodes.put("LA-00","Legal Aspects");
	   classCodes.put("LA-10","Regulations and Rulings");
	   classCodes.put("LA-20","Health and Safety");
	   classCodes.put("LA-30","Environment");
	   classCodes.put("LA-40","Permits and Patents");
	   classCodes.put("MS 16","Plastics and rubber applications");
	   classCodes.put("MS-00","Chemical Businesses Generally");
	   classCodes.put("MS-01","Cosmetics and Toiletries");
	   classCodes.put("MS-02","Household and Cleaning Chemicals");
	   classCodes.put("MS-03","Food, Feed, and Beverages");
	   classCodes.put("MS-04","Pharmaceuticals, Prosthetics, and Medical Chemistry");
	   classCodes.put("MS-05","Agrochemicals");
	   classCodes.put("MS-06","Fertilizers");
	   classCodes.put("MS-07","Chemical Fibres and Textiles");
	   classCodes.put("MS-08","Petrochemical Industry");
	   classCodes.put("MS-09","Polymer and Elastomer Production");
	   classCodes.put("MS-10","Raw Material Winning");
	   classCodes.put("MS-11","Paper Industry");
	   classCodes.put("MS-12","Metals Industries");
	   classCodes.put("MS-13","Transportation and Vehicles");
	   classCodes.put("MS-14","Construction Industry");
	   classCodes.put("MS-15","Electronics Industry");
	   classCodes.put("MS-16","Plastics and Rubbers Applications");
	   classCodes.put("MS-17","Coatings, Paints, and Inks");
	   classCodes.put("MS-18","Dyes and Pigments");
	   classCodes.put("MS-19","Speciality Chemicals");
	   classCodes.put("MS-20","Other Industries");
	   classCodes.put("MS-21","Biotechnology");
	   classCodes.put("S-09","Polymer and elastomer production");
	   classCodes.put("TR-00","Trends - General");
	   classCodes.put("TR-10","Trends - New Technology");
	   classCodes.put("TR-20","Trends - New Material");
	   classCodes.put("TR-30","Trends - New Application");
	   classCodes.put("TR-40","Trends - General");
	   classCodes.put("dMS-05","Agrochemicals");
	   classCodes.put("fMS-20","Other Industries");
   }
    
    public Hashtable getAuthorityCodes()
    {
    	return null;
    }

	public String getTreatmentTitle(String treatmentTitle) {
		return null;
	}
	
}
