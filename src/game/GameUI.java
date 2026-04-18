package game;

import combatant.*;
import item.*;
import status.Stun;
import game.DeveloperConfig.Difficulty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class GameUI {

    private static long TURN_DELAY_MS = 500;
    private static long ROUND_DELAY_MS = 1000;

    public static final Map<String, String[]> ASCII_ART = new LinkedHashMap<>();

    static {
        ASCII_ART.put("WARRIOR", new String[]{
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
        });

        ASCII_ART.put("WIZARD", new String[]{
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
        });

        ASCII_ART.put("GOBLIN", new String[]{
                "  ,___,",
                "  (o_o)",
                " /( | )\\",
                "   / \\",
                "  _| |_"
        });

        ASCII_ART.put("WOLF", new String[]{
                " /\\_____/\\",
                "(  o   o  )",
                " \\   ^   /",
                " /| |_| |\\",
                "  /     \\"
        });
    }

    private static final int BATTLE_BLOCK_WIDTH = 34;
    private static final int ENEMY_BLOCK_WIDTH = 14;
    private static final int BATTLE_GAP = 6;
    private static final int ENEMY_GAP = 3;
    private static final int ENEMY_VERTICAL_OFFSET = 5;

    private GameUI() {
    }

    static int promptMenuChoice(
            Scanner sc,
            String prompt,
            int minChoice,
            int maxChoice,
            String invalidInputMessage,
            String invalidChoiceMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < minChoice || choice > maxChoice) {
                    System.out.println(invalidChoiceMessage);
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println(invalidInputMessage);
            }
        }
    }

    static void showCharacterSelection() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("        SELECT YOUR CHARACTER");
        System.out.println("======================================");

        java.util.List<String> playerTypes = GameSettings.getAvailablePlayerTypes();
        for (int i = 0; i < playerTypes.size(); i++) {
            String type = playerTypes.get(i);
            System.out.println((i + 1) + ") " + GameSettings.getCharacterDescription(type));

            String[] art = ASCII_ART.get(type);
            if (art != null) {
                printAsciiBlock(art, "   ");
            }

            if (i < playerTypes.size() - 1) {
                System.out.println("--------------------------------------");
            }
        }

        System.out.println("======================================");
    }

    static int promptCharacterChoice(Scanner sc) {
        int maxChoice = GameSettings.getAvailablePlayerTypes().size();
        return promptMenuChoice(
                sc,
                "Choose your class [1-" + maxChoice + "]: ",
                1,
                maxChoice,
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - " + maxChoice + ".");
    }

    static String promptPlayerName(Scanner sc) {
        System.out.print("Enter your character name: ");
        return sc.nextLine().trim();
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
        return promptMenuChoice(
                sc,
                "Choose difficulty [1-3]: ",
                1,
                3,
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - 3.");
    }

    static void showItemSelection() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("              PICK ITEMS");
        System.out.println("======================================");
        System.out.println("Choose 2 single-use items (duplicates allowed):");

        java.util.List<String> itemTypes = GameSettings.getAvailableItemTypes();
        for (int i = 0; i < itemTypes.size(); i++) {
            String type = itemTypes.get(i);
            System.out.println((i + 1) + ") " + GameSettings.getItemDescription(type));
        }

        System.out.println("======================================");
    }

    static String promptItemChoice(Scanner sc, String label) {
        java.util.List<String> itemTypes = GameSettings.getAvailableItemTypes();
        int maxChoice = itemTypes.size();

        int choice = promptMenuChoice(
                sc,
                label + " [1-" + maxChoice + "]: ",
                1,
                maxChoice,
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - " + maxChoice + "."
        );

        String itemType = itemTypes.get(choice - 1);
        return itemType;
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
                getStatusEffectsString(player),
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

        System.out.printf("Special Skill Cooldown: %d%n", player.getSpecialSkillCooldown());
        System.out.println("--------------------------------------");
        System.out.println();
    }

    static void printTurnOrder(List<Combatant> orderedCombatants) {
        System.out.println("Turn Order:");
        for (int i = 0; i < orderedCombatants.size(); i++) {
            System.out.printf("%d. %s (Speed: %d)%n",
                    i + 1,
                    orderedCombatants.get(i).getName(),
                    orderedCombatants.get(i).getSpeed());
        }
    }

    static void printBackupWave(Difficulty difficulty) {
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
    }

    static int promptEndGameChoice(Scanner sc) {
        return promptMenuChoice(
                sc,
                "Enter choice [1-3]: ",
                1,
                3,
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - 3.");
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
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - 4.");
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

    static int promptSelectTarget(Scanner sc, int enemyCount) {
        if (enemyCount == 1) {
            System.out.println("Target automatically selected.");
            System.out.println();
            return 0;
        }
        int targetIndex = promptMenuChoice(
                sc,
                "Select target [1-" + enemyCount + "]: ",
                1,
                enemyCount,
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - " + enemyCount + ".");
        System.out.println();
        return targetIndex - 1;
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
                "Invalid input! Please enter a number.",
                "Invalid choice! Please select 1 - " + inventorySize + ".");
        return itemIndex - 1;
    }

    static void printNoItemsInInventory() {
        System.out.println("Your inventory is empty.");
    }

    static void printNoValidTargets() {
        System.out.println("No valid targets available.");
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
            return ASCII_ART.getOrDefault("WARRIOR", new String[]{"(Warrior)"});
        }
        if (player instanceof Wizard) {
            return ASCII_ART.getOrDefault("WIZARD", new String[]{"(Wizard)"});
        }
        return new String[]{"(Player)"};
    }

    private static String[] getEnemyArt(Combatant enemy) {
        if (enemy instanceof Goblin) {
            return ASCII_ART.getOrDefault("GOBLIN", new String[]{"(Goblin)"});
        }
        if (enemy instanceof Wolf) {
            return ASCII_ART.getOrDefault("WOLF", new String[]{"(Wolf)"});
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
                    "HP: " + enemy.getHp(),
                    "ATK: " + enemy.getAttack() + " | DEF: " + enemy.getDefense(),
                    "SPD: " + enemy.getSpeed(),
                    getStatusEffectsString(enemy)
            };
            enemyBlocks[i++] = buildCombatantBlock(art, stats, ENEMY_BLOCK_WIDTH);
        }

        return joinBlocksHorizontally(enemyBlocks, ENEMY_GAP);
    }

    private static String getStatusEffectsString(Combatant combatant) {
        java.util.List<status.StatusEffect> effects = combatant.getStatusEffects();
        if (effects.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder("Status: ");
        for (int i = 0; i < effects.size(); i++) {
            status.StatusEffect effect = effects.get(i);
            if (i > 0) sb.append(", ");

            int duration = effect.getRemainingTurns();
            if (duration > 0) {
                sb.append(effect.getName()).append(" (").append(duration).append("t)");
            }
            else {
                sb.append(effect.getName());
            }
        }
        return sb.toString();
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
