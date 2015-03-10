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

    public MiniMaxer(SimlMfliGameLogic logic) {
        this.logic = logic;
    }

    public int minimaxDecision() {
        int player = logic.getNextPlayer();
        System.out.println("Player: " + player);
        double[] values = new double[logic.getColumnCount()];
        int action = -1;

        if(player == PLAYER_MIN) {
            for(int i = 0; i < logic.getColumnCount(); i++) {
                if(!logic.isColumnFull(i)) {
                    values[i] = maxValue();

                    logic.resetToOriginalState();
                }
            }

            double minValue = Double.MAX_VALUE;
            for(int i = 0; i < values.length; i++) {
                if(values[i] < minValue) {
                    action = i;
                }
            }
        } else {
            for(int i = 0; i < logic.getColumnCount(); i++) {
                if(!logic.isColumnFull(i)) {
                    values[i] = minValue();
                }
            }

            double maxValue = Double.MIN_VALUE;
            for(int i = 0; i < values.length; i++) {
                if(values[i] > maxValue) {
                    action = i;
                }
            }
        }

        return action;
    }

    private double minValue() {
        double utility = getUtility();
        if(utility > 0) {
            return utility;
        }

        double value = Double.MIN_VALUE;
        for (int i = 0; i < logic.getColumnCount(); i++) {
            if (!logic.isColumnFull(i)) {
                logic.insertCoin(i, PLAYER_MIN);
                value = Double.min(value, maxValue());
            }
        }

        return value;
    }

    private double maxValue() {
        double utility = getUtility();
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

        return value;
    }

    private double getUtility() {
        switch(logic.gameFinished()) {
            case PLAYER1:
                return UTILITY_MIN;
            case PLAYER2:
                return UTILITY_MAX;
            case TIE:
                return UTILITY_TIE;
        }

        return -1;
    }
}
