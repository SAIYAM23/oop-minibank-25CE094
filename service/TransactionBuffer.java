package service;

public class TransactionBuffer {

    private final int[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public TransactionBuffer(int capacity) {
        buffer = new int[capacity];
    }

    public synchronized void produce(int value)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = value;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Produced: " + value);

        notifyAll();
    }

    public synchronized int consume()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int value = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("Consumed: " + value);

        notifyAll();

        return value;
    }
}
