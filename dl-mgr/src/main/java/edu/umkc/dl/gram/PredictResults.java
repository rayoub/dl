package edu.umkc.dl.gram;

public class PredictResults {

    private int total;
    private int totalCorrect;

    public int getTotal() {
        return total;
    }

    public void incrementTotalCount(int total) {
        this.total += total;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void incrementCorrectCount(int totalCorrect) {
        this.totalCorrect += totalCorrect;
    }
}
