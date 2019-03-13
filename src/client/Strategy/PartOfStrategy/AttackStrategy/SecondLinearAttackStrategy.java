package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.Ability;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;

public class SecondLinearAttackStrategy extends PartOfStrategy {
    private int heroId;
    int[] healths;

    public SecondLinearAttackStrategy(int maxAp, int hero, int[] healths) {
        super(maxAp);
        this.heroId = hero;
        this.healths = healths;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero hero = world.getHero(heroId);
        //super.actionTurn(world);
        Ability offensiveAbility = hero.getOffensiveAbilities()[0];
        Ability attackAbility = offensiveAbility;
        Cell targetCell1 = getCellWithMostJoneKamShodeForNotLinearAbilities(world, hero.getCurrentCell(), attackAbility.getName(), healths);
        if (targetCell1 != null)
        {
            try{
                castAbility(world, hero, targetCell1, attackAbility.getName());
                ArrayList<Integer> ids = new ArrayList<>();
                getJoneKamOfOppHeroesInRange(world, targetCell1, offensiveAbility.getRange(),
                        offensiveAbility.getName(),healths, ids);
                for (Integer id : ids)
                    healths[id] = Math.max(0, healths[id] - offensiveAbility.getPower());
            }catch (Exception ignored) {}
        }
    }
}
