package com.company;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JPanel {

    public static void main(String[] args) throws IOException {

        guiManager ui = new guiManager("./img/casa-blanca-habitations_small.png");

        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                ui.createAndShowGUI();
            }
        });

    }
}
