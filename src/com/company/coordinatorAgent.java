package com.company;

import madkit.kernel.*;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;
import madkit.message.StringMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



public class coordinatorAgent extends Agent {

    private ArrayList<AbstractAgent> workers;
    private static ArrayList<Point> startingPoints;
    private static BufferedImage originalImage;
    private static String[][] segmentationMap;
    private static int[][] travelMap;

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
        segmentationMap = new String[originalImage.getWidth()][originalImage.getHeight()];
        travelMap = new int[originalImage.getWidth()][originalImage.getHeight()];
        for(int y = 0; y < originalImage.getHeight(); y++)
        {
            for(int x = 0; x < originalImage.getWidth(); x++)
            {
                segmentationMap[x][y] =  "";
                travelMap[x][y] = -1;

            }
        }

        int agentId = 0;
        for(Point p: startingPoints)
        {
            imageAgent a = new imageAgent(originalImage.getRGB(p.x, p.y), p, agentId);
            if(launchAgent(a, true) == ReturnCode.SUCCESS)
            {
                this.workers.add(a);

                getLogger().info("Created agent on point " + p.x + ", " + p.y);
                segmentationMap[p.x][p.y] = "worker_" + originalImage.getRGB(p.x, p.y);
                travelMap[p.x][p.y] = a.getID();
            }
            agentId++;
        }

    }

    @Override
    protected void live() {
        //super.live();
        //boolean shouldQuit = false;
        while(true)
        {
            /*StringMessage m = (StringMessage)nextMessage();

            if(m != null)
            {
                getLogger().info("Received message from " + m.getSender() + "\n Message : " + m.getContent());
                sendReply(m, new StringMessage("Message received !"));

            }*/

            Message m = waitNextMessage();

            if(m instanceof ObjectMessage)
            {

                examineAcquisitionRequest((ObjectMessage<acquisitionMessage>)m);
            }
        }
    }

    private void examineAcquisitionRequest(ObjectMessage<acquisitionMessage> message)
    {

        //Point requestedPosition = message.getContent();
        acquisitionMessage am = message.getContent();
        //getLogger().info("Received acquisition request on " + requestedPosition.toString() + " by " + message.getSender().getSimpleAgentNetworkID() + " ( role : " + message.getSender().getRole() + ")");
        if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(""))
        {
            getLogger().info("Requested position is available, sending confirmation");
            segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
            travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
            sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
        }
        else
        {
            int occupantDistance = getDistanceFromStartingPoint((imageAgent)workers.get(travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y]));
            int askerDistance = getDistanceFromStartingPoint((imageAgent)workers.get(am.getAgentID()));
            // Cooperation
            if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(message.getSender().getRole()))
            {
                getLogger().info("Requested position is occupied by an agent of the same group (cooperation)");


                if(occupantDistance <= askerDistance)
                {
                    getLogger().info("Asking agent is closer to the requested point, acquisition granted");

                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    getLogger().info("Occupant agent is closer to the requested point, acquisition denied");
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }
            else
            {
                getLogger().info("Requested position is occupied by an agent of another group (competition)");
                if(occupantDistance <= askerDistance)
                {
                    getLogger().info("Asking agent is closer to the requested point, acquisition granted");

                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    getLogger().info("Occupant agent is closer to the requested point, acquisition denied");
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }

        }

    }



    private int getDistanceFromStartingPoint(imageAgent a)
    {
        return (Math.abs(a.getCurrentPosition().x - a.getOriginalPosition().x) + (Math.abs(a.getCurrentPosition().y - a.getOriginalPosition().y)));
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
