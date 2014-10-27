package org.ei.domain;

import java.util.*;
import org.ei.xml.Entity;
import org.ei.util.DiskMap;
import org.ei.util.GetPIDDescription;

public class ClassNodeManager {
    private DiskMap uspto;
    private DiskMap ipc;
    private DiskMap ecla;
    int lookupFlag = 2;

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

    public  String seekIPC(String code,int lookupflag) throws Exception {

		if(lookupflag==1)
		{
			this.lookupFlag=1;
		}
		String s = seekIPC(code);
		this.lookupFlag = 2;
		return s;
	}

    public synchronized String seekIPC(String code) throws Exception {

        String s = ipc.get(code);
        s = Entity.replaceLatinChars(s);
		//System.out.println("Code="+code+" name= "+s);
		if(s==null && this.lookupFlag==2)
		{
			GetPIDDescription pid = new GetPIDDescription();
			s = pid.getDescriptionFromLookupIndex(code);
		}
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