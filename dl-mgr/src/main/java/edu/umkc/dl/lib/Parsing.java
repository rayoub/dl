package edu.umkc.dl.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {
    
    public final static Pattern ALPHA_PATTERN = Pattern.compile("[A-Za-z]+");

    public static class ResidueCoords {

        public int ResidueNumber;
        public String InsertCode;
    }

    public static class MapCoords { 

        public int ResidueNumber;
        public String InsertCode;
        public String Code1;
        public String Code2;
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

    public static MapCoords parseMapCoords(String text) {

        MapCoords mapCoords = new MapCoords();
        
        String str = text.substring(0, 4).trim();
        try { 
            mapCoords.ResidueNumber = Integer.parseInt(str);    
        }
        catch (NumberFormatException e) {
            mapCoords.ResidueNumber = Integer.MIN_VALUE;
        }
       
        mapCoords.InsertCode = text.substring(4,5).trim(); 
        mapCoords.Code1 = text.substring(5,6);
        mapCoords.Code2 = text.substring(6,7);

        return mapCoords;
    }
}

