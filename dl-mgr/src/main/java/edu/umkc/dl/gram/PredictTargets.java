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

import edu.umkc.dl.lib.Db;

public class PredictTargets { 

    public static void predict() {

        List<List<Target>> groups = getGroupedTargets();
        for (List<Target> targets : groups) {
            predict(targets);
        }
    }

    public static void predict(List<Target> targets) {


        System.out.println(targets.size());
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
                target.setSs3(rs.getString("ss3"));
                if (rs.wasNull()) 
                    target.setSs3("");
                target.setSs8(rs.getString("ss8"));
                if (rs.wasNull()) 
                    target.setSs8("");

                target.setPhi(rs.getDouble("phi"));
                if (rs.wasNull())
                    target.setPhi(Target.NULL_VAL);
                target.setPsi(rs.getDouble("psi"));
                if (rs.wasNull())
                    target.setPsi(Target.NULL_VAL);

                target.setDescriptor(rs.getString("descriptor"));
                if (rs.wasNull()) 
                    target.setDescriptor("");

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

    public static Map<String, GramProbs> getGramProbs() {

        Map<String, GramProbs> map = new HashMap<>();

        PGSimpleDataSource ds = Db.getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            PreparedStatement stmt = conn.prepareCall(
                "select substr(group_id,2) as group_id, descriptor, group_prob " + 
                "from gram_counts " + 
                "where group_set = '_r1r2r3' " + 
                "order by group_id, group_rank;"
            );
           
            String lastGram = "";
            GramProbs probs = new GramProbs(); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String gram = rs.getString("group_id");
                int descr = rs.getInt("descriptor");
                double prob = rs.getDouble("group_prob");

                if (!lastGram.isEmpty() && !gram.equals(lastGram)) {
                    map.put(gram, probs);
                    probs = new GramProbs();
                }
                lastGram = gram; 
                
                probs.updateDescrProb(descr, prob);
            }

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, e);
        }

        return map; 
    }
}
