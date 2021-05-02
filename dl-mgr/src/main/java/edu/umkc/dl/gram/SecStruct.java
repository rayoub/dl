package edu.umkc.dl.gram;

import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.secstruc.SecStrucInfo;

public class SecStruct {

    public static String getSecStruct8(Group g) {

        String ss8 = "_";
        Object obj = g.getProperty(Group.SEC_STRUC);
        if (obj instanceof SecStrucInfo) {
           SecStrucInfo info = (SecStrucInfo)obj;
           ss8 = String.valueOf(info.getType().type).trim();
           if (ss8.isEmpty()) {
               ss8 = "C";
            }
        }
        return ss8;
    }
   
    public static String getSecStruct3(String ss8) {

        String ss3;
        switch(ss8) {
            case "G":
            case "H":
            case "I":
                ss3 = "H";
                break;
            case "E":
                ss3 = "S";
                break;
            case "B":
            case "T":
            case "S":
            case "C":
                ss3 = "C";
                break;
            default:
                ss3 = "_";
        }
        return ss3;
    }    
    
    public static String toSs(int descr) {

        if (descr <= 3) {
            return "H";
        }
        else if (descr <= 6) {
            return "H";
        }
        else {
            return "C";
        }
    }
}
