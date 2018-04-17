package vtw.threads.vtw;

import java.util.regex.Pattern;

public class InitiatVtwThreads 
{

	private static int numberOfRuns=0;
	private static String queueName = "acc-contributor-event-queue-EV";
	private static String sqlldrFileName = null;
	static int loadNumber = 0;
	static int recsPerZipFile = 20000;
	static int recsPerSingleConnection = 1000;
	static String type = "forward";
	
		public static void main(String[] args) throws Exception {

			if(args[0] !=null)
			{
				numberOfRuns = Integer.parseInt(args[0]);
			}
			if(args[1] !=null)
			{
				queueName = args[1];
			}
			if(args.length >6)
			{
				if(args[2] !=null)
				{
					sqlldrFileName = args[2];
				}

				if(args[3] !=null)
				{
					if(Pattern.matches("^\\d*$", args[3]))
					{
						loadNumber = Integer.parseInt(args[3]);
					}
					else
					{
						System.out.println("loadNumber has wrong format");
						System.exit(1);
					}
				}
				if(args[4] !=null)
				{
					recsPerZipFile = Integer.parseInt(args[4]);
					System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
				}
				if(args[5] !=null)
				{
					recsPerSingleConnection = Integer.parseInt(args[5]);
					System.out.println("Number of keys per one HttpConnection: " + recsPerSingleConnection);
				}
				if(args[6] !=null)
				{
					type = args[6];
					System.out.println("Message type: " + type);
				}
			}
			else
			{
				System.out.println("not enough parameters!");
				System.exit(1);
			}
			/*
			ArchiveVTWPatentRefeed thread1 = new ArchiveVTWPatentRefeed(numberOfRuns,queueName,sqlldrFileName,
																		loadNumber,recsPerZipFile,recsPerSingleConnection,
																		"Thread1");*/
			
			ArchiveVTWPatentAsset thread1 = new ArchiveVTWPatentAsset(numberOfRuns,queueName,sqlldrFileName,
					loadNumber,recsPerZipFile,recsPerSingleConnection,type,
					"Thread1");
			
			thread1.start();
			
			/*ArchiveVTWPatentRefeed thread2 = new ArchiveVTWPatentRefeed(numberOfRuns,queueName,sqlldrFileName,
					loadNumber,recsPerZipFile,recsPerSingleConnection,
					"Thread2");
			thread2.start();*/

	}
		
		
		
		
	
}
