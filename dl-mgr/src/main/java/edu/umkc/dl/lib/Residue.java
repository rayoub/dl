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
    private String ssa;
    private double phi = NULL_VAL;
    private double psi = NULL_VAL;
    private double phiX = NULL_VAL;
    private double phiY = NULL_VAL;
    private double psiX = NULL_VAL;
    private double psiY = NULL_VAL;
    private double philX = NULL_VAL;
    private double phirX = NULL_VAL;
    private double splX = NULL_VAL;
    private double splY = NULL_VAL;
    private double splZ = NULL_VAL;
    private double sprX = NULL_VAL;
    private double sprY = NULL_VAL;
    private double sprZ = NULL_VAL;

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

    public String getSsa() {
        return ssa;
    }

    public void setSsa(String ssa) {
        this.ssa = ssa;
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
    
    public double getPhilX() {
        return philX;
    }

    public void setPhilX(double philX) {
        this.philX = philX;
    }

    public double getPhirX() {
        return phirX;
    }

    public void setPhirX(double phirX) {
        this.phirX = phirX;
    }

    public double getSplX() {
        return splX;
    }

    public void setSplX(double splX) {
        this.splX = splX;
    }

    public double getSplY() {
        return splY;
    }

    public void setSplY(double splY) {
        this.splY = splY;
    }

    public double getSplZ() {
        return splZ;
    }

    public void setSplZ(double splZ) {
        this.splZ = splZ;
    }

    public double getSprX() {
        return sprX;
    }

    public void setSprX(double sprX) {
        this.sprX = sprX;
    }

    public double getSprY() {
        return sprY;
    }

    public void setSprY(double sprY) {
        this.sprY = sprY;
    }

    public double getSprZ() {
        return sprZ;
    }

    public void setSprZ(double sprZ) {
        this.sprZ = sprZ;
    }

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," + residueCode + "," 
            + ssa + "," 
            + ((phi == NULL_VAL) ? "" : phi) + "," 
            + ((psi == NULL_VAL) ? "" : psi) + "," 
            
            + ((phiX == NULL_VAL) ? "" : phiX) + "," 
            + ((phiY == NULL_VAL) ? "" : phiY) + "," 
            + ((psiX == NULL_VAL) ? "" : psiX) + "," 
            + ((psiY == NULL_VAL) ? "" : psiY) + "," 

            + ((philX == NULL_VAL) ? "" : philX) + "," 
            + ((phirX == NULL_VAL) ? "" : phirX) + "," 

            + ((splX == NULL_VAL) ? "" : splX) + "," 
            + ((splY == NULL_VAL) ? "" : splY) + "," 
            + ((splZ == NULL_VAL) ? "" : splZ) + ","

            + ((sprX == NULL_VAL) ? "" : sprX) + "," 
            + ((sprY == NULL_VAL) ? "" : sprY) + "," 
            + ((sprZ == NULL_VAL) ? "" : sprZ) 
            + ")";
        return row;
    }
}

