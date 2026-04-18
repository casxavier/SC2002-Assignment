package game;

import combatant.*;
import item.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameSettings {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    private Player player;

    private List<Item> startingItemTemplate = new ArrayList<>();

    public GameSettings(Difficulty difficulty, Player player) {
        this.difficulty = difficulty;
        this.player = player;
    }

    public static List<String> getAvailablePlayerTypes() {
        return new ArrayList<>(DeveloperConfig.PLAYER_REGISTRY.keySet());
    }

    public static List<String> getAvailableItemTypes() {
        return new ArrayList<>(DeveloperConfig.ITEM_REGISTRY.keySet());
    }

    public static List<String> getAvailableEnemyTypes() {
        return new ArrayList<>(DeveloperConfig.ENEMY_REGISTRY.keySet());
    }

    private static <T> T getFactory(String type, Map<String, T> registry) {
        return registry.get(type);
    }

    public static Function<String, Player> getPlayerFactory(String type) {
        return getFactory(type, DeveloperConfig.PLAYER_REGISTRY);
    }

    public static Supplier<Item> getItemFactory(String type) {
        return getFactory(type, DeveloperConfig.ITEM_REGISTRY);
    }

    public static Function<String, Enemy> getEnemyFactory(String type) {
        return getFactory(type, DeveloperConfig.ENEMY_REGISTRY);
    }

    public static String getCharacterDescription(String type) {
        return DeveloperConfig.CHARACTER_DESCRIPTIONS.getOrDefault(type, "Unknown character");
    }

    public static String getItemDescription(String type) {
        return DeveloperConfig.ITEM_DESCRIPTIONS.getOrDefault(type, "Unknown item");
    }

    public static int getMaxItemChoices() {
        return DeveloperConfig.ITEM_REGISTRY.size();
    }

    public Difficulty getDifficulty() {
        return difficulty;
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
        return new ArrayList<>(DeveloperConfig.INITIAL_WAVE_CONFIG.getOrDefault(difficulty, new ArrayList<>()));
    }

    public List<Map<String, String>> getBackupWaveConfig() {
        return new ArrayList<>(DeveloperConfig.BACKUP_WAVE_CONFIG.getOrDefault(difficulty, new ArrayList<>()));
    }
}
