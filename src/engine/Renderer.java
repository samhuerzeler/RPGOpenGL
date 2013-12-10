package engine;

import game.FloorObject;
import game.GameObject;
import game.Util;
import static game.Game.player;
import game.Stats;
import game.gameobject.StatObject;
import static game.gameobject.StatObject.FRIENDLY;
import static game.gameobject.StatObject.HOSTILE;
import static game.gameobject.StatObject.PLAYER;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class Renderer {

    private FontHandler fontHandler = new FontHandler(20);

    public Renderer() {
    }

    public void renderFloors(ArrayList<FloorObject> floorObjects) {
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        for (FloorObject wo : floorObjects) {
            glPushMatrix();
            {
                glCallList(wo.getHeightmapDisplayList());
            }
            glPopMatrix();
        }
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
    }

    private void renderModel(GameObject go) {
        Animation animation = go.getAnimation();
        Model model = go.getModel();
        Vector3f position = go.getPosition();
        Vector3f rotation = go.getRotation();

        if (animation != null && go.isMoving()) {
            // render model
            glPushMatrix();
            {
                glTranslatef(position.x, position.y, position.z);
                glRotatef(-rotation.y + 180, 0, 1, 0);
                animation.render();
            }
            glPopMatrix();
        } else {
            if (model != null) {
                glPushMatrix();
                {
                    glTranslatef(position.x, position.y, position.z);
                    glRotatef(-rotation.y + 180, 0, 1, 0);
                    model.render();
                }
                glPopMatrix();
            }
        }
    }

    private void renderRangeCircle(GameObject go) {
        Vector3f position = go.getPosition();
        Vector3f rotation = go.getRotation();

        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            glColor3f(1.0f, 1.0f, 1.0f);
            int type = go.getType();

            if (type == HOSTILE || type == FRIENDLY) {
                Util.renderCircle(0.0f, 0.0f, ((StatObject) go).getSightRange());
                Util.renderCircle(0.0f, 0.0f, ((StatObject) go).getAttackRange());
            } else if (type == PLAYER) {
                Util.renderCircle(0.0f, 0.0f, ((StatObject) go).getAttackRange());
            }
        }
        glPopMatrix();
    }

    private void renderBars(GameObject go) {
        Vector3f position = go.getPosition();

        if (go.getType() != PLAYER) {
            Stats stats = go.getStats();

            // render health bar
            int currentHealth = stats.getCurrentHealth();
            int maxHealth = stats.getMaxHealth();
            glPushMatrix();
            {
                glTranslatef(position.x, position.y, position.z);
                if (go.getType() == HOSTILE) {
                    glColor3f(1.0f, 0.0f, 0.0f);
                } else if (go.getType() == FRIENDLY) {
                    glColor3f(1.0f, 1.0f, 0.0f);
                }
                renderBar(currentHealth, maxHealth, 40, 5);
            }
            glPopMatrix();

            // render resource bar
            int currentResource = stats.getCurrentResource();
            int maxResource = stats.getMaxResource();
            glPushMatrix();
            {
                glTranslatef(position.x, position.y, position.z);
                switch (go.getType()) {
                    case HOSTILE:
                        glColor3f(1.0f, 0.0f, 0.0f);
                        break;
                    case FRIENDLY:
                        glColor3f(1.0f, 1.0f, 0.0f);
                        break;
                }
                renderBar(currentResource, maxResource, 35, 5);
            }
            glPopMatrix();
        }
    }

    public void renderObjects(ArrayList<GameObject> gameObjects) {
        for (GameObject go : gameObjects) {
            if (go.getType() == 1) {
                glColor3f(0.0f, 1.0f, 0.0f);
            } else if (go.getType() == 3) {
                glColor3f(0.3f, 0.3f, 1.0f);
            } else {
                glColor3f(1.0f, 0.0f, 0.0f);
            }

            renderSpawnPoint(go, 32.0f);
            renderModel(go);
            renderRangeCircle(go);
            renderBars(go);
        }
    }

    private void renderBar(int current, int max, float y, float height) {
        float percentage = (float) current / (float) max * 100.0f;
        Vector3f cameraRotation = OrbitCamera.camera.getRotation();
        glRotatef(-cameraRotation.y + 180, 0, 1, 0);
        glRotatef(-cameraRotation.x, 1, 0, 0);
        glScalef(-1, 1, 1);
        glTranslatef(-20, 0, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, y);
            glVertex2f(percentage * 20 / 50, y);
            glVertex2f(percentage * 20 / 50, y - height);
            glVertex2f(0, y - height);
        }
        glEnd();
    }

    public void renderSpawnPoint(GameObject go, float r) {
        int numSegments = 100;
        float theta = (float) (2 * 3.1415926 / numSegments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;
        float xx = r;
        float zz = 0;
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < numSegments; i++) {
            glVertex3f(xx + go.getSpawnX(), 0.0f, zz + go.getSpawnZ());
            t = xx;
            xx = c * xx - s * zz;
            zz = s * t + c * zz;
        }
        glEnd();
    }

    public void renderText(ArrayList<GameObject> objects) {
        for (GameObject go : objects) {
            glPushMatrix();
            {
                if (go.getType() != StatObject.PLAYER) {
                    Vector3f position = go.getPosition();
                    glTranslatef(position.x, position.y + 60, position.z);
                    Vector3f cameraRotation = OrbitCamera.camera.getRotation();
                    glRotatef(-cameraRotation.y + 180, 0, 1, 0);
                    glRotatef(-cameraRotation.x, 1, 0, 0);
                    glScalef(-1, -1, 1);
                    fontHandler.drawString(0, 0, go.getName());
                }
            }
            glPopMatrix();
        }
    }

    public void renderHud() {
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0.0, Display.getWidth(), Display.getHeight(), 0.0, -1.0, 10.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glDisable(GL_DEPTH_TEST);  // disable depth-testing

        // player
        // render health bar
        float stretch = 3;
        float xOffset = 20;
        float yOffset = 20;
        float height = 20;
        fontHandler.drawString(xOffset, yOffset + 15, player.getName() + " (" + player.getLevel() + ")");
        yOffset = 40;
        float percentage = (float) player.getCurrentHealth() / (float) player.getMaxHealth() * 100.0f;
        glDisable(GL_LIGHTING);
        glBegin(GL_QUADS);
        {
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex2f(xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset + height);
            glVertex2f(xOffset, yOffset + height);
        }
        glEnd();
        glEnable(GL_LIGHTING);
        fontHandler.drawString(xOffset, yOffset + 15, player.getCurrentHealth() + " / " + player.getMaxHealth());
        // render resource bar
        yOffset = 60;
        percentage = (float) player.getCurrentResource() / (float) player.getMaxResource() * 100.0f;
        glDisable(GL_LIGHTING);
        glBegin(GL_QUADS);
        {
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex2f(xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset + height);
            glVertex2f(xOffset, yOffset + height);
        }
        glEnd();
        glEnable(GL_LIGHTING);
        fontHandler.drawString(xOffset + 2, yOffset + 15, player.getCurrentResource() + " / " + player.getMaxResource());

        // target
        if (player.getTarget() != null) {
            xOffset = 340;
            yOffset = 20;
            fontHandler.drawString(xOffset + 2, yOffset + 15, player.getTarget().getName() + " (" + player.getTarget().getLevel() + ")");
            // render healt bar
            yOffset = 40;
            percentage = (float) player.getTarget().getCurrentHealth() / (float) player.getTarget().getMaxHealth() * 100.0f;
            glDisable(GL_LIGHTING);
            glBegin(GL_QUADS);
            {
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex2f(xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset + height);
                glVertex2f(xOffset, yOffset + height);
            }
            glEnd();
            glEnable(GL_LIGHTING);
            fontHandler.drawString(xOffset + 2, yOffset + 15, player.getTarget().getCurrentHealth() + " / " + player.getTarget().getMaxHealth());
            // render resource bar
            yOffset = 60;
            percentage = (float) player.getTarget().getCurrentResource() / (float) player.getTarget().getMaxResource() * 100.0f;
            glDisable(GL_LIGHTING);
            glBegin(GL_QUADS);
            {
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex2f(xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset + height);
                glVertex2f(xOffset, yOffset + height);
            }
            glEnd();
            glEnable(GL_LIGHTING);
            fontHandler.drawString(xOffset + 2, yOffset + 15, player.getTarget().getCurrentResource() + " / " + player.getTarget().getMaxResource());
        }

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glColor3f(1.0f, 1.0f, 1.0f);
        OrbitCamera.camera.initProjection();
    }
}
