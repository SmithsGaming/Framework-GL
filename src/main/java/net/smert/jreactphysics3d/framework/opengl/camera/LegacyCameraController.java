package net.smert.jreactphysics3d.framework.opengl.camera;

import net.smert.jreactphysics3d.framework.Fw;
import net.smert.jreactphysics3d.framework.helpers.InputProcessor;
import net.smert.jreactphysics3d.framework.helpers.Keyboard;
import net.smert.jreactphysics3d.framework.math.MathHelper;
import net.smert.jreactphysics3d.framework.math.Vector3f;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class LegacyCameraController implements InputProcessor {

    private final static String KEY_ACTION_LOOK_DOWN = "legCamLookDown";
    private final static String KEY_ACTION_LOOK_UP = "legCamLookUp";
    private final static String KEY_ACTION_MOVE_BACK = "legCamMoveBack";
    private final static String KEY_ACTION_MOVE_DOWN = "legCamMoveDown";
    private final static String KEY_ACTION_MOVE_FORWARD = "legCamMoveForward";
    private final static String KEY_ACTION_MOVE_LEFT = "legCamMoveLeft";
    private final static String KEY_ACTION_MOVE_RIGHT = "legCamMoveRight";
    private final static String KEY_ACTION_MOVE_UP = "legCamMoveUp";
    private final static String KEY_ACTION_TURN_LEFT = "legCamTurnLeft";
    private final static String KEY_ACTION_TURN_RIGHT = "legCamTurnRight";

    private float lookSpeed;
    private float moveSpeed;
    private final LegacyCamera camera;

    public LegacyCameraController(LegacyCamera camera) {
        lookSpeed = 10.0f;
        moveSpeed = 9.0f;
        this.camera = camera;
    }

    private void correctHeadingPitchAndRoll() {
        Vector3f camRotation = camera.getRotation();

        if (camRotation.getX() > 90.0f) {
            camera.setRotationX(90.0f);
        } else if (camRotation.getX() < -90.0f) {
            camera.setRotationX(-90.0f);
        }

        if ((camRotation.getY() >= 360.0f) || (camRotation.getY() <= -360.0f)) {
            camera.setRotationY(camRotation.getY() % 360.0f);
        }
        if (camRotation.getY() < 0) {
            camera.setRotationY(camRotation.getY() + 360.0f);
        }
    }

    public float getLookSpeed() {
        return lookSpeed;
    }

    public void setLookSpeed(float lookSpeed) {
        this.lookSpeed = lookSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void update() {
        float delta = Fw.timer.getDelta();
        Vector3f camPosition = camera.getPosition();
        Vector3f camRotation = camera.getRotation();

        float xPositionDelta = 0.0f;
        float yPositionDelta = 0.0f;
        float zPositionDelta = 0.0f;

        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_BACK) == true) {
            xPositionDelta += Math.sin(camRotation.getY() * MathHelper.PI_OVER_180);
            zPositionDelta += Math.cos(camRotation.getY() * MathHelper.PI_OVER_180);
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_FORWARD) == true) {
            xPositionDelta -= Math.sin(camRotation.getY() * MathHelper.PI_OVER_180);
            zPositionDelta -= Math.cos(camRotation.getY() * MathHelper.PI_OVER_180);
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_LEFT) == true) {
            xPositionDelta += Math.sin((camRotation.getY() - 90) * MathHelper.PI_OVER_180);
            zPositionDelta += Math.cos((camRotation.getY() - 90) * MathHelper.PI_OVER_180);
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_RIGHT) == true) {
            xPositionDelta += Math.sin((camRotation.getY() + 90) * MathHelper.PI_OVER_180);
            zPositionDelta += Math.cos((camRotation.getY() + 90) * MathHelper.PI_OVER_180);
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_DOWN) == true) {
            yPositionDelta += 0.5f;
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_MOVE_UP) == true) {
            yPositionDelta -= 0.5f;
        }

        camera.setPositionX(camPosition.getX() + xPositionDelta * delta * moveSpeed);
        camera.setPositionY(camPosition.getY() + yPositionDelta * delta * moveSpeed);
        camera.setPositionZ(camPosition.getZ() + zPositionDelta * delta * moveSpeed);

        float xRotationDelta = 0.0f;
        float yRotationDelta = 0.0f;

        if (Fw.input.isActionKeyDown(KEY_ACTION_LOOK_DOWN) == true) {
            xRotationDelta -= 5.0f;
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_LOOK_UP) == true) {
            xRotationDelta += 5.0f;
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_TURN_LEFT) == true) {
            yRotationDelta -= 5.0f;
        }
        if (Fw.input.isActionKeyDown(KEY_ACTION_TURN_RIGHT) == true) {
            yRotationDelta += 5.0f;
        }

        xRotationDelta += Fw.input.getDeltaY();
        yRotationDelta += Fw.input.getDeltaX();

        // LWJGL will return 0 for mouse movement if the frame rate is higher than 125fps. If we just used the delta
        // amount then the mouse speed would be reduced. MOUSE_POLL is set for 125fps to compensate for this issue.
        // If the framerate is lower than 125fps then we want the larger delta.
        float mousePoll = Math.max(delta, Fw.input.MOUSE_POLL);

        camera.setRotationX(camRotation.getX() - xRotationDelta * mousePoll * lookSpeed);
        camera.setRotationY(camRotation.getY() - yRotationDelta * mousePoll * lookSpeed);

        correctHeadingPitchAndRoll();
    }

    @Override
    public void registerActions() {
        Fw.input.setAction(KEY_ACTION_LOOK_DOWN, Keyboard.DOWN);
        Fw.input.setAction(KEY_ACTION_LOOK_UP, Keyboard.UP);
        Fw.input.setAction(KEY_ACTION_MOVE_BACK, Keyboard.S);
        Fw.input.setAction(KEY_ACTION_MOVE_DOWN, Keyboard.LSHIFT);
        Fw.input.setAction(KEY_ACTION_MOVE_FORWARD, Keyboard.W);
        Fw.input.setAction(KEY_ACTION_MOVE_LEFT, Keyboard.A);
        Fw.input.setAction(KEY_ACTION_MOVE_RIGHT, Keyboard.D);
        Fw.input.setAction(KEY_ACTION_MOVE_UP, Keyboard.SPACE);
        Fw.input.setAction(KEY_ACTION_TURN_LEFT, Keyboard.LEFT);
        Fw.input.setAction(KEY_ACTION_TURN_RIGHT, Keyboard.RIGHT);
    }

    @Override
    public void unregisterActions() {
        Fw.input.removeActionKey(KEY_ACTION_LOOK_DOWN);
        Fw.input.removeActionKey(KEY_ACTION_LOOK_UP);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_BACK);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_DOWN);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_FORWARD);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_LEFT);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_RIGHT);
        Fw.input.removeActionKey(KEY_ACTION_MOVE_UP);
        Fw.input.removeActionKey(KEY_ACTION_TURN_LEFT);
        Fw.input.removeActionKey(KEY_ACTION_TURN_RIGHT);
    }

}