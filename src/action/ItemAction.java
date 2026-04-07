package action;

import combatant.Combatant;
import combatant.Player;
import combatant.Warrior;
import item.Item;
import item.PowerStone;

public class ItemAction extends Action {

    private final Player player;
    private final int itemIndex;
    
    private final Combatant powerStoneTarget;

    public ItemAction(Player actor, int itemIndex, Combatant powerStoneTarget) {
        super(actor);
        this.player = actor;
        this.itemIndex = itemIndex;
        this.powerStoneTarget = powerStoneTarget;
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute() || itemIndex < 0 || itemIndex >= player.getInventory().size()) {
            return false;
        }
        Item item = player.getInventory().get(itemIndex);
        if (item instanceof PowerStone && player instanceof Warrior) {
            return powerStoneTarget != null && powerStoneTarget.isAlive();
        }
        return true;
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        if (itemIndex < 0 || itemIndex >= player.getInventory().size()) {
            return ActionResult.fail("Invalid item selection.");
        }
        Item item = player.getInventory().get(itemIndex);
        String msg = item.use(player);
        if (item instanceof PowerStone) {
            msg = item.useWithTarget(player, ctx, powerStoneTarget);
        }
        player.removeItem(item);
        return ActionResult.ok(msg);
    }
}
