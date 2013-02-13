package game.gameobject.statobject;

import game.Equipment;
import game.Game;
import game.GameObject;
import game.Inventory;
import game.Item;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.StatObject;
import game.gameobject.statobject.mob.Enemy;
import game.item.EquippableItem;
import game.item.equippableitem.Weapon;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import util.Log;

public class Player extends StatObject {

    public static final float DAMPING = 0.5f;
    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    // TODO move gravity stuff to physics class
    private final float GRAVITY = -9.8f;
    private float fallingVelocity = 0.0f;
    private float newVelocity;
    private float jumpingSpeed = 8.0f;
    private boolean jumping = false;
    private Inventory inventory;
    private Equipment equipment;

    public Player(float x, float y, float z) {
        stats = new Stats(100000, true);
        name = "Player";
        size = 32.0f;
        spawnX = x;
        spawnY = y;
        spawnZ = z;
        type = PLAYER;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size);
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150.0f;
        attackRange = 42;
        attackDamage = 20;
        attackDelay.init();
    }

    @Override
    public void update() {
        if (jumping) {
            jump();
        }
        if (y > 0) {
            fall();
        } else {
            y = 0;
            resetFallingVelocity();
            jumping = false;
        }
    }

    private void resetFallingVelocity() {
        fallingVelocity = 0.0f;
    }

    public void getInput() {
        // Mouse Input
        // TODO mouse grabbed conflicts with mwheel
        if (Mouse.isButtonDown(MOUSEB_RIGHT)) {
            mouseRotate();
            //Mouse.setGrabbed(true);
        } else {
            //Mouse.setGrabbed(false);
        }
        // Keyboard Input
        float movementSpeed = 5.0f;
        float rotationSpeed = 2.0f;

        boolean movingForward = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean movingBackward = Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean movingLeft = Keyboard.isKeyDown(Keyboard.KEY_Q);
        boolean movingRight = Keyboard.isKeyDown(Keyboard.KEY_E);

        if ((movingForward && (movingLeft || movingRight)) || (movingBackward && (movingLeft || movingRight))) {
            movementSpeed = (float) (movementSpeed / Math.sqrt(2));
        }
        if (movingForward) {
            move(-movementSpeed, 1);
        }
        if (movingBackward) {
            movementSpeed *= DAMPING;
            move(movementSpeed, 1);
        }
        if (movingLeft) {
            move(-movementSpeed, 0);
        }
        if (movingRight) {
            move(movementSpeed, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            rotateY(-rotationSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            rotateY(rotationSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F) && attackDelay.isOver()) {
            attack();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!jumping && y < 2) {
                y += jumpingSpeed;
                jumping = true;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            Item sword = inventory.findByName("Sword");
            equipWeapon((Weapon) sword);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            Item bow = inventory.findByName("Bow");
            equipWeapon((Weapon) bow);
        }
    }

    private void attack() {
        attackDelay.start();
        ArrayList<GameObject> objects = Game.sphereCollide(x, z, attackRange);
        removeEnemiesInBack(objects);
        ArrayList<Enemy> enemies = findEnemies(objects);
        attackClosestEnemy(enemies);
    }

    private void removeEnemiesInBack(ArrayList<GameObject> objects) {
        // TODO remove enemies in back
    }

    private ArrayList<Enemy> findEnemies(ArrayList<GameObject> objects) {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (GameObject go : objects) {
            if (go.getType() == ENEMY) {
                enemies.add((Enemy) go);
            }
        }
        return enemies;
    }

    private void attackClosestEnemy(ArrayList<Enemy> enemies) {
        if (enemies.size() > 0) {
            // find closest target
            Enemy closestTarget = enemies.get(0);
            if (enemies.size() > 1) {
                for (Enemy e : enemies) {
                    if (Util.dist(x, z, e.getX(), e.getZ()) < Util.dist(x, z, closestTarget.getX(), closestTarget.getZ())) {
                        closestTarget = e;
                    }
                }
            }
            // attack closest target
            if (!closestTarget.isResetting()) {
                setInCombat(this, closestTarget);
                closestTarget.addToThreatMap(this, attackDamage);
                closestTarget.extendFleeRange();
                closestTarget.damage(attackDamage);
                Log.p(name + " attacking " + closestTarget.getName() + " : " + closestTarget.getCurrentHealth() + "/" + closestTarget.getMaxHealth());
            } else {
                Log.p(name + " : Target is resetting");
            }
        } else {
            Log.p(name + " : No Target");
        }
    }

    private void move(float amt, float dir) {
        x += getSpeed() * Time.getDelta() * amt * Math.cos(Math.toRadians(ry + 90 * dir));
        z += getSpeed() * Time.getDelta() * amt * Math.sin(Math.toRadians(ry + 90 * dir));
    }

    public void rotateY(float amt) {
        ry += amt * Time.getDelta();
    }

    private void mouseRotate() {
        dx = Mouse.getDX();
        dy = Mouse.getDY();
        ry += (dx * 0.1f);
        rx += (dy * 0.1f);
        if (rx > 90) {
            rx = 90;
        }
        if (rx < -90) {
            rx = -90;
        }
    }

    public void fall() {
        newVelocity = fallingVelocity + 0.3f * Time.getDelta();
        if (newVelocity < GRAVITY) {
            newVelocity = GRAVITY * Time.getDelta();
        }
        y -= Time.getDelta() * (fallingVelocity + newVelocity) / 2;
        fallingVelocity = newVelocity;
    }

    private void jump() {
        if (y >= 0) {
            y += jumpingSpeed * Time.getDelta();
        } else {
            jumping = false;
        }
    }

    private void equipWeapon(Weapon weapon) {
        if (weapon != null) {
            equipment.equip((EquippableItem) weapon);
            setAttackDamage(((Weapon) weapon).getDamage());
            setAttackRange(((Weapon) weapon).getRange());
            Log.p(weapon.getName() + " equipped");
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void addXp(float amt) {
        stats.addXp(amt);
    }
}
