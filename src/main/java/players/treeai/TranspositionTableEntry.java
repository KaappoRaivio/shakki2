package players.treeai;

import chess.move.Move;
import chess.piece.basepiece.PieceColor;

public class TranspositionTableEntry {
    public static final int NODE_EXACT = 0;
    public static final int NODE_UPPER = 1;
    public static final int NODE_LOWER = 2;

    private double positionValue;
    private int nodeType;
    private int depth;
    private Move bestMove;
    private PieceColor color;

    public TranspositionTableEntry(double positionValue, int nodeType, int depth, Move bestMove, PieceColor color) {
        this.positionValue = positionValue;
        this.nodeType = nodeType;
        this.depth = depth;
        this.bestMove = bestMove;
        this.color = color;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public double getPositionValue() {
        return positionValue;
    }

    public int getNodeType() {
        return nodeType;
    }

    public int getDepth() {
        return depth;
    }

    public PieceColor getColor() {
        return color;
    }
}
