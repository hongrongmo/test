<%
    String database=null;

    Map dataBaseNames = new Hashtable();


    // - - - -BIBLIOGRAPHIC DATABASES- - - -
    dataBaseNames.put("CPX", "Compendex");
    dataBaseNames.put("INS", "Inspec");
    dataBaseNames.put("NTI", "NTIS");
    dataBaseNames.put("GEO", "GEO");

    // - - - -eBOOK- - - -
    dataBaseNames.put("PAG", "Referex|Referex");

    dataBaseNames.put("SPI", "SPIEWeb");
    dataBaseNames.put("OJP", "OJPS");

    // - - - -HANDBOOKS- - - -
    dataBaseNames.put("CRC", "16|CRC ENGnetBASE");

    // - - - -PATENTS- - - -
    dataBaseNames.put("ESN", "Esp@cenet|Esp@cenet");
    dataBaseNames.put("UPO", "8|USPTO");

    // - - - -SPECIFICATIONS- - - -
    dataBaseNames.put("GSP", "GSP|GlobalSpec");

    // - - - -STANDARDS- - - -
    //dataBaseNames.put("CSS", "Techstreet Standards|Techstreet Standards");
    dataBaseNames.put("IHS", "IHS|IHS Standards");

    // - - - -WEB SEARCH- - - -
    dataBaseNames.put("SCI", "Scirus|Scirus");
    dataBaseNames.put("EEV", "EEVL|Intute");
    // EMSAT cartridge is back in use but it point to KellySearch (email from Rafael 1/31/2005)
    // jam - it now points to ReedLink (email from Rafael 6/09/2005)
    dataBaseNames.put("EMS", "ReedLink|ReedLink");

    // - - - -LEXISNEXIS NEWS- - - -
    dataBaseNames.put("LEX", "LexisNexis|LexisNexis News");

    // - - - -PAPER VILLAGE- - - -
    dataBaseNames.put("PCH", "PaperChem");

    // - - - -Chemica VILLAGE- - - -
    dataBaseNames.put("CHM", "Chimica");

    // - - - - CBNB - - - -//
    dataBaseNames.put("CBN", "CBNB");

    // - - - - Encompass - - - - //
    dataBaseNames.put("ELT", "EnCompassLit");
    dataBaseNames.put("EPT", "EnCompassPat");
    if (request.getParameter("alldb") !=null )
    {
        database = request.getParameter("alldb");
    }
    else if(request.getParameterValues("database") != null)
    {
        int sumDb = 0;
        String[] dbs = request.getParameterValues("database");
        for (int i=0; i<dbs.length; i++)
        {
          if("Compendex".equalsIgnoreCase(dbs[i]))
          {
            sumDb += 1;
          }
          else if("Inspec".equalsIgnoreCase(dbs[i]))
          {
            sumDb += 2;
          }
          else if("Com".equalsIgnoreCase(dbs[i]) || "Combined".equalsIgnoreCase(dbs[i]))
          {
            sumDb += 3;
          }
          else
          {
            try
            {
              sumDb += Integer.parseInt(dbs[i]);
            }
            catch(NumberFormatException e)
            {
            log("Non parseable db value " + dbs[i]);
            }
          }

        }
        database = String.valueOf(sumDb);
    }
    else
    {
    	if(dbName!=null)
		{
			database = dbName;
		}
		else
		{
        	database="1";
		}
    }
%>
<SELECTED-DATABASE><%=database%></SELECTED-DATABASE>
<DATABASES>
<%


    String[] cars = user.getCartridge();
    //System.out.println("Cartridge= "+user.getCartridgeString());

    List lstCarts = Arrays.asList(cars);

    int userMask = (DatabaseConfig.getInstance()).getMask(cars);

    if(lstCarts.size() > 0)
    {

        // - - - The order here will affect the order in the "More Search Sources" box on the site!
        // Local Databases come first

        // - - - -BIBLIOGRAPHIC DATABASES- - - -
      	int[] dbMasks = {org.ei.domain.DatabaseConfig.CPX_MASK,
      	                  org.ei.domain.DatabaseConfig.INS_MASK,
      	                  org.ei.domain.DatabaseConfig.NTI_MASK,
      	                  org.ei.domain.DatabaseConfig.UPA_MASK,
      	                  org.ei.domain.DatabaseConfig.EUP_MASK,
      	                  org.ei.domain.DatabaseConfig.GEO_MASK,
                          org.ei.domain.DatabaseConfig.GRF_MASK,
                          org.ei.domain.DatabaseConfig.CBF_MASK,
                          org.ei.domain.DatabaseConfig.EPT_MASK,
                          org.ei.domain.DatabaseConfig.ELT_MASK,
                          org.ei.domain.DatabaseConfig.IBS_MASK};

        for(int x=0; x < dbMasks.length ; x++)
        {
            if((userMask & dbMasks[x]) == dbMasks[x])
            {
                Database[] d = (DatabaseConfig.getInstance()).getDatabases(dbMasks[x]);
                int sum = 0;
                String name = StringUtil.EMPTY_STRING;

                for(int y = 0; y < d.length; y++)
                {

                    if(d[y] != null)
                    {

                        sum += d[y].getMask();
                        if(!StringUtil.EMPTY_STRING.equals(name))
                        {
                            name = name.concat(" &amp; ");
                        }
                        name = name.concat(d[y].getName());
                    }
                } // for

                out.print("<DATABASE VALUE=\"");
                out.print(sum);
                out.print("\" NAME=\"");
                out.print(name);
                out.print("\"/>");
            } // if
        } // for

    // The order of these "More Search Sources" is determined here as they
    // are added to the output buffer.
    // The separate loops are no longer really necessary - but the logic to test the
    // user's cartridges (or lack thereof) is specific to certain sources (i.e. Books)

    /*
        jam - 06/09/2005 here is the new positioning. thanks

        Referex
        CRC ENGnetBASE
        ReedLink
        GlobalSpec
        IHS Standards
        USPTO
        esp@cenet
        Scirus
        EEVL
        LexisNexis News
    */

    // - - - -BOOK SEARCH- - - -
        String[] books = {"PAG"};
        for(int listsize=0; listsize < books.length; listsize++)
        {
            if(((lstCarts.contains(books[listsize]))) && dataBaseNames.get(books[listsize]) != null)
            {
                String[] dbNameValues = ((String) dataBaseNames.get(books[listsize])).split("\\|");
                out.print("<DATABASE VALUE=\"");
                out.print(dbNameValues[0]);
                out.print("\" NAME=\"");
                out.print(dbNameValues[1]);
                out.print("\"/>");
           }
        }

        String[] moresources = {"CRC","EMS","GSP"};
        for(int listsize=0; listsize < moresources.length; listsize++)
        {
            if(lstCarts.contains(moresources[listsize]) && dataBaseNames.get(moresources[listsize])!= null)
            {
                String[] dbNameValues = ((String) dataBaseNames.get(moresources[listsize])).split("\\|");
                out.print("<DATABASE VALUE=\"");
                out.print(dbNameValues[0]);
                out.print("\" NAME=\"");
                out.print(dbNameValues[1]);
                out.print("\"/>");
            }


        }

    // - - - -STANDARDS- - - -
        String[] standards = {"IHS"};
        for(int listsize=0; listsize < standards.length; listsize++)
        {
            if(dataBaseNames.get(standards[listsize]) != null)
            {
              String[] dbNameValues = ((String) dataBaseNames.get(standards[listsize])).split("\\|");
              out.print("<DATABASE VALUE=\"");
              out.print(dbNameValues[0]);
              out.print("\" NAME=\"");
              out.print(dbNameValues[1]);
              out.print("\"/>");

            }
        }

    // - - - -PATENTS- - - -
        moresources = new String[]{"UPO","ESN","SCI","EEV"};
        for(int listsize=0; listsize < moresources.length; listsize++)
        {
            if((lstCarts.contains(moresources[listsize])) && dataBaseNames.get(moresources[listsize]) != null)
            {
                String[] dbNameValues = ((String) dataBaseNames.get(moresources[listsize])).split("\\|");
                out.print("<DATABASE VALUE=\"");
                out.print(dbNameValues[0]);
                out.print("\" NAME=\"");
                out.print(dbNameValues[1]);
                out.print("\"/>");

            }
        }


    // - - - -LEXISNEXIS NEWS- - - -
        /* 08/12/04 ZY
           Right now it is always hardcoded for all users
           Later on, if it is added in the user cartridge,
           it can be retrieved by following code
         */
        /* 10/11/2005 JAM - LEX USer Cartridge
          LEX is a 'negative' cartridge.  So if the
          does NOT have the cartridge, include LexisNexis News
          in the output.
          */
        String[] news = {"LEX"};
        int newslistsize = 0;
        if((!lstCarts.contains(news[newslistsize])) && dataBaseNames.get(news[newslistsize]) != null)
        {
          String[] dbNameValues = ((String) dataBaseNames.get(news[newslistsize])).split("\\|");
          out.print("<DATABASE VALUE=\"");
          out.print(dbNameValues[0]);
          out.print("\" NAME=\"");
          out.print(dbNameValues[1]);
          out.print("\"/>");
        }
    } // if size > 0
    // else USED to give all databases if cartidge size <= 0
    // this is no longer done - user will get empty dropdown
    // if the have no cartridges!
%>
</DATABASES>
<USERMASK><%=userMask%></USERMASK>