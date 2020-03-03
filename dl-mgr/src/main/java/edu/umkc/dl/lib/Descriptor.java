package edu.umkc.dl.lib;

public class Descriptor {

    public static String toSs(int descr) {

        if (descr <= 3) {
            return "H";
        }
        else if (descr <= 6) {
            return "S";
        }
        else {
            return "C";
        }
    }
    
    public static int toSsIndex(int descr) {

        if (descr <= 3) {
            return 0;
        }
        else if (descr <= 6) {
            return 1;
        }
        else {
            return 2;
        }
    }

    public static int toDescriptor(double phi, double psi, String sse) {

        // helix 0, 1, 2, 3
        if (sse.equals("H")) {
            return toHelixDescriptor(phi, psi);
        }

        // strand 4, 5, 6
        else if (sse.equals("S")) {
            return toStrandDescriptor(phi, psi);
        }
     
        // coil 7, 8, 9
        else { 
            return toCoilDescriptor(phi, psi);
        }
    }

    public static int toHelixDescriptor(double phi, double psi) {

        int descr = -1;

        if (psi >= -180 && psi < -135) {
            if (phi >= 0 && phi < 180) {
                descr = 3;
            }
            else {
                descr = 0; 
            }
        }
        else if (psi >= -135 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = 3;
            }
            else {
                descr = 2;
            }
        }
        else if (psi >= -90 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = 1;
            }
            else {
                descr = 2;
            }
        }
        else if (psi >= 90 && psi < 180) {
            if (phi >= 0 && phi < 180) {
                descr = 3;
            }
            else {
                descr = 0;
            }
        }
        
        return descr;
    }

    public static int toStrandDescriptor(double phi, double psi) {

        int descr = -1;

        if (psi >= -180 && psi < -100) {
            descr = 4;
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = 4;
            }
            else {
                descr = 6;
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                descr = 5;
            }
            else {
                descr = 6;
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = 5;
            }
            else {
                descr = 4;
            }
        }
        else if (psi >= 90 && psi < 180) {
            descr = 4;
        }

        return descr;
    }
    
    public static int toCoilDescriptor(double phi, double psi) {

        int descr = -1;

        if (psi >= -180 && psi < -100) {
            descr = 7;
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = 7;
            }
            else {
                descr = 9;
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                descr = 8;
            }
            else {
                descr = 9;
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = 8;
            }
            else {
                descr = 7;
            }
        }
        else if (psi >= 90 && psi < 180) {
            descr = 7;
        }

        return descr;
    }
}

