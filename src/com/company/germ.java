package com.company;

import java.awt.*;

// A germs defines the starting location of an agent.
// A germ also contains the group and ID of the future agent
public class germ {

    private Point location;

    private int groupId, ID;

    public germ(Point location, int groupId, int id) {
        this.location = location;
        this.groupId = groupId;
        this.ID = id;
    }

    public germ(Point location, int id)
    {
        this.location = location;
        this.ID = id;
    }

    public Point getLocation()
    {
        return this.location;
    }

    public int getID()
    {
        return this.ID;
    }

    public int getGroupId()
    {
        return this.groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

}
