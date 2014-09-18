package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdCorrespAffiliations {

    private TreeMap<BdCorrespAffiliation, BdCorrespAffiliation> affmap; // treeMap is used to exlude redundant affiliaitons
    private int affid;
    private LinkedHashMap<BdCorrespAffiliation, BdCorrespAffiliation> bdAffiliationsMap;
    private static ArrayList<String> elements = null;
    private static ArrayList<String> caffElements = new ArrayList<String>();

    static {
        caffElements.add("affid");
        caffElements.add("affVenus");
        caffElements.add("affText");
    }

    public BdCorrespAffiliations(String bdAffiliations) {
        elements = caffElements;
        bdAffiliationsMap = new LinkedHashMap<BdCorrespAffiliation, BdCorrespAffiliation>();
        parseData(bdAffiliations);
    }

    public BdCorrespAffiliations(String bdAffiliations, ArrayList<String> aElements) {
        elements = aElements;
        bdAffiliationsMap = new LinkedHashMap<BdCorrespAffiliation, BdCorrespAffiliation>();
        parseData(bdAffiliations);

    }

    public void parseData(String bdAffiliations) {

        if (bdAffiliations != null) {
            String[] affelements = bdAffiliations.split(BdParser.AUDELIMITER, -1);

            for (int i = 0; i < affelements.length; i++) {
                if (affelements[i] != null && !affelements[i].trim().equals("")) {
                    BdCorrespAffiliation bdaff = new BdCorrespAffiliation(affelements[i], elements);
                    bdAffiliationsMap.put(bdaff, bdaff);
                }
            }

        }
    }

    public LinkedHashMap<BdCorrespAffiliation, BdCorrespAffiliation> getAffMap() {

        return this.bdAffiliationsMap;
    }

    public String[] getSearchValue() {
        ArrayList<String> affSearch = new ArrayList<String>();
        if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0) {
            Iterator<BdCorrespAffiliation> affenum = bdAffiliationsMap.keySet().iterator();
            while (affenum.hasNext()) {
                BdCorrespAffiliation nextaff = (BdCorrespAffiliation) affenum.next();
                affSearch.add(nextaff.getSearchValue());
            }
        }
        return (String[]) affSearch.toArray(new String[1]);
    }

    public List<BdCorrespAffiliation> getAffiliations() {
        List<BdCorrespAffiliation> affList = new ArrayList<BdCorrespAffiliation>();
        affList.addAll(bdAffiliationsMap.values());
        Collections.sort(affList, new AffIDComp());

        return affList;
    }

    public String[] getLocationsSearchValue() {
        ArrayList<String> affSearch = new ArrayList<String>();
        if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0) {

            Iterator<BdCorrespAffiliation> affenum = bdAffiliationsMap.keySet().iterator();
            while (affenum.hasNext()) {
                BdCorrespAffiliation nextaff = (BdCorrespAffiliation) affenum.next();
                affSearch.add(nextaff.getLocationsSearchValue());
            }
        }
        return (String[]) affSearch.toArray(new String[1]);
    }

    public String[] getCountriesSearchValue() {
        ArrayList<String> affSearch = new ArrayList<String>();
        if (bdAffiliationsMap != null && bdAffiliationsMap.size() > 0) {

            Iterator<BdCorrespAffiliation> affenum = bdAffiliationsMap.keySet().iterator();
            while (affenum.hasNext()) {
                BdCorrespAffiliation nextaff = (BdCorrespAffiliation) affenum.next();
                affSearch.add(nextaff.getCountriesSearchValue());
            }
        }
        return (String[]) affSearch.toArray(new String[1]);
    }

    /**
     * @return Returns the affid.
     */
    public int getAffid() {
        return affid;
    }

    public int getAffid(BdCorrespAffiliation cpxaff) {
        AffIDComp comp = new AffIDComp();
        if (affmap != null && affmap.size() > 0) {
            Iterator<BdCorrespAffiliation> itr = affmap.keySet().iterator();
            while (itr.hasNext()) {
                BdCorrespAffiliation nextaff = (BdCorrespAffiliation) itr.next();
                if (comp.compare(nextaff, cpxaff) == 0) {
                    return (int) nextaff.getAffid();
                }
            }
        }

        return -1;
    }

    /**
     * @return Returns the affmap.
     */
    public TreeMap<BdCorrespAffiliation, BdCorrespAffiliation> getAffmap() {
        return affmap;
    }

    /**
     * @param affmap
     *            The affmap to set.
     */
    public void setAffmap(TreeMap<BdCorrespAffiliation, BdCorrespAffiliation> affmap) {
        this.affmap = affmap;
    }

    public boolean contains(BdCorrespAffiliation cpxaff) {
        if (seekAffAffiliaitonID(cpxaff) != 0) {
            return false;
        } else {
            return true;
        }
    }

    public int seekAffAffiliaitonID(BdCorrespAffiliation cpxaff) {
        AffIDComp comp = new AffIDComp();
        if (affmap != null && affmap.size() > 0) {
            Iterator<BdCorrespAffiliation> itr = affmap.keySet().iterator();
            while (itr.hasNext()) {
                BdCorrespAffiliation nextaff = (BdCorrespAffiliation) itr.next();
                if (comp.compare(nextaff, cpxaff) == 0) {
                    return (int) nextaff.getAffid();
                }
            }
        }

        return -1;

    }

    public void incrementAffCurrentID() {
        this.affid = this.affid + 1;
    }

    public void addAff(BdCorrespAffiliation cpxaff) {
        incrementAffCurrentID();
        cpxaff.setAffid(this.affid);
        affmap.put(cpxaff, cpxaff);
    }

    public BdCorrespAffiliations() {
        affmap = new TreeMap<BdCorrespAffiliation, BdCorrespAffiliation>(new AffIDComp());
    }

    class AffIDComp implements Comparator<BdCorrespAffiliation> {

        public int compare(BdCorrespAffiliation o1, BdCorrespAffiliation o2) {

            BdCorrespAffiliation aff1 = (BdCorrespAffiliation) o1;
            BdCorrespAffiliation aff2 = (BdCorrespAffiliation) o2;

            if (aff1.getAffid() == aff2.getAffid()) {
                return 0;
            } else if (aff1.getAffid() > aff2.getAffid()) {
                return 1;
            } else if (aff1.getAffid() < aff2.getAffid()) {
                return -1;
            }
            return -1;
        }
    }

    class AffComp implements Comparator<Object> {

        public int compare(Object o1, Object o2) {

            BdCorrespAffiliation aff1 = (BdCorrespAffiliation) o1;
            BdCorrespAffiliation aff2 = (BdCorrespAffiliation) o2;
            // System.out.println(aff1.toString());
            // System.out.println("------");
            // System.out.println(aff2.toString());
            if ((aff1.getAffOrganization().equals(aff2.getAffOrganization())) && (aff1.getAffCityGroup().equals(aff2.getAffCityGroup()))
                && (aff1.getAffAddressPart().equals(aff2.getAffAddressPart()))) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public TreeMap<BdCorrespAffiliation, BdCorrespAffiliation> getAuthorsSorted() {
        return this.affmap;
    }

    public String toString() {
        int i = 0;
        StringBuffer buf = new StringBuffer();
        if (affmap == null || affmap.size() == 0) {
            return "no authors for this record";
        } else {
            Iterator<BdCorrespAffiliation> itr = affmap.keySet().iterator();
            while (itr.hasNext()) {
                BdCorrespAffiliation aff = (BdCorrespAffiliation) itr.next();
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