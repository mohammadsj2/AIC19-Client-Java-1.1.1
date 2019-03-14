package client.Strategy.PartOfStrategy;

import client.Exception.NotEnoughApException;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;
import java.util.Arrays;

public class PositionPredictor extends PartOfStrategy {
    Hero oppHero;
    ArrayList<Cell> seenBefore = new ArrayList<>();
    int numberOfTurnThatWeDontSeeHim = 0;

    protected PositionPredictor(Hero oppHero) {
        this.oppHero = oppHero;
    }

    private void update(World world) {
        updateHero(world);
        if (oppHero.getCurrentCell() == null) {
            numberOfTurnThatWeDontSeeHim++;
        } else {
            numberOfTurnThatWeDontSeeHim = 0;
            seenBefore.add(oppHero.getCurrentCell());
        }
    }

    private void updateHero(World world) {
        for (Hero hero : world.getOppHeroes()) {
            if (oppHero.getId() == hero.getId()) {
                oppHero = hero;
                break;
            }
        }
    }

    public Cell getPredictCell(World world) {
        updateHero(world);
        if (oppHero.getCurrentCell() != null) {
            return oppHero.getCurrentCell();
        }
        return seenBefore.get(seenBefore.size() - 1);
    }

    @Override
    public void moveTurn(World world) {
        updateHero(world);
    }

    @Override
    public void actionTurn(World world) {
        updateHero(world);
    }

    @Override
    public void preProcess(World world) {
        dead(world);
        updateHero(world);
    }

    private void dead(World world) {
        seenBefore.addAll(Arrays.asList(world.getMap().getOppRespawnZone()));
    }

}
