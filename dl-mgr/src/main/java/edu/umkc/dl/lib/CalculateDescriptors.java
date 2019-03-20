package edu.umkc.dl.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

                    calculateForScopId(scopId, residueNumber, insertCode, sequenceText, mapText);

                } catch (Exception e) {
                    Logger.getLogger(CalculateDescriptors.class.getName()).log(Level.SEVERE, scopId, e);
                }
                
                // output
                processed += 1;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    System.out.println("Split: " + splitIndex + ", Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }

                if (processed == 10)
                    break;
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(CalculateDescriptors.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void calculateForScopId(String scopId, int residueNumber, String insertCode, String sequenceText, String mapText) {

        List<String> seqCodes = Arrays.asList(sequenceText.toUpperCase().split(","));
        List<Parsing.MapCoords> mapCoords = Arrays.asList(mapText.split(",")).stream().map(m -> Parsing.parseMapCoords(m)).collect(Collectors.toList());
        List<ResidueDescriptor> residueDescriptors = Db.getResidueDescriptors(scopId);

        System.out.println(scopId + ", " + seqCodes.size() + ", " + mapCoords.size() + ", " + residueDescriptors.size()); 
       
        // using the sequence residue number and insert code scan for start of sequence in the map text
        
        // sequence index
        int i = 0;

        // map index
        int j = 0;
        if (residueNumber != Integer.MIN_VALUE) {
            while (mapCoords.get(j).ResidueNumber != residueNumber || !mapCoords.get(j).InsertCode.equals(insertCode)) {
                j++;
            }
        }
        else {
            residueNumber = mapCoords.get(j).ResidueNumber;
            insertCode = mapCoords.get(j).InsertCode;
        }

        // residue index
        int k = 0;
        while (residueDescriptors.get(k).getResidueNumber() != residueNumber || !residueDescriptors.get(k).getInsertCode().equals(insertCode)) {
            k++;
        }
       
        System.out.println(scopId + ", " + i + ", " + j + ", " + k);
        System.out.println(scopId + ", " + residueNumber + ", " + insertCode);
        // once found iterate the map in lockstep with the sequence and residues of the domain
        
        // for missing domain residue insert a _ for descriptor

        // 3. get the residues corresponding to the scopId

    }


}
