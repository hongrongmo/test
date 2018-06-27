package org.ei.bulletins;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Represents a Page of EV Bulletins.  
 */
public class BulletinPage {
	private final static Logger log4j = Logger.getLogger(BulletinPage.class);

	public static final String LIT_TYPE = "LIT";
	public static final String PAT_TYPE = "PAT";

	// Cartridge strings
	private StringBuffer sbCartridges = new StringBuffer();
	private StringBuffer displaycartridges = new StringBuffer();
	private StringBuffer patCartridges = new StringBuffer();
	
	// BulletinGUI object
	private BulletinGUI gui = new BulletinGUI();
	
	// List of Bulletin objects
	private List<Bulletin> lstBulletins = new Vector<Bulletin>();
	private List<Bulletin> displayBulletins;

	// BulletinQuery object
	private BulletinQuery bulletinQuery;
	
	// Array of user cartridges
    private String[] cartridges;
	
	// Flags
	private boolean showpdf = false;
	private boolean showhtml = false;

	/**
	 * Add Bulletin to list
	 * @return
	 */
	public void add(Bulletin bulletin) {

		lstBulletins.add(bulletin);

	}

	/**
	 * Return Bulletin at index
	 * @return
	 */
	public Bulletin get(int index) {

		Bulletin bulletin = (Bulletin) lstBulletins.get(index);

		return bulletin;
	}

	/**
	 * Size of Bulletins 
	 * 
	 * @return
	 */
	public int size() {

		return lstBulletins.size();
	}

	/**
	 * Get all Bulletins
	 * @return
	 */
	public List<Bulletin> getBulletins() {

		return lstBulletins;
	}
    
	/**
	 * Get all displayable Bulletins
	 * @return
	 */
	public List<Bulletin> getDisplayableBulletins() {
		if (displayBulletins == null) {
			displayBulletins = new Vector<Bulletin>();

			if (cartridges == null) {
				log4j.warn("User cartridges MUST be set to use this feature!");
				return displayBulletins;
			}
			
			for (Bulletin bulletin : lstBulletins) {
				if (displayBulletin(bulletin.getCategory(), bulletin.getDatabase(), bulletin.getId())) {
					displayBulletins.add(bulletin);
				}
			}
		}
		return displayBulletins;
	}
    
	/**
	 * Use the BulletinXMLVisitor to 
	 * @param v
	 * @throws Exception
	 */
	public void accept(BulletinXMLVisitor v) throws Exception {
		v.visitWith(this);
	}
	
	/**
	 * Checks a bulletin to see if it's displayable or not.
	 * 
	 * @param category
	 * @param db
	 * @param id
	 * @return
	 */
    private boolean displayBulletin(String category, String db, String id) {

        boolean bDisplay = false;

        for (int i = 0; i < cartridges.length; i++) {

            String currCartridge = getCartridge(category, db);
            if (currCartridge.equalsIgnoreCase(cartridges[i]))
                bDisplay = true;
        }
        return bDisplay;

    }


    /**
     * Callers should call this before trying to work with
     * displaying the BulletinPage object
     * 
     * @param cartridges
     * @param type
     */
    public void initForDisplay(String[] cartridges, String db, String cy) {
    	
    	//
    	// Ensure valid cartridges
    	//
    	if (cartridges == null) {
    		log4j.warn("Cartridges are empty!");
    		return;
    	}
    	
		this.bulletinQuery = new BulletinQuery();
		this.cartridges = cartridges;
		String type = "LIT";
		if ("2".equals(db)) type="PAT";
		
    	//
    	// Create the BulletinQuery object
    	//
		bulletinQuery.setDatabase(db);
		bulletinQuery.setCategory(cy);

		// 
		// Store cartridge information
		//
		for (int i = 0; i < cartridges.length; i++) {
			if (cartridges[i].toUpperCase().indexOf(type + "_HTM") > -1)
				showhtml = true;
			if (cartridges[i].toUpperCase().indexOf(type + "_PDF") > -1)
				showpdf = true;

		
			if (gui.validCartridge(bulletinQuery.getDatabase(), cartridges[i])) {
				sbCartridges.append(cartridges[i].toUpperCase());
				if (i != cartridges.length - 1)
					sbCartridges.append(";");
			}
			if (LIT_TYPE.equals(type) && gui.isLITCartridge(cartridges[i])) {
				displaycartridges.append(cartridges[i].toUpperCase());
				if (i != cartridges.length - 1)
					displaycartridges.append(";");
			}  
			if (PAT_TYPE.equals(type) && gui.isPATCartridge(cartridges[i])) {
				displaycartridges.append(cartridges[i].toUpperCase());
				if (i != cartridges.length - 1)
					displaycartridges.append(";");
			} 
		}
		
    }

    /**
     * Retrieve a cartridge for corresponding category, db
     * 
     * @param category
     * @param db
     * @return
     */
    private String getCartridge(String category, String db) {

        String cartridge = "";

        if (category.equalsIgnoreCase("automotive")) {
            cartridge = "A";
        } else if (category.equalsIgnoreCase("catalysts-zeolites")) {
            cartridge = "CZP";
        } else if (category.equalsIgnoreCase("catalysys-zeolites")) {
            cartridge = "CZL";
        } else if (category.equalsIgnoreCase("chemical_products")) {
            cartridge = "CP";
        } else if (category.equalsIgnoreCase("environment_transport_storage")) {
            cartridge = "ETS";
        } else if (category.equalsIgnoreCase("fuel_reformation")) {
            cartridge = "FRL";
        } else if (category.equalsIgnoreCase("fuel_reformulation")) {
            cartridge = "FRP";
        } else if (category.equalsIgnoreCase("health_environment")) {
            cartridge = "HE";
        } else if (category.equalsIgnoreCase("natural_gas")) {
            cartridge = "NG";
        } else if (category.equalsIgnoreCase("oilfield_chemicals")) {

            if (db.equals("1")) //LIT
                cartridge = "OCL";
            else //PAT
                cartridge = "OCP";

        } else if (category.equalsIgnoreCase("petroleum_processes")) {
            cartridge = "PP";
        } else if (category.equalsIgnoreCase("petroleum_refining_petrochemicals")) {
            cartridge = "PRP";
        } else if (category.equalsIgnoreCase("petroleum_speciality_products")) {
            cartridge = "PSP";
        } else if (category.equalsIgnoreCase("petroleum_substitutes")) {

            if (db.equals("1")) //LIT
                cartridge = "PS_L";
            else //PAT
                cartridge = "PS_P";

        } else if (category.equalsIgnoreCase("polymers")) {
            cartridge = "POL";
        } else if (category.equalsIgnoreCase("transportation_storage")) {
            cartridge = "TS";
        } else if (category.equalsIgnoreCase("tribology")) {

            if (db.equals("1")) //LIT
                cartridge = "TL";
            else //PAT
                cartridge = "TP";
        }

        return cartridge;
    }


    //
    // 
    // GETTERS/SETTERS
    //
    //
    
    
	public boolean isShowpdf() {
		return showpdf;
	}

	public boolean isShowhtml() {
		return showhtml;
	}

	public BulletinQuery getBulletinquery() {
		return bulletinQuery;
	}
	
	public String getSelectcategoryoptions() {
		return BulletinGUI.buildCategoryLb(displaycartridges.toString(), bulletinQuery.getCategory(), bulletinQuery.getDatabase());
	}

	public String getCartridgestring() {
		return displaycartridges.toString();
	}
}