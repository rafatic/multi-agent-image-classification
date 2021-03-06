package com.company;

import madkit.kernel.Agent;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;

import java.awt.*;
import java.util.ArrayList;

public class imageAgent extends Agent {

    public static final String COMMUNITY = "execution";
    public static final String ROLE = "image agent";

    // Pixel under germ location
    private Pixel originalPixel;
    // pixel under the agent's current position
    private Pixel currentPixel;
    // germ position
    private Point originalPosition;
    // Agent's current location in the image
    private Point currentPosition;
    private int ID;
    private int groupId;
    // Copy og the image to segment
    private int[][] image;

    private int greyScale;




    private double _avg = 0; // Average greyScale value of each pixels acquired
    private double n = 0; // Number of taken pixels
	// List of neighbouring pixels.
    private ArrayList<Pixel> _toVisit = new ArrayList<Pixel>();

    public imageAgent(Point position, int ID, int groupId, int[][] greyScaleImage)
    {
        this.currentPosition = (Point)position.clone();
        this.originalPosition = (Point)this.currentPosition.clone();
        this.ID = ID;
        this.groupId = groupId;
        this.image = greyScaleImage;



        this.greyScale = image[position.x][position.y];
        this.originalPixel = new Pixel(position.x, position.y, greyScale);
        this.currentPixel = new Pixel(position.x, position.y, greyScale);
    }


    public Point getOriginalPosition()
    {
        return this.originalPosition;
    }
    public Point getCurrentPosition()
    {
        return this.currentPosition;
    }

    public Pixel getCurrentPixel() {
        return currentPixel;
    }

    public Pixel getOriginalPixel() {
        return originalPixel;
    }

    public int getID()
    {
        return this.ID;
    }




	// When the agent is created, it sets its roles and group.
	// its role and group will help it identify itself when communicating with the coordinator
    @Override
    protected void activate() {
        getLogger().info("Image agent activated on point " + originalPosition.x + ", " + currentPosition.y);
        getLogger().info("Looking for the color " + greyScale);

        createGroup(society.COMMUNITY, society.GROUP);

        requestRole(society.COMMUNITY, society.GROUP, society.worker_roles.get(groupId));

        getLogger().info("Role : " + society.worker_roles.get(groupId));


    }

    @Override
    protected void live() {
        getLogger().info("Belief : finding the color " + greyScale + "\nSending my belief to the coordinator");
        // Updates its belief
        updateStats(currentPixel.color);
        // Find the first pixel to explore
        visitPixel(currentPixel, Math.abs( _avg - currentPixel.color));

        // While there is at least on pixel to explore
        while (continueExploration()) {

            Pixel p = nextPixel(); // The agent says "Hey, I want to take this pixel"
            visitPixel(p, Math.abs( _avg - currentPixel.color)); // The coordinator says "Yes you can take it"
        }


    }



    @Override
    protected void end() {
        getLogger().info("Desactivation");
        sendEndOfActivityNotification();
    }


	// Sends an acquisitionMessage for a given pixel
    private boolean sendAcquisitionRequest(Point targetPosition, double similarityScore)
    {
        ReturnCode code = null;

        while(code != ReturnCode.SUCCESS)
        {
            code = sendMessage(society.COMMUNITY,
                    society.GROUP,
                    society.COORDINATOR_ROLE,
                    new ObjectMessage<acquisitionMessage>(new acquisitionMessage(targetPosition, this.ID, this.originalPosition, similarityScore))
            );
        }

        IntegerMessage responseMessage = (IntegerMessage)waitNextMessage();

        if(responseMessage.getContent() == society.ACQUISITION_GRANTED)
        {
            // Acquisition granted
            return true;
        }
        else if(responseMessage.getContent() == society.ACQUISITION_DENIED)
        {
            // Acquisition denied
            return false;
        }
        else
        {
        	// Unknown response
            getLogger().info("ERROR unknown responseMessage : " + responseMessage.getContent());
            return false;
        }
    }


    private void sendEndOfActivityNotification()
    {
        ReturnCode code = null;

        while(code != ReturnCode.SUCCESS)
        {
            code = sendMessage(society.COMMUNITY,
                                society.GROUP,
                                society.COORDINATOR_ROLE,
                                new ObjectMessage<State>(State.ENDING));
        }
    }

    public void visitPixel(Pixel p, double similarityScore) {

        if(sendAcquisitionRequest(new Point(p.x, p.y), similarityScore))
        {
            addNeighbours(p);
            image[p.x][p.y] = -1;
            updateStats(p.color);
        }
    }

    private void updateStats(int color) {
        n ++;

		_avg = (_avg * (n - 1) + color) / n;
    }

    private boolean isOutlier(int color) {


        double Bmin = _avg - 30;
        double Bmax = _avg + 30;

        return color < Bmin || color > Bmax;
    }

    public Pixel nextPixel () {
        double minDeviation = 256;
        Pixel minPixel = _toVisit.get(0);
        int minIndex = 0;
        for(int i=0; i<_toVisit.size(); i++) {
            if ((Math.abs(_toVisit.get(i).color - _avg) < minDeviation) && image[_toVisit.get(i).x][_toVisit.get(i).y] != -1) {
                minDeviation = Math.abs(_toVisit.get(i).color - _avg);
                minPixel = _toVisit.get(i);
                minIndex = i;
            }
        }
        _toVisit.remove(minIndex);
        return minPixel;
    }



    private void addNeighbours(Pixel p) {
        if ((p.x > 0) && image[p.x - 1][p.y] != -1 && !isOutlier(image[p.x - 1][p.y])) {
            _toVisit.add(new Pixel(p.x - 1, p.y, image[p.x - 1][p.y]));
        }

        if ((p.y > 0) && image[p.x][p.y - 1] != -1 && !isOutlier(image[p.x][p.y - 1])) {
            _toVisit.add(new Pixel(p.x, p.y - 1, image[p.x][p.y - 1]));
        }

        if ((p.y < image[0].length-1) && image[p.x][p.y + 1] != -1 && !isOutlier(image[p.x][p.y + 1])) {
            _toVisit.add(new Pixel(p.x, p.y + 1,image[p.x][p.y + 1]));
        }

        if ((p.x < image.length-1) && image[p.x + 1][p.y] != -1 && !isOutlier(image[p.x + 1][p.y])) {
            _toVisit.add(new Pixel(p.x + 1, p.y,image[p.x + 1][p.y]));
        }
    }

    public boolean continueExploration(){
        return _toVisit.size() != 0;
    }


    public void printMyPixels() {
        for (int i = 0; i < image[0].length; i++){
            for(int j = 0; j < image.length; j++) {
                if (image[i][j] == -1) {
                    System.out.println("("+i+","+j+")");
                }
            }
        }


        for(int j = 0; j < image[0].length; j++)
        {
            System.out.print("|");
            for(int i = 0; i < image.length; i++)
            {
                if(image[i][j] == -1)
                {
                    System.out.print("X");
                }
                else
                {
                    System.out.print(" ");
                }

            }
            System.out.print("|\n");
        }

        for(int i = 0; i < image.length; i++)
        {
            System.out.print("_");
        }
        System.out.print("\n");
    }

}
