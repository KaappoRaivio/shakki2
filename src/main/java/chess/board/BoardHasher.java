package chess.board;

import chess.misc.Position;
import chess.piece.*;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;

public class BoardHasher implements Serializable {
    private int[][] bitSets;
    private Random random = new Random(13253453462L);
    private final int length = 32;

    private Map<Piece, Integer> keys = Map.ofEntries(
            Map.entry(new King(PieceColor.WHITE), 0),
            Map.entry(new King(PieceColor.BLACK), 1),

            Map.entry(new Pawn(PieceColor.WHITE), 2),
            Map.entry(new Pawn(PieceColor.BLACK), 3),

            Map.entry(new Bishop(PieceColor.WHITE), 4),
            Map.entry(new Bishop(PieceColor.BLACK), 5),

            Map.entry(new Knight(PieceColor.WHITE), 6),
            Map.entry(new Knight(PieceColor.BLACK), 7),

            Map.entry(new Rook(PieceColor.WHITE), 8),
            Map.entry(new Rook(PieceColor.BLACK), 9),
            Map.entry(new CastlingRook(PieceColor.WHITE), 10),
            Map.entry(new CastlingRook(PieceColor.BLACK), 11),

            Map.entry(new Queen(PieceColor.WHITE), 12),
            Map.entry(new Queen(PieceColor.BLACK), 13),

            Map.entry(new CastlingKing(PieceColor.WHITE), 14),
            Map.entry(new CastlingKing(PieceColor.BLACK), 15)
    );

    public BoardHasher() {
        bitSets = new int[64][16];
        initBitSets();
    }

    private void initBitSets () {
        for (int y = 0; y < bitSets.length; y++) {
            for (int x = 0; x < bitSets[y].length; x++) {
                bitSets[y][x] = random.nextInt();
            }
        }
    }

    public int getPartHash (Position position, Piece piece) {
        return bitSets[position.getY() * 8 + position.getY()][keys.getOrDefault(piece, 0)];
    }

    public int getFullHash (Board board) {
        int hash = 0;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                hash ^= bitSets[y * 8 + x][keys.getOrDefault(board.getPieceInSquare(x, y), 0)];
            }
        }

        return hash;
    }
}
