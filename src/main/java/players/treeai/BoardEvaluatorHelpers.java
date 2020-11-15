package players.treeai;

import chess.board.Board;
import chess.move.Move;
import chess.piece.CastlingKing;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Position;

import java.util.List;
import java.util.Set;

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
        return (getMaterialAmount(board, perspective) / getMaterialSum(board) - 0.5) * 2;
    }

    static double getMaterialAmount(Board board, PieceColor perspective) {
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

    public static double getPawnAdvantage(Board board, PieceColor perspective) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board.getPieceInSquareRelativeTo(perspective, x, y).getType() == PieceType.PAWN) {
                    totalValue += y;
                }
            }
        }

        return totalValue;
    }
}
