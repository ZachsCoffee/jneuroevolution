package networks.gpu;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Test {
    public static void main(String[] _args) {

        final int size = 100000;

        final float[] a = new float[size];
        final float[] b = new float[size];

        for (int i = 0; i < size; i++) {
            a[i] = (float) (Math.random() * 100);
            b[i] = (float) (Math.random() * 100);
        }

        float[] sum = new float[size];
        long time = System.currentTimeMillis();
        Kernel kernel = new Kernel(){
            @Override public void run() {
                int gid = getGlobalId();
                sum[gid] = a[gid] + b[gid];
            }
        };

        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        System.out.println(System.currentTimeMillis() - time);
        kernel.execute(Range.create(size));

//        for (int i = 0; i < size; i++) {
//            System.out.printf("%6.2f + %6.2f = %8.2f\n", a[i], b[i], sum[i]);
//        }

        kernel.dispose();
    }
}
