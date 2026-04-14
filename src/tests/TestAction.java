package tests;

import java.util.ArrayList;
import java.util.List;

import action.*;
import combatant.*;
import item.*;
import status.*;








public class TestAction {

    

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String label, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + label);
            passed++;
        } else {
            System.out.println("  [FAIL] " + label);
            failed++;
        }
    }

    private static BattleContext ctx(Player player, Combatant... enemies) {
        List<Combatant> list = new ArrayList<>();
        for (Combatant e : enemies) list.add(e);
        return new BattleContext(player, list);
    }

    

    public static void main(String[] args) {
        testActionResult();
        testBattleContext();
        testBasicAttackAction();
        testDefendAction();
        testItemAction_Potion();
        testItemAction_SmokeBomb();
        testItemAction_PowerStoneWarrior();
        testItemAction_PowerStoneWizard();
        testItemAction_InvalidIndex();
        testItemAction_BlockedWhenDead();
        testSpecialSkillAction_ShieldBash();
        testSpecialSkillAction_ArcaneBlast();
        testSpecialSkillAction_Cooldown();
        testSpecialSkillAction_BlockedByStun();
        testSpecialSkillAction_BlockedReason();
        testPlayerSpecialSkills_forPlayer();
        testShieldBashSkillDirect();
        testArcaneBlastSkillDirect();

        System.out.println("\n==============================");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("==============================");
    }

    

    private static void testActionResult() {
        System.out.println("\n=== ActionResult ===");

        ActionResult ok = ActionResult.ok("All good");
        check("ok() isSuccess()==true",  ok.isSuccess());
        check("ok() getMessage()==msg",   "All good".equals(ok.getMessage()));

        ActionResult fail = ActionResult.fail("Something broke");
        check("fail() isSuccess()==false", !fail.isSuccess());
        check("fail() getMessage()==msg",  "Something broke".equals(fail.getMessage()));

        ActionResult nullMsg = ActionResult.ok(null);
        check("null message -> empty string", "".equals(nullMsg.getMessage()));
    }

    

    private static void testBattleContext() {
        System.out.println("\n=== BattleContext ===");

        Warrior warrior = new Warrior("CtxWarrior");
        Goblin g1 = new Goblin();
        Goblin g2 = new Goblin();
        BattleContext ctx = ctx(warrior, g1, g2);

        check("getPlayer() returns correct player", ctx.getPlayer() == warrior);
        check("getEnemies() size == 2",             ctx.getEnemies().size() == 2);
        check("getEnemies() contains g1",           ctx.getEnemies().contains(g1));
        check("getEnemies() contains g2",           ctx.getEnemies().contains(g2));
    }

    

    private static void testBasicAttackAction() {
        System.out.println("\n=== BasicAttackAction ===");

        
        Warrior warrior = new Warrior("Attacker");
        Goblin goblin   = new Goblin();
        BattleContext ctx = ctx(warrior, goblin);

        BasicAttackAction atk = new BasicAttackAction(warrior, goblin);
        check("canExecute() true when both alive", atk.canExecute());
        check("blockedReason() empty when ok",     atk.blockedReason().isEmpty());

        ActionResult result = atk.execute(ctx);
        check("execute() success",                  result.isSuccess());
        check("Goblin HP after attack == 30",       goblin.getHp() == 30);
        check("Result message mentions damage",     result.getMessage().contains("25"));

        
        Warrior deadWarrior = new Warrior("Dead");
        deadWarrior.takeDamage(9999);
        BasicAttackAction deadActorAtk = new BasicAttackAction(deadWarrior, goblin);
        check("canExecute() false when actor dead",   !deadActorAtk.canExecute());
        check("blockedReason() mentions actor name",  deadActorAtk.blockedReason().contains("Dead"));

        
        Goblin deadGoblin = new Goblin();
        deadGoblin.takeDamage(9999);
        BasicAttackAction deadTargetAtk = new BasicAttackAction(warrior, deadGoblin);
        check("canExecute() false when target dead",  !deadTargetAtk.canExecute());
        check("blockedReason() mentions no valid target",
              deadTargetAtk.blockedReason().contains("No valid target"));

        
        Warrior stunned = new Warrior("Stunned");
        stunned.addStatusEffect(new Stun());
        BasicAttackAction stunnedAtk = new BasicAttackAction(stunned, goblin);
        check("canExecute() false when actor stunned", !stunnedAtk.canExecute());
    }

    

    private static void testDefendAction() {
        System.out.println("\n=== DefendAction ===");

        Warrior warrior = new Warrior("Defender");
        BattleContext ctx = ctx(warrior);

        int defBefore = warrior.getEffectiveDefense(); 
        check("Base effective defense == 20", defBefore == 20);

        DefendAction defend = new DefendAction(warrior);
        check("canExecute() true for alive player", defend.canExecute());

        ActionResult result = defend.execute(ctx);
        check("execute() success",                        result.isSuccess());
        check("Result message mentions guard",            result.getMessage().contains("guards"));
        check("Effective defense after Defend == 30",     warrior.getEffectiveDefense() == 30);
        check("Has DefendingEffect status",               warrior.hasStatusEffect(DefendingEffect.class));

        
        warrior.onTurnStart(); 
        check("Defense still +10 after 1st turn-start",  warrior.getEffectiveDefense() == 30);
        warrior.onTurnStart(); 
        check("Defense back to 20 after effect expires",  warrior.getEffectiveDefense() == 20);
    }

    

    private static void testItemAction_Potion() {
        System.out.println("\n=== ItemAction – Potion ===");

        Warrior warrior = new Warrior("PotionUser");
        warrior.takeDamage(150);                        
        int hpBeforeHeal = warrior.getHp();
        warrior.addItem(new Potion());
        BattleContext ctx = ctx(warrior);

        ItemAction action = new ItemAction(warrior, 0, null);
        check("canExecute() true",   action.canExecute());

        ActionResult result = action.execute(ctx);
        check("execute() success",                result.isSuccess());
        check("HP increased after potion",        warrior.getHp() > hpBeforeHeal);
        check("Potion removed from inventory",    warrior.getInventory().isEmpty());
        check("Message contains healed",          result.getMessage().toLowerCase().contains("heal"));
    }

    

    private static void testItemAction_SmokeBomb() {
        System.out.println("\n=== ItemAction – SmokeBomb ===");

        Warrior warrior = new Warrior("SmokeUser");
        warrior.addItem(new SmokeBomb());
        BattleContext ctx = ctx(warrior);

        check("Smoke not active before use",  !warrior.isSmokeActive());

        ItemAction action = new ItemAction(warrior, 0, null);
        ActionResult result = action.execute(ctx);
        check("execute() success",            result.isSuccess());
        check("Smoke is now active",          warrior.isSmokeActive());
        check("SmokeTurns == 2",              warrior.getSmokeTurns() == 2);
        check("Damage blocked while smoked",  warrior.takeDamage(9999) == 0);
        check("SmokeBomb removed from inv",   warrior.getInventory().isEmpty());

        warrior.onTurnStart();
        check("Smoke still active next turn", warrior.isSmokeActive());
        check("SmokeTurns == 1",              warrior.getSmokeTurns() == 1);
        check("Damage blocked on next turn",  warrior.takeDamage(9999) == 0);

        warrior.onTurnStart();
        check("Smoke expires after protected turns", !warrior.isSmokeActive());
        warrior.takeDamage(9999);
        check("Damage after smoke expires",   warrior.getHp() == 0);
    }

    

    private static void testItemAction_PowerStoneWarrior() {
        System.out.println("\n=== ItemAction – PowerStone (Warrior) ===");

        Warrior warrior = new Warrior("PSWarrior");
        warrior.consumeSpecialSkillUse();
        check("Pre-condition: special on cooldown", !warrior.canUseSpecialSkill());

        Goblin goblin = new Goblin();
        warrior.addItem(new PowerStone());
        BattleContext ctx = ctx(warrior, goblin);

        
        ItemAction noTarget = new ItemAction(warrior, 0, null);
        check("canExecute() false when Warrior+PS target==null", !noTarget.canExecute());

        
        ItemAction withTarget = new ItemAction(warrior, 0, goblin);
        check("canExecute() true with living target", withTarget.canExecute());

        ActionResult result = withTarget.execute(ctx);
        check("execute() success",                  result.isSuccess());
        check("Goblin damaged (HP < 55)",           goblin.getHp() < 55);
        check("Goblin has Stun after Shield Bash",  goblin.hasStatusEffect(Stun.class));
        check("PowerStone removed from inventory",  warrior.getInventory().isEmpty());
        check("Cooldown still 2 (stone charge used)", warrior.getSpecialSkillCooldown() == 2);
    }

    

    private static void testItemAction_PowerStoneWizard() {
        System.out.println("\n=== ItemAction – PowerStone (Wizard) ===");

        Wizard wizard = new Wizard("PSWizard");
        wizard.consumeSpecialSkillUse();
        check("Pre-condition: special on cooldown", !wizard.canUseSpecialSkill());

        Goblin g1 = new Goblin();
        Goblin g2 = new Goblin();
        wizard.addItem(new PowerStone());
        BattleContext ctx = ctx(wizard, g1, g2);

        
        ItemAction action = new ItemAction(wizard, 0, null);
        check("canExecute() true for Wizard without target", action.canExecute());

        ActionResult result = action.execute(ctx);
        System.out.println("execute() result: " + result.getMessage());
        check("execute() success",       result.isSuccess());
        check("Goblin 1 damaged",        g1.getHp() < 55);
        check("Goblin 2 damaged",        g2.getHp() < 55);
        check("PowerStone removed",      wizard.getInventory().isEmpty());
    }

    

    private static void testItemAction_InvalidIndex() {
        System.out.println("\n=== ItemAction – Invalid Index ===");

        Warrior warrior = new Warrior("InvUser");
        BattleContext ctx = ctx(warrior);

        ItemAction neg = new ItemAction(warrior, -1, null);
        check("canExecute() false for index -1", !neg.canExecute());

        ItemAction outOfRange = new ItemAction(warrior, 5, null);
        check("canExecute() false for index 5 (empty inv)", !outOfRange.canExecute());

        ActionResult result = outOfRange.execute(ctx);
        check("execute() fails gracefully", !result.isSuccess());
        check("Fail message mentions invalid", result.getMessage().contains("Invalid"));
    }

    

    private static void testItemAction_BlockedWhenDead() {
        System.out.println("\n=== ItemAction – Blocked When Dead ===");

        Warrior warrior = new Warrior("DeadUser");
        warrior.addItem(new Potion());
        warrior.takeDamage(9999);
        BattleContext ctx = ctx(warrior);

        ItemAction action = new ItemAction(warrior, 0, null);
        check("canExecute() false when actor dead", !action.canExecute());
    }

    

    private static void testSpecialSkillAction_ShieldBash() {
        System.out.println("\n=== SpecialSkillAction – ShieldBash ===");

        Warrior warrior = new Warrior("ShieldWarrior");
        Goblin goblin = new Goblin();
        BattleContext ctx = ctx(warrior, goblin);

        SpecialSkillAction ssa = new SpecialSkillAction(warrior, goblin);
        check("canExecute() true initially",    ssa.canExecute());
        check("blockedReason() empty",          ssa.blockedReason().isEmpty());

        ActionResult result = ssa.execute(ctx);
        check("execute() success",              result.isSuccess());
        check("Goblin HP reduced",              goblin.getHp() < 55);
        check("Goblin has Stun",                goblin.hasStatusEffect(Stun.class));
        check("Message mentions Shield Bash",   result.getMessage().contains("Shield Bash"));
        check("Warrior cooldown set to 2",      warrior.getSpecialSkillCooldown() == 2);

        
        SpecialSkillAction cooldownSsa = new SpecialSkillAction(warrior, goblin);
        check("canExecute() false while on cooldown", !cooldownSsa.canExecute());
    }

    

    private static void testSpecialSkillAction_ArcaneBlast() {
        System.out.println("\n=== SpecialSkillAction – ArcaneBlast ===");

        Wizard wizard = new Wizard("BlastWizard");
        Goblin g1 = new Goblin(); 
        Goblin g2 = new Goblin();
        Wolf   wolf = new Wolf(); 
        BattleContext ctx = ctx(wizard, g1, g2, wolf);

        SpecialSkillAction ssa = new SpecialSkillAction(wizard, null); 
        check("canExecute() true for Wizard w/o target", ssa.canExecute());

        ActionResult result = ssa.execute(ctx);
        check("execute() success",                  result.isSuccess());
        check("Goblin 1 damaged (HP = 20)",         g1.getHp() == 20);
        check("Goblin 2 damaged (HP = 20)",         g2.getHp() == 20);
        check("Wolf defeated (50 - 5 = 45 ≥ 40)",  !wolf.isAlive());
        check("Message mentions Arcane Blast",      result.getMessage().contains("Arcane Blast"));
        check("Kill count == 1 in message",         result.getMessage().contains("1"));
        check("Wizard cooldown set to 2",           wizard.getSpecialSkillCooldown() == 2);
        check("Wizard effective attack == 60",      wizard.getAttack() == 60);
    }

    

    private static void testSpecialSkillAction_Cooldown() {
        System.out.println("\n=== SpecialSkillAction – Cooldown Ticks ===");

        Warrior warrior = new Warrior("CDWarrior");
        Goblin goblin = new Goblin();
        BattleContext ctx = ctx(warrior, goblin);

        new SpecialSkillAction(warrior, goblin).execute(ctx);
        check("Cooldown == 2 after first use", warrior.getSpecialSkillCooldown() == 2);

        warrior.onTurnStart();
        check("Cooldown == 1 after 1 turn",    warrior.getSpecialSkillCooldown() == 1);
        warrior.onTurnStart();
        check("Cooldown == 0 after 2 turns",   warrior.getSpecialSkillCooldown() == 0);

        Goblin freshGoblin = new Goblin();
        SpecialSkillAction readySsa = new SpecialSkillAction(warrior, freshGoblin);
        check("canExecute() true again after cooldown expires", readySsa.canExecute());
    }

    

    private static void testSpecialSkillAction_BlockedByStun() {
        System.out.println("\n=== SpecialSkillAction – Blocked By Stun ===");

        Warrior warrior = new Warrior("StunWarrior");
        warrior.addStatusEffect(new Stun());
        Goblin goblin = new Goblin();

        SpecialSkillAction ssa = new SpecialSkillAction(warrior, goblin);
        check("canExecute() false when stunned", !ssa.canExecute());
    }

    

    private static void testSpecialSkillAction_BlockedReason() {
        System.out.println("\n=== SpecialSkillAction – blockedReason ===");

        Warrior warrior = new Warrior("BRWarrior");
        warrior.consumeSpecialSkillUse();
        Goblin goblin = new Goblin();
        SpecialSkillAction onCooldown = new SpecialSkillAction(warrior, goblin);
        String reason = onCooldown.blockedReason();
        check("blockedReason contains 'cooldown' or player name",
              reason.contains("cooldown") || reason.contains("BRWarrior"));

        
        Warrior warrior2 = new Warrior("BRWarrior2");
        Goblin deadGoblin = new Goblin();
        deadGoblin.takeDamage(9999);
        SpecialSkillAction deadTarget = new SpecialSkillAction(warrior2, deadGoblin);
        String reason2 = deadTarget.blockedReason();
        check("blockedReason mentions living enemy required",
              reason2.contains("living") || reason2.contains("Shield Bash"));

        
        Warrior deadWarrior = new Warrior("DeadWarrior");
        deadWarrior.takeDamage(9999);
        SpecialSkillAction deadActor = new SpecialSkillAction(deadWarrior, goblin);
        String reason3 = deadActor.blockedReason();
        check("blockedReason mentions actor name when dead",
              reason3.contains("DeadWarrior"));
    }

    

    private static void testPlayerSpecialSkills_forPlayer() {
        System.out.println("\n=== PlayerSpecialSkills.forPlayer ===");

        Warrior warrior = new Warrior("PSWarrior");
        Wizard  wizard  = new Wizard("PSWizard");

        check("Warrior → ShieldBashSkill",  PlayerSpecialSkills.forPlayer(warrior) == ShieldBashSkill.INSTANCE);
        check("Wizard  → ArcaneBlastSkill", PlayerSpecialSkills.forPlayer(wizard)  == ArcaneBlastSkill.INSTANCE);

        
        boolean threw = false;
        try {
            
            Player unknown = new Player("Unknown", 100, 10, 5, 5) {};
            PlayerSpecialSkills.forPlayer(unknown);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("Unknown player type throws IllegalArgumentException", threw);
    }

    

    private static void testShieldBashSkillDirect() {
        System.out.println("\n=== ShieldBashSkill (direct) ===");

        Warrior warrior = new Warrior("DirectWarrior");
        Goblin goblin = new Goblin();
        BattleContext ctx = ctx(warrior, goblin);

        check("canUse() true initially",          ShieldBashSkill.INSTANCE.canUse(warrior));

        
        ActionResult result = ShieldBashSkill.INSTANCE.execute(warrior, ctx, goblin);
        check("execute() success",                result.isSuccess());
        check("Goblin damaged",                   goblin.getHp() < 55);
        check("Goblin has Stun",                  goblin.hasStatusEffect(Stun.class));
        check("Cooldown set after use",           warrior.getSpecialSkillCooldown() == 2);
        check("canUse() false after use",         !ShieldBashSkill.INSTANCE.canUse(warrior));

        
        Warrior warrior2 = new Warrior("W2");
        ActionResult nullTargetResult = ShieldBashSkill.INSTANCE.execute(warrior2, ctx, null);
        check("Null target → fail", !nullTargetResult.isSuccess());

        
        Goblin deadGoblin = new Goblin();
        deadGoblin.takeDamage(9999);
        ActionResult deadTargetResult = ShieldBashSkill.INSTANCE.execute(warrior2, ctx, deadGoblin);
        check("Dead target → fail", !deadTargetResult.isSuccess());

        
        Warrior cooldownWarrior = new Warrior("CooldownWarrior");
        cooldownWarrior.consumeSpecialSkillUse();
        ActionResult cooldownResult = ShieldBashSkill.INSTANCE.execute(cooldownWarrior, ctx, goblin);
        check("On cooldown → fail", !cooldownResult.isSuccess());
    }

    

    private static void testArcaneBlastSkillDirect() {
        System.out.println("\n=== ArcaneBlastSkill (direct) ===");

        Wizard wizard = new Wizard("DirectWizard");
        Goblin g1 = new Goblin(); 
        Goblin g2 = new Goblin();
        Wolf   wolf = new Wolf(); 
        Goblin deadGoblin = new Goblin();
        deadGoblin.takeDamage(9999);                  
        BattleContext ctx = ctx(wizard, g1, g2, wolf, deadGoblin);

        check("canUse() true initially", ArcaneBlastSkill.INSTANCE.canUse(wizard));

        ActionResult result = ArcaneBlastSkill.INSTANCE.execute(wizard, ctx, null);
        check("execute() success",             result.isSuccess());
        check("Goblin 1 HP == 20",             g1.getHp() == 20);
        check("Goblin 2 HP == 20",             g2.getHp() == 20);
        check("Wolf defeated",                 !wolf.isAlive());
        check("Pre-dead Goblin still HP == 0", deadGoblin.getHp() == 0);
        check("Kill count correct (wolf)",     result.getMessage().contains("1"));
        check("Attack bonus == 10 (+10/kill)", ArcaneBlastEffect.getOn(wizard).getAttackModifier() == 10);
        check("canUse() false after use",      !ArcaneBlastSkill.INSTANCE.canUse(wizard));

        
        Wizard wizard2 = new Wizard("W2");
        Wolf w1 = new Wolf(); 
        Wolf w2 = new Wolf(); 
        BattleContext ctx2 = ctx(wizard2, w1, w2);
        ArcaneBlastSkill.INSTANCE.execute(wizard2, ctx2, null);
        check("2 kills → +20 bonus",           ArcaneBlastEffect.getOn(wizard2).getAttackModifier()  == 20);

        
        Warrior warrior = new Warrior("NonWizard");
        Goblin target = new Goblin();
        BattleContext ctx3 = ctx(warrior, target);
        ActionResult wrongActor = ArcaneBlastSkill.INSTANCE.execute(warrior, ctx3, null);
        
        check("Non-Wizard actor → fail", !wrongActor.isSuccess());

        
        Wizard onCooldown = new Wizard("CDWizard");
        onCooldown.consumeSpecialSkillUse();
        Goblin g = new Goblin();
        BattleContext ctx4 = ctx(onCooldown, g);
        ActionResult cdResult = ArcaneBlastSkill.INSTANCE.execute(onCooldown, ctx4, null);
        check("On cooldown → fail", !cdResult.isSuccess());
    }
}
