package data_manipulation;

/**
 *
 * @param <F> FeatureType
 * @param <T> TargetType
 */
public interface Dataset<F, T> {

    FeatureTarget<F, T> getFeatureTargetAt(int index);

    int getDatasetSize();
}
