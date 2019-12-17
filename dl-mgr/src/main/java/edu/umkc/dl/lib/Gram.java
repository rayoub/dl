package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Gram extends PGobject {

    private String scopId;
    private String pdbId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private double phi;
    private double psi;
    private String descriptor;
    private double maxTf;
    private String residueCode1;
    private double maxTf1;
    private String ss81;
    private String ss31;
    private String residueCode2;
    private double maxTf2;
    private String ss82;
    private String ss32;
    private String residueCode3;
    private double maxTf3;
    private String ss83;
    private String ss33;

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

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
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

    public double getMaxTf1() {
        return maxTf1;
    }

    public void setMaxTf1(double maxTf1) {
        this.maxTf1 = maxTf1;
    }

    public String getSs81() {
        return ss81;
    }

    public void setSs81(String ss81) {
        this.ss81 = ss81;
    }

    public String getSs31() {
        return ss31;
    }

    public void setSs31(String ss31) {
        this.ss31 = ss31;
    }

    public String getResidueCode2() {
        return residueCode2;
    }

    public void setResidueCode2(String residueCode2) {
        this.residueCode2 = residueCode2;
    }

    public double getMaxTf2() {
        return maxTf2;
    }

   public void setMaxTf2(double maxTf2) {
        this.maxTf2 = maxTf2;
    }

   public String getSs82() {
        return ss82;
    }

   public void setSs82(String ss82) {
        this.ss82 = ss82;
    }

   public String getSs32() {
        return ss32;
    }

   public void setSs32(String ss32) {
        this.ss32 = ss32;
    }

    public String getResidueCode3() {
        return residueCode3;
    }

    public void setResidueCode3(String residueCode3) {
        this.residueCode3 = residueCode3;
    }

    public double getMaxTf3() {
        return maxTf3;
    }

   public void setMaxTf3(double maxTf3) {
        this.maxTf3 = maxTf3;
    }

   public String getSs83() {
        return ss83;
    }

    public void setSs83(String ss83) {
        this.ss83 = ss83;
    }

    public String getSs33() {
        return ss33;
    }

    public void setSs33(String ss33) {
        this.ss33 = ss33;
    }

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," 
            + phi + "," + psi + "," + descriptor + ","  
            + maxTf + ","

            + residueCode1 + "," 
            + maxTf1 + ","
            + ss81 + "," + ss31 + "," 
            
            + residueCode2 + "," 
            + maxTf2 + ","
            + ss82 + "," + ss32 + "," 
            
            + residueCode3 + "," 
            + maxTf3 + ","
            + ss83 + "," + ss33
            
            + ")";
        return row;
    }
}

