package edu.umkc.dl.gram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PredictNaive { 

    public static List<PredictResult> predict() {

        List<PredictResult> results = new ArrayList<>();
        List<List<Target>> groups = Db.getGroupedTargets();
        for (List<Target> targets : groups) {
            results.add(predict(targets));
        }
        return results;    
    }

    public static PredictResult predict(List<Target> targets) {

        Map<String, DescrProbs> map = Db.getGramDescrProbs();

        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {

            Target target = targets.get(i);
            String descr = target.getDescriptor();
            actual.append(descr); 
        }

        StringBuilder predicted = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {
            
            Target target = targets.get(i);
           
            int descr = -1; 
            if (!target.getDescriptor().equals("_")) {

                // we check for descriptor because we know data is good and there will be a prev and a next
                Target prev = targets.get(i - 1);
                Target next = targets.get(i + 1);

                String gram = prev.getResidueCode() + target.getResidueCode() + next.getResidueCode();
                if (map.containsKey(gram)) { 
                    DescrProbs probs = map.get(gram);
                    descr = probs.getDescrByRank(1);
                }
            }
            if (descr == -1) {
                predicted.append("_");
            }
            else {
                predicted.append(descr);
            }
        }
        
        PredictResult results = new PredictResult(actual, predicted);

        return results;
    }
}

