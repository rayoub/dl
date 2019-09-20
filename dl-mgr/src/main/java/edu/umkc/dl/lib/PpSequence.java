package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class PpSequence extends PGobject {

    private String scopId;
    private String text;
    private int len;
    private int missingLen;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        String row = "(" + scopId + "," + text + "," + len + "," + missingLen + ")"; 
        return row;
    }
}

