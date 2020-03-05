package edu.umkc.dl.gram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Constants;

public class Db {

    public static PGSimpleDataSource getDataSource() {

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName(Constants.DB_NAME);
        ds.setUser(Constants.DB_USER);
        ds.setPassword(Constants.DB_PASSWORD);

        return ds;
    }

    public static List<List<Target>> getGroupedTargets() {

        List<List<Target>> groups = new ArrayList<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM target ORDER BY target_id, order_number;");
           
            String lastTargetId = ""; 
            List<Target> targets = new ArrayList<>(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                Target target = new Target();

                target.setTargetId(rs.getString("target_id"));
                target.setOrderNumber(rs.getInt("order_number"));
                target.setResidueNumber(rs.getInt("residue_number"));
                target.setInsertCode(rs.getString("insert_code"));
                if (rs.wasNull()) 
                    target.setInsertCode("");
                target.setResidueCode(rs.getString("residue_code"));

                target.setPhi(rs.getDouble("phi"));
                if (rs.wasNull())
                    target.setPhi(Target.NULL_VAL);
                target.setPsi(rs.getDouble("psi"));
                if (rs.wasNull())
                    target.setPsi(Target.NULL_VAL);

                target.setDescriptor(rs.getString("descriptor"));

                if (!lastTargetId.isEmpty() && !target.getTargetId().equals(lastTargetId)) {
                    groups.add(targets);
                    targets = new ArrayList<>();
                }
                lastTargetId = target.getTargetId();
                
                targets.add(target);
            }

            groups.add(targets);

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return groups; 
    }

    public static Map<String, DescrProbs> getGramProbs() {

        Map<String, DescrProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from gram_counts table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall(
                "select substr(group_id,2) as group_id, descriptor, group_prob " + 
                "from gram_counts " + 
                "where group_set = '_r1r2r3' " + 
                "order by group_id, group_rank;"
            );
           
            String lastGram = "";
            DescrProbs probs = new DescrProbs(); 

            boolean[] flags = new boolean[10];
            Arrays.fill(flags, false);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String gram = rs.getString("group_id");
                int descr = rs.getInt("descriptor");
                double prob = rs.getDouble("group_prob");

                if (!lastGram.isEmpty() && !gram.equals(lastGram)) {
                    
                    for (int i = 0; i < flags.length; i++) {
                        if (!flags[i]) {
                            probs.updateDescrProb(i,0);
                        }
                    }
                    Arrays.fill(flags, false);

                    map.put(lastGram, probs);
                    probs = new DescrProbs();
                }
                lastGram = gram; 
                
                probs.updateDescrProb(descr, prob);
                flags[descr] = true;
            }
            
            for (int i = 0; i < flags.length; i++) {
                if (!flags[i]) {
                    probs.updateDescrProb(i,0);
                }
            }
            map.put(lastGram, probs);

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return map; 
    }

    public static Map<Integer, DescrProbs> getPairProbs() {

        Map<Integer, DescrProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from pair_counts table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall( "select descriptor_2, descriptor_1, group_prob " + 
                "from pair_counts " + 
                "where group_set = '_d2' " + 
                "order by descriptor_2, group_rank;"
            );
           
            int lastDescr2 = -1;
            DescrProbs probs = new DescrProbs(); 

            boolean[] flags = new boolean[10];
            Arrays.fill(flags, false);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                int descr2 = rs.getInt("descriptor_2");
                int descr1 = rs.getInt("descriptor_1");
                double prob = rs.getDouble("group_prob");

                if (lastDescr2 != -1 && descr2 != lastDescr2) {

                    for (int i = 0; i < flags.length; i++) {
                        if (!flags[i]) {
                            probs.updateDescrProb(i,0);
                        }
                    }
                    Arrays.fill(flags, false);
                
                    map.put(lastDescr2, probs);
                    probs = new DescrProbs();
                }
                lastDescr2 = descr2; 
                
                probs.updateDescrProb(descr1, prob);
                flags[descr1] = true;
            }

            for (int i = 0; i < flags.length; i++) {
                if (!flags[i]) {
                    probs.updateDescrProb(i,0);
                }
            }
            map.put(lastDescr2, probs);

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return map; 
    }

    // TODO finish this function
    /*
    public static Map<Integer, DescrProbs> getDescrProbs() {

        Map<Integer, DescrProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from pair_counts table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall(
                "select descriptor_1, descriptor_2, group_prob " + 
                "from pair_counts " + 
                "where group_set = '_d1' " + 
                "order by descriptor_1, group_rank;"
            );
           
            int lastDescr1 = -1;
            DescrProbs probs = new DescrProbs(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                int descr1 = rs.getInt("descriptor_1");
                int descr2 = rs.getInt("descriptor_2");
                double prob = rs.getDouble("group_prob");

                if (lastDescr1 != -1 && descr1 != lastDescr1) {
                    map.put(lastDescr1, probs);
                    probs = new DescrProbs();
                }
                lastDescr1 = descr1; 
                
                probs.updateDescrProb(descr2, prob);
            }
            map.put(lastDescr1, probs);

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return map; 
    }
    */
}



