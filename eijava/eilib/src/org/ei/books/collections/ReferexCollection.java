package org.ei.books.collections;

import java.util.List;

import org.ei.domain.navigators.EiModifier;

public abstract class ReferexCollection {

	public static final String ALL = "All Collections";
	
	public static ReferexCollection ELE = new Ele();
	public static ReferexCollection CHE = new Che();
	public static ReferexCollection MAT = new Mat();
    public static ReferexCollection CIV = new Civ();
    public static ReferexCollection COM = new Com();
    public static ReferexCollection SEC = new Sec();
	
	public abstract String getShortname();
	public abstract String getDisplayName();
	public abstract String getAbbrev();
	public abstract List populateSubjects(boolean che, boolean chestar);

    private static ReferexCollection[] allcolls = new ReferexCollection[] {ELE, CHE, MAT, CIV, COM, SEC};
    
	public static String translateCollection(String longname) {
	    String shortname = "";
        if(longname != null) {
            
            for(int i = 0; i < allcolls.length; i++) {
                ReferexCollection acol = allcolls[i];
                if(longname.equals(acol.getDisplayName())) {
                    shortname = acol.getShortname();
                    break;
                }
            }
		}

		return shortname;
	}

	public EiModifier getModifier() { 
		return new EiModifier(0,getShortname(),getShortname());
	}

	public static ReferexCollection getCollection(String colname) {
        ReferexCollection coll = null;
        if(colname != null)
		{

            for(int i = 0; i < allcolls.length; i++) {
                ReferexCollection acol = allcolls[i];
                if(colname.toLowerCase().startsWith(acol.getAbbrev().toLowerCase())) {
                    coll = acol;
                    break;
                }
            }
		}
		return coll;
	}
}
