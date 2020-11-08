package chess.board;

import chess.move.Move;
import chess.move.NoMove;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class BoardStateHistory implements Serializable {
    private Deque<BoardState> states = new LinkedList<>();

    public BoardStateHistory (BoardState initialState) {
        states.push(initialState);
    }

    public BoardStateHistory () { }

    void push () {
        states.push(new BoardState(Objects.requireNonNull(states.peek())));
    }

    void pull () {
        states.pop();
    }

    public BoardState getCurrentState () {
        return Objects.requireNonNull(states.peek());
    }

    List<BoardState> getPreviousStates() {
        return (List<BoardState>) states;
    }
}
