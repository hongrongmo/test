package org.ei.dataloading.paper.loadtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.AuthorStream;

public class PaperChemRecordFixer {

    private Perl5Util perl = new Perl5Util();

    private int AU_MAXSIZE = 750;
    private int AF_MAXSIZE = 768;
    private int CL_MAXSIZE = 99;
    private int BN_MAXSIZE = 15;
    private int SU_MAXSIZE = 20;
    private int AC_MAXSIZE = 48;
    private int BR_MAXSIZE = 10;
    private int NR_MAXSIZE = 20;
    private int TG_MAXSIZE = 2;
    private int CN_MAXSIZE = 6;
    private int DT_MAXSIZE = 2;
    private int EX_MAXSIZE = 11;
    private int AT_MAXSIZE = 40;
    private int IG_MAXSIZE = 9;
    private int M1_MAXSIZE = 32;
    private int M2_MAXSIZE = 32;
    private int MC_MAXSIZE = 48;
    private int TR_MAXSIZE = 16;
    private int VO_MAXSIZE = 32;
    private int LA_MAXSIZE = 32;
    private int PN_MAXSIZE = 88;
    private int PC_MAXSIZE = 48;
    private int PA_MAXSIZE = 32;
    private int ST_MAXSIZE = 192;
    private int ISS_MAXSIZE = 32;
    private int EF_MAXSIZE = 80;
    private int VC_MAXSIZE = 48;
    private int XP_MAXSIZE = 80;
    private int YR_MAXSIZE = 10;
    private int SN_MAXSIZE = 9;
    private int VX_MAXSIZE = 64;
    private int ML_MAXSIZE = 64;

    private String currentID;

    public String fixRecord(String record) throws IOException {
        StringBuffer buf = new StringBuffer();
        ArrayList<String> al = new ArrayList<String>();
        perl.split(al, "/\t/", record.trim());
        for (int i = 0; i < al.size(); ++i) {
            if (i > 0) {
                buf.append("    ");
            }

            String fs = (String) al.get(i);
            if (fs != null) {
                buf.append(fixField(i, fs));
            }
        }

        return buf.toString();
    }

    private String fixField(int index, String data) throws IOException {

        String fieldName = PaperChemBaseTableRecord.baseTableFields[index];

        if (data == null && !fieldName.equals("AB")) {
            return "";
        }

        if (fieldName.equals("M_ID")) {
            currentID = data;
        }
        if (fieldName.equals("AB")) {
            if (data == null || data.length() < 1) {
                return "NAN" + currentID;
            }
        } else if (fieldName.equals("EX") || fieldName.equals("AN")) {

            if (data.length() > EX_MAXSIZE) {
                data = getFirst(data, EX_MAXSIZE);
            }
        } else if (fieldName.equals("AU")) {
            if (data.length() > AU_MAXSIZE) {
                data = fixAuthors(data);
            }

        } else if (fieldName.equals("CL") || fieldName.equals("CLS")) {
            if (data.length() > CL_MAXSIZE) {
                data = fixDelimitedFieldSize(data, CL_MAXSIZE);
            }
        } else if (fieldName.equals("PN")) {
            if (data.length() > PN_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("ST")) {
            if (data.length() > ST_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("ML")) {
            if (data.length() > ML_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("VX")) {
            if (data.length() > VX_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("SN")) {
            if (data.length() > SN_MAXSIZE) {
                data = "QQ";
            }

        }

        else if (fieldName.equals("YR")) {
            if (data.length() > YR_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("XP")) {
            if (data.length() > XP_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("PA")) {
            if (data.length() > PA_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("PC")) {
            if (data.length() > PC_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("VC")) {
            if (data.length() > VC_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("ISS")) {
            if (data.length() > ISS_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("BN")) {
            if (data.length() > BN_MAXSIZE) {
                data = getFirst(data, BN_MAXSIZE);
            }

        } else if (fieldName.equals("MC")) {
            if (data.length() > MC_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("EF")) {
            if (data.length() > EF_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("LA")) {
            if (data.length() > LA_MAXSIZE) {
                data = "QQ";
            }

        } else if (fieldName.equals("VO")) {
            if (data.length() > VO_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("M1")) {
            if (data.length() > M1_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("M2")) {
            if (data.length() > M2_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("IG")) {
            if (data.length() > IG_MAXSIZE) {
                data = getFirst(data, IG_MAXSIZE);
            }
        } else if (fieldName.equals("TR")) {
            if (data.length() > TR_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("AT")) {
            if (data.length() > AT_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("SU")) {
            if (data.length() > SU_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("AC")) {
            if (data.length() > AC_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("AF")) {
            if (data.length() > AF_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("BR")) {
            if (data.length() > BR_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("NR")) {
            if (data.length() > NR_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("TG")) {
            if (data.length() > TG_MAXSIZE) {
                data = "QQ";
            }
        } else if (fieldName.equals("CN")) {
            if (data.length() > CN_MAXSIZE) {
                data = getFirst(data, CN_MAXSIZE);
            }
        } else if (fieldName.equals("DT")) {
            if (data.length() > DT_MAXSIZE) {
                data = getFirst(data, DT_MAXSIZE);
            }
        }

        return data;
    }

    private String getFirst(String data, int maxSize) {
        String first = "";
        StringTokenizer t = new StringTokenizer(data, ";");
        if (t.hasMoreTokens()) {
            first = t.nextToken();
            if (first.length() > maxSize) {
                first = "QQ";
            }

        }

        return first;
    }

    private String fixDelimitedFieldSize(String data, int maxSize) {

        StringBuffer buf = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(data, ";");
        while (tokens.hasMoreTokens()) {
            String t = tokens.nextToken();
            if ((buf.length() + t.length()) < maxSize) {
                if (buf.length() > 0) {
                    buf.append(";");
                }

                buf.append(t);
            } else {
                break;
            }
        }

        return buf.toString();

    }

    private String fixAuthors(String authors) throws IOException {

        StringBuffer buf = new StringBuffer();
        AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(authors.getBytes()));
        String author = "";
        while ((author = aStream.readAuthor()) != null) {
            if ((buf.length() + author.length()) < AU_MAXSIZE) {
                if (buf.length() > 0) {
                    buf.append(";");
                }

                buf.append(author);
            } else {
                break;
            }
        }

        return buf.toString();
    }
}
