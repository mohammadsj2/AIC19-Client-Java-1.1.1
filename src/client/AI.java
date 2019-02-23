package client;

import client.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AI {
    private Random random = new Random();

    private ArrayList<Cell> getHeroTargetCells(World world) {
        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        ArrayList<Cell> answer = new ArrayList<>();
        int dr[] = {0, 2, 2, 4, 2, 2, 5};
        int dc[] = {0, -2, 2, 0, -3, 3, 0};

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
        for (int i = 0; i < 7; i++) {
            int nr = bestCell.getRow() + dr[i];
            int nc = bestCell.getColumn() + dc[i];
            Cell cell1 = world.getMap().getCell(nr, nc);
            answer.add(cell1);
        }
        return answer;
    }


    public void preProcess(World world) {
        System.out.println("pre process started");
    }
    static int cnt=0;
    public void pickTurn(World world) {
        System.out.println("pick started");
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

    public void moveTurn(World world) {
        System.out.println("move started");
        Integer Mod = 7,
                turn = 4;
        ArrayList<Cell> targetCells = getHeroTargetCells(world);
        Hero myHeros[] = world.getMyHeroes();
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
            for(int i = 1; i < 4; i++){
                Direction dirs[] = world.getPathMoveDirections(myHeros[i].getCurrentCell(), targetCells.get(i + 3));
                if (dirs.length == 0)
                    continue;
                world.moveHero(myHeros[i], dirs[0]);
            }

        }
    }
    private Cell getNextCellByDirection(World world,Cell cell,Direction direction){
        int r=cell.getRow();
        int c=cell.getColumn();
        if(direction.equals(Direction.LEFT)){
            c--;
        }else if(direction.equals(Direction.RIGHT)){
            c++;
        }else if(direction.equals(Direction.UP)){
            r--;
        }else {
            r++;
        }
        return world.getMap().getCell(r,c);
    }

    public void actionTurn(World world) {
        System.out.println("action started");

        dodge(world);
    }

    private void dodge(World world) {
        Hero[] heroes = world.getMyHeroes();
        ArrayList<Hero> heroArrayList=new ArrayList<>();
        for(int i=0;i<4;i++){
            heroArrayList.add(heroes[i]);
        }
        ArrayList<Cell> targetCells=getHeroTargetCells(world);
        for(int i=0;i<4;i++){
            Hero hero=heroes[i];
            Direction dir[] = world.getPathMoveDirections(heroes[i].getCurrentCell(), targetCells.get(i));
            AbilityName dodgeAbility;
            if(i<2){
                dodgeAbility=AbilityName.GUARDIAN_DODGE;
            }else if(i==2){
                dodgeAbility=AbilityName.BLASTER_DODGE;
            }else{
                dodgeAbility=AbilityName.HEALER_DODGE;
            }
            if(dir.length>=world.getAbilityConstants(dodgeAbility).getRange()){
                Cell targetCell=hero.getCurrentCell();
                for(int j=0;j<world.getAbilityConstants(dodgeAbility).getRange();j++){
                    targetCell=getNextCellByDirection(world,targetCell,dir[j]);
                }
                world.castAbility(hero,dodgeAbility,targetCell);
            }
        }
    }

}
