package edu.umkc.dl.lib;

public class Descriptor {

    public static String toDescriptor(double phi, double psi, String sse) {

        // helix 0, 1, 2, 3
        if (sse.equals("H")) {
            return toHelixDescriptor(phi, psi);
        }

        // strand 4, 5, 6
        else if (sse.equals("S")) {
            return toStrandDescriptor(phi, psi);
        }
     
        // coil 7, 8, 9
        else if (sse.equals("C")) { 
            return toCoilDescriptor(phi, psi);
        }
        else {
            return "_";
        }
    }

    public static String toHelixDescriptor(double phi, double psi) {

        String descr = "_";

        if (psi >= -180 && psi < -135) {
            if (phi >= 0 && phi < 180) {
                descr = "3";
            }
            else {
                descr = "0"; 
            }
        }
        else if (psi >= -135 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = "3";
            }
            else {
                descr = "2";
            }
        }
        else if (psi >= -90 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = "1";
            }
            else {
                descr = "2";
            }
        }
        else if (psi >= 90 && psi < 180) {
            if (phi >= 0 && phi < 180) {
                descr = "3";
            }
            else {
                descr = "0";
            }
        }
        
        return descr;
    }

    public static String toStrandDescriptor(double phi, double psi) {

        String descr = "_";

        if (psi >= -180 && psi < -100) {
            descr = "4";
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = "4";
            }
            else {
                descr = "6";
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                descr = "5";
            }
            else {
                descr = "6";
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = "5";
            }
            else {
                descr = "4";
            }
        }
        else if (psi >= 90 && psi < 180) {
            descr = "4";
        }

        return descr;
    }
    
    public static String toCoilDescriptor(double phi, double psi) {

        String descr = "_";

        if (psi >= -180 && psi < -100) {
            descr = "7";
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                descr = "7";
            }
            else {
                descr = "9";
            }
        }
        else if (psi >= -90 && psi < 40) {
            if (phi >= 0 && phi < 180) {
                descr = "8";
            }
            else {
                descr = "9";
            }
        }
        else if (psi >= 40 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                descr = "8";
            }
            else {
                descr = "7";
            }
        }
        else if (psi >= 90 && psi < 180) {
            descr = "7";
        }

        return descr;
    }
}

