package item;

import combatant.Player;
import action.BattleContext;
import status.SmokeBombEffect;

public class SmokeBomb extends Item {

    public SmokeBomb() {
        super("Smoke Bomb");
    }

    @Override
    public String use(Player user, BattleContext ctx)  {
        user.removeStatusEffect(SmokeBombEffect.class);
        user.addStatusEffect(new SmokeBombEffect());
        return user.getName() + " is protected by smoke (2 turns)";
    }

    @Override
    public Item duplicate() {
        return new SmokeBomb();
    }
}
