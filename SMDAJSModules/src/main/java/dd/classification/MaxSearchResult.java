package dd.classification;

/**
 * Created by Sergey on 20.04.2017.
 */
public class MaxSearchResult {
    private final double max;
    private final int maxRowIndex;
    private final int maxColumnIndex;

    public MaxSearchResult(double max, int maxRowIndex, int maxColumnIndex) {
        this.max = max;
        this.maxRowIndex = maxRowIndex;
        this.maxColumnIndex = maxColumnIndex;
    }

    public double getMax() {
        return max;
    }

    public int getMaxRowIndex() {
        return maxRowIndex;
    }

    public int getMaxColumnIndex() {
        return maxColumnIndex;
    }
}
