/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultNavigator
{
    private static Log log = LogFactory.getLog(ResultNavigator.class);

    public static final String REMOVE_MODIFIER =  "QQ_REMOVE_MODIFIER_QQ";

    private List fastnavigators;

    private int m_compmask = 0;

    private boolean m_patents = false;
    private boolean m_other = false;
    private boolean m_mixed = false;
    private boolean m_books = false;
    
    private boolean isCBF = false;

    public ResultNavigator(Hashtable navs, Database[] databases)
    {    
        this.isCBF = isCBF(databases);    	
        this.fastnavigators = getFastNavigators(navs, databases);
       
        // sets m_mixed, m_patents, m_other
        this.getComposition();

        // since we are coming from fast - edit members/change titles/etc,
        this.adjustComposition();
    }
    
    public boolean isCBF(Database[] databases)
    {
    	int len = 0;
    	if (databases != null)
    	{
    		len = databases.length;   		
    	}
    	
    	for (int i = 0; i < len ; i ++)
    	{
    		if( databases[i].getID().equalsIgnoreCase(DatabaseConfig.CBF_PREF))
    		{
    			return true;
    		}  		
    	}
    	
    	return false;    	
    }

    public ResultNavigator(String navigatorsstring)
    {
        if(navigatorsstring != null)
        {
            this.fastnavigators = getNavs(navigatorsstring);
            // set the members of the class which will be used to determine
            // the output order
            this.getComposition();

            // since we are NOT coming from fast but from the cache
            // we do not need to cleanup, etc.
        }
    }

    public int getNavigatorsCount() { return (fastnavigators != null) ? fastnavigators.size() : 0; }

    public int getCompositionMask()
    {
        return m_compmask;
    }

    private void getComposition()
    {
        EiNavigator dbnavigator = this.getNavigatorByName(EiNavigator.DB);

        if(dbnavigator != null)
        {
            List mods = dbnavigator.getModifiers();
            // set the fast navigators collection to point to these navigators
            // so getNavigatorByName will work
            // we will set fastnavigators = navigators again to the return value of this method

            if(mods != null)
            {
                // 1. remove patents navs (PK, etc.) if results other than patents are in result set
                // 2. change AU navigator displayname if patents are in set along with others
                m_patents = (mods.contains(EiModifier.MOD_UPA) || mods.contains(EiModifier.MOD_EUP));
                m_books = mods.contains(EiModifier.MOD_PAG);
                
                m_other = (mods.contains(EiModifier.MOD_CPX) || 
                mods.contains(EiModifier.MOD_INS) || 
                mods.contains(EiModifier.MOD_NTI) || 
                mods.contains(EiModifier.MOD_GEO) ||
                mods.contains(EiModifier.MOD_CBF)
                );
                
                m_mixed = (m_other && m_patents) || (m_books && m_patents) || (m_other && m_books);

                DatabaseConfig dbConfig = DatabaseConfig.getInstance();
                Iterator itrmods = mods.iterator();
                while(itrmods.hasNext())
                {
                    EiModifier amod = (EiModifier) itrmods.next();
                    // make sure db modifier has a value for looking up db
                    if(amod.getValue() != null)
                    {
                        Database adb = dbConfig.getDatabase(amod.getValue());
                        if(adb == null)
                        {
                            // if we failed - try trimming and lowercasing the code
                            if(amod.getValue().length() >= 3)
                            {
                           		adb = dbConfig.getDatabase(amod.getValue().substring(0,3).toLowerCase());                               
                            }
                        }
                        // if db lookup is not null
                        if(adb != null)
                        { 
                            m_compmask += adb.getMask();
                        }
                    }
                }
            }
        }
        if(isCBF && ((m_compmask & DatabaseConfig.C84_MASK)== DatabaseConfig.C84_MASK) )
        {       	
        	m_compmask = m_compmask  - DatabaseConfig.C84_MASK + DatabaseConfig.CBF_MASK;
        }
        
    }

    private void adjustComposition()
    {
        EiNavigator dbnavigator = this.getNavigatorByName(EiNavigator.DB);

        // if we have dbnavigator, use it to decide how to clean out
        // and change Navigators and Modifiers depending on results DB composition
        if(dbnavigator != null)
        {
            List dbmods = dbnavigator.getModifiers();

            if(dbmods != null)
            {
                // 1. remove patents navs (PK, etc.) if results other than patents are in result set
                // 2. change AU navigator displayname if patents are in set along with others
                if((m_mixed) && m_patents)
                {
                    //log.info("removing Patent only navigators from mixed results");
                    EiNavigator anav = getNavigatorByName(EiNavigator.PK);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.PAC);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.PCI);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.PEC);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.PID);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.PUC);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }

                    // change AU Nav title for mixed results
                    anav = getNavigatorByName(EiNavigator.AU);
                    if(anav != null)
                    {
                        anav.setDisplayname("Author/Inventor");
                    }
                    anav = getNavigatorByName(EiNavigator.AF);
                    if(anav != null)
                    {
                        anav.setDisplayname("Author affiliation/Assignee");
                    }

                    // remove PN and LA from mixed results
                    anav = getNavigatorByName(EiNavigator.PN);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.LA);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }

                    anav = getNavigatorByName(EiNavigator.DT);
                    if(anav != null)
                    {
	                    anav.getModifiers().remove(EiModifier.US_GRANTS);
	                    anav.getModifiers().remove(EiModifier.US_APPLICATIONS);
	                    anav.getModifiers().remove(EiModifier.EU_GRANTS);
	                    anav.getModifiers().remove(EiModifier.EU_APPLICATIONS);
                    }
                }
                if((!m_mixed) && m_patents)
                {
                    // if ONLY patents in results set
                    // handle DT if only patents in results set
                    EiNavigator anav = getNavigatorByName(EiNavigator.AU);
                    if(anav != null)
                    {
                    	anav.setDisplayname("Inventor");
                    }
                    anav = getNavigatorByName(EiNavigator.AF);
                    if(anav != null)
                    {
                    	anav.setDisplayname("Assignee");
                    }
                    anav = getNavigatorByName(EiNavigator.DT);
                    if(anav != null)
                    {
	                    anav.setDisplayname("Patent type");
	                    anav.getModifiers().remove(EiModifier.PATENT);
                    }

                    // remove PN and LA from mixed results
                    anav = getNavigatorByName(EiNavigator.PN);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                    anav = getNavigatorByName(EiNavigator.LA);
                    if(anav != null)
                    {
                    	fastnavigators.remove(anav);
                    }
                }
                if((!m_mixed) && m_other)
                {
                    // change AU Nav title for mixed results
                    EiNavigator anav = getNavigatorByName(EiNavigator.AU);
                    if(anav != null)
                    {
                    	anav.setDisplayname("Author");
                    }                    
                    anav = getNavigatorByName(EiNavigator.AF);
                    if(anav != null)
                    {
                    	anav.setDisplayname("Author affiliation");
                    }
                    // if compostion includes NTIS, exlcude PN navigator
                    if(dbmods.contains(EiModifier.MOD_NTI))
                    {
                      // remove PN and LA from mixed results
                      anav = getNavigatorByName(EiNavigator.PN);
                      if(anav != null)
                      {
                    	  fastnavigators.remove(anav);
                      }
                    }
                }
                if(m_books)
                {
                  EiNavigator anav = getNavigatorByName(EiNavigator.CL);
                  if(anav != null)
                  {
                      anav = BookNavigator.cleanCLNavigator(anav);
	                  fastnavigators.add(anav);
                  }
                }
                if((!m_mixed) && m_books)
                {
                	// get DT nav and always remove it if books only
                	EiNavigator anav = getNavigatorByName(EiNavigator.DT);
	                if(anav != null)
	                {
                      fastnavigators.remove(anav);
                      // check for bookrecords and pagerecords doctypes in results
                      List dtmods = anav.getModifiers();
                      boolean bookrecords = dtmods.contains(EiModifier.DT_BOOK);
                      boolean pagerecords = dtmods.contains(EiModifier.DT_PAGE);
                      // if only book records remove ST (book title) nav
                      if((bookrecords) && (!pagerecords))
                      {
                    	  EiNavigator tinav = getNavigatorByName(EiNavigator.ST);
                    	  fastnavigators.remove(tinav);
                      }
	                }

                	// change Class. Code to chapter for when results are books only
                  anav = getNavigatorByName(EiNavigator.CL);
                  if(anav != null)
                  {
	                  anav.setDisplayname("Book Collection");
                  }
                  anav = getNavigatorByName(EiNavigator.FL);
                  if(anav != null)
                  {
	                  anav.setDisplayname("Keyword");
	                  anav.setFieldname("ky");
	                  anav.setName("kynav");
                  }
                  // change Serial title to Book Title for when results are books only
                  anav = getNavigatorByName(EiNavigator.ST);
                  if(anav != null)
                  {
                	  fastnavigators.remove(anav);
                      anav = BookNavigator.createBookNavigator(anav);
                      fastnavigators.add(anav);
                  }

                  // change Country to chapter for when results are books only
                  anav = getNavigatorByName(EiNavigator.CO);
                  if(anav != null)
                  {
            	  	fastnavigators.remove(anav);
                    anav = BookNavigator.createBookNavigator(anav);
                    fastnavigators.add(anav);
                  }
                }
                if(m_mixed && m_books)
                {
                  // remove CO from mixed results that contain books
                  EiNavigator anav = getNavigatorByName(EiNavigator.CO);
                  if(anav != null)
                  {
                	  fastnavigators.remove(anav);
                  }

                  // remove ST from mixed results that contain books
                  anav = getNavigatorByName(EiNavigator.ST);
                  if(anav != null)
                  {
                	  fastnavigators.remove(anav);
                  }
                  // remove FL from mixed results that contain books
                  anav = getNavigatorByName(EiNavigator.FL);
                  if(anav != null)
                  {
                	  fastnavigators.remove(anav);
                  }
                }

                // we used to remove the DB nav if its size was 1 here
                // but we need it later on and want it in the cache too
                // so the toXML has been modified to skip DBNAV with size() == 1

            } // if(mods != null)
        } //  if(dbnavigator != null)
    }

    public void removeRefinements(Refinements xrefs)
    {
        Iterator itrRefinements = xrefs.iterator();
        while(itrRefinements.hasNext())
        {
            EiNavigator refNav = (EiNavigator) itrRefinements.next();
            EiNavigator resultnav = getNavigatorByName(refNav.getName());
            
            if(resultnav == null)
            {
            	resultnav = EiNavigator.createNavigator(refNav.getName());
            }
            //log.info(" removing refinement from Nav. Field [" + refNav.getName() + "]");

            Iterator itrRefMods = refNav.getModifiers().iterator();

            //log.info(refNav.getName() + " has size of = " + refNav.getModifiers().size());

            while(itrRefMods.hasNext())
            {
                EiModifier refmod = (EiModifier) itrRefMods.next();

                // calls List.remove(Object) which will use EiModifier compare() method
                // to find matches in modifier list

                // if we find a match List.remove(Object) will pull the first occurance from the list
                // previously this method 1) used List.indexOf(Object) to check for its existance
                // if found 2) retrieved the object using a List.get(int), then
                // 3) removed the object using List.remove(Object)
                // This consolidates those three calls into one
                resultnav.getModifiers().remove(refmod);

            } // while itrRefMods.hasNext()

            // jam - This was originally inside inner loop
            // if results navigator has 0 modifiers remaining
            // then remove navigator from results
            if(resultnav.getModifiers().size() == 0)
            {
                //log.info(" REMOVING NAVIGATOR ==> " + resultnav);
                fastnavigators.remove(resultnav);
            }
        } // while itrRefinements.hasNext()

    }
    public List getDeDupableDBList()
    {
        ArrayList dedupableDb = new ArrayList(EiModifier.DEDUPABLE_MODS.length);
        EiNavigator dbnav = getNavigatorByName(EiNavigator.DB);
        if(dbnav != null)
        {
	        List mods = dbnav.getModifiers();
	
	        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
	        if((mods != null) && (mods.size() >= 2))
	        {
	            for(int i = 0; i < EiModifier.DEDUPABLE_MODS.length; i++)
	            {
	                if(mods.contains(EiModifier.DEDUPABLE_MODS[i]))
	                {
	                    int modIdx = mods.indexOf(EiModifier.DEDUPABLE_MODS[i]);
	                    Database adb = dbConfig.getDatabase(((EiModifier)mods.get(modIdx)).getValue());
	                    dedupableDb.add(adb);
	                }
	            }
	        }
        }
	    return dedupableDb;
   }

    public boolean isDeDupable()
    {
        return ((getDeDupableDBList()).size() >= 2);
    }


    /**
     * Returns a {@link EiNavigator} object that can then be written out to XML for
     * on-screen display or to string format for caching  
     * The name argument is the name, or id, of the navigator.
     * <p>
     * This method will return null if a navigator with the matching id 
     * cannot be found 
     *
     * @param  name the id of the navigator
     * @return      the EiNavigator with the specified id
     * @see         EiNavigator
     */
    public EiNavigator getNavigatorByName(String navid)
    {
        // returning a 'null' object if navigastor does not exist
        EiNavigator navigator = null;
        if(fastnavigators != null)
        {
            Iterator itrnavs = this.fastnavigators.iterator();
            while(itrnavs.hasNext())
            {
                EiNavigator anav = (EiNavigator) itrnavs.next();
                if(anav != null)
                {
                    String strnavid = (String) anav.getName();
                    if(strnavid.equals(navid))
                    {
                        navigator = anav;
                        break;
                    }
                }
            }
        }
        return navigator;
    }

    public String toString()
    {

        StringBuffer sb = new StringBuffer();
        Iterator itrnavs = fastnavigators.iterator();
        while(itrnavs.hasNext())
        {
            EiNavigator navigator = (EiNavigator) itrnavs.next();
            if(navigator != null)
            {
                sb.append(navigator.toString());
            }

            sb.append(EiNavigator.NAVS_DELIM);
        }
        //log.info(sb.toString());

        return sb.toString();
    }

    public String toXML(ResultsState resultsstate)
    {
        Map mapstate = new Hashtable();

        if(resultsstate != null)
        {
            mapstate = resultsstate.getStateMap();
        }

        StringBuffer sb = new StringBuffer();
        try
        {
            sb.append("<NAVIGATORS>");
            sb.append("<COMPMASK>");
            sb.append(m_compmask);
            sb.append("</COMPMASK>");

            // drive order off of navigator names list
            Iterator itrnavs = adjustNavigatorOrder().iterator();

            while(itrnavs.hasNext())
            {
                String navname = (String) itrnavs.next();
                EiNavigator navigator = getNavigatorByName(navname);
                if(navigator != null)
                {
                    //log.debug(" " + navigator.getModifiers().size());
                    if(navigator.getModifiers().size() == 0)
                    {
                        continue;
                    }

                    if(EiNavigator.DB.equals(navname))
                    {
                        // jam - Here we SKIP the DB navigator if it only contains one DB
                        if(navigator.getModifiers().size() == 1)
                        {
                            //log.info(" REMOVING Single Modifier DB NAVIGATOR ");
                            continue;
                        }
                    }

                    String navfield = (String) navigator.getFieldname();

                    int modifiercount = ResultsState.DEFAULT_STATE_COUNT;
                    if(mapstate.containsKey(navfield))
                    {
                        modifiercount = ((Integer) mapstate.get(navfield)).intValue();
                    }
                    sb.append(navigator.toXML(modifiercount));
                }
            }

            sb.append("</NAVIGATORS>");
            // No more pagers for Navigators - show all available Navigators
            //sb.append(ResultNavigator.getPagers(getNavigators().size(), navigatorcount));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private List getFastNavigators(Hashtable navs, Database[] databases)
    {
        List navigators = new ArrayList();
        List modifiers = new ArrayList();

        EiModifier modifier;
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        ConvertModifierTitle convertModTitle = new ConvertModifierTitle();

        if((navs != null) && !navs.isEmpty())
        {
            Iterator itrNavs = navs.keySet().iterator();

            EiNavigator dbnavigator = null;
            while(itrNavs.hasNext())
            {
                String navigatorname = (String) itrNavs.next();
                List mods = (List) navs.get(navigatorname);
                if((mods != null) && EiNavigator.DB.equalsIgnoreCase(navigatorname))
                {                	
                    dbnavigator = EiNavigator.createNavigator(navigatorname);

                    modifiers = new ArrayList();

                    // loop through all databases in query
                    // possibly missing parent databases in subcats count
                    // so we have to add it in with value of 0
                    // so upgrade will not be skipped
                    Iterator itrmod;
                    for(int i=0; i < databases.length; i++)
                    {
                        Database backfile = databases[i].getBackfile();
                        if(backfile != null)
                        {
                            boolean foundBackfile = false, foundParent = false;
                            itrmod = mods.iterator();
                            while(itrmod.hasNext())
                            {
                                String[] mod = (String[]) itrmod.next();
                                if(databases[i].getIndexName().equals(mod[0]))
                                {
                                    foundParent = true;
                                }
                                if(backfile.getIndexName().equals(mod[0]))
                                {
                                    foundBackfile = true;
                                }
                            }
                            itrmod = null;
                            if(!foundParent && foundBackfile)
                            {
                                //log.debug(" added missing DB navigator " + databases[i].getIndexName());

                                mods.add(new String[]{databases[i].getIndexName(),"0"});
                            }
                        }

                    }

                    Collections.sort(mods, new DBModifierComparator());

                    itrmod = mods.iterator();
                    while(itrmod.hasNext())
                    {
                        String[] mod = (String[]) itrmod.next();
                        String dbid = mod[0];
                       
                        if(dbid != null)
                        {
                            dbid = dbid.substring(0,3);
                        }
                        
                        if(dbid.trim().equalsIgnoreCase(DatabaseConfig.C84_PREF) && isCBF)
                        {
                        	dbid = DatabaseConfig.CBF_PREF;
                        }
                        Database adb = dbConfig.getDatabase(dbid);                        
  
                        if(adb == null)
                        {
                            //log.error(" SKIPPED NULL DB ==> " + dbid);
                            continue;
                        }
                        if(adb.isBackfile())
                        {
                            //log.debug(" SKIPPED BACKFILE DB ==> " + dbid);
                            continue;
                        }
                        int dbcount = Integer.parseInt(mod[1]);

                        //log.debug(" DB ==> " + dbid + "==" + dbcount);

                        Database backfile = adb.getBackfile();
                        if(backfile != null)
                        {
                            Iterator itrback = mods.iterator();
                            while(itrback.hasNext())
                            {
                                String[] back = (String[]) itrback.next();
                                if(back[0].equals(backfile.getID()))
                                {
                                    dbcount += Integer.parseInt(back[1]);
                                    //log.debug(" adding backfile ==> " + back[0] + "==" + back[1]);
                                    break;
                                }
                            }
                            //log.debug(" DB ==> " + dbid + "==" + dbcount);
                        }

                        modifier = dbnavigator.createModifier(dbcount, adb.getName(), adb.getIndexName());
                        modifiers.add(modifier);
                    }
                    dbnavigator.setModifiers(modifiers);
                    navigators.add(dbnavigator);
                }
                else if(mods != null)
                {
                    EiNavigator navigator = EiNavigator.createNavigator(navigatorname);
                    modifiers = new ArrayList();
                    for(int i=0;i < mods.size();i++)
                    {
                        String[] mod = (String[]) mods.get(i);
                        int modcount = 0;
                        try
                        {
                            modcount = (Integer.parseInt(mod[1]));
                        }
                        catch(NumberFormatException nfe)
                        {
                            modcount = 0;
                        }

                        String value = convertModTitle.getValue(mod[0],navigatorname);
                        String modvalue = value;
                        String modlabel = value;
                        String title = convertModTitle.getTitle(mod[0],navigatorname,dbConfig);
                        
                        if(title != null)
                        {
                            if(REMOVE_MODIFIER.equals(title))
                            {
                                // skip modifiers returned from getTitle with title REMOVE_MODIFIER
                                continue;
                            }
                            modlabel = title;
                        }
                        else
                        {
                            modlabel = mod[0];
                        }
                        modifier = navigator.createModifier(modcount, modlabel, modvalue);

                        modifiers.add(modifier);

                    }
                    navigator.setModifiers(modifiers);
                    navigators.add(navigator);
                }
                else
                {
                    //log.debug(" skipped NULL Fast INavigator ==> " + navigatorname);
                }
            } // while

        } //  if (navs != null)

        return navigators;
    }

    // Parsing Routines !!!
    public static List getNavs(String navigatorsstring)
    {
        List navigators = new ArrayList();

        // create from string
        if(navigatorsstring != null)
        {
            String[] navs = navigatorsstring.split(EiNavigator.NAVS_DELIM);
            for(int i=0; i < navs.length; i++)
            {
                navigators.add(EiNavigator.parseNavigator(navs[i]));
            }
        }
        return navigators;
    }

    public static String getPagers(int totalsize, int currentcount)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<NAVPAGER FIELD=\"PAGE\">");
        if(currentcount > ResultsState.DEFAULT_STATE_COUNT)
        {
            sb.append("<LESS COUNT=\"").append(currentcount / ResultsState.DEFAULT_FACTOR).append("\"/>");
        }
        if(totalsize > currentcount)
        {
            sb.append("<MORE COUNT=\"").append(currentcount * ResultsState.DEFAULT_FACTOR).append("\"/>");
        }
        sb.append("</NAVPAGER>");
        return sb.toString();

    }

    private class DBModifierComparator implements Comparator
    {
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        public int compare(Object o1, Object o2)
        {
            String o1s = ((String[]) o1)[0];
            String o2s = ((String[]) o2)[0];
            if(o1s != null)
            {
                o1s = o1s.substring(0,3);
            }
            Database o1db = dbConfig.getDatabase(o1s);

            if(o2s != null)
            {
                o2s = o2s.substring(0,3);
            }
            Database o2db = dbConfig.getDatabase(o2s);

            return o1db.compareTo(o2db);
        }
        public boolean equals(Object obj)
        {
            return (compare(this, obj) == 0);
        }
    }

    private List adjustNavigatorOrder()
    {
        // get default order
        List neworder = EiNavigator.getNavigatorNames();

        if((!m_mixed) && m_patents)
        {
            neworder = new ArrayList();
            neworder.add(EiNavigator.DB);
            neworder.add(EiNavigator.DT);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.AF);
            neworder.add(EiNavigator.PUC);
            neworder.add(EiNavigator.PEC);
            neworder.add(EiNavigator.PID);
            neworder.add(EiNavigator.CO);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.YR);
            neworder.add(EiNavigator.LA);
        }
        if((!m_mixed) && m_other)
        {
            // return default
        }
        if((!m_mixed) && m_books)
        {
            neworder = new ArrayList();
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.BKT);
            neworder.add(EiNavigator.BKS);
            neworder.add(EiNavigator.KY);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.PN);
        }
        if(m_mixed)
        {
            // return default
        }
        return neworder;
    }

}
