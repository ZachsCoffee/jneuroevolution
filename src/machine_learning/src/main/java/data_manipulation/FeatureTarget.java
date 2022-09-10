package data_manipulation;

public class FeatureTarget<F, T> {

    private final F feature;
    private final T target;

    public FeatureTarget(F feature, T target) {
        this.feature = feature;
        this.target = target;
    }

    public F getFeature() {
        return feature;
    }

    public T getTarget() {
        return target;
    }
}
