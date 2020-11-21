package players.treeai;

import chess.board.Board;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.List;

public class BasicBoardEvaluator2 implements BoardEvaluator {
    private int depth;
    private PieceColor color;

    public BasicBoardEvaluator2(int depth, PieceColor color) {
        this.color = color;
        this.depth = depth;
    }


    int white = 0;
    int black = 0;

    @Override
    public double evaluateBoard(Board board, int currentDepth) {
        PieceColor perspective = board.getTurn();
        if (perspective == PieceColor.WHITE) {
            white += 1;
        } else {
            black += 1;
        }
//        System.out.println(perspective);
        if (board.isDraw()) {
            return 0;
        } else if (board.isCheckmate(perspective.invert())) {
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
            return 1e9 * (currentDepth + 1);
        } else if (board.isCheckmate(perspective)) {
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
            return -1e9 * (currentDepth + 1);
        }

        List<Move> ownMoves = board.getAllPossibleMoves(perspective);
        List<Move> opponentMoves = board.getAllPossibleMoves(perspective.invert());

        double openingEvaluation = calculateOpeningEvalution(board, perspective, ownMoves)
                - calculateOpeningEvalution(board, perspective.invert(), opponentMoves);
        double middlegameEvaluation = calculateMiddlegameEvalution(board, perspective, ownMoves)
                - calculateMiddlegameEvalution(board, perspective.invert(), opponentMoves);
        double endgameEvaluation = calculateEndgameEvaluation(board, perspective, ownMoves)
                - calculateEndgameEvaluation(board, perspective.invert(), opponentMoves);


        GameStage stage = GameStage.getGameStage(board, BoardEvaluatorHelpers.getMaterialSum(board));

        return
                openingEvaluation * stage.getOpeningWeight()
                + middlegameEvaluation * stage.getMiddlegameWeight()
                + endgameEvaluation * stage.getEndgameWeight();
    }

    public GameStage getGameStage (Board board) {
        return GameStage.getGameStage(board, BoardEvaluatorHelpers.getMaterialSum(board));
    }

    private static double calculateEndgameEvaluation(Board board, PieceColor perspective, List<Move> possibleMoves) {
        double totalValue = BoardEvaluatorHelpers.getMaterialPercentage(board, perspective) * 5000;
        totalValue += BoardEvaluatorHelpers.getAttackValue(board, possibleMoves) * 2;
        totalValue += BoardEvaluatorHelpers.getPawnAdvantage(board, perspective) * 2;
        totalValue += BoardEvaluatorHelpers.getPassedPawns(board, perspective) * 500;

//        double totalValue = Math.pow(getMaterialPercentage(board, perspective), 3) * 1000;
//        totalValue += getProtectionValue(board, possibleMoves);
//        return 0;
        return totalValue;
    }

    private static double calculateMiddlegameEvalution (Board board, PieceColor perspective, List<Move> possibleMoves) {
//        double totalValue = Math.pow(getMaterialPercentage(board, perspective), 3) * 10000;
        double totalValue = BoardEvaluatorHelpers.getMaterialAmount(board, perspective);
        totalValue += BoardEvaluatorHelpers.calculateDevelopmentPenalty(board, perspective) * 0.2;
        totalValue += BoardEvaluatorHelpers.getAttackValue(board, possibleMoves) * 2;





        if (BoardEvaluatorHelpers.hasCastled(board, perspective)) {
            totalValue += 50;
        }
        if (BoardEvaluatorHelpers.hasKingMoved(board, perspective)) {
            totalValue -= 50;
        }


        return totalValue;
    }

    private static double calculateOpeningEvalution (Board board, PieceColor perspective, List<Move> possibleMoves) {
        double totalValue = BoardEvaluatorHelpers.getMaterialAmount(board, perspective);
        totalValue += BoardEvaluatorHelpers.calculateDevelopmentPenalty(board, perspective) * 0.25;


        if (board.getPieceInSquareRelativeTo(perspective, 4, 3).getType() == PieceType.PAWN) {
            totalValue += 5;
            if (board.getPieceInSquareRelativeTo(perspective, 2, 2).getType() == PieceType.KNIGHT) {
                totalValue += 5;
            }

            Piece opponentCandinate = board.getPieceInSquareRelativeTo(perspective, 5, 4);
            if (opponentCandinate.getType() != PieceType.NO_PIECE && opponentCandinate.getType() != PieceType.PAWN
            && opponentCandinate.getColor() == perspective.invert()) {
                totalValue += 3;
            }
        }
        if (board.getPieceInSquareRelativeTo(perspective, 3, 3).getType() == PieceType.PAWN) {
            totalValue += 5;
            if (board.getPieceInSquareRelativeTo(perspective, 5, 2).getType() == PieceType.KNIGHT) {
                totalValue += 5;
            }

            Piece opponentCandinate = board.getPieceInSquareRelativeTo(perspective, 2, 4);
            if (opponentCandinate.getType() != PieceType.NO_PIECE && opponentCandinate.getType() != PieceType.PAWN
                    && opponentCandinate.getColor() == perspective.invert()) {
                totalValue += 3;
            }
        }

        if (!BoardEvaluatorHelpers.pieceFoundIn(List.of("D1", "D2", "E2"), PieceType.QUEEN, perspective, board, true)) {
//            System.out.println(perspective);
            totalValue -= 20;
        }

        if (BoardEvaluatorHelpers.hasKingMoved(board, perspective)) {
            totalValue -= 50;
        }

        if (BoardEvaluatorHelpers.hasCastled(board, perspective)) {
            totalValue += 50;
        }


        return totalValue;
    }


    public static void main(String[] args) {
        Board board = Board.fromFEN("8/6pp/P7/8/5p2/4P2k/7P/6K1 w - - 0 1");
        System.out.println(new BasicBoardEvaluator2(4, PieceColor.WHITE).evaluateBoard(board, 4));
    }
}