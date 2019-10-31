package edu.umkc.dl.ndd;

import java.util.HashMap;
import java.util.Map;

public class Graming {

    private final static String[] keys = new String[] { 
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private final static Map<String,Integer> map = new HashMap<>();

    static {

        // vals range from 1 to 26
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], i+1);
        }
    }

    public static Integer[] parseGrams(String seq) {

        String[] codes = seq.toUpperCase().split(",");
        Integer[] grams = new Integer[codes.length - Constants.GRAM_LEN + 1];

        // iterate residue codes
        for (int i = 0; i <= codes.length - Constants.GRAM_LEN; i++) {

            int gram = 0;
            for (int j = 0; j < Constants.GRAM_LEN; j++) {
                 
                gram += map.get(codes[i]) * Math.pow(Constants.HASH_BASE, j);
            }
            grams[i] = gram;
        }

        return grams;
    }
}
