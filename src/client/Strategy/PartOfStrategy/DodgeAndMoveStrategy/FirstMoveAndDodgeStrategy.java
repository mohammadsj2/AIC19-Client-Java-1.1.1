package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.CantFindRandomTargetZone;
import client.Exception.NotEnoughApException;
import client.MyMath;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class FirstMoveAndDodgeStrategy extends PartOfStrategy {
    public static final int NUMBER_OF_ACTION_PHASES = 6;
    private ArrayList<Cell> targetZoneCells = new ArrayList<>();

    protected FirstMoveAndDodgeStrategy(int maxAp) {
        super(maxAp);
    }

    @Override
    public void preProcess(World world) {
        getHeroTargetCellsZone(world);
    }

    ArrayList<Cell> getHeroTargetCellsZone(World world) {
        if (targetZoneCells.size() != 0)
            return targetZoneCells;

        int rangeOfBomb = world.getAbilityConstants(AbilityName.BLASTER_BOMB).getAreaOfEffect();
        for(int minimumDistance = rangeOfBomb*2+1; minimumDistance>=2; minimumDistance--) {
            for(int t=0;t<20;t++) {
                try {
                    targetZoneCells = getRandomHeroTargetZonesByMinimumDistance(world,minimumDistance);
                    return targetZoneCells;
                } catch (CantFindRandomTargetZone ignored) {

                }
            }
        }
        return targetZoneCells;
    }

    private ArrayList<Cell> getRandomHeroTargetZonesByMinimumDistance(World world,int minimumDistance) throws CantFindRandomTargetZone {
        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        ArrayList<Cell> choices = new ArrayList<>(Arrays.asList(objectiveZone));
        ArrayList<Cell> tmp=new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if(choices.isEmpty()){
                throw new CantFindRandomTargetZone();
            }
            int x = MyMath.getRandomIntegerLessThan(choices.size());
            Cell cell=choices.get(x);
            for (Cell cell1 : objectiveZone) {
                if (world.manhattanDistance(cell, cell1) < minimumDistance) {
                    choices.remove(cell1);
                }
            }
            tmp.add(cell);
        }
        return tmp;
    }

    @Override
    public void moveTurn(World world) throws NotEnoughApException {
        Hero myHeros[] = world.getMyHeroes();

        HashMap<Integer, Boolean> heroMoved = new HashMap<>();
        for (Hero hero : world.getMyHeroes()) {
            heroMoved.put(hero.getId(), false);
        }
        ArrayList<Cell> targetCells=getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            Hero hero = myHeros[i];
            Cell targetCell = targetCells.get(i);
            if (betterToWait(world, hero, targetCell)) {
                continue;
            }
            Direction dirs[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
            if (dirs.length != 0) {
                Cell nextCell = getNextCellByDirection(world, hero.getCurrentCell(), dirs[0]);
                if (world.getMyHero(nextCell) == null || heroMoved.get(world.getMyHero(nextCell).getId())) {
                    move(world,hero,dirs[0]);
                    heroMoved.put(hero.getId(), true);
                } else {
                    Hero mozahem = world.getMyHero(nextCell);
                    int index = 0;
                    for (; index < world.getMyHeroes().length; index++) {
                        if (world.getMyHeroes()[index].getId() == mozahem.getId()) {
                            break;
                        }
                    }
                    swapTargetCells(i, index);
                    dirs = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
                    if (dirs.length != 0) {
                        move(world,hero,dirs[0]);
                        heroMoved.put(hero.getId(), true);
                    }
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            Hero hero = myHeros[i];
            if (heroMoved.get(hero.getId())) {
                System.out.println(i);
                continue;
            }
            Cell targetCell = targetCells.get(i);
            if (betterToWait(world, hero, targetCell)) {
                continue;
            }
            Direction dirs[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
            if (dirs.length != 0) {
                move(world,hero,dirs[0]);
            }
        }
    }
    private void swapTargetCells(int i, int j) {
        Cell c = targetZoneCells.get(i);
        targetZoneCells.set(i, targetZoneCells.get(j));
        targetZoneCells.set(j, c);
    }



    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        try {
            return hero.getDodgeAbilities()[0].isReady() && dodgeAHero(world,hero,targetCell,false,true)> NUMBER_OF_ACTION_PHASES;
        } catch (NotEnoughApException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        super.actionTurn(world);
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            dodgeAHero(world, heroes[i], targetCells.get(i));
        }
        for (int i = 0; i < 4; i++)
            dodgeAHero(world, heroes[i], targetCells.get(i), true, true);
    }
    int dodgeAHero(World world, Hero hero, Cell targetCell, boolean action, boolean force) throws NotEnoughApException {
        if(hero.getCurrentCell().equals(targetCell)){
            return 0;
        }
        Direction dir[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
        AbilityName dodgeAbility = hero.getDodgeAbilities()[0].getName();
        int range = world.getAbilityConstants(dodgeAbility).getRange();
        if (dir.length >= range || force) {
            ArrayList<Pair<Cell,Integer>> toSortPairs=new ArrayList<>();
            for (Cell dodgeCell : getARangeOfCellsThatIsNotWall(world, hero.getCurrentCell(), world.getAbilityConstants(dodgeAbility).getRange())) {
                int length = world.getPathMoveDirections(dodgeCell, targetCell).length;
                toSortPairs.add(new Pair<>(dodgeCell,length));
            }
            toSortPairs.sort(Comparator.comparingInt(Pair::getSecond));
            if (action) {
                for(int i=0;i<Math.min(8,toSortPairs.size());i++){
                    dodge(world,hero,toSortPairs.get(i).getFirst());
                }
            }
            return dir.length - world.getPathMoveDirections(toSortPairs.get(0).getFirst(), targetCell).length;
        }
        return 0;
    }

    int dodgeAHero(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        return dodgeAHero(world, hero, targetCell, true, false);
    }
}
