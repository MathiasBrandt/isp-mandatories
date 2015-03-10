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


    public int minimaxDecision(GameState state) {
        int player = state.getNextPlayer();
        System.out.println("Player: " + player);
        double[] values = new double[state.getColumnCount()];
        int action = -1;

        if(player == PLAYER_MIN) {
            for(int i = 0; i < state.getColumnCount(); i++) {
                if(!state.isColumnFull(i)) {
                    values[i] = maxValue(state.copyState());
                }
            }

            double minValue = Double.MAX_VALUE;
            for(int i = 0; i < values.length; i++) {
                if(values[i] < minValue) {
                    System.out.println(values[i]);
                    action = i;
                }
            }
        } else {
            for(int i = 0; i < state.getColumnCount(); i++) {
                if(!state.isColumnFull(i)) {
                    values[i] = minValue(state.copyState());
                }
            }

            double maxValue = Double.MIN_VALUE;
            for(int i = 0; i < values.length; i++) {
                if(values[i] > maxValue) {
                    System.out.println(values[i]);
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
        for (int i = 0; i < state.getColumnCount(); i++) {
            if (!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MIN);
                value = Double.min(value, maxValue(state.copyState()));
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
        for(int i = 0; i < state.getColumnCount(); i++) {
            if(!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MAX);
                value = Double.max(value, minValue(state.copyState()));
            }
        }

        return value;
    }

    private double getUtility(GameState state) {
        switch(state.gameFinished()) {
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
