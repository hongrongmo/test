package org.ei.domain;

import java.util.Hashtable;

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
    private String imgPlus;
    private String imgMinus;

    private static Hashtable<String, DbDisplayConstants> allConstants = new Hashtable<String, DbDisplayConstants>();

    public static final DbDisplayConstants CPX =
        new DbDisplayConstants(DatabaseConfig.CPX_MASK,
                "Compendex",
                "<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">Compendex is the most comprehensive bibliographic database...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">Compendex is the most comprehensive bibliographic database of scientific and technical engineering research available, covering all engineering disciplines. It includes millions of bibliographic citations and abstracts from thousands of engineering journals and conference proceedings. When combined with the Engineering Index Backfile (1884-1969), Compendex covers well over 120 years of core engineering literature.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Compendex</a></br><a class=\"SmBlackText\">Compendex is the most comprehensive bibliographic database of scientific and technical engineering research available, covering all engineering disciplines. It includes millions of bibliographic citations and abstracts from thousands of engineering journals and conference proceedings. When combined with the Engineering Index Backfile (1884-1969), Compendex covers well over 120 years of core engineering literature.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a href=\"\"><img id=\"cpxOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/></a>",
    			"<a href=\"\"><img id=\"cpxOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/></a>");


    public static final DbDisplayConstants C84 =
        new DbDisplayConstants(DatabaseConfig.C84_MASK,
                "Ei Backfile",
                "<a class=\"SmBlueTableText\">Ei Backfile</a></br><a class=\"SmBlackText\">Since its creation in 1884, the Engineering Index&#174 has...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Ei Backfile</a></br><a class=\"SmBlackText\">Since its creation in 1884, the Engineering Index&#174 has covered virtually every major engineering innovation from around the world. It serves as the historical record of virtually every major engineering innovation in the past century.  The Engineering Index Backfile can be searched via controlled vocabulary or free text.   All volume years can be searched simultaneously, eliminating the need to scan multiple volumes. Combined with the Compendex&#174 database, the Ei Backfile provides users with the most comprehensive overview of well over 120 years of engineering literature. </a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Ei Backfile</a></br><a class=\"SmBlackText\">Since its creation in 1884, the Engineering Index&#174 has covered virtually every major engineering innovation from around the world. It serves as the historical record of virtually every major engineering innovation in the past century.  The Engineering Index Backfile can be searched via controlled vocabulary or free text.   All volume years can be searched simultaneously, eliminating the need to scan multiple volumes. Combined with the Compendex&#174 database, the Ei Backfile provides users with the most comprehensive overview of well over 120 years of engineering literature. </a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a href=\"\"><img id=\"cbfOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/></a>",
    			"<a href=\"\"><img id=\"cbfOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1); return false;\"/></a>");

    public static final DbDisplayConstants IBS =
        new DbDisplayConstants(DatabaseConfig.IBS_MASK,
                "Inspec Archive",
                "<a class=\"SmBlueTableText\">Inspec Archive</a></br><a class=\"SmBlackText\">The Inspec Archive is the digital successor to the entire...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Inspec Archive</a></br><a class=\"SmBlackText\">The Inspec Archive is the digital successor to the entire collection of the Science Abstracts Journals. The Archive covers over 873,000 historical abstract records from 1898 to 1968 in the fields of physics, electrical engineering, electronics computing and control engineering. </a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Inspec Archive</a></br><a class=\"SmBlackText\">The Inspec Archive is the digital successor to the entire collection of the Science Abstracts Journals. The Archive covers over 873,000 historical abstract records from 1898 to 1968 in the fields of physics, electrical engineering, electronics computing and control engineering. </a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a href=\"\"><img id=\"ibsOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1048576); return false;\"/></a>",
    			"<a href=\"\"><img id=\"ibsOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1048576); return false;\"/></a>");


    public static final DbDisplayConstants INS =
        new DbDisplayConstants(DatabaseConfig.INS_MASK,
                "Inspec",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">Inspec includes bibliographic citations and indexed...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">Inspec includes bibliographic citations and indexed abstracts from publications in the fields of physics, electrical and electronic engineering, communications, computer science, control engineering, information technology, manufacturing and mechanical engineering, operations research, material science, oceanography, engineering mathematics, nuclear engineering, environmental science, geophysics, nanotechnology, biomedical technology and biophysics.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Inspec</a></br><a class=\"SmBlackText\">Inspec includes bibliographic citations and indexed abstracts from publications in the fields of physics, electrical and electronic engineering, communications, computer science, control engineering, information technology, manufacturing and mechanical engineering, operations research, material science, oceanography, engineering mathematics, nuclear engineering, environmental science, geophysics, nanotechnology, biomedical technology and biophysics.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a href=\"\"><img id=\"insOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2); return false;\"/></a>",
                "<a href=\"\"><img id=\"insOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2); return false;\"/></a>");


    public static final DbDisplayConstants NTI =
        new DbDisplayConstants(DatabaseConfig.NTI_MASK,
                "NTIS",
                "<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">NTIS (The National Technical Information Service) is the...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">NTIS (The National Technical Information Service) is the premier database for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to millions of critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">NTIS</a></br><a class=\"SmBlackText\">NTIS (The National Technical Information Service) is the premier database for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to millions of critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"ntiOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(4); return false;\"/></a>",
        		"<a href=\"\"><img id=\"ntiOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(4); return false;\"/></a>");


    public static final DbDisplayConstants GEO =
        new DbDisplayConstants(DatabaseConfig.GEO_MASK,
                "GEOBASE",
                "<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">GEOBASE&#174 is a multidisciplinary database of indexed...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">GEOBASE&#174 is a multidisciplinary database of indexed research literature on the earth sciences, including geology, human and physical geography, environmental sciences, oceanography, geomechanics, alternative energy sources, pollution, waste management and nature conservation. Covering thousands of peer-reviewed journals, trade publications, book series and conference proceedings, GEOBASE has the most international coverage of any database in the field.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">GEOBASE</a></br><a class=\"SmBlackText\">GEOBASE&#174 is a multidisciplinary database of indexed research literature on the earth sciences, including geology, human and physical geography, environmental sciences, oceanography, geomechanics, alternative energy sources, pollution, waste management and nature conservation. Covering thousands of peer-reviewed journals, trade publications, book series and conference proceedings, GEOBASE has the most international coverage of any database in the field.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"geoOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(8192); return false;\"/></a>",
				"<a href=\"\"><img id=\"geoOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(8192); return false;\"/></a>");

    public static final DbDisplayConstants PAT =
        new DbDisplayConstants(DatabaseConfig.EUP_MASK,
                "EI Patents",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"eupOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/></a>",
				"<a href=\"\"><img id=\"eupOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/></a>");

    public static final DbDisplayConstants EUP =
        new DbDisplayConstants(DatabaseConfig.EUP_MASK,
                "EI Patents",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"eupOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/></a>",
				"<a href=\"\"><img id=\"eupOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(16384); return false;\"/></a>");


    public static final DbDisplayConstants UPA =
        new DbDisplayConstants(DatabaseConfig.UPA_MASK,
                "EI Patents",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EI Patents</a></br><a class=\"SmBlackText\">Ei Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"upaOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(32768); return false;\"/></a>",
        		"<a href=\"\"><img id=\"upaOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(32768); return false;\"/></a>");


    public static final DbDisplayConstants PAG =
        new DbDisplayConstants(DatabaseConfig.PAG_MASK,
                "Referex",
                "<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Referex Engineering is comprised of six collections of...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Referex Engineering is comprised of six collections of professionally focussed e-books, bringing together key sources of engineering reference material. The database is fully searchable and delivers full-text content from the following collections: Chemical, Petrochemical and Process Engineering; Civil and Environmental Engineering; Computing; Electronics and Electrical Engineering; Mechanical Engineering and Materials; Networking and Security.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">Referex</a></br><a class=\"SmBlackText\">Referex Engineering is comprised of six collections of professionally focussed e-books, bringing together key sources of engineering reference material. The database is fully searchable and delivers full-text content from the following collections: Chemical, Petrochemical and Process Engineering; Civil and Environmental Engineering; Computing; Electronics and Electrical Engineering; Mechanical Engineering and Materials; Networking and Security.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"refOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(131072); return false;\"/></a>",
        		"<a href=\"\"><img id=\"refOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(131072); return false;\"/></a>");


    public static final DbDisplayConstants ELT =
        new DbDisplayConstants(DatabaseConfig.ELT_MASK,
                "EnCompassLIT",
                "<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">EnCompassLIT&trade; is a bibliographic service uniquely devoted...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">EnCompassLIT&trade; is a bibliographic service uniquely devoted to covering technical literature published worldwide on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Upstream coverage focuses solely on oil field chemicals. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">EnCompassLIT</a></br><a class=\"SmBlackText\">EnCompassLIT&trade; is a bibliographic service uniquely devoted to covering technical literature published worldwide on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Upstream coverage focuses solely on oil field chemicals. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"eltOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1024); return false;\"/></a>",
        		"<a href=\"\"><img id=\"eltOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(1024); return false;\"/></a>");



    public static final DbDisplayConstants EPT =
        new DbDisplayConstants(DatabaseConfig.EPT_MASK,
                "EnCompassPAT",
                "<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">EnCompassPAT&trade; is a patent service uniquely devoted to...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">EnCompassPAT&trade; is a patent service uniquely devoted to covering worldwide patents on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Patents indexed are selected from 40 international patenting authorities. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">EnCompassPAT</a></br><a class=\"SmBlackText\">EnCompassPAT&trade; is a patent service uniquely devoted to covering worldwide patents on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Patents indexed are selected from 40 international patenting authorities. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"eptOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2048); return false;\"/></a>",
        		"<a href=\"\"><img id=\"eptOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2048); return false;\"/></a>");



    public static final DbDisplayConstants CHM =
        new DbDisplayConstants(DatabaseConfig.CHM_MASK,
                "Chimica",
                "<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Chimica provides access to hundreds of the most influential...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Chimica provides access to hundreds of the most influential international journals focused on chemistry and chemical engineering, with emphasis on applied and analytical chemistry, extending to physical chemistry, health and safety, organic and inorganic chemistry, and materials science. Coverage in Chimica starts in 1970.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">Chimica</a></br><a class=\"SmBlackText\">Chimica provides access to hundreds of the most influential international journals focused on chemistry and chemical engineering, with emphasis on applied and analytical chemistry, extending to physical chemistry, health and safety, organic and inorganic chemistry, and materials science. Coverage in Chimica starts in 1970.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"chmOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(128); return false;\"/></a>",
        		"<a href=\"\"><img id=\"chmOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(128); return false;\"/></a>");



    public static final DbDisplayConstants CBN =
        new DbDisplayConstants(DatabaseConfig.CBN_MASK,
                "CBNB",
                "<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">Chemical Business NewsBase (CBNB) is the leading provider...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">Chemical Business NewsBase (CBNB) is the leading provider of global chemical business news and information. Search for facts, figures, views, and comments on the chemical industry worldwide, from 1985 to the present.  Sources include hundreds of core trade journals, newspapers and company newsletters, plus books, market research reports, annual and interim company reports, press releases and other \"grey literature\" sources.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">CBNB</a></br><a class=\"SmBlackText\">Chemical Business NewsBase (CBNB) is the leading provider of global chemical business news and information. Search for facts, figures, views, and comments on the chemical industry worldwide, from 1985 to the present.  Sources include hundreds of core trade journals, newspapers and company newsletters, plus books, market research reports, annual and interim company reports, press releases and other \"grey literature\" sources.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"cbnOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(256); return false;\"/></a>",
        		"<a href=\"\"><img id=\"cbnOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(256); return false;\"/></a>");




    public static final DbDisplayConstants PCH =
        new DbDisplayConstants(DatabaseConfig.PCH_MASK,
                "PaperChem",
                "<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">PaperChem is a database comprised of indexed bibliographic...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">PaperChem is a database comprised of indexed bibliographic citations and abstracts from journals, conference proceedings, and technical reports focused on pulp and paper technology. Coverage is from 1967 to present, and covers such topics as the chemistry of cellulose, corrugated and particle board; films, foils and laminates; forestry and pulpwood, lignin and extractives, non-wovens, and much more.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a class=\"SmBlueTableText\">PaperChem</a></br><a class=\"SmBlackText\">PaperChem is a database comprised of indexed bibliographic citations and abstracts from journals, conference proceedings, and technical reports focused on pulp and paper technology. Coverage is from 1967 to present, and covers such topics as the chemistry of cellulose, corrugated and particle board; films, foils and laminates; forestry and pulpwood, lignin and extractives, non-wovens, and much more.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
        		"<a href=\"\"><img id=\"pchOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(64); return false;\"/></a>",
        		"<a href=\"\"><img id=\"pchOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(64); return false;\"/></a>");


    public static final DbDisplayConstants GRF =
        new DbDisplayConstants(DatabaseConfig.GRF_MASK,
                "GeoRef",
                "<a class=\"SmBlueTableText\">GeoRef</a></br><a class=\"SmBlackText\">The GeoRef database, established by the American Geological...</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">GeoRef</a></br><a class=\"SmBlackText\">The GeoRef database, established by the American Geological Institute in 1966, provides access to the geoscience literature of the world. GeoRef is the most comprehensive database in the geosciences and continues to grow by more than 90,000 references a year. The database contains over 2.9 million references to geoscience journal articles, books, maps, conference papers, reports and theses.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
                "<a class=\"SmBlueTableText\">GeoRef</a></br><a class=\"SmBlackText\">The GeoRef database, established by the American Geological Institute in 1966, provides access to the geoscience literature of the world. GeoRef is the most comprehensive database in the geosciences and continues to grow by more than 90,000 references a year. The database contains over 2.9 million references to geoscience journal articles, books, maps, conference papers, reports and theses.</a></br><img  src=\"/static/images/s.gif\" border=\"0\" width=\"1\" height=\"6\" />",
            		"<a href=\"\"><img id=\"grfOpenClose\" src=\"/static/images/sidebPlus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2097152); return false;\"/></a>",
            		"<a href=\"\"><img id=\"grfOpenClose\" src=\"/static/images/sidebMinus.gif\" border=\"0\" width=\"10\" height=\"10\" onclick=\"javascript:redrawCell(2097152); return false;\"/></a>");


    static
    {
        allConstants.put("cpx", CPX);
        allConstants.put("c84", C84);
        allConstants.put("ins", INS);
        allConstants.put("nti", NTI);
        allConstants.put("geo", GEO);
        allConstants.put("grf", GRF);
        allConstants.put("pat", EUP);
        allConstants.put("upa", UPA);
        allConstants.put("pch", PCH);
        allConstants.put("cbn", CBN);
        allConstants.put("chm", CHM);
        allConstants.put("elt", ELT);
        allConstants.put("ept", EPT);
        allConstants.put("eup", EUP);
       // allConstants.put("ref", REF);
        allConstants.put("pag", PAG);
        allConstants.put("ibs", IBS);
    }

    private DbDisplayConstants(int dbmask,
            				   String dbName,
            				   String shortText,
            				   String text,
            				   String textNoMinus,
            				   String imgPlus,
            				   String imgMinus)
    {
        this.dbmask = dbmask;
        this.dbName = dbName;
        this.shortText = shortText;
        this.text = text;
        this.textNoMinus = textNoMinus;
        this.imgPlus = imgPlus;
        this.imgMinus = imgMinus;
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

    public String getImgPlus()
    {
        return this.imgPlus;
    }
    public String getImgMinus()
    {
        return this.imgMinus;
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

    public static String getImgPlus(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getImgPlus();
    }

    public static String getImgMinus(String dbcode)
    {
        DbDisplayConstants ddc =(DbDisplayConstants) allConstants.get(dbcode);
        return ddc.getImgMinus();
    }

    public String constDispName(String dbname)
    {
        DbDisplayConstants dbconst =(DbDisplayConstants) allConstants.get(dbname);
        return dbconst.dbName;
    }


}
