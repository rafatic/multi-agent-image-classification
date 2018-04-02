package com.company;

import java.awt.*;

public class acquisitionMessage {
    private Point requestedPoint;
    private int agentID;
    private Point agentOriginalPosition;
    private double similarityScore;

    public acquisitionMessage(Point requestedPoint, int agentID, Point agentOriginalPosition, double similarityScore)
    {
        this.requestedPoint = requestedPoint;
        this.agentID = agentID;
        this.agentOriginalPosition = agentOriginalPosition;
        this.similarityScore = similarityScore;
    }

    public Point getRequestedPoint()
    {
        return this.requestedPoint;
    }

    public int getAgentID()
    {
        return this.agentID;
    }

    public Point getAgentOriginalPosition()
    {
        return this.agentOriginalPosition;
    }

    public double getSimilarityScore()
    {
        return this.similarityScore;
    }
}
