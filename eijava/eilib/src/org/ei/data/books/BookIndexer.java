package org.ei.data.books;

import java.io.*;
import java.util.*;
import org.ei.query.base.PorterStemmer;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.*;
import java.sql.*;

public class BookIndexer
{

	private static PorterStemmer stemmer = new PorterStemmer();
	private static TextFileFilter filter = new TextFileFilter();
    private static Perl5Util perl = new Perl5Util();
	private static Map stopWords = new HashMap();
	private static int numfiles = 0;
	private static int numbooks = 0;

	static
	{
		stopWords.put("Engineering", "n");
		stopWords.put("Engineers", "n");
		stopWords.put("pH", "n");
	}

	public static void main(String args[])
		//throws Exception
	{
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
        String username = "AP_PRO1"; //args[2];
        String password = "ei3it"; //args[3];

		List terms = new ArrayList();

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try
		{
            url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
            username = "AP_EV_SEARCH"; //args[2];
            password = "ei3it"; //args[3];
            con = DriverManager.getConnection(url,username,password);

            stmt = con.createStatement();
            rs = stmt.executeQuery("select MAIN_TERM_SEARCH from CPX_THESAURUS WHERE STATUS='C'");
            while(rs.next())
            {
                String term = rs.getString("MAIN_TERM_SEARCH");
                terms.add(term);
                System.out.println(term);
            }

            if(rs != null) {
                close(rs);
            }
            if(stmt != null) {
                close(stmt);
            }
		}
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }                          
        }

		try
		{
            // "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it"
            
            url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; //args[1];
            username = "AP_PRO1"; //args[2];
            password = "ei3it"; //args[3];
			con = DriverManager.getConnection(url,username,password);
			Map books = new HashMap();

            stmt = con.createStatement();
			rs = stmt.executeQuery("select unique(bn), ti from BOOK_PAGES_TEMP where BN13='9780884154303'");
			while(rs.next())
			{
				String bn = rs.getString("bn");
				String ti = rs.getString("ti");
				books.put(bn,ti);
			}

            if(rs != null) {
                close(rs);
            }
            if(stmt != null) {
                close(stmt);
            }
            
			Iterator booksIt = (books.keySet()).iterator();

			PreparedStatement pstmt1 = null;

			pstmt1 = con.prepareStatement("select * from BOOK_PAGES_TEMP where bn = ?");

			while(booksIt.hasNext())
			{
				String isbn = (String)booksIt.next();
				String bookTitle = (String)books.get(isbn);

				pstmt1.setString(1, isbn);
				rs = pstmt1.executeQuery();

				while(rs.next())
				{
					String id = rs.getString("docid");
					String keywords = null;
					Clob clob = rs.getClob("page_txt");
					if(clob != null)
					{
						String abs = null;
                        try {
                            abs = StringUtil.getStringFromClob(clob);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if(abs != null) {
    						abs = abs.replaceFirst(bookTitle, "");
    						keywords = getKeywords(terms, abs);
    						if(keywords != null)
    						{
    							System.out.println("update book_pages set page_keywords = '"+keywords.replaceAll("'", "''")+"' where docid = '"+id +"';");
    						}
                        }
					}
				}
            }
            if(rs != null) {
                close(rs);
            }
            if(pstmt1 != null) {
                close(pstmt1);
            }		
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }     
        }
        System.out.println("commit;");
	}

	private static void close(ResultSet rs)
	{
		if(rs != null)
		{
			try
			{
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void close(Statement stmt)
	{
		if(stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void close(PreparedStatement pstmt)
	{
		if(pstmt != null)
		{
			try
			{
				pstmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static String getKeywords(List terms, String text)
	{
		StringBuffer termbuf = new StringBuffer();
		String spage = stem(text);
		for(int j=0; j<terms.size();j++)
		{
			String term = (String) terms.get(j);
			term = term.trim();
			if(term.length()>0 &&
			   !stopWords.containsKey(term))
			{
				String stemmedTerm = stem(term);
				String regex = "m/\\b"+stemmedTerm+"\\b/";
				if(perl.match(regex,spage))
				{
					//System.out.println("Hit:"+term);
					if(termbuf.length() > 0)
					{
						termbuf.append("|");
					}
					termbuf.append(perl.substitute("s/'/''/g",term));
				}
			}
		}

		if(termbuf.length()>0)
		{
			return termbuf.toString();
		}

		return null;
	}

	private static String stem(String words)
	{
		String lwords = words.toLowerCase();
		String[] splitwords = lwords.split("\\W+");
		StringBuffer stemmedBuffer = new StringBuffer();

		for(int i=0; i<splitwords.length;i++)
		{
			if(stemmedBuffer.length() > 0)
			{
				stemmedBuffer.append(" ");
			}

			stemmedBuffer.append(splitwords[i]);
		}

		return stemmedBuffer.toString();
	}
}