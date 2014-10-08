package org.ei.data.bd;
import java.util.Comparator;
import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdAffiliations
{
    private TreeMap affmap; // treeMap is used to exlude redundant affiliaitons
    private int affid;
    private LinkedHashMap bdAffiliationsMap ;
    private static ArrayList elements = null;
    private static ArrayList affElements = new ArrayList();

    static
    {
         affElements.add("affid");
         affElements.add("affOrganization");
         affElements.add("affCityGroup");
         affElements.add("affCountry");
         affElements.add("affAddressPart");
         affElements.add("affCity");
         affElements.add("affState");
         affElements.add("affPostalCode");
         affElements.add("affText");
    }

    public BdAffiliations( String bdAffiliations, ArrayList aElements )
    {
        elements = aElements;
        bdAffiliationsMap = new LinkedHashMap();
        parseData(bdAffiliations);

    }

    public BdAffiliations(String bdAffiliations)
    {
        elements = affElements;
	    bdAffiliationsMap = new LinkedHashMap();
	    parseData(bdAffiliations);
	}
    public void parseData(String bdAffiliations)
    {

	    if(bdAffiliations != null)
	    {
	        String [] affelements = bdAffiliations.split(BdParser.AUDELIMITER,-1);
	        for (int i = 0; i < affelements.length; i++)
	        {
	            if(affelements[i] != null && !affelements[i].trim().equals(""))
	            {
	                BdAffiliation bdaff = new BdAffiliation(affelements[i], elements);
	                bdAffiliationsMap.put(bdaff,bdaff);
	            }
			}
	    }
    }

    public LinkedHashMap getAffMap()
    {

        return this.bdAffiliationsMap;
    }

	public String[] getSearchValue()
	{
		ArrayList affSearch = new ArrayList();
		if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
		{
			Iterator affenum = bdAffiliationsMap.keySet().iterator();
			while (affenum.hasNext())
			{
				BdAffiliation nextaff= (BdAffiliation) affenum.next();
				affSearch.add(nextaff.getSearchValue());
			}
		}
		return (String[]) affSearch.toArray(new String[1]);
	}

	public List getAffiliations()
	{
		List affList = new ArrayList();
		affList.addAll(bdAffiliationsMap.values());
		Collections.sort(affList,new AffIDComp());

		return affList;
	}

	public String[] getLocationsSearchValue()
	{
		ArrayList affSearch = new ArrayList();
		if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
		{

			Iterator affenum = bdAffiliationsMap.keySet().iterator();
			while (affenum.hasNext())
			{
				BdAffiliation nextaff= (BdAffiliation) affenum.next();
				affSearch.add(nextaff.getLocationsSearchValue());
			}
		}
		return (String[]) affSearch.toArray(new String[1]);
	}

	public String[] getCountriesSearchValue()
	{
		ArrayList affSearch = new ArrayList();
		if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
		{

			Iterator affenum = bdAffiliationsMap.keySet().iterator();
			while (affenum.hasNext())
			{
				BdAffiliation nextaff= (BdAffiliation) affenum.next();
				affSearch.add(nextaff.getCountriesSearchValue());
			}
		}
		return (String[]) affSearch.toArray(new String[1]);
	}

    /**
     * @return Returns the affid.
     */
    public int getAffid()
    {
        return affid;
    }

    public int getAffid(BdAffiliation cpxaff)
    {
        AffIDComp comp = new AffIDComp();
        if(affmap != null && affmap.size() > 0)
        {
            Iterator itr = affmap.keySet().iterator();
            while(itr.hasNext())
            {
                BdAffiliation nextaff = (BdAffiliation) itr.next();
                if(comp.compare(nextaff, cpxaff) == 0)
                {
                    return (int)nextaff.getAffid();
                }
            }
        }

        return -1;
    }

    /**
     * @return Returns the affmap.
     */
    public TreeMap getAffmap()
    {
        return affmap;
    }
    /**
     * @param affmap The affmap to set.
     */
    public void setAffmap(TreeMap affmap)
    {
        this.affmap = affmap;
    }

    public boolean contains(BdAffiliation cpxaff)
    {
        if (seekAffAffiliaitonID(cpxaff) != 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int seekAffAffiliaitonID(BdAffiliation cpxaff)
    {
        AffIDComp comp = new AffIDComp();
        if(affmap != null && affmap.size() > 0)
        {
            Iterator itr = affmap.keySet().iterator();
            while(itr.hasNext())
            {
                BdAffiliation nextaff = (BdAffiliation) itr.next();
                if(comp.compare(nextaff, cpxaff) == 0)
                {
                    return (int)nextaff.getAffid();
                }
            }
        }

        return -1;

    }

    public void incrementAffCurrentID()
    {
        this.affid = this.affid + 1;
    }
    public void addAff(BdAffiliation cpxaff)
    {
        incrementAffCurrentID();
        cpxaff.setAffid(this.affid);
        affmap.put(cpxaff,cpxaff);
    }

	public BdAffiliations()
	{
	    affmap = new TreeMap(new AffIDComp());
	}

	 class AffIDComp
		implements Comparator
	{

		    public int compare (Object o1, Object o2)
		    {

		        BdAffiliation aff1 = (BdAffiliation) o1;
		        BdAffiliation aff2 = (BdAffiliation) o2;

		        if(aff1.getAffid() == aff2.getAffid())
		        {
		            return 0;
		        }
		        else if (aff1.getAffid() > aff2.getAffid())
		        {
		            return 1;
		        }
		        else if(aff1.getAffid() < aff2.getAffid())
		        {
		            return -1;
		        }
		        return -1;
		    }
	}

	 class AffComp
		implements Comparator
	{

		    public int compare (Object o1, Object o2)
		    {

		        BdAffiliation aff1 = (BdAffiliation) o1;
		        BdAffiliation aff2 = (BdAffiliation) o2;

		        if((aff1.getAffOrganization().equals(aff2.getAffOrganization())) &&
		                (aff1.getAffCityGroup().equals(aff2.getAffCityGroup()))&&
		                (aff1.getAffAddressPart().equals(aff2.getAffAddressPart())))
		        {
		            return 0;
		        }
		        else
		        {
		            return -1;
		        }
		    }
	}

	 public TreeMap getAuthorsSorted()
	 {
	     return this.affmap;
	 }


	 public String toString()
	 {
	     int i = 0;
	     StringBuffer buf = new StringBuffer();
	     if (affmap == null || affmap.size() == 0)
	     {
	         return "no authors for this record";
	     }
	     else
	     {
	         Iterator itr = affmap.keySet().iterator();
	         while (itr.hasNext())
	         {
	             BdAffiliation aff  =(BdAffiliation) itr.next();
	             buf.append("\nauid = ").append(aff.getAffid());
	             buf.append("\nAffCityGroup = ").append(aff.getAffCityGroup());
	             buf.append("\nAffAddressPart = ").append(aff.getAffAddressPart());
	             buf.append("\nAffCountry = ").append(aff.getAffCountry());
	             buf.append("\nAffCity = ").append(aff.getAffCity());
	             buf.append("\nAffState = ").append(aff.getAffState());
	             buf.append("\nAffPostalCode = ").append(aff.getAffPostalCode());
	             buf.append("\n");
	             buf.append("\n---------- end of authorAffiliation set # = ").append(i++);
	         }
	    }

	     return buf.toString();
	 }

}
