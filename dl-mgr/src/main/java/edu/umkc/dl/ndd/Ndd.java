package edu.umkc.dl.ndd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Db;

public class Ndd {

    public static void importGramsAndHashes() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> importSplit(splitIndex));
    }
    
    private static void importSplit(int splitIndex) {

        int processed = 0;

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
               
                String dbId = "";
                String seq = "";

                try {
               
                    dbId = rs.getString("scop_id").toLowerCase();
                    seq = rs.getString("sequence_text").toUpperCase();
                    
                    Integer[] grams = Graming.parseGrams(seq);
                    Hashes hashes = Hashing.hash(grams);

                    saveGrams(dbId, grams, conn);
                    saveHashes(dbId, hashes, conn);

                } catch (Exception e) {
                    Logger.getLogger(Ndd.class.getName()).log(Level.SEVERE, dbId, e);
                }
                
                // output
                processed += 1;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    System.out.println("Split: " + splitIndex + ", Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(Ndd.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void saveGrams(String dbId, Integer[] grams, Connection conn) throws SQLException {

        PreparedStatement updt = conn.prepareStatement("SELECT insert_ndd_grams(?,?);");
        updt.setString(1, dbId);
        updt.setArray(2, conn.createArrayOf("INTEGER", grams));
        updt.execute();
        updt.close();
    }
    
    private static void saveHashes(String dbId, Hashes hashes, Connection conn) throws SQLException {

        PreparedStatement updt = conn.prepareStatement("SELECT insert_ndd_hashes(?,?,?);");
        updt.setString(1, dbId);
        updt.setArray(2, conn.createArrayOf("INTEGER", hashes.getMinHashes()));
        updt.setArray(3, conn.createArrayOf("INTEGER", hashes.getBandHashes()));
        updt.execute();
        updt.close();
    }
}
