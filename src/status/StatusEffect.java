package status;

import combatant.Combatant;


public interface StatusEffect {


    String getName();

    default void onApply(Combatant target) {}
    default void onTurnStart(Combatant target) {}
    default void onRemove(Combatant target) {}

    default boolean canAct() {
        return true;
    }


    default int getAttackModifier() {
        return 0;
    }

    default int getDefenseModifier() {
        return 0;
    }

    default int getRemainingTurns() {
        return 0;
    }

    // Allow effects to modify incoming damage
    default int modifyDamage(int incomingDamage) {
        return incomingDamage;
    }

    // Decide whether this effect should be displayed in UI
    default boolean shouldDisplay() {
        return getRemainingTurns() > 0;
    }

    boolean isExpired();
}
