package players.treeai;

public class TranspositionTableEntry {
    public static final int NODE_EXACT = 0;
    public static final int NODE_UPPER = 1;
    public static final int NODE_LOWER = 2;

    private double positionValue;
    private int nodeType;
    private int depth;

    public TranspositionTableEntry(double positionValue, int nodeType, int depth) {
        this.positionValue = positionValue;
        this.nodeType = nodeType;
        this.depth = depth;
    }

    public double getPositionValue() {
        return positionValue;
    }

    public int getNodeType() {
        return nodeType;
    }

    public int getDepth() {
        return depth;
    }
}
