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

public class SetSsSequences {

    public static void set() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> setSplit(splitIndex));
    }

    private static void setSplit(int splitIndex) {

        int processed = 0;
        List<SsSequence> sequences = new ArrayList<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {

            Connection conn = ds.getConnection();
            conn.setAutoCommit(true);

            // *** get split

            System.out.println("Split: " + splitIndex + ", Getting Ids to Process.");
                
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_split(?,?);");
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

                    List<String> assignments = calculateForScopId(scopId, coords1, coords2, sequenceText, mapText);

                    String text = assignments.stream().collect(Collectors.joining("\\,"));
                    int len = assignments.size();
                    int missingLen = (int)assignments.stream().filter(a -> a.equals("_")).count();

                    if (assignments.size() > 0 && len > missingLen) {

                        SsSequence sequence = new SsSequence();
                        sequence.setScopId(scopId);
                        sequence.setText(text);
                        sequence.setLen(len);
                        sequence.setMissingLen(missingLen);

                        sequences.add(sequence);
                    }

                } catch (Exception e) {
                    Logger.getLogger(SetSsSequences.class.getName()).log(Level.SEVERE, scopId, e);
                }
                
                // output
                processed++;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    saveSsSequences(sequences);
                    sequences.clear();
                    System.out.println("Split: " + splitIndex + ", Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }
            }

            if (sequences.size() > 0) {
                saveSsSequences(sequences);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(SetSsSequences.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static List<String> calculateForScopId(
            String scopId, 
            Parsing.ResidueCoords coords1, 
            Parsing.ResidueCoords coords2, 
            String sequenceText, 
            String mapText) {

        List<String> seq = new ArrayList<>();

        List<String> codes = Arrays.asList(sequenceText.toUpperCase().split(","));
        List<Parsing.MapCoords> maps = Arrays.asList(mapText.split(",")).stream().map(m -> Parsing.parseMapCoords(m)).collect(Collectors.toList());
        List<Residue> residues = Db.getResidues(scopId);

        // we have a more inclusive definition of missing than ASTRAL - no CA atom rather than no atoms at all
        // also assigns start and end coords when null - i.e. for chain domains 
        Parsing.identifyMissingResidues(coords1, coords2, maps, residues);
        
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
            return seq;
        }
        
        // residue index
        int k = 0;
        
        // *** iterate lists
        
        for (i = 0; i < codes.size(); i++) {

            String code = codes.get(i);
            Parsing.MapCoords map = maps.get(j);
            Residue residue = null;
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

            // assignments
            if (check) {

                if (!map.Code1.equals(".")) {
                    // this may already be assigned _ 
                    // we still count as a present residue in that case
                    seq.add(residue.getSsa());
                    k++;
                }
                else {
                    seq.add("_");
                }
                j++;
            }
            else {

                seq.clear();
                break;
            }
        } 

        return seq;
    }

    private static void printlnError(String message) {

        System.out.println((char)27 + "[31m" + message + (char)27 + "[0m");
    }
    
    private static void saveSsSequences(List<SsSequence> sequences) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType("ss_sequence", SsSequence.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_ss_sequences(?);");
    
        SsSequence a[] = new SsSequence[sequences.size()];
        sequences.toArray(a);
        updt.setArray(1, conn.createArrayOf("ss_sequence", a));
    
        updt.execute();
        updt.close();
    }
}
