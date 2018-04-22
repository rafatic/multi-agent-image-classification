package com.company;

import madkit.kernel.*;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/* The coordinator agent is a single agent managing the different workers. It keeps track of the ongoing segmentation
 * and decides whether a worker can acquire a pixel (see acquisitionMessage).
*/
public class coordinatorAgent extends Agent {

    // List of workers agents
    private ArrayList<AbstractAgent> workers;
    // Number of workers still working
    private int nbWorkersAlive;
    // List of germs where the workers will be created
    private static ArrayList<germ> germs;
    // Image to segment
    private static BufferedImage originalImage;
    // A copy of the original image in grey scales
    private int[][] greyScaleImage;
    // Map of the segmentation, keeps track of every pixel state (acquired or not, by whom)
    private static String[][] segmentationMap;
    // Keeps track of the workers location, a cell contains the ID of the agent who acquired the pixel
    // the cell contains "" if it is available
    private int[][] travelMap;
    // Used to store the acquisition score of each pixel (-1 if no agent has acquired the pixel)
    private double[][] similarityMap;




    // Called when the agent is created
    @Override
    protected void activate() {

        getLogger().info("Activating coordinator agent and workers");

        createGroup(society.COMMUNITY, society.GROUP);
        requestRole(society.COMMUNITY, society.GROUP, society.COORDINATOR_ROLE);
        nbWorkersAlive = 0;
        workers = new ArrayList<>();

        // Initiation of the differents maps
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

        // Launch worker agents
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
        // While there is at least on worker in the "active" state
        // In this loop, the coordinator waits for any message.
        // It can receive two types of message :
        //      - acquisitionMessage : a worker requests access of a pixel
        //      - State message : a worker notifies its end
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

    // When a worker notifies its end, update the number of workers alive
    private void processStateChange(ObjectMessage<State> message)
    {
        State s = message.getContent();

        if(s == State.ENDING)
        {
            nbWorkersAlive--;
        }

        System.out.println(nbWorkersAlive + " workers left");
    }

    // Decides whether an acquisition request must be accepted or not
    private void examineAcquisitionRequest(ObjectMessage<acquisitionMessage> message)
    {
        acquisitionMessage am = message.getContent();

        // If the requested pixel has not been segmented yet
        if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(""))
        {
            // The acquisition is accepted by default
            segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
            travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
            similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
            sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
        }
        else
        {
            int askerDistance = getDistance(am.getRequestedPoint(), am.getAgentOriginalPosition());
            int occupantDistance = getDistance(am.getRequestedPoint(), ((imageAgent)workers.get(travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y])).getOriginalPosition());

            // If the requested pixel has already been segmented by an agent of the same group : COOPERATION
            if(segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y].equals(message.getSender().getRole()))
            {
                // Compare the distances between each agent starting location and the position of the requested pixel
                // The agent closest to the pixel will win


                if(occupantDistance <= askerDistance)
                {
                    // Acquisition granted
                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    // Acquisition denied
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }

            // If the requested pixel has already been segmented by an agent of another group : COMPETITION
            else
            {
                // We compare the similarity score of each agent and the distance between the agents starting location and the requested pixel.
                // The agent with the highest score will win
                if(occupantDistance <= askerDistance && am.getSimilarityScore() < similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y])
                {
                    // Acquisition granted
                    segmentationMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = message.getSender().getRole();
                    travelMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getAgentID();
                    similarityMap[am.getRequestedPoint().x][am.getRequestedPoint().y] = am.getSimilarityScore();
                    sendReply(message, new IntegerMessage(society.ACQUISITION_GRANTED));
                }
                else
                {
                    // Acquisition denied
                    sendReply(message, new IntegerMessage(society.ACQUISITION_DENIED));
                }
            }
        }
    }



    private int getDistance(Point a, Point b)
    {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
    }

    // Get the gray scale value from an RGB color
    private int getGrayScaleFromRGB(int rgb)
    {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);

        return (r + g + b) / 3;
    }

    // When the coordinator ends, show the segmented result and save it in a file.
    @Override
    protected void end() {
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
