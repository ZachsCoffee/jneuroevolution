package core.builder;

public abstract class AbstractChainableBuilder implements ChainableBuilder, TrainableBuilder {

    private TrainableBuilder builder;

    @Override
    public ChainableBuilder setParentBuilder(TrainableBuilder builder) {
        this.builder = builder;

        return this;
    }

    @Override
    public TrainableBuilder and() {
        if (builder == null) throw new IllegalStateException(
            "Need to pass a builder before using it."
        );

        return builder;
    }
}
