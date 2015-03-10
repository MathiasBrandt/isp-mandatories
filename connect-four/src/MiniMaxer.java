import java.util.Arrays;

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

    public int minimaxDecision(GameState state) {
        int player = logic.getNextPlayer();
        System.out.println("Player: " + player);
        double[] values = new double[logic.getColumnCount()];
        int action = -1;

        if(player == PLAYER_MIN) {
            for(int i = 0; i < logic.getColumnCount(); i++) {
                if(!logic.isColumnFull(i)) {
                    values[i] = maxValue(state);

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
                    values[i] = minValue(state);

                    logic.resetToOriginalState();
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

    private double minValue(GameState state) {
        double utility = getUtility(state);
        if(utility > 0) {
            return utility;
        }

        double value = Double.MAX_VALUE;
        for (int i = 0; i < logic.getColumnCount(); i++) {
            if (!logic.isColumnFull(i)) {
                // logic.insertCoin(i, PLAYER_MIN);
                logic.insertCoin(state);
                value = Double.min(value, maxValue(state.copy()));
            }
        }
        return value;
    }

    private double maxValue(GameState state) {
        double utility = getUtility(state);
        if(utility > 0) {
            return utility;
        }

        double value = Double.MIN_VALUE;
        for(int i = 0; i < logic.getColumnCount(); i++) {
            if(!logic.isColumnFull(i)) {
                //logic.insertCoin(i, PLAYER_MAX);
                logic.insertCoin(state);
                value = Double.max(value, minValue(state.copy()));
            }
        }
        return value;
    }

    private double getUtility(GameState boardState) {
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
