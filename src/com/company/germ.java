package com.company;

import java.awt.*;

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
