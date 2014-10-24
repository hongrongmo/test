package org.ei.fulldoc;


import java.sql.ResultSet;

public class CompendexDOIFinder
    extends DOIFinder
{
     public static void main(String args[])
        throws Exception
     {
            String user = args[0];
            String password = args[1];
            String driver = args[2];
            String url = args[3];
            String database = args[4];
            String rtype = args[5];
            String load = args[6];
            String skipID = null;
            if(args.length == 8)
            {
                System.out.println("Setting skip ID:"+args[6]);
                skipID = args[7];

            }

            DOIFinder d = new CompendexDOIFinder(user,
                                                 password,
                                                 driver,
                                                 url,
                                                 database,
                                                 rtype,
                                                 load,
                                                 skipID);
            d.findDOIs();
     }


    public CompendexDOIFinder(String user,
             String password,
             String driver,
             String url,
             String database,
             String rtype,
             String load,
             String skipID)
    {
        super(user,
              password,
              driver,
              url,
              database,
              rtype,
              load,
              skipID);
    }


    protected String getSQLHook(String load)
    {
        String loadParam = null;

        if(load.length() > 4)
        {
            loadParam = "load_number="+load;
        }
        else
        {
            loadParam = "yr='"+load+"'";
        }

        return "select m_id, aus, yr, do, cn, vo, iss, xp from cpx_master where "+ loadParam;
    }



    protected OHUBID[] getOHUBIDHook(ResultSet rs,
                                     String rtype)
        throws Exception
    {
        OHUBID[] ids = null;


        if(rtype.equals("ivip"))
        {
            String issn = getIssn(rs.getString("cn"));
            String issue = getFirstIssue(rs.getString("iss"));
            String volume = getFirstVolume(rs.getString("vo"));
            String pages = getFirstPage(rs.getString("xp"));

            if((issn == null || issn.length() == 0) ||
               (volume == null || volume.length() == 0) ||
               (pages == null || pages.length() == 0))
            {
                return null;
            }


            if(issue != null)
            {
                ids = new OHUBID[2];
            }
            else
            {
                ids = new OHUBID[1];
            }

            IssueVolumeID isid0 = new IssueVolumeID();
            isid0.setISSN(issn);
            isid0.setFirstVolume(volume);
            isid0.setFirstPage(pages);
            ids[0] = isid0;

            if(issue != null)
            {
                IssueVolumeID isid1 = new IssueVolumeID();
                isid1.setISSN(issn);
                isid1.setFirstIssue(issue);
                isid1.setFirstVolume(volume);
                isid1.setFirstPage(pages);
                ids[1] = isid1;
            }
        }
        else
        {
            String aus = getFirstAuthorSurname(rs.getString("aus"));
            String initial = getFirstAuthorInitial(rs.getString("aus"));
            String yr = rs.getString("yr");
            String firstPage = getFirstPage(rs.getString("xp"));
            String lastPage = getLastPage(rs.getString("xp"));

           if((aus == null || aus.length() == 0) ||
              (yr == null || yr.length() == 0) ||
              (firstPage == null || firstPage.length() == 0) ||
              (lastPage == null ||  lastPage.length() == 0) ||
              (initial == null || initial.length() == 0))
           {
               return null;
           }



            ids = new OHUBID[1];

            AuthorRefID ref2 = new AuthorRefID();
            ref2.setFirstAuthorSurname(aus);
            ref2.setYear(yr);
            ref2.setFirstPage(firstPage);
            ref2.setLastPage(lastPage);
            ref2.setFirstAuthorInitial(initial);
            ids[0] = ref2;

        }

        return ids;
    }




    protected String  getUpdateHook(String id, String doi)
    {
        return "UPDATE cpx_master SET do = '"+doi+"' WHERE m_id = '"+id+"';";
    }

}