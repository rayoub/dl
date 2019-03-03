package edu.umkc.dl.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {
    
    public final static Pattern ALPHA_PATTERN = Pattern.compile("[A-Za-z]+");

    public static class ResidueCoords {

        public int ResidueNumber;
        public String InsertCode;
    }

    public static ResidueCoords parseResidueCoords(String text, boolean negate) {

        ResidueCoords coords = new ResidueCoords();

        coords.ResidueNumber = Integer.MIN_VALUE;
        coords.InsertCode = "";

        Matcher matcher = ALPHA_PATTERN.matcher(text);
        if (matcher.find()) {
            coords.ResidueNumber = Integer.parseInt(text.substring(0,matcher.start()));
            coords.InsertCode = text.substring(matcher.start()).toUpperCase();
        }
        else {
            coords.ResidueNumber = Integer.parseInt(text);
        }

        if (negate) {
            coords.ResidueNumber = -coords.ResidueNumber;
        }

        return coords;
    }
}

