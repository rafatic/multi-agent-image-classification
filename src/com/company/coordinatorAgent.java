package com.company;

import madkit.kernel.AbstractAgent;
import madkit.kernel.Agent;
import madkit.kernel.Madkit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class coordinatorAgent extends Agent {

    private ArrayList<AbstractAgent> workers;
    public static ArrayList<Point> startingPoints;
    public static BufferedImage originalImage;
    private int[][] segmentationMap;
    /*public coordinatorAgent(ArrayList<imageAgent> workers)
    {
        this.workers = workers;
    }*/

    @Override
    protected void activate() {
        getLogger().info("Activating coordinator agent and workers");

        workers = new ArrayList<>();
        segmentationMap = new int[originalImage.getWidth()][originalImage.getHeight()];

        for(int y = 0; y < originalImage.getHeight(); y++)
        {
            for(int x = 0; x < originalImage.getWidth(); x++)
            {
                segmentationMap[x][y] = -1;
            }
        }
        for(Point p: startingPoints)
        {
            imageAgent a = new imageAgent(originalImage.getRGB(p.x, p.y), p);
            if(launchAgent(a, true) == ReturnCode.SUCCESS)
            {
                this.workers.add(a);
                getLogger().info("Created agent on point " + p.x + ", " + p.y);
            }
        }

    }

    @Override
    protected void live() {
        super.live();
    }

    @Override
    protected void end() {
        super.end();
    }

    public static void main(ArrayList<Point> germs, BufferedImage image)
    {
        startingPoints = germs;
        originalImage = image;


        executeThisAgent();

        //String[] args = { "--launchAgents", coordinatorAgent.class.getName() + ",true,1" };
        //Madkit.main(args);
    }
}
