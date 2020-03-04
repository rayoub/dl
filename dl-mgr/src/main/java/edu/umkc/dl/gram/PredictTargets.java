package edu.umkc.dl.gram;

import java.util.List;
import java.util.Map;

public class PredictTargets { 

    public static void predict() {

        PredictResults results = new PredictResults();

        List<List<Target>> groups = Db.getGroupedTargets();
        for (List<Target> targets : groups) {
            PredictResults r = predict(targets);
            results.incrementTotalCount(r.getTotal());
            results.incrementCorrectCount(r.getTotalCorrect());
        }
        
        System.out.println(((double)results.getTotalCorrect()) / results.getTotal());
    }

    public static PredictResults predict(List<Target> targets) {

        Map<String, DescrProbs> map = Db.getGramProbs();

        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {

            Target target = targets.get(i);
            String ss = "_";
            String descr = target.getDescriptor();
            if (!descr.equals("_")) {
                ss = SecStruct.toSs(Integer.parseInt(descr)); 
            }
            actual.append(ss); 
        }

        StringBuilder predicted = new StringBuilder();
        for (int i = 0; i < targets.size(); i++) {
            
            Target target = targets.get(i);
           
            String ss = "_"; 
            if (!target.getDescriptor().equals("_")) {

                // we check for descriptor because we know data is good and there will be a prev and a next
                Target prev = targets.get(i - 1);
                Target next = targets.get(i + 1);

                String gram = prev.getResidueCode() + target.getResidueCode() + next.getResidueCode();
                if (map.containsKey(gram)) { 
                    DescrProbs probs = map.get(gram);
                    ss = probs.getSs(1);
                }
            }
            predicted.append(ss);
        }

        int total = 0;
        int correct = 0;
        for (int i = 0; i < actual.length(); i++) {
            
            char a = actual.charAt(i);
            char p = predicted.charAt(i);
           
            if (a != '_' && p != '_') {
                total++;
                if (a == p) {
                    correct++;
                }
            } 
        }

        System.out.println(actual.toString());
        System.out.println(predicted.toString());
        System.out.println("");
        
        PredictResults results = new PredictResults();
        results.incrementTotalCount(total);
        results.incrementCorrectCount(correct);

        return results;
    }
}

