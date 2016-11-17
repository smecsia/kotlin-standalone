package util;

import java.util.concurrent.BlockingQueue;

/**
 * Primitive fixed size thread pool
 */
public class ThreadPool {

    private final BlockingQueue<Runnable> queue;
    private final PooledThread[] threads;

    public ThreadPool(int poolSize) {
        threads = new PooledThread[poolSize];
        queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < poolSize; ++i) {
            threads[i] = new PooledThread();
            threads[i].start();
        }
    }

    synchronized public void execute(Runnable job) {
        queue.offer(job);
    }

    private class PooledThread extends Thread {
        public void run() {
            while (true) {
                try {
                    final Runnable job = queue.take();
                    if (job == null) {
                        continue;
                    }
                    job.run();
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
    }

    public void join() {
        for (PooledThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}