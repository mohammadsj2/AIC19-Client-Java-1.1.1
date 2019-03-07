package client.Strategy.PartOfStrategy.DodgeStrategy;

import client.Exception.NotEnoughApException;
import client.model.*;

import java.util.ArrayList;
import java.util.Comparator;

public class FirstDodgeStrategy extends DodgeStrategy{
    protected FirstDodgeStrategy(int maxAp) {
        super(maxAp);
    }

    @Override
    public void run(World world) throws NotEnoughApException {
        super.run(world);

    }
    int dodgeAHero(World world, Hero hero, Cell targetCell, boolean action, boolean force) {
        if(hero.getCurrentCell().equals(targetCell)){
            return 0;
        }
        Direction dir[] = world.getPathMoveDirections(hero.getCurrentCell(), targetCell);
        AbilityName dodgeAbility = hero.getDodgeAbilities()[0].getName();
        int range = world.getAbilityConstants(dodgeAbility).getRange();
        if (dir.length >= range || force) {
            ArrayList<Pair<Cell,Integer>> toSortPairs=new ArrayList<>();
            for (Cell dodgeCell : getARangeOfCellsThatIsNotWall(world, hero.getCurrentCell(), world.getAbilityConstants(dodgeAbility).getRange())) {
                int length = world.getPathMoveDirections(dodgeCell, targetCell).length;
                toSortPairs.add(new Pair<>(dodgeCell,length));
            }
            toSortPairs.sort(Comparator.comparingInt(Pair::getSecond));
            if (action) {
                for(int i=0;i<Math.min(8,toSortPairs.size());i++){
                    world.castAbility(hero, dodgeAbility, toSortPairs.get(i).getFirst());
                }
            }
            return dir.length - world.getPathMoveDirections(toSortPairs.get(0).getFirst(), targetCell).length;
        }
        return 0;
    }

    int dodgeAHero(World world, Hero hero, Cell targetCell) {
        return dodgeAHero(world, hero, targetCell, true, false);
    }
}
