package edu.umkc.dl.lib;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {
    
    public final static Pattern ALPHA_PATTERN = Pattern.compile("[A-Za-z]+");

    public static class ResidueCoords {

        public int ResidueNumber = Integer.MIN_VALUE; // initialized to missing residue
        public String InsertCode = "";
    }

    public static class MapCoords { 

        public int ResidueNumber;
        public String InsertCode;
        public String Code1;
        public String Code2;
    }

    public static ResidueCoords parseResidueCoords(String text, boolean negate) {

        ResidueCoords coords = new ResidueCoords();

        Matcher matcher = ALPHA_PATTERN.matcher(text);
        if (matcher.find()) {
            coords.ResidueNumber = Integer.parseInt(text.substring(0,matcher.start()));
            coords.InsertCode = text.substring(matcher.start()).toUpperCase();
        }
        else {
            coords.ResidueNumber = Integer.parseInt(text);
        }

        // the negative is striped off during import of aa sequences
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
            // a missing residue is indicated by blank residue number in the RAF
            mapCoords.ResidueNumber = Integer.MIN_VALUE;
        }
      
        // a missing residue is indicated by a '.' for the first code in the RAF 
        mapCoords.InsertCode = text.substring(4,5).trim(); 
        mapCoords.Code1 = text.substring(5,6);
        mapCoords.Code2 = text.substring(6,7);

        return mapCoords;
    }

    public static void identifyMissingResidues(
            Parsing.ResidueCoords coords1, 
            Parsing.ResidueCoords coords2,
            List<Parsing.MapCoords> maps, 
            List<Residue> residues)
    {
        // map indices
        int j = 0, jEnd = maps.size() - 1;

        // determine start and end coords if not present by skipping ASTRAL empties
        // (also adjust indices)
        if (coords1.ResidueNumber == Integer.MIN_VALUE) {
            while (maps.get(j).ResidueNumber == Integer.MIN_VALUE) {
                j++;
            }
            coords1.ResidueNumber = maps.get(j).ResidueNumber;
            coords1.InsertCode = maps.get(j).InsertCode;
        }
        if (coords2.ResidueNumber == Integer.MIN_VALUE) {
            while (maps.get(jEnd).ResidueNumber == Integer.MIN_VALUE) {
                jEnd--;
            }
            coords2.ResidueNumber = maps.get(jEnd).ResidueNumber;
            coords2.InsertCode = maps.get(jEnd).InsertCode;
        }

        // iterate maps
        while (j <= jEnd) {

            Parsing.MapCoords map = maps.get(j);

            if (withinRange(coords1, coords2, map)) {
                
                if (!residues.stream().anyMatch(r -> r.getResidueNumber() == map.ResidueNumber && r.getInsertCode().equals(map.InsertCode))) {
                
                    // this is contrary to ASTRAL definition of missing which is no atoms present (B|M|E)
                    // the period indicates the residue is missing
                    // if it is of our more inclusive missing a residue number will be present
                    // we maintain the residue number for iterating
                    map.Code1 = ".";
                }
            }

            j++;
        }
    }

    public static boolean withinRange(Parsing.ResidueCoords coords1, Parsing.ResidueCoords coords2, Parsing.MapCoords map) {

        if (
                (map.ResidueNumber > coords1.ResidueNumber ||
                (map.ResidueNumber == coords1.ResidueNumber && map.InsertCode.compareTo(coords1.InsertCode) >= 0))
                &&
                (map.ResidueNumber < coords2.ResidueNumber ||
                (map.ResidueNumber == coords2.ResidueNumber && map.InsertCode.compareTo(coords2.InsertCode) <= 0))
        )
            return true;
        else
            return false;
    }
}

