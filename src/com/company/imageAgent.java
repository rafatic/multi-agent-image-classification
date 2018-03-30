package com.company;

import madkit.kernel.Agent;
import madkit.kernel.Message;
import madkit.message.IntegerMessage;
import madkit.message.ObjectMessage;
import madkit.message.StringMessage;

import java.awt.*;

public class imageAgent extends Agent {

    public static final String COMMUNITY = "execution";
    public static final String ROLE = "image agent";


    private Point originalPosition;
    private Point currentPosition;
    private int ID;
    private int groupId;

    private String group;
    private int RGBColor;

    public imageAgent(int RGB, Point position, int ID, int groupId)
    {
        this.RGBColor = RGB;
        this.currentPosition = (Point)position.clone();
        this.originalPosition = (Point)this.currentPosition.clone();
        this.ID = ID;
        this.groupId = groupId;
    }

    public Point getOriginalPosition()
    {
        return this.originalPosition;
    }

    public Point getCurrentPosition()
    {
        return this.currentPosition;
    }

    public int getID()
    {
        return this.ID;
    }

    @Override
    protected void activate() {
        getLogger().info("Image agent activated on point " + originalPosition.x + ", " + currentPosition.y);
        getLogger().info("Looking for the color " + RGBColor);

        createGroup(society.COMMUNITY, society.GROUP);

        requestRole(society.COMMUNITY, society.GROUP, "worker_" + society.worker_roles.get(groupId));

        getLogger().info("Role : worker_" + society.worker_roles.get(groupId));


    }

    @Override
    protected void live() {
        getLogger().info("Belief : finding the color " + RGBColor + "\nSending my belief to the coordinator");

        sendAcquisitionRequest(new Point(300,300));
        pause(100000);

    }



    @Override
    protected void end() {
        getLogger().info("Desactivation");
        sendEndOfActivityNotification();
    }



    private boolean sendAcquisitionRequest(Point targetPosition)
    {
        ReturnCode code = null;

        while(code != ReturnCode.SUCCESS)
        {
            code = sendMessage(society.COMMUNITY,
                    society.GROUP,
                    society.COORDINATOR_ROLE,
                    new ObjectMessage<acquisitionMessage>(new acquisitionMessage(targetPosition, this.ID, this.originalPosition))
            );
        }

        IntegerMessage responseMessage = (IntegerMessage)waitNextMessage();

        if(responseMessage.getContent() == society.ACQUISITION_GRANTED)
        {
            getLogger().info("ACQUISITION GRANTED : Moving to next spot");
            return true;
        }
        else if(responseMessage.getContent() == society.ACQUISITION_DENIED)
        {
            getLogger().info("ACQUISITION DENIED : searching for another pixel");
            return false;
        }
        else
        {
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

}
