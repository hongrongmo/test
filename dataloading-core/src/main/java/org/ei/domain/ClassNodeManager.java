package org.ei.domain;

import java.io.IOException;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.DiskMap;
import org.ei.util.GetPIDDescription;
import org.ei.xml.Entity;

public class ClassNodeManager {
    private final static Logger log4j = Logger.getLogger(ClassNodeManager.class);
    private DiskMap uspto;
    private DiskMap ipc;
    private DiskMap ecla;
    int lookupFlag = 2;

    private String usptodir;
    private String ipcdir;
    private String ecladir;

    private static ClassNodeManager instance;

    public static synchronized ClassNodeManager getInstance() {
        if (instance == null) {
            instance = new ClassNodeManager();
        }

        return instance;
    }

    private ClassNodeManager() {}

    public static synchronized boolean initialized() {
        return instance != null;
    }

    /**
     * Initialize class - looks for properties defining various lucene index directories
     *
     * @param applicationproperties
     */
    public static void init(ApplicationProperties applicationproperties) {
    	//HH 01/28/2015 to fix log4j appender issue for Pat extract
    	BasicConfigurator.configure();
    	//
    	
        ClassNodeManager instance = ClassNodeManager.getInstance();

        // Init uspto dir
        instance.usptodir = applicationproperties.getProperty(ApplicationProperties.USPTO_LUCENE_INDEX_DIR);
        if (GenericValidator.isBlankOrNull(instance.usptodir)) {
            throw new IllegalArgumentException("USPTO directory for lucene index is NOT defined!");
        }

        // Init ipcdir dir
        instance.ipcdir = applicationproperties.getProperty(ApplicationProperties.IPC_LUCENE_INDEX_DIR);
        if (GenericValidator.isBlankOrNull(instance.ipcdir)) {
            throw new IllegalArgumentException("IPC directory for lucene index is NOT defined!");
        }

        // Init ecladir dir
        instance.ecladir = applicationproperties.getProperty(ApplicationProperties.ECLA_LUCENE_INDEX_DIR);
        if (GenericValidator.isBlankOrNull(instance.ecladir)) {
            throw new IllegalArgumentException("ECLA directory for lucene index is NOT defined!");
        }

        try {
            instance.openUSPTO();
            instance.openIPC();
            instance.openECLA();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create ClassNodeManager!", e);
        }

    }

    public synchronized String seekECLA(String code) throws IOException {
        if (!this.initialized()) {
            throw new RuntimeException("ClassNodeManager is NOT initialized!");
        }
        String s = ecla.get(code);
        s = Entity.replaceLatinChars(s);
        return s;
    }

    public String seekIPC(String code, int lookupflag) throws IOException, InfrastructureException {
        if (!this.initialized()) {
            throw new RuntimeException("ClassNodeManager is NOT initialized!");
        }
        if (lookupflag == 1) {
            this.lookupFlag = 1;
        }
        String s = seekIPC(code);
        this.lookupFlag = 2;
        return s;
    }

    public synchronized String seekIPC(String code) throws IOException, InfrastructureException {
        if (!this.initialized()) {
            throw new RuntimeException("ClassNodeManager is NOT initialized!");
        }
        String s = ipc.get(code);
        s = Entity.replaceLatinChars(s);
        // System.out.println("Code="+code+" name= "+s);
        if (s == null && this.lookupFlag == 2) {
            GetPIDDescription pid = new GetPIDDescription();
            s = pid.getDescriptionFromLookupIndex(code);
        }
        return s;
    }

    public synchronized String seekUS(String code) throws IOException {
        if (!this.initialized()) {
            throw new RuntimeException("ClassNodeManager is NOT initialized!");
        }
        String s = uspto.get(code);
        return s;
    }

    public synchronized void close() {
        if (!this.initialized()) {
            throw new RuntimeException("ClassNodeManager is NOT initialized!");
        }
        try {
            this.uspto.close();
            log4j.warn("Closed USPTO index...");
        } catch (Exception e) {
            log4j.error(e);
        }

        try {
            this.ecla.close();
            log4j.warn("Closed ECLA index...");
        } catch (Exception e) {
            log4j.error(e);
        }

        try {
            this.ipc.close();
            log4j.warn("Closed IPC index...");
        } catch (Exception e) {
            log4j.error(e);
        }

    }

    private void openUSPTO() throws IOException {
        log4j.warn("Opening USPTO index at: '" + this.usptodir + "'");
        uspto = new DiskMap();
        uspto.openRead(this.usptodir, false);
    }

    private void openIPC() throws IOException {
        log4j.warn("Opening IPC index at: '" + this.ipcdir + "'");
        ipc = new DiskMap();
        ipc.openRead(this.ipcdir, false);
    }

    private void openECLA() throws IOException {
        log4j.warn("Opening ECLA index at: '" + this.ecladir + "'");
        ecla = new DiskMap();
        ecla.openRead(this.ecladir, false);

    }
}