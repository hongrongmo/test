
/*
 * @author: hteleb
 * @description: AMazon check adjacent cells status (0/1) for n days
 * if cell is active today (1), it becomes inactive tomorrow (0), 
 * if surrounding cells are both active/inactive, the cell becomes inactive
 * if otherwise, the cell is active
 * first cell, one virtual left is inactive
 * last cell, one virtual on right is inactive
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AmazonAdjacentCells 
{
	
	public List<Integer> cellsStatus(int [] states, int days)
	{
		int size = states.length;
		
		
		int [] temp = new int [states.length];
		List<Integer> output = new ArrayList<Integer>();
		
		// copy stats to temp
		System.arraycopy(states, 0, temp, 0, size);
		if(size >=2)
		{
			for(int i = 1; i<= days; i++)
			{
				temp [0] = 0^ states[1];
				temp [size-1] = temp[size-2] ^0;
				
				for(int j=1;j<size-2;j++)
				{
					temp[j] = states[j-1]^states[j+1];
				}
				
				// copy Temp result to states
				System.arraycopy(temp, 0, states, 0, size);
			}
			// copy integer array to List
			Collections.addAll(output, Arrays.stream(states).boxed().toArray(Integer[]::new));	
		}
		
		return output;
		
	}
	public static void main(String[] args)
	{
		int days=4, active=0, inactive=0;
		//int[] states = {0, 1, 0, 1, 0, 1, 0, 1};		// case 1
		int[] states = {0, 1, 1, 1, 0, 1, 1, 0};
		AmazonAdjacentCells obj = new AmazonAdjacentCells();
		List<Integer> output = obj.cellsStatus(states, days);
		
		for(int status:output)
		{
			if(status ==0)
				inactive++;
				else
					active++;
			System.out.println(status);
		}
		System.out.println("status after " + days + " days: Inactive: " + inactive + " , active: " + active);
		
		
		
	}
	
}
