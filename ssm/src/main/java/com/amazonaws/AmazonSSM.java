package com.amazonaws;

/**
 * 
 * @author TELEBH
 * @Date: 05/14/2021
 * @Description: Test Amazon Simple System Manager for reading Oracle RDS credentials from Parameter store
 */

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;


public class AmazonSSM {


	/**
	 * 
	 * @param args
	 * Param is Parameter store name
	 * @result: Parameter store value
	 * It worked perfectly. i.e. for parameter: /ev/dl/dev/db_driver and /ev/dl/dev/db_password
	 * 
	 * NOTE: Setting GetParameterRequest with option withDecryption(true) can read password in text value
	 */
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetParameter <paraName>\n\n" +
                "Where:\n" +
                "    paraName - the name of the parameter.\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String paraName = args[0];
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        getParaValue(ssmClient, paraName);
        ssmClient.close();
    }

    public static void getParaValue(SsmClient ssmClient, String paraName) {

        try {
            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(paraName).withDecryption(true)
                .build();

            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            System.out.println("The parameter value is "+parameterResponse.parameter().value());

        } catch (SsmException e) {
        System.err.println(e.getMessage());
        System.exit(1);
        }
   }
}
