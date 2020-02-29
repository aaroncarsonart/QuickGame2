package com.aaroncarsonart.quickgame2.monster;

import com.aaroncarsonart.quickgame2.Constants;

import java.util.Random;

public interface Battler {

    static Random rng = Constants.RNG;
    int getMaxHealth();
    int getHealth();
    void setHealth(int health);

    int getAttack();
    int getDefense();
    int getEvade();
    int getAccuracy();

    default int attack(Battler target) {
        int toHit = 1 + rng.nextInt(10);
        int toDodge = 1 + rng.nextInt(10);
        int accuracy = this.getAccuracy() + toHit;
        int evade = target.getEvade() + toDodge;
//        if (toDodge == 20) {
//            return 0;
//        }
        if (accuracy > evade || (1 + rng.nextInt(20)) == 20) {
            if (rng.nextBoolean()) {
                return - 1;
            }
            int baseAttack = 1 + rng.nextInt(this.getAttack());
            int baseDefense = 1 + rng.nextInt(target.getDefense());
            int damage = Math.max(1, baseAttack - baseDefense);
            int newHealth = Math.max(0, target.getHealth() - damage);
            target.setHealth(newHealth);
            return damage;
        }
        return 0;
    }

    default int getCriticalHitDamage(Battler target) {
        int baseAttack = 1 + rng.nextInt(this.getAttack()) + 1 + this.getAttack();
        int baseDefense = 1 + rng.nextInt(target.getDefense());
        int damage = Math.max(1, baseAttack - baseDefense);
        return damage;
    }
}
