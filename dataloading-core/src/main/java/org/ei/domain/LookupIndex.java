package org.ei.domain;

import java.util.ArrayList;
import java.util.List;

import org.ei.exception.InfrastructureException;
import org.ei.util.GetPIDDescription;

/**
 * Simple POJO for LookupIndex objects
 * @author harovetm
 *
 */
public class LookupIndex {
    private List<String> databases = new ArrayList<String>();
    private String name;
    private String value;
    private boolean dynamic;
    private List<String> sequence = new ArrayList<String>();

    public String getPidDescription() throws InfrastructureException {
        return GetPIDDescription.getDescription(name);
    }

    public List<String> getDatabases() {
        return this.databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public void addDatabase(String database) {
        this.databases.add(database);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getSequence() {
        return sequence;
    }

    public void setSequence(List<String> sequence) {
        this.sequence = sequence;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
