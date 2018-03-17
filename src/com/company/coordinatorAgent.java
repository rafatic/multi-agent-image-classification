package com.company;

import sun.management.Agent;

import java.util.ArrayList;

public class coordinatorAgent extends Agent {

    private ArrayList<imageAgent> workers;

    public coordinatorAgent(ArrayList<imageAgent> workers)
    {
        this.workers = workers;
    }
}
