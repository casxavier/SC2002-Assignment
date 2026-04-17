package action;

import combatant.Combatant;
import combatant.Player;


public interface SpecialSkill {

    boolean canUse(Player player);

    boolean requiresTarget();

    
    ActionResult execute(Player player, BattleContext ctx, Combatant singleTarget);
}
