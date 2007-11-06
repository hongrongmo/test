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
import java.util.Enumeration;
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

    private Mods mods = null;
      
    public ResultNavigator(Hashtable navs, Database[] databases)
    {

        this.fastnavigators = getFastNavigators(navs, databases);

        // sets m_mixed, m_patents, m_other
        this.getComposition();

        // since we are coming from fast - edit members/change titles/etc,
        this.adjustComposition();
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
            // set the fast navigators collection to point to these navigators
            // so getNavigatorByName will work
            // we will set fastnavigators = navigators again to the return value of this method
            mods = new Mods();
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            Iterator itrmods = null;
            if(mods.getListmods() != null)
            {
                itrmods = mods.getListmods().iterator();
            }

            while(itrmods!= null && itrmods.hasNext())
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

    private void adjustComposition()
    {
        ResultNavigatorComposition composition = new ResultNavigatorComposition();
        composition.adjustComposition();
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
                                if(back[0].equals(backfile.getIndexName()))
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
        
      //  if(mods != null && mods.isEpt())
        if(mods != null && 
           mods.isMod(NavConstants.EPT))
        {
            neworder = new ArrayList();
            neworder.add(EiNavigator.DB);
            neworder.add(EiNavigator.DT);
            
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.AF);
            neworder.add(EiNavigator.PAC);
            neworder.add(EiNavigator.PEC);
            //neworder.add(EiNavigator.PID); // IPC codes - reformated with standard format
          //  neworder.add(EiNavigator.PUC); // IPC codes - not reformated
            neworder.add(EiNavigator.PK); // IPC codes - not reformated
            neworder.add(EiNavigator.CO);
            neworder.add(EiNavigator.CV);
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.YR);
            neworder.add(EiNavigator.LA);
            neworder.add(EiNavigator.FL);
            return neworder;
        }
        
        if(mods != null && 
                mods.isMod(NavConstants.ELT))
             {
//default
             }        
       // if(mods != null && (!mods.isMixed()) && mods.patents&& !mods.isEpt())
        if(mods != null && (!mods.isMod(NavConstants.MIXED)) && 
           mods.isMod(NavConstants.PAT)&& 
           !mods.isMod(NavConstants.EPT))
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
      //  if(mods != null &&(!mods.isMixed()) && mods.isOther() && !mods.isEpt())
        if(mods != null &&
        	(!mods.isMod(NavConstants.MIXED)) && 
        	mods.isMod(NavConstants.OTHER) && 
        	!mods.isMod(NavConstants.EPT))
        {
        	neworder = new ArrayList();
            // return default
        	neworder.add(EiNavigator.DB);
        	neworder.add(EiNavigator.AU);
        	neworder.add(EiNavigator.AF);
        	neworder.add(EiNavigator.CV);
        	neworder.add(EiNavigator.CL);
        	neworder.add(EiNavigator.CO);
        	neworder.add(EiNavigator.DT);
        	neworder.add(EiNavigator.LA);
        	neworder.add(EiNavigator.YR);
        	neworder.add(EiNavigator.PUC);
        	neworder.add(EiNavigator.PEC);
        	neworder.add(EiNavigator.PID);
//            navigatorNames.add(EiNavigator.PCI);
//            navigatorNames.add(EiNavigator.PK);
        	neworder.add(EiNavigator.PAC);
        	neworder.add(EiNavigator.FL);
        	neworder.add(EiNavigator.ST);
        	neworder.add(EiNavigator.PN);
        	neworder.add(EiNavigator.KY);
        	neworder.add(EiNavigator.BKT);
        	neworder.add(EiNavigator.BKS);
        }

       // if(mods != null &&(!mods.isMixed()) && mods.isBooks() && !mods.isEpt())
        if(mods != null &&
        		(!mods.isMod(NavConstants.MIXED)) && 
        		mods.isMod(NavConstants.PAG) && 
        		!mods.isMod(NavConstants.EPT))
        {
            neworder = new ArrayList();
            neworder.add(EiNavigator.CL);
            neworder.add(EiNavigator.BKT);
            neworder.add(EiNavigator.BKS);
            neworder.add(EiNavigator.KY);
            neworder.add(EiNavigator.AU);
            neworder.add(EiNavigator.PN);
        }
      //  if(mods != null && mods.isMixed() && !mods.isEpt())
        if(mods != null && 
           mods.isMod(NavConstants.MIXED) && 
           !mods.isMod(NavConstants.EPT))
        {
        	
            // return default
        }
        
        int size = neworder.size();
        System.out.println("size::"+ size);
        for (int i = 0; i < size; i++)
        {

        	System.out.println("navs order::"+ neworder.get(i));
        	
        }

        return neworder;
    }

    private class Mods
    {
        private List listdbmods = new ArrayList();        
        private Hashtable dbmodes = new Hashtable();
        
        private Mods()
        {
            EiNavigator dbnavigator = getNavigatorByName(EiNavigator.DB);
            if (dbnavigator != null)
            {
                setMod(NavConstants.DBMODE.getValue());
                this.listdbmods = dbnavigator.getModifiers();
                setMods();
            }            
        }

        private void setMods()
        {
        	// add all modifiers to reslut ht 
        	int size = this.listdbmods.size();
        	for (int i = 0; i < size; i++)
        	{        		
        		setMod((EiModifier) this.listdbmods.get(i));       		
        	}
        	setModPatents();       	
        	setModOther();
        	setModMixed();
        }
        
//        private boolean isMod(String mod)
//        {
//        	if(this.dbmodes.containsKey(mod))
//        	{
//        		return true;
//        	}
//        	return false;
//        }
        
        private boolean isMod(NavConstants navconst)
        {
        	if(this.dbmodes.containsKey(navconst.getValue()))
        	{
        		return true;
        	}
        	return false;
        }
        
//        private boolean isMod(EiModifier em)
//        {
//        	String emvalue = em.getValue();
//        	return isMod(emvalue);
//        }
        
        private void setMod(EiModifier em)
        {
    		this.dbmodes.put(em.getValue().trim(),"true"); 
        }
        private void setMod(String emvalue)
        {
        	this.dbmodes.put(emvalue.trim(),"true"); 
        }
        
        private void setModPatents()
        {
            if (this.dbmodes.containsKey(NavConstants.UPA.getValue()) ||
            		this.dbmodes.containsKey(NavConstants.EUP.getValue()))
            {
                this.dbmodes.put(NavConstants.PAT.getValue(),"true");
            }
        }
        private void setModOther()
        {
            if (this.dbmodes.containsKey(NavConstants.CPX.getValue()) ||
            		this.dbmodes.containsKey(NavConstants.INS.getValue()) ||
            		this.dbmodes.containsKey(NavConstants.NTI.getValue()) ||
            		this.dbmodes.containsKey(NavConstants.GEO.getValue()) ||
            		this.dbmodes.containsKey(NavConstants.CBF.getValue()) )
            {
            	this.dbmodes.put(NavConstants.OTHER.getValue(),"true");
            }
        }
        private void setModMixed()
        {
//            if( (other && patents) ||
//                    (books && patents) ||
//                    (other && books))
        	if((isMod(NavConstants.OTHER) && 
        			isMod(NavConstants.PAT)) ||
        			(isMod(NavConstants.PAG) && isMod(NavConstants.PAT))||
        			(isMod(NavConstants.OTHER) && isMod(NavConstants.PAG)))
            {
                this.dbmodes.put(NavConstants.MIXED.getValue(),"true");
            }
        }
        private List getListmods() 
        {
            return listdbmods;
        }
                
        public String toString()
        {
        	Enumeration en = dbmodes.keys();
        	while (en.hasMoreElements())
        	{
        		System.out.println(en.nextElement());
        	}
        	return null;

        }
    }

    private class ResultNavigatorComposition
    {
       private void adjustComposition()
       {
           // if(mods!= null && mods.isDbmode())
    	   	if(mods!= null && mods.isMod(NavConstants.DBMODE))
            {
    	   	   mods.toString();
    	   		
               if(mods.getListmods() != null)
               {
                  // if((mods.isMixed()) && mods.isPatents() )
            	   if((mods.isMod(NavConstants.MIXED)) && 
            			   mods.isMod(NavConstants.PAT) )
                   {
                	   removeFacet(EiNavigator.PK);
                       removeFacet(EiNavigator.PAC);
                       removeFacet(EiNavigator.PCI);
                       removeFacet(EiNavigator.PEC);
                       removeFacet(EiNavigator.PID);
                       removeFacet(EiNavigator.PUC);
                       removeFacet(EiNavigator.PN);
                       removeFacet(EiNavigator.LA);

                       adjustFacetName(EiNavigator.AU,"Author/Inventor");
                       adjustFacetName(EiNavigator.AF,"Author affiliation/Assignee");

                       adjustModifier(EiNavigator.DT, EiModifier.US_GRANTS);
                       adjustModifier(EiNavigator.DT, EiModifier.US_APPLICATIONS);
                       adjustModifier(EiNavigator.DT, EiModifier.EU_GRANTS);
                       adjustModifier(EiNavigator.DT, EiModifier.EU_APPLICATIONS);
                   }

                   if((!mods.isMod(NavConstants.MIXED)) && 
                		   mods.isMod(NavConstants.PAT))
                   {
                	   removeFacet(EiNavigator.PN);
                       removeFacet(EiNavigator.LA);
                       
                       adjustFacetName(EiNavigator.AU,"Inventor");
                       adjustFacetName(EiNavigator.AF,"Assignee");
                       adjustFacetName(EiNavigator.DT,"Patent type");
                       adjustModifier(EiNavigator.DT, EiModifier.PATENT);
                   }
                   //if((!mods.isMixed()) && mods.isOther())
                   if((!mods.isMod(NavConstants.MIXED)) && 
                		   mods.isMod(NavConstants.OTHER))
                   {
                	   //removeFacet(EiNavigator.PN);
                       //removeFacet(EiNavigator.LA);

                       adjustFacetName(EiNavigator.AU,"Author");
                       adjustFacetName(EiNavigator.AF,"Author affiliation");
                       //if(mods.isNti())
                       if(mods.isMod(NavConstants.NTI))
                       {
                           removeFacet(EiNavigator.PN);
                       }
                   }

                  // if(mods.isMixed() && mods.isBooks())
                  //  EiModifier.MOD_PAG -books
                   if(mods.isMod(NavConstants.MIXED) && 
                		   mods.isMod(NavConstants.PAG))                   
                   {
                      removeFacet(EiNavigator.ST);
                      removeFacet(EiNavigator.CO);
                      removeFacet(EiNavigator.FL);
                   }
                   //if(mods.isBooks())
                   if(mods.isMod(NavConstants.PAG))
                   {
                      cleanCLFacet();
                   }
                   //if((!mods.isMixed()) && mods.isBooks())
                   if(mods.isMod(NavConstants.MIXED) && 
                		   mods.isMod(NavConstants.PAG))
                   {
                	   removeFacet(EiNavigator.DT);
                	   
                     //  if(mods.isBooksRecords() && (!mods.isBooksPages()))
                	 //  EiModifier.DT_BOOK - BooksRecords
                	 //  EiModifier.DT_PAGE - BooksPages
                	   
                	   if(mods.isMod(NavConstants.PAG) && 
                			   mods.isMod(NavConstants.BOOK))
                       {
                           removeFacet(EiNavigator.DT, EiNavigator.ST);
                       }

                       adjustFacetName(EiNavigator.CL,"Book Collection");
                       removeFacet(EiNavigator.ST);
                       removeFacet(EiNavigator.CO);
                       EiNavigator conav = getNavigatorByName(EiNavigator.CO);
                       EiNavigator stnav = getNavigatorByName(EiNavigator.ST);

                       if(conav!= null)
                       {
                    	   addFacet(EiNavigator.CO,BookNavigator.createBookNavigator(conav) );
                       }
                       if(stnav!= null)
                       {
                    	   addFacet(EiNavigator.ST,BookNavigator.createBookNavigator(stnav) );
                       }

                       EiNavigator kynav = new EiNavigator("kynav");
                       kynav.setDisplayname("Keyword");
                       kynav.setFieldname("ky");
                       kynav.setName("kynav");

                       adjustFacetName(EiNavigator.FL, kynav);

                   }

                //   if((!mods.isMixed()) && (!mods.isOther()) &&  
                //		   mods.isEpt() && 
                //		   mods.isElt() )
                   if((!mods.isMod(NavConstants.MIXED)) &&
                		   (!mods.isMod(NavConstants.OTHER)) &&
                		   mods.isMod(NavConstants.EPT) && 
                		   mods.isMod(NavConstants.ELT))                		   
                   {
                       adjustFacetName(EiNavigator.PEC,"Major terms");
                       adjustFacetName(EiNavigator.PK,"IPC Codes");
                       adjustFacetName(EiNavigator.AU,"Author/Inventor");
                       adjustFacetName(EiNavigator.AF,"Author affiliation/Assignee");
                       // change name for uscode ipc 
                       removeFacet(EiNavigator.PID);
                       // remove ipc nav
                   }
//                 else if((!mods.isMixed()) && (!mods.isOther()) &&  
//                		   	mods.isEpt() &&
//                		   !mods.isEup() &&
//                		   !mods.isElt())
                   
                   else if((!mods.isMod(NavConstants.MIXED)) && 
                		   (!mods.isMod(NavConstants.OTHER)) &&  
                		   mods.isMod(NavConstants.EPT) &&  
                   		   !mods.isMod(NavConstants.EUP) && 
                   		   !mods.isMod(NavConstants.ELT))
                   {
                       adjustFacetName(EiNavigator.PEC,"Major terms");
                       adjustFacetName(EiNavigator.PUC,"IPC Codes");
                       adjustFacetName(EiNavigator.AU,"Inventor");
                       adjustFacetName(EiNavigator.AF,"Assignee");
                        // remove  nav
                   }
                                     
//                   else if((!mods.isMixed()) && (!mods.isOther()) &&  
//                		   mods.isEpt() && 
//                		   mods.isEup() &&
//                		   !mods.isElt())
                   	else if((!mods.isMod(NavConstants.MIXED)) &&
                   			(!mods.isMod(NavConstants.OTHER)) &&  
                   			mods.isMod(NavConstants.EPT) && 
                   			mods.isMod(NavConstants.EUP) &&
                   			!mods.isMod(NavConstants.ELT))
                   {
                       adjustFacetName(EiNavigator.PEC,"Major terms");
                       adjustFacetName(EiNavigator.PUC,"IPC Codes");
                       adjustFacetName(EiNavigator.AU,"Inventor");
                       adjustFacetName(EiNavigator.AF,"Assignee");
                       adjustModifier(EiNavigator.DT, EiModifier.EU_APPLICATIONS);
                       adjustModifier(EiNavigator.DT, EiModifier.EU_GRANTS);
                   }
//                   else if((!mods.isMixed()) && 
//                		   (!mods.isOther()) &&  
//                		   	mods.isEpt() && 
//                		   	mods.isUpt()&&
//                		   !mods.isElt())
                  else if((!mods.isMod(NavConstants.MIXED)) && 
                		  (!mods.isMod(NavConstants.OTHER)) &&  
                		  mods.isMod(NavConstants.EPT) && 
                		  mods.isMod(NavConstants.UPA)&&
                		  !mods.isMod(NavConstants.ELT))
                   {
                       adjustFacetName(EiNavigator.PEC,"Major terms");
                       adjustFacetName(EiNavigator.PUC,"IPC Codes");
                       adjustFacetName(EiNavigator.AU,"Inventor");
                       adjustFacetName(EiNavigator.AF,"Assignee");
                       adjustModifier(EiNavigator.DT, EiModifier.PATENT);
                   }
                  // if((!mods.isMixed()) && (!mods.isOther())&& mods.isElt())
                   if((!mods.isMod(NavConstants.MIXED)) && 
                		   (!mods.isMod(NavConstants.OTHER))&& 
                		   mods.isMod(NavConstants.ELT))
                   {
                	   adjustFacetName(EiNavigator.PEC,"Major terms");                	   
                   }
                   //if((!mods.isMixed()) && (!mods.isOther())&& mods.isEpt())
                   if((!mods.isMod(NavConstants.MIXED)) && 
                		   (!mods.isMod(NavConstants.OTHER))&& 
                		   mods.isMod(NavConstants.EPT))
                   {
                	   removeFacet(EiNavigator.PID);
                   }
               }
            }
        }
       
       	private void addFacet(EiNavigator nav)
       	{
           fastnavigators.add(nav);
       	}
       	
        private void addFacet(String firstFacetName, EiNavigator nav)
        {
            EiNavigator facet = getNavigatorByName(firstFacetName);
            if(facet != null)
            {
                addFacet(nav);
            }
        }

        private void cleanCLFacet()
        {
            EiNavigator anav = getNavigatorByName(EiNavigator.CL);
            if(anav != null)
            {
               anav = BookNavigator.cleanCLNavigator(anav);
               fastnavigators.add(anav);
            }
        }
        
        private void removeFacet(String removeFacetName)
        {
            EiNavigator facet = getNavigatorByName(removeFacetName);
            if(facet != null)
            {
                fastnavigators.remove(facet);
            }
        }
        
        private void removeFacet(String firstFacetName, String removeFacetName)
        {
            EiNavigator facet = getNavigatorByName(firstFacetName);
            if(facet != null)
            {
                removeFacet(removeFacetName);
            }
        }

        private void adjustFacetName(String name, String displayName)
        {
            EiNavigator facet = getNavigatorByName(name);
            if(facet != null)
            {
                facet.setDisplayname(displayName);
            }
        }
        
        private void adjustFacetName(String name, EiNavigator nav)
        {
            EiNavigator facet = getNavigatorByName(name);
            if(facet != null)
            {
                facet.setDisplayname(nav.getDisplayname());
                facet.setFieldname(nav.getFieldname());
                facet.setName(nav.getName());
            }
        }
        
        private void adjustModifier(String removeFacetName, EiModifier modifierName)
        {
            EiNavigator facet = getNavigatorByName(removeFacetName);
            if(facet != null)
            {
                facet.getModifiers().remove(modifierName);
            }
        }
    }
}
