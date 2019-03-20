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

    public static List<ResidueDescriptor> getResidueDescriptors(String scopId) {

        List<ResidueDescriptor> residueDescriptors = new ArrayList<ResidueDescriptor>();

        PGSimpleDataSource ds = getDataSource();

        try {
       
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
       
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_residue_descriptors(?);");
       
            stmt.setString(1, scopId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                ResidueDescriptor residueDescriptor = new ResidueDescriptor();

                residueDescriptor.setScopId(rs.getString("scop_id"));
                residueDescriptor.setResidueNumber(rs.getInt("residue_number"));
                residueDescriptor.setInsertCode(rs.getString("insert_code"));
                residueDescriptor.setResidueCode(rs.getString("residue_code"));
                residueDescriptor.setDescriptor(rs.getString("descriptor"));

                residueDescriptors.add(residueDescriptor);
            }

            rs.close();
            stmt.close();
            conn.close();
        
        } catch (SQLException e) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, scopId, e);
        }

        return residueDescriptors; 
    }
}

