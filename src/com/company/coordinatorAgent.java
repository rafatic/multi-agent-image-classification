package com.company;

import madkit.kernel.AbstractAgent;
import madkit.kernel.Agent;
import madkit.kernel.Madkit;
import madkit.kernel.Message;
import madkit.message.ObjectMessage;
import madkit.message.StringMessage;

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

        createGroup(society.COMMUNITY, society.GROUP);
        requestRole(society.COMMUNITY, society.GROUP, society.COORDINATOR_ROLE);

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
        //super.live();
        //boolean shouldQuit = false;
        while(true)
        {
            StringMessage m = (StringMessage)nextMessage();

            if(m != null)
            {
                getLogger().info("Received message from " + m.getSender() + "\n Message : " + m.getContent());
                sendReply(m, new StringMessage("Message received !"));

            }
        }
    }

    @Override
    protected void end() {
        //super.end();
        pause(100000);
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
