package org.ei.dataloading.upt.loadtime;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 *  This class parses the non patent refs.
 *  @author Elsevier
 *  @version 0.1
 */
public class NonPatRef {
    private String ref;
    private String firstAuthor;
    private String year;
    private String volume;
    private String issue;
    private String startPage;
    private String title;
    private String day;
    private String month;
    private String issn;
    private String isbn;
    private boolean keys = false;

     Matcher mRefs = Pattern.compile("^(.*)" +
        "(?:&amp;#x201c;|&ldquo;)(.*)(?:&amp;#x201d;|&rdquo;)(.*)$")
        .matcher("");

    public NonPatRef(String ref){
         this.ref=ref;
         this.setRef(ref);
     }

    public String getTitle(){return title;}
    public String getFirstAuthor(){return firstAuthor;}
    public String getYear(){return year;}
    public String getVolume(){return volume;}
    public String getIssue(){return issue;}
    public String getStartPage(){return startPage;}
    public boolean hasKeys(){return keys;}
    public String getRef(){return ref;}
    public String getISSN(){return issn;}
    public String getISBN(){return isbn;}

    public boolean hasIVIP()
    {
        return ((issn!=null || isbn!=null) && volume!=null && startPage!=null);
    }
    public boolean hasTitleKey()
    {
        return (title !=null && year !=null && firstAuthor !=null);
    }
     private boolean setTitle(String text) {


    this.title = text.trim();

        return true;

}

    private boolean setYear(String text)
    {
        String year = "";
    Pattern p = null;
    Matcher m = null;
    try {

         //String regularExpression = "\\(((19|20)\\d\\d)\\w?\\)";
         String regularExpression = "((19\\d|200)\\d)\\w?";
         p = Pattern.compile(regularExpression);
         m = p.matcher(text);
         if (m.find())
         {
            this.year = m.group(1).trim();
         }
         if (this.year == null) //Search the full range
            this.year="[1884;2006]";

    }catch (PatternSyntaxException e) {
        System.err.println("Bad pattern.");
        System.err.println(e.getMessage());
    }

    return true;
}

private boolean setDate(String text)
{
    String year = "";
    Pattern p = null;
    Matcher m = null;
    try {

         //String regularExpression = "\\(((19|20)\\d\\d)\\w?\\)";
         String day = "[^\\w\\/]([0-3][0-9]?[a-z]*?)?(\\s*\\-\\s*[0-3][0-9]?[a-z]*?)?\\s?";
         String month ="(Jan[\\.\\s]|January\\b|Feb[\\.\\s]|February\\b|Mar[\\.\\s]|March\\b|Apr[\\.\\s]|April\\b|May|Jun[\\.\\s]|June\\b|Jul[\\.\\s]|July\\b|Aug[\\.\\s]|August\\b|Sep[\\.\\s]|September\\b|Oct[\\.\\s]|October\\b|Nov[\\.\\s]|November\\b|Dec[\\.\\s]|December\\b)";
         p = Pattern.compile(day+month);
         m = p.matcher(text);
         if (m.find())
         {
            this.day = m.group(1);
            this.month=m.group(3);
         }


    }catch (PatternSyntaxException e) {
        System.err.println("Bad pattern.");
        System.err.println(e.getMessage());
    }

    return true;
}

private boolean setAuthor(String text) {
    text = text.replaceAll("&amp;#x\\w+;", " ");
    StringBuffer author = new StringBuffer();
    Matcher m1 = null;
    Matcher m2 = null;
    Matcher m3 = null;
    Matcher m4 = null;
    try {



         //s/[,; ]+et\.?\s+al\.?([,; ]+|$)/,et al,/i;
         final String reName = "([A-Z][a-z]+|[A-Z][a-z]+-[A-Za-z]+)";
         final String reInit = "(?:-?[A-Z]\\.?\\s?)";
         final String reVon = "^\\s*(((van|von|de|den|der)\\s+)+)(\\S\\S+)\\s+(.+))";
         final String reSuffix = "(Jr|Sr|Snr|II|III)";
         //String regularExpression = "(\\w*,\\s([A-Z][\\.,]*\\s?)*)+et\\.?\\s+al\\.?";
         Pattern auLastInit = Pattern.compile(reName + "," + "\\s?(" + reInit + "{0,3})?(?:[ ,]*et\\.?\\s+al\\.?)?");
         Pattern auInitLast = Pattern.compile("(" + reInit + "{1,3})\\s" + reName + "(?:[ ,]*et\\.?\\s+al\\.?)?");
         Pattern auFirstInitLast = Pattern.compile(reName + "\\s?(?:" + reInit + "{0,2})?" + reName + "(?:[ ,]*et\\.?\\s+al\\.?)?");
         Pattern auLast = Pattern.compile(reName + "\\s(?:[ ,]*et\\.?\\s+al\\.?)");
         //System.out.println(authorLast);
         //p = Pattern.compile(authorLast);
         m1 = auLastInit.matcher(text);
         m2 = auInitLast.matcher(text);
         m3 = auFirstInitLast.matcher(text);
         m4 = auLast.matcher(text);
         if (m1.find()) {
            // System.out.println(m1.groupCount());
             for (int i = 0; i <= m1.groupCount(); i++) {

                 author.append(i).append(") ").append(m1.group(i)).append("|");
             }
             //System.out.println(author.toString());
             firstAuthor = m1.group(1).trim();
             return true;
         } else if (m2.find()) {
             //System.out.println(m2.groupCount());
             for (int i = 0; i <= m2.groupCount(); i++) {
                 author.append(i).append(") ").append(m2.group(i)).append("|");
             }
             //System.out.println(author.toString());
             firstAuthor = m2.group(2).trim();
             return true;
         } else if (m3.find()) {
             //System.out.println(m3.groupCount());
             for (int i = 0; i <= m3.groupCount(); i++) {
                 author.append(i).append(") ").append(m3.group(i)).append("|");
             }
             //System.out.println(author.toString());
             firstAuthor = m3.group(2).trim();
             return true;
         } else if (m4.find()) {
             //System.out.println(m3.groupCount());
             for (int i = 0; i <= m4.groupCount(); i++) {
                 author.append(i).append(") ").append(m4.group(i)).append("|");
             }
             //System.out.println(author.toString());
             firstAuthor = m4.group(1).trim();
             return true;
         }
           //System.out.println(text);


    } catch (PatternSyntaxException e) {
        System.err.println("Bad pattern.");
        System.err.println(e.getMessage());
    }
    return false;
}



private void setRef(String text)
{
    text=text.replaceAll("\\s+\\(","("); // ( 1998) ==> (1998)
    text=text.replaceAll("\\s+\\)",")"); // (1998 ) ==> (1998)
    Matcher mRefs = Pattern.compile("^(.*)(?:&amp;#x201c;|&ldquo;|\")(.*)(?:&amp;#x201d;|&rdquo;|\")(.*)$").matcher("");
    mRefs.reset(text);
    //System.out.println(text);
    if(mRefs.find())
    {
          setTitle(mRefs.group(2));
          setAuthor(mRefs.group(1));
          setVolumeIssue(mRefs.group(3));
          setPage(mRefs.group(3));
          setYear(mRefs.group(3));
          setDate(text);
          setISSN(text);
          setISBN(text);
          this.keys=true;

     }

     else if(setISSN(text)||setISBN(text))
     {
        setAuthor(text);
        setYear(text);
        setVolumeIssue(text);
        setPage(text);
        this.keys = true;
    }



}

private boolean setVolumeIssue(String text) {
    Pattern p = Pattern.compile("[,;. ]\\s*(?:volume|vol|v)\\.?\\s*(\\d+)\\s*(?:[ ,;]\\s*(?:n|no|issue|iss|#)\\.?\\s*(\\d+)\\b)?",Pattern.CASE_INSENSITIVE) ;
    Matcher m = p.matcher(text);
    if (m.find()) {


         this.volume = m.group(1);
         this.issue = m.group(2);
         return true;

    } else {
         p = Pattern.compile("(?:volume|vol|v)?\\.?[,;. ]\\s*(?:(\\d+)\\((\\d+)\\)(?:\\:([a-z]*\\d+[a-z]*))?)");
         m = p.matcher(text);
        if (m.find()) {
             this.volume = m.group(1);
             this.issue = m.group(2);
             this.startPage = m.group(3);
             return true;
        } else {
             p = Pattern.compile("(?:volume|vol|v)?\\.?[,;. ]\\s*(\\d+)(?:\\:([a-z]*\\d+[a-z]*))");
             m = p.matcher(text);
            if (m.find()) {


                 this.volume = m.group(1);
                 this.startPage = m.group(2);
                 return true;

            }
        }
    }
    return false;
}

private boolean setPage(String text)
{
    Pattern p = Pattern.compile("[,;:. ]\\s*(?:pages|page|pp|p)\\s*[.# ]\\s*([a-z]*\\d+[a-z]*)\\b",Pattern.CASE_INSENSITIVE) ;
    Matcher m = p.matcher(text);
    if (m.find()) {

        if(m.group(1) != null) {
            this.startPage = m.group(1);
            return true;
        }
    }
    return false;
}

private boolean setISSN(String text)
{

    Pattern p = Pattern.compile("\\b(?:issn|sn)?[ -:]*(\\d{4}\\-\\d{3}[\\dX])\\b",Pattern.CASE_INSENSITIVE) ;
    Matcher m = p.matcher(text);
    if (m.find()) {

        if (m.group(1) != null) {
            if (isISSN(m.group(1))) {
                this.issn = m.group(1);
                return true;
            }
        }
    }
    return false;
}

private boolean setISBN(String text) {
    Pattern p = Pattern.compile("(?:isbn|bn)[-:#\\s]*(\\d[\\d\\-\\s]{9}\\d\\-?[\\dX])\\b",Pattern.CASE_INSENSITIVE) ;
    Matcher m = p.matcher(text);
    if (m.find()) {

        if (m.group(1) != null) {
            if (isISBN(m.group(1)))
            {
                this.isbn = m.group(1);
                return true;
            }
        }
    }
    return false;
}

private boolean isISSN(String text)
{
    int sum = 0;
    final int ISSN_LENGTH = 8;
    text = text.replaceAll("\\-", "");

    if (text.length() == ISSN_LENGTH) {
        for (int i = 0; i < ISSN_LENGTH-1; i++) {
            if (Character.isDigit(text.charAt(i))) {
                sum = sum + (Character.getNumericValue(text.charAt(i))*(8-i));
            } else
                return false;
        }
        int checkDigit= 11 - sum % 11;

        if (checkDigit == 11)
            checkDigit=0;
        if(checkDigit==Character.getNumericValue(text.charAt(7))) {
            return true;

        }else if (checkDigit == 10 && text.toUpperCase().charAt(7) == 'X' ) {
            return true;
        }
    }
    return false;
}

private boolean isISBN(String text) {
    int sum = 0;
    final  int isbnLength = 10;
    final  int modDigit = 11;
    text = text.replaceAll("[- ]", "");
    System.out.println(text);
    if (text.length() == isbnLength) {
        for (int i = 0; i < isbnLength - 1; i++) {
            if (Character.isDigit(text.charAt(i))) {
                sum = sum
                    + (Character.getNumericValue(text.charAt(i))
                            * (isbnLength - i));
            } else {
                return false;
            }
        }
        int checkDigit = modDigit - sum % modDigit;

        if (checkDigit == modDigit) {
            checkDigit = 0;
        }
        if (checkDigit
                == Character.getNumericValue(text.charAt(isbnLength - 1))) {
            return true;
        } else if (checkDigit == isbnLength
                && text.toUpperCase().charAt(isbnLength - 1) == 'X') {
            return true;
        }
    }
    return false;
}

public String toString() {
    return "au=" + firstAuthor
            + "|ti=" + title
            + "|yr=" + year
            + "|vo=" + volume
            + "|is=" + issue
            + "|sp=" + startPage
            + "|mo=" + month
            + "|da=" + day
            + "|sn=" + issn
            + "|bn=" + isbn;
}

public static void main(String[] args)throws Exception {
     BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
     String line = null;

     while ((line = in.readLine()) != null) {

         NonPatRef r = new NonPatRef(line);
         //if(r.hasIVIP() || r.hasTitleKey())
            System.out.println(r);
            System.out.println(line);
         //line=line.replaceAll("(&ldquo;|&rdquo)","\"");


         System.out.println("------------------------------------------\n");
        // myDeciter.dodecite (line, "", "",new PrintWriter(System.out,true));
           // necessary to get output for this one
         }

     }


}


