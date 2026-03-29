package action;

import combatant.Combatant;
import combatant.Player;
import combatant.Warrior;
import item.Item;
import item.PowerStone;

public class ItemAction extends Action {

    private final Player player;
    private final int itemIndex;
    /** Required when using {@link PowerStone} with {@link Warrior}; ignored for Wizard blast. */
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
        if (item instanceof PowerStone && player instanceof Warrior
                && (powerStoneTarget == null || !powerStoneTarget.isAlive())) {
            return ActionResult.fail("Power Stone requires a living enemy target.");
        }
        String msg = item.use(player);
        if (item instanceof PowerStone) {
            ActionResult skillResult = PlayerSpecialSkills.forPlayer(player).execute(player, ctx, powerStoneTarget);
            if (!skillResult.isSuccess()) {
                return ActionResult.fail(msg + " " + skillResult.getMessage());
            }
            msg = msg + " " + skillResult.getMessage();
        }
        player.removeItem(item);
        return ActionResult.ok(msg);
    }
}
