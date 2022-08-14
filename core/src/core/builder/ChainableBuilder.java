package core.builder;

public interface ChainableBuilder {
    ChainableBuilder setParentBuilder(TrainableBuilder builder);

    TrainableBuilder and();
}
