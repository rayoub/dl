package edu.umkc.dl.gram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PredictHMM { 

    private static int DESCR_COUNT = 10;

    public static List<PredictResult> predict() {

        List<PredictResult> results = new ArrayList<>();
        List<List<Target>> groups = Db.getGroupedTargets();
        for (List<Target> targets : groups) {
            results.add(predict(targets));
        }
        return results;    
    }

    public static PredictResult predict(List<Target> targets) {

        double[] priors = Db.getPriorDescrProbs();
        Map<String, DescrProbs> gramProbs = Db.getGramDescrProbs();
        Map<Integer, DescrProbs> pairProbs = Db.getPairDescrProbs();

        double[][] score = new double[targets.size()][DESCR_COUNT];
        int[][] path = new int[targets.size()][DESCR_COUNT];

        // 0.0 will never happen so it is used as a flag
        fillDoubleArray(score, 0.0);

        // -1 will never happen so it is used as a flag
        fillIntArray(path, -1);

        // i across j down
        for (int i = 1; i < targets.size(); i++) {

            Target last = targets.get(i - 1);
            Target current = targets.get(i);
            
            if (current.getDescriptor().equals("_")) {

                // set 0 to signal end of segment - should already be zero
                for (int k = 0; k < DESCR_COUNT; k++) {
                    score[i][k] = 0.0;
                }
            }
            else if (last.getDescriptor().equals("_")) {

                // score using prior probs to start segment
                for (int k = 0; k < DESCR_COUNT; k++) {
                    score[i][k] = priors[k];
                }
            }
            else {

                // determine max probs




            }
        }        

        for (int k = 0; k < score[0].length; k++) {
            for (int i = 0; i < score.length; i++) {
                System.out.print(score[i][k] + " ");
            }
            System.out.println("");
        }

        return null;
    }

    private static void fillDoubleArray(double[][] a, double val) {

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = val;
            }
        }
    }

    private static void fillIntArray(int[][] a, int val) {

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = val;
            }
        }
    }
}

