package chess.board;

import chess.misc.exceptions.ChessException;
import chess.misc.Position;
import chess.piece.King;
import chess.piece.Knight;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

public class ThreatChecker {
    static boolean isUnderThreat (Position square, Board board) {
        boolean pawns = checkForPawns(square, board);
        boolean kings = checkForKings(square, board);
        boolean knights = checkForKnights(square, board);
        boolean bishopsAndQueens = checkForBishopsAndQueens(square, board);
        boolean rooksAndQueens = checkForRooksAndQueens(square, board);

        return pawns
            || kings
            || knights
            || bishopsAndQueens
            || rooksAndQueens;
    }

    private static boolean checkForRooksAndQueens (Position square, Board board) {


        Piece piece = board.getPieceInSquare(square);

        int x = square.getX();
        int y = square.getY();

        for (int checkY = y + 1; checkY < board.getDimY(); checkY++) {
            Piece currentPiece = board.getPieceInSquare(new Position(x, checkY));

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.ROOK || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }
        }

        for (int checkY = y - 1; checkY >= 0; checkY--) {
            Piece currentPiece = board.getPieceInSquare(new Position(x, checkY));

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.ROOK || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }
        }

        for (int checkX = x + 1; checkX < board.getDimX(); checkX++) {
            Piece currentPiece = board.getPieceInSquare(new Position(checkX, y));

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.ROOK || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }
        }

        for (int checkX = x - 1; checkX >= 0; checkX--) {
            Piece currentPiece = board.getPieceInSquare(new Position(checkX, y));

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.ROOK || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }
        }

        return false;
    }

    private static boolean checkForBishopsAndQueens (Position square, Board board) {
        Piece piece = board.getPieceInSquare(square);

        int x = square.getX();
        int y = square.getY();

        int checkX = x + 1;
        int checkY = y + 1;

        while (checkX < board.getDimX() && checkY < board.getDimY()) {
            Piece currentPiece = board.getPieceInSquare(checkX, checkY);

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.BISHOP || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }

            checkX += 1;
            checkY += 1;
        }

        checkX = x + 1;
        checkY = y - 1;

        while (checkX < board.getDimX() && checkY >= 0) {
            Piece currentPiece = board.getPieceInSquare(checkX, checkY);

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.BISHOP || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }

            checkX += 1;
            checkY -= 1;
        }

        checkX = x - 1;
        checkY = y - 1;

        while (checkX >= 0 && checkY >= 0) {
            Piece currentPiece = board.getPieceInSquare(checkX, checkY);

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.BISHOP || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }

            checkX -= 1;
            checkY -= 1;
        }

        checkX = x - 1;
        checkY = y + 1;

        while (checkX >= 0 && checkY < board.getDimY()) {
            Piece currentPiece = board.getPieceInSquare(checkX, checkY);

            if (currentPiece.getColor() == piece.getColor().invert()) {
                if (currentPiece.getType() == PieceType.BISHOP || currentPiece.getType() == PieceType.QUEEN) {
                    return true;
                } else {
                    break;
                }
            } else if (currentPiece.getColor() == piece.getColor()) {
                break;
            }

            checkX -= 1;
            checkY += 1;
        }

        return false;
    }



    private static boolean checkForKnights (Position square, Board board) {
        PieceColor color = board.getPieceInSquare(square).getColor();

        for (Position offset : Knight.offsets) {
            Position newPosition;

            newPosition = square.offset(offset, false);
            if (!newPosition.verify()) {
                continue;
            }

            Piece piece = board.getPieceInSquare(newPosition);
            if (piece.getType() == PieceType.KNIGHT && piece.getColor() == color.invert()) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkForKings (Position square, Board board) {
        PieceColor color = board.getPieceInSquare(square).getColor();

        for (Position offset : King.offsets) {
            Position newPosition;

            newPosition = square.offset(offset, false);
            if (!newPosition.verify()) {
                continue;
            }

            Piece piece = board.getPieceInSquare(newPosition);
            if (piece.getType() == PieceType.KING && piece.getColor() == color.invert()) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkForPawns (Position square, Board board) {
        Piece piece = board.getPieceInSquare(square);



        Position offset = square.offset(1, -piece.getForwardDirection(), false);
        Piece right;
        if (offset.verify()) {
            right = board.getPieceInSquare(offset);
        } else {
            right = null;
        }

        offset = square.offset(-1, -piece.getForwardDirection(), false);
        Piece left;
        if (offset.verify()) {
            left = board.getPieceInSquare(offset);
        } else {
            left = null;
        }


        return right != null && right.getType() == PieceType.PAWN && right.getColor() == piece.getColor().invert()
            ||  left != null && left.getType() == PieceType.PAWN &&  left.getColor() == piece.getColor().invert();
    }

    public static void main(String[] args) {
        Board board = Board.fromFile("/home/kaappo/git/chess/src/main/resources/boards/1564930703584.out");

        System.out.println(board.isCheck(PieceColor.BLACK));
    }

}
