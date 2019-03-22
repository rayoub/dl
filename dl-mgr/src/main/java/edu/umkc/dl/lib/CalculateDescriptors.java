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
                int residueNumber = 0;
                String insertCode = "";
                String sequenceText = "";
                String mapText = "";

                try {
               
                    scopId = rs.getString("scop_id").toLowerCase();
                    residueNumber = rs.getInt("residue_number");
                    if (rs.wasNull()) 
                        residueNumber = Integer.MIN_VALUE;
                    insertCode = rs.getString("insert_code");
                    if (rs.wasNull())
                        insertCode = "";
                    insertCode = insertCode.toUpperCase();
                    sequenceText = rs.getString("sequence_text");
                    mapText = rs.getString("map_text");

                    String text = calculateForScopId(scopId, residueNumber, insertCode, sequenceText, mapText);

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

    private static String calculateForScopId(String scopId, int residueNumber, String insertCode, String sequenceText, String mapText) {

        List<String> codes = Arrays.asList(sequenceText.toUpperCase().split(","));
        List<Parsing.MapCoords> maps = Arrays.asList(mapText.split(",")).stream().map(m -> Parsing.parseMapCoords(m)).collect(Collectors.toList());
        List<ResidueDescriptor> residues = Db.getResidueDescriptors(scopId);
      
        // *** starting indices 
        
        // sequence index
        int i = 0;

        // map index
        int j = 0;
        while (maps.get(j).ResidueNumber == Integer.MIN_VALUE) {
            j++;
        }
        if (residueNumber != Integer.MIN_VALUE) {
            while (maps.get(j).ResidueNumber != residueNumber || !maps.get(j).InsertCode.equals(insertCode)) {
                j++;
            }
        }
        else {
            residueNumber = maps.get(j).ResidueNumber;
            insertCode = maps.get(j).InsertCode;
        }

        // residue index
        int k = 0;
        while (residues.get(k).getResidueNumber() != residueNumber || !residues.get(k).getInsertCode().equals(insertCode)) {
            k++;
        }

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
                        printlnError(scopId + ": missing residue at map coords (" + map.ResidueNumber + "," + map.InsertCode + ")");
                    }
                    else if (!(residue.getResidueNumber() == map.ResidueNumber && residue.getInsertCode().equals(map.InsertCode))) {

                        check = false;
                        printlnError(scopId + ": residue mismatch at map coords (" + map.ResidueNumber + "," + map.InsertCode + ")");
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
