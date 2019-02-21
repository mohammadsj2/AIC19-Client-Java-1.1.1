package client;

import client.model.*;

import java.util.ArrayList;
import java.util.Random;

public class AI
{
    private World world;
    private ArrayList<Cell> getHeroTargetCells(World world) {
        Cell[] objectiveZone = world.getMap().getObjectiveZone();
        ArrayList<Cell> answer = new ArrayList<>();
        int dr[]={0,2,2,4,2,2,5};
        int dc[]={0,-2,2,0,-3,3,0};

        Cell bestCell=objectiveZone[0];
        int bestAns=-100;

        for (Cell cell:objectiveZone) {
            int r = cell.getRow();
            int c = cell.getColumn();
            int ans=0;
            for(int i=0;i<7;i++){
                int nr=r+dr[i];
                int nc=c+dc[i];
                Cell cell1=world.getMap().getCell(nr,nc);
                if(cell1.isWall()){
                    ans--;
                }else if(cell1.isInObjectiveZone()){
                    ans++;
                }
            }
            if(ans>bestAns){
                bestCell=cell;
                bestAns=ans;
            }
        }
        for(int i=0;i<7;i++){
            int nr=bestCell.getRow()+dr[i];
            int nc=bestCell.getColumn()+dc[i];
            Cell cell1=world.getMap().getCell(nr,nc);
            answer.add(cell1);
        }
        return answer;
    }
    private Random random = new Random();

    public void preProcess(World world)
    {
        this.world=world;
        System.out.println("pre process started");
    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");
        world.pickHero(HeroName.values()[world.getCurrentTurn()]);
    }

    public void moveTurn(World world)
    {
        System.out.println("move started");
        Hero[] heroes = world.getMyHeroes();

        for (Hero hero : heroes)
        {
            world.moveHero(hero, Direction.values()[random.nextInt(4)]);
        }
    }

    public void actionTurn(World world) {
        System.out.println("action started");
        Hero[] heroes = world.getMyHeroes();
        Map map = world.getMap();
        for (Hero hero : heroes)
        {
            int row = random.nextInt(map.getRowNum());
            int column = random.nextInt(map.getColumnNum());
            world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);
        }
    }

}
