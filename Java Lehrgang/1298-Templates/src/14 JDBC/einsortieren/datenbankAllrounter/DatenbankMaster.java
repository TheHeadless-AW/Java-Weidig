
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DatenbankMaster.java
 *
 * Created on 11.05.2010, 12:04:54
 */
package einsortieren.datenbankAllrounter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.*;

import einsortieren.meindatenbankclient.DBTyp;
import einsortieren.meindatenbankclient.DatenbankZugriff;
import einsortieren.meindatenbankclient.MyModel;
import einsortieren.meindatenbankclient.exceptions.NoConnectionException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;

/**
 *
 * @author dborkowitz
 */
public class DatenbankMaster extends JFrame {

    MyModel model;
    CompoModel compomodel;
    TableRowSorter sorter;

    private class CompoModel extends DefaultComboBoxModel {

        public void initialize(ResultSet result) {
            try {
                while (result.next()) {
                    this.addElement(result.getString(1));
                }

            } catch (SQLException ex) {
               JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    /** Creates new form DatenbankMaster */
    public DatenbankMaster() {
        model = new MyModel();
        sorter = new TableRowSorter();
        sorter.setModel(model);

        compomodel = new CompoModel();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (DatenbankZugriff.getInstance().isConnected()) {
                    try {
                        DatenbankZugriff.getInstance().close();
                    } catch (SQLException ex) {
                        Logger.getLogger(DatenbankMaster.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoConnectionException ex) {
                        Logger.getLogger(DatenbankMaster.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        initComponents();
        jButton4.setVisible(false);
        jTable1.setRowSorter(sorter);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Server:");

        jTextField1.setText("localhost");

        jLabel2.setText("Database:");

        jComboBox1.setEditable(true);
        jComboBox1.setModel(compomodel);
        jComboBox1.setEnabled(false);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Connect");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("User:");

        jLabel4.setText("Password:");

        jTextField2.setText("root");

        jPasswordField1.setText("password");

        jButton2.setText("Disconnect");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Back");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("MySQL");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("ORACLE");

        jTable1.setModel(model);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton4.setText("jButton4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4))
                            .addComponent(jComboBox1, 0, 394, Short.MAX_VALUE)))
                    .addComponent(jButton3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton4))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String host = jTextField1.getText();
        String user = jTextField2.getText();
        String password = String.copyValueOf(jPasswordField1.getPassword());
        if (host.length() > 0 && user.length() > 0 && password.length() > 0) {
            try {
                if (jRadioButton1.isSelected()) {
                    DatenbankZugriff.getInstance().setTyp(DBTyp.MySQL);
                } else {
                    DatenbankZugriff.getInstance().setTyp(DBTyp.ORACLE);
                }
                DatenbankZugriff.getInstance().connect(host, user, password);
                if (jRadioButton1.isSelected()) {
                    jComboBox1.setEnabled(true);
                    compomodel.initialize(DatenbankZugriff.getInstance().getAllDatabases());
                } else {
                    ResultSet tables = DatenbankZugriff.getInstance().executeQuery("SELECT TABLE_NAME FROM user_tables");
                    
                    model.setSet("Tables", tables);
                    jButton3.setEnabled(false);
                }
                jButton1.setEnabled(false);
                jButton2.setEnabled(true);
            } catch (Exception ex) {
                //Logger.getLogger(DatenbankMaster.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        try {
            String database = (String) jComboBox1.getSelectedItem();
            if (DatenbankZugriff.getInstance().isConnected() && database != null) {
                ResultSet tables = DatenbankZugriff.getInstance().executeQuery("show tables;", database);
                model.setSet("Tables", tables);
                jButton3.setEnabled(false);
            }
        } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            compomodel.removeAllElements();
            model.clear();
            DatenbankZugriff.getInstance().close();
            jComboBox1.setEnabled(false);
            jButton1.setEnabled(true);
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } 
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            if (DatenbankZugriff.getInstance().isConnected()) {
                if (jRadioButton1.isSelected()) {
                    String database = (String) jComboBox1.getSelectedItem();
                    if (database != null) {
                        ResultSet tables = DatenbankZugriff.getInstance().executeQuery("show tables;", database);
                        model.setSet("Tables", tables);
                    }
                } else {
                    ResultSet tables = DatenbankZugriff.getInstance().executeQuery("SELECT TABLE_NAME FROM USER_TABLES");
                    model.setSet("Tables", tables);
                }
                jButton3.setEnabled(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } 
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1
                && evt.getClickCount() == 2
                && !jButton3.isEnabled()) {
            try {
                String table = model.getValueAt(sorter.convertRowIndexToModel(jTable1.getSelectedRow()), jTable1.getSelectedColumn()).toString();

                ResultSet set;
                if (jRadioButton1.isSelected()) {
                    String database = jComboBox1.getSelectedItem().toString();
                    set = DatenbankZugriff.getInstance().executeQuery("SELECT * from " + table, database);
                } else {
                    set = DatenbankZugriff.getInstance().executeQuery("SELECT * from " + table);

                }
                model.setSet(set);
                jButton3.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            /*
             *  stmt = connection.prepareCall("begin nxt_flight_time_and_num_routes(?,?,?); end;");
             * Binds the parameter types
             *  stmt.setString(1, flightCode);
             * Bind 1st parameter
             * stmt.setInt(2, departureTime);
             * Bind 2nd parameter
             * stmt.registerOutParameter(2, Types.INTEGER);
             * 2nd parameter is IN OUT parameter
             * stmt.registerOutParameter(3, Types.INTEGER);
             * 3rd parameter is OUT paremeter
             * Execute the callable statement
             * stmt.execute();
             * departureTime = stmt.getInt(2); // Get Next DepartureTime
             * numRoutes = stmt.getInt(3);     // Get total number of routes
             */
            CallableStatement stmt = DatenbankZugriff.getInstance().con.prepareCall("begin pkg_workflow.proc_buchen; end;");
              stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatenbankMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

        /**
         * @param args the command line arguments
         */
    public

    static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DatenbankMaster().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
