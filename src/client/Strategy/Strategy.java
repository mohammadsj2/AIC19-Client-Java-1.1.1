package client.Strategy;

import client.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public abstract class Strategy {
    Random random = new Random();

    public abstract void preProcess(World world);
    public abstract void pickTurn(World world);
    public abstract void moveTurn(World world);
    public abstract void actionTurn(World world);

    Cell getNextCellByDirection(World world, Cell cell, Direction direction) {
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

    int dodgeAHero(World world, Hero hero, Cell targetCell, boolean action) {
        Direction dir[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
        AbilityName dodgeAbility=hero.getDodgeAbilities()[0].getName();
        int range = world.getAbilityConstants(dodgeAbility).getRange();

        if (dir.length >= range) {
            ArrayList<Cell> cellsOfPath=new ArrayList<>();
            cellsOfPath.add(hero.getCurrentCell());
            Cell targetCell2 = hero.getCurrentCell();
            for (Direction direction:dir) {
                targetCell2 = getNextCellByDirection(world, targetCell2, direction);
                cellsOfPath.add(targetCell2);
            }
            for(int i=cellsOfPath.size()-1;i>=0;i--){
                Cell cell=cellsOfPath.get(i);
                if(world.manhattanDistance(hero.getCurrentCell(),cell)<=range){
                    if(action)world.castAbility(hero, dodgeAbility, targetCell2);
                    return i;
                }
            }
        }
        return 0;
    }
    int dodgeAHero(World world, Hero hero, Cell targetCell){
        return dodgeAHero(world,hero,targetCell,true);
    }

    ArrayList<Cell> getARangeOfCellsThatIsNotWall(World world, Cell cell, int range) {
        ArrayList<Cell> answer = new ArrayList<>();
        Cell[][] cells = world.getMap().getCells();
        for (int r = Math.max(0, cell.getRow() - range - 2); r < Math.min(cells.length, cell.getRow() + range + 2); r++) {
            for (int c = Math.max(0, cell.getColumn() - range - 2); c < Math.min(cells.length, cell.getColumn() + range + 2); c++) {
                Cell secondCell = world.getMap().getCell(r, c);
                if (!secondCell.isWall() && world.manhattanDistance(cell, secondCell) <= range) {
                    answer.add(secondCell);
                }
            }
        }
        return answer;
    }

    Cell getBestCellForBomb(World world, Hero blaster) {
        Cell bestCell = blaster.getCurrentCell();
        int best = 0;

        for (Cell cell : getARangeOfCellsThatIsNotWall(world, blaster.getCurrentCell(), world.getAbilityConstants(AbilityName.BLASTER_BOMB).getRange())) {
            int ans = 0;
            for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, world.getAbilityConstants(AbilityName.BLASTER_BOMB).getAreaOfEffect())) {
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

    void healTheBestHero(World world, Hero healer) {
        AbilityConstants abilityHealer = world.getAbilityConstants(AbilityName.HEALER_HEAL);
        int range = abilityHealer.getRange(),
                power = abilityHealer.getPower();
        ArrayList<Hero> inArea = new ArrayList<>();
        for (Hero hero : world.getMyHeroes()) {
            int distance = world.manhattanDistance(hero.getCurrentCell(), healer.getCurrentCell());
            if (distance <= range)
                inArea.add(hero);
        }
        inArea.sort(Comparator.comparingInt(Hero::getCurrentHP));
        for (Hero hero : inArea) {
            if (hero.getCurrentHP() + power <= hero.getMaxHP()) {
                world.castAbility(healer, AbilityName.HEALER_HEAL, hero.getCurrentCell());
                return;
            }
        }
        if (inArea.size() != 0)
            world.castAbility(healer, AbilityName.HEALER_HEAL, inArea.get(0).getCurrentCell());
    }

    void blastersBombAttack(World world, Hero blaster) {
        Cell bestCell = getBestCellForBomb(world, blaster);
        if (bestCell != null) {
            world.castAbility(blaster, AbilityName.BLASTER_BOMB, bestCell);
        }
    }

    int getRandomIntegerLessThan(int x) {
        return ((random.nextInt() % x) + x) % x;
    }
}
