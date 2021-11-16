package util;

import java.io.IOException;

public class RunScript{

    public static void main(String[] args) throws IOException, InterruptedException {

        String fileName = args[0];
        System.out.println("FileName: " + fileName);

        Runtime r = Runtime.getRuntime();

        Process p = r.exec("./"+fileName);
        int t = p.waitFor();
        
        System.out.println("Reached end of running shell script");

    }
}