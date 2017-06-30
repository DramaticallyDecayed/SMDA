package dd.classification;

import java.io.Serializable;

/**
 * Created by Sergey on 20.04.2017.
 */
public class SignificanceSearchResult implements Serializable {
    private final double significance;
    private final ClassificationResult classificationResult;

    public SignificanceSearchResult(double significance, ClassificationResult classificationResult) {
        this.significance = significance;
        this.classificationResult = classificationResult;
    }


    public double getSignificance() {
        return significance;
    }

    public ClassificationResult getClassificationResult() {
        return classificationResult;
    }

    public String toString() {
        return "significance: " + significance + " " + classificationResult.toString();
    }
}
