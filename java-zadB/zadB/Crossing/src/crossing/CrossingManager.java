/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crossing;

/**
 *
 * @author r
 */
public class CrossingManager {
    private boolean[][] state = new boolean[4][4];
    private boolean busy;
    private Direction privilaged;
    private CrossingQueue[] in = new CrossingQueue[4];
    private CrossingQueue[] out = new CrossingQueue[4];
    {
        for (int i = 0; i < 4; i++) {
            in[i] = new CrossingQueue();
            out[i] = new CrossingQueue();
        }
    }
    
    
    public synchronized void arrive(Direction s, Direction d) {
        //System.err.println("arrive" + s.ordinal());
        while (busy) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        int k = d.ordinal();
        do {
            k = (k + 1) % 4;
            state[s.ordinal()][k] = true;
        } while (k != s.ordinal());
        
        if (stuck())
            privilaged = s;
    }
    
    public synchronized void ask(Direction d) {
        /*for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                System.out.print(state[i][j] + " ");
            System.out.print("\n");
        */
        //System.err.println("ask" + d.ordinal());
        while (!canGo(d) || busy) {
            //System.err.println(d.ordinal() + " has to wait");
            try {
                wait();
            } catch (InterruptedException e) {};
        }
        busy = true;
    }
    
    public synchronized void leave(Direction d) {
        //System.err.println("leave" + d.ordinal());
        for (int i = 0; i < 4; i++)
            state[d.ordinal()][i] = false;
        busy = false;
        privilaged = null;
        notifyAll();
    }
    
    boolean canGo(Direction d) {
        if (d == privilaged)
            return true;
        for (int i = 0; i < 4; i++) {
            if (i == d.ordinal())
                continue;
            if (state[d.ordinal()][i] && state[i][i])
                return false;
        }
        return true;
    }
    
    boolean stuck() {
        for (int i = 0; i < 4; i++)
            if (state[i][i] && canGo(Direction.values()[i]))
                return false;
        return true;
    }
    
    public CrossingQueue getInQueue(Direction d) {
        return in[d.ordinal()];
    }
    
    public CrossingQueue getOutQueue(Direction d) {
        return out[d.ordinal()];
    }
}
