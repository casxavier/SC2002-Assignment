package item;

import combatant.Player;

public class SmokeBomb extends Item {

    public SmokeBomb() {
        super("SmokeBomb");
    }

    @Override
    public String use(Player target) {
        target.applySmokeBomb();
        return target.getName() + " is protected by smoke (2 turns)";
    }
}
