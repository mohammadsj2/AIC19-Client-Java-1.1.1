package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.NotEnoughApException;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;

public class SecondMoveAndDodgeStrategy extends FirstMoveAndDodgeStrategy{
    public SecondMoveAndDodgeStrategy(int maxAp) {
        super(maxAp);
    }

    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        return super.betterToWait(world, hero, targetCell);
    }

    @Override
    int dodgeAHero(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        return super.dodgeAHero(world, hero, targetCell);
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        super.actionTurn(world);
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            dodgeAHero(world, heroes[i], targetCells.get(i));
        }
    }
}
