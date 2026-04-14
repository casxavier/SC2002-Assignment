package game;

import combatant.*;
import game.Gameflow.Difficulty;
import item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSettings {
    private Difficulty difficulty;
    private Player player;
    
    private List<Item> startingItemTemplate = new ArrayList<>();

    public GameSettings(Difficulty difficulty, Player player) {
        this.difficulty = difficulty;
        this.player = player;
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

}