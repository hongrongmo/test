import java.util.ArrayList;
import java.util.List;

/*import org.ei.common.Constants;*/


/*
 * @author: HT
 * @Date: 08/15/2017
 * @Description: test logic of how is GRF/GEO coordinates is working
 */
public class GrfCoordinatesTest {

	
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	/*public static final String AUDELIMITER = Constants.AUDELIMITER;
	public static final String IDDELIMITER = Constants.IDDELIMITER;*/
	
	
	public static void main(String[] args) {
		
		
		String coordinates = "N440000N441000E1255000E1254000";
		
		if(coordinates != null)
		{
		  String strcoordinates = coordinates;
		  String[] termcoordinate = strcoordinates.split(AUDELIMITER);
		  for(int j = 0; j < termcoordinate.length; j++)
		  {
			String[] termcoordinates = termcoordinate[j].split(IDDELIMITER);
			if(termcoordinates.length == 1)
			{
				String[] termcoordinates_tmp = new String[2];
				termcoordinates_tmp[0] = j + "";
				termcoordinates_tmp[1] = termcoordinates[0];
				termcoordinates = termcoordinates_tmp;

			}
		  }
			
			

	}
	}
}
