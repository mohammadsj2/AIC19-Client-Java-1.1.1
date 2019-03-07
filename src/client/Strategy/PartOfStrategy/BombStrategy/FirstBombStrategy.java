package client.Strategy.PartOfStrategy.BombStrategy;


import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

/*
    jayi ro peyda mikone ke be tedad bishtari sadame bezane

 */
public class FirstBombStrategy extends PartOfStrategy {
    Hero blaster;
    FirstBombStrategy(int maxAp, Hero blaster) {
        super(maxAp);
        this.blaster=blaster;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {

        Cell bestCell = getBestCellForNotLinearStrategies(world, blaster.getCurrentCell() , AbilityName.BLASTER_BOMB);
        if (bestCell != null) {
            bombAttack(world,blaster,bestCell);
        }
    }


}
