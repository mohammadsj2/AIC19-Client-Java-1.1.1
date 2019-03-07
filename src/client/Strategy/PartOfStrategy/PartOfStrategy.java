package client.Strategy.PartOfStrategy;

import client.Exception.NotEnoughApException;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Direction;
import client.model.World;

import java.util.ArrayList;

public abstract class PartOfStrategy {
    protected int maxAp=100;
    protected int remainAp=100;
    protected PartOfStrategy(int maxAp){
        this.maxAp=maxAp;
        remainAp=maxAp;
    }

    public void decreaseAp(int x) throws NotEnoughApException {
        if(x>remainAp){
            throw new NotEnoughApException();
        }
        remainAp-=x;
    }

    public void run(World world) throws NotEnoughApException {
        remainAp=maxAp;
    }

    public int getRemainAp() {
        return remainAp;
    }

    protected ArrayList<Cell> getARangeOfCellsThatIsNotWall(World world, Cell cell, int range) {
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

    protected Cell getBestCellForNotLinearStrategies(World world, Cell currentCell, AbilityName abilityName) {
        Cell bestCell = currentCell;
        int best = 0;

        for (Cell cell : getARangeOfCellsThatIsNotWall(world,currentCell, world.getAbilityConstants(abilityName).getRange())) {
            int ans = 0;
            for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, world.getAbilityConstants(abilityName).getAreaOfEffect())) {
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
}
