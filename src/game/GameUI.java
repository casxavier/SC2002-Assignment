package game;

import combatant.Combatant;
import combatant.Player;
import combatant.Warrior;
import combatant.Wizard;
import item.Item;
import item.Potion;
import item.PowerStone;
import item.SmokeBomb;
import status.Stun;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class GameUI {

    
    private static long TURN_DELAY_MS = 1000;
    private static long ROUND_DELAY_MS = 3000;

    private static final String[] WARRIOR_ART = {
            "           !          /",
            "   ____   .-.       //",
            "  /    |__|=|__    //",
            " ||  /||_/`-`\\_) _[ ]",
            " ||/  |//\\___/\\\\-'",
            "  \\___// /   \\/",
            "        |\\_._/|",
            "         <_I_>",
            "          |||",
            "         /_|_\\"
    };

    private static final String[] WIZARD_ART = {
            "          /^\\",
            "     /\\   \"V\"",
            "    /__\\   I      O  o",
            "   //..\\\\  I     .",
            "   /l\\/j\\  (]    .  O",
            "  /. ~~ ,\\/I          .",
            "  \\\\L__j^\\/I       o",
            "   \\/--v}  I     o   .",
            "   |    |  I",
            " _/j  L l\\_!"
    };

    private static final String[] GOBLIN_ART = {
            "  ,___,",
            "  (o_o)",
            " /( | )\\",
            "   / \\",
            "  _| |_"
    };

    private static final String[] WOLF_ART = {
            " /\\_____/\\",
            "(  o   o  )",
            " \\   ^   /",
            " /| |_| |\\",
            "  /     \\"
    };

    private static final int BATTLE_BLOCK_WIDTH = 34;
    private static final int ENEMY_BLOCK_WIDTH = 14;
    private static final int BATTLE_GAP = 6;
    private static final int ENEMY_GAP = 3;
    private static final int ENEMY_VERTICAL_OFFSET = 5;

    private GameUI() {
    }

    static void showCharacterSelection() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("        SELECT YOUR CHARACTER");
        System.out.println("======================================");
        System.out.println("1) Warrior");
        printAsciiBlock(WARRIOR_ART, "   ");
        System.out.println("   HP: 260 | Attack: 40 | Defense: 20 | Speed: 30");
        System.out.println("   Special Skill: Shield Bash");
        System.out.println("   - Deal basic attack damage to one enemy.");
        System.out.println("   - Target cannot act this turn and next turn.");
        System.out.println("--------------------------------------");
        System.out.println("2) Wizard");
        printAsciiBlock(WIZARD_ART, "   ");
        System.out.println("   HP: 200 | Attack: 50 | Defense: 10 | Speed: 20");
        System.out.println("   Special Skill: Arcane Blast");
        System.out.println("   - Deal basic attack damage to all enemies.");
        System.out.println("   - Each enemy defeated grants +10 attack until level ends.");
        System.out.println("======================================");
    }

    static int promptCharacterChoice(Scanner sc) {
        while (true) {
            try {
                System.out.print("Choose your class [1-2]: ");
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > 2) {
                    throw new IllegalArgumentException("Invalid choice. Please enter 1 or 2.");
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static Player createSelectedPlayer(int choice) {
        switch (choice) {
            case 1:
                return new Warrior("Warrior");
            case 2:
                return new Wizard("Wizard");
            default:
                return new Warrior("Warrior");
        }
    }

    static void showDifficultySelection() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("          SELECT DIFFICULTY");
        System.out.println("======================================");
        System.out.println("1) Easy");
        System.out.println("2) Medium");
        System.out.println("3) Hard");
        System.out.println("======================================");
    }

    static int promptDifficultyChoice(Scanner sc) {
        while (true) {
            try {
                System.out.print("Choose difficulty [1-3]: ");
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > 3) {
                    throw new IllegalArgumentException("Invalid choice. Please enter 1, 2, or 3.");
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static void showItemSelection() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("              PICK ITEMS");
        System.out.println("======================================");
        System.out.println("Choose 2 single-use items (duplicates allowed):");
        System.out.println("1) Potion      - Heal 100 HP");
        System.out.println("2) Power Stone - One free special skill use");
        System.out.println("3) Smoke Bomb  - Enemies deal 0 damage this turn and next");
        System.out.println("======================================");
    }

    static Item promptItemChoice(Scanner sc, String label) {
        while (true) {
            System.out.print(label + " [1-3]: ");
            String line = sc.nextLine().trim();
            int n;
            try {
                n = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1, 2, or 3.");
                continue;
            }

            switch (n) {
                case 1:
                    return new Potion();
                case 2:
                    return new PowerStone();
                case 3:
                    return new SmokeBomb();
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    static void printTurnHeader(int turnCount) {
        System.out.println();
        System.out.println("======================================");
        System.out.printf("               ROUND %d%n", turnCount);
        System.out.println("======================================");
    }

    static void printBattleState(Player player, List<Combatant> enemies) {
        System.out.println("[Battle State]");

        String[] playerArt = getPlayerArt(player);
        String[] playerInfo = {
                "Player: " + player.getName(),
                "HP: " + player.getHp() + "/" + player.getMaxHp(),
                "ATK: " + player.getAttack() + " | DEF: " + player.getDefense(),
                "SPD: " + player.getSpeed(),
                "Special Skill CD: " + player.getSpecialSkillCooldown(),
        };
        String[] leftBlock = buildCombatantBlock(playerArt, playerInfo, BATTLE_BLOCK_WIDTH);

        int aliveCount = 0;
        for (Combatant e : enemies) {
            if (e.isAlive()) {
                aliveCount++;
            }
        }

        if (aliveCount == 0) {
            printTwoColumnBattlefield(leftBlock, null);
            System.out.println("No enemies currently alive.");
            return;
        }

        String[] rightBlock = buildEnemiesBattleBlock(enemies);
        printTwoColumnBattlefield(leftBlock, rightBlock);

    }

    static void printRoundSummary(int turnCount, Player player, List<Combatant> enemies, List<Combatant> deadEnemies) {
        System.out.println();
        System.out.println("------------- ROUND SUMMARY ----------");
        System.out.printf("End of Round %d%n", turnCount);
        System.out.printf("%s HP: %d/%d%n", player.getName(), player.getHp(), player.getMaxHp());

        if (!enemies.isEmpty()) {
            System.out.println("Enemies:");
        }
        for (Combatant aliveEnemy : enemies) {
            System.out.printf("- %s HP: %d", aliveEnemy.getName(), aliveEnemy.getHp());
            if (aliveEnemy.hasStatusEffect(Stun.class)) {
                System.out.print(" [STUNNED]");
            }
            System.out.println();
        }

        for (Combatant deadEnemy : deadEnemies) {
            System.out.printf("- %s HP: 0 (Defeated)%n", deadEnemy.getName());
        }

        List<Item> inventory = player.getInventory();
        if (!inventory.isEmpty()) {
            System.out.println("Inventory:");
            Map<String, Integer> itemCountMap = new LinkedHashMap<>();
            for (Item item : inventory) {
                itemCountMap.put(item.getName(), itemCountMap.getOrDefault(item.getName(), 0) + 1);
            }
            for (String itemName : itemCountMap.keySet()) {
                System.out.printf("- %s: %d%n", itemName, itemCountMap.get(itemName));
            }
        } else {
            System.out.println("Inventory: (empty)");
        }

        if (player.isSmokeActive()) {
            System.out.printf("Smoke Bomb Effect: %d turn%s remaining%n",
                    player.getSmokeTurns(),
                    player.getSmokeTurns() == 1 ? "" : "s");
        }

        System.out.printf("Special Skill Cooldown: %d%n", player.getSpecialSkillCooldown());
        System.out.println("--------------------------------------");
        System.out.println();
    }

    static void printTurnOrder(List<Combatant> orderedCombatants) {
        System.out.println("Turn Order:");
        for (int i = 1; i < orderedCombatants.size() - 1; i++) {
            System.out.printf("%d. %s (Speed: %d)%n",
                    i,
                    orderedCombatants.get(i).getName(),
                    orderedCombatants.get(i).getSpeed());
        }
    }

    static void printBackupWave(Gameflow.Difficulty difficulty) {
        switch (difficulty) {
            case MEDIUM:
                System.out.println("Backup Spawn Triggered! 2 Wolves (HP: 40) entered the arena!");
                break;
            case HARD:
                System.out.println("Backup Spawn Triggered! 1 Goblin (HP: 55) and 2 Wolves (HP: 40) entered the arena!");
                break;
            default:
                break;
        }
    }

    static void printGameCompletion(boolean won, Player player, int enemiesRemaining, int turnCount) {
        System.out.println();
        System.out.println("======================================");
        String gameResult = won ? "VICTORY!!!" : "DEFEAT";
        System.out.println(gameResult);
        System.out.println("======================================");

        if (won) {
            System.out.println("Congratulations! You defeated all enemies.");
            System.out.printf("Statistics: Remaining HP: %d | Total Rounds: %d%n",
                    player.getHp(), turnCount);
        } else {
            System.out.println("You were defeated. Try again!");
            System.out.printf("Statistics: Enemies Remaining: %d | Rounds Survived: %d%n",
                    enemiesRemaining, turnCount);
        }
    }

    static void showEndGameOptions() {
        System.out.println();
        System.out.println("What would you like to do next?");
        System.out.println("1) Replay with same settings");
        System.out.println("2) Start a new game");
        System.out.println("3) Exit");
        System.out.print("Enter choice [1-3]: ");
    }

    static int promptEndGameChoice(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void printExitMessage() {
        System.out.println("Thanks for playing. Goodbye!");
    }

    static void printBlankLine() {
        System.out.println();
    }

    static int promptTurnActionChoice(Scanner sc) {
        System.out.println();
        System.out.println("Choose Action:");
        System.out.println("1) Basic Attack");
        System.out.println("2) Defend");
        System.out.println("3) Use Item");
        System.out.println("4) Special Skill");
        return promptMenuChoice(
                sc,
                "Enter choice [1-4]: ",
                1,
                4,
                "Invalid input. Please enter a number.",
                "Invalid choice. Please select from 1 to 4.");
    }

    static int promptMenuChoice(
            Scanner sc,
            String prompt,
            int minChoice,
            int maxChoice,
            String invalidInputMessage,
            String invalidChoiceMessage) {
        System.out.print(prompt);
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice < minChoice || choice > maxChoice) {
                System.out.println(invalidChoiceMessage);
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println(invalidInputMessage);
            return -1;
        }
    }

    static void printNoEnemiesToAttack() {
        System.out.println("There are no enemies to attack.");
    }

    static void printCombatantTargets(String heading, List<Combatant> enemies, boolean showHp) {
        System.out.println(heading);
        for (int i = 0; i < enemies.size(); i++) {
            if (showHp) {
                System.out.printf("  %d) %s (HP: %d)%n", i + 1, enemies.get(i).getName(), enemies.get(i).getHp());
            } else {
                System.out.printf("  %d) %s%n", i + 1, enemies.get(i).getName());
            }
        }
    }

    static int promptTargetIndex(
            Scanner sc,
            String prompt,
            int enemyCount,
            String invalidInputMessage,
            String invalidChoiceMessage) {
        int targetIndex = promptMenuChoice(sc, prompt, 1, enemyCount, invalidInputMessage, invalidChoiceMessage);
        return targetIndex == -1 ? -1 : targetIndex - 1;
    }

    static int promptAttackTargetIndex(Scanner sc, int enemyCount) {
        return promptTargetIndex(
                sc,
                "Select target (1-" + enemyCount + "): ",
                enemyCount,
                "Invalid input!",
                "Invalid target!");
    }

    static int promptShieldBashTargetIndex(Scanner sc, int enemyCount) {
        return promptTargetIndex(
                sc,
                "Select target (1-" + enemyCount + "): ",
                enemyCount,
                "Invalid target.",
                "Invalid target.");
    }

    static void printInventory(List<Item> inventory) {
        System.out.println("Inventory:");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, inventory.get(i).getName());
        }
    }

    static int promptInventoryChoice(Scanner sc, int inventorySize) {
        int itemIndex = promptMenuChoice(
                sc,
                "Select item [1-" + inventorySize + "]: ",
                1,
                inventorySize,
                "Invalid input. Please enter a number.",
                "Invalid choice. Please select a valid item.");
        return itemIndex == -1 ? -1 : itemIndex - 1;
    }

    static void printNoItemsInInventory() {
        System.out.println("Your inventory is empty.");
    }

    static void printNoValidTargetsForShieldBash() {
        System.out.println("No valid targets available for Shield Bash.");
    }

    static String formatTurnSummary(int turnNum, Combatant actor, boolean isStunned, String targetName, int dealtDamage) {
        if (isStunned) {
            return String.format("Turn %d: %s was stunned and could not act.%n", turnNum, actor.getName());
        }
        if ("ALL".equals(targetName)) {
            return String.format("Turn %d: %s used Arcane Blast.%n", turnNum, actor.getName());
        }
        return String.format("Turn %d: %s dealt %d damage (%d - %d def) to %s.%n",
                turnNum, actor.getName(), dealtDamage, actor.getAttack(), actor.getAttack() - dealtDamage, targetName);
    }

    static void printInvalidChoice() {
        System.out.println("Invalid choice.");
    }

    static void printMessage(String message) {
        System.out.println(message);
    }

    static void printDefeatMessage(Combatant combatant) {
        printMessage(combatant.getName() + " was defeated!");
    }

    static void waitBetweenTurns() {
        sleep(TURN_DELAY_MS);
    }

    static void waitBetweenRounds() {
        sleep(ROUND_DELAY_MS);
    }

    private static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String[] getPlayerArt(Player player) {
        if (player instanceof Warrior) {
            return WARRIOR_ART;
        }
        if (player instanceof Wizard) {
            return WIZARD_ART;
        }
        return new String[]{"(Player)"};
    }

    private static String[] getEnemyArt(Combatant enemy) {
        String enemyName = enemy.getName().toLowerCase();
        if (enemyName.contains("goblin")) {
            return GOBLIN_ART;
        }
        if (enemyName.contains("wolf")) {
            return WOLF_ART;
        }
        return new String[]{"(Enemy)"};
    }

    private static String[] buildCombatantBlock(String[] art, String[] stats, int width) {
        String[] block = new String[art.length + stats.length];
        int line = 0;

        for (String artLine : art) {
            block[line++] = padRight(artLine, width);
        }
        for (String statLine : stats) {
            block[line++] = padRight(statLine, width);
        }

        return block;
    }

    private static String[] buildEnemiesBattleBlock(List<Combatant> enemies) {
        int aliveCount = 0;
        for (Combatant enemy : enemies) {
            if (enemy.isAlive()) {
                aliveCount++;
            }
        }

        String[][] enemyBlocks = new String[aliveCount][];
        int i = 0;
        for (Combatant enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            String[] art = getEnemyArt(enemy);
            String[] stats = {
                    enemy.getName(),
                    "HP: " + enemy.getHp()
            };
            enemyBlocks[i++] = buildCombatantBlock(art, stats, ENEMY_BLOCK_WIDTH);
        }

        return joinBlocksHorizontally(enemyBlocks, ENEMY_GAP);
    }

    private static String[] joinBlocksHorizontally(String[][] blocks, int gapSize) {
        if (blocks.length == 0) {
            return new String[0];
        }

        int maxRows = 0;
        for (String[] block : blocks) {
            if (block.length > maxRows) {
                maxRows = block.length;
            }
        }

        int[] widths = new int[blocks.length];
        for (int b = 0; b < blocks.length; b++) {
            int w = 0;
            for (String line : blocks[b]) {
                if (line.length() > w) {
                    w = line.length();
                }
            }
            widths[b] = w;
        }

        String[] out = new String[maxRows];
        String gap = repeat(" ", gapSize);

        for (int r = 0; r < maxRows; r++) {
            StringBuilder row = new StringBuilder();
            for (int b = 0; b < blocks.length; b++) {
                String line = r < blocks[b].length ? blocks[b][r] : "";
                row.append(padRight(line, widths[b]));
                if (b < blocks.length - 1) {
                    row.append(gap);
                }
            }
            out[r] = row.toString();
        }

        return out;
    }

    private static void printTwoColumnBattlefield(String[] left, String[] right) {
        int rightLen = right == null ? 0 : right.length + ENEMY_VERTICAL_OFFSET;
        int totalRows = Math.max(left.length, rightLen);
        String gap = repeat(" ", BATTLE_GAP);

        for (int i = 0; i < totalRows; i++) {
            String leftLine = i < left.length ? left[i] : padRight("", BATTLE_BLOCK_WIDTH);
            String rightLine = "";
            if (right != null) {
                int rightIndex = i - ENEMY_VERTICAL_OFFSET;
                if (rightIndex >= 0 && rightIndex < right.length) {
                    rightLine = right[rightIndex];
                }
            }
            System.out.println(leftLine + gap + rightLine);
        }
    }

    private static void printAsciiBlock(String[] art, String prefix) {
        for (String line : art) {
            System.out.println(prefix + line);
        }
    }

    private static String padRight(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= width) {
            return text;
        }
        return text + repeat(" ", width - text.length());
    }

    private static String repeat(String s, int times) {
        if (times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
