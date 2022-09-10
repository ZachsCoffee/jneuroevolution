package stdout;

public class StdOut {
    private StdOut() {

    }

    public static void printPercent(int current, int total) {
        printPercent(current, total, "Percent:");
    }

    public static void printPercent(int current, int total, String message) {
        System.out.printf("%c%s %.2f%%", (char) 13, message, ((100d * current) / total));
    }

}
