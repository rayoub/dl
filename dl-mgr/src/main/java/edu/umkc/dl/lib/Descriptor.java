package edu.umkc.dl.lib;

public class Descriptor {

    public static int calculateRegion(double phi, double psi, String sse) {

        // helix 0, 1, 2, 3
        if (sse.equals("Helix")) {
            return calculateHelixRegion(phi, psi);
        }

        // strand 4, 5, 6
        else if (sse.equals("Strand")) {
            return calculateStrandRegion(phi, psi);
        }
     
        // coil 7, 8, 9
        else { 
            return calculateCoilRegion(phi, psi);
        }
    }

    public static int calculateHelixRegion(double phi, double psi) {

        int region = -1;

        if (psi >= -180 && psi < -135) {
            if (phi >= 0 && phi < 180) {
                region = 3;
            }
            else {
                region = 0; 
            }
        }
        else if (psi >= -135 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                region = 3;
            }
            else {
                region = 2;
            }
        }
        else if (psi >= -90 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = 1;
            }
            else {
                region = 2;
            }
        }
        else if (psi >= 90 && psi < 180) {
            if (phi >= 0 && phi < 180) {
                region = 3;
            }
            else {
                region = 0;
            }
        }
        
        return region;
    }

    public static int calculateStrandRegion(double phi, double psi) {

        int region = -1;

        if (psi >= -180 && psi < -100) {
            region = 4;
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                region = 4;
            }
            else {
                region = 6;
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                region = 5;
            }
            else {
                region = 6;
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = 5;
            }
            else {
                region = 4;
            }
        }
        else if (psi >= 90 && psi < 180) {
            region = 4;
        }

        return region;
    }
    
    public static int calculateCoilRegion(double phi, double psi) {

        int region = -1;

        if (psi >= -180 && psi < -100) {
            region = 7;
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                region = 7;
            }
            else {
                region = 9;
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                region = 8;
            }
            else {
                region = 9;
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = 8;
            }
            else {
                region = 7;
            }
        }
        else if (psi >= 90 && psi < 180) {
            region = 7;
        }

        return region;
    }
}

