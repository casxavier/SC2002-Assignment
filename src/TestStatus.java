import combatant.Wizard;
import combatant.Warrior;
import combatant.Goblin;
import status.StatusEffect;
import status.Stun;
import status.ArcaneBlastEffect;

public class TestStatus {

    public static void main(String[] args) {
        System.out.println("=== Status Effect Tests ===\n");

        testStunEffect();
        testArcaneBlastEffect();
        testMultipleWizards();
        testAttackModifiers();
        testStatusCleanup();

        System.out.println("\n=== All tests completed ===");
    }

    private static void testStunEffect() {
        System.out.println("TEST 1: Stun Effect");
        System.out.println("-----------------");

        Warrior warrior = new Warrior("TestWarrior");
        Stun stun = new Stun();

        System.out.println("Initial state:");
        System.out.println("  Can act: " + warrior.canAct());
        System.out.println("  Stun remaining turns: " + stun.getRemainingTurns());

        warrior.addStatusEffect(stun);
        System.out.println("\nAfter adding Stun:");
        System.out.println("  Can act: " + warrior.canAct());
        System.out.println("  Stun remaining turns: " + stun.getRemainingTurns());

        System.out.println("\nTurn 1 start:");
        warrior.onTurnStart();
        System.out.println("  Can act: " + warrior.canAct());
        System.out.println("  Stun remaining turns: " + stun.getRemainingTurns());

        System.out.println("\nTurn 2 start:");
        warrior.onTurnStart();
        System.out.println("  Can act: " + warrior.canAct());
        System.out.println("  Stun remaining turns: " + stun.getRemainingTurns());

        System.out.println("\nTurn 3 start (should be expired):");
        warrior.onTurnStart();
        System.out.println("  Can act: " + warrior.canAct());
        System.out.println("  Stun expired: " + stun.isExpired());
        System.out.println("  Status effects count: " + warrior.getStatusEffects().size());

        System.out.println();
    }

    private static void testArcaneBlastEffect() {
        System.out.println("TEST 2: Arcane Blast Effect");
        System.out.println("---------------------------");

        Wizard wizard = new Wizard("TestWizard");

        System.out.println("Initial state:");
        System.out.println("  Base attack: " + wizard.getBaseAttack());
        System.out.println("  Actual attack: " + wizard.getAttack());
        System.out.println("  Arcane Blast bonus: " + wizard.getArcaneBlastBonus());

        System.out.println("\nAfter defeating 2 enemies with Arcane Blast:");
        wizard.registerArcaneBlastDefeats(2);
        System.out.println("  Base attack: " + wizard.getBaseAttack());
        System.out.println("  Actual attack: " + wizard.getAttack());
        System.out.println("  Arcane Blast bonus: " + wizard.getArcaneBlastBonus());

        System.out.println("\nAfter defeating 3 more enemies:");
        wizard.registerArcaneBlastDefeats(3);
        System.out.println("  Base attack: " + wizard.getBaseAttack());
        System.out.println("  Actual attack: " + wizard.getAttack());
        System.out.println("  Arcane Blast bonus: " + wizard.getArcaneBlastBonus());

        System.out.println("\nAfter level end reset:");
        wizard.resetArcaneBlastBonusAtLevelEnd();
        System.out.println("  Base attack: " + wizard.getBaseAttack());
        System.out.println("  Actual attack: " + wizard.getAttack());
        System.out.println("  Arcane Blast bonus: " + wizard.getArcaneBlastBonus());

        System.out.println();
    }

    private static void testMultipleWizards() {
        System.out.println("TEST 3: Multiple Wizards Have Separate Effects");
        System.out.println("---------------------------------------------");

        Wizard wizard1 = new Wizard("Wizard1");
        Wizard wizard2 = new Wizard("Wizard2");

        System.out.println("Initial state:");
        System.out.println("  Wizard1 attack: " + wizard1.getAttack());
        System.out.println("  Wizard2 attack: " + wizard2.getAttack());

        System.out.println("\nWizard1 defeats 3 enemies, Wizard2 defeats 1:");
        wizard1.registerArcaneBlastDefeats(3);
        wizard2.registerArcaneBlastDefeats(1);

        System.out.println("  Wizard1 attack: " + wizard1.getAttack() + " (bonus: " + wizard1.getArcaneBlastBonus() + ")");
        System.out.println("  Wizard2 attack: " + wizard2.getAttack() + " (bonus: " + wizard2.getArcaneBlastBonus() + ")");

        System.out.println("\nWizard1 defeats 2 more:");
        wizard1.registerArcaneBlastDefeats(2);
        System.out.println("  Wizard1 attack: " + wizard1.getAttack() + " (bonus: " + wizard1.getArcaneBlastBonus() + ")");
        System.out.println("  Wizard2 attack: " + wizard2.getAttack() + " (bonus: " + wizard2.getArcaneBlastBonus() + ")");

        System.out.println("\nWizard1 resets at level end:");
        wizard1.resetArcaneBlastBonusAtLevelEnd();
        System.out.println("  Wizard1 attack: " + wizard1.getAttack() + " (bonus: " + wizard1.getArcaneBlastBonus() + ")");
        System.out.println("  Wizard2 attack: " + wizard2.getAttack() + " (bonus: " + wizard2.getArcaneBlastBonus() + ")");

        System.out.println();
    }

    private static void testAttackModifiers() {
        System.out.println("TEST 4: Attack Modifiers Stack Correctly");
        System.out.println("----------------------------------------");

        Wizard wizard = new Wizard("TestWizard");

        System.out.println("Initial attack: " + wizard.getAttack());

        System.out.println("\nAdding 5 Arcane Blast kills (50 attack):");
        wizard.registerArcaneBlastDefeats(5);
        System.out.println("  Attack: " + wizard.getAttack());

        // Note: Stun doesn't affect attack, only action blocking
        System.out.println("\nAdding Stun (shouldn't affect attack):");
        Stun stun = new Stun();
        wizard.addStatusEffect(stun);
        System.out.println("  Attack: " + wizard.getAttack());
        System.out.println("  Can act: " + wizard.canAct());

        System.out.println();
    }

    private static void testStatusCleanup() {
        System.out.println("TEST 5: Status Effect Cleanup");
        System.out.println("-----------------------------");

        Warrior warrior = new Warrior("TestWarrior");

        System.out.println("Initial status effects: " + warrior.getStatusEffects().size());

        Stun stun = new Stun();
        warrior.addStatusEffect(stun);
        System.out.println("After adding Stun: " + warrior.getStatusEffects().size());

        System.out.println("\nProcessing 3 turns to expire Stun:");
        warrior.onTurnStart();
        System.out.println("  Turn 1 - Effects: " + warrior.getStatusEffects().size() + ", Stun expired: " + stun.isExpired());

        warrior.onTurnStart();
        System.out.println("  Turn 2 - Effects: " + warrior.getStatusEffects().size() + ", Stun expired: " + stun.isExpired());

        warrior.onTurnStart();
        System.out.println("  Turn 3 - Effects: " + warrior.getStatusEffects().size() + ", Stun expired: " + stun.isExpired());

        System.out.println("\nChecking hasStatusEffect:");
        System.out.println("  Has Stun: " + warrior.hasStatusEffect(Stun.class));

        System.out.println();
    }
}
