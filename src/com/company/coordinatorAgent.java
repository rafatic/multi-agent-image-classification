package com.company;

import madkit.kernel.*;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



public class coordinatorAgent extends Agent {

    private ArrayList<AbstractAgent> workers;
    private int nbWorkersAlive;
    private static ArrayList<germ> germs;
    private static BufferedImage originalImage;
    private int[][] greyScaleImage;
    private static String[][] segmentationMap;
    private int[][] travelMap;
    private double[][] similarityMap;

    private long startTime;
    long endTime;



    @Override
    protected void activate() {
        startTime = System.currentTimeMillis();



        getLogger().info("Activating coordinator agent and workers");

        createGroup(society.COMMUNITY, society.GROUP);
        requestRole(society.COMMUNITY, society.GROUP, society.COORDINATOR_ROLE);
        nbWorkersAlive = 0;
        workers = new ArrayList<>();
        segmentationMap = new String[originalImage.getWidth()][originalImage.getHeight()];

        travelMap = new int[originalImage.getWidth()][originalImage.getHeight()];
        greyScaleImage = new int[originalImage.getWidth()][originalImage.getHeight()];
        similarityMap = new double[originalImage.getWidth()][originalImage.getHeight()];
        for(int y = 0; y < originalImage.getHeight(); y++)
        {
            for(int x = 0; x < originalImage.getWidth(); x++)
            {
                segmentationMap[x][y] =  "";
                travelMap[x][y] = -1;
                greyScaleImage[x][y] = getGrayScaleFromRGB(originalImage.getRGB(x, y));
                similarityMap[x][y] = Double.MAX_VALUE;

            }
        }
        int agentId = 0;
        for(germ g: germs)
        {

            imageAgent a = new imageAgent(g.getLocation(), agentId, g.getGroupId(), greyScaleImage);
            if(launchAgent(a, false) == ReturnCode.SUCCESS)
            {
                this.workers.add(a);

                getLogger().info("Created agent on point " + g.getLocation().x + ", " + g.getLocation().y);
                segmentationMap[g.getLocation().x][g.getLocation().y] = society.worker_roles.get(g.getGroupId());
                travelMap[g.getLocation().x][g.getLocation().y] = a.getID();
            }
            agentId++;
            nbWorkersAlive++;

        }

    }

    @Override
    protected void live() {
        while(nbWorkersAlive > 0)
        {
            Message m = waitNextMessage();

            if(m instanceof ObjectMessage)
            {
                if(((ObjectMessage) m).getContent() instanceof acquisitionMessage)
                {
                    examineAcquisitionRequest((ObjectMessage<acquisitionMessage>)m);
                }
                else if(((ObjectMessage) m).getContent() instanceof Agent.State)
                {
                    System.out.println("One worker has finished its work");
                    processStateChange((ObjectMessage<State>)m);

                }

            }
        }
    }

    private void processStateChange(ObjectMessage<State> message)
    {
        State s = message.getContent();

        if(s == State.ENDING)
        {
            nbWorkersAlive--;
        }

        System.out.println(nbWorkersAlive + " workers left");
    }

    private void examineAcquisitionRequest(ObjectMessage<acquisitionMessage> message)
    {

        //Point requestedPosition = message.getContent();
        acquisitionMessage am = message.getContent();
        //getLogger().info("Received acquisition request on " + requestedPosition.toString() + " by " + message.getSender().getSimpleAgentNetworkID() + " ( role : " + message.getSender().getRole() + ")");
        if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(""))
        {
            //getLogger().info("Requested position is available, sending confirmation");
            segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
            travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
            similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
            sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
        }
        else
        {
            int askerDistance = getDistance(am.getRequestedPoint(), am.getAgentOriginalPosition());
            int occupantDistance = getDistance(am.getRequestedPoint(), ((imageAgent)workers.get(travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y])).getOriginalPosition());
            // Cooperation
            if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(message.getSender().getRole()))
            {
                //getLogger().info("Requested position is occupied by an agent of the same group (cooperation)");


                if(occupantDistance <= askerDistance)
                {
                    //getLogger().info("Asking agent is closer to the requested point, acquisition granted");

                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    //getLogger().info("Occupant agent is closer to the requested point, acquisition denied");
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }
            // Competition
            else
            {
                //getLogger().info("Requested position is occupied by an agent of another group (competition)");
                if(occupantDistance <= askerDistance && am.getSimilarityScore() < similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y])
                {
                    //getLogger().info("Asking agent is closer to the requested point, acquisition granted");

                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    //getLogger().info("Occupant agent is closer to the requested point, acquisition denied");
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }

        }

    }



    private int getDistance(Point a, Point b)
    {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
    }

    private int getDistanceFromStartingPoint(imageAgent a)
    {
        return (Math.abs(a.getCurrentPosition().x - a.getOriginalPosition().x) + (Math.abs(a.getCurrentPosition().y - a.getOriginalPosition().y)));
    }

    private int getGrayScaleFromRGB(int rgb)
    {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);

        return (r + g + b) / 3;
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    @Override
    protected void end() {
        endTime = System.currentTimeMillis();
        System.out.println("Execution time : " + ((endTime - startTime) / 1000) + " seconds");
        segmentationResultDialog resultDialog = new segmentationResultDialog();
        resultDialog.showDialog(segmentationMap);
    }

    public static void main(ArrayList<germ> createdGerms, BufferedImage image)
    {
        germs = createdGerms;
        originalImage = image;

        executeThisAgent();

    }
}
