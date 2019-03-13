package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.Tools.BFS;
import client.model.*;

import java.util.ArrayList;

public class SecondMoveAndDodgeStrategy extends FirstMoveAndDodgeStrategy {
    public static final int NUMBER_OF_MOVE_PHASES = 6;
    BFS bfs;

    public SecondMoveAndDodgeStrategy(int maxAp, BFS bfs) {
        super(maxAp);
        this.bfs = bfs;
    }

    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        return super.betterToWait(world, hero, targetCell);
    }

    private Pair<Cell, Boolean> whatToDo(World world, Hero hero, Cell targetCell) {
        Ability dodgeAbility = hero.getDodgeAbilities()[0];
        int[][][] distance = bfs.getDistance(targetCell, dodgeAbility);
        int range = Math.max(dodgeAbility.getRange(), NUMBER_OF_MOVE_PHASES);
        ArrayList<Cell> cells = getARangeOfCellsThatIsNotWall(world, hero.getCurrentCell(), range);
        ArrayList<Pair<Pair<Integer, Boolean>, Pair<Cell, Integer>>> toSort = new ArrayList<>();
        int remainCoolDown = hero.getAbility(dodgeAbility.getName()).getRemCooldown();
        for (Cell cell : cells) {
            int cooldown = dodgeAbility.getCooldown();
            int r = cell.getRow(), c = cell.getColumn();
            int manhattanDistance = world.manhattanDistance(hero.getCurrentCell(), cell);
            if (remainCoolDown == 0 && manhattanDistance <= dodgeAbility.getRange()) {
                toSort.add(new Pair<>(new Pair<>(distance[r][c][cooldown - 1], true), new Pair<>(cell, cooldown)));
            }
            if (manhattanDistance <= NUMBER_OF_MOVE_PHASES){

            }


        }
        return null;
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

    @Override
    public void moveTurn(World world) throws NotEnoughApException {
        super.moveTurn(world);
    }
}
