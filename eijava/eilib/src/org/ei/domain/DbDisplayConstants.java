package org.ei.domain;

import java.util.ArrayList;
import java.util.*;

import org.ei.domain.DatabaseConfig;

/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbDisplayConstants
{
    private int dbmask;
    private String dbName;
    private String text;
    private String shortText;
    private String textNoMinus;

    private static Hashtable allConstants = new Hashtable();

    public static final DbDisplayConstants CPX =
        new DbDisplayConstants(DatabaseConfig.CPX_MASK,
                "Compendex",
                "<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">A bibliographic database of engineering...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/>",
                "<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">A bibliographic database of engineering research containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports. Compendex is the most comprehensive bibliographic database of engineering research available today, containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/>",
				"<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">A bibliographic database of engineering research containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports. Compendex is the most comprehensive bibliographic database of engineering research available today, containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports.</a>");
                //"A bibliographic database of engineering research containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports. Compendex is the most comprehensive bibliographic database of engineering research available today, containing over nine million references and abstracts taken from over 5,000 engineering journals, conferences and technical reports. ");

    public static final DbDisplayConstants INS =
        new DbDisplayConstants(DatabaseConfig.INS_MASK,
                "Inspec",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">Provides access to the world&#146;s scientific...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2); return false;\"/>",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">is the leading bibliographic database providing access to the world\\'s scientific literature in electrical engineering, electronics, physics, control engineering, information technology, communications, computers, computing, and manufacturing and production engineering. The database contains over eight million bibliographic records taken from 3,500 scientific and technical journals and 1,500 conference proceedings. Over 400,000 new records are added to the database annually.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2); return false;\"/>",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">is the leading bibliographic database providing access to the world\\'s scientific literature in electrical engineering, electronics, physics, control engineering, information technology, communications, computers, computing, and manufacturing and production engineering. The database contains over eight million bibliographic records taken from 3,500 scientific and technical journals and 1,500 conference proceedings. Over 400,000 new records are added to the database annually.</a>");
                //"is the leading bibliographic database providing access to the world's scientific literature in electrical engineering, electronics, physics, control engineering, information technology, communications, computers, computing, and manufacturing and production engineering. The database contains over eight million bibliographic records taken from 3,500 scientific and technical journals and 1,500 conference proceedings. Over 400,000 new records are added to the database annually.");


    public static final DbDisplayConstants NTI =
        new DbDisplayConstants(DatabaseConfig.NTI_MASK,
                "NTIS",
                "<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">The National Technical Information Service...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(4); return false;\"/>",
                "<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">The National Technical Information Service (NTIS) database from the U.S. Department of Commerce is the premier source for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to over two million critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(4); return false;\"/>",
        		"<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">The National Technical Information Service (NTIS) database from the U.S. Department of Commerce is the premier source for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to over two million critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense.</a>");


    public static final DbDisplayConstants GEO =
        new DbDisplayConstants(DatabaseConfig.GEO_MASK,
                "GEOBASE",
                "<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">A multidisciplinary database for Earth sciences...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(8192); return false;\"/>",
                "<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">is a multidisciplinary database supplying bibliographic information and abstracts for the Earth sciences, ecology, geomechanics, human geography, and oceanography. The database covers approximately 2,000 international journals, including both peer-reviewed titles and trade publications.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(8192); return false;\"/>",
        		"<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">is a multidisciplinary database supplying bibliographic information and abstracts for the Earth sciences, ecology, geomechanics, human geography, and oceanography. The database covers approximately 2,000 international journals, including both peer-reviewed titles and trade publications.</a>");

    public static final DbDisplayConstants PAT =
        new DbDisplayConstants(DatabaseConfig.EUP_MASK,
                "EI Patents",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">The patents databases include US and European...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/>",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">EI Patents The patents databases on Engineering Village include US and European patents grants and applications. Currently, there are over 10 million patents that are being offered.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/>",
        		"<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">EI Patents The patents databases on Engineering Village include US and European patents grants and applications. Currently, there are over 10 million patents that are being offered.</a>" );

    public static final DbDisplayConstants UPA =
        new DbDisplayConstants(DatabaseConfig.UPA_MASK,
                "EI Patents",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">EI patents includes US and European...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(32768); return false;\"/>",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">The patents databases on Engineering Village include US and European patents grants and applications. Currently, there are over 10 million patents that are being offered.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(32768); return false;\"/>",
        		"<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">The patents databases on Engineering Village include US and European patents grants and applications. Currently, there are over 10 million patents that are being offered.</a>");

    public static final DbDisplayConstants PAG =
        new DbDisplayConstants(DatabaseConfig.PAG_MASK,
                "Referex",
                "<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Provides access to the broadest and deepest...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(131072); return false;\"/>",
                "<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Referex provides access to the broadest and deepest available coverage of engineering reference titles in eBook format. Collections within engineering include the following topic areas: Materials and Mechanical, Electronics and Electrical, Chemical, Petrochemical and Process Civil & Environmental, Security & Networking and Computing. Close to 1400 titles are currently available.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(131072); return false;\"/>",
        		"<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Referex provides access to the broadest and deepest available coverage of engineering reference titles in eBook format. Collections within engineering include the following topic areas: Materials and Mechanical, Electronics and Electrical, Chemical, Petrochemical and Process Civil & Environmental, Security & Networking and Computing. Close to 1400 titles are currently available.</a>");

    public static final DbDisplayConstants ELT =
        new DbDisplayConstants(DatabaseConfig.ELT_MASK,
                "EnCompassLIT",
                "<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">Provides access to the broadest and deepest...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1024); return false;\"/>",
                "<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">Provides access to the broadest and deepest available coverage of engineering reference titles in eBook format. Collections within engineering include the following topic areas: Materials and Mechanical, Electronics and Electrical, Chemical, Petrochemical and Process Civil & Environmental, Security & Networking and Computing. Close to 1400 titles are currently available.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1024); return false;\"/>",
        		"<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">Provides access to the broadest and deepest available coverage of engineering reference titles in eBook format. Collections within engineering include the following topic areas: Materials and Mechanical, Electronics and Electrical, Chemical, Petrochemical and Process Civil & Environmental, Security & Networking and Computing. Close to 1400 titles are currently available.</a>");


    public static final DbDisplayConstants EPT =
        new DbDisplayConstants(DatabaseConfig.EPT_MASK,
                "EnCompassPAT",
                "<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">Covers worldwide patent information on...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2048); return false;\"/>",
                "<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">Is the only bibliographic service in the world devoted solely to covering worldwide patents on the downstream and upstream (only oil field chemicals) petroleum, petrochemical, natural gas, energy and allied industries. EnCompassPAT database back to 1964 and contains over 360, 000 records, and contains especially selected patents from 40 patenting authorities throughout the world, indexed on the basis of the EnCompass Thesaurus. More than 25,000 records are added annually, in weekly updates.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2048); return false;\"/>",
        		"<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">Is the only bibliographic service in the world devoted solely to covering worldwide patents on the downstream and upstream (only oil field chemicals) petroleum, petrochemical, natural gas, energy and allied industries. EnCompassPAT database back to 1964 and contains over 360, 000 records, and contains especially selected patents from 40 patenting authorities throughout the world, indexed on the basis of the EnCompass Thesaurus. More than 25,000 records are added annually, in weekly updates.</a>");


    public static final DbDisplayConstants CHM =
        new DbDisplayConstants(DatabaseConfig.CHM_MASK,
                "Chimica",
                "<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Provides exceptional focus on applied...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(128); return false;\"/>",
                "<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Provides access to over 600 of the most influential international journals related to chemistry and chemical engineering. Chimica provides exceptional focus on applied and analytical chemistry, and extends to cover physical chemistry, health and safety, organic and inorganic chemistry, and materials science. Coverage in Chimica starts in 1970.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(128); return false;\"/>",
        		"<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Provides access to over 600 of the most influential international journals related to chemistry and chemical engineering. Chimica provides exceptional focus on applied and analytical chemistry, and extends to cover physical chemistry, health and safety, organic and inorganic chemistry, and materials science. Coverage in Chimica starts in 1970.</a>");


    public static final DbDisplayConstants CBN =
        new DbDisplayConstants(DatabaseConfig.CBN_MASK,
                "CBNB",
                "<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">The leading provider of worldwide chemical...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(256); return false;\"/>",
                "<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">Chemical Business NewsBase (CBNB) is the leading provider of worldwide chemical business news and information. Search for facts, figures, views, and comments on the chemical industry worldwide, from 1985 to the present.  Sources include over 300 core trade journals, newspapers and company newsletters, plus hundreds of books, market research reports, annual and interim company reports, press releases and other \"grey literature\" sources.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(256); return false;\"/>",
        		"<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">Chemical Business NewsBase (CBNB) is the leading provider of worldwide chemical business news and information. Search for facts, figures, views, and comments on the chemical industry worldwide, from 1985 to the present.  Sources include over 300 core trade journals, newspapers and company newsletters, plus hundreds of books, market research reports, annual and interim company reports, press releases and other \"grey literature\" sources.</a>");



    public static final DbDisplayConstants PCH =
        new DbDisplayConstants(DatabaseConfig.CBN_MASK,
                "PaperChem",
                "<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">Covering international literature related to...</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(64); return false;\"/>",
                "<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">Is a comprehensive bibliographic database covering international literature related to pulp and paper technology. It includes abstracts of journal articles, conference papers and technical reports with more than 590,000 records. Subjects covered are: Chemistry of Cellulose, Corrugated and Particle Board, Engineering and Process Control, Films, Foils and Laminates, Forestry and Pulpwood, Graphic Arts (prior to 1998), Lignin and Extractives, Mill Construction and Operation, Pulp, Paper and Board Pollution, Silver Chemicals and Residues, Tissue Culture, Corrosion, Economics and Research, Fiber Webs and NonWovens, Finishing and Converting, Gluing, Labeling, and Sealing, Hemicellulose, Machinery Equipment and Maintenance, Packaging, Power, Spent Liquors and Pollution Control and Water. The database was created in 1967 and records are added weekly. There are also some records in the database with publication years prior to 1967.</a><img id=\"cpxOpenClose\" src=\"/engresources/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(64); return false;\"/>",
        		"<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">Is a comprehensive bibliographic database covering international literature related to pulp and paper technology. It includes abstracts of journal articles, conference papers and technical reports with more than 590,000 records. Subjects covered are: Chemistry of Cellulose, Corrugated and Particle Board, Engineering and Process Control, Films, Foils and Laminates, Forestry and Pulpwood, Graphic Arts (prior to 1998), Lignin and Extractives, Mill Construction and Operation, Pulp, Paper and Board Pollution, Silver Chemicals and Residues, Tissue Culture, Corrosion, Economics and Research, Fiber Webs and NonWovens, Finishing and Converting, Gluing, Labeling, and Sealing, Hemicellulose, Machinery Equipment and Maintenance, Packaging, Power, Spent Liquors and Pollution Control and Water. The database was created in 1967 and records are added weekly. There are also some records in the database with publication years prior to 1967.</a>");

    static
    {
        allConstants.put("cpx", CPX);
        allConstants.put("ins", INS);
        allConstants.put("nti", NTI);
        allConstants.put("geo", GEO);
        allConstants.put("pat", PAT);
        allConstants.put("upa", PAT);
        allConstants.put("pch", PCH);
        allConstants.put("cbn", CBN);
        allConstants.put("chm", CHM);
        allConstants.put("elt", ELT);
        allConstants.put("ept", EPT);
        allConstants.put("eup", PAT);
       // allConstants.put("ref", REF);
        allConstants.put("pag", PAG);
    }

    private DbDisplayConstants(int dbmask,
            				   String dbName,
            				   String shortText,
            				   String text,
            				   String textNoMinus)
    {
        this.dbmask = dbmask;
        this.dbName = dbName;
        this.shortText = shortText;
        this.text = text;
        this.textNoMinus = textNoMinus;
    }
    public int getDbmask()
    {
        return this.dbmask;
    }
    public String getDbname()
    {
        return this.dbName;
    }
    public String getDisplayText()
    {
        return this.text;
    }

    public String getDisplayTextNoMinus()
    {
        return this.textNoMinus;
    }

    public String getDisplayShortText()
    {
        return this.shortText;
    }

    public static String getDbname(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getDbname();
    }
    public static String getDisplayText(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getDisplayText();
    }
    public static String getDisplayTextNoMinus(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getDisplayTextNoMinus();
    }

    public static String getDisplayShortText(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getDisplayShortText();
    }

    public String constDispName(String dbname)
    {
        DbDisplayConstants dbconst =(DbDisplayConstants) allConstants.get(dbname);
        return dbconst.dbName;
    }
}
