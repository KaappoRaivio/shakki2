package runner;

import chess.board.Board;
import misc.exceptions.StopException;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import ui.UndoException;

import java.util.List;
import java.util.Scanner;

public class Runner {
    private Board board;
    private CapableOfPlaying[] players;
    private UI ui;
    private int moveCount;
    private List<Spectator> spectators;

    public Runner(Board board, CapableOfPlaying[] players, UI ui, List<Spectator> spectators) {
        this.board = board;
        this.players = players;
        this.ui = ui;
        this.spectators = spectators;
        moveCount = 0;

    }

    public PieceColor play (PieceColor initialTurn) {
        PieceColor turn = initialTurn;
        int moveCount = initialTurn == PieceColor.WHITE ? 1 : 2;


        for (CapableOfPlaying capableOfPlaying : players) {
            capableOfPlaying.updateValues(board.deepCopy(), turn, moveCount);
        }
        ui.updateValues(board.deepCopy(), turn, moveCount);

        mainloop: while (true) {

            CapableOfPlaying currentPlayer = players[(moveCount + 1) % 2];
            if (board.isCheckmate()) {
                System.out.println("=== === Checkmate! === ===");
                System.out.println(board);
                System.out.println(currentPlayer.getColor().invert() + " wins!");
                break;
            } else if (board.isDraw()) {
                System.out.println("=== === Draw! === ===");
                System.out.println(board);
                break;
            }

            ui.commit();

            while (true) {
                try {
//                    System.out.println(board.getAllPossibleMoves(turn));
                    long start = System.currentTimeMillis();
                    Move move = currentPlayer.getMove();
                    long end = System.currentTimeMillis();
                    System.out.println("Took " + ((end - start) / 1000) + " seconds. Move is: " + move);
                    board.makeMove(move);
                    break;
                } catch (UndoException e) {
                    board.unMakeMove(2);
                    for (CapableOfPlaying player : players) {
                        player.updateValues(board.deepCopy(), turn, moveCount);
                    }
                    ui.updateValues(board.deepCopy(), turn, moveCount);

                    continue mainloop;
                } catch (StopException e) {
                    throw e;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.out.println("Move is invalid!");
                    System.out.print("Press enter to continue:> ");
                    new Scanner(System.in).nextLine();
                }
            }

            System.out.println(board.getMoveHistoryPretty());

            turn = turn.invert();
            moveCount += 1;

            int finalMoveCount = moveCount;
            PieceColor finalTurn = turn;
            spectators.forEach(spectator -> spectator.spectate(board.deepCopy(), finalMoveCount, finalTurn));


            for (CapableOfPlaying player : players) {
                player.updateValues(board.deepCopy(), turn, moveCount);
            }
            ui.updateValues(board.deepCopy(), turn, moveCount);

        }

        return turn;
    }

    public Board getBoard() {
        return board;
    }
}
