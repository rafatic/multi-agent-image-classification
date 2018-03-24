package com.company;

import madkit.kernel.Agent;
import madkit.kernel.Message;
import madkit.message.StringMessage;

import java.awt.*;

public class imageAgent extends Agent {

    public static final String COMMUNITY = "execution";
    public static final String ROLE = "image agent";


    private Point originalPosition;
    private Point currentPosition;

    private String group;
    private int RGBColor;

    public imageAgent(int RGB, Point position)
    {
        this.RGBColor = RGB;
        this.currentPosition = (Point)position.clone();
        this.originalPosition = (Point)this.currentPosition.clone();



    }

    @Override
    protected void activate() {
        getLogger().info("Image agent activated on point " + originalPosition.x + ", " + currentPosition.y);
        getLogger().info("Looking for the color " + RGBColor);

        createGroup(society.COMMUNITY, society.GROUP);

        if(!society.worker_roles.contains("worker_" + RGBColor))
        {
            society.worker_roles.add("worker_" + RGBColor);
        }


        requestRole(society.COMMUNITY, society.GROUP, "worker_" + RGBColor);


    }

    @Override
    protected void live() {
        getLogger().info("Belief : finding the color " + RGBColor + "\nSending my belief to the coordinator");
        ReturnCode code = null;
        while(code != ReturnCode.SUCCESS)
        {
            code = sendMessage(society.COMMUNITY,
                                society.GROUP,
                                society.COORDINATOR_ROLE,
                                new StringMessage("Hello from group worker_" + RGBColor + "I believe in finding the color " + RGBColor)
            );
            getLogger().info(code.toString());
        }
        getLogger().info("Message sent, wainting for response");
        StringMessage response = (StringMessage)waitNextMessage();
        getLogger().info("Response received : " + response.getContent());
        pause(100000);

    }

    @Override
    protected void end() {
        getLogger().info("Desactivation");
    }

    public void start()
    {
        executeThisAgent();
    }

    public static void main(int nbAgents)
    {
        executeThisAgent(nbAgents, true);
    }


}
