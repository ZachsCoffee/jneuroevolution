package filters;

public class Kernel {
    private Kernel() {}

    public static final double[][] IDENTITY = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    public static final double[][] EDGE_DETECTION_SOFT = {
            {1, 0, -1},
            {0, 0, 0},
            {-1, 0, 1}
    };

    public static final double[][] EDGE_DETECTION_MEDIUM = {
            {0, 1, 0},
            {1, -1, 1},
            {0, 1, 0}
    };

    public static final double[][] EDGE_DETECTION_HIGH = {
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
    };

    public static final double[][] SHARPEN = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };

    public static final double[][] CUSTOM_1 = {
            {-1, -1, -1},
            {-1, -1, -1},
            {1, 1, 1}
    };

    public static final double[][] CUSTOM_2 = {
            {0, -1, -1},
            {1, 0, -1},
            {1, 1, 0}
    };

    public static final double[][] CUSTOM_3 = {
            {-1, -1, 0},
            {-1, 0, 1},
            {0, 1, 1}
    };
    public static final double[][] CUSTOM_4 = {
            {1, 0, -1},
            {1, 0, -1},
            {1, 0, -1}
    };
    public static final double[][] CUSTOM_5 = {
            {-1, 0, 1},
            {-1, 0, 1},
            {-1, 0, 1}
    };

    public static final double[][] CUSTOM_6 = {
            {1, -1, -1},
            {1, -1, -1},
            {1, -1, -1}
    };
    public static final double[][] CUSTOM_7 = {
            {-1, -1, 1},
            {-1, -1, 1},
            {-1, -1, 1}
    };
}
