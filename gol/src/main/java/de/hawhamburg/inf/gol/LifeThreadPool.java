package de.hawhamburg.inf.gol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A pool of LifeThreads and a queue holding to be processed.
 * 
 * @author Christian Lins
 */
public class LifeThreadPool {
    /* Unsynchronized Queue of Runnables */
    private final Queue<Runnable> tasks = new LinkedList<>();
    
    /* Number of threads managed by this pool */
    private final int numThreads;
    
    /* The collection of LifeThread instances forming this pool */
    private final LifeThread[] threads;
    
    public LifeThreadPool(int numThreads) {
        this.numThreads = numThreads;
        this.threads = new LifeThread[numThreads];
    }
    
    /**
     * This method will block until the queue of tasks has been emptied by the
     * running threads.
     * @throws InterruptedException 
     */
    public void barrier() throws InterruptedException {
    boolean areThreadsRunnig = true;
    
        /*while (areThreadsRunnig)

            areThreadsRunnig = false;
            for (LifeThread thread : threads)
            {
                if (thread != null && thread.isAlive())
                {
                    areThreadsRunnig = true;
                    break;
                }
            }
        if (areThreadsRunnig)
        {
            Thread.sleep(100);
        }*/
        while (!tasks.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                System.err.println("Application InterruptedException");
            }
        }
    }
    /**
     * Calls interrupt() on every thread in this pool.
     */
    public synchronized void interrupt()
    {
        //Stream.of(threads).forEach(Thread::interrupt);
        Arrays.stream(threads).forEach(t -> t.interrupt());
    }

    
    /**
     * Waits for all tasks to finish and calls interrupt on every thread. This
     * method is identical to synchronized calling barrier() and interrupt().
     * 
     * @throws InterruptedException 
     */
    public synchronized void joinAndExit() throws InterruptedException
    {
        barrier(); 
        interrupt(); 
    }
    /**
     * Adds a task to the queue of this pool.
     * 
     * @param task Runnable containing the work to be done 
     */
    public void submit(Runnable task) {
    synchronized (tasks) {
        /*tasks.offer(task);
        tasks.notify();*/
        tasks.add(task);
    }

        
    }
    
    /**
     * Removes and returns the next task from the queue of this pool.
     * This method blocks if the queue is currently empty.
     * 
     * @return Next task from the pool queue
     * @throws InterruptedException 
     */
    public Runnable nextTask() throws InterruptedException {
    /*while (tasks.isEmpty()) {
        tasks.wait();
    }
    return tasks.poll(); */
        synchronized(tasks) {
            while (tasks.isEmpty()) {
                tasks.wait();
            }
            return tasks.poll();
        }
}

    
    
    public void start() {
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new LifeThread(this);
            //threads[i].start();
        }
        Arrays.stream(threads).forEach(t -> t.start());
        }
    
}

