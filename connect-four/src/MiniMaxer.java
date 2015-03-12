import java.util.Collections;

/**
 * Created by brandt on 10/03/15.
 */
public class MiniMaxer {
    public final double UTILITY_MIN = -100;
    public final double UTILITY_MAX = 100;
    public final double UTILITY_TIE = 0;
    public static final int PLAYER_BLANK = 0;
    public final int PLAYER_MIN = 1;
    public final int PLAYER_MAX = 2;
    public int aiPlayerId;
    public final int CUTOFF = 1;

    public MiniMaxer(int playerId) {
        aiPlayerId = playerId;
        System.out.println("AI Player id is: " + aiPlayerId);
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
                    GameState copyState = state.copyState();
                    copyState.insertCoin(i, PLAYER_MIN);
                    minimaxValues[i] = maxValue(copyState, alpha, beta, 0);
                } else {
                    // if the action is not in the list of possible actions, assign a value to indicate such.
                    minimaxValues[i] = Double.NaN;
                }
            }

            // initialize with dummy value
            double minValue = Double.POSITIVE_INFINITY;

            // go through all of the calculated minimax values to pick the best corresponding action
            System.out.println("Minimizer Choosing");
            for(int i = 0; i < minimaxValues.length; i++) {
                System.out.println(minimaxValues[i]);
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
                    GameState copyState = state.copyState();
                    copyState.insertCoin(i, PLAYER_MAX);
                    minimaxValues[i] = minValue(copyState, alpha, beta, 0);
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
                GameState copyState = state.copyState();
                copyState.insertCoin(i, PLAYER_MIN);
                value = Double.min(value, maxValue(copyState, alpha, beta, depth));

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
                GameState copyState = state.copyState();
                copyState.insertCoin(i, PLAYER_MAX);
                value = Double.max(value, minValue(copyState, alpha, beta, depth));

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
     * Counts the maximum amount of coins that are connected in a row based upon the lastCoinPosition. Counts for both the current player and the opponent and returns the highest number.
     * @param state
     * @return
     */
    public int maxCoinsInARow(GameState state){
        int initialColumn = state.getLastCoinPosition().fst;
        int initialRow = state.getLastCoinPosition().snd;
        int playerID = state.getBoard()[initialColumn][initialRow];

        int count = Integer.max(state.checkDiagonalOne(playerID, initialColumn, initialRow, false), state.checkDiagonalTwo(playerID, initialColumn, initialRow, false));
        count = Integer.max(count, state.checkHorizontal(playerID, initialColumn, initialRow, false));
        count = Integer.max(count, state.checkVertical(playerID, initialColumn, initialRow, false));

        // Create score for adversary
        if(playerID == PLAYER_MIN){
            playerID = PLAYER_MAX;
        } else if (playerID == PLAYER_MAX){
            playerID = PLAYER_MIN;
        } else {
            System.out.println("Something went wrong");
        }

        int opponentCount = Integer.max(state.checkDiagonalOne(playerID, initialColumn, initialRow, false), state.checkDiagonalTwo(playerID, initialColumn, initialRow, false));
        opponentCount = Integer.max(opponentCount, state.checkHorizontal(playerID, initialColumn, initialRow, false));
        opponentCount = Integer.max(opponentCount, state.checkVertical(playerID, initialColumn, initialRow, false));

        return Integer.max(opponentCount, count);
    }

    /**
     *
     * @param state
     * @return the number of directions that is a possible win (i.e., has 4 coins or blanks in a row)
     */
    public int winCombinationsCount(GameState state){
        int initialColumn = state.getLastCoinPosition().fst;
        int initialRow = state.getLastCoinPosition().snd;
        int playerID = state.getBoard()[initialColumn][initialRow];

        int possibleWins = 0;
        if(state.checkHorizontal(playerID, initialColumn, initialRow, true) >= GameState.WIN_CONDITION) { possibleWins++; }
        if(state.checkVertical(playerID, initialColumn, initialRow, true) >= GameState.WIN_CONDITION) { possibleWins++; }
        if(state.checkDiagonalOne(playerID, initialColumn, initialRow, true) >= GameState.WIN_CONDITION) { possibleWins++; }
        if(state.checkDiagonalTwo(playerID, initialColumn, initialRow, true) >= GameState.WIN_CONDITION) { possibleWins++; }

        return possibleWins;
    }

    /**
     * Looks up static evaluation table and returns a value based on the {@code} lastCoinPosition.
     * Credit goes to: http://programmers.stackexchange.com/questions/263514/why-does-this-evaluation-function-work-in-a-connect-four-game-in-java
     * @param state
     * @return
     */
    public int coinPositionValue(GameState state){
        // For now, this is only implemented for a 7x6 board.
        if(!(state.getColumnCount() == 7) || !(state.getRowCount() == 6)){
            return 0;
        }

        int[][] evaluationTable = {
                {3, 4, 5, 7, 5, 4, 3},
                {4, 6, 8, 10, 8, 6, 4},
                {5, 8, 11, 13, 11, 8, 5},
                {5, 8, 11, 13, 11, 8, 5},
                {4, 6, 8, 10, 8, 6, 4},
                {3, 4, 5, 7, 5, 4, 3}
        };

        int initialColumn = state.getLastCoinPosition().fst;
        int initialRow = state.getLastCoinPosition().snd;
        int playerID = state.getBoard()[initialColumn][initialRow];

        return evaluationTable[initialRow][initialColumn];
    }

    /**
     * If the game has finished in the current state, i.e., the board is full or one of the
     * players has won, return the corresponding utility value. Otherwise return -1
     * @param state the current state
     * @return the utility value of the winning player, or -1 if the game is not over
     */
    private double eval(GameState state){
        //
        switch(state.gameFinished()) {
            case PLAYER1:
                System.out.println("player1");
                return UTILITY_MIN;
            case PLAYER2:
                System.out.println("player2");
                return UTILITY_MAX;
            case TIE:
                System.out.println("tie");
                return UTILITY_TIE;
        }

        int column = state.getLastCoinPosition().fst;
        int row = state.getLastCoinPosition().snd;
        int playerID = state.getBoard()[column][row];

        int score = 0;

        int coinsInARow = maxCoinsInARow(state);
        if(coinsInARow >= GameState.WIN_CONDITION){
            if(playerID == PLAYER_MIN){
                return UTILITY_MIN;
            } else {
                return UTILITY_MAX;
            }
        } else {
            if(playerID == PLAYER_MIN){
                score -= coinsInARow;
            } else {
                score += coinsInARow;
            }
        }

        int positionValue = coinPositionValue(state);
        if(playerID == PLAYER_MIN){
            score += -positionValue;
        } else {
            score += positionValue;
        }

        int winCombinations = winCombinationsCount(state);

        if(winCombinations <= 0){
            if(playerID == PLAYER_MIN){
                return UTILITY_MAX;
            } else {
                return UTILITY_MIN;
            }
        } else {
            if(playerID == PLAYER_MIN){
                score += -winCombinations;
            } else {
                score += winCombinations;
            }
        }

        System.out.println("The score is: " + score);
        return score;
    }
}
