package edu.umkc.dl.gram;

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
import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Constants;
import edu.umkc.dl.lib.Db;
import edu.umkc.dl.lib.Descriptor;
import edu.umkc.dl.lib.Residue;

public class ImportPairs {

    public static void importPairs() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> importPairsSplit(splitIndex));
    }

    private static void importPairsSplit(int splitIndex) {

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
                
                    List<Pair> pairs = parseStructure(scopId, pdbId, structure, conn);

                    savePairs(pairs, conn);

                    inputStream.close();

                } catch (Exception e) {
                    Logger.getLogger(ImportGrams.class.getName()).log(Level.SEVERE, scopId, e);
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
            Logger.getLogger(ImportGrams.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static List<Pair> parseStructure(String scopId, String pdbId, Structure structure, Connection conn) throws SQLException {

        // assign secondary structure
        SecStrucCalc ssCalc = new SecStrucCalc();
        try {
            ssCalc.calculate(structure, true);
        } catch (StructureException e) {
            // do nothing
        }

        // get first chain 
        Chain chain = structure.getChainByIndex(0);

        // gather grams 
        List<Pair> pairs = new ArrayList<>();
        List<Group> groups = chain.getAtomGroups();
        for (int i = 1; i < groups.size() - 2; i++) {

            // we should expect everyhing of g2, g3 including torsion angles because we need descriptors

            Group g1 = groups.get(i-1);
            Group g2 = groups.get(i);
            Group g3 = groups.get(i+1);
            Group g4 = groups.get(i+2);
                    
            // we need amino acids 
            if (!(g1 instanceof AminoAcid && g2 instanceof AminoAcid && g3 instanceof AminoAcid && g4 instanceof AminoAcid)) {
                continue;
            }

            // we need the amino atoms
            if (!(g1.hasAminoAtoms() && g2.hasAminoAtoms() && g3.hasAminoAtoms() && g4.hasAminoAtoms())) {
                continue;
            }

            // residue codes            
            String residueCode1 = g2.getChemComp().getOne_letter_code().toUpperCase();
            String residueCode2 = g3.getChemComp().getOne_letter_code().toUpperCase();

            // we need valid residue codes
            if (!(Codes.ValidCodes.contains(residueCode1) && Codes.ValidCodes.contains(residueCode2))) {
                continue;
            }

            // get secondary structure 8
            String ss8_2 = SecStruct.getSecStruct8(g2);
            String ss8_3 = SecStruct.getSecStruct8(g3);
                
            // map to secondary structure 3
            String ss3_2 = SecStruct.getSecStruct3(ss8_2);
            String ss3_3 = SecStruct.getSecStruct3(ss8_3);
            
            // calculate torsion angles
            double phi_2 = Residue.NULL_VAL;
            double psi_2 = Residue.NULL_VAL;
            double phi_3 = Residue.NULL_VAL;
            double psi_3 = Residue.NULL_VAL;
            try {
                    AminoAcid a1 = (AminoAcid) g1;
                    AminoAcid a2 = (AminoAcid) g2;
                    AminoAcid a3 = (AminoAcid) g3;
                    AminoAcid a4 = (AminoAcid) g4;
                   
                    // check connectivity
                    boolean break1 = !Calc.isConnected(a1,a2);
                    boolean break2 = !Calc.isConnected(a2,a3);
                    boolean break3 = !Calc.isConnected(a3,a4);
                    if (!break1 && !break2 && !break3) {
                        phi_2 = Calc.getPhi(a1,a2);
                        psi_2 = Calc.getPsi(a2,a3);
                        phi_3 = Calc.getPhi(a2,a3);
                        psi_3 = Calc.getPsi(a3,a4);
                    }
            } catch (StructureException e) {
                // do nothing
            }

            // we need torsion angles
            if (phi_2 == Residue.NULL_VAL || psi_2 == Residue.NULL_VAL || 
                phi_3 == Residue.NULL_VAL || psi_3 == Residue.NULL_VAL) {
                continue;
            }
                              
            // get the descriptors
            String descriptor1 = Descriptor.toDescriptor(phi_2, psi_2, ss3_2);
            String descriptor2 = Descriptor.toDescriptor(phi_3, psi_3, ss3_3);

            // get max temp factor
            double maxTf1 = getMaxTf(g1);
            double maxTf2 = getMaxTf(g2);
            double maxTf3 = getMaxTf(g3);
            double maxTf4 = getMaxTf(g4);
            double maxTf = Math.max(maxTf1, Math.max(maxTf2, Math.max(maxTf3, maxTf4)));

            Pair pair = new Pair();

            pair.setScopId(scopId);
            pair.setPdbId(pdbId);
            pair.setMaxTf(maxTf);
            pair.setResidueCode1(residueCode1);
            pair.setResidueCode2(residueCode2);
            pair.setDescriptor1(descriptor1);
            pair.setDescriptor2(descriptor2);

            pairs.add(pair);
        }

        return pairs;
    }
    
    private static double getMaxTf(Group g) {
       
        double maxTf = 1000.0;
        
        Atom n = g.getAtom("N");
        Atom ca = g.getAtom("CA");
        Atom c = g.getAtom("C");
        Atom o = g.getAtom("O");
        maxTf = Math.max(Math.max(Math.max(n.getTempFactor(), ca.getTempFactor()), c.getTempFactor()), o.getTempFactor());

        return maxTf; 
    } 

    public static void savePairs(List<Pair> pairs, Connection conn) throws SQLException {

        ((PGConnection) conn).addDataType("pair", Pair.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_pairs(?);");
     
        Pair a[] = new Pair[pairs.size()];
        pairs.toArray(a);
        updt.setArray(1, conn.createArrayOf("pair", a));
    
        updt.execute();
        updt.close();
    }
}

