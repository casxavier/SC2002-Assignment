package combatant;

public abstract class Player extends Combatant {
    protected int maxHp;

    public Player(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.maxHp = hp;
    }

    public void heal(int amount) {
        hp = Math.min(hp + amount, maxHp);
    }
}
