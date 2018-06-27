package org.ei.common;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AuthorStream extends FilterInputStream {

    private int CLEAN = 1;
    private int IN_ENTITY = 2;
    private int AUTHOR_END = 3;

    private int state = CLEAN;
    private char[] buf = new char[2000];
    private int bufIndex = -1;

    public static void main(String args[]) throws IOException {
        String authors = "Joel E. Bernstein;Dave e&adn;sd smith";
        AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(authors.getBytes()));
        String author = "";
        while ((author = aStream.readAuthor()) != null) {
            System.out.println("Author:" + author);
        }

    }

    public String readAuthor() throws IOException {
        int i = -1;
        state = CLEAN;
        bufIndex = -1;
        while (state != AUTHOR_END && (i = read()) != -1) {

            char c = (char) i;
            // System.out.println("Adding "+ c);
            if (state == CLEAN) {
                if (c == '&') {
                    state = IN_ENTITY;
                } else if (c == ';') {
                    state = AUTHOR_END;
                }
            } else if (state == IN_ENTITY) {
                if (c == ' ' || c == ';') {
                    state = CLEAN;
                }
            }

            if (state != AUTHOR_END) {
                if (bufIndex == (buf.length - 1)) {
                    return null;
                }

                buf[++bufIndex] = c;
            }
        }

        // System.out.println(bufIndex);
        if (bufIndex == -1) {
            return null;
        }

        return new String(buf, 0, bufIndex + 1);
    }

    public AuthorStream(InputStream in) {
        super(in);
    }

    public void close() throws IOException {
        in.close();
    }

    public void mark(int readLimit) {
        in.mark(readLimit);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}
