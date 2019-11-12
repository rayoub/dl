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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private static Set<String> validCodes = new HashSet<>();

    static {

        validCodes.add("A");
        validCodes.add("C");
        validCodes.add("D");
        validCodes.add("E");
        validCodes.add("F");
        validCodes.add("G");
        validCodes.add("H");
        validCodes.add("I");
        validCodes.add("K");
        validCodes.add("L");
        validCodes.add("M");
        validCodes.add("N");
        validCodes.add("P");
        validCodes.add("Q");
        validCodes.add("R");
        validCodes.add("S");
        validCodes.add("T");
        validCodes.add("V");
        validCodes.add("W");
        validCodes.add("Y");
    }

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

            // calculate max tf
            double maxTf = 1000.0; // it will definitely be filtered out if we filter
            if (g.hasAminoAtoms()) {

                Atom n = g.getAtom("N");
                Atom ca = g.getAtom("CA");
                Atom c = g.getAtom("C");
                Atom o = g.getAtom("O");

                maxTf = Math.max(Math.max(Math.max(n.getTempFactor(), ca.getTempFactor()), c.getTempFactor()), o.getTempFactor());
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

            // calculate torsion angles
            double phi = Residue.NULL_VAL;
            double psi = Residue.NULL_VAL;
            String gram = "";

            if (validCodes.contains(residueCode)) {

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

                                // a valid gram
                                String rc1 = a1.getChemComp().getOne_letter_code().toUpperCase();
                                String rc2 = a2.getChemComp().getOne_letter_code().toUpperCase();
                                String rc3 = a3.getChemComp().getOne_letter_code().toUpperCase();
                                if (validCodes.contains(rc1) && validCodes.contains(rc3)) {
                                    gram = rc1 + rc2 + rc3;
                                }
                            }
                        }
                    } catch (StructureException e) {
                        // do nothing
                    }
                }
            }

            Residue residue = new Residue();

            residue.setScopId(scopId);
            residue.setPdbId(pdbId);
            residue.setResidueNumber(g.getResidueNumber().getSeqNum());
            residue.setInsertCode(String.valueOf(g.getResidueNumber().getInsCode()).toUpperCase());
            residue.setResidueCode(residueCode);
            residue.setGram(gram);
            residue.setMaxTf(maxTf);
            residue.setSsa(ssa);
            residue.setPhi(phi);
            residue.setPsi(psi);

            residues.add(residue);
        }
        
        // calculate circular coords
        for (int i = 0; i < residues.size(); i++) {

            Residue residue = residues.get(i);

            // torsion angles present
            if (residue.getPhi() != Residue.NULL_VAL && residue.getPsi() != Residue.NULL_VAL){
             
                double phi = Math.toRadians(residue.getPhi());
                double psi = Math.toRadians(residue.getPsi());
              
                residue.setPhiX(Math.cos(phi));
                residue.setPhiY(Math.sin(phi));
                residue.setPsiX(Math.cos(psi));
                residue.setPsiY(Math.sin(psi));
            }
        } 

        // set order numbers
        for (int i = 0; i < residues.size(); i++) {
            residues.get(i).setOrderNumber(i+1);
        }

        return residues;
    }
}

