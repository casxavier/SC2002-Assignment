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

    
    public Gameflow(Combatant player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy) {
        gameSettings = new GameSettings(difficulty, (Player) player);
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    private static Item promptItem(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " (1-3): ");
            String line = scanner.nextLine().trim();
            int n;
            try {
                n = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Enter 1, 2, or 3.");
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
                    System.out.println("Enter 1, 2, or 3.");
            }
        }
    }
        
    public static Gameflow initializeGame(Scanner sc) {
        int choice = 0;
        boolean validChar = false;
        boolean validDiff = false;
        Player player = null;

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

        while(!validChar){
            try{
                System.out.print("Select Character Class (1 or 2): ");
                choice = Integer.parseInt(sc.nextLine().trim()); 
                if (choice < 1 || choice > 2){
                    throw new IllegalArgumentException("Invalid choice. Please select 1 or 2.");
                }
                validChar = true; 
            }catch(NumberFormatException e){
                System.err.println("Please enter a valid integer (1 or 2).");
            }catch(IllegalArgumentException e){
                System.err.println(e.getMessage());
            }
        }

        
        switch (choice) {
            case 1:
                player = new Warrior("Warrior");
                break;
            case 2:
                player = new Wizard("Wizard");
                break;
            default:
                player = new Warrior("Warrior");
        }
        System.out.println();

        
        int chosenDifficulty = 0;
        System.out.println("Next, select your difficulty: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");

        while(!validDiff){
            try{
                System.out.print("Select Difficulty: ");
                chosenDifficulty = Integer.parseInt(sc.nextLine().trim()); 
                if (chosenDifficulty <1 || chosenDifficulty >3){
                    throw new IllegalArgumentException("Invalid choice. Please select 1, 2 or 3.");
                }
                validDiff = true;
            }catch (NumberFormatException e){
                System.err.println("Invalid choice. Please enter a number.");
            }catch (IllegalArgumentException e){
                System.err.println(e.getMessage());
            }
        }
        gameSettings = new GameSettings(null, player);
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

        System.out.println("\nChoose two single-use items (duplicates allowed):");
        System.out.println("1. Potion — Heal 100 HP");
        System.out.println("2. Power Stone — One free special skill use");
        System.out.println("3. Smoke Bomb — Enemies deal 0 damage this turn and next");
        player.addItem(promptItem(sc, "First item"));
        player.addItem(promptItem(sc, "Second item"));
        System.out.println();

        Gameflow newGame = new Gameflow(player, gameSettings.getDifficulty(), new OrderBySpeed());
        return newGame;
    }

    
    public void executeGameLoop(Scanner sc) {

        
        List<Combatant> orderedCombatants = getOrder();
        printTurnOrder(orderedCombatants);

        
        
        Player player = gameSettings.getPlayer();
        while (player.isAlive()) {

            System.out.printf("Round %d\n", turnCount);
            System.out.println("==========");

            printBattleState();

            
            currentTurn = new Turn(turnCount, player, enemies, orderedCombatants);
            currentTurn.executeTurn(sc);

            
            if(!player.isAlive()){
                break;
            }
            
            for (int i = 0; i < enemies.size(); i++) {
                if (!enemies.get(i).isAlive()) {
                    deadEnemies.add(enemies.get(i));
                    enemies.remove(i);
                    i--;
                }
            }
            
            if (!backupSpawned && enemies.size() == 0) {
                spawnBackupWave();
                backupSpawned = true;
            }

            
            if (enemies.isEmpty()) {
                won = true;
                break;
            }

            
            printRoundSummary();
            turnCount++;
            history.add(currentTurn);
            orderedCombatants = getOrder();
        }

        
        if (gameSettings.getPlayer() instanceof Wizard) {
            ((Wizard) gameSettings.getPlayer()).resetArcaneBlastBonus();
        }
        gameCompletion(sc);
    }

    
    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(gameSettings.getPlayer());
        orderedCombatants.addAll(enemies);
        return turnOrderStrategy.getOrder(orderedCombatants);
    }

    
    public void printRoundSummary() {
        Player currPlayer = gameSettings.getPlayer();
        System.out.printf("End of Round %d:%n", turnCount);

        
        System.out.printf("%s HP: %d/%d%n", currPlayer.getName(),currPlayer.getHp(),currPlayer.getMaxHp()); 

        
        for (Combatant aliveEnemy : enemies){
            System.out.printf("%s HP: %d", aliveEnemy.getName(), aliveEnemy.getHp());
            if (aliveEnemy.hasStatusEffect(Stun.class)){
                System.out.print("[STUNNED]");
            }
            System.out.println();
        } 
        for (Combatant deadEnemy : deadEnemies){
            System.out.printf("%s HP: 0 (Defeated)%n", deadEnemy.getName());
        }

        
        List<Item> inventory = currPlayer.getInventory();
        if (!inventory.isEmpty()){
            Map<String,Integer> itemCountMap = new LinkedHashMap<>();

            for (Item i : inventory){
                itemCountMap.put(i.getName(),itemCountMap.getOrDefault(i.getName(), 0)+1);
            }
            for (String itemName: itemCountMap.keySet()){
                System.out.printf("%s: %d%n", itemName, itemCountMap.get(itemName));
            }
        }
        
        if (currPlayer.isSmokeActive()){ 
            System.out.printf("Effect: %d turn%s remaining%n", currPlayer.getSmokeTurns(),currPlayer.getSmokeTurns() == 1?"":"s");
        }
        
        System.out.printf("Special Skills Cooldown: %d%n", currPlayer.getSpecialSkillCooldown(), currPlayer.getSpecialSkillCooldown() == 1 ? "round":"rounds");
        
        System.out.println();
    }

    
    public void printTurnOrder(List<Combatant> oCombatants) {
        System.out.println("Turn Order:");
        for (int i = 1; i <oCombatants.size()-1; i++) {
            System.out.printf("%d. %s (Speed: %d)\n", i, oCombatants.get(i).getName(), oCombatants.get(i).getSpeed());
        }
    }

    
    public void gameCompletion(Scanner sc) {
        
        
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
                executeGameLoop(sc);
                break;
            case 2:
                initializeGame(sc);
                executeGameLoop(sc);
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

    
    private List<Combatant> spawnInitialEnemy() { 
        List <Combatant> enemyList  = new ArrayList<>();
        switch (gameSettings.getDifficulty()) {

            case EASY: 
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Goblin("Goblin B"));
                enemyList.add(new Goblin("Goblin C"));
                break;
            case MEDIUM:
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Wolf("Wolf A"));
                break;
            case HARD:
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Goblin("Goblin B"));
                break;
            default:
                break;
        }
        return enemyList;
    }

    private void spawnBackupWave() {
        switch (gameSettings.getDifficulty()) {
            case MEDIUM:
                enemies.add(new Wolf("Wolf A"));
                enemies.add(new Wolf("Wolf B"));
                System.out.println("Backup Spawn Triggered! 2 Wolves (HP: 40) entered the arena!");
                break;
            case HARD:
                enemies.add(new Goblin("Goblin A"));
                enemies.add(new Wolf("Wolf A"));
                enemies.add(new Wolf("Wolf B"));
                System.out.println("Backup Spawn Triggered! 1 Goblin (HP: 55) and 2 Wolves (HP: 40) entered the arena!");
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
