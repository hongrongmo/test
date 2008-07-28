package org.ei.books.library;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Arrays;
import org.ei.books.collections.ReferexCollection;
import java.net.URLEncoder;

public class LibraryVisitor implements Visitor
{

	private HashMap collectionMap = new HashMap();

	private TreeMap colCountMap = new TreeMap();

	private String[] creds;

	private boolean perpetual = true;

	public LibraryVisitor(String[] creds, boolean perpetual)
	{
		this.creds = creds;
		this.perpetual = perpetual;
	}

	public void visit(LibraryComponent libraryComponent)
	{
		System.out.println("LIBRARYCOMPONENT");
	}

	private boolean checkCredentials(Book book, String curCollectionName)
	{
		int subCol = book.getSubCollection();
		String colTest = curCollectionName;

		if (subCol > 0)
		{
			colTest = curCollectionName + Integer.toString(subCol);
		}

		if (perpetual)
		{
			for (int i = 0; i < creds.length; i++)
			{
				String curColTest = colTest.toLowerCase();
				String curCred = creds[i].toLowerCase();
				if (curColTest.equals(curCred))
					return true;
			}
		} else
		{
			for (int i = 0; i < creds.length; i++)
			{
			  // substring(0,3) to get three characters - not substring(0,2)
			  // length of the substring is endIndex-beginIndex.
				String curColTest = colTest.substring(0,3).toLowerCase();
				String curCred = creds[i].substring(0,3).toLowerCase();
				if (curColTest.equals(curCred))
					return true;
			}

		}

		return false;
	}

	public boolean isColInCreds(String collectionName)
	{
		boolean inCreds = false;

		for (int i = 0; i < creds.length; i++)
		{
      if((creds[i] != null) && (creds[i].length() >= 3))
      {
  			String curColName = collectionName.substring(0,2).toLowerCase();
  			String curCred = creds[i].substring(0,2).toLowerCase();
  			if (curColName.equals(curCred))
  			{
  				return true;
  			}
  		}
		}

		return inCreds;

	}

	public void visit(BookCollection bookCollection)
	{
		int bookCount = 0;
		String curCollectionName = bookCollection.getName();

		if (isColInCreds(curCollectionName))
		{
			for (int i = 0; i < bookCollection.getBookCount(); i++)
			{
				Book book = (Book) bookCollection.getBook(i);

				if (checkCredentials(book, curCollectionName))
				{
					bookCount++;
					if (book.getCVS() != null)
					{
						String[] cvs = book.getCVS().split(";");

						for (int j = 0; j < cvs.length; j++)
						{
							if (cvs[j] != null && cvs[j].length() > 1)
							{
								if (colCountMap.containsKey(curCollectionName
										+ ":" + cvs[j]))
								{
									Integer prevCount = (Integer) (colCountMap
											.get(curCollectionName + ":"
													+ cvs[j]));
									Integer curCount = new Integer(prevCount
											.intValue() + 1);
									colCountMap.put(curCollectionName + ":"
											+ cvs[j], curCount);
								} else
								{
									colCountMap.put(curCollectionName + ":"
											+ cvs[j], new Integer(1));
								}
							}
						}
					}
				}
			}

			collectionMap.put(curCollectionName, new Integer(bookCount));
		}
	}

	public void toXML(Writer out, String sbstate)
	{
		try
		{
			int bstate = 0;
			if (sbstate != null)
			{
				try
				{
					bstate = Integer.parseInt(sbstate);
					if ((bstate > 127) || (bstate < 0))
					{
						bstate = 0;
					}
				}
				catch (NumberFormatException e)
				{
					bstate = 0;
				}

			}
			ReferexCollection[] allcolls = ReferexCollection.allcolls;
			LibraryFilters libraryFilters = LibraryFilters.getInstance();
			String curCollectionName = null;

			for (int i = 0; i < allcolls.length; i++)
			{
				for (int j = 0; j < creds.length; j++)
				{
					if(creds[j].length() >= 3)
					{
  					curCollectionName = creds[j].substring(0, 3);

  					if (curCollectionName.equals(allcolls[i].getAbbrev()))
  					{
 						if (j == creds.length-1
  						|| ((j + 1) < creds.length && !curCollectionName.equals(creds[j + 1].substring(0, 3)))
  						|| (!perpetual && creds[j].length() == 3))
  						{
  							Integer subCount = (Integer) collectionMap
  									.get(allcolls[i].getAbbrev());
  							out.write("<" + allcolls[i].getAbbrev() + ">");

  							int colmask = allcolls[i].getColMask();
  							out
  									.write("<BSTATE><![CDATA[<a class=\"SpLink\" href=\"/controller/servlet/Controller?database=131072&CID=ebookSearch&bstate="
  											+ String
  													.valueOf(((bstate & colmask) == colmask) ? (bstate - colmask)
  															: (bstate + colmask))
  											+ "\">");
  							out
  									.write(((bstate & colmask) == colmask) ? "...less"
  											: "more...");
  							out.write("</a>]]></BSTATE>");

  							out
  									.write("<HEAD><![CDATA[<a class=\"SmWhiteText\" href=\"/controller/servlet/Controller?database=131072&sortdir=up&sort=stsort&yearselect=yearrange&CID=quickSearchCitationFormat&searchtype=Book&col="
  											+ URLEncoder.encode(allcolls[i].getShortname())
  											+ "\"><b>"
  											+ allcolls[i].getDisplayName()
  											+ " ("
  											+ subCount
  											+ ")</b></a> <a class=\"SmWhiteText\"></a>]]></HEAD>");
  							out.write("<CVS>");

  							TreeSet treeSet = getColTreeSet();
  							int cvscount = 0;
  							int cvscountshown = ((bstate & colmask) == colmask) ? treeSet
  									.size()
  									: 6;

  							for (Iterator iter = treeSet.iterator(); iter
  									.hasNext();)
  							{
  								Map.Entry entry = (Map.Entry) iter.next();
  								String key = (String) entry.getKey();

  								String curEntry = key.substring(0, 3);
  								String curCVS = key.substring(4, key.length());
  								if (curEntry.equals(curCollectionName))
  								{
  									Integer val = (Integer) entry.getValue();

  									java.lang.reflect.Field[] fields = libraryFilters
  											.getClass().getDeclaredFields();
  									for (int k = 0; k < fields.length; k++)
  									{
  										if (fields[k].getName().equals(
  												curCollectionName))
  										{
  											String[] filter = (String[]) fields[k]
  													.get(libraryFilters);
  											Arrays.sort(filter);

  											if (Arrays.binarySearch(filter,
  													curCVS) >= 0)
  											{
  												out
  														.write("<CV><![CDATA[<a class=\"SpLink\" href=\"/controller/servlet/Controller?database=131072&sortdir=up&sort=stsort&yearselect=yearrange&CID=quickSearchCitationFormat&searchtype=Book&searchWord1={"
  																+ URLEncoder.encode(curCVS)
  																+ "}&section1=CV&boolean1=AND&col="
  																+ URLEncoder.encode(curCollectionName)
  																+ "\">"
  																+ curCVS
  																+ " ("
  																+ val
  																+ ")</a><br/>]]></CV>");
  												cvscount++;
  											}
  										}
  									}

  								}
  								// break out of loop depending on count which
  								// was
  								// set according to state of more/less links
  								// bstate
  								if (cvscount >= cvscountshown)
  								{
  									break;
  								}

							}

							out.write("</CVS>");
							out.write("</" + allcolls[i].getAbbrev() + ">");

						}
					}
				}
      }
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public HashMap getCollectionMap()
	{
		return collectionMap;
	}

	public TreeMap getColCountMap()
	{
		return colCountMap;
	}

	public TreeSet getColTreeSet()
	{

		TreeSet tset = new TreeSet(new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				int returnVal = -1;
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;

				Integer d1 = (Integer) e1.getValue();
				Integer d2 = (Integer) e2.getValue();

				if (d1.equals(d2))
				{
					returnVal = -1;
				} else
					returnVal = d2.compareTo(d1);

				return returnVal;
			}
		});

		tset.addAll(colCountMap.entrySet());

		return tset;
	}

	public void visit(Library library)
	{
		for (int i = 0; i < library.getCollectionCount(); i++)
		{
			BookCollection bookCollection = (BookCollection) library
					.getChild(i);

			bookCollection.accept(this);
		}
	}
}
