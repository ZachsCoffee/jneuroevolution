package data_manipulation;

import java.util.List;

/**
 *
 * @param <F> FeatureType
 * @param <T> TargetType
 */
public interface Dataset<F, T> {

    FeatureTarget<F, T> getFeatureTargetAt(int index);

    int getDatasetSize();
}
