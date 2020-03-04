package edu.umkc.dl.gram;

import org.postgresql.util.PGobject;

public class Pair extends PGobject {

    private String scopId;
    private String descriptor1;
    private String descriptor2;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
    }
    
    public String getDescriptor1() {
        return descriptor1;
    }

    public void setDescriptor1(String descriptor1) {
        this.descriptor1 = descriptor1;
    }

    public String getDescriptor2() {
        return descriptor2;
    }

    public void setDescriptor2(String descriptor2) {
        this.descriptor2 = descriptor2;
    }

    @Override
    public String getValue() {
        String row = "(" + scopId + "," + descriptor1 + "," + descriptor2 + ")";
        return row;
    }
}

