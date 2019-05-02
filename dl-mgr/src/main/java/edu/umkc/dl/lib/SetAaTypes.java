package edu.umkc.dl.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.ds.PGSimpleDataSource;

public class SetAaTypes {

    public static void set() {

        List<String> symbols = getAaTypes();
        try {

            saveAaTypes(symbols);

        } catch (SQLException e) {
            Logger.getLogger(SetAaTypes.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static List<String> getAaTypes() {

        Set<String> symbols = new HashSet<>();

        int processed = 0;

        PGSimpleDataSource ds = Db.getDataSource();

        try {

            Connection conn = ds.getConnection();

            // *** get sequences

            System.out.println("Getting Sequences to Process.");
                
            PreparedStatement stmt = conn.prepareCall("SELECT text FROM aa_sequence;");
            
            ResultSet rs = stmt.executeQuery();

            System.out.println("Got Sequences.");

            // *** iterate sequences
            
            while (rs.next()) {
              
                String text = rs.getString("text");
                String[] seq = text.split(",");
                
                for (int i = 0; i < seq.length; i++) {
                    symbols.add(seq[i]);
                }
                
                // output
                processed++;
                if (processed % Constants.PROCESSED_INCREMENT == 0) {
                    System.out.println("Processed: "
                            + (Constants.PROCESSED_INCREMENT * (processed / Constants.PROCESSED_INCREMENT)));
                }
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(SetAaTypes.class.getName()).log(Level.SEVERE, null, e);
        }

        return new ArrayList<>(symbols);
    }
    
    private static void saveAaTypes(List<String> symbols) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        for (String symbol : symbols) {

            PreparedStatement updt = conn.prepareStatement("INSERT INTO aa_type (symbol) VALUES (?);");
            updt.setString(1, symbol); 
            updt.execute();
            updt.close();
        }

        conn.close();
    }
}
