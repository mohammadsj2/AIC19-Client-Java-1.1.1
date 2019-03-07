package client;


import client.Strategy.*;
import client.model.*;
import com.google.gson.Gson;
import common.network.Json;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Formatter;

public class AI {
    public static Strategy strategy=new BBBBBetterStrategy();

    public void preProcess(World world) {
        //System.out.println("pre process started");
        strategy.preProcess(world);
    }

    public void pickTurn(World world) {
        //System.out.println("pick started");
        strategy.pickTurn(world);
    }

    public void moveTurn(World world) {
        //System.out.println("move started");
        strategy.moveTurn(world);
    }

    public void actionTurn(World world) {
        //System.out.println("actionTurn started");
        strategy.actionTurn(world);
    }
}
