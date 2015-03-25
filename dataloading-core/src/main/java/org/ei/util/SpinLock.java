package org.ei.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

public class SpinLock {
    private Hashtable<String, Lock> lockTable = new Hashtable<String, Lock>();
    private static SpinLock instance;

    public static String FOR_READ = "read";
    public static String FOR_WRITE = "write";

    public static synchronized SpinLock getInstance() {
        if (instance == null) {
            instance = new SpinLock();
        }

        return instance;
    }

    private SpinLock() {
        // Private to make it a singleton
    }

    /*
     * Main test locking functionality
     */
    public static void main(String args[]) throws Exception {
        SpinLock spin = SpinLock.getInstance();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));

        // First place read lock
        System.out.println("Placed read lock on key1");
        spin.placeLock("key1", SpinLock.FOR_READ, 4, 1000);

        // Now try and get write lock, should fail
        spin.lockdump(out);

        if (!spin.placeLock("key1", SpinLock.FOR_WRITE, 4, 1000)) {
            System.out.println("GOOD: Could not place write lock on key1");
        }

        // No try and put another read lock on key 1;

        if (spin.placeLock("key1", SpinLock.FOR_READ, 4, 1000)) {
            System.out.println("GOOD: put second read lock on key1");
        } else {
            System.out.println("BAD: Could not place second read lock on key1");
        }
        spin.lockdump(out);

        // Now try to put a read lock on a key 2

        if (spin.placeLock("key2", SpinLock.FOR_READ, 4, 1000)) {
            System.out.println("GOOD: put a read lock on key2");
        }

        spin.lockdump(out);

        // Now remove one read lock from key1

        spin.releaseLock("key1", SpinLock.FOR_READ);
        System.out.println("GOOD: removed one read lock from key1");
        spin.lockdump(out);

        // Now try to write key1

        if (!spin.placeLock("key1", SpinLock.FOR_WRITE, 4, 1000)) {
            System.out.println("GOOD: Could not place write lock on key1");
        }

        // Now remove last read lock from key

        spin.releaseLock("key1", SpinLock.FOR_READ);
        System.out.println("GOOD: removed one read lock from key1");
        spin.lockdump(out);

        // Now try putting the write lock on key1

        if (spin.placeLock("key1", SpinLock.FOR_WRITE, 4, 1000)) {
            System.out.println("Good: Placed write lock on key1");
        }
        spin.lockdump(out);

        // Now remove one read lock from key2

        spin.releaseLock("key2", SpinLock.FOR_READ);
        System.out.println("GOOD: removed one read lock from key2");
        spin.lockdump(out);
        out.close();

    }

    private synchronized boolean placedLock(String key, String lockType) {
        boolean placed = false;

        if (!lockTable.containsKey(key)) {
            Lock l = new Lock();
            if (lockType.equals(FOR_READ)) {
                l.incrementRead();
                // System.out.println("Placed Read Lock");
            } else {
                l.incrementWrite();
                // System.out.println("Placed Write Lock");
            }

            lockTable.put(key, l);

            placed = true;
        } else if (lockType.equals(FOR_READ)) {
            Lock l = (Lock) lockTable.get(key);
            if (l.writeCount < 1) {
                l.incrementRead();
                lockTable.put(key, l);
                placed = true;
                // System.out.println("Place Another Read Lock");
            }
        } else {
            // System.out.println("Unable to place lock");
            placed = false;
        }

        return placed;
    }

    public boolean placeLock(String key, String lockType, int cycles, long sleepTime) throws SpinLockException {

        boolean didIt = false;
        try {
            for (int x = 0; x < cycles; ++x) {
                if (!placedLock(key, lockType)) {
                    System.out.println("Sleeping:" + x);
                    Thread.sleep(sleepTime);
                } else {
                    didIt = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new SpinLockException(e);
        }

        return didIt;
    }

    public synchronized void releaseLock(String key, String lockType) {

        if (lockType.equals(FOR_WRITE)) {
            lockTable.remove(key);
            // System.out.println("Released Write Lock");
        } else {

            Lock l = (Lock) lockTable.get(key);

            l.decrementRead();
            // System.out.println("Decrementing the Read Lock");

            if (l.getReadCount() == 0) {

                lockTable.remove(key);
                // System.out.println("Released Last Read Lock");
            }

        }
    }

    public synchronized void lockdump(Writer out) throws IOException {
        Enumeration<String> keys = lockTable.keys();
        out.write("Total locks:" + lockTable.size());
        out.write("\n");
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Lock l = (Lock) lockTable.get(key);
            out.write(key);
            out.write(" : ");
            out.write("write locks (" + l.getWriteCount() + ") ");
            out.write("read locks (" + l.getReadCount() + ")");
            out.write("\n");
            out.flush();
        }
    }

    class Lock {
        private int readCount;
        private int writeCount;

        Lock() {
            readCount = 0;
            writeCount = 0;
        }

        void incrementRead() {
            ++readCount;

        }

        void decrementRead() {
            --readCount;

        }

        void incrementWrite() {
            ++writeCount;
        }

        void decrementWrite() {
            --writeCount;
        }

        int getReadCount() {
            return readCount;
        }

        int getWriteCount() {
            return writeCount;
        }

    }

}
