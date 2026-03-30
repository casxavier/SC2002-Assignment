import action.BattleContext;
import action.BasicAttackAction;
import action.ShieldBashSkill;
import combatant.Goblin;
import combatant.Warrior;
import item.PowerStone;
import status.DefendingEffect;
import status.Stun;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual Appendix A–style checks (run with {@code java -ea AppendixVerification}).
 */
public final class AppendixVerification {

    private AppendixVerification() {}

    public static void main(String[] args) {
        verifyBasicAttackEasyWarriorRound1();
        verifyDefendRaisesEffectiveDefense();
        verifyStunSkipsTwoTurns();
        verifyPowerStoneLeavesCooldown();
        System.out.println("AppendixVerification: all checks passed.");
    }

    private static void verifyBasicAttackEasyWarriorRound1() {
        Warrior w = new Warrior("Warrior");
        Goblin g = new Goblin();
        assert g.getHp() == 55;
        BattleContext ctx = new BattleContext(w, new ArrayList<>(List.of(g)));
        new BasicAttackAction(w, g).execute(ctx);
        assert g.getHp() == 30 : "Expected 55 - 25 = 30, got " + g.getHp();
    }

    private static void verifyDefendRaisesEffectiveDefense() {
        Warrior w = new Warrior("W");
        assert w.getEffectiveDefense() == 20;
        w.addStatusEffect(new DefendingEffect());
        assert w.getEffectiveDefense() == 30;
    }

    private static void verifyStunSkipsTwoTurns() {
        Goblin g = new Goblin();
        g.addStatusEffect(new Stun());
        assert !g.canAct();
        g.onTurnStart();
        assert !g.canAct();
        g.onTurnStart();
        assert !g.canAct();
        g.onTurnStart();
        assert g.canAct();
    }

    /** Medium appendix: with cooldown already 3, Power Stone + Shield Bash must not lower cooldown. */
    private static void verifyPowerStoneLeavesCooldown() {
        Warrior w = new Warrior("Warrior");
        Goblin g = new Goblin();
        ShieldBashSkill.INSTANCE.execute(w, new BattleContext(w, new ArrayList<>(List.of(g))), g);
        assert w.getSpecialSkillCooldown() == 3;
        new PowerStone().use(w);
        ShieldBashSkill.INSTANCE.execute(w, new BattleContext(w, new ArrayList<>(List.of(g))), g);
        assert w.getSpecialSkillCooldown() == 3;
        assert !w.hasPowerStoneCharge();
    }
}
