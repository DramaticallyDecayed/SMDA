package dd.classification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 20.04.2017.
 */
public class ClassificationResult implements Serializable{
    private final double functional;
    private final Map<Integer, List<Integer>> result;

    public ClassificationResult(double significance, Map<Integer, List<Integer>> result) {
        this.functional = significance;
        this.result = result;
    }

    public double getFunctional() {
        return functional;
    }

    public Map<Integer, List<Integer>> getResult() {
        return result;
    }

    public boolean compareClassification(ClassificationResult other) {
        if (result.size() == other.getResult().size()) {
            for (Integer key : result.keySet()) {
                if (other.getResult().containsKey(key)) {
                    List<Integer> curRes = result.get(key);
                    List<Integer> prevRes = other.getResult().get(key);
                    if(curRes.size() != prevRes.size()){
                        return false;
                    }
                    for (int i = 0; i < curRes.size(); i++) {
                        if (curRes.get(i) != prevRes.get(i)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public String toString(){
        return "functional: " + functional + " classification: " + result;
    }
}
