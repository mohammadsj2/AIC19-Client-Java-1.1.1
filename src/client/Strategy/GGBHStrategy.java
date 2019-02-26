package client.Strategy;

import client.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GGBHStrategy extends Strategy {
    private ArrayList<Integer> heroIds = new ArrayList<>();

    private ArrayList<Cell> getHeroTargetCells(World world) {
        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        ArrayList<Cell> answer = new ArrayList<>();
        int dr[] = {0, 2, 2, 4, 2, 2, 5, 2};
        int dc[] = {0, -2, 2, 0, -3, 3, 0, 0};

        Cell bestCell = objectiveZone[0];
        int bestAns = -100;

        for (Cell cell : objectiveZone) {
            int r = cell.getRow();
            int c = cell.getColumn();
            int ans = 0;
            for (int i = 0; i < 7; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                Cell cell1 = world.getMap().getCell(nr, nc);
                if (cell1.isWall()) {
                    ans--;
                } else if (cell1.isInObjectiveZone()) {
                    ans++;
                }
            }
            if (ans > bestAns) {
                bestCell = cell;
                bestAns = ans;
            }
        }
        for (int i = 0; i < 8; i++) {
            int nr = bestCell.getRow() + dr[i];
            int nc = bestCell.getColumn() + dc[i];
            Cell cell1 = world.getMap().getCell(nr, nc);
            answer.add(cell1);
        }
        return answer;
    }

    @Override
    public void preProcess(World world) {
    }

    static int cnt = 0;

    @Override
    public void pickTurn(World world) {
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.GUARDIAN);
                break;
            case 1:
                world.pickHero(HeroName.GUARDIAN);
                break;
            case 2:
                world.pickHero(HeroName.BLASTER);
                break;
            case 3:
                world.pickHero(HeroName.HEALER);
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
    public boolean heroIdsSetted=false;
    @Override
    public void moveTurn(World world) {
        if(!heroIdsSetted)setHeroIds(world);
        Integer Mod = 7,
                turn = 4;
        ArrayList<Cell> targetCells = getHeroTargetCells(world);
        Hero myHeros[] = world.getMyHeroes();
        for (int i = 0; i < 4; i++) {
            System.out.print(targetCells.get(i).getRow());
            System.out.print(" ");
            System.out.println(targetCells.get(i).getColumn());
        }
        if ((world.getCurrentTurn() % Mod) < turn) {
            System.out.println(world.getCurrentTurn());
            for (int i = 0; i < 4; i++) {
                Direction dir[] = world.getPathMoveDirections(myHeros[i].getCurrentCell(), targetCells.get(i));
                if (dir.length == 0)
                    continue;
                world.moveHero(myHeros[i], dir[0]);
            }
        } else {
            Direction dir[] = world.getPathMoveDirections(myHeros[0].getCurrentCell(), targetCells.get(0));
            if (dir.length != 0)
                world.moveHero(myHeros[0], dir[0]);

            for (int i = 1; i < 4; i++) {
                Direction dirs[] = world.getPathMoveDirections(myHeros[i].getCurrentCell(), targetCells.get(i + 3));
                if (dirs.length == 0)
                    continue;
                world.moveHero(myHeros[i], dirs[0]);
            }

        }
    }

    @Override
    public void actionTurn(World world) {
        System.out.println("action started");

        guardianActions(world);
        healerAction(world);
        attackActions(world);
        dodge(world);
    }

    private void attackActions(World world) {
        blastersAttack(world);
        guardiansAttacks(world);
        healersAttack(world);
    }

    private void healersAttack(World world) {
        Hero healer = world.getHero(heroIds.get(3));
        heroUseAbilityToAEnemy(world, healer, AbilityName.HEALER_ATTACK);
    }

    private void guardiansAttacks(World world) {
        heroUseAbilityToAEnemy(world, world.getHero(heroIds.get(0)), AbilityName.GUARDIAN_ATTACK);
        heroUseAbilityToAEnemy(world, world.getHero(heroIds.get(1)), AbilityName.GUARDIAN_ATTACK);
    }

    private void blastersAttack(World world) {
        Hero blaster = world.getHero(heroIds.get(2));
        blastersBombAttack(world, blaster);
        heroUseAbilityToAEnemy(world, blaster, AbilityName.BLASTER_ATTACK);
    }

    private void heroUseAbilityToAEnemy(World world, Hero blaster, AbilityName abilityName) {
        for (Cell cell : getARangeOfCells(world, blaster.getCurrentCell(),
                world.getAbilityConstants(abilityName).getRange())) {
            if (world.getOppHero(cell) != null) {
                world.castAbility(blaster, abilityName, cell);
                return;
            }
        }
    }

    private void blastersBombAttack(World world, Hero blaster) {
        ArrayList<Hero> heroArrayList = new ArrayList<>();
        for (Hero hero : world.getOppHeroes()) {
            if (hero.getCurrentCell().getRow() != -1) {
                heroArrayList.add(hero);
            }
        }
        Cell bestCell = getBestCellForBomb(world, blaster);
        if (bestCell != null) {
            world.castAbility(blaster, AbilityName.BLASTER_BOMB, bestCell);
        }
    }

    private Cell getBestCellForBomb(World world, Hero blaster) {
        Cell bestCell = blaster.getCurrentCell();
        int best = 0;

        for (Cell cell : getARangeOfCells(world, blaster.getCurrentCell(), world.getAbilityConstants(AbilityName.BLASTER_BOMB).getRange())) {
            int ans = 0;
            for (Cell cell2 : getARangeOfCells(world, cell, world.getAbilityConstants(AbilityName.BLASTER_BOMB).getAreaOfEffect())) {
                if (world.getOppHero(cell2) != null) {
                    ans++;
                }
            }
            if (ans > best) {
                best = ans;
                bestCell = cell;
            }
        }
        if (best == 0) {
            return null;
        }
        return bestCell;
    }

    private ArrayList<Cell> getARangeOfCells(World world, Cell cell, int range) {
        ArrayList<Cell> answer = new ArrayList<>();
        Cell[][] cells = world.getMap().getCells();
        for (int r = Math.max(0, cell.getRow() - range - 2); r < Math.min(cells.length, cell.getRow() + range + 2); r++) {
            for (int c = Math.max(0, cell.getColumn() - range - 2); c < Math.min(cells.length, cell.getColumn() + range + 2); c++) {
                if (world.manhattanDistance(cell, world.getMap().getCell(r, c)) <= range) {
                    answer.add(world.getMap().getCell(r, c));
                }
            }
        }
        return answer;
    }

    private void guardianActions(World world) {
        int cooldownTurns = world.getAbilityConstants(AbilityName.GUARDIAN_FORTIFY).getCooldown();
        int turn = world.getCurrentTurn();
        turn%=cooldownTurns;
        if (turn> 1) {
            return;
        }
        Hero guardian = world.getHero(heroIds.get(turn));
        world.castAbility(guardian, AbilityName.GUARDIAN_FORTIFY, getHeroTargetCells(world).get(7));
    }

    private void healerAction(World world) {
        Hero myHeros[] = world.getMyHeroes();

        AbilityConstants abilityHealer = world.getAbilityConstants(AbilityName.HEALER_HEAL);
        int range = abilityHealer.getRange(),
                power = abilityHealer.getPower();

        Hero healer = world.getHero(heroIds.get(3)); // HEALER ID = 3
        ArrayList<Hero> inArea = new ArrayList<>();
        for (Hero hero : myHeros) {
            int distance = world.manhattanDistance(hero.getCurrentCell(), healer.getCurrentCell());
            if (distance <= range)
                inArea.add(hero);
        }
        Collections.sort(inArea, Comparator.comparingInt(Hero::getCurrentHP));
        for (Hero hero : inArea) {
            if (hero.getCurrentHP() + power <= hero.getMaxHP()) {
                world.castAbility(healer, AbilityName.HEALER_HEAL, hero.getCurrentCell());
                return;
            }
        }
        if (inArea.size() != 0)
            world.castAbility(healer, AbilityName.HEALER_HEAL, inArea.get(0).getCurrentCell());
    }

    private void dodge(World world) {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Hero> heroArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            heroArrayList.add(heroes[i]);
        }
        ArrayList<Cell> targetCells = getHeroTargetCells(world);
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
