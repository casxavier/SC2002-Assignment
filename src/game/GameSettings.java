package game;

import combatant.*;
import game.Gameflow.Difficulty;
import item.Item;

import java.util.*;

public class GameSettings {
    private Difficulty difficulty;
    private Player player;
    
    private List<Item> startingItemTemplate = new ArrayList<>();

    private static final Map<Difficulty, List<Map<String, String>>> INITIAL_WAVE_CONFIG = new HashMap<>();
    private static final Map<Difficulty, List<Map<String, String>>> BACKUP_WAVE_CONFIG = new HashMap<>();

    static {
        List<Map<String, String>> easyInitial = new ArrayList<>();
        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin B"));
        easyInitial.add(createEnemyConfig("GOBLIN", "Goblin C"));
        INITIAL_WAVE_CONFIG.put(Difficulty.EASY, easyInitial);

        List<Map<String, String>> mediumInitial = new ArrayList<>();
        mediumInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        mediumInitial.add(createEnemyConfig("WOLF", "Wolf A"));
        INITIAL_WAVE_CONFIG.put(Difficulty.MEDIUM, mediumInitial);

        List<Map<String, String>> hardInitial = new ArrayList<>();
        hardInitial.add(createEnemyConfig("GOBLIN", "Goblin A"));
        hardInitial.add(createEnemyConfig("GOBLIN", "Goblin B"));
        INITIAL_WAVE_CONFIG.put(Difficulty.HARD, hardInitial);

        BACKUP_WAVE_CONFIG.put(Difficulty.EASY, new ArrayList<>());

        List<Map<String, String>> mediumBackup = new ArrayList<>();
        mediumBackup.add(createEnemyConfig("WOLF", "Wolf A"));
        mediumBackup.add(createEnemyConfig("WOLF", "Wolf B"));
        BACKUP_WAVE_CONFIG.put(Difficulty.MEDIUM, mediumBackup);

        List<Map<String, String>> hardBackup = new ArrayList<>();
        hardBackup.add(createEnemyConfig("GOBLIN", "Goblin A"));
        hardBackup.add(createEnemyConfig("WOLF", "Wolf A"));
        hardBackup.add(createEnemyConfig("WOLF", "Wolf B"));
        BACKUP_WAVE_CONFIG.put(Difficulty.HARD, hardBackup);
    }

    public GameSettings(Difficulty difficulty, Player player) {
        this.difficulty = difficulty;
        this.player = player;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    private static Map<String, String> createEnemyConfig(String type, String name) {
        Map<String, String> config = new HashMap<>();
        config.put("type", type);
        config.put("name", name);
        return config;
    }

    public Player getPlayer() {
        return player;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setStartingItemTemplate(List<Item> template) {
        this.startingItemTemplate = template != null ? new ArrayList<>(template) : new ArrayList<>();
    }

    public List<Item> getStartingItemTemplate() {
        return Collections.unmodifiableList(startingItemTemplate);
    }

    
    public List<Map<String, String>> getInitialWaveConfig() {
        return new ArrayList<>(INITIAL_WAVE_CONFIG.getOrDefault(difficulty, new ArrayList<>()));
    }

    
    public List<Map<String, String>> getBackupWaveConfig() {
        return new ArrayList<>(BACKUP_WAVE_CONFIG.getOrDefault(difficulty, new ArrayList<>()));
    }
}
