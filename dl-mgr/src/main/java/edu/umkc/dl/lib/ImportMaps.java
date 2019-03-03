package edu.umkc.dl.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

import edu.umkc.dl.lib.Parsing.ResidueCoords;

public class ImportMaps {

    public static void importMaps() {

        String pdbId = "";
        String chain = "";
        String fileName = Constants.DATA_PATH + "astral-rapid-access-2.07-stable.raf";

        try {

            int processed = 0;
            List<Map> maps = new ArrayList<>();

            List<String> lines = Files.readAllLines(Paths.get(fileName));
            for (String line : lines) {

                if (line.startsWith("#")) {
                    continue;
                }
                    
                String header = line.substring(0,38);
                String text = splitByFixedWidth(line.substring(38), 7).stream().collect(Collectors.joining("\\,"));
                
                pdbId = header.substring(0,4).toLowerCase();
                chain = header.substring(4,6).trim().toUpperCase();

                ResidueCoords coords1 = Parsing.parseResidueCoords(header.substring(28,33).trim(), false);
                ResidueCoords coords2 = Parsing.parseResidueCoords(header.substring(33,38).trim(), false);
            
                Map map = new Map();

                map.setPdbId(pdbId);
                map.setChain(chain);
                map.setResidueNumber1(coords1.ResidueNumber);
                map.setInsertCode1(coords1.InsertCode);
                map.setResidueNumber2(coords2.ResidueNumber);
                map.setInsertCode2(coords2.InsertCode);
                map.setText(text);

                maps.add(map);

                processed++;
                if (processed % 5000 == 0) {
                    saveMaps(maps);
                    maps.clear();
                    System.out.println("processed: " + processed);
                }
            }  
                    
            if (maps.size() > 0) {
                saveMaps(maps);
            }

        } catch (NumberFormatException e) {
            Logger.getLogger(ImportMaps.class.getName()).log(Level.SEVERE, pdbId + chain, e);
        } catch (SQLException e) {
            Logger.getLogger(ImportMaps.class.getName()).log(Level.SEVERE, pdbId + chain, e);
        } catch (IOException e) {
            Logger.getLogger(ImportMaps.class.getName()).log(Level.SEVERE, pdbId + chain, e);
        }
    }
    
    public static void saveMaps(List<Map> maps) throws SQLException {

        PGSimpleDataSource ds = Db.getDataSource();

        Connection conn = ds.getConnection();
        conn.setAutoCommit(true);

        ((PGConnection) conn).addDataType("map", Map.class);

        PreparedStatement updt = conn.prepareStatement("SELECT insert_maps(?);");
     
        Map a[] = new Map[maps.size()];
        maps.toArray(a);
        updt.setArray(1, conn.createArrayOf("map", a));
    
        updt.execute();
        updt.close();
    }

    public static List<String> splitByFixedWidth(String text, int width) {

        List<String> parts = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += width) {
            parts.add(text.substring(i, Math.min(length, i + width)));
        }
        return parts;
    }
}
