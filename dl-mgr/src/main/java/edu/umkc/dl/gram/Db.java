package edu.umkc.dl.gram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public static Map<String, DescrProbs> getDescrProbs() {

        Map<String, DescrProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from *_probs table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall(
                "select gram, descriptor, group_prob " + 
                "from descr_probs " + 
                "order by gram, descriptor;"
            );
           
            String lastGram = "";
            DescrProbs probs = new DescrProbs(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String gram = rs.getString("gram");
                int descr = rs.getInt("descriptor");
                double prob = rs.getDouble("group_prob");

                if (!lastGram.isEmpty() && !gram.equals(lastGram)) {
                    map.put(lastGram, probs);
                    probs = new DescrProbs();
                }
                lastGram = gram; 
           
                probs.setProbByDesc(descr, Math.log(prob));     
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

    public static Map<Integer, GramProbs> getGramProbs() {

        Map<Integer, GramProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from *_probs table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall(
                "select descriptor, gram, group_prob " + 
                "from gram_probs " + 
                "order by descriptor, gram;"
            );
           
            int lastDescr = -1;
            GramProbs probs = new GramProbs(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                int descr = rs.getInt("descriptor");
                String gram = rs.getString("gram");
                double prob = rs.getDouble("group_prob");

                if (lastDescr != -1 && descr != lastDescr) {
                    map.put(lastDescr, probs);
                    probs = new GramProbs();
                }
                lastDescr = descr; 
           
                probs.setProbByGram(gram, Math.log(prob));     
            }
            
            map.put(lastDescr, probs);

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return map; 
    }

    public static Map<Integer, PairProbs> getPairProbs() {

        Map<Integer, PairProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            // descriptors from *_probs table are always non-null integers
        
            PreparedStatement stmt = conn.prepareCall(
                "select descriptor_2, descriptor_1, group_prob " + 
                "from pair_probs " + 
                "order by descriptor_2, descriptor_1;"
            );
           
            int lastDescr2 = -1;
            PairProbs probs = new PairProbs(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                int descr2 = rs.getInt("descriptor_2");
                int descr1 = rs.getInt("descriptor_1");
                double prob = rs.getDouble("group_prob");

                if (lastDescr2 != -1 && descr2 != lastDescr2) {
                    map.put(lastDescr2, probs);
                    probs = new PairProbs();
                }
                lastDescr2 = descr2; 
                
                probs.setProbByDescr1(descr1, Math.log(prob));     
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
}



