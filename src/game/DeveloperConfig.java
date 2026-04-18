package game;

import combatant.*;
import item.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import game.GameSettings.Difficulty;

// Developer settings for all game registries and configurations.
public class DeveloperConfig {
    public static final Map<String, Function<String, Player>> PLAYER_REGISTRY = new LinkedHashMap<>();
    public static final Map<String, Supplier<Item>> ITEM_REGISTRY = new LinkedHashMap<>();
    public static final Map<String, Function<String, Enemy>> ENEMY_REGISTRY = new LinkedHashMap<>();

    public static final Map<String, String> CHARACTER_DESCRIPTIONS = new LinkedHashMap<>();
    public static final Map<String, String> ITEM_DESCRIPTIONS = new LinkedHashMap<>();

    public static final Map<Difficulty, List<Map<String, String>>> INITIAL_WAVE_CONFIG = new LinkedHashMap<>();
    public static final Map<Difficulty, List<Map<String, String>>> BACKUP_WAVE_CONFIG = new LinkedHashMap<>();

    static {
        PLAYER_REGISTRY.put("WARRIOR", Warrior::new);
        PLAYER_REGISTRY.put("WIZARD", Wizard::new);

        CHARACTER_DESCRIPTIONS.put("WARRIOR",
                "Warrior\n" +
                "   HP: 260 | Attack: 40 | Defense: 20 | Speed: 30\n" +
                "   Special Skill: Shield Bash\n" +
                "   - Deal basic attack damage to one enemy.\n" +
                "   - Target cannot act this turn and next turn.");
        CHARACTER_DESCRIPTIONS.put("WIZARD",
                "Wizard\n" +
                "   HP: 200 | Attack: 50 | Defense: 10 | Speed: 20\n" +
                "   Special Skill: Arcane Blast\n" +
                "   - Deal basic attack damage to all enemies.\n" +
                "   - Each enemy defeated grants +10 attack until level ends.");

        ITEM_REGISTRY.put("POTION", Potion::new);
        ITEM_REGISTRY.put("POWER_STONE", PowerStone::new);
        ITEM_REGISTRY.put("SMOKE_BOMB", SmokeBomb::new);

        ITEM_DESCRIPTIONS.put("POTION", "Potion - Heal 100 HP");
        ITEM_DESCRIPTIONS.put("POWER_STONE", "Power Stone - One free special skill use");
        ITEM_DESCRIPTIONS.put("SMOKE_BOMB", "Smoke Bomb - Enemies deal 0 damage this turn and next");

        ENEMY_REGISTRY.put("GOBLIN", Goblin::new);
        ENEMY_REGISTRY.put("WOLF", Wolf::new);

        List<Map<String, String>> easyInitial = new ArrayList<>();
        List<Map<String, String>> mediumInitial = new ArrayList<>();
        List<Map<String, String>> hardInitial = new ArrayList<>();
        List<Map<String, String>> easyBackup = new ArrayList<>();
        List<Map<String, String>> mediumBackup = new ArrayList<>();
        List<Map<String, String>> hardBackup = new ArrayList<>();

        // Initial Wave

        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin B"));
        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin C"));

        mediumInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        mediumInitial.add(createEnemyConfig("WOLF", "Wolf A"));

        hardInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        hardInitial.add(createEnemyConfig("GOBLIN", "Goblin B"));

        // Backup Wave

        mediumBackup.add(createEnemyConfig("WOLF", "Wolf A"));
        mediumBackup.add(createEnemyConfig("WOLF", "Wolf B"));

        hardBackup.add(createEnemyConfig("GOBLIN", "Goblin A"));
        hardBackup.add(createEnemyConfig("WOLF", "Wolf A"));
        hardBackup.add(createEnemyConfig("WOLF", "Wolf B"));

        INITIAL_WAVE_CONFIG.put(Difficulty.EASY, easyInitial);
        INITIAL_WAVE_CONFIG.put(Difficulty.MEDIUM, mediumInitial);
        INITIAL_WAVE_CONFIG.put(Difficulty.HARD, hardInitial);
        BACKUP_WAVE_CONFIG.put(Difficulty.EASY, easyBackup);
        BACKUP_WAVE_CONFIG.put(Difficulty.MEDIUM, mediumBackup);
        BACKUP_WAVE_CONFIG.put(Difficulty.HARD, hardBackup);
    }

    private DeveloperConfig() {
    }

    private static Map<String, String> createEnemyConfig(String type, String name) {
        Map<String, String> config = new HashMap<>();
        config.put("type", type);
        config.put("name", name);
        return config;
    }
}
