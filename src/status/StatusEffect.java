package status;

import combatant.Combatant;

/**
 * Core interface for all status effects.
 */
public interface StatusEffect {

    /**
     * A stable identifier/name for this status effect (e.g. "Stun").
     */
    String getName();

    default void onApply(Combatant target) {}
    default void onTurnStart(Combatant target) {}
    default void onRemove(Combatant target) {}
    /**
     * Whether this effect prevents the target from taking actions.
     */
    default boolean canAct() {
        return true;
    }

    /**
     * Attack modifier contributed by this effect.
     */
    default int getAttackModifier() {
        return 0;
    }

    /**
     * Whether the effect has completed and should be removed.
     */
    boolean isExpired();
}
