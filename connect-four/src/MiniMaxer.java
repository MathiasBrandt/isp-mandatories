import com.sun.tools.javac.util.Pair;

/**
 * Created by brandt on 10/03/15.
 */
public class MiniMaxer {
    public final int UTILITY_MIN = 1;
    public final int UTILITY_MAX = 2;

    public Pair<Integer, Integer> minimaxDecision() {
        return null;
    }

    private int minValue(int column, int row) {
        return UTILITY_MIN;
    }

    private int maxValue(int column, int row) {
        return UTILITY_MAX;
    }

    private Boolean terminalTest(int column, int row) {
        return false;
    }
}
