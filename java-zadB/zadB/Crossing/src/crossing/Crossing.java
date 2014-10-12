/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Cross.java
 *
 * Created on 2012-01-02, 19:03:14
 */
package crossing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author r
 */
public class Crossing extends javax.swing.JPanel {
    Timer timer;
    Image bg = new ImageIcon(getClass().getResource("/crossing/resources/crossing_bg.png")).getImage();
    ArrayList<Car> cars = new ArrayList<Car>();
    CrossingManager cm = new CrossingManager();

    /** Creates new form Crossing */
    public Crossing() {
        initComponents();
        
        timer = new Timer(40, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                for (Car c : cars) {
                    if (c.t > 710) {
                        cars.remove(c);
                        break;
                    }
                }
                repaint();
            }
        });
        timer.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(700, 700));
        setPreferredSize(new java.awt.Dimension(700, 700));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(bg, 0, 0, 700, 700, null);
        
        // helpers
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(300, 300, 100, 100);

        for(Car c : cars) {
            c.paint(g);
        }
    }
    
    public void addCar(Direction source, Direction destination, double speed) {
        Car c = new Car(cm, source, destination, speed);
        cars.add(c);
        new Thread(c).start();
    }
}