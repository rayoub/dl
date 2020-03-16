package edu.umkc.dl.gram;

import java.util.Arrays;

// descriptors from *_probs tables are always non-null integers

public class DescrProbs {

    public static int DESCR_COUNT = 10;

    private double[] descrProbs; 
    
    public DescrProbs() {

        this.descrProbs = new double[DESCR_COUNT];

        Arrays.fill(this.descrProbs, 0.0);
    }

    public double getProbByDescr(int descr) {
        return this.descrProbs[descr];
    }
    
    public void setProbByDesc(int descr, double descrProb) {
        this.descrProbs[descr] = descrProb;
    }
}
