package action;

import java.util.ArrayList;
import java.util.List;

import combatant.Combatant;
import combatant.Player;
import combatant.Wizard;
import status.ArcaneBlastEffect;
import status.StatusEffect;


public final class ArcaneBlastSkill implements SpecialSkill {

    public static final ArcaneBlastSkill INSTANCE = new ArcaneBlastSkill();

    private ArcaneBlastSkill() {}

    @Override
    public boolean canUse(Player player) {
        return player.canUseSpecialSkill();
    }

    @Override
    public boolean requiresTarget() {
        return false;
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
        // Snapshot to avoid issues if enemies die mid-loop
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
            detail.append(String.format(
                    "  %s took %d damage (%d - %d defense) (HP: %d).%n",
                    enemy.getName(),
                    dealt,
                    atk,
                    enemy.getDefense(),
                    enemy.getHp()));
            // Only count if this action killed them
            if (wasAlive && !enemy.isAlive()) {
                kills++;
            }
        }
        registerArcaneBlastDefeats(wizard, kills);
        wizard.consumeSpecialSkillUse();
        return ActionResult.ok(String.format(
                "%s used Arcane Blast (kills this blast: %d).%n%s",
                wizard.getName(), kills, detail));
    }

    
    // Track cumulative kills for arcane blast bonus effect
    public static void registerArcaneBlastDefeats(Wizard wizard, int enemiesDefeated) {
        if (enemiesDefeated <= 0) {
            return;
        }
        // Find or create effect to accumulate kills
        ArcaneBlastEffect effect = null;
        for (StatusEffect se : wizard.getStatusEffects()) {
            if (se instanceof ArcaneBlastEffect) {
                effect = (ArcaneBlastEffect) se;
                break;
            }
        }
        if (effect == null) {
            effect = new ArcaneBlastEffect();
            wizard.addStatusEffect(effect);
        }
        effect.addKills(enemiesDefeated);
    }
}
