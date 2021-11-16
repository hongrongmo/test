import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * @author: telebh
 * @Date: 03/24/2019
 * @Description: create a bank workbook/sheet
 */
public class CreateWorkBook 
{

	public void CreateBlankWorkBook()
	{
		WritableWorkbook workBook = null;
		
		try
		{
			
			File out = new File("AK-trial.xls");

			//create workbook & sheet
			workBook = Workbook.createWorkbook(out);
			WritableSheet sheet = workBook.createSheet("sample1", 0);
			
			
			//create header cells
			sheet.addCell(new Label(0, 0, "X"));
			sheet.addCell(new Label(1,0, "Y"));
			workBook.write();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			if(workBook !=null)
			{
				try
				{
					workBook.close();
				}
				catch(Exception e)
				{
					System.out.println("Failed to closed teh workbok!");
					e.printStackTrace();
				}
			}

		}
		
	

	}
}
