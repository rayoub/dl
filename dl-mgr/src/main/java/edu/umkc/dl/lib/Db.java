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
                    residue.setPhi(Residue.NULL_ANGLE);
                residue.setPsi(rs.getDouble("psi"));
                if (rs.wasNull())
                    residue.setPsi(Residue.NULL_ANGLE);
                
                residue.setPhiX(rs.getDouble("phi_x"));
                if (rs.wasNull())
                    residue.setPhiX(Residue.NULL_COORD);
                residue.setPhiY(rs.getDouble("phi_y"));
                if (rs.wasNull())
                    residue.setPhiY(Residue.NULL_COORD);
                residue.setPsiX(rs.getDouble("psi_x"));
                if (rs.wasNull())
                    residue.setPsiX(Residue.NULL_COORD);
                residue.setPsiY(rs.getDouble("psi_y"));
                if (rs.wasNull())
                    residue.setPsiY(Residue.NULL_COORD);

                residue.setSpX(rs.getDouble("sp_x"));
                if (rs.wasNull())
                    residue.setSpX(Residue.NULL_COORD);
                residue.setSpY(rs.getDouble("sp_y"));
                if (rs.wasNull())
                    residue.setSpY(Residue.NULL_COORD);
                residue.setSpZ(rs.getDouble("sp_z"));
                if (rs.wasNull())
                    residue.setSpZ(Residue.NULL_COORD);

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

