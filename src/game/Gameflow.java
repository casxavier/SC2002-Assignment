package game;

import combatant.*;
import item.*;
import status.Stun;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
public class Gameflow {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static GameSettings gameSettings;
    private final List<Combatant> enemies;
    private final List<Combatant> deadEnemies = new ArrayList<>();
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<Turn> history = new ArrayList<>();
    private Turn currentTurn;
    private int turnCount = 1;
    private boolean backupSpawned = false;
    private boolean won = false;

    // Constructor
    public Gameflow(Combatant player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy) {
        gameSettings = new GameSettings(difficulty, (Player) player);
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    // Initialize Game settings (difficulty, character class)
    public void initializeGame() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        System.out.println("First, select your character class: ");
        System.out.println("1. Warrior");
        System.out.println("HP: 260");
        System.out.println("Attack: 40");
        System.out.println("Defense: 20");
        System.out.println("Speed: 30");
        System.out.println(
                "Special Skill: Shield Bash - Deal basic attack damage to selected enemy. Selected enemy is unable to take action for the current and next turn.");
        System.out.println("------------------------------");
        System.out.println("2. Wizard");
        System.out.println("HP: 200");
        System.out.println("Attack: 50");
        System.out.println("Defense: 10");
        System.out.println("Speed: 20");
        System.out.println(
                "Special Skill: Arcane Blast - Deal basic attack damage to all enemies. Each enemy defeated by Arcane Blast adds 10 to the Wizard's attack, lasting until the end of the level.");
        System.out.println("------------------------------");
        System.out.print("Select Character Class (1 or 2): ");
        choice = sc.nextInt();
        while (choice < 1 || choice > 2) {
            System.err.println("Invalid choice. Please select 1 or 2.");
            System.out.print("Select Character Class (1 or 2): ");
            choice = sc.nextInt();
        }

        // Switch case to allow expansion in the future
        switch (choice) {
            case 1:
                gameSettings.setPlayer(new Warrior("Warrior"));
                break;
            case 2:
                gameSettings.setPlayer(new Warrior("Wizard"));
                break;
            default:
                gameSettings.setPlayer(new Warrior("Warrior"));
                break;
        }
        System.out.println();

        // Difficulty Selection
        System.out.println("Next, select your difficulty: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.print("Select Difficulty: ");
        int chosenDifficulty = sc.nextInt();
        while (chosenDifficulty < 1 || chosenDifficulty > 3) {
            System.err.println("Invalid choice. Please select 1, 2 or 3.");
            System.out.print("Select Difficulty: ");
            chosenDifficulty = sc.nextInt();
        }

        System.out.printf("You are about to start as a %s on %s mode. Good luck!\n\n",
                gameSettings.getPlayer().getName(),
                chosenDifficulty == 1 ? "easy" : chosenDifficulty == 2 ? "medium" : "hard");

        switch (chosenDifficulty) {
            case 1:
                gameSettings.setDifficulty(Difficulty.EASY);
                break;
            case 2:
                gameSettings.setDifficulty(Difficulty.MEDIUM);
                break;
            case 3:
                gameSettings.setDifficulty(Difficulty.HARD);
                break;
            default:
                gameSettings.setDifficulty(Difficulty.EASY);
                break;
        }
        sc.close();
    }

    // Run Game Loop
    public void executeGameLoop() {
        Scanner sc = new Scanner(System.in);

        // Print Order of Attacks
        List<Combatant> orderedCombatants = getOrder();
        printTurnOrder(orderedCombatants);

        // Main game loop, continues until either player is defeated.
        // Break statement below exits if all enemies are defeated.
        Player player = gameSettings.getPlayer();
        while (player.isAlive()) {

            System.out.printf("Round %d\n", turnCount);
            System.out.println("==========");

            printBattleState();

            // Execute the turn
            currentTurn = new Turn(turnCount, player, enemies, orderedCombatants);
            currentTurn.executeTurn();

            // Remove dead enemies from list
            for (int i = 0; i < enemies.size(); i++) {
                if (!enemies.get(i).isAlive()) {
                    deadEnemies.add(enemies.get(i));
                    enemies.remove(i);
                    i--;
                }
            }
            // Check to spawn backup waves
            if (!backupSpawned && enemies.size() == 0) {
                spawnBackupWave();
                backupSpawned = true;
            }

            // Check win condition
            if (enemies.isEmpty()) {
                won = true;
                break;
            }

            // Print summary of round, ready next turn
            printRoundSummary();
            turnCount++;
            history.add(currentTurn);
            orderedCombatants = getOrder();
        }

        // End of game, print victory/defeat screen depending on 'won' variable.
        if (gameSettings.getPlayer() instanceof Wizard) {
            ((Wizard) gameSettings.getPlayer()).resetArcaneBlastBonus();
        }
        gameCompletion();
        sc.close();
    }

    // orders the current combatants according to turn order strategy (speed in this case)
    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(gameSettings.getPlayer());
        orderedCombatants.addAll(enemies);
        return turnOrderStrategy.getOrder(orderedCombatants);
    }

    // Prints Round Summary
    public void printRoundSummary() {
        Player currPlayer = gameSettings.getPlayer();
        System.out.printf("End of Round %d:%n", turnCount);

        // Player
        System.out.printf("%s HP: %d/%d%n", currPlayer.getName(),currPlayer.getHp(),currPlayer.getMaxHp()); //yet to implement getMaxHp()

        // Alive + Dead Enemies
        for (Combatant aliveEnemy : enemies){// summary of alive enemies
            System.out.printf("%s HP: %d", aliveEnemy.getName(), aliveEnemy.getHp());
            if (aliveEnemy.hasStatusEffect(Stun.class)){
                System.out.print("[STUNNED]");
            }
            System.out.println();
        } 
        for (Combatant deadEnemy : deadEnemies){// summary of dead enemies
            System.out.printf("%s HP: 0 (Defeated)%n", deadEnemy.getName());
        }

        // Inventory
        List<Item> inventory = currPlayer.getInventory();
        if (!inventory.isEmpty()){
            Map<String,Integer> itemCountMap = new LinkedHashMap<>();

            for (Item i : inventory){// create or update count for each item
                itemCountMap.put(i.getName(),itemCountMap.getOrDefault(i.getName(), 0)+1);
            }
            for (String itemName: itemCountMap.keySet()){// print values of item + count
                System.out.printf("%s: %d%n", itemName, itemCountMap.get(itemName));
            }
        }
        // Special Skills/ Effects
        if (currPlayer.isSmokeActive()){ // if affected by smoke, show remaining affected turns
            System.out.printf("Effect: %d turn%s remaining%n", currPlayer.getSmokeTurns(),currPlayer.getSmokeTurns() == 1?"":"s");
        }
        // printing special skill cooldown
        System.out.printf("Special Skills Cooldown: %d%n", currPlayer.getSpecialSkillCooldown(), currPlayer.getSpecialSkillCooldown() == 1 ? "round":"rounds");
        
        System.out.println();
    }

    // Print turn order of combatants
    public void printTurnOrder(List<Combatant> oCombatants) {
        System.out.println("Turn Order:");
        for (int i = 1; i <= oCombatants.size(); i++) {
            System.out.printf("%d. %s (Speed: %d)\n", i, oCombatants.get(i).getName(), oCombatants.get(i).getSpeed());
        }
    }

    // Print Victory or Defeat depending on conditions at end of game
    public void gameCompletion() {
        // prints either "Victory" or "Defeat" based on conditions (Pending
        // implementation of player +enemy)
        Scanner sc = new Scanner(System.in);
        String gameResult = won ? "Victory" : "Defeat";
        System.out.println(gameResult);
        if (won) {
            System.out.println("Congratulations, you have defeated all your enemies.");
            System.out.printf("Statistics: Remaining HP: %d | Total Rounds: %d\n", gameSettings.getPlayer().getHp(),
                    turnCount);
        } else {
            System.out.println("Defeated. Don't give up, try again!");
            int left = enemies.size();
            System.out.printf("Statistics: Enemies remaining: %d | Total Rounds Survived: %d\n", left, turnCount);
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
                executeGameLoop();
                break;
            case 2:
                initializeGame();
                executeGameLoop();
                break;
            case 3:
                System.out.println("Thanks for playing. Goodbye!");
                System.exit(0);
                break;
            default:
                break;
        }
        sc.close();
    }

    // Spawning Functions
    private List<Combatant> spawnInitialEnemy() { // spawning of initial enemies for each difficulty
        switch (gameSettings.getDifficulty()) {

            case EASY: // Spawn 3 Goblins
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
        return enemies;
    }

    private void spawnBackupWave() {
        switch (gameSettings.getDifficulty()) {
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

    private void printBattleState() {
        System.out.printf("%s — HP: %d\n", gameSettings.getPlayer().getName(), gameSettings.getPlayer().getHp());
        for (int i = 0; i < enemies.size(); i++) {
            Combatant e = enemies.get(i);
            if (e.isAlive()) {
                System.out.printf("  Enemy %d: %s — HP: %d\n", i + 1, e.getName(), e.getHp());
            }
        }
        System.out.printf("Special cooldown: %d | Can use special: %s\n",
                gameSettings.getPlayer().getSpecialSkillCooldown(), gameSettings.getPlayer().canUseSpecialSkill());
    }

}
