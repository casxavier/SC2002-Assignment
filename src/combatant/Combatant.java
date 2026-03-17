package combatant;

public abstract class Combatant {
    protected String name;
    protected int hp;
    protected int attack;
    protected int defense;
    protected int speed;

    public Combatant(String name, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(0, damage - defense);
        hp -= actualDamage;
    }

    public int getAttack() {
        return attack;
    }

    public int getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }
}
