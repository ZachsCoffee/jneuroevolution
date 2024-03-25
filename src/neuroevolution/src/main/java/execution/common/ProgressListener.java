package execution.common;

public interface ProgressListener {
    void epochUpdate(int currentEpoch);

    void evolutionBestUpdate(double bestPersonFitness);
}
