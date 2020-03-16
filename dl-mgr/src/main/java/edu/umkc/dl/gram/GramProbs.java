package edu.umkc.dl.gram;

import java.util.HashMap;
import java.util.Map;

// descriptors from *_probs tables are always non-null integers

public class GramProbs {

    private Map<String, Double> gramProbs;
    
    public GramProbs() {
        
        this.gramProbs = new HashMap<>();
    }

    public double getProbByGram(String gram) {

        if (this.gramProbs.containsKey(gram)) {
            return this.gramProbs.get(gram);
        }
        else {
            return 0.0;
        }
    }
    
    public void setProbByGram(String gram, double gramProb) {
        this.gramProbs.put(gram, gramProb);
    }
}

