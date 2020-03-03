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

import edu.umkc.dl.lib.Constants;
import edu.umkc.dl.lib.Db;
import edu.umkc.dl.lib.Descriptor;
import edu.umkc.dl.lib.Residue;

public class ImportGrams {

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

    public static void importGrams() {

        IntStream.range(0, Constants.SPLIT_COUNT)
            .boxed()
            .parallel()
            .forEach(splitIndex -> importGramsSplit(splitIndex));
    }

    private static void importGramsSplit(int splitIndex) {

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
                
                    List<Gram> grams = parseStructure(scopId, pdbId, structure);

                    saveGrams(grams, conn);

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

    public static void saveGrams(List<Gram> grams, Connection conn) throws SQLException {

        ((PGConnection) conn).addDataType("gram", Gram.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_grams(?);");
     
        Gram a[] = new Gram[grams.size()];
        grams.toArray(a);
        updt.setArray(1, conn.createArrayOf("gram", a));
    
        updt.execute();
        updt.close();
    }

    public static List<Gram> parseStructure(String scopId, String pdbId, Structure structure) {

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
        List<Gram> grams = new ArrayList<>();
        List<Group> groups = chain.getAtomGroups();
        for (int i = 1; i < groups.size() - 1; i++) {

            // we should expect everyhing of g1 and g3 except torsion angles
            // because we do not need to worry about grams of descriptors (for now)

            Group g1 = groups.get(i-1);
            Group g2 = groups.get(i);
            Group g3 = groups.get(i+1);
                    
            // we need amino acids 
            if (!(g1 instanceof AminoAcid && g2 instanceof AminoAcid && g3 instanceof AminoAcid)) {
                continue;
            }

            // we need the amino atoms
            if (!(g1.hasAminoAtoms() && g2.hasAminoAtoms() && g3.hasAminoAtoms())) {
                continue;
            }
           
            String residueCode1 = g1.getChemComp().getOne_letter_code().toUpperCase();
            String residueCode2 = g2.getChemComp().getOne_letter_code().toUpperCase();
            String residueCode3 = g3.getChemComp().getOne_letter_code().toUpperCase();

            // get secondary structure 8
            String ss81 = getSecStruct8(g1);
            String ss82 = getSecStruct8(g2);
            String ss83 = getSecStruct8(g3);
                
            // map to secondary structure 3
            String ss31 = getSecStruct3(ss81);
            String ss32 = getSecStruct3(ss82);
            String ss33 = getSecStruct3(ss83);

            // we need valid residue codes
            if (!(validCodes.contains(residueCode1) && validCodes.contains(residueCode2) && validCodes.contains(residueCode3))) {
                continue;
            }
            
            // calculate torsion angles
            double phi = Residue.NULL_VAL;
            double psi = Residue.NULL_VAL;
            try {
                    AminoAcid a1 = (AminoAcid) g1;
                    AminoAcid a2 = (AminoAcid) g2;
                    AminoAcid a3 = (AminoAcid) g3;
                   
                    // check connectivity
                    boolean breakBefore = !Calc.isConnected(a1,a2);
                    boolean breakAfter = !Calc.isConnected(a2,a3);
                    if (!breakBefore && !breakAfter) {
                        phi = Calc.getPhi(a1,a2);
                        psi = Calc.getPsi(a2,a3);
                    }
            } catch (StructureException e) {
                // do nothing
            }

            // we need torsion angles
            if (phi == Residue.NULL_VAL || psi == Residue.NULL_VAL) {
                continue;
            }
                              
            // get the descriptor  
            String descriptor = Integer.toString(Descriptor.calculateRegion(phi, psi, ss32));

            // get max temp factor
            double maxTf1 = getMaxTf(g1);
            double maxTf2 = getMaxTf(g2);
            double maxTf3 = getMaxTf(g3);
            double maxTf = Math.max(maxTf1, Math.max(maxTf2, maxTf3));

            Gram gram = new Gram();

            gram.setScopId(scopId);
            gram.setPdbId(pdbId);
            gram.setResidueNumber(g2.getResidueNumber().getSeqNum());
            gram.setInsertCode(String.valueOf(g2.getResidueNumber().getInsCode()).toUpperCase());
            gram.setResidueCode1(residueCode1);
            gram.setResidueCode2(residueCode2);
            gram.setResidueCode3(residueCode3);
            gram.setSs31(ss31);
            gram.setSs32(ss32);
            gram.setSs33(ss33);
            gram.setSs81(ss81);
            gram.setSs82(ss82);
            gram.setSs83(ss83);
            gram.setMaxTf(maxTf);
            gram.setPhi(phi);
            gram.setPsi(psi);
            gram.setDescriptor(descriptor);

            grams.add(gram);
        }

        // set order numbers
        for (int i = 0; i < grams.size(); i++) {
            grams.get(i).setOrderNumber(i+1);
        }

        return grams;
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

    private static String getSecStruct8(Group g) {

        String ss8 = "C";
        Object obj = g.getProperty(Group.SEC_STRUC);
        if (obj instanceof SecStrucInfo) {
           SecStrucInfo info = (SecStrucInfo)obj;
           ss8 = String.valueOf(info.getType().type).trim();
           if (ss8.isEmpty()) {
               ss8 = "C";
            }
        }
        return ss8;
    }
   
    private static String getSecStruct3(String ss8) {

        String ss3;
        switch(ss8) {
            case "G":
            case "H":
            case "I":
            case "T":
                ss3 = "H";
                break;
            case "E":
            case "B":
                ss3 = "S";
                break;
            case "S":
            case "C":
                ss3 = "C";
                break;
            default:
                ss3 = "C";
        }
        return ss3;
    }    
}

