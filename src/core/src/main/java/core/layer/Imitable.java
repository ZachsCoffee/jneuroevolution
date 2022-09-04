package core.layer;

public interface Imitable<T extends Imitable<T>> {
    T copy();
}
