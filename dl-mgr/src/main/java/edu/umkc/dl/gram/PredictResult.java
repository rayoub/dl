package edu.umkc.dl.gram;

import java.util.ArrayList;
import java.util.List;

public class PredictResult {

    private String targetId;
    private StringBuilder actual;
    private List<StringBuilder> predictions = new ArrayList<>();
    private int total;
    private int totalCorrect;

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

    public int getTotal() {
        return total;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public List<StringBuilder> getPredictions() {
        return predictions;
    }

    public void addPrediction(StringBuilder prediction) {
        predictions.add(prediction);
    }

    public void resetTotals() {

        this.total = 0;
        this.totalCorrect = 0;

        for (StringBuilder prediction : predictions) {
            for (int i = 0; i < this.actual.length(); i++) {
                
                char a = this.actual.charAt(i);
                char p = prediction.charAt(i);
               
                if (a != '_' && p != '_') {
                    this.total++;
                    if (a == p) {
                        this.totalCorrect++;
                    }
                } 
            }
        }
    }
}
