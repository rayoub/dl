package edu.umkc.dl.gram;

import org.postgresql.util.PGobject;

public class Pair extends PGobject {

    private String scopId;
    private String pdbId;
    private double maxTf;
    private String residueCode1;
    private String residueCode2;
    private String descriptor1;
    private String descriptor2;

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

    public double getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(double maxTf) {
        this.maxTf = maxTf;
    }

    public String getResidueCode1() {
        return residueCode1;
    }

    public void setResidueCode1(String residueCode1) {
        this.residueCode1 = residueCode1;
    }

    public String getResidueCode2() {
        return residueCode2;
    }

    public void setResidueCode2(String residueCode2) {
        this.residueCode2 = residueCode2;
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
        String row = "(" + scopId + "," + pdbId + "," + maxTf + "," 
            + residueCode1 + "," + residueCode2 + "," 
            + descriptor1 + "," + descriptor2 + ")";
        return row;
    }
}

