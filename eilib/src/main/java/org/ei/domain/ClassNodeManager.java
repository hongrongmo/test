package org.ei.domain;

import java.io.IOException;

import org.apache.commons.validator.GenericValidator;
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

    private static ClassNodeManager instance;

    public static synchronized ClassNodeManager getInstance() throws InfrastructureException {
        if (instance == null) {
            instance = new ClassNodeManager();
        }

        return instance;
    }

    public static synchronized boolean initialized() {
        if (instance == null) {
            return false;
        }

        return true;
    }

    public synchronized String seekECLA(String code) throws IOException {
        String s = ecla.get(code);
        s = Entity.replaceLatinChars(s);
        return s;
    }

    public String seekIPC(String code, int lookupflag) throws IOException, InfrastructureException {

        if (lookupflag == 1) {
            this.lookupFlag = 1;
        }
        String s = seekIPC(code);
        this.lookupFlag = 2;
        return s;
    }

    public synchronized String seekIPC(String code) throws IOException, InfrastructureException {

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
        String s = uspto.get(code);
        return s;
    }

    public synchronized void close() {
        try {
            uspto.close();
            log4j.warn("Closed USPTO index...");
        } catch (Exception e) {
            log4j.error(e);
        }

        try {
            ecla.close();
            log4j.warn("Closed ECLA index...");
        } catch (Exception e) {
            log4j.error(e);
        }

        try {
            ipc.close();
            log4j.warn("Closed IPC index...");
        } catch (Exception e) {
            log4j.error(e);
        }

    }

    private ClassNodeManager() throws InfrastructureException {
        openUSPTO();
        openIPC();
        openECLA();
    }

    private void openUSPTO() throws InfrastructureException {
        try {
            String usptodir = ApplicationProperties.getInstance().getProperty(ApplicationProperties.USPTO_LUCENE_INDEX_DIR);
            if (GenericValidator.isBlankOrNull(usptodir)) {
                throw new IllegalArgumentException("USPTO directory for lucene index is NOT defined!");
            }
            log4j.warn("Opening USPTO index at: '" + usptodir + "'");
            uspto = new DiskMap();
            uspto.openRead(usptodir, false);
        } catch (IOException e) {
            throw new InfrastructureException(SystemErrorCodes.CLASSNODEMANAGER_ERROR, e);
        }
    }

    private void openIPC() throws InfrastructureException {
        try {
            String ipcdir = ApplicationProperties.getInstance().getProperty(ApplicationProperties.IPC_LUCENE_INDEX_DIR);
            if (GenericValidator.isBlankOrNull(ipcdir)) {
                throw new IllegalArgumentException("IPC directory for lucene index is NOT defined!");
            }
            log4j.warn("Opening IPC index at: '" + ipcdir + "'");
            ipc = new DiskMap();
            ipc.openRead(ipcdir, false);
        } catch (IOException e) {
            throw new InfrastructureException(SystemErrorCodes.CLASSNODEMANAGER_ERROR, e);
        }
    }

    private void openECLA() throws InfrastructureException {
        try {
            String ecladir = ApplicationProperties.getInstance().getProperty(ApplicationProperties.ECLA_LUCENE_INDEX_DIR);
            if (GenericValidator.isBlankOrNull(ecladir)) {
                throw new IllegalArgumentException("ECLA directory for lucene index is NOT defined!");
            }
            log4j.warn("Opening ECLA index at: '" + ecladir + "'");
            ecla = new DiskMap();
            ecla.openRead(ecladir, false);
        } catch (IOException e) {
            throw new InfrastructureException(SystemErrorCodes.CLASSNODEMANAGER_ERROR, e);
        }

    }
}