package edu.umkc.dl.ndd;

public class Similarity {

    public static double getEstimatedSimilarity(Integer[] minHashes1, Integer[] minHashes2) {

        // assuming array lengths are equal
      
        int similarity = 0;
        for (int i = 0; i < minHashes1.length; i++) {

            if (minHashes1[i].equals(minHashes2[i])) {
                similarity++;
            }
        }

        return ((double)similarity) / minHashes1.length;
    }
}
