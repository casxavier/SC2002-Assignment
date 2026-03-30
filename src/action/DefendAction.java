package action;

import combatant.Player;
import status.DefendingEffect;

public class DefendAction extends Action {

    public DefendAction(Player actor) {
        super(actor);
    }

    @Override
    public boolean canExecute() {
        return actor instanceof Player && super.canExecute();
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        Player player = (Player) actor;
        player.addStatusEffect(new DefendingEffect());
        return ActionResult.ok(player.getName() + " guards (+10 Defense this round and next).");
    }
}
