package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class FitSequence extends PGobject {

    private String scopId;
    private String seq;
    private String weights;
    private int len;
    private int missingLen;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getWeights() {
        return weights;
    }

    public void setWeights(String weights) {
        this.weights = weights;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getMissingLen() {
        return missingLen;
    }

    public void setMissingLen(int missingLen) {
        this.missingLen = missingLen;
    }

    @Override
    public String getValue() {
        String row = "(" + scopId + "," + seq + "," + weights + "," + len + "," + missingLen + ")"; 
        return row;
    }
}

