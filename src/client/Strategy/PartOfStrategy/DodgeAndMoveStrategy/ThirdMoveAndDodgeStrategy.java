package client.Strategy.PartOfStrategy.DodgeAndMoveStrategy;

import client.Exception.CantFindRandomTargetZone;
import client.Strategy.Tools.BFS;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Pair;
import client.model.World;

import java.util.ArrayList;

public class ThirdMoveAndDodgeStrategy extends SecondMoveAndDodgeStrategy {

    public ThirdMoveAndDodgeStrategy(int maxAp, BFS bfs) {
        super(maxAp, bfs);
    }

    @Override
    ArrayList<Cell> getHeroTargetCellsZone(World world) {
        if (targetZoneCells.size() != 0)
            return targetZoneCells;

        int rangeOfBomb = world.getAbilityConstants(AbilityName.BLASTER_BOMB).getAreaOfEffect();
        ArrayList<Pair<Pair<Integer, Boolean>, ArrayList<Cell>>> toSort = new ArrayList();
        for (int minimumDistance = rangeOfBomb * 2 + 1; minimumDistance >= 2; minimumDistance--) {
            boolean flag = false;
            for (int t = 0; t < 30; t++) {
                try {
                    ArrayList<Cell> rndTargetZonesByMinimumDistance =
                            getRandomHeroTargetZonesByMinimumDistance(world, minimumDistance);
                    int maximumDistance = getMaximumDistance(world, rndTargetZonesByMinimumDistance);
                    boolean twoOfThemIsInALine = isTwoOfThemIsInALineOfShadow(world, rndTargetZonesByMinimumDistance);
                    toSort.add(new Pair<>(new Pair<>(maximumDistance, twoOfThemIsInALine), rndTargetZonesByMinimumDistance));
                    flag = true;
                } catch (CantFindRandomTargetZone ignored) {

                }
            }
            if (flag) {
                break;
            }
        }
        toSort.sort((o1, o2) -> {
            if (o1.getFirst().getSecond() != o2.getFirst().getSecond()) {
                if (!o1.getFirst().getSecond()) {
                    return -1;
                }
                return 1;
            }
            return o1.getFirst().getFirst() - o2.getFirst().getFirst();
        });
        targetZoneCells = toSort.get(0).getSecond();
        return targetZoneCells;
    }

    private int getMaximumDistance(World world, ArrayList<Cell> targetZones) {
        int ans = 0;
        for (Cell cell : targetZones) {
            for (Cell cell1 : targetZones) {
                ans = Math.max(ans, world.manhattanDistance(cell, cell1));
            }
        }
        return ans;
    }

    private boolean isTwoOfThemIsInALineOfShadow(World world, ArrayList<Cell> targetZones) {
        for (Cell cell : targetZones) {
            for (Cell cell1 : targetZones) {
                int range = world.getAbilityConstants(AbilityName.SHADOW_SLASH).getRange();
                int manhattanDistance = world.manhattanDistance(cell, cell1);
                if (manhattanDistance > range) {
                    continue;
                }
                if (cell.getRow() == cell1.getRow() || cell.getColumn() == cell1.getColumn())
                    return true;
            }
        }
        return false;
    }
}
