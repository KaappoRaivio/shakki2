package players;

import chess.board.Board;

class GameStage {
    private final double openingWeight;
    private final double middlegameWeight;
    private final double endgameWeight;

    private GameStage(double openingWeight, double middlegameWeight, double endgameWeight) {
        this.openingWeight = openingWeight;
        this.middlegameWeight = middlegameWeight;
        this.endgameWeight = endgameWeight;
    }

    static GameStage getGameStage(Board board, double materialSum) {
        materialSum /= 200;

        double openingWeight = calculateOpeningWeight(materialSum);
        double middlegameWeight = calculateMiddlegameWeight(materialSum);
        double endgameWeight = calculateEndgameWeight(materialSum);

        double divisor = openingWeight + middlegameWeight + endgameWeight;

        openingWeight /= divisor;
        middlegameWeight /= divisor;
        endgameWeight /= divisor;

        return new GameStage(openingWeight, middlegameWeight, endgameWeight);
    }


    private static double calculateOpeningWeight(double materialSum) {
//        return 0.000000469 * Math.exp(0.364 * materialSum);
        return clamp(0.2 * materialSum - 7);
    }

    private static double calculateMiddlegameWeight(double materialSum) {
        if (materialSum < 40 && materialSum >= 35) {
            return clamp(-0.1 * materialSum + 4.5);
        } else if (materialSum < 35 && materialSum >= 20) {
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
