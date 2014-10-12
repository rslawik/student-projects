/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crossing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author r
 */
public class Car implements Runnable {
    protected double t = 0, speed;
    private int rotate;
    protected Direction source, destination;
    protected CrossingManager cm;
    Direction turning = Direction.UP;
    int blinker = -1;
    boolean turnleft, turnright;
    
    final int WIDTH = 32;
    final int HEIGHT = 16;
    
    public Car(CrossingManager cm, Direction source, Direction destination, double speed) {
        assert source != destination;
        assert speed > 0;
        
        this.cm = cm;
        this.speed = speed;
        this.source = source;
        this.destination = destination;
        
        turnright = destination.ordinal() == (source.ordinal() + 3) % 4;
        turnleft = destination.ordinal() == (source.ordinal() + 1) % 4;
    }
    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        if (blinker >= 0)
            blinker = blinker == 20 ? 0 : blinker + 1;
        
        g2d.rotate(rotate*Math.PI/2, 350, 350);

        if (turning == Direction.RIGHT) {
            g2d.translate(300, 400);
            g2d.rotate(t/30);
            g2d.fillRect(-WIDTH/2, -30-HEIGHT/2, WIDTH, HEIGHT);
            if (blinker > 9) {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(-WIDTH/2, -30+HEIGHT/2-3, WIDTH, 3);
                g2d.setColor(Color.YELLOW);
            }
            g2d.rotate(-t/30);
            g2d.translate(-300, -400);
        } else if (turning == Direction.LEFT) {
            g2d.translate(300, 300);
            g2d.rotate(-t/70);
            g2d.fillRect(-WIDTH/2, 70-HEIGHT/2, WIDTH, HEIGHT);
            if (blinker > 9) {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(-WIDTH/2, 70-HEIGHT/2, WIDTH, 3);
                g2d.setColor(Color.YELLOW);
            }
            g2d.rotate(t/70);
            g2d.translate(-300, -300);
        } else {
            g2d.fillRect((int)t-WIDTH/2, 370-HEIGHT/2, WIDTH, HEIGHT);
            if (blinker > 9) {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect((int)t-WIDTH/2, 370-HEIGHT/2 + (turnright ? HEIGHT-3 : 0), WIDTH, 3);
                g2d.setColor(Color.YELLOW);
            }
        }
        
        g2d.rotate(-rotate*Math.PI/2, 350, 350);
    }

    
    @Override
    public void run() {
        // towards the cross
        rotate = source.ordinal();
        CrossingQueue q = cm.getInQueue(source);
        q.enter(this);
        while (t+WIDTH/2 < 300) {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {}
            q.move(this);
            t += speed;
            
            if (t > 150 && (turnright || turnleft))
                blinker = blinker == -1 ? 0 : blinker;
        }
        
        // arrive
        cm.arrive(source, destination);
        
        // waiting
        try {
            //Thread.sleep(1000);
            Thread.sleep(750);
        } catch (InterruptedException e) {}
        
        // ask
        cm.ask(source);
        
        // going through the cross
        while (t < 300) {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {}
            q.move(this);
            t += speed;
        }
        q.leave();
        if (destination.ordinal() == (source.ordinal() + 3) % 4) {
            // turn right
            turning = Direction.RIGHT;
            t = 0;
            while (Math.sin(-Math.PI/2+t/30) < 0) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {}
                //q.move(this);
                t += speed;
            }
            rotate = (destination.ordinal() + 2) % 4;
            t = 400;
        } else if (destination.ordinal() == (source.ordinal() + 1) % 4) {
            // turn left
            turning = Direction.LEFT;
            t = 0;
            while (Math.sin(-Math.PI/2+t/70) < 0) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {}
                //q.move(this);
                t += speed;
            }
            rotate = (destination.ordinal() + 2) % 4;
            t = 400;
        } else {
            // go straight
            while (t < 400) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {}
                //q.move(this);
                t += speed;
            }
        }
        turning = Direction.DOWN;
        
        // leave
        cm.leave(source);
        //q.leave();
        q = cm.getOutQueue(destination);
        q.enter(this);
        
        // fromwards the cross
        while (t-WIDTH/2 < 700) {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {}
            q.move(this);
            t += speed;
            
            if (t > 500) {
                blinker = -1;
            }
        }
        
        // leave
        q.leave();
    }
}
