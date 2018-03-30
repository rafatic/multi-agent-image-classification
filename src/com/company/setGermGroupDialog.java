package com.company;

import javax.swing.*;
import java.awt.event.*;

public class setGermGroupDialog extends JDialog {
    private JPanel contentPane;
    private JButton btn_save;
    private JButton btn_remove;
    private JLabel lbl_createGermInfo;
    private JComboBox cbx_selectGroup;
    private JPanel lbl_germInfos;
    private JLabel lbl_germsInfos;
    private static germ createdGerm;





    public setGermGroupDialog(germ g) {

        createdGerm = g;
        cbx_selectGroup.setModel(new DefaultComboBoxModel(society.worker_roles.toArray()));
        lbl_germsInfos.setText("Creating germs n°" + createdGerm.getID() + " on location [" + createdGerm.getLocation().x + ", " + createdGerm.getLocation().y + "]");

    }

    public void showDialog()
    {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btn_save);

        btn_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                createdGerm.setGroupId(cbx_selectGroup.getSelectedIndex());
                System.out.println("Selected group : " + cbx_selectGroup.getSelectedItem().toString());
                onOK();
            }
        });

        btn_remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdGerm.setGroupId(-1);
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                createdGerm.setGroupId(-1);
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdGerm.setGroupId(-1);
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.pack();
        this.setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
