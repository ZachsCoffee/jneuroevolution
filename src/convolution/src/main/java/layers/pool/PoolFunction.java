package layers.pool;

public enum PoolFunction {
    MAX(new Function() {
        double max;
        @Override
        public void compute(double value) {
            if (value > max) {
                max = value;
            }
        }

        @Override
        public double getResult() {
            double temp = max;
            max = 0;
            return temp;
        }
    }),

    AVERAGE(new Function() {
        double sum;
        int count;
        @Override
        public void compute(double value) {
            sum += value;
            count++;
        }

        @Override
        public double getResult() {
            if (count == 0) {
                return 0;
            }

            double temp = sum / count;
            sum = count = 0;
            return temp;
        }
    }),

    SUM(new Function() {
        double sum;
        @Override
        public void compute(double value) {
            sum += value;
        }

        @Override
        public double getResult() {
            double temp = sum;
            sum = 0;
            return temp;
        }
    });

    private final Function function;

    PoolFunction(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public interface Function {
        void compute(double value);

        double getResult();
    }
}
