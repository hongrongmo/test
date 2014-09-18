package org.ei.data.bd;

import java.util.*;
import org.ei.data.bd.loadtime.*;

public class BdAuthors {
    private TreeMap<BdAuthor, BdAuthor> cpxausmap;
    private LinkedHashMap<BdAuthor, BdAuthor> bdAuthorsMap;
    private static ArrayList<String> elements = null;
    private static ArrayList<String> auElements = new ArrayList<String>();

    static {
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

    public BdAuthors() {
        cpxausmap = new TreeMap<BdAuthor, BdAuthor>(new AlphaAuComp());
    }

    public BdAuthors(String bdAuthors, ArrayList<String> aElements) {
        elements = aElements;
        bdAuthorsMap = new LinkedHashMap<BdAuthor, BdAuthor>();
        parseData(bdAuthors);

    }

    public BdAuthors(String bdAuthors) {
        elements = auElements;
        bdAuthorsMap = new LinkedHashMap<BdAuthor, BdAuthor>();
        parseData(bdAuthors);
    }

    public void parseData(String bdAuthors) {
        if (bdAuthors != null) {
            String[] auelements = bdAuthors.split(BdParser.AUDELIMITER, -1);

            for (int i = 0; i < auelements.length; i++) {
                if (auelements[i] != null && !auelements[i].trim().equals("")) {
                    BdAuthor bdauthor = new BdAuthor(auelements[i], elements);
                    bdAuthorsMap.put(bdauthor, bdauthor);
                }
            }
        }
    }

    public String[] getSearchValue() {

        ArrayList<String> searchValue = new ArrayList<String>();
        if (bdAuthorsMap != null && bdAuthorsMap.size() > 0) {
            Iterator<BdAuthor> auenum = bdAuthorsMap.keySet().iterator();
            while (auenum.hasNext()) {
                BdAuthor nextau = (BdAuthor) auenum.next();
                searchValue.add(nextau.getSearchValue());
            }

        }

        return (String[]) searchValue.toArray(new String[1]);
    }

    public List<BdAuthor> getAuthors() {
        List<BdAuthor> authorsList = new ArrayList<BdAuthor>();
        authorsList.addAll(bdAuthorsMap.values());
        return authorsList;
    }

    public TreeMap<BdAuthor, BdAuthor> getCpxaus() {
        return cpxausmap;
    }

    public void setCpxaus(TreeMap<BdAuthor, BdAuthor> cpxaus) {
        this.cpxausmap = cpxaus;
    }

    public void addCpxaus(BdAuthor cpxaus) {
        if (seekAuthorID(cpxaus) != 0) {
            cpxausmap.put(cpxaus, cpxaus);
        } else {
            BdAuthor aus = (BdAuthor) cpxausmap.get(cpxaus);
            ArrayList<?> arrid = (ArrayList<?>) cpxaus.getAffid();
            Integer tmp = (Integer) arrid.get(0);
            aus.addAffid(tmp.intValue());
        }
    }

    public BdAuthor getCpxau(BdAuthor cpxaus) {
        return (BdAuthor) cpxausmap.get(cpxaus);
    }

    public int seekAuthorID(BdAuthor cpxaus) {
        if (cpxausmap != null && cpxausmap.size() > 0) {
            Iterator<BdAuthor> itr = cpxausmap.keySet().iterator();
            while (itr.hasNext()) {
                BdAuthor nextaus = (BdAuthor) itr.next();
                if (cpxaus.getSec().equals(nextaus.getSec())) {
                    return 0;
                }
            }
        }
        return -1;
    }

    public boolean contains(BdAuthor cpxaus) {
        if (seekAuthorID(cpxaus) != 0) {
            return false;
        } else {
            return true;
        }
    }

    class AlphaAuComp implements Comparator<Object> {
        BdAuthor au1 = null;
        BdAuthor au2 = null;

        public int compare(Object o1, Object o2) {
            try {
                au1 = (BdAuthor) o1;
                au2 = (BdAuthor) o2;

                if (au1.getSec() == null || (au1.getSec().trim()).length() == 0) {
                    au1.setSec("0");
                }

                if (au2.getSec() == null || (au2.getSec().trim()).length() == 0) {
                    au2.setSec("0");
                }

                int cpxisorder1 = Integer.parseInt(au1.getSec().trim());
                int cpxisorder2 = Integer.parseInt(au2.getSec().trim());

                if (cpxisorder1 == cpxisorder2) {
                    return 0;
                } else if (cpxisorder1 > cpxisorder2) {
                    return 1;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            return -1;
        }

    }
}
