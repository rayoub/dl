package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class AaSequence extends PGobject {

    private String scopId;
    private String pdbId;
    private String cl;
    private String cf;
    private String sf;
    private String fa;
    private String chain;
    private int residueNumber1;
    private String insertCode1;
    private int residueNumber2;
    private String insertCode2;
    private String text;
    private int len;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
    }

    public String getPdbId() {
        return pdbId;
    }

    public void setPdbId(String pdbId) {
        this.pdbId = pdbId;
    }

    public String getCl() {
        return cl;
    }

    public void setCl(String cl) {
        this.cl = cl;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getSf() {
        return sf;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getFa() {
        return fa;
    }

    public void setFa(String fa) {
        this.fa = fa;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public int getResidueNumber1() {
        return residueNumber1;
    }

    public void setResidueNumber1(int residueNumber1) {
        this.residueNumber1 = residueNumber1;
    }

    public String getInsertCode1() {
        return insertCode1;
    }

    public void setInsertCode1(String insertCode1) {
        this.insertCode1 = insertCode1;
    }

    public int getResidueNumber2() {
        return residueNumber2;
    }

    public void setResidueNumber2(int residueNumber2) {
        this.residueNumber2 = residueNumber2;
    }

    public String getInsertCode2() {
        return insertCode2;
    }

    public void setInsertCode2(String insertCode2) {
        this.insertCode2 = insertCode2;
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

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + ","
            + cl + "," + cf + "," + sf + "," + fa + ","
            + chain + "," 
            + (residueNumber1 == Integer.MIN_VALUE ? "" : residueNumber1) + "," 
            + (insertCode1 == null || insertCode1.toLowerCase().equals("null") ? "" : insertCode1) + ","
            + (residueNumber2 == Integer.MIN_VALUE ? "" : residueNumber2) + "," 
            + (insertCode2 == null || insertCode2.toLowerCase().equals("null") ? "" : insertCode2) + ","
            + text + "," + len
            + ")";
        return row;
    }
}

