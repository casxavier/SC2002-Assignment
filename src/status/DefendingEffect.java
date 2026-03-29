package status;

import combatant.Combatant;

/**
 * Defend action buff: +10 defense for the current round and the next round.
 * Modeled as two decrements on the defender's {@code onTurnStart} (after the
 * turn Defend is used, the next two defender turn starts tick the counter down).
 */
public class DefendingEffect implements StatusEffect {
    private int roundsRemaining;

    public DefendingEffect() {
        this.roundsRemaining = 2;
    }

    @Override
    public String getName() {
        return "Defend";
    }

    @Override
    public int getDefenseModifier() {
        return roundsRemaining > 0 ? 10 : 0;
    }

    @Override
    public void onTurnStart(Combatant target) {
        if (roundsRemaining > 0) {
            roundsRemaining--;
        }
    }

    @Override
    public boolean isExpired() {
        return roundsRemaining <= 0;
    }
}
