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
        while (!tasks.isEmpty()) { // Solange die tasks liste leer ist 
            try {
                Thread.sleep(100); // thread pausieren 
            } catch (InterruptedException ie) {
                System.err.println("Application InterruptedException"); // Wenn eine InterruptedException auftritt, gebe eine Fehlermeldung aus
            }
        }
    }
    /**
     * Calls interrupt() on every thread in this pool.
     */
    public synchronized void interrupt()
    {
        Stream.of(threads).forEach(Thread::interrupt); // Unterbreche jeden Thread in der 'threads'-Sammlung
    }

    
    /**
     * Waits for all tasks to finish and calls interrupt on every thread. This
     * method is identical to synchronized calling barrier() and interrupt().
     * 
     * @throws InterruptedException 
     */
    public synchronized void joinAndExit() throws InterruptedException
    {
        barrier(); // Warte, bis alle Aufgaben abgeschlossen sind, indem die Methode 'barrier()' aufgerufen wird
        interrupt(); // Unterbreche alle Threads, indem die Methode 'interrupt()' aufgerufen wird
    }
    /**
     * Adds a task to the queue of this pool.
     * 
     * @param task Runnable containing the work to be done 
     */
    public void submit(Runnable task) {
    synchronized (tasks) {
        tasks.offer(task);
        tasks.notify();
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
        synchronized(tasks) { // Synchronisiere den Zugriff auf die tasks-Liste
            while (tasks.isEmpty()) { // Füge die Aufgabe zur tasks-Liste hinzu
                tasks.wait(); // Benachrichtige andere Threads, die auf tasks warten 
            }
            return tasks.poll();
        }
}

    
    
    public void start() {
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new LifeThread(this); // Erstelle einen neuen LifeThread und speichere ihn im threads-Array
        }
        Arrays.stream(threads).forEach(t -> t.start()); // Starte jeden Thread im 'threads'-Array
        }
    
}

