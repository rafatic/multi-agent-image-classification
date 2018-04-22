package com.company;

import java.awt.*;

/* An acquisition message is sent by a worker agent to the coordinator when the worker wants to acquire a pixel
 * The message contains the coordinates of the requested pixel, the ID of the sender, the original position of the sender (germ)
 * and a score representing the difference between the agent's belief and the requested pixel value.
*/
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
