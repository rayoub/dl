package edu.umkc.dl.gram;

import java.util.ArrayList;
import java.util.List;

public class PredictResult {

    private String targetId;
    private StringBuilder actual;
    private List<StringBuilder> predictions = new ArrayList<>();
    private int totalPositives;
    private int totalNegatives;
    private int totalTruePositives;
    private int totalTrueNegatives;

    public PredictResult(String targetId, StringBuilder actual) {

        this.targetId = targetId;
        this.actual = actual;
    }

    public String getTargetId() {
        return targetId;
    }

    public StringBuilder getActual() {
        return actual;
    }

    public int getTotalPositives() {
        return totalPositives;
    }

    public int getTotalNegatives() {
        return totalNegatives;
    }

    public int getTotalTruePositives() {
        return totalTruePositives;
    }

    public int getTotalTrueNegatives() {
        return totalTrueNegatives;
    }

    public List<StringBuilder> getPredictions() {
        return predictions;
    }

    public void addPrediction(StringBuilder prediction) {
        predictions.add(prediction);
    }

    public void resetTotals() {

        this.totalPositives = 0;
        this.totalTruePositives = 0;
        this.totalNegatives = 0;
        this.totalTrueNegatives = 0;

        for (StringBuilder prediction : predictions) {
            for (int i = 0; i < this.actual.length(); i++) {
                
                char a = this.actual.charAt(i);
                char p = prediction.charAt(i);
               
                if (a != '_' && p != '_') {
                    if (a == 'C') { // positives
                        this.totalPositives++;
                        if (a == p) {
                            this.totalTruePositives++;
                        }
                    }
                    else { // negatives
                        this.totalNegatives++;
                        if (a == p) {
                            this.totalTrueNegatives++;
                        }
                    }
                } 
            }
        }
    }
}
