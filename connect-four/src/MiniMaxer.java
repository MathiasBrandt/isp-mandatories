import java.util.Collections;

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
    public final int CUTOFF = 10;

    public MiniMaxer(int playerId) {
        aiPlayerId = playerId;
    }

    /**
     * In the current state, calculate the best possible action.
     * @param state the current state
     * @return the best possible action
     */
    public int minimaxDecision(GameState state) {
        // the minimaxValues array will hold the minimax values for each action (i.e., column) in the current state
        double[] minimaxValues = new double[state.getColumnCount()];

        // the best action to pick
        int bestAction = 0;

        // initialize alpha and beta with dummy values
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        // if the AI is playing as MIN we want to find the lowest minimax value.
        if(aiPlayerId == PLAYER_MIN) {
            // go through each possible action (i.e., column) in the current state
            for(int i = 0; i < state.getColumnCount(); i++) {
                // if the action is actually in the list of possible actions, i.e., if the column is NOT full
                if(!state.isColumnFull(i)) {
                    // calculate the minimax value for this action
                    minimaxValues[i] = maxValue(state.copyState(), alpha, beta, 0);
                } else {
                    // if the action is not in the list of possible actions, assign a value to indicate such.
                    minimaxValues[i] = Double.NaN;
                }
            }

            // initialize with dummy value
            double minValue = Double.POSITIVE_INFINITY;

            // go through all of the calculated minimax values to pick the best corresponding action
            for(int i = 0; i < minimaxValues.length; i++) {
                //System.out.println(minimaxValues[i]);
                if(minimaxValues[i] < minValue) {
                    bestAction = i;
                    minValue = minimaxValues[i];
                }
            }
        }
        // if the AI is playing as MAX the operations are the same as if the AI was
        // playing as MIN, except now we want the highest minimax value.
        // Comments have been omitted.
        else {
            for(int i = 0; i < state.getColumnCount(); i++) {
                if(!state.isColumnFull(i)) {
                    minimaxValues[i] = minValue(state.copyState(), alpha, beta, 0);
                } else {
                    minimaxValues[i] = Double.NaN;
                }
            }

            double maxValue = Double.NEGATIVE_INFINITY;
            for(int i = 0; i < minimaxValues.length; i++) {
                System.out.println(minimaxValues[i]);

                if(minimaxValues[i] > maxValue) {
                    bestAction = i;
                    maxValue = minimaxValues[i];
                }
            }
        }

        return bestAction;
    }

    /**
     * Returns the lowest utility value for the current state.
     * @param state the current state
     * @param alpha the parent node's alpha value
     * @param beta the parent node's beta value
     * @return the lowest utility value
     */
    private double minValue(GameState state, double alpha, double beta, int depth) {
        if(cutoffTest(state, depth)){
            return eval(state);
        }
        depth++;

        // initialize with dummy value
        double value = Double.POSITIVE_INFINITY;

        // try to perform each possible action and keep track of the best one
        for (int i = 0; i < state.getColumnCount(); i++) {
            if (!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MIN);
                value = Double.min(value, maxValue(state.copyState(), alpha, beta, depth));

                // if the value is lower than the parent's alpha value, prune the tree
                if(value <= alpha){
                    return value;
                }

                // if not, record new beta value
                beta = Double.min(beta, value);
            }
        }

        return value;
    }

    /**
     * Returns the highest utility value for the current state.
     * @param state the current state
     * @param alpha the parent node's alpha value
     * @param beta the parent node's beta value
     * @return the highest utility value
     */
    private double maxValue(GameState state, double alpha, double beta, int depth) {
        // NOTE: comments have been omitted since they are more or less the same as in the minValue method
        if(cutoffTest(state, depth)){
            return eval(state);
        }
        depth++;

        double value = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < state.getColumnCount(); i++) {
            if(!state.isColumnFull(i)) {
                state.insertCoin(i, PLAYER_MAX);
                value = Double.max(value, minValue(state.copyState(), alpha, beta, depth));

                if(value >= beta){
                    return value;
                }

                alpha = Double.max(alpha, value);
            }
        }

        return value;
    }

    private boolean cutoffTest(GameState state, int depth) {
        if(depth >= CUTOFF){
            return true;
        }

        switch(state.gameFinished()) {
            case PLAYER1:
                return true;
            case PLAYER2:
                return true;
            case TIE:
                return true;
        }

        return false;
    }

    /**
     * Counts the maximum amount of coins that are connected in a row based upon the lastCoinPosition.
     * @param state
     * @return
     */
    public int maxCoinsInARow(GameState state){



        return 0;
    }

    /**
     *
     * @param state
     * @return amount of win combinations that the lastCoinPosition is a part of.
     */
    public int winCombinationsCount(GameState state){


        return 0;
    }

    /**
     * Looks up static evaluation table and returns a value based on the {@code} lastCoinPosition.
     * @param state
     * @return
     */
    public int coinPositionValue(GameState state){

        return 0;
    }

    /**
     * If the game has finished in the current state, i.e., the board is full or one of the
     * players has won, return the corresponding utility value. Otherwise return -1
     * @param state the current state
     * @return the utility value of the winning player, or -1 if the game is not over
     */
    private double eval(GameState state){
        //
        int score = 0;

        score += maxCoinsInARow(state);
        score += winCombinationsCount(state);
        score += coinPositionValue(state);

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
