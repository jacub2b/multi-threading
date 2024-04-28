import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        ComplexCalculation complexCalculation = new ComplexCalculation();
        BigInteger bigInteger = complexCalculation.calculateResult(BigInteger.valueOf(1L), BigInteger.valueOf(10), BigInteger.valueOf(3), BigInteger.valueOf(3));
        System.out.println(bigInteger);
    }

    public static class DelayThread extends Thread {
        public DelayThread() {
            this.setUncaughtExceptionHandler((Thread t, Throwable e) -> System.out.println("Hello " + e));
//            this.setDaemon(true);
        }

        @Override
        public void run() {
            System.out.println("good night");

            while (true) {
                if (Thread.currentThread().isInterrupted()) System.exit(43343);
                System.out.println("bla");
            }

//            System.out.println("good morning");
        }

    }
}