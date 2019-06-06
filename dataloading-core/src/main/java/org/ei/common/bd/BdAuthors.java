
package org.ei.common.bd;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.ei.common.Constants;

public class BdAuthors
{
    private TreeMap cpxausmap;
    private LinkedHashMap bdAuthorsMap ;
    private static ArrayList elements = null;
    private static ArrayList auElements = new ArrayList();
    private List auid = new ArrayList();

    static
    {
        auElements.add("sec");
        auElements.add("auid");
        auElements.add("affidStr");
        auElements.add("indexedName");
        auElements.add("initials");
        auElements.add("surname");
        auElements.add("degrees");
        auElements.add("givenName");
        auElements.add("suffix");
        auElements.add("nametext");
        auElements.add("prefnameInitials");
        auElements.add("prefnameDegrees");
        auElements.add("prefnameSurname");
        auElements.add("prefnameGivenname");
        auElements.add("affid");
        auElements.add("eAddress");
    }

    public BdAuthors()
	{
		cpxausmap = new TreeMap(new AlphaAuComp());
	}

    public BdAuthors( String bdAuthors, ArrayList aElements )
    {
        elements = aElements;
        bdAuthorsMap = new LinkedHashMap();
        parseData(bdAuthors);

    }

    public BdAuthors(String bdAuthors)
    {
        elements = auElements;
	    bdAuthorsMap = new LinkedHashMap();
	    parseData(bdAuthors);
	}

    public void parseData(String bdAuthors)
    {
	    if(bdAuthors != null)
	    {
	        String [] auelements = bdAuthors.split(Constants.AUDELIMITER, -1);

	        for (int i = 0; i < auelements.length; i++)
	        {
	            if(auelements[i] != null && !auelements[i].trim().equals(""))
	            {
	                BdAuthor bdauthor = new BdAuthor(auelements[i],elements);
	                bdAuthorsMap.put(bdauthor,bdauthor);
	            }
			}
	    }
    }

	public String[] getSearchValue()
	{

		ArrayList searchValue = new ArrayList();
		ArrayList auidValue = new ArrayList();
		if (bdAuthorsMap != null && bdAuthorsMap.size() > 0)
		{
			Iterator auenum = bdAuthorsMap.keySet().iterator();
			while (auenum.hasNext())
			{
				String aid = null;
				String orcid = null;
				BdAuthor nextau = (BdAuthor) auenum.next();				
				//pre Frank request, block orcid appear in author navigator
				//setup a new field in fast extract file
				if(nextau.getAuid()!=null){
					// modified to get both authorid and orcid on CAFE data project 3/2/2016
					String authorid = nextau.getAuid();
					String[] authorids = null;
					
					if(authorid.indexOf(",")>-1)
					{
						authorids = authorid.split(",");
						orcid = authorids[0];
						aid = authorids[1];
						
					}
					else
					{
						authorids = new String[1];
						authorids[0] = authorid;
						orcid = authorid;
						aid = null;
					}
					
					for(int i=0;i<authorids.length;i++)
					{
						auidValue.add(authorids[i]);
					}
				}
				
				/*
				 * remove this to avoid unwant characters by hmo on 5/8/2019
				 *
				if(aid!=null)
				{
					searchValue.add(aid+Constants.GROUPDELIMITER+nextau.getSearchValue());
				}
				else
				{
					searchValue.add(nextau.getSearchValue());
				}
				*/
				searchValue.add(nextau.getSearchValue());
				
			}
			if(auidValue.size()>0)
			{
				setAuID(auidValue);
			}

		}

		return (String[]) searchValue.toArray(new String[1]);
	}
	
	public List getAuID()
	{
		return this.auid;
	}
	
	public void setAuID(List auidValue)
	{
		this.auid = auidValue;
	}

	public List getAuthors()
	{
		List authorsList = new ArrayList();
		authorsList.addAll(bdAuthorsMap.values());
		return authorsList;
	}

    public TreeMap getCpxaus()
    {
        return cpxausmap;
    }

    public void setCpxaus(TreeMap cpxaus)
    {
        this.cpxausmap = cpxaus;
    }

    public void addCpxaus(BdAuthor cpxaus)
    {
	    if( seekAuthorID(cpxaus) != 0)
	    {
	        cpxausmap.put(cpxaus,cpxaus);
	    }
	    else
	    {
	        BdAuthor aus = (BdAuthor)cpxausmap.get(cpxaus);
	        ArrayList arrid = (ArrayList) cpxaus.getAffid();
	        Integer  tmp = (Integer) arrid.get(0);
	        aus.addAffid(tmp.intValue());
	    }
    }

    public BdAuthor getCpxau(BdAuthor cpxaus)
    {
        return (BdAuthor)cpxausmap.get(cpxaus);
    }

    public int seekAuthorID(BdAuthor cpxaus)
    {
        if(cpxausmap != null && cpxausmap.size() > 0)
        {
            Iterator itr = cpxausmap.keySet().iterator();
            while(itr.hasNext())
            {
                BdAuthor nextaus = (BdAuthor) itr.next();
                if(cpxaus.getSec().equals(nextaus.getSec()))
                {
                    return 0;
                }
            }
        }
        return -1;
    }

    public boolean contains(BdAuthor cpxaus)
    {
        if (seekAuthorID(cpxaus) != 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

	class AlphaAuComp
		implements Comparator
	{
		BdAuthor au1 = null;
		BdAuthor au2 = null;
		public int compare(Object o1, Object o2)
		{
			try
			{
				au1 = (BdAuthor)o1;
				au2 = (BdAuthor)o2;

				if(au1.getSec()==null || (au1.getSec().trim()).length()==0)
				{
					au1.setSec("0");
				}

				if(au2.getSec()==null || (au2.getSec().trim()).length()==0)
				{
					au2.setSec("0");
				}

				int cpxisorder1 = Integer.parseInt(au1.getSec().trim());
				int cpxisorder2 = Integer.parseInt(au2.getSec().trim());

				if(cpxisorder1 == cpxisorder2)
				{
					return 0;
				}
				else if(cpxisorder1 > cpxisorder2)
				{
					return 1;
				}


			}
			catch(Exception e)
			{

				e.printStackTrace();
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
}
