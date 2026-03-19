import combatant.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
        System.out.print("Select Character Class (1 or 2): ");
        choice = scanner.nextInt();
        while (choice < 1 || choice > 2) {
            System.err.println("Invalid choice. Please select 1 or 2.");
            System.out.print("Select Character Class (1 or 2): ");
            choice = scanner.nextInt();
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
        System.out.println("Next, select your difficulty: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.print("Select Difficulty: ");
        int difficulty = scanner.nextInt();
        while (difficulty < 1 || difficulty > 3) {
            System.err.println("Invalid choice. Please select 1, 2 or 3.");
            System.out.print("Select Difficulty: ");
            difficulty = scanner.nextInt();
        }

        System.out.printf("You are about to start as a %s on %s mode. Good luck!\n\n", player.getName(),
                difficulty == 1 ? "easy" : difficulty == 2 ? "medium" : "hard");
    }
}