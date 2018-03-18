package com.company;

import madkit.kernel.Agent;

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
    }

    @Override
    protected void live() {
        getLogger().info("Belief : finding the color " + RGBColor);
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
