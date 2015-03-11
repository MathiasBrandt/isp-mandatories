/**
 * Created by brandt on 10/03/15.
 */
public class MiniMaxer {
    public final double UTILITY_MIN = 1.0;
    public final double UTILITY_MAX = 2.0;
    public final double UTILITY_TIE = 1.5;
    public final int PLAYER_MIN = 1;
    public final int PLAYER_MAX = 2;
    public int aiPlayerId;

    public MiniMaxer(int playerId) {
        aiPlayerId = playerId;
    }

    public int minimaxDecision(GameState state) {
        System.out.println("Making decision for player " + aiPlayerId);

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        double[] values = new double[state.getColumnCount()];
        int action = -1;

        if(aiPlayerId == PLAYER_MIN) {
            for(int i = 0; i < state.getColumnCount(); i++) {
                if(!state.isColumnFull(i)) {
                    values[i] = maxValue(state.copyState(), alpha, beta);
                } else {
                    values[i] = Double.NaN;
                }
            }

            double minValue = Double.MAX_VALUE;
            for(int i = 0; i < values.length; i++) {
                System.out.println(values[i]);

                if(values[i] < minValue) {
                    action = i;
                    minValue = values[i];
                }
            }
        } else {
            for(int i = 0; i < state.getColumnCount(); i++) {
                if(!state.isColumnFull(i)) {
                    values[i] = minValue(state.copyState(), alpha, beta);
                } else {
                    values[i] = Double.NaN;
                }
            }

            double maxValue = Double.MIN_VALUE;
            for(int i = 0; i < values.length; i++) {
                System.out.println(values[i]);

                if(values[i] > maxValue) {
                    action = i;
                    maxValue = values[i];
                }
            }
        }
        return action;
    }

    private double minValue(GameState state, double alpha, double beta) {
        double utility = getUtility(state);
        if(utility > 0) {
            return utility;
        }

        double value = Double.MAX_VALUE;
        for (int i = 0; i < state.getColumnCount(); i++) {
            if (!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MIN);
                value = Double.min(value, maxValue(state.copyState(), alpha, beta));

                beta = Double.min(beta, value);

                if(value <= alpha){
                    System.out.println("Pruning");
                    return value;
                }
            }
        }

        return value;
    }

    private double maxValue(GameState state, double alpha, double beta) {
        double utility = getUtility(state);
        if(utility > 0) {
            return utility;
        }

        double value = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < state.getColumnCount(); i++) {
            if(!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MAX);
                value = Double.max(value, minValue(state.copyState(), alpha, beta));
                System.out.println(value + " " + beta);

                alpha = Double.max(alpha, value); // TODO: overall alpha or the one supplied?

                if(value >= beta){
                    System.out.println("Pruning");
                    return value;
                }

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
