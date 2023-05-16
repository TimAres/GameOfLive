package de.hawhamburg.inf.gol;

/**
 * Cell living on the screen.
 * 
 * @author Christian Lins
 */
public class Cell {
    
    public static final byte DEAD = (byte)0;
    public static final byte ALIVE = (byte)1;
    
    /* Current state of this cell, can be DEAD or ALIVE */
    private byte value = DEAD;
    
    /* Calculate state of the next generation */
    private byte valueNext = DEAD;
    
    public Cell(int value) {
        this.value = (byte)value;
    }
    
    public byte getValue() {
        return value;
    }
    
    /**
     * Returns true if this cell is considered to be alive.
     * @return true if cell is alive, false if cell is dead.
     */
    public boolean isAlive() {
        return value != 0;
    }
    
    /** Sets the state for this cell in the next generation.
     * @param value New state of this cell, can be DEAD or ALIVE.
      */
    public void setValueNext(byte value) {
        this.valueNext = value;
    }
    
    /** Make the next generation the current generation */
    public void nextGen() {
        this.value = this.valueNext;
        
    }
}
