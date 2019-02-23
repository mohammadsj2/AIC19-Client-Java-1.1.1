package client.Strategy;

import client.model.World;

public abstract class Strategy {
    public abstract void preProcess(World world);
    public abstract void pickTurn(World world);
    public abstract void moveTurn(World world);
    public abstract void actionTurn(World world);
}
