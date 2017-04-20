package dd.classification;

import dd.utils.MatrixUtils;
import dd.utils.TestCSVReader;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sergey on 20.04.2017.
 */
public class LeastSquaresSignificanceSearcherTest {
    private Double[][] correlationMatrix;

    @Before
    public void initData() throws FileNotFoundException {
        Double[][] diagonalMatrixValues = TestCSVReader.readData();
        correlationMatrix = MatrixUtils.fromDiagonal2Full(diagonalMatrixValues);
    }

    @Test
    public void searchSignificanceTest() {
        double initialLambda = 0.1;
        LeastSquaresSignificanceSearcher searcher = new LeastSquaresSignificanceSearcher();
        SignificanceSearchResult result =
                searcher.searchSignificance(initialLambda, correlationMatrix);
        assertEquals(0.287, result.getSignificance(), 0.001d);

    }
}
