package client.Strategy.PartOfStrategy;

import client.Exception.NotEnoughApException;
import client.model.*;

import java.util.ArrayList;

public abstract class PartOfStrategy {
    public static final int INFINIT_AP = 1000000000;
    protected int maxAp = INFINIT_AP;
    protected int remainAp = INFINIT_AP;

    protected PartOfStrategy(int maxAp) {
        this.maxAp = maxAp;
        remainAp = maxAp;
    }

    public void decreaseAp(int x) throws NotEnoughApException {
        if (x > remainAp) {
            throw new NotEnoughApException();
        }
        remainAp -= x;
    }

    public void actionTurn(World world) throws NotEnoughApException {

    }

    public void moveTurn(World world) throws NotEnoughApException {

    }

    public void preProcess(World world) {

    }

    public void setMaxAp(int maxAp) {
        this.maxAp = maxAp;
    }

    public int getMaxAp() {
        return maxAp;
    }

    public void setRemainAp(int remainAp) {
        this.remainAp = remainAp;
    }

    public int getRemainAp() {
        return remainAp;
    }

    protected ArrayList<Cell> getARangeOfCellsThatIsNotWall(World world, Cell cell, int range) {
        ArrayList<Cell> tmp = getARangeOfCells(world, cell, range);
        ArrayList<Cell> answer = new ArrayList<>();
        for (Cell cell1 : tmp) {
            if (!cell1.isWall()) {
                answer.add(cell1);
            }
        }
        return answer;
    }

    protected ArrayList<Cell> getARangeOfCells(World world, Cell cell, int range) {
        ArrayList<Cell> answer = new ArrayList<>();
        Cell[][] cells = world.getMap().getCells();
        for (int r = Math.max(0, cell.getRow() - range - 2); r < Math.min(cells.length, cell.getRow() + range + 2); r++) {
            for (int c = Math.max(0, cell.getColumn() - range - 2); c < Math.min(cells.length, cell.getColumn() + range + 2); c++) {
                Cell secondCell = world.getMap().getCell(r, c);
                if (world.manhattanDistance(cell, secondCell) <= range) {
                    answer.add(secondCell);
                }
            }
        }
        return answer;
    }

    protected Cell getNextCellByDirection(World world, Cell cell, Direction direction) {
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

    protected Cell getCellWithMostOppHeroesForNotLinearAbilities(World world, Cell currentCell, AbilityName abilityName) {
        Cell bestCell = currentCell;
        int best = 0;

        int range = world.getAbilityConstants(abilityName).getRange();
        for (Cell cell : getARangeOfCells(world, currentCell, range)) {
            int areaOfEffect = world.getAbilityConstants(abilityName).getAreaOfEffect();
            int ans = getNumberOfOppHeroesInRange(world, cell, areaOfEffect);
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

    private int getNumberOfOppHeroesInRange(World world, Cell cell, int range) {
        int ans = 0;
        for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, range)) {
            if (world.getOppHero(cell2) != null) {
                ans++;
            }
        }
        return ans;
    }

    protected Cell getCellWithMostOwnHeroesForNotLinearAbilities(World world, Cell currentCell, AbilityName abilityName) {
        Cell bestCell = currentCell;
        int best = 0;

        for (Cell cell : getARangeOfCellsThatIsNotWall(world, currentCell,
                world.getAbilityConstants(abilityName).getRange())) {
            int ans = getNumberOfOwnHeroesInRange(world, cell, world.getAbilityConstants(abilityName).getAreaOfEffect());
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

    private int getNumberOfOwnHeroesInRange(World world, Cell cell, int range) {
        int ans = 0;
        for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, range)) {
            if (world.getMyHero(cell2) != null) {
                ans++;
            }
        }
        return ans;
    }

    protected void heal(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.HEALER_HEAL);
    }

    protected void move(World world, Hero hero, Direction direction) throws NotEnoughApException {
        decreaseAp(hero.getMoveAPCost());
        world.moveHero(hero, direction);
    }

    protected void dodge(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        Ability dodgeAbility = hero.getDodgeAbilities()[0];
        castAbility(world, hero, targetCell, dodgeAbility.getName());
    }

    protected void bombAttack(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.BLASTER_BOMB);
    }

    protected void guard(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.GUARDIAN_FORTIFY);
    }

    protected void castAbility(World world, Hero hero, Cell targetCell, AbilityName blasterBomb) throws NotEnoughApException {
        System.out.println("NOOOOOO pokhte :" + blasterBomb);
        decreaseAp(hero.getAbility(blasterBomb).getAPCost());
        System.out.println("shalghame pokhte :" + blasterBomb);
        world.castAbility(hero.getId(), blasterBomb, targetCell);
    }

}
