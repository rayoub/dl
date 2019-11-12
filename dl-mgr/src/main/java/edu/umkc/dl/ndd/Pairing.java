package edu.umkc.dl.ndd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Db;

public class Pairing {

    private final static int MAX_CACHED_PAIRS = 1000;
    
    public static void pair() {

        IntStream
            .range(0, Constants.BAND_HASH_COUNT)
            .boxed()
            .parallel()
            .forEach(bandIndex -> pairOnBand(bandIndex));
    }

    private static void pairOnBand(int bandIndex) {
        
        try {
            
            PGSimpleDataSource ds = Db.getDataSource();
            
            Connection conn;
            PreparedStatement stmt;
            
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            
            //the order by is critical - we only store pairs for which db_id_1 < db_id_2
            stmt = conn.prepareStatement("SELECT db_id, min_hashes, band_hashes FROM ndd_hashes ORDER BY band_hashes[?], db_id;");
            stmt.setInt(1, bandIndex + 1); // bands are one-based in DB
            
            int lastBandHash = -1;
            List<Hashes> tile = new ArrayList<>();
            List<NddPair> pairs = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                  
                    Hashes hashes = new Hashes();
                    
                    hashes.setDbId(rs.getString("db_id"));
                    hashes.setMinHashes((Integer[]) rs.getArray("min_hashes").getArray());
                    hashes.setBandHashes((Integer[]) rs.getArray("band_hashes").getArray());
                    
                    if (lastBandHash != -1 && hashes.getBandHashes()[bandIndex] != lastBandHash) {
                        unwindTile(tile, bandIndex, pairs);
                    }
                    
                    if (pairs.size() >= MAX_CACHED_PAIRS) {
                        savePairs(pairs, conn);
                        pairs.clear();
                    }
                    
                    tile.add(hashes);
                    
                    lastBandHash = hashes.getBandHashes()[bandIndex];
                }
            }
            
            // now unwind the trailing pairs
            unwindTile(tile, bandIndex, pairs);
            
            if (pairs.size() > 0) {
                savePairs(pairs, conn);
            }
            
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            Logger.getLogger(Pairing.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void unwindTile(List<Hashes> tile, int band, List<NddPair> pairs) {

        //unwind tile
        if(tile.size() >= 2){
            for (int i = 0; i < tile.size() - 1; i++) {
                for (int j = i + 1; j < tile.size(); j++) {
                    if (!lowerBandMatch(tile.get(i).getBandHashes(), tile.get(j).getBandHashes(), band)) {
                        double similarity = Similarity.getEstimatedSimilarity(tile.get(i).getMinHashes(), tile.get(j).getMinHashes());
                        if(similarity >= Constants.SIMILARITY_THRESHOLD){
                            NddPair pair = new NddPair();
                            pair.setDbId1(tile.get(i).getDbId());
                            pair.setDbId2(tile.get(j).getDbId());
                            pair.setSimilarity(similarity);
                            pairs.add(pair);
                        }
                    }
                }
            }
        }
        tile.clear();
    }

    private static boolean lowerBandMatch(Integer[] bandHashes1, Integer[] bandHashes2, int band) {

        for (int i = 0; i < band; i++) {
            if (Objects.equals(bandHashes1[i], bandHashes2[i])) {
                return true;
            }
        }
        return false;
    }
            
    private static void savePairs(List<NddPair> pairs, Connection conn) throws SQLException {
        
        PreparedStatement updt;

        ((PGConnection)conn).addDataType("ndd_pair", NddPair.class);
        
        updt = conn.prepareStatement("SELECT insert_ndd_pairs(?);");

        NddPair a[] = new NddPair[pairs.size()];
        pairs.toArray(a);
        updt.setArray(1, conn.createArrayOf("ndd_pair", a));
        updt.execute();
        
        updt.close();
    }
}