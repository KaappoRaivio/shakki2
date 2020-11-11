package players.treeai;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.CastlingKing;
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
        PieceColor perspective = board.getTurn().invert();
//        System.out.println(perspective);
        if (board.isDraw()) {
            return 0;
        } else if (board.isCheckmate(perspective)) {
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
            return 1e9 * (currentDepth + 1);
        } else if (board.isCheckmate(perspective.invert())) {
//            System.out.println("Checkmate for " + (perspective == color ? "self " : "opponent ") + board + ", " + currentDepth);
            return -1e9 * (currentDepth + 1);
        } else {
//            return 0;
            return getMaterialAmount(board, perspective) - getMaterialAmount(board, perspective.invert());
        }

//        Set<Move> ownMoves = board.getAllPossibleMoves(perspective, true, true);
//        Set<Move> ownMoves = Collections.emptySet();
//        Set<Move> opponentMoves = board.getAllPossibleMoves(perspective.invert(), true, true);
//        Set<Move> opponentMoves = Collections.emptySet();

//        double openingEvaluation = 0;
////        System.out.println(getMaterialSum(board) / 200);
////        double openingEvaluation = calculateOpeningEvalution(board, perspective, ownMoves)
////                - calculateOpeningEvalution(board, perspective.invert(), opponentMoves);
//        System.out.println(board.getTurn());
//        double middlegameEvaluation = calculateMiddlegameEvalution(board, perspective, ownMoves)
//                - calculateMiddlegameEvalution(board, perspective.invert(), opponentMoves);
////        System.out.println(middlegameEvaluation + ", " + (calculateMiddlegameEvalution(board, perspective.invert(), ownMoves)
////                - calculateMiddlegameEvalution(board, perspective, opponentMoves)));
//        double endgameEvaluation = calculateEndgameEvaluation(board, perspective, ownMoves)
//                - calculateEndgameEvaluation(board, perspective.invert(), opponentMoves);

//        if (board.getLastMove().toString().equalsIgnoreCase("Rxd3")) {
//            System.out.println("Moi");
//            middlegameEvaluation += 100000;
//        }

//        GameStage stage = GameStage.getGameStage(board, getMaterialSum(board));

//        return
//                openingEvaluation * stage.getOpeningWeight()
//                + middlegameEvaluation * stage.getMiddlegameWeight()
//                + endgameEvaluation * stage.getEndgameWeight();
//        return middlegameEvaluation * (currentDepth + 1);
//        return getMaterialBalance(board, perspective) - getMaterialBalance(board, perspective.invert());
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
                totalValue -= piece.getValue(x, perspective == PieceColor.WHITE ? 0 : 7);
            }
        }

        return totalValue;
    }

    private static double calculateEndgameEvaluation(Board board, PieceColor perspective, Set<Move> possibleMoves) {
//        double totalValue = Math.pow(getMaterialPercentage(board, perspective), 3) * 1000;
//        totalValue += getProtectionValue(board, possibleMoves);
        return 0;
//        return totalValue;
    }

    private static double calculateMiddlegameEvalution (Board board, PieceColor perspective, Set<Move> possibleMoves) {
//        double totalValue = Math.pow(getMaterialPercentage(board, perspective), 3) * 10000;
        double totalValue = getMaterialAmount(board, perspective);
//        totalValue += calculateDevelopmentPenalty(board, perspective) * .05;



//        totalValue += getProtectionValue(board, possibleMoves) * 2;


//        if (hasCastled(board, perspective)) {
//            totalValue += 25;
//        }
//        if (hasKingMoved(board, perspective)) {
//            totalValue -= 25;
//        }


        return totalValue;
    }

    private static boolean hasKingMoved(Board board, PieceColor perspective) {
        return !(board.getPieceInSquareRelativeTo(perspective, 4, 0, true) instanceof CastlingKing);
    }

    private static double getProtectionValue(Board board, Set<Move> ownMoves) {
        double totalValue = 0;
        for (Move move : ownMoves) {
            Position threatenedSquare = move.getDestination();
            Piece protectivePiece = move.getPiece();
            Piece protectedPiece = board.getPieceInSquare(move.getDestination());

            if (protectivePiece.getType() == PieceType.PAWN && threatenedSquare.getX() == move.getOrigin().getX()) continue;
            if (move.isCapturingMove()) {
//                totalValue += 1;
                totalValue += protectedPiece.getValue(move.getDestination()) / protectivePiece.getValue(move.getOrigin());
            }
        }
        return totalValue;
    }

    private static boolean hasCastled(Board board, PieceColor perspective) {
//        return board.getMoveHistory().stream().anyMatch(move -> move instanceof CastlingMove && move.getColor() == perspective);
        Piece supposedKingSideKing = board.getPieceInSquareRelativeTo(perspective, 6, 0, true);
        Piece supposedKingSideRook = board.getPieceInSquareRelativeTo(perspective, 5, 0, true);
        Piece supposedQueenSideKing = board.getPieceInSquareRelativeTo(perspective, 2, 0, true);
        Piece supposedQueenSideRook = board.getPieceInSquareRelativeTo(perspective, 3, 0, true);

        return supposedKingSideKing.getType() == PieceType.KING && supposedKingSideKing.getColor() == perspective
                && supposedKingSideRook.getType() == PieceType.ROOK && supposedKingSideRook.getColor() == perspective
                ||
                supposedQueenSideKing.getType() == PieceType.KING && supposedQueenSideKing.getColor() == perspective
                        && supposedQueenSideRook.getType() == PieceType.ROOK && supposedQueenSideRook.getColor() == perspective;
    }

    private static double getMaterialSum (Board board) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                totalValue += piece.getValue(x, y);
            }
        }
        return totalValue;
    }

    private static double getMaterialPercentage (Board board, PieceColor perspective) {
        return (getMaterialAmount(board, perspective) / getMaterialSum(board) - 0.5) * 2;
    }

    private static double getMaterialAmount(Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                if (piece.getColor() == perspective) {
                    totalValue += piece.getValue(x, y);
                }
            }
        }
        return totalValue;
    }

    private static double calculateOpeningEvalution (Board board, PieceColor perspective, Set<Move> possibleMoves) {
        double totalValue = getMaterialAmount(board, perspective) * 15;
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
//        Board board = Board.fromFEN("1k6/3r4/8/8/8/3P4/8/1K6 b - - 0 1");
//
//        TreeAI ai = new TreeAI(PieceColor.BLACK, board, 2, 8, false, 0);
//        ai.updateValues(board, board.getTurn(), 10);
////        BoardEvaluator evaluator = new BoardEvaluator(4, PieceColor.WHITE);
//        System.out.println(board);
//        Move move = ai.getMove();
//        System.out.println(move);
//        board.makeMove(move);
//        System.out.println(board);
////        board.makeMove(Move.parseMove("d7d3", PieceColor.BLACK, board));
//        ai.updateValues(board, board.getTurn(), 10);
//        System.out.println(ai.getMove());
//        System.out.println(board);
//        System.out.println(evaluator.evaluateBoard(board, 4));
//        BoardEvaluator evaluator = new BoardEvaluator(4, PieceColor.WHITE);
//        while (true) {
//            if (board.isCheckmate() || board.isDraw()) break;
//            System.out.println(evaluator.evaluateBoard(board, 3));
//            board.makeMove(choice(board.getAllPossibleMoves()));
//
//        }
//        System.out.println(board);
//        System.out.println(evaluator.evaluateBoard(board, 3));

//        Board board2 = Board.fromFEN("1k3r2/8/5b2/8/8/8/8/1K3R2 w - - 0 1");
//        System.out.println(new BoardEvaluator(4, PieceColor.WHITE).evaluateBoard(board2, 3));
    }
}

//class BoardEvaluationHelpers () {
//
//}