package edu.umkc.dl.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Parsing.ResidueCoords;

public class ImportAaSequences {

    public static void importAaSequences() {

        String scopId = "";
        String fileName = Constants.DATA_PATH + "astral-scopedom-seqres-gd-all-2.07-stable.fa";

        try {

            InputStream inputStream = new FileInputStream(fileName);
            LinkedHashMap<String, ProteinSequence> seq = FastaReaderHelper.readFastaProteinSequence(inputStream);
            
            int processed = 0;
            List<AaSequence> sequences = new ArrayList<>();
            for (Entry<String, ProteinSequence> entry : seq.entrySet()) {

                // get header information
                String header = entry.getKey().replaceAll("\\{.*\\}","");
               
                String[] headerParts = header.split("\\s+",4);
               
                scopId = headerParts[0].toLowerCase();
                String pdbId = scopId.substring(1,5);

                String[] cats = headerParts[1].split("\\.",4);
                String cl = cats[0];
                String cf = cats[1];
                String sf = cats[2];
                String fa = cats[3];

                String[] segs = headerParts[2].replaceAll("[()]","").split(",");

                if (segs.length > 1) {

                    // only process single segment domains
                    continue;
                }

                String[] segParts = segs[0].split(":");
                String chain = segParts[0].toUpperCase();

                ResidueCoords coords1 = new ResidueCoords();
                ResidueCoords coords2 = new ResidueCoords();
                if (segParts.length > 1) {

                    String[] rangeParts = segParts[1].split("-");

                    String text1;
                    String text2;
                    boolean negate = rangeParts.length > 2;
                    if (negate) {
    
                        text1 = rangeParts[1];
                        text2 = rangeParts[2];
                    }
                    else {
                        
                        text1 = rangeParts[0];
                        text2 = rangeParts[1];
                    }

                    coords1 = Parsing.parseResidueCoords(text1, negate);
                    coords2 = Parsing.parseResidueCoords(text2, false);
                }

                // get sequence text and length
                List<AminoAcidCompound> compounds = entry.getValue().getAsList();
                String text = compounds.stream().map(c -> c.getShortName()).collect(Collectors.joining("\\,"));
                int len = compounds.size();

                // add sequence to list
                AaSequence sequence = new AaSequence();

                sequence.setScopId(scopId);
                sequence.setPdbId(pdbId);
                sequence.setCl(cl);
                sequence.setCf(cf);
                sequence.setSf(sf);
                sequence.setFa(fa);
                sequence.setChain(chain);
                sequence.setResidueNumber1(coords1.ResidueNumber);
                sequence.setInsertCode1(coords1.InsertCode);
                sequence.setResidueNumber2(coords2.ResidueNumber);
                sequence.setInsertCode2(coords2.InsertCode);
                sequence.setText(text);
                sequence.setLen(len);

                sequences.add(sequence);

                processed++;
                if (processed % 5000 == 0) {
                    saveAaSequences(sequences);
                    sequences.clear();
                    System.out.println("processed: " + processed);
                }
            }

            if (sequences.size() > 0) {
                saveAaSequences(sequences);
            }

        } catch (NumberFormatException e) {
            Logger.getLogger(ImportAaSequences.class.getName()).log(Level.SEVERE, scopId, e);
        } catch (SQLException e) {
            Logger.getLogger(ImportAaSequences.class.getName()).log(Level.SEVERE, scopId, e);
        } catch (IOException e) {
            Logger.getLogger(ImportAaSequences.class.getName()).log(Level.SEVERE, scopId, e);
        }
    }
    
    public static void saveAaSequences(List<AaSequence> sequences) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType("aa_sequence", AaSequence.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_aa_sequences(?);");
     
        AaSequence a[] = new AaSequence[sequences.size()];
        sequences.toArray(a);
        updt.setArray(1, conn.createArrayOf("aa_sequence", a));
    
        updt.execute();
        updt.close();
    }
}
