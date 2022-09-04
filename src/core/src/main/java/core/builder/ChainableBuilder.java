package core.builder;

public interface ChainableBuilder<T> {
    ChainableBuilder<T> setParentBuilder(T builder);

    T and();
}
