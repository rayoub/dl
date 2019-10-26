package edu.umkc.dl.lib;

public enum FitSequenceType {

    PP("pp", 2),
    SPL("spl", 3),
    SPR("spr", 3),
    CI("ci", 4),
    CIL("cil", 3),
    CIR("cir", 3);
    
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
