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

    default int modifyDamage(int incomingDamage) {
        return incomingDamage;
    }

    boolean isExpired();
}
