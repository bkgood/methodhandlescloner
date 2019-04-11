import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class Main {
    private static class Data {
        int x;
        String name = "UNSET";
        double y;

        void copyFrom(Data src) {
            try {
                copyToThis.invokeExact(src);
            } catch (Throwable t) {
                throw new RuntimeException(
                    "unexpected exception during copying",
                    t);
            }
        }

        private static MethodHandle copyFrom
            = Cloner.ofClass(Data.class, MethodHandles.lookup());
        private MethodHandle copyToThis = copyFrom.bindTo(this);

        @Override
        public String toString() {
            return String.format(
                "Data(x=%d, name=\"%s\", y=%f)",
                x, name, y);
        }
    }

    public static void main(String[] args) {
        Data d1 = new Data();
        Data d2 = new Data();

        d1.x = 5;
        d1.name = "will";
        d1.y = Math.PI;

        System.out.printf("d1=%s\nd2=%s\n", d1, d2);

        d2.copyFrom(d1);

        System.out.printf("d1=%s\nd2=%s\n", d1, d2);
    }
}
