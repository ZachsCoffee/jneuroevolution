package data_manipulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonDataset<F, T> implements Dataset<F, T>, Iterable<FeatureTarget<F, T>> {

    private final List<FeatureTarget<F, T>> featureTargets = new ArrayList<>();

    public CommonDataset<F, T> addFeatureTarget(F feature, T target) {
        featureTargets.add(new FeatureTarget<>(feature, target));

        return this;
    }

    @Override
    public FeatureTarget<F, T> getFeatureTargetAt(int index) {
        return featureTargets.get(index);
    }

    @Override
    public int getDatasetSize() {
        return featureTargets.size();
    }

    @Override
    public Iterator<FeatureTarget<F, T>> iterator() {
        return featureTargets.iterator();
    }
}
