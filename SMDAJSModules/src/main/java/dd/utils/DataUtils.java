package dd.utils;

/**
 * Created by Sergey on 23.04.2017.
 */
public final class DataUtils {
    private DataUtils(){}

    public static Double[][] fromPrimitive2ObjectArray(double[][] correlation) {
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
}
