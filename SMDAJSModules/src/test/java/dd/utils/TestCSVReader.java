package dd.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sergey on 20.04.2017.
 */
public final class TestCSVReader {

    private static final String SEPARATOR = ";";
    private static final String FILLER = "-";
    private static final String FILE_NAME = "internalLinkMaximizerTestData.csv";

    private TestCSVReader(){}

    public static Double[][] readData() throws FileNotFoundException {
        String path = TestCSVReader.class.getClassLoader().getResource(FILE_NAME).getPath();
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        List<Double[]> valueList = new ArrayList<>();
        for (int i = 0; scanner.hasNext(); i++) {
            String line = scanner.nextLine();
            String[] elements = line.split(SEPARATOR);
            if (i == 0) {
                continue;
            }
            Double[] parameterValues = new Double[elements.length - 1];
            for (int j = 0; j < elements.length; j++) {
                if (j == 0) {
                    continue;
                }
                if (elements[j].equals(FILLER)) {
                    continue;
                }
                parameterValues[j - 1] = Double.valueOf(elements[j]);
            }
            valueList.add(parameterValues);
        }
        Double[][] values = new Double[valueList.size()][];
        for (int i = 0; i < valueList.size(); i++) {
            values[i] = valueList.get(i);
        }
        return values;
    }
}
