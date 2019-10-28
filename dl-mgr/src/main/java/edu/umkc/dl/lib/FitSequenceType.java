package edu.umkc.dl.lib;

public enum FitSequenceType {

    PP("pp", 2),
    CI("ci", 4);
    
    private String dbName;
    private int modBy;

    FitSequenceType(String dbName, int modBy) {
        this.dbName = dbName;
        this.modBy = modBy;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getModBy() {
        return modBy;
    }

    public void setModBy(int modBy) {
        this.modBy = modBy;
    }
}
