package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class segmentationResultDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel lbl_segmentationResult;

    private BufferedImage bi;


    public void showDialog(String[][] segmentationMap) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        bi = new BufferedImage(segmentationMap.length, segmentationMap[0].length, BufferedImage.TYPE_INT_RGB);

        ImageIcon icon = new ImageIcon(bi);
        lbl_segmentationResult.setIcon(icon);

        for(int y = 0; y < segmentationMap[0].length; y++)
        {
            for(int x = 0; x < segmentationMap.length; x++)
            {
                if(!segmentationMap[x][y].equals(""))
                {
                    bi.setRGB(x, y, society.worker_color.get(society.worker_roles.indexOf(segmentationMap[x][y])).getRGB());
                }
                else
                {
                    bi.setRGB(x, y, Color.PINK.getRGB());
                }
            }
        }

        try {
            File outputfile = new File("result.png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            // handle exception
        }


        pack();
        setVisible(true);
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
