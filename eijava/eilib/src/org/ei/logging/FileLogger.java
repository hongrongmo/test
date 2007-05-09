package org.ei.logging;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FileLogger
{

    private String fileName;
    private long rollEvery;
    private PrintWriter out;
    private long fileOpened;
    private static FileLogger instance;


    public static FileLogger getInstance(String fileName,
                                         long roll)
        throws IOException
    {
        if(instance == null)
        {
            instance = new FileLogger(fileName,
                                      roll);
        }

        return instance;
    }


    private FileLogger(String fileName,
                       long roll)
        throws IOException
    {
        this.fileName = fileName;
        this.rollEvery = roll;
        out = new PrintWriter(new FileWriter(fileName, true));
        fileOpened = System.currentTimeMillis();
    }

    public void write(String message)
        throws IOException
    {
        rollLog();
        out.println(message);
        out.flush();
    }

    public void rollLog()
        throws IOException
    {
        long current = System.currentTimeMillis();
        if((current - fileOpened) > rollEvery)
        {
            GregorianCalendar calendar = new GregorianCalendar();
            StringBuffer buf = new StringBuffer();
            String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            String month = Integer.toString(calendar.get(Calendar.MONTH));
            String year = Integer.toString(calendar.get(Calendar.YEAR));
            buf.append(month);
            buf.append("-");
            buf.append(day);
            buf.append("-");
            buf.append(year);
            buf.append("-");
            buf.append(Long.toString(current));

            out.flush();
            out.close();
            File file = new File(this.fileName);
            file.renameTo(new File(this.fileName+"."+buf.toString()));
            out = new PrintWriter(new FileWriter(fileName));
            fileOpened = current;
        }
    }

    public void close()
        throws IOException
    {
        out.flush();
        out.close();
    }
}
