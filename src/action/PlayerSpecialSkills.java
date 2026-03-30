package action;

import combatant.Player;
import combatant.Warrior;
import combatant.Wizard;

public final class PlayerSpecialSkills {

    private PlayerSpecialSkills() {}

    public static SpecialSkill forPlayer(Player player) {
        if (player instanceof Warrior) {
            return ShieldBashSkill.INSTANCE;
        }
        if (player instanceof Wizard) {
            return ArcaneBlastSkill.INSTANCE;
        }
        throw new IllegalArgumentException("Unsupported player type: " + player.getClass().getName());
    }
}
