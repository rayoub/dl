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
                
            PreparedStatement stmt = conn.prepareCall("SELECT * FROM get_sequence_split(?,?);");
            stmt.setInt(1, splitIndex);
            stmt.setInt(2, Constants.SPLIT_COUNT);
            
            ResultSet rs = stmt.executeQuery();

            System.out.println("Split: " + splitIndex + ", Got Ids.");

            // *** iterate split
            
            while (rs.next()) {
               
                String scopId = "";

                try {
               
                    scopId = rs.getString("scop_id").toLowerCase();
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
                
                    List<Residue> residues = parseStructure(scopId, structure);

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

    public static List<Residue> parseStructure(String scopId, Structure structure) {

        List<Residue> residues = new ArrayList<>();

        SecStrucCalc ssCalc = new SecStrucCalc();
            
        // assign secondary structure
        try {
            ssCalc.calculate(structure, true);
        } catch (StructureException e) {
            // do nothing
        }

        // iterate chains
        List<Chain> chains = structure.getChains();
        for(Chain chain : chains) {
        
            // *** gather residues
            
            // iterate residues (i.e. groups of atoms)
            List<Group> groups = chain.getAtomGroups();
            for (int i = 0; i < groups.size(); i++) {

                Group g = groups.get(i);
               
                String residueCode = g.getChemComp().getOne_letter_code().toUpperCase();

                // we need the carbon alpha
                if (!g.hasAtom("CA")) {
                    continue;
                }

                // get secondary structure assignment
                String ssa = "C";
                Object obj = g.getProperty(Group.SEC_STRUC);
                if (obj instanceof SecStrucInfo) {
                   SecStrucInfo info = (SecStrucInfo)obj;
                   ssa = String.valueOf(info.getType().type).trim();
                   if (ssa.isEmpty()) {
                       ssa = "C";
                    }
                }

                // map to extension coding
                String sse;
                switch(ssa) {
                    case "G":
                    case "H":
                    case "I":
                    case "T":
                        sse = "Helix";
                        break;
                    case "E":
                    case "B":
                        sse = "Strand";
                        break;
                    case "S":
                    case "C":
                        sse = "Loop";
                        break;
                    default:
                        sse = "Loop";
                }
                
                // calculate torsion angles
                double phi = 360.0;
                double psi = 360.0;
                if (i > 0 && i < groups.size() - 1) {

                    Group g1 = groups.get(i - 1);
                    Group g3 = groups.get(i + 1);
                    try {
                        if (g1 instanceof AminoAcid && g instanceof AminoAcid && g3 instanceof AminoAcid) {
                            
                            AminoAcid a1 = (AminoAcid) g1;
                            AminoAcid a2 = (AminoAcid) g;
                            AminoAcid a3 = (AminoAcid) g3;
                           
                            // check connectivity
                            boolean breakBefore = !Calc.isConnected(a1,a2);
                            boolean breakAfter = !Calc.isConnected(a2,a3);
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

                double cbX = -1;
                double cbY = -1;
                double cbZ = -1;
                if (g.hasAtom("CB")) {
                
                    Atom cb = g.getAtom("CB");
                    cbX = cb.getX();
                    cbY = cb.getY();
                    cbZ = cb.getZ();
                }

                Residue residue = new Residue();

                residue.setScopId(scopId);
                residue.setResidueNumber(g.getResidueNumber().getSeqNum());
                residue.setInsertCode(String.valueOf(g.getResidueNumber().getInsCode()).toUpperCase());
                residue.setResidueCode(residueCode);
                residue.setSsa(ssa);
                residue.setSse(sse);
                residue.setPhi(phi);
                residue.setPsi(psi);
                residue.setDescriptor(calculateRegion(phi,psi,sse));
                residue.setCaX(caX);
                residue.setCaY(caY);
                residue.setCaZ(caZ);
                residue.setCbX(cbX);
                residue.setCbY(cbY);
                residue.setCbZ(cbZ);

                residues.add(residue);
            }
            
        } // iterating chains

        // set order numbers
        for (int i = 0; i < residues.size(); i++) {
            residues.get(i).setOrderNumber(i+1);
        }

        return residues;
    }
    
    public static String calculateRegion(double phi, double psi, String sse) {

        if (phi == 360 || psi == 360) 
            return "";
        
        // helix 0, 1, 2, 3
        else if (sse.equals("Helix")) {
            return calculateHelixRegion(phi, psi);
        }

        // strand 4, 5, 6
        else if (sse.equals("Strand")) {
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
}

