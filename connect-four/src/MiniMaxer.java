import com.sun.tools.javac.util.Pair;

/**
 * Created by brandt on 10/03/15.
 */
public class MiniMaxer {
    public final double UTILITY_MIN = 1.0;
    public final double UTILITY_MAX = 2.0;
    public final double UTILITY_TIE = 1.5;
    public final int PLAYER_MIN = 1;
    public final int PLAYER_MAX = 2;

    private SimlMfliGameLogic logic;

    public Pair<Integer, Integer> minimaxDecision(SimlMfliGameLogic logic) {
        this.logic = logic;

        return null;
    }

    private double minValue() {
        return UTILITY_MIN;
    }

    private double maxValue() {
        double utility = utility();
        if(utility > 0) {
            return utility;
        }

        double value = Double.MAX_VALUE;
        for(int i = 0; i < logic.getColumnCount(); i++) {
            if(!logic.isColumnFull(i)) {
                logic.insertCoin(i, PLAYER_MAX);
                value = Double.max(value, minValue());
            }
        }
    }

    private double utility() {
        switch(logic.gameFinished()) {
            case PLAYER1:
                return UTILITY_MIN;
            case PLAYER2:
                return UTILITY_MAX;
            case TIE:
                return UTILITY_TIE;
            case NOT_FINISHED:
                return -1;
        }
    }
}
