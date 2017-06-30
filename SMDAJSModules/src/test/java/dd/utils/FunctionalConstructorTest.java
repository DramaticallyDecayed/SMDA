package dd.utils;

import dd.application.FunctionalConstructor;
import dd.classification.ClassificationResult;
import dd.classification.InternalLinkMaximizer;
import dd.classification.LeastSquaresSignificanceSearcher;
import dd.classification.SignificanceSearchResult;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 20.04.2017.
 */
public class FunctionalConstructorTest {
    @Test
    public void processTest() throws FileNotFoundException {
        String path = TestCSVReader.class.getClassLoader().getResource("data").getPath();
        Map<String, SignificanceSearchResult> functionals = new FunctionalConstructor().process(path);
        List<String> keys = new ArrayList<>(functionals.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            SignificanceSearchResult result = functionals.get(key);
            System.out.println(key
                    + " functional: " + result.getClassificationResult().getFunctional()
                    + " significance: " + result.getSignificance()
                    + " classification: " + result.getClassificationResult().getResult()

            );
        }
    }

    @Test
    public void testSingle() throws FileNotFoundException {
        String path = TestCSVReader.class.getClassLoader().getResource("kr88").getPath();
        double[][] data = SpecificFileReader.read(path);
        double[][] correlation = calculateCorrelationMatrix(data);
        Double[][] correlationMatrix = DataUtils.fromPrimitive2ObjectArray(correlation);
        InternalLinkMaximizer maximizer = new InternalLinkMaximizer();
        ClassificationResult result = maximizer.classify(0.2268171, correlationMatrix);
        System.out.println(result.getFunctional());
        System.out.println(result.getResult());


        LeastSquaresSignificanceSearcher leastSquaresSignificanceSearcher = new LeastSquaresSignificanceSearcher();
        SignificanceSearchResult significanceSearchResult =leastSquaresSignificanceSearcher.searchSignificance(0.0, correlationMatrix);
        System.out.println(significanceSearchResult.getClassificationResult().getFunctional());

    }

    private double[][] calculateCorrelationMatrix(double[][] data) {
        RealMatrix realMatrix = new PearsonsCorrelation().computeCorrelationMatrix(data);
        return realMatrix.getData();
    }
}
