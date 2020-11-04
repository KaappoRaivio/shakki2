package ui;

import chess.board.Board;
import chess.misc.exceptions.ChessException;
import chess.misc.exceptions.StopException;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import misc.OrdinalConverter;
import runner.UI;

import java.util.Arrays;
import java.util.Scanner;

public class TtyUI implements UI {
    private Board board;
    private PieceColor turn;
    private int moveCount;

    @Override
    public void updateValues (Board board, PieceColor turn, int moveCount) {
        this.board = board;
        this.turn = turn;
        this.moveCount = moveCount;
    }

    @Override
    public Move getMove (PieceColor color) {
        while (true) {
            try {
                System.out.print("Your move:> ");
                String response = new Scanner(System.in).nextLine();
                if (response.toLowerCase().equals("stop")) {
                    throw new StopException();
                }
                return Move.parseMove(response, turn, board);
            } catch (ChessException e) { e.printStackTrace();}
        }
    }

    @Override
    public void commit () {
        System.out.println(turn + "'s move, " + OrdinalConverter.toOrdinal(moveCount / 2) + " move:");
        System.out.println(board.getAllPossibleMoves());
        if (board.isCheck()) {
            System.out.println("Check!");
        }
        Arrays.stream(board.toString().split("\n")).forEach(line -> System.out.println("\t" + line));
    }

    @Override
    public void close () {

    }
}
