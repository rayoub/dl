package edu.umkc.dl.gram;

import java.util.List;

public class PredictTargets { 

    public static void predict() {

        List<PredictResult> results = PredictHMM.predict();
        int total = 0;
        int totalCorrect = 0;
        for (PredictResult result : results) {
            
            total += result.getTotal();
            totalCorrect += result.getTotalCorrect();

            System.out.println(result.getActual());
            System.out.println(result.getPredicted());
            System.out.println("");
        }

        System.out.println(((double)totalCorrect) / total);
    }
}
