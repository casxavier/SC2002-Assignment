package game;

import combatant.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Gameflow {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static GameSettings gameSettings;
    private final List<Combatant> enemies;
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<Turn> history = new ArrayList<>();
    private Turn currentTurn;
    private int turnCount = 1;
    private boolean backupSpawn = false;
    private boolean won = false;

    
    public Gameflow(Combatant player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy) {
        this.gameSettings = new GameSettings(difficulty, (Player) player);
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    
    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(gameSettings.getPlayer());
        orderedCombatants.addAll(enemies);

        return turnOrderStrategy.getOrder(orderedCombatants); 
    }

    
    public void initializeGame() {
        Scanner scanner = new Scanner(System.in);
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
        choice = scanner.nextInt();
        while (choice < 1 || choice > 2) {
            System.err.println("Invalid choice. Please select 1 or 2.");
            System.out.print("Select Character Class (1 or 2): ");
            choice = scanner.nextInt();
        }

        
        switch (choice) {
            case 1:
                gameSettings.setPlayer(new Warrior("Warrior"));
                break;
            case 2:
                gameSettings.setPlayer(new Wizard("Wizard"));
                break;
            default:
                gameSettings.setPlayer(new Warrior("Warrior"));
        }
        System.out.println();

        
        System.out.println("Next, select your difficulty: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.print("Select Difficulty: ");
        int chosenDifficulty = scanner.nextInt();
        while (chosenDifficulty < 1 || chosenDifficulty > 3) {
            System.err.println("Invalid choice. Please select 1, 2 or 3.");
            System.out.print("Select Difficulty: ");
            chosenDifficulty = scanner.nextInt();
        }

        System.out.printf("You are about to start as a %s on %s mode. Good luck!\n\n",
                gameSettings.getPlayer().getName(),
                chosenDifficulty == 1 ? "easy" : chosenDifficulty == 2 ? "medium" : "hard");

        Difficulty difficulty;
        switch (chosenDifficulty) {
            case 1:
                difficulty = Difficulty.EASY;
                break;
            case 2:
                difficulty = Difficulty.MEDIUM;
                break;
            case 3:
                difficulty = Difficulty.HARD;
                break;
            default:
                difficulty = Difficulty.EASY;
                break;
        }
        gameSettings.setDifficulty(difficulty);
    }

    
    public void executeGameLoop() {
        List<Combatant> orderedCombatants = getOrder();
        printTurnOrder(orderedCombatants);

        while (gameSettings.getPlayer().isAlive() && !enemies.isEmpty()) { 
                                                                           
            

            System.out.printf("Round %d%n", turnCount);
            System.out.println("==========");

            
            
            
            

            for (Combatant combatant : orderedCombatants) {
                if (combatant.isAlive()) {
                    
                }
            }
            
            printRoundSummary();
            turnCount++;

        }

    }

    

    
    public void printTurnOrder(List<Combatant> oCombatants) {
        System.out.println("Turn Order:");
        for (int i = 1; i <= oCombatants.size(); i++) {
            System.out.printf("%d. %s (Speed: %d)%n", i, oCombatants.get(i).getName(), oCombatants.get(i).getSpeed());
        }
    }

    
    public void printRoundSummary() { 
        System.out.printf("End of Round %d%n", turnCount);
    }

    
    
    public void printGameCompletionScreen() {
        
        
        String gameResult = won ? "Victory" : "Defeat";
        System.out.println(gameResult);
        if (won) {
            System.out.println("Congratulations, you have defeated all your enemies.");
        } else {
            System.out.println("Defeated. Don't give up, try again!");
        }

        Scanner sc = new Scanner(System.in);
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
    }

    
    private List<Combatant> spawnInitialEnemy() { 
        switch (gameSettings.getDifficulty()) {

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
        return enemies;
    }

    private void checkBackupSpawn() { 
        if (!backupSpawn)
            return;
        else {
            switch (gameSettings.getDifficulty()) {
                case MEDIUM: 
                    enemies.add(new Wolf());
                    enemies.add(new Wolf());
                    break;

                case HARD: 
                    enemies.add(new Goblin());
                    enemies.add(new Wolf());
                    enemies.add(new Wolf());
                    break;

                default:
                    break;
            }
        }

    }
}