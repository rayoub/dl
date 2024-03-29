package edu.umkc.dl.gram;

import org.postgresql.util.PGobject;

public class Target extends PGobject {

    public static double NULL_VAL = -9999.0;

    private String targetId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private String residueCode;
    private double phi = NULL_VAL;
    private double psi = NULL_VAL;
    private String descriptor;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
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

    @Override
    public String getValue() {
        String row = "(" 
            + targetId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," + residueCode + "," 

            + ((phi == NULL_VAL) ? "" : phi) + "," 
            + ((psi == NULL_VAL) ? "" : psi) + "," 
           
            + descriptor  
            
            + ")";
        return row;
    }
}

