/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crossing;

import java.util.ArrayList;

/**
 *
 * @author r
 */
public class CrossingQueue {
    ArrayList<Car> list = new ArrayList<Car>();
    
    public synchronized void enter(Car c) {
        //System.err.println("enter");
        list.add(c);
    }
    
    public synchronized void leave() {
        //System.err.println("leave");
        list.remove(0);
        notifyAll();
    }
    
    public synchronized void move(Car c) {
        while (!canMove(c)) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        notifyAll();
    }
    
    private boolean canMove(Car c) {
        int p = list.indexOf(c);
        if (p == 0)
            return true;
        Car pc = list.get(p - 1);
        if (c.t + c.WIDTH/2 + c.speed + 4 >= pc.t - pc.WIDTH/2) {
            c.speed = pc.speed;
            return false;
        }
        return true;
    }
}
