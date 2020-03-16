package edu.umkc.dl.gram;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.biojava.nbio.structure.AminoAcid;
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
import edu.umkc.dl.lib.ImportStructures;

public class ImportTargets {

    public static void importTargets() {

        try {

            Files.newDirectoryStream(Paths.get(Constants.TARGET_PATH), path -> path.toString().endsWith(".pdb")).forEach(path -> {

                String fileName = path.getFileName().toString();
                String targetId = fileName.substring(0, fileName.lastIndexOf('.'));

                try {

                    InputStream inputStream = new FileInputStream(path.toString());

                    PDBFileReader reader = new PDBFileReader();
                    reader.setFetchBehavior(FetchBehavior.LOCAL_ONLY);
                    
                    Structure structure = reader.getStructure(inputStream);
                
                    List<Target> targets = parseStructure(targetId, structure);

                    saveTargets(targets);

                    inputStream.close();

                } catch (Exception e) {
                    Logger.getLogger(ImportTargets.class.getName()).log(Level.SEVERE, targetId, e);
                }

            });

        } catch (IOException e) {
            Logger.getLogger(ImportStructures.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void saveTargets(List<Target> targets) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType("target", Target.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_targets(?);");
     
        Target a[] = new Target[targets.size()];
        targets.toArray(a);
        updt.setArray(1, conn.createArrayOf("target", a));
    
        updt.execute();
    
        updt.close();
        conn.close();
    }

    public static List<Target> parseStructure(String targetId, Structure structure) throws StructureException {

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
        List<Target> targets = new ArrayList<>();
        List<Group> groups = chain.getAtomGroups();
        for (int i = 0; i < groups.size(); i++) {

            Group g = groups.get(i);

            // we need the carbon alpha
            if (!g.hasAtom("CA")) {
                // we expect good target data 
                throw new StructureException("missing carbon alpha");
            }
           
            String residueCode = g.getChemComp().getOne_letter_code().toUpperCase();

            // get secondary structure 8
            String ss8 = SecStruct.getSecStruct8(g);
                
            // map to secondary structure 3
            String ss3 = SecStruct.getSecStruct3(ss8);
            
            // we need valid residue codes
            if (!Codes.ValidCodes.contains(residueCode)) {

                // we expect good target data 
                throw new StructureException("invalid residue code: " + residueCode);
            }                

            // calculate torsion angles
            double phi = Target.NULL_VAL;
            double psi = Target.NULL_VAL;
            String descriptor = "_";
            if (i > 0 && i < groups.size() - 1) {
                
                Group g1 = groups.get(i - 1);
                Group g3 = groups.get(i + 1);
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
                        descriptor = Descriptor.toDescriptor(phi, psi, ss3);
                    }
                }
            }
                              
            Target target = new Target();

            target.setTargetId(targetId);
            target.setResidueNumber(g.getResidueNumber().getSeqNum());
            target.setInsertCode(String.valueOf(g.getResidueNumber().getInsCode()).toUpperCase());
            target.setResidueCode(residueCode);
            target.setPhi(phi);
            target.setPsi(psi);
            target.setDescriptor(descriptor);

            targets.add(target);
        }

        // set order numbers
        for (int i = 0; i < targets.size(); i++) {
            targets.get(i).setOrderNumber(i+1);
        }

        return targets;
    }
}

