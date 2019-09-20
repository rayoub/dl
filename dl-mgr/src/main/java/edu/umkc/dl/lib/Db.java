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
                residue.setDescriptor(rs.getString("descriptor"));
                if (rs.wasNull()) 
                    residue.setDescriptor("_");
                residue.setCkX(rs.getDouble("ck_x"));
                if (rs.wasNull())
                    residue.setCkX(Residue.NULL_COORD);
                residue.setCkY(rs.getDouble("ck_y"));
                if (rs.wasNull())
                    residue.setCkY(Residue.NULL_COORD);
                residue.setCkZ(rs.getDouble("ck_z"));
                if (rs.wasNull())
                    residue.setCkZ(Residue.NULL_COORD);

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

