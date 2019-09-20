package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Residue extends PGobject {

    public static double NULL_ANGLE = 360.0;
    public static double NULL_COORD = -9999.0;

    private String scopId;
    private int orderNumber;
    private int residueNumber;
    private String insertCode;
    private String residueCode;
    private String ssa;
    private double phi = NULL_ANGLE;
    private double psi = NULL_ANGLE;
    private String descriptor;
    private double caX;
    private double caY;
    private double caZ;
    private double cbX = NULL_COORD;
    private double cbY = NULL_COORD;
    private double cbZ = NULL_COORD;
    private double nX = NULL_COORD;
    private double nY = NULL_COORD;
    private double nZ = NULL_COORD;
    private double ckX = NULL_COORD;
    private double ckY = NULL_COORD;
    private double ckZ = NULL_COORD;
    private boolean breakBefore;
    private boolean breakAfter;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
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

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public double getCaX() {
        return caX;
    }

    public void setCaX(double caX) {
        this.caX = caX;
    }

    public double getCaY() {
        return caY;
    }

    public void setCaY(double caY) {
        this.caY = caY;
    }

    public double getCaZ() {
        return caZ;
    }

    public void setCaZ(double caZ) {
        this.caZ = caZ;
    }
    
    public double getCbX() {
        return cbX;
    }

    public void setCbX(double cbX) {
        this.cbX = cbX;
    }

    public double getCbY() {
        return cbY;
    }

    public void setCbY(double cbY) {
        this.cbY = cbY;
    }

    public double getCbZ() {
        return cbZ;
    }

    public void setCbZ(double cbZ) {
        this.cbZ = cbZ;
    }

    public double getNX() {
        return nX;
    }

    public void setNX(double nX) {
        this.nX = nX;
    }

    public double getNY() {
        return nY;
    }

    public void setNY(double nY) {
        this.nY = nY;
    }

    public double getNZ() {
        return nZ;
    }

    public void setNZ(double nZ) {
        this.nZ = nZ;
    }

    public double getCkX() {
        return ckX;
    }

    public void setCkX(double ckX) {
        this.ckX = ckX;
    }

    public double getCkY() {
        return ckY;
    }

    public void setCkY(double ckY) {
        this.ckY = ckY;
    }

    public double getCkZ() {
        return ckZ;
    }

    public void setCkZ(double ckZ) {
        this.ckZ = ckZ;
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
            + scopId + "," + orderNumber + "," 
            + residueNumber + "," + (insertCode == null || insertCode.toLowerCase().equals("null") ? "" : insertCode) + "," + residueCode + "," 
            + ssa + "," 
            + ((phi == 360.0) ? "" : phi) + "," 
            + ((psi == 360.0) ? "" : psi) + "," 
            + descriptor + ","
            + caX + "," + caY + "," + caZ + ","
            + ((cbX == NULL_COORD) ? "" : cbX) + "," 
            + ((cbY == NULL_COORD) ? "" : cbY) + "," 
            + ((cbZ == NULL_COORD) ? "" : cbZ) + ","
            + ((ckX == NULL_COORD) ? "" : ckX) + "," 
            + ((ckY == NULL_COORD) ? "" : ckY) + "," 
            + ((ckZ == NULL_COORD) ? "" : ckZ) 
            + ")";
        return row;
    }
}

