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

    public int minimaxDecision(int[][] boardState) {
        int player = logic.getNextPlayer();
        System.out.println("Player: " + player);
        double[] values = new double[logic.getColumnCount()];
        int action = -1;

        if(player == PLAYER_MIN) {
            for(int i = 0; i < logic.getColumnCount(); i++) {
                if(!logic.isColumnFull(i)) {
                    values[i] = maxValue(boardState);

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
                    values[i] = minValue(boardState);

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

    private double minValue(int[][] boardState) {
        double utility = getUtility(boardState);
        if(utility > 0) {
            return utility;
        }

        double value = Double.MAX_VALUE;
        for (int i = 0; i < logic.getColumnCount(); i++) {
            if (!logic.isColumnFull(i)) {
                // logic.insertCoin(i, PLAYER_MIN);
                logic.insertCoin(boardState);
                value = Double.min(value, maxValue(copyState(boardState)));
            }
        }
        return value;
    }

    private double maxValue(int[][] boardState) {
        double utility = getUtility(boardState);
        if(utility > 0) {
            return utility;
        }

        double value = Double.MIN_VALUE;
        for(int i = 0; i < logic.getColumnCount(); i++) {
            if(!logic.isColumnFull(i)) {
                //logic.insertCoin(i, PLAYER_MAX);
                logic.insertCoin(boardState);
                value = Double.max(value, minValue(copyState(boardState)));
            }
        }
        return value;
    }

    private double getUtility(int[][] boardState) {
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

    public int[][] copyState(int[][] currentState) {
        int[][] result = new int[logic.getColumnCount()][logic.getRowCount()];
        for(int i = 0; i < currentState.length; i++) {
            result[i] = Arrays.copyOf(currentState[i], currentState[i].length);
        }

        return result;
    }
}
