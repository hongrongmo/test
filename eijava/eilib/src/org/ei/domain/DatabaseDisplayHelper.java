package org.ei.domain;

import java.util.*;
import org.ei.util.StringUtil;

public class DatabaseDisplayHelper {
	// jam: Changed db Mask Value
	// mask will include upgrade (32 + 1)
	public static String getNewsText(int mask) {

		String strText = StringUtil.EMPTY_STRING;

		if((mask & 8) == 8)
		{
			//System.out.println("Removing CRC");
			mask = mask - 8;
		}

		if((mask & 16) == 16)
		{
			//System.out.println("Removing uspto");
			mask = mask - 16;
		}

		// Remove referex from the mask.
		boolean referex = false;
		if(mask == 131072)
		{
			return "<p>Referex Engineering is a specialized electronic reference product that draws upon hundreds of premium engineering titles to provide engineering students and professionals with the answers and information they require at school, work, and in practice.";
		}

		if((mask & 131072) == 131072)
		{
			referex = true;
			mask = mask - 131072;
		}
		if((mask & 262144) == 262144)
		{
			return "Engineering Index Backfile (EI Backfile) covers the information from the printed Engineering Index from 1884-1969.  It provides engineering researchers with the most comprehensive overview of engineering literature available from that period.";
		}

		switch (mask & 61479)
		{
			case 1 : // CPX
				strText =
					"Compendex&#174; is the most comprehensive interdisciplinary engineering database in the world.  Compendex contains over 9 million records and references over  5,000 international engineering sources including journal, conference, and trade publications. Coverage is from 1969 to present and the database is updated weekly.";
				break;
			case ((16384 + 32768)) : // (US & EP Patents)
				strText =
					"Ei patents cover US and European patents applications and grants.";
				break;
			case (1 + (16384 + 32768)) : // CPX & (US & EP Patents)
				strText =
					"Compendex&#174; and Patents are the most comprehensive interdisciplinary engineering database in the world.  Compendex contains over 9 million records and references over  5,000 international engineering sources including journal, conference, and trade publications. Coverage is from 1969 to present and the database is updated weekly.";
				break;
			case 33 : // C84
				strText =
					"Compendex&#174; is the most comprehensive interdisciplinary engineering database in the world.  Compendex contains over 9 million records and references over 5,000 international engineering sources including journal, conference, and trade publications.  Coverage is from 1969 to present and the database is updated weekly.  Combined with the Engineering Index Backfile, the database includes over 9.7 million records and spans back to 1884.";
				break;
			case (33 + (16384 + 32768)) : // C84 & (US & EP Patents)
				strText =
					"Compendex&#174; and Patents are the most comprehensive interdisciplinary engineering database in the world.  Compendex contains over 9 million records and references over 5,000 international engineering sources including journal, conference, and trade publications.  Coverage is from 1969 to present and the database is updated weekly.  Combined with the Engineering Index Backfile, the database includes over 9.7 million records and spans back to 1884.";
				break;
			case 2 : // INS
				strText =
					"Inspec&#174; is the world's leading bibliographic database covering the fields of physics, electronics, computing, control engineering and information technology. Inspec includes over 8 million records taken from 3,400 technical and scientific journals and 2,000 conference proceedings. Coverage is from 1969 to the present. The database is updated weekly.";
				break;
			case (2 + (16384 + 32768)) : // INS & (US & EP Patents)
				strText =
					"Inspec&#174; and Patents are the world's leading bibliographic database covering the fields of physics, electronics, computing, control engineering and information technology. Inspec includes over 8 million records taken from 3,400 technical and scientific journals and 2,000 conference proceedings. Coverage is from 1969 to the present. The database is updated weekly.";
				break;
			case (2 + 4096) : // INS & IBF
				strText =
					"Inspec&#174; is the world's leading bibliographic database covering the fields of physics, electronics, computing, control engineering and information technology. Inspec includes over 8 million records taken from 3,400 technical and scientific journals and 2,000 conference proceedings. Coverage is from 1969 to the present and the database is updated weekly. Combined with the Inspec Archive, the database includes over 8.8 million records and spans back to 1896.";
				break;
			case (2 + 4096 + (16384 + 32768)) : // INS & IBF & (US & EP Patents)
				strText =
					"Inspec&#174; and Patents are the world's leading bibliographic database covering the fields of physics, electronics, computing, control engineering and information technology. Inspec includes over 8 million records taken from 3,400 technical and scientific journals and 2,000 conference proceedings. Coverage is from 1969 to the present and the database is updated weekly. Combined with the Inspec Archive, the database includes over 8.8 million records and spans back to 1896.";
				break;
			case 4 : // NTIS
				strText =
					"NTIS&trade; is the premier source for accessing unclassified reports from influential US and international government agencies. The database includes over two million citations from government departments including NASA, U.S. Department of Energy and the U.S. Department of Defense. Coverage is from 1964 to the present and the database is updated weekly.";
				break;
			case (4 + (16384 + 32768)) : // NTIS & (US & EP Patents)
				strText =
					"NTIS&trade; and Patents are the premier source for accessing unclassified reports from influential US and international government agencies. The database includes over two million citations from government departments including NASA, U.S. Department of Energy and the U.S. Department of Defense. Coverage is from 1964 to the present and the database is updated weekly.";
				break;
			case (2 + 4) : // INS & NTIS
				strText =
					"The combined Inspec&#174; & NTIS&trade; databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (2 + 4 + (16384 + 32768)) : // INS & NTIS & (US & EP Patents)
				strText =
					"The combined Inspec&#174; & NTIS&trade;, Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (2 + 4 + 4096) : // INS & NTIS & IBF
				strText =
					"The combined Inspec&#174;, Inspec Archive  & NTIS&trade; databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (2 + 4 + 4096 + (16384 + 32768)) : // INS & NTIS & IBF & (US & EP Patents)
				strText =
					"The combined Inspec&#174;, Inspec Archive  & NTIS&trade;, Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (1 + 2) : // CPX & INS
				strText =
					"The Combined Compendex&#174; & Inspec&#174; database allows for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1969 to present. The database is updated weekly.";
				break;
			case (1 + 2 + (16384 + 32768)) : // CPX & INS & (US & EP Patents)
				strText =
					"The Combined Compendex&#174; & Inspec&#174;, Patents database allows for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1969 to present. The database is updated weekly.";
				break;
			case (1 + 2 + 4096) : // CPX & INS & IBF
				strText =
					"The combined Compendex&#174;, Inspec&#174; and Inspec Archive databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (1 + 2 + 4096 + (16384 + 32768)) : // CPX & INS & IBF & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Inspec&#174;, Patents and Inspec Archive databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (33 + 2) : // C84 & INS
				strText =
					"The combined Compendex&#174;, Ei Backfile & Inspec&#174; databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 2 + (16384 + 32768)) : // C84 & INS & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Ei Backfile & Inspec&#174;, Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 2 + 4096) : // C84 & INS & IBF
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174; and Inspec Archive databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 2 + 4096 + (16384 + 32768)) : // C84 & INS & IBF & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Patents and Inspec Archive databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (1 + 4) : // CPX & NTIS
				strText =
					"The combined Compendex&#174; & NTIS&trade; databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (1 + 4 + (16384 + 32768)) : // CPX & NTIS & (US & EP Patents)
				strText =
					"The combined Compendex&#174; & NTIS&trade;, Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (33 + 4) : // C84 & NTIS
				strText =
					"The combined Compendex&#174;, Ei Backfile & NTIS&trade; databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 4 + (16384 + 32768)) : // C84 & NTIS & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Ei Backfile & NTIS&trade;, Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (1 + 2 + 4) : // CPX & INS & NTIS
				strText =
					"The combined Compendex&#174;, Inspec&#174; & NTIS&trade; databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (1 + 2 + 4 + (16384 + 32768)) : // CPX & INS & NTIS & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Inspec&#174; & NTIS&trade;, Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1964 to present. The databases are updated weekly.";
				break;
			case (1 + 2 + 4 + 4096) : // CPX & INS & NTIS & IBF;
				strText =
					"The combined Compendex&#174;, Inspec&#174;, Inspec Archive & NTIS&trade; databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (1 + 2 + 4 + 4096 + (16384 + 32768)) : // CPX & INS & NTIS & IBF & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Inspec&#174;, Inspec Archive & NTIS&trade; databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 2 + 4) : // C84 & INS & NTIS
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174; & NTIS&trade; databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly";
				break;
			case (33 + 2 + 4 + (16384 + 32768)) : // C84 & INS & NTIS & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174; & NTIS&trade;, Patents databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly";
				break;
			case (33 + 2 + 4 + 4096) : // C84 & INS & NTIS & IBF
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive & NTIS&trade; databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (33 + 2 + 4 + 4096 + (16384 + 32768)) : // C84 & INS & NTIS & IBF & (US & EP Patents)
				strText =
					"The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive & NTIS&trade;, Patents databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and includes patents, journal articles, proceedings, unclassified government reports, and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			/**** GEOBASE ****/
			case 8192 : // GEOBASE
				strText =
					"GEOBASE is a multidisciplinary database supplying bibliographic information and abstracts for the Earth sciences, ecology, geomechanics, human geography, and oceanography. The database covers approximately 2,000 international journals, including both peer-reviewed titles and trade publications. Coverage is from 1973 to present and the database is updated weekly.";
				break;
			case (8192 + 1) : // GEOBASE & CPX
				strText =
					"The Combined Compendex&#174; & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and more. The databases are updated weekly.";
				break;
			case (8192 + 33) : // GEOBASE & CPX & C84
				strText = "The combined Compendex&#174;, Ei Backfile & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines,  earth sciences, ecology, oceanography and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 2) : // GEOBASE & INS
				strText = "The combined Inspec&#174; & GEOBASE databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography and more. The databases are updated weekly.";
				break;
			case (8192 + 2 + (16384 + 32768)) : // GEOBASE & INS & (US & EP Patents)
				strText = "The combined Inspec&#174;, GEOBASE & Patents databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 4096 + (16384 + 32768)) : // GEOBASE & INS & (US & EP Patents)
				strText = "The combined Inspec&#174;, Inspec Archive, GEOBASE & Patents databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 4 + 4096 + (16384 + 32768)) : // GEOBASE & INS & NTIS &(US & EP Patents)
				strText = "The combined Inspec&#174;, Inspec Archive, NTIS&trade;, GEOBASE and Patents databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography, unclassified government reports and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 4 + 4096) : // GEOBASE & INS & IBF& NTIS
				strText = "The combined Inspec&#174;, Inspec Archive, NTIS&trade;, GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more.  The databases are updated weekly.";
				break;
			case (8192 + 2 + 4 ) : // GEOBASE & INS & NTIS
				strText = "The combined Inspec&#174;, NTIS&trade;, GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines and includes journal articles, proceedings, unclassified government reports, and more.  The databases are updated weekly.";
				break;
			case (8192 + 2 + 4 + (16384 + 32768)) : // GEOBASE & INS & NTIS &(US & EP Patents)
				strText = "The combined Inspec&#174;, NTIS&trade;, GEOBASE and Patents databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography, unclassified government reports and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4) : // GEOBASE & NTIS
				strText = "The combined NTIS & GEOBASE databases allows for a multidisciplinary exploration of funded research reports from around the world as well as earth sciences research. National Technical Information Service (NTIS) database is produced by the U.S. Department of Commerce and includes unclassified reports from influential U.S. and international government agencies.  Geobase covers earth sciences, ecology, oceanography and more. The databases are updated weekly.";
				break;
			case (8192 + 2 + 4096) : // GEOBASE & INS & IBF
				strText = "The combined Inspec&#174;, Inspec Archive & GEOBASE databases allow for extensive exploration of topics within electrical engineering, electronics, physics, earth sciences, ecology, oceanography and more. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (8192 + 1 + 4) : // GEOBASE & CPX & NTIS
				strText = "The combined Compendex&#174;, NTIS&trade; & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and unclassified government reports. Coverage is from 1899 to present. The databases are updated weekly.";
				break;
			case (8192 + 1 + 4 + 32) : // GEOBASE & CPX & NTIS & C84
				strText = "The combined Compendex&#174;, EI Backfile, NTIS&trade; & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and unclassified government reports. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1) : // GEOBASE & CPX & INS
				strText = "The Combined Compendex&#174;, Inspec&#174; & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and more. Coverage is from 1969 to present. The databases are updated weekly.";
				break;
			case (8192 + 16384 + 32768) : // GEOBASE & Patents
				strText = "The combined GEOBASE and Patents databases include coverage of US and European patents as well as earth sciences, ecology, oceanography and more. The databases are updated weekly.";
				break;
			case (1 + 8192 + 16384 + 32768) : // CPX & GEOBASE & Patents
				strText = "The Combined Compendex&#174;, GEOBASE and Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (1 + 32 + 8192 + 16384 + 32768) : // CPX & C84 & GEOBASE & Patents
				strText = "The Combined Compendex&#174;, Ei Backfile, GEOBASE and Patents databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and US and European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + (16384 + 32768)) : // GEOBASE & CPX & INS & (US & EP Patents)
				strText =
					"The combined Compendex, Inspec&#174;, Patents databases & Geobase databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and a comprehensive geographical information. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 32) : // GEOBASE & CPX & INS & C84
				strText = "The Combined Compendex&#174;, Ei Backfile, Inspec&#174; & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 4096) : // GEOBASE & CPX & INS & IBF
				strText = "The Combined Compendex&#174;, Inspec&#174;, Inspec Archive & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and more. Coverage is from 1896 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 33 + (16384 + 32768)) : // GEOBASE & CPX & INS & C84 & (US & EP Patents)
				strText =
					"The combined Compendex, Ei Backfile, Inspec&#174;, Patents databases & Geobase databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and a comprehensive geographical information. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 32 + 4096) : // GEOBASE & CPX & INS & C84 & IBF
				strText = "The Combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive & GEOBASE databases allow for searching on a broad range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and more. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 33 + 4096 + (16384 + 32768)) : // GEOBASE & CPX & INS & C84 & IBF & (US & EP Patents)
				strText =
					"The combined Compendex, Ei Backfile, Inspec&#174;, Inspec Archive, Patents databases & Geobase databases allows for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines and a comprehensive geographical information. The databases are updated weekly.";
				break;
			case (8192 + 4 + (16384 + 32768)) : // GEOBASE & NTIS & Patents
				strText = "The combined NTIS&trade;, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within earth sciences, ecology, oceanography, unclassified government reports and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1) : // GEOBASE & CPX & INS & NTIS
				strText = "The combined Compendex&#174;, Inspec&#174;, NTIS&trade; & GEOBASE databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and unclassified government reports. Coverage is from 1899 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + (16384 + 32768)) : // GEOBASE & CPX & INS & NTIS & (US & EP Patents)
				strText = "The combined Compendex&#174;, Inspec&#174;, NTIS&trade;, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography, unclassified government reports and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + 32) : // GEOBASE & CPX & INS & NTIS & C84
				strText = "The combined Compendex&#174;, Ei Backfile, Inspec&#174;, NTIS&trade; & GEOBASE databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and unclassified government reports. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + 32 + (16384 + 32768)) : // GEOBASE & CPX & INS & NTIS & C84 & (US & EP Patents)
				strText = "The combined Compendex&#174;, Ei Backfile, Inspec&#174;, NTIS&trade;, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography, unclassified government reports and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + 4096 + (16384 + 32768)) : // GEOBASE & CPX & INS & NTIS & IBF & (US & EP Patents)
				strText = "The combined Compendex&#174;, Inspec&#174;, Inspec Archive, NTIS&trade;, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography, unclassified government reports and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + 32 + 4096) : // GEOBASE & CPX & INS & NTIS & C84 & IBF
				strText = "The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive, NTIS&trade; & GEOBASE databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and unclassified government reports. Coverage is from 1884 to present. The databases are updated weekly.";
				break;
			case (8192 + 2 + 1 + 32 + 4096 + (16384 + 32768)) : // GEOBASE & CPX & INS & C84 & IBF
				strText = "The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
			case (8192 + 4 + 2 + 1 + 32 + 4096 + (16384 + 32768)) : // GEOBASE & CPX & INS & NTIS & C84 & IBF
				strText = "The combined Compendex&#174;, Ei Backfile, Inspec&#174;, Inspec Archive, NTIS&trade;, GEOBASE and Patents databases allow for searching on the broadest possible range of topics within the scientific, applied science, technical and engineering disciplines, earth sciences, ecology, oceanography, unclassified government reports and US & European patents. Coverage is from 1790 to present. The databases are updated weekly.";
				break;
		}
/*
		if(referex)
		{
			strText = strText+ " <p>Referex Engineering is a specialized electronic reference product that draws upon hundreds of premium engineering titles to provide engineering students and professionals with the answers and information they require at school, work, and in practice.";
		}
*/


		return strText;
	}

	public static String getDisplayName(int mask) {

		StringBuffer buf = new StringBuffer();
		try {
			Map h = DriverConfig.getDriverTable();
			DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
			Database[] databases = dconfig.getDatabases(mask);
			for (int i = 0; i < databases.length; i++) {
				if (i > 0) {
					if ((databases.length - i) == 1) {
						buf.append(" & ");
					} else {
						buf.append(", ");
					}
				}

				buf.append(databases[i].getName());
			}

		} catch (Exception e) {
			e.printStackTrace();
			//Yes, I meant to sit on this. - Joel
		}
		System.out.println("database= "+buf.toString());
		return buf.toString();
	}

	public static String getIndexName(int mask) {

		StringBuffer buf = new StringBuffer();
		try {
			Map h = DriverConfig.getDriverTable();
			DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
			Database[] databases = dconfig.getDatabases(mask);
			if (databases != null) {
				int i = 0;
				if (databases[i] != null) {
					buf = new StringBuffer(databases[i].getIndexName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Yes, I meant to sit on this. - Joel
		}

		return buf.toString();
	}

	public static String getDisplayInits(int mask) {
		return getSearchCodesLegend(mask, false);
	}

	public static String getSearchCodesLegend(int mask) {
		return getSearchCodesLegend(mask, true);
	}

	private static String getSearchCodesLegend(int mask, boolean legend) {
		if ((legend == true) && DatabaseDisplayHelper.singleDatabase(mask)) {
			return StringUtil.EMPTY_STRING;
		} else {
			StringBuffer buf = new StringBuffer();
			try {
				Map h = DriverConfig.getDriverTable();
				DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
				Database[] databases = dconfig.getDatabases(mask);

				for (int i = 0; i < databases.length; i++) {
					if (legend) {
						buf.append("<span class=\"BoldBlueText\">");
					}
					if ((!legend) && (i != 0)) {
						buf.append(", ");
					}
					//buf.append((databases[i].getName()).substring(0, 1));
					buf.append((databases[i].getID()).substring(0, 1));
					if (legend) {
						buf.append("</span>");
						buf.append(" ");
						buf.append(databases[i].getName());
						buf.append(" ");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//Yes, I meant to sit on this. - Joel
			}
			return buf.toString();
		}
	}

	// only works with scrubbed mask
	public static boolean singleDatabase(int mask)
	{
		if ((mask == 1) ||
		        (mask == 2) ||
		        (mask == 4) ||
		        (mask == 128) ||
		        (mask == 8192) || // geobase
		        (mask == 16384) ||
		        (mask == 32768))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public static String isDeDupable(int mask) {
		return String.valueOf(((mask == 3) || (mask == 7)));
	}

}
