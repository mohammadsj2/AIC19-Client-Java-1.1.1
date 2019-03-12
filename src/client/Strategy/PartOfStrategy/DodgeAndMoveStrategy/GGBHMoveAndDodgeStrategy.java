package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.model.*;

import java.util.ArrayList;

public class GGBHMoveAndDodgeStrategy extends FirstMoveAndDodgeStrategy {
    public GGBHMoveAndDodgeStrategy(int maxAp) {
        super(maxAp);
    }

    @Override
    ArrayList<Cell> getHeroTargetCellsZone(World world) {
        int mod = world.getAbilityConstants(AbilityName.GUARDIAN_FORTIFY).getCooldown();
        if (targetZoneCells.size() > 0 && (world.getCurrentTurn() % mod) <= 2) {
            return targetZoneCells;
        } else if (targetZoneCells.size() > 0) {
            ArrayList<Cell> answer = new ArrayList<>();
            for (int i = 1; i < 4; i++) {
                answer.add(targetZoneCells.get(i + 4));
            }
            return answer;
        }
        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        ArrayList<Cell> answer = new ArrayList<>();
        int dr[] = {0, 2, 2, 4, 0, 2, 2, 5};
        int dc[] = {0, -2, 2, 0, 0, -3, 3, 0};

        Cell bestCell = objectiveZone[0];
        int bestAns = -100;

        for (Cell cell : objectiveZone) {
            int r = cell.getRow();
            int c = cell.getColumn();
            int ans = 0;
            for (int i = 0; i < 8; i++) {
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
        targetZoneCells = answer;
        return getHeroTargetCellsZone(world);
    }

    @Override
    protected void swapTargetCells(int i, int j) {
        super.swapTargetCells(i, j);
        super.swapTargetCells(i + 4, j + 4);
    }
}
