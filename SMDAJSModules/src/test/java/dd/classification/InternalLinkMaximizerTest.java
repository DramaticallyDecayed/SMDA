package dd.classification;

import dd.utils.MatrixUtils;
import dd.utils.TestCSVReader;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by Sergey on 20.04.2017.
 */
public class InternalLinkMaximizerTest {

    private Double[][] correlationMatrix;
    private Integer[] renumbering = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 24, 25, 26, 12, 13, 22, 23, 20, 21, 18, 19, 16, 17, 14, 15, 27, 28, 29, 30, 31, 32, 33};
    private InternalLinkMaximizer maximizer = new InternalLinkMaximizer();

    @Before
    public void initData() throws FileNotFoundException {
        Double[][] diagonalMatrixValues = TestCSVReader.readData();
        correlationMatrix = MatrixUtils.fromDiagonal2Full(diagonalMatrixValues);
    }

    @Test
    public void classifyTest() {
        ClassificationResult result = maximizer.classify(0.2, correlationMatrix);
        renumbering(result);
        sortResultClasses(result.getResult());
        ClassificationResult test = createEthalonFor0Dot2();
        assertTrue(result.compareClassification(test));
    }

    private ClassificationResult createEthalonFor0Dot2() {
        Map<Integer, List<Integer>> test = new HashMap<>();
        test.put(0,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7}
                ))
        );
        test.put(1,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{8, 9}
                ))
        );
        test.put(2,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{10, 11}
                ))
        );
        test.put(3,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25}
                ))
        );
        test.put(4,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{26, 27}
                ))
        );
        test.put(5,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{28, 29}
                ))
        );
        test.put(6,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{30, 31}
                ))
        );
        test.put(7,
                new ArrayList<Integer>(Arrays.asList(
                        new Integer[]{32, 33}
                ))
        );
        return new ClassificationResult(0, test);
    }

    private void sortResultClasses(Map<Integer, List<Integer>> result) {
        for (List<Integer> l : result.values()) {
            Collections.sort(l);
        }
    }

    private void renumbering(ClassificationResult result) {
        for (List<Integer> l : result.getResult().values()) {
            for (int i = 0; i < l.size(); i++) {
                l.set(i, renumbering[l.get(i)]);
            }
        }
    }


}
