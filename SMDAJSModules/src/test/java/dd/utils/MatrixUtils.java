package dd.utils;

/**
 * Created by Sergey on 20.04.2017.
 */
public final class MatrixUtils {
    private MatrixUtils(){}

    public static Double[][] fromDiagonal2Full(Double[][] diagonalMatrixValues) {
        Double[][] fullMatrix = new Double[diagonalMatrixValues.length][];
        for (int i = 0; i < diagonalMatrixValues.length; i++) {
            fullMatrix[i] = new Double[fullMatrix.length];
            for (int j = 0; j < diagonalMatrixValues[i].length; j++) {
                if (i == j) {
                    fullMatrix[i][j] = null;
                } else if (j > i) {
                    fullMatrix[i][j] = diagonalMatrixValues[j][i];
                } else {
                    fullMatrix[i][j] = diagonalMatrixValues[i][j];
                }
            }
        }
        return fullMatrix;
    }
}
