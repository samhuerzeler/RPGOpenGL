package game.gameobject.statobject;

import engine.Physics;
import game.Equipment;
import game.Game;
import game.GameObject;
import game.Inventory;
import game.Item;
import game.Stats;
import game.Time;
import game.Util;
import game.World;
import game.gameobject.StatObject;
import game.gameobject.statobject.mob.Enemy;
import game.item.EquippableItem;
import game.item.equippableitem.Weapon;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import util.Log;

public class Player extends StatObject {

    private World world = Game.world;
    public static final float DAMPING = 0.5f;
    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    private float jumpingSpeed = 7.0f;
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
        if (y > world.getHeight(x, z)) {
            fall();
        } else {
            y = world.getHeight(x, z);
            Physics.resetFallingVelocity();
            jumping = false;
        }
    }

    public void getInput() {
        // Mouse Input
        // TODO mouse grabbed conflicts with mwheel
        if (Mouse.isButtonDown(MOUSEB_RIGHT)) {
            mouseRotate();
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
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
            if (!jumping && y < world.getHeight(x, z) + 1) {
                y += jumpingSpeed;
                jumping = true;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            EquippableItem sword = inventory.findByName("Sword");
            equipItem(sword);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            EquippableItem bow = inventory.findByName("Bow");
            equipItem(bow);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            listEquippedItems();
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
        y -= Physics.getFallingDistance() * Time.getDelta();
    }

    private void jump() {
        if (y >= world.getHeight(x, z)) {
            y += jumpingSpeed * Time.getDelta();
        } else {
            jumping = false;
        }
    }

    private void equipItem(EquippableItem item) {
        if (item != null) {
            equipment.equip(item);
            if (item.getSlot() == EquippableItem.WEAPON_SLOT) {
                setAttackDamage(((Weapon) item).getDamage());
                setAttackRange(((Weapon) item).getRange());
            }
            Log.p(item.getName() + " equipped");
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    private void listEquippedItems() {
        EquippableItem[] equippedItems = equipment.getItems();
        Log.p("Equipped items:");
        for (EquippableItem equippedItem : equippedItems) {
            Log.p(equippedItem.getName());
        }
    }

    public void addXp(float amt) {
        stats.addXp(amt);
        Log.p("experience added" + amt);
    }
}
