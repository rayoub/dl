package edu.umkc.dl.lib;

import org.postgresql.util.PGobject;

public class Residue extends PGobject {

    private String scopId;
    private int residueNumber;
    private String insertCode;
    private String residueCode;
    private String ssa;
    private String sse;
    private double phi = 360.0;
    private double psi = 360.0;
    private String descriptor;
    private double caX;
    private double caY;
    private double caZ;
    private double cbX = -1;
    private double cbY = -1;
    private double cbZ = -1;

    public String getScopId() {
        return scopId;
    }

    public void setScopId(String scopId) {
        this.scopId = scopId;
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

    public String getSse() {
        return sse;
    }

    public void setSse(String sse) {
        this.sse = sse;
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

    @Override
    public String getValue() {
        String row = "(" 
            + scopId + ","
            + residueNumber + "," + (insertCode == null || insertCode.equals("null") ? "" : insertCode) + "," + residueCode + "," 
            + ssa + "," + sse + "," 
            + ((phi == 360.0) ? "" : phi) + "," 
            + ((psi == 360.0) ? "" : psi) + "," 
            + descriptor + ","
            + caX + "," + caY + "," + caZ + ","
            + ((cbX == -1) ? "" : cbX) + "," 
            + ((cbY == -1) ? "" : cbY) + "," 
            + ((cbZ == -1) ? "" : cbZ) 
            + ")";
        return row;
    }
}

