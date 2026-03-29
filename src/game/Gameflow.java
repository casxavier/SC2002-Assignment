package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import action.Action;
import action.ActionResult;
import action.BasicAttackAction;
import action.BattleContext;
import action.DefendAction;
import action.ItemAction;
import action.SpecialSkillAction;
import combatant.Combatant;
import combatant.Enemy;
import combatant.Goblin;
import combatant.Player;
import combatant.Warrior;
import combatant.Wizard;
import combatant.Wolf;
import item.Item;
import item.PowerStone;

public class Gameflow {
    public enum Difficulty { EASY, MEDIUM, HARD }

    private final Player player;
    private final List<Combatant> enemies;
    private final TurnOrderStrategy turnOrderStrategy;

    private Difficulty difficulty;
    private int turnCount = 1;
    private boolean backupSpawnDone = false;
    private boolean won = false;

    public Gameflow(Player player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy) {
        this.player = player;
        this.difficulty = difficulty;
        this.enemies = new ArrayList<>();
        spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    private void pruneDeadEnemies() {
        enemies.removeIf(e -> !e.isAlive());
    }

    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(player);
        orderedCombatants.addAll(enemies);
        return turnOrderStrategy.getOrder(orderedCombatants);
    }

    public void showMenu() {
    }

    public void printTurnOrder(List<Combatant> oCombatants) {
        System.out.println("Turn Order:");
        for (int i = 0; i < oCombatants.size(); i++) {
            Combatant c = oCombatants.get(i);
            System.out.printf("%d. %s (Speed: %d)%n", i + 1, c.getName(), c.getSpeed());
        }
    }

    public void startGame() {
        Scanner sc = new Scanner(System.in);
        BattleContext ctx = new BattleContext(player, enemies);

        while (player.isAlive()) {
            pruneDeadEnemies();
            if (enemies.isEmpty()) {
                if (!backupSpawnDone && (difficulty == Difficulty.MEDIUM || difficulty == Difficulty.HARD)) {
                    spawnBackupWave();
                    backupSpawnDone = true;
                }
                if (enemies.isEmpty()) {
                    won = true;
                    break;
                }
            }

            printRoundHeader();
            List<Combatant> orderedCombatants = getOrder();
            printTurnOrder(orderedCombatants);

            for (Combatant combatant : orderedCombatants) {
                pruneDeadEnemies();
                if (!player.isAlive()) {
                    break;
                }
                if (enemies.isEmpty()) {
                    break;
                }
                if (!combatant.isAlive()) {
                    continue;
                }

                combatant.onTurnStart();
                if (!combatant.canAct()) {
                    System.out.println(combatant.getName() + " is unable to act (skipped).");
                    continue;
                }

                if (combatant == player) {
                    runPlayerTurn(sc, ctx);
                } else if (combatant instanceof Enemy) {
                    Action attack = new BasicAttackAction(combatant, player);
                    if (attack.canExecute()) {
                        ActionResult res = attack.execute(ctx);
                        System.out.println(res.getMessage());
                    }
                }
                pruneDeadEnemies();
            }

            if (!player.isAlive()) {
                won = false;
                break;
            }

            printRoundSummary();
            turnCount++;
        }

        if (player instanceof Wizard) {
            ((Wizard) player).resetArcaneBlastBonus();
        }
        printGameCompletionScreen(sc);
        sc.close();
    }

    private List<Combatant> aliveEnemies() {
        List<Combatant> list = new ArrayList<>();
        for (Combatant e : enemies) {
            if (e.isAlive()) {
                list.add(e);
            }
        }
        return list;
    }

    private void printBattleState() {
        System.out.printf("%s — HP: %d%n", player.getName(), player.getHp());
        for (int i = 0; i < enemies.size(); i++) {
            Combatant e = enemies.get(i);
            if (e.isAlive()) {
                System.out.printf("  Enemy %d: %s — HP: %d%n", i + 1, e.getName(), e.getHp());
            }
        }
        System.out.printf("Special cooldown: %d | Can use special: %s%n",
                player.getSpecialSkillCooldown(), player.canUseSpecialSkill());
    }

    private void runPlayerTurn(Scanner sc, BattleContext ctx) {
        while (true) {
            printBattleState();
            System.out.println("Choose action:");
            System.out.println("1. Basic Attack");
            System.out.println("2. Defend");
            System.out.println("3. Use Item");
            System.out.println("4. Special Skill");
            System.out.print("Enter choice (1-4): ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input.");
                continue;
            }

            Action action = null;

            switch (choice) {
                case 1: {
                    List<Combatant> alive = aliveEnemies();
                    if (alive.isEmpty()) {
                        System.out.println("No enemies to attack.");
                        continue;
                    }
                    System.out.println("Select target:");
                    for (int i = 0; i < alive.size(); i++) {
                        System.out.printf("  %d. %s (HP %d)%n", i + 1, alive.get(i).getName(), alive.get(i).getHp());
                    }
                    System.out.print("Target number: ");
                    int t;
                    try {
                        t = Integer.parseInt(sc.nextLine().trim()) - 1;
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid target.");
                        continue;
                    }
                    if (t < 0 || t >= alive.size()) {
                        System.out.println("Invalid target.");
                        continue;
                    }
                    action = new BasicAttackAction(player, alive.get(t));
                    break;
                }
                case 2:
                    action = new DefendAction(player);
                    break;
                case 3: {
                    List<Item> inv = player.getInventory();
                    if (inv.isEmpty()) {
                        System.out.println("No items in inventory.");
                        continue;
                    }
                    System.out.println("Inventory:");
                    for (int i = 0; i < inv.size(); i++) {
                        System.out.printf("  %d. %s%n", i, inv.get(i).getName());
                    }
                    System.out.print("Item index: ");
                    int idx;
                    try {
                        idx = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid index.");
                        continue;
                    }
                    Combatant pst = null;
                    if (idx >= 0 && idx < inv.size() && inv.get(idx) instanceof PowerStone && player instanceof Warrior) {
                        List<Combatant> alive = aliveEnemies();
                        if (alive.isEmpty()) {
                            System.out.println("No valid target for Power Stone.");
                            continue;
                        }
                        System.out.println("Select enemy for Shield Bash:");
                        for (int i = 0; i < alive.size(); i++) {
                            System.out.printf("  %d. %s%n", i + 1, alive.get(i).getName());
                        }
                        System.out.print("Target number: ");
                        try {
                            int ti = Integer.parseInt(sc.nextLine().trim()) - 1;
                            if (ti < 0 || ti >= alive.size()) {
                                System.out.println("Invalid target.");
                                continue;
                            }
                            pst = alive.get(ti);
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid target.");
                            continue;
                        }
                    }
                    action = new ItemAction(player, idx, pst);
                    break;
                }
                case 4: {
                    Combatant skillTarget = null;
                    if (player instanceof Warrior) {
                        List<Combatant> alive = aliveEnemies();
                        if (alive.isEmpty()) {
                            System.out.println("No enemies for Shield Bash.");
                            continue;
                        }
                        System.out.println("Select enemy for Shield Bash:");
                        for (int i = 0; i < alive.size(); i++) {
                            System.out.printf("  %d. %s%n", i + 1, alive.get(i).getName());
                        }
                        System.out.print("Target number: ");
                        try {
                            int ti = Integer.parseInt(sc.nextLine().trim()) - 1;
                            if (ti < 0 || ti >= alive.size()) {
                                System.out.println("Invalid target.");
                                continue;
                            }
                            skillTarget = alive.get(ti);
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid target.");
                            continue;
                        }
                    }
                    action = new SpecialSkillAction(player, skillTarget);
                    break;
                }
                default:
                    System.out.println("Invalid choice.");
                    continue;
            }

            if (action == null) {
                continue;
            }
            if (!action.canExecute()) {
                System.out.println(action.blockedReason());
                continue;
            }
            System.out.println(action.execute(ctx).getMessage());
            return;
        }
    }

    public void printRoundHeader() {
        System.out.printf("%nRound %d%n", turnCount);
        System.out.println("==========");
    }

    public void printRoundSummary() {
        System.out.printf("End of Round %d%n", turnCount);
    }

    public void printGameCompletionScreen(Scanner sc) {
        String gameResult = won ? "Victory" : "Defeat";
        System.out.println(gameResult);
        if (won) {
            System.out.println("Congratulations, you have defeated all your enemies.");
            System.out.printf("Statistics: Remaining HP: %d | Total Rounds: %d%n", player.getHp(), turnCount);
        } else {
            System.out.println("Defeated. Don't give up, try again!");
            int left = 0;
            for (Combatant e : enemies) {
                if (e.isAlive()) {
                    left++;
                }
            }
            System.out.printf("Statistics: Enemies remaining: %d | Total Rounds Survived: %d%n", left, turnCount);
        }

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Replay with same settings");
        System.out.println("2. Start a new game");
        System.out.println("3. Exit");
        System.out.print("Choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                restartSameSettings();
                break;
            case 2:
                restartGame();
                break;
            case 3:
                System.out.println("Thanks for playing. Goodbye!");
                System.exit(0);
                break;
            default:
                break;
        }
    }

    private void restartSameSettings() {
    }

    private void restartGame() {
    }

    private void spawnInitialEnemy() {
        switch (difficulty) {
            case EASY:
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                break;
            case MEDIUM:
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                break;
            case HARD:
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                break;
            default:
                break;
        }
    }

    private void spawnBackupWave() {
        switch (difficulty) {
            case MEDIUM:
                enemies.add(new Wolf());
                enemies.add(new Wolf());
                System.out.println("Backup wave: 2 Wolves entered the arena!");
                break;
            case HARD:
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                enemies.add(new Wolf());
                System.out.println("Backup wave: 1 Goblin and 2 Wolves entered the arena!");
                break;
            default:
                break;
        }
    }
}
