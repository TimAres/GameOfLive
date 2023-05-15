package de.hawhamburg.inf.gol;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Main application class.
 *
 * @author Christian Lins
 */
public class Application {

    /* Size of the playground in X dimension */
    public static final int DIM_X = 200;

    /* Size of the playground in Y dimension */
    public static final int DIM_Y = 200;

    /* Probability threshold that a cell is initially being created */
    public static final float ALIVE_PROBABILITY = 0.1125f;

    /* Sleep time between every generation in milliseconds */
    public static final int SLEEP = 200;

    /**
     * Creates a potentially unlimited stream of Cell objects. The stream uses
     * random numbers between [0, 1] and the probability threshold whether a
     * cell is created DEAD (random > p) or ALIVE (random <= p).
     *
     * @param p Cell alive probability threshold.
     * @return Stream of Cell objects.
     */
    private static Stream<Cell> createCellStream(float p) {
        return Stream.generate(new Supplier<Cell>() {
            public Cell get() {
                if (Math.random() <= p) {
                    return new Cell(Cell.ALIVE);
                } else {
                    return new Cell(Cell.DEAD);
                }
            }
        });
    }

    public static void main(String[] args) {
        Stream<Cell> cellStream = createCellStream(ALIVE_PROBABILITY);
        Playground playground = new Playground(DIM_X, DIM_Y, cellStream);

        // Create and show the application window
        ApplicationFrame window = new ApplicationFrame();
        window.setVisible(true);
        window.getContentPane().add(new PlaygroundComponent(playground));

        // Create and start a LifeThreadPool with 50 threads
        LifeThreadPool pool = new LifeThreadPool(50);
        pool.start();

        while (true) {
            int count = 0;
            Life life = new Life(playground);
            List<Cell> cells = playground.asList();
            for (int xi = 0; xi < DIM_X; xi++) {
                for (int yi = 0; yi < DIM_Y; yi++) {
                    final int x = xi;
                    final int y = yi;
                    final int c = count;
                    pool.submit(() -> {
                        life.process(cells.get(c), x, y);
                    });
                    count++;
                }
            }
            pool.start();
            try {
                // Wait for all threads to finish this generation
                //pool.barrier();
                pool.joinAndExit();
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Submit switch to next generation for each cell and force a
            // window repaint to update the graphics
            pool.submit(() -> {
                playground.asList().forEach(cell -> cell.nextGen());
                window.repaint();
            });

            try {
                // Wait SLEEP milliseconds until the next generation
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
