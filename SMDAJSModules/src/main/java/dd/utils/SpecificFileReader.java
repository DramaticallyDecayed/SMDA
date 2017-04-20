package dd.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sergey on 20.04.2017.
 */
public final class SpecificFileReader {
    private SpecificFileReader() {
    }

    public static double[][] read(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        List<Double[]> valueList = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", " ").trim();
            String[] elements = line.split(" ");
            Double[] parameterValues = new Double[elements.length];
            for (int j = 0; j < elements.length; j++) {
                parameterValues[j] = Double.valueOf(elements[j]);
            }
            valueList.add(parameterValues);
        }
        double[][] values = new double[valueList.size()][];
        for (int i = 0; i < valueList.size(); i++) {
            values[i] = new double[valueList.get(i).length];
            for (int j = 0; j < valueList.get(i).length; j++) {
                values[i][j] = valueList.get(i)[j];
            }
        }
        return values;
    }
}

