package org.ei.thesaurus.cpx;

import java.sql.*;

public class Concept {

	private String mainTermDisplay="";
	private String status="C";
	private String type="";
	private String year="";
	private StringBuffer casNumber=new StringBuffer();
	private StringBuffer casNumberBroadTerm=new StringBuffer();
	private StringBuffer sa=new StringBuffer();
	private StringBuffer see=new StringBuffer();
	private String searchingInformation="";
	private String historyScopeNotes="";
	private StringBuffer useTerms=new StringBuffer();
	private StringBuffer useAndTerms=new StringBuffer();
	private StringBuffer useOrTerms=new StringBuffer();
	private String dateOfIntro="";
	private String scopeNotes="";
	private StringBuffer leadinTerms=new StringBuffer();
	private StringBuffer narrowerTerms=new StringBuffer();
	private StringBuffer broaderTerms=new StringBuffer();
	private StringBuffer relatedTerms=new StringBuffer();
	private StringBuffer classCodes=new StringBuffer();
	private StringBuffer priorTerms=new StringBuffer();
	private StringBuffer topTerms=new StringBuffer();
	private StringBuffer chemicalAspects=new StringBuffer();
	private String textContent="";
	private String geographicType="";
	private String useFor="";
	private String label="";
	private String plus="";
	private String string="";
	private String coordinates="";


	public void setCoordinates(String coordinatesTerm)
	{
		this.coordinates = coordinatesTerm;
	}

	public String getCoordinates()
	{
		return coordinates.toString();
	}

	public void setPlus(String plus)
	{
		this.plus = plus;
	}

	public String getPlus()
	{
		return this.plus;
	}

	public void setString(String string)
	{
		this.string = string;
	}

	public String getString()
	{
		return this.string;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return this.label;
	}

	public void setGeographicType(String geographicType)
	{
		this.geographicType = geographicType;
	}

	public String getGeographicType()
	{
		return this.geographicType;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return this.type;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public String getYear()
	{
		return this.year;
	}

	public void setCasNumber(String casNumberString)
	{
		if(casNumber.length()>0)
		{
			casNumber.append(";");
		}
		this.casNumber.append(casNumberString);
	}

	public String getCasNumber()
	{
		return this.casNumber.toString();
	}

	public void setCasNumberBroadTerm(String casNumberBroadTermString)
	{
		if(casNumberBroadTerm.length()>0)
		{
			casNumberBroadTerm.append(";");
		}
		this.casNumberBroadTerm.append(casNumberBroadTermString);

	}

	public String getCasNumberBroadTerm()
	{
		return this.casNumberBroadTerm.toString();
	}

	public void setChemicalAspects(String chemicalAspect)
	{
		if(chemicalAspects.length()>0)
		{
			chemicalAspects.append(";");
		}
		chemicalAspects.append(chemicalAspect);
	}

	public String getChemicalAspects()
	{
		return this.chemicalAspects.toString();
	}

	public void setSA(String saString)
	{
		if(sa.length()>0)
		{
			sa.append(";");
		}
		sa.append(saString);
	}

	public String getSA()
	{
		return sa.toString();
	}

	public void setSEE(String seeString)
	{
		if(see.length()>0)
		{
			see.append(";");
		}
		see.append(seeString);
	}

	public String getSEE()
	{
		return this.see.toString();
	}

	public void setUseFor(String useFor)
	{
		this.useFor = useFor;
	}

	public String getUseFor()
	{
		return this.useFor;
	}

	public void setSearchingInformation(String searchingInformation)
	{
		this.searchingInformation = searchingInformation;
	}

	public String getSearchingInformation()
	{
		return this.searchingInformation;
	}

	public void setMainTermDisplay(String mainTermDisplay)
	{
		this.mainTermDisplay = mainTermDisplay;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setHistoryScopeNotes(String historyScopeNotes)
	{
		this.historyScopeNotes = historyScopeNotes;
	}

	public void setUseTerms(String useTerm)
	{
		if(useTerms.length()>0)
		{
			useTerms.append(";");
		}
		useTerms.append(useTerm);
	}

	public void setUseAndTerms(String useAndTerm)
	{
		if(useAndTerms.length()>0)
		{
			useAndTerms.append(" AND ");
		}
		useAndTerms.append(useAndTerm);
	}

	public void setUseOrTerms(String useOrTerm)
	{
		if(useOrTerms.length()>0)
		{
			useOrTerms.append(" OR ");
		}
		useOrTerms.append(useOrTerm);
	}

	public void setDateOfIntro(String dateOfIntro)
	{
		this.dateOfIntro = dateOfIntro;
	}

	public void setScopeNotes(String scopeNotes)
	{
		this.scopeNotes = scopeNotes;
	}

	public void setLeadinTerm(String leadinTerm)
	{
		if(leadinTerms.length()>0)
		{
			leadinTerms.append(";");
		}
		leadinTerms.append(leadinTerm);
	}

	public void setNarrowerTerms(String narrowerTerm)
	{
		if(narrowerTerms.length()>0 && (narrowerTerms.length()+narrowerTerm.length())<4000)
		{
			narrowerTerms.append(";");
		}

		if((narrowerTerms.length() + narrowerTerm.length())<4000){
			narrowerTerms.append(narrowerTerm);
		}
	}

	public void setBroaderTerms(String broaderTerm)
	{
		if(broaderTerms.length()>0)
		{
			broaderTerms.append(";");
		}
		broaderTerms.append(broaderTerm);
	}

	public void setRelatedTerms(String relatedTerm)
	{
		if(relatedTerms.length()>0)
		{
			relatedTerms.append(";");
		}
		relatedTerms.append(relatedTerm);
	}

	public void setClassCodes(String classCode)
	{
		if(classCodes.length()>0)
		{
			classCodes.append(";");
		}
		classCodes.append(classCode);
	}

	public void setPriorTerms(String priorTerm)
	{
		if(priorTerms.length()>0)
		{
			priorTerms.append(";");
		}
		priorTerms.append(priorTerm);
	}

	public void setTopTerms(String topTerm)
		{
			if(topTerms.length()>0)
			{
				topTerms.append(";");
			}
			topTerms.append(topTerm);
	}

	public void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}

	public String getMainTermDisplay()
	{
		return this.mainTermDisplay;
	}

	public String getStatus()
	{
		return this.status;
	}

	public String getHistoryScopeNotes()
	{
		return this.historyScopeNotes;
	}

	public String getUseTerms()
	{
		return this.useTerms.toString();
	}

	public String getUseOrTerms()
	{
		return this.useOrTerms.toString();
	}

	public String getUseAndTerms()
	{
		return this.useAndTerms.toString();
	}

	public String getDateOfIntro()
	{
		return this.dateOfIntro;
	}

	public String getScopeNotes()
	{
		return this.scopeNotes;
	}

	public String getNarrowerTerms()
	{
		return this.narrowerTerms.toString();
	}

	public String getBroaderTerms()
	{
		return this.broaderTerms.toString();
	}

	public String getRelatedTerms()
	{
		return this.relatedTerms.toString();
	}

	public String getClassCodes()
	{
		return this.classCodes.toString();
	}

	public String getPriorTerms()
	{
		return this.priorTerms.toString();
	}

	public String getTopTerms()
	{
		return this.topTerms.toString();
	}


	public String getLeadinTerms()
	{
		return this.leadinTerms.toString();
	}

	public String getTextContent()
	{
		return this.textContent;
	}
}