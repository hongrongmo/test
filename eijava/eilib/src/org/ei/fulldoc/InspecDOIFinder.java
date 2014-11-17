package org.ei.fulldoc;


import java.sql.ResultSet;

import org.apache.oro.text.perl.Perl5Util;

public class InspecDOIFinder
    extends DOIFinder
{

    private Perl5Util perl = new Perl5Util();


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




            DOIFinder d = new InspecDOIFinder(user,
                                             password,
                                             driver,
                                             url,
                                             database,
                                             rtype,
                                             load,
                                             skipID);
            d.findDOIs();
     }


    public InspecDOIFinder(String user,
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
            loadParam = "substr(pdate,length(pdate)-3) ='"+load+"'";
        }

        return "select m_id, sn, doi do, pdate, voliss, ipn, npl1, npl2 from ins_master where sn is not null and "+ loadParam;

    }

    protected OHUBID[] getOHUBIDHook(ResultSet rs,
                                    String rtype)
        throws Exception
    {
        IssueVolumeID[] ids = null;
        String issn = getIssn(rs.getString("sn"));
        String issue = null;
        String volume = null;
        String pages = null;



        if(rs.getString("VOLISS") != null)
        {

            String volumeIssue = rs.getString("VOLISS");
            if(perl.match("/,/", volumeIssue))
            {
                volume = perl.preMatch();
                issue = perl.postMatch();
            }
            else
            {
                if(perl.match("/vol/i",volumeIssue))
                {
                    volume = volumeIssue;
                }
                else if(perl.match("/no/i",volumeIssue))
                {
                    issue = volumeIssue;
                }
            }
        }


        if(rs.getString("IPN") != null)
        {
              pages = rs.getString("IPN");
        }
        else if (rs.getString("NPL1") != null)
        {
              pages = rs.getString("NPL1");
        }
        else if (rs.getString("NPL2") != null)
        {
              pages = rs.getString("NPL2");
        }

        pages = getFirstPage(pages);
        volume = getFirstVolume(volume);
        issue = getFirstIssue(issue);

        //System.out.println(issn+":"+volume+":"+issue+":"+pages);


        if((issn == null || issn.length() == 0) ||
           (volume == null || volume.length() == 0) ||
           (pages == null || pages.length() == 0))
        {
            return null;
        }



        if(issue != null)
        {
            ids = new IssueVolumeID[2];
        }
        else
        {
            ids = new IssueVolumeID[1];
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

        return ids;
    }

    protected String  getUpdateHook(String id, String doi)
    {
        return "UPDATE ins_master SET doi = '"+doi+"' WHERE m_id = '"+id+"';";
    }

}