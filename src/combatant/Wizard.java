package combatant;

import action.ArcaneBlastSkill;
import action.SpecialSkill;

public class Wizard extends Player {

    public Wizard(String name) {
        super(name, 200, 50, 10, 20);
    }

    public SpecialSkill getSpecialSkill() {
        return ArcaneBlastSkill.INSTANCE;
    }
}