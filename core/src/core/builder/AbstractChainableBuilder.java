package core.builder;

public abstract class AbstractChainableBuilder<T> implements TrainableBuilder<T> {

    private T builder;

    @Override
    public ChainableBuilder<T> setParentBuilder(T builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public T and() {
        if (builder == null) throw new IllegalStateException(
            "Need to pass a builder before using it."
        );

        return builder;
    }
}
