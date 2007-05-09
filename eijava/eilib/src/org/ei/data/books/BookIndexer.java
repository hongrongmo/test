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
	private static HashMap stopWords = new HashMap();
	private static int numfiles = 0;
	private static int numbooks = 0;

	static
	{
		stopWords.put("Engineering", "n");
		stopWords.put("Engineers", "n");
		stopWords.put("pH", "n");
	}

	public static void main(String args[])
		throws Exception
	{

		BufferedReader vocabReader = null;
		ArrayList terms = new ArrayList();

		try
		{
			vocabReader = new BufferedReader(new FileReader("vocab.txt"));
			String term = null;
			while((term = vocabReader.readLine()) != null)
			{
				terms.add(term);
			}
		}
		finally
		{
			if(vocabReader != null)
			{
				vocabReader.close();
			}
		}

		Connection con = null;

		try
		{
			String driver = args[0];
			String url = args[1];
			String username = args[2];
			String password = args[3];
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url,username,password);
			ResultSet rs = null;
			Statement stmt = null;
			Map books = new HashMap();
			try
			{
					stmt = con.createStatement();
					rs = stmt.executeQuery("select bn, ti from books");
					while(rs.next())
					{
						String bn = rs.getString("bn");
						String ti = rs.getString("ti");
						books.put(bn,ti);
					}
			}
			finally
			{
				if(rs != null)
				{
					close(rs);
				}

				if(stmt != null)
				{
					close(stmt);
				}
			}

			Iterator booksIt = (books.keySet()).iterator();

			PreparedStatement pstmt1 = null;

			pstmt1 = con.prepareStatement("select * from book_pages where bn = ?");

			try
			{
				while(booksIt.hasNext())
				{
					String isbn = (String)booksIt.next();
					String bookTitle = (String)books.get(isbn);

					try
					{
						pstmt1.setString(1, isbn);
						rs = pstmt1.executeQuery();

						while(rs.next())
						{
							String id = rs.getString("docid");
							String keywords = null;
							Clob clob = rs.getClob("page_txt");
							if(clob != null)
							{
								String abs = StringUtil.getStringFromClob(clob);
								abs = abs.replaceFirst(bookTitle, "");
								keywords = getKeywords(terms, abs);
								if(keywords != null)
								{
									System.out.println("update book_pages set page_keywords = '"+keywords.replaceAll("'", "''")+"' where docid = '"+id +"';");
								}
							}
						}
					}
					finally
					{
						close(rs);
					}
					System.out.println("commit;");
				}
			}
			finally
			{
				close(pstmt1);
			}
		}
		finally
		{
			if(con != null)
			{
				con.close();
			}
		}
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