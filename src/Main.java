import combatant.*;
import game.Gameflow;
import game.Gameflow.Difficulty;
import game.OrderBySpeed;
import item.Item;
import item.Potion;
import item.PowerStone;
import item.SmokeBomb;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean validChar = false;
        boolean validDiff = false;

        // Start Menu
        System.out.println("Welcome to SC2002 Assignment: Turn-Based Combat Arena! ");
        System.out.println("Press Enter to Start...");
        scanner.nextLine(); // Wait for user to press Enter

        // Character Selection
        int choice = 0;
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
                choice = Integer.parseInt(scanner.nextLine().trim()); // convert input to int
                if (choice < 1 || choice > 2){
                    throw new IllegalArgumentException("Invalid choice. Please select 1 or 2.");
                }
                validChar = true; // exit loop if input valid
            }catch(NumberFormatException e){
                System.err.println("Please enter a valid integer (1 or 2).");
            }catch(IllegalArgumentException e){
                System.err.println(e.getMessage());
            }
        }

        // Switch case to allow expansion in the future
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

        // Difficulty Selection
        int chosenDifficulty = 0;
        System.out.println("Next, select your difficulty: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");

        while(!validDiff){
            try{
                System.out.print("Select Difficulty: ");
                chosenDifficulty = Integer.parseInt(scanner.nextLine().trim()); // convert input to int
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

        System.out.printf("You are about to start as a %s on %s mode. Good luck!\n\n", player.getName(),
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

        scanner.nextLine();
        System.out.println("Choose two single-use items (duplicates allowed):");
        System.out.println("1. Potion — Heal 100 HP");
        System.out.println("2. Power Stone — One free special skill use");
        System.out.println("3. Smoke Bomb — Enemies deal 0 damage this turn and next");
        player.addItem(promptItem(scanner, "First item"));
        player.addItem(promptItem(scanner, "Second item"));
        System.out.println();

        Gameflow newGame = new Gameflow(player, difficulty, new OrderBySpeed());
        newGame.initializeGame();
        newGame.executeGameLoop();
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
}