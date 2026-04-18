package combatant;

import action.SpecialSkill;
import action.ShieldBashSkill;

public class Warrior extends Player {

    public Warrior(String name) {
        super(name, 260, 40, 20, 30);
    }

    @Override
    public SpecialSkill getSpecialSkill() {
        return ShieldBashSkill.INSTANCE;
    }
}
