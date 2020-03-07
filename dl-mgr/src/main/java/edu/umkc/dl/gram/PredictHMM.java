package edu.umkc.dl.gram;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PredictHMM { 

    private static int DESCR_COUNT = 10;
    private static double DOUBLE_MIN = -999_999.999;

    public static List<PredictResult> predict() {

        List<PredictResult> results = new ArrayList<>();
        List<List<Target>> groups = Db.getGroupedTargets();
        for (List<Target> targets : groups) {
            results.add(predict(targets));
        }
        return results;    
    }

    public static PredictResult predict(List<Target> targets) {

        Map<String, DescrProbs> descrProbsMap = Db.getDescrProbs();
        Map<Integer, GramProbs> gramProbsMap = Db.getGramProbs();
        Map<Integer, PairProbs> pairProbsMap = Db.getPairProbs();

        double[][] score = new double[targets.size()][DESCR_COUNT];
        int[][] path = new int[targets.size()][DESCR_COUNT];

        // 0.0 will never happen so it is used as a flag
        fillDoubleArray(score, 0.0);

        // -1 will never happen so it is used as a flag
        fillIntArray(path, -1);

        // i across j down
        for (int i = 1; i < targets.size() - 1; i++) {

            Target last = targets.get(i - 1);
            Target current = targets.get(i);
            Target next = targets.get(i + 1);
               
            // get descr probs for current gram 
            String gram = last.getResidueCode() + current.getResidueCode() + next.getResidueCode();
            DescrProbs descrProbs = descrProbsMap.get(gram);
            
            if (current.getDescriptor().equals("_")) {

                // set 0 to signal end of segment - should already be zero
                for (int j = 0; j < DESCR_COUNT; j++) {
                    score[i][j] = 0.0;
                }
            }
            else if (last.getDescriptor().equals("_")) {

                // score using prior probs to start segment
                for (int j = 0; j < DESCR_COUNT; j++) {
                    score[i][j] = descrProbs.getProbByDescr(j);
                }
            }
            else {

                // main logic
                for (int j = 0; j < DESCR_COUNT; j++) {

                    // get prob of descriptor j for gram
                    GramProbs gramProbs = gramProbsMap.get(j);
                    double gramProb = gramProbs.getProbByGram(gram); // this look very inefficient

                    // break on flag
                    if (gramProb == 0.0) {
                        continue;
                    }

                    // get transition probs to descriptor j
                    PairProbs pairProbs = pairProbsMap.get(j);
                    
                    int maxK = -1;
                    double maxScore = DOUBLE_MIN;
                    for (int k = 0; k < DESCR_COUNT; k++) {
                        
                        // get prob to descriptor j from descriptor k 
                        double pairProb = pairProbs.getProbByDescr1(k); 
                        
                        // break on flag
                        if (pairProb != 0.0 && score[i-1][k] != 0.0) {

                            double s = score[i-1][k] + pairProb + gramProb;
                            if (s > maxScore) {
                                maxK = k;
                                maxScore = s;
                            }
                        }
                    }

                    path[i][j] = maxK; 
                    score[i][j] = maxScore;
                } 
            }
        }        

        /*
        // DEBUG OUTPUT
        System.out.println("target id = " + targets.get(0).getTargetId());
        System.out.println("target length = " + targets.size());
        printScoresLeft(targets, score);
        printPathLeft(targets, path);
        printScoresRight(targets, score);
        printPathRight(targets, path);
        */

        int pointer = -1;
        StringBuilder predicted = new StringBuilder();
        for (int i = score.length - 1; i >= 0; i--) {
           
            if (pointer == -1) {

                int maxJ = -1;
                double maxScore = DOUBLE_MIN;
                for (int j = 0; j < DESCR_COUNT; j++) {
                    if (score[i][j] != 0.0 && score[i][j] > maxScore) {
                        maxJ = j;
                        maxScore = score[i][j];
                    }
                }
                if (maxJ != -1) {
                    predicted.append(maxJ);
                    pointer = path[i][maxJ];
                }
                else {
                    predicted.append("_");
                    pointer = -1;
                }
            }
            else {

                predicted.append(pointer);
                pointer = path[i][pointer];
            }
        }

        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {

            Target target = targets.get(i);
            String descr = target.getDescriptor();
            actual.append(descr); 
        }

        PredictResult results = new PredictResult(actual, predicted.reverse());
        
        return results;
    }

    /* DEBUG OUTPUT */

    private static int LEAD = 20;

    public static void printScoresLeft(List<Target> targets, double[][] score) {
        printScoresRange(targets, score, 0, LEAD);
    }

    public static void printScoresRight(List<Target> targets, double[][] score) {
        printScoresRange(targets, score, score.length - LEAD, score.length);
    }

    public static void printScoresRange(List<Target> targets, double[][] score, int start, int end) {

        DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
        formatter.applyPattern("0000.00"); // 7 chars
        formatter.setNegativePrefix("");

        System.out.println("range: " + start + ", " + end);
        if (start > 0) {
            System.out.print("     ");
        }
        else {
            System.out.print(" ");
        }
        for (int i = start; i < end; i++) {
            System.out.print("       " + targets.get(i).getResidueCode());
        }
        System.out.println("");
        for (int j = 0; j < score[0].length; j++) {
            System.out.print(j + " ");
            if (start > 0) {
                System.out.print("... ");
            }
            for (int i = start; i < end; i++) {
                System.out.print(formatter.format(score[i][j]) + " ");
            }
            if (end < score.length) {
                System.out.print("...");
            }
            System.out.println("");
        }
    }

    public static void printPathLeft(List<Target> targets, int[][] path) {
        printPathRange(targets, path, 0, LEAD);
    }

    public static void printPathRight(List<Target> targets, int[][] path) {
        printPathRange(targets, path, path.length - LEAD, path.length);
    }

    public static void printPathRange(List<Target> targets, int[][] path, int start, int end) {

        System.out.println("range: " + start + ", " + end);
        if (start > 0) {
            System.out.print("     ");
        }
        else {
            System.out.print(" ");
        }
        for (int i = start; i < end; i++) {
            System.out.print("       " + targets.get(i).getResidueCode());
        }
        System.out.println("");
        for (int j = 0; j < path[0].length; j++) {
            System.out.print(j + " ");
            if (start > 0) {
                System.out.print("... ");
            }
            for (int i = start; i < end; i++) {
                System.out.print(String.format("%7d", path[i][j]) + " ");
            }
            if (end < path.length) {
                System.out.print("...");
            }
            System.out.println("");
        }
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

