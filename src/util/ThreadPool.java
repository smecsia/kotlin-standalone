package util;

/**
 * Primitive fixed size thread pool
 */
public class ThreadPool {

    private final BlockingQueue<Runnable> queue;
    private final PooledThread[] threads;

    public ThreadPool(int poolSize) {
        threads = new PooledThread[poolSize];
        queue = new BlockingQueue<>(1000);
        for (int i = 0; i < poolSize; ++i) {
            threads[i] = new PooledThread();
            threads[i].start();
        }
    }

    synchronized public void execute(Runnable job) {
        try {
            queue.offer(job);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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