package players.treeai;

import chess.board.Board;
import chess.misc.Position;
import chess.move.CastlingMove;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.List;
import java.util.Set;

public class BoardEvaluator {
    private int depth;
    private PieceColor color;

    public BoardEvaluator(int depth, PieceColor color) {
        this.color = color;
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
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
            return 1e9 * (currentDepth + 1);
        } else if (board.isCheckmate(perspective)) {
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
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

    private static double calculateDevelopmentPenalty (Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int x = 0; x < 8; x++) {
            Piece piece = board.getPieceInSquareRelativeTo(perspective, x, 0);
            if (piece.getColor() == perspective && (piece.getType() == PieceType.BISHOP || piece.getType() == PieceType.KNIGHT)) {
                totalValue -= piece.getValue(null);
            }
        }

        return totalValue;
    }

    private static double calculateEndgameEvalution(Board board, PieceColor perspective) {
        return 0;
    }

    private static double calculateMiddlegameEvalution (Board board, PieceColor perspective) {
        double totalValue = 0;
        totalValue = getMaterialBalance(board, perspective);
        totalValue += calculateDevelopmentPenalty(board, perspective) * 0.5;


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
            totalValue -= 50;
        }

        if (board.getMoveHistory().stream().anyMatch(move -> move instanceof CastlingMove && move.getColor() == perspective)) {
            totalValue += 100;
        }


        return totalValue;
    }

    private static double getMaterialBalance (Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                if (piece.getColor() == perspective) {
                    totalValue += piece.getValue(null);
                }
            }
        }
        return totalValue;
    }

    private static double calculateOpeningEvalution (Board board, PieceColor perspective) {
//        return 0;
        double totalValue = getMaterialBalance(board, perspective);
        totalValue += calculateDevelopmentPenalty(board, perspective);

        for (int x = 0; x < 8; x++) {
            Piece piece = board.getPieceInSquareRelativeTo(perspective, x, 0);
            PieceType type = piece.getType();
            if (type == PieceType.KNIGHT) {
//                System.out.println(piece);
                totalValue -= 1;
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
        Board board = Board.fromFEN("rnbq1rk1/ppp1ppb1/3p1n1p/6N1/3PP2B/2NB4/PPP2PPP/R2QK2R b KQ - 0 1");
        System.out.println(board);
        System.out.println(new BoardEvaluator(4, PieceColor.WHITE).evaluateBoard(board, 3));
        board.makeMove(Move.parseMove("h6g5", PieceColor.BLACK, board));
        System.out.println(board);
        System.out.println(new BoardEvaluator(4, PieceColor.WHITE).evaluateBoard(board, 3));

//        Board board2 = Board.fromFEN("1k3r2/8/5b2/8/8/8/8/1K3R2 w - - 0 1");
//        System.out.println(new BoardEvaluator(4, PieceColor.WHITE).evaluateBoard(board2, 3));
    }
}



