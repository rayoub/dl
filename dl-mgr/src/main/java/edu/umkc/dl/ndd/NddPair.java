package edu.umkc.dl.ndd;

import org.postgresql.util.PGobject;
import org.postgresql.util.PGtokenizer;

public class NddPair extends PGobject {

    private String dbId1;
    private String dbId2;
    private double similarity;

    public String getDbId1() {
        return dbId1;
    }

    public void setDbId1(String dbId1) {
        this.dbId1 = dbId1;
    }

    public String getDbId2() {
        return dbId2;
    }

    public void setDbId2(String dbId2) {
        this.dbId2 = dbId2;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public void setValue(String s) {

        //remove parens and tokenize
        PGtokenizer t = new PGtokenizer(PGtokenizer.removePara(s), ',');

        dbId1 = t.getToken(1); 
        dbId2 = t.getToken(2);
        similarity = Double.parseDouble(t.getToken(3));
    }

    @Override
    public String getValue() {
        return "(" + dbId1 + "," + dbId2 + "," + similarity + ")";
    }
}
