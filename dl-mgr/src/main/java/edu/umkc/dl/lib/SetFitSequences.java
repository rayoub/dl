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

public class SetFitSequences {

    public static void set(FitSequenceType type) {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> setSplit(type, splitIndex));
    }

    private static void setSplit(FitSequenceType fitType, int splitIndex) {

        int processed = 0;
        List<FitSequence> sequences = new ArrayList<>();

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

                    List<Double> coordinates = calculateForScopId(fitType, scopId, coords1, coords2, sequenceText, mapText);

                    int len = coordinates.size();
                    if (len > 0) {

                        int missingLen = 0;
                        StringBuilder seqB = new StringBuilder();
                        StringBuilder weightsB = new StringBuilder();
                        for (int i = 0; i < coordinates.size(); i++) {
                            
                            double c = coordinates.get(i); 
                            seqB.append(String.format("%.3f\\,", c));
                            if (i % fitType.getModBy() == 0) {
                                if (c == Residue.NULL_VAL) {
                                    weightsB.append("0.0\\,");
                                    missingLen++;
                                }
                                else {
                                    weightsB.append("1.0\\,");
                                }
                            }
                        }
                        seqB.deleteCharAt(seqB.length() - 1);
                        seqB.deleteCharAt(seqB.length() - 1);
                        weightsB.deleteCharAt(weightsB.length() - 1);
                        weightsB.deleteCharAt(weightsB.length() - 1);
                        String seq = seqB.toString(); 
                        String weights = weightsB.toString(); 

                        if (len > missingLen) {

                            FitSequence sequence = new FitSequence();
                            sequence.setScopId(scopId);
                            sequence.setSeq(seq);
                            sequence.setWeights(weights);
                            sequence.setLen(len);
                            sequence.setMissingLen(missingLen);

                            sequences.add(sequence);
                        }
                    }

                } catch (Exception e) {
                    Logger.getLogger(SetFitSequences.class.getName()).log(Level.SEVERE, scopId, e);
                }
                
                // output
                processed++;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    saveFitSequences(fitType, sequences);
                    sequences.clear();
                    System.out.println("Split: " + splitIndex + ", Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }
            }

            if (sequences.size() > 0) {
                saveFitSequences(fitType, sequences);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(SetFitSequences.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static List<Double> calculateForScopId(
            FitSequenceType fitType,
            String scopId, 
            Parsing.ResidueCoords coords1, 
            Parsing.ResidueCoords coords2, 
            String sequenceText, 
            String mapText) {

        List<Double> seq = new ArrayList<>();

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

            // coordinates
            if (check) {

                if (!map.Code1.equals(".")) {

                    // NULL_COORD is possible even if the residue is present
                    if (fitType == FitSequenceType.PP) {

                        // always used for final testing
                        seq.add(residue.getPhi());
                        seq.add(residue.getPsi());
                    }
                    else { // FitSequenceType.CI 

                        seq.add(residue.getPhiX());
                        seq.add(residue.getPhiY());
                        seq.add(residue.getPsiX());
                        seq.add(residue.getPsiY());
                    }

                    // the residue is present
                    k++;
                }
                else {

                    // the residue is not present
                    for (int l = 0; l < fitType.getModBy(); l++) {
                        seq.add(Residue.NULL_VAL);
                    }
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
    
    private static void saveFitSequences(FitSequenceType fitType, List<FitSequence> sequences) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType(fitType.getDbName() + "_sequence", FitSequence.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_" + fitType.getDbName() + "_sequences(?);");
    
        FitSequence a[] = new FitSequence[sequences.size()];
        sequences.toArray(a);
        updt.setArray(1, conn.createArrayOf(fitType.getDbName() + "_sequence", a));
    
        updt.execute();
        updt.close();
    }

    private static void printlnError(String message) {

        System.out.println((char)27 + "[31m" + message + (char)27 + "[0m");
    }
}
