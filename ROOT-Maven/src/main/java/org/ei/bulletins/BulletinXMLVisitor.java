package org.ei.bulletins;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.ei.util.StringUtil;

public class BulletinXMLVisitor implements BulletinVisitor {

    private Writer out;
    private String[] cartridges;
    boolean showHtml = false;
    boolean showPdf = false;

    public BulletinXMLVisitor(Writer out, String[] cartridges) {
        this.out = out;
        this.cartridges = cartridges;
    }
    public BulletinXMLVisitor(Writer out) {
        this.out = out;
    }
    public boolean displayBulletin(String category, String db, String id) {

        boolean bDisplay = false;

        for (int i = 0; i < cartridges.length; i++) {

            String currCartridge = getCartridge(category, db);
            if (currCartridge.equalsIgnoreCase(cartridges[i]))
                bDisplay = true;
        }
        return bDisplay;

    }
    /**
     * @return
     */
    public void visitWith(BulletinPage page) throws Exception {

        List lstBulletins = page.getBulletins();

        for (Iterator iter = lstBulletins.iterator(); iter.hasNext();) {
            Bulletin bulletin = (Bulletin) iter.next();
            bulletin.accept(this);
        }

    }
    /**
     * @return
     */
    public void visitWith(Bulletin bulletin) throws Exception
    {

		String fileName = bulletin.getFileName();


        if (displayBulletin(bulletin.getCategory(), bulletin.getDatabase(), bulletin.getId())) {

            out.write("<BL");

            if (bulletin.getFormat() != -1) {

                out.write(" FORMAT=\"");
                out.write(Integer.toString(bulletin.getFormat()));
                out.write("\"");
            }
            out.write(">");
            out.write("<ID>");
            out.write(bulletin.getId());
            out.write("</ID>");
			out.write("<YR>");
			out.write(bulletin.getYear());
            out.write("</YR>");
			out.write("<DB>");
				out.write(bulletin.getDatabase());
            out.write("</DB>");
            if (bulletin.getCategory() != null)
            {
                out.write("<CY><![CDATA[");
                out.write(BulletinQuery.getDisplayCategory(bulletin.getCategory()));
                out.write("]]></CY>");
            //CYD is used to build the directory on the file system
			    out.write("<CYD><![CDATA[");
			    out.write(bulletin.getCategory());
			    out.write("]]></CYD>");
            }

            out.write("<PD><![CDATA[");
            out.write(bulletin.getPublishedDt());
            out.write("]]></PD>");

            out.write("<NM><![CDATA[");
            out.write(bulletin.getFileName());
            out.write("]]></NM>");

            out.write("<WK><![CDATA[");
			out.write(StringUtil.notNull(bulletin.getWeek()));
            out.write("]]></WK>");

            out.write("<ZP><![CDATA[");
            out.write(StringUtil.notNull(bulletin.getZipFileName()));
            out.write("]]></ZP>");
            out.write("</BL>");
        }
    }
    public String getCartridge(String category, String db) {

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

}