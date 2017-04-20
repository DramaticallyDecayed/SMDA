package dd.utils;

import dd.application.FunctionalConstructor;
import dd.classification.SignificanceSearchResult;
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
}
