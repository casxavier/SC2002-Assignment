package item;

import combatant.Player;
import status.SmokeBombEffect;

public class SmokeBomb extends Item {

    public SmokeBomb() {
        super("SmokeBomb");
    }

    @Override
    public String use(Player user)  {
        user.removeStatusEffect(SmokeBombEffect.class);
        user.addStatusEffect(new SmokeBombEffect());
        return user.getName() + " is protected by smoke (2 turns)";
    }
}
