package action;

import java.util.List;

import combatant.Combatant;
import combatant.Player;




public final class BattleContext {
    private final Player player;
    private final List<Combatant> enemies;

    public BattleContext(Player player, List<Combatant> enemies) {
        this.player = player;
        this.enemies = enemies;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Combatant> getEnemies() {
        return enemies;
    }
}
