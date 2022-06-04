package options;

public class Lockable {

    private final String exceptionMessage;
    private boolean locked = false;

    protected Lockable() {
        this(null);
    }

    protected Lockable(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public final void lock() {
        locked = true;
    }

    protected final boolean isLocked() {
        return locked;
    }

    protected final void checkLock() {
        if (locked) {
            String message = exceptionMessage == null
                ? "The class have locked state."
                : exceptionMessage;

            throw new IllegalStateException(message);
        }
    }
}
