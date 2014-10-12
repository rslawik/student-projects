/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Window.java
 *
 * Created on 2012-01-02, 18:45:15
 */
package crossing;

import java.util.Random;

/**
 *
 * @author r
 */
public class Window extends javax.swing.JFrame {
    /** Creates new form Window */
    public Window() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        crossing = new crossing.Crossing();
        jPanel1 = new javax.swing.JPanel();
        sComboBox = new javax.swing.JComboBox();
        dComboBox = new javax.swing.JComboBox();
        speedTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        randomButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Skrzyżowanie");
        setMinimumSize(new java.awt.Dimension(700, 700));
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        javax.swing.GroupLayout crossingLayout = new javax.swing.GroupLayout(crossing);
        crossing.setLayout(crossingLayout);
        crossingLayout.setHorizontalGroup(
            crossingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        crossingLayout.setVerticalGroup(
            crossingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );

        getContentPane().add(crossing);

        jPanel1.setPreferredSize(new java.awt.Dimension(100, 122));

        sComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lewo", "Góra", "Prawo", "Dół" }));
        jPanel1.add(sComboBox);

        dComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lewo", "Góra", "Prawo", "Dół" }));
        jPanel1.add(dComboBox);

        speedTextField.setPreferredSize(new java.awt.Dimension(50, 20));
        jPanel1.add(speedTextField);

        addButton.setLabel("Dodaj");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addButton);

        randomButton.setText("Losowo");
        randomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomButtonActionPerformed(evt);
            }
        });
        jPanel1.add(randomButton);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            if (sComboBox.getSelectedIndex() == dComboBox.getSelectedIndex())
                throw new Exception("really?!");
            double v = Double.parseDouble(speedTextField.getText());
            crossing.addCar(Direction.values()[sComboBox.getSelectedIndex()], Direction.values()[dComboBox.getSelectedIndex()], v);
        } catch (NumberFormatException e) {
            System.err.println("parse error");
        } catch (Exception e) {
            System.err.println("really?!");
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void randomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomButtonActionPerformed
        Random r = new Random();
        int s = r.nextInt(4);
        int d = s;
        while (d == s)
            d = r.nextInt(4);
        double v = 0;
        while (v < 0.5)
            v = r.nextFloat() * r.nextInt(4);
        crossing.addCar(Direction.values()[s], Direction.values()[d], v);
    }//GEN-LAST:event_randomButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Window().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private crossing.Crossing crossing;
    private javax.swing.JComboBox dComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton randomButton;
    private javax.swing.JComboBox sComboBox;
    private javax.swing.JTextField speedTextField;
    // End of variables declaration//GEN-END:variables
}