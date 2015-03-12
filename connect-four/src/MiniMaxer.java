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
    public int otherPlayerId;
    public final int CUTOFF = 5;

    public MiniMaxer(int playerId) {
        aiPlayerId = playerId;
        if(playerId == PLAYER_MAX){
            otherPlayerId = PLAYER_MIN;
        } else {
            otherPlayerId = PLAYER_MAX;
        }
        System.out.println("AI Player id is: " + aiPlayerId);
        System.out.println("Other player id is: " + otherPlayerId);
    }

    /**
     * In the current state, calculate the best possible action.
     * @param state the current state
     * @return the best possible action
     */
    public int minimaxDecision(GameState state) {
        // the best action to pick
        int bestColumn = -1;
        double bestValue = Double.NEGATIVE_INFINITY;

        // go through each possible action (i.e., column) in the current state
        for(int i = 0; i < state.getColumnCount(); i++) {
            // if the action is actually in the list of possible actions, i.e., if the column is NOT full
            if(!state.isColumnFull(i)) {
                // calculate the minimax value for this action
                GameState copyState = state.copyState();
                copyState.insertCoin(i, aiPlayerId);
                double value = minValue(copyState, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
                // TODO: IF stuff does not work, try calling max.  Maybe depending on player ID
                System.out.println("### Column " + i + " " + value);
                if(value > bestValue){
                    // This must be the currently best option. So store it's value and store the best column.
                    bestValue = value;
                    bestColumn = i;
                }
            }
        }

        return bestColumn;
    }

    /**
     * Returns the lowest utility value for the current state.
     * @param state the current state
     * @param alpha the parent node's alpha value
     * @param beta the parent node's beta value
     * @return the lowest utility value
     */
    private double minValue(GameState state, double alpha, double beta, int depth) {
        double finished = isGameFinished(state);
        if(!Double.isNaN(finished)){
            // The game is over, so return terminal value.
            return finished;
        }

        if(cutoffTest(depth)){
            // Maximum depth reached, evaluate current state as a terminal state.
            return eval(state);
        }
        depth++;

        // initialize with dummy value
        double result = Double.POSITIVE_INFINITY;

        // try to perform each possible action and keep track of the best one
        for (int i = 0; i < state.getColumnCount(); i++) {
            if (!state.isColumnFull(i)) {
                GameState copyState = state.copyState();
                copyState.insertCoin(i, otherPlayerId);
                result = Double.min(result, maxValue(copyState, alpha, beta, depth));

                // if the value is lower than the parent's alpha value, prune the tree
                if(result <= alpha){
                    return result;
                }

                // if not, record new beta value
                beta = Double.min(beta, result);
            }
        }

        return result;
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
        double finished = isGameFinished(state);
        if(!Double.isNaN(finished)){
            // The game is over, so return terminal value.
            return finished;
        }

        if(cutoffTest(depth)){
            return eval(state);
        }
        depth++;

        double result = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < state.getColumnCount(); i++) {
            if(!state.isColumnFull(i)) {
                GameState copyState = state.copyState();
                copyState.insertCoin(i, aiPlayerId);
                result = Double.max(result, minValue(copyState, alpha, beta, depth));

                if(result >= beta){
                    return result;
                }

                alpha = Double.max(alpha, result);
            }
        }

        return result;
    }

    private boolean cutoffTest(int depth) {
        return depth >= CUTOFF;
    }

    private double isGameFinished(GameState state){
        IGameLogic.Winner winner = state.gameFinished();
        if(winner != IGameLogic.Winner.NOT_FINISHED){
            // The game has ended
            if(winner == IGameLogic.Winner.TIE){
                return 0d;
            } else if(winner == IGameLogic.Winner.PLAYER1 && aiPlayerId == 1){
                // AI won, so return high value
                return UTILITY_MAX;
            } else if(winner == IGameLogic.Winner.PLAYER2 && aiPlayerId == 2){
                // AI won, so return high value
                return UTILITY_MAX;
            } else {
                // Game is over, but AI lost.
                return UTILITY_MIN;
            }
        } else {
            return Double.NaN;
        }
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

        // Create score for adversary by switching id and counting score.
        playerID = playerID == aiPlayerId ? otherPlayerId : aiPlayerId;

        // How many coins does the opponent have in a row.
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

        return evaluationTable[initialRow][initialColumn];
    }

    /**
     * If the game has finished in the current state, i.e., the board is full or one of the
     * players has won, return the corresponding utility value. Otherwise return -1
     * @param state the current state
     * @return the utility value of the winning player, or -1 if the game is not over
     */
    private double eval(GameState state){
        // Get info for last placed coin.
        int column = state.getLastCoinPosition().fst;
        int row = state.getLastCoinPosition().snd;
        int playerID = state.getBoard()[column][row];

        int score = 0;

        int positionValue = coinPositionValue(state);
        score += positionValue;

        int coinsInARow = maxCoinsInARow(state);
        if(coinsInARow >= GameState.WIN_CONDITION ){
            return UTILITY_MAX;
        } else {
            if(playerID == aiPlayerId){
                score += coinsInARow * 3;
            }
        }

        int winCombinations = winCombinationsCount(state);
        // If the coin placed has any value in terms of winning combinations.
        if(winCombinations > 0) {
            score += winCombinations * 2;
        }
        return score;
    }
}
