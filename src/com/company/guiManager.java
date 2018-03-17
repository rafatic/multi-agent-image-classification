package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class guiManager extends JPanel{
    private JButton btn_simulate;
    private JLabel lbl_image;
    private JPanel pnl_main;

    private ArrayList<Point> germsCoordinates;
    private BufferedImage bi, originalImage;


    public guiManager(String imagePath) throws IOException {

        germsCoordinates = new ArrayList<Point>();
        BufferedImage image = ImageIO.read(new File(imagePath));
        bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        originalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        lbl_image.setIcon(new ImageIcon(bi));

        for(int y = 0; y < image.getHeight(null); y++)
        {
            for(int x = 0; x < image.getWidth(null); x++)
            {
                bi.setRGB(x, y, image.getRGB(x, y));
            }
        }

        lbl_image.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {

                System.out.println("CLICKED : " + e.getPoint().x + " : " + e.getPoint().y);
                bi.setRGB(e.getX() - 1, e.getY(), Color.RED.getRGB());
                bi.setRGB(e.getX(), e.getY() -1 , Color.RED.getRGB());
                bi.setRGB(e.getX(), e.getY(), Color.RED.getRGB());
                bi.setRGB(e.getX() + 1, e.getY(), Color.RED.getRGB());
                bi.setRGB(e.getX(), e.getY() +1 , Color.RED.getRGB());


                if(!germsCoordinates.contains(e.getPoint()))
                {
                    germsCoordinates.add(e.getPoint());
                }
                lbl_image.repaint();
            }
        });

        btn_simulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked, found " + germsCoordinates.size() + " germs");

                if(germsCoordinates.size() != 0)
                {
                    System.out.println("Germs :");
                    for(Point x: germsCoordinates)
                    {
                        System.out.println(x.toString());
                    }

                }

            }
        });
    }

    public ArrayList<Point> getGermsCoordinates() {
        return germsCoordinates;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void createAndShowGUI()
    {
        JFrame frame = new JFrame("Image viewer");
        frame.setContentPane(pnl_main);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }


}
