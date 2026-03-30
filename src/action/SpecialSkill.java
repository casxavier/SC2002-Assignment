package action;

import combatant.Combatant;
import combatant.Player;

/**
 * Class-specific player special (Shield Bash / Arcane Blast).
 */
public interface SpecialSkill {

    boolean canUse(Player player);

    /**
     * @param singleTarget required for single-target skills; may be null for Wizard blast.
     */
    ActionResult execute(Player player, BattleContext ctx, Combatant singleTarget);
}
