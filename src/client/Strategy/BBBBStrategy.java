package client.Strategy;

import client.Exception.CantFindRandomTargetZone;
import client.MyMath;
import client.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class BBBBStrategy extends Strategy {
    private ArrayList<Integer> heroIds = new ArrayList<>();
    private ArrayList<Cell> targetZoneCells = new ArrayList<>();
    private Boolean heroIdsSetted=false;


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
    public void preProcess(World world) {
        getHeroTargetCellsZone(world);
    }

    private static int cnt = 0;

    @Override
    public void pickTurn(World world) {
        System.out.println(world.getCurrentTurn());
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.BLASTER);
                break;
            case 1:
                world.pickHero(HeroName.BLASTER);
                break;
            case 2:
                world.pickHero(HeroName.BLASTER);
                break;
            case 3:
                world.pickHero(HeroName.BLASTER);
                break;
        }
        cnt++;
    }

    private void setHeroIds(World world) {
        for (Hero hero : world.getMyHeroes()) {
            heroIds.add(hero.getId());
        }
        heroIdsSetted=true;
    }

    @Override
    public void moveTurn(World world) {
        if(!heroIdsSetted)setHeroIds(world);
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        Hero myHeros[] = world.getMyHeroes();

        HashMap<Integer, Boolean> heroMoved = new HashMap<>();
        for (Hero hero : world.getMyHeroes()) {
            heroMoved.put(hero.getId(), false);
        }
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
                    world.moveHero(hero, dirs[0]);
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
                        world.moveHero(hero, dirs[0]);
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
                world.moveHero(hero, dirs[0]);
            }
        }
    }

    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        return world.getCurrentTurn() <= 5 && hero.getDodgeAbilities()[0].isReady();
        // TODO: 3/1/2019 turn hashon az 4 shoroo mishe badan momkene avazesh konan :/ khodemoon bayad turn ro bezanim !!!!!
    }

    private void swapTargetCells(int i, int j) {
        Cell c = targetZoneCells.get(i);
        targetZoneCells.set(i, targetZoneCells.get(j));
        targetZoneCells.set(j, c);
    }

    @Override
    public void actionTurn(World world) {
        blastersBombAttacks(world);
        blasterAttacks(world);
        dodge(world);
    }

    void blastersBombAttacks(World world) {
        for (Hero hero : world.getMyHeroes()) {
            blastersBombAttack(world, hero);
        }
    }

    void blasterAttacks(World world) {
        ArrayList<Pair<Cell, Integer>> cells = new ArrayList<>();
        ArrayList<Cell> importantCells = new ArrayList<>();
        for (Hero hero : world.getMyHeroes()) {
            importantCells.addAll(getARangeOfCellsThatIsNotWall(world, hero.getCurrentCell()
                    , world.getAbilityConstants(AbilityName.BLASTER_ATTACK).getRange()));
        }
        for (Cell cell : importantCells) {
            int score = 0;
            for (Cell cell1 : getARangeOfCellsThatIsNotWall(world, cell, 1)) {
                if (world.getOppHero(cell1) != null) {
                    score++;
                }
            }
            Pair<Cell, Integer> target = new Pair<>(cell, score);
            cells.add(target);
        }

        cells.sort((t1, t2) -> t2.getSecond() - t1.getSecond());

        int range = world.getAbilityConstants(AbilityName.BLASTER_ATTACK).getRange();
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            for (Pair<Cell, Integer> target : cells) {
                Cell targetCell = target.getFirst();
                if (target.getSecond() > 0 && world.manhattanDistance(hero.getCurrentCell(), targetCell) <= range) {
                    if (world.isInVision(hero.getCurrentCell(), targetCell)) {
                        world.castAbility(hero, AbilityName.BLASTER_ATTACK, targetCell);
                        break;
                    }
                }
            }
        }
    }


    void dodge(World world) {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            dodgeAHero(world, heroes[i], targetCells.get(i));
        }
        for (int i = 0; i < 4; i++)
            dodgeAHero(world, heroes[i], targetCells.get(i), true, true);

    }

}
