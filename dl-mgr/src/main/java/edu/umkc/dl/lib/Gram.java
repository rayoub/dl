package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Gram extends PGobject {

    private String scopId;
    private String pdbId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private String residueCode1;
    private String residueCode2;
    private String residueCode3;
    private double maxTf;
    private double phi;
    private double psi;
    private String ss8;
    private String ss3;
    private String descriptor;

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

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getResidueNumber() {
        return residueNumber;
    }

    public void setResidueNumber(int residueNumber) {
        this.residueNumber = residueNumber;
    }

    public String getInsertCode() {
        return insertCode;
    }

    public void setInsertCode(String insertCode) {
        this.insertCode = insertCode;
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

    public String getResidueCode3() {
        return residueCode3;
    }

    public void setResidueCode3(String residueCode3) {
        this.residueCode3 = residueCode3;
    }

    public double getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(double maxTf) {
        this.maxTf = maxTf;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public double getPsi() {
        return psi;
    }

    public void setPsi(double psi) {
        this.psi = psi;
    }

    public String getSs8() {
        return ss8;
    }

    public void setSs8(String ss8) {
        this.ss8 = ss8;
    }

    public String getSs3() {
        return ss3;
    }

    public void setSs3(String ss3) {
        this.ss3 = ss3;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," 
            + residueCode1 + "," 
            + residueCode2 + "," 
            + residueCode3 + ","
            + maxTf + ","
            + phi + "," + psi + "," 
            + ss8 + "," + ss3 + "," + descriptor 
            + ")";
        return row;
    }
}

