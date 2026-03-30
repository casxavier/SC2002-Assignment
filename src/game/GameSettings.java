package game;

import combatant.*;
import game.Gameflow.Difficulty;

public class GameSettings {
    private Difficulty difficulty;
    private Player player;

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

}