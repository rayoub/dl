package edu.umkc.dl.lib;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.biojava.nbio.structure.AminoAcid;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Calc;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.io.LocalPDBDirectory.FetchBehavior;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.biojava.nbio.structure.secstruc.SecStrucCalc;
import org.biojava.nbio.structure.secstruc.SecStrucInfo;
import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

public class ImportStructures {

    public static void importStructures() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> importStructuresSplit(splitIndex));
    }

    private static void importStructuresSplit(int splitIndex) {

        int processed = 0;

        String fileName = "";

        PGSimpleDataSource ds = Db.getDataSource();

        try {

            Connection conn = ds.getConnection();
            conn.setAutoCommit(true);

            // *** get split

            System.out.println("Split: " + splitIndex + ", Getting Ids to Import.");
                
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_split(?,?);");
            stmt.setInt(1, splitIndex);
            stmt.setInt(2, Constants.SPLIT_COUNT);
            
            ResultSet rs = stmt.executeQuery();

            System.out.println("Split: " + splitIndex + ", Got Ids.");

            // *** iterate split
            
            while (rs.next()) {
               
                String scopId = "";
                String pdbId = "";

                try {
               
                    scopId = rs.getString("scop_id").toLowerCase();
                    pdbId = rs.getString("pdb_id").toLowerCase();

                    fileName = Constants.PDB_PATH + scopId + ".ent";

                    if (Files.notExists(Paths.get(fileName))) {
                        System.out.println("File Not Found: " + fileName);
                        continue;
                    }

                    InputStream inputStream = new FileInputStream(fileName);

                    PDBFileReader reader = new PDBFileReader();
                    reader.setFetchBehavior(FetchBehavior.LOCAL_ONLY);
                    reader.getStructure(inputStream);

                    Structure structure = reader.getStructure(fileName);
                
                    List<Residue> residues = parseStructure(scopId, pdbId, structure);

                    saveResidues(residues, conn);

                    inputStream.close();

                } catch (Exception e) {
                    Logger.getLogger(ImportStructures.class.getName()).log(Level.SEVERE, scopId, e);
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
            Logger.getLogger(ImportStructures.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void saveResidues(List<Residue> residues, Connection conn) throws SQLException {

        ((PGConnection) conn).addDataType("residue", Residue.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_residues(?);");
     
        Residue a[] = new Residue[residues.size()];
        residues.toArray(a);
        updt.setArray(1, conn.createArrayOf("residue", a));
    
        updt.execute();
        updt.close();
    }

    public static List<Residue> parseStructure(String scopId, String pdbId, Structure structure) {

        // assign secondary structure
        SecStrucCalc ssCalc = new SecStrucCalc();
        try {
            ssCalc.calculate(structure, true);
        } catch (StructureException e) {
            // do nothing
        }

        // get first chain 
        Chain chain = structure.getChainByIndex(0);

        // gather residues 
        List<Residue> residues = new ArrayList<>();
        List<Group> groups = chain.getAtomGroups();
        for (int i = 0; i < groups.size(); i++) {

            Group g = groups.get(i);
           
            String residueCode = g.getChemComp().getOne_letter_code().toUpperCase();

            // we need the carbon alpha
            if (!g.hasAtom("CA")) {
                continue;
            }

            // get secondary structure assignment 
            // empty strings will be converted to _ when getting
            String ssa = "";
            Object obj = g.getProperty(Group.SEC_STRUC);
            if (obj instanceof SecStrucInfo) {
               SecStrucInfo info = (SecStrucInfo)obj;
               ssa = String.valueOf(info.getType().type).trim();
               if (ssa.isEmpty()) {
                   ssa = "C";
                }
            }

            // map to sse group 
            String grouping;
            switch(ssa) {
                case "G":
                case "H":
                case "I":
                case "T":
                    grouping = "Helix";
                    break;
                case "E":
                case "B":
                    grouping = "Strand";
                    break;
                case "S":
                case "C":
                    grouping = "Loop";
                    break;
                default:
                    // empty strings will be converted to _ when getting
                    grouping = "";
            }
            
            // calculate torsion angles
            double phi = Residue.NULL_ANGLE;
            double psi = Residue.NULL_ANGLE;
            boolean breakBefore = true;
            boolean breakAfter = true;
            if (i > 0 && i < groups.size() - 1) {

                Group g1 = groups.get(i - 1);
                Group g3 = groups.get(i + 1);
                try {
                    if (g1 instanceof AminoAcid && g instanceof AminoAcid && g3 instanceof AminoAcid) {
                        
                        AminoAcid a1 = (AminoAcid) g1;
                        AminoAcid a2 = (AminoAcid) g;
                        AminoAcid a3 = (AminoAcid) g3;
                       
                        // check connectivity
                        breakBefore = !Calc.isConnected(a1,a2);
                        breakAfter = !Calc.isConnected(a2,a3);
                        if (!breakBefore && !breakAfter) {
                            phi = Calc.getPhi(a1,a2);
                            psi = Calc.getPsi(a2,a3);
                        }
                    }
                } catch (StructureException e) {
                    // do nothing
                }
            }

            // get coordinates
            Atom ca = g.getAtom("CA");
            double caX = ca.getX();
            double caY = ca.getY();
            double caZ = ca.getZ();

            double cbX = Residue.NULL_COORD;
            double cbY = Residue.NULL_COORD;
            double cbZ = Residue.NULL_COORD;
            if (g.hasAtom("CB")) {
            
                Atom cb = g.getAtom("CB");
                cbX = cb.getX();
                cbY = cb.getY();
                cbZ = cb.getZ();
            }
            
            double nX = Residue.NULL_COORD;
            double nY = Residue.NULL_COORD;
            double nZ = Residue.NULL_COORD;
            if (g.hasAtom("N")) {
                
                Atom n = g.getAtom("N");
                nX = n.getX();
                nY = n.getY();
                nZ = n.getZ();
            }

            Residue residue = new Residue();

            residue.setScopId(scopId);
            residue.setPdbId(pdbId);
            residue.setResidueNumber(g.getResidueNumber().getSeqNum());
            residue.setInsertCode(String.valueOf(g.getResidueNumber().getInsCode()).toUpperCase());
            residue.setResidueCode(residueCode);
            residue.setSsa(ssa);
            residue.setPhi(phi);
            residue.setPsi(psi);
            residue.setDescriptor(calculateRegion(phi,psi,grouping));
            residue.setCaX(caX);
            residue.setCaY(caY);
            residue.setCaZ(caZ);
            residue.setCbX(cbX);
            residue.setCbY(cbY);
            residue.setCbZ(cbZ);
            residue.setNX(nX);
            residue.setNY(nY);
            residue.setNZ(nZ);
            residue.setBreakBefore(breakBefore);
            residue.setBreakAfter(breakAfter);

            residues.add(residue);
        }

        // calculate cossack coords
        for (int i = 0; i < residues.size(); i++) {

            Residue residue = residues.get(i);

            // torsion angles present
            if (residue.getPhi() != Residue.NULL_ANGLE && residue.getPhi() != Residue.NULL_ANGLE){
              
                double phi = Math.abs(Math.toRadians(residue.getPhi()));
                double psi = Math.toRadians(residue.getPsi());
               
                // spherical coords with phi in 0 to pi
                double x = Math.sin(phi) * Math.cos(psi);
                double y = Math.sin(phi) * Math.sin(psi);
                double z = Math.cos(phi);

                // assign the ca3 coords 
                residue.setCkX(x);
                residue.setCkY(y);
                residue.setCkZ(z);
            }
        } 
        
        /*
        // calculate cossack coords
        for (int i = 1; i < residues.size() - 1; i++) {

            Residue residue1 = residues.get(i - 1);
            Residue residue2 = residues.get(i);
            Residue residue3 = residues.get(i + 1);

            // connected residues
            if (!residue2.isBreakBefore() && !residue2.isBreakAfter()) {
            
                double[][] coords = new double[3][4];

                // translate ca2 to origin

                // ca1
                coords[0][0] = residue1.getCaX() - residue2.getCaX();
                coords[1][0] = residue1.getCaY() - residue2.getCaY();
                coords[2][0] = residue1.getCaZ() - residue2.getCaZ();
               
                // n2 
                coords[0][1] = residue2.getNX() - residue2.getCaX();
                coords[1][1] = residue2.getNY() - residue2.getCaY();
                coords[2][1] = residue2.getNZ() - residue2.getCaZ();

                // ca2
                coords[0][2] = 0;
                coords[1][2] = 0;
                coords[2][2] = 0;

                // ca3
                coords[0][3] = residue3.getCaX() - residue2.getCaX();
                coords[1][3] = residue3.getCaY() - residue2.getCaY();
                coords[2][3] = residue3.getCaZ() - residue2.getCaZ();

                // 1. rotate ca1 about the x-axis to fall in the xy-plane
                // 2. rotate ca1 about the z-axis to fall in the xz-plane along negative x-axis
                // 1&2 will put ca1 on the x-axis an approximately fixed distance from ca2 at the origin
                // 3. rotate n2 about the x-axis to fall in the xy-plane with y > 0 

                // 1.
                double x = coords[0][0]; 
                double y = coords[1][0]; 
                double z = coords[2][0]; 

                if (z != 0) {

                    double cn = Math.sqrt(Math.pow(y,2) + Math.pow(z,2));
                    double pn = Math.sqrt(Math.pow(y,2));
                    double dot = Math.pow(y,2);
                    double cos = 0;
                    if (pn != 0) {
                        cos = dot / (pn * cn);
                    }
                    double angle = Math.acos(cos);

                    if (((z > 0) && (y > 0)) || ((z < 0) && (y < 0))) {
                        angle = Math.PI - angle;
                    }

                    double[][] r = getRotationX(angle);
                    coords = matmul(r, coords);
                } 

                // 2.
                x = coords[0][0]; 
                y = coords[1][0]; 
                z = coords[2][0]; 

                if (!(x < 0 && y == 0)) {

                    double cn = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                    double pn = Math.sqrt(Math.pow(x,2));
                    double dot = Math.pow(x,2);
                    double cos = 0;
                    if (pn != 0) {
                        cos = dot / (pn * cn);
                    }
                    double angle = Math.acos(cos);
                    
                    if (y < 0) {
                        if (x < 0) {
                            angle = 2 * Math.PI - angle;
                        } 
                        else {
                            angle = Math.PI + angle;
                        }
                    }
                    else if (x > 0) {
                        angle = Math.PI - angle; 
                    }

                    double[][] r = getRotationZ(angle);
                    coords = matmul(r, coords);
                }

                // 3. 
                x = coords[0][1]; 
                y = coords[1][1]; 
                z = coords[2][1]; 

                if (z != 0) {

                    double cn = Math.sqrt(Math.pow(y,2) + Math.pow(z,2));
                    double pn = Math.sqrt(Math.pow(y,2));
                    double dot = Math.pow(y,2);
                    double cos = 0;
                    if (pn != 0) {
                        cos = dot / (pn * cn);
                    }
                    double angle = Math.acos(cos);

                    if (y < 0) {
                        if (z < 0) {
                            angle = Math.PI - angle;
                        } 
                        else {
                            angle = Math.PI + angle;
                        }
                    }
                    else if (z > 0) {
                        angle = 2 * Math.PI - angle; 
                    }

                    double[][] r = getRotationX(angle);
                    coords = matmul(r, coords);
                }
                
                // get ca3 coords
                x = coords[0][3]; 
                y = coords[1][3]; 
                z = coords[2][3]; 

                // get norm
                double norm = Math.sqrt(Math.pow(x,2) + Math.pow(y, 2) + Math.pow(z, 2));
                
                // assign the ca3 coords 
                residue2.setCkX(coords[0][3] / norm);
                residue2.setCkY(coords[1][3] / norm);
                residue2.setCkZ(coords[2][3] / norm);
            }
        } 
        */

        // set order numbers
        for (int i = 0; i < residues.size(); i++) {
            residues.get(i).setOrderNumber(i+1);
        }

        return residues;
    }
    
    public static String calculateRegion(double phi, double psi, String grouping) {

        if (phi == Residue.NULL_ANGLE || psi == Residue.NULL_ANGLE || grouping.isEmpty()) 
            return "";
        
        // helix 0, 1, 2, 3
        else if (grouping.equals("Helix")) {
            return calculateHelixRegion(phi, psi);
        }

        // strand 4, 5, 6
        else if (grouping.equals("Strand")) {
            return calculateStrandRegion(phi, psi);
        }
     
        // loop 7, 8, 9
        else { 
            return calculateLoopRegion(phi, psi);
        }
    }

    public static String calculateHelixRegion(double phi, double psi) {

        String region = "";

        if (psi >= -180 && psi < -135) {
            if (phi >= 0 && phi < 180) {
                region = "3";
            }
            else {
                region = "0"; 
            }
        }
        else if (psi >= -135 && psi < -75) {
            if (phi >= 0 && phi < 180) {
                region = "3";
            }
            else {
                region = "2";
            }
        }
        else if (psi >= -75 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = "1";
            }
            else {
                region = "2";
            }
        }
        else if (psi >= 90 && psi < 120) {
            if (phi >= 0 && phi < 180) {
                region = "1";
            }
            else {
                region = "0";
            }
        }
        else if (psi >= 120 && psi < 180) {
            if (phi >= 0 && phi < 180) {
                region = "3";
            }
            else {
                region = "0";
            }
        }
        
        return region;
    }

    public static String calculateStrandRegion(double phi, double psi) {

        String region = "";

        if (psi >= -180 && psi < -110) {
            region = "4";
        }
        else if (psi >= -110 && psi < -60) {
            if (phi >= 0 && phi < 180) {
                region = "4";
            }
            else {
                region = "6";
            }
        }
        else if (psi >= -60 && psi < 60) {
            if (phi >= 0 && phi < 180) {
                region = "5";
            }
            else {
                region = "6";
            }
        }
        else if (psi >= 60 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = "5";
            }
            else {
                region = "4";
            }
        }
        else if (psi >= 90 && psi < 180) {
            region = "4";
        }

        return region;
    }
    
    public static String calculateLoopRegion(double phi, double psi) {

        String region = "";

        if (psi >= -180 && psi < -100) {
            region = "7";
        }
        else if (psi >= -100 && psi < -90) {
            if (phi >= 0 && phi < 180) {
                region = "7";
            }
            else {
                region = "9";
            }
        }
        else if (psi >= -90 && psi < 60) {
            if (phi >= 0 && phi < 180) {
                region = "8";
            }
            else {
                region = "9";
            }
        }
        else if (psi >= 60 && psi < 90) {
            if (phi >= 0 && phi < 180) {
                region = "8";
            }
            else {
                region = "7";
            }
        }
        else if (psi >= 90 && psi < 180) {
            region = "7";
        }
            
        return region;
    }
    
    public static double[][] getRotationX(double angle) {

        double[][] r = { 
            { 1.0, 0.0, 0.0 },
            { 0.0, Math.cos(angle), -Math.sin(angle) },
            { 0.0, Math.sin(angle), Math.cos(angle) }
        };
        
        return r;
    }

    public static double[][] getRotationY(double angle) {

        double[][] r = { 
            { Math.cos(angle), 0.0, Math.sin(angle) },
            { 0.0, 1.0, 0.0 },
            { -Math.sin(angle), 0.0, Math.cos(angle) }
        };
        
        return r;
    }
    
    public static double[][] getRotationZ(double angle) {

        double[][] r = { 
            { Math.cos(angle), -Math.sin(angle), 0.0 },
            { Math.sin(angle), Math.cos(angle), 0.0 },
            { 0.0, 0.0, 1.0 }
        };
        
        return r;
    }

    public static double[][] matmul(double[][] a, double[][] b) {

        int aRows = a.length;
        int aColumns = a[0].length;
        int bColumns = b[0].length;

        // initialize matrix
        double[][] c = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                c[i][j] = 0.0;
            }
        }

        // multiply matrices
        for (int i = 0; i < aRows; i++) { 
            for (int j = 0; j < bColumns; j++) { 
                for (int k = 0; k < aColumns; k++) { 
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return c;
    }
}

