package com.aaroncarsonart.quickgame2.hero;

import com.aaroncarsonart.quickgame2.Constants;

public class HeroCreator {

    public static Hero createDefaultHero() {
        Hero hero = new Hero();
        hero.setName("James");
        hero.setHeroClass(HeroClass.FIGHTER);
        hero.setHeroRace(HeroRace.HUMAN);
        hero.setLevel(1);
        hero.setExp(0);

        int health = 10 + Constants.RNG.nextInt(10);
        hero.setHealth(health);
        hero.setMaxHealth(health);

        int mana = 5 + Constants.RNG.nextInt(5);
        hero.setMana(mana);
        hero.setMaxMana(mana);

        int energy = 100;
        hero.setEnergy(energy);
        hero.setMaxEnergy(energy);

        hero.setStrength(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));
        hero.setStamina(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));
        hero.setAgility(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));
        hero.setIntelligence(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));
        hero.setWisdom(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));
        hero.setCharisma(Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6)
                + Constants.RNG.nextInt(6));

        hero.setGold(0);

        return hero;
    }
}
