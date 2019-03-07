package client.Strategy;

import client.model.*;

import java.util.ArrayList;

public class BBBBStrategy extends Strategy {
    private static int cnt = 0;

    @Override
    public void pickTurn(World world) {
        System.out.println(world.getCurrentTurn());
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.BLASTER);
                break;
            case 1:
                world.pickHero(HeroName.BLASTER);
                break;
            case 2:
                world.pickHero(HeroName.BLASTER);
                break;
            case 3:
                world.pickHero(HeroName.BLASTER);
                break;
        }
        cnt++;
    }

    @Override
    public void preProcess(World world) {

    }

    @Override
    public void moveTurn(World world) {

    }


    @Override
    public void actionTurn(World world) {
        dodge(world);
        blastersBombAttacks(world);
        blastersAttacks(world);
    }

    void blastersBombAttacks(World world) {
        for (Hero hero : world.getMyHeroes()) {
            blastersBombAttack(world, hero);
        }
    }

    void blastersAttacks(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            heroAttack(world,hero);
        }
    }

    void heroAttack(World world, Hero hero) {

    }


}
