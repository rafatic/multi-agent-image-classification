package com.company;

import java.awt.*;

public class acquisitionMessage {
    private Point requestedPoint;
    private int agentID;
    private Point agentOriginalPosition;

    public acquisitionMessage(Point requestedPoint, int agentID, Point agentOriginalPosition)
    {
        this.requestedPoint = requestedPoint;
        this.agentID = agentID;
        this.agentOriginalPosition = agentOriginalPosition;
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
}
