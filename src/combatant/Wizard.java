package combatant;

import status.ArcaneBlastEffect;

public class Wizard extends Player {

    public Wizard(String name) {
        super(name, 200, 50, 10, 20);
    }

    /**
     * Registers enemies defeated by Arcane Blast and stacks the temporary
     * attack bonus (+10 per defeated enemy).
     *
     * @param enemiesDefeated number of enemies defeated by one Arcane Blast usage
     */
    public void registerArcaneBlastDefeats(int enemiesDefeated) {
        if (enemiesDefeated <= 0) {
            return;
        }
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        if (effect == null) {
            effect = new ArcaneBlastEffect();
            addStatusEffect(effect);
        }
        effect.addKills(enemiesDefeated);
    }

    /**
     * @return current Arcane Blast temporary attack bonus for this level.
     */
    public int getArcaneBlastBonus() {
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        return effect != null ? effect.getTotalBonus() : 0;
    }

    /**
     * Clears Arcane Blast temporary attack bonus at level end.
     */
    public void resetArcaneBlastBonus() {
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        if (effect != null) {
            effect.clear();
        }
    }
}
