package neuroevolution;

public class NeuroevolutionGenesOptions {

    private final double weightStatusMutationChance;
    private final double deactivateWeightMutationChange;

    private NeuroevolutionGenesOptions(double weightStatusMutationChance, double deactivateWeightMutationChange) {
        validate(weightStatusMutationChance);
        validate(deactivateWeightMutationChange);

        this.weightStatusMutationChance = weightStatusMutationChance;
        this.deactivateWeightMutationChange = deactivateWeightMutationChange;
    }

    public double getWeightStatusMutationChance() {
        return weightStatusMutationChance;
    }

    public double getDeactivateWeightMutationChange() {
        return deactivateWeightMutationChange;
    }

    public boolean hasDeactivationChange() {
        return weightStatusMutationChance != 0 && deactivateWeightMutationChange != 0;
    }

    private void validate(double chance) {
        if (chance < 0 || chance > 1) throw new IllegalArgumentException(
            "The change must be at range [0, 1]. Given: " + chance
        );
    }

    public static class Builder {

        public static Builder getInstance() {
            return new Builder();
        }

        private double weightStatusMutationChance = 0;
        private double deactivateWeightMutationChange = .5;

        public double getWeightStatusMutationChance() {
            return weightStatusMutationChance;
        }

        public Builder setWeightStatusMutationChance(double weightStatusMutationChance) {
            this.weightStatusMutationChance = weightStatusMutationChance;
            return this;
        }

        public double getDeactivateWeightMutationChange() {
            return deactivateWeightMutationChange;
        }

        public Builder setDeactivateWeightMutationChange(double deactivateWeightMutationChange) {
            this.deactivateWeightMutationChange = deactivateWeightMutationChange;
            return this;
        }

        public NeuroevolutionGenesOptions build() {
            return new NeuroevolutionGenesOptions(weightStatusMutationChance, deactivateWeightMutationChange);
        }
    }
}
