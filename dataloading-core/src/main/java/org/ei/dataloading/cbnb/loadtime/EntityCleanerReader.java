package org.ei.dataloading.cbnb.loadtime;

import java.io.*;

public class EntityCleanerReader extends FilterReader {
    private boolean openFile = true;
    private final static int BUFFER = 1;
    private final static int CLEAN = 0;
    private int state = CLEAN;
    boolean done = false;
    int count = 0;

    public EntityCleanerReader(Reader in) {
        super(in);
    }

    public boolean ready() throws IOException {
        return in.ready();
    }

    public void close() throws IOException {
        in.close();
    }

    public void mark(int readLimit) throws IOException {
        in.mark(readLimit);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    private boolean endOfStream = false;

    public int read(char[] text, int offset, int length) throws IOException {

        if (endOfStream) return -1;
        int numRead = 0;

        for (int i = offset; i < offset+length; i++) {
            int temp = this.read();
            if (temp == -1) {
              this.endOfStream = true;
              break;
            }
            text[i] = (char) temp;
            numRead++;
        }
        return numRead;

    }




    public int read() throws IOException
    {


        int i = in.read();

        if (i == -1)
        {
            return -1;
        }

        if (state == CLEAN)
        {
            if (i == 38)
            {
               // state = BUFFER;
               return 126;
            }
            else
            {
                if (i >= 1 && i <= 126)
                {
                    return i;
                }
                else
                {
                    return 32;
                }
            }
        }
        else
        {
            if (i == 59)
            {
                state = CLEAN;
            }
        }

        return 32;
    }
}
