package client.Strategy.Tools;

import client.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BFS {
    private static final int NUMBER_OF_MOVE_PHASES = 6;
    private static final int MAX_DISTANCE = 1000000000;
    private static final int MAX_COOL_DOWN = 9;
    private static final int MAXIMUM_MEMORY_SIZE = 20;

    private Map map;
    private ArrayList<Pair<Cell, Integer>> bfsQueue = new ArrayList<>();
    private ArrayList<Pair<Pair<Cell, Ability>, int[][][]>> memory;

    BFS(Map map) {
        this.map = map;
    }

    //Notebayad too avalin moveTurn seda zade beshe ha !!
    private int[][][] getDistancesWithBFS(Cell targetCell, Ability ability) {
        int[][][] distance = new int[map.getRowNum()][map.getColumnNum()][MAX_COOL_DOWN + 1];
        int queueHead = 0;
        int coolDownDuration = ability.getCooldown();
        int range = ability.getRange();

        for (int[][] tmp : distance) {
            for (int[] tmp2 : tmp) {
                for (int i = 0; i < tmp2.length; i++) {
                    tmp2[i] = MAX_DISTANCE;
                }
            }
        }

        for (int i = 0; i < coolDownDuration; i++) {
            bfsQueue.add(new Pair<>(targetCell, 0));
            distance[targetCell.getRow()][targetCell.getColumn()][i] = 0;
        }

        while (queueHead != bfsQueue.size()) {
            Pair<Cell, Integer> u = bfsQueue.get(queueHead++);
            int r = u.getFirst().getRow(), c = u.getFirst().getColumn();
            int dis = distance[r][c][u.getSecond()];


            for (int i = -NUMBER_OF_MOVE_PHASES; i <= NUMBER_OF_MOVE_PHASES; i++) {
                for (int j = -NUMBER_OF_MOVE_PHASES; j <= NUMBER_OF_MOVE_PHASES; j++) {
                    if (Math.abs(i) + Math.abs(j) > NUMBER_OF_MOVE_PHASES ) {
                        continue;
                    }
                    int nr = r + i, nc = c + j;
                    if (!map.isInMap(nr, nc)) {
                        continue;
                    }
                    int nv = u.getSecond() + 1;
                    if (nv == coolDownDuration) {
                        continue;
                    }
                    Cell cell = map.getCell(nr, nc);
                    Pair<Cell, Integer> v = new Pair<>(cell, nv);
                    if (distance[nr][nc][nv] > dis + 1) {
                        distance[nr][nc][nv] = dis + 1;
                        bfsQueue.add(v);
                    }
                    if (u.getSecond() == 0) {
                        nv = 0;
                        v = new Pair<>(cell, nv);
                        if (distance[nr][nc][nv] > dis + 1) {
                            distance[nr][nc][nv] = dis + 1;
                            bfsQueue.add(v);
                        }
                    }
                }
            }
            if (u.getSecond() == coolDownDuration - 1) {
                for (int i = -range; i <= range; i++) {
                    for (int j = -range; j <= range; j++) {
                        if (Math.abs(i) + Math.abs(j) > range) {
                            continue;
                        }
                        int nr = r + i, nc = c + j;
                        if (!map.isInMap(nr, nc)) {
                            continue;
                        }
                        Cell cell = map.getCell(nr, nc);
                        Pair<Cell, Integer> v = new Pair<>(cell, 0);
                        if (distance[nr][nc][0] > dis + 1) {
                            distance[nr][nc][0] = dis + 1;
                            bfsQueue.add(v);
                        }
                    }
                }
            }
        }
        return distance;
    }

    public int[][][] getDistance(Cell targetCell, Ability ability) {
        int[][][] distance = getDistanceFromMemory(targetCell, ability);
        if (distance != null) {
            return distance;
        }
        distance = getDistancesWithBFS(targetCell, ability);
        Pair<Pair<Cell, Ability>, int[][][]> ozv = new Pair<>(new Pair<>(targetCell, ability), distance);
        while (memory.size() >= MAXIMUM_MEMORY_SIZE) {
            memory.remove(0);
        }
        memory.add(ozv);
        return ozv.getSecond();
    }

    private int[][][] getDistanceFromMemory(Cell targetCell, Ability ability) {
        int[][][] distance = null;
        for (Pair<Pair<Cell, Ability>, int[][][]> p : memory) {
            if (p.getFirst().getFirst().equals(targetCell) && p.getFirst().getSecond().getName().equals(ability.getName())) {
                distance = p.getSecond();
                break;
            }
        }
        return distance;
    }
    public int[][] getNormalDistance(Cell startCell) {
        /*if (startCell == null || endCell == null || startCell == endCell || blockedCells == null ||
                startCell.isWall() || endCell.isWall()) return new Direction[0];
        HashMap<Cell, Pair<Cell, Direction>> lastMoveInfo = new HashMap<>(); // saves parent cell and direction to go from parent cell to current cell
        Cell[] bfsQueue = new Cell[map.getRowNum() * map.getColumnNum() + 10];
        int queueHead = 0;
        int queueTail = 0;

        lastMoveInfo.put(startCell, new Pair<>(null, null));
        for (Cell cell : blockedCells) {
            lastMoveInfo.put(cell, new Pair<>(null, null));
        }

        bfsQueue[queueTail++] = startCell;

        while (queueHead != queueTail) {
            Cell currentCell = bfsQueue[queueHead++];
            if (currentCell.equals(endCell)) {
                ArrayList<Direction> directions = new ArrayList<>();
                while (!currentCell.equals(startCell)) {
                    directions.add(lastMoveInfo.get(currentCell).getSecond());
                    currentCell = lastMoveInfo.get(currentCell).getFirst();
                }
                Collections.reverse(directions);
                return directions.toArray(new Direction[0]);
            }
            for (Direction direction : Direction.values()) {
                Cell nextCell = getNextCell(currentCell, direction);
                if (nextCell != null && isAccessible(nextCell) && !lastMoveInfo.containsKey(nextCell)) {
                    lastMoveInfo.put(nextCell, new Pair<>(currentCell, direction));
                    bfsQueue[queueTail++] = nextCell;
                }
            }
        }
        return new Direction[0];*/
        return null;
    }
}
