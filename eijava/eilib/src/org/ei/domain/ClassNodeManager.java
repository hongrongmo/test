package org.ei.domain;

import java.util.*;
import org.ei.xml.Entity;
import org.ei.util.DiskMap;

public class ClassNodeManager {
    private DiskMap uspto;
    private DiskMap ipc;
    private DiskMap ecla;

    private static ClassNodeManager instance;

    public static synchronized ClassNodeManager getInstance() throws Exception {
        if (instance == null) {
            instance = new ClassNodeManager();
        }

        return instance;
    }

    public static synchronized boolean initialized()
    {
		if(instance == null)
		{
			return false;
		}

		return true;
	}

    public synchronized String seekECLA(String code) throws Exception {
        String s = ecla.get(code);
        s = Entity.replaceLatinChars(s);
        return s;
    }

    public synchronized String seekIPC(String code) throws Exception {
        if (code != null) {
            //System.out.println("CODE:" + code);
        }
        else {
            //System.out.println("ISNULL");
        }

        String s = ipc.get(code);
        s = Entity.replaceLatinChars(s);

        return s;
    }

    public synchronized String seekUS(String code) throws Exception {
        String s = uspto.get(code);
        return s;
    }

    public synchronized void close() {
        try {
            uspto.close();
            System.out.println("Closed USPTO index...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ecla.close();
            System.out.println("Closed ECLA index...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ipc.close();
            System.out.println("Closed IPC index...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ClassNodeManager() throws Exception {
        openUSPTO();
        openIPC();
        openECLA();
    }

    private void openUSPTO() throws Exception {
        System.out.println("Opening USPTO index...");
        uspto = new DiskMap();
        uspto.openRead("uspto", false);
    }

    private void openIPC() throws Exception {
        System.out.println("Opening IPC index...");
        ipc = new DiskMap();
        ipc.openRead("ipc", false);
    }

    private void openECLA() throws Exception {
        System.out.println("Opening ECLA index...");
        ecla = new DiskMap();
        ecla.openRead("ecla", false);

    }
}