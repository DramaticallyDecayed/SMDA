package dd.classification;

import java.util.*;

/**
 * Created by Sergey on 20.04.2017.
 */
public class InternalLinkMaximizer {

    private static final double MAX_LIMIT = 0.0;

    public ClassificationResult classify(double significance, Double[][] initialCorrelationMatrix) {
        Double[][] correlationMatrix = copyCorrelationMatrix(initialCorrelationMatrix);
        considerThreshold(correlationMatrix, significance);
        Map<Integer, List<Integer>> result = new HashMap<>();
        initializeResult(result, initialCorrelationMatrix.length);
        while (true) {
            MaxSearchResult maxSearchResult = findMax(correlationMatrix);
            if (maxSearchResult.getMax() <= MAX_LIMIT) {
                break;
            }
            mergeCorrelations(
                    correlationMatrix,
                    maxSearchResult.getMaxRowIndex(),
                    maxSearchResult.getMaxColumnIndex()
            );
            correlationMatrix =
                    collapseCorrelationMatrix(correlationMatrix, maxSearchResult.getMaxColumnIndex());
            mergeClasses(result, maxSearchResult.getMaxRowIndex(), maxSearchResult.getMaxColumnIndex());
        }
        sortResultClasses(result);
        return new ClassificationResult(
                calculateFunctional(result, initialCorrelationMatrix, significance),
                result
        );
    }

    private void initializeResult(Map<Integer, List<Integer>> result, int size) {
        for (int i = 0; i < size; i++) {
            List list = new ArrayList<>();
            list.add(i);
            result.put(i, list);
        }
    }

    private void sortResultClasses(Map<Integer, List<Integer>> result) {
        for (List<Integer> l : result.values()) {
            Collections.sort(l);
        }
    }

    private void considerThreshold(Double[][] correlationMatrix, double significance) {
        for (int i = 0; i < correlationMatrix.length; i++) {
            for (int j = 0; j < correlationMatrix[i].length; j++) {
                if (i != j) {
                    correlationMatrix[i][j] -= significance;
                }
            }
        }
    }

    private Double[][] copyCorrelationMatrix(Double[][] correlationMatrix) {
        Double[][] reference = new Double[correlationMatrix.length][correlationMatrix.length];
        for (int i = 0; i < correlationMatrix.length; i++) {
            for (int j = 0; j < correlationMatrix[i].length; j++) {
                if (correlationMatrix[i][j] != null) {
                    reference[i][j] = new Double(correlationMatrix[i][j]);
                } else {
                    reference[i][j] = null;
                }
            }
        }
        return reference;
    }

    private double calculateFunctional(
            Map<Integer, List<Integer>> result,
            Double[][] initialCorrelationMatrix,
            double significance

    ) {
        double functional = 0;
        for (List<Integer> l : result.values()) {
            for (int i = 0; i < l.size(); i++) {
                for (int j = 0; j < l.size(); j++) {
                    if (i != j) {
                        functional += (initialCorrelationMatrix[l.get(i)][l.get(j)] - significance);
                    }
                }
            }
        }
        return functional;
    }

    private void mergeClasses(Map<Integer, List<Integer>> result, int maxRowIndex, int maxColumnIndex) {
        List<Integer> miList = result.get(maxRowIndex);
        miList.addAll(result.get(maxColumnIndex));
        result.put(maxRowIndex, miList);
        for (int i = maxColumnIndex; i < result.size(); i++) {
            result.put(i, result.get(i + 1));
        }
        result.remove(result.size() - 1);
    }

    private void mergeCorrelations(Double[][] correlationMatrix, int maxRowIndex, int maxColumnIndex) {
        for (int i = 0; i < correlationMatrix.length; i++) {
            if (maxRowIndex != i && maxColumnIndex != i) {
                correlationMatrix[maxRowIndex][i] =
                        correlationMatrix[maxRowIndex][i] + correlationMatrix[maxColumnIndex][i];
                correlationMatrix[i][maxRowIndex] =
                        correlationMatrix[i][maxRowIndex] + correlationMatrix[i][maxColumnIndex];
            }
        }
    }

    private Double[][] collapseCorrelationMatrix(Double[][] correlationMatrix, int maxColumnIndex) {
        Double[][] collapsedCorrelationMatrix =
                new Double[correlationMatrix.length - 1][correlationMatrix.length - 1];
        for (int i1 = 0, i2 = 0; i1 < correlationMatrix.length; i1++, i2++) {
            if (i1 == maxColumnIndex) {
                i2--;
                continue;
            }
            for (int j1 = 0, j2 = 0; j1 < correlationMatrix[i1].length; j1++, j2++) {
                if (j1 == maxColumnIndex) {
                    j2--;
                    continue;
                }
                collapsedCorrelationMatrix[i2][j2] = correlationMatrix[i1][j1];
            }
        }
        return collapsedCorrelationMatrix;
    }

    private MaxSearchResult findMax(Double[][] correlationMatrix) {
        double max = MAX_LIMIT;
        int maxRowIndex = -1;
        int maxColumnIndex = -1;
        for (int i = 0; i < correlationMatrix.length; i++) {
            for (int j = 0; j < correlationMatrix[i].length; j++) {
                if (correlationMatrix[i][j] != null) {
                    if (max < correlationMatrix[i][j]) {
                        max = correlationMatrix[i][j];
                        maxRowIndex = i;
                        maxColumnIndex = j;
                    }
                }
            }
        }
        return new MaxSearchResult(max, maxRowIndex, maxColumnIndex);
    }
}
