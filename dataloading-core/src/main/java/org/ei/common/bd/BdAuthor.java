/*
 * Created on Jun 6, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.common.bd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ei.common.Constants;

//import org.ei.data.bd.loadtime.BdParser;

/**
 * @author solovyevat
 *
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BdAuthor {

    private String auid;  //0
    private String authorId;//1
    private String sec;//2
    private String indexedName;//3
    private String initials;//4       
    private String surname;//6
    private String degrees;//5
    private String givenName;//7
    private String suffix;//8
    private String nametext;//9
    private String eAddress;//10
    private String prefnameInitials;//11
    private String prefnameIndexedname;//12
    private String prefnameDegrees;//13
    private String prefnameSurname;//14
    private String prefnameGivenname;//15
    private String affidStr;//16
    private String alias;//17
    private String altname;//18
    
    private ArrayList<Integer> affid = new ArrayList<Integer>();

    public BdAuthor() {

    }

    public BdAuthor(String bdData, ArrayList<?> elements) {   	
        if (bdData != null && bdData.trim().length() > 0) {
            String[] auelements = bdData.split(Constants.IDDELIMITER, -1);
            for (int i = 0; i < elements.size(); i++) {
                String auField = (String) elements.get(i);
                
                if (i == auelements.length) {
                    break;
                }
                /*
                if(auField.contentEquals("alias")) {
                	if(bdData.indexOf("Fisher")>0) {
                		//System.out.println("AUTHOR STRING= "+bdData);             
                		//System.out.println("auField= "+auField+" I= "+i+" auelements "+auelements.length+" elements= "+elements.size()+" value= "+auelements[i]);
                		for(int j=0;j<auelements.length;j++) {
                			System.out.println(j+" content= "+auelements[j]);
                		}
                	}
                }
                 */
                if (auField.equals("auid")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setAuid(auelements[i]);
                        //System.out.println("AUID="+auelements[i]);
                    }

                }
                
                if (auField.equals("sec")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setSec(auelements[i]);
                        //System.out.println("SEC="+auelements[i]);
                    }

                }
                if (auField.equals("surname")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setSurname(auelements[i]);
                        //System.out.println("SURENAME="+auelements[i]);
                    }

                } else if (auField.equals("givenName")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setGivenName(auelements[i]);
                        //System.out.println("GIVENNAME="+auelements[i]);
                    }

                } else if (auField.equals("indexedName")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setIndexedName(auelements[i]);
                        //System.out.println("INDEXEDNAME="+auelements[i]);
                    }
                } else if (auField.equals("affidStr")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setAffidString(auelements[i]);
                        //System.out.println("AFFIDSTR="+auelements[i]);
                    }
                } else if (auField.equals("initials")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setInitials(auelements[i]);
                        //System.out.println("INITIALS="+auelements[i]);
                    }
                } else if (auField.equals("eAddress")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setEaddress(auelements[i]);
                        //System.out.println("EADDRESS="+auelements[i]);
                    }
                }else if (auField.equals("alias")) {
                    if (auelements[i] != null && !auelements[i].trim().equals("")) {
                        this.setAlias(auelements[i]);
                        //System.out.println("ALIAS= "+auelements[i]);
                    }
                }

            }
        }
    }

    public String getDisplayName() {
        StringBuffer au = new StringBuffer();
        if (this.getSurname() != null && this.getGivenName() != null) {
            au.append(this.getSurname().trim()).append(", ").append(this.getGivenName().trim());
        } else if (this.getSurname() != null && this.getGivenName() == null && this.getInitials() != null) {
            au.append(this.getSurname().trim()).append(", ").append(this.getInitials().trim());
        } else if (this.getIndexedName() != null) {
            au.append(this.getIndexedName());
        } else if (this.getSurname() != null) {
            au.append(this.getSurname());
        } else if (this.getGivenName() != null) {
            au.append(this.getGivenName());
        }

        return au.toString();
    }

    public String getSearchValue() 
    {    	   	
    	if ((this.surname != null) && (this.givenName != null)) 
    	{
            return (addSpecialMarkup(this.surname).concat(" ").concat(addSpecialMarkup(this.givenName)));
        } 
    	else if (this.indexedName != null) 
    	{
            return addSpecialMarkup(this.indexedName);
        }
    	else if (this.surname != null) 
    	{
    		return addSpecialMarkup(this.surname);
    	}
    	else if (this.givenName != null) 
    	{
    		return addSpecialMarkup(this.givenName);
    	}
        return null;
    }
    
    public String getSearchValueWithAlias() {
    	
    	// add alias for search by hmo at 7/16/2019
    	if ((this.surname != null) && (this.givenName != null) && (this.alias != null)) {
    		//System.out.println("ALIAS= "+this.alias);
             return (addSpecialMarkup(this.surname).concat(" ").concat(addSpecialMarkup(this.givenName)).concat(" ").concat(addSpecialMarkup(this.alias)));
    	} 
        return null;
    }
    
    //add special markup to dash by hmo at 6/2/2017
    private String addSpecialMarkup(String input)
    {
    	String output="";
    	if(input!=null)
    	{
    		output = input.replaceAll("-", " qqdashqq ");
    	}
    	else
    	{
    		output = null;
    	}
    	return output;
    }

    /**
     * @return Returns the auid.
     */
    public ArrayList<Integer> getAffid() {
        return affid;
    }

    /**
     * @param auid
     *            The auid to set.
     */
    public void setAffid(int id) {
        Integer i = new Integer(id);
        this.affid.add(i);
    }

    public void addAffid(int id) {
        Integer i = new Integer(id);
        this.affid.add(i);
    }

    /**
     * @return Returns the auid.
     */
    public String getAuid() {
        return auid;
    }

    public void setAffidString(String affidStr) {
        this.affidStr = affidStr;
    }

    public String getAffidString() {
        return affidStr;
    }

    public String getAffidStr() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.affid.size(); i++) {
            buf.append(this.affid.get(i));
            buf.append(Constants.GROUPDELIMITER);
        }
        buf.deleteCharAt((buf.length() - 1));
        return buf.toString();
    }

    public String[] getAffIdList() {
        List<String> result = null;
        if (this.affidStr != null && !this.affidStr.trim().equals("")) {

            if (this.affidStr.indexOf(Constants.GROUPDELIMITER) > 0) {
                List<String> l = Arrays.asList(this.affidStr.split(Constants.GROUPDELIMITER, -1));
                result = new ArrayList<String>();
                for (int i = 0; i < l.size(); i++) {
                    String str = (String) l.get(i);
                    if (str != null && !str.trim().equals("")) {
                        result.add(str);
                    }
                }
            } else {
                result = new ArrayList<String>();
                String abc = affidStr;
                result.add(abc);
            }
        }
        if (result != null && result.size() > 0) {
            return (String[]) result.toArray(new String[result.size()]);
        }
        return null;
    }
    
    /**
     * @return Returns the auid.
     */
    public String getAuthorId() {
        return authorId;
    }
    
    /**
     * @param authorId
     * The authorId to set.
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * @param authorId
     * The authorId to set.
     */
    public void setAuid(String auid) {
        this.auid = auid;
    }

    /**
     * @return Returns the degrees.
     */
    public String getDegrees() {
        return degrees;
    }

    /**
     * @param degrees
     *            The degrees to set.
     */
    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }

    /**
     * @return Returns the e_address.
     */
    public String getEaddress() {
        return eAddress;
    }

    /**
     * @param e_address
     *            The e_address to set.
     */
    public void setEaddress(String eAddress) {
        this.eAddress = eAddress;
    }

    /**
     * @return Returns the given_name.
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param given_name
     *            The given_name to set.
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return Returns the indexed_name.
     */
    public String getIndexedName() {
        return indexedName;
    }

    /**
     * @param indexed_name
     *            The indexed_name to set.
     */
    public void setIndexedName(String indexedName) {
        this.indexedName = indexedName;
    }

    /**
     * @return Returns the initials.
     */
    public String getInitials() {
        return initials;
    }

    /**
     * @param initials
     *            The initials to set.
     */
    public void setInitials(String initials) {
        this.initials = initials;
    }

    /**
     * @return Returns the nametext.
     */
    public String getNametext() {
        return nametext;
    }

    /**
     * @param nametext
     *            The nametext to set.
     */
    public void setNametext(String nametext) {
        this.nametext = nametext;
    }

    /**
     * @return Returns the prefnameDegrees.
     */
    public String getPrefnameDegrees() {
        return prefnameDegrees;
    }

    /**
     * @param prefnameDegrees
     *            The prefnameDegrees to set.
     */
    public void setPrefnameDegrees(String prefnameDegrees) {
        this.prefnameDegrees = prefnameDegrees;
    }

    /**
     * @return Returns the prefnameGivenname.
     */
    public String getPrefnameGivenname() {
        return prefnameGivenname;
    }

    /**
     * @param prefnameGivenname
     *            The prefnameGivenname to set.
     */
    public void setPrefnameGivenname(String prefnameGivenname) {
        this.prefnameGivenname = prefnameGivenname;
    }

    /**
     * @return Returns the prefnameIndexedname.
     */
    public String getPrefnameIndexedname() {
        return prefnameIndexedname;
    }

    /**
     * @param prefnameIndexedname
     *            The prefnameIndexedname to set.
     */
    public void setPrefnameIndexedname(String prefnameIndexedname) {
        this.prefnameIndexedname = prefnameIndexedname;
    }

    /**
     * @return Returns the prefnameInitials.
     */
    public String getPrefnameInitials() {
        return prefnameInitials;
    }

    /**
     * @param prefnameInitials
     *            The prefnameInitials to set.
     */
    public void setPrefnameInitials(String prefnameInitials) {
        this.prefnameInitials = prefnameInitials;
    }

    /**
     * @return Returns the prefnameSurname.
     */
    public String getPrefnameSurname() {
        return prefnameSurname;
    }

    /**
     * @param prefnameSurname
     *            The prefnameSurname to set.
     */
    public void setPrefnameSurname(String prefnameSurname) {
        this.prefnameSurname = prefnameSurname;
    }

    /**
     * @return Returns the sec.
     */
    public String getSec() {
        return sec;
    }

    /**
     * @param sec
     *            The sec to set.
     */
    public void setSec(String sec) {
        this.sec = sec;
    }

    /**
     * @return Returns the suffix.
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix
     *            The suffix to set.
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return Returns the surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname
     *            The surname to set.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return Returns the Alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param AltName
     * The AltName to set.
     */
    public void setAltName(String altname) {
        this.altname = altname;
    }
    
    /**
     * @return Returns the AltName.
     */
    public String getAltName() {
        return altname;
    }

    /**
     * @param Alias
     * The Alias to set.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int compare(Object o1, Object o2) {
        BdAuthor cpxxmlau1 = (BdAuthor) o1;
        BdAuthor cpxxmlau2 = (BdAuthor) o2;

        int cpxisorder1 = Integer.parseInt(cpxxmlau1.getSec());
        int cpxisorder2 = Integer.parseInt(cpxxmlau2.getSec());

        if (cpxisorder1 == cpxisorder2) {
            return 0;
        } else if (cpxisorder1 > cpxisorder2) {
            return 1;
        } else {
            return -1;
        }
    }

}
