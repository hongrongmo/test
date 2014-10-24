package org.ei.util.test;

public class TestSession {
    public TestSession() {
    }

    public void run() throws Exception {
        SessionProcedure sessproc = new SessionProcedure();

        sessproc.createSession();
        sessproc.touchSession();
        sessproc.updateSession();
        /*
         * SessionBroker_updateSession1
         */
    }

}
