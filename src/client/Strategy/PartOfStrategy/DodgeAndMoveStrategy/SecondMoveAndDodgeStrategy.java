package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.CantFindRandomTargetZone;
import client.Exception.NotEnoughApException;
import client.Strategy.Tools.BFS;
import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SecondMoveAndDodgeStrategy extends FirstMoveAndDodgeStrategy {
    private static final int NUMBER_OF_MOVE_PHASES = 6;
    private int movePhase = 0;
    private ArrayList<Pair<Cell, Boolean>>[] whatToDoArrayList = new ArrayList[8];
    private BFS bfs;

    public SecondMoveAndDodgeStrategy(int maxAp, BFS bfs) {
        super(maxAp);
        this.bfs = bfs;
    }



    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        if (hero.getCurrentCell().getRow() == -1) return true;
        ArrayList<Pair<Cell, Boolean>> moves = whatToDoArrayList[hero.getId()];
        Pair<Cell, Boolean> move = moves.get(0);



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
        int initialTime = toSort.get(0).getFirst().getFirst();
        for (Pair<Pair<Integer, Boolean>, Pair<Cell, Integer>> p : toSort) {
            int time = p.getFirst().getFirst();
            double rate = (double) (time - initialTime) / (double) (BFS.ONE_TURN_IN_BFS);
            if (rate > 1.0) {
                break;
            }
            ans.add(new Pair<>(p.getSecond().getFirst(), p.getFirst().getSecond()));
        }
        if (world.getCurrentTurn() == 5) {
            int e = 213;
            System.out.println(e);
        }
        return ans;
    }

    @Override
    void dodgeAHero(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        ArrayList<Pair<Cell, Boolean>> moves = whatToDoArrayList[hero.getId()];
        if (!moves.get(0).getSecond()) return;
        System.out.println("BetterToWait:" + moves.get(0).getFirst());
        boolean decreaseMoney = true;
        //TODO dg nabayad ta 8 bashe ha !!
        for (Pair<Cell, Boolean> move : moves) {
            if (move.getSecond()) {
                int manhattanDistance = world.manhattanDistance(move.getFirst(), hero.getCurrentCell());
                if (manhattanDistance <= hero.getDodgeAbilities()[0].getRange())
                    dodge(world, hero, move.getFirst(), decreaseMoney);
                decreaseMoney = false;
            }
        }
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            if (heroes[i].getCurrentCell().getRow() != -1)
                dodgeAHero(world, heroes[i], targetCells.get(i));
        }
    }

    @Override
    public void moveTurn(World world) throws NotEnoughApException {
        if (movePhase == 0) {
            refreshWhatToDoArrayLists(world);
            if (world.getCurrentTurn() == 5) {
                int e = 213;
                System.out.println(e);
            }
        }
        Hero[] myHeroes = world.getMyHeroes();

        HashMap<Integer, Boolean> heroMoved = new HashMap<>();
        for (Hero hero : myHeroes) {
            heroMoved.put(hero.getId(), false);
        }

        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            Hero hero = myHeroes[i];
            Cell targetCell = targetCells.get(i);
            Cell targetCell2 = whatToDoArrayList[hero.getId()].get(0).getFirst();
            if (betterToWait(world, hero, targetCell)) {
                continue;
            }
            Direction dirs[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell2);
            if (dirs.length != 0) {
                Cell nextCell = getNextCellByDirection(world, hero.getCurrentCell(), dirs[0]);
                if (world.getMyHero(nextCell) == null || heroMoved.get(world.getMyHero(nextCell).getId())) {
                    move(world, hero, dirs[0]);
                    heroMoved.put(hero.getId(), true);
                } else {
                    Hero mozahem = world.getMyHero(nextCell);
                    int index = 0;
                    for (; index < myHeroes.length; index++) {
                        if (myHeroes[index].getId() == mozahem.getId()) {
                            break;
                        }
                    }
                    swapTargetCells(i, index);
                    swapWhatToDoArrayLists(myHeroes[index].getId(), hero.getId());
                    if (betterToWait(world, hero, targetCell)) {
                        continue;
                    }
                    targetCell2 = whatToDoArrayList[hero.getId()].get(0).getFirst();
                    dirs = world.getPathMoveDirections(hero.getCurrentCell(), targetCell2);
                    if (dirs.length != 0) {
                        move(world, hero, dirs[0]);
                        heroMoved.put(hero.getId(), true);
                    }
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            Hero hero = myHeroes[i];
            if (heroMoved.get(hero.getId())) {
                continue;
            }
            Cell targetCell = targetCells.get(i);
            Cell targetCell2 = whatToDoArrayList[hero.getId()].get(0).getFirst();

            if (betterToWait(world, hero, targetCell)) {
                continue;
            }
            Direction dirs[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell2);
            if (dirs.length != 0) {
                move(world, hero, dirs[0]);
            }
        }
        movePhase++;
        movePhase %= 6;
    }

    private void refreshWhatToDoArrayLists(World world) {
        for (int i = 0; i < 4; i++) {
            Hero myHero = world.getMyHeroes()[i];
            if (myHero.getCurrentCell().getRow() != -1) {
                whatToDoArrayList[myHero.getId()] = whatToDo(world,
                        myHero, targetZoneCells.get(i));
            }
        }
    }

    private void swapWhatToDoArrayLists(int i, int j) {
        ArrayList<Pair<Cell, Boolean>> c = whatToDoArrayList[i];
        whatToDoArrayList[i] = whatToDoArrayList[j];
        whatToDoArrayList[j] = c;
    }
}
