package dd.application;

import dd.classification.LeastSquaresSignificanceSearcher;
import dd.classification.SignificanceSearchResult;
import dd.utils.FolderReader;
import dd.utils.SpecificFileReader;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Sergey on 20.04.2017.
 */
public class FunctionalConstructor {

    public static void main(String... args) throws FileNotFoundException, UnsupportedEncodingException {
        if (args.length == 0) {
            System.err.println("Too few arguments! Give relative file path!");
            System.exit(-1);
        }
        Map<String, SignificanceSearchResult> functionals = new FunctionalConstructor().process(args[0]);
        List<String> keys = new ArrayList<>(functionals.keySet());
        Collections.sort(keys);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("out.txt", "UTF-8");
            for (String key : keys) {
                SignificanceSearchResult result = functionals.get(key);
                String line = key
                        + " functional: " + result.getClassificationResult().getFunctional()
                        + " significance: " + result.getSignificance()
                        + " classification: " + result.getClassificationResult().getResult();
                writer.println(line);
            }

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public Map<String, SignificanceSearchResult> process(String arg) throws FileNotFoundException {
        Map<String, String> fileNames = FolderReader.listFilesForFolder(new File(arg));
        Map<String, SignificanceSearchResult> functionals = new HashMap<>();
        for (String fileName : fileNames.keySet()) {
            double[][] data = SpecificFileReader.read(fileNames.get(fileName));
            double[][] correlation = calculateCorrelationMatrix(data);
            Double[][] correlationMatrix = adaptData(correlation);
            double initialLambda = 0.1;
            LeastSquaresSignificanceSearcher searcher = new LeastSquaresSignificanceSearcher();
            SignificanceSearchResult result =
                    searcher.searchSignificance(initialLambda, correlationMatrix);
            functionals.put(fileName, result);
        }
        return functionals;
    }

    private Double[][] adaptData(double[][] correlation) {
        Double[][] correlationMatrix = new Double[correlation.length][correlation.length];
        for (int i = 0; i < correlation.length; i++) {
            for (int j = 0; j < correlation[i].length; j++) {
                if (i == j) {
                    correlationMatrix[i][j] = null;
                    continue;
                }
                correlationMatrix[i][j] = correlation[i][j];
            }
        }
        return correlationMatrix;
    }

    private double[][] calculateCorrelationMatrix(double[][] data) {
        RealMatrix realMatrix = new PearsonsCorrelation().computeCorrelationMatrix(data);
        return realMatrix.getData();
    }
}
