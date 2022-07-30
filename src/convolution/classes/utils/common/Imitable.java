package utils.common;

public interface Imitable<T extends Imitable<T>> {
    T copy();
}
