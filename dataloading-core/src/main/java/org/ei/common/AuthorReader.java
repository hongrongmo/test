package org.ei.common;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class AuthorReader extends FilterReader {
      private int CLEAN = 1;
      private int IN_ENTITY = 2;
      private int AUTHOR_END = 3;

      private int state = CLEAN;
      private int[] buf = new int[2000];
      private int bufIndex = -1;

      public AuthorReader(StringReader in) {
             super(in);
      }

      public String readAuthor() throws IOException {
             int i = -1;
             state = CLEAN;
             bufIndex = -1;
             while (state != AUTHOR_END && (i = read()) != -1) {

                   int c = (int) i;
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
}

