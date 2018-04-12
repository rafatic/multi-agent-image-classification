package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class guiManager extends JPanel{
    private JButton btn_simulate;
    private JLabel lbl_image;
    private JPanel pnl_main;
    private JTextField txt_addGroup;
    private JLabel lbl_addGroup;
    private JButton btn_addGroup;
    private JComboBox cbx_groups;
    private JLabel lbl_availableGroups;
    private JTable tbl_germs;
    private JComboBox cbx_colorList;

    private ArrayList<Point> germsCoordinates;

    private ArrayList<germ> germsList;
    private int germIdIncrement;


    private BufferedImage bi, originalImage;


    private ArrayList<String> colorListModel;


    public guiManager(String imagePath) throws IOException {

        germsCoordinates = new ArrayList<Point>();
        germsList = new ArrayList<germ>();
        germIdIncrement = 0;
        BufferedImage image = ImageIO.read(new File(imagePath));
        bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        originalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        lbl_image.setIcon(new ImageIcon(bi));


        society.worker_roles.add("Immeuble");
        society.worker_color.add(Color.BLUE);
        society.worker_roles.add("Sol");
        society.worker_color.add(Color.RED);

        cbx_groups.setModel(new DefaultComboBoxModel(society.worker_roles.toArray()));

        DefaultTableModel germsTableModel = new DefaultTableModel();

        germsTableModel.addColumn("ID");
        germsTableModel.addColumn("Location");
        germsTableModel.addColumn("Group");

        tbl_germs.setModel(germsTableModel);


        colorListModel = new ArrayList<String>();

        colorListModel.add("black");
        colorListModel.add("white");
        colorListModel.add("gray");
        colorListModel.add("green");
        colorListModel.add("blue");
        colorListModel.add("red");
        colorListModel.add("magenta");
        colorListModel.add("yellow");
        cbx_colorList.setModel(new DefaultComboBoxModel(colorListModel.toArray()));


        for(int y = 0; y < image.getHeight(null); y++)
        {
            for(int x = 0; x < image.getWidth(null); x++)
            {
                bi.setRGB(x, y, image.getRGB(x, y));
                originalImage.setRGB(x, y, image.getRGB(x, y));
            }
        }

        lbl_image.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if(society.worker_roles.isEmpty())
                {
                    noGroupsErrorDialog.main(null);
                }
                else
                {


                    System.out.println("CLICKED : " + e.getPoint().x + " : " + e.getPoint().y);


                    germ g = new germ(e.getPoint(), germIdIncrement);
                    germIdIncrement++;
                    setGermGroupDialog dialog = new setGermGroupDialog(g);

                    dialog.showDialog();
                    if(g.getGroupId() != -1)
                    {



                        if(!germsList.contains(g))
                        {
                            germsList.add(g);

                            bi.setRGB(e.getX() - 1, e.getY(), Color.RED.getRGB());
                            bi.setRGB(e.getX(), e.getY() -1 , Color.RED.getRGB());
                            bi.setRGB(e.getX(), e.getY(), Color.RED.getRGB());
                            bi.setRGB(e.getX() + 1, e.getY(), Color.RED.getRGB());
                            bi.setRGB(e.getX(), e.getY() +1 , Color.RED.getRGB());

                            germsTableModel.addRow(new Object[]{g.getID(), g.getLocation().x + ", " + g.getLocation().y, society.worker_roles.get(g.getGroupId())});

                            tbl_germs.setModel(germsTableModel);



                            lbl_image.repaint();

                        }
                    }
                }

            }
        });

        btn_addGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!txt_addGroup.getText().isEmpty())
                {
                    if(!society.worker_roles.contains(txt_addGroup.getText()))
                    {
                        society.worker_roles.add(txt_addGroup.getText());
                        if(getColorByName(cbx_colorList.getSelectedItem().toString()) != null)
                        {
                            society.worker_color.add(getColorByName(cbx_colorList.getSelectedItem().toString()));
                            cbx_groups.setModel(new DefaultComboBoxModel(society.worker_roles.toArray()));
                        }
                        else
                        {
                            System.out.println("Unknown color : " + cbx_colorList.getSelectedItem().toString());
                        }

                    }
                    else
                    {
                        System.out.println("The group '" + txt_addGroup.getText() + "' already exists");
                    }
                }
                else
                {
                    System.out.println("Please enter a group name");
                }

            }
        });

        btn_simulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked, found " + germsList.size() + " germs");

                ArrayList<imageAgent> workers = new ArrayList<>();
                coordinatorAgent coordinator;

                coordinatorAgent.main(germsList, originalImage);

            }
        });
    }

    public ArrayList<Point> getGermsCoordinates() {
        return germsCoordinates;
    }

    public ArrayList<germ> getGermsList() {
        return germsList;
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


    private Color getColorByName(String name) {
        try {
            return (Color)Color.class.getField(name.toUpperCase()).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }


}
