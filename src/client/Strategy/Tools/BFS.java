package client.Strategy.Tools;

import client.model.*;
import client.model.Map;

import java.util.*;

public class BFS {
    private static final int NUMBER_OF_MOVE_PHASES = 6;
    private static final int MAX_DISTANCE = 1000 * 1000 * 1000;
    private static final int MAX_COOL_DOWN = 9;
    private static final int MAXIMUM_MEMORY_SIZE = 20;
    private static final int NUMBER_OF_NEIGHBOURS = 4;
    private static final int oo = MAX_DISTANCE;
    public static final int ONE_TURN_IN_BFS = 10000;
    public static final int BFS_DODGE_TOADD = 5;
    public static final int BFS_INOBJECTIVE_ZONE_TOADD = -50;

    private Map map;

    private ArrayList<Pair<Pair<Cell, Ability>, int[][][]>> memory = new ArrayList<>();
    private int[][][][] normalDistance;

    public BFS(Map map) {
        this.map = map;
        int columnNum = map.getColumnNum() + 3;
        int rowNum = map.getRowNum() + 3;
        normalDistance = new int[rowNum][columnNum][][];
        for (Cell[] cells : map.getCells()) {
            for (Cell cell : cells) {
                normalDistance[cell.getRow()][cell.getColumn()] = getNormalDistance(cell);
            }
        }
    }

    //Note bayad too avalin moveTurn seda zade beshe ha !!
    private int[][][] getDistancesWithBFS(Cell targetCell, Ability ability) {
        ArrayList<Pair<Cell, Integer>> bfsQueue = new ArrayList<>();
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
            bfsQueue.add(new Pair<>(targetCell, i));
            distance[targetCell.getRow()][targetCell.getColumn()][i] = 5 * i;
        }

        while (queueHead != bfsQueue.size()) {
            Pair<Cell, Integer> u = bfsQueue.get(queueHead++);
            int r = u.getFirst().getRow(), c = u.getFirst().getColumn();
            int dis = distance[r][c][u.getSecond()];


            for (int i = -NUMBER_OF_MOVE_PHASES; i <= NUMBER_OF_MOVE_PHASES; i++) {
                for (int j = -NUMBER_OF_MOVE_PHASES; j <= NUMBER_OF_MOVE_PHASES; j++) {
                    int nr = r + i, nc = c + j;
                    int manhattanDistance = Math.abs(i) + Math.abs(j);
                    if (manhattanDistance > NUMBER_OF_MOVE_PHASES) {
                        continue;
                    }
                    if (!map.isInMap(nr, nc) || map.getCell(r, c).isWall()
                            || normalDistance[r][c][nr][nc] > NUMBER_OF_MOVE_PHASES) {
                        continue;
                    }
                    Cell cell = map.getCell(nr, nc);

                    int nv = u.getSecond() + 1;
                    if (nv == coolDownDuration) {
                        continue;
                    }
                    Pair<Cell, Integer> v = new Pair<>(cell, nv);
                    int turnDistance = dis + ONE_TURN_IN_BFS + manhattanDistance +
                            (v.getFirst().isInObjectiveZone() ? BFS_INOBJECTIVE_ZONE_TOADD : 0);
                    if (distance[nr][nc][nv] > turnDistance) {
                        distance[nr][nc][nv] = turnDistance;
                        bfsQueue.add(v);
                    }
                    if (u.getSecond() == 0) {
                        nv = 0;
                        v = new Pair<>(cell, nv);
                        int turnDistance2 = dis + ONE_TURN_IN_BFS + manhattanDistance +
                                (v.getFirst().isInObjectiveZone() ? BFS_INOBJECTIVE_ZONE_TOADD : 0);
                        if (distance[nr][nc][nv] > turnDistance2) {
                            distance[nr][nc][nv] = turnDistance2;
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
                        if (!map.isInMap(nr, nc) || map.getCell(nr, nc).isWall()) {
                            continue;
                        }
                        Cell cell = map.getCell(nr, nc);
                        Pair<Cell, Integer> v = new Pair<>(cell, 0);
                        if (distance[nr][nc][0] > dis + ONE_TURN_IN_BFS + BFS_DODGE_TOADD) {
                            distance[nr][nc][0] = dis + ONE_TURN_IN_BFS + BFS_DODGE_TOADD;
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

    public int getNormalDistance(Cell firstCell, Cell secondCell) {
        return normalDistance[firstCell.getRow()][firstCell.getColumn()][secondCell.getRow()][secondCell.getColumn()];
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

    private int[][] getNormalDistance(Cell startCell) {

        int[][] distance = new int[map.getRowNum()][map.getColumnNum()];
        for (int r = 0; r < map.getRowNum(); r++)
            for (int c = 0; c < map.getColumnNum(); c++)
                distance[r][c] = oo;

        if (startCell.isWall()) {
            return distance;
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        ArrayList<Pair<Integer, Integer>> queue = new ArrayList<>();

        queue.add(new Pair<>(startCell.getRow(), startCell.getColumn()));
        distance[startCell.getRow()][startCell.getColumn()] = 0;
        int st = 0;

        while (queue.size() != st) {
            Pair<Integer, Integer> p = queue.get(st);
            st++;
            int row = p.getFirst(), col = p.getSecond();
            for (int i = 0; i < NUMBER_OF_NEIGHBOURS; i++) {
                int nr = row + dx[i], nc = col + dy[i];
                if (!map.isInMap(nr, nc)) continue;
                if (map.getCell(nr, nc).isWall()) continue;
                if (distance[nr][nc] > distance[row][col] + 1) {
                    distance[nr][nc] = distance[row][col] + 1;
                    queue.add(new Pair<>(nr, nc));
                }
            }
        }

        return distance;
    }
}
