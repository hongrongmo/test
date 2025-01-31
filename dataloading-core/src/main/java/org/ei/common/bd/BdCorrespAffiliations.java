package org.ei.common.bd;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.ei.common.Constants;

public class BdCorrespAffiliations
{

    private TreeMap affmap; // treeMap is used to exlude redundant affiliaitons
    private int affid;
    private LinkedHashMap bdAffiliationsMap ;
    private static ArrayList elements = null;
    private static ArrayList caffElements = new ArrayList();
    private List affiliationIds;
    private List departmentIds;


       static
       {
			caffElements.add("affid");
			caffElements.add("affVenus");
			caffElements.add("affText");
       }

       public BdCorrespAffiliations( String bdAffiliations)
       {
           elements = caffElements;
           bdAffiliationsMap = new LinkedHashMap();
           parseData(bdAffiliations);
       }


		public BdCorrespAffiliations( String bdAffiliations, ArrayList aElements )
		{
			elements = aElements;
			bdAffiliationsMap = new LinkedHashMap();
			parseData(bdAffiliations);

		}
    	public void parseData(String bdAffiliations)
    	{

	    	if(bdAffiliations != null)
	    	{
	        	String [] affelements = bdAffiliations.split(Constants.AUDELIMITER,-1);

	        	for (int i = 0; i < affelements.length; i++)
	        	{
		            if(affelements[i] != null && !affelements[i].trim().equals(""))
	            	{
	                	BdCorrespAffiliation bdaff = new BdCorrespAffiliation(affelements[i], elements);
	                	bdAffiliationsMap.put(bdaff,bdaff);
	            	}
				}

	    	}
    	}


       public LinkedHashMap getAffMap()
       {

           return this.bdAffiliationsMap;
       }
       
/* old method used before CAFE data @20160319
   	public String[] getSearchValue()
   	{
   		ArrayList affSearch = new ArrayList();
   		if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
   		{
   			Iterator affenum = bdAffiliationsMap.keySet().iterator();
   			while (affenum.hasNext())
   			{
   				BdCorrespAffiliation nextaff= (BdCorrespAffiliation) affenum.next();
   				affSearch.add(nextaff.getSearchValue());
   			}
   		}
   		return (String[]) affSearch.toArray(new String[1]);
   	}
 */  	
   	public String[] getSearchValue()
	{
		ArrayList affSearch = new ArrayList();
		ArrayList affId = new ArrayList();
		ArrayList affDepartmentid = new ArrayList();
		if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0)
		{
			Iterator affenum = bdAffiliationsMap.keySet().iterator();
			while (affenum.hasNext())
			{
				BdCorrespAffiliation nextaff= (BdCorrespAffiliation) affenum.next();
				affId.add(nextaff.getAffiliationId());
				affDepartmentid.add(nextaff.getAffDepartmentId());
				affSearch.add(nextaff.getSearchValue());
			}
		}
		setAffiliationId(affId);
		setDepartmentId(affDepartmentid);
		return (String[]) affSearch.toArray(new String[1]);
	}
   	
   	private void setAffiliationId(List affiliationIds)
	{
		this.affiliationIds = affiliationIds;
	}
	
	public String[] getAffiliationId()
	{
		return (String[]) this.affiliationIds.toArray(new String[1]);
	}
	
	private void setDepartmentId(List departmentIds)
	{
		this.departmentIds = departmentIds;
	}
	
	public String[] getDepartmentId()
	{
		return (String[]) this.departmentIds.toArray(new String[1]);
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
   				BdCorrespAffiliation nextaff= (BdCorrespAffiliation) affenum.next();
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
   				BdCorrespAffiliation nextaff= (BdCorrespAffiliation) affenum.next();
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

       public int getAffid(BdCorrespAffiliation cpxaff)
       {
           AffIDComp comp = new AffIDComp();
           if(affmap != null && affmap.size() > 0)
           {
               Iterator itr = affmap.keySet().iterator();
               while(itr.hasNext())
               {
                   BdCorrespAffiliation nextaff = (BdCorrespAffiliation) itr.next();
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

       public boolean contains(BdCorrespAffiliation cpxaff)
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

       public int seekAffAffiliaitonID(BdCorrespAffiliation cpxaff)
       {
           AffIDComp comp = new AffIDComp();
           if(affmap != null && affmap.size() > 0)
           {
               Iterator itr = affmap.keySet().iterator();
               while(itr.hasNext())
               {
                   BdCorrespAffiliation nextaff = (BdCorrespAffiliation) itr.next();
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
       public void addAff(BdCorrespAffiliation cpxaff)
       {
           incrementAffCurrentID();
           cpxaff.setAffid(this.affid);
           affmap.put(cpxaff,cpxaff);
       }

   	public BdCorrespAffiliations()
   	{
   	    affmap = new TreeMap(new AffIDComp());
   	}

   	 class AffIDComp
   		implements Comparator
   	{

   		    public int compare (Object o1, Object o2)
   		    {

   		        BdCorrespAffiliation aff1 = (BdCorrespAffiliation) o1;
   		        BdCorrespAffiliation aff2 = (BdCorrespAffiliation) o2;

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

			@Override
			public Comparator reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Comparator other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Function keyExtractor,
					Comparator keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Function keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingInt(ToIntFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingLong(ToLongFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
				// TODO Auto-generated method stub
				return null;
			}
			public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> nullsFirst(
					Comparator<? super T> comparator) {
				// TODO Auto-generated method stub
				return null;
			}

			public <T> Comparator<T> nullsLast(
					Comparator<? super T> comparator) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T, U> Comparator<T> comparing(
					Function<? super T, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T, U extends Comparable<? super U>> Comparator<T> comparing(
					Function<? super T, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingInt(
					ToIntFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingLong(
					ToLongFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingDouble(
					ToDoubleFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
   	}

   	 class AffComp
   		implements Comparator
   	{

   		    public int compare (Object o1, Object o2)
   		    {

   		        BdCorrespAffiliation aff1 = (BdCorrespAffiliation) o1;
   		        BdCorrespAffiliation aff2 = (BdCorrespAffiliation) o2;
   		        //System.out.println(aff1.toString());
   		        //System.out.println("------");
   		        //System.out.println(aff2.toString());
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

			@Override
			public Comparator reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Comparator other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Function keyExtractor,
					Comparator keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparing(Function keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingInt(ToIntFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingLong(ToLongFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
				// TODO Auto-generated method stub
				return null;
			}
			public <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> nullsFirst(
					Comparator<? super T> comparator) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> nullsLast(
					Comparator<? super T> comparator) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T, U> Comparator<T> comparing(
					Function<? super T, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T, U extends Comparable<? super U>> Comparator<T> comparing(
					Function<? super T, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingInt(
					ToIntFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingLong(
					ToLongFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> Comparator<T> comparingDouble(
					ToDoubleFunction<? super T> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
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
   	             BdCorrespAffiliation aff  =(BdCorrespAffiliation) itr.next();
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
