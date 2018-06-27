/*
 * Created on Aug 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Refinements {
    private static Log log = LogFactory.getLog(Refinements.class);
    private Stack refinements = new Stack();

    public Refinements() {
    }

    public Refinements(String state) {
        if (state != null) {
            refinements.clear();
            setRefinements(state);
        }

    }

    public int size() {
        if (refinements != null) {
            return refinements.size();
        } else {
            return 0;
        }
    }

    public Iterator iterator() {
        return refinements.iterator();
    }

    public Stack getRefinements() {
        return refinements;
    }

    public String getRefinementQueryString() {
        return getRefinementQueryString(false);
    }

    // private static Pattern PCIpattern = Pattern.compile("\\({0,2}(\\w+)\\){0,1}\\s+WN\\s+PCI\\){0,1}", Pattern.CASE_INSENSITIVE);

    public void addInitialRefinementStep(String physquery, String displayquery) {
        EiNavigator easynav = EiNavigator.createNavigator(EiNavigator.ALL);

        /*
         * Matcher m = PCIpattern.matcher(physquery); if(m.matches() && (m.group(1) != null)) { displayquery =
         * "Patents that cite ".concat(m.group(1).toUpperCase()); }
         */
        // fake "modifier" with count~value~label
        String allmod = "0".concat(EiModifier.MOD_DELIM).concat(physquery).concat(EiModifier.MOD_DELIM).concat(displayquery);
        easynav.setModifierValue(allmod);

        this.addRefinement(easynav);
    }

    public String getRefinementQueryString(boolean prefix) {

        StringBuffer sb = new StringBuffer();
        int refsize = getRefinements().size();
        Iterator itr = getRefinements().iterator();
        boolean isfirst = true;
        while (itr.hasNext()) {
            EiNavigator nav = (EiNavigator) itr.next();

            if (nav != null) {
                if (refsize > 1) {
                    sb.append("(");
                }
                sb.append(nav.getQueryString());
                if (refsize > 1) {
                    sb.append(")");
                }
            }
            if (itr.hasNext()) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }

    public String connectRefinements() {

        StringBuffer sb = new StringBuffer();

        int refsize = getRefinements().size();
        Iterator itr = getRefinements().iterator();
        boolean isfirst = true;
        while (itr.hasNext()) {
            EiNavigator nav = (EiNavigator) itr.next();

            if (refsize > 1) {
                sb.insert(0, "(");
            }

            if (!nav.getIncludeExcludeAll()) {
                sb.append(" NOT ");
            } else {
                if (!isfirst) {
                    sb.append(" AND ");
                }
            }
            if (nav != null) {
                if (!isfirst) {
                    sb.append("(");
                }
                sb.append(nav.getQueryString());
                if (!isfirst) {
                    sb.append(")");
                }

            }
            if (refsize > 1) {
                sb.append(")");
            }

            isfirst = false;
        }

        return sb.toString();
    }

    public void removeRefinementStep(String index) {
        if (index != null) {
            int idx = Integer.parseInt(index);
            if (idx > 0) {
                if (refinements.size() > idx - 1) {
                    refinements.removeElementAt(idx - 1);
                }
            }
        }
    }

    public void setRefinementStep(String index) {
        if (index != null) {
            int idx = Integer.parseInt(index);
            if (idx > 0) {
                while (refinements.size() > idx) {
                    refinements.removeElementAt(idx);
                }
            }

        }
    }

    public void addRefinement(EiNavigator nav) {
        refinements.push(nav);
    }

    public void setRefinements(String navigators) {
        refinements.clear();

        if (navigators != null) {

            refinements.addAll(ResultNavigator.getNavs(navigators));
        }

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator itr = refinements.iterator();
        while (itr.hasNext()) {
            EiNavigator nav = (EiNavigator) itr.next();
            sb.append(nav.toString());
            sb.append(EiNavigator.NAVS_DELIM);
        }
        return sb.toString();
    }

    public String toUnlimitedString() {
        StringBuffer sb = new StringBuffer();
        Iterator itr = refinements.iterator();
        while (itr.hasNext()) {
            EiNavigator nav = (EiNavigator) itr.next();
            if (nav != null) {
                sb.append(nav.toStringNoMaxlength());
                sb.append(EiNavigator.NAVS_DELIM);
            }
        }
        return sb.toString();
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<REFINE-STACK>");

        Iterator itr = refinements.iterator();
        while (itr.hasNext()) {
            EiNavigator nav = (EiNavigator) itr.next();
            if (nav != null) {
                sb.append(nav.toXML());
            }
        }

        sb.append("</REFINE-STACK>");
        return sb.toString();
    }

}
