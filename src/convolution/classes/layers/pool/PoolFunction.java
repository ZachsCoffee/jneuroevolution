package layers.pool;

public enum PoolFunction {
    MAX(new Function() {
        float max;
        @Override
        public void compute(float value) {
            if (value > max) {
                max = value;
            }
        }

        @Override
        public float getResult() {
            float temp = max;
            max = 0;
            return temp;
        }
    }),

    AVERAGE(new Function() {
        float sum;
        int count;
        @Override
        public void compute(float value) {
            sum += value;
            count++;
        }

        @Override
        public float getResult() {
            if (count == 0) {
                return 0;
            }

            float temp = sum / count;
            sum = count = 0;
            return temp;
        }
    }),

    SUM(new Function() {
        float sum;
        @Override
        public void compute(float value) {
            sum += value;
        }

        @Override
        public float getResult() {
            float temp = sum;
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
        void compute(float value);

        float getResult();
    }
}
