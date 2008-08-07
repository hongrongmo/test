
package org.ei.data.bd;
import java.util.Comparator;
import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdAuthors
{
    private TreeMap cpxausmap;
    private LinkedHashMap bdAuthorsMap ;
    private static ArrayList elements = null;
    private static ArrayList auElements = new ArrayList();

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
	        String [] auelements = bdAuthors.split(BdParser.AUDELIMITER, -1);

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
		if (bdAuthorsMap != null && bdAuthorsMap.size() > 0)
		{
			Iterator auenum = bdAuthorsMap.entrySet().iterator();
			while (auenum.hasNext())
			{
				BdAuthor nextau = (BdAuthor) auenum.next();
				searchValue.add(nextau.getSearchValue());
			}

		}

		return (String[]) searchValue.toArray(new String[1]);
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
		public int compare(Object o1, Object o2)
		{
		    BdAuthor au1 = (BdAuthor)o1;
			BdAuthor au2 = (BdAuthor)o2;
			int cpxisorder1 = Integer.parseInt(au1.getSec());
			int cpxisorder2 = Integer.parseInt(au2.getSec());

			if(cpxisorder1 == cpxisorder2)
			{
				return 0;
			}
			else if(cpxisorder1 > cpxisorder2)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}
}
