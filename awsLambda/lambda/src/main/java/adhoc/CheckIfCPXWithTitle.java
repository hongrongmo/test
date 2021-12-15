package adhoc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ei.common.bd.BdCitationTitle;
import org.ei.dataloading.db.DBConnection;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import org.ei.xml.Entity;



/**
 * 
 * @author TELEBH
 * @Date: 12/09/2021
 * @Description: Judy provided Us excel sheet with 200k+ records with fields (authors, titiel, doi, conferencename) as asking
 * to check if these records are cpx by comparing with BD_MASTER (not c84) and mark each record whether cpx or not (y/n)
 * 
 * The issue with this this list there is no unique identifier like AN/PUI and only text fields existlisk Title to check
 * but tile issue is that database has special delimiters and also issue of special characters mapping
 */
public class CheckIfCPXWithTitle {

	String fileName = null;
	private String url="jdbc:oracle:thin:@localhost:1521:eid";
    private String driver="oracle.jdbc.driver.OracleDriver";
    private String userName;
    private String passwd;
    Set<String> pubyr;
    List<String> titlesList;
    
    FileWriter out;
    
	public static void main(String[] args)
	{
		CheckIfCPXWithTitle obj = new CheckIfCPXWithTitle();
		obj.init();
		obj.run(args);
		obj.fetchBDMasterCPXTitles();
		obj.getCpxPubYear();
		// close the file
		obj.end();
	}
	
	private void init()
	{
		try
		{
			out = new FileWriter(new File("matched_cpx.txt"));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run(String[] args)
	{
		titlesList = new ArrayList<>();
		
		if(args.length > 4)
		{
			fileName = args[0];
			url = args[1];
			driver = args[2];
			userName = args[3];
			passwd = args[4];
		}
		else
		{
			System.out.println("Not enough parameters!!");
			System.exit(1);
		}
		if(fileName != null && !fileName.isBlank())
		{
			System.out.println(fileName);
			try(FileInputStream fis = new FileInputStream(new File(fileName)))
			{
				//define workbook
				XSSFWorkbook wb = new XSSFWorkbook(fis);
				//define sheet
				XSSFSheet sh = wb.getSheetAt(0);
				
				//iterate through rows
				
				sh.forEach(row -> {
				titlesList.add(row.getCell(5).getStringCellValue().toLowerCase().trim());     // Unnique titles size: 167415
				
				} );
			}
			catch(FileNotFoundException ex)
			{
				System.out.println("File not found");
				ex.printStackTrace();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			System.out.println("Total rows in the excel sheet with Title: " + titlesList.size());
		}	
	}
	
	public void fetchBDMasterCPXTitles()
	{
		ResultSet rs;
		PreparedStatement stmt;
		Map<String,String> db_titles = new HashMap<>();
		Map<String,String> db_confInfo = new HashMap<>();
		
		try(Connection con = new DBConnection().getConnection(url, driver, userName, passwd))
		{
			String query;
			getCpxPubYear();
			for (String yr : pubyr) {
				if(pubyr == null)
					query = "Select accessnumber,citationtitle,confname,confcode,confdate from bd_master where database='cpx' and publicationyear is null";
				else
					query = "Select accessnumber,citationtitle,confname,confcode,confdate from bd_master where database='cpx' and publicationyear='"+ yr + "'";
				System.out.println(query);
				stmt = con.prepareStatement(query);
				stmt.setFetchSize(1000);
				rs = stmt.executeQuery();
				System.out.println("Time Before: " + System.currentTimeMillis());
				int i = 1;
				while (rs.next()) {
					String an = rs.getString(1);
					String str = rs.getString(2);
					String confName = rs.getString(3);
					String confCode = rs.getString(4);
					String confDate = rs.getString(5);
					// lowercase and map special characters
					String[] titles = prepareCitationTitle(str);

					if (titles.length > 0) {
						for (String st : titles) {
							if (st != null && !st.isBlank())
							{
								db_titles.put(an,Entity.prepareString(st).toLowerCase().trim());
								db_confInfo.put(an, confName + " *** " + confCode + " *** " + confDate);
							}
								
						}
					}
				}
				System.out.println("Time after: " + System.currentTimeMillis());
				//Compare DB titles with given titles list to see if any match , and if any , write it in file with title, match
				compareTitles(db_titles, db_confInfo);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Total CPX records: " + db_titles.size());
		
	}
	
	private String[] prepareCitationTitle(String citationTitle) throws Exception {
		List<String> list = new ArrayList<>();
		if (citationTitle != null) {
			BdCitationTitle ct = new BdCitationTitle(citationTitle);
			List<BdCitationTitle> ctList = ct.getCitationTitle();

			for (int i = 0; i < ctList.size(); i++) {
				BdCitationTitle ctObject = (BdCitationTitle) ctList.get(i);

				if (ctObject.getTitle() != null) {
					list.add(ctObject.getTitle());
				}

			}
		}

		return (String[]) list.toArray(new String[1]);
	}
	
	private void getCpxPubYear()
	{
		String[] yrs = {"1989","1975","1959","2022","1963","1905","1910","1927","1956","1977-1978","1995-1996","1954","1979-1981","1993-94","1917","1948","1928","2010-2014","1919","1915","1896",
		                "1965-1965","2010","2004","2008","1992","1991","1987","1977","2002","2013","2019","1981","1974","1983","1940","1997-1998","1965","1955","1980-1981","1931","1996-1997",
		                "1999-2000","1986-1987","1991-1992","1921","1990-1991","1980-1982","1951","1926","1993-1995","5Eng","1929","1943","1920","2014-2012","1914","1918","1901","1970-1971",
		                "1986-1996","2005","1990","2016","1973","1979","1976","1978","1988","1995","2020","1964","1978-1979","1936","1906","1993-1994","1992-1993","1979-1980","1981-1982","1944",
		                "1938","1989-190","1924","1903","1925","2013-2014","2005-2012","1892","1894","1993","1998","1980","2009","1967","1982","2004-2005","1985","1923","1912","1962","1984-1985",
		                "2001-2002","1958","1998-1999","1989-1990","2006-2007","1909","1953","1932","1941","2008-1996","820C","199","1998-99","1995-96","2010-2011","1913","1891","1902","1900",
		                "2013-2013","1997","2000","2014","2018","1969","2001","","2003-2006","1904","1977-1980","1930","2009-2010","2012-2013","1897","1996","2007","2012","2017","1986","2021",
		                "1957","1987-1988","1964-1980","1989-1989","1934","1933","2011-2012","1911","1946","2008-2010","1889","2023","2015","1972","1994","1999","1984","1968","2005-2006","1960",
		                "1952","1966","1950","1907","1983-1984","1985-1986","1935","1997-98","1979-1989","1885","1942","2016-2017","1922","1899","1908","2012-2014","2006","2003","1971","2011",
		                "1970","2002-2003","2003-2004","1937","1961","1947","1939","1994-1995","1982-1983","2000-2001","1997-2003","1988-1989","1945","1981-1983","1991-1991","2001-1993",
		                "2007-2008","1916","1992-1994","1989-1993","1949","2008-2009","1890","2014-2015","1976-1977", "null"};
		pubyr = new HashSet<>();
		pubyr.addAll(Arrays.asList(yrs));
		System.out.println("pubyr list size: " + pubyr.size());
	}
	
	private void compareTitles(Map<String,String> dbInfo, Map<String,String> dbconfInfo)
	{
	
		
		dbInfo.forEach((k,v) -> {
			if(titlesList.contains(v))
			{
				
				try
				{
					
				out.write(k + "\t" + v + " *** " + dbconfInfo.get(k) + "\n");
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		
	}
	
	public void end()
	{
		try
		{
			if(out != null)
			{
				out.flush();
				out.close();
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception closing the file");
			e.printStackTrace();
		}
	}
}
