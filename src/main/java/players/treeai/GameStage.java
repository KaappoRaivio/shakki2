package players.treeai;

import chess.board.Board;
import chess.piece.basepiece.Piece;

class GameStage {
    private final double openingWeight;
    private final double middlegameWeight;
    private final double endgameWeight;

    private GameStage(double openingWeight, double middlegameWeight, double endgameWeight) {
        this.openingWeight = openingWeight;
        this.middlegameWeight = middlegameWeight;
        this.endgameWeight = endgameWeight;
    }

    static GameStage getGameStage(Board board) {
        double materialSum = getSumOfMaterial(board) / 200;

        double openingWeight = calculateOpeningWeight(materialSum);
        double middlegameWeight = calculateMiddlegameWeight(materialSum);
        double endgameWeight = calculateEndgameWeight(materialSum);

        return new GameStage(openingWeight, middlegameWeight, endgameWeight);
    }

    private static double getSumOfMaterial(Board board) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                totalValue += piece.getValue(null);
            }
        }
        return totalValue;
    }

    private static double calculateOpeningWeight(double materialSum) {
//        return 0.000000469 * Math.exp(0.364 * materialSum);
        return clamp(-3 + materialSum / 10.0);
    }

    private static double calculateMiddlegameWeight(double materialSum) {
        if (materialSum < 40 && materialSum >= 30) {
            return clamp(-0.1 * materialSum + 4);
        } else if (materialSum < 30 && materialSum >= 20) {
            return 1;
        } else if (materialSum < 20 && materialSum >= 14) {
            return clamp(1. / 6 * materialSum - 7. / 3);
        } else {
            return 0;
        }
    }

    private static double calculateEndgameWeight(double materialSum) {
        if (materialSum > 20) {
            return 0;
        } else if (materialSum > 14) {
            return clamp(-1. / 6 * materialSum + 10. / 3);
        } else {
            return 1;
        }
    }

    public double getOpeningWeight() {
        return openingWeight;
    }

    public double getMiddlegameWeight() {
        return middlegameWeight;
    }

    public double getEndgameWeight() {
        return endgameWeight;
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    @Override
    public String toString() {
        return "GameStage{" +
                "openingWeight=" + openingWeight +
                ", middlegameWeight=" + middlegameWeight +
                ", endgameWeight=" + endgameWeight +
                '}';
    }
}
