package edu.umkc.dl.gram;

// descriptors from *_counts tables are always non-null integers

public class DescrProbs {

    private int[] descrs;
    private double[] descrProbs;
    private int descrIndex;

    private String[] ss;
    private double[] ssProbs;
    private String[] ssWorking;
    private double[] ssWorkingProbs;
    
    public DescrProbs() {

        this.descrs = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        this.descrProbs = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; 

        this.ss = new String[] { "", "", "" };
        this.ssProbs = new double[] { 0, 0, 0 };
       
        this.descrIndex = 0;
        this.ssWorking = new String[] { "H", "S", "C" }; 
        this.ssWorkingProbs = new double[] { 0, 0, 0 };
    }

    public int getDescrByRank(int rank) {
        return this.descrs[rank - 1];
    }

    public double getDescrProbByRank(int rank) {
        return this.descrProbs[rank - 1];
    }
    
    public String getSsByRank(int rank) {
        return this.ss[rank - 1];
    }

    public double getSsProbByRank(int rank) {
        return this.ssProbs[rank - 1];
    }

    public double getProbByDescr(int descr) {

        int i;
        for (i = 0; i < this.descrs.length; i++) {
            if (this.descrs[i] == descr) {
                break;
            }
        }
        return  this.descrProbs[i];
    }

    public double getProbBySs(String ss) {

        int i;
        for (i = 0; i < this.ss.length; i++) {
            if (this.ss[i].equals(ss)) {
                break;
            }
        }
        return  this.ssProbs[i];
    }

    /* call this in order */
    public void updateDescrProb(int descr, double descrProb) {

        this.descrs[descrIndex] = descr;
        this.descrProbs[descrIndex] = descrProb;
        this.descrIndex++;

        // update working probs
        this.ssWorkingProbs[SecStruct.toSsIndex(descr)] += descrProb; 

        // update ss probs
        // this is not very pretty
        int i1, i2, i3;
        if (this.ssWorkingProbs[0] >= this.ssWorkingProbs[1]) {

            // 0 > 1
        
            if (this.ssWorkingProbs[1] >= this.ssWorkingProbs[2]) {
                
                // 0 > 1, 1 > 2
        
                i1 = 0;
                i2 = 1;
                i3 = 2;
            } 
            else if (this.ssWorkingProbs[0] >= this.ssWorkingProbs[2]) {

                // 0 > 1, 2 > 1, 0 > 2
                
                i1 = 0;
                i2 = 2;
                i3 = 1;
            }
            else {

                // 0 > 1, 2 > 1, 2 > 0
                
                i1 = 2;
                i2 = 0;
                i3 = 1;
            }
        }
        else {

            // 1 > 0
    
            if (this.ssWorkingProbs[2] >= this.ssWorkingProbs[1]) {

                // 1 > 0, 2 > 1 
            
                i1 = 2;
                i2 = 1;
                i3 = 0;
            }
            else if (this.ssWorkingProbs[0] >= this.ssWorkingProbs[2]) {

                // 1 > 0, 1 > 2, 0 > 2
            
                i1 = 1;
                i2 = 0;
                i3 = 2;
            }
            else {

                // 1 > 0, 1 > 2, 2 > 0

                i1 = 1;
                i2 = 2;
                i3 = 0;
            }
        }

        this.ss[0] = this.ssWorking[i1];
        this.ssProbs[0] = this.ssWorkingProbs[i1];
        this.ss[1] = this.ssWorking[i2];
        this.ssProbs[1] = this.ssWorkingProbs[i2];
        this.ss[2] = this.ssWorking[i3];
        this.ssProbs[2] = this.ssWorkingProbs[i3];
    }
}
