package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.Ability;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;

public class ThirdLinearAttackStrategy extends PartOfStrategy {
    private int heroId;
    int[] healths;

    public ThirdLinearAttackStrategy(int hero, int[] healths) {
        this.heroId = hero;
        this.healths = healths;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero hero = world.getHero(heroId);
        //super.actionTurn(world);
        Ability attackAbility = hero.getOffensiveAbilities()[0];
        Cell targetCell1 = getCellWithMostKills(world, hero.getCurrentCell(), attackAbility.getName(), healths, true);
        if (targetCell1 != null)
        {
            try{
                castAbility(world, hero, targetCell1, attackAbility.getName());
                ArrayList<Integer> ids = new ArrayList<>();
                getKillsOfOppHeroesInRange(world, targetCell1, attackAbility.getRange(),
                        attackAbility.getName(),healths, ids);
                for (Integer id : ids)
                    healths[id] = Math.max(0, healths[id] - attackAbility.getPower());
            }catch (Exception ignored) {}
        }
    }
}
