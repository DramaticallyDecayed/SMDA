package dd.classification;

import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 20.04.2017.
 */
public class LeastSquaresSignificanceSearcher {

    public SignificanceSearchResult searchSignificance(double initialLambda, Double[][] initialCorrelationMatrix) {
        double lambda = initialLambda;
        InternalLinkMaximizer maximizer = new InternalLinkMaximizer();

        ClassificationResult previousResult
                = maximizer.classify(lambda2Significance(lambda), initialCorrelationMatrix);
        lambda = recalculateLambda(previousResult.getResult(), initialCorrelationMatrix);

        while (true) {
            ClassificationResult result
                    = maximizer.classify(lambda2Significance(lambda), initialCorrelationMatrix);
            if (result.compareClassification(previousResult)) {
                return new SignificanceSearchResult(lambda2Significance(lambda), result);
            } else {
                lambda = recalculateLambda(result.getResult(), initialCorrelationMatrix);
                previousResult = result;
            }
        }
    }

    private double lambda2Significance(double lambda){
        return lambda / 2.0;
    }

    private double recalculateLambda(Map<Integer, List<Integer>> result, Double[][] initialCorrelationMatrix) {
        double numerator = 0;
        for (List<Integer> l : result.values()) {
            for (int i = 0; i < l.size(); i++) {
                for (int j = 0; j < l.size(); j++) {
                    if (i != j) {
                        numerator += initialCorrelationMatrix[l.get(i)][l.get(j)];
                    }
                }
            }
        }
        double denominator = 0;
        for (List<Integer> l : result.values()) {
            denominator += l.size() * (l.size() - 1);
        }
        return numerator / denominator;
    }

}
