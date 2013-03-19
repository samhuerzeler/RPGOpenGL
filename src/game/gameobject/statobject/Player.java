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
    private Physics physics;
    private float jumpingSpeed = 7.0f;
    private boolean jumping = false;
    private Inventory inventory;
    private Equipment equipment;

    public Player(float x, float y, float z) {
        physics = new Physics();
        stats = new Stats(100000, true);
        name = "Player";
        size = 32.0f;
        spawnPosition.x = x;
        spawnPosition.y = y;
        spawnPosition.z = z;
        type = PLAYER;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size);
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150.0f;
        attackRange = 42;
        attackDamage = 20;
        attackDelay.start();
        tick.start();
    }

    @Override
    public void update() {
        if (tick.isOver()) {
            tick.start();
            replenishHealth();
        }
        if (jumping) {
            jump();
        }
        if (position.y > currentFloor.getHeight(position.x, position.z)) {
            applyGravity();
        } else {
            position.y = currentFloor.getHeight(position.x, position.z);
            physics.resetFallingVelocity();
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
            System.out.println(attackDelay.isOver());
            attack();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!jumping && position.y < currentFloor.getHeight(position.x, position.z) + 1) {
                position.y += jumpingSpeed;
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
        attackDelay.restart();
        ArrayList<GameObject> objects = Game.sphereCollide(position.x, position.z, attackRange);
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
                    if (Util.dist(position.x, position.z, e.getX(), e.getZ()) < Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ())) {
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
        position.x += getSpeed() * Time.getDelta() * amt * Math.cos(Math.toRadians(rotation.y + 90 * dir));
        position.z += getSpeed() * Time.getDelta() * amt * Math.sin(Math.toRadians(rotation.y + 90 * dir));
    }

    private void rotateY(float amt) {
        rotation.y += amt * Time.getDelta();
    }

    private void mouseRotate() {
        direction.x = Mouse.getDX();
        direction.y = Mouse.getDY();
        rotation.y += (direction.x * 0.1f);
        rotation.x += (direction.y * 0.1f);
        if (rotation.x > 90) {
            rotation.x = 90;
        }
        if (rotation.x < -90) {
            rotation.x = -90;
        }
    }

    private void jump() {
        if (position.y >= currentFloor.getHeight(position.x, position.z)) {
            position.y += jumpingSpeed * Time.getDelta();
        } else {
            jumping = false;
        }
    }

    private void applyGravity() {
        position.y -= physics.getFallingDistance() * Time.getDelta();
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

    private void listEquippedItems() {
        EquippableItem[] equippedItems = equipment.getItems();
        Log.p("Equipped items:");
        for (EquippableItem equippedItem : equippedItems) {
            Log.p(equippedItem.getName());
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void addXp(float amt) {
        stats.addXp(amt);
        Log.p("experience added: " + amt);
    }
}
