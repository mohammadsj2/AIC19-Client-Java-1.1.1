package client.Strategy;

import client.model.*;

import java.util.ArrayList;

public class BBBBStrategy extends Strategy {
    private ArrayList<Integer> heroIds = new ArrayList<>();
    private ArrayList<Cell> targetCells = new ArrayList<>();




    private ArrayList<Cell> getHeroTargetCellsZone(World world) {

        if (targetCells.size() != 0)
            return targetCells;

        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        Boolean[] mark = new Boolean[objectiveZone.length];
        for (int i = 0; i < objectiveZone.length; i++)
            mark[i] = false;


        for (int i = 0; i < 4; i++) {
            Integer x = getRandomIntegerLessThan(objectiveZone.length);
            while (mark[x]) {
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

    private static int cnt = 0;

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
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        Hero myHeros[] = world.getMyHeroes();
        Hero oppHeros[] = world.getOppHeroes();
        int cnt[] = {0, 0, 0, 0};

        for (int i = 0; i < 4; i++) {
            Hero hero=myHeros[i];
            Cell targetCell = targetCells.get(i);
            boolean goAfter = false;
            for (int j = 0; j < 4; j++)
                if (oppHeros[j].getCurrentCell().getRow() >= 0 && cnt[j] < 2) {
                    goAfter = true;
                    cnt[j]++;
                    Direction dir[] = world.getPathMoveDirections(hero.getCurrentCell(),
                            oppHeros[j].getCurrentCell());
                    if (dir.length != 0)
                        world.moveHero(hero, dir[0]);
                    break;
                }
            if (!goAfter) {
                Direction dirs[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
                if (dirs.length != 0)
                    world.moveHero(hero, dirs[0]);
            }
        }
    }

    @Override
    public void actionTurn(World world) {
        blastersBombAttacks(world);
        blasterAttacks(world);
        dodge(world);
    }

    private void blastersBombAttacks(World world) {
        for(Hero hero:world.getMyHeroes()){
            blastersBombAttack(world,hero);
        }
    }

    private void blasterAttacks(World world) {
        int dr[] = {-1, 0, +1, 0};
        int dc[] = {0, +1, 0, -1};
        ArrayList<Pair<Cell, Integer>> cells = new ArrayList<>();
        for (int i = 0; i < world.getMap().getRowNum(); i++)
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                Cell cell = world.getMap().getCell(i, j);
                int score = 0;
                for (int k = 0; k < 4; k++) {
                    int nr = i + dr[k];
                    int nc = j + dc[k];
                    if (world.getMap().isInMap(nr, nc) && world.getOppHero(nr, nc) != null)
                        score++;
                }
                Pair<Cell, Integer> target = new Pair<>(cell, score);
                cells.add(target);
            }

        cells.sort((t1, t2) -> t2.getSecond()-t1.getSecond());

        int range = world.getAbilityConstants(AbilityName.BLASTER_ATTACK).getRange();
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            for (Pair<Cell, Integer> target : cells) {
                Cell cell = target.getFirst();
                if (world.manhattanDistance(hero.getCurrentCell(), cell) <= range) {
                    world.castAbility(hero, AbilityName.BLASTER_ATTACK, cell);
                    break;
                }
            }
        }
    }


    private void dodge(World world) {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Cell> targetCells = getHeroTargetCellsZone(world);
        for (int i = 0; i < 4; i++) {
            dodgeAHero(world, heroes[i], targetCells.get(i));
        }
    }

}
