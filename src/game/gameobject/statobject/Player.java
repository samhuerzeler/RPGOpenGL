package game.gameobject.statobject;

import engine.InputHandler;
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

public abstract class Player extends StatObject {

    public static final float DAMPING = 0.5f;
    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    private Physics physics;
    private float jumpingSpeed = 1.0f;
    private boolean jumping = false;
    private Inventory inventory;
    private Equipment equipment;
    protected boolean autoAttack = false;
    protected InputHandler input = new InputHandler();

    public Player(float x, float y, float z) {
        physics = new Physics();
        stats = new Stats(100000, true);
        name = "Player";
        size = 5.0f;
        spawnPosition.x = x;
        spawnPosition.y = y;
        spawnPosition.z = z;
        type = PLAYER;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size);
        loadModel("res/models/monkey.obj");
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150.0f;
        attackRange = size;
        attackDamage = 20;
        attackDelay.start();
        tick.start();
    }

    @Override
    public void update() {
        if (autoAttack) {
            autoAttack();
        }
        if (tick.isOver()) {
            tick.restart();
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
        input.update();
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


        boolean movingForward = input.keyPressed(Keyboard.KEY_W, true);
        boolean movingBackward = input.keyPressed(Keyboard.KEY_S, true);
        boolean movingLeft = input.keyPressed(Keyboard.KEY_A, true);
        boolean movingRight = input.keyPressed(Keyboard.KEY_D, true);

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
        if (input.keyPressed(Keyboard.KEY_Q, true)) {
            rotateY(-rotationSpeed);
        }
        if (input.keyPressed(Keyboard.KEY_E, true)) {
            rotateY(rotationSpeed);
        }
        if (input.keyPressed(Keyboard.KEY_F)) {
            autoAttack = !autoAttack;
        }
        if (input.keyPressed(Keyboard.KEY_SPACE)) {
            if (!jumping && position.y < currentFloor.getHeight(position.x, position.z) + 1) {
                position.y += jumpingSpeed;
                jumping = true;
            }
        }
        if (input.keyPressed(Keyboard.KEY_NUMPAD1)) {
            EquippableItem sword = inventory.findByName("Sword");
            equipItem(sword);
        }
        if (input.keyPressed(Keyboard.KEY_NUMPAD2)) {
            EquippableItem bow = inventory.findByName("Bow");
            equipItem(bow);
        }
        if (input.keyPressed(Keyboard.KEY_C)) {
            listEquippedItems();
        }
    }

    protected void autoAttack() {
        if (attackDelay.isOver()) {
            ArrayList<GameObject> objects = Game.sphereCollide(position.x, position.z, sightRange);
            removeEnemiesInBack(objects);
            ArrayList<Enemy> enemies = findEnemies(objects);
            if (!enemies.isEmpty()) {
                attackClosestEnemy(enemies);
            } else {
                autoAttack = false;
            }
        }
        if (!isInCombat()) {
            autoAttack = false;
        }
    }

    protected void removeEnemiesInBack(ArrayList<GameObject> objects) {
        // TODO remove enemies in back
    }

    protected ArrayList<Enemy> findEnemies(ArrayList<GameObject> objects) {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (GameObject go : objects) {
            if (go.getType() == ENEMY) {
                enemies.add((Enemy) go);
            }
        }
        return enemies;
    }

    protected void attackClosestEnemy(ArrayList<Enemy> enemies) {
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
            if (!closestTarget.isResetting() && Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ()) <= attackRange) {
                if (!isInCombat()) {
                    setInCombat(this, closestTarget);
                }
                closestTarget.addToThreatMap(this, attackDamage);
                closestTarget.extendFleeRange();
                closestTarget.damage(attackDamage);
                attackDelay.restart();
                Log.p(name + " attacking " + closestTarget.getName() + " : " + closestTarget.getCurrentHealth() + "/" + closestTarget.getMaxHealth());
            } else {
                Log.p(name + " : Target is resetting or too far away");
            }
        } else {
            Log.p(name + " : No Target");
        }
    }

    protected void move(float amt, float dir) {
        // TODO add speed based scaling
        position.x += MOVEMENT_SPEED * amt * Math.cos(Math.toRadians(rotation.y + 90 * dir));
        position.z += MOVEMENT_SPEED * amt * Math.sin(Math.toRadians(rotation.y + 90 * dir));
    }

    protected void rotateY(float amt) {
        rotation.y += amt * Time.getDelta();
    }

    protected void mouseRotate() {
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

    protected void jump() {
        if (position.y >= currentFloor.getHeight(position.x, position.z)) {
            position.y += jumpingSpeed * Time.getDelta();
        } else {
            jumping = false;
        }
    }

    protected void applyGravity() {
        position.y -= physics.getFallingDistance();
    }

    protected void equipItem(EquippableItem item) {
        if (item != null) {
            equipment.equip(item);
            if (item.getSlot() == EquippableItem.WEAPON_SLOT) {
                setAttackDamage(((Weapon) item).getDamage());
                setAttackRange(((Weapon) item).getRange());
            }
            Log.p(item.getName() + " equipped");
        }
    }

    protected void listEquippedItems() {
        EquippableItem[] equippedItems = equipment.getItems();
        Log.p("Equipped items:");
        for (EquippableItem equippedItem : equippedItems) {
            if (equippedItem != null) {
                Log.p(equippedItem.getName());
            }
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
