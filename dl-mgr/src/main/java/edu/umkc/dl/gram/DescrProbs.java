package edu.umkc.dl.gram;

import java.util.Arrays;

// descriptors from *_counts tables are always non-null integers

public class DescrProbs {

    public static int DESCR_COUNT = 10;

    private int[] descrs;
    private double[] descrProbs;
    private int descrIndex;
    
    public DescrProbs() {

        this.descrs = new int[DESCR_COUNT];
        this.descrProbs = new double[DESCR_COUNT];
        this.descrIndex = 0;

        Arrays.fill(this.descrs, -1);
        Arrays.fill(this.descrProbs, 0.0);
    }

    public int getDescrByRank(int rank) {
        return this.descrs[rank - 1];
    }

    public double getDescrProbByRank(int rank) {
        return this.descrProbs[rank - 1];
    }

    public double getProbByDescr(int descr) {

        int i;
        for (i = 0; i < this.descrs.length; i++) {
            if (this.descrs[i] == descr) {
                break;
            }
        }
        return this.descrProbs[i];
    }

    /* call this in order */
    public void updateDescrProbs(int descr, double descrProb) {

        this.descrs[descrIndex] = descr;
        this.descrProbs[descrIndex] = descrProb;
        this.descrIndex++;
    }
}
