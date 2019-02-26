package client.Strategy;

import client.model.*;

import java.util.ArrayList;
import java.util.Random;

public class BBBBStrategy extends Strategy {
    private ArrayList<Integer> heroIds = new ArrayList<>();

    ArrayList<Cell> targetCells = new ArrayList<>();

    private ArrayList<Cell> getHeroTargetCellsZone(World world) {

        if (targetCells.size() != 0)
            return targetCells;

        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        Boolean[] mark = new Boolean[objectiveZone.length];
        for (int i = 0; i < objectiveZone.length; i++)
            mark[i] = false;

        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            Integer x = random.nextInt() % objectiveZone.length;
            while (mark[x] == true) {
                x = random.nextInt() % objectiveZone.length;
            }
            targetCells.add(objectiveZone[x]);
        }
        return targetCells;
    }

    @Override
    public void preProcess(World world) {
        System.out.println("pre process started");
    }

    static int cnt = 0;

    @Override
    public void pickTurn(World world) {
        System.out.println("pick started");
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
            case 4:
                preProcessAfterPickTurn(world);
                break;
        }
        cnt++;
    }

    private void preProcessAfterPickTurn(World world) {
        for (Hero hero : world.getMyHeroes()) {
            heroIds.add(hero.getId());
        }
    }

    @Override
    public void moveTurn(World world) {
        System.out.println("move started");
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        Hero myHeros[] = world.getMyHeroes();
        Hero oppHeros[] = world.getOppHeroes();
        Integer cnt[] = {0, 0, 0, 0};

        for (int i = 0; i < 4; i++) {
            Boolean goAfter = false;
            for (int j = 0; j < 4; j++)
                if (oppHeros[j].getCurrentCell().getRow() >= 0 && cnt[j] < 2) {
                    goAfter = true;
                    cnt[j]++;
                    Direction dir[] = world.getPathMoveDirections(myHeros[i].getCurrentCell(), oppHeros[j].getCurrentCell());
                    if (dir.length != 0)
                        world.moveHero(myHeros[i], dir[0]);
                    break;
                }
            if(goAfter == false) {
                Direction dirs[] = world.getPathMoveDirections(myHeros[i].getCurrentCell(), targetCells.get(i));
                if (dirs.length != 0)
                    world.moveHero(myHeros[i], dirs[0]);
            }
        }
    }

    @Override
    public void actionTurn(World world) {
        System.out.println("action started");

        blasterActions(world);
        blasterAttacks(world);
        dodge(world);
    }

    private void blasterAttacks(World world) {
        // TODO
    }

    private void blasterActions(World world) {
        Hero[] oppHeros = world.getOppHeroes();
        Cell[][] cells = world.getMap().getCells();
        // TODO
    }

    private void dodge(World world) {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Hero> heroArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            heroArrayList.add(heroes[i]);
        }
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            Hero hero = heroes[i];
            Direction dir[] = world.getPathMoveDirections(heroes[i].getCurrentCell(), targetCells.get(i));
            AbilityName dodgeAbility;
            if (i < 2) {
                dodgeAbility = AbilityName.GUARDIAN_DODGE;
            } else if (i == 2) {
                dodgeAbility = AbilityName.BLASTER_DODGE;
            } else {
                dodgeAbility = AbilityName.HEALER_DODGE;
            }
            if (dir.length >= world.getAbilityConstants(dodgeAbility).getRange()) {
                Cell targetCell = hero.getCurrentCell();
                for (int j = 0; j < world.getAbilityConstants(dodgeAbility).getRange(); j++) {
                    targetCell = getNextCellByDirection(world, targetCell, dir[j]);
                }
                world.castAbility(hero, dodgeAbility, targetCell);
            }
        }
    }

    private Cell getNextCellByDirection(World world, Cell cell, Direction direction) {
        int r = cell.getRow();
        int c = cell.getColumn();
        if (direction.equals(Direction.LEFT)) {
            c--;
        } else if (direction.equals(Direction.RIGHT)) {
            c++;
        } else if (direction.equals(Direction.UP)) {
            r--;
        } else {
            r++;
        }
        return world.getMap().getCell(r, c);
    }

}
