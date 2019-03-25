package edu.umkc.dl.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

public class CalculateDescriptors {

    public static void calculate() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> calculateSplit(splitIndex));
    }

    private static void calculateSplit(int splitIndex) {

        int processed = 0;
        List<SequenceDescriptors> seqDescrs = new ArrayList<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {

            Connection conn = ds.getConnection();
            conn.setAutoCommit(true);

            // *** get split

            System.out.println("Split: " + splitIndex + ", Getting Ids to Process.");
                
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_sequence_split(?,?);");
            stmt.setInt(1, splitIndex);
            stmt.setInt(2, Constants.SPLIT_COUNT);
            
            ResultSet rs = stmt.executeQuery();

            System.out.println("Split: " + splitIndex + ", Got Ids.");

            // *** iterate split
            
            while (rs.next()) {
               
                String scopId = "";

                Parsing.ResidueCoords coords1 = new Parsing.ResidueCoords();
                Parsing.ResidueCoords coords2 = new Parsing.ResidueCoords();

                String sequenceText = "";
                String mapText = "";

                try {
               
                    scopId = rs.getString("scop_id").toLowerCase();

                    coords1.ResidueNumber = rs.getInt("residue_number_1");
                    if (rs.wasNull()) 
                        coords1.ResidueNumber = Integer.MIN_VALUE;
                    coords1.InsertCode = rs.getString("insert_code_1");
                    if (rs.wasNull())
                        coords1.InsertCode = "";
                    coords1.InsertCode = coords1.InsertCode.toUpperCase();
                    
                    coords2.ResidueNumber = rs.getInt("residue_number_2");
                    if (rs.wasNull()) 
                        coords2.ResidueNumber = Integer.MIN_VALUE;
                    coords2.InsertCode = rs.getString("insert_code_2");
                    if (rs.wasNull())
                        coords2.InsertCode = "";
                    coords2.InsertCode = coords2.InsertCode.toUpperCase();

                    sequenceText = rs.getString("sequence_text");
                    mapText = rs.getString("map_text");

                    String text = calculateForScopId(scopId, coords1, coords2, sequenceText, mapText);

                    if (!text.isEmpty()) {

                        SequenceDescriptors seqDescr = new SequenceDescriptors();
                        seqDescr.setScopId(scopId);
                        seqDescr.setText(text);

                        seqDescrs.add(seqDescr);
                    }

                } catch (Exception e) {
                    Logger.getLogger(CalculateDescriptors.class.getName()).log(Level.SEVERE, scopId, e);
                }
                
                // output
                processed++;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    saveDescriptors(seqDescrs);
                    seqDescrs.clear();
                    System.out.println("Split: " + splitIndex + ", Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }
            }

            if (seqDescrs.size() > 0) {
                saveDescriptors(seqDescrs);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(CalculateDescriptors.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static String calculateForScopId(String scopId, Parsing.ResidueCoords coords1, Parsing.ResidueCoords coords2, String sequenceText, String mapText) {

        List<String> codes = Arrays.asList(sequenceText.toUpperCase().split(","));
        List<Parsing.MapCoords> maps = Arrays.asList(mapText.split(",")).stream().map(m -> Parsing.parseMapCoords(m)).collect(Collectors.toList());
        List<ResidueDescriptor> residues = Db.getResidueDescriptors(scopId);

        // we have a more inclusive definition of missing than ASTRAL - no CA atom rather than no atoms at all
        // also assigns start and end coords when null - chain domains 
        identifyMissingResidues(coords1, coords2, maps, residues);
        
        // *** starting indices

        // sequence index
        int i = 0;

        // map index
        int j = 0;
        while (j < maps.size() && !(maps.get(j).ResidueNumber == coords1.ResidueNumber && maps.get(j).InsertCode.equals(coords1.InsertCode))) {
            j++;
        }
        if (j == maps.size()) {
            printlnError(scopId + ": missing map at start coords (" + coords1.ResidueNumber + "," + coords1.InsertCode + ")");
            return "";
        }
        
        // residue index
        int k = 0;
        
        // *** iterate lists
       
        List<String> descriptors = new ArrayList<>();
        for (i = 0; i < codes.size(); i++) {

            String code = codes.get(i);
            Parsing.MapCoords map = maps.get(j);
            ResidueDescriptor residue = null;
            if (k < residues.size()) {
                residue = residues.get(k);    
            }
            
            // check assumptions
            boolean check = true;
            if (code.equals(map.Code2)) {

                if (!map.Code1.equals(".")) {
                    if (residue == null) {

                        check = false;
                        printlnError(scopId + ": no more residues at map coords (" + map.ResidueNumber + "," + map.InsertCode + ")");
                    }
                    else if (!(residue.getResidueNumber() == map.ResidueNumber && residue.getInsertCode().equals(map.InsertCode))) {

                        check = false;
                        printlnError(scopId + ": missing residue at map coords (" + map.ResidueNumber + "," + map.InsertCode + ")");
                    }
                }
            }
            else {

                check = false;
                printlnError(scopId + ": code mismatch at map coords (" + map.ResidueNumber + "," + map.InsertCode + ")");
            }

            // assign descriptors
            if (check) {

                if (!map.Code1.equals(".")) {
                    descriptors.add(residue.getDescriptor());
                    k++;
                }
                else {
                    descriptors.add("_");
                }
                j++;
            }
            else {

                descriptors.clear();
                break;
            }
        } 

        // check results
        String descriptorText = descriptors.stream().collect(Collectors.joining(","));
        if (!descriptorText.isEmpty() && descriptorText.length() != sequenceText.length()) {

            printlnError(scopId + ": size mismatch between sequence text and descriptor text");
            descriptorText = "";
        }
        else {

            descriptorText = descriptors.stream().collect(Collectors.joining("\\,"));
        }

        // output 
        if (!descriptorText.isEmpty()) {

            boolean debug = false;
            if (debug) {

                System.out.println(scopId);
                System.out.println(sequenceText);
                System.out.println(descriptorText);
            }
        }

        return descriptorText;
    }

    private static void identifyMissingResidues(
            Parsing.ResidueCoords coords1, 
            Parsing.ResidueCoords coords2,
            List<Parsing.MapCoords> maps, 
            List<ResidueDescriptor> residues)
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
                
                    // we maintain the residue number for iterating
                    // this is contrary to ASTRAL definition of missing which is no atoms present (B|M|E)
                    map.Code1 = ".";
                }
            }

            j++;
        }
    }

    private static boolean withinRange(Parsing.ResidueCoords coords1, Parsing.ResidueCoords coords2, Parsing.MapCoords map) {

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

    private static void printlnError(String message) {

        System.out.println((char)27 + "[31m" + message + (char)27 + "[0m");
    }
    
    private static void saveDescriptors(List<SequenceDescriptors> seqDescrs) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType("sequence_descriptors", SequenceDescriptors.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_sequence_descriptors(?);");
    
        SequenceDescriptors a[] = new SequenceDescriptors[seqDescrs.size()];
        seqDescrs.toArray(a);
        updt.setArray(1, conn.createArrayOf("sequence_descriptors", a));
    
        updt.execute();
        updt.close();
    }
}
