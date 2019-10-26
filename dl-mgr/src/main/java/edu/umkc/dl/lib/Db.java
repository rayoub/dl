package edu.umkc.dl.lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.ds.PGSimpleDataSource;

public class Db {

    public static PGSimpleDataSource getDataSource() {

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setDatabaseName(Constants.DB_NAME);
        ds.setUser(Constants.DB_USER);
        ds.setPassword(Constants.DB_PASSWORD);

        return ds;
    }

    public static List<Residue> getResidues(String scopId) {

        List<Residue> residues = new ArrayList<Residue>();

        PGSimpleDataSource ds = getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_residues(?);");
       
            stmt.setString(1, scopId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                Residue residue = new Residue();

                residue.setScopId(rs.getString("scop_id"));
                residue.setOrderNumber(rs.getInt("order_number"));
                residue.setResidueNumber(rs.getInt("residue_number"));
                residue.setInsertCode(rs.getString("insert_code"));
                if (rs.wasNull()) 
                    residue.setInsertCode("");
                residue.setResidueCode(rs.getString("residue_code"));
                residue.setSsa(rs.getString("ssa"));
                if (rs.wasNull()) 
                    residue.setSsa("_");

                residue.setPhi(rs.getDouble("phi"));
                if (rs.wasNull())
                    residue.setPhi(Residue.NULL_VAL);
                residue.setPsi(rs.getDouble("psi"));
                if (rs.wasNull())
                    residue.setPsi(Residue.NULL_VAL);
                
                residue.setPhiX(rs.getDouble("phi_x"));
                if (rs.wasNull())
                    residue.setPhiX(Residue.NULL_VAL);
                residue.setPhiY(rs.getDouble("phi_y"));
                if (rs.wasNull())
                    residue.setPhiY(Residue.NULL_VAL);
                residue.setPsiX(rs.getDouble("psi_x"));
                if (rs.wasNull())
                    residue.setPsiX(Residue.NULL_VAL);
                residue.setPsiY(rs.getDouble("psi_y"));
                if (rs.wasNull())
                    residue.setPsiY(Residue.NULL_VAL);

                residue.setPhilX(rs.getDouble("phil_x"));
                if (rs.wasNull())
                    residue.setPhilX(Residue.NULL_VAL);
                residue.setPhirX(rs.getDouble("phir_x"));
                if (rs.wasNull())
                    residue.setPhirX(Residue.NULL_VAL);

                residue.setSplX(rs.getDouble("spl_x"));
                if (rs.wasNull())
                    residue.setSplX(Residue.NULL_VAL);
                residue.setSplY(rs.getDouble("spl_y"));
                if (rs.wasNull())
                    residue.setSplY(Residue.NULL_VAL);
                residue.setSplZ(rs.getDouble("spl_z"));
                if (rs.wasNull())
                    residue.setSplZ(Residue.NULL_VAL);

                residue.setSprX(rs.getDouble("spr_x"));
                if (rs.wasNull())
                    residue.setSprX(Residue.NULL_VAL);
                residue.setSprY(rs.getDouble("spr_y"));
                if (rs.wasNull())
                    residue.setSprY(Residue.NULL_VAL);
                residue.setSprZ(rs.getDouble("spr_z"));
                if (rs.wasNull())
                    residue.setSprZ(Residue.NULL_VAL);

                residues.add(residue);
            }

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, scopId, e);
        }

        return residues; 
    }
}

