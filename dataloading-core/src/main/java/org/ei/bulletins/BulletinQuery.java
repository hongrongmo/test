/*
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.bulletins;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;

/**
 * @author KFokuo
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulletinQuery {

    String db;
    String yr;
    String category;
    private Perl5Util perl = new Perl5Util();
    private static Properties mappings = new Properties();

    static {
        mappings.put("automotive", "Automotive");
        mappings.put("catalysys-zeolites:catalysts-zeolites", "Catalysts/Zeolites");
        mappings.put("catalysts-zeolites", "Catalysts/Zeolites");
        mappings.put("catalysys-zeolites", "Catalysts/Zeolites");
        mappings.put("chemical_products", "Chemical Products");
        mappings.put("environment_transport_storage", "Environment Transport and Storage ");
        mappings.put("fuel_reformation:fuel_reformulation", "Fuel Reformulation");
        mappings.put("fuel_reformation", "Fuel Reformulation");
        mappings.put("fuel_reformulation", "Fuel Reformulation");
        mappings.put("health_environment", "Health and Environment");
        mappings.put("natural_gas", "Natural Gas");
        mappings.put("oilfield_chemicals", "Oil Field Chemicals");
        mappings.put("petroleum_processes", "Petroleum Processes");
        mappings.put("petroleum_refining_petrochemicals", "Petroleum Refining and Petrochemicals ");
        mappings.put("petroleum_speciality_products", "Petroleum and Specialty Products");
        mappings.put("petroleum_substitutes", "Petroleum Substitutes");
        mappings.put("polymers", "Polymers");
        mappings.put("transportation_storage", "Transportation and Storage");
        mappings.put("tribology", "Tribology");
    }

    /**
     * Returns the bulletin category
     * 
     * @return
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the bulletin database
     * 
     * @return
     */
    public String getDatabase() {
        return db;
    }

    /**
     * Returns the bulletin year
     * 
     * @return
     */
    public String getYr() {
        return yr;
    }

    /**
     * Sets the bulletin category
     * 
     * @return
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Sets the bulletin database
     * 
     * @return
     */
    public void setDatabase(String db) {
        this.db = db;
    }

    /**
     * Sets the bulletin year
     * 
     * @return
     */
    public void setYr(String yr) {
        this.yr = yr;
    }

    /**
     * @return
     */
    public void setQuery(String qryString) {

        List<String> qryParms = new ArrayList<String>();

        perl.split(qryParms, "/::/", qryString);

        db = notNull((String) qryParms.get(0));
        yr = notNull((String) qryParms.get(1));
        category = notNull((String) qryParms.get(2));

    }

    /**
     * @return
     */
    private String notNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }

    public String getDisplayQuery() {

        StringBuffer display = new StringBuffer();

        display.append(getDbText(db)).append(" > ").append(yr).append(" > ").append(mappings.get(category));

        return display.toString();
    }

    public String getTitleQuery() {
        return (String) mappings.get(category);
    }

    public static String getDisplayCategory(String category) {

        String sVal = (String) mappings.get(category);

        if (sVal == null)
            return category;

        return sVal;

    }

    public String getDbText(String db) {

        String sVal = "";

        if (db.equals("1"))
            sVal = "EnCompassLIT";
        else if (db.equals("2"))
            sVal = "EnCompassPAT";

        return sVal;
    }

    /**
     * Returns the BulletinQuery as a string
     * 
     * @return
     */
    public String toString() {

        StringBuffer query = new StringBuffer();

        if (db == null && yr == null && category == null)
            return query.toString();
        else
            query.append(db).append("::").append(yr).append("::").append(category);

        return query.toString();
    }
}
