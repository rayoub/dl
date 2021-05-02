package edu.umkc.dl.gram;

import java.util.List;

public class PredictTargets { 

    public static void predict() {

        List<PredictResult> results = PredictHMM.predict();
        int totalPositives = 0;
        int totalTruePositives = 0;
        int totalNegatives = 0;
        int totalTrueNegatives = 0;

        for (PredictResult result : results) {
            
            totalPositives += result.getTotalPositives();
            totalTruePositives += result.getTotalTruePositives();
            
            totalNegatives += result.getTotalNegatives();
            totalTrueNegatives += result.getTotalTrueNegatives();

            System.out.println(result.getTargetId());
            System.out.println(result.getActual());
            System.out.println(result.getPredictions().get(0));
            System.out.println("");
        }

        System.out.println("ACC: " + ((double)(totalTruePositives + totalTrueNegatives)) / (totalPositives + totalNegatives));
        System.out.println("TPR: " + ((double)totalTruePositives) / totalPositives);
        System.out.println("TNR: " + ((double)totalTrueNegatives) / totalNegatives);
    }
}
