package edu.umkc.dl.gram;

import java.util.Arrays;

// descriptors from *_probs tables are always non-null integers

public class PairProbs {

    public static int DESCR_COUNT = 10;

    private double[] descr1Probs; 
    
    public PairProbs() {

        this.descr1Probs = new double[DESCR_COUNT];

        Arrays.fill(this.descr1Probs, 0.0);
    }

    public double getProbByDescr1(int descr1) {
        return this.descr1Probs[descr1];
    }
    
    public void setProbByDescr1(int descr1, double descr1Prob) {
        this.descr1Probs[descr1] = descr1Prob;
    }
}

