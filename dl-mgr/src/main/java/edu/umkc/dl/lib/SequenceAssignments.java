package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class SequenceAssignments extends PGobject {

    private String scopId;
    private String text;

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

    @Override
    public String getValue() {
        String row = "(" + scopId + "," + text + ")"; 
        return row;
    }
}

