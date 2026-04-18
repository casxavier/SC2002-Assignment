package status;

import combatant.Combatant;


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
        // Defense applied before onTurnStart decrement, so bonus active for 2 full turns
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

    public int getRemainingTurns() {
        return Math.max(0, roundsRemaining);
    }
}
