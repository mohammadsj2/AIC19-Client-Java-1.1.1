package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.Tools.BFS;
import client.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SecondMoveAndDodgeStrategy extends FirstMoveAndDodgeStrategy {
    public static final int NUMBER_OF_MOVE_PHASES = 6;
    BFS bfs;

    public SecondMoveAndDodgeStrategy(int maxAp, BFS bfs) {
        super(maxAp);
        this.bfs = bfs;
    }

    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {

        ArrayList<Pair<Cell, Boolean>> moves = whatToDo(world, hero, targetCell);
        Pair<Cell,Boolean> move=moves.get(0);
        System.err.println("Turn is : " + world.getCurrentTurn() + "\n" + hero.getId() + "\n" +
                move.getSecond());

        return move.getSecond();
    }

    private ArrayList<Pair<Cell, Boolean>> whatToDo(World world, Hero hero, Cell targetCell) {
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
                int temp = cooldown - 1;
                toSort.add(new Pair<>(new Pair<>(distance[r][c][temp], true), new Pair<>(cell, temp)));
            }
            int normalDistance = bfs.getNormalDistance(hero.getCurrentCell(), cell);
            if (normalDistance <= NUMBER_OF_MOVE_PHASES) {
                int temp = Math.max(0, remainCoolDown - 1);
                toSort.add(new Pair<>(new Pair<>(distance[r][c][temp], false), new Pair<>(cell, temp)));
            }
        }
        toSort.sort((o1, o2) -> {
            Integer first = o1.getFirst().getFirst();
            Integer second = o2.getFirst().getFirst();
            if (!first.equals(second))
                return first - second;
            Boolean second1 = o1.getFirst().getSecond();
            Boolean second2 = o2.getFirst().getSecond();
            if (!second1.equals(second2)) {
                if (second1.equals(false)) {
                    return -1;
                }
                return 1;
            }
            return 0;
        });

        ArrayList<Pair<Cell, Boolean>> ans = new ArrayList<>();
        for (Pair<Pair<Integer, Boolean>, Pair<Cell, Integer>> p : toSort) {
            ans.add(new Pair<>(p.getSecond().getFirst(), p.getFirst().getSecond()));
        }
        return ans;
    }

    @Override
    int dodgeAHero(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        ArrayList<Pair<Cell, Boolean>> moves = whatToDo(world, hero, targetCell);
        if (!moves.get(0).getSecond()) return 0;
        System.out.println("BetterToWait:" + moves.get(0).getFirst());
        boolean decreaseMoney = true;
        for (Pair<Cell, Boolean> move : moves) {
            dodge(world, hero, move.getFirst(), decreaseMoney);
            decreaseMoney = false;
        }
        return 0;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
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
