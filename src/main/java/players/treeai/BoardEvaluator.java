package players.treeai;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.King;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.List;
import java.util.Set;

public class BoardEvaluator {
    private int depth;

    public BoardEvaluator(int depth) {
//        this.color = color;
        this.depth = depth;
    }

    double evaluateBoard(Board board, int currentDepth) {
//        double totalValue = 0;
//        for (int y = 0; y < 8; y++) {
//            for (int x = 0; x < 8; x++) {
//                Piece piece = board.getPieceInSquare(x, y);
//                totalValue += piece.getColor() == perspective ? piece.getValue(null) : -piece.getValue(null);
//            }
//        }
        PieceColor perspective = board.getTurn();
        if (board.isCheckmate(perspective.invert())) {
            return 1e9 * (currentDepth + 1);
        } else if (board.isCheckmate(perspective)) {
            return -1e9 * (currentDepth + 1);
        }


        double openingEvaluation = calculateOpeningEvalution(board, perspective)
                - calculateOpeningEvalution(board, perspective.invert());
        double middlegameEvaluation = calculateMiddlegameEvalution(board, perspective)
                - calculateMiddlegameEvalution(board, perspective.invert());
        double endgameEvaluation = calculateEndgameEvalution(board, perspective)
                - calculateEndgameEvalution(board, perspective.invert());

        GameStage stage = GameStage.getGameStage(board);
//        System.out.println(openingEvalution + ", " + middlegameEvalution + ", " + endgameEvalution);
//        System.out.println(stage);


//        System.out.println(stage);
//        return openingEvaluation;
//        return middlegameEvaluation;
        return
                openingEvaluation * stage.getOpeningWeight()
                + middlegameEvaluation * stage.getMiddlegameWeight()
                + endgameEvaluation * stage.getEndgameWeight();
    }

    private static boolean pieceFoundIn(List<String> positions, PieceType piece, PieceColor perspective, Board board) {
        return pieceFoundIn(positions, piece, perspective, board, false);
    }

    private static boolean pieceFoundIn (List<String> positions, PieceType piece, PieceColor perspective, Board board, boolean flip) {
        for (String position : positions) {
            if (board.getPieceInSquareRelativeTo(perspective, Position.fromString(position), flip).getType() == piece) {
                return true;
            }
        }

        return false;
    }

    private static double calculateEndgameEvalution(Board board, PieceColor perspective) {
        return 0;
    }

    private static double calculateMiddlegameEvalution(Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                if (piece.getColor() == perspective) {
                    totalValue += piece.getValue(null) * 10;
                }
            }
        }

        int[][] threats = new int[8][8];

        Set<Move> ownMoves = board.getAllPossibleMoves(perspective, true, true);
//        System.out.println(ownMoves);
        for (Move move : ownMoves) {
            Position threatenedSquare = move.getDestination();
            Piece protectivePiece = move.getPiece();
            Piece protectedPiece = board.getPieceInSquare(move.getDestination());

            if (protectivePiece.getType() == PieceType.PAWN && threatenedSquare.getX() == move.getOrigin().getX()) continue;
            if (move.isCapturingMove()) {
//                totalValue += 1;
                totalValue += protectedPiece.getValue(null) / protectivePiece.getValue(null) * 2;
            }
        }

        Set<Move> opponentMoves = board.getAllPossibleMoves(perspective.invert(), true, true);
//        System.out.println(opponentMoves);
        for (Move move : opponentMoves) {
            Position threatenedSquare = move.getDestination();
            Piece protectivePiece = move.getPiece();
            Piece protectedPiece = board.getPieceInSquare(move.getDestination());

            if (protectivePiece.getType() == PieceType.PAWN && threatenedSquare.getX() == move.getOrigin().getX()) continue;
            if (move.isCapturingMove()) {
//                totalValue += 1;
                totalValue -= protectedPiece.getValue(null) / protectivePiece.getValue(null) * 2;
            }
        }

        if (!pieceFoundIn(List.of("e1", "g1"), PieceType.KING, perspective, board, true)) {
            totalValue -= 30;
        }
//        System.out.println(ownMoves);
//        System.out.println(opponentMoves);
//        for (int y = 0; y < 8; y++) {
//            for (int x = 0; x < 8; x++) {
//                System.out.println(x + ", " + y + ", " + threats[y][x]);
//                totalValue += threats[y][x] * board.getPieceInSquare(x, y).getValue(null);
//            }
//        }

        return totalValue;
    }

    private static double calculateOpeningEvalution (Board board, PieceColor perspective) {
//        return 0;
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                if (piece.getColor() == perspective) {
                    totalValue += piece.getValue(null) * 10;
                }
            }
        }

        for (int x = 0; x < 8; x++) {
            Piece piece = board.getPieceInSquareRelativeTo(perspective, x, 0);
            PieceType type = piece.getType();
            if (type == PieceType.KNIGHT) {
//                System.out.println(piece);
                totalValue -= 10;
            } else if (type == PieceType.BISHOP) {
//                System.out.println(piece);
                totalValue -= 8;
            }
        }

        if (board.getPieceInSquareRelativeTo(perspective, 4, 3).getType() == PieceType.PAWN) {
            totalValue += 7.5;
            if (board.getPieceInSquareRelativeTo(perspective, 2, 2).getType() == PieceType.KNIGHT) {
                totalValue += 2.5;
            }

            Piece opponentCandinate = board.getPieceInSquareRelativeTo(perspective, 5, 4);
            if (opponentCandinate.getType() != PieceType.NO_PIECE && opponentCandinate.getType() != PieceType.PAWN
            && opponentCandinate.getColor() == perspective.invert()) {
                totalValue += 4;
            }
        }
        if (board.getPieceInSquareRelativeTo(perspective, 3, 3).getType() == PieceType.PAWN) {
            totalValue += 7.5;
            if (board.getPieceInSquareRelativeTo(perspective, 5, 2).getType() == PieceType.KNIGHT) {
                totalValue += 2.5;
            }

            Piece opponentCandinate = board.getPieceInSquareRelativeTo(perspective, 2, 4);
            if (opponentCandinate.getType() != PieceType.NO_PIECE && opponentCandinate.getType() != PieceType.PAWN
                    && opponentCandinate.getColor() == perspective.invert()) {
                totalValue += 4;
            }
        }

        if (board.getPieceInSquareRelativeTo(perspective, 0, 2).getType() == PieceType.KNIGHT
         || board.getPieceInSquareRelativeTo(perspective, 7, 2).getType() == PieceType.KNIGHT) {
            totalValue -= 3.0;
        }

        if (!pieceFoundIn(List.of("D1", "D2", "E2"), PieceType.QUEEN, perspective, board, true)) {
//            System.out.println(perspective);
            totalValue -= 0.5;
        }
//        System.out.println(totalValue + ", " + perspective);

        return totalValue;
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("1k5r/6p1/5b2/8/8/8/8/1K2R3 w - - 0 1");
        System.out.println(new BoardEvaluator(4).evaluateBoard(board, 3));

        Board board2 = Board.fromFEN("1k3r2/8/5b2/8/8/8/8/1K3R2 w - - 0 1");
        System.out.println(new BoardEvaluator(4).evaluateBoard(board2, 3));
    }
}



