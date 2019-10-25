package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Residue extends PGobject {

    public static double NULL_ANGLE = -9999.0;
    public static double NULL_COORD = -9999.0;

    private String scopId;
    private String pdbId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private String residueCode;
    private String ssa;
    private double phi = NULL_ANGLE;
    private double psi = NULL_ANGLE;
    private double phi_x = NULL_COORD;
    private double phi_y = NULL_COORD;
    private double psi_x = NULL_COORD;
    private double psi_y = NULL_COORD;
    private double spX = NULL_COORD;
    private double spY = NULL_COORD;
    private double spZ = NULL_COORD;
    private boolean breakBefore;
    private boolean breakAfter;

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
        return phi_x;
    }

    public void setPhiX(double phi_x) {
        this.phi_x = phi_x;
    }

    public double getPhiY() {
        return phi_y;
    }

    public void setPhiY(double phi_y) {
        this.phi_y = phi_y;
    }

    public double getPsiX() {
        return psi_x;
    }

    public void setPsiX(double psi_x) {
        this.psi_x = psi_x;
    }

    public double getPsiY() {
        return psi_y;
    }

    public void setPsiY(double psi_y) {
        this.psi_y = psi_y;
    }
    
    public double getSpX() {
        return spX;
    }

    public void setSpX(double spX) {
        this.spX = spX;
    }

    public double getSpY() {
        return spY;
    }

    public void setSpY(double spY) {
        this.spY = spY;
    }

    public double getSpZ() {
        return spZ;
    }

    public void setSpZ(double spZ) {
        this.spZ = spZ;
    }

    public boolean isBreakBefore() {
        return breakBefore;
    }

    public void setBreakBefore(boolean breakBefore) {
        this.breakBefore = breakBefore;
    }

    public boolean isBreakAfter() {
        return breakAfter;
    }

    public void setBreakAfter(boolean breakAfter) {
        this.breakAfter = breakAfter;
    }

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + "," + pdbId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," + residueCode + "," 
            + ssa + "," 
            + ((phi == NULL_ANGLE) ? "" : phi) + "," 
            + ((psi == NULL_ANGLE) ? "" : psi) + "," 
            + ((phi_x == NULL_COORD) ? "" : phi_x) + "," 
            + ((phi_y == NULL_COORD) ? "" : phi_y) + "," 
            + ((psi_x == NULL_COORD) ? "" : psi_x) + "," 
            + ((psi_y == NULL_COORD) ? "" : psi_y) + "," 
            + ((spX == NULL_COORD) ? "" : spX) + "," 
            + ((spY == NULL_COORD) ? "" : spY) + "," 
            + ((spZ == NULL_COORD) ? "" : spZ) 
            + ")";
        return row;
    }
}

