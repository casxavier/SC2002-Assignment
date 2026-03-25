package status;

import combatant.Combatant;

/**
 * Arcane Blast temporary attack-bonus effect.
 *
 * Each enemy defeated by Arcane Blast grants +10 attack to the Wizard.
 * The accumulated bonus lasts until the end of the level.
 */
public class ArcaneBlastEffect implements StatusEffect {
    private static final int BONUS_PER_KILL = 10;
    private int totalBonus;

    public ArcaneBlastEffect() {
        this.totalBonus = 0;
    }

    /**
     * Adds bonus based on number of enemies defeated by Arcane Blast.
     *
     * @param kills number of enemies defeated by one Arcane Blast usage
     */
    public void addKills(int kills) {
        if (kills <= 0) {
            return;
        }
        totalBonus += kills * BONUS_PER_KILL;
    }

    /**
     * @return current accumulated attack bonus
     */
    public int getTotalBonus() {
        return totalBonus;
    }

    /**
     * Resets bonus (typically called at end of level).
     */
    public void clear() {
        totalBonus = 0;
    }

    @Override
    public String getName() {
        return "Arcane Blast";
    }

    @Override
    public int getAttackModifier() {
        return totalBonus;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    /**
     * Retrieves an existing {@code ArcaneBlastEffect} from the given combatant,
     * or returns {@code null} if none is present.
     *
     * @param target the combatant to inspect
     * @return the existing effect, or {@code null}
     */
    public static ArcaneBlastEffect getOn(Combatant target) {
        for (StatusEffect effect : target.getStatusEffects()) {
            if (effect instanceof ArcaneBlastEffect) {
                return (ArcaneBlastEffect) effect;
            }
        }
        return null;
    }
}
