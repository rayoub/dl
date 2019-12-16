package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Residue extends PGobject {

    public static double NULL_VAL = -9999.0;

    private String scopId;
    private String pdbId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private String residueCode;
    private double maxTf;
    private String ssa;
    private String sse;
    private String descriptor;
    private double phi = NULL_VAL;
    private double psi = NULL_VAL;
    private double phiX = NULL_VAL;
    private double phiY = NULL_VAL;
    private double psiX = NULL_VAL;
    private double psiY = NULL_VAL;

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
    
    public String getResidueCode() {
        return residueCode;
    }

    public void setResidueCode(String residueCode) {
        this.residueCode = residueCode;
    }

    public double getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(double maxTf) {
        this.maxTf = maxTf;
    }

    public String getSsa() {
        return ssa;
    }

    public void setSsa(String ssa) {
        this.ssa = ssa;
    }

    public String getSse() {
        return sse;
    }

    public void setSse(String sse) {
        this.sse = sse;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
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

    public double getPhiX() {
        return phiX;
    }

    public void setPhiX(double phiX) {
        this.phiX = phiX;
    }

    public double getPhiY() {
        return phiY;
    }

    public void setPhiY(double phiY) {
        this.phiY = phiY;
    }

    public double getPsiX() {
        return psiX;
    }

    public void setPsiX(double psiX) {
        this.psiX = psiX;
    }

    public double getPsiY() {
        return psiY;
    }

    public void setPsiY(double psiY) {
        this.psiY = psiY;
    }

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," + residueCode + "," 
            + maxTf + ","
            + ssa + "," + sse + "," + descriptor + ","

            + ((phi == NULL_VAL) ? "" : phi) + "," 
            + ((psi == NULL_VAL) ? "" : psi) + "," 
            
            + ((phiX == NULL_VAL) ? "" : phiX) + "," 
            + ((phiY == NULL_VAL) ? "" : phiY) + "," 
            + ((psiX == NULL_VAL) ? "" : psiX) + "," 
            + ((psiY == NULL_VAL) ? "" : psiY) 

            + ")";
        return row;
    }
}

