import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class SemaphoreExample {
    public static void main(String[] args) {
        final int CAPACITY = 20;

        Queue<Object> queue = new ArrayBlockingQueue<>(CAPACITY);
        Semaphore consumerSemaphore = new Semaphore(0);
        Semaphore producerSemaphore = new Semaphore(CAPACITY);
        List<Producer> producers = createProducers(10, queue, producerSemaphore, consumerSemaphore);
        List<Consumer> consumers = createConsumers(20, queue, consumerSemaphore, producerSemaphore);
        producers.forEach(Thread::start);
        consumers.forEach(Thread::start);
    }

    private static List<Consumer> createConsumers(int amount, Queue<Object> queue, Semaphore consumerSemaphore, Semaphore producerSemaphore) {
        return IntStream.range(0, amount).mapToObj(i -> new Consumer("producer " + i, queue, producerSemaphore, consumerSemaphore, Duration.ofMillis(2000))).toList();
    }

    private static List<Producer> createProducers(int amount, Queue<Object> queue, Semaphore producerSemaphore, Semaphore consumerSemaphore) {
        return IntStream.range(0, amount).mapToObj(i -> new Producer("producer " + i, queue, producerSemaphore, consumerSemaphore, Duration.ofMillis(10))).toList();
    }

    private static class Producer extends Thread{
        private Queue<Object> queue;
        private Semaphore producerSemaphore;
        private Semaphore consumerSemaphore;
        private Duration rateLimiter;

        public Producer(String name,
                        Queue<Object> queue,
                        Semaphore producerSemaphore,
                        Semaphore consumerSemaphore,
                        Duration rateLimiter) {
            super(name);
            this.queue = queue;
            this.producerSemaphore = producerSemaphore;
            this.consumerSemaphore = consumerSemaphore;
            this.rateLimiter = rateLimiter;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // No matter the rate of production set (any change in sleep time),
                    // the producer doesn't produce more than the consumer consumes
                    Thread.sleep(rateLimiter);
                } catch (InterruptedException e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }

                try {
                    producerSemaphore.acquire();
                } catch (InterruptedException e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }

                queue.offer(new Object());
                System.out.println(STR."\{this.getName()} produced object to queue");

                consumerSemaphore.release();
            }
        }
    }

    private static class Consumer extends Thread{
        private Queue<Object> queue;
        private Semaphore producerSemaphore;
        private Semaphore consumerSemaphore;
        private Duration rateLimiter;

        public Consumer(String name,
                        Queue<Object> queue,
                        Semaphore producerSemaphore,
                        Semaphore consumerSemaphore,
                        Duration rateLimiter) {
            super(name);
            this.queue = queue;
            this.producerSemaphore = producerSemaphore;
            this.consumerSemaphore = consumerSemaphore;
            this.rateLimiter = rateLimiter;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // No matter the rate of consumption set (any change in sleep time),
                    // the consumers thread will sleep and not consume resources until the producer produces
                    Thread.sleep(rateLimiter);
                } catch (InterruptedException e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }

                try {
                    consumerSemaphore.acquire();
                } catch (InterruptedException e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }

                queue.remove();
                System.out.println(STR."\{this.getName()} consumed object from queue");

                producerSemaphore.release();
            }
        }
    }
}
