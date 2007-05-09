package org.ei.books.collections;

import java.util.List;

import org.ei.domain.navigators.EiModifier;

public abstract class ReferexCollection {

	public static final String ALL = "All Collections";
	
	public static ReferexCollection ELE = new Ele();
	public static ReferexCollection CHE = new Che();
	public static ReferexCollection MAT = new Mat();
	
	public abstract String getShortname();
	public abstract String getDisplayName();
	public abstract String getAbbrev();
	public abstract List populateSubjects(boolean che, boolean chestar);

	public static String translateCollection(String longname) {
		if(longname != null) {
			if(longname.equals(ELE.getDisplayName())) {
				return ELE.getShortname();
			}
			else if(longname.equals(CHE.getDisplayName())) {
				return CHE.getShortname();
			}
			else if(longname.equals(MAT.getDisplayName())) {
				return MAT.getShortname();
			}
		}

		return longname;
	}

	public EiModifier getModifier() { 
		return new EiModifier(0,getShortname(),getShortname());
	}

	public static ReferexCollection getCollection(String col) {
		if(col != null)
		{
			if(col.toLowerCase().startsWith(ELE.getAbbrev().toLowerCase())) {
				return ELE;
			}
			else if(col.toLowerCase().startsWith(CHE.getAbbrev().toLowerCase())) {
				return CHE;
			}
			else if(col.toLowerCase().startsWith(MAT.getAbbrev().toLowerCase())) {
				return MAT;
			}
		}
		return null;
	}
}
