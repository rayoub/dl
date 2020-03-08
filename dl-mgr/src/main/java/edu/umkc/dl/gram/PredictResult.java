package edu.umkc.dl.gram;

public class PredictResult {

    private String targetId;
    private StringBuilder actual;
    private StringBuilder predicted;
    private int total;
    private int totalCorrect;

    public PredictResult(String targetId, StringBuilder actual, StringBuilder predicted) {

        this.targetId = targetId;
        this.actual = actual;
        this.predicted = predicted;
        
        this.total = 0;
        this.totalCorrect = 0;

        for (int i = 0; i < actual.length(); i++) {
            
            char a = actual.charAt(i);
            char p = predicted.charAt(i);
           
            if (a != '_' && p != '_') {
                total++;
                if (a == p) {
                    totalCorrect++;
                }
            } 
        }
    }

    public String getTargetId() {
        return targetId;
    }

    public StringBuilder getActual() {
        return actual;
    }

    public StringBuilder getPredicted() {
        return predicted;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }
}
