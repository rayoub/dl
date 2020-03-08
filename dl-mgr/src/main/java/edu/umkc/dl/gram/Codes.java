package edu.umkc.dl.gram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Codes {

    public static Set<String> ValidCodes = new HashSet<>();
    public static Map<String, String> CodeMap = new HashMap<>();

    static {

        ValidCodes.add("A");
        ValidCodes.add("C");
        ValidCodes.add("D");
        ValidCodes.add("E");
        ValidCodes.add("F");
        ValidCodes.add("G");
        ValidCodes.add("H");
        ValidCodes.add("I");
        ValidCodes.add("K");
        ValidCodes.add("L");
        ValidCodes.add("M");
        ValidCodes.add("N");
        ValidCodes.add("P");
        ValidCodes.add("Q");
        ValidCodes.add("R");
        ValidCodes.add("S");
        ValidCodes.add("T");
        ValidCodes.add("V");
        ValidCodes.add("W");
        ValidCodes.add("Y");
       
        CodeMap.put("A","H");
        CodeMap.put("C","U");
        CodeMap.put("D","C");
        CodeMap.put("E","C");
        CodeMap.put("F","H");
        CodeMap.put("G","G");
        CodeMap.put("H","C");
        CodeMap.put("I","H");
        CodeMap.put("K","C");
        CodeMap.put("L","H");
        CodeMap.put("M","H");
        CodeMap.put("N","U");
        CodeMap.put("P","P");
        CodeMap.put("Q","U");
        CodeMap.put("R","C");
        CodeMap.put("S","U");
        CodeMap.put("T","U");
        CodeMap.put("V","H");
        CodeMap.put("W","H");
        CodeMap.put("Y","H");
    }

}
