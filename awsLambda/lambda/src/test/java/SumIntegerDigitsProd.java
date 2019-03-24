import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

//import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * @Author: HTeleb
 * @Date: 03/05/2019
 * @Description: Test  how to Sum the digits of an integer
 */

public class SumIntegerDigitsProd {
	public static void main(String[] args) {
		int x, y = 0, z = 0, i = 0, curr;
		Scanner scan = null;
		Label label1 = null, label2 = null;
		Workbook workBook = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		FileOutputStream outputStream = null;

		try {

			File inFile = new File("AK-trial.xls");
			if (!inFile.exists()) 
			{
				System.out.println("Workbook not exist, create a new one");
				new CreateWorkBook().CreateBlankWorkBook();
			}
			
				
				FileInputStream inputStream = new FileInputStream(inFile);
				workBook = WorkbookFactory.create(inputStream);
				

				Map<Integer, Integer> freq = new HashMap<Integer, Integer>();

				SumIntegerDigitsProd obj = new SumIntegerDigitsProd();

				scan = new Scanner(System.in);
				System.out.println("Enter the numbers, to stop enter 0");

				/*
				 * sheet.addCell(new Label(0, 0, "X")); sheet.addCell(new Label(1,0, "Y"));
				 */

				sheet = workBook.getSheet("sample1");
				i = sheet.getLastRowNum();

				if (inputStream != null)

				{
					while ((x = scan.nextInt()) != 0) {
						if (obj.isValid(x)) {
							freq.clear();

							// sheet.addCell(new Label());
							while (x != 0 && !(freq.containsValue(2))) {
								curr = x;
								i++;

								if (x > 0)
									x = x - Integer.valueOf(
											new StringBuffer(Integer.toString(Math.abs(x))).reverse().toString());
								else if (x < 0)
									x = x + Integer.valueOf(
											new StringBuffer(Integer.toString(Math.abs(x))).reverse().toString());

								if (freq.containsKey(x))
									freq.put(x, freq.get(x) + 1);
								else
									freq.put(x, 1);

								System.out.println(x);

								row = sheet.createRow(i);
								cell = row.createCell(0);
								cell.setCellValue(curr);
								cell = row.createCell(1);
								cell.setCellValue(x);

								/*
								 * sheet.addCell(new Label(0,i,Integer.toString(curr))); sheet.addCell(new
								 * Label(1,i,Integer.toString(x)));
								 */

								// System.out.println("Row#: " + i); // only for debugging

							}

							System.out.println("frequency: " + freq);

							i++;
							// add blank line to separate between multiple inputs

							row = sheet.createRow(i);
							cell = row.createCell(0);
							cell.setCellValue("");
							;
							cell = row.createCell(1);
							cell.setCellValue("");

							/*
							 * sheet.addCell(new Label(0,i,"")); sheet.addCell(new Label(1,i,""));
							 */
						} else
							System.out.println("Invalid number, try again with non zero, and diff digits number");

					}
					inputStream.close();
					// workBook.write();

					outputStream = new FileOutputStream("AK-trial.xls");
					workBook.write(outputStream);

				}

		} catch (InputMismatchException ex) {
			ex.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (workBook != null) {
				try {
					workBook.close();
				} catch (Exception e) {
					System.out.println("Failed to closed teh workbok!");
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					System.out.println("Failed to close outputStream!!!!" + e.getMessage());
				}
			}

			if (scan != null) {
				try {
					scan.close();
				} catch (Exception e) {
					System.out.println("Failed to close the scanner!");
					e.printStackTrace();
				}

			}

		}

	}

	public boolean isValid(int x) {
		/*
		 * int y =0, count=0; while(x>0) { y = x%10; // so it gets each digit
		 * individually x = x /10;
		 * 
		 * 
		 * System.out.println("x: " + x + ", y: " + y + ", count: " + count);
		 * 
		 * }
		 */

		if (x != 0 && x > 0 && !(Integer.toString(x).matches("^(\\d)\\1*$")))
			return true;
		return false;
	}

}
