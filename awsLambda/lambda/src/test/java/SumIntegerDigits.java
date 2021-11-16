import java.io.File;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * @Author: HTeleb
 * @Date: 03/05/2019
 * @Description: Test  how to Sum the digits of an integer
 */
public class SumIntegerDigits 
{

	public static void main(String[] args)
	{
		int x,y=0,z=0,i=0, curr;
		Scanner scan = null;
		Label label1 = null, label2 = null;
		WritableWorkbook workBook = null;

		try
		{

			File out = new File("AK.xls");

			workBook = Workbook.createWorkbook(out);
			WritableSheet sheet = workBook.createSheet("sample1", 0);
			Map <Integer,Integer> freq = new HashMap<Integer, Integer>();

			SumIntegerDigits obj = new SumIntegerDigits();

			scan = new Scanner(System.in);

			sheet.addCell(new Label(0, 0, "X"));
			sheet.addCell(new Label(1,0, "Y"));

			if(out.exists() && !(out.isDirectory()))

			{
				while ((x = scan.nextInt()) !=0)
				{
					if(obj.isValid(x))
					{
						freq.clear();

						//sheet.addCell(new Label());
						while (x !=0 && !(freq.containsValue(2)))
						{
							curr = x;
							i++;

							if(x >0)
								x = x - Integer.valueOf(new StringBuffer(Integer.toString(Math.abs(x))).reverse().toString());
							else if(x <0)
								x = x + Integer.valueOf(new StringBuffer(Integer.toString(Math.abs(x))).reverse().toString());

							if(freq.containsKey(x))
								freq.put(x,freq.get(x) +1);
							else
								freq.put(x,1);

							System.out.println(x);


							sheet.addCell(new Label(0,i,Integer.toString(curr))); 
							sheet.addCell(new Label(1,i,Integer.toString(x)));
							
							//System.out.println("Row#: " + i);   // only for debugging

						}

						System.out.println("frequency: " + freq);

						i++;
						// add blank line to separate between multiple inputs
						sheet.addCell(new Label(0,i,"")); 
						sheet.addCell(new Label(1,i,""));
					}
					else
						System.out.println("Invalid number, try again with non zero, and diff digits number");



				}
				
				workBook.write();

			}

		}
		catch(InputMismatchException ex)
		{
			ex.printStackTrace();
		}
		catch(NoSuchElementException e)
		{
			e.printStackTrace();
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

			if(scan !=null)
			{
				try
				{
					scan.close();
				}
				catch(Exception e)
				{
					System.out.println("Failed to close the scanner!");
					e.printStackTrace();
				}

			}

		}

	}

	public boolean isValid(int x)
	{
		/*
		 * int y =0, count=0; while(x>0) { y = x%10; // so it gets each digit
		 * individually x = x /10;
		 * 
		 * 
		 * System.out.println("x: " + x + ", y: " + y + ", count: " + count);
		 * 
		 * }
		 */

		if(x!= 0 && x >0 && !(Integer.toString(x).matches("^(\\d)\\1*$")))
			return true;
		return false;
	}
}
