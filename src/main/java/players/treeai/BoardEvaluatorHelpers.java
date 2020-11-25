package players.treeai;

import chess.board.Board;
import chess.board.BoardHelpers;
import chess.move.Move;
import chess.piece.CastlingKing;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Position;

import java.util.List;
import java.util.Map;

public class BoardEvaluatorHelpers {
    static boolean pieceFoundIn(List<String> positions, PieceType piece, PieceColor perspective, Board board) {
        return pieceFoundIn(positions, piece, perspective, board, false);
    }

    static boolean pieceFoundIn(List<String> positions, PieceType piece, PieceColor perspective, Board board, boolean flip) {
        for (String position : positions) {
            if (board.getPieceInSquareRelativeTo(perspective, Position.fromString(position), flip).getType() == piece) {
                return true;
            }
        }

        return false;
    }

    static double calculateDevelopmentPenalty(Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int x = 0; x < 8; x++) {
            Piece piece = board.getPieceInSquareRelativeTo(perspective, x, 0);
            if (piece.getColor() == perspective && (piece.getType() == PieceType.BISHOP || piece.getType() == PieceType.KNIGHT)) {
                totalValue -= piece.getValue(x, perspective == PieceColor.WHITE ? 0 : 7);
            }
        }

        return totalValue;
    }

    static boolean hasKingMoved(Board board, PieceColor perspective) {
        return !(board.getPieceInSquareRelativeTo(perspective, 4, 0, true) instanceof CastlingKing);
    }

    static double getAttackValue(Board board, List<Move> ownMoves) {
        double totalValue = 0;
        for (Move move : ownMoves) {
            Position threatenedSquare = move.getDestination();
            Piece attackingPiece = move.getPiece();
            Piece attackedPiece = board.getPieceInSquare(threatenedSquare);

            if (move.isCapturingMove()) {
//                totalValue += 1;
                totalValue += attackedPiece.getValue(threatenedSquare) / attackingPiece.getValue(move.getOrigin());
            }
        }
        return totalValue;
    }

    static boolean hasCastled(Board board, PieceColor perspective) {
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

    static double getMaterialSum(Board board) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                totalValue += piece.getValue(x, y);
            }
        }
        return totalValue;
    }

    static double getMaterialPercentage(Board board, PieceColor perspective) {
        return getMaterialAmount(board, perspective) / getMaterialSum(board);
    }

    public static double getMaterialAmount(Board board, PieceColor perspective) {
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

    static double getPawnAdvantage(Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquareRelativeTo(perspective, x, y);
                if (piece.getType() == PieceType.PAWN && piece.getColor() == perspective) {
                    totalValue += y;
                }
            }
        }

        return totalValue;
    }

    static double getPassedPawns(Board board, PieceColor perspective) {
        Map<PieceType, Integer> pieceComposition = BoardHelpers.getPieceComposition(board, perspective.invert());
//        System.out.println(pieceComposition);
//        System.out.println(pieceComposition.get(PieceType.QUEEN) + ", " + pieceComposition.get(PieceType.ROOK));
        boolean heavyOfficers = pieceComposition.get(PieceType.QUEEN) != 0 || pieceComposition.get(PieceType.ROOK) != 0;
        boolean lightOfficers = pieceComposition.get(PieceType.BISHOP) != 0 || pieceComposition.get(PieceType.KNIGHT) != 0;
//        System.out.println(heavyOfficers + ", " + lightOfficers);

        if (heavyOfficers || lightOfficers) return 0;

        double freePawns = 0;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquareRelativeTo(perspective, x, y);
                if (piece.getColor() == perspective && piece.getType() == PieceType.PAWN) {
                    if (isFreePawn(board, perspective, x, y)) freePawns += y;
                }
            }
        }

        return freePawns;
    }

    private static boolean isFreePawn (Board board, PieceColor perspective, int x, int y) {
        Position kingPos = board.getKingPosition(perspective.invert()).flip(perspective);

        int distanceToPromotion = 7 - y;
        return !canKingReach(kingPos, x, y, distanceToPromotion) && isPathClear(board, perspective, x, y);
    }

    private static boolean isPathClear(Board board, PieceColor perspective, int x, int y) {
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            if (x + offsetX < 0 || x + offsetX > 7) continue;
            for (int offsetY = 1; offsetY + y < 8; offsetY++) {
                if (offsetY + y > 7 || offsetY + y < 0) continue;
                if (board.getPieceInSquareRelativeTo(perspective, x + offsetX, y + offsetY).getColor() == perspective.invert()) return false;
           }
        }

        return true;
    }

    private static boolean canKingReach(Position kingPosition, int pawnX, int pawnY, int distance) {
        return kingPosition.getY() >= pawnY
                && Math.abs(kingPosition.getX() - pawnX) <= distance;
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("8/7p/8/P4pp1/8/5r1k/5P1P/4R1K1 b - - 0 6");
        System.out.println(board);
        System.out.println(getPassedPawns(board, PieceColor.WHITE));
    }
}
