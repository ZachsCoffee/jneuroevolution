package filters;

public class Kernel {
    private Kernel() {}

    public static final int[][] IDENTITY = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    public static final int[][] EDGE_DETECTION_SOFT = {
            {1, 0, -1},
            {0, 0, 0},
            {-1, 0, 1}
    };

    public static final int[][] EDGE_DETECTION_MEDIUM = {
            {0, 1, 0},
            {1, -1, 1},
            {0, 1, 0}
    };

    public static final int[][] EDGE_DETECTION_HIGH = {
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
    };

    public static final int[][] SHARPEN = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };

    public static final int[][] CUSTOM_1 = {
            {-1, -1, -1},
            {-1, -1, -1},
            {1, 1, 1}
    };
}
