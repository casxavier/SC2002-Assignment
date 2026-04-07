package item;

import combatant.Player;

public class SmokeBomb extends Item {

    public SmokeBomb() {
        super("SmokeBomb");
    }

    @Override
    public String use(Player user)  {
        user.applySmokeBomb();
        return user.getName() + " is protected by smoke (2 turns)";
    }
}
