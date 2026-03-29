package action;

import java.util.ArrayList;
import java.util.List;

import combatant.Combatant;
import combatant.Player;
import combatant.Wizard;

/**
 * Wizard special: attack all living enemies; kills grant +10 attack each until level end.
 */
public final class ArcaneBlastSkill implements SpecialSkill {

    public static final ArcaneBlastSkill INSTANCE = new ArcaneBlastSkill();

    private ArcaneBlastSkill() {}

    @Override
    public boolean canUse(Player player) {
        return player.canUseSpecialSkill();
    }

    @Override
    public ActionResult execute(Player player, BattleContext ctx, Combatant singleTarget) {
        if (!(player instanceof Wizard)) {
            return ActionResult.fail("Arcane Blast is only available to Wizards.");
        }
        if (!canUse(player)) {
            return ActionResult.fail(player.getName() + " cannot use Arcane Blast right now.");
        }
        Wizard wizard = (Wizard) player;
        List<Combatant> snapshot = new ArrayList<>(ctx.getEnemies());
        int kills = 0;
        StringBuilder detail = new StringBuilder();
        int atk = wizard.getAttack();
        for (Combatant enemy : snapshot) {
            if (!enemy.isAlive()) {
                continue;
            }
            boolean wasAlive = enemy.getHp() > 0;
            int dealt = enemy.takeDamage(atk);
            detail.append(String.format("  %s took %d damage (HP: %d).%n", enemy.getName(), dealt, enemy.getHp()));
            if (wasAlive && !enemy.isAlive()) {
                kills++;
            }
        }
        wizard.registerArcaneBlastDefeats(kills);
        wizard.consumeSpecialSkillUse();
        return ActionResult.ok(String.format(
                "%s used Arcane Blast (kills this blast: %d).%n%s",
                wizard.getName(), kills, detail));
    }
}
