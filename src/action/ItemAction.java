package action;

import combatant.Combatant;
import combatant.Player;
import item.Item;

public class ItemAction extends Action {

    private final Player player;
    private final int itemIndex;
    
    private final Combatant target;

    public ItemAction(Player actor, int itemIndex, Combatant target) {
        super(actor);
        this.player = actor;
        this.itemIndex = itemIndex;
        this.target = target;
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute() || itemIndex < 0 || itemIndex >= player.getInventory().size()) {
            return false;
        }
        Item item = player.getInventory().get(itemIndex);
        // Only validate target if item actually needs one
        if (item.requiresTarget(player)) {
            return target != null && target.isAlive();
        }
        return true;
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        if (itemIndex < 0 || itemIndex >= player.getInventory().size()) {
            return ActionResult.fail("Invalid item selection.");
        }
        Item item = player.getInventory().get(itemIndex);
        String msg;
        if (item.requiresTarget(player)) {
            msg = item.useWithTarget(player, ctx, target);
        }
        else {
            msg = item.use(player, ctx);
        }
        player.removeItem(item);
        return ActionResult.ok(msg);
    }
}
